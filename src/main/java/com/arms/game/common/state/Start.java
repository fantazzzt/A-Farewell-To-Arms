package com.arms.game.common.state;

import com.arms.game.common.Game;

public final class Start implements GameState {
    @Override
    public GameState advance(Game game) {
        game.beginTurn(game.getGameMap().getFirstTurn());
        return new Administrative();
    }
}
