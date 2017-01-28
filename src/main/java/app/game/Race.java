package app.game;

/**
 * Created by Phaethon on 28-Jan-17
 */
public class Race {
    private String raceName;
    private String fileName;
    
    Race(String raceName, String fileName) {
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
}
