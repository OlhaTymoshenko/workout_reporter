package ua.com.amicablesoft.android.wr;

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

import ua.com.amicablesoft.android.wr.models.Powerlifter;
import ua.com.amicablesoft.android.wr.models.User;

/**
 * Created by lapa on 30.09.16.
 */

public class Repository implements IRepository {

    @Override
    public void writeNewPowerlifter(String name, String lastName) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        String key = firebaseDatabase.child("users/" + userId + "/powerlifters").push().getKey();
        Powerlifter powerlifter = new Powerlifter();
        powerlifter.setName(name);
        powerlifter.setLastName(lastName);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + userId + "/powerlifters/" + key, powerlifter);
        firebaseDatabase.updateChildren(childUpdates);

    }

    @Override
    public void writeNewUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        String userEmail = firebaseAuth.getCurrentUser().getEmail();
        assert userEmail != null;
        User user = new User(userEmail);
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void getPowerlifters(final LoadPowerliftersCallback loadPowerliftersCallback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
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
                        Log.w("geLifters:onCancelled", databaseError.toException());
                        loadPowerliftersCallback.onDataNotAvailable();
                    }
                }
        );
    }

    @Override
    public ArrayList<Powerlifter> readPowerlifters() {
        final ArrayList<Powerlifter> powerlifters = new ArrayList<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase.child("users/" + userId + "/powerlifters").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Powerlifter powerlifter = snapshot.getValue(Powerlifter.class);
                            powerlifters.add(powerlifter);
                        }
            }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("geLifters:onCancelled", databaseError.toException());
                    }
                }
        );
        return powerlifters;

    }
}
