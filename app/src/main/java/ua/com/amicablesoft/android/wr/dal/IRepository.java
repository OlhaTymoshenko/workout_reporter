package ua.com.amicablesoft.android.wr.dal;

import java.util.ArrayList;

import ua.com.amicablesoft.android.wr.models.Powerlifter;
import ua.com.amicablesoft.android.wr.models.User;

/**
 * Created by lapa on 07.10.16.
 */
public interface IRepository {

    interface LoadUserCallback {
        void found(boolean userFound);
    }

    interface LoadPowerliftersCallback {
        void onPowerliftersLoaded(ArrayList<Powerlifter> powerlifterArrayList);
        void onDataNotAvailable();
    }

    void writeNewPowerlifter(String name, String lastName);

    void getPowerlifters(LoadPowerliftersCallback loadPowerliftersCallback);

    void userExist(User user, LoadUserCallback loadUserCallback);
    void userSave(User user);
}
