package com.arms.game.common;

import com.arms.game.models.Faction;
import com.arms.game.models.Player;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.AttackUnit;
import com.arms.game.models.squares.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatPhaseTests {

    private Game game;
    private final HexCoordinate alpini = HexCoordinate.of(3, 0);

    @BeforeEach
    void setUp() throws IOException {
        game = GameBuilder.initializeGame()
                .addPlayer(new Player(true, "Caden", Faction.CENTRAL))
                .addPlayer(new Player(false, "Opponent", Faction.ENTENTE))
                .addScenario("montegrappa")
                .startGame();
    }

    private void advanceTo(String phase) {
        while (!game.getGameState().phaseName().equals(phase)) {
            game.advancePhase();
        }
    }

    private Hex hex(int q, int r) {
        return game.getGameMap().getHex(HexCoordinate.of(q, r));
    }

    /** Moves the German-side division next to the Alpini regiment's trench at (3,0). */
    private Unit divisionAt(int q, int r) {
        Unit division = hex(0, 0).getUnits().getFirst();
        hex(0, 0).getUnits().remove(division);
        hex(q, r).getUnits().add(division);
        return division;
    }

    private Unit regimentAt(int q, int r) {
        Unit regiment = AttackUnit.factory("extra", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 4, false,
                new com.arms.game.common.Pair<>(4, 2), new com.arms.game.common.Pair<>(4, 2),
                new com.arms.game.common.Pair<>(4, 4));
        hex(q, r).getUnits().add(regiment);
        return regiment;
    }

    @Test
    void cannotAttackOutsideTheCombatPhase() {
        Unit division = divisionAt(2, 0);
        advanceTo("Movement");
        assertThrows(IllegalStateException.class, () -> game.attack(alpini, List.of(division)));
    }

    @Test
    void attackRulesAreEnforced() {
        Unit division = divisionAt(2, 0);
        Unit farAway = regimentAt(0, 0);
        Unit enemyUnit = hex(3, 1).getUnits().getFirst();
        advanceTo("Combat");

        assertThrows(IllegalArgumentException.class, () -> game.attack(alpini, List.of()));
        assertThrows(IllegalArgumentException.class, () -> game.attack(alpini, List.of(farAway)));       // not adjacent
        assertThrows(IllegalArgumentException.class, () -> game.attack(alpini, List.of(enemyUnit)));     // not the phasing side
        assertThrows(IllegalArgumentException.class, () -> game.attack(HexCoordinate.of(1, 0), List.of(division))); // empty hex

        division.disorganized = true;
        assertThrows(IllegalArgumentException.class, () -> game.attack(alpini, List.of(division)));
    }

    @Test
    void eachHexAndEachUnitAttacksOnlyOncePerPhase() {
        Unit division = divisionAt(2, 0);
        Unit second = regimentAt(2, 1);
        Unit third = regimentAt(2, 1);
        advanceTo("Combat");

        game.setDice(() -> 3);
        game.attack(alpini, List.of(division));
        assertTrue(division.hasAttacked);
        assertThrows(IllegalStateException.class, () -> game.attack(alpini, List.of(second))); // hex attacked already

        third.hasAttacked = true;
        assertThrows(IllegalStateException.class, () -> game.attack(HexCoordinate.of(3, 1), List.of(third)));
    }

    @Test
    void attackersAreRemovedWhenTheyDie() {
        // 8 vs a trenched Alpini regiment (6 doubled = 12) is far below 1/2, so the attacker loses 3 steps.
        Unit division = divisionAt(2, 0);
        advanceTo("Combat");
        game.setDice(() -> {
            throw new AssertionError("no dice should be rolled");
        });

        var report = game.attack(alpini, List.of(division));
        assertTrue(report.attackerAutoLoss());
        assertTrue(hex(2, 0).getUnits().isEmpty());
    }

    @Test
    void combatFlagsResetNextCombatPhase() {
        Unit bystander = regimentAt(0, 0);
        advanceTo("Combat");
        bystander.hasAttacked = true;

        advanceTo("Operation");
        advanceTo("Combat");
        assertFalse(bystander.hasAttacked);
    }

    @Test
    void markersAdjustAtTheEndOfExploitation() {
        Unit division = hex(0, 0).getUnits().getFirst();
        Unit alpiniRegiment = hex(3, 0).getUnits().getFirst();
        division.disorganized = true;
        division.exploitation = true;
        alpiniRegiment.reorganizing = true;

        advanceTo("Exploitation");
        game.advancePhase();

        assertFalse(division.disorganized);
        assertTrue(division.reorganizing);  // flipped to Reorganizing
        assertFalse(division.exploitation); // removed
        assertFalse(alpiniRegiment.reorganizing); // recovered: not in an enemy ZoC
    }

    @Test
    void reorganizingUnitsInEnemyZocStayReorganizing() {
        Unit division = divisionAt(2, 0); // adjacent to the Alpini regiment
        division.reorganizing = true;
        advanceTo("Exploitation");
        game.advancePhase();
        assertTrue(division.reorganizing);
    }

    @Test
    void ownerCanEliminateUnitsToFixOverstacking() {
        advanceTo("Movement");
        Unit extraDivision = hex(3, 1).getUnits().getFirst();
        hex(3, 1).getUnits().remove(extraDivision);
        hex(0, 0).getUnits().add(extraDivision);
        assertThrows(IllegalStateException.class, () -> game.advancePhase());

        game.eliminateUnits(HexCoordinate.of(0, 0), List.of(extraDivision));
        game.advancePhase();
        assertEquals("Combat", game.getGameState().phaseName());
    }
}
