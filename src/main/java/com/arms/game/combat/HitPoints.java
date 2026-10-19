package com.arms.game.combat;

import com.arms.game.models.squares.Unit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Converts Hit Points into step losses and Disorganisation (rulebook 12.2.8). */
public final class HitPoints {

    public record Result(List<Unit> eliminated, int stepLosses, int disorganized) {
    }

    private HitPoints() {
    }

    /**
     * 1) the first step loss goes to the lead unit if there are enough HPs,
     * 2) remaining HPs become step losses wherever possible, finishing off damaged units first,
     * 3) anything left Disorganises units at 1 HP each, lead unit first.
     * Units must each be given a chance, so the order of the list is the owner's tie-break.
     */
    public static Result apply(List<Unit> units, Unit lead, int hitPoints) {
        List<Unit> alive = new ArrayList<>(units);
        List<Unit> eliminated = new ArrayList<>();
        int remaining = hitPoints;
        int stepLosses = 0;

        if (lead != null && remaining >= lead.hpPerStep()) {
            remaining -= lead.hpPerStep();
            stepLosses++;
            loseStep(lead, alive, eliminated);
        }

        Comparator<Unit> preference = Comparator
                .comparingInt((Unit u) -> -u.stepsLost)
                .thenComparing(u -> u != lead)
                .thenComparingInt(Unit::hpPerStep);
        while (true) {
            final int budget = remaining;
            Unit next = alive.stream()
                    .filter(u -> u.hpPerStep() <= budget)
                    .min(preference)
                    .orElse(null);
            if (next == null) {
                break;
            }
            remaining -= next.hpPerStep();
            stepLosses++;
            loseStep(next, alive, eliminated);
        }

        int disorganized = 0;
        List<Unit> order = new ArrayList<>(alive);
        if (lead != null && order.remove(lead)) {
            order.addFirst(lead);
        }
        for (Unit unit : order) {
            if (remaining <= 0) {
                break;
            }
            if (unit.isDisorganized()) {
                continue; // a Disorganized unit cannot be Disorganized again, so no HP is spent
            }
            unit.disorganized = true;
            disorganized++;
            remaining--;
        }
        return new Result(eliminated, stepLosses, disorganized);
    }

    /** Fixed step losses with no HP cost, taken by the highest morale units first. */
    public static Result applySteps(List<Unit> units, int steps) {
        List<Unit> alive = new ArrayList<>(units);
        List<Unit> eliminated = new ArrayList<>();
        int taken = 0;
        for (int i = 0; i < steps && !alive.isEmpty(); i++) {
            Unit target = alive.stream().max(Comparator.comparingInt(u -> u.morale)).orElseThrow();
            loseStep(target, alive, eliminated);
            taken++;
        }
        return new Result(eliminated, taken, 0);
    }

    private static void loseStep(Unit unit, List<Unit> alive, List<Unit> eliminated) {
        if (unit.takeStepLoss()) {
            alive.remove(unit);
            eliminated.add(unit);
        }
    }
}
