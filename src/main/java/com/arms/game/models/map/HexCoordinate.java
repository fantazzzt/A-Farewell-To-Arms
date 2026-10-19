package com.arms.game.models.map;

import java.util.Arrays;
import java.util.List;

public record HexCoordinate(int q, int r, int s) {

    public HexCoordinate {
        if (q + r + s != 0) {
            throw new IllegalArgumentException("Cube coordinates must sum to 0: " + q + "," + r + "," + s);
        }
    }

    public static HexCoordinate of(int q, int r) {
        return new HexCoordinate(q, r, -q - r);
    }

    private static final int[][] DIRECTIONS = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};

    /** The six adjacent coordinates. They may or may not exist on a given map. */
    public List<HexCoordinate> neighbors() {
        return Arrays.stream(DIRECTIONS).map(d -> HexCoordinate.of(q + d[0], r + d[1])).toList();
    }

    public int distanceTo(HexCoordinate other) {
        return (Math.abs(q - other.q) + Math.abs(r - other.r) + Math.abs(s - other.s)) / 2;
    }
}
