package com.jabravo.android_chat.Data;

/**
 * Created by JoseAntonio on 03/01/2016.
 */
public class FriendRow extends Friend
{

    private boolean isChecked;

    public FriendRow(int id)
    {
        super(id);
        isChecked = false;
    }

    public boolean isChecked()
    {
        return isChecked;
    }

    public void changeState()
    {
        isChecked = !isChecked;
    }
}
