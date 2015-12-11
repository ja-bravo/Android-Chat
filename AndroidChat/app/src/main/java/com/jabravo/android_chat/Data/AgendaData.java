package com.jabravo.android_chat.Data;

public class AgendaData implements Comparable<AgendaData>
{

    private String name;
    private String phoneNumber;

    public AgendaData(String name, String phoneNumber)
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName()
    {
        return name;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int compareTo(AgendaData another)
    {
        return another.getPhoneNumber().compareTo(phoneNumber);
    }
}