package com.jabravo.android_chat;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{

    private ImageButton sendButton;
    private EditText keyboard;
    private ScrollView scrollView;
    private LinearLayout messagesLayout;

    private MessageList messages;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sendButton = (ImageButton) findViewById(R.id.chat_send);
        keyboard = (EditText) findViewById(R.id.chat_keyboard);
        scrollView = (ScrollView) findViewById(R.id.chat_scroll);
        messagesLayout = (LinearLayout) findViewById(R.id.chat_messages);

        sendButton.setOnClickListener(this);

        counter = 0;
        messages = new MessageList();

        // Esto es para conseguir y hacer que suene el sonido de notificacion.
        SharedPreferences getAlarms = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String alarms = getAlarms.getString("message-notification-sound", "default ringtone");

        Uri uri = Uri.parse(alarms);
        MediaPlayer player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        player.setLooping(false);

        try
        {
            player.setDataSource(this,uri);
            player.prepare();
            player.start();
        }
        catch(IOException e) {}
    }

    // Save the messages and the counter when the app changes orientation.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("counter",counter);
        savedInstanceState.putParcelable("messages", messages);
    }

    // Load the messages and the counter when the app changes orientation.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        counter = savedInstanceState.getInt("counter");
        messages = savedInstanceState.getParcelable("messages");
        for(Message message : messages)
        {
            showMessage(message);
        }
    }

    private void showMessage(Message message)
    {
        TextView textView = new TextView(this);
        textView.setText(message.getText());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        if(message.getSender().equals("user"))
        {
            params.gravity = Gravity.RIGHT;
            textView.setPadding(50, 10, 10, 10);
        }
        else
        {
            params.gravity = Gravity.LEFT;
            textView.setPadding(10, 10, 50, 10);
        }

        textView.setBackgroundResource(R.drawable.message);

        textView.setLayoutParams(params);
        messagesLayout.addView(textView);

        keyboard.setText("");

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

    @Override
    public void onClick(View v)
    {
        Message message = new Message();
        if (counter % 2 == 0)
        {
            message.setSender("user");
        }
        else
        {
            message.setSender("partner");
        }
        message.setText(keyboard.getText().toString());
        messages.add(message);

        showMessage(message);
        counter++;
    }
}