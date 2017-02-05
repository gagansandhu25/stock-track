package com.udacity.stockhawk.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.DetailsActivity;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by Indian Dollar on 1/16/2017.
 */

public class WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_DETAILS_ACTIVITY = "ACTION_DETAILS_ACTIVITY";
    public static final String EXTRA_SYMBOL = "SYMBOL";
    private static final String TAG = "WidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(

                    context.getPackageName(),
                    R.layout.widget

            );


            /*Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widgetTitleLabel, pendingIntent);*/


            // Sets up the intent that points to the StackViewService that will
            // provide the views for this collection.
            Intent intent1 = new Intent(context, WidgetRemoteViewService.class);
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent1.setData(Uri.parse(intent1.toUri(Intent.URI_INTENT_SCHEME)));


            views.setRemoteAdapter(R.id.widgetListView, intent1);
            //views.setEmptyView(R.id.widgetListView, R.id.widget_empty);

            Intent clickIntentTemplate = new Intent(context, WidgetProvider.class);

            clickIntentTemplate.setAction(WidgetProvider.ACTION_DETAILS_ACTIVITY);
            clickIntentTemplate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            intent1.setData(Uri.parse(intent1.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntentTemplate,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widgetListView, toastPendingIntent);


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: " + intent.getAction().equals(ACTION_DETAILS_ACTIVITY));

        if (intent.getAction().equals(ACTION_DETAILS_ACTIVITY)) {
            String item = intent.getExtras().getString(EXTRA_SYMBOL);
            Log.d(TAG, "onReceive: " + item);
            Toast.makeText(context, item, Toast.LENGTH_LONG).show();
        }

        super.onReceive(context, intent);
    }
}