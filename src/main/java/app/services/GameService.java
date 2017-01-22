package app.services;

import app.game.Game;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by Phaethon on 21-Jan-17
 */
@Service
public class GameService {
    private static Game game;
    private final Object gameLock = new Object();

    public void startGame() {
        synchronized (gameLock) {
            if (game == null) {
                try {
                    game = new Game();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Game getGame() {
        return game;
    }

    public void stopGame() {
        synchronized (gameLock) {
            game = null;
        }
    }
}
