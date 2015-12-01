package com.jabravo.android_chat.Data;

/**
 * Created by Jose on 20/11/2015.
 */
public class User
{
    private static User user;
    private int ID;
    private String number;
    private String status;
    private String nick;

    private User(int ID, String number, String status, String nick)
    {
        this.ID = ID;
        this.number = number;
        this.status = status;
        this.nick = nick;
    }

    public static User getInstance()
    {
        if(user == null)
        {
            user = new User(-1,"1","1","1");
        }

        return user;
    }

    public void setID(int id)
    {
        this.ID = id;
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
