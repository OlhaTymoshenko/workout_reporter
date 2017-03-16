package ua.com.amicablesoft.android.wr.dal

import ua.com.amicablesoft.android.wr.models.Competition
import ua.com.amicablesoft.android.wr.models.Powerlifter
import ua.com.amicablesoft.android.wr.models.User
import java.util.*

/**
 * Created by olha on 3/16/17.
 */
interface IRepository {

    interface LoadUserCallback {
        fun found(userFound: Boolean)
    }

    interface LoadPowerliftersCallback {
        fun onPowerliftersLoaded(powerlifterArrayList: ArrayList<Powerlifter>)
        fun onDataNotAvailable()
    }

    interface LoadCompetitionsCallback {
        fun onCompetitionsLoaded(competitionArrayList: ArrayList<Competition>)
        fun onDataNotAvailable()
    }

    fun userExist(user: User, loadUserCallback: LoadUserCallback)
    fun userSave(user: User)

    fun writeNewPowerlifter(name: String, lastName: String)
    fun getPowerlifters(loadPowerliftersCallback: LoadPowerliftersCallback)

    fun writeNewCompetition(competition: String)
    fun getCompetitions(loadCompetitionsCallback: LoadCompetitionsCallback)
}