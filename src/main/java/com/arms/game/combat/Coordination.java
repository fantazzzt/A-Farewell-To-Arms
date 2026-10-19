package com.arms.game.combat;

import com.arms.game.models.squares.Unit;

/**
 * Result of a Tactical Coordination roll (rulebook 12.2.6).
 * Misunderstood Orders gives no benefit, like NONE. Its extra penalty table is not implemented yet.
 */
public enum Coordination {
    MISUNDERSTOOD, NONE, PARTIAL, PERFECT;

    public static Coordination fromRoll(int natural, int modifier) {
        int modified = natural + modifier;
        if (natural == 6 || modified >= 6) {
            return PERFECT;
        }
        if (modified <= 0) {
            return MISUNDERSTOOD;
        }
        if (natural == 1 || modified <= 2) {
            return NONE;
        }
        return PARTIAL;
    }

    // INFERRED: only the modifiers I could read clearly from the photos (lead unit elite +1, Disorganized -1).
    public static int modifier(Unit leadUnit) {
        return (leadUnit.isElite() ? 1 : 0) + (leadUnit.isDisorganized() ? -1 : 0);
    }

    public boolean givesBenefit() {
        return this == PARTIAL || this == PERFECT;
    }
}
