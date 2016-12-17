package ua.com.amicablesoft.android.wr.ui;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.dal.IRepository;
import ua.com.amicablesoft.android.wr.dal.Repository;
import ua.com.amicablesoft.android.wr.models.Exercise;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

/**
 * Created by lapa on 25.09.16.
 */

public class MainPresenter {

    private final MainView mainView;
    private Powerlifter currentPowerlifter;
    private Exercise currentExercise;
    private Repository repository;
    private File currentVideoPath;

    public MainPresenter (MainView mainView) {
        this.mainView = mainView;
    }

    public void start() {
        repository = new Repository();
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
        mainView.setExercise(1);
        currentExercise = Exercise.BenchPress;
    }

    public void onPowerlifterAdded() {
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
    }

    public void changePowerlifter(Powerlifter powerlifter) {
        currentPowerlifter = powerlifter;
    }

    public void changeExercise(Integer exercise) {
        if (exercise == 0) {
            currentExercise = Exercise.Squats;
        } else if (exercise == 1) {
            currentExercise = Exercise.BenchPress;
        } else if (exercise == 2) {
            currentExercise = Exercise.DeadLift;
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
            mainView.showSnackbar(R.string.snackbar_text_powerlifter);
            return null;
        }
    }

    public File createFile() throws IOException {
        String dirName = createDirName();
        File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File videoPath = new File(filePath, dirName);
        videoPath.mkdirs();
        currentVideoPath = videoPath;
        String fileName = createVideoFileName();
        return new File(videoPath, fileName);
    }

    public void callWriteNewUser() {
        Repository repository = new Repository();
        repository.writeNewUser();
    }

    public void onNewVideoAction() {
        mainView.requestPermissions();
    }

    public void setSnackbarVideoAction() {
        mainView.showSnackbar(R.string.snackbar_text_video);
    }

    public void setSnackbarPermissions() {
        mainView.showSnackbar(R.string.snackbar_text_permissions);
    }

    public void createVideo() throws IOException {
        File videoFile = createFile();
        mainView.recordVideo(videoFile);
    }

    private String getPowerlifterName() {
        String name = currentPowerlifter.getName();
        String lastName = currentPowerlifter.getLastName();
        return lastName.substring(0, 1) + name.substring(0, 1);
    }

    private int getNumberOfFiles() {
        int count = 0;
        if (currentVideoPath != null) {
            count = currentVideoPath.listFiles().length;
        }
        return count;
    }

    private String getSetNumber() {
        int count = getNumberOfFiles();
        Integer number = count + 1;
        return number.toString();
    }
}
