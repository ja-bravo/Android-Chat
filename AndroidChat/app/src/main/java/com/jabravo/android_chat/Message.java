package com.jabravo.android_chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JoseAntonio on 26/10/2015.
 */
public class Message implements Parcelable
{
    private String text;
    private String sender;

    public Message(String sender, String text)
    {
        this.sender = sender;
        this.text = text;
    }

    public Message(Parcel in)
    {
        this.sender = in.readString();
        this.text = in.readString();
    }

    public Message()
    {
        this.sender = "user";
        this.text = "";
    }

    public String getText()
    {
        return text;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    // This is so we can put a Message in a bundle.
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(sender);
        dest.writeString(text);
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
