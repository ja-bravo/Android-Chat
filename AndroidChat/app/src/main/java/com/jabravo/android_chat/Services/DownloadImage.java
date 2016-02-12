package com.jabravo.android_chat.Services;

import android.os.Environment;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Josewer on 22/12/2015.
 */
public class DownloadImage
{

    private String nameFile;
    private boolean saveOK;

    public DownloadImage(String nameFile)
    {
        this.nameFile = nameFile;
        saveOK = saveImageInSD();
    }

    private JSONObject getJsonImage()
    {
        try
        {
            String url = "http://146.185.155.88/download.php?nameFile=" + nameFile;
            System.out.println("url= " + url);
            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            con.setDoOutput(true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            in.close();

            return new JSONObject(response.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    private byte[] decodeImage()
    {

        String encodedImage = "";

        try
        {

            encodedImage = getJsonImage().getString("json");

        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }

        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);

        return imageBytes;
    }

    private boolean saveImageInSD()
    {
        String status = Environment.getExternalStorageState();

        try
        {

            byte[] imageBytes = decodeImage();

            File ruta_sd = Environment.getExternalStorageDirectory();
            File dir = new File(ruta_sd.getAbsolutePath() + "/IMAGES_CHAT_ANDROID");

            boolean ok = dir.mkdirs();

            if (ok)
            {
                File f = new File(dir, nameFile + ".jpg");

                OutputStream file = new FileOutputStream(f);


                file.write(imageBytes, 0, imageBytes.length);
                file.close();
            }

            return true;

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        return false;
    }
}
