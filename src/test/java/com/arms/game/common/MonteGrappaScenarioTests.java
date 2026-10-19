package com.arms.game.common;

import com.arms.game.models.Faction;
import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.Unit;
import com.arms.game.rules.Victory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/** The shipped placeholder scenario: it loads, plays through, and its reinforcement arrives on time. */
class MonteGrappaScenarioTests {

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
        game.advancePhase();
        while (!game.getGameState().phaseName().equals(phase)) {
            game.advancePhase();
        }
    }

    @Test
    void loadsWithNoOverstackingAndEveryUnitOnTheMap() {
        GameMap map = game.getGameMap();
        assertEquals(24, map.getHexMap().size());
        assertEquals(2, map.getObjectiveHexes().size());
        assertTrue(map.getHexMap().values().stream().noneMatch(h -> h.isOverstacked()));
        assertEquals(6, map.getHexMap().values().stream().mapToInt(h -> h.getUnits().size()).sum());
        assertEquals(1, map.getReinforcements().size());
    }

    @Test
    void fiftiethDivisionArrivesAtTheStartOfTheSecondSequence() {
        advanceTo("Movement"); // turn 17, sequence 1
        assertNull(find("50 ID AH"));
        game.drainEvents();

        while (game.getSequence() < 2 || !game.getGameState().phaseName().equals("Movement")
                || game.getPhasingFaction() != Faction.CENTRAL) {
            game.advancePhase();
        }
        assertEquals(2, game.getSequence());
        assertEquals(HexCoordinate.of(0, 0), game.getGameMap().hexOf(find("50 ID AH")).getCdn());
        assertTrue(game.drainEvents().stream().anyMatch(e -> e.contains("50 ID AH arrives")));
    }

    @Test
    void victoryDependsOnWhatTheCentralPowersHold() {
        GameMap map = game.getGameMap();
        assertEquals(Faction.ENTENTE, Victory.evaluate(map).winner()); // holds no trenches at the start

        Unit division = find("4 ID AH");
        map.hexOf(division).getUnits().remove(division);
        map.getHex(HexCoordinate.of(3, 0)).getUnits().add(division); // 1 trench hex
        map.getHex(HexCoordinate.of(3, 3)).getUnits().add(find("13 SchD AH")); // 2 trench hexes
        assertNull(Victory.evaluate(map).winner()); // 2 points equals the threshold: a draw

        map.getHex(HexCoordinate.of(3, 0)).getUnits().add(find("Alpenkorps"));
        map.getHex(HexCoordinate.of(3, 0)).getUnits().clear();
        map.getHex(HexCoordinate.of(3, 0)).getUnits().add(find("Alpenkorps"));
        map.getHex(HexCoordinate.of(3, 1)).getUnits().clear();
        map.getHex(HexCoordinate.of(3, 1)).getUnits().add(division);
        assertEquals(Faction.CENTRAL, Victory.evaluate(map).winner()); // 3 trench hexes
    }

    @Test
    void holdingBothObjectivesIsAnOperationalVictory() {
        GameMap map = game.getGameMap();
        map.getHex(HexCoordinate.of(4, 1)).getUnits().clear();
        map.getHex(HexCoordinate.of(4, 1)).getUnits().add(find("4 ID AH"));
        map.getHex(HexCoordinate.of(4, 2)).getUnits().add(find("13 SchD AH"));
        Victory.Outcome outcome = Victory.evaluate(map);
        assertEquals(Faction.CENTRAL, outcome.winner());
        assertTrue(outcome.description().startsWith("Operational Victory"));
    }

    private Unit find(String id) {
        return game.getGameMap().getHexMap().values().stream()
                .flatMap(h -> h.getUnits().stream())
                .filter(u -> u.id.equals(id))
                .findFirst().orElse(null);
    }
}
