package com.udacity.stockhawk.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Indian Dollar on 1/25/2017.
 */

public class WidgetRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewFactory(this.getApplicationContext(), intent);
    }
}
