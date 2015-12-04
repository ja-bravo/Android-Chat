package com.jabravo.android_chat.Services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Josewer on 02/12/2015.
 */
public class SenderPhones
{


    private Set<AgendaData> agenda;


    public SenderPhones()
    {
        agenda = new TreeSet<>();
    }


    public void addPhone(String phone)
    {
        phone = phone.replace("+34","");
        while(phone.contains(" "))
        {
            phone = phone.replace(" ", "");
        }
        agenda.add(new AgendaData("", phone));
    }


    public JSONObject getListJSON() throws JSONException
    {

        JSONObject response = new JSONObject();
        JSONArray friends = new JSONArray();

        for ( AgendaData data : agenda ) {

            JSONObject friend = new JSONObject();
            friend.put("PHONE", data.getPhoneNumber());

            friends.put(friend);
        }

        response.put("Friends", friends);

        return response;
    }


    public class AgendaData implements Comparable<AgendaData>
    {

        private String name;
        private String phoneNumber;

        public AgendaData(String name, String phoneNumber)
        {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getName()
        {
            return name;
        }

        public String getPhoneNumber()
        {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber)
        {
            this.phoneNumber = phoneNumber;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        @Override
        public int compareTo(AgendaData another)
        {
            return another.getPhoneNumber().compareTo(phoneNumber);
        }
    }

    public void send()
    {
        Sender sender = new Sender();
        sender.execute();
    }

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
                URL url = new URL("http://146.185.155.88:8080/api/get/friends/" + getListJSON());

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

}


