package com.arms.game.common;

import com.arms.game.models.Faction;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MapLoaderTests {

    @Test
    void loadsSkirmish() throws IOException {
        GameMap map = MapLoader.readGameMap("skirmish");

        assertEquals(Faction.CENTRAL, map.getInitiativeFaction());
        assertEquals(17, map.getFirstTurn());
        assertEquals(18, map.getLastTurn());
        assertEquals(15, map.getHexMap().size());

        Hex peak = map.getHex(HexCoordinate.of(3, 1));
        assertEquals(Hex.Terrain.HIGH_MOUNTAIN, peak.getTerrain());
        assertTrue(peak.isTrench());
        assertEquals("23 DI", peak.getUnits().getFirst().id);
    }

    @Test
    void missingScenarioFails() {
        assertThrows(IOException.class, () -> MapLoader.readGameMap("nope"));
    }

    @Test
    void hexCoordinateDistance() {
        assertEquals(3, HexCoordinate.of(0, 0).distanceTo(HexCoordinate.of(3, 0)));
        assertEquals(2, HexCoordinate.of(0, 0).distanceTo(HexCoordinate.of(1, 1)));
    }
}
