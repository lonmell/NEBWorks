package com.krafte.nebworks.ui.approval;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.ApprovalUpdateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.TaskSelectMInterface;
import com.krafte.nebworks.databinding.ActivityTaskapprovalDetailBinding;
import com.krafte.nebworks.pop.PhotoPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TaskApprovalDetail extends AppCompatActivity {
    private ActivityTaskapprovalDetailBinding binding;
    private final static String TAG = "EmployeeMyWorkDetail";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String id = "";
    String state = "";
    String request_task_no = "";
    String requester_id = "";
    String requester_name = "";
    String requester_img_path = "";
    String requester_department = "";
    String requester_position = "";
    String title = "";
    String contents = "";
    String complete_kind = "";
    String end_time = "0";
    String complete_time = "0";
    String task_img_path = "";
    String complete_yn = "";
    String incomplete_reason = "";
    String reject_reason = "";
    String task_date = "";
    String request_date = "";
    String approval_date = "";
    String place_owner_id = "";
    String place_name = "";

    //Other
    Drawable icon_off;
    Drawable icon_on;
    RetrofitConnect rc = new RetrofitConnect();
    Dlog dlog = new Dlog();
    GetResultData resultData = new GetResultData();
    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String fileName = "";
    String task_image_url = "0";
    String message = "";
    PageMoveClass pm = new PageMoveClass();
    /*
     * task_loop_kind
     * 1 = 매일
     * 2 = 주간
     * 3 = 월간
     * 4 = 하루
     * */

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_taskapproval_detail);
        binding = ActivityTaskapprovalDetailBinding.inflate(getLayoutInflater()); // 1
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

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","0");
        place_id = shardpref.getString("place_id","0");
        place_owner_id = shardpref.getString("place_owner_id", "");
        place_name = shardpref.getString("place_name", "");
        id = shardpref.getString("id", "");
        state = shardpref.getString("state", "");
        request_task_no = shardpref.getString("request_task_no", "");
        requester_id = shardpref.getString("requester_id",  "");
        requester_name = shardpref.getString("requester_name",  "");
        requester_img_path = shardpref.getString("requester_img_path",  "");
        title = shardpref.getString("title",  "");
        contents = shardpref.getString("contents",  "");
        complete_kind = shardpref.getString("complete_kind", "");
        end_time = shardpref.getString("end_time",  "");
        complete_time = shardpref.getString("complete_time", "");
        task_img_path = shardpref.getString("task_img_path", "0");
        complete_yn = shardpref.getString("complete_yn",  "");
        incomplete_reason = shardpref.getString("incomplete_reason",  "");
        reject_reason = shardpref.getString("reject_reason",  "");
        task_date = shardpref.getString("task_date",  "");
        request_date = shardpref.getString("request_date", "");
        approval_date = shardpref.getString("approval_date",  "");

        fileName = USER_INFO_ID;
        dateFormat = new SimpleDateFormat("HH:mm:ss", getResources().getConfiguration().locale);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getTime = dateFormat.format(date);
        int setToday = Integer.parseInt(getTime.substring(0, 2));

        try {
            Log.i(TAG, "-------------------------TaskApprovalDetail-------------------------");
            Log.i(TAG, "task_no : " + id);
            Log.i(TAG, "GET_TIME : " + getTime.substring(0, 2));
            Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
            Log.i(TAG, "업무내용 : " + contents);
            Log.i(TAG, "업무종류 : " + (complete_kind.equals("0")?"체크":"매장사진"));
            Log.i(TAG, "state : " + state);
            Log.i(TAG, "요청 업무 번호 : " + request_task_no);
            Log.i(TAG, "task_input_id : " + requester_name);
            Log.i(TAG, "task_success_method : " + complete_kind);
            Log.i(TAG, "task_check : " + complete_yn);
            Log.i(TAG, "task_notsuccess_txt : " + incomplete_reason);
            Log.i(TAG, "task_title : " + title);
            Log.i(TAG, "task_img_path : " + task_img_path);
            Log.i(TAG, "reject_reason : " + reject_reason);
            String success_time = request_date;
            Log.i(TAG, "request_date : " + request_date);
            setTodoData(request_date,id);
            Log.i(TAG, "----------------------------------------------------------------------");

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
            try {
                Glide.with(mContext).load(requester_img_path)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.workimg);

                binding.selectEmployeeDate.setText(request_date);
                binding.selectEmployeeTxt.setText(requester_name);

                binding.inputWorktitle.setText(title);
                binding.workContentSet.setText(contents);

                binding.endtimeTxt.setText(end_time + " " + (Integer.parseInt(end_time.substring(0,2)) > 12 ? "AM" : "PM"));
                binding.endtimeTxt2.setText(complete_time + " " + (Integer.parseInt(complete_time.substring(0, 2)) > 12 ? "AM" : "PM"));
            } catch (Exception e) {
                Log.i(TAG, "Exception : " + e);
            }
            if (complete_kind.equals("1")) {
                binding.successCheckArea.setVisibility(View.GONE);
                binding.uploadSuccessImg.setVisibility(View.VISIBLE);
                binding.loCanvas.setVisibility(View.VISIBLE);
                binding.workadd01Txt.setTextColor(Color.parseColor("#1483FE"));
                binding.workadd02Txt.setTextColor(Color.parseColor("#696969"));
                binding.workadd02Txt.setVisibility(View.GONE);
            } else if (complete_kind.equals("0")) {
                binding.successCheckArea.setVisibility(View.VISIBLE);
                binding.uploadSuccessImg.setVisibility(View.GONE);
                binding.loCanvas.setVisibility(View.GONE);
                binding.incompleteInput.setText(incomplete_reason.equals("null") ? "" : incomplete_reason);
                binding.incompleteInput.setEnabled(false);
                binding.incompleteInput.setClickable(false);
                binding.workadd01Txt.setTextColor(Color.parseColor("#696969"));
                binding.workadd02Txt.setTextColor(Color.parseColor("#1483FE"));
                binding.workadd01Txt.setVisibility(View.GONE);
            }

            Glide.with(mContext).load(task_img_path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.loCanvas);

            binding.inrejectInput.setText(reject_reason.equals("null")?"":reject_reason);
            dlog.i("reject_reason : " + reject_reason);
            dlog.i("task_img_path : " + task_img_path);
            if(!task_img_path.equals("0")){
                binding.loCanvas.setOnClickListener(v -> {
                    Intent intent = new Intent(this, PhotoPopActivity.class);
                    intent.putExtra("data", task_img_path);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                });
            }

            //-- 결재대기/승인/반려 중일때.
            Log.i(TAG, "결재대기/승인/반려 중일때 state : " + state);
            if (state.equals("0") || state.equals("") || state.isEmpty()) {
                binding.apploveState.setText("처리 중");
                if (complete_kind.equals("1")) {
                    binding.successCheckArea.setVisibility(View.GONE);
                    binding.uploadSuccessImg.setVisibility(View.VISIBLE);
                    binding.loCanvas.setVisibility(View.VISIBLE);
                } else {
                    binding.success01Txt.setClickable(false);
                    binding.success01Txt.setEnabled(false);
                    binding.success02Txt.setEnabled(false);
                    binding.success02Txt.setClickable(false);
                    binding.incompleteInput.setEnabled(false);
                    if (complete_yn.equals("y")) {
                        binding.incompleteInput.setBackgroundColor(Color.parseColor("#dcdcdc"));
                        binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success01Txt.setTextColor(Color.parseColor("#1483FE"));
                        binding.success02Txt.setVisibility(View.GONE);
                        binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success02Txt.setTextColor(Color.parseColor("#696969"));
                    } else {
                        binding.incompleteInput.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        binding.success01Txt.setVisibility(View.GONE);
                        binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success01Txt.setTextColor(Color.parseColor("#696969"));
                        binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success02Txt.setTextColor(Color.parseColor("#1483FE"));
                    }
//                binding.workSaveAccept.setVisibility(View.GONE);
//                binding.uploadSuccessImg.setVisibility(View.GONE);
//                binding.loCanvas.setVisibility(View.GONE);
                }
                binding.bottomBtnBox.setVisibility(View.VISIBLE);
                binding.bottomBtntv01.setText("반려");
                binding.bottomBtntv02.setText("승인");
            } else if (state.equals("1")) {
                if (complete_kind.equals("1")) {
//                binding.workSaveAccept.setVisibility(View.GONE);
                    binding.apploveState.setText("승인");
                    binding.successCheckArea.setVisibility(View.GONE);
                    binding.uploadSuccessImg.setVisibility(View.VISIBLE);
                    binding.loCanvas.setVisibility(View.VISIBLE);
                } else {
                    binding.success01Txt.setClickable(false);
                    binding.success01Txt.setEnabled(false);
                    binding.success02Txt.setEnabled(false);
                    binding.success02Txt.setClickable(false);
                    binding.incompleteInput.setEnabled(false);
                    if (complete_yn.equals("y")) {
                        binding.incompleteInput.setBackgroundColor(Color.parseColor("#dcdcdc"));
                        binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success01Txt.setTextColor(Color.parseColor("#1483FE"));
                        binding.success02Txt.setVisibility(View.GONE);
                        binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success02Txt.setTextColor(Color.parseColor("#696969"));
                    } else {
                        binding.incompleteInput.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        binding.success01Txt.setVisibility(View.GONE);
                        binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success01Txt.setTextColor(Color.parseColor("#696969"));
                        binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success02Txt.setTextColor(Color.parseColor("#1483FE"));
                    }
                    binding.apploveState.setText("승인");
                }
                binding.bottomBtnBox.setVisibility(View.VISIBLE);
                binding.bottomBtntv01.setText("승인취소");
                binding.bottomBtntv02.setText("반려");
            } else if (state.equals("2")) {
                if (complete_kind.equals("1")) {
                    binding.successCheckArea.setVisibility(View.GONE);
                    binding.apploveState.setText("반려");
                    binding.uploadSuccessImg.setVisibility(View.VISIBLE);
                    binding.loCanvas.setVisibility(View.VISIBLE);
                } else {
                    binding.success01Txt.setClickable(false);
                    binding.success01Txt.setEnabled(false);
                    binding.success02Txt.setEnabled(false);
                    binding.success02Txt.setClickable(false);
                    binding.incompleteInput.setEnabled(false);
                    if (complete_yn.equals("1")) {
                        binding.incompleteInput.setBackgroundColor(Color.parseColor("#dcdcdc"));
                        binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success01Txt.setTextColor(Color.parseColor("#1483FE"));
                        binding.success02Txt.setVisibility(View.GONE);
                        binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success02Txt.setTextColor(Color.parseColor("#696969"));
                    } else {
                        binding.incompleteInput.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        binding.success01Txt.setVisibility(View.GONE);
                        binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success01Txt.setTextColor(Color.parseColor("#696969"));
                        binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        binding.success02Txt.setTextColor(Color.parseColor("#1483FE"));
                    }
                    binding.apploveState.setText("반려");
                }
                binding.bottomBtnBox.setVisibility(View.VISIBLE);
                binding.bottomBtntv01.setText("반려취소");
                binding.bottomBtntv02.setText("승인");
            }

            if(USER_INFO_AUTH.equals("1")){
                binding.bottomBtnBox.setVisibility(View.GONE);
                binding.bottomBtntv01.setClickable(false);
                binding.bottomBtntv02.setClickable(false);
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception E" + e);
        }


    }

    private void setBtnEvent() {
        if (!complete_yn.isEmpty()) {
            binding.incompleteInput.setEnabled(false);
            if (complete_yn.equals("1")) {
                binding.incompleteInput.setBackgroundColor(Color.parseColor("#dcdcdc"));
                binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                binding.success01Txt.setTextColor(Color.parseColor("#1483FE"));
                binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
                binding.success02Txt.setTextColor(Color.parseColor("#696969"));
            } else {
                binding.incompleteInput.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.success01Txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
                binding.success01Txt.setTextColor(Color.parseColor("#696969"));
                binding.success02Txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                binding.success02Txt.setTextColor(Color.parseColor("#1483FE"));
            }
        }

        if (state.equals("1")) {
            binding.workSaveAccept.setEnabled(false);
        } else if (state.equals("3") || state.isEmpty() || state.equals("0")) {
            binding.workSaveAccept.setEnabled(true);
        }

        binding.workSaveAccept.setOnClickListener(v -> {
            if (state.equals("0")) {
                setUpdateWorktodo("2");
            } else {
                setUpdateWorktodo("0");
            }
        });

        binding.workDelete.setOnClickListener(v -> {
            if (state.equals("2") || state.equals("0")) {
                setUpdateWorktodo("1");
            } else {
                setUpdateWorktodo("2");
            }
        });

        binding.menu.setOnClickListener(v -> {
            pm.Approval(mContext);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("id");
        shardpref.remove("state");
        shardpref.remove("request_task_no");
        shardpref.remove("requester_id");
        shardpref.remove("requester_name");
        shardpref.remove("requester_img_path");
        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("complete_kind");
        shardpref.remove("end_time");
        shardpref.remove("complete_time");
        shardpref.remove("task_img_path");
        shardpref.remove("complete_yn");
        shardpref.remove("incomplete_reason");
        shardpref.remove("reject_reason");
        shardpref.remove("task_date");
        shardpref.remove("request_date");
        shardpref.remove("approval_date");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void setUpdateWorktodo(String kind) {
        dlog.i("-----setUpdateWorktodo-----");
        dlog.i("id : " + id);
        dlog.i("requester_id : " + requester_id);
        dlog.i("kind : " + kind);
        dlog.i("incomplete_reason : " + incomplete_reason);
        dlog.i("inrejectInput : " + binding.inrejectInput.getText().toString());
        dlog.i("-----setUpdateWorktodo-----");

        incomplete_reason = binding.incompleteInput.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApprovalUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApprovalUpdateInterface api = retrofit.create(ApprovalUpdateInterface.class);
        Call<String> call = api.getData(id,USER_INFO_ID,kind,binding.inrejectInput.getText().toString());
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "setUpdateWorktodo function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "resultData : " + resultData.getRESULT());
                    if (response.body().replace("\"", "").equals("success")) {
                        for(int a = 0; a < user_id.size(); a++){
                            if(place_owner_id.equals(user_id.get(a))){
                                getManagerToken(user_id.get(a), "0", place_id, place_name,state);
                            }else{
                                getManagerToken(user_id.get(a), "1", place_id, place_name,state);
                            }
                        }

                        Log.i(TAG, "complete_kind : " + complete_kind);
                        pm.Approval(mContext);
                    } else {
                        Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    List<String> inmember = new ArrayList<>();
    List<String> user_id = new ArrayList<>();
    List<String> task_member_id = new ArrayList<>();
    public void setTodoData(String selectdate, String task_no) {
        dlog.i("setTodoMList place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectMInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectMInterface api = retrofit.create(TaskSelectMInterface.class);
        Call<String> call = api.getData(place_id,selectdate);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        Log.i(TAG, "GET SIZE : " + Response.length());
                        if (Response.length() == 0) {
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            Log.i(TAG, "GET SIZE : " + Response.length());
                        } else {
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(jsonObject.getString("id").equals(request_task_no)){
                                    task_member_id = Collections.singletonList(jsonObject.getString("users"));
                                }
                            }
                            JSONArray Response2 = new JSONArray(task_member_id.toString().replace("[[", "[").replace("]]", "]"));
                            if (Response2.length() > 3) {
                                for (int i = 0; i < 3; i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    inmember.add(jsonObject.getString("user_id"));
                                }
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    inmember.add(jsonObject.getString("user_id"));
                                }
                            }
                            user_id.removeAll(user_id);
                            for (int i = 0; i < Response2.length(); i++) {
                                JSONObject jsonObject = Response2.getJSONObject(i);
                                if (!jsonObject.getString("user_name").equals("null")) {
                                    user_id.add(jsonObject.getString("user_id"));
                                }
                            }
                            dlog.i("이 작업에 배정된 직원 : " + user_id);
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

    /* -- 할일 추가 FCM 전송 영역 */
    public void getManagerToken(String user_id, String type, String place_id, String place_name, String state) {
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
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        String department = shardpref.getString("USER_INFO_SOSOK", "");
                        String position = shardpref.getString("USER_INFO_JIKGUP", "");
                        String name = shardpref.getString("USER_INFO_NAME", "");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            message = "[" + place_name + "]" + "결재가 변동된 업무보고가 있습니다.";
                            PushFcmSend(id, "", message, token, "1", place_id);
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

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "TaskApprovalFragment";
            dlog.i("------FcmTestFunction------");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("------FcmTestFunction------");
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
    /* -- 할일 추가 FCM 전송 영역 */
}
