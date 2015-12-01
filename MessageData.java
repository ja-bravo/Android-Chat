package com.jabravo.android_chat;

/**
 * Created by Josewer on 16/11/2015.
 */
public class MessageData {

    private String text;
    private String date;
    private int id;
    private boolean read;
    private int sender;
    private int receiver;

    public MessageData(String text , String date , int id , boolean read , int sender , int receiver)
    {
        this.text = text;
        this.date = date;
        this.id = id;
        this.read = read;
        this.sender = sender;
        this.receiver = receiver;
    }

    public MessageData(String text , String date , int id ,
                       boolean read , int sender )
    {
        this.text = text;
        this.date = date;
        this.id = id;
        this.read = read;
        this.sender = sender;
    }


    public int getReceiver() {
        return receiver;
    }


    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public boolean isRead() {
        return read;
    }


    public void setRead(boolean read) {
        this.read = read;
    }


    public int getSender() {
        return sender;
    }


    public void setSender(int sender) {
        this.sender = sender;
    }
}
