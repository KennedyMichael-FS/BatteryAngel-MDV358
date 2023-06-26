package com.mskennedy.batteryangel.activity;

import static com.mskennedy.batteryangel.activity.MainActivity.prefsEdit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.mskennedy.batteryangel.R;
import com.mskennedy.batteryangel.models.FirebaseFuncs;

public class StatisticsActivity extends AppCompatActivity {

    private TextView below20body;
    private TextView above80body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (prefsEdit.getSharedPrefs().getBoolean("relentlessActive", false)) {
            setTheme(R.style.Theme_BatteryAngel_Relentless);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        below20body = findViewById(R.id.below20body);
        above80body = findViewById(R.id.above80body);

        fetchBelow20Count();
        fetchAbove80Count();
    }

    private void fetchBelow20Count() {
        FirebaseFuncs.getCounterValue("below20", new FirebaseFuncs.CounterValueCallback() {
            @Override
            public void onCounterValue(int value) {
                setBelow20Text(value);
            }

            @Override
            public void onError(DatabaseError error) {
                setBelow20Text(0);
            }
        }, getApplicationContext());
    }

    private void fetchAbove80Count() {
        FirebaseFuncs.getCounterValue("above80", new FirebaseFuncs.CounterValueCallback() {
            @Override
            public void onCounterValue(int value) {
                setAbove80Text(value);
            }

            @Override
            public void onError(DatabaseError error) {
                setAbove80Text(0);
            }
        }, getApplicationContext());
    }

    private void fetchThermalEventCount() {
        FirebaseFuncs.getCounterValue("thermalEvents", new FirebaseFuncs.CounterValueCallback() {
            @Override
            public void onCounterValue(int value) {
                //setThermalEventText(value);
            }

            @Override
            public void onError(DatabaseError error) {
                //setThermalEventText(0);
            }
        }, getApplicationContext());
    }

    @SuppressLint("StringFormatMatches")
    private void setBelow20Text(int count) {
        switch (count) {
            case 0:
                below20body.setText(getString(R.string.below20none) + " " + getString(R.string.below20positiveSubnote));
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                below20body.setText(getString(R.string.below20low) + " " + getString(R.string.below20positiveSubnote));



                break;
            default:
                below20body.setText(getString(R.string.below20high) + " " + getString(R.string.below20negativeSubnote));
                break;
        }
    }

    private void setAbove80Text(int count) {
        switch (count) {
            case 0:
                above80body.setText(getString(R.string.above80none) + " " + getString(R.string.above80positiveSubnote));
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                above80body.setText(getString(R.string.above80low) + " " + getString(R.string.above80positiveSubnote));
                break;
            default:
                above80body.setText(getString(R.string.above80high) + " " + getString(R.string.above80negativeSubnote));
                break;
        }
    }
}
