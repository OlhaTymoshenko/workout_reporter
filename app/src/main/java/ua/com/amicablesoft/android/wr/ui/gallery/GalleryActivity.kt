/*
 * Created by Olha Tymoshenko on 5/25/18 1:08 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.gallery

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import ua.com.amicablesoft.android.wr.R
import ua.com.amicablesoft.android.wr.dal.Specification
import ua.com.amicablesoft.android.wr.models.Powerlifter
import ua.com.amicablesoft.android.wr.ui.processing.CommonProcessDialogFragment
import java.util.*

class GalleryActivity : AppCompatActivity(), GalleryView {

    companion object {

        private val TAG = GalleryActivity::class.java.simpleName
        private val DIALOG_ID = 3
    }

    private val powerlifters = ArrayList<Powerlifter>()
    private var galleryPresenter: GalleryPresenter? = null
    private var drawer: DrawerLayout? = null
    private var spinner: Spinner? = null
    private var galleryAdapter: GalleryAdapter? = null
    private var drawerAdapter: DrawerAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var specification: Specification? = null
    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        val layoutParams = ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.END
        supportActionBar!!.setCustomView(layoutInflater
                .inflate(R.layout.custom_right_bar, null), layoutParams)
        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val rightToggle = toolbar.findViewById<View>(R.id.right_toggle) as ImageView
        rightToggle.setOnClickListener {
            if (drawer!!.isDrawerOpen(GravityCompat.END)) {
                drawer!!.closeDrawer(GravityCompat.END)
            } else {
                drawer!!.openDrawer(GravityCompat.END)
            }
        }
        val rightDrawer = findViewById<View>(R.id.right_drawer) as ListView
        galleryPresenter = GalleryPresenter(this, applicationContext)
        drawerAdapter = DrawerAdapter(applicationContext)
        galleryPresenter!!.getCompetitions(applicationContext)
        specification = Specification()
        drawerAdapter!!.setButtonClickListener(object : DrawerAdapter.ButtonClickListener {
            override fun onButtonClick(specification: Specification) {
                this@GalleryActivity.specification = specification
                createObservable(this@GalleryActivity.specification)
                drawer!!.closeDrawer(GravityCompat.END)
            }
        })
        rightDrawer.adapter = drawerAdapter

        spinner = findViewById<View>(R.id.spinner_gallery_powerlifter) as Spinner
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val powerlifter = powerlifters[position]
                galleryPresenter!!.setCurrentPowerlifter(powerlifter)
                createObservable(specification)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        galleryPresenter!!.setPowerlifters()

        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView!!.layoutManager = gridLayoutManager
        galleryAdapter = GalleryAdapter(applicationContext)
        recyclerView!!.adapter = galleryAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.END)) {
            drawer!!.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setListPowerlifters(list: ArrayList<Powerlifter>) {
        powerlifters.clear()
        powerlifters.addAll(list)
        val powerlifterNames = getPowerlifterNames(powerlifters)
        val powerlifterAdapter = ArrayAdapter(applicationContext, R.layout.spinner_gallery_item, powerlifterNames)
        spinner!!.adapter = powerlifterAdapter
    }

    override fun setPowerlifter(position: Int) {
        spinner!!.setSelection(position)
    }

    override fun setListItems(items: ArrayList<String>) {
        drawerAdapter!!.setListItems(items)
    }

    override fun showLoading() {
        val dialog = CommonProcessDialogFragment()
        dialog.show(this.fragmentManager, DIALOG_ID)
    }

    override fun dismissLoading() {
        val dialog = CommonProcessDialogFragment()
        dialog.dismiss(this.fragmentManager, DIALOG_ID)
    }

    private fun getPowerlifterNames(powerlifters: List<Powerlifter>): List<String> {
        val names = ArrayList<String>()
        for ((name, lastName) in powerlifters) {
            names.add("$lastName $name")
        }
        return names
    }

    private fun createObservable(specification: Specification?) {
        showLoading()
        val listObservable = Observable.fromCallable { galleryPresenter!!.getVideoFiles(specification!!) }

        subscription = listObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { videoFiles ->
                            val textView = findViewById<View>(R.id.empty_view) as TextView
                            if (videoFiles == null) {
                                textView.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                            } else {
                                textView.visibility = View.GONE
                                recyclerView!!.visibility = View.VISIBLE
                                galleryAdapter!!.setVideoFilesList(videoFiles)
                            }
                            dismissLoading()
                        },
                        { error ->
                            Log.e(TAG, "Error", error)
                        })
    }
}
