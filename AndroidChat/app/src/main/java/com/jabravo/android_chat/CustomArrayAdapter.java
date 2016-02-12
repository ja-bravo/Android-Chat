package com.jabravo.android_chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jabravo.android_chat.Data.FriendRow;

import java.util.List;

/**
 * Created by JoseAntonio on 03/01/2016.
 */
public class CustomArrayAdapter extends ArrayAdapter<FriendRow>
{

    private LayoutInflater layoutInflater;

    public CustomArrayAdapter(Context context, List<FriendRow> objects)
    {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Holder holder = null;
        if (convertView == null)
        {
            holder = new Holder();

            convertView = layoutInflater.inflate(R.layout.row, parent, false);
            holder.setTextViewTitle((TextView) convertView.findViewById(R.id.row_text));
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        FriendRow row = getItem(position);
        holder.getTextViewTitle().setText(row.getNick());

        return convertView;
    }

    static class Holder
    {
        TextView textViewTitle;
        TextView textViewSubtitle;

        public TextView getTextViewTitle()
        {
            return textViewTitle;
        }

        public void setTextViewTitle(TextView textViewTitle)
        {
            this.textViewTitle = textViewTitle;
        }

    }

}