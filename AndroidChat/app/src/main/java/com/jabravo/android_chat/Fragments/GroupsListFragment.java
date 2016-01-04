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

import com.jabravo.android_chat.Data.Group;
import com.jabravo.android_chat.Data.User;
import com.jabravo.android_chat.GroupActivity;
import com.jabravo.android_chat.R;

import java.util.List;

public class GroupsListFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private OnFragmentInteractionListener mListener;
    private ListView groupsList;
    private List<Group> groups;

    public static GroupsListFragment newInstance()
    {
        GroupsListFragment fragment = new GroupsListFragment();
        return fragment;
    }

    public GroupsListFragment()
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
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        groupsList = (ListView) view.findViewById(R.id.groups_list);

        User user = User.getInstance();
        groups = user.getGroups();

        ArrayAdapter<Group> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, groups);
        groupsList.setAdapter(adapter);

        groupsList.setOnItemClickListener(this);
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
        bundle.putInt("groupID", groups.get(position).getId());

        Intent intent = new Intent(getActivity(), GroupActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

}
