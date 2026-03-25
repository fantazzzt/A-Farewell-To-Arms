package com.arms.game.common;

import com.arms.game.models.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private Game currentGame;

    public void startGame(List<Player> players) {
        GameBuilder builder = GameBuilder.initializeGame();
        players.forEach(builder::addPlayer);
        this.currentGame = builder.startGame();
    }

    public Game getGame() {
        if (currentGame == null) {
            throw new IllegalStateException("Game not started");
        }
        return currentGame;
    }
}