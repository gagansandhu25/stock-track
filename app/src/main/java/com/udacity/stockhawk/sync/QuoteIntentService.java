package com.udacity.stockhawk.sync;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.udacity.stockhawk.Models.QuoteHistory;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import yahoofinance.histquotes.HistoricalQuote;


public class QuoteIntentService extends IntentService {

    private static final String TAG = "QuoteIntentService";
    private String mAction;
    private String mIntentUpdate = "UPDATE_DETAILS_ACTIVITY";

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        mAction = intent.getAction();

        String symbol = intent.getStringExtra("SYMBOL");

        if(mAction != null && mAction.equals("GET_HISTORY")) {
            QuoteSyncJob.getQuoteHistory(getApplicationContext(), symbol);
        } else {
            QuoteSyncJob.getQuotes(getApplicationContext());
        }
    }
}
