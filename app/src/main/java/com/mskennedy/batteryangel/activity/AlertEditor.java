package com.mskennedy.batteryangel.activity;

import static com.mskennedy.batteryangel.activity.MainActivity.performFirstTimeSetup;
import static com.mskennedy.batteryangel.activity.MainActivity.prefsEdit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mskennedy.batteryangel.R;
import com.mskennedy.batteryangel.receivers.BatteryService;

public class AlertEditor extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (prefsEdit.getSharedPrefs().getBoolean("relentlessActive", false)) {
            setTheme(R.style.Theme_BatteryAngel_Relentless);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertedit);

        if (prefsEdit.getSharedPrefs().getBoolean("relentlessActive", false)) {
            TextView relentlessActiveLabel = findViewById(R.id.relentlessStatusEditorLabel);
            relentlessActiveLabel.setVisibility(View.VISIBLE);
            TextView relentlessSaferangeLabel = findViewById(R.id.relentlessSaferangeLabel);
            relentlessSaferangeLabel.setVisibility(View.VISIBLE);
            TextView relentlessPercentLabel = findViewById(R.id.percent7);
            relentlessPercentLabel.setVisibility(View.VISIBLE);
            EditText relentlessSaferangeEdit = findViewById(R.id.relentlessSaferangeEdit);
            relentlessSaferangeEdit.setVisibility(View.VISIBLE);
            relentlessSaferangeEdit.setText(String.valueOf(prefsEdit.getSharedPrefs().getInt("relentlessSaferange", 30)));
            relentlessSaferangeEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    // Req'd method
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // This method is called when the text is changing
                    if (!relentlessSaferangeEdit.getText().toString().equals("")) {
                        String newValue = charSequence.toString();
                        prefsEdit.saveRelentlessRange(Integer.parseInt(newValue));
                        Log.d("Edited pref", String.valueOf(prefsEdit.getSharedPrefs().getInt("relentlessSaferange", 0)));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Req'd method
                }

            });
        }

        // Reiterating the service in this class to start/restart later.
        Intent intent = new Intent(this, BatteryService.class);

        // Initialize the EditText and SwitchCompat fields
        alert1percent = findViewById(R.id.alert1percentEdit);
        alert2percent = findViewById(R.id.alert2percentEdit);
        alert3percent = findViewById(R.id.alert3percentEdit);
        alert4percent = findViewById(R.id.alert4percentEdit);
        alert5percent = findViewById(R.id.alert5percentEdit);
        alert6percent = findViewById(R.id.alert6percentEdit);

        alert1enabled = findViewById(R.id.alert1switch);
        alert2enabled = findViewById(R.id.alert2switch);
        alert3enabled = findViewById(R.id.alert3switch);
        alert4enabled = findViewById(R.id.alert4switch);
        alert5enabled = findViewById(R.id.alert5switch);
        alert6enabled = findViewById(R.id.alert6switch);

        loadAlertSelections();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {saveAlertSelections();
        Log.d("Saved", "Saved prefs");
        stopService(intent);
        startService(intent);});

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.reset_alerts_toast_header);
            builder.setMessage(R.string.reset_alerts_toast_body);

            // Add the "Cancel" button
            builder.setNegativeButton(R.string.keyword_cancel, (dialog, which) -> {
                // Dismiss the dialog
                dialog.dismiss();
            });

            // Add the "Reset" button
            builder.setPositiveButton(R.string.keyword_reset, (dialog, which) -> {
                // Execute the function
                performFirstTimeSetup();
                stopService(intent);
                startService(intent);

                // Dismiss the dialog
                dialog.dismiss();
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    private void saveAlertSelections() {
        prefsEdit.saveAlert1(Integer.parseInt(String.valueOf(alert1percent.getText())), alert1enabled.isChecked());
        prefsEdit.saveAlert2(Integer.parseInt(String.valueOf(alert2percent.getText())), alert2enabled.isChecked());
        prefsEdit.saveAlert3(Integer.parseInt(String.valueOf(alert3percent.getText())), alert3enabled.isChecked());
        prefsEdit.saveAlert4(Integer.parseInt(String.valueOf(alert4percent.getText())), alert4enabled.isChecked());
        prefsEdit.saveAlert5(Integer.parseInt(String.valueOf(alert5percent.getText())), alert5enabled.isChecked());
        prefsEdit.saveAlert6(Integer.parseInt(String.valueOf(alert6percent.getText())), alert6enabled.isChecked());
    }

    private void loadAlertSelections() {
        alert1percent.setText(String.valueOf(prefsEdit.loadAlertPercent(1)));
        alert1enabled.setChecked(prefsEdit.loadAlertActive(1));
        alert2percent.setText(String.valueOf(prefsEdit.loadAlertPercent(2)));
        alert2enabled.setChecked(prefsEdit.loadAlertActive(2));
        alert3percent.setText(String.valueOf(prefsEdit.loadAlertPercent(3)));
        alert3enabled.setChecked(prefsEdit.loadAlertActive(3));
        alert4percent.setText(String.valueOf(prefsEdit.loadAlertPercent(4)));
        alert4enabled.setChecked(prefsEdit.loadAlertActive(4));
        alert5percent.setText(String.valueOf(prefsEdit.loadAlertPercent(5)));
        alert5enabled.setChecked(prefsEdit.loadAlertActive(5));
        alert6percent.setText(String.valueOf(prefsEdit.loadAlertPercent(6)));
        alert6enabled.setChecked(prefsEdit.loadAlertActive(6));
    }

    // Fields used to edit and access percentages.
    private EditText alert1percent;
    private EditText alert2percent;
    private EditText alert3percent;
    private EditText alert4percent;
    private EditText alert5percent;
    private EditText alert6percent;

    // Fields used to edit and access alert switches.
    private SwitchCompat alert1enabled;
    private SwitchCompat alert2enabled;
    private SwitchCompat alert3enabled;
    private SwitchCompat alert4enabled;
    private SwitchCompat alert5enabled;
    private SwitchCompat alert6enabled;
}