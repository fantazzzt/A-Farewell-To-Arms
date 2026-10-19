package com.arms.game.models.squares;

import com.arms.game.models.map.HexCoordinate;

import java.util.List;

public class UnitStack {
    public List<Unit> unitList;
    public HexCoordinate currentLocation;

    public void attack() {
        // TODO
    }

    // Need assertions that they are all in the same tile and can move together
    public UnitStack(List<Unit> units, HexCoordinate location) {
        unitList = units;
        currentLocation = location;
    }
}
