package com.arms.game.cli;

import com.arms.game.combat.CombatDecisions;
import com.arms.game.combat.CombatReport;
import com.arms.game.combat.CombatResolver;
import com.arms.game.common.Game;
import com.arms.game.common.GameBuilder;
import com.arms.game.common.state.Combat;
import com.arms.game.common.state.GameOver;
import com.arms.game.common.state.Movement;
import com.arms.game.models.Faction;
import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.Unit;
import com.arms.game.rules.MovementRules;
import com.arms.game.rules.Victory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Two players take turns at one terminal. Hexes are written q,r.
 * Run with: ./mvnw -q spring-boot:run -Dspring-boot.run.main-class=com.arms.game.cli.GameCli
 */
public class GameCli {

    private static final int CELL_WIDTH = 9;
    private static final int ROW_INDENT = CELL_WIDTH / 2;

    private final Game game;
    private final BufferedReader in;
    private final PrintStream out;
    private int shownTurn = -1;
    private int shownSequence = -1;

    public GameCli(Game game, BufferedReader in, PrintStream out) {
        this.game = game;
        this.in = in;
        this.out = out;
    }

    public static void main(String[] args) throws IOException {
        String scenario = args.length > 0 ? args[0] : "montegrappa";
        Game game = GameBuilder.initializeGame()
                .addPlayer(new Player(true, "Central Powers", Faction.CENTRAL))
                .addPlayer(new Player(false, "Entente", Faction.ENTENTE))
                .addScenario(scenario)
                .startGame();
        new GameCli(game, new BufferedReader(new InputStreamReader(System.in)), System.out).run();
    }

    public void run() throws IOException {
        out.println("A FAREWELL TO ARMS - " + game.getGameMap().getNameOfMap());
        out.println("Type 'help' for commands.");
        autoAdvance();
        announcePhase();
        printMap();

        while (!(game.getGameState() instanceof GameOver)) {
            out.print(prompt());
            out.flush();
            String line = in.readLine();
            if (line == null || !handle(line.trim())) {
                return;
            }
        }
        announceGameOver();
    }

    // ---- commands ----

    private boolean handle(String line) {
        if (line.isEmpty()) {
            return true;
        }
        String[] words = line.split("\\s+");
        List<String> args = List.of(words).subList(1, words.length);
        try {
            switch (words[0].toLowerCase()) {
                case "help", "?" -> printHelp();
                case "map", "m" -> printMap();
                case "units", "u" -> printUnits();
                case "info", "i" -> info(args);
                case "reach", "r" -> reach(args);
                case "move", "mv" -> move(args);
                case "attack", "a" -> attack(args);
                case "drop" -> drop(args);
                case "status", "s" -> out.println(statusLine());
                case "next", "n" -> next();
                case "quit", "q" -> {
                    out.println("Goodbye.");
                    return false;
                }
                default -> out.println("Unknown command '" + words[0] + "'. Type 'help'.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            out.println("! " + e.getMessage());
        }
        return true;
    }

    private void printHelp() {
        out.println("""
                Hexes are written q,r (see the map labels).
                  map                        draw the map
                  units                      list every unit
                  info q,r                   hex details with numbered units
                  reach q,r [n ...]          where the stack (or units n...) in that hex can move
                  move q,r q,r [n ...]       move the stack (or units n...) from the first hex to the second
                  attack q,r q,r [q,r ...]   attack the first hex with all eligible units from the others
                  drop q,r n                 eliminate unit n in a hex (to fix overstacking)
                  status                     show turn and phase
                  next                       end the current phase
                  quit""");
    }

    private void info(List<String> args) {
        Hex hex = hex(arg(args, 0));
        out.println(describeHex(hex));
        List<Unit> units = hex.getUnits();
        for (int i = 0; i < units.size(); i++) {
            out.println("  " + (i + 1) + ". " + describeUnit(units.get(i)));
        }
        if (units.isEmpty()) {
            out.println("  (empty)");
        }
    }

    private void reach(List<String> args) {
        Hex from = hex(arg(args, 0));
        List<Unit> movers = pickUnits(from, args.subList(1, args.size()), u -> u.faction == game.getPhasingFaction());
        Map<HexCoordinate, Integer> reachable = MovementRules.reachableHexes(game.getGameMap(), from.getCdn(), movers);
        out.println("Stack moves at " + MovementRules.stackMovement(movers) + " MP. Reachable hexes (MP left):");
        reachable.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<HexCoordinate, Integer> e) -> e.getKey().r())
                        .thenComparingInt(e -> e.getKey().q()))
                .forEach(e -> out.println("  " + label(e.getKey()) + " (" + e.getValue() + ")"));
    }

    private void move(List<String> args) {
        Hex from = hex(arg(args, 0));
        Hex to = hex(arg(args, 1));
        List<Unit> movers = pickUnits(from, args.subList(2, args.size()),
                u -> u.faction == game.getPhasingFaction() && !u.hasMoved);
        game.moveUnits(from.getCdn(), movers, to.getCdn());
        out.println("Moved " + names(movers) + " to " + label(to.getCdn()) + ".");
    }

    private void attack(List<String> args) {
        if (!(game.getGameState() instanceof Combat)) {
            throw new IllegalStateException("Units can only attack during the Combat phase, not "
                    + game.getGameState().phaseName());
        }
        Hex target = hex(arg(args, 0));
        if (args.size() < 2) {
            throw new IllegalArgumentException("Say where the attackers are: attack q,r q,r [q,r ...]");
        }
        List<Unit> attackers = new ArrayList<>();
        for (String from : args.subList(1, args.size())) {
            hex(from).getUnits().stream()
                    .filter(u -> u.faction == game.getPhasingFaction() && u.isCombatUnit()
                            && !u.isDisorganized() && !u.hasAttacked)
                    .forEach(attackers::add);
        }
        if (attackers.isEmpty()) {
            throw new IllegalArgumentException("No eligible attackers in those hexes");
        }
        out.println("Attacking " + label(target.getCdn()) + " with " + names(attackers) + ".");
        printReport(game.attack(target.getCdn(), attackers, new PromptDecisions()));
    }

    private void drop(List<String> args) {
        Hex hex = hex(arg(args, 0));
        List<Unit> units = pickUnits(hex, args.subList(1, args.size()), u -> false);
        game.eliminateUnits(hex.getCdn(), units);
        out.println("Eliminated " + names(units) + ".");
    }

    private void next() {
        if (isInteractive()) {
            game.advancePhase();
        }
        autoAdvance();
        announcePhase();
    }

    // ---- turn flow ----

    private boolean isInteractive() {
        return game.getGameState() instanceof Movement || game.getGameState() instanceof Combat;
    }

    /** Phases with nothing for a player to do yet (or that need no input) are skipped. */
    private void autoAdvance() {
        while (!isInteractive() && !(game.getGameState() instanceof GameOver)) {
            game.advancePhase();
        }
    }

    private void announcePhase() {
        if (game.getGameState() instanceof GameOver) {
            printEvents();
            return;
        }
        if (game.getCurrentTurn() != shownTurn || game.getSequence() != shownSequence) {
            shownTurn = game.getCurrentTurn();
            shownSequence = game.getSequence();
            out.println();
            out.println("=== Turn " + shownTurn + ", Operational Sequence " + shownSequence
                    + " of " + game.getSequencesThisTurn() + " ===");
        }
        printEvents();
        out.println(game.getPhasingFaction() + " " + game.getGameState().phaseName() + " phase.");
    }

    private void printEvents() {
        game.drainEvents().forEach(e -> out.println("* " + e));
    }

    private void announceGameOver() {
        Victory.Outcome outcome = Victory.evaluate(game.getGameMap());
        out.println();
        out.println("=== GAME OVER ===");
        out.println(outcome.description());
    }

    private String statusLine() {
        return "Turn " + game.getCurrentTurn() + ", sequence " + game.getSequence() + "/" + game.getSequencesThisTurn()
                + ", " + game.getPhasingFaction() + " " + game.getGameState().phaseName();
    }

    private String prompt() {
        return "[T" + game.getCurrentTurn() + " seq " + game.getSequence() + "/" + game.getSequencesThisTurn()
                + " | " + game.getPhasingFaction() + " | " + game.getGameState().phaseName() + "] > ";
    }

    // ---- display ----

    private void printMap() {
        GameMap map = game.getGameMap();
        int minQ = map.getHexMap().keySet().stream().mapToInt(HexCoordinate::q).min().orElse(0);
        int maxQ = map.getHexMap().keySet().stream().mapToInt(HexCoordinate::q).max().orElse(0);
        int minR = map.getHexMap().keySet().stream().mapToInt(HexCoordinate::r).min().orElse(0);
        int maxR = map.getHexMap().keySet().stream().mapToInt(HexCoordinate::r).max().orElse(0);

        out.println();
        for (int r = minR; r <= maxR; r++) {
            StringBuilder labels = new StringBuilder(" ".repeat(ROW_INDENT * (r - minR)));
            StringBuilder content = new StringBuilder(labels);
            for (int q = minQ; q <= maxQ; q++) {
                Hex hex = map.getHex(HexCoordinate.of(q, r));
                labels.append(pad(hex == null ? "" : label(hex.getCdn()) + (map.getObjectiveHexes().contains(hex.getCdn()) ? "*" : "")));
                content.append(pad(hex == null ? "" : cell(hex)));
            }
            out.println(labels);
            out.println(content);
        }
        out.println();
        out.println("Terrain: . clear  f forest  w marsh  t town  T city  m mountain  M high mountain   # trench   * objective");
        out.println("Units: C = Central Powers, E = Entente, with a count.  ! = some units Disorganized");
    }

    private static String pad(String text) {
        return String.format("%-" + CELL_WIDTH + "s", text);
    }

    private static String cell(Hex hex) {
        String terrain = switch (hex.getTerrain()) {
            case CLEAR -> ".";
            case FOREST -> "f";
            case MARSH -> "w";
            case TOWN -> "t";
            case CITY -> "T";
            case MOUNTAIN -> "m";
            case HIGH_MOUNTAIN -> "M";
        };
        StringBuilder cell = new StringBuilder(terrain).append(hex.isTrench() ? "#" : " ").append(" ");
        for (Faction faction : Faction.values()) {
            long count = hex.getUnits().stream().filter(u -> u.faction == faction).count();
            if (count > 0) {
                cell.append(faction == Faction.CENTRAL ? "C" : "E").append(count);
            }
        }
        if (hex.getUnits().stream().anyMatch(Unit::isDisorganized)) {
            cell.append("!");
        }
        return cell.toString();
    }

    private void printUnits() {
        for (Faction faction : Faction.values()) {
            out.println(faction + ":");
            game.getGameMap().getHexMap().values().stream()
                    .sorted(Comparator.comparingInt((Hex h) -> h.getCdn().r()).thenComparingInt(h -> h.getCdn().q()))
                    .forEach(hex -> hex.getUnits().stream()
                            .filter(u -> u.faction == faction)
                            .forEach(u -> out.println("  " + label(hex.getCdn()) + "  " + describeUnit(u))));
        }
    }

    private String describeHex(Hex hex) {
        boolean objective = game.getGameMap().getObjectiveHexes().contains(hex.getCdn());
        return label(hex.getCdn()) + "  " + hex.getTerrain() + (hex.isTrench() ? " + trench" : "")
                + (objective ? "  [objective]" : "") + "  move cost " + hex.movementCost()
                + "  stacking " + hex.stackingValue() + "/" + Hex.MAX_STACKING_VALUE;
    }

    private static String describeUnit(Unit u) {
        StringBuilder text = new StringBuilder(u.id).append("  ").append(u.faction).append(' ').append(u.rank)
                .append("  A").append(u.attackFactor())
                .append(" D").append(u.defenseFactor())
                .append(" M").append(u.currentMovement())
                .append("  morale ").append(u.morale)
                .append("  steps ").append(u.maxSteps() - u.stepsLost).append('/').append(u.maxSteps());
        if (u.stepLoss) {
            text.append(" [reduced]");
        }
        if (u.disorganized) {
            text.append(" [Disorganized]");
        }
        if (u.reorganizing) {
            text.append(" [Reorganizing]");
        }
        if (u.exploitation) {
            text.append(" [Exploitation]");
        }
        if (u.hasMoved) {
            text.append(" [moved]");
        }
        if (u.hasAttacked) {
            text.append(" [attacked]");
        }
        return text.toString();
    }

    private void printReport(CombatReport report) {
        out.println("Attack " + report.attackFactors() + " vs defence " + report.defenseFactors()
                + ", final odds " + oddsLabel(report.columnPosition()) + ".");
        if (report.attackerAutoLoss()) {
            out.println("Odds below 1/2: the attacker loses 3 steps without a roll.");
        } else {
            out.println("White dice " + report.whiteDiceTotal() + ": attacker takes " + report.attackerHitPoints()
                    + " HP, defender takes " + report.defenderHitPoints() + " HP.");
            out.println("Tactical Coordination: attacker " + report.attackerCoordination()
                    + ", defender " + report.defenderCoordination() + ".");
            if (report.defenderRetreatHexes() > 0) {
                out.println("Defender retreated " + report.defenderRetreatHexes() + " hex(es).");
            }
            if (report.attackerAdvanced()) {
                out.println("Attackers advanced into the attacked hex.");
            }
        }
        if (!report.eliminated().isEmpty()) {
            out.println("Eliminated: " + String.join(", ", report.eliminated()));
        }
    }

    /** 1/1 is position 0, N/1 is N-1 and 1/N is -(N-1). */
    static String oddsLabel(int position) {
        return position >= 0 ? (position + 1) + "/1" : "1/" + (1 - position);
    }

    // ---- parsing helpers ----

    private static String label(HexCoordinate c) {
        return c.q() + "," + c.r();
    }

    private static String arg(List<String> args, int index) {
        if (index >= args.size()) {
            throw new IllegalArgumentException("Missing argument. Type 'help'.");
        }
        return args.get(index);
    }

    private Hex hex(String text) {
        String[] parts = text.split(",");
        try {
            if (parts.length != 2) {
                throw new NumberFormatException();
            }
            Hex hex = game.getGameMap().getHex(HexCoordinate.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
            if (hex == null) {
                throw new IllegalArgumentException("There is no hex " + text);
            }
            return hex;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Hexes are written q,r like 2,1 (not '" + text + "')");
        }
    }

    /** Units picked by their 1-based position in the hex, or the default selection if none were named. */
    private static List<Unit> pickUnits(Hex hex, List<String> numbers, java.util.function.Predicate<Unit> byDefault) {
        List<Unit> picked = new ArrayList<>();
        if (numbers.isEmpty()) {
            hex.getUnits().stream().filter(byDefault).forEach(picked::add);
        } else {
            for (String number : numbers) {
                try {
                    picked.add(hex.getUnits().get(Integer.parseInt(number) - 1));
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("No unit " + number + " in " + label(hex.getCdn()));
                }
            }
        }
        if (picked.isEmpty()) {
            throw new IllegalArgumentException("No eligible units in " + label(hex.getCdn()));
        }
        return picked;
    }

    private static String names(List<Unit> units) {
        return units.stream().map(u -> u.id).collect(Collectors.joining(", "));
    }

    /** Asks the players at the terminal. If input ends it falls back to the automatic choices. */
    private class PromptDecisions implements CombatDecisions {
        @Override
        public int defenderRetreatHexes(int maxHexes, int hitPointsIncoming) {
            out.print("Defender may retreat up to " + maxHexes + " hex(es); " + hitPointsIncoming
                    + " HP incoming and each hex retreated removes " + CombatResolver.RETREAT_HP_PER_HEX
                    + ". Hexes to retreat [0-" + maxHexes + "]: ");
            out.flush();
            String answer = readLine();
            if (answer == null) {
                return CombatDecisions.AUTO.defenderRetreatHexes(maxHexes, hitPointsIncoming);
            }
            try {
                return Math.max(0, Math.min(maxHexes, Integer.parseInt(answer.trim())));
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public boolean attackerAdvances() {
            out.print("The hex is empty. Advance the attackers into it? [Y/n]: ");
            out.flush();
            String answer = readLine();
            return answer == null || !answer.trim().toLowerCase().startsWith("n");
        }

        private String readLine() {
            try {
                return in.readLine();
            } catch (IOException e) {
                return null;
            }
        }
    }
}
