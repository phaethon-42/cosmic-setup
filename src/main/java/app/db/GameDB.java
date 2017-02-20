package app.db;

import app.game.Game;
import app.game.Player;
import app.game.Race;
import app.game.RaceType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Phaethon on 08-Feb-17
 */
@Service
public class GameDB {

    public void addGame(Game game) throws SQLException {
        DBConnection connection = new DBConnection();

        Object[] params = new Object[5];
        params[0] = game.getId();
        params[1] = game.getName();
        params[2] = getPlayerNames(game.getPlayers());
        params[3] = game.getRacesPerPlayer();
        params[4] = game.isRevealed();

        try {
            connection.executeDml("INSERT INTO games VALUES (?, ?, ?, ?, ?)", params);

            for (Player player : game.getPlayers()) {
                addPlayer(game, connection, player);
            }
        } finally {
            connection.close();
        }
    }

    private void addPlayer(Game game, DBConnection connection, Player player) throws SQLException {
        Object[] playerParams = new Object[4];
        playerParams[0] = game.getId();
        playerParams[1] = player.getName();
        playerParams[2] = player.getChosenRace();
        playerParams[3] = player.getRaces().toArray();
        connection.executeDml("INSERT INTO game_players VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING", playerParams);
    }

    public void addPlayer(Game game, Player player) throws SQLException {
        DBConnection connection = new DBConnection();
        try {
            updateGamePlayers(game, connection);
            addPlayer(game, connection, player);
        } finally {
            connection.close();
        }
    }

    private void updateGamePlayers(Game game, DBConnection connection) throws SQLException {
        Object[] params = {getPlayerNames(game.getPlayers()), game.getId()};
        connection.executeDml("UPDATE games SET PLAYER_NAMES = ? WHERE GAME_ID = ?", params);
    }

    public void deletePlayer(Game game, Player player) throws SQLException {
        DBConnection connection = new DBConnection();
        try {
            updateGamePlayers(game, connection);
            connection.executeDml("DELETE FROM game_players WHERE GAME_ID = ? AND PLAYER_NAME = ?", new Object[]{game.getId(), player.getName()});
        } finally {
            connection.close();
        }
    }

    public void deleteGame(Game game) throws SQLException {
        DBConnection connection = new DBConnection();
        try {
            if (game.getPlayers() != null) {
                for (Player p : game.getPlayers()) {
                    deletePlayer(game, p);
                }
            }
            connection.executeDml("DELETE FROM games WHERE GAME_ID = ?", new Object[]{game.getId()});
        } finally {
            connection.close();
        }
    }

    public void revealChoices(long gameId) throws SQLException {
        DBConnection connection = new DBConnection();
        try {
            connection.executeDml("UPDATE games SET REVEALED = TRUE WHERE GAME_ID = ?", new Object[]{gameId});
        } finally {
            connection.close();
        }
    }

    public Game getGame(long gameId) throws SQLException, IOException, URISyntaxException {
        DBConnection connection = new DBConnection();
        try {
            ResultSet resultSet = connection.query("SELECT * FROM games WHERE GAME_ID = ?", new Object[]{gameId});
            if (resultSet.next()) {
                String gameName = resultSet.getString("GAME_NAME");
                Array playerNames = resultSet.getArray("PLAYER_NAMES");
                int racesPerPlayer = resultSet.getInt("RACES_PER_PLAYER");
                boolean revealed = resultSet.getBoolean("REVEALED");
                Set<Player> players = new HashSet<>();


                if (playerNames != null && playerNames.getArray() != null && playerNames.getArray() instanceof Object[]) {
                    for (Object nameObject : (Object[]) playerNames.getArray()) {
                        String name = (String) nameObject;
                        Player player = getPlayer(gameId, name, connection);
                        if (player != null) {
                            players.add(player);
                        }
                    }
                }

                return new Game(gameId, gameName, players, racesPerPlayer, revealed);
            } else {
                Game game = new Game();
                addGame(game);
                return game;
            }
        } finally {
            connection.close();
        }
    }

    public void chooseRace(long gameId, String playerName, String chosenRace) throws SQLException {
        DBConnection connection = new DBConnection();
        try {
            Object[] params = new Object[3];
            params[0] = chosenRace;
            params[1] = gameId;
            params[2] = playerName;
            connection.executeDml("UPDATE game_players SET CHOSEN_RACE = ? WHERE GAME_ID = ? AND PLAYER_NAME = ?", params);
        } finally {
            connection.close();
        }
    }

    private Player getPlayer(long gameId, String name, DBConnection connection) throws SQLException {
        ResultSet resultSet = connection.query("SELECT * FROM game_players WHERE GAME_ID = ? AND PLAYER_NAME = ?", new Object[]{gameId, name});
        Player player = null;
        if (resultSet.next()) {
            String chosenRace = resultSet.getString("CHOSEN_RACE");
            Array raceNames = resultSet.getArray("RACES");
            List<Race> races = new ArrayList<>();

            if (raceNames != null && raceNames.getArray() != null && raceNames.getArray() instanceof Object[]) {
                for (Object nameObj : (Object[]) raceNames.getArray()) {
                    if (nameObj != null && nameObj instanceof String) {
                        String raceName = (String) nameObj;
                        RaceType raceType = RaceType.getByName(raceName);
                        if (raceType != null) {
                            races.add(new Race(raceName, raceType.getFileName()));
                        }
                    }
                }
            }

            player = new Player(name, chosenRace, races);

        }

        return player;
    }

    private String[] getPlayerNames(Set<Player> players) {
        List<String> names = new ArrayList<>();
        if (players != null) {
            for (Player player : players) {
                names.add(player.getName());
            }
        }

        return names.toArray(new String[names.size()]);
    }
}
