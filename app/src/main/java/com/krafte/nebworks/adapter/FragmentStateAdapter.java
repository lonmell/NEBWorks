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

    public FragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public int returnPosition() {
        return START_POSITION;
    }

    @Override
    public long getItemId(int position) {
        Log.d("getItemId", String.valueOf(position));
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;

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

        cal.getTime().getTime();

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

        return new CalenderFragment(itemId / 100L, itemId % 100L);
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
