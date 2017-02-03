package com.udacity.stockhawk.Widget;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Indian Dollar on 1/24/2017.
 */

public class WidgetIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WidgetIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
