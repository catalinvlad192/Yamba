package com.example.yamba;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.w3c.dom.Text;

import java.util.Date;

public class TimelineFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = TimelineFragment.class.getSimpleName();
    private static final String[] FROM = {StatusContract.Column.USER,
                StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT,
                StatusContract.Column.CREATED_AT};
    private static final int[] TO = {R.id.list_item_text_user,
                R.id.list_item_text_message, R.id.list_item_text_created_at,
                R.id.list_item_freshness};
    private static final int LOADER_ID = 42;
    private SimpleCursorAdapter mAdapter;

    private static final SimpleCursorAdapter.ViewBinder VIEW_BINDER = new SimpleCursorAdapter.ViewBinder(){
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex)
        {
            long timestamp;

            //Custom binder
            switch(view.getId())
            {
                case R.id.list_item_text_created_at: {
                    timestamp = cursor.getLong(columnIndex);
                    CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
                    ((TextView) view).setText(relTime);
                    return true;
                }
                case R.id.list_item_freshness: {
                    timestamp = cursor.getLong(columnIndex);

                    // calculate percentage
                    Date current = new Date();
                    long today = current.getTime();
                    long currentMilis = today - timestamp;
                    long x = (currentMilis * 100)/86400000;
                    int percentage = (int)x;
                    // end calculate percentage

                    ((ProgressBar) view).setProgress(percentage);
                    return true;
                }
                default: return false;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);
        mAdapter.setViewBinder(VIEW_BINDER);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void onListItemClick(ListView l , View v, int position, long id)
    {
        // Get the details fragment
        DetailsFragment fragment = (DetailsFragment) getFragmentManager().findFragmentById(R.id.fragment_details);

        // Is details fragment visible?
        if(fragment != null && fragment.isVisible())
        {
            fragment.updateView(id);
        }
        else
        {
            startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra(StatusContract.Column.ID, id));
        }
    }

    // ----Loader Callbacks ---
    // Executed on UI thread
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if(id != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(), StatusContract.CONTENT_URI, null, null, null, StatusContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        // get the details fragment
        DetailsFragment fragment = (DetailsFragment) getFragmentManager().findFragmentById(R.id.fragment_details);

        // Is details fragment visible
        if(fragment != null && fragment.isVisible() && cursor.getCount() == 0)
        {
            fragment.updateView(-1);
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "onLoadFinished with cursor: " + cursor.getCount());
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mAdapter.swapCursor(null);
    }
}
