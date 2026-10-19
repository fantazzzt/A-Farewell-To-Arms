package com.arms.game.common.state;

import com.arms.game.common.Game;

public final class GameOver implements GameState {
    @Override
    public GameState advance(Game game) {
        throw new IllegalStateException("The game is over");
    }
}
