package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MainMemberLAdapter;
import com.krafte.nebworks.adapter.MainNotiLAdapter;
import com.krafte.nebworks.bottomsheet.MemberOption;
import com.krafte.nebworks.data.MainMemberLData;
import com.krafte.nebworks.data.MainNotiData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMCrerateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FCMUpdateInterface;
import com.krafte.nebworks.dataInterface.MainContentsInterface;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.dataInterface.UpdateIoMethod;
import com.krafte.nebworks.databinding.HomefragmentBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RandomOut;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 2022 11 03 방창배 작성 / 점주용 페이지
 * 2023 03 08 매장 이미지 추가 기능 작성
 */

public class HomeFragment extends Fragment {
    private final static String TAG = "HomeFragment";
    private HomefragmentBinding binding;
    int GALLEY_CODE = 10;

    Context mContext;
    Activity activity;

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String registr_num = "";
    String store_kind = "";
    String place_address = "";
    String place_latitude = "";
    String place_longitude = "";
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";
    String place_totalcnt = "";
    int place_feed_cnt = 0;

    String place_pay_day = "";
    String place_test_period = "";
    String place_vacation_select = "";
    String place_insurance = "";
    String place_save_kind = "";
    String place_wifi_name = "";
    String place_icnt = "";
    String place_ocnt = "";

    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_AUTH = "";

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    RandomOut ro = new RandomOut();
    DBConnection dbc = new DBConnection();

    int isAuth = 0;

    ArrayList<MainMemberLData.MainMemberLData_list> mList;
    MainMemberLAdapter mAdapter = null;

    ArrayList<MainNotiData.MainNotiData_list> mList2;
    MainNotiLAdapter mAdapter2 = null;


    public static HomeFragment newInstance(int number) {
        HomeFragment fragment = new HomeFragment();
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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.homefragment, container, false);
        binding = HomefragmentBinding.inflate(inflater);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);

        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            //Singleton Area
            place_id = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
            place_owner_id = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", UserCheckData.getInstance().getUser_account());
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");

            String date = dc.GET_YEAR + "년 " + dc.GET_MONTH + "월 기준";
            binding.payDate.setText(date);

            String[] dateWithTime = dc.GET_TIME.split(" ");
//            binding.detailInout.setText(date + dateWithTime[1]);

            //shardpref Area
            shardpref.putInt("SELECT_POSITION", 0);
            isAuth = shardpref.getInt("isAuth", 0);

            setBtnEvent();
            dlog.i("HomeFragment START!");

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

        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
//        place_end_time
        return binding.getRoot();
//        return rootView;
    }

    Timer timer;

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        getPlaceData();
        getWifiState();
        PlaceWorkCheck(place_id, USER_INFO_AUTH, "0");
        timer = new Timer();
    }

    @Override
    public void onResume() {
        super.onResume();
        shardpref.remove("Tap");
        shardpref.remove("item_user_id");
        shardpref.remove("item_user_name");
        UserCheck();
        SetAllMemberList();
        PlaceWorkCheck(place_id, USER_INFO_AUTH, "1");
//        timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                //5초마다 실행
//                PlaceWorkCheck(place_id, USER_INFO_AUTH, "1");
//            }
//        };
//        timer.schedule(timerTask,1000,5000);
//        timer.cancel();
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

    public void setBtnEvent() {

        binding.itemArea.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, PlacePhotoActivity.class);
//            startActivity(intent);
//            activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putString("USER_INFO_AUTH", "0");
                shardpref.putString("event", "out_store");
                pm.PlaceList(mContext);
            }
        });
        binding.cardview00.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                pm.FeedList(mContext);
            }
        });

        binding.payLayout.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (USER_INFO_AUTH.equals("0")) {
                    pm.PayManagement(mContext);
                } else {
                    pm.PayManagement2(mContext);
                }
            }
        });

        binding.placeState.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putString("USER_INFO_AUTH", "0");
                shardpref.putString("event", "out_store");
                pm.PlaceList(mContext);
            }
        });

        binding.commuteArea.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putInt("SELECT_POSITION", 2);
                pm.Main(mContext);
//                pm.MemberManagement(mContext);
            }
        });

        binding.addMemberBtn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                MemberOption mo = new MemberOption();
                mo.show(getChildFragmentManager(), "MemberOption");
            }
        });
        binding.addMemberArea.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                MemberOption mo = new MemberOption();
                mo.show(getChildFragmentManager(), "MemberOption");
            }
        });

        binding.homeMenu01.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                dlog.i("직원관리");
                pm.MemberManagement(mContext);
            }
        });
        binding.homeMenu02.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                dlog.i("결재현황");
                pm.Approval(mContext);
            }
        });
        binding.homeMenu03.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                dlog.i("급여관리");
                if (USER_INFO_AUTH.equals("0")) {
                    pm.PayManagement(mContext);
                } else {
                    pm.PayManagement2(mContext);
                }
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

//        binding.detailInout.setOnClickListener(v -> {
//            if (USER_INFO_AUTH.isEmpty()) {
//                isAuth();
//            } else {
//                shardpref.putInt("SELECT_POSITION", 2);
//                pm.Main(mContext);
//            }
//        });

        binding.addMemberBtn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                MemberOption mo = new MemberOption();
                mo.show(getChildFragmentManager(), "MemberOption");
            }
        });
    }

    public void SetAllMemberList() {
        dlog.i("SetAllMemberList place_id : " + place_id);
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id, "");

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                        dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            dlog.i("SetAllMemberList response.body() length : " + jsonResponse);

                            if (Response.length() == 0) {
                                //직원이 없을때
                                dlog.i("SIZE 1 : " + Response.length());
                                binding.addMemberArea.setVisibility(View.VISIBLE);
                                // binding.importantList.setVisibility(View.GONE);
                            } else {
                                //직원이 한명이라도 있을때
                                dlog.i("SIZE 2 : " + Response.length());
                                binding.addMemberArea.setVisibility(View.GONE);
                                // binding.importantList.setVisibility(View.VISIBLE);
                                //-- 직원 총 급여 표시할것
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        binding.wifiSwitch.setOnClickListener(v -> {
            if (binding.wifiSwitch.isChecked()) {
                setWifiOnOff("y");
            } else {
                setWifiOnOff("n");
            }
        });
    }


    /*
     * 20230105 HomFragment에서만 한번 사용자 id , 매장 id를 사용해
     * 사용자 정보를 체크, 이후 다른 페이지에서는 Singleton 전역변수로 사용
     * */
    public void UserCheck() {
        Thread th = new Thread(() -> {
//            dbc.UserCheck(place_id, USER_INFO_ID);
            activity.runOnUiThread(() -> {
                String id = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
                String name = shardpref.getString("USER_INFO_NAME", UserCheckData.getInstance().getUser_name());
                String nick_name = shardpref.getString("USER_INFO_NICKNAME", UserCheckData.getInstance().getUser_nick_name());
                String img_path = shardpref.getString("USER_INFO_PROFILE", UserCheckData.getInstance().getUser_img_path());

                try {
                    dlog.i("------UserCheck-------");
                    dlog.i("프로필 사진 url : " + img_path);
                    dlog.i("성명 : " + name);
                    dlog.i("닉네임 : " + nick_name);
                    dlog.i("------UserCheck-------");

                    if (place_owner_id.equals(id)) {
                        USER_INFO_AUTH = "0";
                        binding.gotoPlace.setVisibility(View.VISIBLE);
                    } else {
                        USER_INFO_AUTH = "1";
                        binding.gotoPlace.setVisibility(View.GONE);
                    }
                    shardpref.putString("USER_INFO_PROFILE", img_path);
                    shardpref.putString("USER_INFO_NAME", name);
                    shardpref.putString("USER_INFO_NICKNAME", nick_name);
                    getFCMToken();
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

    public void getPlaceData() {
        Thread th = new Thread(() -> {
            activity.runOnUiThread(() -> {
                try {
                    place_name = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());
                    place_owner_id = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());
                    place_owner_name = shardpref.getString("place_owner_name", PlaceCheckData.getInstance().getPlace_owner_name());
                    registr_num = shardpref.getString("registr_num", PlaceCheckData.getInstance().getRegistr_num());
                    store_kind = shardpref.getString("store_kind", PlaceCheckData.getInstance().getStore_kind());
                    place_address = shardpref.getString("place_address", PlaceCheckData.getInstance().getPlace_address());
                    place_latitude = shardpref.getString("place_latitude", PlaceCheckData.getInstance().getPlace_latitude());
                    place_longitude = shardpref.getString("place_longitude", PlaceCheckData.getInstance().getPlace_longitude());
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

                    dlog.i("getPlaceData place_name : " + place_name);
                    Glide.with(mContext).load(place_img_path)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.ic_store_icon)
                            .skipMemoryCache(true)
                            .into(binding.storeThumnail);

                    if (USER_INFO_ID.equals(place_owner_id)) {
                        USER_INFO_AUTH = "0";
                    } else {
                        USER_INFO_AUTH = "1";
                    }

                    dlog.i("place_owner_id : " + place_owner_id);
                    dlog.i("USER_INFO_ID : " + USER_INFO_ID);
                    dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);

                    shardpref.putString("USER_INFO_AUTH", USER_INFO_AUTH);

                    binding.title.setText(place_name);
                    binding.memberCnt.setText(place_totalcnt + "명");
                    if (new Integer(place_totalcnt) > 0) {
                        binding.addMemberArea.setVisibility(View.GONE);
                        binding.payLayout.setVisibility(View.VISIBLE);
                    } else {
                        binding.addMemberArea.setVisibility(View.VISIBLE);
                        binding.payLayout.setVisibility(View.VISIBLE);
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

    private void getWifiState() {
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
                                    if (Response.getJSONObject(0).getString("io_method").equals("y")) {
                                        binding.wifiSwitch.setChecked(true);
                                    } else {
                                        binding.wifiSwitch.setChecked(false);
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

    public void setWifiOnOff(String state) {
        dlog.i("--------setWifiOnOff--------");
        dlog.i("PlaceWorkCheck id : " + place_id);
        dlog.i("PlaceWorkCheck state : " + state);
        dlog.i("--------setWifiOnOff--------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UpdateIoMethod.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UpdateIoMethod api = retrofit.create(UpdateIoMethod.class);
        Call<String> call = api.getData(place_id, state);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("setWifiOnOff jsonResponse length : " + jsonResponse.length());
                            dlog.i("setWifiOnOff jsonResponse : " + jsonResponse);
                            try {
                                if (jsonResponse.replace("\"", "").equals("success")) {
                                    String statetv = "";
                                    if (state.equals("y")) {
                                        statetv = "활성화 되었습니다";
                                    } else {
                                        statetv = "비활성화 되었습니다";
                                    }
                                    Toast_Nomal("와이파이 설정이 " + statetv);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    public void PlaceWorkCheck(String place_id, String auth, String kind) {
        dlog.i("--------PlaceWorkCheck--------");
        dlog.i("PlaceWorkCheck place_id : " + place_id);
        dlog.i("PlaceWorkCheck auth : " + auth);
        dlog.i("PlaceWorkCheck kind : " + kind);
        dlog.i("--------PlaceWorkCheck--------");
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
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    dlog.i("Response length : " + Response.length());
                                    try {
                                        if (kind.equals("0")) {
                                            binding.inCnt.setText(Response.getJSONObject(0).getString("i_cnt"));
                                            binding.outCnt.setText(Response.getJSONObject(0).getString("o_cnt"));
                                            //결근 숫자에서 휴가숫자는 빠지지 않기때문에 결근-휴가수를 빼줘야한다
                                            if (Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) == 0) {
                                                binding.notinCnt.setText(Response.getJSONObject(0).getString("absence_cnt"));
                                            } else if (Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) > 0) {
                                                binding.notinCnt.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt"))
                                                        - Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
                                            }
                                            binding.restCnt.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("rest_cnt"))
                                                    + Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));

                                            dlog.i("-----MainData1-----");
                                            dlog.i("i_cnt : " + Response.getJSONObject(0).getString("i_cnt"));
                                            dlog.i("o_cnt : " + Response.getJSONObject(0).getString("o_cnt"));
                                            dlog.i("absence_cnt : " + Response.getJSONObject(0).getString("absence_cnt"));
                                            dlog.i("vaca_cnt : " + Response.getJSONObject(0).getString("vaca_cnt"));
                                            dlog.i("rest_cnt : " + Response.getJSONObject(0).getString("rest_cnt"));
                                            int allPay = 0;
                                            for (int i = 0; i < Response.length(); i++) {
                                                allPay += Integer.parseInt(Response.getJSONObject(i).getString("recent_pay").replace(",", ""));
                                            }
                                            allPay = allPay - Integer.parseInt(Response.getJSONObject(0).getString("deductpay").replace(",", ""));
                                            DecimalFormat myFormatter = new DecimalFormat("###,###");
                                            binding.paynum.setText(myFormatter.format(allPay) + "원");
                                            binding.payText.setText("급여 이체 " + myFormatter.format(allPay) + "원");
                                            dlog.i("allPay : " + myFormatter.format(allPay));
                                            dlog.i("-----MainData1-----");

                                            mList = new ArrayList<>();
                                            mAdapter = new MainMemberLAdapter(mContext, mList);
                                            //binding.importantList.setAdapter(mAdapter);
                                            // binding.importantList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                            dlog.i("SIZE : " + Response.length());
                                            if (jsonResponse.equals("[]")) {
                                                dlog.i("SetNoticeListview Thread run! ");
                                                dlog.i("GET SIZE : " + Response.length());
                                            } else {
                                                for (int i = 0; i < Response.length(); i++) {
                                                    JSONObject jsonObject = Response.getJSONObject(i);
                                                    mAdapter.addItem(new MainMemberLData.MainMemberLData_list(
                                                            jsonObject.getString("id"),
                                                            jsonObject.getString("join_date"),
                                                            jsonObject.getString("user_id"),
                                                            jsonObject.getString("user_name"),
                                                            jsonObject.getString("user_img"),
                                                            jsonObject.getString("recent_pay")
                                                    ));
                                                }
                                                dlog.i("mList : " + mList.get(0).getUser_name());
                                                mAdapter.setOnItemClickListener(new MainMemberLAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View v, int position) {
                                                        if (USER_INFO_AUTH.isEmpty()) {
                                                            isAuth();
                                                        } else {
                                                        }
                                                    }
                                                });

                                            }
                                            mAdapter.notifyDataSetChanged();
                                            mAdapter.setOnItemClickListener(new MainMemberLAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(View v, int position) {
                                                    if (USER_INFO_AUTH.isEmpty()) {
                                                        isAuth();
                                                    } else {
                                                        shardpref.putString("Tap", "1");
                                                        if (USER_INFO_AUTH.equals("0")) {
                                                            pm.PayManagement(mContext);
                                                        } else {
                                                            pm.PayManagement2(mContext);
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            mList2 = new ArrayList<>();
                                            mAdapter2 = new MainNotiLAdapter(mContext, mList2);
                                            binding.mainNotiList.setAdapter(mAdapter2);
                                            binding.mainNotiList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                            dlog.i("SIZE : " + Response.length());

                                            if (Response.length() == 0) {
                                                binding.limitNotitv.setText("별도의 공지사항이 없습니다.");
                                                binding.limitNotitv.setTextColor(Color.parseColor("#949494"));
                                                binding.mainNotiList.setVisibility(View.GONE);
                                            } else {
                                                binding.limitNotitv.setVisibility(View.GONE);
                                                binding.mainNotiList.setVisibility(View.VISIBLE);

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
                                                        if (isAuth == 1) {
                                                            isAuth();
                                                        } else {
                                                            pm.FeedList(mContext);
                                                        }
                                                    }
                                                });
                                            }
                                            mAdapter2.notifyDataSetChanged();
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

    String id = "";
    String user_id = "";
    String type = "";
    String get_token = "";
    String channel1 = "1";
    String channel2 = "1";
    String channel3 = "1";
    String channel4 = "1";
    String channel5 = "1";

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
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
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
                                    channel5 = "1";
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
                                    channel5 = Response.getJSONObject(0).getString("channel5");

                                    shardpref.putString("token", token);
                                    shardpref.putString("type", type);
                                    shardpref.putBoolean("channelId1", channel1.equals("1"));
                                    shardpref.putBoolean("channelId2", channel2.equals("1"));
                                    shardpref.putBoolean("channelId3", channel3.equals("1"));
                                    shardpref.putBoolean("channelId4", channel4.equals("1"));
                                    shardpref.putBoolean("channelId5", channel5.equals("1"));

                                    dlog.i("channel1 : " + channel1);
                                    dlog.i("channel2 : " + channel2);
                                    dlog.i("channel3 : " + channel3);
                                    dlog.i("channel4 : " + channel4);
                                    dlog.i("channel5 : " + channel5);
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
        dlog.i("channel5 :" + channel5);
        dlog.i("------FcmTokenCreate-------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMCrerateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMCrerateInterface api = retrofit.create(FCMCrerateInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, type, token, channel1, channel2, channel3, channel4, channel5);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
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
        dlog.i("channel5 :" + channel5);
        dlog.i("------FcmTokenUpdate-------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMUpdateInterface api = retrofit.create(FCMUpdateInterface.class);
        Call<String> call = api.getData(id, token, channel1, channel2, channel3, channel4, channel5);
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

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) binding.getRoot().findViewById(R.id.toast_layout));
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
}
