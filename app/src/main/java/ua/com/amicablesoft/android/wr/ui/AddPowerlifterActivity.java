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

public class AddPowerlifterActivity extends AppCompatActivity implements AddPowerlifterView {

    private AddPowerlifterPresenter addPowerlifterPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_powerlifter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        addPowerlifterPresenter = new AddPowerlifterPresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_powerlifter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.action_save_new_powerlifter:
                attemptSaveNewPowerlifter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptSaveNewPowerlifter() {
        TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.name_input_layout);
        nameLayout.setError(null);
        TextInputLayout lastNameLayout = (TextInputLayout) findViewById(R.id.last_name_input_layout);
        lastNameLayout.setError(null);
        TextInputEditText nameView = (TextInputEditText) findViewById(R.id.name_edit_text);
        String name = nameView.getText().toString();
        TextInputEditText lastNameView = (TextInputEditText) findViewById(R.id.last_name_edit_text);
        String lastName = lastNameView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(lastName)) {
            lastNameLayout.setError(getString(R.string.error_required_field));
            focusView = lastNameLayout;
            cancel = true;
        } else if (!addPowerlifterPresenter.isLastNameValid(lastName)) {
            lastNameLayout.setError(getString(R.string.error_short_last_name));
            focusView = lastNameLayout;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.error_required_field));
            focusView = nameLayout;
            cancel = true;
        } else if (!addPowerlifterPresenter.isNameValid(name)) {
            nameLayout.setError(getString(R.string.error_short_name));
            focusView = nameLayout;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            addPowerlifterPresenter.callWriteNewPowerlifter(name, lastName);
            setResult(RESULT_OK);
            finish();
        }
    }
}
