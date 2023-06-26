package com.mskennedy.batteryangel.activity;

import static com.mskennedy.batteryangel.activity.MainActivity.prefsEdit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.mskennedy.batteryangel.R;
import com.mskennedy.batteryangel.activity.accounts.login.LoginActivity;
import com.mskennedy.batteryangel.models.FirebaseFuncs;
import com.mskennedy.batteryangel.models.MutablePrefs;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (prefsEdit.getSharedPrefs().getBoolean("relentlessActive", false)) {
            setTheme(R.style.Theme_BatteryAngel_Relentless);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button relentlessToggle = findViewById(R.id.relentlessToggleButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button resetAppButton = findViewById(R.id.resetAppButton);
        Button resetCounterButton = findViewById(R.id.resetCountersButton);

        relentlessToggle.setOnClickListener(v -> {
            MutablePrefs mP = new MutablePrefs(getApplicationContext());
            boolean currentlyEnabled = mP.getSharedPrefs().getBoolean("relentlessActive", false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (!currentlyEnabled) {
                builder.setTitle("Enable Relentless Mode?");
                builder.setMessage("Relentless mode offers less control, and makes the app more aggressive in many ways. Are you sure you want to enable it?");
                builder.setPositiveButton("Enable", (dialog, which) -> {
                    mP.saveRelentlessToggle(true);
                    appWillRestartWarning();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                builder.setTitle("Disable Relentless Mode?");
                builder.setMessage("Disabling Relentless mode will make the app less aggressive, giving you more control, but may be less effective. Are you sure you want to disable it?");
                builder.setPositiveButton("Disable", (dialog, which) -> {
                    mP.saveRelentlessToggle(false);
                    appWillRestartWarning();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing.
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        logoutButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log Out?");
            builder.setMessage("Are you sure you want to log out?");
            builder.setPositiveButton("Confirm", ((dialog, which) -> {
                SharedPreferences sharedPreferences = getSharedPreferences("alertPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("uid_persist", null);
                editor.apply();

                FirebaseApp.getInstance().delete();

                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
            }));
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Do nothing
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        resetCounterButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reset Counters?");
            builder.setMessage("Are you sure you want to reset your counters? All of your warnings will be removed, but your data will become inaccurate until more is gathered.");
            builder.setPositiveButton("Confirm", ((dialog, which) -> {

                FirebaseFuncs.zeroOutCounter("above80", getApplicationContext());
                FirebaseFuncs.zeroOutCounter("below20", getApplicationContext());
                FirebaseFuncs.zeroOutCounter("thermalEvents", getApplicationContext());
                FirebaseFuncs.zeroOutCounter("thermalSeverity", getApplicationContext());

            }));
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Do nothing
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        resetAppButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reset App?");
            builder.setMessage("Are you sure you want to reset the whole app? You will be logged out, Relentless mode will be disabled, and alert preferences will be set to defaults.");
            builder.setPositiveButton("Confirm", ((dialog, which) -> {

                SharedPreferences prefs = getSharedPreferences("alertPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // Resets percentages
                MainActivity.performFirstTimeSetup();

                editor.putBoolean("relentlessActive", false);
                editor.putInt("relentlessSaferange", 30);
                editor.putBoolean("isLoggedIn", false);
                editor.putString("uid_persist", null);
                editor.apply();

                FirebaseApp.getInstance().delete();

                appWillRestartWarning();

            }));
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Do nothing
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    public void appWillRestartWarning(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart Required");
        builder.setMessage("The app needs to restart to apply changes. Tap 'Confirm' below to proceed.");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            finishAffinity();
            System.exit(0);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}