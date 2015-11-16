package com.jabravo.android_chat;

/**
 * Created by Josewer on 16/11/2015.
 */

public class Conversation {

    private int idConver;
    private int idFiendOrGroup;
    private boolean isGroup;
    private boolean show;
    private Friend friend;
    private Group group;

    public Conversation(int idConver , int idFiendOrGroup,
                        boolean isGroup) {

        this.idConver = idConver;
        this.idFiendOrGroup = idFiendOrGroup;
        this.isGroup = isGroup;


        if (this.isGroup)
        {
            group = new Group (idFiendOrGroup);

            if (group.getListMessages().size() != 0)
            {
                show = true;
            }
            else
            {
                show = false;
            }
        }
        else
        {
            friend = new Friend (idFiendOrGroup);

            if (friend.getListMessages().size() != 0)
            {
                show = true;
            }
            else
            {
                show = false;
            }
        }
    }

    public int getIdConver() {
        return idConver;
    }

    public void setIdConver(int idConver) {
        this.idConver = idConver;
    }

    public int getIdFiendOrGroup() {
        return idFiendOrGroup;
    }

    public void setIdFiendOrGroup(int idFiendOrGroup) {
        this.idFiendOrGroup = idFiendOrGroup;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
