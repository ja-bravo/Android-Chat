package com.jabravo.android_chat.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jabravo.android_chat.R;

public class ChatsListFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    private ListView chatLists;
    private String[] chats = {"Juan", "Ana", "Pedro", "Silvana", "Miguel",
            "Lucas"};

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

        ArrayAdapter<String> chatAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, chats);

        chatLists.setAdapter(chatAdapter);

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

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

}
