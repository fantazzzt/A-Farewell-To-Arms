package com.arms.game.cli;

import com.arms.game.common.Game;
import com.arms.game.common.GameBuilder;
import com.arms.game.models.Faction;
import com.arms.game.models.Player;
import com.arms.game.models.map.HexCoordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class GameCliTests {

    private Game game;

    @BeforeEach
    void setUp() throws IOException {
        game = GameBuilder.initializeGame()
                .addPlayer(new Player(true, "Caden", Faction.CENTRAL))
                .addPlayer(new Player(false, "Opponent", Faction.ENTENTE))
                .addScenario("skirmish")
                .startGame();
    }

    private String play(String... commands) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BufferedReader in = new BufferedReader(new StringReader(String.join("\n", commands) + "\n"));
        new GameCli(game, in, new PrintStream(bytes)).run();
        return bytes.toString();
    }

    @Test
    void startsInTheFirstMovementPhaseWithAMap() throws IOException {
        String output = play("quit");
        assertTrue(output.contains("Turn 17, Operational Sequence 1 of 3"));
        assertTrue(output.contains("CENTRAL Movement phase."));
        assertTrue(output.contains("Terrain:"));
        assertTrue(output.contains("declares an Offensive"));
        assertTrue(output.contains("Goodbye."));
    }

    @Test
    void mapShowsUnitsAndTrenches() throws IOException {
        String output = play("map", "quit");
        assertTrue(output.contains("C1"));
        assertTrue(output.contains("E1"));
        assertTrue(output.contains("m#"));
        assertTrue(output.contains("3,1*"));
    }

    @Test
    void moveCommandMovesTheStack() throws IOException {
        String output = play("move 0,0 1,0", "quit");
        assertTrue(output.contains("Moved 50 ID AH to 1,0."));
        assertEquals(1, game.getGameMap().getHex(HexCoordinate.of(1, 0)).getUnits().size());
    }

    @Test
    void badInputIsReportedNotThrown() throws IOException {
        String output = play("move 0,0 6,6", "move 0,0 nonsense", "attack 3,0 2,0", "fly", "reach", "quit");
        assertTrue(output.contains("! There is no hex 6,6"));
        assertTrue(output.contains("! Hexes are written q,r"));
        assertTrue(output.contains("! Units can only attack during the Combat phase"));
        assertTrue(output.contains("Unknown command 'fly'"));
        assertTrue(output.contains("! Missing argument"));
    }

    @Test
    void infoAndReachDescribeTheBoard() throws IOException {
        String output = play("info 3,1", "reach 0,0", "units", "quit");
        assertTrue(output.contains("HIGH_MOUNTAIN + trench"));
        assertTrue(output.contains("23 DI"));
        assertTrue(output.contains("Reachable hexes (MP left):"));
        assertTrue(output.contains("ENTENTE:"));
    }

    @Test
    void attackReportsTheResult() throws IOException {
        game.setDice(() -> 3);
        String output = play("move 0,0 2,0", "next", "attack 3,0 2,0", "quit");
        assertTrue(output.contains("Attacking 3,0 with 50 ID AH."));
        assertTrue(output.contains("Odds below 1/2"));
        assertTrue(output.contains("Eliminated: 50 ID AH"));
    }

    @Test
    void retreatAndAdvancePromptsAreAnswered() throws IOException {
        // Put the Entente regiment in the open, next to the German division.
        var regiment = game.getGameMap().getHex(HexCoordinate.of(3, 0)).getUnits().getFirst();
        game.getGameMap().getHex(HexCoordinate.of(3, 0)).getUnits().clear();
        game.getGameMap().getHex(HexCoordinate.of(1, 0)).getUnits().add(regiment);
        int[] rolls = {6, 5, 6, 3}; // whites 11, attacker 6 (Perfect), defender 3 +1 elite (Partial)
        int[] next = {0};
        game.setDice(() -> rolls[next[0]++]);

        String output = play("next", "attack 1,0 0,0", "1", "y", "quit");

        assertTrue(output.contains("Defender may retreat up to 2 hex(es)"));
        assertTrue(output.contains("Tactical Coordination: attacker PERFECT, defender PARTIAL."));
        assertTrue(output.contains("Defender retreated 1 hex(es)."));
        assertTrue(output.contains("Advance the attackers into it?"));
        assertTrue(game.getGameMap().getHex(HexCoordinate.of(1, 1)).getUnits().contains(regiment));
    }

    @Test
    void playingEveryPhaseEndsTheGameWithAResult() throws IOException {
        String output = play("next", "next", "next", "next", "next", "next", "next", "next", "next", "next",
                "next", "next", "next", "next", "next", "next", "next", "next", "next", "next");
        assertTrue(output.contains("=== Turn 17, Operational Sequence 3 of 3 ==="));
        assertTrue(output.contains("=== Turn 18, Operational Sequence 1 of 1 ==="));
        assertTrue(output.contains("=== GAME OVER ==="));
        assertTrue(output.contains("Entente victory")); // nobody moved, so the CP scored 0 points
    }

    @Test
    void overstackingMustBeFixedBeforeAdvancing() throws IOException {
        var extra = game.getGameMap().getHex(HexCoordinate.of(3, 1)).getUnits().getFirst();
        game.getGameMap().getHex(HexCoordinate.of(3, 1)).getUnits().remove(extra);
        game.getGameMap().getHex(HexCoordinate.of(0, 0)).getUnits().add(extra);

        String output = play("next", "info 0,0", "drop 0,0 2", "next", "quit");
        assertTrue(output.contains("! Overstacked hexes must be fixed first"));
        assertTrue(output.contains("Eliminated 23 DI."));
        assertTrue(output.contains("CENTRAL Combat phase."));
    }

    @Test
    void oddsLabels() {
        assertEquals("1/1", GameCli.oddsLabel(0));
        assertEquals("3/1", GameCli.oddsLabel(2));
        assertEquals("1/2", GameCli.oddsLabel(-1));
        assertEquals("1/4", GameCli.oddsLabel(-3));
    }
}
