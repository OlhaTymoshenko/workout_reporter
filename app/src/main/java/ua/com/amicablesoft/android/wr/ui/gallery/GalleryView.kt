/*
 * Created by Olha Tymoshenko on 5/25/18 1:37 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.gallery

import ua.com.amicablesoft.android.wr.models.Powerlifter
import java.util.*

internal interface GalleryView {

    fun setListPowerlifters(list: ArrayList<Powerlifter>)
    fun setPowerlifter(position: Int)

    fun setListItems(items: ArrayList<String>)

    fun showLoading()
    fun dismissLoading()
}
