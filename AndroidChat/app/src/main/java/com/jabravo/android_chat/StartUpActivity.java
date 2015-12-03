package com.jabravo.android_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.Services.UserService;
import org.json.JSONException;
import org.json.JSONObject;

public class StartUpActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private EditText phone1, phone2, nick;
    private TextView textNick, textRepeatPhone, textPhone, textWelcome;
    private Button registerButton;
    private UserService service;
    private Switch option;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        phone1 = (EditText) findViewById(R.id.phone1);
        phone2 = (EditText) findViewById(R.id.phone2);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        textNick = (TextView) findViewById(R.id.textNick);
        textRepeatPhone = (TextView) findViewById(R.id.textRepeatPhone);
        textPhone = (TextView) findViewById(R.id.textPhone);
        textWelcome = (TextView) findViewById(R.id.textWelcome);

        option = (Switch) findViewById(R.id.switch1);
        option.setOnCheckedChangeListener(this);
        service = new UserService();
    }

    @Override
    public void onClick(View v)
    {
        if(isValid(phone1.getText().toString()) &&
           isValid(phone2.getText().toString()))
        {
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
        try
        {
            JSONObject userObject = service.getUser(phone1.getText().toString()).getJSONObject("user");
            User user = User.getInstance();
            user.setID(userObject.getInt("ID"));
            user.setNick(userObject.getString("NICK"));
            user.setNumber(String.valueOf(userObject.getInt("PHONE")));
            user.setStatus(userObject.getString("STATUS"));

            user.updatePreferences();

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private boolean isValid(String number)
    {
        return number.length() == 9 && number.matches("[0-9]*");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if(isChecked)
        {
            textNick.setVisibility(View.INVISIBLE);
            textRepeatPhone.setVisibility(View.INVISIBLE);
            textPhone.setVisibility(View.INVISIBLE);
            textWelcome.setVisibility(View.INVISIBLE);
        }
        else
        {
            textNick.setVisibility(View.VISIBLE);
            textRepeatPhone.setVisibility(View.VISIBLE);
            textPhone.setVisibility(View.VISIBLE);
            textWelcome.setVisibility(View.VISIBLE);
        }
    }
}
