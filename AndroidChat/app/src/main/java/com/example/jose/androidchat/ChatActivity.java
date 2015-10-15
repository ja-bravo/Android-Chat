package com.example.jose.androidchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{
    LinearLayout layout;
    Button sendButton;
    EditText text;
    ScrollView scrollView;

    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout);
        sendButton = (Button) findViewById(R.id.button);
        sendButton.setOnClickListener(this);

        text = (EditText) findViewById(R.id.editText);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        counter = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        TextView t = new TextView(this);
        String newText = text.getText().toString();

        t.setText(newText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.gravity = counter % 2 == 0? Gravity.RIGHT : Gravity.LEFT;
        params.setMargins(0,10,0,0);
        t.setLayoutParams(params);

        t.setPadding(20, 10, 0, 20);
        t.setBackgroundResource(R.drawable.rounded_corner);

        layout.addView(t);

        text.setText("");

        scrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        counter++;
    }
}
