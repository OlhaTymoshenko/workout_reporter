package ua.com.amicablesoft.android.wr.ui;

import ua.com.amicablesoft.android.wr.dal.Repository;

/**
 * Created by olha on 2/28/17.
 */

public class AddCompetitionPresenter {

    public boolean isCompetitionValid(String competition) {
        return competition.length() > 1;
    }

    public void callWriteNewCompetition(String competition) {
        Repository repository = new Repository();
        repository.writeNewCompetition(competition);

    }
}
