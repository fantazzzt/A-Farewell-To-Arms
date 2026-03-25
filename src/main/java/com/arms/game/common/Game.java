package com.arms.game.common;

import com.arms.game.common.state.GameState;
import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
public class Game {

    private final List<Player> players;
    private Date currentDay;
    private int operation;
    private GameState gameState;
    private GameMap gameMap;

    public void processState(GameState state) {
        // TODO
    }
}
