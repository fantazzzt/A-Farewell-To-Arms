package com.arms.game.common;

import com.arms.game.models.Player;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GameService {

    private Game currentGame;

    public void startGame(List<Player> players, String scenario) throws IOException {
        GameBuilder builder = GameBuilder.initializeGame();
        players.forEach(builder::addPlayer);
        builder.addScenario(scenario);
        this.currentGame = builder.startGame();
    }

    public Game getGame() {
        if (currentGame == null) {
            throw new IllegalStateException("Game not started");
        }
        return currentGame;
    }
}