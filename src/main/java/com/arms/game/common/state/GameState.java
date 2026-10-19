package com.arms.game.common.state;

import com.arms.game.common.Game;

/**
 * One step of the turn sequence. Each state does its end-of-phase checks and returns the next state.
 */
public sealed interface GameState permits
        Start,
        Administrative,
        Operation,
        Weather,
        Movement,
        Combat,
        Disengagement,
        Exploitation,
        GameOver {

    GameState advance(Game game);

    default String phaseName() {
        return getClass().getSimpleName();
    }
}
