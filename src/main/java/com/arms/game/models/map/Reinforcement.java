package com.arms.game.models.map;

import com.arms.game.models.squares.Unit;

/** A unit that joins the game at the start of a player's Movement phase. */
public record Reinforcement(int turn, int sequence, Unit unit, HexCoordinate at) {
}
