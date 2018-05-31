/*
 * Created by Olha Tymoshenko on 5/25/18 3:45 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import ua.com.amicablesoft.android.wr.R
import ua.com.amicablesoft.android.wr.ui.main.MainActivity
import java.util.*

class StartActivity : AppCompatActivity() {

    companion object {

        private val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            startAuthActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val idpResponse = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("response", idpResponse)
                startActivity(intent)
                finish()
                return
            } else {
                if (idpResponse == null) {
                    showSnackbar(R.string.snackbar_text_sign_in_cancelled)
                    startAuthActivity()
                    return
                }
                if (idpResponse.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.snackbar_text_no_internet_connection)
                    startAuthActivity()
                    return
                }
                if (idpResponse.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.snackbar_text_unknown_error)
                    startAuthActivity()
                    return
                }
            }
            showSnackbar(R.string.snackbar_text_unknown_sign_in_response)
            startAuthActivity()
        }
    }

    private fun startAuthActivity() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList<AuthUI.IdpConfig>(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.ic_icon)
                        .build(),
                RC_SIGN_IN)
    }

    private fun showSnackbar(message: Int) {
        Snackbar.make(findViewById<View>(R.id.activity_start), message, Snackbar.LENGTH_LONG).show()
    }
}
