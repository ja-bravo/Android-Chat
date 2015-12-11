package com.jabravo.android_chat.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jabravo.android_chat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsListFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    private ListView chatLists;
    private String[] chats = {"Juan", "Ana", "Pedro", "Silvana", "Miguel",
            "Lucas"};

    public static ChatsListFragment newInstance(String param1, String param2)
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
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
