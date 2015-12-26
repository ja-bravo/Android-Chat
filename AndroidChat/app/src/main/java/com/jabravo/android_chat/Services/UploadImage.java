package com.jabravo.android_chat.Services;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.R;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Josewer on 21/12/2015.
 */

public class UploadImage extends AsyncTask<String, Void, Boolean>
{

    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    private Context context;
    private String nameFile;

    public UploadImage(Context context, String nameFile)
    {
        this.context = context;
        this.nameFile = nameFile;
        builder = new AlertDialog.Builder(context);
    }


    private void updateImageDB()
    {
        try
        {

            JSONObject image = new JSONObject();
            image.put("ID", User.getInstance().getID());
            image.put("PATH", nameFile);

            String json = URLEncoder.encode(image.toString(), "ISO-8859-9").replaceAll("\\+", "%20");
            URL url = new URL("http://146.185.155.88:8080/api/post/user/update/" + json);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            Log.i("test", String.valueOf(connection.getResponseCode()));
        }
        catch (Exception e)
        {
            Log.i("test", String.valueOf(e.toString()) + " pete");
        }
    }

    // Antes de comenzar la tarea muestra el progressDialog
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.please_wait), context.getResources().getString(R.string.uploading));
    }

    @Override
    protected Boolean doInBackground(String... params)
    {

        String encodedImage = params[0];

        // Subo un json con la imagen y la id del usuario para que se genere en el servidor.
        try
        {
            JSONObject image = new JSONObject();
            image.put("json", encodedImage);

            // Para evitar errores al subir
            String json = image.toString().replaceAll("\\+", "SUMAR");

            URL url = new URL("http://146.185.155.88/uploadImage.php/");

            int idUser = User.getInstance().getID();

            String urlParameters = "nameFile=" + nameFile + "&json=" + json;

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            connection.setDoOutput(true);

            // Añado los parametros, para que sea envio Post... si no seria GET..
            // NOTA: Si lo envias por GET peta, porque no te dejan subir tantos caracteres por get.
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            Log.i("test", String.valueOf(connection.getResponseCode()));


            updateImageDB();

            return true;
        }
        catch (Exception e)
        {
            Log.i("test", String.valueOf(e.toString()) + " pete");
        }

        return false;

    }

    //Cuando se termina de ejecutar, cierra el progressDialog y avisa

    @Override
    protected void onPostExecute(Boolean result)
    {
        progressDialog.dismiss();
        if (result)
        {
            builder.setMessage(context.getResources().getString(R.string.image_success))
                    .setTitle("Android chat")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    }).create().show();
        }
        else
        {
            builder.setMessage(context.getResources().getString(R.string.image_fail))
                    .setTitle("Android chat")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    }).create().show();
        }
    }
}
