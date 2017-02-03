package com.udacity.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ColorFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.Models.QuoteHistory;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.NetworkUtils;
import com.udacity.stockhawk.sync.QuoteIntentService;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import static com.udacity.stockhawk.R.id.chart;

/**
 * Created by Indian Dollar on 1/11/2017.
 */

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private String mSymbol;
    private Context mContext;
    private ArrayList<QuoteHistory> mList;
    private LineChart mChart;
    private TextView mStockNameLabel;
    private com.udacity.stockhawk.Models.Stock mStock;
    private TextView mStockPriceLabel;
    private TextView mStockChangeLabel;
    private TextView mStockPercentChangeLabel;
    private TextView mStockCompanyNameLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        mSymbol = getIntent().getStringExtra("SYMBOL");
        mContext = this;

        mChart = (LineChart) findViewById(chart);
        mStockNameLabel = (TextView) findViewById(R.id.stockNameLabel);
        mStockPriceLabel = (TextView) findViewById(R.id.stockPriceLabel);
        mStockChangeLabel = (TextView) findViewById(R.id.stockChangeLabel);
        mStockPercentChangeLabel = (TextView) findViewById(R.id.stockPercentChangeLabel);
        mStockCompanyNameLabel = (TextView) findViewById(R.id.stockCompanyNameLabel);

        mChart.setDrawGridBackground(false);

        List<Entry> entries = new ArrayList<Entry>();

        Intent nowIntent = new Intent(getApplicationContext(), QuoteIntentService.class);
        nowIntent.setAction("GET_HISTORY");
        nowIntent.putExtra("SYMBOL", mSymbol);

        getApplicationContext().startService(nowIntent);


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mList = intent.getParcelableArrayListExtra("HISTORY");
                mStock = intent.getParcelableExtra("STOCK");

                mStockNameLabel.setText(mSymbol);
                mStockPriceLabel.setText(String.valueOf(mStock.getPrice()));
                mStockChangeLabel.setText(String.valueOf(mStock.getChange()));
                mStockPercentChangeLabel.setText("(" + String.valueOf(mStock.getPercentChange()) + "%)");
                mStockCompanyNameLabel.setText(mStock.getCompanyName());

                //Log.d(TAG, "onReceive: " + mList);

                List<Entry> entries = new ArrayList<Entry>();
                ArrayList<String> labels = new ArrayList<>();

                float i = 0;
                for (QuoteHistory data : mList) {

                    Log.d(TAG, "onReceive: " + data.getFormattedDate());

                    // turn your data into Entry objects
                    entries.add(new Entry(i, data.getHigh()));

                    labels.add(data.getFormattedDate());

                    i++;
                }

                //Log.d(TAG, "onReceive: " + labels);

                LineDataSet dataSet = new LineDataSet(entries, null);
                dataSet.setLineWidth(3);
                dataSet.setDrawValues(false);
                dataSet.setDrawFilled(true);
                dataSet.setLabel(null);
                dataSet.setFillColor(ColorTemplate.rgb("#FFC107"));
                dataSet.setColor(ColorTemplate.rgb("#E64A19"));
                dataSet.setDrawCircles(false);

                //dataSet.setColor(R.color.colorAccent);
                LineData lineData = new LineData(dataSet);

                XAxis xAxis = mChart.getXAxis();
                xAxis.setDrawLabels(true);
                xAxis.setValueFormatter(new AxisValueFormatter(labels));
                xAxis.setGranularity(1f);
                mChart.setAutoScaleMinMaxEnabled(true);
                mChart.setKeepPositionOnRotation(true);

                mChart.setData(lineData);
                mChart.animateY(3000);
                mChart.setDescription(null);
                mChart.getLegend().setEnabled(false);


                mChart.invalidate();

                //Log.d(TAG, "onReceive: " + entries);


            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_DETAILS_ACTIVITY"));

    }

}
