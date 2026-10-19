package com.arms.game.combat;

/**
 * The choices a player makes during combat. {@link #AUTO} makes sensible choices so the game can run without
 * asking anyone (a CLI can supply its own implementation).
 */
public interface CombatDecisions {

    /** How many hexes the defender retreats, up to maxHexes. Each hex retreated removes 3 HP. */
    int defenderRetreatHexes(int maxHexes, int hitPointsIncoming);

    boolean attackerAdvances();

    CombatDecisions AUTO = new CombatDecisions() {
        @Override
        public int defenderRetreatHexes(int maxHexes, int hitPointsIncoming) {
            return Math.min(maxHexes, hitPointsIncoming / CombatResolver.RETREAT_HP_PER_HEX);
        }

        @Override
        public boolean attackerAdvances() {
            return true;
        }
    };
}
