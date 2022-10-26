package com.krafte.kogas.ui.BottomNavi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.adapter.WorkstatusDataListAdapter;
import com.krafte.kogas.data.WorkStatusData;
import com.krafte.kogas.dataInterface.AllMemberInterface;
import com.krafte.kogas.dataInterface.WorkStatusDataInterface;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkState1Activity extends AppCompatActivity {

    private final static String TAG = "EmployerWorkGotoList";
    Context mContext;

    //XML ID
    RecyclerView StatusList01, StatusList02;
    TextView top_num01, top_num02, top_num03, top_num04,workstatus_tv;
    CardView notice_cnt_y;
    RelativeLayout login_alert_text;
    ImageView loading_view,workstatus_icon;


    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String place_id;

    int SELECTED_POSITION = 0;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    Handler mHandler;
    DateCurrent dc = new DateCurrent();
    RetrofitConnect rc = new RetrofitConnect();

    ArrayList<WorkStatusData.WorkStatusData_list> mList;
    ArrayList<WorkStatusData.WorkStatusData_list> mList2;
    WorkstatusDataListAdapter mAdapter = null; // -- 근무중 commute 0
    WorkstatusDataListAdapter mAdapter2 = null; // -- 퇴근 commute 1
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    int topNum1 = 0;
    int topNum2 = 0;
    int topNum3 = 0;
    int topNum4 = 0;

    public void setContentLayout() {
        //main xml
        StatusList01 = findViewById(R.id.StatusList01);
        StatusList02 = findViewById(R.id.StatusList02);

        top_num01 = findViewById(R.id.top_num01);
        top_num02 = findViewById(R.id.top_num02);
        top_num03 = findViewById(R.id.top_num03);
        top_num04 = findViewById(R.id.top_num04);

        loading_view = findViewById(R.id.loading_view);
        notice_cnt_y = findViewById(R.id.notice_cnt_y);
        login_alert_text = findViewById(R.id.login_alert_text);

        workstatus_tv = findViewById(R.id.workstatus_tv);
        workstatus_icon = findViewById(R.id.workstatus_icon);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workstate1);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
        place_id = shardpref.getString("place_id", "");

        workstatus_icon = findViewById(R.id.workstatus_icon);
        workstatus_tv = findViewById(R.id.workstatus_tv);

        workstatus_icon.setBackgroundResource(R.drawable.workstatus_on_resize);
        workstatus_tv.setTextColor(Color.parseColor("#6395EC"));

        dlog.i("place_id : "+place_id);
        setContentLayout();
        setBtnEvent();
        SetAllMemberList();
        GetWorkStateInfo(place_id);
    }

    @Override
    public void onResume() {
        super.onResume();
        topNum1 = 0;
        topNum2 = 0;
        topNum3 = 0;
        topNum4 = 0;
//        GetStoreNewsCnt();
    }



//    public void GetStoreNewsCnt() {
//        runOnUiThread(() -> {
//            login_alert_text.setVisibility(View.VISIBLE);
//        });
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(StoreNewsInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        StoreNewsInterface api = retrofit.create(StoreNewsInterface.class);
//        Call<String> call = api.getDate(USER_INFO_ID, store_no);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                login_alert_text.setVisibility(View.GONE);
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
//                    dlog.e("ConnectThread_UserInfo onSuccess" + jsonResponse);
//                    try {
//                        //Array데이터를 받아올 때
//                        JSONArray Response = new JSONArray(jsonResponse);
//                        String newNotify = Response.getJSONObject(0).getString("newNotify");
//                        String newMemberRequest = Response.getJSONObject(0).getString("newMemberRequest");
//                        String newApproval = Response.getJSONObject(0).getString("newApproval");
//                        dlog.i("newNotify : " + newNotify);
//                        dlog.i("newMemberRequest : " + newMemberRequest);
//                        dlog.i("newApproval : " + newApproval);
//
//                        if(newNotify.equals("0") && newMemberRequest.equals("0") && newApproval.equals("0")){
//                            notice_cnt_y.setVisibility(View.GONE);
//                        }else{
//                            notice_cnt_y.setVisibility(View.VISIBLE);
//                        }
//                        login_alert_text.setVisibility(View.GONE);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                login_alert_text.setVisibility(View.GONE);
//                dlog.e("에러1 = " + t.getMessage());
//            }
//        });
//    }

    public void btnOnclick(View view) {
        if (view.getId() == R.id.out_store) {
            pm.PlaceListBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            pm.MainBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation02) {
            pm.PlaceWorkGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation03) {
            pm.CalenderGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation05) {
            pm.MoreGo(mContext);
        }
    }

    private void setBtnEvent() {
    }

    /*직원 전체 리스트 START*/
    public void SetAllMemberList() {
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
                            top_num01.setText(String.valueOf(Response.length()) + "\n전체인원");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }

    public void GetWorkStateInfo(String place_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkStatusDataInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkStatusDataInterface api = retrofit.create(WorkStatusDataInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetWorkStateInfo function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                runOnUiThread(() -> {

                    if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                        try {
                            JSONArray Response = new JSONArray(response.body());
                            mList = new ArrayList<>();
                            mAdapter = new WorkstatusDataListAdapter(mContext, mList);
                            StatusList01.setAdapter(mAdapter);
                            StatusList01.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            mList2 = new ArrayList<>();
                            mAdapter2 = new WorkstatusDataListAdapter(mContext, mList2);
                            StatusList02.setAdapter(mAdapter2);
                            StatusList02.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (jsonObject.getString("commute").equals("0")){
                                        mAdapter.addItem(new WorkStatusData.WorkStatusData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("department"),
                                                jsonObject.getString("position"),
                                                jsonObject.getString("commute")
                                        ));
                                    }else if(jsonObject.getString("commute").equals("1") ){
                                        mAdapter2.addItem(new WorkStatusData.WorkStatusData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("department"),
                                                jsonObject.getString("position"),
                                                jsonObject.getString("commute")
                                        ));
                                    }
                                    if (jsonObject.getString("commute").equals("0")) {
                                        topNum2 += 1;
                                    }
                                    if(jsonObject.getString("commute").equals("1")){
                                        topNum3 += 1;
                                    }
                                }

                                Log.i(TAG, "근무 중 : " + topNum2);
                                Log.i(TAG, "퇴근 : " + topNum3);
                                top_num02.setText(topNum2 + "\n근무중");
                                top_num03.setText(topNum3 + "\n퇴근");
                                mAdapter.notifyDataSetChanged();
                            }
//                            topNum1 = topNum2 + topNum3 + topNum4;
//                            top_num01.setText(topNum1 + "\n전체인원");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }

    /*출퇴근 리스트 END*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pm.MainBack(mContext);
    }

    //    //-------몰입화면 설정
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }
//
//    private void hideSystemUI() {
//        // Enables regular immersive mode.
//        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
//        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
//    }
//    //-------몰입화면 설정
}
