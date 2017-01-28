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
    private List<Race> availableRaces;
    private int racesPerPlayer = 2;
    private boolean revealed;
    private final Object playerLock = new Object();
    private final Object racesLock = new Object();

    public Game() throws IOException {
        players = new HashSet<Player>();
        availableRaces = getRaces();
    }

    private List<Race> getRaces() throws IOException {
        List<Race> races = new ArrayList<>();
        ClassPathResource raceDirectory = new ClassPathResource("races");
        File raceDirectoryFile = raceDirectory.getFile();
        races.addAll(getRacesFromDir("", raceDirectoryFile));
        return races;
    }

    private List<Race> getRacesFromDir(String path, File raceDirectoryFile) {
        List<Race> races = new ArrayList<>();
        if (raceDirectoryFile.isDirectory()) {
            File[] raceFiles = raceDirectoryFile.listFiles();
            if (raceFiles != null) {
                for (File raceFile : raceFiles) {
                    if (raceFile.isDirectory()) {
                        races.addAll(getRacesFromDir(path + "/" + raceFile.getName(), raceFile));
                    } else {
                        String fileName = raceFile.getName();
                        String name = fileName;
                        if (name.contains(".")) {
                            name = name.substring(0, name.indexOf("."));
                        }
                        races.add(new Race(name, path + "/" + fileName));
                    }
                }
            }
        }
        return races;
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
