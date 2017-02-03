package com.udacity.stockhawk.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

/**
 * Created by Indian Dollar on 1/16/2017.
 */

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(

                    context.getPackageName(),
                    R.layout.widget

            );

            views.setTextViewText(R.id.widgetTitleLabel, "hi");


            views.setRemoteAdapter(R.id.widgetListView, new Intent(context, WidgetRemoteViewService.class));
            //views.setEmptyView(R.id.widgetListView, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
}