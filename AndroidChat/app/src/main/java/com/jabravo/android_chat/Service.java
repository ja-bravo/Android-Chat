package com.jabravo.android_chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josewer on 20/11/2015.
 */
public class Service implements Runnable{

    private int id;

    public Service (int id)
    {
        this.id = 2;
    }

    private String getJMessage(){

        StringBuffer response = null;

        try {
            //Generar la URL
            String url ="http://146.185.155.88:8080/api/get/messages/"+id;
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
            //System.out.println("\nSending 'POST' request to URL : " + url);
           // System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            //Mostramos la respuesta del servidor por consola
            //System.out.println("Respuesta del servidor: "+response);
            //System.out.println();
            //cerramos la conexión
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    @Override
    public void run() {
        while (true)
        {
            try {
                showJSON(getJMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(250);
            }catch (Exception e) {};

        }
    }

    private  void showJSON(String json) throws JSONException {

        List<MessageData> mensajes = new ArrayList<>(); //inicializamos la lista donde almacenaremos los objetos Fruta

        JSONObject object = new JSONObject(json);
        JSONArray json_array = object.optJSONArray("messages");

        for (int i = 0; i < json_array.length(); i++) {

            JSONObject objetoJSON = json_array.getJSONObject(i);

            String text = objetoJSON.getString("TEXT");
            String date = "hoy";
            boolean read = false;
            int sender = objetoJSON.getInt("ID_USER_SENDER");

            mensajes.add(new MessageData(text , date , id , read , sender , id )); //creamos un objeto Fruta y lo insertamos en la lista

            if (!text.equals(""))

            System.out.println(text);
        }
    }
}
