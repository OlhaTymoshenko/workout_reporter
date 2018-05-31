/*
 * Created by Olha Tymoshenko on 5/25/18 1:48 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.main

import ua.com.amicablesoft.android.wr.models.Competition
import ua.com.amicablesoft.android.wr.models.Powerlifter
import java.io.File
import java.io.IOException
import java.util.*

internal interface MainView {
    fun requestPermissions()
    @Throws(IOException::class)
    fun recordVideo(videoFile: File)

    fun setListPowerlifters(list: ArrayList<Powerlifter>)
    fun setListCompetitions(listCompetitions: ArrayList<Competition>)

    fun setPowerlifter(position: Int)
    fun setCompetition(position: Int)
    fun setExercise(exercise: Int?)

    fun showSnackbar(message: Int)
    fun showLoading()
    fun dismissLoading()

    fun openAddPowerlifterView()
    fun openAddCompetitionView()
    fun openVideoGallery()
}