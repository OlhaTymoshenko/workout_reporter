package ua.com.amicablesoft.android.wr.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

public class MainActivity extends AppCompatActivity implements MainView,
        NavigationView.OnNavigationItemSelectedListener {

    private Spinner spinner;
    private MainPresenter mainPresenter;
    private File video;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private static final int DIALOG_ID = 3;
    static final int PERMISSIONS_REQUEST = 1;
    static final int REQUEST_VIDEO_CAPTURE = 0;
    static final int REQUEST_ADD_POWERLIFTER = 2;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();
        drawerToggle.setDrawerIndicatorEnabled(true);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Powerlifter powerlifter = (Powerlifter) adapterView.getAdapter().getItem(i);
                mainPresenter.changePowerlifter(powerlifter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                int id = radioButton.getId();
                Integer exercise = chooseExercise(id);
                mainPresenter.changeExercise(exercise);
            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPresenter.onNewVideoAction();
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (isAuthenticated()) {
            initPresenter();
        } else {
            startAuthActivity();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_lifter) {
            openAddPowerlifterView();
        } else if (id == R.id.action_open_gallery) {

        } else if (id == R.id.action_sign_out) {
            showLoading();
            signOut();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isAuthenticated()) {
            initPresenter();
        } else {
            startAuthActivity();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                initPresenter();
                mainPresenter.onAuthInSuccess();
            }
            if (resultCode == RESULT_CANCELED){
                Snackbar.make(findViewById(R.id.activity_main),
                        R.string.snackbar_text_sign_in_canceled, Snackbar.LENGTH_LONG).show();
            }
            if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                Snackbar.make(findViewById(R.id.activity_main),
                        R.string.snackbar_text_no_internet_connection, Snackbar.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mainPresenter.onSnackbarVideoAction();
            } else if (resultCode == RESULT_CANCELED) {
                boolean deleted = video.delete();
            }
        }
        if (requestCode == REQUEST_ADD_POWERLIFTER) {
            if (resultCode == RESULT_OK) {
                mainPresenter.onPowerlifterAdded();
            }
        }
    }

    @Override
    public void requestPermissions() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED)) {
            try {
                mainPresenter.createVideo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                mainPresenter.createVideo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mainPresenter.onSnackbarPermissions();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setListPowerlifters(ArrayList<Powerlifter> list) {
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), R.layout.spinner_item, list);
        spinner.setAdapter(customAdapter);
    }

    @Override
    public void setPowerlifter(int position) {
        spinner.setSelection(position);
    }

    @Override
    public void setExercise(Integer exercise) {
        if (exercise == 0) {
            RadioButton radioButtonSquats = (RadioButton) findViewById(R.id.radio_button_squats);
            radioButtonSquats.setChecked(true);
        } else if (exercise == 1) {
            RadioButton radioButtonBenchPress = (RadioButton) findViewById(R.id.radio_button_bench_press);
            radioButtonBenchPress.setChecked(true);
        } else if (exercise == 2) {
            RadioButton radioButtonDeadLift = (RadioButton) findViewById(R.id.radio_button_dead_lift);
            radioButtonDeadLift.setChecked(true);
        }
    }

    @Override
    public void openAddPowerlifterView() {
        Intent intent = new Intent(this, AddPowerlifterActivity.class);
        startActivityForResult(intent, REQUEST_ADD_POWERLIFTER);
    }

    @Override
    public void showLoading() {
        CommonProcessDialogFragment.show(this.getFragmentManager(), DIALOG_ID);
    }

    @Override
    public void dismissLoading() {
        CommonProcessDialogFragment.dismiss(this.getFragmentManager(), DIALOG_ID);
    }

    @Override
    public void showSnackbar(int message) {
        Snackbar.make(findViewById(R.id.activity_main), message,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void recordVideo(File videoFile) throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            video = videoFile;
            Uri contentUri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                contentUri = Uri.fromFile(video);
            } else {
                contentUri = FileProvider.getUriForFile(getApplicationContext(),
                        "ua.com.amicablesoft.android.wr.fileprovider", video);
                List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
                for (ResolveInfo info : resolveInfos) {
                    String packageName = info.activityInfo.packageName;
                    grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void startAuthActivity() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.ic_icon)
                        .build(),
                RC_SIGN_IN);
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                dismissLoading();
            }
        });
    }

    private boolean isAuthenticated() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser() != null;
    }

    private void initPresenter() {
        if (mainPresenter == null) {
            mainPresenter = new MainPresenter(this);
            mainPresenter.onStart();
        }
    }

    private Integer chooseExercise(int id) {
        Integer exercise = null;
        if (id == R.id.radio_button_squats) {
            exercise = 0;
        } else if (id == R.id.radio_button_bench_press) {
            exercise = 1;
        } else if (id == R.id.radio_button_dead_lift) {
            exercise = 2;
        }
        return exercise;
    }
}


