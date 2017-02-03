package ua.com.amicablesoft.android.wr.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ua.com.amicablesoft.android.wr.models.Powerlifter;

/**
 * Created by lapa on 25.09.16.
 */

public interface MainView {
    void setExercise(Integer exercise);
    void setPowerlifter(int position);
    void setListPowerlifters(ArrayList<Powerlifter> list);
    void requestPermissions();
    void showSnackbar(int message);
    void recordVideo(File videoFile) throws IOException;

    void showLoading();
    void dismissLoading();

    void openAddPowerlifterView();
    void openVideoGallery();
}
