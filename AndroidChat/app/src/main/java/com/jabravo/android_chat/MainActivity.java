package com.jabravo.android_chat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.jabravo.android_chat.Data.DB_Android;
import com.jabravo.android_chat.Fragments.ChatsListFragment;
import android.provider.ContactsContract.CommonDataKinds.Phone;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   ChatsListFragment.OnFragmentInteractionListener
{

    private  NavigationView navigationView;
    public static DB_Android dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dataBase  = new DB_Android ( this , "Data Base" , null , 1); // El 1 es la version.

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String nick = prefs.getString("username","");

        if(nick.equals(""))
        {
            Intent intent = new Intent(this,StartUpActivity.class);
            startActivity(intent);
        }

        // Conseguir contactos
        Cursor cursor = null;
        try
        {
            cursor = getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
            int contactIdIdx = cursor.getColumnIndex(Phone._ID);
            int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
            int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
            int photoIdIdx = cursor.getColumnIndex(Phone.PHOTO_ID);
            cursor.moveToFirst();

            do
            {
                String idContact = cursor.getString(contactIdIdx);
                String name = cursor.getString(nameIdx);
                String phoneNumber = cursor.getString(phoneNumberIdx);

                Log.i("test",name + " " + phoneNumber );
            }
            while (cursor.moveToNext());
        }
        catch (Exception e)
        {
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Intent intent;
        switch (id)
        {
            case R.id.nav_chats:
                ChatsListFragment fragment = new ChatsListFragment();
                transaction.replace(R.id.mainlayout,fragment);

                break;

            case R.id.nav_test:
                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean vibrar = preferences.getBoolean("message-vibration" , true);

                if (vibrar)
                {
                    vibrate(1000);
                }

                intent = new Intent(this,ChatActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_newGroup:
                vibrate(200);
                break;

            case R.id.nav_profile:
                break;

            case R.id.nav_settings:
                intent = new Intent(this,Preferences.class);
                startActivity(intent);
                break;
        }
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    public void vibrate(int duration)
    {
        // hay que darle permisos en el manifests
        Vibrator vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibs.vibrate(duration); // en milisegundos
    }
}
