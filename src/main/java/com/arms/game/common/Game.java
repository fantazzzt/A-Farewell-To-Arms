package com.arms.game.common;

import com.arms.game.combat.CombatDecisions;
import com.arms.game.combat.CombatReport;
import com.arms.game.combat.CombatResolver;
import com.arms.game.common.state.Combat;
import com.arms.game.common.state.GameState;
import com.arms.game.common.state.Movement;
import com.arms.game.common.state.Start;
import com.arms.game.models.Dice;
import com.arms.game.models.Faction;
import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.map.Reinforcement;
import com.arms.game.models.squares.Unit;
import com.arms.game.rules.MovementRules;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntSupplier;

public class Game {

    // An Offensive gives the declaring turn this many Operational Sequences, otherwise just one.
    private static final int SEQUENCES_WITH_OFFENSIVE = 3;
    private static final int SEQUENCES_WITHOUT_OFFENSIVE = 1;

    @Getter
    private final List<Player> players;
    @Getter
    private final GameMap gameMap;
    @Getter
    private GameState gameState = new Start();
    @Getter
    private int currentTurn;
    /** Which Operational Sequence of the current turn we are in, starting at 1. */
    @Getter
    private int sequence;
    @Getter
    private int sequencesThisTurn = SEQUENCES_WITHOUT_OFFENSIVE;
    @Getter
    private Faction phasingFaction;
    private final Set<HexCoordinate> attackedHexes = new HashSet<>();
    private IntSupplier dice = Dice::roll;
    private final List<String> events = new ArrayList<>();

    public Game(List<Player> players, GameMap gameMap) {
        this.players = players;
        this.gameMap = gameMap;
        this.phasingFaction = gameMap.getInitiativeFaction();
    }

    /** Ends the current phase and moves on to the next one. */
    public void advancePhase() {
        gameState = gameState.advance(this);
    }

    public void moveUnits(HexCoordinate from, List<Unit> units, HexCoordinate to) {
        if (!(gameState instanceof Movement)) {
            throw new IllegalStateException("Units can only move during the Movement phase, not " + gameState.phaseName());
        }
        if (units.stream().anyMatch(u -> u.faction != phasingFaction)) {
            throw new IllegalArgumentException("Only " + phasingFaction + " units can move now");
        }
        MovementRules.move(gameMap, from, units, to);
    }

    /** Lets tests (or a UI with physical dice) supply the rolls. */
    public void setDice(IntSupplier dice) {
        this.dice = dice;
    }

    public CombatReport attack(HexCoordinate target, List<Unit> attackers) {
        return attack(target, attackers, CombatDecisions.AUTO);
    }

    /**
     * Attacks one hex with units from adjacent hexes. A hex can be attacked once per Combat phase and each unit
     * attacks once (rulebook 12.0).
     */
    public CombatReport attack(HexCoordinate target, List<Unit> attackers, CombatDecisions decisions) {
        if (!(gameState instanceof Combat)) {
            throw new IllegalStateException("Units can only attack during the Combat phase, not " + gameState.phaseName());
        }
        Hex defendHex = gameMap.getHex(target);
        if (defendHex == null) {
            throw new IllegalArgumentException("No hex at " + target);
        }
        if (attackers.isEmpty()) {
            throw new IllegalArgumentException("No attacking units");
        }
        for (Unit unit : attackers) {
            Hex from = gameMap.hexOf(unit);
            if (unit.faction != phasingFaction || !unit.isCombatUnit() || from == null) {
                throw new IllegalArgumentException(unit.id + " cannot attack now");
            }
            if (unit.isDisorganized()) {
                throw new IllegalArgumentException(unit.id + " is Disorganized and cannot attack");
            }
            if (unit.hasAttacked) {
                throw new IllegalStateException(unit.id + " has already attacked this phase");
            }
            if (from.getCdn().distanceTo(target) != 1) {
                throw new IllegalArgumentException(unit.id + " is not adjacent to " + target);
            }
        }
        if (!defendHex.hasCombatUnitOf(phasingFaction.opponent())) {
            throw new IllegalArgumentException("No enemy combat units at " + target);
        }
        if (!attackedHexes.add(target)) {
            throw new IllegalStateException(target + " has already been attacked this phase");
        }
        attackers.forEach(u -> u.hasAttacked = true);
        return CombatResolver.resolve(gameMap, target, attackers, dice, decisions);
    }

    /** Owner's choice of which units to lose when a hex is overstacked. */
    public void eliminateUnits(HexCoordinate at, List<Unit> units) {
        Hex hex = gameMap.getHex(at);
        if (hex == null || !hex.getUnits().containsAll(units)) {
            throw new IllegalArgumentException("Those units are not at " + at);
        }
        hex.getUnits().removeAll(units);
    }

    /** The end of a phase cannot be reached while any hex is overstacked (rulebook 4.0). */
    public void requireNoOverstacking() {
        List<HexCoordinate> overstacked = gameMap.getHexMap().values().stream()
                .filter(Hex::isOverstacked)
                .map(Hex::getCdn)
                .toList();
        if (!overstacked.isEmpty()) {
            throw new IllegalStateException("Overstacked hexes must be fixed first: " + overstacked);
        }
    }

    // The methods below are called by the GameState classes as they advance the game.

    public void beginTurn(int turn) {
        currentTurn = turn;
        sequence = 0;
        sequencesThisTurn = SEQUENCES_WITHOUT_OFFENSIVE;
    }

    public void declareOffensive() {
        sequencesThisTurn = SEQUENCES_WITH_OFFENSIVE;
        events.add(gameMap.getInitiativeFaction() + " declares an Offensive: " + SEQUENCES_WITH_OFFENSIVE
                + " Operational Sequences this turn.");
    }

    /** Things that happened on their own (reinforcements, offensives) since the last call, for display. */
    public List<String> drainEvents() {
        List<String> drained = List.copyOf(events);
        events.clear();
        return drained;
    }

    public void beginSequence() {
        sequence++;
        phasingFaction = gameMap.getInitiativeFaction();
    }

    public void switchPhasingFaction() {
        phasingFaction = phasingFaction.opponent();
    }

    public void beginCombatPhase() {
        attackedHexes.clear();
        phasingUnits().forEach(unit -> unit.hasAttacked = false);
    }

    /**
     * Marker Adjustment (rulebook 14.3), applied to both sides' units: Reorganizing units outside an enemy ZoC
     * recover, Disorganized units become Reorganizing and Exploitation markers are removed.
     */
    public void adjustMarkers() {
        for (Faction faction : Faction.values()) {
            Set<HexCoordinate> enemyZoc = MovementRules.zoneOfControl(gameMap, faction.opponent());
            for (Hex hex : gameMap.getHexMap().values()) {
                for (Unit unit : hex.getUnits()) {
                    if (unit.faction != faction) {
                        continue;
                    }
                    if (unit.reorganizing && !enemyZoc.contains(hex.getCdn())) {
                        unit.reorganizing = false;
                    }
                    if (unit.disorganized) {
                        unit.disorganized = false;
                        unit.reorganizing = true;
                    }
                    unit.exploitation = false;
                }
            }
        }
    }

    private java.util.stream.Stream<Unit> phasingUnits() {
        return gameMap.getHexMap().values().stream()
                .flatMap(hex -> hex.getUnits().stream())
                .filter(unit -> unit.faction == phasingFaction);
    }

    /** Units of the player about to move have not moved yet, and any reinforcements due now arrive. */
    public void beginMovementPhase() {
        phasingUnits().forEach(unit -> unit.hasMoved = false);
        for (Reinforcement r : gameMap.getReinforcements()) {
            if (r.turn() == currentTurn && r.sequence() == sequence && r.unit().faction == phasingFaction) {
                gameMap.getHex(r.at()).getUnits().add(r.unit());
                events.add(r.unit().id + " arrives at " + r.at().q() + "," + r.at().r() + ".");
            }
        }
    }
}
