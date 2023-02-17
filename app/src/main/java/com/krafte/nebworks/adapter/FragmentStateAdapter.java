package com.krafte.nebworks.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.krafte.nebworks.ui.CalenderFragment;

import java.util.Calendar;

public class FragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    static final int START_POSITION = Integer.MAX_VALUE / 2;
    Calendar cal = Calendar.getInstance();
    Context mContext;
    String year = "";
    String month = "";
    int state = 0;

    String iYear = "";
    String iMonth = "";
    boolean datePickerState = false;

    // state: 1: workGoto 2: workStatus 3: taskApproval
    public FragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, int state) {
        super(fragmentActivity);
        this.state = state;
    }

    public FragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, boolean datePickerState, String year, String month, int state) {
        super(fragmentActivity);
        this.datePickerState = datePickerState;
        this.iYear = year;
        this.iMonth = month;
        this.state = state;
    }

    public int returnPosition() {
        return START_POSITION;
    }

    @Override
    public long getItemId(int position) {
        Log.d("getItemId", String.valueOf(position));
        int currentYear;
        int currentMonth;
        if (datePickerState) {
            currentMonth = Integer.parseInt(this.iMonth);
            currentYear = Integer.parseInt(this.iYear);
        } else {
            currentYear = cal.get(Calendar.YEAR);
            currentMonth = cal.get(Calendar.MONTH) + 1;
        }
//        Log.d("FragmentStateAdapter", "CurrentDate: " + currentYear + " " + currentMonth);

        int move = position - START_POSITION;
        int bias = move < 0 ? -1 : 1;

        int moveYear = Math.abs(move) / 12 * bias;
        int moveMonth = Math.abs(move) % 12 * bias;

        currentYear += moveYear;

        if ((currentMonth + moveMonth) < 1) {
            currentMonth = 12 + (currentMonth + moveMonth);
            currentYear--;
        } else if ((currentMonth + moveMonth) > 12) {
            currentMonth = (currentMonth + moveMonth) - 12;
            currentYear++;
        } else {
            currentMonth = currentMonth + moveMonth;
        }

        return (currentYear * 100 + currentMonth);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("createFragment", String.valueOf(position));
        long itemId = getItemId(position);

        long year = itemId / 100L;
        long month = itemId % 100L;

        this.year = String.valueOf(Math.toIntExact(year));
        this.month = String.format("%02d", Math.toIntExact(month));

        Log.d("FragmentStateAdapter", "year, month : " + this.year + " " + this.month);

        return new CalenderFragment(this.year, this.month, state);
    }

    public String returnMonth() {
        return month;
    }

    public String returnYear() {
        return year;
    }

    @Override
    public boolean containsItem(long itemId) {
        // Your code
        return super.containsItem(itemId);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
