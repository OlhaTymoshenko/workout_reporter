package ua.com.amicablesoft.android.wr.ui;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ua.com.amicablesoft.android.wr.R;

public class AddCompetitionActivity extends AppCompatActivity implements AddCompetitionView {

    private AddCompetitionPresenter addCompetitionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_competition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.competition_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        addCompetitionPresenter = new AddCompetitionPresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_competition_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_save_new_competition:
                attemptSaveNewCompetition(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptSaveNewCompetition(MenuItem item) {
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.competition_input_layout);
        textInputLayout.setError(null);
        TextInputEditText textInputEditText = (TextInputEditText) findViewById(R.id.competition_edit_text);
        String competition = textInputEditText.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(competition)) {
            textInputLayout.setError(getString(R.string.error_required_field));
            focusView = textInputLayout;
            cancel = true;
        } else if (!addCompetitionPresenter.isCompetitionValid(competition)) {
            textInputLayout.setError(getString(R.string.error_short_competition));
            focusView = textInputLayout;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();

        } else {
            addCompetitionPresenter.callWriteNewCompetition(competition);
            setResult(RESULT_OK);
            item.setEnabled(false);
            finish();
        }
    }
}

