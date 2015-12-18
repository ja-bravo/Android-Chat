package com.jabravo.android_chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jabravo.android_chat.Data.Actions_DB;
import com.jabravo.android_chat.Data.Friend;
import com.jabravo.android_chat.Data.Message;
import com.jabravo.android_chat.Data.MessageList;
import com.jabravo.android_chat.Data.PausableThreadPool;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.Services.Sender;
import com.jabravo.android_chat.Services.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton sendButton;
    private EditText keyboard;
    private ScrollView scrollView;
    private LinearLayout messagesLayout;
    private ImageView userImage;
    private int timeSleep;
    private Ringtone ringtone;
    private static PausableThreadPool executor;
    private Thread threadReceiver;
    private MessageList messages;
    private User user;
    private Friend friend;
    private static Service service;
    private int timeSleepMin;
    private static LinkedBlockingQueue<Runnable> queue;

    private int toID;

    private static boolean isStarted = false; // no tocar de aqui

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        toID = getIntent().getExtras().getInt("toID");

        user = User.getInstance();
        friend = user.getFriendsHashMap().get(String.valueOf(toID));

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sendButton = (ImageButton) findViewById(R.id.chat_send);
        keyboard = (EditText) findViewById(R.id.chat_keyboard);
        scrollView = (ScrollView) findViewById(R.id.chat_scroll);
        messagesLayout = (LinearLayout) findViewById(R.id.chat_messages);
        userImage = (ImageView) findViewById(R.id.chat_user_image);

        sendButton.setOnClickListener(this);

        changeToolBar();

        messages = new MessageList();

        // Esto es para conseguir y hacer que suene el sonido de notificacion.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String alarms = preferences.getString("message-notification-sound", "default ringtone");

        Uri uri = Uri.parse(alarms);
        ringtone = RingtoneManager.getRingtone(this, uri);


        if (!isStarted)
        {
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

            executor = new PausableThreadPool(2,2,10, TimeUnit.SECONDS,queue);
            executor.execute(service);
        }

        timeSleepMin = 250;
        timeSleep = timeSleepMin;
        loadMessageDB();

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
    }



    private void changeToolBar()
    {
        // TODO: 14/12/2015 CAMBIAR LA IMAGEN POR LA DEL USUARIO
        setTitle(friend.getNick());
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
        saveMessagesDB();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        executor.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        threadReceiver = new Thread(Receiver);

        threadReceiver.start();
        executor.resume();
    }

    // Load the messages and the counter when the app changes orientation.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        messages = savedInstanceState.getParcelable("messages");

        for (Message message : messages)
        {
            showMessage(message);
        }
    }


    public void interrumptThread ()
    {
        //service.
    }


    public void saveMessagesDB ()
    {
        for (int i = 0 ; i <  messages.size() ; i++)
        {
            String text =  messages.get(i).getText();
            String date =  messages.get(i).getDate();
            int idReceiver =  messages.get(i).getReceiver();
            int idFriend =  messages.get(i).getIdFriend();
            boolean read =  messages.get(i).isRead();

            Actions_DB.insertMessagePrivate(text, date, read, idFriend, idReceiver);
        }

        messages.clear();
    }


    public void loadMessageDB ()
    {
        List<Message> messagesDB = Actions_DB.loadMessages(toID);

        System.out.println(messagesDB.size());
        for (int i = 0 ; i < messagesDB.size() ; i++)
        {
            showMessage(messagesDB.get(i));
            System.out.println(messagesDB.get(i).getText());
        }
    }


    public void showMessage(Message message)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 20);

        TextView textView = new TextView(this);

        textView.setText(message.getText());

        if (message.getReceiver() != user.getID())
        {
            params.gravity = Gravity.RIGHT;

            textView.setBackgroundResource(R.drawable.message_1);
        }
        else
        {
            ringtone.play();
            params.gravity = Gravity.LEFT;

            textView.setBackgroundResource(R.drawable.message_2);
        }

        textView.setLayoutParams(params);
        messagesLayout.addView(textView);

        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);


        keyboard.setText("");

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
    }


    @Override
    public void onClick(View v)
    {
        if (keyboard.getText().toString().length() != 0)
        {
            Calendar cal = new GregorianCalendar();
            Date date = cal.getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormat = df.format(date);

            Message message = new Message(keyboard.getText().toString(), dateFormat , true,
                    toID , toID); // con quien es la conversacion, y quien lo tiene que recivir

            messages.add(message);

            sendMessage(message.getText());
            showMessage(message);
        }
    }


    private void sendMessage(String message)
    {
        Sender sender = new Sender();
        sender.execute(message,String.valueOf(toID),String.valueOf(user.getID()));
    }


    Runnable runThreadTimeOut = new Runnable() {
        @Override
        public void run() {

            int timeMax = 1000 * 60 * 1;

            while (!MainActivity.openProgram && timeMax > timeSleep)
            {
                timeSleep += 500 * 1;
                service.setTimeSleep(timeSleep);

                System.out.println("time: " + timeSleep );

                try {
                    Thread.sleep(timeSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            service.setTimeSleep(timeSleepMin);
        }
    };



    public  void showNotification (Message message) {
        Intent intent = new Intent(this, Notification.class);

        int notificationID = message.getIdFriend(); // con esto consigo que no salgan nuevas notoficaciones para este usuario
        intent.putExtra("notificationID", notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String friendName = User.getInstance().getFriendsHashMap().get(String.valueOf(notificationID)).getNick();

        CharSequence ticker = "Say: " + message.getText();
        CharSequence contentTitle = "Android Chat";
        CharSequence contentText = "Message from " + friendName;

        android.app.Notification noti = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setLights(Color.RED, 1, 0)
                .setSmallIcon(R.mipmap.ic_ini)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_ini, ticker, pendingIntent)
                .setVibrate(new long[]{100, 250, 100, 500})
                .build();

        nm.notify(notificationID, noti);
        ringtone.play();
    }



    // **********************************************
    // Clase para recibir mensajes
    // **********************************************

    public Runnable Receiver = new Runnable() {
        @Override
        public void run() {

            while (!Thread.interrupted())
            {
                if (!service.getBuffer().isEmpty())
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            Log.i("pruebas", String.valueOf(service.getBuffer().size()));
                            Iterator<Message> it = service.getBuffer().iterator();
                            while(it.hasNext())
                            {
                                Message message = it.next();
                                Log.i("pruebas", String.valueOf(message.getIdFriend() + "-" + toID));

                                if(message.getIdFriend() == toID)
                                {
                                    showMessage(message);
                                }

                                if (!MainActivity.openProgram){

                                    showNotification(message);
                                }

                                messages.add(message);
                                it.remove();
                            }
                        }
                    });
                }
                try
                {
                    Thread.sleep(timeSleep);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (!MainActivity.openProgram && timeSleep == timeSleepMin)
                {
                    timeSleep++;
                    Thread threadOutTime = new Thread (runThreadTimeOut);
                    threadOutTime.start();
                }
            }
        }
    };
}