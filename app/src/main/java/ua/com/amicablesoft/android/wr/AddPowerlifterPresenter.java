package ua.com.amicablesoft.android.wr;

import android.content.Intent;

/**
 * Created by lapa on 03.10.16.
 */

public class AddPowerlifterPresenter {

    public boolean isLastNameValid(String lastName) {
        return lastName.length() > 1;
    }

    public boolean isNameValid(String name) {
        return name.length() > 1;
    }

    public void callWriteNewPowerlifter(String name, String lastName) {
        Repository repository = new Repository();
        repository.writeNewPowerlifter(name, lastName);

    }
}
