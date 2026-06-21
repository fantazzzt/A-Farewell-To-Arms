package com.arms.game.models.map;

import com.arms.game.models.Faction;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;

public class GameMap {
    @Getter
    @Setter
    private Faction initiativeFaction;
    @Getter
    @Setter
    private String nameOfMap;
    @Getter
    @Setter
    private int totalGridSize;
    @Getter
    @Setter
    private HashMap<HexCoordinate, Hex> hexMap;
    @Getter
    @Setter
    private Date operationStartingDay;


    public GameMap() {
    }
}
