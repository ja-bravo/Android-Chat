package com.jabravo.android_chat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jabravo.android_chat.Fragments.GroupCreatorFragment;

public class GroupInvite extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        GroupCreatorFragment fragment = GroupCreatorFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("groupID", getIntent().getIntExtra("groupID", 0));
        fragment.setArguments(bundle);

        transaction.replace(R.id.group_invite, fragment);
        transaction.commit();
    }
}
