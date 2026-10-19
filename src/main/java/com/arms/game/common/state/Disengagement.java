package com.arms.game.common.state;

import com.arms.game.common.Game;

public final class Disengagement implements GameState {
    @Override
    public GameState advance(Game game) {
        return new Exploitation();
    }
}
