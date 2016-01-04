package com.jabravo.android_chat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
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
import com.jabravo.android_chat.Fragments.GroupCreatorFragment;
import com.jabravo.android_chat.Fragments.WelcomeFragment;
import com.jabravo.android_chat.Services.DownloadImage;
import com.jabravo.android_chat.Services.UploadImage;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatsListFragment.OnFragmentInteractionListener, View.OnClickListener
{

    private NavigationView navigationView;

    private View view;
    private TextView name;
    private TextView status;
    private ImageView image;
    public static boolean openProgram;
    public static int timeSleep;
    public static int timeSleepStart;
    Bitmap photobmp;
    public static boolean isChatPrivate;

    public static DB_Android dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dataBase = new DB_Android(this, "Data Base", null, 1); // El 1 es la version.

        timeSleepStart = 250;
        timeSleep = timeSleepStart;

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
        insertFriendsDB();

        new Thread(checkAndLoadImages).start();

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        WelcomeFragment fragment = WelcomeFragment.newInstance();
        transaction.replace(R.id.mainlayout, fragment);
        transaction.commit();

        openProgram = true;
        System.out.println("abierto");
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        openProgram = false;
        System.out.println("cerrado");
    }


    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
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
                //intent = new Intent(this,GroupActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putInt("groupID",2);

                //intent.putExtras(bundle);

                //startActivity(intent);
                GroupCreatorFragment groupCreatorFragment = GroupCreatorFragment.newInstance();
                transaction.replace(R.id.mainlayout, groupCreatorFragment);
                break;

            case R.id.nav_settings:
                intent = new Intent(this, Preferences.class);
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


    private void insertFriendsDB()
    {
        List<Friend> listDbAndroid = Actions_DB.getAllFriends();
        List<Friend> list = User.getInstance().getFriends();

        for (int i = 0 ; i < list.size() ; i++)
        {
            String phone_1 = list.get(i).getPhone();
            String phone_2 ="";

            boolean found = false;

            for (int j = 0 ; j < listDbAndroid.size() && !found ; j++)
            {
                phone_2 = listDbAndroid.get(j).getPhone();

                if (phone_1.equals(phone_2))
                {
                    found = true;
                }
            }

            if (!found)
            {
                System.out.println(phone_1 + " - " + phone_2);

                String id = String.valueOf(list.get(i).getId());
                String status = list.get(i).getStatus();
                String image = list.get(i).getImage();
                String nick = list.get(i).getNick();
                String phone = list.get(i).getPhone();

                Actions_DB.insertFriend(id, phone, status, image, nick);
            }
        }
    }


    Runnable checkAndLoadImages = new Runnable() {
        @Override
        public void run() {

            List<Friend> listDbAndroid = Actions_DB.getAllFriends();
            List<Friend> list = User.getInstance().getFriends();

            for (int i = 0 ; i < list.size() ; i++)
            {

                String image_1 = list.get(i).getImage();
                String image_2 = "";

                String id = String.valueOf(list.get(i).getId());


                boolean found = false;

                for (int j = 0 ; j < listDbAndroid.size() && !found ; j++)
                {
                    image_2 = listDbAndroid.get(j).getImage();

                    if (image_1.equals(image_2))
                    {
                        found = true;
                    }
                }

                if (!found)
                {
                    if (image_1 != "" && image_1 != null)
                    {
                        System.out.println("Descargando...");

                        new DownloadImage(image_1);
                    }

                    Actions_DB.upDateImageFriend(id, image_1);

                }
            }
        }
    };


    private void loadUserData()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String nick = prefs.getString("username", "");
        User user = User.getInstance(getBaseContext());
        user.setID(prefs.getInt("ID", -1));

        if (nick.equals(""))
        {
            Intent intent = new Intent(this, StartUpActivity.class);
            startActivity(intent);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        user.setNick(preferences.getString("username", ""));
        user.setNumber(preferences.getString("numberPhone", ""));
        user.setStatus(preferences.getString("status", ""));

        name.setText(user.getNick());
        status.setText(user.getStatus());

        try {
            image.setImageURI(Uri.parse(preferences.getString("image", "")));
        } catch (Exception e) {e.printStackTrace();}
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

        if (resultCode == RESULT_OK)
        {
            Uri selectedImageUri = imageReturnedIntent.getData();

            String pathReal = getRealPathFromURI(selectedImageUri);

            photobmp = BitmapFactory.decodeFile(pathReal);

            image.setImageBitmap(photobmp);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("image", imageReturnedIntent.getDataString()).apply();

            Log.i("image", prefs.getString("image", ""));

            // Al cambiar la imagen, la subo.
            uploadImage (pathReal);
        }
    }

    public void uploadImage (String pathReal )
    {
        String nameImage = pathReal.substring(pathReal.lastIndexOf("/") + 1 , pathReal.lastIndexOf("."));

        //Codifica la imagen con Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photobmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        //Se ejecuta en segundo plano para no colgar la aplicacion
        new UploadImage(MainActivity.this , nameImage).execute(encodedImage);
    }

    // COn esta funcion saco la ruta real de donde esta la imagen.
    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getApplicationContext().getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
