package com.jabravo.android_chat; /**
 * Created by Jose on 16/11/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task, we'll just display a message
        Log.i("test",String.valueOf(System.currentTimeMillis()));

    }

}
