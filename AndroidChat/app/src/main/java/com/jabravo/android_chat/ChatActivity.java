package com.jabravo.android_chat;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{

    private ImageButton sendButton;
    private EditText keyboard;
    private ScrollView scrollView;
    private LinearLayout messagesLayout;
    private int myId;
    private int idSender;
    private MediaPlayer player;
    SoundPool soundPool; // 1º cosa hacer: declarar la variables, 2º inicializar
    int carga;

    private Thread service;
    private Thread receiver;

    private boolean runReceiver;

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String alarms = preferences.getString("message-notification-sound", "default ringtone");

        Uri uri = Uri.parse(alarms);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        player.setLooping(false);


        // 8, porque asi tengo 8 canales. Siempre es 0 el ultimo parametro
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Cargo los audios
        carga = soundPool.load(this, R.raw.gako, 1);


        myId = Integer.parseInt(preferences.getString("userToTransmitter", "1"));
        idSender = Integer.parseInt(preferences.getString("userToSend", "2"));

        service = new Thread(new Service(myId));
        receiver = new Thread(new Receiver());


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

    @Override
    protected void onPause() {
        super.onPause();

        Service.setRun(false);
        runReceiver = false;

    }

    @Override
    protected void onResume() {
        super.onResume();

        Service.setRun(true);
        runReceiver = true;

        service.start();
        receiver.start();
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



    public void showMessage(Message message)
    {

        TextView textView = new TextView(this);
        textView.setText(message.getText());


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        if(message.getSender() == myId)
        {
            params.gravity = Gravity.RIGHT;
            textView.setPadding(50, 10, 10, 10);
        }
        else
        {
            Thread playThead = new Thread() {
                public void run() {
                    soundPool.play(carga, 1, 1, 0, 0, 1);
                }
            };

            playThead.start();

            params.gravity = Gravity.LEFT;
            textView.setPadding(10, 10, 50, 10);
        }

        textView.setBackgroundResource(R.drawable.message);

        textView.setLayoutParams(params);

        messagesLayout.addView(textView); // SOLO PUEDE TOCAR LA VISTA EL HILO QUE LA HA CREADO, O SEA, EL PRINCIPAL.
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

        } catch (Exception e) {System.out.println(e);}
    }

    @Override
    public void onClick(View v) {

        if (keyboard.getText().toString().length() != 0)
        {
            Message message = new Message(keyboard.getText().toString(), myId);
            //message.setText(keyboard.getText().toString());
            messages.add(message);


            sendMessage(message.getText());
            showMessage(message);
            counter++;
        }
    }

    private void sendMessage(String message)
    {
        Sender sender = new Sender();
        sender.execute(message);
    }


// **********************************************
// Clase para enviar mensajes
// **********************************************


    public class Sender extends AsyncTask<String,Integer,Void>
    {
        public Sender()
        {
            super();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            try
            {
                params[0] = params[0].replaceAll(" ", "%20");

                URL url = new URL("http://146.185.155.88:8080/api/post/message/"+idSender+"&"+params[0]+"&"+myId);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                connection.setDoOutput(true);

                System.out.println("---------------" + connection.getResponseCode());
                Log.i("test",String.valueOf(connection.getResponseCode()));
            } catch (Exception e)
            {
                Log.i("test", String.valueOf(e.toString()));
            }
            return null;
        }

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
    }



// **********************************************
// Clase para recibir mensajes
// **********************************************

    public class Receiver implements Runnable {

        @Override
        public void run() {
            while (runReceiver) {

                synchronized (Service.buffer) {

                    if (Service.buffer.size() != 0)
                    {
                        // SOLO PUEDE TOCAR LA VISTA EL HILO QUE LA HA CREADO, O SEA, EL PRINCIPAL.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < Service.buffer.size(); i++)
                                {
                                    showMessage(Service.buffer.get(i));
                                }

                                Service.buffer.clear();
                            }
                        });
                    }
                }


                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}