package com.jabravo.android_chat.Services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josewer on 02/12/2015.
 */
public class SenderPhones {


    private  List<AgendaData> l;


    public SenderPhones ()
    {
        l = new ArrayList<>();
    }


    public void addPhone (String name , String phone)
    {
        l.add(new AgendaData(name , phone));
    }


    public JSONObject getListJSON () throws JSONException {

        JSONObject response = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < l.size(); i++)
        {
            JSONObject dataPhone = new JSONObject();

            dataPhone.put("NAME", l.get(i).getName());
            dataPhone.put("PHONE", l.get(i).getPhoneNumber());

            jsonArray.put(dataPhone);
        }

        response.put("DATA", jsonArray);
        return response;
    }


    public class AgendaData {

        private String name;
        private String phoneNumber;

        public AgendaData (String name , String phoneNumber)
        {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setName(String name) {
            this.name = name;
        }
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
                String message = java.net.URLEncoder.encode(params[0], "ISO-8859-9").replaceAll("\\+", "%20");


                URL url = new URL("http://146.185.155.88:8080/api/post/message/" + getListJSON());

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


