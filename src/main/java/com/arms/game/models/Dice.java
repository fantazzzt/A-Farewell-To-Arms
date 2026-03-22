package com.arms.game.models;

import lombok.Getter;

public class Dice {
    public enum DiceKind {
        NORMAL(0), GERMAN(1), ITALIAN(3);

        private DiceKind(int i) {
            this.dieKind = i;
        }

        @Getter
        private final int dieKind;
    }

    public int roll(){
        return -1;
    }
}
