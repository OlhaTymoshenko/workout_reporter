/*
 * Created by Olha Tymoshenko on 5/24/18 8:32 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.gallery;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.dal.Specification;
import ua.com.amicablesoft.android.wr.models.Powerlifter;
import ua.com.amicablesoft.android.wr.models.VideoFile;
import ua.com.amicablesoft.android.wr.ui.processing.CommonProcessDialogFragment;

public class GalleryActivity extends AppCompatActivity implements GalleryView {

    private static final String TAG = GalleryActivity.class.getSimpleName();
    private static final int DIALOG_ID = 3;
    private final List<Powerlifter> powerlifters = new ArrayList<>();
    private GalleryPresenter galleryPresenter;
    private DrawerLayout drawer;
    private Spinner spinner;
    private GalleryAdapter galleryAdapter;
    private DrawerAdapter drawerAdapter;
    private RecyclerView recyclerView;
    private Specification specification;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.END;
        getSupportActionBar().setCustomView(getLayoutInflater()
                .inflate(R.layout.custom_right_bar, null), layoutParams);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView rightToggle = (ImageView) toolbar.findViewById(R.id.right_toggle);
        rightToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
        ListView rightDrawer = (ListView) findViewById(R.id.right_drawer);
        galleryPresenter = new GalleryPresenter(this, getApplicationContext());
        drawerAdapter = new DrawerAdapter(getApplicationContext());
        galleryPresenter.getCompetitions(getApplicationContext());
        specification = new Specification();
        drawerAdapter.setButtonClickListener(new DrawerAdapter.ButtonClickListener() {
            @Override
            public void onButtonClick(Specification spec) {
                specification = spec;
                createObservable(specification);
                drawer.closeDrawer(GravityCompat.END);
            }
        });
        rightDrawer.setAdapter(drawerAdapter);

        spinner = (Spinner) findViewById(R.id.spinner_gallery_powerlifter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Powerlifter powerlifter = powerlifters.get(position);
                galleryPresenter.setCurrentPowerlifter(powerlifter);
                createObservable(specification);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        galleryPresenter.setPowerlifters();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(gridLayoutManager);
        galleryAdapter = new GalleryAdapter(getApplicationContext());
        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setListPowerlifters(ArrayList<Powerlifter> list) {
        powerlifters.clear();
        powerlifters.addAll(list);
        List<String> powerlifterNames = getPowerlifterNames(powerlifters);
        ArrayAdapter powerlifterAdapter =
                new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_gallery_item, powerlifterNames);
        spinner.setAdapter(powerlifterAdapter);
    }

    @Override
    public void setPowerlifter(int position) {
        spinner.setSelection(position);
    }

    @Override
    public void setListItems(ArrayList<String> items) {
        drawerAdapter.setListItems(items);
    }

    @Override
    public void showLoading() {
        CommonProcessDialogFragment.show(this.getFragmentManager(), DIALOG_ID);
    }

    @Override
    public void dismissLoading() {
        CommonProcessDialogFragment.dismiss(this.getFragmentManager(), DIALOG_ID);
    }

    private List<String> getPowerlifterNames(List<Powerlifter> powerlifters) {
        List<String> names = new ArrayList<>();
        for (Powerlifter p : powerlifters) {
            names.add(p.getLastName() + " " + p.getName());
        }
        return names;
    }

    private void createObservable(final Specification specification) {
        showLoading();
        Observable<List<VideoFile>> listObservable = Observable.fromCallable(new Callable<List<VideoFile>>() {
            @Override
            public List<VideoFile> call() {
                return galleryPresenter.getVideoFiles(specification);
            }
        });

        subscription = listObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<List<VideoFile>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Error", e);
                            }

                            @Override
                            public void onNext(List<VideoFile> videoFiles) {
                                TextView textView = (TextView) findViewById(R.id.empty_view);
                                if (videoFiles == null) {
                                    textView.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    textView.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    galleryAdapter.setVideoFilesList(videoFiles);
                                }
                                dismissLoading();
                            }
                        });
    }
}
