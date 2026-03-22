package com.arms.game.models;

import java.util.List;

public class UnitStack {
    public List<Unit> unitList;

    public void move() {
        // TODO
    }

    public void attack() {
        // TODO
    }

    // Need assertions that they are all in the same tile and can move together
    public UnitStack(List<Unit> units) {
        unitList = units;
    }
}
