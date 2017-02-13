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
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.models.Competition;
import ua.com.amicablesoft.android.wr.models.Powerlifter;

public class MainActivity extends AppCompatActivity implements MainView,
        CompetitionDialogFragment.CompetitionDialogListener {

    private Spinner spinnerPowerlifters;
    private Spinner spinnerCompetitions;
    private MainPresenter mainPresenter;
    private File video;
    private static final int DIALOG_ID = 3;
    static final int PERMISSIONS_REQUEST = 1;
    static final int REQUEST_VIDEO_CAPTURE = 0;
    static final int REQUEST_ADD_POWERLIFTER = 2;

    private final List<Powerlifter> powerlifters = new ArrayList<>();
    private final List<Competition> competitions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerPowerlifters = (Spinner) findViewById(R.id.spinner_powerlifter);
        spinnerPowerlifters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Powerlifter powerlifter = powerlifters.get(i);
                mainPresenter.changePowerlifter(powerlifter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerCompetitions = (Spinner) findViewById(R.id.spinner_competition);
        spinnerCompetitions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Competition competition = competitions.get(position);
                String nameOfCompetition = competition.getCompetition();
                if (nameOfCompetition.contentEquals("- Add new competition -")) {
                    new CompetitionDialogFragment().show(getSupportFragmentManager(), "dialog");
                } else {
                    mainPresenter.changeCompetition(competition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        initPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_lifter:
                openAddPowerlifterView();
                return true;
            case R.id.action_sign_out:
                showLoading();
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void setListPowerlifters(ArrayList<Powerlifter> list) {
        powerlifters.clear();
        powerlifters.addAll(list);
        List<String> powerlifterNames = powerlifterNames(powerlifters);
        ArrayAdapter powerlifterAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, powerlifterNames);
        spinnerPowerlifters.setAdapter(powerlifterAdapter);
    }

    @Override
    public void setListCompetitions(ArrayList<Competition> listCompetitions) {
        competitions.clear();
        competitions.addAll(listCompetitions);
        List<String> competitions = competitions(listCompetitions);
        ArrayAdapter competitionAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, competitions);
        spinnerCompetitions.setAdapter(competitionAdapter);
    }

    @Override
    public void setCompetition(int position) {
        spinnerCompetitions.setSelection(position);
    }

    @Override
    public void setPowerlifter(int position) {
        spinnerPowerlifters.setSelection(position);
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

    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                dismissLoading();
                finish();
            }
        });
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

    private List<String> powerlifterNames(List<Powerlifter> powerlifters) {
        List<String> names = new ArrayList<>();
        for (Powerlifter p : powerlifters) {
            names.add(p.getLastName() + " " + p.getName());
        }
        return names;
    }

    private List<String> competitions(List<Competition> competitions) {
        List<String> listCompetitions = new ArrayList<>();
        for (Competition c : competitions) {
            listCompetitions.add(c.getCompetition());
        }
        return listCompetitions;
    }

    @Override
    public void onOkButtonClick(String competition) {
        mainPresenter.callWriteNewCompetition(competition);
    }
}


