package com.arms.game.common;

import com.arms.game.common.state.Administrative;
import com.arms.game.models.Player;
import com.arms.game.models.map.GameMap;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameBuilder {
    private static final int MIN_NUM_PLAYERS = 2;
    private static final int MAX_NUM_PLAYERS = 2;
    private  List<Player> playerList;
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

    public GameBuilder addScenario(@NonNull String map) throws IOException {
        gameMap = MapLoader.readGameMap(map);
        return this;
    }

    public GameBuilder removePlayer(@NonNull String playerName) {
        playerList = playerList.stream().filter(player -> !Objects.equals(player.name, playerName)).toList();
        return this;
    }

    public List<Player> getPlayerNames() {
        return new ArrayList<>(playerList);
    }

    public Game startGame() {
        if (playerList.size() < MIN_NUM_PLAYERS) {
            throw new IllegalStateException("Not enough players in the game! Need at least " + MIN_NUM_PLAYERS + "players");
        }
        return new Game(playerList, gameMap.getOperationStartingDay(), 0, new Administrative(), gameMap);
    }
}
