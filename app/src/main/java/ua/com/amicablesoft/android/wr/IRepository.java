package ua.com.amicablesoft.android.wr;

import java.util.ArrayList;

import ua.com.amicablesoft.android.wr.models.Powerlifter;

/**
 * Created by lapa on 07.10.16.
 */
public interface IRepository {

    interface LoadPowerliftersCallback {
        void onPowerliftersLoaded(ArrayList<Powerlifter> powerlifterArrayList);
        void onDataNotAvailable();
    }

    void writeNewPowerlifter(String name, String lastName);

    void writeNewUser();

    void getPowerlifters(LoadPowerliftersCallback loadPowerliftersCallback);

    ArrayList<Powerlifter> readPowerlifters();
}
