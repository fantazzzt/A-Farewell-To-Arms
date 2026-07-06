package com.arms.game.models;

import lombok.Getter;

public class Dice {
    public enum DiceType {
        NORMAL(0), GERMAN(1), ITALIAN(3);

        private DiceType(int i) {
            this.dieType = i;
        }

        @Getter
        private final int dieType;
    }

    public static int roll() {
        return (int)(Math.random() * 6) + 1;
    }
}
