package com.krafte.kogas.ui.worksite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.adapter.WorkplaceListAdapter;
import com.krafte.kogas.data.PlaceListData;
import com.krafte.kogas.dataInterface.AllMemberInterface;
import com.krafte.kogas.dataInterface.FCMSelectInterface;
import com.krafte.kogas.dataInterface.PlaceListInterface;
import com.krafte.kogas.dataInterface.UserSelectInterface;
import com.krafte.kogas.databinding.ActivityWorksiteBinding;
import com.krafte.kogas.pop.TwoButtonPopActivity;
import com.krafte.kogas.util.DBConnection;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

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
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();

    //Other 변수
    ArrayList<PlaceListData.PlaceListData_list> mList;
    WorkplaceListAdapter mAdapter = null;
    int listitemsize = 0;
    String USER_INFO_EMAIL = "";
    String USER_INFO_NAME = "";
    String USER_INFO_ID = "";
    String USER_INFO_NICKNAME = "";

    //사용자 정보 체크
    Timer timer = new Timer();
    String id = "";
    String name = "";
    String email = "";
    String employee_no = "";
    String department = "";
    String position = "";
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

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        setBtnEvent();


    }

    @Override
    public void onResume() {
        super.onResume();
        LoginCheck(USER_INFO_EMAIL);
        GetPlaceList(USER_INFO_EMAIL);
    }

    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    private void setBtnEvent() {
        binding.addPlace.setOnClickListener(v -> {
            pm.PlaceAddGo(mContext);
        });

        binding.refreshBtn.setVisibility(View.GONE);
        binding.refreshBtn.setOnClickListener(v -> {
            GetPlaceList(USER_INFO_EMAIL);
        });

        binding.shutdownApp.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
            intent.putExtra("data", "앱을 종료 하시겠습니까?");
            intent.putExtra("flag", "종료");
            intent.putExtra("left_btn_txt", "닫기");
            intent.putExtra("right_btn_txt", "종료");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up,0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });
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
                                    employee_no = Response.getJSONObject(0).getString("employee_no");
                                    department = Response.getJSONObject(0).getString("department");
                                    position = Response.getJSONObject(0).getString("position");
                                    img_path = Response.getJSONObject(0).getString("img_path");

                                    shardpref.putString("USER_INFO_ID", id);
                                    shardpref.putString("USER_INFO_NAME", name);
                                    shardpref.putString("USER_INFO_EMAIL", account);
                                    shardpref.putString("USER_INFO_SABUN", employee_no);
                                    shardpref.putString("USER_INFO_SOSOK", department);
                                    shardpref.putString("USER_INFO_JIKGUP", position);
                                    shardpref.putString("USER_INFO_PROFILE_URL", img_path);

                                    dlog.i("id : " + id);
                                    dlog.i("USER_INFO_ID : " +shardpref.getString("USER_INFO_ID", "0"));
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


    public void GetPlaceList(String account) {
        dlog.i("GetPlaceList account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData("", "");
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

                                if (listitemsize != Response.length()) {
                                    mList = new ArrayList<>();
                                    mAdapter = new WorkplaceListAdapter(mContext, mList);
                                    binding.placeList.setAdapter(mAdapter);
                                    binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                    listitemsize = Response.length();
                                    if (Response.length() == 0) {
                                        dlog.i("SetNoticeListview Thread run! ");
                                        dlog.i("GET SIZE : " + Response.length());
                                    } else {

                                        for (int i = 0; i < Response.length(); i++) {
                                            JSONObject jsonObject = Response.getJSONObject(i);
                                            //작업 일자가 없으면 표시되지 않음.
                                            if (!jsonObject.getString("start_date").equals("null")) {
                                                mAdapter.addItem(new PlaceListData.PlaceListData_list(
                                                        jsonObject.getString("id"),
                                                        jsonObject.getString("name"),
                                                        jsonObject.getString("owner_id"),
                                                        jsonObject.getString("owner_name"),
                                                        jsonObject.getString("management_office"),
                                                        jsonObject.getString("address"),
                                                        jsonObject.getString("latitude"),
                                                        jsonObject.getString("longitude"),
                                                        jsonObject.getString("start_time"),
                                                        jsonObject.getString("end_time"),
                                                        jsonObject.getString("img_path"),
                                                        jsonObject.getString("start_date"),
                                                        jsonObject.getString("total_cnt"),
                                                        jsonObject.getString("i_cnt"),
                                                        jsonObject.getString("o_cnt"),
                                                        jsonObject.getString("created_at")
                                                ));
                                            }
                                        }

                                        mAdapter.notifyDataSetChanged();
                                        mAdapter.setOnItemClickListener(new WorkplaceListAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int pos) {
                                                try {
                                                    dlog.i("place_latitude : " + shardpref.getString("place_latitude", ""));
                                                    dlog.i("place_longitude : " + shardpref.getString("place_longitude", ""));
                                                    String owner_id = Response.getJSONObject(pos).getString("owner_id");
                                                    String palce_name = Response.getJSONObject(pos).getString("name");
                                                    String myid = shardpref.getString("USER_INFO_ID", "0");
                                                    String place_id = Response.getJSONObject(pos).getString("id");

                                                    if (department.equals("null") || department.isEmpty() || position.equals("null") || position.isEmpty()) {
                                                        pm.ProfileEditGo(mContext);
                                                    } else {
                                                        ConfirmUserPlacemember(place_id, myid, owner_id, palce_name);
                                                        pm.UserPlsceMapGo(mContext);
                                                    }
                                                } catch (Exception e) {
                                                    dlog.i("GetPlaceList OnItemClickListener Exception :" + e);
                                                }

                                            }
                                        });
                                    }
                                }
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
        Call<String> call = api.getData(place_id);
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
                        String message = department + " " + position + " " + name + " 님이 " + place_name + " 현장에 참여하셨습니다";
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
