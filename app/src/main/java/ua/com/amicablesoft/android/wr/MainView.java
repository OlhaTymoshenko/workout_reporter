package ua.com.amicablesoft.android.wr;

import java.util.ArrayList;

import ua.com.amicablesoft.android.wr.models.Exercise;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

/**
 * Created by lapa on 25.09.16.
 */

public interface MainView {

    void setPowerlifter(int position);
    void setExercise(Exercise exercise);
    void setListPowerlifters(ArrayList<Powerlifter> list);
    int getNumberOfFiles();
    void setError();
}
