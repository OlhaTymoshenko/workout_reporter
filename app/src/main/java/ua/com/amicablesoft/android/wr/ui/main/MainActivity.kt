/*
 * Created by Olha Tymoshenko on 5/25/18 1:39 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import ua.com.amicablesoft.android.wr.R
import ua.com.amicablesoft.android.wr.models.Competition
import ua.com.amicablesoft.android.wr.models.Powerlifter
import ua.com.amicablesoft.android.wr.ui.AddCompetitionActivity
import ua.com.amicablesoft.android.wr.ui.AddPowerlifterActivity
import ua.com.amicablesoft.android.wr.ui.StartActivity
import ua.com.amicablesoft.android.wr.ui.gallery.GalleryActivity
import ua.com.amicablesoft.android.wr.ui.processing.CommonProcessDialogFragment
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), MainView, NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val DIALOG_ID = 3
        internal val PERMISSIONS_REQUEST = 1
        internal val REQUEST_VIDEO_CAPTURE = 0
        internal val REQUEST_ADD_POWERLIFTER = 2
        internal val REQUEST_ADD_COMPETITION = 4
    }

    private var mainPresenter: MainPresenter? = null
    private var spinnerPowerlifters: Spinner? = null
    private var spinnerCompetitions: Spinner? = null
    private var drawerLayout: DrawerLayout? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var video: File? = null
    private val powerlifters = ArrayList<Powerlifter>()
    private val competitions = ArrayList<Competition>()

    private val userEmail: String?
        get() {
            val firebaseAuth = FirebaseAuth.getInstance()
            return firebaseAuth.currentUser!!.email
        }

    private val userName: String?
        get() {
            val firebaseAuth = FirebaseAuth.getInstance()
            return firebaseAuth.currentUser!!.displayName
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.main_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        drawerLayout = findViewById<View>(R.id.main_drawer_layout) as DrawerLayout
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close)
        drawerLayout!!.addDrawerListener(drawerToggle!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        drawerToggle!!.syncState()
        drawerToggle!!.isDrawerIndicatorEnabled = true

        spinnerPowerlifters = findViewById<View>(R.id.spinner_powerlifter) as Spinner
        spinnerPowerlifters!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val powerlifter = powerlifters[i]
                mainPresenter!!.changePowerlifter(powerlifter)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        spinnerCompetitions = findViewById<View>(R.id.spinner_competition) as Spinner
        spinnerCompetitions!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val competition = competitions[position]
                mainPresenter!!.changeCompetition(competition)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        val radioGroup = findViewById<View>(R.id.radio_group) as RadioGroup
        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<View>(i) as RadioButton
            val id = radioButton.id
            val exercise = chooseExercise(id)
            mainPresenter!!.changeExercise(exercise)
        }
        val floatingActionButton = findViewById<View>(R.id.fab) as FloatingActionButton
        floatingActionButton.setOnClickListener { mainPresenter!!.onNewVideoAction() }
        val navigationView = findViewById<View>(R.id.navigation_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        val nameTextView = navigationView.getHeaderView(0).findViewById<View>(R.id.user_name_text_view) as TextView
        nameTextView.text = userName
        val emailTextView = navigationView.getHeaderView(0).findViewById<View>(R.id.user_email_text_view) as TextView
        emailTextView.text = userEmail
        initPresenter()
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle!!.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add_lifter) {
            openAddPowerlifterView()
        } else if (id == R.id.action_add_competition) {
            openAddCompetitionView()
        } else if (id == R.id.action_open_gallery) {
            openVideoGallery()
        } else if (id == R.id.action_sign_out) {
            showLoading()
            signOut()
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                mainPresenter!!.onSnackbarVideoAction()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val deleted = video!!.delete()
            }
        }
        if (requestCode == REQUEST_ADD_POWERLIFTER) {
            if (resultCode == Activity.RESULT_OK) {
                mainPresenter!!.onPowerlifterAdded()
            }
        }
        if (requestCode == REQUEST_ADD_COMPETITION) {
            if (resultCode == Activity.RESULT_OK) {
                mainPresenter!!.onCompetitionAdded()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                mainPresenter!!.createVideo()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            mainPresenter!!.onSnackbarPermissions()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                mainPresenter!!.createVideo()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST)
        }
    }

    @Throws(IOException::class)
    override fun recordVideo(videoFile: File) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            video = videoFile
            val contentUri: Uri
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                contentUri = Uri.fromFile(video)
            } else {
                contentUri = FileProvider.getUriForFile(applicationContext,
                        "ua.com.amicablesoft.android.wr.fileprovider", video!!)
                val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                for (info in resolveInfos) {
                    val packageName = info.activityInfo.packageName
                    grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
        }
    }

    override fun setListPowerlifters(list: ArrayList<Powerlifter>) {
        powerlifters.clear()
        powerlifters.addAll(list)
        val powerlifterNames = powerlifterNames(powerlifters)
        val powerlifterAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, powerlifterNames)
        spinnerPowerlifters!!.adapter = powerlifterAdapter
    }

    override fun setListCompetitions(listCompetitions: ArrayList<Competition>) {
        competitions.clear()
        competitions.addAll(listCompetitions)
        val competitions = competitions(listCompetitions)
        val competitionAdapter = ArrayAdapter(applicationContext, R.layout.spinner_item, competitions)
        spinnerCompetitions!!.adapter = competitionAdapter
    }

    override fun setPowerlifter(position: Int) {
        spinnerPowerlifters!!.setSelection(position)
    }

    override fun setCompetition(position: Int) {
        spinnerCompetitions!!.setSelection(position)
    }

    override fun setExercise(exercise: Int?) {
        if (exercise == 0) {
            val radioButtonSquats = findViewById<View>(R.id.radio_button_squats) as RadioButton
            radioButtonSquats.isChecked = true
        } else if (exercise == 1) {
            val radioButtonBenchPress = findViewById<View>(R.id.radio_button_bench_press) as RadioButton
            radioButtonBenchPress.isChecked = true
        } else if (exercise == 2) {
            val radioButtonDeadLift = findViewById<View>(R.id.radio_button_dead_lift) as RadioButton
            radioButtonDeadLift.isChecked = true
        }
    }

    override fun showSnackbar(message: Int) {
        Snackbar.make(findViewById(R.id.activity_main), message,
                Snackbar.LENGTH_LONG).show()
    }

    override fun showLoading() {
        val dialog = CommonProcessDialogFragment()
        dialog.show(this.fragmentManager, DIALOG_ID)
    }

    override fun dismissLoading() {
        val dialog = CommonProcessDialogFragment()
        dialog.dismiss(this.fragmentManager, DIALOG_ID)
    }

    override fun openAddPowerlifterView() {
        val intent = Intent(this, AddPowerlifterActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_POWERLIFTER)
    }

    override fun openAddCompetitionView() {
        val intent = Intent(this, AddCompetitionActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_COMPETITION)
    }

    override fun openVideoGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            val intent = Intent(this@MainActivity, StartActivity::class.java)
            startActivity(intent)
            dismissLoading()
            finish()
        }
    }

    private fun initPresenter() {
        if (mainPresenter == null) {
            mainPresenter = MainPresenter(this)
            mainPresenter!!.onStart()
        }
    }

    private fun chooseExercise(id: Int): Int? {
        var exercise: Int? = null
        if (id == R.id.radio_button_squats) {
            exercise = 0
        } else if (id == R.id.radio_button_bench_press) {
            exercise = 1
        } else if (id == R.id.radio_button_dead_lift) {
            exercise = 2
        }
        return exercise
    }

    private fun powerlifterNames(powerlifters: List<Powerlifter>): List<String> {
        val names = ArrayList<String>()
        for ((name, lastName) in powerlifters) {
            names.add("$lastName $name")
        }
        return names
    }

    private fun competitions(competitions: List<Competition>): List<String> {
        val listCompetitions = ArrayList<String>()
        for ((competition) in competitions) {
            listCompetitions.add(competition!!)
        }
        return listCompetitions
    }
}


