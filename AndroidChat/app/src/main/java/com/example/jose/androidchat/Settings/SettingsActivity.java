package com.example.jose.androidchat.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.jose.androidchat.R;

public class SettingsActivity extends AppCompatActivity
{
    ListView settingsList;
    String[] settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new String[] {getResources().getString(R.string.settings_profile),
                                 getResources().getString(R.string.settings_notifications),
                                 getResources().getString(R.string.settings_chats)};

        settingsList = (ListView) findViewById(R.id.settingsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settings);
        settingsList.setAdapter(adapter);
    }
}
