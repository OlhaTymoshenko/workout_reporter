package ua.com.amicablesoft.android.wr.dal;

import java.util.List;

import ua.com.amicablesoft.android.wr.models.VideoFile;

/**
 * Created by olha on 2/27/17.
 */

interface IVideoRepository {

    List<VideoFile> getListVideoFiles(String powerlifter);
}
