package com.arms.game.combat;

import com.arms.game.common.Pair;
import com.arms.game.models.Faction;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.AttackUnit;
import com.arms.game.models.squares.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

import static org.junit.jupiter.api.Assertions.*;

/** A row of clear hexes along q at r=0, (0,0) to (6,0). Attackers are CENTRAL, defenders ENTENTE. */
class CombatTests {

    private Map<HexCoordinate, Hex> hexes;
    private GameMap map;

    @BeforeEach
    void setUp() {
        hexes = new HashMap<>();
        for (int q = 0; q <= 6; q++) {
            put(q, Hex.Terrain.CLEAR, false);
        }
        rebuild();
    }

    private void put(int q, Hex.Terrain terrain, boolean trench) {
        HexCoordinate c = HexCoordinate.of(q, 0);
        hexes.put(c, new Hex(c, terrain, trench));
    }

    private void rebuild() {
        map = new GameMap("test", Faction.CENTRAL, 1, 1, hexes, List.of(), List.of());
    }

    private static Unit unit(String id, Faction faction, Unit.UnitRank rank, int morale, int attack, int defense) {
        return AttackUnit.factory(id, faction, rank, morale, false,
                new Pair<>(attack, attack / 2), new Pair<>(defense, defense / 2), new Pair<>(4, 4));
    }

    private Unit place(Unit unit, int q) {
        hexes.get(HexCoordinate.of(q, 0)).getUnits().add(unit);
        return unit;
    }

    private static IntSupplier dice(int... values) {
        int[] next = {0};
        return () -> values[next[0]++];
    }

    private static CombatDecisions retreat(int hexes) {
        return new CombatDecisions() {
            @Override
            public int defenderRetreatHexes(int maxHexes, int hitPointsIncoming) {
                return hexes;
            }

            @Override
            public boolean attackerAdvances() {
                return true;
            }
        };
    }

    @Test
    void ladderRoundsInTheDefendersFavour() {
        assertEquals(1, CombatResolver.ladderPosition(10, 5));  // 2/1
        assertEquals(1, CombatResolver.ladderPosition(8, 3));   // 2.67 -> 2/1
        assertEquals(0, CombatResolver.ladderPosition(5, 5));   // 1/1
        assertEquals(0, CombatResolver.ladderPosition(7, 5));   // 1.4 -> 1/1
        assertEquals(-1, CombatResolver.ladderPosition(5, 7));  // 1/1.4 -> 1/2
        assertEquals(-1, CombatResolver.ladderPosition(5, 10)); // 1/2
        assertEquals(-3, CombatResolver.ladderPosition(2, 8));  // 1/4
    }

    @Test
    void trenchesMultiplyDefenceDifferentlyPerSide() {
        put(3, Hex.Terrain.CLEAR, true);
        rebuild();
        Hex trench = hexes.get(HexCoordinate.of(3, 0));
        Unit central = unit("c", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 4, 4, 5);
        Unit entente = unit("e", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4, 4, 5);
        assertEquals(15, CombatResolver.defenseTotal(trench, List.of(central)));
        assertEquals(10, CombatResolver.defenseTotal(trench, List.of(entente)));
    }

    @Test
    void columnShiftsFromMoraleAndTerrain() {
        Unit morale5 = unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 5, 4, 4);
        Unit morale4 = unit("b", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4, 4, 4);
        Hex clear = hexes.get(HexCoordinate.of(0, 0));
        put(1, Hex.Terrain.MOUNTAIN, false);
        Hex mountain = hexes.get(HexCoordinate.of(1, 0));

        assertEquals(1, CombatResolver.columnShift(clear, morale5, morale4));
        assertEquals(-1, CombatResolver.columnShift(clear, morale4, morale5));
        assertEquals(0, CombatResolver.columnShift(mountain, morale5, morale4));
        assertEquals(-2, CombatResolver.columnShift(mountain, morale4, morale5));
    }

    @Test
    void crtGetsBetterForTheAttackerAsOddsRise() {
        for (int roll = 2; roll <= 12; roll++) {
            for (int c = 1; c < CRT.COLUMNS; c++) {
                CRT.Result worse = CRT.lookup(c - 1, roll);
                CRT.Result better = CRT.lookup(c, roll);
                assertTrue(better.defenderHp() >= worse.defenderHp());
                assertTrue(better.attackerHp() <= worse.attackerHp());
            }
        }
        assertEquals(new CRT.Result(0, 3), CRT.lookup(2, 11));
        assertThrows(IllegalArgumentException.class, () -> CRT.lookup(7, 7));
        assertThrows(IllegalArgumentException.class, () -> CRT.lookup(0, 13));
    }

    @Test
    void tacticalCoordinationTable() {
        assertEquals(Coordination.PERFECT, Coordination.fromRoll(6, -5));
        assertEquals(Coordination.PERFECT, Coordination.fromRoll(5, 1));
        assertEquals(Coordination.PARTIAL, Coordination.fromRoll(3, 0));
        assertEquals(Coordination.PARTIAL, Coordination.fromRoll(5, 0));
        assertEquals(Coordination.NONE, Coordination.fromRoll(2, 0));
        assertEquals(Coordination.NONE, Coordination.fromRoll(1, 0));
        assertEquals(Coordination.NONE, Coordination.fromRoll(1, 1));
        assertEquals(Coordination.MISUNDERSTOOD, Coordination.fromRoll(1, -1));
        assertEquals(Coordination.MISUNDERSTOOD, Coordination.fromRoll(4, -4));
    }

    @Test
    void eliteAndDisorganizedLeadUnitsChangeCoordination() {
        Unit elite = unit("elite", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 5, 4, 4);
        assertEquals(1, Coordination.modifier(elite));
        elite.disorganized = true;
        assertEquals(0, Coordination.modifier(elite));
        Unit division = unit("div", Faction.CENTRAL, Unit.UnitRank.DIVISION, 5, 4, 4);
        assertEquals(0, Coordination.modifier(division)); // divisions are never elite
    }

    // The next three are the worked examples B, C and D from rulebook 12.2.8.

    @Test
    void hitPointsExampleB_notEnoughForLeadDivisionSoRegimentLoses() {
        Unit division = unit("div", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4, 8, 8);
        Unit regiment = unit("reg", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 3, 4, 4);
        HitPoints.apply(List.of(division, regiment), division, 2);
        assertEquals(0, division.stepsLost);
        assertEquals(1, regiment.stepsLost);
        assertFalse(division.isDisorganized());
    }

    @Test
    void hitPointsExampleC_leftoverDisorganisesTheSurvivingLead() {
        Unit regiment = unit("reg", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 5, 4, 4);
        Unit division = unit("div", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4, 8, 8);
        HitPoints.apply(List.of(regiment, division), regiment, 3);
        assertEquals(1, regiment.stepsLost);
        assertTrue(regiment.disorganized);
        assertEquals(0, division.stepsLost);
    }

    @Test
    void hitPointsExampleD_secondStepEliminatesTheRegiment() {
        Unit regiment = unit("reg", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 5, 4, 4);
        Unit division = unit("div", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4, 8, 8);
        HitPoints.Result result = HitPoints.apply(List.of(regiment, division), regiment, 4);
        assertEquals(List.of(regiment), result.eliminated());
        assertEquals(0, division.stepsLost);
    }

    @Test
    void disorganisingSkipsUnitsAlreadyDisorganized() {
        Unit first = unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 5, 4, 4);
        Unit second = unit("b", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 4, 4, 4);
        first.disorganized = true;
        HitPoints.apply(List.of(first, second), first, 1);
        assertTrue(second.disorganized);
    }

    @Test
    void stepLossesHitTheHighestMoraleFirst() {
        Unit low = unit("low", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 3, 4, 4);
        Unit high = unit("high", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 6, 4, 4);
        HitPoints.Result result = HitPoints.applySteps(List.of(low, high), 3);
        assertEquals(List.of(high), result.eliminated()); // 2 steps, the 3rd then hits the other unit
        assertEquals(1, low.stepsLost);
    }

    @Test
    void disorganizedMovementIsHalved() {
        Unit u = unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 4, 4, 4); // movement 4
        u.disorganized = true;
        assertEquals(2, u.currentMovement());
    }

    @Test
    void goodOddsWithPerfectCoordinationRetreatsDefenderAndAdvances() {
        Unit attacker = place(unit("att", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4, 8, 9), 2);
        Unit defender = place(unit("def", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4, 4, 3), 3);

        // whites 6+5 on the 2/1 column -> 0 vs 3 HP. Attacker rolls 6 (Perfect), defender 3 (Partial).
        CombatReport report = CombatResolver.resolve(map, HexCoordinate.of(3, 0), List.of(attacker),
                dice(6, 5, 6, 3), CombatDecisions.AUTO);

        assertEquals(8, report.attackFactors());
        assertEquals(3, report.defenseFactors());
        assertEquals(1, report.columnPosition());
        assertEquals(3, report.defenderHitPoints());
        assertEquals(0, report.attackerHitPoints());
        assertEquals(Coordination.PERFECT, report.attackerCoordination());
        assertEquals(Coordination.PARTIAL, report.defenderCoordination());

        assertEquals(1, report.defenderRetreatHexes()); // AUTO retreats 1 hex, which soaks up all 3 HP
        assertTrue(hexes.get(HexCoordinate.of(4, 0)).getUnits().contains(defender));
        assertEquals(0, defender.stepsLost);
        assertFalse(defender.isDisorganized());

        assertTrue(report.attackerAdvanced());
        assertTrue(hexes.get(HexCoordinate.of(3, 0)).getUnits().contains(attacker));
        assertTrue(hexes.get(HexCoordinate.of(2, 0)).getUnits().isEmpty());
        assertTrue(attacker.exploitation);
    }

    @Test
    void retreatingTwoHexesDisorganisesTheDefenders() {
        Unit attacker = place(unit("att", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4, 8, 9), 2);
        Unit defender = place(unit("def", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4, 4, 3), 3);

        CombatReport report = CombatResolver.resolve(map, HexCoordinate.of(3, 0), List.of(attacker),
                dice(6, 5, 3, 5), retreat(2));

        assertEquals(2, report.defenderRetreatHexes());
        assertTrue(hexes.get(HexCoordinate.of(5, 0)).getUnits().contains(defender));
        assertTrue(defender.disorganized);
        assertEquals(0, defender.stepsLost); // 3 HP - 6 = 0
    }

    @Test
    void defenderThatCannotRetreatTakesTheFullHits() {
        Unit attacker = place(unit("att", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4, 8, 9), 5);
        Unit defender = place(unit("def", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4, 4, 3), 6); // map edge

        CombatReport report = CombatResolver.resolve(map, HexCoordinate.of(6, 0), List.of(attacker),
                dice(6, 5, 1, 5), CombatDecisions.AUTO);

        assertEquals(0, report.defenderRetreatHexes());
        assertEquals(1, defender.stepsLost); // 3 HP: a step loss (2) then Disorganised (1)
        assertTrue(defender.disorganized);
        assertFalse(report.attackerAdvanced()); // attacker rolled 1: no benefit
    }

    @Test
    void hopelessOddsCostTheAttackerThreeStepsWithoutRolling() {
        Unit attacker = place(unit("att", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 4, 1, 1), 2);
        place(unit("def", Faction.ENTENTE, Unit.UnitRank.DIVISION, 4, 9, 9), 3);

        CombatReport report = CombatResolver.resolve(map, HexCoordinate.of(3, 0), List.of(attacker),
                dice(), CombatDecisions.AUTO); // an empty dice supply proves nothing is rolled

        assertTrue(report.attackerAutoLoss());
        assertEquals(List.of("att"), report.eliminated());
        assertTrue(hexes.get(HexCoordinate.of(2, 0)).getUnits().isEmpty());
    }

    @Test
    void attackersCanBeEliminatedByCombatResults() {
        // 1/1 odds, whites 2+2 -> the worst row of the column; check losses are applied to the attacker.
        Unit attacker = place(unit("att", Faction.CENTRAL, Unit.UnitRank.DETACHMENT, 4, 4, 4), 2);
        place(unit("def", Faction.ENTENTE, Unit.UnitRank.DIVISION, 4, 4, 4), 3);
        CRT.Result expected = CRT.lookup(1, 4);

        CombatReport report = CombatResolver.resolve(map, HexCoordinate.of(3, 0), List.of(attacker),
                dice(2, 2, 1, 1), CombatDecisions.AUTO);

        assertEquals(expected.attackerHp(), report.attackerHitPoints());
        assertEquals(expected.defenderHp(), report.defenderHitPoints());
        assertTrue(expected.attackerHp() >= 2);
        assertTrue(report.eliminated().contains("att")); // a 1-step detachment dies to any step loss
    }
}
