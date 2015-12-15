package com.jabravo.android_chat.Data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jabravo.android_chat.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josewer on 15/12/2015.
 */
public class Actions_DB {

    public static void insertFriend (String id, String phone, String status, String image, String nick)
    {
        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase();

        if (db != null)
        {
            String sql = "INSERT INTO FRIENDS (ID_FRIEND , NICK , STATUS , PHONE, IMAGE ) " +
                    "VALUES ( '<ID_FRIEND>' , '<NICK>' , '<STATUS>' , '<PHONE>' , '<IMAGE>' );";

            sql = sql
                    .replace("<ID_FRIEND>" , id)
                    .replace("<NICK>", nick)
                    .replace("<STATUS>" , status)
                    .replace("<PHONE>" , phone)
                    .replace("<IMAGE>" , image);

            db.execSQL(sql);

            db.close();
        }
    }


    private static int insertMessage (String text, String date , boolean read)
    {
        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase();
        int id = -1;

        if (db != null)
        {
            String sql = "INSERT INTO MESSAGES ( DATE_MESSAGE , TEXT , IS_READ) " +
                    "VALUES ( '<DATE_MESSAGE>' , '<TEXT>' , '<IS_READ>');";

            sql = sql
                    .replace("<DATE_MESSAGE>", String.valueOf(date))
                    .replace("<TEXT>", String.valueOf(text))
                    .replace("<IS_READ>", String.valueOf(read));

            db.execSQL(sql);

            sql = "SELECT MAX(ID_MESSAGE) AS ID FROM MESSAGES;";
            String [] m = {};
            Cursor c = db.rawQuery(sql , m);

            String t = "";
            c.moveToNext();

            id = c.getInt(0);

            c.close();
            db.close();
        }

        return id;
    }


    public static void insertMessagePrivate (String text, String date , boolean read
            , int idFriend , int idReceiver)
    {
        int idMessage = insertMessage(text, date, read);

        if (idMessage != -1) {
            SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase();

            if (db != null) {
                String sql = "INSERT INTO SEND_MESSAGES_PRIVATE (ID_MESSAGE , ID_FRIEND , ID_RECEIVER ) " +
                        "VALUES ( <ID_MESSAGE> , <ID_FRIEND> , <ID_RECEIVER> );";

                sql = sql
                        .replace("<ID_MESSAGE>", String.valueOf(idMessage))
                        .replace("<ID_FRIEND>", String.valueOf(idFriend))
                        .replace("<ID_RECEIVER>", String.valueOf(idReceiver));

                db.execSQL(sql);

                db.close();
            }
        }
    }


    public static void insertMessageGroup (String text, String date , boolean read
            , int idGroup , int idFriend)
    {
        int idMessage = insertMessage(text, date, read);

        if (idMessage != -1) {
            SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase();

            if (db != null) {
                String sql = "INSERT INTO SEND_MESSAGES_GROUP (ID_MESSAGE , ID_GROUP , ID_FRIEND ) " +
                        "VALUES ( <ID_MESSAGE> , <ID_GROUP> , <ID_FRIEND> );";

                sql = sql
                        .replace("<ID_MESSAGE>", String.valueOf(idMessage))
                        .replace("<ID_GROUP>", String.valueOf(idGroup))
                        .replace("<ID_FRIEND>", String.valueOf(idFriend));

                db.execSQL(sql);

                db.close();
            }
        }
    }


    public static List<Friend> getAllFriends ()
    {
        List<Friend> l = new ArrayList<>();

        SQLiteDatabase db = MainActivity.dataBase.getWritableDatabase();

        if (db != null)
        {
            String sql = "SELECT ID_FRIEND , PHONE , STATUS , IMAGE , NICK  FROM FRIENDS;";
            String[] m = {};
            Cursor cursor = db.rawQuery(sql, m);

            while (cursor.moveToNext())
            {
                String id = cursor.getString(0);
                String phone = cursor.getString(1);
                String status = cursor.getString(2);
                String image = cursor.getString(3);
                String nick = cursor.getString(4);

                l.add(new Friend( id, phone, status, image, nick));
            }

            cursor.close();
            db.close();
        }
        return l;
    }
}