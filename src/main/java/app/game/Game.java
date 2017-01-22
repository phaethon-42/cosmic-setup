package app.game;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Phaethon on 21-Jan-17
 */
public class Game {
    private Set<Player> players;
    private List<String> availableRaces;
    private int racesPerPlayer = 2;
    private boolean revealed;
    private final Object playerLock = new Object();
    private final Object racesLock = new Object();

    public Game() throws IOException {
        players = new HashSet<Player>();
        availableRaces = getRaceNames();
    }

    private List<String> getRaceNames() throws IOException {
        List<String> raceNames = new ArrayList<String>();
        ClassPathResource raceDirectory = new ClassPathResource("races");
        File raceDirectoryFile = raceDirectory.getFile();
        if (raceDirectoryFile.isDirectory()) {
            File[] races = raceDirectoryFile.listFiles();
            if (races != null) {
                for (File race : races) {
                    String name = race.getName();
                    if (name.contains(".")) {
                        name = name.substring(0, name.indexOf("."));
                    }
                    raceNames.add(name);
                }
            }
        }
        return raceNames;
    }

    public List<String> getAvailableRaces() {
        return availableRaces;
    }

    public String takeRace() {
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
