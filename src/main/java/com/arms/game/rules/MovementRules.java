package com.arms.game.rules;

import com.arms.game.models.Faction;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;
import com.arms.game.models.squares.Unit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Movement rules (rulebook section 11). Simplifications for now: each unit moves once per phase,
 * and there are no roads, rivers or Disorganized markers.
 */
public final class MovementRules {

    private MovementRules() {
    }

    /** Hexes adjacent to a combat unit of the given faction. Support units exert no ZoC. */
    public static Set<HexCoordinate> zoneOfControl(GameMap map, Faction faction) {
        Set<HexCoordinate> zoc = new HashSet<>();
        for (Hex hex : map.getHexMap().values()) {
            if (hex.hasCombatUnitOf(faction)) {
                hex.getCdn().neighbors().stream()
                        .filter(map.getHexMap()::containsKey)
                        .forEach(zoc::add);
            }
        }
        return zoc;
    }

    /** A stack moves at the speed of its slowest unit. */
    public static int stackMovement(List<Unit> movers) {
        return movers.stream().mapToInt(Unit::currentMovement).min().orElse(0);
    }

    /**
     * Every hex the stack can reach, mapped to the MPs it would have left on arrival.
     * Entering an enemy ZoC ends movement, so ZoC hexes are reachable but never expanded from.
     */
    public static Map<HexCoordinate, Integer> reachableHexes(GameMap map, HexCoordinate start, List<Unit> movers) {
        Faction faction = movers.getFirst().faction;
        Set<HexCoordinate> enemyZoc = zoneOfControl(map, faction.opponent());
        int allowance = stackMovement(movers);

        Map<HexCoordinate, Integer> best = new HashMap<>();
        PriorityQueue<Map.Entry<HexCoordinate, Integer>> queue =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        best.put(start, allowance);
        queue.add(Map.entry(start, allowance));

        while (!queue.isEmpty()) {
            Map.Entry<HexCoordinate, Integer> current = queue.poll();
            HexCoordinate here = current.getKey();
            int left = current.getValue();
            if (left < best.get(here)) {
                continue; // stale queue entry
            }
            boolean isStart = here.equals(start);
            if (!isStart && enemyZoc.contains(here)) {
                continue; // had to stop on entering
            }
            for (HexCoordinate next : here.neighbors()) {
                Hex hex = map.getHex(next);
                if (hex == null || hex.isOccupiedBy(faction.opponent())) {
                    continue;
                }
                boolean zocToZoc = isStart && enemyZoc.contains(start) && enemyZoc.contains(next);
                if (zocToZoc) {
                    continue;
                }
                int cost = hex.movementCost();
                if (cost > left) {
                    if (!isStart) {
                        continue;
                    }
                    cost = left; // a unit can always move at least one hex
                }
                int remaining = left - cost;
                if (remaining > best.getOrDefault(next, -1)) {
                    best.put(next, remaining);
                    queue.add(Map.entry(next, remaining));
                }
            }
        }
        best.remove(start);
        return best;
    }

    /** Moves the stack and marks the units as moved. Throws if the move is illegal. */
    public static void move(GameMap map, HexCoordinate from, List<Unit> movers, HexCoordinate to) {
        Hex origin = map.getHex(from);
        if (origin == null) {
            throw new IllegalArgumentException("No hex at " + from);
        }
        if (movers.isEmpty() || !origin.getUnits().containsAll(movers)) {
            throw new IllegalArgumentException("All moving units must start in " + from);
        }
        if (movers.stream().map(u -> u.faction).distinct().count() > 1) {
            throw new IllegalArgumentException("A stack cannot mix factions");
        }
        if (movers.stream().anyMatch(u -> u.hasMoved)) {
            throw new IllegalStateException("A unit has already moved this phase");
        }
        if (!reachableHexes(map, from, movers).containsKey(to)) {
            throw new IllegalArgumentException("Stack cannot reach " + to);
        }
        origin.getUnits().removeAll(movers);
        map.getHex(to).getUnits().addAll(movers);
        movers.forEach(u -> u.hasMoved = true);
    }
}
