package com.mskennedy.batteryangel.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.mskennedy.batteryangel.R;
import com.mskennedy.batteryangel.models.FirebaseFuncs;

import java.util.ArrayList;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BatteryChannel";
    private static final String CHANNEL_NAME = "Battery Notifications";

    private final Context context;
    private final ArrayList<Integer> notificationPercents;
    private boolean shouldShowNotification;
    private boolean deviceIsCharging;

    public BatteryBroadcastReceiver(Context context, ArrayList<Integer> notificationPercents) {
        this.context = context;
        this.notificationPercents = notificationPercents;
        this.shouldShowNotification = true;
        this.deviceIsCharging = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPercent = (level / (float) scale) * 100;
            if (batteryPercent < 20) {
                FirebaseFuncs.incrementCounter("below20");
            } else if (batteryPercent > 80) {
                FirebaseFuncs.incrementCounter("above80");
            }

            for (int notificationPercent : notificationPercents) {
                if (shouldShowNotification && batteryPercent <= notificationPercent) {
                    showBatteryNotification(batteryPercent);
                    shouldShowNotification = false;
                    break; // Exit the loop after showing the first notification
                } else if (!shouldShowNotification && batteryPercent > notificationPercent) {
                    shouldShowNotification = true;
                    break; // Exit the loop after resetting the flag
                }
            }
        }else if (action != null && action.equals(Intent.ACTION_POWER_CONNECTED)) {

            shouldShowNotification = false; // If the device is charging, we need to stop sending notifications.
            deviceIsCharging = true;

        } else if (action != null && action.equals(Intent.ACTION_POWER_DISCONNECTED)) {

            shouldShowNotification = true; // If the device begins discharging, we need to start sending notifications again.
            deviceIsCharging = false;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            int thermalStatus = powerManager.getCurrentThermalStatus();
            handleThermalStatus(thermalStatus);
        }
    }

    private void showBatteryNotification(float batteryPercent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        // Build the notification
        String notificationText = "Battery is below " + batteryPercent + "%";
        Notification.Builder builder =
                new Notification.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Battery Notification")
                        .setContentText(notificationText);

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // Everything beyond this point is just for thermals.

    private void handleThermalStatus(int thermalStatus) {
        // Handle thermal status change here
        switch (thermalStatus) {
            case PowerManager.THERMAL_STATUS_NONE:
                // No thermal status reported
                Log.d("No overheat", "NONE: No thermal status to report.");
                break;
            case PowerManager.THERMAL_STATUS_LIGHT:
                // Device is lightly heated
                Log.d("No overheat", "LIGHT: Device is beginning to heat up. No hazard.");
                break;
            case PowerManager.THERMAL_STATUS_MODERATE:
                // Device is moderately heated
                Log.d("No overheat", "MODERATE: Device is heated up. No hazard.");
                break;
            case PowerManager.THERMAL_STATUS_SEVERE:
                // Device is severely heated
                Log.e("Overheat", "SEVERE: Device is overheating. May become hazardous.");
                FirebaseFuncs.incrementCounter("thermalEvents");
                break;
            case PowerManager.THERMAL_STATUS_CRITICAL:
                // Device is critically heated
                Log.e("Overheat", "CRITICAL: Device is overheating. May shut down soon. Hazardous.");
                FirebaseFuncs.incrementCounter("thermalEvents");
                break;
            case PowerManager.THERMAL_STATUS_EMERGENCY:
                // Device is now at risk of damage
                Log.e("Overheat", "EMERGENCY: Device is at risk of thermal damage. Shutdown imminent.");
                FirebaseFuncs.incrementCounter("thermalEvents");
                break;
            case PowerManager.THERMAL_STATUS_SHUTDOWN:
                // Device is forcing thermal shutdown
                Log.e("MAYDAY", "MAYDAY: Device may have suffered thermal damage. Shutting down now.");
                FirebaseFuncs.incrementCounter("thermalEvents");
                break;
            default:
                // Unknown thermal status
                break;
        }
    }
}