package com.arms.game.common;

import com.arms.game.models.map.GameMap;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

/**
 * Helper class for loading the maps into Java objects. The maps specify the scenario. For example,
 * they can specify where units reside, the terrain of the battlefield, The phasing player, and any special
 * rules that are specific to the map.
 */
public class MapLoader {

    public static final ObjectMapper mapper = new ObjectMapper();

    private MapLoader() {
    }

    public static GameMap readGameMap(String filePath) {
        File file = new File(filePath);
        return mapper.readValue(file, GameMap.class);
    }
}
