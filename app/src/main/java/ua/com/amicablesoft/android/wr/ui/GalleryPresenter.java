package ua.com.amicablesoft.android.wr.ui;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.dal.IRepository;
import ua.com.amicablesoft.android.wr.dal.Repository;
import ua.com.amicablesoft.android.wr.dal.VideoRepository;
import ua.com.amicablesoft.android.wr.models.Competition;
import ua.com.amicablesoft.android.wr.models.Powerlifter;
import ua.com.amicablesoft.android.wr.models.Specification;
import ua.com.amicablesoft.android.wr.models.VideoFile;

/**
 * Created by olha on 2/16/17.
 */

class GalleryPresenter {

    private final GalleryView view;
    private Repository repository;
    private Powerlifter currentPowerlifter;
    private Context context;

    GalleryPresenter (GalleryView view, Context context) {
        this.view = view;
        this.context = context;
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

    void getCompetitions(final Context context) {
        repository = new Repository();
        repository.getCompetitions(new IRepository.LoadCompetitionsCallback() {
            @Override
            public void onCompetitionsLoaded(ArrayList<Competition> competitionArrayList) {
                ArrayList<String> listItems = new ArrayList<>();
                listItems.add(context.getString(R.string.title_exercise));
                listItems.add(context.getString(R.string.title_squats));
                listItems.add(context.getString(R.string.title_bench_press));
                listItems.add(context.getString(R.string.title_dead_lift));
                listItems.add(context.getString(R.string.title_competition));
                for (Competition c : competitionArrayList) {
                    listItems.add(c.getCompetition());
                }
                listItems.add(context.getString(R.string.button_show));
                view.setListItems(listItems);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    void setCurrentPowerlifter (Powerlifter powerlifter) {
        currentPowerlifter = powerlifter;
    }

    private String getCurrentPowerlifterName() {
        return currentPowerlifter.getLastName() + "-" + currentPowerlifter.getName();
    }

    List<VideoFile> getVideoFiles(Specification specification) {
        VideoRepository videoRepository = new VideoRepository(context);
        specification.setPowerlifterName(getCurrentPowerlifterName());
        return videoRepository.getListVideoFiles(specification);
    }
}
