package com.udacity.stockhawk.Widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;

import java.util.ArrayList;

/**
 * Created by Indian Dollar on 1/25/2017.
 */
public class WidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetRemoteViewFactory";
    private ArrayList<String> mResults = new ArrayList<>();
    private Context mContext;

    public WidgetRemoteViewFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        for (int i = 1; i <= 10; i++) {
            mResults.add("Row: " + i);
        }
    }

    @Override
    public void onDataSetChanged() {
        for (int i = 1; i <= 10; i++) {
            mResults.add("Row: " + i);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "getViewAt: " + mResults.get(position));
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.widgetItemStockNameLabel, mResults.get(position));
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
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
