package com.krafte.kogas.util;

import com.krafte.kogas.data.LastVersion;
import com.krafte.kogas.data.PlaceNotiData;
import com.krafte.kogas.data.TodoReuseData;
import com.krafte.kogas.data.TodolistData;

import java.util.ArrayList;

public class RetrofitConnect {
    private static final String TAG = "RetrofitConnect";

    /*버전구분*/
    public LastVersion lastVersion = new LastVersion();

    public ArrayList<PlaceNotiData.PlaceNotiData_list> placeNotiData_lists = new ArrayList<>();
    public ArrayList<TodolistData.TodolistData_list> todolistData_lists = new ArrayList<>();
    public ArrayList<TodoReuseData.TodoReuseData_list> todoReuseData_lists = new ArrayList<>();
}
