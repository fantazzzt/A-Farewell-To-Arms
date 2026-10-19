package com.arms.game.common.state;

import com.arms.game.common.Game;

/**
 * Start of an Operational Sequence. The initiative player always phases first.
 */
public final class Operation implements GameState {
    @Override
    public GameState advance(Game game) {
        game.beginSequence();
        return new Weather();
    }
}
