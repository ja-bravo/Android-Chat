package com.jabravo.android_chat;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jabravo.android_chat.Data.Message;
import com.jabravo.android_chat.Data.MessageList;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.Services.Sender;
import com.jabravo.android_chat.Services.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton sendButton;
    private EditText keyboard;
    private ScrollView scrollView;
    private LinearLayout messagesLayout;
    private Ringtone ringtone;

    private int toID;

    private ThreadPoolExecutor executor;

    private boolean runReceiver;

    private MessageList messages;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        user = User.getInstance();

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sendButton = (ImageButton) findViewById(R.id.chat_send);
        keyboard = (EditText) findViewById(R.id.chat_keyboard);
        scrollView = (ScrollView) findViewById(R.id.chat_scroll);
        messagesLayout = (LinearLayout) findViewById(R.id.chat_messages);

        sendButton.setOnClickListener(this);

        messages = new MessageList();

        // Esto es para conseguir y hacer que suene el sonido de notificacion.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String alarms = preferences.getString("message-notification-sound", "default ringtone");

        Uri uri = Uri.parse(alarms);
        ringtone = RingtoneManager.getRingtone(this, uri);

        toID = Integer.parseInt(preferences.getString("userToSend", "2"));

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        executor.execute(new Service());
        executor.execute(new Receiver());
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

        executor.shutdownNow();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(executor.isTerminated())
        {
            executor.execute(new Service());
            executor.execute(new Receiver());
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
            showMessage(message);
        }
    }

    public void showMessage(Message message)
    {

        TextView textView = new TextView(this);
        textView.setText(message.getText());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        if (message.getSender() == user.getID())
        {
            params.gravity = Gravity.RIGHT;
            textView.setPadding(50, 10, 10, 10);
        }
        else
        {
            ringtone.play();
            params.gravity = Gravity.LEFT;
            textView.setPadding(10, 10, 50, 10);
        }

        textView.setBackgroundResource(R.drawable.message);
        textView.setLayoutParams(params);

        messagesLayout.addView(textView);
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
            System.out.println(e);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (keyboard.getText().toString().length() != 0)
        {
            Message message = new Message(keyboard.getText().toString(), user.getID(), toID);
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

    // **********************************************
    // Clase para recibir mensajes
    // **********************************************
    public class Receiver implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                if (!Service.buffer.isEmpty())
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            while(!Service.buffer.isEmpty())
                            {
                                try
                                {
                                    showMessage(Service.buffer.take());
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                try
                {
                    Thread.sleep(250);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}