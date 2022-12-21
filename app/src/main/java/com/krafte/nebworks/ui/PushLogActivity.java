package com.krafte.nebworks.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.databinding.ActivityPushBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class PushLogActivity extends AppCompatActivity {
    private ActivityPushBinding binding;

    private static final String TAG = "PushLogActivity";
    Context mContext;
    Handler mHandler;

    //Sharedf
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();


    Dlog dlog = new Dlog();
    String USER_INFO_ID = "";
    String place_id = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "LongLogTag", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pushmanagement);
        binding = ActivityPushBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);

        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        place_id = shardpref.getString("place_id", "");

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    private void setBtnEvent() {

    }

    public void GetPushLogList() {
//        dlog.i("------GetPushLogList------");
//        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
//        dlog.i("place_id : " + place_id);
//        dlog.i("------GetPushLogList------");
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(PushLogListInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        PushLogListInterface api = retrofit.create(PushLogListInterface.class);
//        Call<String> call = api.getData("", USER_INFO_ID, USER_INFO_AUTH);
//        //
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
//                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
//                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
//                            try {
//                                //Array데이터를 받아올 때
//                                store_cnt = 0;
//                                JSONArray Response = new JSONArray(jsonResponse);
//                                if (USER_INFO_AUTH.equals("0")) {
//                                    binding.storeCntTv.setText("관리중인 매장");
//                                } else {
//                                    binding.storeCntTv.setText("참여중인 매장");
//                                }
//                                mList = new ArrayList<>();
//                                mAdapter = new WorkplaceListAdapter(mContext, mList);
//                                binding.placeList.setAdapter(mAdapter);
//                                binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//                                dlog.i("SIZE : " + Response.length());
//                                if (response.body().equals("[]")) {
//                                    binding.noData.setVisibility(View.VISIBLE);
//                                    dlog.i("SetNoticeListview Thread run! ");
//                                    dlog.i("GET SIZE : " + Response.length());
//                                    binding.storeCnt.setText(String.valueOf(Response.length()));
//                                } else {
//                                    binding.noData.setVisibility(View.GONE);
//                                    for (int i = 0; i < Response.length(); i++) {
//                                        JSONObject jsonObject = Response.getJSONObject(i);
//                                        store_cnt++;
//                                        mAdapter.addItem(new PlaceListData.PlaceListData_list(
//                                                jsonObject.getString("id"),
//                                                jsonObject.getString("name"),
//                                                jsonObject.getString("owner_id"),
//                                                jsonObject.getString("owner_name"),
//                                                jsonObject.getString("registr_num"),
//                                                jsonObject.getString("store_kind"),
//                                                jsonObject.getString("address"),
//                                                jsonObject.getString("latitude"),
//                                                jsonObject.getString("longitude"),
//                                                jsonObject.getString("pay_day"),
//                                                jsonObject.getString("test_period"),
//                                                jsonObject.getString("vacation_select"),
//                                                jsonObject.getString("insurance"),
//                                                jsonObject.getString("start_time"),
//                                                jsonObject.getString("end_time"),
//                                                jsonObject.getString("save_kind"),
//                                                jsonObject.getString("img_path"),
//                                                jsonObject.getString("accept_state"),
//                                                jsonObject.getString("total_cnt"),
//                                                jsonObject.getString("i_cnt"),
//                                                jsonObject.getString("o_cnt"),
//                                                jsonObject.getString("created_at")
//                                        ));
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                dlog.e("에러1 = " + t.getMessage());
//            }
//        });
    }
}

