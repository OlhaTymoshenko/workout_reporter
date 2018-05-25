/*
 * Created by Olha Tymoshenko on 5/25/18 3:38 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import ua.com.amicablesoft.android.wr.R

class AddCompetitionActivity : AppCompatActivity(), AddCompetitionView {

    private var addCompetitionPresenter: AddCompetitionPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_competition)
        val toolbar = findViewById<View>(R.id.competition_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        addCompetitionPresenter = AddCompetitionPresenter()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_competition_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
                return true
            }
            R.id.action_save_new_competition -> {
                attemptSaveNewCompetition(item)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun attemptSaveNewCompetition(item: MenuItem) {
        val textInputLayout = findViewById<View>(R.id.competition_input_layout) as TextInputLayout
        textInputLayout.error = null
        val textInputEditText = findViewById<View>(R.id.competition_edit_text) as TextInputEditText
        val competition = textInputEditText.text.toString()
        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(competition)) {
            textInputLayout.error = getString(R.string.error_required_field)
            focusView = textInputLayout
            cancel = true
        } else if (!addCompetitionPresenter!!.isCompetitionValid(competition)) {
            textInputLayout.error = getString(R.string.error_short_competition)
            focusView = textInputLayout
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()

        } else {
            addCompetitionPresenter!!.callWriteNewCompetition(competition)
            setResult(Activity.RESULT_OK)
            item.isEnabled = false
            finish()
        }
    }
}

internal interface AddCompetitionView