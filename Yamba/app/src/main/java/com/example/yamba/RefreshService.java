package com.example.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import org.w3c.dom.Text;

import java.util.List;

public class RefreshService extends IntentService
{
    static final String TAG = "RefreshService";

    public RefreshService()
    {
        super(TAG);
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    // Executes on a separate thread (not UI thread)
    @Override
    protected void onHandleIntent(Intent intent)
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        final String username = prefs.getString("username", "");
        final String password = prefs.getString("password", "");

        // Check that username and password are not empty
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please update your username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "onStarted");

        ContentValues values = new ContentValues();

        YambaClient cloud = new YambaClient(username, password, "http://yamba.newcircle.com/api");
        try{
            int count = 0;
            List<YambaClient.Status> timeline = cloud.getTimeline(20);
            for(YambaClient.Status status : timeline)
            {
                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getUser());
                values.put(StatusContract.Column.MESSAGE, status.getMessage());
                values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());

                Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
                if(uri != null)
                {
                    count++;
                    Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
                }
            }

            if(count > 0)
            {
                sendBroadcast(new Intent("NEW_STATUSES").putExtra("count", count));
            }
        }catch(YambaClientException e)
        {
            Log.e(TAG, "Failed to fetch the timeline", e);
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
