package com.arms.game.rules;

import com.arms.game.common.Pair;
import com.arms.game.models.Faction;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.AttackUnit;
import com.arms.game.models.squares.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** A straight row of hexes along q at r=0: (0,0) to (6,0), all clear unless a test changes one. */
class MovementRulesTests {

    private Map<HexCoordinate, Hex> hexes;
    private GameMap map;

    @BeforeEach
    void setUp() {
        hexes = new HashMap<>();
        for (int q = 0; q <= 6; q++) {
            put(q, Hex.Terrain.CLEAR, false);
        }
        rebuild();
    }

    private void put(int q, Hex.Terrain terrain, boolean trench) {
        HexCoordinate c = HexCoordinate.of(q, 0);
        hexes.put(c, new Hex(c, terrain, trench));
    }

    private void rebuild() {
        map = new GameMap("test", Faction.CENTRAL, 1, 1, hexes, List.of());
    }

    private Unit unit(String id, Faction faction, Unit.UnitRank rank, int movement) {
        return AttackUnit.factory(id, faction, rank, 4, false,
                new Pair<>(5, 2), new Pair<>(5, 2), new Pair<>(movement, movement));
    }

    private Unit place(Unit unit, int q) {
        hexes.get(HexCoordinate.of(q, 0)).getUnits().add(unit);
        return unit;
    }

    private Map<HexCoordinate, Integer> reach(int fromQ, Unit... movers) {
        return MovementRules.reachableHexes(map, HexCoordinate.of(fromQ, 0), List.of(movers));
    }

    @Test
    void stackingValues() {
        Hex hex = hexes.get(HexCoordinate.of(0, 0));
        place(unit("div", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4), 0);
        place(unit("reg", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 4), 0);
        assertEquals(4, hex.stackingValue());
        assertFalse(hex.isOverstacked());

        place(unit("brig", Faction.CENTRAL, Unit.UnitRank.BRIGADE, 4), 0);
        assertTrue(hex.isOverstacked());
    }

    @Test
    void reducedDivisionCountsTwoAndDetachmentsAreCapped() {
        Hex hex = hexes.get(HexCoordinate.of(0, 0));
        Unit div = place(unit("div", Faction.CENTRAL, Unit.UnitRank.DIVISION, 4), 0);
        div.stepLoss = true;
        assertEquals(2, hex.stackingValue());

        for (int i = 0; i < 3; i++) {
            place(unit("det" + i, Faction.CENTRAL, Unit.UnitRank.DETACHMENT, 4), 0);
        }
        assertEquals(2, hex.stackingValue()); // detachments are free...
        assertTrue(hex.isOverstacked());      // ...but only 2 are allowed
    }

    @Test
    void movesAsFarAsMovementPointsAllow() {
        Unit u = place(unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 3), 0);
        Map<HexCoordinate, Integer> reach = reach(0, u);
        assertEquals(Set.of(1, 2, 3), reach.keySet().stream().map(HexCoordinate::q).collect(Collectors.toSet()));
        assertEquals(0, reach.get(HexCoordinate.of(3, 0)));
    }

    @Test
    void stackMovesAtSlowestUnitSpeed() {
        Unit fast = place(unit("fast", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 5), 0);
        Unit slow = place(unit("slow", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 2), 0);
        assertEquals(2, MovementRules.stackMovement(List.of(fast, slow)));
        assertFalse(reach(0, fast, slow).containsKey(HexCoordinate.of(3, 0)));
    }

    @Test
    void terrainAndTrenchesCostExtra() {
        put(1, Hex.Terrain.FOREST, false); // costs 2
        put(2, Hex.Terrain.CLEAR, true);   // clear + trench costs 2
        rebuild();
        Unit u = place(unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 4), 0);
        Map<HexCoordinate, Integer> reach = reach(0, u);
        assertEquals(2, reach.get(HexCoordinate.of(1, 0)));
        assertEquals(0, reach.get(HexCoordinate.of(2, 0)));
        assertFalse(reach.containsKey(HexCoordinate.of(3, 0)));
    }

    @Test
    void alwaysAllowedOneHexEvenIfTooExpensive() {
        put(1, Hex.Terrain.HIGH_MOUNTAIN, false); // costs 4
        rebuild();
        Unit u = place(unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 2), 0);
        assertTrue(reach(0, u).containsKey(HexCoordinate.of(1, 0)));
    }

    @Test
    void enemyZocStopsMovement() {
        Unit mover = place(unit("mover", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 6), 0);
        place(unit("enemy", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4), 4);
        Map<HexCoordinate, Integer> reach = reach(0, mover);
        assertTrue(reach.containsKey(HexCoordinate.of(3, 0)));  // enters the ZoC...
        assertFalse(reach.containsKey(HexCoordinate.of(4, 0))); // ...cannot enter the enemy hex
        assertFalse(reach.containsKey(HexCoordinate.of(5, 0))); // ...or go past it
    }

    @Test
    void supportUnitsExertNoZoc() {
        Unit mover = place(unit("mover", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 6), 0);
        place(unit("hq", Faction.ENTENTE, Unit.UnitRank.HQ, 4), 4);
        assertTrue(MovementRules.zoneOfControl(map, Faction.ENTENTE).isEmpty());
        assertTrue(reach(0, mover).containsKey(HexCoordinate.of(3, 0)));
    }

    @Test
    void cannotMoveFromZocToZoc() {
        // Two enemies at q=0 and q=3 put hexes 1,2 both in enemy ZoC; mover starts at 1.
        place(unit("e1", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4), 0);
        place(unit("e2", Faction.ENTENTE, Unit.UnitRank.REGIMENT, 4), 3);
        Unit mover = place(unit("mover", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 6), 1);
        assertFalse(reach(1, mover).containsKey(HexCoordinate.of(2, 0)));
    }

    @Test
    void moveRelocatesUnitsAndMarksThemMoved() {
        Unit u = place(unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 3), 0);
        MovementRules.move(map, HexCoordinate.of(0, 0), List.of(u), HexCoordinate.of(2, 0));
        assertTrue(hexes.get(HexCoordinate.of(0, 0)).getUnits().isEmpty());
        assertEquals(List.of(u), hexes.get(HexCoordinate.of(2, 0)).getUnits());
        assertTrue(u.hasMoved);
        assertThrows(IllegalStateException.class,
                () -> MovementRules.move(map, HexCoordinate.of(2, 0), List.of(u), HexCoordinate.of(3, 0)));
    }

    @Test
    void illegalMoveIsRejected() {
        Unit u = place(unit("a", Faction.CENTRAL, Unit.UnitRank.REGIMENT, 2), 0);
        assertThrows(IllegalArgumentException.class,
                () -> MovementRules.move(map, HexCoordinate.of(0, 0), List.of(u), HexCoordinate.of(5, 0)));
        assertFalse(u.hasMoved);
    }
}
