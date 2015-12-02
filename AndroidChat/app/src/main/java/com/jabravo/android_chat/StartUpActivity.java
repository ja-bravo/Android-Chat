package com.jabravo.android_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jabravo.android_chat.Services.UserService;

public class StartUpActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText phone1, phone2, nick;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        phone1 = (EditText) findViewById(R.id.phone1);
        phone2 = (EditText) findViewById(R.id.phone2);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(isValid(phone1.getText().toString()) &&
           isValid(phone2.getText().toString()))
        {
            UserService service = new UserService();
            if(service.phoneExists(phone1.getText().toString()))
            {
                loadUserData();
            }
            else
            {
                // Registrar usuario
            }
            finish();
        }
        else
        {
            Toast.makeText(this,"Invalid number.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed()
    {

    }

    private void loadUserData()
    {

    }

    private boolean isValid(String number)
    {
        return number.length() == 9 && number.matches("[0-9]*");
    }
}
