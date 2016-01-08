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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jabravo.android_chat.ChatActivity;
import com.jabravo.android_chat.Data.Actions_DB;
import com.jabravo.android_chat.Data.Friend;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.R;

import java.util.List;

public class ChatsListFragment extends Fragment
        implements AdapterView.OnItemClickListener
{
    private OnFragmentInteractionListener mListener;
    private ListView chatLists;
    private List<Friend> contacts;

    public static ChatsListFragment newInstance()
    {
        ChatsListFragment fragment = new ChatsListFragment();
        return fragment;
    }

    public ChatsListFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_chats_list, container, false);
        chatLists = (ListView) view.findViewById(R.id.chats_list_view);

        User user = User.getInstance();
        contacts = user.getFriends();

        for (int i = 0 ; i < contacts.size() ; i++)
        {
            if (!Actions_DB.existConversation(contacts.get(i).getId())) {
                contacts.remove(i);
                i--;
            }
        }

        ArrayAdapter<Friend> chatAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_list_item_1, contacts);

        chatLists.setAdapter(chatAdapter);

        chatLists.setOnItemClickListener(this);

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
        bundle.putInt("toID", contacts.get(position).getId());

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(bundle);


        startActivity(intent);
    }


    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

}
