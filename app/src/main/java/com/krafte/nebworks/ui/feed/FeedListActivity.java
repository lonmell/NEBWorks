package com.krafte.nebworks.ui.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.PlaceNotiAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.ActivityFeedListBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FeedListActivity extends AppCompatActivity {
    private ActivityFeedListBinding binding;
    private final static String TAG = "FeedListActivity";
    Context mContext;

    ArrayList<PlaceNotiData.PlaceNotiData_list> mList = new ArrayList<>();
    PlaceNotiAdapter mAdapter = null;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID, USER_INFO_NAME, USER_INFO_AUTH;
    String place_id = "";

    //Other
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();

    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();
    int listitemsize = 0;
    String feed_spinner = "";
    int spinner_i = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_feed_list);
        binding = ActivityFeedListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        place_id = shardpref.getString("place_id", "");
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        ArrayList<String> stringCategory1 = new ArrayList<>();
        stringCategory1.add("정렬순서");
        stringCategory1.add("오름차순");
        stringCategory1.add("내림차순");

        ArrayAdapter<String> select_filter1 = new ArrayAdapter<>(mContext, R.layout.dropdown_item_list, stringCategory1);
        binding.feedSpinner.setAdapter(select_filter1);
        binding.feedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dlog.i("i : " + stringCategory1.get(i));
                feed_spinner = stringCategory1.get(i);
                spinner_i = i;
                setRecyclerView(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                feed_spinner = "알바";
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView(spinner_i);
    }


    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

        binding.addWorkBtn.setOnClickListener(v -> {
            pm.addNotiGo(mContext);
        });
    }


    public void setRecyclerView(int i) {
        //i : 0 = 정렬없음 / 1 = 오름차순 / 2 = 내림차순
        dlog.i("setRecyclerView place_id : " + place_id);
        mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "", String.valueOf(i));
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        mList = new ArrayList<>();
                        mAdapter = new PlaceNotiAdapter(mContext, mList);
                        binding.feedList.setAdapter(mAdapter);
                        binding.feedList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();

                        if (Response.length() == 0) {
                            binding.noDataTxt.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        } else {
                            binding.noDataTxt.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                mAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("place_id"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("contents"),
                                        jsonObject.getString("writer_id"),
                                        jsonObject.getString("writer_name"),
                                        jsonObject.getString("writer_img_path"),
                                        jsonObject.getString("jikgup"),
                                        jsonObject.getString("view_cnt"),
                                        jsonObject.getString("comment_cnt"),
                                        jsonObject.getString("link"),
                                        jsonObject.getString("feed_img_path"),
                                        jsonObject.getString("created_at"),
                                        jsonObject.getString("updated_at"),
                                        jsonObject.getString("open_date"),
                                        jsonObject.getString("close_date")
                                ));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void UpdateWorkNotifyReadYn(String flag, String notify_no, String notify_store_no) {

//        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
//            dbConnection.WorkNotifyManagement(flag, notify_no, notify_store_no, "", "", 0, "", "");
////                    Log.i(TAG, "Result = " + resultData.getRESULT());
//            String getMessage = resultData.getRESULT().replaceAll("\"", "");
//            dlog.i("getMessage = " + getMessage);
//
//            if (getMessage.equals("success")) {
//                if (flag.equals("4")) {
//
//                } else {
//                    dlog.i("getMessage = " + getMessage);
//                }
//            } else {
//                Toast.makeText(mContext, "이력서전송에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        shardpref.putInt("SELECT_POSITION",0);
        if(USER_INFO_AUTH.equals("0")){
            pm.Main(mContext);
        }else{
            pm.Main2(mContext);
        }
    }
}

