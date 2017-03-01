package ua.com.amicablesoft.android.wr.models;

/**
 * Created by olha on 2/27/17.
 */

public class Specification {

    private boolean squats;
    private boolean benchPress;
    private boolean deadLift;
    private String powerlifterName;
    private String competition;

    public Specification() {
        squats = true;
        benchPress = true;
        deadLift = true;
        competition = "- None -";
    }

    public boolean isSquats() {
        return squats;
    }

    public void setSquats(boolean squats) {
        this.squats = squats;
    }

    public boolean isBenchPress() {
        return benchPress;
    }

    public void setBenchPress(boolean benchPress) {
        this.benchPress = benchPress;
    }

    public boolean isDeadLift() {
        return deadLift;
    }

    public void setDeadLift(boolean deadLift) {
        this.deadLift = deadLift;
    }

    public String getPowerlifterName() {
        return powerlifterName;
    }

    public void setPowerlifterName(String powerlifterName) {
        this.powerlifterName = powerlifterName;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }
}
