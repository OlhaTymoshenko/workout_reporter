package ua.com.amicablesoft.android.wr;

import com.ibm.icu.text.Transliterator;

import java.io.File;
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
        mainView.setPowerlifter(1);
        mainView.setExercise(Exercise.BenchPress);
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

    public File makeVideo(File storageDir) {
        File file = null;
        try {
            file = createVideoFile(storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private File createVideoFile(File storageDir) throws IOException {
        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String name = getPowerlifterName();
        String exercise = currentExercise.toString();
        String setNumber = getSetNumber();
        String videoFileName = date + "_" + name + "_" + exercise + "_" + setNumber + "_";
        return File.createTempFile(videoFileName, ".mp4", storageDir);
    }

    private String getSetNumber() {
        return "1";
    }

    private String getPowerlifterName() {
        String name = currentPowerlifter.getName();
        if (name.contains(" ")) {
            int index = name.indexOf(" ");
            name = name.substring(0, index);
        }
        if (name.contains("ь")) {
            name = name.replace("ь", "");
        }
        String id = "NFD; Any-Latin; NFC; [:Nonspacing Mark:] Remove";
        return Transliterator.getInstance(id).transform(name);

    }

    public ArrayList<Powerlifter> createList() {
        ArrayList<Powerlifter> powerlifters = new ArrayList<>();
        Powerlifter powerlifter = new Powerlifter("1", "Крымов Андрей");
        powerlifters.add(powerlifter);
        Powerlifter powerlifter1 = new Powerlifter("2", "Наньев Андрей");
        powerlifters.add(powerlifter1);
        Powerlifter powerlifter2 = new Powerlifter("3", "Соловьева Лариса");
        powerlifters.add(powerlifter2);
        return powerlifters;
    }
}
