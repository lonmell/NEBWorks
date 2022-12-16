package com.krafte.nebworks.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCurrent {

    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate_age = new SimpleDateFormat("yyyy");

    public String GET_TIME = simpleDate.format(mDate);

    public String GET_YEAR = simpleDate.format(mDate).substring(0,4);
    public String GET_MONTH = simpleDate.format(mDate).substring(5,7);
    public String GET_DAY = simpleDate.format(mDate).substring(8,10);

    Calendar cal = Calendar.getInstance();

    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    public String numDayOfWeek = String.valueOf(dayOfWeek);
}
