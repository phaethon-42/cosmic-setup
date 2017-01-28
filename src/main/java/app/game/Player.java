package app.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phaethon on 21-Jan-17
 */
public class Player {
    private String name;
    private List<Race> races;
    private String chosenRace;

    public Player(String name, Game game) {
        this.name = name;
        races = new ArrayList<>();
        for (int i = 0; i < game.getRacesPerPlayer(); ++i) {
            races.add(game.takeRace());
        }
    }

    public List<Race> getRaces() {
        return races;
    }

    public String getChosenRace() {
        return chosenRace;
    }

    public void setChosenRace(String chosenRace) {
        this.chosenRace = chosenRace;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;

        Player player = (Player) o;

        return name != null ? name.equals(player.name) : player.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
