package com.mskennedy.batteryangel.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mskennedy.batteryangel.R;
import com.mskennedy.batteryangel.models.MutablePrefs;
import com.mskennedy.batteryangel.receivers.BatteryService;

public class MainActivity extends AppCompatActivity {

    public static MutablePrefs prefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Start the BatteryService, which will begin saving logs.
        Intent intent = new Intent(this, BatteryService.class);
        startService(intent);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        // Setting up a preference editor, as this activity will be heavy in preference changes.
        prefsEdit = new MutablePrefs(getApplicationContext());

        // Check if it's the first-time launch
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = prefs.getBoolean(MutablePrefs.PREF_FIRST_LAUNCH, true);

        if (isFirstTime) {
            // Execute first-time code or actions
            performFirstTimeSetup();

            // Update the first-time launch flag
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(MutablePrefs.PREF_FIRST_LAUNCH, false);
            editor.apply();
        }

        Button alertEditorButton = findViewById(R.id.alertEditorButton);
        Button statisticsButton = findViewById(R.id.statisticsButton);

        alertEditorButton.setOnClickListener(v -> {
            Intent toAlertEditor = new Intent(MainActivity.this, AlertEditor.class);
            startActivity(toAlertEditor);
        });

        statisticsButton.setOnClickListener(v -> {
            Intent toStatistics = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(toStatistics);
        });
    }

    public static void performFirstTimeSetup() {
        Log.d("FTS", "Performing first time setup");
        prefsEdit.saveAlert1(20, true);
        prefsEdit.saveAlert2(30, false);
        prefsEdit.saveAlert3(40, false);
        prefsEdit.saveAlert4(50, false);
        prefsEdit.saveAlert5(60, false);
        prefsEdit.saveAlert6(70, false);
    }
}