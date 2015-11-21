package com.jabravo.android_chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by JoseAntonio on 27/10/2015.
 */
public class MessageList extends ArrayList<Message> implements Parcelable
{

    public MessageList()
    {

    }

    public MessageList(Parcel in)
    {
        this();
        readFromParcel(in);
    }


    // This is so we can put a Message in a bundle.
    private void readFromParcel(Parcel in)
    {
        this.clear();

        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            Message message = new Message(in.readString(),in.readInt());
            this.add(message);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        int size = this.size();

        dest.writeInt(size);

        for (int i = 0; i < size; i++)
        {
            Message message = this.get(i);

            dest.writeInt(message.getSender());
            dest.writeString(message.getText());
        }
    }

    public static final Parcelable.Creator<MessageList> CREATOR = new Parcelable.Creator<MessageList>()
    {
        public MessageList createFromParcel(Parcel in)
        {
            return new MessageList(in);
        }

        public MessageList[] newArray(int size)
        {
            return new MessageList[size];
        }
    };
}