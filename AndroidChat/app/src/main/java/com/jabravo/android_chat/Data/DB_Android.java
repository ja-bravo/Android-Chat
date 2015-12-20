package com.jabravo.android_chat.Data;

/**
 * Created by Josewer on 16/11/2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Josewer on 15/11/2015.
 */
public class DB_Android extends SQLiteOpenHelper
{

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String createTableFriends =
                "CREATE TABLE FRIENDS " +
                        " ( " +
                        " ID_FRIEND INT NOT NULL, " +
                        " NICK VARCHAR(20) NOT NULL, " +
                        " STATUS VARCHAR (20), " +
                        " PHONE INT (10) NOT NULL, " +
                        " IMAGE VARCHAR (100), " +

                        " CONSTRAINT UQ_PHONE_FRIENDS UNIQUE (PHONE), " +
                        " CONSTRAINT PK_FRIENDS PRIMARY KEY (ID_FRIEND) " +
                        " );";

        String createTableGroups =
                "CREATE TABLE GROUPS " +
                        " ( " +
                        " ID_GROUP INT NOT NULL, " +
                        " NAME_GROUP VARCHAR (20) NOT NULL, " +
                        " ID_GROUP_ADMIN INT (10), " +
                        " IMAGE_GROUP VARCHAR (60), " +

                        " CONSTRAINT PK_GROUPS PRIMARY KEY (ID_GROUP) " +
                        " );";


        String createTableMessages =
                "CREATE TABLE MESSAGES " +
                        " ( " +
                        " ID_MESSAGE INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " DATE_MESSAGE DATE, " +
                        " TEXT VARCHAR (100) NOT NULL, " +
                        " IS_READ VARCHAR (20) DEFAULT 'FALSE' " +
                        " );";


        String createTableSendMessagesPrivate =
                "CREATE TABLE SEND_MESSAGES_PRIVATE " +
                        " ( " +
                        " ID_MESSAGE INT NOT NULL ," +
                        " ID_FRIEND INT NOT NULL," +
                        " ID_RECEIVER INT NOT NULL, " +

                        " CONSTRAINT PK_SEND_MESSAGES_PRIVATE PRIMARY KEY (ID_MESSAGE , ID_FRIEND ), " +

                        " CONSTRAINT FK_SEND_MESSAGES_PRIVATE_MESSAGES FOREIGN KEY (ID_MESSAGE) " +
                        " REFERENCES MESSAGES (ID_MESSAGE) ," +

                        " CONSTRAINT FK__SEND_MESSAGES_PRIVATE_FRIENDS FOREIGN KEY (ID_FRIEND) " +
                        " REFERENCES FRIENDS (ID_FRIEND) " +
                        " );";


        String createTableSendMessagesGroup =
                " CREATE TABLE SEND_MESSAGES_GROUP " +
                        " ( " +
                        " ID_MESSAGE INT NOT NULL , " +
                        " ID_GROUP INT NOT NULL," +
                        " ID_FRIEND INT NOT NULL, " +

                        " CONSTRAINT PK_SEND_MESSAGES_GROUP PRIMARY KEY (ID_MESSAGE , ID_GROUP ), " +

                        " CONSTRAINT FK_SEND_MESSAGES_GROUP_MESSAGES FOREIGN KEY (ID_MESSAGE) " +
                        " REFERENCES MESSAGES (ID_MESSAGE), " +

                        " CONSTRAINT FK__SEND_MESSAGES_GROUP_GROUP FOREIGN KEY (ID_GROUP) " +
                        " REFERENCES GROUPS (ID_GROUP) " +
                        " ); ";


        db.execSQL(createTableFriends);
        db.execSQL(createTableGroups);
        db.execSQL(createTableMessages);
        db.execSQL(createTableSendMessagesPrivate);
        db.execSQL(createTableSendMessagesGroup);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public DB_Android(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }
}

