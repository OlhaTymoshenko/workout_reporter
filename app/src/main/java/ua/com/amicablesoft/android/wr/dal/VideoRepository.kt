/*
 * Created by Olha Tymoshenko on 5/24/18 8:32 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.dal

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import ua.com.amicablesoft.android.wr.models.VideoFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class VideoRepository(private val context: Context) : IVideoRepository {

    companion object {
        private val TAG = VideoRepository::class.java.simpleName
    }

    override fun getListVideoFiles(specification: Specification): List<VideoFile>? {
        val videoFiles = ArrayList<VideoFile>()
        val parentDir: File
        val competition = specification.competition
        if (competition == "- None -") {
            parentDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString()
                    + "/" + specification.powerlifterName + "/")
        } else {
            parentDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString()
                    + "/" + competition + "/" + specification.powerlifterName + "/")
        }
        val videos = getListFiles(parentDir)
        if (videos != null) {
            val filteredVideos = filterListVideo(videos, specification)
            if (filteredVideos != null) {
                for (video in filteredVideos) {
                    val fileName = video.name
                    val thumbName = fileName.substring(0, fileName.length - 4)
                    val path = context.externalCacheDir!!.toString()
                    var thumb = File(path, "$thumbName.png")
                    if (thumb.exists()) {
                        val videoFile = VideoFile(video.path, thumb, thumbName)
                        videoFiles.add(videoFile)
                    } else {
                        try {
                            thumb.createNewFile()
                        } catch (e: IOException) {
                            Log.e(TAG, "Error", e)
                        }

                        thumb = createThumbnailFromFile(video, thumb)
                        val videoFile = VideoFile(video.path, thumb, thumbName)
                        videoFiles.add(videoFile)
                    }
                }
            } else {
                return null
            }
            return videoFiles
        } else {
            return null
        }
    }

    private fun getListFiles(parentDir: File): MutableList<File>? {
        val listFiles = ArrayList<File>()
        val files = parentDir.listFiles()
        return if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    listFiles.addAll(getListFiles(file)!!)
                } else
                    listFiles.add(file)
            }
            listFiles
        } else {
            null
        }
    }

    private fun createThumbnailFromFile(file: File, thumb: File): File {
        val filePath = file.path
        val bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
        return saveBitmapToFile(bitmap, thumb)
    }

    private fun saveBitmapToFile(bitmap: Bitmap, thumb: File): File {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(thumb)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return thumb
    }

    private fun filterListVideo(videos: MutableList<File>, specification: Specification): List<File>? {
        var filteredVideos: MutableList<File>? = ArrayList()
        val squats = specification.isSquats
        val benchPress = specification.isBenchPress
        val deadLift = specification.isDeadLift
        if (squats and benchPress and deadLift) {
            filteredVideos = videos
        } else if (!squats and benchPress and deadLift) {
            for (file in videos) {
                val name = file.name
                if (name.contains("BENCHPRESS") || name.contains("DEADLIFT")) {
                    filteredVideos!!.add(file)
                }
            }
        } else if (squats and !benchPress and deadLift) {
            for (file in videos) {
                val name = file.name
                if (name.contains("SQUATS") || name.contains("DEADLIFT")) {
                    filteredVideos!!.add(file)
                }
            }
        } else if (squats and benchPress and !deadLift) {
            for (file in videos) {
                val name = file.name
                if (name.contains("SQUATS") || name.contains("BENCHPRESS")) {
                    filteredVideos!!.add(file)
                }
            }
        } else if (!squats and !benchPress and deadLift) {
            for (file in videos) {
                val name = file.name
                if (name.contains("DEADLIFT")) {
                    filteredVideos!!.add(file)
                }
            }
        } else if (squats and !benchPress and !deadLift) {
            for (file in videos) {
                val name = file.name
                if (name.contains("SQUATS")) {
                    filteredVideos!!.add(file)
                }
            }
        } else if (!squats and benchPress and !deadLift) {
            for (file in videos) {
                val name = file.name
                if (name.contains("BENCHPRESS")) {
                    filteredVideos!!.add(file)
                }
            }
        } else if (!squats and !benchPress and !deadLift) {
            filteredVideos = null
        }
        return filteredVideos
    }
}
