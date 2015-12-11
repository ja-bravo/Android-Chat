package com.jabravo.android_chat.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    public int insertUser(String nick, final String phone) throws UnsupportedEncodingException, JSONException
    {
        final AtomicInteger ID = new AtomicInteger(-1);

        JSONObject user = new JSONObject();
        user.put("nick",nick);
        user.put("phone",phone);

        final String json = URLEncoder.encode(user.toString(),"ISO-8859-9").replaceAll("\\+","%20");
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String url = "http://146.185.155.88:8080/api/post/user/" + json;
                StringBuffer response = new StringBuffer();
                try
                {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setDoOutput(true);

                    BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                    {
                        response.append(inputLine);
                    }
                    in.close();

                    String result = response.toString();
                    int index = result.lastIndexOf("}");
                    result = result.substring(index-3);
                    result = result.trim();
                    int userID = Integer.parseInt(result.substring(0,result.length() - 1));

                    ID.set(userID);
                }
                catch(Exception e) {e.printStackTrace();}
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
        return ID.get();
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
