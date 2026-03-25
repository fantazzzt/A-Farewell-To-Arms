package com.arms.game.common;

import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameBuilder {
    private static final int MIN_NUM_PLAYERS = 2;
    private static final int MAX_NUM_PLAYERS = 2;
    private final List<Player> playerList;
    private GameMap gameMap;

    private GameBuilder() {
        this.playerList = new ArrayList<>();
        this.gameMap = null;
    }

    public static GameBuilder initializeGame() {
        return new GameBuilder();
    }

    public boolean isGameFull() {
        return playerList.size() >= MAX_NUM_PLAYERS;
    }


    public GameBuilder addPlayer(@NonNull Player newPlayer) {
        if (playerList.stream().anyMatch(player -> Objects.equals(player, newPlayer))) {
            throw new IllegalArgumentException("Cannot have 2 players with " +
                    "same name or the same faction ");
        }
        if (isGameFull()) {
            throw new IllegalStateException("Cannot have more than " + MAX_NUM_PLAYERS + " players in game");
        }
        playerList.add(newPlayer);
        return this;
    }

    public GameBuilder addScenario(@NonNull GameMap map) {
        gameMap = map;
        return this;
    }

    public GameBuilder removePlayer(@NonNull String playerName) {
        playerList.remove(playerName);
        return this;
    }

    public List<Player> getPlayerNames() {
        return new ArrayList<>(playerList);
    }

    public Game startGame() {
        if (playerList.size() < MIN_NUM_PLAYERS) {
            throw new IllegalStateException("Not enough players in the game! Need at least " + MIN_NUM_PLAYERS + "players");
        }
        return new Game(playerList, null, 0, null, null);
    }
}
