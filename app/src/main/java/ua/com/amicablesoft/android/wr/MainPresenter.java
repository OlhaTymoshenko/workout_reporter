package ua.com.amicablesoft.android.wr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lapa on 25.09.16.
 */

public class MainPresenter {

    private final MainView mainView;
    private Powerlifter currentPowerlifter;
    private Exercise currentExercise;

    public MainPresenter (MainView mainView) {
        this.mainView = mainView;
    }

    public void start() {
        ArrayList<Powerlifter> powerlifters = createList();
        mainView.setListPowerlifters(powerlifters);
        mainView.setPowerlifter(0);
        mainView.setExercise(Exercise.BenchPress);
        currentExercise = Exercise.BenchPress;
    }

    public void changePowerlifter(Powerlifter powerlifter) {
        currentPowerlifter = powerlifter;
    }

    public void changeExercise(String exercise) {
        switch (exercise) {
            case "Squats":
                currentExercise = Exercise.Squats;
                break;
            case "Bench press":
                currentExercise = Exercise.BenchPress;
                break;
            case "Dead lift":
                currentExercise = Exercise.DeadLift;
                break;
        }

    }

    private String getPowerlifterName() {
        String name = currentPowerlifter.getName();
        String lastName = currentPowerlifter.getLastName();
        return lastName.substring(0, 1) + name.substring(0, 1);

    }

    private String getSetNumber() {
        int count = mainView.getNumberOfFiles();
        Integer number = count + 1;
        return number.toString();
    }

    public String createVideoFileName() throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String name = getPowerlifterName();
        String exercise = currentExercise.toString();
        String setNumber = getSetNumber();
        return name + "-" + exercise + "-" + date + "-" + setNumber + ".mp4";
    }

    public String createDirName() {
        String powerlifterName = currentPowerlifter.getLastName() + "-" + currentPowerlifter.getName();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String exercise = currentExercise.toString();
        return "/" + powerlifterName + "/" + date + "/" + exercise + "/";
    }

    public ArrayList<Powerlifter> createList() {
        ArrayList<Powerlifter> powerlifters = new ArrayList<>();
        Powerlifter powerlifter = new Powerlifter("1", "Андрей", "Крымов");
        powerlifters.add(powerlifter);
        Powerlifter powerlifter1 = new Powerlifter("2", "Андрей", "Наньев");
        powerlifters.add(powerlifter1);
        Powerlifter powerlifter2 = new Powerlifter("3", "Лариса", "Соловьева");
        powerlifters.add(powerlifter2);
        return powerlifters;
    }
}
