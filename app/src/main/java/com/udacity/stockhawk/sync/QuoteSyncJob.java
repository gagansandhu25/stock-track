package com.udacity.stockhawk.sync;

import java.io.FileNotFoundException;
import java.math.*;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.udacity.stockhawk.Models.QuoteHistory;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Widget.WidgetProvider;
import com.udacity.stockhawk.data.Const;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.data.StockProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import static com.udacity.stockhawk.R.id.price;
import static com.udacity.stockhawk.R.id.time;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 1;
    private static final int MONTHS_OF_HISTORY = 6;
    private static final String TAG = "QuoteSyncJob";
    private static final String ACTION_HISTORY_UPDATED = "com.udacity.stockhawk.ACTION_HISTORY_UPDATED";

    private QuoteSyncJob() {
    }

    static void getQuotes(Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.MONTH, -MONTHS_OF_HISTORY);

        try {

            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

            if (stockArray.length == 0) {
                return;
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();
            ArrayList<ContentValues> historyCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                String symbol = iterator.next();

                Stock stock = quotes.get(symbol);
                StockQuote quote = stock.getQuote();

                if(quote.getPrice() == null || quote.getAsk() == null || quote.getBid() == null) continue;

                float price = quote.getPrice().floatValue();
                float change = quote.getChange().floatValue();
                float percentChange = quote.getChangeInPercent().floatValue();
                String companyName = stock.getName();

                List<HistoricalQuote> history = stock.getHistory(from, to, Interval.DAILY);

                ContentValues quoteCV = new ContentValues();
                quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                quoteCV.put(Contract.Quote.COLUMN_COMPANY_NAME, companyName);

                for (HistoricalQuote it : history) {
                    ContentValues historyCV = new ContentValues();
                    historyCV.put(Contract.Quote.COLUMN_HISTORY_NAME, symbol);
                    historyCV.put(Contract.Quote.COLUMN_HISTORY_PRICE_HIGH, it.getClose().floatValue());
                    historyCV.put(Contract.Quote.COLUMN_HISTORY_PRICE_LOW, it.getOpen().floatValue());
                    historyCV.put(Contract.Quote.COLUMN_HISTORY_DATE, it.getDate().getTimeInMillis());
                    historyCVs.add(historyCV);
                }

                quoteCVs.add(quoteCV);

            }

            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI_HISTORY,
                            historyCVs.toArray(new ContentValues[historyCVs.size()]));

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

            WidgetProvider.sendRefreshBroadcast(context);

        } catch (FileNotFoundException e) {
            Log.d(TAG, "getQuotes: Server Down");
        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }


    public static void getQuoteHistory(Context context, String symbol, String timeFrame) {

        ArrayList<QuoteHistory> entries = new ArrayList<>();
        com.udacity.stockhawk.Models.Stock s = null;
        String companyName = null;
        float price;
        float change;
        float percentChange;

        Cursor quoteCursor = context.getContentResolver()
                .query(Contract.Quote.makeUriForStock(symbol), null, null, null, null);

        if(quoteCursor != null && quoteCursor.getCount() > 0)
        {
            quoteCursor.moveToFirst();

            price = quoteCursor.getFloat(Contract.Quote.POSITION_PRICE);
            change = quoteCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            percentChange = quoteCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
            companyName = quoteCursor.getString(Contract.Quote.POSITION_COMPANY_NAME);
            s = new com.udacity.stockhawk.Models.Stock(price, change, percentChange, companyName);

            intentToActivity(context, s, entries);
        }


        //Log.d(TAG, "getQuoteHistory: " + historyRecords.getCount());
        //List<HistoricalQuote> history = stock.getHistory(from, to, Interval.DAILY);

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        if(timeFrame != null) {
            switch (timeFrame) {
                case "1W":
                    from.add(Calendar.DATE, -7);
                    break;
                case "1M":
                    from.add(Calendar.MONTH, -1);
                    break;
                case "3M":
                    from.add(Calendar.MONTH, -3);
                    break;
                case "6M":
                    from.add(Calendar.MONTH, -6);
                    break;
                case "1Y":
                    from.add(Calendar.YEAR, -1);
                    break;
                default:
                    from.add(Calendar.DATE, -7);
                    break;
            }
        } else {
            from.add(Calendar.DATE, -7);
        }



        String[] selectionArgs = {String.valueOf(from.getTimeInMillis()), String.valueOf(to.getTimeInMillis())};
        Cursor historyRecords = context.getContentResolver()
                .query(Contract.Quote.makeUriForStockHistory(symbol), null, null, selectionArgs, null);

        while (historyRecords.moveToNext()) {
            entries.add(new QuoteHistory(historyRecords.getFloat(2), historyRecords.getFloat(3), historyRecords.getString(4)));
        }


        if(quoteCursor != null && quoteCursor.getCount() > 0) {
            intentToActivity(context, s, entries);
        }


        // REQUEST TO SERVER
        try {
            Stock stock = YahooFinance.get(symbol);
            StockQuote quote = stock.getQuote();


            if (quote.getPrice() != null) {
                price = quote.getPrice().floatValue();
                change = quote.getChange().floatValue();
                percentChange = quote.getChangeInPercent().floatValue();
                s = new com.udacity.stockhawk.Models.Stock(price, change, percentChange, companyName);

                intentToActivity(context, s, entries);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void intentToActivity(Context context, com.udacity.stockhawk.Models.Stock s, ArrayList<QuoteHistory> entries) {
        Intent inte = new Intent(Const.ACTION_UPDATE_DETAILS_ACTIVITY);
        inte.putExtra(Const.EXTRA_STOCK, s);
        inte.putParcelableArrayListExtra(Const.EXTRA_HISTORY, entries);
        context.sendBroadcast(inte);
    }


    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));

            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());

        }
    }


}
