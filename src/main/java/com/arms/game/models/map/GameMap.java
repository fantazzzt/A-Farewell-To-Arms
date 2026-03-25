package com.arms.game.models.map;

import com.arms.game.models.Faction;

import java.util.HashMap;

public class GameMap {
    private Faction initiativeFaction;
    private String nameOfMap;
    private int totalGridSize;
    private HashMap<HexCoordinate, Hex> hexMap;

    public GameMap() {
    }
}
