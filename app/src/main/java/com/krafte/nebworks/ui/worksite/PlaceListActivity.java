package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.WorkplaceListAdapter;
import com.krafte.nebworks.bottomsheet.StoreListBottomSheet;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityWorksiteBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/*
 * 2022-10-05 방창배 작성
 * */
public class PlaceListActivity extends AppCompatActivity {

    private ActivityWorksiteBinding binding;
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    PageMoveClass pm = new PageMoveClass();

    //Other 변수
    ArrayList<PlaceListData.PlaceListData_list> mList;
    WorkplaceListAdapter mAdapter = null;
    int listitemsize = 0;
    String USER_INFO_EMAIL = "";
    String USER_INFO_NAME = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    //사용자 정보 체크
//    Timer timer = new Timer();
    String id = "";
    String name = "";
    String email = "";
    String phone = "";
    String gender = "";
    String img_path = "";

    int confirm_cnt = 0;
    List<String> confirm_member = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityWorksiteBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-99");// 0:점주 / 1:근로자

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            setBtnEvent();
            LoginCheck(USER_INFO_EMAIL);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        GetPlaceList();
    }

    @Override
    public void onResume() {
        super.onResume();
//        GetPlaceList();
        GetPlaceList();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setBtnEvent() {
        binding.addPlace.setOnClickListener(v -> {
            onStartAuth();
        });
        binding.addPlace2.setOnClickListener(v -> {
            onStartAuth();
        });
    }

    private void onStartAuth() {
        if (USER_INFO_AUTH.equals("0")) {
            pm.PlaceAddGo(mContext);
        } else {
            StoreListBottomSheet slb = new StoreListBottomSheet();
            slb.show(getSupportFragmentManager(), "StoreListBottomSheet");
            slb.setOnClickListener01(v -> pm.PlaceSearch(mContext));
            slb.setOnClickListener02(v -> pm.PlaceAddGo(mContext));
            slb.setOnClickListener03(v -> pm.Career(mContext));
        }
    }

    public void LoginCheck(String account) {
        dlog.i("LoginCheck account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSelectInterface api = retrofit.create(UserSelectInterface.class);
        Call<String> call = api.getData(account);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                            dlog.i("LoginCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    id = Response.getJSONObject(0).getString("id");
                                    name = Response.getJSONObject(0).getString("name");
                                    email = Response.getJSONObject(0).getString("account");
                                    phone = Response.getJSONObject(0).getString("phone");
                                    gender = Response.getJSONObject(0).getString("gender");
                                    img_path = Response.getJSONObject(0).getString("img_path");
                                    img_path = Response.getJSONObject(0).getString("img_path");

                                    shardpref.putString("USER_INFO_ID", id);
                                    shardpref.putString("USER_INFO_NAME", name);
                                    shardpref.putString("USER_INFO_EMAIL", account);
                                    shardpref.putString("USER_INFO_SABUN", phone);
                                    shardpref.putString("USER_INFO_SOSOK", gender);
                                    shardpref.putString("USER_INFO_PROFILE", img_path);

                                    dlog.i("id : " + id);
                                    dlog.i("USER_INFO_ID : " + shardpref.getString("USER_INFO_ID", "0"));
                                    dlog.i("USER_INFO_EMAIL : " + shardpref.getString("USER_INFO_EMAIL", "0"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    int store_cnt = 0;
    public void GetPlaceList() {
        dlog.i("------GetPlaceList------");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("------GetPlaceList------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData("", USER_INFO_ID,USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + response.body().length());
                            dlog.i("GetPlaceList jsonResponse : " + response.body());
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(response.body());
                                if (USER_INFO_AUTH.equals("0")) {
                                    binding.storeCntTv.setText("관리중인 매장");
                                } else {
                                    binding.storeCntTv.setText("참여중인 매장");
                                }
                                if (listitemsize != Response.length()) {
                                    mList = new ArrayList<>();
                                    mAdapter = new WorkplaceListAdapter(mContext, mList);
                                    binding.placeList.setAdapter(mAdapter);
                                    binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                    listitemsize = Response.length();

                                    if (Response.length() == 0) {
                                        binding.noData.setVisibility(View.VISIBLE);
                                        dlog.i("SetNoticeListview Thread run! ");
                                        dlog.i("GET SIZE : " + Response.length());
                                        binding.storeCnt.setText(String.valueOf(Response.length()));
                                    } else {
                                        binding.noData.setVisibility(View.GONE);
                                        for (int i = 0; i < Response.length(); i++) {
                                            JSONObject jsonObject = Response.getJSONObject(i);
                                            store_cnt++;
                                            mAdapter.addItem(new PlaceListData.PlaceListData_list(
                                                    jsonObject.getString("id"),
                                                    jsonObject.getString("name"),
                                                    jsonObject.getString("owner_id"),
                                                    jsonObject.getString("owner_name"),
                                                    jsonObject.getString("registr_num"),
                                                    jsonObject.getString("store_kind"),
                                                    jsonObject.getString("address"),
                                                    jsonObject.getString("latitude"),
                                                    jsonObject.getString("longitude"),
                                                    jsonObject.getString("pay_day"),
                                                    jsonObject.getString("test_period"),
                                                    jsonObject.getString("vacation_select"),
                                                    jsonObject.getString("insurance"),
                                                    jsonObject.getString("start_time"),
                                                    jsonObject.getString("end_time"),
                                                    jsonObject.getString("save_kind"),
                                                    jsonObject.getString("img_path"),
                                                    jsonObject.getString("accept_state"),
                                                    jsonObject.getString("total_cnt"),
                                                    jsonObject.getString("i_cnt"),
                                                    jsonObject.getString("o_cnt"),
                                                    jsonObject.getString("created_at")
                                            ));
                                        }
                                    }
                                    binding.storeCnt.setText(String.valueOf(store_cnt));

                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.setOnItemClickListener(new WorkplaceListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int pos) {
                                            try {
                                                dlog.i("place_latitude : " + shardpref.getString("place_latitude", ""));
                                                dlog.i("place_longitude : " + shardpref.getString("place_longitude", ""));
                                                String owner_id = Response.getJSONObject(pos).getString("owner_id");
                                                String place_name = Response.getJSONObject(pos).getString("name");
                                                String myid = shardpref.getString("USER_INFO_ID", "0");
                                                String place_id = Response.getJSONObject(pos).getString("id");
                                                String save_kind = Response.getJSONObject(pos).getString("save_kind");
                                                String accept_state = Response.getJSONObject(pos).getString("accept_state");
                                                String place_imgpath = Response.getJSONObject(pos).getString("img_path");
                                                dlog.i("owner_id : " + owner_id);
                                                dlog.i("place_name : " + place_name);
                                                dlog.i("myid : " + myid);
                                                dlog.i("place_id : " + place_id);
                                                dlog.i("save_kind : " + save_kind);
                                                dlog.i("accept_state : " + accept_state);
                                                dlog.i("place_imgpath : " + place_imgpath);

                                                shardpref.putString("place_id", place_id);
                                                shardpref.putString("place_name", place_name);
                                                shardpref.putString("place_imgpath", place_imgpath);
                                                if (save_kind.equals("0")) {
                                                    //임시저장된 매장
                                                    pm.PlaceEditGo(mContext);
                                                } else {
                                                    //저장된 매장
//                                                    if (phone.equals("null") || phone.isEmpty() || gender.equals("null") || gender.isEmpty()) {
//                                                        pm.ProfileEditGo(mContext);
//                                                    } else {
                                                        if (accept_state.equals("null")) {
                                                            if (!owner_id.equals(USER_INFO_ID)) {
                                                                accept_state = "1";
                                                            } else {
                                                                accept_state = "0";
                                                            }
                                                        }
                                                        shardpref.putInt("accept_state", Integer.parseInt(accept_state));
                                                        ConfirmUserPlacemember(place_id, myid, owner_id, place_name);
                                                        shardpref.putInt("SELECT_POSITION", 0);
                                                        if (USER_INFO_AUTH.equals("0")) {
                                                            pm.Main(mContext);
                                                        } else {
                                                            pm.Main2(mContext);
                                                        }

//                                                    }
                                                }
                                            } catch (JSONException e) {
                                                dlog.i("GetPlaceList OnItemClickListener Exception :" + e);
                                            }
                                        }
                                    });
                                }
                            dlog.i("SetNoticeListview Thread run! ");
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onFailure (@NonNull Call < String > call, @NonNull Throwable t){
            dlog.e("에러1 = " + t.getMessage());
        }
    });
}


    public void ConfirmUserPlacemember(String place_id, String myid, String owner_id, String place_name) {
        dlog.i("---------SetAllMemberList Check---------");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("getMonth : " + (dc.GET_MONTH.length() == 1 ? "0" + dc.GET_MONTH : dc.GET_MONTH));
        dlog.i("---------SetAllMemberList Check---------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AllMemberInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        AllMemberInterface api = retrofit.create(AllMemberInterface.class);
        Call<String> call = api.getData(place_id, "");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("GetWorkStateInfo function START");
                dlog.e("response 1: " + response.isSuccessful());
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());

                            if (Response.length() == 0) {
                                dlog.i("GET SIZE : " + Response.length());
                                confirm_cnt = 0;
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    confirm_member.add(jsonObject.getString("id"));
                                }
                                dlog.i("confirm_member :" + confirm_member);
                                dlog.i("confirm place member in me : " + confirm_member.contains(myid));
                                if (!owner_id.equals(myid) && !confirm_member.contains(myid)) {
                                    getManagerToken(owner_id, "0", place_id, place_name);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러2 = " + t.getMessage());
            }
        });
    }

    public void getManagerToken(String user_id, String type, String place_id, String place_name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(user_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
                    dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                    dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                    id = Response.getJSONObject(0).getString("id");

                    String token = Response.getJSONObject(0).getString("token");
                    boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                    if (!token.isEmpty() && channelId1) {
                        String message = name + " 님이 " + place_name + " 매장에 참여하셨습니다";
                        PushFcmSend(id, "", message, token, "1", place_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "PlaceListActivity";
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        pm.AuthSelect(mContext);
    }
}
