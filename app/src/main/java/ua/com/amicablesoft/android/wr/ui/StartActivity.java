package ua.com.amicablesoft.android.wr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.ui.main.MainActivity;

public class StartActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            startAuthActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == com.firebase.ui.auth.ResultCodes.OK) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("response", idpResponse);
                startActivity(intent);
                finish();
                return;
            } else {
                if (idpResponse == null) {
                    showSnackbar(R.string.snackbar_text_sign_in_cancelled);
                    startAuthActivity();
                    return;
                }
                if (idpResponse.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.snackbar_text_no_internet_connection);
                    startAuthActivity();
                    return;
                }
                if (idpResponse.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.snackbar_text_unknown_error);
                    startAuthActivity();
                    return;
                }
            }
            showSnackbar(R.string.snackbar_text_unknown_sign_in_response);
            startAuthActivity();
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

    private void showSnackbar(int message) {
        Snackbar.make(findViewById(R.id.activity_start), message, Snackbar.LENGTH_LONG).show();
    }
}
