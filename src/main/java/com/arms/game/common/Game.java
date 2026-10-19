package com.arms.game.common;

import com.arms.game.common.state.GameState;
import com.arms.game.common.state.Movement;
import com.arms.game.common.state.Start;
import com.arms.game.models.Faction;
import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.Unit;
import com.arms.game.rules.MovementRules;
import lombok.Getter;

import java.util.List;

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
    }

    public void beginSequence() {
        sequence++;
        phasingFaction = gameMap.getInitiativeFaction();
    }

    public void switchPhasingFaction() {
        phasingFaction = phasingFaction.opponent();
    }

    /** Units of the player about to move have not moved yet. */
    public void resetMovement() {
        gameMap.getHexMap().values().stream()
                .flatMap(hex -> hex.getUnits().stream())
                .filter(unit -> unit.faction == phasingFaction)
                .forEach(unit -> unit.hasMoved = false);
    }
}
