package com.krafte.kogas.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.krafte.kogas.R;
import com.krafte.kogas.dataInterface.FCMCrerateInterface;
import com.krafte.kogas.dataInterface.FCMSelectInterface;
import com.krafte.kogas.dataInterface.FCMUpdateInterface;
import com.krafte.kogas.dataInterface.MainWorkCntInterface;
import com.krafte.kogas.dataInterface.PlaceThisDataInterface;
import com.krafte.kogas.dataInterface.UserSelectInterface;
import com.krafte.kogas.databinding.ActivityMainBinding;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 2022-10-05 방창배 작성
 * */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    Context mContext;

    ImageView home_icon;
    TextView home_tv;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    //shared 
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String place_management_office = "";
    String place_address = "";
    String place_latitude = "";
    String place_longitude = "";
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";
    String place_totalcnt = "";

    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_AUTH = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();

        //UI 데이터 세팅
        try {
            place_id = shardpref.getString("place_id", "0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-1");
            home_icon = findViewById(R.id.home_icon);
            home_tv = findViewById(R.id.home_tv);
            home_icon.setBackgroundResource(R.drawable.home_on_resize);
            home_tv.setTextColor(Color.parseColor("#6395EC"));

            getPlaceData();
            PlaceWorkCheck(place_id);

            //사용자 ID로 FCM 보낼수 있도록 토픽 세팅
            FirebaseMessaging.getInstance().subscribeToTopic("P"+place_id).addOnCompleteListener(task -> {
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
                if (place_owner_id.equals(USER_INFO_ID)) {
                    USER_INFO_AUTH = "0";
                } else {
                    USER_INFO_AUTH = "1";
                }
                switch (USER_INFO_AUTH) {
                    case "0":
                        binding.gotoPlace.setVisibility(View.VISIBLE);
                        break;
                    case "1":
                        binding.gotoPlace.setVisibility(View.GONE);
                        break;
                }
            } else {
                switch (USER_INFO_AUTH) {
                    case "0":
                        binding.gotoPlace.setVisibility(View.VISIBLE);
                        break;
                    case "1":
                        binding.gotoPlace.setVisibility(View.GONE);
                        break;
                }
            }
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        UserCheck(USER_INFO_EMAIL);
    }


    public void btnOnclick(View view) {
        if (view.getId() == R.id.out_store) {
            pm.PlaceListBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            dlog.i("Main Page");
        } else if (view.getId() == R.id.bottom_navigation02) {
            pm.PlaceWorkGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation03) {
            pm.CalenderGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation04) {
            pm.WorkStateListGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation05) {
            pm.MoreGo(mContext);
        }
    }

    public void setBtnEvent() {
//        binding.outStore.setOnClickListener(v -> {
//            pm.PlaceListBack(mContext);
//        });
//        binding.bottomNavigation02.setOnClickListener(v -> {
//            pm.PlaceWorkGo(mContext);
//        });
//        binding.bottomNavigation05.setOnClickListener(v -> {
//            pm.MoreGo(mContext);
//        });


        binding.workstateGo.setOnClickListener(v -> {
            pm.WorkStateListGo(mContext);
        });


        binding.taskstateGo.setOnClickListener(v -> {
            pm.PlaceWorkGo(mContext);
            shardpref.putInt("SELECT_POSITION",1);
        });

        binding.taskoverGo.setOnClickListener(v -> {
            pm.ApprovalGo(mContext);
            shardpref.putInt("SELECT_POSITION",0);
        });

        binding.approval1Go.setOnClickListener(v -> {
            pm.ApprovalGo(mContext);
            shardpref.putInt("SELECT_POSITION",0);
        });
        binding.approval2Go.setOnClickListener(v -> {
            pm.ApprovalGo(mContext);
            shardpref.putInt("SELECT_POSITION",1);
        });
        binding.approval3Go.setOnClickListener(v -> {
            pm.ApprovalGo(mContext);
            shardpref.putInt("SELECT_POSITION",2);
        });

        binding.myplace.setOnClickListener(v -> {
            shardpref.putString("return_page", "MainActivity");
            pm.MyPlsceGo(mContext);
        });

        binding.communityBtn.setOnClickListener(view -> {
            //지시사항 페이지
            Intent intent = new Intent(mContext, InstructionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.gotoPlace.setOnClickListener(v -> {
            pm.PlaceEditGo(mContext);
        });

        binding.paymentText.setOnClickListener(v -> {
            pm.MemberGo(mContext);
        });

        binding.businessApproval.setOnClickListener(view -> {
            pm.ApprovalGo(mContext);
        });

        binding.notice.setOnClickListener(v -> {
            pm.NotifyListGo(mContext);
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
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("getPlaceData jsonResponse length : " + response.body().length());
                            dlog.i("getPlaceData jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());

                                    place_name = Response.getJSONObject(0).getString("name");
                                    place_owner_id = Response.getJSONObject(0).getString("owner_id");
                                    place_owner_name = Response.getJSONObject(0).getString("owner_name");
                                    place_management_office = Response.getJSONObject(0).getString("management_office");
                                    place_address = Response.getJSONObject(0).getString("address");
                                    place_latitude = Response.getJSONObject(0).getString("latitude");
                                    place_longitude = Response.getJSONObject(0).getString("longitude");
                                    place_start_time = Response.getJSONObject(0).getString("start_time");
                                    place_end_time = Response.getJSONObject(0).getString("end_time");
                                    place_img_path = Response.getJSONObject(0).getString("img_path");
                                    place_start_date = Response.getJSONObject(0).getString("start_date");
                                    place_created_at = Response.getJSONObject(0).getString("created_at");
                                    place_totalcnt = Response.getJSONObject(0).getString("total_cnt");

                                    if (place_owner_id.equals(USER_INFO_ID)) {
                                        USER_INFO_AUTH = "0";
                                    } else {
                                        USER_INFO_AUTH = "1";
                                    }
                                    shardpref.putString("USER_INFO_AUTH", USER_INFO_AUTH);
                                    shardpref.putString("place_name", place_name);
                                    shardpref.putString("place_owner_id", place_owner_id);
                                    shardpref.putString("place_owner_name", place_owner_name);
                                    shardpref.putString("place_management_office", place_management_office);
                                    shardpref.putString("place_address", place_address);
                                    shardpref.putString("place_latitude", place_latitude);
                                    shardpref.putString("place_longitude", place_longitude);
                                    shardpref.putString("place_start_time", place_start_time);
                                    shardpref.putString("place_end_time", place_end_time);
                                    shardpref.putString("place_img_path", place_img_path);
                                    shardpref.putString("place_start_date", place_start_date);
                                    shardpref.putString("place_created_at", place_created_at);
                                    shardpref.putString("place_totalcnt", place_totalcnt);

                                    Glide.with(mContext).load(place_img_path)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.no_image)
                                            .skipMemoryCache(true)
                                            .into(binding.storeThumnail);

                                    binding.placeName.setText(place_management_office);

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
                                    binding.name.setText(place_owner_name);
                                    binding.date.setText(place_start_date);
                                    binding.itemPeoplecnt.setText(place_totalcnt);
                                    binding.memberCntY.setVisibility(View.GONE);
                                    binding.approvalCntY.setVisibility(View.GONE);
                                    binding.noticeCntY.setVisibility(View.GONE);
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
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    String id = Response.getJSONObject(0).getString("id");
                                    String kind = Response.getJSONObject(0).getString("kind");
                                    String name = Response.getJSONObject(0).getString("name");
                                    String account = Response.getJSONObject(0).getString("account"); //-- 가입할때의 게정
                                    String employee_no = Response.getJSONObject(0).getString("employee_no"); //-- 사번
                                    String department = Response.getJSONObject(0).getString("department");
                                    String position = Response.getJSONObject(0).getString("position");
                                    String img_path = Response.getJSONObject(0).getString("img_path");

                                    try {
                                        dlog.i("------UserCheck-------");
                                        dlog.i("프로필 사진 url : " + img_path);
                                        dlog.i("직원소속구분분 : " + (kind.equals("0") ? "정직원" : "협력업체"));
                                        dlog.i("성명 : " + name);
                                        dlog.i("부서 : " + department);
                                        dlog.i("직책 : " + position);
                                        dlog.i("사번 : " + employee_no); //-- 사번이 없는 회사도 있을 수 있으니 필수X
                                        dlog.i("------UserCheck-------");
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
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
//                                    i_cnt;                // 출근 count(퇴근한 인원은 제외)
//                                    o_cnt;                // 퇴근 count
//                                    task_total_cnt;       // 할일 전체
//                                    task_complete_cnt;    // 완료된 업무
//                                    task_incomplete_cnt;  // 미완료 업무
//                                    approval_total_cnt;   // 결재 전체
//                                    waiting_cnt;          // 결재 대기
//                                    approval_cnt;         // 결재 승인
//                                    reject_cnt;           // 결재 반려

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

                                        dlog.i("------UserCheck-------");
                                        dlog.i("출근 count(퇴근한 인원은 제외) : " + i_cnt);
                                        dlog.i("퇴근 count : " + o_cnt);
                                        dlog.i("할일 전체 : " + task_total_cnt);
                                        dlog.i("완료된 업무 : " + task_complete_cnt);
                                        dlog.i("미완료 업무 : " + task_incomplete_cnt);
                                        dlog.i("결재 전체 : " + approval_total_cnt);
                                        dlog.i("결재 대기 : " + waiting_cnt);
                                        dlog.i("결재 승인 : " + approval_cnt);
                                        dlog.i("결재 반려 : " + reject_cnt);
                                        int total_cnt = 0;
                                        total_cnt = Integer.parseInt(i_cnt) + Integer.parseInt(o_cnt) + Integer.parseInt(task_total_cnt)
                                                + Integer.parseInt(task_complete_cnt) + Integer.parseInt(task_incomplete_cnt) + Integer.parseInt(approval_total_cnt)
                                                + Integer.parseInt(waiting_cnt) + Integer.parseInt(approval_cnt) + Integer.parseInt(reject_cnt);

                                        binding.noticeCnt.setText(String.valueOf(total_cnt));
                                        binding.stateCnt01.setText("근무 중  " + i_cnt);
                                        binding.stateCnt02.setText("퇴근  " + o_cnt);
                                        binding.stateCnt05.setText(task_incomplete_cnt);
                                        binding.stateCnt06.setText(task_complete_cnt);
                                        binding.stateCnt07.setText(waiting_cnt);
                                        binding.stateCnt08.setText(approval_cnt);
                                        binding.stateCnt09.setText(reject_cnt);
                                        dlog.i("------UserCheck-------");
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
                    runOnUiThread(() -> {
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        pm.PlaceListBack(mContext);
    }
}