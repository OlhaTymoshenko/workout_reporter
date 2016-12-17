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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

import static com.firebase.ui.auth.AuthUI.EMAIL_PROVIDER;
import static com.firebase.ui.auth.AuthUI.GOOGLE_PROVIDER;
import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements MainView {

    private Spinner spinner;
    private MainPresenter mainPresenter;
    private File video;
    static final int PERMISSIONS_REQUEST = 1;
    static final int REQUEST_VIDEO_CAPTURE = 0;
    static final int REQUEST_ADD_POWERLIFTER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        if (isAuthenticated()) {
            initPresenter();
        } else {
            startAuthActivity();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                initPresenter();
                mainPresenter.callWriteNewUser();
            } else {
                finish();
            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mainPresenter.setSnackbarVideoAction();
            } else if (resultCode == RESULT_CANCELED) {
                boolean deleted = video.delete();
            }
        }
        if (requestCode == REQUEST_ADD_POWERLIFTER) {
            if (resultCode == RESULT_CANCELED) {
                mainPresenter.onPowerlifterAdded();
            }
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
            mainPresenter.setSnackbarPermissions();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_lifter:
                Intent intent = new Intent(this, AddPowerlifterActivity.class);
                startActivityForResult(intent, REQUEST_ADD_POWERLIFTER);
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
    public void showSnackbar(int message) {
        Snackbar.make(findViewById(R.id.activity_main), message,
                Snackbar.LENGTH_LONG).show();
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

    private void startAuthActivity() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setProviders(EMAIL_PROVIDER, GOOGLE_PROVIDER)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.ic_icon)
                        .build(),
                RC_SIGN_IN);
    }

    private void initPresenter() {
        mainPresenter = new MainPresenter(this);
        mainPresenter.start();
    }

    private boolean isAuthenticated() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser() != null;
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
//                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}


