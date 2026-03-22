package com.arms.game.models;

import lombok.Getter;

public interface Unit {

    public enum UnitRank {
        DETACHMENT(0), REGIMENT(1), BATTALION(3), BRIGADE(2), DIVISION(4), SUPPORT(5), HQ(6);

        UnitRank(int i) {
            this.rank = i;
        }

        @Getter
        private final int rank;
    }

}
