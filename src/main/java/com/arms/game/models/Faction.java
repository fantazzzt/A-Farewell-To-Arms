package com.arms.game.models;

public enum Faction {
    ENTENTE, CENTRAL;

    public Faction opponent() {
        return this == ENTENTE ? CENTRAL : ENTENTE;
    }

    public enum Entente {
        FRANCE, ITALY
    }

    public enum Central{
        GERMANY, AUSTRIA
    }
}
