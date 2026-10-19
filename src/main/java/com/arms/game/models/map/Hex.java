package com.arms.game.models.map;

import com.arms.game.models.Faction;
import com.arms.game.models.squares.Unit;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Hex {
    @Getter
    private final HexCoordinate cdn;
    @Getter
    private final Terrain terrain;
    @Getter
    private final boolean trench;

    @Setter
    @Getter
    private List<Unit> units = new ArrayList<>();

    public static final int MAX_STACKING_VALUE = 4;
    public static final int MAX_DETACHMENTS = 2;

    public int stackingValue() {
        return units.stream().mapToInt(Unit::stackingValue).sum();
    }

    public boolean isOverstacked() {
        long detachments = units.stream().filter(u -> u.rank == Unit.UnitRank.DETACHMENT).count();
        return stackingValue() > MAX_STACKING_VALUE || detachments > MAX_DETACHMENTS;
    }

    public boolean isOccupiedBy(Faction faction) {
        return units.stream().anyMatch(u -> u.faction == faction);
    }

    public boolean hasCombatUnitOf(Faction faction) {
        return units.stream().anyMatch(u -> u.faction == faction && u.isCombatUnit());
    }

    /** MPs to enter this hex. A trench adds 1. */
    public int movementCost() {
        return terrain.getMovementCost() + (trench ? 1 : 0);
    }

    public Hex(HexCoordinate cdn, Terrain terrain, boolean trench) {
        this.cdn = cdn;
        this.terrain = terrain;
        this.trench = trench;
    }

    // INFERRED: movement costs are placeholders until we have the Terrain Effects Chart.
    // Impassable terrain uses a cost of -1.
    public enum Terrain {
        CLEAR(1), FOREST(2), MARSH(2), TOWN(1), CITY(1), MOUNTAIN(3), HIGH_MOUNTAIN(4);

        @Getter
        private final int movementCost;

        Terrain(int movementCost) {
            this.movementCost = movementCost;
        }
    }
}
