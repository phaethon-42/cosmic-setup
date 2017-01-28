package app.game;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Phaethon on 21-Jan-17
 */
public class Game {
    private Set<Player> players;
    private List<Race> availableRaces;
    private int racesPerPlayer = 2;
    private boolean revealed;
    private final Object playerLock = new Object();
    private final Object racesLock = new Object();

    public Game() throws IOException, URISyntaxException {
        players = new HashSet<>();
        availableRaces = new ArrayList<>();
        for (RaceType raceType : getRaces()) {
            availableRaces.add(new Race(raceType.getRaceName(), raceType.getFileName()));
        }
    }

    private RaceType[] getRaces() throws IOException, URISyntaxException {
        return RaceType.values();
    }

    public List<Race> getAvailableRaces() {
        return availableRaces;
    }

    public Race takeRace() {
        synchronized (racesLock) {
            int raceIndex = (int) (Math.random() * availableRaces.size());
            return availableRaces.remove(raceIndex);
        }
    }

    public int getRacesPerPlayer() {
        return racesPerPlayer;
    }

    public Player addPlayer(Player newPlayer) {
        synchronized (playerLock) {
            for (Player existingPlayer : players) {
                if (existingPlayer.equals(newPlayer)) {
                    newPlayer = existingPlayer;
                    break;
                }
            }
            players.add(newPlayer);
        }
        return newPlayer;
    }

    public boolean quitPlayer(Player player) {
        synchronized (playerLock) {
            Player leavingPlayer = null;
            for (Player existingPlayer : players) {
                if (existingPlayer.equals(player)) {
                    leavingPlayer = existingPlayer;
                    break;
                }
            }
            if (leavingPlayer != null) {
                synchronized (racesLock) {
                    availableRaces.addAll(leavingPlayer.getRaces());
                }
                players.remove(leavingPlayer);
                return true;
            }
        }
        return false;
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public Player getPlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }

        return null;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void revealChoices() {
        revealed = true;
    }
}
