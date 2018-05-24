/*
 * Created by Olha Tymoshenko on 5/24/18 8:34 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.dal

import ua.com.amicablesoft.android.wr.models.Competition
import ua.com.amicablesoft.android.wr.models.Powerlifter
import ua.com.amicablesoft.android.wr.models.User

interface IRepository {

    interface LoadUserCallback {
        fun found(userFound: Boolean?)
    }

    interface LoadPowerliftersCallback {
        fun onPowerliftersLoaded(powerlifterArrayList: List<Powerlifter?>)
        fun onDataNotAvailable()
    }

    interface LoadCompetitionsCallback {
        fun onCompetitionsLoaded(competitionArrayList: List<Competition?>)
        fun onDataNotAvailable()
    }

    fun userExist(user: User, loadUserCallback: LoadUserCallback)
    fun userSave(user: User)

    fun writeNewPowerlifter(name: String, lastName: String)
    fun getPowerlifters(loadPowerliftersCallback: LoadPowerliftersCallback)

    fun writeNewCompetition(competition: String)
    fun getCompetitions(loadCompetitionsCallback: LoadCompetitionsCallback)
}