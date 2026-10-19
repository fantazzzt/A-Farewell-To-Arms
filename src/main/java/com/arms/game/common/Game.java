package com.arms.game.common;

import com.arms.game.common.state.GameState;
import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.Unit;
import com.arms.game.rules.MovementRules;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Game {

    private final List<Player> players;
    private int currentTurn;
    private int operation;
    private GameState gameState;
    private GameMap gameMap;

    public void moveUnits(HexCoordinate from, List<Unit> units, HexCoordinate to) {
        MovementRules.move(gameMap, from, units, to);
    }

    public void processState(GameState state) {
        // TODO
    }
}
