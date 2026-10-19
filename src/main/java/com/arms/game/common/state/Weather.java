package com.arms.game.common.state;

import com.arms.game.common.Game;

/**
 * TODO: roll for weather (rulebook 10.0). Monte Grappa fixes the weather, so there is nothing to roll yet.
 */
public final class Weather implements GameState {
    @Override
    public GameState advance(Game game) {
        game.resetMovement();
        return new Movement();
    }
}
