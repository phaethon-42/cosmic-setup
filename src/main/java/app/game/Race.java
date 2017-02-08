package app.game;

/**
 * Created by Phaethon on 28-Jan-17
 */
public class Race {
    private String raceName;
    private String fileName;
    
    public Race(String raceName, String fileName) {
        this.raceName = raceName;
        this.fileName = fileName;
    }

    public String getRaceName() {
        return raceName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return raceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Race)) return false;

        Race race = (Race) o;

        return getRaceName() != null ? getRaceName().equals(race.getRaceName()) : race.getRaceName() == null;
    }

    @Override
    public int hashCode() {
        return getRaceName() != null ? getRaceName().hashCode() : 0;
    }
}
