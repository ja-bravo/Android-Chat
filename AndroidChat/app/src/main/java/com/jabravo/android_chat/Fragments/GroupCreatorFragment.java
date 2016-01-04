package com.jabravo.android_chat.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jabravo.android_chat.ChatActivity;
import com.jabravo.android_chat.CustomArrayAdapter;
import com.jabravo.android_chat.Data.FriendRow;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.GroupActivity;
import com.jabravo.android_chat.R;
import com.jabravo.android_chat.Services.UserService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GroupCreatorFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener
{
    private OnFragmentInteractionListener mListener;
    private ListView list;
    private List<FriendRow> rows;

    private Button button;
    private TextView text;

    public static GroupCreatorFragment newInstance()
    {
        GroupCreatorFragment fragment = new GroupCreatorFragment();
        return fragment;
    }

    public GroupCreatorFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_group_creator, container, false);
        list = (ListView) view.findViewById(R.id.group_contacts_list);
        button = (Button) view.findViewById(R.id.create_group);
        text = (TextView) view.findViewById(R.id.editText);

        button.setOnClickListener(this);

        User user = User.getInstance();
        rows =  user.getFriendsRows();
        list.setAdapter(new CustomArrayAdapter(view.getContext(), rows));

        list.setOnItemClickListener(this);
        return view;
    }

    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("toID", rows.get(position).getId());

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onClick(View v)
    {
        JSONObject object = null;
        try
        {
           object = new JSONObject();
           object.put("NAME",text.getText().toString());
           object.put("ADMIN",User.getInstance().getID());
           object.put("IMAGE","");


           JSONArray users = new JSONArray();
           for(FriendRow row : rows)
           {
               if(row.isChecked())
               {
                   users.put(row.getId());
               }
           }
           object.put("USERS", users);

            int groupID = new UserService().createGroup(object);
            Intent intent = new Intent(getActivity(), GroupActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("groupID",groupID);
            intent.putExtras(bundle);

            startActivity(intent);
        }
        catch(Exception e){e.printStackTrace();}
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

}
