package com.arms.game.models;

import com.arms.game.models.squares.Unit;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Hex {
    @Getter
    private final HexCoordinate cdn;
    @Getter
    private final Terrain terrain;

    @Setter
    @Getter
    private List<Unit> units;


    public Hex(int q, int r, int s, Terrain terrain) {
        this.cdn = new HexCoordinate(q, r, s);
        this.terrain = terrain;
    }

    // TODO: Make terrain a builder where you can have a base terrain and add on trench, VPs etc
    public enum Terrain {
        GRASSLAND(0);

        @Getter
        private final int terrain;

        Terrain(int i) {
            this.terrain = i;
        }
    }

}
