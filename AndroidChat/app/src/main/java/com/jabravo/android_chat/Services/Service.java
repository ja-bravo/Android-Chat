package com.jabravo.android_chat.Services;

import com.jabravo.android_chat.Data.Message;
import com.jabravo.android_chat.Data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Josewer on 16/12/2015.
 */
/*
    Clase que comprueba si hay mensajes en el servidor para el usuario actual, y si los hay, los mete
    en una cola.
    Luego el runnable receiver de la clase chatActivity, se encargara de ir comprobando si hay mensajes en la cola (buffer)
    y si los hay, los muestra por la activity.
 */

public class Service implements Runnable
{

    private int id;
    private int timeSleep;
    private List<Message> buffer;


    public Service()
    {
        buffer = Collections.synchronizedList(new ArrayList<Message>());
        this.id = User.getInstance().getID();
        timeSleep = 250;

    }

    public List<Message> getBuffer()
    {
        return buffer;
    }


    private String getMessage()
    {
        StringBuffer response = null;

        try
        {
            String url = "http://146.185.155.88:8080/api/get/messages/" + id;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response.toString();
    }

    public void setTimeSleep(int timeSleep)
    {
        this.timeSleep = timeSleep;
    }


    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                showJSON(getMessage());
                Thread.sleep(timeSleep);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void showJSON(String json) throws JSONException
    {
        JSONObject object = new JSONObject(json);
        JSONArray json_array = object.optJSONArray("messages");

        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormat = df.format(date);

        for (int i = 0; i < json_array.length(); i++)
        {
            JSONObject messageJSON = json_array.getJSONObject(i);

            String text = messageJSON.getString("TEXT");

            int idFriend = messageJSON.getInt("ID_USER_SENDER");
            int idGroup = messageJSON.getInt("ID_GROUP");

            boolean isGroup = idGroup != 0;

            int receiver = isGroup ? idGroup : User.getInstance().getID();

            System.out.println("Es grupo Service: " + isGroup);

            Message message = new Message(text, idFriend, receiver, isGroup, dateFormat);

            message.setPhone(messageJSON.getString("PHONE"));
            buffer.add(message);
        }
    }
}