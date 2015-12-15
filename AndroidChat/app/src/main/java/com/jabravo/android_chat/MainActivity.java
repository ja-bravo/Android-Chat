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
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jabravo.android_chat.Data.Actions_DB;
import com.jabravo.android_chat.Data.DB_Android;
import com.jabravo.android_chat.Data.Friend;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.Fragments.ChatsListFragment;
import com.jabravo.android_chat.Fragments.ContactsFragment;
import com.jabravo.android_chat.Fragments.WelcomeFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   ChatsListFragment.OnFragmentInteractionListener, View.OnClickListener
{

    private  NavigationView navigationView;

    View view;
    TextView name;
    TextView status;
    ImageView image;

    public static DB_Android dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dataBase  = new DB_Android ( this , "Data Base" , null , 1); // El 1 es la version.
        
        List<Friend> l = Actions_DB.getAllFriends();

        for (int i = 0 ; i < l.size() ; i++)
        {
            System.out.println(l.get(i).getNick());
        }

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

        view = navigationView.getHeaderView(0);
        name = (TextView) view.findViewById(R.id.nav_user_name);
        status = (TextView) view.findViewById(R.id.nav_user_status);
        image = (ImageView) view.findViewById(R.id.nav_user_image);

        image.setOnClickListener(this);

        loadUserData();
        loadContacts();

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        WelcomeFragment fragment = WelcomeFragment.newInstance();
        transaction.replace(R.id.mainlayout, fragment);
        transaction.commit();
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
                ChatsListFragment fragment = ChatsListFragment.newInstance();
                transaction.replace(R.id.mainlayout, fragment);
                break;

            case R.id.nav_contacts:
                ContactsFragment contactsFragment = ContactsFragment.newInstance();
                transaction.replace(R.id.mainlayout, contactsFragment);
                break;

            case R.id.nav_newGroup:
                vibrate(200);
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

    private void loadUserData()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String nick = prefs.getString("username", "");
        User user = User.getInstance(getBaseContext());
        user.setID(prefs.getInt("ID", -1));

        if(nick.equals(""))
        {
            Intent intent = new Intent(this,StartUpActivity.class);
            startActivity(intent);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        user.setNick(preferences.getString("username",""));
        user.setNumber(preferences.getString("numberPhone", ""));
        user.setStatus(preferences.getString("status", ""));

        // TODO: 14/12/2015 CAMBIAR LA IMAGEN POR LA DEL USUARIO
        name.setText(user.getNick());
        status.setText(user.getStatus());
        //image.setImageURI();
    }

    private void loadContacts()
    {
        Cursor cursor = null;
        try
        {
            cursor = getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
            int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);

            cursor.moveToFirst();
            do
            {
                String phoneNumber = cursor.getString(phoneNumberIdx);

                User.getInstance().addFriend(phoneNumber);
            }
            while (cursor.moveToNext());
            User.getInstance().updateFriends();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
    public void onClick(View v)
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        int result = 0;
        startActivityForResult(photoPickerIntent, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode == RESULT_OK)
        {
            // TODO: 14/12/2015  QUE LA IMAGEN TENGA EL TAMAÑO CORRECTO.
            image.setImageURI(imageReturnedIntent.getData());
        }
    }
}
