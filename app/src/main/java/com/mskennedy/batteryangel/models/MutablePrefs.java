package com.mskennedy.batteryangel.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class MutablePrefs {

    public static final String PREF_FIRST_LAUNCH = "first_launch";
    private final Context context;

    public MutablePrefs(Context context) {
        this.context = context;
    }

    public SharedPreferences.Editor getSharedPrefsEditor() {
        SharedPreferences settings = context.getSharedPreferences("alertPrefs", Context.MODE_PRIVATE);
        return settings.edit();
    }

    public SharedPreferences getSharedPrefs() {
        return context.getSharedPreferences("alertPrefs", Context.MODE_PRIVATE);
    }

    // Get UID

    public String getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("alertPrefs", Context.MODE_PRIVATE);
        return preferences.getString("uid_persist", null);
    }



    // For saving data pertaining to alerts.

    public void saveAlert1(int percentage, boolean active) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putInt("alert1Percent", percentage);
        editor.putBoolean("alert1Active", active);
        editor.apply();
    }
    public void saveAlert2(int percentage, boolean active) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putInt("alert2Percent", percentage);
        editor.putBoolean("alert2Active", active);
        editor.apply();
    }
    public void saveAlert3(int percentage, boolean active) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putInt("alert3Percent", percentage);
        editor.putBoolean("alert3Active", active);
        editor.apply();
    }
    public void saveAlert4(int percentage, boolean active) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putInt("alert4Percent", percentage);
        editor.putBoolean("alert4Active", active);
        editor.apply();
    }
    public void saveAlert5(int percentage, boolean active) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putInt("alert5Percent", percentage);
        editor.putBoolean("alert5Active", active);
        editor.apply();
    }
    public void saveAlert6(int percentage, boolean active) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putInt("alert6Percent", percentage);
        editor.putBoolean("alert6Active", active);
        editor.apply();
    }

    public void saveRelentlessRange(int percentage) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putInt("relentlessSaferange", percentage);
        editor.apply();
    }

    public void saveRelentlessToggle(boolean active) {
        SharedPreferences.Editor editor = getSharedPrefsEditor();
        editor.putBoolean("relentlessActive", active);
        editor.apply();
    }

    // For loading data pertaining to alerts.

    public int loadAlertPercent(int alertNum) {
        SharedPreferences sP = getSharedPrefs();
        switch (alertNum) {
            case 1:
                return sP.getInt("alert1Percent", 20);
            case 2:
                return sP.getInt("alert2Percent", 20);
            case 3:
                return sP.getInt("alert3Percent", 20);
            case 4:
                return sP.getInt("alert4Percent", 20);
            case 5:
                return sP.getInt("alert5Percent", 20);
            case 6:
                return sP.getInt("alert6Percent", 20);
            default:
                return 0;
        }
    }

    public boolean loadAlertActive(int alertNum) {
        SharedPreferences sP = getSharedPrefs();
        switch (alertNum) {
            case 1:
                return sP.getBoolean("alert1Active", false);
            case 2:
                return sP.getBoolean("alert2Active", false);
            case 3:
                return sP.getBoolean("alert3Active", false);
            case 4:
                return sP.getBoolean("alert4Active", false);
            case 5:
                return sP.getBoolean("alert5Active", false);
            case 6:
                return sP.getBoolean("alert6Active", false);
            default:
                return false;
        }
    }

    public ArrayList<Integer> loadAllActivePercentInts() {
        SharedPreferences sP = getSharedPrefs();

        ArrayList<Integer> enabledInts = new ArrayList<>();

        if (sP.getBoolean("alert1Active", false)) {
            enabledInts.add(sP.getInt("alert1Percent", 0));
        }
        if (sP.getBoolean("alert2Active", false)) {
            enabledInts.add(sP.getInt("alert2Percent", 0));
        }
        if (sP.getBoolean("alert3Active", false)) {
            enabledInts.add(sP.getInt("alert3Percent", 0));
        }
        if (sP.getBoolean("alert4Active", false)) {
            enabledInts.add(sP.getInt("alert4Percent", 0));
        }
        if (sP.getBoolean("alert5Active", false)) {
            enabledInts.add(sP.getInt("alert5Percent", 0));
        }
        if (sP.getBoolean("alert6Active", false)) {
            enabledInts.add(sP.getInt("alert6Percent", 0));
        }

        return enabledInts;
    }

}
