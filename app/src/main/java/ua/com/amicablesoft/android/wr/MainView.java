package ua.com.amicablesoft.android.wr;

import java.util.ArrayList;

/**
 * Created by lapa on 25.09.16.
 */

public interface MainView {

    void setPowerlifter(int position);
    void setExercise(Exercise exercise);
    void setListPowerlifters(ArrayList<Powerlifter> list);
}
