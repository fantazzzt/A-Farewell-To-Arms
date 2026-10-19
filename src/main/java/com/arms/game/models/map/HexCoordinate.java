package com.arms.game.models.map;

public record HexCoordinate(int q, int r, int s) {

    public HexCoordinate {
        if (q + r + s != 0) {
            throw new IllegalArgumentException("Cube coordinates must sum to 0: " + q + "," + r + "," + s);
        }
    }

    public static HexCoordinate of(int q, int r) {
        return new HexCoordinate(q, r, -q - r);
    }

    public int distanceTo(HexCoordinate other) {
        return (Math.abs(q - other.q) + Math.abs(r - other.r) + Math.abs(s - other.s)) / 2;
    }
}
