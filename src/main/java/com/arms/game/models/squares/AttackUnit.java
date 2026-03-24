package com.arms.game.models.squares;

import com.arms.game.common.Pair;

public class AttackUnit extends Unit {

    protected AttackUnit(UnitRank rank, Pair<Integer, Integer> attack,  Pair<Integer, Integer> defense,  Pair<Integer, Integer> movement) {
        super(rank, attack, defense, movement);
    }

    public static AttackUnit factory(UnitRank rank,  Pair<Integer, Integer> attack,  Pair<Integer, Integer> defense,  Pair<Integer, Integer> movement) {
        return new AttackUnit(rank, attack, defense, movement);
    }
}
