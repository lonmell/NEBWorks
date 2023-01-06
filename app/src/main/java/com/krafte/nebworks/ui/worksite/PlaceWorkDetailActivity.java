package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.dataInterface.TaskApprovalInterface;
import com.krafte.nebworks.dataInterface.TaskSaveInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityPlaceworkDetailBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
//--결재페이지에서 쓸것
public class PlaceWorkDetailActivity extends AppCompatActivity {
    private final static String TAG = "PlaceWorkDetailActivity";
    private ActivityPlaceworkDetailBinding binding;
    Context mContext;
    int GALLEY_CODE = 10;
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private static final int PINSELECT_LOCATION_ACTIVITY = 20000;

    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();
    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";

    //Other
    Drawable icon_off;
    Drawable icon_on;
    GetResultData resultData = new GetResultData();

    String fileName = "";
    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/nebworks/";
    String ProfileUrl = "";

    Dlog dlog = new Dlog();
    boolean channelId = false;
    boolean rcvchannelId2 = false;

    String task_no = "";
    String user_id = "";
    String place_id = "";
    String place_name = "";
    String WorkTitle = "";
    String WorkContents = "";
    String writer_id = "";
    String kind = "";
    String WorkDay = "";
    String complete_yn = "y";
    String incomplete_reason = "";
    String approval_state = "";

    //업무 종류
    String complete_kind = "1";

    //시작시간
    String start_time = "-99";

    //마감시간
    int SelectEndTime = 1;
    String end_time = "-99";
    String task_check = "";
    String message = "업무가 배정되었습니다.";
    String topic = "";
    String click_action = "";

    Handler mHandler;
    String sendTopic = "";
    String sendToken = "";
    String create_date = "";

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceworkDetailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
        icon_on = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_on);

        setBtnEvent();
        //Singleton Area
        place_id        = PlaceCheckData.getInstance().getPlace_id();
        place_name      = PlaceCheckData.getInstance().getPlace_name();
        USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
        USER_INFO_NAME  = UserCheckData.getInstance().getUser_name();
        USER_INFO_EMAIL = UserCheckData.getInstance().getUser_account();
        USER_INFO_AUTH  = UserCheckData.getInstance().getUser_auth();

        //shardpref Area
        shardpref   = new PreferenceHelper(mContext);
        task_no     = shardpref.getString("task_no", "");

        shardpref.putInt("SELECT_POSITION", 0);
        shardpref.putInt("SELECT_POSITION_sub", 1);

        fileName = USER_INFO_ID;
        dateFormat = new SimpleDateFormat("HH:mm:ss", getResources().getConfiguration().locale);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getTime = dateFormat.format(date);
        int setToday = Integer.parseInt(getTime.substring(0, 2));
        binding.workSaveAccept.setClickable(true);

        if (saveBitmap != null) {
            binding.clearImg.setVisibility(View.VISIBLE);
            binding.imgPlus.setVisibility(View.GONE);
        } else {
            binding.clearImg.setVisibility(View.GONE);
            binding.imgPlus.setVisibility(View.VISIBLE);
        }

        getTaskContents();
        UserCheck(USER_INFO_EMAIL);
        /**
         * task_kind
         * 1 - 일반업무
         * 2 - 개인업무
         * 3 - 휴가신청
         *
         * task_success_method
         * 1 = 인증사진
         * 2 = 체크
         *
         * task_loop_kind
         * 1 = 매일
         * 2 = 주간
         * 3 = 월간
         * 4 = 하루
         *
         * task_settime
         * 1 = 주간
         * 2 = 야간
         * 3 = 주/야간( 24시간 )
         *
         * task_endtime01
         * 1 = 오전 / 2 = 오후
         *
         * state
         * 0 - 처리중
         * 1 - 승인
         * 2 - 반려
         * 3 - 거부
         *
         * task_check
         * 1 - 완료
         * 2 - 미완료
         *
         * task_notsuccess_txt
         *
         */

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
                                    String name = Response.getJSONObject(0).getString("name");
                                    String account = Response.getJSONObject(0).getString("account"); //-- 가입할때의 게정
                                    String employee_no = Response.getJSONObject(0).getString("employee_no"); //-- 사번
                                    String department = Response.getJSONObject(0).getString("department");
                                    String position = Response.getJSONObject(0).getString("position");
                                    String img_path = Response.getJSONObject(0).getString("img_path");

                                    try {
                                        dlog.i("------UserCheck-------");
                                        dlog.i("프로필 사진 url : " + img_path);
                                        dlog.i("성명 : " + name);
                                        dlog.i("부서 : " + department);
                                        dlog.i("직책 : " + position);
                                        dlog.i("사번 : " + employee_no); //-- 사번이 없는 회사도 있을 수 있으니 필수X
                                        dlog.i("------UserCheck-------");

                                        Glide.with(mContext).load(img_path)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .skipMemoryCache(true)
                                                .into(binding.workimg);
                                        binding.name.setText(name);
                                        binding.department.setText(department + " " + position);

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

    @SuppressLint("SetTextI18n")
    private void getTaskContents() {

        task_no = shardpref.getString("task_no", "0");
        writer_id = shardpref.getString("writer_id", "0");
        kind = shardpref.getString("kind", "0");        // 0:할일, 1:일정
        WorkTitle = shardpref.getString("title", "0");
        WorkContents = shardpref.getString("contents", "0");
        complete_kind = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
        user_id = shardpref.getString("users", "0");
        WorkDay = shardpref.getString("task_date", "0");
        start_time = shardpref.getString("start_time", "0");
        end_time = shardpref.getString("end_time", "0");
        create_date = shardpref.getString("task_date", "0");

        ProfileUrl = shardpref.getString("img_path", "0").equals("null") ? "" : shardpref.getString("img_path", "0");
        complete_yn = shardpref.getString("complete_yn", "null");// y:완료, n:미완료
        incomplete_reason = shardpref.getString("incomplete_reason", "n").equals("null") ? "" : shardpref.getString("incomplete_reason", "0"); // n: 미완료 사유
        approval_state = shardpref.getString("approval_state", "3");// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전
        try {
            dlog.i("-----getTaskContents-----");
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("task_no : " + task_no);
            dlog.i("writer_id : " + writer_id);
            dlog.i("kind : " + kind);
            dlog.i("WorkTitle : " + WorkTitle);
            dlog.i("WorkContents : " + WorkContents);
            dlog.i("complete_kind : " + complete_kind);
            dlog.i("user_id : " + user_id);
            dlog.i("WorkDay : " + WorkDay);
            dlog.i("start_time : " + start_time);
            dlog.i("end_time : " + end_time);
            dlog.i("create_date : " + create_date);
            dlog.i("ProfileUrl : " + ProfileUrl);
            dlog.i("complete_yn : " + complete_yn);
            dlog.i("incomplete_reason : " + incomplete_reason);
            dlog.i("approval_state : " + approval_state);
            dlog.i("user_id.contains(USER_INFO_ID) : " + user_id.contains(USER_INFO_ID));
            dlog.i("-----getTaskContents-----");

            binding.endtimeTxt.setText(end_time);
            binding.inputWorktitle.setText(WorkTitle.equals("null") ? "" : WorkTitle);
            binding.workContentSet.setText(WorkContents.equals("null") ? "" : WorkContents);
            binding.incompleteInput.setText(incomplete_reason.equals("null") ? "" : incomplete_reason);
            String approvalStatetv = "";
            // 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전

            if(approval_state.equals("0")){
                approvalStatetv = "결재대기";
                binding.apploveState.setTextColor(Color.parseColor("#696969"));
            }else if(approval_state.equals("1")){
                approvalStatetv = "승인";
                binding.apploveState.setTextColor(Color.parseColor("#5B93FF"));
            }else if(approval_state.equals("2")){
                approvalStatetv = "반려";
                binding.apploveState.setTextColor(Color.parseColor("#FF0000"));
            }else if(approval_state.equals("3")){
                binding.apploveState.setVisibility(View.GONE);
            }
            binding.apploveState.setText(approvalStatetv);

            if (user_id.contains(USER_INFO_ID)) {
                if(!complete_yn.equals("null")){
                    binding.success01Txt.setClickable(false);
                    binding.success01Txt.setEnabled(false);
                    binding.success02Txt.setEnabled(false);
                    binding.success02Txt.setClickable(false);
                    binding.incompleteInput.setEnabled(false);
                    binding.clearImg.setVisibility(View.GONE);
                    binding.imgPlus.setVisibility(View.GONE);
                    binding.workSaveAccept.setVisibility(View.GONE);
                }else{
                    binding.success01Txt.setClickable(true);
                    binding.success01Txt.setEnabled(true);
                    binding.success02Txt.setEnabled(true);
                    binding.success02Txt.setClickable(true);
                    binding.incompleteInput.setEnabled(true);
                    binding.workSaveAccept.setVisibility(View.VISIBLE);
                }
            } else {
                binding.success01Txt.setClickable(false);
                binding.success01Txt.setEnabled(false);
                binding.success02Txt.setEnabled(false);
                binding.success02Txt.setClickable(false);
                binding.incompleteInput.setEnabled(false);
                binding.clearImg.setVisibility(View.GONE);
                binding.imgPlus.setVisibility(View.GONE);
                binding.workSaveAccept.setVisibility(View.GONE);
            }

            //0:체크, 1:사진
            if (complete_kind.equals("0")) {
                complete_yn = "y";
                binding.imgArea.setVisibility(View.GONE);
                binding.successCheckBox.setVisibility(View.VISIBLE);
                binding.incompleteInput.setBackgroundColor(Color.parseColor("#dcdcdc"));
                binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
            } else {
                binding.imgArea.setVisibility(View.VISIBLE);
                binding.successCheckBox.setVisibility(View.GONE);
                if (!ProfileUrl.equals("null")) {
                    Glide.with(mContext).load(ProfileUrl)
                            .skipMemoryCache(true).into(binding.uploadSuccessImg);
                    binding.clearImg.setVisibility(View.VISIBLE);
                    binding.imgPlus.setVisibility(View.GONE);
                } else {
                    saveBitmap = null;
                    binding.clearImg.setVisibility(View.GONE);
                    binding.imgPlus.setVisibility(View.VISIBLE);
                }
                if (incomplete_reason.isEmpty()) {
                    binding.incompleteInput.setVisibility(View.GONE);
                    binding.incompleteTitle.setVisibility(View.GONE);
                } else {
                    binding.incompleteTitle.setVisibility(View.VISIBLE);
                    binding.incompleteInput.setVisibility(View.VISIBLE);
                }
                binding.clearImg.setOnClickListener(v -> {
                    try {
                        saveBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                        saveBitmap.eraseColor(Color.TRANSPARENT);
                        binding.uploadSuccessImg.setImageBitmap(saveBitmap);
                        binding.uploadSuccessImg.setBackgroundResource(R.drawable.img_box_round);
                        ProfileUrl = "";
                        binding.clearImg.setVisibility(View.GONE);
                        binding.imgPlus.setVisibility(View.VISIBLE);
                        binding.incompleteTitle.setVisibility(View.VISIBLE);
                        binding.incompleteInput.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        dlog.i("clearImg Exception : " + e);
                    }
                });
            }

            //작성자, 작업자 구분
            binding.success01Txt.setOnClickListener(v -> {
                task_check = "1";
                complete_yn = "y";
                binding.incompleteInput.setBackgroundColor(Color.parseColor("#dcdcdc"));
                binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
                binding.incompleteInput.setEnabled(false);
            });
            binding.success02Txt.setOnClickListener(v -> {
                task_check = "2";
                complete_yn = "n";
                binding.incompleteInput.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
                binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                binding.incompleteInput.setEnabled(true);
            });

            binding.uploadSuccessImg.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLEY_CODE);
            });

            binding.workadd01Txt.setText(complete_kind.equals("1") ? "매장사진" : "체크");

//            if (complete_yn.equals("y")) {
//                binding.successCheckArea.setVisibility(View.GONE);
//                binding.uploadSuccessImg.setVisibility(View.VISIBLE);
//            } else if (complete_yn.equals("n")) {
//                binding.successCheckArea.setVisibility(View.VISIBLE);
//                binding.uploadSuccessImg.setVisibility(View.GONE);
//                binding.incompleteInput.setText(incomplete_reason.equals("null") ? "" : incomplete_reason);
//                binding.incompleteInput.setEnabled(false);
//                binding.incompleteInput.setClickable(false);
//            }

            message = "수정된 업무가 있습니다.";
            binding.inputWorktitle.setText(WorkTitle);
            binding.workContentSet.setText(WorkContents);

            if (!end_time.isEmpty()) {

                String ampm = "";
                if (Integer.parseInt(end_time.substring(0, 2)) <= 12) {
                    ampm = " AM";
                    SelectEndTime = 1;
                } else {
                    ampm = " PM";
                    SelectEndTime = 2;
                }
                binding.endtimeTxt.setText(end_time + " " + ampm);
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception : " + e);
        }

    }

    private void setBtnEvent() {
        if (!task_check.isEmpty()) {
            binding.incompleteInput.setEnabled(false);
            if (task_check.equals("1")) {
                binding.incompleteInput.setBackgroundColor(Color.parseColor("#dcdcdc"));
                binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
            } else {
                binding.incompleteInput.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
                binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
            }
        }

        binding.workSaveAccept.setOnClickListener(v -> {
            String task_id = task_no;
            String task_date = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            String reject_reason = binding.incompleteInput.getText().toString();
            //0:체크 1:사진
            if (complete_kind.equals("0")) {
                setSaveTask(task_id, task_date, ProfileUrl, complete_yn, reject_reason);
            } else {
                if (ProfileUrl != null) {
                    setSaveTask(task_id, task_date, ProfileUrl, "y", reject_reason);
                } else {
                    if (!reject_reason.isEmpty()) {
                        setSaveTask(task_id, task_date, ProfileUrl, "y", reject_reason);
                    } else {
                        Toast.makeText(mContext, "매장 사진을 추가해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });


        binding.workDelete.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });

        binding.menu.setOnClickListener(v -> {
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.putInt("SELECT_POSITION_sub",1);
            pm.PlaceWorkBack(mContext);
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        ImgfileMaker = ImageNameMaker();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    RetrofitConnect rc = new RetrofitConnect();
    public void setSaveTask(String task_id, String task_date, String img_path, String complete_yn, String reject_reason) {
        dlog.i("------setSaveTask------");
        dlog.i("task_id : " + task_id);
        dlog.i("task_date : " + task_date);
        dlog.i("img_path : " + img_path);
        dlog.i("complete_yn : " + complete_yn);
        dlog.i("reject_reason : " + reject_reason);
        dlog.i("------setSaveTask------");
        img_path = img_path.equals("null") ? "" : img_path;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSaveInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSaveInterface api = retrofit.create(TaskSaveInterface.class);
        Call<String> call = api.getData(task_id, task_date, img_path, complete_yn, reject_reason);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            runOnUiThread(() -> {
                                Log.i(TAG, "resultData : " + resultData.getRESULT());
                                if (jsonResponse.replace("\"", "").equals("success")) {
                                    dlog.i("ProfileUrl : " + ProfileUrl);
                                    dlog.i("saveBitmap : " + saveBitmap);
                                    if (!ProfileUrl.isEmpty() && saveBitmap != null) {
                                        saveBitmapAndGetURI();
                                    }
                                    setUpdateWorktodo(task_id);
//                                    //근로자일때 -- 저장할때는 알림 필요없음
//                                    topic = task_id;
//                                    message = "업무 결제요청이 도착하였습니다";
//                                    click_action = "PlaceWorkFragment";
//                                    Log.i(TAG, "task_input_id : " + writer_id);
//                                    Log.i(TAG, "task_conduct_id : " + USER_INFO_ID);
//                                    getPushBoolean();
                                } else {
                                    Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
                                }
                            });
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
    private void setUpdateWorktodo(String task_id) {
        dlog.i("setUpdateWorktodo user_id : " + task_id);
        String task_date = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskApprovalInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskApprovalInterface api = retrofit.create(TaskApprovalInterface.class);
        Call<String> call = api.getData(place_id, task_id, task_date, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
//                            dlog.i("http://krafte.net/kogas/task_approval/post.php?place_id="+place_id+"&task_id="+task_id+"&task_date="+task_date+"&user_id="+USER_INFO_ID);
                            if (jsonResponse.replace("\"", "").equals("success")) {
                                Toast_Nomal("결재 요청이 완료되었습니다.");
                                shardpref.putInt("SELECT_POSITION",1);
                                shardpref.putInt("SELECT_POSITION_sub", 1);
                                pm.PlaceWorkBack(mContext);
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

    String type = "";
    String gettoken = "";

    public void getPushBoolean() {
        dlog.i("-----getPushBoolean-----");
        dlog.i("writer_id : " + writer_id);
        dlog.i("type : 0 ");
        type = USER_INFO_AUTH.equals("0") ? "0" : "1";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(writer_id, "0");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
                    type = Response.getJSONObject(0).getString("type");
                    gettoken = Response.getJSONObject(0).getString("token");
                    dlog.i("type : " + type);
                    dlog.i("token : " + gettoken);
                    channelId = Response.getJSONObject(0).getString("channel2").equals("1");
                    String place_id = shardpref.getString("place_id", "0");

                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                        Log.i(TAG, "token : " + token);
                        if (channelId) {
                            FcmTestFunctionCall(writer_id, "", message, gettoken, "1", place_id);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
        dlog.i("-----getPushBoolean-----");
    }

    DBConnection dbConnection = new DBConnection();

    private void FcmTestFunctionCall(String topic, String title, String message, String token, String tag, String place_id) {

        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            dbConnection.FcmTestFunction(topic, "", message, token, click_action, tag, place_id);
            runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
//            pm.BusinessResult(mContext);
//            pm.PlaceWorkBack(mContext);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //이미지 업로드에 필요한 소스 START
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_CODE) {
            if (resultCode == RESULT_OK) {

                String imagePath = "";
                try {
                    //1) data의 주소 사용하는 방법
                    imagePath = data.getDataString(); // "content://media/external/images/media/7215"

                    Glide.with(this)
                            .load(imagePath)
                            .into(binding.uploadSuccessImg);
                    binding.clearImg.setVisibility(View.VISIBLE);
                    binding.imgPlus.setVisibility(View.GONE);

                    Glide.with(getApplicationContext()).asBitmap().load(imagePath).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            saveBitmap = resource;
                        }
                    });

                    final String IMG_FILE_EXTENSION = ".JPEG";
                    String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
                    ProfileUrl = "http://krafte.net/NEBWorks/image/task_img/" + file_name;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                binding.imgPlus.setVisibility(View.VISIBLE);
                binding.clearImg.setVisibility(View.GONE);
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }

    }

    //절대경로를 구한다.
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return url;
    }

    @SuppressLint({"SimpleDateFormat", "LongLogTag"})
    public Uri saveBitmapAndGetURI() {
        //Create Bitmap
//            saveBitmap = CanvasIO.openBitmap(mContext);
        binding.loginAlertText.setVisibility(View.VISIBLE);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        //Create Bitmap -> File
        final String IMG_FILE_EXTENSION = ".JPEG";
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
        String fullFileName = BACKUP_PATH;

        dlog.i("(saveBitmapAndGetURI)ex_storage : " + ex_storage);
        dlog.i("(saveBitmapAndGetURI)USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("(saveBitmapAndGetURI)fullFileName : " + fullFileName);

        File file_path;
        try {
            file_path = new File(fullFileName);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            dlog.i("(saveBitmapAndGetURI)file_path : " + file_path);
            dlog.i("(saveBitmapAndGetURI)file_name : " + file_name);
            file = new File(file_path, file_name);
            FileOutputStream out = new FileOutputStream(file);
            saveBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            ProfileUrl = "http://krafte.net/NEBWorks/image/task_img/" + file_name;
            saveBitmapToFile(file);

            dlog.e("사인 저장 경로 : " + ProfileUrl);
//            binding.loCanvas.setImageBitmap(ProfileUrl);
            Glide.with(mContext).load(ProfileUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(binding.uploadSuccessImg);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            RetrofitInterface retrofitInterface = ApiClient.getApiClient().create(RetrofitInterface.class);
            Call<String> call = retrofitInterface.request(body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("uploaded_file()", "성공 : call = " + call + "response = " + response);

                    if (fileDelete(String.valueOf(file))) {
                        Log.e("uploaded_file()", "기존 이미지 삭제 완료");
                    } else {
                        Log.e("uploaded_file()", "이미지 삭제 오류");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("uploaded_file()", "에러 : " + t.getMessage());
                }
            });
            Log.d("(saveBitmapAndGetURI)이미지 경로 : ", Uri.fromFile(file).toString());

            out.close();
            binding.loginAlertText.setVisibility(View.GONE);
            dlog.i("(saveBitmapAndGetURI)file : " + file);
        } catch (FileNotFoundException exception) {
            dlog.e("FileNotFoundException : " + exception.getMessage());
        } catch (IOException exception) {
            dlog.e("IOException : " + exception.getMessage());
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static boolean fileDelete(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public static class ApiClient {
        private static final String BASE_URL = "http://krafte.net/NEBWorks/image/";
        private static Retrofit retrofit;

        public static Retrofit getApiClient() {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
            return retrofit;
        }

    }

    public interface RetrofitInterface {
        //api를 관리해주는 인터페이스
        @Multipart
        @POST("upload_task_img.php")
        Call<String> request(@Part MultipartBody.Part file);
    }

    public File saveBitmapToFile(File file) {
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 8;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private String ImageNameMaker() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MakeFileNameInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MakeFileNameInterface api = retrofit.create(MakeFileNameInterface.class);
        Call<String> call = api.getData("");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        if (!Response.toString().equals("[]")) {
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                ImgfileMaker = jsonObject.getString("id");
                                dlog.i("ImgfileMaker : " + ImgfileMaker);
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
                dlog.e("에러 = " + t.getMessage());
            }
        });
        return ImgfileMaker;
    }

    public void Toast_Nomal(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup)findViewById(R.id.toast_layout));
        TextView toast_textview  = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
