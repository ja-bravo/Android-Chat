package com.jabravo.android_chat;

/**
 * Created by Josewer on 16/11/2015.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class Friend {

    private String phone;
    private String status;
    private String image;
    private int id;
    private String nick;
    private List<MessageData> listMessages;
    private Context context; // puede que falle esto

    public Friend(String phone , String status , String image ,
                  int id , String nick) {

        this.phone = phone;
        this.id = id;
        this.status = status;
        this.nick = nick;
        this.image = image;
    }

    public Friend( int id )
    {
        this.id = id;

        DB_Android dataBase = new DB_Android ( context , "Data Base" , null , 1); // El 1 es la version.
        // Mientras no lo cambie no se volvera a crear ni actulizar la base de datos.

        SQLiteDatabase db = dataBase.getWritableDatabase(); // Abro la base de datos

        if (db != null) // Compruebo que se ha abierto bien
        {
            String sql = "SELECT PHONE , STATUS , IMAGE , NICK  FROM FRIENDS WHERE ID_FRIEND=?;";
            String [] m = {""+ id}; // CUIDADO CON ESTO.
            Cursor c = db.rawQuery(sql , m);

            while (c.moveToNext())
            {
                this.phone = c.getString(0);
                this.status = c.getString(1);
                this.image= c.getString(2);
                this.nick = c.getString(3);
            }

            db.close();
        }

        createListMessages ();
    }


    public void createListMessages ()
    {
        listMessages = new ArrayList<MessageData>();

        DB_Android dataBase = new DB_Android ( context , "Data Base" , null , 1);

        SQLiteDatabase db = dataBase.getWritableDatabase();

        if (db != null) // Compruebo que se ha abierto bien
        {
            String sql = "SELECT MESSAGES.ID_MESSAGE , MESSAGES.DATE_MESSAGE , " +
                    " MESSAGES.TEXT , MESSAGES.IS_READ ," +
                    " ID_RECEIVER , SEND_MESSAGES_PRIVATE.ID_FRIEND " +
                    " FROM FRIENDS , SEND_MESSAGES_PRIVATE , MESSAGES WHERE " +
                    " (FRIENDS.ID_FRIEND=SEND_MESSAGES_PRIVATE.ID_FRIEND OR" +
                    " FRIENDS.ID_FRIEND=ID_RECEIVER) AND " +
                    " MESSAGES.ID_MESSAGE = SEND_MESSAGES_PRIVATE.ID_MESSAGE AND " +
                    " FRIENDS.ID_FRIEND=? ;";

            String [] m = {""+ id}; // CUIDADO CON ESTO.
            Cursor c = db.rawQuery(sql , m);

            while (c.moveToNext())
            {
                boolean r;

                if (c.getString(3).toLowerCase().equals("true"))
                    r = true;
                else
                    r = false;

                listMessages.add(new MessageData (c.getString(2), c.getString(1),
                        c.getInt(0) , r , c.getInt(4) , c.getInt(5)));
            }

            db.close();
        }
    }


    public void addMessages (String text , String date, int id , boolean read , int sender , int receiver )
    {
        listMessages.add(new MessageData (text , date , id , read , sender ,receiver));
    }

    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getNick() {
        return nick;
    }


    public void setNick(String nick) {
        this.nick = nick;
    }


    public List getListMessages() {
        return listMessages;
    }


    public void setListMessages(List listMessages) {
        this.listMessages = listMessages;
    }
}
