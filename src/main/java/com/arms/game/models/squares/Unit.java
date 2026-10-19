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
    public  int stepsLost;
    public  boolean hasMoved = false;
    public  boolean hasAttacked = false;
    public  boolean disorganized = false;
    public  boolean reorganizing = false;
    public  boolean exploitation = false;

    protected Unit(String id, Faction faction, UnitRank rank, int morale, boolean stepLoss,
                   Pair<Integer, Integer> attack, Pair<Integer, Integer> defense, Pair<Integer, Integer> movement) {
        this.id = id;
        this.faction = faction;
        this.rank = rank;
        this.morale = morale;
        this.stepLoss = stepLoss;
        this.stepsLost = stepLoss ? 1 : 0;
        this.attack = attack;
        this.defense = defense;
        this.movement = movement;
    }

    public boolean isCombatUnit() {
        return rank != UnitRank.SUPPORT && rank != UnitRank.HQ;
    }

    /** Disorganized and Reorganizing units suffer the same penalties. */
    public boolean isDisorganized() {
        return disorganized || reorganizing;
    }

    /** Movement allowance of whichever side of the counter is face up. Halved (rounded down) if Disorganized. */
    public int currentMovement() {
        int allowance = stepLoss ? movement.second() : movement.first();
        return isDisorganized() ? allowance / 2 : allowance;
    }

    public int attackFactor() {
        return stepLoss ? attack.second() : attack.first();
    }

    public int defenseFactor() {
        return stepLoss ? defense.second() : defense.first();
    }

    /** A regiment, brigade or detachment with morale 5 or 6 (rulebook 2.0). */
    public boolean isElite() {
        return (rank == UnitRank.REGIMENT || rank == UnitRank.BRIGADE || rank == UnitRank.DETACHMENT) && morale >= 5;
    }

    // INFERRED from the step loss rules (6.0): detachments have 1 step, regiments and brigades 2,
    // divisions 2 (morale 2-3) or 3 (morale 4+).
    public int maxSteps() {
        return switch (rank) {
            case DETACHMENT, SUPPORT, HQ -> 1;
            case REGIMENT, BRIGADE -> 2;
            case DIVISION -> morale >= 4 ? 3 : 2;
        };
    }

    /** Hit Points needed to inflict one step loss (rulebook 12.2.8). */
    public int hpPerStep() {
        return switch (rank) {
            case DETACHMENT, REGIMENT -> 2;
            case BRIGADE, DIVISION -> 3;
            case SUPPORT, HQ -> 2;
        };
    }

    /** Flips the counter to its reduced side. Returns true if that was the last step and the unit is eliminated. */
    public boolean takeStepLoss() {
        stepsLost++;
        stepLoss = true;
        return stepsLost >= maxSteps();
    }

    /** Points this unit uses up in a hex. A full division is 3, a reduced one 2. Detachments and supports use none. */
    public int stackingValue() {
        return switch (rank) {
            case DIVISION -> stepLoss ? 2 : 3;
            case REGIMENT, BRIGADE -> 1;
            case DETACHMENT, SUPPORT, HQ -> 0;
        };
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
