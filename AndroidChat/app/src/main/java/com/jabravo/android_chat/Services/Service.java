package com.jabravo.android_chat.Services;

import android.util.Log;
import com.jabravo.android_chat.Data.Message;
import com.jabravo.android_chat.Data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.concurrent.LinkedBlockingQueue;


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
    public static LinkedBlockingQueue<Message> buffer;
    private boolean run;

    public Service()
    {
        buffer = new LinkedBlockingQueue<>();
        this.id = User.getInstance().getID();
        timeSleep = 250;
        run = true;
    }


    public void setRun(boolean run)
    {
        this.run = run;
    }

    public boolean isRunning()
    {
        return run;
    }

    private String getMessage()
    {
        StringBuffer response = null;

        try
        {
            //Generar la URL
            String url = "http://146.185.155.88:8080/api/get/messages/" + id;
            //Creamos un nuevo objeto URL con la url donde pedir el JSON
            URL obj = new URL(url);
            //Creamos un objeto de conexión
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //Añadimos la cabecera
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // Enviamos la petición por POST
            con.setDoOutput(true);
            //Capturamos la respuesta del servidor
            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            //Mostramos la respuesta del servidor por consola
            Log.i("service","Response Code : " + responseCode);
            Log.i("service","Respuesta del servidor: " + response);
            System.out.println("Response Code : " + responseCode);
            System.out.println("Respuesta del servidor: " + response);

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response.toString();
    }

    @Override
    public void run()
    {
        while (run)
        {
            try
            {
                showJSON(getMessage());
                Thread.sleep(timeSleep);
            }
            catch (Exception e)  { e.printStackTrace(); }
        }
    }

    private void showJSON(String json) throws JSONException
    {
        JSONObject object = new JSONObject(json);
        JSONArray json_array = object.optJSONArray("messages");

        for (int i = 0; i < json_array.length(); i++)
        {
            JSONObject objetoJSON = json_array.getJSONObject(i);

            String text = objetoJSON.getString("TEXT");
            //String date = objetoJSON.getString("DATE");
            int idFriend = objetoJSON.getInt("ID_USER_SENDER");
            int receiver = User.getInstance().getID();
            boolean isGroup = objetoJSON.getInt("ID_GROUP") != 0;
            boolean read = true;

            System.out.println("idfriend: " + idFriend );

            try
            {
                buffer.put(new Message(text, idFriend, receiver));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
