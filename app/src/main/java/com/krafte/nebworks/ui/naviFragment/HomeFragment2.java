package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MainMemberLAdapter;
import com.krafte.nebworks.adapter.MainNotiLAdapter;
import com.krafte.nebworks.adapter.MainTaskLAdapter;
import com.krafte.nebworks.data.MainMemberLData;
import com.krafte.nebworks.data.MainNotiData;
import com.krafte.nebworks.data.MainTaskData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMCrerateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FCMUpdateInterface;
import com.krafte.nebworks.dataInterface.InOutInsertInterface;
import com.krafte.nebworks.dataInterface.InOutLogInterface;
import com.krafte.nebworks.dataInterface.MainContentsInterface;
import com.krafte.nebworks.dataInterface.PlaceMemberUpdateBasic;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.databinding.Homefragment2Binding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.GpsTracker;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 2022 11 10 방창배 작성 / 근로자용 페이지
 */
public class HomeFragment2 extends Fragment {
    private final static String TAG = "HomeFragment2";
    private Homefragment2Binding binding;

    Context mContext;
    Activity activity;

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String registr_num = "";
    String store_kind = "";
    int accept_state = 0;
    String place_address = "";
    Double place_latitude = 0.0;
    Double place_longitude = 0.0;
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";
    String place_totalcnt = "";

    String place_pay_day = "";
    String place_test_period = "";
    String place_vacation_select = "";
    String place_insurance = "";
    String place_save_kind = "";
    String place_wifi_name = "";
    String place_icnt = "";
    String place_ocnt = "";
    String kind = "";

    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_AUTH = "";


    String mem_id = "";
    String mem_kind = "";
    String mem_name = "";
    String mem_phone = "";
    String mem_gender = "";
    String mem_jumin = "";
    String mem_join_date = "";
    String mem_state = "";
    String mem_jikgup = "";
    String mem_pay = "";
    String mem_img_path = "";
    String io_state = "";
    String input_date = "";
    String in_time = "";
    String jongeob = "";

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    RetrofitConnect rc = new RetrofitConnect();
    GpsTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;

    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate_age = new SimpleDateFormat("yyyy");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate_time = new SimpleDateFormat("HH:mm:ss");

    ArrayList<MainTaskData.MainTaskData_list> mList;
    MainTaskLAdapter mAdapter = null;

    ArrayList<MainNotiData.MainNotiData_list> mList2;
    MainNotiLAdapter mAdapter2 = null;

    ArrayList<MainMemberLData.MainMemberLData_list> mList3;
    MainMemberLAdapter mAdapter3 = null;

    public static HomeFragment2 newInstance(int number) {
        HomeFragment2 fragment = new HomeFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
            Log.i(TAG, "num : " + num);
        }
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.homefragment2, container, false);
        binding = Homefragment2Binding.inflate(inflater);
        mContext = inflater.getContext();

        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            setBtnEvent();
            dlog.i("HomeFragment 2 START!");
            place_id = shardpref.getString("place_id", "0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            accept_state = shardpref.getInt("accept_state", -99);
            input_date = shardpref.getString("input_date", "-1");
            in_time = shardpref.getString("in_time", "");
            shardpref.putInt("SELECT_POSITION",0);
            if (USER_INFO_AUTH.isEmpty()) {
                setDummyData();
            }

            //사용자 ID로 FCM 보낼수 있도록 토픽 세팅
            FirebaseMessaging.getInstance().subscribeToTopic("P" + USER_INFO_ID).addOnCompleteListener(task -> {
                String msg = getString(R.string.msg_subscribed);
                if (!task.isSuccessful()) {
                    msg = getString(R.string.msg_subscribe_failed);
                }
                dlog.i("msg : " + msg);
            });

            //0-관리자 / 1- 근로자
            dlog.i("gotoplace location view USER_INFO_AUTH : " + USER_INFO_AUTH);
            //USER_INFO_AUTH 가 -1일때
//            USER_INFO_AUTH = place_owner_id.equals(USER_INFO_ID) ? "0" : "1";
//            shardpref.putString("USER_INFO_AUTH", USER_INFO_AUTH);

            if (accept_state == 3) {
                //승인 대기중
                binding.acceptArea.setVisibility(View.VISIBLE);
            } else {
                binding.acceptArea.setVisibility(View.GONE);
            }

            String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            binding.ioMytime.setText(dc.GET_YEAR + "년 " + dc.GET_MONTH + "월 " + dc.GET_DAY + "일");
            binding.todayWorkdate.setText(dc.GET_YEAR + "년 " + dc.GET_MONTH + "월 " + dc.GET_DAY + "일");
            binding.inTime.setText(in_time);

            binding.cardview02.setOnClickListener(v -> {
                if (USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                    shardpref.putString("stub_place_id", place_id);
                    shardpref.putString("stub_user_id", USER_INFO_ID);
                    shardpref.putString("stub_user_account", USER_INFO_EMAIL);
                    shardpref.putString("change_place_name", place_name);
                    pm.MemberDetail(mContext);
                }
            });
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
//        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        shardpref.remove("Tap");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //5초마다 실행
                if(!USER_INFO_ID.isEmpty() && !USER_INFO_EMAIL.isEmpty() && !place_id.isEmpty() && !USER_INFO_AUTH.isEmpty()){
                    UserCheck();
                    getPlaceData();
                    PlaceWorkCheck(place_id, USER_INFO_AUTH, "0");
                    PlaceWorkCheck(place_id, USER_INFO_AUTH, "1");
                    PlaceWorkCheck(place_id, USER_INFO_AUTH, "2");
                    PlaceWorkCheck(place_id, USER_INFO_AUTH, "3");
                    PlaceWorkCheck(place_id, USER_INFO_AUTH, "4");
                    InOutLogMember();
//                    getFCMToken();
                    timer.cancel();
                }
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    public void setDummyData() {
        mList = new ArrayList<>();
        binding.mainTaskList.setVisibility(View.VISIBLE);
        mAdapter = new MainTaskLAdapter(mContext, mList);
        binding.mainTaskList.setAdapter(mAdapter);
        binding.mainTaskList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mAdapter.addItem(new MainTaskData.MainTaskData_list(
                "할 일",
                "2023년 1월 1일",
                "01",
                "23"
        ));

        mList2 = new ArrayList<>();
        binding.mainNotiList.setVisibility(View.VISIBLE);
        mAdapter2 = new MainNotiLAdapter(mContext, mList2);
        binding.mainNotiList.setAdapter(mAdapter2);
        binding.mainNotiList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mAdapter2.addItem(new MainNotiData.MainNotiData_list(
                "공지 사항",
                "2022년 01월 01일"
        ));

        mList3 = new ArrayList<>();
        binding.importantList.setVisibility(View.VISIBLE);
        mAdapter3 = new MainMemberLAdapter(mContext, mList3);
        binding.importantList.setAdapter(mAdapter3);
        binding.importantList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mAdapter3.addItem(new MainMemberLData.MainMemberLData_list(
                "0",
                "2023년 01년 01일",
                "1",
                "김이름",
                "",
                "2,000,000"
        ));
    }

    public void InOutLogMember() {//출퇴근상황 / 날짜가 변경됬을때는 퇴근중이 아닌 미출근 / 휴무날에는 휴무로 표시해야함
        dlog.i("--------InOutLogMember--------");
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("--------InOutLogMember--------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InOutLogInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        InOutLogInterface api = retrofit.create(InOutLogInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        String jsonResponse = rc.getBase64decode(response.body());
                        if (jsonResponse.replace("[", "").replace("]", "").length() == 0) {
                            //그날 최초 출근
                            kind = "-1";
//                            InOutInsert("0");
                            binding.oArea.setVisibility(View.GONE);
                            binding.ioArea.setVisibility(View.VISIBLE);
                        } else if (jsonResponse.replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && jsonResponse != null) {
                                dlog.i("InOutLogMember jsonResponse length : " + jsonResponse.length());
                                dlog.i("InOutLogMember jsonResponse : " + jsonResponse);
                                try {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    kind = Response.getJSONObject(0).getString("kind");
                                    dlog.i("InOutLogMember kind : " + kind);
                                    if (kind.equals("1")) {
                                        kind = "-1";
                                        binding.oArea.setVisibility(View.GONE);
                                        binding.ioArea.setVisibility(View.VISIBLE);
                                    }else{
                                        binding.oArea.setVisibility(View.VISIBLE);
                                        binding.ioArea.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        dlog.i("InOutLogMember kind 2 : " + kind);
                        if (kind.equals("-1")) {
                            binding.ioImg.setBackgroundResource(R.drawable.workinout01);
                            binding.state.setText("현재 퇴근 중");
                        } else if (kind.equals("0")) {
                            binding.ioImg.setBackgroundResource(R.drawable.workinout02);
                            binding.state.setText("현재 출근 중");
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

    public void setBtnEvent() {
        binding.approvalGo.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putString("stub_place_id", place_id);
                shardpref.putString("stub_user_id", USER_INFO_ID);
                shardpref.putString("stub_user_account", USER_INFO_EMAIL);
                pm.MemberDetail(mContext);
            }
        });
        binding.itemArea.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putString("USER_INFO_AUTH", "1");
                shardpref.putString("event", "out_store");
                pm.PlaceList(mContext);
            }
        });

        binding.memberManagement01.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                pm.MemberManagement(mContext);
            }
        });


        binding.ioArea.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (kind.equals("-1") || kind.equals("0")) {
                    if (kind.equals("-1")) {
                        kind = "0";
                    } else {
                        kind = "1";
                    }
                    shardpref.putString("kind", kind);
                    pm.EmployeeProcess(mContext);
                }
            }
        });
        binding.oBtn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putString("kind", "1");
                pm.EmployeeProcess(mContext);
            }
        });
        binding.acceptBtn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                UpdateDirectMemberBasic();
            }
        });

        binding.homeMenu03.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                dlog.i("급여관리");
                pm.PayManagement(mContext);
            }
        });

        binding.homeMenu04.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                dlog.i("근로계약서 전체 관리");
                pm.ContractFragment(mContext);
            }
        });

        binding.cardview01.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                pm.FeedList(mContext);
            }
        });

        binding.todoMore.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putInt("SELECT_POSITION", 1);
                pm.Main2(mContext);
            }
        });
    }

    private void InOutInsert(String kind) {
        dlog.i("--------InOutInsert--------");
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("kind - 0출근, 1퇴근 : " + kind);
        dlog.i("--------InOutInsert--------");

        if (kind.equals("0")) {
            io_state = "출근";
        } else {
            io_state = "퇴근";
        }
        binding.state.setText("현재" + io_state + " 중");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InOutInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        InOutInsertInterface api = retrofit.create(InOutInsertInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, kind);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        if (jsonResponse.replace("[", "").replace("]", "").replace("\"", "").length() == 0) {
                            //최초 출근

                        } else if (jsonResponse.replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && response.body() != null) {
                                dlog.i("LoginCheck jsonResponse length : " + jsonResponse.length());
                                dlog.i("LoginCheck jsonResponse : " + jsonResponse);
                                try {
                                    if (jsonResponse.replace("[", "").replace("]", "").replace("\"", "").equals("success")) {
//                                        timer.cancel();
//                                        Intent intent = new Intent(mContext, InoutPopActivity.class);
//                                        intent.putExtra("title", io_state + " 처리되었습니다.");
//                                        intent.putExtra("time", GET_TIME);
//                                        intent.putExtra("state", "1");
//                                        intent.putExtra("store_name", place_name);
//                                        mContext.startActivity(intent);
//                                        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                        if (!place_owner_id.equals(USER_INFO_ID)) {
//                                            getEmployerToken();
                                        }
                                        InOutLogMember();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    private void getPlaceData() {
        dlog.i("PlaceCheck place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceThisDataInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceThisDataInterface api = retrofit.create(PlaceThisDataInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    place_name = Response.getJSONObject(0).getString("name");
                                    place_owner_id = Response.getJSONObject(0).getString("owner_id");
                                    place_owner_name = Response.getJSONObject(0).getString("owner_name");
                                    registr_num = Response.getJSONObject(0).getString("registr_num");
                                    store_kind = Response.getJSONObject(0).getString("store_kind");
                                    place_address = Response.getJSONObject(0).getString("address");
                                    place_latitude = Double.parseDouble(Response.getJSONObject(0).getString("latitude"));
                                    place_longitude = Double.parseDouble(Response.getJSONObject(0).getString("longitude"));
                                    place_pay_day = Response.getJSONObject(0).getString("pay_day");
                                    place_test_period = Response.getJSONObject(0).getString("test_period");
                                    place_vacation_select = Response.getJSONObject(0).getString("vacation_select");
                                    place_insurance = Response.getJSONObject(0).getString("insurance");
                                    place_start_time = Response.getJSONObject(0).getString("start_time");
                                    place_end_time = Response.getJSONObject(0).getString("end_time");
                                    place_save_kind = Response.getJSONObject(0).getString("save_kind");
                                    place_wifi_name = Response.getJSONObject(0).getString("wifi_name");
                                    place_img_path = Response.getJSONObject(0).getString("img_path");
                                    place_start_date = Response.getJSONObject(0).getString("start_date");
                                    place_created_at = Response.getJSONObject(0).getString("created_at");
                                    place_icnt = Response.getJSONObject(0).getString("i_cnt");
                                    place_ocnt = Response.getJSONObject(0).getString("o_cnt");
                                    place_totalcnt = Response.getJSONObject(0).getString("total_cnt");

                                    Glide.with(mContext).load(place_img_path)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.no_image)
                                            .skipMemoryCache(true)
                                            .into(binding.storeThumnail);

                                    dlog.i("place_owner_id : " + place_owner_id);
                                    dlog.i("USER_INFO_ID : " + USER_INFO_ID);
                                    dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
                                    shardpref.putString("USER_INFO_AUTH", USER_INFO_AUTH);
                                    shardpref.putString("place_end_time",place_end_time);
                                    binding.title.setText(place_name);
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

    public void UserCheck() {
        dlog.i("---------UserCheck---------");
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("getMonth : " + (dc.GET_MONTH.length() == 1 ? "0" + dc.GET_MONTH : dc.GET_MONTH));
        dlog.i("---------UserCheck---------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AllMemberInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        AllMemberInterface api = retrofit.create(AllMemberInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("UserCheck function START");
                dlog.e("response 1: " + response.isSuccessful());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            try {
                                mem_id = Response.getJSONObject(0).getString("id");
                                mem_name = Response.getJSONObject(0).getString("name");
                                mem_phone = Response.getJSONObject(0).getString("phone");
                                mem_gender = Response.getJSONObject(0).getString("gender");
                                mem_img_path = Response.getJSONObject(0).getString("img_path");
                                mem_jumin = Response.getJSONObject(0).getString("jumin");
                                mem_kind = Response.getJSONObject(0).getString("kind");
                                mem_join_date = Response.getJSONObject(0).getString("join_date");
                                mem_state = Response.getJSONObject(0).getString("state");
                                mem_jikgup = Response.getJSONObject(0).getString("jikgup");
                                mem_pay = Response.getJSONObject(0).getString("pay");
                                jongeob = Response.getJSONObject(0).getString("jongeob");
                                shardpref.putString("jongeob",jongeob);
                                dlog.i("------UserCheck-------");
                                USER_INFO_ID = mem_id;
                                dlog.i("프로필 사진 url : " + mem_img_path);
                                dlog.i("직원소속구분분 : " + (mem_kind.equals("0") ? "정직원" : "협력업체"));
                                dlog.i("성명 : " + mem_name);
                                dlog.i("부서 : " + mem_jikgup);
                                dlog.i("급여 : " + mem_pay);
                                dlog.i("------UserCheck-------");

                                shardpref.putString("mem_name",mem_name);
                                if (USER_INFO_AUTH.isEmpty()) {
                                    binding.ioTime.setText("김이름님 오늘도 화이팅하세요!");
                                } else {
                                    binding.ioTime.setText(mem_name + "님 오늘도 화이팅하세요!");
                                }
//                                getFCMToken();
                            } catch (Exception e) {
                                dlog.i("UserCheck Exception : " + e);
                            }
                        } catch (Exception e) {
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

    public void PlaceWorkCheck(String place_id, String auth, String kind) {
        dlog.i("PlaceWorkCheck place_id : " + place_id);
        dlog.i("PlaceWorkCheck auth : " + auth);
        dlog.i("PlaceWorkCheck kind : " + kind);
        dlog.i("PlaceWorkCheck USER_INFO_ID : " + USER_INFO_ID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainContentsInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainContentsInterface api = retrofit.create(MainContentsInterface.class);
        Call<String> call = api.getData(place_id, auth, USER_INFO_ID, kind);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("PlaceWorkCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("PlaceWorkCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]") && !jsonResponse.equals("null")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    try {
                                        if (kind.equals("0")) {
                                            mList = new ArrayList<>();
                                            mAdapter = new MainTaskLAdapter(mContext, mList);
                                            binding.mainTaskList.setAdapter(mAdapter);
                                            binding.mainTaskList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                            dlog.i("Task Get SIZE : " + Response.length());
                                            if (Response.length() == 0) {
                                                dlog.i("SetNoticeListview Thread run! ");
                                                dlog.i("GET SIZE : " + Response.length());
                                                binding.mainTaskList.setVisibility(View.GONE);
                                                binding.limitTasktv.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.mainTaskList.setVisibility(View.VISIBLE);
                                                binding.limitTasktv.setVisibility(View.GONE);
                                                for (int i = 0; i < Response.length(); i++) {
                                                    JSONObject jsonObject = Response.getJSONObject(i);
                                                    mAdapter.addItem(new MainTaskData.MainTaskData_list(
                                                            jsonObject.getString("title"),
                                                            jsonObject.getString("end_date"),
                                                            jsonObject.getString("end_hour"),
                                                            jsonObject.getString("end_min")
                                                    ));
                                                }

                                                mAdapter.setOnItemClickListener(new MainTaskLAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View v, int position) {
                                                        if (USER_INFO_AUTH.isEmpty()) {
                                                            isAuth();
                                                        } else {}
                                                    }
                                                });

                                            }
                                            mAdapter.notifyDataSetChanged();
                                        } else if (kind.equals("1")) {
                                            mList2 = new ArrayList<>();
                                            mAdapter2 = new MainNotiLAdapter(mContext, mList2);
                                            binding.mainNotiList.setAdapter(mAdapter2);
                                            binding.mainNotiList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                            dlog.i("Noti Get SIZE : " + Response.length());
                                            if (Response.length() == 0) {
                                                dlog.i("SetNoticeListview Thread run! ");
                                                dlog.i("GET SIZE : " + Response.length());
                                                binding.mainNotiList.setVisibility(View.GONE);
                                                binding.limitNotitv.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.mainNotiList.setVisibility(View.VISIBLE);
                                                binding.limitNotitv.setVisibility(View.GONE);
                                                for (int i = 0; i < Response.length(); i++) {
                                                    JSONObject jsonObject = Response.getJSONObject(i);
                                                    mAdapter2.addItem(new MainNotiData.MainNotiData_list(
                                                            jsonObject.getString("feed_title"),
                                                            jsonObject.getString("updated_at")
                                                    ));
                                                }

                                                mAdapter2.setOnItemClickListener(new MainNotiLAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View v, int position) {
                                                        if (USER_INFO_AUTH.isEmpty()) {
                                                            isAuth();
                                                        } else {
                                                            pm.FeedList(mContext);
                                                        }
                                                    }
                                                });
                                            }
                                            mAdapter.notifyDataSetChanged();
                                        } else if (kind.equals("2")) {
                                            binding.inCnt.setText(Response.getJSONObject(0).getString("i_cnt"));
                                            binding.outCnt.setText(Response.getJSONObject(0).getString("o_cnt"));
                                            binding.notinCnt.setText(Response.getJSONObject(0).getString("absence_cnt"));
                                            binding.restCnt.setText(Response.getJSONObject(0).getString("rest_cnt"));
                                            dlog.i("-----MainData-----");
                                            dlog.i("i_cnt : " + Response.getJSONObject(0).getString("i_cnt"));
                                            dlog.i("o_cnt : " + Response.getJSONObject(0).getString("o_cnt"));
                                            dlog.i("absence_cnt : " + Response.getJSONObject(0).getString("absence_cnt"));
                                            dlog.i("rest_cnt : " + Response.getJSONObject(0).getString("rest_cnt"));
                                        } else if (kind.equals("3")) {
                                            int allPay = 0;
                                            for (int i = 0; i < Response.length(); i++) {
                                                allPay += Integer.parseInt(Response.getJSONObject(i).getString("recent_pay").replace(",", ""));
                                            }
                                            DecimalFormat myFormatter = new DecimalFormat("###,###");
                                            binding.paynum.setText(myFormatter.format(allPay) + "원");
                                            dlog.i("allPay : " + myFormatter.format(allPay));
                                        } else if(kind.equals("4")){
                                            dlog.i("kind 4 Result : " + jsonResponse);

                                            mList3 = new ArrayList<>();
                                            mAdapter3 = new MainMemberLAdapter(mContext, mList3);
                                            binding.importantList.setAdapter(mAdapter3);
                                            binding.importantList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                            dlog.i("SIZE : " + Response.length());
                                            if (Response.length() == 0) {
                                                dlog.i("SetNoticeListview Thread run! ");
                                                dlog.i("GET SIZE : " + Response.length());
                                                binding.importantList.setVisibility(View.GONE);
                                                binding.emptyPayarea.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.importantList.setVisibility(View.VISIBLE);
                                                binding.emptyPayarea.setVisibility(View.GONE);
                                                for (int i = 0; i < Response.length(); i++) {
                                                    JSONObject jsonObject = Response.getJSONObject(i);
                                                    mAdapter3.addItem(new MainMemberLData.MainMemberLData_list(
                                                            jsonObject.getString("id"),
                                                            jsonObject.getString("join_date"),
                                                            jsonObject.getString("user_id"),
                                                            jsonObject.getString("user_name"),
                                                            jsonObject.getString("user_img"),
                                                            jsonObject.getString("recent_pay")
                                                    ));
                                                }
                                                mAdapter3.setOnItemClickListener(new MainMemberLAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View v, int position) {
                                                        if (USER_INFO_AUTH.isEmpty()) {
                                                            isAuth();
                                                        } else {}
                                                    }
                                                });

                                            }
                                            mAdapter3.notifyDataSetChanged();
                                        }
                                    } catch (Exception e) {
                                        dlog.i("UserCheck Exception : " + e);
                                    }
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

    private void UpdateDirectMemberBasic() {
        //직접 입력직원 기본정보 업데이트
        dlog.i("------UpdateDirectMemberBasic------");
        dlog.i("place_id : " + place_id);
        dlog.i("mem_id : " + mem_id);
        dlog.i("mem_name : " + mem_name);
        dlog.i("mem_phone : " + mem_phone);
        dlog.i("mem_jumin : " + mem_jumin);
        dlog.i("mem_kind : 1");
        dlog.i("mem_join_date : " + mem_join_date);
        dlog.i("------UpdateDirectMemberBasic------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberUpdateBasic.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberUpdateBasic api = retrofit.create(PlaceMemberUpdateBasic.class);
        Call<String> call = api.getData(place_id, mem_id, mem_name, mem_phone, mem_jumin, "1", mem_join_date);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("AddPlaceMember jsonResponse length : " + jsonResponse.length());
                            dlog.i("AddPlaceMember jsonResponse : " + jsonResponse);
                            if (jsonResponse.replace("\"", "").equals("success")) {
                                Toast_Nomal("초대 수락이 완료되었습니다.");
                                binding.acceptArea.setVisibility(View.GONE);
                                accept_state = 1;
                                String message = "[" + mem_name +"]근로자님이 매장초대를 수락하셨습니다.";
                                getUserToken(place_owner_id,"0",message);
                                AddPush("매장초대",message,place_owner_id);
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast_Nomal("기본정보 업데이트 에러  = " + t.getMessage());
            }
        });
    }


    String id = "";
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
        type = place_owner_id.equals(USER_INFO_ID) ? "0" : "1";

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
        dlog.i("place_owner_id : " + place_owner_id);
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
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("FcmStateSelect jsonResponse length : " + jsonResponse.length());
                            dlog.i("FcmStateSelect jsonResponse : " + jsonResponse);
                            try {

                                if (jsonResponse.replace("[", "").replace("]", "").length() == 0) {
                                    id = place_id;
                                    user_id = USER_INFO_ID;
                                    get_token = "";
                                    type = place_owner_id.equals(USER_INFO_ID) ? "0" : "1";
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

    String message = "";
    //근로자 > 점주 ( 초대수락 FCM )
    public void getUserToken(String user_id, String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + user_id);
        dlog.i("type : " + type);
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
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel4").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "4", place_id);
                        }
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
    public void AddPush(String title, String content, String user_id) {
        place_owner_id = shardpref.getString("place_owner_id","");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, USER_INFO_ID, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "PlaceList1";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            activity.runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                    activity.runOnUiThread(() -> {
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
                    dlog.i("FcmTokenUpdate jsonResponse length : " + response.body().length());
                    dlog.i("FcmTokenUpdate jsonResponse : " + response.body());
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

    private void MoveMyLocation() {
        try {
            gpsTracker = new GpsTracker(mContext);
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

//            mapViewContainer.addView(mapView);
//
//            /*현재 내 위치로 지도 중앙을 이동, 위치 트래킹 기능 on*/
//            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 1, true);
//            mapView.setZoomLevel(0, true);
//            mapView.zoomIn(true);
            reverseCoding(latitude, longitude);
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }
    }

    //역 지오코딩 ( 위,경도 >> 주소 ) START
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    public void reverseCoding(double latitude, double longitube) { // 위도 경도 넣어서 역지오코딩 주소값 뽑아낸다
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> gList = null;
        String Setaddress = "";
        dlog.i("(reverseCoding)latitude,longitube : " + latitude + "," + longitube);
        try {
            gList = geocoder.getFromLocation(latitude, longitube, 6);
        } catch (IOException e) {
            e.printStackTrace();
            dlog.e("setMaskLocation() - 서버에서 주소변환시 에러발생");
            // Fragment1 으로 강제이동 시키기
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Toast.makeText(mContext, " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();
            } else {
                Address address = gList.get(0);
                Address address1 = gList.get(1);
                Address address2 = gList.get(2);
                Address address3 = gList.get(3);
                dlog.i("address : " + address);
                dlog.i("address1 : " + address1);
                dlog.i("address2 : " + address2);
                dlog.i("address3 : " + address3);
                String addresslines = address.getAddressLine(0);
                String subaddresslines = address1.getAddressLine(0);

//                String city = address.getLocality() == null ? "" : address.getLocality();
//                String state = address.getAdminArea() == null ? "" : address.getAdminArea();
//
//                String country = address.getCountryName() == null ? "" : address.getCountryName();
//                String jibun = address.getFeatureName() == null ? "" : address.getFeatureName();
//                String postalCode = address.getPostalCode() == null ? "" : address.getPostalCode();
//                String roadAddress = address.getSubAdminArea() == null ? "" : address.getSubAdminArea();

                Setaddress = addresslines.replace("대한민국", "").trim();
                String dong = address1.getThoroughfare() == null ? "" : address1.getThoroughfare();
                String jibun = address1.getFeatureName() == null ? "" : address1.getFeatureName();
                String postalCode = address1.getPostalCode() == null ? "" : address1.getPostalCode();
                subaddresslines = dong + " " + jibun;
                dlog.i("Setaddress : " + Setaddress);
                dlog.i("subaddresslines : " + subaddresslines);

//                //MainAddrerss
//                address01.setText(Setaddress);
//
//                //subAddress
//                address02.setText("[지번] " + subaddresslines);
                shardpref.putString("pin_store_address", Setaddress);
                shardpref.putString("pin_store_addressdetail", subaddresslines);
                shardpref.putString("pin_zipcode", postalCode);
                shardpref.putString("pin_latitude", String.valueOf(latitude));
                shardpref.putString("pin_longitube", String.valueOf(longitube));
            }
        }
    }
    //역 지오코딩 ( 위,경도 >> 주소 ) END

    /**
     * Returns The approximate distance in meters between this
     * location and the given location. Distance is defined using
     * the WGS84 ellipsoid.
     *
     * @param //dest the destination location
     * @return the approximate distance in meters
     */
    //설정된 매장과 현재 내 위치의 거리를 재고 작업시작/종료 버튼의 활성화 비활성화 목적
    @SuppressLint("LongLogTag")
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;
        dlog.i("매장 위치 : " + lat1 + "," + lng1);
        dlog.i("현재 위치 : " + lat2 + "," + lng2);

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }


    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, null);
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
