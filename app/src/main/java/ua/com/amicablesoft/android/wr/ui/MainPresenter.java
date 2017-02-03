package ua.com.amicablesoft.android.wr.ui;

import android.os.Environment;

import com.google.firebase.auth.FirebaseAuth;

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
import ua.com.amicablesoft.android.wr.models.User;

/**
 * Created by lapa on 25.09.16.
 */

class MainPresenter {

    private final MainView view;
    private Powerlifter currentPowerlifter;
    private Exercise currentExercise;
    private Repository repository;
    private File currentVideoPath;

    MainPresenter (MainView view) {
        this.view = view;
    }

    void onStart() {
        view.showLoading();
        repository = new Repository();
        repository.getPowerlifters(new IRepository.LoadPowerliftersCallback() {
            @Override
            public void onPowerliftersLoaded(ArrayList<Powerlifter> powerlifters) {
                view.setListPowerlifters(powerlifters);
                if (powerlifters.isEmpty()) {
                    view.openAddPowerlifterView();
                }
                view.dismissLoading();
            }

            @Override
            public void onDataNotAvailable() {
                view.dismissLoading();
            }
        });
        view.setPowerlifter(0);
        view.setExercise(1);
        currentExercise = Exercise.BenchPress;
    }

    void onPowerlifterAdded() {
        repository.getPowerlifters(new IRepository.LoadPowerliftersCallback() {
            @Override
            public void onPowerliftersLoaded(ArrayList<Powerlifter> powerlifters) {
                view.setListPowerlifters(powerlifters);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
        view.setPowerlifter(0);
    }

    void changePowerlifter(Powerlifter powerlifter) {
        currentPowerlifter = powerlifter;
    }

    void changeExercise(Integer exercise) {
        if (exercise == 0) {
            currentExercise = Exercise.Squats;
        } else if (exercise == 1) {
            currentExercise = Exercise.BenchPress;
        } else if (exercise == 2) {
            currentExercise = Exercise.DeadLift;
        }
    }

    private String createVideoFileName() throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String name = getPowerlifterName();
        String exercise = currentExercise.toString();
        String setNumber = getSetNumber();
        return name + "-" + exercise + "-" + date + "-" + setNumber + ".mp4";
    }

    private String createDirName() {
        if (currentPowerlifter != null) {
            String powerlifterName = currentPowerlifter.getLastName() + "-" + currentPowerlifter.getName();
            String exercise = currentExercise.toString();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            return "/" + powerlifterName + "/" + exercise + "/" + date + "/";
        } else {
            view.showSnackbar(R.string.snackbar_text_powerlifter);
            return null;
        }
    }

    private File createFile() throws IOException {
        String dirName = createDirName();
        File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File videoPath = new File(filePath, dirName);
        videoPath.mkdirs();
        currentVideoPath = videoPath;
        String fileName = createVideoFileName();
        return new File(videoPath, fileName);
    }

    void onAuthInSuccess() {
        final Repository repository = new Repository();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final User user = new User(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail());
        repository.userExist(user, new IRepository.LoadUserCallback() {
                    @Override
                    public void found(boolean userFound) {
                        if (!userFound) {
                            repository.userSave(user);
                        }
                    }
                });
    }

    void onNewVideoAction() {
        view.requestPermissions();
    }

    void onSnackbarVideoAction() {
        view.showSnackbar(R.string.snackbar_text_video);
    }

    void onSnackbarPermissions() {
        view.showSnackbar(R.string.snackbar_text_permissions);
    }

    void createVideo() throws IOException {
        File videoFile = createFile();
        view.recordVideo(videoFile);
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
