package com.jabravo.android_chat;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import java.io.IOException;

/**
 * Created by JoseAntonio on 26/10/2015.
 */
public class Message implements Parcelable
{
    private String text;
    private int sender;

    public Message(String text , int sender)
    {
        this.sender = sender;
        this.text = text;
    }


    public void playRingTone ()
    {

    }

    public Message(Parcel in)
    {
        this.sender = in.readInt();
        this.text = in.readString();
    }

    /*
    public Message()
    {
        this.sender = "user";
        this.text = "";
    }
    */

    public String getText()
    {
        return text;
    }

    public int getSender()
    {
        return sender;
    }

    public void setSender(int sender)
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
        dest.writeInt(sender);
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
