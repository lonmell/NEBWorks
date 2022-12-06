package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.YoilStringAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.databinding.ActivityContractAdd04Binding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;
import java.util.List;

public class AddContractPage04 extends AppCompatActivity {
    private ActivityContractAdd04Binding binding;
    private final static String TAG = "AddContractPage04";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    List<String> yoil = new ArrayList<>();
    ArrayList<StringData.StringData_list> workmList = new ArrayList<>();
    YoilStringAdapter workmAdapter = null;

    ArrayList<StringData.StringData_list> restmList = new ArrayList<>();
    YoilStringAdapter restmAdapter = null;

    List<String> workYoil = new ArrayList<>();
    List<String> restYoil = new ArrayList<>();

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractAdd04Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref       = new PreferenceHelper(mContext);
        place_id        = shardpref.getString("place_id","0");
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID","0");
        worker_id       = shardpref.getString("worker_id","0");

        setBtnEvent();

        //basic setting
        yoil.add("월");
        yoil.add("화");
        yoil.add("수");
        yoil.add("목");
        yoil.add("금");
        yoil.add("토");
        yoil.add("일");
        //근무요일
        workmList = new ArrayList<>();
        workmAdapter = new YoilStringAdapter(mContext, workmList);
        binding.workyoilList.setAdapter(workmAdapter);
        binding.workyoilList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        for (int i = 0; i < yoil.size(); i++) {
            workmAdapter.addItem(new StringData.StringData_list(
                    yoil.get(i)
            ));
        }
        workmAdapter.notifyDataSetChanged();
        workmAdapter.setOnItemClickListener(new YoilStringAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                dlog.i("Get onItem : " + workmList.get(position));
                workYoil.add(String.valueOf(workmList.get(position)));
            }
        });

        //휴무일
        restmList = new ArrayList<>();
        restmAdapter = new YoilStringAdapter(mContext, restmList);
        binding.restyoilList.setAdapter(restmAdapter);
        binding.restyoilList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        for (int i = 0; i < yoil.size(); i++) {
            restmAdapter.addItem(new StringData.StringData_list(
                    yoil.get(i)
            ));
        }
        restmAdapter.notifyDataSetChanged();
        restmAdapter.setOnItemClickListener(new YoilStringAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                dlog.i("Get onItem : " + restmList.get(position));
                restYoil.add(String.valueOf(restmList.get(position)));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void setBtnEvent(){

    }
}
