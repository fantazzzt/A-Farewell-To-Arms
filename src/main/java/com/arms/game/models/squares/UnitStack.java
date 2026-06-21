package com.arms.game.models.squares;

import com.arms.game.models.map.HexCoordinate;

import java.util.Comparator;
import java.util.List;

public class UnitStack {
    public List<Unit> unitList;
    public HexCoordinate currentLocation;

    public void move(HexCoordinate hexCoordinate) {
        if (!unitList.isEmpty()) {
            boolean hasMovedAny =  unitList.stream().anyMatch(unit -> unit.hasMoved);
            if (hasMovedAny){
                return;
            }

            int maxTileMovement = 0;
            maxTileMovement = unitList.stream()
                    .map(unit -> unit.stepLoss ? unit.movement.second() : unit.movement.first())
                    .max(Comparator.comparing(Integer::valueOf))
                    .get();

            int distance = calculateHexDistance(hexCoordinate, currentLocation);

            if (distance <= maxTileMovement){
                this.currentLocation = hexCoordinate;
            }

        }
    }

    /**
     * // TODO Please check if I should return 0 or just fail if either input is wrong
     *
     * @param hexOne A non null hex coordinate
     * @param hexTwo A non null hex coordinate
     * @return the distance between the two coordinates
     */
    private static int calculateHexDistance(HexCoordinate hexOne, HexCoordinate hexTwo) {
        if (hexOne == null || hexTwo == null) {
            return 0;
        }

        int topSum = Math.abs(hexOne.q() - hexTwo.q()) +
                Math.abs(hexOne.r() - hexTwo.r()) +
                Math.abs(hexOne.q() + hexOne.r() - hexTwo.q() - hexTwo.r());
        int divisor = 2;
        return topSum / divisor;
    }

    public void attack() {
        // TODO
    }

    // Need assertions that they are all in the same tile and can move together
    public UnitStack(List<Unit> units, HexCoordinate location) {
        unitList = units;
        currentLocation = location;
    }
}
