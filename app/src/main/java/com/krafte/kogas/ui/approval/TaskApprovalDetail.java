package com.krafte.kogas.ui.approval;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.kogas.R;
import com.krafte.kogas.data.GetResultData;
import com.krafte.kogas.dataInterface.ApprovalUpdateInterface;
import com.krafte.kogas.dataInterface.FCMSelectInterface;
import com.krafte.kogas.pop.PhotoPopActivity;
import com.krafte.kogas.util.DBConnection;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TaskApprovalDetail extends AppCompatActivity {
    private final static String TAG = "EmployeeMyWorkDetail";
    Context mContext;

    //XML ID
    LinearLayout success_check_area, bottom_btn_box;
    RelativeLayout login_alert_text;
    TextView select_employee_txt;
    CardView upload_success_img;
    TextView input_worktitle, work_content_set;
    TextView endtime_txt;
    TextView success_01_txt, success_02_txt;
    EditText incomplete_input, inreject_input;
    ImageView canvasContainer, workimg;
    CardView work_save_accept, work_delete, work_retry;
    TextView applove_state, select_employee_date;
    TextView workadd_01_txt, workadd_02_txt, endtime_txt2;
    TextView bottom_btntv01, bottom_btntv02;
    ImageView menu;


    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String id = "";
    String state = "";
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
    String task_confirmx = "0";
    private Bitmap saveBitmap;

    String message = "";
    String topic = "";

    PageMoveClass pm = new PageMoveClass();
    Handler mHandler;
    String sendTopic = "";
    String sendToken = "";
    boolean rcvchannelId2 = false;
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
        setContentView(R.layout.activity_taskapproval_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
        icon_on = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_on);

        setContentLayout();
        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","0");
        place_id = shardpref.getString("place_id","0");
        place_owner_id = shardpref.getString("place_owner_id", "");
        place_name = shardpref.getString("place_name", "");
        id = shardpref.getString("id", "");
        state = shardpref.getString("state", "");
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
            Log.i(TAG, "GET_TIME : " + getTime.substring(0, 2));
            Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
            Log.i(TAG, "업무내용 : " + contents);
            Log.i(TAG, "업무종류 : " + (complete_kind.equals("0")?"체크":"현장사진"));
            Log.i(TAG, "state : " + state);
            Log.i(TAG, "task_input_id : " + requester_name);
            Log.i(TAG, "task_success_method : " + complete_kind);
            Log.i(TAG, "task_check : " + complete_yn);
            Log.i(TAG, "task_notsuccess_txt : " + incomplete_reason);
            Log.i(TAG, "task_title : " + title);
            Log.i(TAG, "task_img_path : " + task_img_path);
            Log.i(TAG, "reject_reason : " + reject_reason);
            String success_time = request_date;
            Log.i(TAG, "request_date : " + request_date);
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
            Glide.with(mContext).load(requester_img_path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(workimg);

            select_employee_date.setText(request_date);
            select_employee_txt.setText(requester_name);

            input_worktitle.setText(title);
            work_content_set.setText(contents);

            try {
                endtime_txt.setText(end_time + " " + (Integer.parseInt(end_time.substring(0,2)) > 12 ? "AM" : "PM"));
                endtime_txt2.setText(complete_time + " " + (Integer.parseInt(complete_time.substring(0, 2)) > 12 ? "AM" : "PM"));
            } catch (Exception e) {
                Log.i(TAG, "Exception : " + e);
            }
            if (complete_kind.equals("1")) {
                success_check_area.setVisibility(View.GONE);
                upload_success_img.setVisibility(View.VISIBLE);
                canvasContainer.setVisibility(View.VISIBLE);
                workadd_01_txt.setTextColor(Color.parseColor("#1483FE"));
                workadd_02_txt.setTextColor(Color.parseColor("#696969"));
                workadd_02_txt.setVisibility(View.GONE);
            } else if (complete_kind.equals("0")) {
                success_check_area.setVisibility(View.VISIBLE);
                upload_success_img.setVisibility(View.GONE);
                canvasContainer.setVisibility(View.GONE);
                incomplete_input.setText(incomplete_reason.equals("null") ? "" : incomplete_reason);
                incomplete_input.setEnabled(false);
                incomplete_input.setClickable(false);
                workadd_01_txt.setTextColor(Color.parseColor("#696969"));
                workadd_02_txt.setTextColor(Color.parseColor("#1483FE"));
                workadd_01_txt.setVisibility(View.GONE);
            }

            Glide.with(mContext).load(task_img_path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(canvasContainer);
            inreject_input.setText(reject_reason);
            dlog.i("task_img_path : " + task_img_path);
            if(!task_img_path.equals("0")){
                canvasContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(this, PhotoPopActivity.class);
                    intent.putExtra("data", task_image_url);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                });
            }

            //-- 결재대기/승인/반려 중일때.
            Log.i(TAG, "결재대기/승인/반려 중일때 state : " + state);
            if (state.equals("0") || state.equals("") || state.isEmpty()) {
                applove_state.setText("처리 중");
                if (complete_kind.equals("1")) {
                    success_check_area.setVisibility(View.GONE);
                    upload_success_img.setVisibility(View.VISIBLE);
                    canvasContainer.setVisibility(View.VISIBLE);
                } else {
                    success_01_txt.setClickable(false);
                    success_01_txt.setEnabled(false);
                    success_02_txt.setEnabled(false);
                    success_02_txt.setClickable(false);
                    incomplete_input.setEnabled(false);
                    if (complete_yn.equals("y")) {
                        incomplete_input.setBackgroundColor(Color.parseColor("#dcdcdc"));
                        success_01_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_01_txt.setTextColor(Color.parseColor("#1483FE"));
                        success_02_txt.setVisibility(View.GONE);
                        success_02_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_02_txt.setTextColor(Color.parseColor("#696969"));
                    } else {
                        incomplete_input.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        success_01_txt.setVisibility(View.GONE);
                        success_01_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_01_txt.setTextColor(Color.parseColor("#696969"));
                        success_02_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_02_txt.setTextColor(Color.parseColor("#1483FE"));
                    }
//                work_save_accept.setVisibility(View.GONE);
//                upload_success_img.setVisibility(View.GONE);
//                canvasContainer.setVisibility(View.GONE);
                }
                bottom_btn_box.setVisibility(View.VISIBLE);
                bottom_btntv01.setText("반려");
                bottom_btntv02.setText("승인");
            } else if (state.equals("1")) {
                if (complete_kind.equals("1")) {
//                work_save_accept.setVisibility(View.GONE);
                    applove_state.setText("승인");
                    success_check_area.setVisibility(View.GONE);
                    upload_success_img.setVisibility(View.VISIBLE);
                    canvasContainer.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(task_image_url)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(canvasContainer);
                } else {
                    success_01_txt.setClickable(false);
                    success_01_txt.setEnabled(false);
                    success_02_txt.setEnabled(false);
                    success_02_txt.setClickable(false);
                    incomplete_input.setEnabled(false);
                    if (complete_yn.equals("y")) {
                        incomplete_input.setBackgroundColor(Color.parseColor("#dcdcdc"));
                        success_01_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_01_txt.setTextColor(Color.parseColor("#1483FE"));
                        success_02_txt.setVisibility(View.GONE);
                        success_02_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_02_txt.setTextColor(Color.parseColor("#696969"));
                    } else {
                        incomplete_input.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        success_01_txt.setVisibility(View.GONE);
                        success_01_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_01_txt.setTextColor(Color.parseColor("#696969"));
                        success_02_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_02_txt.setTextColor(Color.parseColor("#1483FE"));
                    }
                    applove_state.setText("승인");
                }
                bottom_btn_box.setVisibility(View.VISIBLE);
                bottom_btntv01.setText("승인취소");
                bottom_btntv02.setText("반려");
            } else if (state.equals("2")) {
                if (complete_kind.equals("1")) {
                    success_check_area.setVisibility(View.GONE);
                    applove_state.setText("반려");
                    upload_success_img.setVisibility(View.VISIBLE);
                    canvasContainer.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(task_image_url)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(canvasContainer);
                } else {
                    success_01_txt.setClickable(false);
                    success_01_txt.setEnabled(false);
                    success_02_txt.setEnabled(false);
                    success_02_txt.setClickable(false);
                    incomplete_input.setEnabled(false);
                    if (complete_yn.equals("1")) {
                        incomplete_input.setBackgroundColor(Color.parseColor("#dcdcdc"));
                        success_01_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_01_txt.setTextColor(Color.parseColor("#1483FE"));
                        success_02_txt.setVisibility(View.GONE);
                        success_02_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_02_txt.setTextColor(Color.parseColor("#696969"));
                    } else {
                        incomplete_input.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        success_01_txt.setVisibility(View.GONE);
                        success_01_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_01_txt.setTextColor(Color.parseColor("#696969"));
                        success_02_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        success_02_txt.setTextColor(Color.parseColor("#1483FE"));
                    }
                    applove_state.setText("반려");
                }
                bottom_btn_box.setVisibility(View.VISIBLE);
                bottom_btntv01.setText("반려취소");
                bottom_btntv02.setText("승인");
            }

            if(USER_INFO_AUTH.equals("1")){
                bottom_btn_box.setVisibility(View.GONE);
                bottom_btntv01.setClickable(false);
                bottom_btntv02.setClickable(false);
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception E" + e);
        }


    }

    private void setBtnEvent() {
        if (!complete_yn.isEmpty()) {
            incomplete_input.setEnabled(false);
            if (complete_yn.equals("1")) {
                incomplete_input.setBackgroundColor(Color.parseColor("#dcdcdc"));
                success_01_txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                success_01_txt.setTextColor(Color.parseColor("#1483FE"));
                success_02_txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
                success_02_txt.setTextColor(Color.parseColor("#696969"));
            } else {
                incomplete_input.setBackgroundColor(Color.parseColor("#f2f2f2"));
                success_01_txt.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
                success_01_txt.setTextColor(Color.parseColor("#696969"));
                success_02_txt.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
                success_02_txt.setTextColor(Color.parseColor("#1483FE"));
            }
        }

        if (state.equals("1")) {
            work_save_accept.setEnabled(false);
        } else if (state.equals("3") || state.isEmpty() || state.equals("0")) {
            work_save_accept.setEnabled(true);
        }

        work_save_accept.setOnClickListener(v -> {
            if (state.equals("0")) {
                setUpdateWorktodo("2");
            } else {
                setUpdateWorktodo("0");
            }
        });

        work_delete.setOnClickListener(v -> {
            if (state.equals("2") || state.equals("0")) {
                setUpdateWorktodo("1");
            } else {
                setUpdateWorktodo("2");
            }
        });

        menu.setOnClickListener(v -> {
            pm.ApprovalBack(mContext);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("id");
        shardpref.remove("state");
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
        dlog.i("inreject_input : " + inreject_input);
        dlog.i("-----setUpdateWorktodo-----");

        incomplete_reason = incomplete_input.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApprovalUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApprovalUpdateInterface api = retrofit.create(ApprovalUpdateInterface.class);
        Call<String> call = api.getData(id,USER_INFO_ID,kind,inreject_input.getText().toString());
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
                        getManagerToken(requester_id, "1", place_id, place_name,state);
                        Log.i(TAG, "complete_kind : " + complete_kind);
                        pm.ApprovalBack(mContext);
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

    private void setContentLayout() {
        login_alert_text = findViewById(R.id.login_alert_text);
        select_employee_txt = findViewById(R.id.select_employee_txt);
        input_worktitle = findViewById(R.id.input_worktitle);
        endtime_txt = findViewById(R.id.endtime_txt);
        upload_success_img = findViewById(R.id.upload_success_img);
        success_check_area = findViewById(R.id.success_check_area);

        success_01_txt = findViewById(R.id.success_01_txt);
        success_02_txt = findViewById(R.id.success_02_txt);

        incomplete_input = findViewById(R.id.incomplete_input);
        inreject_input = findViewById(R.id.inreject_input);
        canvasContainer = findViewById(R.id.lo_canvas);

        work_save_accept = findViewById(R.id.work_save_accept);
        work_delete = findViewById(R.id.work_delete);
        applove_state = findViewById(R.id.applove_state);
        workadd_01_txt = findViewById(R.id.workadd_01_txt);
        workadd_02_txt = findViewById(R.id.workadd_02_txt);
        work_content_set = findViewById(R.id.work_content_set);

        select_employee_date = findViewById(R.id.select_employee_date);
        workimg = findViewById(R.id.workimg);
        endtime_txt2 = findViewById(R.id.endtime_txt2);
        bottom_btn_box = findViewById(R.id.bottom_btn_box);
        menu = findViewById(R.id.menu);

        bottom_btntv01 = findViewById(R.id.bottom_btntv01);
        bottom_btntv02 = findViewById(R.id.bottom_btntv02);

    }
}
