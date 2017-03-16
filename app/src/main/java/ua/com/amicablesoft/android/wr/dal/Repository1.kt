package ua.com.amicablesoft.android.wr.dal

import ua.com.amicablesoft.android.wr.models.User

/**
 * Created by olha on 3/16/17.
 */
class Repository1: IRepository {

    override fun userExist(user: User, loadUserCallback: IRepository.LoadUserCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun userSave(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeNewPowerlifter(name: String, lastName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPowerlifters(loadPowerliftersCallback: IRepository.LoadPowerliftersCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeNewCompetition(competition: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCompetitions(loadCompetitionsCallback: IRepository.LoadCompetitionsCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}