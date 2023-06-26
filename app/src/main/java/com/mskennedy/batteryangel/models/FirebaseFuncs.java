package com.mskennedy.batteryangel.models;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class FirebaseFuncs {
    private static final String USERS_NODE = "users";

    public FirebaseFuncs() {
    }

    public static void createUserFolder(Context context) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        MutablePrefs mP = new MutablePrefs(context);

        DatabaseReference userRef = databaseReference.child(USERS_NODE).child(mP.getUserId(context));
        userRef.keepSynced(true);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    DatabaseReference countersRef = userRef.child("counters");
                    DatabaseReference above80Ref = countersRef.child("above80");
                    above80Ref.setValue(0);
                    DatabaseReference below20Ref = countersRef.child("below20");
                    below20Ref.setValue(0);
                    DatabaseReference thermalEventRef = countersRef.child("thermalEvents");
                    thermalEventRef.setValue(0);
                    DatabaseReference thermalSeverityRef = countersRef.child("thermalSeverity");
                    thermalSeverityRef.setValue(0);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }


    public static void incrementCounter(String counterName, Context context) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        MutablePrefs mP = new MutablePrefs(context);

        DatabaseReference usersRef = databaseReference.child(USERS_NODE);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String currentUserKey = userSnapshot.getKey();
                    if (currentUserKey.equals(mP.getUserId(context))) {
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

    public static void zeroOutCounter(String counterName, Context context) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        MutablePrefs mP = new MutablePrefs(context);

        DatabaseReference usersRef = databaseReference.child(USERS_NODE);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String currentUserKey = userSnapshot.getKey();
                    if (currentUserKey.equals(mP.getUserId(context))) {
                        DatabaseReference currentUserRef = usersRef.child(currentUserKey);
                        DatabaseReference countersRef = currentUserRef.child("counters");
                        DatabaseReference counterRef = countersRef.child(counterName);

                        counterRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                mutableData.setValue(0);
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


    public static void getCounterValue(String counterName, CounterValueCallback callback, Context context) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        MutablePrefs mP = new MutablePrefs(context);

        DatabaseReference counterRef = databaseReference.child(USERS_NODE)
                .child(mP.getUserId(context))
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

