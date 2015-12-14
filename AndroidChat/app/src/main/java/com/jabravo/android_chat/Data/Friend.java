package com.jabravo.android_chat.Data;

/**
 * Created by Josewer on 16/11/2015.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jabravo.android_chat.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class Friend
{

    private String phone;
    private String status;
    private String image;
    private int id;
    private String nick;
    private List<Message> messages;

    public Friend(String id, String phone, String status, String image, String nick)
    {
        messages = new ArrayList<>();
        this.phone = phone;
        this.id = Integer.parseInt(id);
        this.status = status;
        this.nick = nick;
        this.image = image;
    }

    public Friend(String phone)
    {
        messages = new ArrayList<>();
        this.phone = phone;
        this.id = -1;
        this.status = "";
        this.nick = "";
        this.image = "";
    }

    public Friend(int id)
    {
        messages = new ArrayList<>();
        this.id = id;

        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase(); // Abro la base de datos

        if (db != null) // Compruebo que se ha abierto bien
        {
            String sql = "SELECT PHONE , STATUS , IMAGE , NICK  FROM FRIENDS WHERE ID_FRIEND=?;";
            String[] m = {"" + id}; // CUIDADO CON ESTO.
            Cursor cursor = db.rawQuery(sql, m);

            while (cursor.moveToNext())
            {
                this.phone = cursor.getString(0);
                this.status = cursor.getString(1);
                this.image = cursor.getString(2);
                this.nick = cursor.getString(3);
            }

            db.close();
        }

        createListMessages();
    }


    public void createListMessages()
    {
        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase();

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

            String[] m = {"" + id}; // CUIDADO CON ESTO.
            Cursor cursor = db.rawQuery(sql, m);

            while (cursor.moveToNext())
            {
                boolean r;

                if (cursor.getString(3).toLowerCase().equals("true"))
                    r = true;
                else
                    r = false;

                messages.add(new Message(cursor.getString(2), cursor.getString(1),
                        cursor.getInt(0), r, cursor.getInt(4), cursor.getInt(5)));
            }

            db.close();
        }
    }


    public void addMessages(String text, String date, int id, boolean read, int sender, int receiver)
    {
        messages.add(new Message(text, date, id, read, sender, receiver));
    }

    public String getPhone()
    {
        return phone;
    }


    public void setPhone(String phone)
    {
        this.phone = phone;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus(String status)
    {
        this.status = status;
    }


    public String getImage()
    {
        return image;
    }


    public void setImage(String image)
    {
        this.image = image;
    }


    public int getId()
    {
        return id;
    }


    public void setId(int id)
    {
        this.id = id;
    }


    public String getNick()
    {
        return nick;
    }


    public void setNick(String nick)
    {
        this.nick = nick;
    }


    public List getMessages()
    {
        return messages;
    }

    public void setMessages(List messages)
    {
        this.messages = messages;
    }

    @Override
    public String toString()
    {
        return String.valueOf(nick.charAt(0)).toUpperCase() + nick.substring(1).toLowerCase();
    }
}
