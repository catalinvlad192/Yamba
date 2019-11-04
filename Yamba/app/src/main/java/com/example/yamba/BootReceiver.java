package com.example.yamba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver
{
    private static final String TAG = BootReceiver.class.getSimpleName();
    private static final long DEFAULT_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "RESTART COMPLETED", Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long interval = Long.parseLong(prefs.getString("refresh", Long.toString(DEFAULT_INTERVAL)));

        PendingIntent operation = PendingIntent.getService(context, -1, new Intent(context, RefreshService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(interval == 0)
        {
            alarmManager.cancel(operation);
            Log.d(TAG, "cancelling repeat operation");
        }
        else
        {
            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, operation);
            Log.d(TAG, "setting repeat operation for: " + interval);
        }
        Log.d(TAG, "onReceived");
    }
}
