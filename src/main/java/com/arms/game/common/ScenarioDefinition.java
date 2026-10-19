package com.arms.game.common;

import com.arms.game.models.Faction;
import com.arms.game.models.map.Hex;
import com.arms.game.models.squares.Unit;

import java.util.List;

/**
 * Plain data shape of a scenario JSON file. {@link MapLoader} turns this into a GameMap.
 * Stats are [front, back] to match the two-sided counters.
 */
public record ScenarioDefinition(
        String name,
        Faction initiative,
        int firstTurn,
        int lastTurn,
        List<HexDef> hexes,
        List<CoordDef> objectives,
        List<UnitDef> units) {

    public record CoordDef(int q, int r) {
    }

    public record HexDef(int q, int r, Hex.Terrain terrain, Boolean trench) {
    }

    public record UnitDef(
            String id,
            Faction faction,
            Unit.UnitRank rank,
            int morale,
            int[] attack,
            int[] defense,
            int[] movement,
            Boolean reduced,
            int q,
            int r) {
    }
}
