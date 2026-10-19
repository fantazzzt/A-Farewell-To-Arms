package com.arms.game.models.map;

import com.arms.game.models.Faction;
import com.arms.game.models.squares.Unit;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class GameMap {
    @Getter
    private final String nameOfMap;
    @Getter
    private final Faction initiativeFaction;
    @Getter
    private final int firstTurn;
    @Getter
    private final int lastTurn;
    @Getter
    private final Map<HexCoordinate, Hex> hexMap;
    @Getter
    private final List<HexCoordinate> objectiveHexes;
    /** Turns on which the initiative player must declare an Offensive. */
    @Getter
    private final List<Integer> offensiveTurns;
    /** Victory Points the CP must beat for a Tactical Victory (see Victory). */
    @Getter
    private final int vpThreshold;
    @Getter
    private final List<Reinforcement> reinforcements;

    public GameMap(String nameOfMap, Faction initiativeFaction, int firstTurn, int lastTurn,
                   Map<HexCoordinate, Hex> hexMap, List<HexCoordinate> objectiveHexes,
                   List<Integer> offensiveTurns, int vpThreshold, List<Reinforcement> reinforcements) {
        this.nameOfMap = nameOfMap;
        this.initiativeFaction = initiativeFaction;
        this.firstTurn = firstTurn;
        this.lastTurn = lastTurn;
        this.hexMap = hexMap;
        this.objectiveHexes = objectiveHexes;
        this.offensiveTurns = offensiveTurns;
        this.vpThreshold = vpThreshold;
        this.reinforcements = reinforcements;
    }

    public Hex hexOf(Unit unit) {
        return hexMap.values().stream().filter(h -> h.getUnits().contains(unit)).findFirst().orElse(null);
    }

    public Hex getHex(HexCoordinate coordinate) {
        return hexMap.get(coordinate);
    }
}
