package com.jabravo.android_chat.Services;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

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
            String message = java.net.URLEncoder.encode(params[0], "ISO-8859-9").replaceAll("\\+", "%20");
            String toID = params[1];
            String fromID = params[2];

            URL url = new URL("http://146.185.155.88:8080/api/post/message/" + toID + "&" + message + "&" + fromID);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setDoOutput(true);

            System.out.println("---------------" + connection.getResponseCode());
            Log.i("test", String.valueOf(connection.getResponseCode()));
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