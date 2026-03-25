package com.arms.game.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Player {

    public boolean phasingPlayer;
    public final String name;
    public final Faction faction;
}
