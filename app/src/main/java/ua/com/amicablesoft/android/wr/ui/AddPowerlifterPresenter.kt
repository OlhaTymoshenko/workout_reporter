/*
 * Created by Olha Tymoshenko on 5/25/18 3:43 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui

import ua.com.amicablesoft.android.wr.dal.Repository

class AddPowerlifterPresenter {

    fun isLastNameValid(lastName: String): Boolean {
        return lastName.length > 1
    }

    fun isNameValid(name: String): Boolean {
        return name.length > 1
    }

    fun callWriteNewPowerlifter(name: String, lastName: String) {
        val repository = Repository()
        repository.writeNewPowerlifter(name, lastName)

    }
}