/*
 * Created by Olha Tymoshenko on 5/25/18 3:42 PM.
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

class AddPowerlifterActivity : AppCompatActivity(), AddPowerlifterView {

    private var addPowerlifterPresenter: AddPowerlifterPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_powerlifter)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        addPowerlifterPresenter = AddPowerlifterPresenter()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_powerlifter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
                return true
            }
            R.id.action_save_new_powerlifter -> {
                attemptSaveNewPowerlifter(item)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun attemptSaveNewPowerlifter(item: MenuItem) {
        val nameLayout = findViewById<View>(R.id.name_input_layout) as TextInputLayout
        nameLayout.error = null
        val lastNameLayout = findViewById<View>(R.id.last_name_input_layout) as TextInputLayout
        lastNameLayout.error = null
        val nameView = findViewById<View>(R.id.name_edit_text) as TextInputEditText
        val name = nameView.text.toString()
        val lastNameView = findViewById<View>(R.id.last_name_edit_text) as TextInputEditText
        val lastName = lastNameView.text.toString()
        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(lastName)) {
            lastNameLayout.error = getString(R.string.error_required_field)
            focusView = lastNameLayout
            cancel = true
        } else if (!addPowerlifterPresenter!!.isLastNameValid(lastName)) {
            lastNameLayout.error = getString(R.string.error_short_last_name)
            focusView = lastNameLayout
            cancel = true
        }
        if (TextUtils.isEmpty(name)) {
            nameLayout.error = getString(R.string.error_required_field)
            focusView = nameLayout
            cancel = true
        } else if (!addPowerlifterPresenter!!.isNameValid(name)) {
            nameLayout.error = getString(R.string.error_short_name)
            focusView = nameLayout
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()

        } else {
            addPowerlifterPresenter!!.callWriteNewPowerlifter(name, lastName)
            setResult(Activity.RESULT_OK)
            item.isEnabled = false
            finish()
        }
    }
}

interface AddPowerlifterView
