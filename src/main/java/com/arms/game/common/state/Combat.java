package com.arms.game.common.state;

import com.arms.game.common.Game;

public final class Combat implements GameState {
    @Override
    public GameState advance(Game game) {
        game.requireNoOverstacking();
        return new Disengagement();
    }
}
