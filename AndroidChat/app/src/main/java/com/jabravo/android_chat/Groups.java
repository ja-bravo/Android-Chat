package com.jabravo.android_chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josewer on 16/11/2015.
 */
public class Groups {

    private int id;
    private String name;
    private int admin;
    private String image;

    private List<MessageData> listMessages;


    public Groups(int id, String name, int admin, String image) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.image = image;
    }


    public Groups(int id) {
        this.id = id;

        // TODO
    }

    public void createListMessages ()
    {
        // TODO
        listMessages = new ArrayList<MessageData>();
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
