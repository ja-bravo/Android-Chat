package chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.DataOutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class GestionarJSON {
    
    private static final String SERVER_PATH = "http://146.185.155.88:8080/";
    
    
    static String nombre = "Jose";
    static String apellidos = "Torregrosa Luque";
    static String email = "joseTorregrosa.com";
    
    public static void main(String[] args) {
    	//sendPost();
        //Obtenemos el JSON
        String json = getJSON();
        //Lo mostramos
        showJSON(json);
    }    
    
    private static String getJSON(){
        
        StringBuffer response = null;
        
        try {
            //Generar la URL
            String url = SERVER_PATH+"api/get/users/";
            //Creamos un nuevo objeto URL con la url donde pedir el JSON
            URL obj = new URL(url);
            //Creamos un objeto de conexión
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //Añadimos la cabecera
            con.setRequestMethod("GET");
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
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            //Mostramos la respuesta del servidor por consola
            System.out.println("Respuesta del servidor: "+response);
            System.out.println();
            //cerramos la conexión
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return response.toString();
    }
    
    
    
    public static void sendPost(){
        //Creamos un objeto JSON
        JSONObject jsonObj = new JSONObject();
        //Añadimos el nombre, apellidos y email del usuario
        jsonObj.put("nombre",nombre);
        jsonObj.put("apellidos", apellidos);
        jsonObj.put("email", email);
        //Creamos una lista para almacenar el JSON
        List  l = new LinkedList();
        l.addAll(Arrays.asList(jsonObj));
        //Generamos el String JSON
        String jsonString = JSONValue.toJSONString(l);
        System.out.println("JSON GENERADO:");
        System.out.println(jsonString);
        System.out.println("");
        
        try {
            //Codificar el json a URL
            jsonString = URLEncoder.encode(jsonString, "UTF-8");
            //Generar la URL
            String url = SERVER_PATH+"listenPost.php";
            //Creamos un nuevo objeto URL con la url donde queremos enviar el JSON
            URL obj = new URL(url);
            //Creamos un objeto de conexión
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //Añadimos la cabecera
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            //Creamos los parametros para enviar
            String urlParameters = "json="+jsonString;
            // Enviamos los datos por POST
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            //Capturamos la respuesta del servidor
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            //Mostramos la respuesta del servidor por consola
            System.out.println(response);
            //cerramos la conexión
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    private static void showJSON(String json){
        System.out.println("INFORMACIÓN OBTENIDA DE LA BASE DE DATOS:");
        //Crear un Objeto JSON a partir del string JSON
        JSONObject jsonObject =(JSONObject)JSONValue.parse(json.toString());
        System.out.println(jsonObject);
        
        //Convertir el objeto JSON en un array. Recojo una lista de users.
        JSONArray array=(JSONArray)jsonObject.get("users");
     
        //Iterar el array y extraer la información
        for(int i=0;i<array.size();i++) {
        	
            JSONObject row =(JSONObject)array.get(i);
            String nombre = row.get("NICK").toString();
      
            System.out.println("Nombre: " + nombre + "|| Apellidos: " + apellidos + "|| Email: " + email);
        }
    }  
}

