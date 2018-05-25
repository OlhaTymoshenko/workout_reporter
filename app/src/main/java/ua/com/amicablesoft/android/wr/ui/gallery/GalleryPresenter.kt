/*
 * Created by Olha Tymoshenko on 5/25/18 1:19 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.gallery

import android.content.Context
import ua.com.amicablesoft.android.wr.R
import ua.com.amicablesoft.android.wr.dal.IRepository
import ua.com.amicablesoft.android.wr.dal.Repository
import ua.com.amicablesoft.android.wr.dal.Specification
import ua.com.amicablesoft.android.wr.dal.VideoRepository
import ua.com.amicablesoft.android.wr.models.Competition
import ua.com.amicablesoft.android.wr.models.Powerlifter
import ua.com.amicablesoft.android.wr.models.VideoFile
import java.util.*

internal class GalleryPresenter(private val view: GalleryView, private val context: Context) {
    private var repository: Repository? = null
    private var currentPowerlifter: Powerlifter? = null

    private val currentPowerlifterName: String
        get() = currentPowerlifter!!.lastName + "-" + currentPowerlifter!!.name

    fun setPowerlifters() {
        repository!!.getPowerlifters(object : IRepository.LoadPowerliftersCallback {
            override fun onPowerliftersLoaded(powerlifterArrayList: List<Powerlifter?>) {

            }
            override fun onDataNotAvailable() {}
        })
        view.setPowerlifter(0)
    }

    fun getCompetitions(context: Context) {
        repository = Repository()
        repository!!.getCompetitions(object : IRepository.LoadCompetitionsCallback {
            override fun onCompetitionsLoaded(competitionArrayList: List<Competition?>) {
                val listItems = ArrayList<String>()
                listItems.add(context.getString(R.string.title_exercise))
                listItems.add(context.getString(R.string.title_squats))
                listItems.add(context.getString(R.string.title_bench_press))
                listItems.add(context.getString(R.string.title_dead_lift))
                listItems.add(context.getString(R.string.title_competition))
                competitionArrayList.forEach { competition -> listItems.add(competition.toString()) }
                listItems.add(context.getString(R.string.button_show))
                view.setListItems(listItems)
            }
            override fun onDataNotAvailable() {

            }
        })
    }

    fun setCurrentPowerlifter(powerlifter: Powerlifter) {
        currentPowerlifter = powerlifter
    }

    fun getVideoFiles(specification: Specification): List<VideoFile>? {
        val videoRepository = VideoRepository(context)
        specification.powerlifterName = currentPowerlifterName
        val videoFiles = videoRepository.getListVideoFiles(specification)
        return if (videoFiles == null || videoFiles.isEmpty()) {
            null
        } else {
            videoFiles
        }
    }

}
