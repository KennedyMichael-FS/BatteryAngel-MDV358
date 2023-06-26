package com.mskennedy.batteryangel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mskennedy.batteryangel.R;
import com.mskennedy.batteryangel.models.FirebaseFuncs;
import com.mskennedy.batteryangel.models.MutablePrefs;
import com.mskennedy.batteryangel.receivers.BatteryService;

public class MainActivity extends AppCompatActivity {

    public static MutablePrefs prefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setting up a preference editor, as this activity will be heavy in preference changes.
        prefsEdit = new MutablePrefs(getApplicationContext());
        if (prefsEdit.getSharedPrefs().getBoolean("relentlessActive", false)) {
            setTheme(R.style.Theme_BatteryAngel_Relentless);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (prefsEdit.getSharedPrefs().getBoolean("relentlessActive", false)) {
            TextView relentlessStatus = findViewById(R.id.relentlessStatusLabel);
            relentlessStatus.setVisibility(View.VISIBLE);
        }

        // Start the BatteryService, which will begin saving logs.
        Intent intent = new Intent(this, BatteryService.class);
        startService(intent);

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
        Button settingsButton = findViewById(R.id.settingsButton);

        alertEditorButton.setOnClickListener(v -> {
            Intent toAlertEditor = new Intent(MainActivity.this, AlertEditor.class);
            startActivity(toAlertEditor);
        });

        statisticsButton.setOnClickListener(v -> {
            Intent toStatistics = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(toStatistics);
        });

        settingsButton.setOnClickListener(v -> {
            Intent toSettings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(toSettings);
        });

        // We do this to ensure that the user folder exists. If it does, this call does nothing but is rather lightweight so no performance or
        // data retention issues should stem from it.
        FirebaseFuncs.createUserFolder(getApplicationContext());
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