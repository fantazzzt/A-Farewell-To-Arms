package com.arms.game.common.state;

import com.arms.game.common.Game;

/**
 * Declaring Offensives (rulebook 8.0). Supply (9.0) and Air Superiority (17.0) are not implemented yet.
 */
public final class Administrative implements GameState {
    @Override
    public GameState advance(Game game) {
        if (game.getGameMap().getOffensiveTurns().contains(game.getCurrentTurn())) {
            game.declareOffensive();
        }
        return new Operation();
    }
}
