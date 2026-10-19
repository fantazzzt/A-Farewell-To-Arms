package com.arms.game.combat;

import java.util.List;

/** What happened in one combat, for display and tests. */
public record CombatReport(
        int attackFactors,
        int defenseFactors,
        int columnPosition,
        int whiteDiceTotal,
        int attackerHitPoints,
        int defenderHitPoints,
        Coordination attackerCoordination,
        Coordination defenderCoordination,
        int defenderRetreatHexes,
        boolean attackerAdvanced,
        boolean attackerAutoLoss,
        List<String> eliminated) {
}
