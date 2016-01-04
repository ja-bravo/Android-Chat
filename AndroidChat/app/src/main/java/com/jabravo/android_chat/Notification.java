package com.jabravo.android_chat;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // para cancelar la notificacion


        boolean isGroup = getIntent().getExtras().getBoolean("isGroup");

        System.out.println("NOTIFICATION es group " +  isGroup);
        System.out.println("NOTIFICATION es group " +  getIntent().getExtras().getInt("notificationID"));


        if (!isGroup)
        {
            Intent intentViewImages = new Intent(this, ChatActivity.class);
            intentViewImages.putExtra("toID" , getIntent().getExtras().getString("notificationID"));
            startActivity(intentViewImages);
        }
        else
        {
            Intent intentViewImages = new Intent(this, GroupActivity.class);
            intentViewImages.putExtra("groupID" , getIntent().getExtras().getString("notificationID"));
            startActivity(intentViewImages);
        }


        nm.cancel(getIntent().getExtras().getInt("notificationID"));

        this.finish();
    }
}
