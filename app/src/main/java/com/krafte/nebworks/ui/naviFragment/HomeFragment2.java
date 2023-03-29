package com.krafte.nebworks.ui.naviFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.net.ParseException;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MainMemberLAdapter;
import com.krafte.nebworks.adapter.MainNotiLAdapter;
import com.krafte.nebworks.adapter.MainTaskLAdapter;
import com.krafte.nebworks.bottomsheet.InoutPopActivity;
import com.krafte.nebworks.data.MainMemberLData;
import com.krafte.nebworks.data.MainNotiData;
import com.krafte.nebworks.data.MainTaskData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.InOutLogInterface;
import com.krafte.nebworks.dataInterface.MainContentsInterface;
import com.krafte.nebworks.dataInterface.PlaceMemberUpdateBasic;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.dataInterface.TaskSelectWInterface;
import com.krafte.nebworks.dataInterface.paymanaInterface;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

    int allPay = 0;
    String today = "";

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    RetrofitConnect rc = new RetrofitConnect();
    GpsTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;

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
    public void onAttach(@NonNull Context context) {
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
            //Singleton Area
            place_id = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", UserCheckData.getInstance().getUser_account());
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");

            //shardpref Area
            accept_state = shardpref.getInt("accept_state", -99);
            input_date = shardpref.getString("input_date", "-1");
            shardpref.putInt("SELECT_POSITION", 0);
            if (USER_INFO_AUTH.isEmpty()) {
                setDummyData();
            }

            setBtnEvent();
            SetAllMemberList();
            //사용자 ID로 FCM 보낼수 있도록 토픽 세팅
            FirebaseMessaging.getInstance().subscribeToTopic("P" + USER_INFO_ID).addOnCompleteListener(task -> {
                String msg = getString(R.string.msg_subscribed);
                if (!task.isSuccessful()) {
                    msg = getString(R.string.msg_subscribe_failed);
                }
                dlog.i("msg : " + msg);
            });

            //0-관리자 / 1- 근로자
            if (accept_state == 3) {
                //승인 대기중
                binding.acceptArea.setVisibility(View.VISIBLE);
            } else {
                binding.acceptArea.setVisibility(View.GONE);
            }


            binding.ioMytime.setText(dc.GET_YEAR + "년 " + dc.GET_MONTH + "월 " + dc.GET_DAY + "일");
            binding.cardview02.setOnClickListener(v -> {
                if (USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                    shardpref.putString("stub_place_id", place_id);
                    shardpref.putString("stub_user_id", USER_INFO_ID);
                    shardpref.putString("stub_user_account", USER_INFO_EMAIL);
                    shardpref.putString("change_place_name", place_name);
                    timer.cancel();
                    pm.MemberDetail(mContext);
                }
            });



            //-- 출퇴근 2023-03-29 -- EmployeeProcess에서 옮김
            cal = Calendar.getInstance();
            today = sdf.format(cal.getTime());
            dlog.i("오늘 :" + today);
            dlog.i("jongeob :" + jongeob.substring(3));

            outTime();
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
        getPlaceData();
    }

    Timer timer = new Timer();

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }


    @Override
    public void onResume() {
        super.onResume();
        shardpref.remove("Tap");
        TFFunction();

        UserCheck();
        allPay = 0;
        taskList();
        PlaceWorkCheck(place_id, USER_INFO_AUTH, "1"); // 공지
        PlaceWorkCheck(place_id, USER_INFO_AUTH, "2"); // 출퇴근 현황
        PlaceWorkCheck(place_id, USER_INFO_AUTH, "3"); // 급여현황
        PlaceWorkCheck(place_id, USER_INFO_AUTH, "4"); // 근로자 정보 및 최종급여

        String today = dc.GET_YEAR + "-" + dc.GET_MONTH;
        WritePaymentList(place_id, USER_INFO_ID, today);
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //5초마다 실행
                PlaceWorkCheck(place_id, USER_INFO_AUTH, "1");
                WritePaymentList(place_id, USER_INFO_ID, today);// 근로자 정보 및 최종급여
                InOutLogMember();
            }
        };
        timer.schedule(timerTask, 1000, 6000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void setDummyData() {
        binding.paynum.setText("2,000,000원");

        mList = new ArrayList<>();
        binding.mainTaskList.setVisibility(View.VISIBLE);
        mAdapter = new MainTaskLAdapter(mContext, mList);
        binding.mainTaskList.setAdapter(mAdapter);
        binding.mainTaskList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mAdapter.addItem(new MainTaskData.MainTaskData_list(
                "121",
                "143",
                "0",
                "할 일",
                "할일에 대한 설명이에요",
                "1",
                new ArrayList<String>(Collections.singleton("[{\"user_id\":\"77\",\"user_name\":\"김준호\",\"img_path\":\"null\",\"jikgup\":\"\\uc815\\uc9c1\\uc6d0\"},{\"user_id\":\"89\",\"user_name\":\"박찬성\",\"img_path\":\"null\",\"jikgup\":\"\\ub9e4\\ub2c8\\uc800\"},{\"user_id\":\"102\",\"user_name\":\"최치호\",\"img_path\":null,\"jikgup\":\"\\ub9e4\\ub2c8\\uc800\"},{\"user_id\":\"115\",\"user_name\":\"\\ud06c\\ub798\\ud504\\ud2b8\\uace0\\uac1d\\uc9c0\\uc6d0\\ud300\",\"img_path\":\"null\",\"jikgup\":null}],\"task_date\":\"2023-01-02\",\"start_time\":\"2023-01-02 6:9\",\"end_time\":\"2023-01-02 18:09\",\"sun\":\"0\",\"mon\":\"0\",\"tue\":\"0\",\"wed\":\"0\",\"thu\":\"0\",\"fri\":\"0\",\"sat\":\"0\",\"img_path\":null,\"complete_yn\":null,\"incomplete_reason\":null,\"approval_state\":\"3\",\"task_overdate\":\"\",\"reject_reason\":null,\"updated_at\":null}]")),
                "2023-01-01",
                "2023-01-01 01:00",
                "2023-01-01 23:00",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1",
                "0",
                "",
                "y",
                "",
                "0",
                "2023-01-01",
                "0",
                "2023-01-01"
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
                        dlog.i("InOutLogMember jsonResponse : " + jsonResponse);
                        if (jsonResponse.replace("[", "").replace("]", "").length() == 0) {
                            //그날 최초 출근
                            kind = "-1";
//                            InOutInsert("0");
//                            binding.oArea.setVisibility(View.GONE);
                            binding.ioArea.setVisibility(View.VISIBLE);
                        } else if (jsonResponse.replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && response.body() != null) {
                                dlog.i("InOutLogMember jsonResponse length : " + jsonResponse.length());
                                dlog.i("InOutLogMember jsonResponse : " + jsonResponse);
                                try {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    kind = Response.getJSONObject(0).getString("kind");
                                    String io_time = Response.getJSONObject(0).getString("io_time");
                                    dlog.i("InOutLogMember kind : " + kind);
                                    if (kind.equals("1")) {
                                        kind = "-1";
//                                        binding.oArea.setVisibility(View.GONE);
                                        binding.ioImg.setBackgroundResource(R.drawable.workinout01);
                                        binding.inTime.setVisibility(View.GONE);
                                        binding.inTimeTxt.setVisibility(View.GONE);
                                        binding.ioArea.setVisibility(View.VISIBLE);
                                    } else {
//                                        binding.oArea.setVisibility(View.VISIBLE);
                                        binding.ioImg.setBackgroundResource(R.drawable.workinout02);
                                        binding.inTime.setVisibility(View.VISIBLE);
                                        binding.inTimeTxt.setVisibility(View.VISIBLE);
//                                        binding.ioArea.setVisibility(View.GONE);
                                    }
                                    binding.inTime.setText(io_time);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        dlog.i("InOutLogMember kind 2 : " + kind);
                        if (kind.equals("-1")) {
                            binding.ioImg.setBackgroundResource(R.drawable.workinout01);
                            binding.state.setText("현재 퇴근");
                        } else if (kind.equals("0")) {
                            binding.ioImg.setBackgroundResource(R.drawable.workinout02);
                            binding.state.setText("현재 출근");
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 1 = " + t.getMessage());
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
                timer.cancel();
                pm.MemberDetail(mContext);
            }
        });
        binding.itemArea.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putString("USER_INFO_AUTH", "1");
                shardpref.putString("event", "out_store");
                timer.cancel();
                pm.PlaceList(mContext);
            }
        });

        binding.memberManagement01.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                timer.cancel();
                pm.MemberManagement(mContext);
            }
        });


        binding.ioArea.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                permissionCheck();
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
                if (USER_INFO_AUTH.equals("0")) {
                    timer.cancel();
                    pm.PayManagement(mContext);
                } else {
                    timer.cancel();
                    pm.PayManagement2(mContext);
                }
            }
        });

        binding.homeMenu04.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                dlog.i("근로계약서 전체 관리");
                timer.cancel();
                pm.ContractFragment(mContext);
            }
        });

        binding.cardview01.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                timer.cancel();
                pm.FeedList(mContext);
            }
        });

        binding.todoMore.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putInt("SELECT_POSITION", 1);
                timer.cancel();
                pm.Main2(mContext);
            }
        });

        binding.contractPrint.setOnClickListener(v -> {
            dlog.i("------contractPrint------");
            dlog.i("contractPrint contract_id : " + contract_id);
            dlog.i("------contractPrint------");
            if (contract_id.equals("0")) {
                Toast_Nomal("작성된 근로계약서가 없습니다.");
            } else {
                String Contract_uri = "http://krafte.net/NEBWorks/ContractPDF2.php?id=" + contract_id;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Contract_uri));
                startActivity(intent);
            }
        });
        binding.inoutPrint.setOnClickListener(v -> {
            dlog.i("------inoutPrint------");
            dlog.i("inoutPrint USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("inoutPrint place_id : " + place_id);
            dlog.i("------inoutPrint------");
            String Contract_uri = "http://krafte.net/NEBWorks/Commute.php?user_id=" + USER_INFO_ID + "&place_id=" + place_id + "&date=";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Contract_uri));
            startActivity(intent);
        });
    }



    String place_iomethod = "";
    String getMySSID = "";
    Handler handler = new Handler();
    String title = "";
    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate_time = new java.text.SimpleDateFormat("HH:mm:ss");

    String GET_TIME = simpleDate_time.format(mDate);

    public String getNetworkName(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getSSID();
    }

    private void TFFunction(){
        getPlaceData2();
        MoveMyLocation();
        getMySSID = getNetworkName(mContext).replace("\"", "");
        if (getMySSID.equals("<unknown ssid>")) {
            getMySSID = "";
        }
        handler.postDelayed(() -> {
            long now = System.currentTimeMillis();
            Date mDate = new Date(now);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");
            dlog.i("GET_TIME : " + simpleDate.format(mDate));
            if(place_iomethod.equals("y")){
                if (getMySSID.equals(place_wifi_name)) {
                    if (kind.equals("0")) {
                        title = "출근처리 가능";
                    } else {
                        title = "퇴근처리 가능";
                    }
                    dlog.i("title 1: " + title);
                    dlog.i("getMySSID 1: " + getMySSID);
                    dlog.i("binding.selectWorkse setOnClickListener kind : " + kind);
                } else {
                    if (kind.equals("0")) {
                        title = "출근처리 불가";
                    } else {
                        title = "퇴근처리 불가";
                    }
                    dlog.i("title 2: " + title);
                    dlog.i("getMySSID 2: " + getMySSID);
                    TFFunction();
                }
            }else if(place_iomethod.equals("n")){
                if (kind.equals("0")) {
                    title = "출근처리 가능";
                } else {
                    title = "퇴근처리 가능";
                }
                dlog.i("title 1: " + title);
                dlog.i("getMySSID 1: " + getMySSID);
                dlog.i("binding.selectWorkse setOnClickListener kind : " + kind);
            }else{
                TFFunction();
            }
        }, 500); //0.5초 후 핸들러 실행
    }

    private void permissionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();
                UserCheck();
                dlog.i("UserCheckData.getInstance().getUser_sieob() : " + UserCheckData.getInstance().getUser_sieob());
                if (!UserCheckData.getInstance().getUser_sieob().equals("null")) {
                    if (kind.equals("-1") || kind.equals("0")) {
                        if (kind.equals("-1")) {
                            kind = "0";
                        } else {
                            kind = "1";
                        }
//                        shardpref.putString("kind", kind);
//                        pm.EmployeeProcess(mContext);
                    }

                    MoveMyLocation();
                    long now = System.currentTimeMillis();
                    Date mDate = new Date(now);
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");
                    dlog.i("GET_TIME : " + simpleDate.format(mDate));
                    dlog.i("kind : " + kind);

                    if (kind.equals("0")) {
                        if(place_iomethod.equals("y")){
                            if (getMySSID.equals(place_wifi_name)) {
                                //가게 등록한 와이파이와 현재 디바이스에서 접속중인 와이파이 비교
                                io_state = "출근처리";
                                InOutPop(GET_TIME, "1", place_name, io_state, "", "0");
                            } else {
                                InOutPop(GET_TIME, "2", place_name, "출근처리 불가", "매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요", "0");
                            }
                        }else if(place_iomethod.equals("n")){
                            io_state = "출근처리";
                            InOutPop(GET_TIME, "1", place_name, io_state, "", "0");
                        }
                    } else {
                        if(place_iomethod.equals("y")){
                            if (getMySSID.equals(place_wifi_name)) {
                                io_state = "퇴근처리";
                                dlog.i("compareDate2 :" + compareDate2());
                                //가게 등록한 와이파이와 현재 디바이스에서 접속중인 와이파이 비교
//                        if (compareDate2()) {
//                            InOutPop(GET_TIME, "4", place_name, io_state, "", "1");
//                        } else {
//                            InOutPop(GET_TIME, "3", place_name, io_state, "등록된 퇴근시간이 아닙니다.", "1");//퇴근시간 전일때
//                        }
                                InOutPop(GET_TIME, "4", place_name, io_state, "", "1");
                            } else {
                                InOutPop(GET_TIME, "2", place_name, "퇴근처리 불가", "매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요", "1");
                            }
                        }else if(place_iomethod.equals("n")){
                            io_state = "퇴근처리";
                            dlog.i("compareDate2 :" + compareDate2());
                            //가게 등록한 와이파이와 현재 디바이스에서 접속중인 와이파이 비교
                            InOutPop(GET_TIME, "4", place_name, io_state, "", "1");
                        }
                    }
                } else {
                    shardpref.putString("mem_name",UserCheckData.getInstance().getUser_name());
                    Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
                    intent.putExtra("data", "근무시작 시간이 배정되지 않았습니다.\n추가근무를 생성하시겠습니까?");
                    intent.putExtra("flag", "추가근무");
                    intent.putExtra("left_btn_txt", "닫기");
                    intent.putExtra("right_btn_txt", "추가하기");
                    startActivity(intent);
//                    Toast_Nomal("근무시작 시간이 배정되지 않았습니다.");
                }
                dlog.i("permissionCheck() : Permission Granted");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(mContext, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                dlog.i("permissionCheck() : Permission Denied");
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한]에서 위치액세스 권한이 필요합니다")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    public boolean compareDate2() throws ParseException {
        boolean returntf = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date1 = sdf.parse(today);
            Date date2 = sdf.parse(jongeob.substring(3));
            System.out.println(sdf.format(date1));
            System.out.println(sdf.format(date2));

            returntf = date1.after(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returntf;
    }
    private void outTime() {
        String[] now = dc.GET_TIME.split(" ");
        String[] outTimeSplit = jongeob.split(":");
        String[] time = now[1].split(":");

        int times = Integer.parseInt(time[0]) * 3600 + Integer.parseInt(time[1]) * 60;
        int outTime = Integer.parseInt(outTimeSplit[0]) * 3600 + Integer.parseInt(outTimeSplit[1]) * 60;

        int t = outTime - times;

        if (t < 0) {
            binding.ioTime.setText("설정된 퇴근시간을 초과하셨습니다.");
        } else {
            int hour = t / (60 * 60);
            int minute = t / 60 - (hour * 60);

            binding.ioTime.setText("퇴근까지 " + hour + "시간 " + minute + "분");
        }
    }
    private void InOutPop(String time, String state, String store_name, String inout_tv, String inout_tv2, String kind) {
        shardpref.putString("kind", kind);
        shardpref.putString("time", time);
        shardpref.putString("state", state);
        shardpref.putString("store_name", store_name);
        shardpref.putString("inout_tv", inout_tv);
        shardpref.putString("inout_tv2", inout_tv2);
        InoutPopActivity ipp = new InoutPopActivity();
        ipp.show(getChildFragmentManager(), "InoutPopActivity");
    }

    public void getPlaceData() {
        Thread th = new Thread(() -> {
            activity.runOnUiThread(() -> {
                try {
                    place_name = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());
                    place_owner_id = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());
                    place_owner_name = shardpref.getString("place_owner_name", PlaceCheckData.getInstance().getPlace_owner_name());
                    registr_num = shardpref.getString("registr_name", PlaceCheckData.getInstance().getRegistr_num());
                    store_kind = shardpref.getString("store_kind", PlaceCheckData.getInstance().getStore_kind());
                    place_address = shardpref.getString("place_address", PlaceCheckData.getInstance().getPlace_address());
                    place_latitude = Double.parseDouble(shardpref.getString("place_latitude", PlaceCheckData.getInstance().getPlace_latitude()));
                    place_longitude = Double.parseDouble(shardpref.getString("place_longitude", PlaceCheckData.getInstance().getPlace_longitude()));
                    place_pay_day = shardpref.getString("place_pay_day", PlaceCheckData.getInstance().getPlace_pay_day());
                    place_test_period = shardpref.getString("place_test_period", PlaceCheckData.getInstance().getPlace_test_period());
                    place_vacation_select = shardpref.getString("place_vacation_select", PlaceCheckData.getInstance().getPlace_vacation_select());
                    place_insurance = shardpref.getString("place_insurance", PlaceCheckData.getInstance().getPlace_insurance());
                    place_start_time = shardpref.getString("place_start_time", PlaceCheckData.getInstance().getPlace_start_time());
                    place_end_time = shardpref.getString("place_end_time", PlaceCheckData.getInstance().getPlace_end_time());
                    place_save_kind = shardpref.getString("place_save_kind", PlaceCheckData.getInstance().getPlace_save_kind());
                    place_wifi_name = shardpref.getString("place_wifi_name", PlaceCheckData.getInstance().getPlace_wifi_name());
                    place_img_path = shardpref.getString("place_img_path", PlaceCheckData.getInstance().getPlace_img_path());
                    place_start_date = shardpref.getString("place_start_date", PlaceCheckData.getInstance().getPlace_start_date());
                    place_created_at = shardpref.getString("place_created_at", PlaceCheckData.getInstance().getPlace_created_at());
                    place_icnt = shardpref.getString("place_icnt", PlaceCheckData.getInstance().getPlace_icnt());
                    place_ocnt = shardpref.getString("place_ocnt", PlaceCheckData.getInstance().getPlace_ocnt());
                    place_totalcnt = shardpref.getString("place_totalcnt", PlaceCheckData.getInstance().getPlace_totalcnt());

                    Glide.with(mContext).load(place_img_path)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.ic_store_icon)
                            .skipMemoryCache(true)
                            .into(binding.storeThumnail);

                    dlog.i("place_owner_id : " + place_owner_id);
                    dlog.i("USER_INFO_ID : " + USER_INFO_ID);
                    dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
                    shardpref.putString("USER_INFO_AUTH", USER_INFO_AUTH);
                    shardpref.putString("place_end_time", place_end_time);
                    if (!USER_INFO_AUTH.isEmpty()) {
                        dlog.i("place_name: " + place_name);
                        binding.title.setText(place_name);
                    } else {
                        binding.title.setText("나의 매장");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        th.start();
        try {
            th.join(); // 작동한 스레드의 종료까지 대기 후 메인 스레드 실행

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * 20230105 HomFragment2에서만 한번 사용자 id , 매장 id를 사용해
     * 사용자 정보를 체크, 이후 다른 페이지에서는 Singleton 전역변수로 사용
     * */
    DBConnection dbc = new DBConnection();
    String contract_id = "";

    public void UserCheck() {
        Thread th = new Thread(() -> {
            dbc.UserCheck(place_id, USER_INFO_ID);
            activity.runOnUiThread(() -> {
                try {
                    mem_id = shardpref.getString("mem_id", UserCheckData.getInstance().getUser_id());
                    mem_name = shardpref.getString("mem_name", UserCheckData.getInstance().getUser_name());
                    mem_phone = shardpref.getString("mem_phone", UserCheckData.getInstance().getUser_phone());
                    mem_gender = shardpref.getString("mem_gender", UserCheckData.getInstance().getUser_gender());
                    mem_img_path = shardpref.getString("mem_img_path", UserCheckData.getInstance().getUser_img_path());
                    mem_jumin = shardpref.getString("mem_jumin", UserCheckData.getInstance().getUser_jumin());
                    mem_join_date = shardpref.getString("mem_join_date", UserCheckData.getInstance().getUser_join_date());
                    mem_state = shardpref.getString("mem_state", UserCheckData.getInstance().getUser_state());
                    mem_jikgup = shardpref.getString("mem_jikgup", UserCheckData.getInstance().getUser_jikgup());
                    mem_pay = shardpref.getString("mem_pay", UserCheckData.getInstance().getUser_pay());
                    jongeob = shardpref.getString("jongeob", UserCheckData.getInstance().getUser_jongeob());
                    String kind = shardpref.getString("kind", UserCheckData.getInstance().getUser_kind());
                    contract_id = shardpref.getString("contract_id", UserCheckData.getInstance().getUser_contract_id());

                    if (kind.equals("4")) {
                        binding.noMemberLine.setVisibility(View.VISIBLE);
                        binding.memberLine.setVisibility(View.GONE);
                        binding.payline.setVisibility(View.GONE);
                        binding.state.setVisibility(View.GONE);
                        binding.placeState.setVisibility(View.GONE);
                    } else {
                        binding.noMemberLine.setVisibility(View.GONE);
                        binding.memberLine.setVisibility(View.VISIBLE);
                        binding.payline.setVisibility(View.VISIBLE);
                        binding.state.setVisibility(View.VISIBLE);
                        binding.placeState.setVisibility(View.VISIBLE);

                        shardpref.putString("jongeob", jongeob);
                        dlog.i("------UserCheck-------");
                        USER_INFO_ID = mem_id;
                        dlog.i("프로필 사진 url : " + mem_img_path);
                        dlog.i("성명 : " + mem_name);
                        dlog.i("부서 : " + mem_jikgup);
                        dlog.i("급여 : " + mem_pay);
                        dlog.i("------UserCheck-------");

                        shardpref.putString("mem_name", mem_name);
                        if (USER_INFO_AUTH.isEmpty() || USER_INFO_AUTH.equals("-1")) {
                            binding.ioTime.setText("김이름님 오늘도 화이팅하세요!");
                        } else {
                            binding.ioTime.setText(mem_name + "님 오늘도 화이팅하세요!");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        th.start();
        try {
            th.join(); // 작동한 스레드의 종료까지 대기 후 메인 스레드 실행

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void SetAllMemberList() {
        dlog.i("SetAllMemberList place_id : " + place_id);
        dlog.i("SetAllMemberList user_id : " + user_id);
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id, USER_INFO_ID);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            if (Response.length() != 0) {
                                String mem_id = Response.getJSONObject(0).getString("id");
                                String name = Response.getJSONObject(0).getString("name");
                                String place_name = Response.getJSONObject(0).getString("place_name");
                                String join_date = Response.getJSONObject(0).getString("join_date").replace("-", ".");
                                String img_path = Response.getJSONObject(0).getString("img_path");
                                String phone = Response.getJSONObject(0).getString("phone");
                                String owner_phone = Response.getJSONObject(0).getString("owner_phone");
                                String jumin = Response.getJSONObject(0).getString("jumin");
                                String gender = Response.getJSONObject(0).getString("gender");
                                String kind = Response.getJSONObject(0).getString("kind");
                                String state = Response.getJSONObject(0).getString("state");
                                String pay = Response.getJSONObject(0).getString("pay");

                                if (!owner_phone.isEmpty()) {
                                    owner_phone = Response.getJSONObject(0).getString("owner_phone").substring(0, 3) + "-"
                                            + Response.getJSONObject(0).getString("owner_phone").substring(3, 7) + "-"
                                            + Response.getJSONObject(0).getString("owner_phone").substring(7, 11);
                                }
                                String jikgup = Response.getJSONObject(0).getString("jikgup");

                                dlog.i("setAll placeName: " + join_date);
                                if (pay.equals("null")) {
                                    binding.paynum.setText("0원");
                                } else {
                                    binding.paynum.setText(pay + "원");
                                }
                                binding.joinPlaceDate.setText(join_date + " 입사");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 2 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    int state_null = 0;
    String writer_id = "";

    public void PlaceWorkCheck(String place_id, String auth, String kind) {
        dlog.i("----------PlaceWorkCheck----------");
        dlog.i("PlaceWorkCheck place_id : " + place_id);
        dlog.i("PlaceWorkCheck auth : " + auth);
        dlog.i("PlaceWorkCheck kind : " + kind);
        dlog.i("PlaceWorkCheck USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("----------PlaceWorkCheck----------");
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
//                                            mList = new ArrayList<>();
//                                            mAdapter = new MainTaskLAdapter(mContext, mList);
//                                            binding.mainTaskList.setAdapter(mAdapter);
//                                            binding.mainTaskList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//                                            dlog.i("Task Get SIZE : " + Response.length());
//                                            if (Response.length() == 0) {
//                                                dlog.i("SetNoticeListview Thread run! ");
//                                                dlog.i("GET SIZE : " + Response.length());
//                                                binding.mainTaskList.setVisibility(View.GONE);
//                                                binding.limitTasktv.setVisibility(View.VISIBLE);
//                                            } else {
//                                                binding.mainTaskList.setVisibility(View.VISIBLE);
//                                                binding.limitTasktv.setVisibility(View.GONE);
//                                                for (int i = 0; i < Response.length(); i++) {
//                                                    JSONObject jsonObject = Response.getJSONObject(i);
//                                                    mAdapter.addItem(new MainTaskData.MainTaskData_list(
//                                                            jsonObject.getString("title"),
//                                                            jsonObject.getString("end_date"),
//                                                            jsonObject.getString("end_hour"),
//                                                            jsonObject.getString("end_min")
//                                                    ));
//                                                }
//
//                                                mAdapter.setOnItemClickListener(new MainTaskLAdapter.OnItemClickListener() {
//                                                    @Override
//                                                    public void onItemClick(View v, int position) {
//                                                        if (USER_INFO_AUTH.isEmpty()) {
//                                                            isAuth();
//                                                        } else {}
//                                                    }
//                                                });
//
//                                            }
//                                            mAdapter.notifyDataSetChanged();
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
                                                            timer.cancel();
                                                            pm.FeedList(mContext);
                                                        }
                                                    }
                                                });
                                            }
                                            mAdapter.notifyDataSetChanged();
                                        } else if (kind.equals("2")) {
                                            binding.inCnt.setText(Response.getJSONObject(0).getString("i_cnt"));
                                            binding.outCnt.setText(Response.getJSONObject(0).getString("o_cnt"));
                                            //결근 숫자에서 휴가숫자는 빠지지 않기때문에 결근-휴가수를 빼줘야한다
                                            if(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) == 0){
                                                binding.notinCnt.setText(Response.getJSONObject(0).getString("absence_cnt"));
                                            } else if(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) > 0){
                                                binding.notinCnt.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt"))
                                                        -Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
                                            }
//                                            binding.notinCnt.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt"))
//                                                    -Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
//                                            binding.restCnt.setText(Response.getJSONObject(0).getString("vaca_cnt"));
                                            binding.restCnt.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("rest_cnt"))
                                                    + Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
                                            dlog.i("-----MainData-----");
                                            dlog.i("i_cnt : " + Response.getJSONObject(0).getString("i_cnt"));
                                            dlog.i("o_cnt : " + Response.getJSONObject(0).getString("o_cnt"));
                                            dlog.i("absence_cnt : " + Response.getJSONObject(0).getString("absence_cnt"));
                                            dlog.i("rest_cnt : " + Response.getJSONObject(0).getString("vaca_cnt"));
//                                            binding.inCnt.setText(Response.getJSONObject(0).getString("i_cnt"));
//                                            binding.outCnt.setText(Response.getJSONObject(0).getString("o_cnt"));
//                                            binding.notinCnt.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt"))
//                                                    - Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
//                                            binding.restCnt.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("rest_cnt"))
//                                                    + Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
//                                            dlog.i("-----MainData-----");
//                                            dlog.i("i_cnt : " + Response.getJSONObject(0).getString("i_cnt"));
//                                            dlog.i("o_cnt : " + Response.getJSONObject(0).getString("o_cnt"));
//                                            dlog.i("absence_cnt : " + Response.getJSONObject(0).getString("absence_cnt"));
//                                            dlog.i("vaca_cnt : " + Response.getJSONObject(0).getString("vaca_cnt"));
//                                            dlog.i("rest_cnt : " + Response.getJSONObject(0).getString("rest_cnt"));
//                                            dlog.i("-----MainData-----");
                                        } else if (kind.equals("3")) {
                                            for (int i = 0; i < Response.length(); i++) {
                                                allPay += Integer.parseInt(Response.getJSONObject(i).getString("recent_pay").replace(",", ""));
                                            }
                                            allPay = allPay - Integer.parseInt(Response.getJSONObject(0).getString("deductpay").replace(",", ""));
                                        } else if (kind.equals("4")) {
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
                                                        }
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
                dlog.e("에러 3 = " + t.getMessage());
            }
        });
    }

    public void taskList() {
        String getYMPicker = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectWInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectWInterface api = retrofit.create(TaskSelectWInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, getYMPicker, USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dlog.i("taskList");
                dlog.i("response 1: " + response.isSuccessful());
                dlog.i("response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    try {
                        JSONArray Response = new JSONArray(rc.getBase64decode(response.body()));
                        mList = new ArrayList<>();
                        mAdapter = new MainTaskLAdapter(mContext, mList);
                        binding.mainTaskList.setAdapter(mAdapter);
                        binding.mainTaskList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        dlog.i("GET SIZE : " + Response.length());
                        if (Response.length() == 0) {
                            dlog.i("SetNoticeListview Thread run! ");
                            dlog.i("GET SIZE : " + Response.length());
                            binding.mainTaskList.setVisibility(View.GONE);
                        } else {
                            binding.mainTaskList.setVisibility(View.VISIBLE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if (!jsonObject.getString("id").isEmpty() || !jsonObject.getString("id").equals("null")) {
                                    if (!jsonObject.getString("complete_kind").equals("3")) {
                                        mAdapter.addItem(new MainTaskData.MainTaskData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("writer_id"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("title"),
                                                jsonObject.getString("contents"),
                                                jsonObject.getString("complete_kind"),
                                                Collections.singletonList(jsonObject.getString("users")),
                                                jsonObject.getString("task_date"),
                                                jsonObject.getString("start_time"),
                                                jsonObject.getString("end_time"),
                                                jsonObject.getString("sun"),
                                                jsonObject.getString("mon"),
                                                jsonObject.getString("tue"),
                                                jsonObject.getString("wed"),
                                                jsonObject.getString("thu"),
                                                jsonObject.getString("fri"),
                                                jsonObject.getString("sat"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("complete_yn"),
                                                jsonObject.getString("incomplete_reason"),
                                                jsonObject.getString("approval_state"),
                                                jsonObject.getString("task_overdate"),
                                                jsonObject.getString("reject_reason"),
                                                jsonObject.getString("updated_at")
                                        ));
                                    }
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
                        }

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void WritePaymentList(String place_id, String SelectId, String GET_DATE) {
        dlog.i("------------PaymentFragment2 List------------");
        dlog.i("place_id : " + place_id);
        dlog.i("GET_DATE : " + GET_DATE);
        dlog.i("SelectId : " + SelectId);
        dlog.i("------------PaymentFragment2 List------------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(paymanaInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        paymanaInterface api = retrofit.create(paymanaInterface.class);
        Call<String> call = api.getData("1", place_id, GET_DATE, SelectId, "", "", "", "", "", "", "", "", "", "");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WritePaymentList / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
//                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        if(Response.toString().equals("[]")){
                            HomeFragment2.super.onResume();
                        }else{
                            dlog.i("workhour : " + Response.getJSONObject(0).getString("workhour"));
                            dlog.i("jikgup : " + Response.getJSONObject(0).getString("jikgup"));
                            dlog.i("payment : " + Response.getJSONObject(0).getString("payment"));
                            String workhour = Response.getJSONObject(0).getString("workhour");
                            String jikgup = Response.getJSONObject(0).getString("jikgup");
                            String payment = Response.getJSONObject(0).getString("payment");
                            String paykind = Response.getJSONObject(0).getString("paykind");
                            //알바, 정직원, 매니저, 기타
                            DecimalFormat myFormatter = new DecimalFormat("###,###");
                            if (paykind.equals("시급") || paykind.equals("주급")) {
                                binding.realPaynum.setText("근무 " + workhour + "시간 X 시급 " + payment + "원 = " + myFormatter.format(allPay) + "원");
                            } else if (paykind.equals("월급")) {
                                binding.realPaynum.setText(myFormatter.format(allPay) + "원");
                            }
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
                                String message = "[" + mem_name + "]근로자님이 매장초대를 수락하셨습니다.";
                                getUserToken(place_owner_id, "0", message);
                                AddPush("매장초대", message, place_owner_id);
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
                dlog.e("에러 4 = " + t.getMessage());
            }
        });
    }

    public void AddPush(String title, String content, String user_id) {
        place_owner_id = shardpref.getString("place_owner_id", "");
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
                dlog.e("에러 5 = " + t.getMessage());
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

    private void MoveMyLocation() {
        try {
            gpsTracker = new GpsTracker(mContext);
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

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

                Setaddress = addresslines.replace("대한민국", "").trim();
                String dong = address1.getThoroughfare() == null ? "" : address1.getThoroughfare();
                String jibun = address1.getFeatureName() == null ? "" : address1.getFeatureName();
                String postalCode = address1.getPostalCode() == null ? "" : address1.getPostalCode();
                subaddresslines = dong + " " + jibun;
                dlog.i("Setaddress : " + Setaddress);
                dlog.i("subaddresslines : " + subaddresslines);

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

    private void getPlaceData2() {
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
                                    place_latitude = Double.parseDouble(Response.getJSONObject(0).getString("latitude"));
                                    place_longitude = Double.parseDouble(Response.getJSONObject(0).getString("longitude"));
                                    place_pay_day = Response.getJSONObject(0).getString("pay_day");
                                    place_test_period = Response.getJSONObject(0).getString("test_period");
                                    place_vacation_select = Response.getJSONObject(0).getString("vacation_select");
                                    place_insurance = Response.getJSONObject(0).getString("insurance");
                                    place_wifi_name = Response.getJSONObject(0).getString("wifi_name");
                                    place_iomethod = Response.getJSONObject(0).getString("io_method");
                                    shardpref.putString("place_wifi_name", place_wifi_name);
                                    shardpref.putString("place_latitude", String.valueOf(place_latitude));
                                    shardpref.putString("place_longitude", String.valueOf(place_longitude));
                                    dlog.i("place_iomethod : " + place_iomethod);
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
        intent.putExtra("flag", "더미");
        intent.putExtra("data", "먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
