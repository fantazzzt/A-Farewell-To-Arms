package com.arms.game.rules;

import com.arms.game.models.Faction;
import com.arms.game.models.map.GameMap;
import com.arms.game.models.map.Hex;
import com.arms.game.models.map.HexCoordinate;

/**
 * Monte Grappa victory conditions (rulebook scenario notes), simplified: the Strategic Victory and the supply
 * requirements are not implemented, and the CP victory level is not lowered by Entente-held CP trenches.
 */
public final class Victory {

    /** The winner is null for a draw. */
    public record Outcome(Faction winner, String description) {
    }

    private Victory() {
    }

    public static Outcome evaluate(GameMap map) {
        boolean holdsObjectives = !map.getObjectiveHexes().isEmpty()
                && map.getObjectiveHexes().stream().allMatch(c -> heldBy(map.getHex(c), Faction.CENTRAL));
        if (holdsObjectives) {
            return new Outcome(Faction.CENTRAL, "Operational Victory: the Central Powers hold every objective hex.");
        }

        int points = (int) map.getHexMap().values().stream()
                .filter(h -> h.isTrench() && heldBy(h, Faction.CENTRAL))
                .count();
        int threshold = map.getVpThreshold();
        String score = points + " Victory Points (a Tactical Victory needs more than " + threshold + ").";
        if (points > threshold) {
            return new Outcome(Faction.CENTRAL, "Tactical Victory for the Central Powers: " + score);
        }
        if (points == threshold) {
            return new Outcome(null, "Draw: " + score);
        }
        return new Outcome(Faction.ENTENTE, "Entente victory: the Central Powers scored only " + score);
    }

    private static boolean heldBy(Hex hex, Faction faction) {
        return hex != null && hex.hasCombatUnitOf(faction) && !hex.hasCombatUnitOf(faction.opponent());
    }
}
