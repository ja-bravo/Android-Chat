package com.jabravo.android_chat;

/**
 * Created by JoseAntonio on 26/10/2015.
 */
public class Message
{
    private String text;
    private String sender;

    public Message(String sender, String text)
    {
        this.sender = sender;
        this.text = text;
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
}
