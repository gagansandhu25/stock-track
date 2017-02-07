package com.udacity.stockhawk.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

/**
 * Created by Indian Dollar on 1/25/2017.
 */
public class WidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetRemoteViewFactory";
    private ArrayList<String> mResults = new ArrayList<>();
    private Context mContext;
    private Cursor mCursor;

    public WidgetRemoteViewFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        if (mCursor != null) {
            mCursor.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        Uri weatherForLocationUri = Contract.Quote.URI;
        mCursor = mContext.getContentResolver().query(weatherForLocationUri,
                null,
                null,
                null,
                Contract.Quote._ID + " ASC");

        Log.d(TAG, "onDataSetChanged: " + mCursor);

        Binder.restoreCallingIdentity(identityToken);
        
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        //Log.d(TAG, "getViewAt: " + mResults.get(position));
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.widgetItemStockNameLabel, mCursor.getString(1));
        rv.setTextViewText(R.id.widgetItemStockPriceLabel, mCursor.getString(2));


        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(WidgetProvider.EXTRA_SYMBOL, mCursor.getString(1));
        rv.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);


        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(Contract.Quote.POSITION_ID) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
