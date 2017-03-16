package ua.com.amicablesoft.android.wr.dal

import ua.com.amicablesoft.android.wr.models.VideoFile

/**
 * Created by olha on 3/16/17.
 */
interface IVideoRepository {

    fun getListVideoFiles(specification: Specification): List<VideoFile>
}