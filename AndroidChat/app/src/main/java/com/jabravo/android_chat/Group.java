package com.jabravo.android_chat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class Group {

    private int id;
    private String name;
    private int admin;
    private String image;

    private List<MessageData> listMessages;


    public Group(int id , String name , int admin , String image) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.image = image;
    }


    public Group(int id) {

        this.id = id;

        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase(); // Abro la base de datos

        if (db != null) // Compruebo que se ha abierto bien
        {
            String sql = "SELECT NAME_GROUP , ID_GROUP_ADMIN , IMAGE_GROUP FROM GROUPS WHERE ID_GROUP=?;";
            String [] m = {""+ id}; // CUIDADO CON ESTO.
            Cursor c = db.rawQuery(sql , m);

            while (c.moveToNext())
            {
                this.name = c.getString(0);
                this.admin = c.getInt(1);
                this.image = c.getString(2);
            }

            db.close();
        }

        createListMessages ();
    }

    public void createListMessages () {

        listMessages = new ArrayList<MessageData>();

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
            Cursor c = db.rawQuery(sql, m);

            while (c.moveToNext()) {
                boolean r;

                if (c.getString(3).toLowerCase().equals("true"))
                    r = true;
                else
                    r = false;

                listMessages.add(new MessageData(c.getString(2), c.getString(1),
                        c.getInt(0), r, c.getInt(4)));
            }

            db.close();
        }
    }


    public void addMessages (String text , String date, int id , boolean read , int sender , int receiver )
    {
        listMessages.add(new MessageData (text , date , id , read , sender ,receiver));
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getAdmin() {
        return admin;
    }


    public void setAdmin(int admin) {
        this.admin = admin;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public List<MessageData> getListMessages() {
        return listMessages;
    }


    public void setListMessages(List<MessageData> listMessages) {
        this.listMessages = listMessages;
    }

}