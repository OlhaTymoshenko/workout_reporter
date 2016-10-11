package ua.com.amicablesoft.android.wr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ua.com.amicablesoft.android.wr.models.Exercise;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

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
        Repository repository = new Repository();
        repository.getPowerlifters(new IRepository.LoadPowerliftersCallback() {
            @Override
            public void onPowerliftersLoaded(ArrayList<Powerlifter> powerlifterArrayList) {
                ArrayList<Powerlifter> powerlifters = new ArrayList<>();
                powerlifters.addAll(powerlifterArrayList);
                mainView.setListPowerlifters(powerlifters);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
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

    public String createVideoFileName() throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String name = getPowerlifterName();
        String exercise = currentExercise.toString();
        String setNumber = getSetNumber();
        return name + "-" + exercise + "-" + date + "-" + setNumber + ".mp4";
    }

    public String createDirName() {
        if (currentPowerlifter != null) {
            String powerlifterName = currentPowerlifter.getLastName() + "-" + currentPowerlifter.getName();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String exercise = currentExercise.toString();
            return "/" + powerlifterName + "/" + date + "/" + exercise + "/";
        } else {
            mainView.setError();
            return null;
        }

    }

    public void callWriteNewUser() {
        Repository repository = new Repository();
        repository.writeNewUser();
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
}
