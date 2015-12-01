package com.jabravo.android_chat.Services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jose on 01/12/2015.
 */
public class UserService
{
    public UserService()
    {

    }

    public boolean phoneExists(String phone)
    {
        String url = "http://146.185.155.88:8080/api/get/user/exists/" + phone;
        StringBuffer response = new StringBuffer();
        try
        {
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
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            //Mostramos la respuesta del servidor por consola
            System.out.println("Respuesta del servidor: " + response);
            System.out.println();

            in.close();
        }
        catch(Exception e) {}
    }
}
