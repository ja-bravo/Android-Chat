package com.jabravo.android_chat.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private HashMap<String, Friend> friends;
    private HashMap<String, Group> groups;

    private Context context;

    private User(int ID, String number, String status, String nick)
    {
        this.ID = ID;
        this.number = number;
        this.status = status;
        this.nick = nick;
        friends = new HashMap<>();
        groups = new HashMap<>();
    }

    private User(int ID, String number, String status, String nick, Context context)
    {
        this.ID = ID;
        this.number = number;
        this.status = status;
        this.nick = nick;
        this.context = context;
        friends = new HashMap<>();
        groups = new HashMap<>();
    }

    public static User getInstance()
    {
        if (user == null)
        {
            user = new User(-1, "1", "1", "1");
        }

        return user;
    }

    public static User getInstance(Context context)
    {
        if (user == null)
        {
            user = new User(-1, "1", "1", "1", context);
        }
        return user;
    }

    public void setID(int id)
    {
        this.ID = id;
    }

    public int getID()
    {
        return ID;
    }

    public String getNick()
    {
        return String.valueOf(nick.charAt(0)).toUpperCase() + nick.substring(1);
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

    public void updatePreferences()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("username", nick).apply();
        prefs.edit().putString("numberPhone", number).apply();
        prefs.edit().putString("status", status).apply();
        prefs.edit().putInt("ID", ID).apply();
    }

    public void addFriend(String phone)
    {
        if (!friends.containsKey(phone))
        {
            phone = phone.replace("+34", "");
            while (phone.contains(" "))
            {
                phone = phone.replace(" ", "");
            }

            friends.put(phone, new Friend(phone));
        }
    }

    public List<Group> getGroups()
    {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String url = "http://146.185.155.88:8080/api/get/groups/" + ID;
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setDoOutput(false);

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                    {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray array = jsonResponse.getJSONArray("groups");

                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject row = array.getJSONObject(i);

                        String groupID = String.valueOf(row.getInt("ID"));
                        String adminID = String.valueOf(row.getInt("ADMIN"));
                        String groupImage = row.getString("IMAGE");
                        String groupName = row.getString("NAME");

                        groups.put(groupID, new Group(Integer.parseInt(groupID), groupName,
                                                      Integer.parseInt(adminID), groupImage));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return new ArrayList<>(groups.values());
    }

    public HashMap<String, Group> getGroupsHashMap()
    {
        return groups;
    }

    public List<Friend> getFriends()
    {
        return new ArrayList<>(friends.values());
    }

    public List<FriendRow> getFriendsRows()
    {
        List<FriendRow> rows = new ArrayList<>();
        for(Friend friend : friends.values())
        {
            FriendRow row = new FriendRow(friend.getId());
            row.setImage(friend.getImage());
            row.setNick(friend.getNick());
            row.setPhone(friend.getPhone());
            row.setMessages(friend.getMessages());
            row.setStatus(friend.getStatus());

            rows.add(row);
        }

        return rows;
    }

    public HashMap<String, Friend> getFriendsHashMap()
    {
        return friends;
    }

    public JSONObject getFriendsJSON() throws JSONException
    {
        JSONObject response = new JSONObject();
        JSONArray items = new JSONArray();

        for (Map.Entry<String, Friend> friend : friends.entrySet())
        {
            JSONObject item = new JSONObject();
            item.put("PHONE", friend.getValue().getPhone());

            items.put(item);
        }

        response.put("Friends", items);
        return response;
    }

    public void updateFriends()
    {
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String url = null;
                try
                {
                    String json = URLEncoder.encode(getFriendsJSON().toString(), "UTF-8");
                    url = "http://146.185.155.88:8080/api/get/friends/" + json;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setDoOutput(false);

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                    {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString().replace("\\", ""));
                    JSONArray array = jsonResponse.getJSONArray("friends");

                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject row = array.getJSONObject(i).getJSONObject("friend");

                        String fID = String.valueOf(row.getInt("ID"));
                        String fNick = row.getString("NICK");
                        String fNumber = String.valueOf(row.getInt("PHONE"));
                        String fStatus = row.getString("STATUS");
                        String fImage = row.getString("USER_IMAGE");

                        fNick = String.valueOf(fNick.charAt(0)).toUpperCase() + fNick.substring(1);

                        friends.put(fID, new Friend(fID, fNumber, fStatus, fImage, fNick));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for (Iterator<Map.Entry<String, Friend>> it = friends.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<String, Friend> entry = it.next();
            if (entry.getValue().getId() == -1 || entry.getValue().getPhone().equals(User.getInstance().getNumber()))
            {
                it.remove();
            }
        }

    }
}
