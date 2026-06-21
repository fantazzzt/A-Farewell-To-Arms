package com.arms.game.models.squares;

import com.arms.game.common.Pair;

public class AttackUnit extends Unit {

    protected AttackUnit(UnitRank rank, boolean stepLoss, Pair<Integer, Integer> attack, Pair<Integer, Integer> defense, Pair<Integer, Integer> movement) {
        super(rank, stepLoss, attack, defense, movement);
    }

    public static AttackUnit factory(UnitRank rank, boolean stepLoss, Pair<Integer, Integer> attack, Pair<Integer, Integer> defense, Pair<Integer, Integer> movement) {
        return new AttackUnit(rank, stepLoss, attack, defense, movement);
    }
}
