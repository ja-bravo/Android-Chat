package com.jabravo.android_chat.Services;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jose on 01/12/2015.
 */
public class UserService
{
    public UserService()
    {

    }

    public boolean phoneExists(final String phone)
    {
        final AtomicBoolean exists = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String url = "http://146.185.155.88:8080/api/get/user/exists/" + phone;
                StringBuffer response = new StringBuffer();
                try
                {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    con.setDoOutput(true);

                    BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                    {
                        response.append(inputLine);
                    }
                    in.close();
                    exists.set(response.toString().contains("true"));
                }
                catch(Exception e) {}
            }
        });
        thread.start();

        try
        {
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return exists.get();
    }

    public JSONObject getUser(final String phone)
    {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String url = "http://146.185.155.88:8080/api/get/users/" + phone;

                try
                {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    con.setDoOutput(true);

                    BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                    {
                        response.append(inputLine);

                    }
                    in.close();
                }
                catch(Exception e) {}
            }
        });
        thread.start();

        try
        {
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        JSONObject user = null;
        try
        {
            user = new JSONObject(response.toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return user;
    }
}
