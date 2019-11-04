package com.example.yamba;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Date;

public class DetailsFragment extends Fragment
{
    private TextView textUser, textMessage, textCreatedAt;
    private ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState)
    {
        View view = inflater.inflate(R.layout.list_item, null, false);
        textUser = (TextView) view.findViewById(R.id.list_item_text_user);
        textMessage = (TextView) view.findViewById(R.id.list_item_text_message);
        textCreatedAt = (TextView) view.findViewById(R.id.list_item_text_created_at);
        progress = (ProgressBar) view.findViewById(R.id.list_item_freshness);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        long id = getActivity().getIntent().getLongExtra(StatusContract.Column.ID, 0);
        updateView(id);
    }

    public void updateView(long id)
    {
        if(id==-1)
        {
            textUser.setText("");
            textMessage.setText("");
            textCreatedAt.setText("");
            progress.setProgress(0);
        }

        Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

        if(!cursor.moveToFirst())
            return;

        String user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
        long createdAt = cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT));

        // calculate percentage
        Date current = new Date();
        long today = current.getTime();
        long currentMilis = today - createdAt;
        long x = (currentMilis * 100)/86400000;
        int percentage = (int)x;
        // end calculate percentage

        textUser.setText(user);
        textMessage.setText(message);
        textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));
        progress.setProgress(percentage);
    }
}
