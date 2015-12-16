package com.jabravo.android_chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;

/**
 * Created by Josewer on 16/12/2015.
 */

public class Notification extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // para cancelar la notificacion
        nm.cancel(getIntent().getExtras().getInt("notificationID"));
    }
}
