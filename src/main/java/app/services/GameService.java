package app.services;

import app.db.GameDB;
import app.game.Game;
import app.game.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * Created by Phaethon on 21-Jan-17
 */
@Service
public class GameService {
    private static Game game;
    private final Object gameLock = new Object();
    @Autowired
    private GameDB gameDB;

    public void startGame() {
        synchronized (gameLock) {
            if (game == null) {
                try {
                    game = gameDB.getGame(1);
                } catch (IOException | URISyntaxException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void setChosenRace(Game game, Player player, String raceName) {
        try {
            gameDB.chooseRace(game.getId(), player.getName(), raceName);
            player.setChosenRace(raceName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Player addPlayer(Game game, String playerName) {
        Player player = game.addPlayer(playerName);
        try {
            gameDB.addPlayer(game, player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public synchronized boolean quitPlayer(Game game, Player player) {
        boolean success = game.quitPlayer(player);
        if (success) {
            try {
                gameDB.deletePlayer(game, player);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public void revealChoices(Game game) {
        try {
            gameDB.revealChoices(game.getId());
            game.revealChoices();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Game getGame() {
        return game;
    }

    public void stopGame() {
        synchronized (gameLock) {
            if (game != null) {
                try {
                    gameDB.deleteGame(game);
                    game = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
