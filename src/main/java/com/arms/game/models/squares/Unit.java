package com.arms.game.models.squares;

import com.arms.game.common.Pair;
import com.arms.game.models.Faction;
import lombok.Getter;

public abstract class Unit {

    // Tokens have two sides. The front side is the full value of that token.
    // The second side is the step loss side. Hence two of each value
    public final String id;
    public final Faction faction;
    public final UnitRank rank;
    public final int morale;
    public final Pair<Integer, Integer> attack;
    public final Pair<Integer, Integer> defense;
    public final Pair<Integer, Integer> movement;
    public  boolean stepLoss;
    public  boolean hasMoved = false;

    protected Unit(String id, Faction faction, UnitRank rank, int morale, boolean stepLoss,
                   Pair<Integer, Integer> attack, Pair<Integer, Integer> defense, Pair<Integer, Integer> movement) {
        this.id = id;
        this.faction = faction;
        this.rank = rank;
        this.morale = morale;
        this.stepLoss = stepLoss;
        this.attack = attack;
        this.defense = defense;
        this.movement = movement;
    }

    public enum UnitRank {
        DETACHMENT(0), REGIMENT(1), BRIGADE(2), DIVISION(3), SUPPORT(4), HQ(5);

        UnitRank(int i) {
            this.rank = i;
        }

        @Getter
        private final int rank;
    }

}
