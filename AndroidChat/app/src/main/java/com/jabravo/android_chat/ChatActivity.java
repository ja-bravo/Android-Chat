package com.jabravo.android_chat;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jabravo.android_chat.Data.Actions_DB;
import com.jabravo.android_chat.Data.Friend;
import com.jabravo.android_chat.Data.Group;
import com.jabravo.android_chat.Data.Message;
import com.jabravo.android_chat.Data.MessageList;
import com.jabravo.android_chat.Data.PausableThreadPool;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.Services.Sender;
import com.jabravo.android_chat.Services.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener,
        PopupMenu.OnMenuItemClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    private ImageButton sendButton;
    private EditText keyboard;
    private ScrollView scrollView;
    private LinearLayout messagesLayout;
    private ImageView userImage;
    private TextView userName;
    private ImageButton attachButton;

    private Ringtone ringtone;
    private static PausableThreadPool executor;
    private static Thread threadReceiver;
    private static MessageList messages;
    private User user;
    private Friend friend;
    private static Service service;
    private static LinkedBlockingQueue<Runnable> queue;


    private ImageView imageMapView;

    private static int toID;

    private static boolean isStarted = false; // no tocar de aqui

    private Intent intentViewImages;
    private PopupMenu popupMenu;

    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20000)
                .setFastestInterval(10000);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        toID = getIntent().getExtras().getInt("toID");
        user = User.getInstance();
        friend = user.getFriendsHashMap().get(String.valueOf(toID));

        popupMenu = new PopupMenu(this, toolbar, Gravity.RIGHT);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup);

        sendButton = (ImageButton) findViewById(R.id.chat_send);
        keyboard = (EditText) findViewById(R.id.chat_keyboard);
        scrollView = (ScrollView) findViewById(R.id.chat_scroll);
        messagesLayout = (LinearLayout) findViewById(R.id.chat_messages);
        userImage = (ImageView) findViewById(R.id.chat_user_image);
        userName = (TextView) findViewById(R.id.chat_user_name);
        attachButton = (ImageButton) findViewById(R.id.chat_attach);

        sendButton.setOnClickListener(this);
        attachButton.setOnClickListener(this);

        changeToolBar();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String alarms = preferences.getString("message-notification-sound", "default ringtone");

        Uri uri = Uri.parse(alarms);
        ringtone = RingtoneManager.getRingtone(this, uri);

        if (!isStarted)
        {
            // Si es la primera vez que entro, lo inicializo todo y asi evito que se creen varias veces y pasen las cosas raras
            messages = new MessageList();
            queue = new LinkedBlockingQueue<>();
            isStarted = true;
            service = new Service();

            try
            {
                queue.put(service);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            executor = new PausableThreadPool(2, 2, 10, TimeUnit.SECONDS, queue);
            executor.execute(service);

            threadReceiver = new Thread(Receiver);
            threadReceiver.start();
        }
        else
        {
            // Si no es la primera vez que entro, me tengo que cargar este hilo para rehacerlo,
            // porque el que era su hilo principal, ya no es el hilo principal que ha creado esta nueva interfaz.
            // asi evito el problema de que no me salieran los mensajes que recibia..
            while (threadReceiver.isAlive())
            {
                threadReceiver.interrupt();
            }

            threadReceiver = new Thread(Receiver);
            threadReceiver.start();
        }

        loadDBMessages();

        try
        {
            // This scrolls the ScrollView after the message has been added
            scrollView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        loadImage ();
    }

    private void changeToolBar()
    {
        // TODO: 14/12/2015 CAMBIAR LA IMAGEN POR LA DEL USUARIO
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userName.setText(friend.getNick());
        //userImage.setImageURI();
    }

    // Save the messages and the counter when the app changes orientation.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("messages", messages);
    }

    @Override
    protected void onPause()
    {
        if (mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        saveDBMessages();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        mGoogleApiClient.connect();
        MainActivity.isChatPrivate = true;
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        saveDBMessages();
        System.out.println("Estoy en el destroy");
        toID = 0;
        super.onDestroy();
    }

    @Override
    protected void finalize() throws Throwable {
        saveDBMessages();
        super.finalize();
    }

    public void loadImage ()
    {
        String nameFile = User.getInstance().getFriendsHashMap().get(String.valueOf(toID)).getImage();

        if (!nameFile.equals("") && !nameFile.equals(null)) {
            File ruta_sd = Environment.getExternalStorageDirectory();

            String ruta = ruta_sd.getAbsolutePath() + "/IMAGES_CHAT_ANDROID/" + nameFile + ".jpg";

            File file = new File(ruta);

            if (file.exists()) {

                Bitmap photobmp = BitmapFactory.decodeFile(ruta);
                userImage.setImageBitmap(photobmp);

                intentViewImages = new Intent(this, ViewImage.class);
                intentViewImages.putExtra("path" , ruta);

                userImage.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        System.out.println("Ver imagen!!!!!!!!!!!!!");
                        startActivity(intentViewImages);

                    }

                });
            }
        }
    }

    // Load the messages and the counter when the app changes orientation.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        messages = savedInstanceState.getParcelable("messages");

        for (Message message : messages)
        {
            if(isAMap(message.getText()))
            {
                try
                {
                    showMap(message);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                showMessage(message);
            }
        }
    }

    public void saveDBMessages()
    {
        for (int i = 0; i < messages.size(); i++)
        {
            String text = messages.get(i).getText();
            String date = messages.get(i).getDate();
            int idReceiver = messages.get(i).getReceiver();
            int idFriend = messages.get(i).getIdFriend();
            boolean read = messages.get(i).isRead();


            if (!messages.get(i).getIsGroup())
            {
                Actions_DB.insertMessagePrivate(text, date, read, idFriend, idReceiver);
                System.out.println("GUARDANDO EN PRIVADO - PRIVADO.");
            }
            else
            {
                Actions_DB.insertMessageGroup(text, date, read, idReceiver  , idFriend);
                System.out.println("GUARDANDO EN GRUPO - PRIVADO .");
            }
        }

        messages.clear();
    }

    public void loadDBMessages()
    {
        List<Message> messagesDB = Actions_DB.loadMessagesPrivate(toID);

        String lastDate = "";

        for (int i = 0; i < messagesDB.size(); i++)
        {
            Message message = messagesDB.get(i);

            if (!message.getDate().trim().equals(lastDate.trim()))
            {
                lastDate = message.getDate();
                showDate(lastDate);
            }

            if(isAMap(message.getText()))
            {
                try
                {
                    showMap(message);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                showMessage(message);
            }
        }
    }

    private void showDate (String date)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 20);

        TextView textView = new TextView(ChatActivity.this);
        params.gravity =  Gravity.CENTER;
        textView.setBackgroundResource(R.drawable.message_date);

        textView.setText(date);

        textView.setLayoutParams(params);
        messagesLayout.addView(textView);

        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);
    }


    private void showMessage(Message message)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 20);

        TextView textView = new TextView(ChatActivity.this);

        String nameSender;

        if (message.getReceiver() != user.getID()) {
            params.gravity =  Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.message_1);

            nameSender = user.getNick();
        } else {
            params.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.message_2);

            nameSender = User.getInstance().getFriendsHashMap().get(String.valueOf(message.getIdFriend())).getNick();
        }

        String text = "<font color=#161F89><small><b>" + nameSender + ":" + "</b></small></font><br/>" + message.getText();

        textView.setText(Html.fromHtml(text));

        textView.setLayoutParams(params);
        messagesLayout.addView(textView);

        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);

        keyboard.setText("");
        try {
            // This scrolls the ScrollView after the message has been added
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.chat_send && keyboard.getText().toString().length() != 0)
        {
            Calendar cal = new GregorianCalendar();
            Date date = cal.getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormat = df.format(date);

            boolean isGroup = false;

            Message message = new Message(keyboard.getText().toString(), dateFormat, true,
                    toID, toID , isGroup); // con quien es la conversacion, y quien lo tiene que recivir

            messages.add(message);

            sendMessage(message.getText());
            showMessage(message);
        }
        else
        {
            popupMenu.show();
        }
    }


    private void sendMessage(String message)
    {
        Sender sender = new Sender();
        sender.execute(message, String.valueOf(toID), String.valueOf(user.getID()));
    }


    Runnable runThreadTimeOut = new Runnable()
    {
        @Override
        public void run()
        {

            int timeMax = 1000 * 60 * 1;

            while (!MainActivity.openProgram && timeMax > MainActivity.timeSleep)
            {
                MainActivity.timeSleep += 500 * 1;
                service.setTimeSleep(MainActivity.timeSleep);

                System.out.println("time: " + MainActivity.timeSleep);

                try
                {
                    Thread.sleep(MainActivity.timeSleep);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            service.setTimeSleep(MainActivity.timeSleepStart);
        }
    };


    public void showNotification(Message message)
    {
        Intent intent = new Intent(this, Notification.class);

        boolean isGroup = message.getIsGroup();
        int notificationID = isGroup? message.getReceiver(): message.getIdFriend();

        System.out.println("NOTIFICATION es group " +  notificationID);

        intent.putExtra("notificationID", notificationID);
        intent.putExtra("isGroup", isGroup);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String name = "";

        if (isGroup)
        {
            Group group = user.getGroupsHashMap().get(String.valueOf(notificationID));
            name = group.getName();
        }
        else
        {
            name = User.getInstance().getFriendsHashMap().get(String.valueOf(notificationID)).getNick();
        }

        CharSequence ticker = message.getText();
        CharSequence contentTitle = "Android Chat";
        CharSequence contentText = "Message from " + name;

        NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                //.setLights(Color.RED, 1, 0)
                .setSmallIcon(R.mipmap.ic_ini)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_ini, ticker, pendingIntent)
                .setVibrate(new long[]{100, 250, 100, 500});


        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationID , noti.build());
        ringtone.play();
    }


    // **********************************************
    // Clase para recibir mensajes
    // **********************************************

    public Runnable Receiver = new Runnable()
    {
        @Override
        public void run()
        {

            while (!Thread.interrupted())
            {
                System.out.println("UUU SOY RECEIVER INVI");

                if (!service.getBuffer().isEmpty())
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.i("pruebas", String.valueOf(service.getBuffer().size()));
                            Iterator<Message> it = service.getBuffer().iterator();
                            while (it.hasNext())
                            {
                                Message message = it.next();
                                messages.add(message);
                                boolean messageIsDisplay = false;
                                boolean isGroup = message.getIsGroup();

                                System.out.println("UUU el grupo esta abierto " +  !MainActivity.isChatPrivate);

                                if (!isGroup && message.getIdFriend() == toID && MainActivity.isChatPrivate)
                                {
                                    if(isAMap(message.getText()))
                                    {
                                        try
                                        {
                                            showMap(message);
                                            messageIsDisplay = true;
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("MM mensajes es grupo: " + message.getIsGroup());
                                        System.out.println("MM es chat es privado: " +  MainActivity.isChatPrivate);

                                        if ((!message.getIsGroup() && MainActivity.isChatPrivate) ) {
                                            showMessage(message);
                                            messageIsDisplay = true;
                                        }
                                    }

                                    ringtone.play();
                                }
                                else
                                {
                                    saveDBMessages();
                                }

                                if (!MainActivity.openProgram || !messageIsDisplay)
                                {
                                    showNotification(message);
                                }


                                it.remove();
                            }
                        }
                    });
                }
                try
                {
                    Thread.sleep(MainActivity.timeSleep);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (!MainActivity.openProgram && MainActivity.timeSleep == MainActivity.timeSleepStart)
                {
                    MainActivity.timeSleep++;
                    Thread threadOutTime = new Thread(runThreadTimeOut);
                    threadOutTime.start();
                }
            }
        }
    };

    private void showMap(final Message message) throws JSONException
    {
        JSONObject json = new JSONObject(message.getText().substring(3));
        final double longitude = json.getDouble("longitude");
        final double latitude = json.getDouble("latitude");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x / 2;
        final int height = size.y / 5;

        final String URL = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude
                + "&zoom=18&size=1200x1000&sensor=false&markers=color:blue%7C" + latitude + "," + longitude;

        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap imageMap = getGoogleMapThumbnail(URL);
                    imageMapView= new ImageView(ChatActivity.this);
                    imageMapView.setImageBitmap(imageMap);
                    imageMapView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Location targetLocation = new Location(location);
                            targetLocation.setLongitude(longitude);
                            targetLocation.setLatitude(latitude);

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("my_position",location);
                            bundle.putParcelable("target_position",targetLocation);

                            Intent intent = new Intent(ChatActivity.this,MapsActivity.class);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                    });

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    if (message.getReceiver() != user.getID()) {
                        params.gravity = Gravity.RIGHT;
                        imageMapView.setBackgroundResource(R.drawable.message_1);
                    } else {
                        params.gravity = Gravity.LEFT;
                        imageMapView.setBackgroundResource(R.drawable.message_2);
                    }

                    params.setMargins(0, 0, 0, 20);

                    imageMapView.setLayoutParams(params);
                    imageMapView.setPadding(16, 16, 16, 16);
                    imageMapView.setScaleType(ImageView.ScaleType.FIT_XY);

                    imageMapView.setMaxHeight(height);
                    imageMapView.setMaxWidth(width);
                    imageMapView.setMinimumHeight(height);
                    imageMapView.setMinimumWidth(width);
                }
            });

            thread.start();
            thread.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

        messagesLayout.addView(imageMapView);

        keyboard.setText("");
        try {
            // This scrolls the ScrollView after the message has been added
            scrollView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap getGoogleMapThumbnail(String url)
    {
        Bitmap bmp = null;
        try
        {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoOutput(true);

            InputStream in = con.getInputStream();
            try
            {
                bmp = BitmapFactory.decodeStream(in);
                in.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        catch (Exception e) {}
        return bmp;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Calendar cal = new GregorianCalendar();
            Date date = cal.getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormat = df.format(date);

            if (location != null)
            {
                try
                {
                    JSONObject position = new JSONObject();
                    position.put("latitude",location.getLatitude());
                    position.put("longitude",location.getLongitude());

                    boolean isGroup = false;

                    Message message = new Message("MAP"+position.toString(), dateFormat, true, toID, toID , isGroup);
                    messages.add(message);

                    sendMessage(message.getText());
                    showMap(message);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(this,getResources().getString(R.string.gps_disabled),Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.gps_off))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(final DialogInterface dialog, final int id)
                        {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        public void onClick(final DialogInterface dialog, final int id)
                        {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }

        return false;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.location = location;
    }

    private boolean isAMap(String text)
    {
        return text.contains("MAP") && text.contains("latitude") && text.contains("longitude");
    }
}