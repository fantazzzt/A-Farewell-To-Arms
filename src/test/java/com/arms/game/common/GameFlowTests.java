package com.arms.game.common;

import com.arms.game.models.Faction;
import com.arms.game.models.Player;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameFlowTests {

    private Game game;

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

    @Test
    void startsAtTheBeginning() {
        assertEquals("Start", game.getGameState().phaseName());
        game.advancePhase();
        assertEquals("Administrative", game.getGameState().phaseName());
        assertEquals(17, game.getCurrentTurn());
    }

    @Test
    void firstSequenceOrder() {
        List<String> phases = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            game.advancePhase();
            phases.add(game.getGameState().phaseName());
        }
        assertEquals(List.of("Administrative", "Operation", "Weather",
                "Movement", "Combat", "Disengagement", "Exploitation",
                "Movement", "Combat", "Disengagement", "Exploitation", "Operation"), phases);
    }

    @Test
    void playersAlternateWithinASequence() {
        advanceTo("Movement");
        assertEquals(Faction.CENTRAL, game.getPhasingFaction());
        advanceTo("Exploitation");
        game.advancePhase();
        assertEquals("Movement", game.getGameState().phaseName());
        assertEquals(Faction.ENTENTE, game.getPhasingFaction());
    }

    @Test
    void monteGrappaPlaysFourSequencesThenEnds() {
        int movementPhases = 0;
        while (!game.getGameState().phaseName().equals("GameOver")) {
            game.advancePhase();
            if (game.getGameState().phaseName().equals("Movement")) {
                movementPhases++;
            }
        }
        assertEquals(8, movementPhases); // 4 sequences x 2 players
        assertEquals(18, game.getCurrentTurn());
        assertThrows(IllegalStateException.class, () -> game.advancePhase());
    }

    @Test
    void offensiveTurnHasThreeSequencesOtherTurnsOne() {
        advanceTo("Operation");
        assertEquals(3, game.getSequencesThisTurn());
        while (game.getCurrentTurn() == 17) {
            game.advancePhase();
        }
        advanceTo("Operation");
        assertEquals(1, game.getSequencesThisTurn());
        assertEquals(18, game.getCurrentTurn());
    }

    @Test
    void unitsOnlyMoveInMovementPhaseAndOnlyForThePhasingPlayer() {
        HexCoordinate start = HexCoordinate.of(0, 0);
        Unit centralDivision = game.getGameMap().getHex(start).getUnits().getFirst();
        HexCoordinate next = HexCoordinate.of(1, 0);

        assertThrows(IllegalStateException.class, () -> game.moveUnits(start, List.of(centralDivision), next));

        advanceTo("Movement");
        game.moveUnits(start, List.of(centralDivision), next);
        assertTrue(game.getGameMap().getHex(next).getUnits().contains(centralDivision));

        Unit alpini = game.getGameMap().getHex(HexCoordinate.of(3, 0)).getUnits().getFirst();
        assertThrows(IllegalArgumentException.class,
                () -> game.moveUnits(HexCoordinate.of(3, 0), List.of(alpini), HexCoordinate.of(2, 0)));
    }

    @Test
    void unitsCanMoveAgainNextTurn() {
        HexCoordinate start = HexCoordinate.of(0, 0);
        Unit division = game.getGameMap().getHex(start).getUnits().getFirst();
        advanceTo("Movement");
        game.moveUnits(start, List.of(division), HexCoordinate.of(1, 0));
        assertTrue(division.hasMoved);

        advanceTo("Operation");
        advanceTo("Movement");
        assertFalse(division.hasMoved);
    }

    @Test
    void overstackedHexBlocksEndOfMovement() {
        advanceTo("Movement");
        // Put both divisions (3 + 3 points) in one hex.
        Unit alien = game.getGameMap().getHex(HexCoordinate.of(3, 1)).getUnits().getFirst();
        game.getGameMap().getHex(HexCoordinate.of(3, 1)).getUnits().remove(alien);
        game.getGameMap().getHex(HexCoordinate.of(0, 0)).getUnits().add(alien);

        assertThrows(IllegalStateException.class, () -> game.advancePhase());
        assertEquals("Movement", game.getGameState().phaseName());
    }

    @Test
    void startingWithoutAScenarioFails() {
        GameBuilder builder = GameBuilder.initializeGame()
                .addPlayer(new Player(true, "A", Faction.CENTRAL))
                .addPlayer(new Player(false, "B", Faction.ENTENTE));
        assertThrows(IllegalStateException.class, builder::startGame);
    }
}
