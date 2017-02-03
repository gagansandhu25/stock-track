package com.udacity.stockhawk.ui;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Indian Dollar on 1/15/2017.
 */
public class AxisValueFormatter implements IAxisValueFormatter {


    private static final String TAG = "AxisValueFormatter";
    private ArrayList<String> mLabels;

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        Log.d(TAG, "getFormattedValue: "+ value);
        return mLabels.get((int)value);

    }

    public AxisValueFormatter(ArrayList<String> labels) {
        mLabels = labels;
    }

}
