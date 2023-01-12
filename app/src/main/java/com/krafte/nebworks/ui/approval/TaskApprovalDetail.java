package com.krafte.nebworks.ui.approval;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MemberListPopAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
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
import java.util.Arrays;
import java.util.Calendar;
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
    String start_time = "";
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

    String users = "";
    String usersn = "";
    String usersimg = "";
    String usersjikgup = "";

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
    Calendar cal;
    String format = "yyyy-MM-dd";
    android.icu.text.SimpleDateFormat sdf = new android.icu.text.SimpleDateFormat(format);
    String toDay = "";

    List<String> item_user_id;
    List<String> item_user_name;
    List<String> item_user_img;
    List<String> item_user_jikgup;
    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mem_mList;
    MemberListPopAdapter mem_mAdapter;

    Drawable check_on;
    Drawable check_off;
    Drawable x_on;
    Drawable x_off;


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
        check_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_blue_check);
        check_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_circle_gray_check);
        x_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_red_x);
        x_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_white_x);

        setBtnEvent();
        shardpref = new PreferenceHelper(mContext);
        //Singleton Area
        USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","");
        USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
        place_id        = PlaceCheckData.getInstance().getPlace_id();
        place_owner_id  = PlaceCheckData.getInstance().getPlace_owner_id();
        place_name      = PlaceCheckData.getInstance().getPlace_name();


        //shardpref Area
        id              = shardpref.getString("id", "");
        state           = shardpref.getString("state", "");
        request_task_no = shardpref.getString("request_task_no", "");
        requester_id    = shardpref.getString("requester_id",  "");
        requester_name  = shardpref.getString("requester_name",  "");
        requester_img_path = shardpref.getString("requester_img_path",  "");
        title           = shardpref.getString("title",  "");
        contents        = shardpref.getString("contents",  "");
        complete_kind   = shardpref.getString("complete_kind", "");
        start_time      = shardpref.getString("start_time",  "");
        end_time        = shardpref.getString("end_time",  "");
        complete_time   = shardpref.getString("complete_time", "");
        task_img_path   = shardpref.getString("task_img_path", "0");
        complete_yn     = shardpref.getString("complete_yn",  "");
        incomplete_reason = shardpref.getString("incomplete_reason",  "");
        reject_reason   = shardpref.getString("reject_reason",  "");
        task_date       = shardpref.getString("task_date",  "");
        request_date    = shardpref.getString("request_date", "");
        approval_date   = shardpref.getString("approval_date",  "");
        users           = shardpref.getString("users", "0");
        usersn          = shardpref.getString("usersn", "0");
        usersimg        = shardpref.getString("usersimg", "0");
        usersjikgup     = shardpref.getString("usersjikgup", "0");

        fileName = USER_INFO_ID;
        dateFormat = new SimpleDateFormat("HH:mm:ss", getResources().getConfiguration().locale);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getTime = dateFormat.format(date);
        int setToday = Integer.parseInt(getTime.substring(0, 2));

        try {
            dlog.i( "-------------------------TaskApprovalDetail-------------------------");
            dlog.i("task_no : " + id);
            dlog.i("GET_TIME : " + getTime.substring(0, 2));
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("업무내용 : " + contents);
            dlog.i("업무종류 : " + (complete_kind.equals("0")?"체크":"매장사진"));
            dlog.i("state : " + state);
            dlog.i("요청 업무 번호 : " + request_task_no);
            dlog.i("task_input_id : " + requester_name);
            dlog.i("task_success_method : " + complete_kind);
            dlog.i("task_check : " + complete_yn);
            dlog.i("task_notsuccess_txt : " + incomplete_reason);
            dlog.i("task_title : " + title);
            dlog.i("task_img_path : " + task_img_path);
            dlog.i("reject_reason : " + reject_reason);
            String success_time = request_date;
            dlog.i("request_date : " + request_date);
            dlog.i("users : " + users);
            dlog.i("usersn : " + usersn);
            dlog.i("usersimg : " + usersimg);
            setTodoData(request_date,id);

            dlog.i( "----------------------------------------------------------------------");

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
                binding.inputWorktitle.setText(title);
                binding.inputWorkcontents.setText(contents);
                binding.taskKind.setText(complete_kind.equals("0")?"체크":"인증사진");

                cal = Calendar.getInstance();
                toDay = sdf.format(cal.getTime()).replace("-",".");
                dlog.i("오늘 :" + toDay);
                shardpref.putString("FtoDay",toDay);

                binding.startTime.setText(start_time + " 시작");
                binding.endTime.setText(end_time + " 마감");

                item_user_id = new ArrayList<>();
                item_user_name = new ArrayList<>();
                item_user_img = new ArrayList<>();
                item_user_jikgup = new ArrayList<>();

                item_user_id.addAll(Arrays.asList(users.replace("[", "").replace("]", "").split(",")));
                item_user_name.addAll(Arrays.asList(usersn.replace("[", "").replace("]", "").split(",")));
                item_user_img.addAll(Arrays.asList(usersimg.replace("[", "").replace("]", "").split(",")));
                item_user_jikgup.addAll(Arrays.asList(usersjikgup.replace("[", "").replace("]", "").split(",")));

                dlog.i("item_user_id : " + item_user_id);
                dlog.i("item_user_name : " + item_user_name);
                dlog.i("item_user_img : " + item_user_img);
                dlog.i("item_user_jikgup : " + item_user_jikgup);

                mem_mList = new ArrayList<>();
                mem_mAdapter = new MemberListPopAdapter(mContext, mem_mList, 1);
                binding.selectMemberList.setAdapter(mem_mAdapter);
                binding.selectMemberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                if (users.isEmpty() || users.equals("0")) {
                    dlog.i("getTaskContents getuser_id : " + item_user_id);
                    dlog.i("getTaskContents getuser_name : " + item_user_name);
                    dlog.i("getTaskContents getuser_img : " + item_user_img);
                    item_user_id.clear();
                    item_user_name.clear();
                    item_user_img.clear();
                    item_user_jikgup.clear();
                } else {
                    dlog.i("getTaskContents item_user_id.size() : " + item_user_id.size());
                    for (int i = 0; i < item_user_id.size(); i++) {
                        dlog.i("getTaskContents item_user_id : " + item_user_id.get(i));
                        dlog.i("getTaskContents item_user_name : " + item_user_name.get(i));
                        dlog.i("getTaskContents item_user_img : " + item_user_img.get(i));
                        dlog.i("getTaskContents item_user_jikgup : " + item_user_jikgup.get(i));
                        mem_mAdapter.addItem(new WorkPlaceMemberListData.WorkPlaceMemberListData_list(
                                item_user_id.get(i).trim(),
                                "",
                                "",
                                item_user_name.get(i).trim(),
                                "",
                                "",
                                item_user_img.get(i).trim(),
                                "",
                                "",
                                "",
                                "",
                                item_user_jikgup.get(i).trim(),
                                "",
                                "",
                                ""
                        ));
                    }
                    mem_mAdapter.notifyDataSetChanged();
                }

                binding.reportTime.setText(complete_time);
                if (!complete_kind.isEmpty()) {
                    if (complete_kind.equals("0")) {
                        binding.taskKind00.setVisibility(View.VISIBLE);
                        binding.taskKind01.setVisibility(View.GONE);

                        binding.taskKind00.setTextColor(Color.parseColor(complete_yn.equals("y")?"#6395EC":"#FF0000"));
                        binding.taskKind00.setText(complete_yn.equals("y")?"완료":"미완료");
                    } else if (complete_kind.equals("1")) {
                        binding.taskKind00.setVisibility(View.GONE);
                        binding.taskKind01.setVisibility(View.VISIBLE);

                        Glide.with(mContext).load(task_img_path)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(binding.taskKind01);
                        binding.taskKind01.setOnClickListener(v -> {
                            Intent intent = new Intent(mContext, PhotoPopActivity.class);
                            intent.putExtra("data", task_img_path);
                            mContext.startActivity(intent);
                            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        });
                    }
                }
                if(!incomplete_reason.equals("null")){
                    binding.incompleteArea.setVisibility(View.VISIBLE);
                    binding.incompleteTitle.setText(incomplete_reason);
                }
                //--approval_state -- // 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전
                if(state.equals("0") || state.equals("1") || state.equals("3")){
                    binding.approvalState.setTextColor(Color.parseColor("#6395EC"));
                    if(state.equals("0")){
                        binding.approvalState.setText("결재대기중");
                    }else if(state.equals("1")){
                        binding.approvalState.setText("승인");
                    }
                    binding.rejectArea.setVisibility(View.GONE);
                }else{
                    binding.rejectArea.setVisibility(View.VISIBLE);
                    binding.approvalState.setTextColor(Color.parseColor("#FF0000"));
                    binding.approvalState.setText("반려");
                    if(reject_reason.length() > 0){
                        binding.rejectReasonTv.setText(reject_reason);
                    }
                }
            } catch (Exception e) {
                dlog.i( "Exception : " + e);
            }

            if(USER_INFO_AUTH.equals("0")){
                binding.decisionArea.setVisibility(View.VISIBLE);
                binding.bottomBtnBox.setVisibility(View.GONE);
            }else{
                binding.decisionArea.setVisibility(View.GONE);
                binding.bottomBtnBox.setVisibility(View.VISIBLE);
            }

            binding.selectApproval01.setCardBackgroundColor(Color.parseColor("#E0EAFB"));
            binding.selectApproval01tv.setTextColor(Color.parseColor("#6395EC"));
            binding.selectApproval01tv.setCompoundDrawablesWithIntrinsicBounds(check_on,null,null,null);
            binding.selectApproval02.setCardBackgroundColor(Color.parseColor("#FCF0EC"));
            binding.selectApproval02tv.setTextColor(Color.parseColor("#DD6540"));
            binding.selectApproval02tv.setCompoundDrawablesWithIntrinsicBounds(x_on,null,null,null);
            binding.rejectTv.setVisibility(View.GONE);
            binding.rejectSave.setVisibility(View.GONE);

            binding.selectApproval01.setOnClickListener(v -> {
                //승인버튼
                binding.selectApproval01.setCardBackgroundColor(Color.parseColor("#E0EAFB"));
                binding.selectApproval01tv.setTextColor(Color.parseColor("#6395EC"));
                binding.selectApproval01tv.setCompoundDrawablesWithIntrinsicBounds(check_on,null,null,null);
                binding.selectApproval02.setCardBackgroundColor(Color.parseColor("#FCF0EC"));
                binding.selectApproval02tv.setTextColor(Color.parseColor("#DD6540"));
                binding.selectApproval02tv.setCompoundDrawablesWithIntrinsicBounds(x_on,null,null,null);
                binding.rejectTv.setVisibility(View.GONE);
                binding.rejectSave.setVisibility(View.GONE);
                setUpdateWorktodo("1");
            });
            binding.selectApproval02.setOnClickListener(v -> {
                //반려버튼
                binding.selectApproval01.setCardBackgroundColor(Color.parseColor("#DBDBDB"));
                binding.selectApproval01tv.setTextColor(Color.parseColor("#949494"));
                binding.selectApproval01tv.setCompoundDrawablesWithIntrinsicBounds(check_off,null,null,null);
                binding.selectApproval02.setCardBackgroundColor(Color.parseColor("#DD6540"));
                binding.selectApproval02tv.setTextColor(Color.parseColor("#ffffff"));
                binding.selectApproval02tv.setCompoundDrawablesWithIntrinsicBounds(x_off,null,null,null);
                binding.rejectTv.setVisibility(View.VISIBLE);
                binding.rejectSave.setVisibility(View.VISIBLE);
            });

            binding.rejectSave.setOnClickListener(v -> {
                if( binding.rejectTv.getText().toString().length() == 0){
                    Toast_Nomal("반려사유를 입력해주세요.");
                }else{
                    setUpdateWorktodo("2");
                }
            });

        } catch (Exception e) {
            dlog.i( "Exception E" + e);
        }


    }

    private void setBtnEvent() {

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void removeShared(){
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
        shardpref.remove("users");
        shardpref.remove("usersn");
        shardpref.remove("usersimg");
        shardpref.remove("usersjikgup");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeShared();
    }


    public void setUpdateWorktodo(String kind) {
        incomplete_reason = binding.rejectTv.getText().toString();

        dlog.i("-----setUpdateWorktodo-----");
        dlog.i("id : " + id);
        dlog.i("requester_id : " + requester_id);
        dlog.i("kind : " + kind);
        dlog.i("incomplete_reason : " + incomplete_reason);
        dlog.i("-----setUpdateWorktodo-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApprovalUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ApprovalUpdateInterface api = retrofit.create(ApprovalUpdateInterface.class);
        Call<String> call = api.getData(id,USER_INFO_ID,kind,incomplete_reason);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("setUpdateWorktodo jsonResponse length : " + jsonResponse.length());
                    dlog.i("setUpdateWorktodo jsonResponse : " + jsonResponse);
                    if (jsonResponse.replace("\"", "").equals("success")) {
                        dlog.i("user_id : " + user_id);
                        dlog.i("place_owner_id : " + place_owner_id);
                        getManagerToken(requester_id, "1", place_id, place_name,state);
                        removeShared();
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
                Log.e(TAG, "response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + rc.getBase64decode(response.body()));
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(rc.getBase64decode(response.body()));
                        dlog.i( "GET SIZE : " + Response.length());
                        if (Response.length() == 0) {
                            dlog.i( "SetNoticeListview Thread run! ");
                            dlog.i( "GET SIZE : " + Response.length());
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
                        boolean channelId1 = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            message = "[" + place_name + "]" + "결재가 변동된 업무보고가 있습니다.";
                            PushFcmSend(id, "", message, token, "2", place_id);
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
            click_action = "TaskList1";
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


    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
