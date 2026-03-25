package com.arms.game.common.state;

public sealed interface GameState permits
        Start,
        Administrative,
        Movement,
        Disengagement,
        Exploitation,
        Operation,
        Weather,
        GameOver {
}
