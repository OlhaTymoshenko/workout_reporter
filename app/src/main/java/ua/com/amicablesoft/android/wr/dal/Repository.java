package ua.com.amicablesoft.android.wr.dal;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.com.amicablesoft.android.wr.dto.UserDto;
import ua.com.amicablesoft.android.wr.models.Competition;
import ua.com.amicablesoft.android.wr.models.Powerlifter;
import ua.com.amicablesoft.android.wr.models.User;

/**
 * Created by lapa on 30.09.16.
 */

public class Repository implements IRepository {

    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    public void userExist(final User user, final LoadUserCallback loadUserCallback) {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase.child("users").child(user.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        loadUserCallback.found(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO error
                        loadUserCallback.found(false);
                    }
                }
        );
    }

    @Override
    public void userSave(User user) {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase.child("users").child(user.getId()).setValue(new UserDto(user.getEmail()));
    }

    @Override
    public void writeNewPowerlifter(String name, String lastName) {
        DatabaseReference firebaseDatabase = getDatabaseReference();
        String userId = getUserId();
        String key = firebaseDatabase.child("users/" + userId + "/powerlifters").push().getKey();
        Powerlifter powerlifter = new Powerlifter();
        powerlifter.setName(name);
        powerlifter.setLastName(lastName);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + userId + "/powerlifters/" + key, powerlifter);
        firebaseDatabase.updateChildren(childUpdates);

    }

    @Override
    public void getPowerlifters(final LoadPowerliftersCallback loadPowerliftersCallback) {
        DatabaseReference firebaseDatabase = getDatabaseReference();
        String userId = getUserId();
        firebaseDatabase.child("users/" + userId + "/powerlifters").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Powerlifter> powerlifters = new ArrayList<>();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Powerlifter powerlifter = snapshot.getValue(Powerlifter.class);
                            powerlifters.add(powerlifter);
                        }
                        loadPowerliftersCallback.onPowerliftersLoaded(powerlifters);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("getLifters:onCancelled", databaseError.toException());
                        loadPowerliftersCallback.onDataNotAvailable();
                    }
                }
        );
    }

    @Override
    public void writeNewCompetition(String nameOfCompetition) {
        DatabaseReference firebaseDatabase = getDatabaseReference();
        String userId = getUserId();
        String key = firebaseDatabase.child("users/" + userId + "/competitions").push().getKey();
        Competition competition = new Competition();
        competition.setCompetition(nameOfCompetition);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + userId + "/competitions/" + key, competition);
        firebaseDatabase.updateChildren(childUpdates);
    }

    @Override
    public void getCompetitions(final LoadCompetitionsCallback loadCompetitionsCallback) {
        DatabaseReference firebaseDatabase = getDatabaseReference();
        String userId = getUserId();
        firebaseDatabase.child("users/" + userId + "/competitions").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Competition> competitions = new ArrayList<>();
                        Competition c = new Competition();
                        c.setCompetition("- None -");
                        competitions.add(c);
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Competition competition = snapshot.getValue(Competition.class);
                            competitions.add(competition);
                        }
                        loadCompetitionsCallback.onCompetitionsLoaded(competitions);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("getCompets:onCancelled", databaseError.toException());
                        loadCompetitionsCallback.onDataNotAvailable();
                    }
                }
        );
    }

    private DatabaseReference getDatabaseReference() {
        firebaseAuth = FirebaseAuth.getInstance();
        return FirebaseDatabase.getInstance().getReference();
    }

    private String getUserId() {
        return firebaseAuth.getCurrentUser().getUid();
    }
}
