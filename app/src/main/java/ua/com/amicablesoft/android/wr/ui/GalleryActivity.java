package ua.com.amicablesoft.android.wr.ui;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

public class GalleryActivity extends AppCompatActivity implements GalleryView {

    private GalleryPresenter galleryPresenter;
    private DrawerLayout drawer;
    private ListView rightDrawer;
    private Spinner spinner;
    private final List<Powerlifter> powerlifters = new ArrayList<>();

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
        rightDrawer = (ListView) findViewById(R.id.right_drawer);
        galleryPresenter = new GalleryPresenter(this);
        List<String> items = galleryPresenter.getCompetitions(getApplicationContext());
        final DrawerAdapter drawerAdapter = new DrawerAdapter(getApplicationContext(), items);
        drawerAdapter.setButtonClickListener(new DrawerAdapter.ButtonClickListener() {
            @Override
            public void onButtonClick() {
                drawer.closeDrawer(GravityCompat.END);
            }
        });
        rightDrawer.setAdapter(drawerAdapter);

        spinner = (Spinner) findViewById(R.id.spinner_gallery_powerlifter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        galleryPresenter.setPowerlifters();
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

    private List<String> getPowerlifterNames(List<Powerlifter> powerlifters) {
        List<String> names = new ArrayList<>();
        for (Powerlifter p : powerlifters) {
            names.add(p.getLastName() + " " + p.getName());
        }
        return names;
    }
}
