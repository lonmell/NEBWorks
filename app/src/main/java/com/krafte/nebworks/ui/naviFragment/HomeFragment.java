package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkTapMemberAdapter;
import com.krafte.nebworks.bottomsheet.MemberOption;
import com.krafte.nebworks.data.WorkStatusTapData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMCrerateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FCMUpdateInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.dataInterface.MainWorkCntInterface;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.HomefragmentBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RandomOut;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 2022 11 03 방창배 작성 / 점주용 페이지
 */

public class HomeFragment extends Fragment {
    private final static String TAG = "HomeFragment";
    private HomefragmentBinding binding;

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

    ArrayList<WorkStatusTapData.WorkStatusTapData_list> mList;
    WorkTapMemberAdapter mAdapter = null;

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

        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            setBtnEvent();
            dlog.i("HomeFragment START!");
            place_id = shardpref.getString("place_id", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-1");

            //사용자 ID로 FCM 보낼수 있도록 토픽 세팅
            FirebaseMessaging.getInstance().subscribeToTopic("P" + place_id).addOnCompleteListener(task -> {
                String msg = getString(R.string.msg_subscribed);
                if (!task.isSuccessful()) {
                    msg = getString(R.string.msg_subscribe_failed);
                }
                dlog.i("msg : " + msg);
            });

            //0-관리자 / 1- 근로자
            dlog.i("gotoplace location view USER_INFO_AUTH : " + USER_INFO_AUTH);
            //USER_INFO_AUTH 가 -1일때
            if (USER_INFO_AUTH.equals("-1")) {
                USER_INFO_AUTH = place_owner_id.equals(USER_INFO_ID) ? "0" : "1";
                shardpref.putString("USER_INFO_AUTH", USER_INFO_AUTH);
            }
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
    }

    @Override
    public void onResume() {
        super.onResume();
        UserCheck(USER_INFO_EMAIL);
        getPlaceData();
        PlaceWorkCheck(place_id);
        SetAllMemberList();
    }

    public void setBtnEvent() {
        binding.cardview00.setOnClickListener(v -> {
            pm.FeedList(mContext);
        });

        binding.itemArea.setOnClickListener(v -> {
            shardpref.putString("USER_INFO_AUTH", "0");
            pm.PlaceList(mContext);
        });

        binding.allMemberGo.setOnClickListener(v -> {
            shardpref.putInt("SELECT_POSITION", 1);
            pm.MemberManagement(mContext);
        });

        binding.addMemberBtn.setOnClickListener(v -> {
            MemberOption mo = new MemberOption();
            mo.show(getChildFragmentManager(), "MemberOption");
        });

        binding.homeMenu01.setOnClickListener(v -> {
            pm.MemberManagement(mContext);
        });
        binding.homeMenu02.setOnClickListener(v -> {
            pm.Approval(mContext);
        });
        binding.homeMenu03.setOnClickListener(v -> {
            pm.PayManagement(mContext);
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
                                binding.importantList.setVisibility(View.GONE);
                            } else {
                                //직원이 한명이라도 있을때
                                dlog.i("SIZE 2 : " + Response.length());
                                binding.addMemberArea.setVisibility(View.GONE);
                                binding.importantList.setVisibility(View.VISIBLE);
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
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);


                                    place_name = Response.getJSONObject(0).getString("name");
                                    place_owner_id = Response.getJSONObject(0).getString("owner_id");
                                    place_owner_name = Response.getJSONObject(0).getString("owner_name");
                                    registr_num = Response.getJSONObject(0).getString("registr_num");
                                    store_kind = Response.getJSONObject(0).getString("store_kind");
                                    place_address = Response.getJSONObject(0).getString("address");
                                    place_latitude = Response.getJSONObject(0).getString("latitude");
                                    place_longitude = Response.getJSONObject(0).getString("longitude");
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
                                    binding.memberCnt.setText(place_totalcnt);
                                    newlyFeedOne();
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

    public void newlyFeedOne() {
        //i : 0 = 정렬없음 / 1 = 오름차순 / 2 = 내림차순
        dlog.i("setRecyclerView place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "", "2","1");
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

                        dlog.i("place_feed_cnt : " + Response.length());

                        if (Response.length() == 0) {
                            binding.limitNotitv.setText("별도의 공지사항이 없습니다.");
                            binding.limitNotitv.setTextColor(Color.parseColor("#949494"));
                        } else {
                            String feedtv = Response.getJSONObject(0).getString("title");
                            binding.limitNotitv.setText(feedtv);
                            binding.limitNotitv.setTextColor(Color.parseColor("#000000"));
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

    public void UserCheck(String account) {
        dlog.i("UserCheck account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSelectInterface api = retrofit.create(UserSelectInterface.class);
        Call<String> call = api.getData(account);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("UserCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    String id = Response.getJSONObject(0).getString("id");
//                                    String kind = Response.getJSONObject(0).getString("kind");
                                    String name = Response.getJSONObject(0).getString("name");
                                    String account = Response.getJSONObject(0).getString("account"); //-- 가입할때의 게정
//                                    String employee_no = Response.getJSONObject(0).getString("employee_no"); //-- 사번
//                                    String department = Response.getJSONObject(0).getString("department");
//                                    String position = Response.getJSONObject(0).getString("position");
                                    String img_path = Response.getJSONObject(0).getString("img_path");

                                    try {
                                        dlog.i("------UserCheck-------");
                                        dlog.i("프로필 사진 url : " + img_path);
                                        dlog.i("성명 : " + name);
//                                        dlog.i("부서 : " + department);
//                                        dlog.i("직책 : " + position);
//                                        dlog.i("사번 : " + employee_no); //-- 사번이 없는 회사도 있을 수 있으니 필수X
                                        dlog.i("------UserCheck-------");

                                        if (place_owner_id.equals(id)) {
                                            USER_INFO_AUTH = "0";
                                            binding.gotoPlace.setVisibility(View.VISIBLE);
                                        } else {
                                            USER_INFO_AUTH = "1";
                                            binding.gotoPlace.setVisibility(View.GONE);
                                        }
                                        getFCMToken();
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

    public void PlaceWorkCheck(String place_id) {
        dlog.i("PlaceWorkCheck place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainWorkCntInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainWorkCntInterface api = retrofit.create(MainWorkCntInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("PlaceWorkCheck jsonResponse length : " + response.body().length());
                            dlog.i("PlaceWorkCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
//                                    i_cnt;                // 출근 count(퇴근한 인원은 제외)
//                                    o_cnt;                // 퇴근 count
//                                    task_total_cnt;       // 할일 전체
//                                    task_complete_cnt;    // 완료된 업무
//                                    task_incomplete_cnt;  // 미완료 업무
//                                    approval_total_cnt;   // 결재 전체
//                                    waiting_cnt;          // 결재 대기
//                                    approval_cnt;         // 결재 승인
//                                    reject_cnt;           // 결재 반려
//                                    rest_cnt              // 휴무 직원 수
//                                     absence_cnt           // 결석
                                    try {
                                        String i_cnt = Response.getJSONObject(0).getString("i_cnt");
                                        String o_cnt = Response.getJSONObject(0).getString("o_cnt");
                                        String task_total_cnt = Response.getJSONObject(0).getString("task_total_cnt");
                                        String task_complete_cnt = Response.getJSONObject(0).getString("task_complete_cnt"); //-- 가입할때의 게정
                                        String task_incomplete_cnt = Response.getJSONObject(0).getString("task_incomplete_cnt"); //-- 사번
                                        String approval_total_cnt = Response.getJSONObject(0).getString("approval_total_cnt");
                                        String waiting_cnt = Response.getJSONObject(0).getString("waiting_cnt");
                                        String approval_cnt = Response.getJSONObject(0).getString("approval_cnt");
                                        String reject_cnt = Response.getJSONObject(0).getString("reject_cnt");
                                        String rest_cnt = Response.getJSONObject(0).getString("rest_cnt");
                                        String absence_cnt = Response.getJSONObject(0).getString("absence_cnt");

                                        dlog.i("------PlaceWorkCheck-------");
                                        dlog.i("출근 count(퇴근한 인원은 제외) : " + i_cnt);
                                        dlog.i("퇴근 count : " + o_cnt);
                                        dlog.i("할일 전체 : " + task_total_cnt);
                                        dlog.i("완료된 업무 : " + task_complete_cnt);
                                        dlog.i("미완료 업무 : " + task_incomplete_cnt);
                                        dlog.i("결재 전체 : " + approval_total_cnt);
                                        dlog.i("결재 대기 : " + waiting_cnt);
                                        dlog.i("결재 승인 : " + approval_cnt);
                                        dlog.i("결재 반려 : " + reject_cnt);
                                        dlog.i("휴무 : " + rest_cnt);
                                        dlog.i("결석/미출근 : " + absence_cnt);
                                        int total_cnt = 0;
                                        total_cnt = Integer.parseInt(i_cnt) + Integer.parseInt(o_cnt) + Integer.parseInt(task_total_cnt)
                                                + Integer.parseInt(task_complete_cnt) + Integer.parseInt(task_incomplete_cnt) + Integer.parseInt(approval_total_cnt)
                                                + Integer.parseInt(waiting_cnt) + Integer.parseInt(approval_cnt) + Integer.parseInt(reject_cnt);
                                        binding.inCnt.setText(i_cnt);
                                        binding.notinCnt.setText(absence_cnt);
                                        binding.outCnt.setText(o_cnt);
                                        binding.restCnt.setText(rest_cnt);
//                                        binding.noticeCnt.setText(String.valueOf(total_cnt));
//                                        binding.stateCnt01.setText("출근  " + i_cnt);
//                                        binding.stateCnt02.setText("퇴근  " + o_cnt);
//                                        binding.stateCnt05.setText(task_incomplete_cnt);
//                                        binding.stateCnt06.setText(task_complete_cnt);
//                                        binding.stateCnt07.setText(waiting_cnt);
//                                        binding.stateCnt08.setText(approval_cnt);
//                                        binding.stateCnt09.setText(reject_cnt);
                                        dlog.i("------PlaceWorkCheck-------");
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
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("FcmStateSelect jsonResponse length : " + response.body().length());
                            dlog.i("FcmStateSelect jsonResponse : " + response.body());
                            try {

                                if (response.body().replace("[", "").replace("]", "").length() == 0) {
                                    id = place_id;
                                    user_id = USER_INFO_ID;
                                    get_token = "";
                                    type = place_owner_id.equals(USER_INFO_ID) ? "0" : "1";
                                    channel1 = "1";
                                    channel2 = "1";
                                    channel3 = "1";
                                    channel4 = "1";
                                } else {
                                    JSONArray Response = new JSONArray(response.body());
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
                dlog.i("Response Result : " + response.body());
                if (response.body().replace("\"", "").equals("success")) {
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

    private void CountingSubscribeTopic() {

    }
}
