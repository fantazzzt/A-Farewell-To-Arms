package com.arms.game.common;

import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.AttackUnit;
import com.arms.game.models.squares.Unit;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for loading the maps into Java objects. The maps specify the scenario. For example,
 * they can specify where units reside, the terrain of the battlefield, The phasing player, and any special
 * rules that are specific to the map.
 */
public class MapLoader {

    public static final ObjectMapper mapper = new ObjectMapper();

    private MapLoader() {
    }

    /**
     * @param scenarioName name of a file under resources/scenarios, without the .json extension
     */
    public static GameMap readGameMap(String scenarioName) throws IOException {
        String path = "/scenarios/" + scenarioName + ".json";
        try (InputStream in = MapLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Scenario not found on classpath: " + path);
            }
            return build(mapper.readValue(in, ScenarioDefinition.class));
        }
    }

    static GameMap build(ScenarioDefinition def) {
        Map<HexCoordinate, Hex> hexes = new HashMap<>();
        for (ScenarioDefinition.HexDef h : def.hexes()) {
            HexCoordinate coordinate = HexCoordinate.of(h.q(), h.r());
            if (hexes.put(coordinate, new Hex(coordinate, h.terrain(), Boolean.TRUE.equals(h.trench()))) != null) {
                throw new IllegalArgumentException("Duplicate hex at " + coordinate);
            }
        }

        for (ScenarioDefinition.UnitDef u : def.units()) {
            Hex hex = hexes.get(HexCoordinate.of(u.q(), u.r()));
            if (hex == null) {
                throw new IllegalArgumentException("Unit " + u.id() + " placed off the map at " + u.q() + "," + u.r());
            }
            hex.getUnits().add(toUnit(u));
        }

        List<HexCoordinate> objectives = def.objectives().stream()
                .map(o -> HexCoordinate.of(o.q(), o.r()))
                .toList();
        return new GameMap(def.name(), def.initiative(), def.firstTurn(), def.lastTurn(), hexes, objectives);
    }

    private static Unit toUnit(ScenarioDefinition.UnitDef u) {
        return AttackUnit.factory(u.id(), u.faction(), u.rank(), u.morale(), Boolean.TRUE.equals(u.reduced()),
                pair(u.attack()), pair(u.defense()), pair(u.movement()));
    }

    private static Pair<Integer, Integer> pair(int[] values) {
        if (values == null || values.length != 2) {
            throw new IllegalArgumentException("Stats must be [front, back]");
        }
        return new Pair<>(values[0], values[1]);
    }
}
