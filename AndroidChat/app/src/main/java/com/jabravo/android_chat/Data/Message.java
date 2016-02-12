package com.jabravo.android_chat.Data;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jose Antonio on 26/10/2015.
 */
public class Message implements Parcelable
{
    private String text;
    private String date;
    private int id;
    private boolean read;
    private int idFriend;
    private int receiver;
    private boolean isGroup;
    private String phone;

    public Message(String text, String date, int id, boolean read, int idFriend, int receiver)
    {
        this.text = text;

        this.date = date;
        this.id = id;
        this.read = read;
        this.idFriend = idFriend;
        this.receiver = receiver;
        phone = "";
    }

    public Message(String text, String date, int id, boolean read, int sender)
    {
        this.text = text;
        this.date = date;
        this.id = id;
        this.read = read;
        this.idFriend = sender;
        phone = "";
    }

    public Message(String text, String date, boolean read, int idFriend, int receiver, boolean isGroup)
    {
        this.isGroup = isGroup;
        this.text = text;
        this.date = date;
        this.read = read;
        this.idFriend = idFriend;
        this.receiver = receiver;
    }


    public Message(String text, int idFriend, int receiver, boolean isGroup, String date)
    {
        this.text = text;
        this.date = date;
        this.id = -1;
        this.read = false;
        this.idFriend = idFriend;
        this.receiver = receiver;
        this.isGroup = isGroup;
        phone = "";
    }

    public Message()
    {
        this.text = "";
        this.date = "";
        this.id = -1;
        this.read = false;
        this.idFriend = -1;
        this.receiver = -1;
        phone = "";
    }

    public Message(Parcel in)
    {
        text = in.readString();
        date = in.readString();
        id = in.readInt();
        read = in.readInt() == 1;
        idFriend = in.readInt();
        receiver = in.readInt();
    }


    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getText()
    {
        return text;
    }

    public int getIdFriend()
    {
        return idFriend;
    }

    public void setIdFriend(int idFriend)
    {
        this.idFriend = idFriend;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public int getId()
    {
        return id;
    }

    public boolean getIsGroup()
    {
        return isGroup;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public boolean isRead()
    {
        return read;
    }

    public void setRead(boolean read)
    {
        this.read = read;
    }

    public int getReceiver()
    {
        return receiver;
    }

    public void setReceiver(int receiver)
    {
        this.receiver = receiver;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(text);
        dest.writeString(date);
        dest.writeInt(id);
        dest.writeInt(read ? 1 : 0);
        dest.writeInt(idFriend);
        dest.writeInt(receiver);
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>()
    {
        public Message createFromParcel(Parcel in)
        {
            return new Message(in);
        }

        public Message[] newArray(int size)
        {
            return new Message[size];
        }
    };
}
