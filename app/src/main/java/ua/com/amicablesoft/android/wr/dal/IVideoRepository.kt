/*
 * Created by Olha Tymoshenko on 5/24/18 8:35 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.dal

import ua.com.amicablesoft.android.wr.models.VideoFile

interface IVideoRepository {

    fun getListVideoFiles(specification: Specification): List<VideoFile>?
}