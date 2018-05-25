/*
 * Created by Olha Tymoshenko on 5/25/18 3:40 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui

import ua.com.amicablesoft.android.wr.dal.Repository

class AddCompetitionPresenter {

    fun isCompetitionValid(competition: String): Boolean {
        return competition.length > 1
    }

    fun callWriteNewCompetition(competition: String) {
        val repository = Repository()
        repository.writeNewCompetition(competition)

    }
}