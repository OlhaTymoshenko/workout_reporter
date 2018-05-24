/*
 * Created by Olha Tymoshenko on 5/24/18 8:31 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.dal

class Specification {

    var isSquats: Boolean = false
    var isBenchPress: Boolean = false
    var isDeadLift: Boolean = false
    var powerlifterName: String? = null
    var competition: String? = null

    init {
        isSquats = true
        isBenchPress = true
        isDeadLift = true
        competition = "- None -"
    }
}