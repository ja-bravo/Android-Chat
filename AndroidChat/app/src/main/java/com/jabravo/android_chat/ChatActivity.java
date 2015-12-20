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
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.PopupMenu;


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


public class ChatActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener
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

    private static int toID;

    private static boolean isStarted = false; // no tocar de aqui

    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    public void saveDBMessages()
    {
        for (int i = 0; i < messages.size(); i++)
        {
            String text = messages.get(i).getText();
            String date = messages.get(i).getDate();
            int idReceiver = messages.get(i).getReceiver();
            int idFriend = messages.get(i).getIdFriend();
            boolean read = messages.get(i).isRead();

            Actions_DB.insertMessagePrivate(text, date, read, idFriend, idReceiver);
        }

        messages.clear();
    }

    public void loadDBMessages()
    {
        List<Message> messagesDB = Actions_DB.loadMessages(toID);

        System.out.println("Total mensajes: " + messagesDB.size());
        for (int i = 0; i < messagesDB.size(); i++)
        {
            showMessage(messagesDB.get(i));
            System.out.println(messagesDB.get(i).getText());
        }
    }

    private void showMessage(Message message)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 20);

        TextView textView = new TextView(ChatActivity.this);

        String nameSender;

        if (message.getReceiver() != user.getID())
        {
            params.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.message_1);

            nameSender = user.getNick();
        }
        else
        {
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
        if (v.getId() == R.id.chat_send && keyboard.getText().toString().length() != 0)
        {
            Calendar cal = new GregorianCalendar();
            Date date = cal.getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormat = df.format(date);

            Message message = new Message(keyboard.getText().toString(), dateFormat, true,
                    toID, toID); // con quien es la conversacion, y quien lo tiene que recivir

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

        int notificationID = message.getIdFriend(); // con esto consigo que no salgan nuevas notoficaciones para este usuario
        intent.putExtra("notificationID", notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String friendName = User.getInstance().getFriendsHashMap().get(String.valueOf(notificationID)).getNick();

        CharSequence ticker = message.getText();
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

    public Runnable Receiver = new Runnable()
    {
        @Override
        public void run()
        {

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
                            while (it.hasNext())
                            {
                                Message message = it.next();
                                messages.add(message);

                                if (message.getIdFriend() == toID)
                                {
                                    showMessage(message);
                                    ringtone.play();
                                }
                                else
                                {
                                    saveDBMessages();
                                }

                                if (!MainActivity.openProgram || message.getIdFriend() != toID)
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

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return false;
    }
}