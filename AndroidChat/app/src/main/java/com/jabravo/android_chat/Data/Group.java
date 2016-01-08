package com.jabravo.android_chat.Data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jabravo.android_chat.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class Group
{

    private int id;
    private String name;
    private int admin;
    private String image;
    private List<Message> messages;
    private List<Integer> userIDs;

    public Group(int id, String name, int admin, String image, List<Integer> userIDs)
    {
        messages = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.image = image;
        this.userIDs = userIDs;
    }


    public Group(int id)
    {
        messages = new ArrayList<>();
        userIDs = new ArrayList<>();
        this.id = id;

        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase(); // Abro la base de datos

        if (db != null) // Compruebo que se ha abierto bien
        {
            String sql = "SELECT NAME_GROUP , ID_GROUP_ADMIN , IMAGE_GROUP FROM GROUPS WHERE ID_GROUP=?;";
            String[] m = {"" + id}; // CUIDADO CON ESTO.
            Cursor c = db.rawQuery(sql, m);

            while (c.moveToNext())
            {
                this.name = c.getString(0);
                this.admin = c.getInt(1);
                this.image = c.getString(2);
            }

            db.close();
        }

        createListMessages();
    }

    public List<Integer> getUserIDs()
    {
        return userIDs;
    }

    public void createListMessages()
    {

        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase(); // Abro la base de datos

        if (db != null) // Compruebo que se ha abierto bien
        {
            String sql = "SELECT MESSAGES.ID_MESSAGE , MESSAGES.DATE_MESSAGE , " +
                    " MESSAGES.TEXT , MESSAGES.IS_READ ," +
                    " SEND_MESSAGES_GROUP.ID_FRIEND " +
                    " FROM GROUPS , SEND_MESSAGES_GROUP , MESSAGES WHERE " +
                    " GROUPS.ID_GROUP=SEND_MESSAGES_GROUP.ID_GROUP AND " +
                    " MESSAGES.ID_MESSAGE = SEND_MESSAGES_GROUP.ID_MESSAGE AND " +
                    " GROUPS.ID_GROUP=? ;";

            String[] m = {"" + id}; // CUIDADO CON ESTO.
            Cursor cursor = db.rawQuery(sql, m);

            while (cursor.moveToNext())
            {
                boolean messageRead = cursor.getString(3).toLowerCase().equals("true");
                int messageID = cursor.getInt(0);
                String messageDate = cursor.getString(1);
                String messageText = cursor.getString(2);
                int messageIDSender = cursor.getInt(4);

                messages.add(new Message(messageText, messageDate, messageID, messageRead, messageIDSender));
            }

            db.close();
        }
    }


    public void addMessages(String text, String date, int id, boolean read, int sender, int receiver)
    {
        messages.add(new Message(text, date, id, read, sender, receiver));
    }

    public int getId()
    {
        return id;
    }


    public void setId(int id)
    {
        this.id = id;
    }


    public String getName()
    {
        return String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public int getAdmin()
    {
        return admin;
    }


    public void setAdmin(int admin)
    {
        this.admin = admin;
    }


    public String getImage()
    {
        return image;
    }


    public void setImage(String image)
    {
        this.image = image;
    }


    public List<Message> getMessages()
    {
        return messages;
    }


    public void setMessages(List<Message> messages)
    {
        this.messages = messages;
    }

    @Override
    public String toString()
    {
        return String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
    }
}