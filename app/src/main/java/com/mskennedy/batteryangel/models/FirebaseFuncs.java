package com.mskennedy.batteryangel.models;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FirebaseFuncs {
    private static final String USERS_NODE = "users";

    public FirebaseFuncs() {
    }

    // TODO: Handle null object possibilities, such as if user bypasses login somehow.
    public static String getUserId(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }

    public static void incrementCounter(String counterName) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        DatabaseReference usersRef = databaseReference.child(USERS_NODE);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String currentUserKey = userSnapshot.getKey();
                    if (currentUserKey.equals(getUserId())) {
                        DatabaseReference currentUserRef = usersRef.child(currentUserKey);
                        DatabaseReference countersRef = currentUserRef.child("counters");
                        DatabaseReference counterRef = countersRef.child(counterName);

                        counterRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Integer counterValue = mutableData.getValue(Integer.class);
                                if (counterValue == null) {
                                    counterValue = 0;
                                }
                                counterValue++;

                                mutableData.setValue(counterValue);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean committed,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    public static void getCounterValue(String counterName, CounterValueCallback callback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        DatabaseReference counterRef = databaseReference.child(USERS_NODE)
                .child(getUserId())
                .child("counters")
                .child(counterName);

        counterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer value = dataSnapshot.getValue(Integer.class);
                    if (value != null) {
                        callback.onCounterValue(value);
                        return;
                    }
                }
                callback.onCounterValue(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError);
            }
        });
    }
    public interface CounterValueCallback {
        void onCounterValue(int value);
        void onError(DatabaseError error);
    }
}

