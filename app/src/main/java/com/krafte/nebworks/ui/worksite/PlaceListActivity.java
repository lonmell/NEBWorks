package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkplaceListAdapter;
import com.krafte.nebworks.bottomsheet.StoreListBottomSheet;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMCrerateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FCMUpdateInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityWorksiteBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    String event = "";

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

            //Singleton Area - not // 회원 정보 조회 전
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_NAME  = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "-99");// 0:점주 / 1:근로자

            //shardpref Area
            event           = shardpref.getString("event", "");

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            setBtnEvent();
            LoginCheck(USER_INFO_EMAIL);

            //사용자 ID로 FCM 보낼수 있도록 토픽 세팅
            FirebaseMessaging.getInstance().subscribeToTopic("P" + USER_INFO_ID).addOnCompleteListener(task -> {
                String msg = getString(R.string.msg_subscribed);
                if (!task.isSuccessful()) {
                    msg = getString(R.string.msg_subscribe_failed);
                }
                dlog.i("msg : " + msg);
            });
            getFCMToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        shardpref.remove("page_state");
        dlog.i("-----onResume-----");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("USER_INFO_EMAIL : " + USER_INFO_EMAIL);
        dlog.i("USER_INFO_NAME : " + USER_INFO_NAME);
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("-----onResume-----");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //5초마다 실행
                if(!USER_INFO_ID.isEmpty() && !USER_INFO_EMAIL.isEmpty() && !USER_INFO_AUTH.isEmpty()){
                    GetPlaceList();
                    getNotReadFeedcnt();
                    timer.cancel();
                }
            }
        };
        timer.schedule(timerTask,0,1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            if(event.equals("out_store")){
                super.onBackPressed();
            }else{
                Logout();
            }
        });
        binding.addPlace.setOnClickListener(v -> {
            onStartAuth();
        });
        binding.addPlace2.setOnClickListener(v -> {
            onStartAuth();
        });
        binding.notiArea.setOnClickListener(v -> {
            shardpref.putString("returnPage","PlaceListActivity");
            pm.FeedList(mContext);
        });
    }

    private void onStartAuth() {
        if (USER_INFO_AUTH.equals("0")) {
            shardpref.putString("page_state","0");//첫 입력
            pm.PlaceAddGo(mContext);
        } else {
            StoreListBottomSheet slb = new StoreListBottomSheet();
            slb.show(getSupportFragmentManager(), "StoreListBottomSheet");
            slb.setOnClickListener01(v -> pm.PlaceSearch(mContext));
            slb.setOnClickListener02(v -> pm.PlaceAddGo(mContext));
            slb.setOnClickListener03(v -> pm.Career(mContext));
        }
    }

    RetrofitConnect rc = new RetrofitConnect();

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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("LoginCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("LoginCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
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
        Call<String> call = api.getData("", USER_INFO_ID, USER_INFO_AUTH);
        //
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                            try {
                                //Array데이터를 받아올 때
                                store_cnt = 0;
                                JSONArray Response = new JSONArray(jsonResponse);
                                if (USER_INFO_AUTH.equals("0")) {
                                    binding.storeCntTv.setText("관리중인 매장");
                                } else {
                                    binding.storeCntTv.setText("참여중인 매장");
                                }
                                mList = new ArrayList<>();
                                mAdapter = new WorkplaceListAdapter(mContext, mList);
                                binding.placeList.setAdapter(mAdapter);
                                binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                dlog.i("SIZE : " + Response.length());
                                if (response.body().equals("[]")) {
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
                                                jsonObject.getString("owner_phone"),
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
                                dlog.i("SetNoticeListview Thread run! ");
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

    public void getNotReadFeedcnt() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData("", "", "","1",USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("getNotReadFeedcnt");
                dlog.e( "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        if(!response.body().equals("[]") && Response.length() != 0){
                            String NotRead = Response.getJSONObject(0).getString("notread_feed");
                            if(NotRead.equals("0") || NotRead.isEmpty()){
                                binding.notiRed.setVisibility(View.INVISIBLE);
                            }else{
                                binding.notiRed.setVisibility(View.VISIBLE);
                            }
                        }else{
                            binding.notiRed.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e( "에러 = " + t.getMessage());
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
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
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
                    boolean channelId1 = Response.getJSONObject(0).getString("channel4").equals("1");
                    if (!token.isEmpty() && channelId1) {
                        String message = name + " 님이 " + place_name + " 매장에 참여하셨습니다";
                        PushFcmSend(id, "", message, token, "4", place_id);
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


    String user_id = "";
    String type = "";
    String get_token = "";
    String channel1 = "1";
    String channel2 = "1";
    String channel3 = "1";
    String channel4 = "1";


    //본인 토큰 생성
    @SuppressLint("LongLogTag")
    public void getFCMToken() {
        type = USER_INFO_AUTH;
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
//                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d("TAG", msg);
                    dlog.i("getFCMToken token : " + token);
                    FcmStateSelect(token);
                });

    }

    private void FcmStateSelect(String token) {
        //메인페이지 처음 들어왔을때 생성 - 본인

        dlog.i("-----FcmStateSelect-----");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("type : " + type);
        dlog.i("-----FcmStateSelect-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {

                                if (jsonResponse.replace("[", "").replace("]", "").length() == 0) {
                                    id = "";
                                    user_id = USER_INFO_ID;
                                    get_token = "";
                                    type = USER_INFO_AUTH;
                                    channel1 = "1";
                                    channel2 = "1";
                                    channel3 = "1";
                                    channel4 = "1";
                                } else {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    id = Response.getJSONObject(0).getString("id");
                                    user_id = Response.getJSONObject(0).getString("user_id");
                                    type = Response.getJSONObject(0).getString("type");
                                    get_token = Response.getJSONObject(0).getString("token");
                                    channel1 = Response.getJSONObject(0).getString("channel1");
                                    channel2 = Response.getJSONObject(0).getString("channel2");
                                    channel3 = Response.getJSONObject(0).getString("channel3");
                                    channel4 = Response.getJSONObject(0).getString("channel4");

                                    shardpref.putString("token", token);
                                    shardpref.putString("type", type);
                                    shardpref.putBoolean("channelId1", channel1.equals("1"));
                                    shardpref.putBoolean("channelId2", channel2.equals("1"));
                                    shardpref.putBoolean("channelId3", channel3.equals("1"));
                                    shardpref.putBoolean("channelId4", channel4.equals("1"));

                                    dlog.i("channel1 : " + channel1);
                                    dlog.i("channel2 : " + channel2);
                                    dlog.i("channel3 : " + channel3);
                                    dlog.i("channel4 : " + channel4);
                                }
                                if (get_token.isEmpty()) {
                                    dlog.i("getFCMToken FcmTokenCreate");
                                    FcmTokenCreate(token);
                                } else {
                                    dlog.i("getFCMToken FcmTokenUpdate");
                                    FcmTokenUpdate(token);
                                }
                            } catch (Exception e) {
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
    private void FcmTokenCreate(String token) {
        //메인페이지 처음 들어왔을때 생성 - 본인
        dlog.i("------FcmTokenCreate-------");
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("type :" + type);
        dlog.i("token :" + token);
        dlog.i("channel1 :" + channel1);
        dlog.i("channel2 :" + channel2);
        dlog.i("channel3 :" + channel3);
        dlog.i("channel4 :" + channel4);
        dlog.i("------FcmTokenCreate-------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMCrerateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMCrerateInterface api = retrofit.create(FCMCrerateInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, type, token, channel1, channel2, channel3, channel4);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("FcmTokenCreate jsonResponse length : " + response.body().length());
                            dlog.i("FcmTokenCreate jsonResponse : " + response.body());
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

    public void FcmTokenUpdate(String token) {
        dlog.i("------FcmTokenUpdate-------");
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("type :" + type);
        dlog.i("token :" + token);
        dlog.i("channel1 :" + channel1);
        dlog.i("channel2 :" + channel2);
        dlog.i("channel3 :" + channel3);
        dlog.i("channel4 :" + channel4);
        dlog.i("------FcmTokenUpdate-------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMUpdateInterface api = retrofit.create(FCMUpdateInterface.class);
        Call<String> call = api.getData(id, token, channel1, channel2, channel3, channel4);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                if (jsonResponse.replace("\"", "").equals("success")) {
                    dlog.i("FcmTokenUpdate jsonResponse length : " + jsonResponse.length());
                    dlog.i("FcmTokenUpdate jsonResponse : " + jsonResponse);
                } else {
                    Toast.makeText(mContext, "네트워크가 정상적이지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    private void Logout(){
        if(USER_INFO_AUTH.equals("-1")){
            super.onBackPressed();
        }else{
            Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
            intent.putExtra("data", "로그아웃하시겠습니까?");
            intent.putExtra("flag", "로그아웃");
            intent.putExtra("left_btn_txt", "닫기");
            intent.putExtra("right_btn_txt", "로그아웃");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(event.equals("out_store")){
            super.onBackPressed();
        }else{
            Logout();
        }
    }
}
