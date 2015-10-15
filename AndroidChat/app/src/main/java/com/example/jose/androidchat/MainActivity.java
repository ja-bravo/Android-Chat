package com.example.jose.androidchat;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import com.example.jose.androidchat.Settings.SettingsActivity;


public class MainActivity extends AppCompatActivity
{

    private ListView chatLists;
    private String[] chats = {"Juan", "Ana", "Pedro", "Silvana", "Miguel",
            "Lucas"};

    private ListView contactsList;
    private String[] contacts = {"Juan", "Ana", "Pedro", "Silvana", "Miguel",
            "Lucas", "Kira", "Abel", "Jose", "Pablo","Abel", "Jose", "Pablo"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = getResources();

        TabHost tabs= (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.chats),
                res.getDrawable(android.R.drawable.ic_btn_speak_now));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.contacts),
                res.getDrawable(android.R.drawable.ic_dialog_map));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i("AndroidTabsDemo", "Pulsada pestaña: " + tabId);
            }
        });


        // Chats

        chatLists = (ListView)findViewById(R.id.lvChats);

        ArrayAdapter<String> chatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chats);

        chatLists.setAdapter(chatAdapter);

        chatLists.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
            {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Ha pulsado el item " + position, Toast.LENGTH_SHORT).show();

            }

        });


        // Contactos

        contactsList = (ListView)findViewById(R.id.lvContactos);

        ArrayAdapter<String> contactsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);

        contactsList.setAdapter(contactsAdapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
            {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Ha pulsado el item " + position, Toast.LENGTH_SHORT).show();

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.newChat)
        {
            openNewChat();
        }
        else if(id == R.id.action_settings)
        {
            openSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openNewChat()
    {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    private void openSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
