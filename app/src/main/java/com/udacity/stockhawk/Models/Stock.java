package com.udacity.stockhawk.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Indian Dollar on 1/18/2017.
 */

public class Stock implements Parcelable {

    private float mPrice;
    private float mChange;
    private float mPercentChange;
    private String mCompanyName;

    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        mCompanyName = companyName;
    }

    public Stock(float price, float change, float percentChange, String companyName) {
        mPrice = price;
        mChange = change;
        mPercentChange = percentChange;
        mCompanyName = companyName;
    }

    public float getPercentChange() {
        return mPercentChange;
    }

    public void setPercentChange(float percentChange) {
        mPercentChange = percentChange;
    }

    public float getChange() {
        return mChange;
    }

    public void setChange(float change) {
        mChange = change;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
    }


    protected Stock(Parcel in) {
        mPrice = in.readFloat();
        mChange = in.readFloat();
        mPercentChange = in.readFloat();
        mCompanyName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mPrice);
        dest.writeFloat(mChange);
        dest.writeFloat(mPercentChange);
        dest.writeString(mCompanyName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}