package com.arms.game.models.squares;

import com.arms.game.common.Pair;
import lombok.Getter;

public abstract class Unit {

    // Tokens have two sides. The front side is the full value of that token.
    // The second side is the step loss side. Hence two of each value
    public final UnitRank rank;
    public final Pair<Integer, Integer> attack;
    public final Pair<Integer, Integer> defense;
    public final Pair<Integer, Integer> movement;

    protected Unit(UnitRank rank, Pair<Integer, Integer> attack, Pair<Integer, Integer> defense, Pair<Integer, Integer> movement) {
        this.rank = rank;
        this.attack = attack;
        this.defense = defense;
        this.movement = movement;
    }

    public enum UnitRank {
        DETACHMENT(0), REGIMENT(1), BATTALION(3), BRIGADE(2), DIVISION(4), SUPPORT(5), HQ(6);

        UnitRank(int i) {
            this.rank = i;
        }

        @Getter
        private final int rank;
    }

}
