package com.arms.game.common;

import com.arms.game.models.GameMap;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GameBuilder {
    private static final int MIN_NUM_PLAYERS = 2;
    private static final int MAX_NUM_PLAYERS = 2;


    private final List<String> playerNames;
    private GameMap gameMap;

    private GameBuilder() {
        this.gameMap = null;
        this.playerNames = new ArrayList<>();
    }

    public static GameBuilder initializeGame() {
        return new GameBuilder();
    }

    public boolean isGameFull() {
        return playerNames.size() >= MAX_NUM_PLAYERS;
    }


    public GameBuilder addPlayer(@NonNull String playerName) {
        if (playerNames.contains(playerName)) {
            throw new IllegalArgumentException("Cannot have 2 players with " +
                    "same name " + playerName);
        }
        if (isGameFull()) {
            throw new IllegalStateException("Cannot have more than " + MAX_NUM_PLAYERS + " players in game");
        }
        playerNames.add(playerName);
        return this;
    }

    public GameBuilder addScenario(@NonNull GameMap map) {
        gameMap = map;
        return this;
    }

    public GameBuilder removePlayer(@NonNull String playerName) {
        playerNames.remove(playerName);
        return this;
    }

    public List<String> getPlayerNames() {
        return new ArrayList<>(playerNames);
    }

    public Game startGame() {
        if (playerNames.size() < MIN_NUM_PLAYERS) {
            throw new IllegalStateException("Not enough players in the game! Need at least " + MIN_NUM_PLAYERS + "players");
        }
        return new Game();
    }
}
