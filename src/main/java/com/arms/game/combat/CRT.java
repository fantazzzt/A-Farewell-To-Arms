package com.arms.game.combat;

import com.arms.game.common.Pair;
import com.arms.game.models.Dice;

import java.util.List;

public class CRT {

    private static final String[][] crtTable = {
    // Row 1
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 2
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 3
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 4
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 5
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 6
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 7
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 8
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 9
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 10
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},

    // Row 11
    { "A12D0", "A11D0", "A12D0", "A11D0", "A12D0", "A11D0", "A12D0",},
};

    private CRT() {
    }

    public static Pair<String, String> rollAttackDefendTable(int attack, int defend) {
        int rollOne = Dice.roll();
        int rollTwo = Dice.roll();

        int attackingRatio = attack / defend;

        String result = crtTable[rollOne+rollTwo][attackingRatio];
        int defenseStop = (result.indexOf("D"));
        String attackResult = result.substring(0, defenseStop);
        String defenseResult = result.substring(defenseStop);


        return new Pair<>(attackResult, defenseResult);


    }
}
