package com.arms.game.models.squares;

import com.arms.game.models.map.HexCoordinate;

import java.util.Comparator;
import java.util.List;

public class UnitStack {
    public List<Unit> unitList;

    public void move(HexCoordinate hexCoordinate) {

        if (!unitList.isEmpty() ){
            int maxTileMovement = 0;
            maxTileMovement = unitList.stream()
                    .map(unit -> unit.stepLoss ? unit.movement.second() : unit.movement.first())
                    .max(Comparator.comparing(Integer::valueOf))
                    .get();

            // TODO Move to desired coordinate location
            // Probably need validators, move will be complicated
        }

    }

    public void attack() {
        // TODO
    }

    // Need assertions that they are all in the same tile and can move together
    public UnitStack(List<Unit> units) {
        unitList = units;
    }
}
