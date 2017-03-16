package ua.com.amicablesoft.android.wr.ui.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ua.com.amicablesoft.android.wr.models.Competition;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

/**
 * Created by lapa on 25.09.16.
 */

interface MainView {
    void requestPermissions();
    void recordVideo(File videoFile) throws IOException;

    void setListPowerlifters(ArrayList<Powerlifter> list);
    void setListCompetitions(ArrayList<Competition> listCompetitions);

    void setPowerlifter(int position);
    void setCompetition(int position);
    void setExercise(Integer exercise);

    void showSnackbar(int message);
    void showLoading();
    void dismissLoading();

    void openAddPowerlifterView();
    void openAddCompetitionView();
    void openVideoGallery();
}
