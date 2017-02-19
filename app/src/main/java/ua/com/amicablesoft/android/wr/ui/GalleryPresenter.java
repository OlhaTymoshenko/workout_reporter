package ua.com.amicablesoft.android.wr.ui;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.dal.IRepository;
import ua.com.amicablesoft.android.wr.dal.Repository;
import ua.com.amicablesoft.android.wr.models.Competition;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

/**
 * Created by olha on 2/16/17.
 */

class GalleryPresenter {

    private final GalleryView view;
    private Repository repository;

    GalleryPresenter (GalleryView view) {
        this.view = view;
    }

    void setPowerlifters() {
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

    List<String> getCompetitions(final Context context) {
        repository = new Repository();
        final List<String> listItems = new ArrayList<>();
        listItems.add(context.getString(R.string.title_exercise));
        listItems.add(context.getString(R.string.title_squats));
        listItems.add(context.getString(R.string.title_bench_press));
        listItems.add(context.getString(R.string.title_dead_lift));
        listItems.add(context.getString(R.string.title_competition));
        repository.getCompetitions(new IRepository.LoadCompetitionsCallback() {
            @Override
            public void onCompetitionsLoaded(ArrayList<Competition> competitionArrayList) {
                for (Competition c : competitionArrayList) {
                    listItems.add(c.getCompetition());
                }
                listItems.add(context.getString(R.string.button_show));
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
        return listItems;
    }
}
