package com.jabravo.android_chat.Services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Jose on 01/12/2015.
 */
public class Sender extends AsyncTask<String, Integer, Void>
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
            String text = params[0];
            String toID = params[1];
            String fromID = params[2];

            JSONObject message = new JSONObject();
            message.put("to", toID);
            message.put("from", fromID);
            message.put("text", text);

            String json = URLEncoder.encode(message.toString(), "ISO-8859-9").replaceAll("\\+", "%20");
            URL url = new URL("http://146.185.155.88:8080/api/post/message/" + json);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            Log.i("test", String.valueOf(connection.getResponseCode()));
        }
        catch (Exception e)
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