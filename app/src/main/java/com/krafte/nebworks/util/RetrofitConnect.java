package com.krafte.nebworks.util;

import com.krafte.nebworks.data.LastVersion;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.data.TodoReuseData;
import com.krafte.nebworks.data.TodolistData;

import java.util.ArrayList;

public class RetrofitConnect {
    private static final String TAG = "RetrofitConnect";

    /*버전구분*/
    public LastVersion lastVersion = new LastVersion();

    public ArrayList<PlaceNotiData.PlaceNotiData_list> placeNotiData_lists = new ArrayList<>();
    public ArrayList<TodolistData.TodolistData_list> todolistData_lists = new ArrayList<>();
    public ArrayList<TodoReuseData.TodoReuseData_list> todoReuseData_lists = new ArrayList<>();
}
