package com.arms.game.models.squares;

import com.arms.game.common.Pair;
import com.arms.game.models.Faction;

public class AttackUnit extends Unit {

    protected AttackUnit(String id, Faction faction, UnitRank rank, int morale, boolean stepLoss,
                         Pair<Integer, Integer> attack, Pair<Integer, Integer> defense, Pair<Integer, Integer> movement) {
        super(id, faction, rank, morale, stepLoss, attack, defense, movement);
    }

    public static AttackUnit factory(String id, Faction faction, UnitRank rank, int morale, boolean stepLoss,
                                     Pair<Integer, Integer> attack, Pair<Integer, Integer> defense, Pair<Integer, Integer> movement) {
        return new AttackUnit(id, faction, rank, morale, stepLoss, attack, defense, movement);
    }
}
