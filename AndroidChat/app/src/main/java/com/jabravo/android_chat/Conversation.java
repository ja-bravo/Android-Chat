package com.jabravo.android_chat;

import java.security.acl.Group;

/**
 * Created by Josewer on 16/11/2015.
 */
public class Conversation {

    private int idConver;
    private int idFiendOrGroup;
    private boolean isGroup;
    private boolean show;
    private Friend friend;
    private Groups group;

    public Conversation(int idConver , int idFiendOrGroup,
                        boolean isGroup) {

        this.idConver = idConver;
        this.idFiendOrGroup = idFiendOrGroup;
        this.isGroup = isGroup;

        if (this.isGroup)
        {
            group = new Groups (idFiendOrGroup);

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

    public Groups getGroup() {
        return group;
    }

    public void setGroup(Groups group) {
        this.group = group;
    }
}
