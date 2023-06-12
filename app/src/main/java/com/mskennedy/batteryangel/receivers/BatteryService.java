package com.mskennedy.batteryangel.receivers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.mskennedy.batteryangel.models.MutablePrefs;

public class BatteryService extends Service {

    private BatteryBroadcastReceiver batteryReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        MutablePrefs prefEdit = new MutablePrefs(getApplicationContext());
        batteryReceiver = new BatteryBroadcastReceiver(getApplicationContext(), prefEdit.loadAllActivePercentInts());
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, batteryFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
