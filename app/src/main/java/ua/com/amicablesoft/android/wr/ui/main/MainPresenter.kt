/*
 * Created by Olha Tymoshenko on 5/25/18 1:43 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.main

import android.os.Environment
import com.google.firebase.auth.FirebaseAuth
import ua.com.amicablesoft.android.wr.R
import ua.com.amicablesoft.android.wr.dal.IRepository
import ua.com.amicablesoft.android.wr.dal.Repository
import ua.com.amicablesoft.android.wr.models.Competition
import ua.com.amicablesoft.android.wr.models.Exercise
import ua.com.amicablesoft.android.wr.models.Powerlifter
import ua.com.amicablesoft.android.wr.models.User
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

internal class MainPresenter(private val view: MainView) {
    private var currentPowerlifter: Powerlifter? = null
    private var currentCompetition: Competition? = null
    private var currentExercise: Exercise? = null
    private var repository: Repository? = null
    private var currentVideoPath: File? = null

    private val powerlifterName: String
        get() {
            val name = currentPowerlifter!!.name
            val lastName = currentPowerlifter!!.lastName
            return lastName!!.substring(0, 1) + name!!.substring(0, 1)
        }

    private val numberOfFiles: Int
        get() {
            var count = 0
            if (currentVideoPath != null) {
                count = currentVideoPath!!.listFiles().size
            }
            return count
        }

    private val setNumber: String
        get() {
            val count = numberOfFiles
            val number = count + 1
            return number.toString()
        }

    fun onStart() {
        view.showLoading()
        onAuthInSuccess()
        repository = Repository()
        repository!!.getPowerlifters(object : IRepository.LoadPowerliftersCallback {
            override fun onPowerliftersLoaded(powerlifterArrayList: List<Powerlifter?>) {
                view.setListPowerlifters(powerlifterArrayList as ArrayList<Powerlifter>)
                if (powerlifterArrayList.isEmpty()) {
                    view.openAddPowerlifterView()
                }
                view.dismissLoading()
            }

            override fun onDataNotAvailable() {
                view.dismissLoading()
            }
        })
        getCompetitionsFromRepository()
        view.setPowerlifter(0)
        view.setExercise(1)
        currentExercise = Exercise.BENCHPRESS
    }

    fun onPowerlifterAdded() {
        repository!!.getPowerlifters(object : IRepository.LoadPowerliftersCallback {
            override fun onPowerliftersLoaded(powerlifterArrayList: List<Powerlifter?>) {
                view.setListPowerlifters(powerlifterArrayList as ArrayList<Powerlifter>)
            }

            override fun onDataNotAvailable() {

            }
        })
        view.setPowerlifter(0)
    }

    fun onCompetitionAdded() {
        getCompetitionsFromRepository()
    }

    private fun getCompetitionsFromRepository() {
        repository!!.getCompetitions(object : IRepository.LoadCompetitionsCallback {
            override fun onCompetitionsLoaded(competitionArrayList: List<Competition?>) {
                view.setListCompetitions(competitionArrayList as ArrayList<Competition>)
            }

            override fun onDataNotAvailable() {

            }
        })
        view.setCompetition(0)
    }

    fun changePowerlifter(powerlifter: Powerlifter) {
        currentPowerlifter = powerlifter
    }

    fun changeCompetition(competition: Competition) {
        currentCompetition = competition
    }

    fun changeExercise(exercise: Int?) {
        if (exercise == 0) {
            currentExercise = Exercise.SQUATS
        } else if (exercise == 1) {
            currentExercise = Exercise.BENCHPRESS
        } else if (exercise == 2) {
            currentExercise = Exercise.DEADLIFT
        }
    }

    @Throws(IOException::class)
    private fun createVideoFileName(): String {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val name = powerlifterName
        val exercise = currentExercise!!.toString()
        val setNumber = setNumber
        return "$name-$exercise-$date-$setNumber.mp4"
    }

    private fun createDirName(): String? {
        if (currentPowerlifter != null) {
            val competition = currentCompetition!!.competition
            val powerlifterName = currentPowerlifter!!.lastName + "-" + currentPowerlifter!!.name
            val exercise = currentExercise!!.toString()
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            return if (!competition!!.contentEquals("- None -")) {
                "/$competition/$powerlifterName/$exercise/$date/"
            } else {
                "/$powerlifterName/$exercise/$date/"
            }
        } else {
            view.showSnackbar(R.string.snackbar_text_powerlifter)
            return null
        }
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        val dirName = createDirName()
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val videoPath = File(filePath, dirName!!)
        videoPath.mkdirs()
        currentVideoPath = videoPath
        val fileName = createVideoFileName()
        return File(videoPath, fileName)
    }

    private fun onAuthInSuccess() {
        val repository = Repository()
        val auth = FirebaseAuth.getInstance()
        val user = User(auth.currentUser!!.uid, auth.currentUser!!.email!!)
        repository.userExist(user, object : IRepository.LoadUserCallback {
            override fun found(userFound: Boolean?) {
                if ((!userFound!!)) {
                    repository.userSave(user)
                }
            }
        })
    }

    fun onNewVideoAction() {
        view.requestPermissions()
    }

    fun onSnackbarVideoAction() {
        view.showSnackbar(R.string.snackbar_text_video)
    }

    fun onSnackbarPermissions() {
        view.showSnackbar(R.string.snackbar_text_permissions)
    }

    @Throws(IOException::class)
    fun createVideo() {
        val videoFile = createFile()
        view.recordVideo(videoFile)
    }
}
