/*
 * Created by Olha Tymoshenko on 5/24/18 8:35 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.dal

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ua.com.amicablesoft.android.wr.dto.UserDto
import ua.com.amicablesoft.android.wr.models.Competition
import ua.com.amicablesoft.android.wr.models.Powerlifter
import ua.com.amicablesoft.android.wr.models.User

class Repository : IRepository {

    override fun userExist(user: User, loadUserCallback: IRepository.LoadUserCallback) {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference
        firebaseDatabase.child("users").child(user.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //TODO error
                loadUserCallback.found(false)
            }

            override fun onDataChange(p0: DataSnapshot) {
                loadUserCallback.found(p0.exists())
            }
        })
    }

    override fun userSave(user: User) {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference
        firebaseDatabase.child("users").child(user.id).setValue(UserDto(user.email))
    }

    override fun writeNewPowerlifter(name: String, lastName: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val key = databaseReference.child("users/$userId/powerlifters").push().key
        val powerlifter = Powerlifter(name, lastName)
        val childUpdates = mapOf(Pair("users/$userId/powerlifters/$key", powerlifter))
        databaseReference.updateChildren(childUpdates)
    }

    override fun getPowerlifters(loadPowerliftersCallback: IRepository.LoadPowerliftersCallback) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference.child("users/$userId/powerlifters").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.w("getLifters:onCancelled", p0.toException())
                loadPowerliftersCallback.onDataNotAvailable()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val powerlifters = p0.children.map { s -> s.getValue(Powerlifter::class.java) }
                loadPowerliftersCallback.onPowerliftersLoaded(powerlifters)
            }
        })
    }

    override fun writeNewCompetition(competition: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val key = databaseReference.child("users/$userId/competitions").push().key
        val competitionObject = Competition(competition)
        val childUpdates = mapOf(Pair("users/$userId/competitions/$key", competitionObject))
        databaseReference.updateChildren(childUpdates)
    }

    override fun getCompetitions(loadCompetitionsCallback: IRepository.LoadCompetitionsCallback) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference.child("users/$userId/competitions").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.w("getCompets:onCancelled", p0.toException())
                loadCompetitionsCallback.onDataNotAvailable()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val competitions = dataSnapshot.children.map { child ->
                    child.getValue(Competition::class.java)
                }

                val noneCompetition = Competition("- None -")

                val allCompetitions = listOf(noneCompetition).plus(competitions)
                loadCompetitionsCallback.onCompetitionsLoaded(allCompetitions)
            }
        })
    }
}