package com.arms.game.combat;

import com.arms.game.models.Faction;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.Unit;
import com.arms.game.rules.MovementRules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.IntSupplier;

/**
 * Resolves one attack against one hex (rulebook 12.2). Dice are rolled in this order:
 * white, white, attacker Tactical Coordination, defender Tactical Coordination.
 */
public final class CombatResolver {

    public static final int RETREAT_HP_PER_HEX = 3;

    // INFERRED from the Monte Grappa special rules: the Entente (Evolving / Cadorna-Foch) double their
    // defence in trenches. Everyone else triples it (rulebook 12.2.4 terrain modifiers).
    private static final int ENTENTE_TRENCH_MULTIPLIER = 2;
    private static final int DEFAULT_TRENCH_MULTIPLIER = 3;

    private static final int MIN_COLUMN = -1; // 1/2
    private static final int MAX_COLUMN = 5;  // 6/1
    private static final int AUTO_LOSS_STEPS = 3;

    private static final Set<Hex.Terrain> TERRAIN_SHIFTS_LEFT = Set.of(
            Hex.Terrain.FOREST, Hex.Terrain.MARSH, Hex.Terrain.MOUNTAIN,
            Hex.Terrain.HIGH_MOUNTAIN, Hex.Terrain.TOWN, Hex.Terrain.CITY);

    private CombatResolver() {
    }

    public static CombatReport resolve(GameMap map, HexCoordinate target, List<Unit> attackers,
                                       IntSupplier die, CombatDecisions decisions) {
        Hex defendHex = map.getHex(target);
        List<Unit> defenders = defendHex.getUnits().stream().filter(Unit::isCombatUnit).toList();
        Faction attackerSide = attackers.getFirst().faction;
        Unit attackLead = leadUnit(attackers);
        Unit defendLead = leadUnit(defenders);
        List<String> eliminated = new ArrayList<>();

        int attackTotal = attackers.stream().mapToInt(Unit::attackFactor).sum();
        int defenseTotal = defenseTotal(defendHex, defenders);
        int column = ladderPosition(attackTotal, defenseTotal)
                + columnShift(defendHex, attackLead, defendLead);

        // Below 1/2 the attack is hopeless: the attacker loses 3 steps and nothing is rolled.
        if (column < MIN_COLUMN) {
            HitPoints.Result loss = HitPoints.applySteps(attackers, AUTO_LOSS_STEPS);
            removeEliminated(map, loss.eliminated(), eliminated);
            return new CombatReport(attackTotal, defenseTotal, column, 0, 0, 0,
                    Coordination.NONE, Coordination.NONE, 0, false, true, eliminated);
        }

        int whiteTotal = die.getAsInt() + die.getAsInt();
        CRT.Result crt = CRT.lookup(Math.min(column, MAX_COLUMN) - MIN_COLUMN, whiteTotal);
        Coordination attackCoordination = Coordination.fromRoll(die.getAsInt(), Coordination.modifier(attackLead));
        Coordination defendCoordination = Coordination.fromRoll(die.getAsInt(), Coordination.modifier(defendLead));

        int defenderHp = crt.defenderHp();
        int retreatHexes = 0;
        if (defendCoordination.givesBenefit()) {
            retreatHexes = retreat(map, target, defenders, attackerSide, defendCoordination,
                    defenderHp, decisions, eliminated);
            defenderHp = Math.max(0, defenderHp - retreatHexes * RETREAT_HP_PER_HEX);
        }

        HitPoints.Result defenderLoss = HitPoints.apply(survivors(defenders, eliminated), defendLead, defenderHp);
        removeEliminated(map, defenderLoss.eliminated(), eliminated);
        HitPoints.Result attackerLoss = HitPoints.apply(attackers, attackLead, crt.attackerHp());
        removeEliminated(map, attackerLoss.eliminated(), eliminated);

        boolean advanced = false;
        if (attackCoordination.givesBenefit()) {
            List<Unit> eligible = attackers.stream()
                    .filter(u -> !eliminated.contains(u.id) && !u.stepLoss && !u.isDisorganized())
                    .toList();
            if (!hasEnemyCombatUnits(defendHex, attackerSide) && decisions.attackerAdvances()) {
                advanced = advance(map, defendHex, eligible, attackerSide);
            }
            int markers = attackCoordination == Coordination.PERFECT ? 2 : 1;
            eligible.stream().limit(markers).forEach(u -> u.exploitation = true);
        }

        return new CombatReport(attackTotal, defenseTotal, column, whiteTotal, crt.attackerHp(), crt.defenderHp(),
                attackCoordination, defendCoordination, retreatHexes, advanced, false, eliminated);
    }

    /** The highest morale unit leads. Ties go to the first in the list (the owner's choice). */
    static Unit leadUnit(List<Unit> units) {
        return units.stream().max(Comparator.comparingInt(u -> u.morale)).orElseThrow();
    }

    static int defenseTotal(Hex defendHex, List<Unit> defenders) {
        int total = defenders.stream().mapToInt(Unit::defenseFactor).sum();
        if (defendHex.isTrench()) {
            boolean entente = defenders.getFirst().faction == Faction.ENTENTE;
            total *= entente ? ENTENTE_TRENCH_MULTIPLIER : DEFAULT_TRENCH_MULTIPLIER;
        }
        return total;
    }

    /**
     * Odds as a position on the CRT ladder: 1/1 is 0, N/1 is N-1 and 1/N is -(N-1). Rounds in the defender's favour.
     */
    static int ladderPosition(int attack, int defense) {
        if (attack >= defense) {
            return defense == 0 ? MAX_COLUMN : attack / defense - 1;
        }
        int n = (defense + attack - 1) / attack; // ceil(defense / attack)
        return -(n - 1);
    }

    /** Column shifts: positive favours the attacker. */
    static int columnShift(Hex defendHex, Unit attackLead, Unit defendLead) {
        int shift = attackLead.morale - defendLead.morale;
        if (TERRAIN_SHIFTS_LEFT.contains(defendHex.getTerrain())) {
            shift--;
        }
        return shift;
    }

    private static List<Unit> survivors(List<Unit> units, List<String> eliminatedIds) {
        return units.stream().filter(u -> !eliminatedIds.contains(u.id)).toList();
    }

    private static boolean hasEnemyCombatUnits(Hex hex, Faction attackerSide) {
        return hex.hasCombatUnitOf(attackerSide.opponent());
    }

    private static void removeEliminated(GameMap map, List<Unit> units, List<String> ids) {
        for (Unit unit : units) {
            Hex hex = map.hexOf(unit);
            if (hex != null) {
                hex.getUnits().remove(unit);
            }
            ids.add(unit.id);
        }
    }

    /**
     * The defender may retreat up to 3 hexes on Perfect coordination and 2 on Partial, or 2 and 1 if any unit is
     * Disorganized. Each hex retreated removes 3 HP. Two or more hexes Disorganises the stack, and passing through
     * an enemy ZoC costs a step loss. The path moves directly away from the attacked hex, so a stack can end up
     * retreating fewer hexes than it wanted if it runs out of room.
     */
    private static int retreat(GameMap map, HexCoordinate target, List<Unit> defenders, Faction attackerSide,
                               Coordination coordination, int hitPoints, CombatDecisions decisions,
                               List<String> eliminated) {
        boolean anyDisorganized = defenders.stream().anyMatch(Unit::isDisorganized);
        int max = coordination == Coordination.PERFECT ? 3 : 2;
        if (anyDisorganized) {
            max--;
        }
        int wanted = Math.min(max, decisions.defenderRetreatHexes(max, hitPoints));
        if (wanted <= 0) {
            return 0;
        }

        Faction defenderSide = attackerSide.opponent();
        Set<HexCoordinate> enemyZoc = MovementRules.zoneOfControl(map, attackerSide);
        HexCoordinate current = target;
        int zocHexes = 0;
        int moved = 0;
        while (moved < wanted) {
            HexCoordinate from = current;
            HexCoordinate next = current.neighbors().stream()
                    .filter(c -> map.getHex(c) != null)
                    .filter(c -> !map.getHex(c).isOccupiedBy(attackerSide))
                    .filter(c -> target.distanceTo(c) > target.distanceTo(from))
                    .min(Comparator.<HexCoordinate>comparingInt(c -> inHostileZoc(map, c, enemyZoc, defenderSide) ? 1 : 0)
                            .thenComparingInt(HexCoordinate::q).thenComparingInt(HexCoordinate::r))
                    .orElse(null);
            if (next == null) {
                break;
            }
            if (inHostileZoc(map, next, enemyZoc, defenderSide)) {
                zocHexes++;
            }
            current = next;
            moved++;
        }
        if (moved == 0) {
            return 0;
        }

        Hex origin = map.getHex(target);
        Hex destination = map.getHex(current);
        origin.getUnits().removeAll(defenders);
        destination.getUnits().addAll(defenders);
        if (moved >= 2) {
            defenders.forEach(u -> u.disorganized = true);
        }
        for (int i = 0; i < zocHexes; i++) {
            List<Unit> alive = survivors(defenders, eliminated);
            if (alive.isEmpty()) {
                break;
            }
            removeEliminated(map, HitPoints.applySteps(List.of(alive.getFirst()), 1).eliminated(), eliminated);
        }
        return moved;
    }

    private static boolean inHostileZoc(GameMap map, HexCoordinate c, Set<HexCoordinate> enemyZoc, Faction friendly) {
        return enemyZoc.contains(c) && !map.getHex(c).isOccupiedBy(friendly);
    }

    /** Eligible attackers move into the attacked hex, as many as the stacking limit allows. */
    private static boolean advance(GameMap map, Hex defendHex, List<Unit> eligible, Faction attackerSide) {
        defendHex.getUnits().removeIf(u -> u.faction != attackerSide); // leftover supports are eliminated
        boolean moved = false;
        for (Unit unit : eligible) {
            Hex from = map.hexOf(unit);
            if (from == null || from == defendHex) {
                continue;
            }
            defendHex.getUnits().add(unit);
            if (defendHex.isOverstacked()) {
                defendHex.getUnits().remove(unit);
                continue;
            }
            from.getUnits().remove(unit);
            moved = true;
        }
        return moved;
    }
}
