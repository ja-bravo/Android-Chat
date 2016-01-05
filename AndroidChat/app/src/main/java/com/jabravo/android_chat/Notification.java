package com.jabravo.android_chat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        boolean isGroup = getIntent().getExtras().getBoolean("isGroup");
        int notificationID =  getIntent().getExtras().getInt("notificationID");

        System.out.println("NOTIFICATION es group " +  notificationID);
        System.out.println("NOTIFICATION es group " + isGroup);

        // para cancelar la notificacion

        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationID);

        if (!isGroup)
        {
            Intent intentViewImages = new Intent(this, ChatActivity.class);
            intentViewImages.putExtra("toID" , notificationID);
            startActivity(intentViewImages);
        }
        else
        {
            Intent intentViewImages = new Intent(this, GroupActivity.class);
            intentViewImages.putExtra("groupID" , notificationID);
            startActivity(intentViewImages);
        }

        this.finish();
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
