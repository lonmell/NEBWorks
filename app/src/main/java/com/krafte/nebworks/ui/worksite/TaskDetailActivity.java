package com.krafte.nebworks.ui.worksite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MemberListPopAdapter;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.databinding.ActivityTaskDetailBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {
    private ActivityTaskDetailBinding binding;
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    boolean channelId1 = false;
    boolean channelId2 = false;
    boolean EmployeeChannelId1 = false;

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
    String task_no = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mem_mList;
    MemberListPopAdapter mem_mAdapter;

    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    String user_id = "";
    String usersn = "";
    String usersimg = "";
    String usersjikgup = "";
    String WorkTitle = "";
    String WorkContents = "";
    String writer_id = "";
    String WorkDay = "";
    String approval_state = "";
    int make_kind = 0;

    String TaskKind = "1";
    String start_time = "-99";
    String end_time = "-99";
    String Sun = "0", Mon = "0", Tue = "0", Wed = "0", Thu = "0", Fri = "0", Sat = "0";

    String message = "업무가 배정되었습니다.";
    Drawable icon_on, icon_off;
    String searchDate = "";

    String return_page = "";
    List<String> inmember = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityTaskDetailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            shardpref.putInt("SELECT_POSITION_sub", 1);
            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            place_owner_name = shardpref.getString("place_owner_name", "0");
            place_management_office = shardpref.getString("place_management_office", "0");
            place_address = shardpref.getString("place_address", "0");
            place_latitude = shardpref.getString("place_latitude", "0");
            place_longitude = shardpref.getString("place_longitude", "0");
            place_start_time = shardpref.getString("place_start_time", "0");
            place_end_time = shardpref.getString("place_end_time", "0");
            place_img_path = shardpref.getString("place_img_path", "0");
            place_start_date = shardpref.getString("place_start_date", "0");
            place_created_at = shardpref.getString("place_created_at", "0");
            return_page = shardpref.getString("return_page", "0");
            make_kind = shardpref.getInt("make_kind", 0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-1");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 1);

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            setBtnEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    String picker_year = "";
    String picker_month = "";
    String picker_day = "";
    String input_pop_time = "";

    String SET_TASK_TIME_VALUE = "";
    List<String> yoillist = new ArrayList<>();
    String overdate = "";
    boolean RepeatCheck = false;
    String Time01 = "-99";
    String Time02 = "-99";

    List<String> item_user_id;
    List<String> item_user_name;
    List<String> item_user_img;
    List<String> item_user_jikgup;
    InputMethodManager imm;

    @Override
    public void onResume() {
        super.onResume();
        dlog.i("-----getTaskContents START-----");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        task_no = shardpref.getString("task_no", "0");
        writer_id = shardpref.getString("writer_id", "0");
        WorkTitle = shardpref.getString("title", "0");
        WorkContents = shardpref.getString("contents", "0");
        TaskKind = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
        user_id = shardpref.getString("users", "0");
        usersn = shardpref.getString("usersn", "0");
        usersimg = shardpref.getString("usersimg", "0");
        usersjikgup = shardpref.getString("usersjikgup", "0");
        WorkDay = shardpref.getString("task_date", "0");
        start_time = shardpref.getString("start_time", "0");
        end_time = shardpref.getString("end_time", "0");
        Sun = shardpref.getString("sun", "0");
        Mon = shardpref.getString("mon", "0");
        Tue = shardpref.getString("tue", "0");
        Wed = shardpref.getString("wed", "0");
        Thu = shardpref.getString("thu", "0");
        Fri = shardpref.getString("fri", "0");
        Sat = shardpref.getString("sat", "0");
        overdate = shardpref.getString("overdate", "0");
        approval_state = shardpref.getString("approval_state","0");// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전


        dlog.i("getTaskContents users : " + user_id);
        dlog.i("getTaskContents usersn : " + usersn);
        dlog.i("getTaskContents usersimg : " + usersimg);
        dlog.i("getTaskContents complete_kind : " + TaskKind);
        dlog.i("getTaskContents Mon : " + Mon);
        dlog.i("getTaskContents Tue : " + Tue);
        dlog.i("getTaskContents Wed : " + Wed);
        dlog.i("getTaskContents Thu : " + Thu);
        dlog.i("getTaskContents Fri : " + Fri);
        dlog.i("getTaskContents Sat : " + Sat);
        dlog.i("getTaskContents Sun : " + Sun);
        dlog.i("getTaskContents overdate : " + overdate);
        dlog.i("getTaskContents start_time : " + start_time);
        dlog.i("getTaskContents end_time : " + end_time);
        dlog.i("getTaskContents approval_state : " + approval_state);// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전

        if(approval_state.equals("3")){
            if(USER_INFO_AUTH.equals("0")){//0-관리자 / 1- 근로자
                binding.acceptTv.setText("수정하기");
            }else{
                binding.acceptTv.setText("업무 보고하기");
                int a = 0;
                for(String str : user_id.split(",")){
                    dlog.i("acceptTv str : " + str);
                    dlog.i("USER_INFO_ID : " + USER_INFO_ID);
                    dlog.i("str.equals(USER_INFO_ID) : " + str.replace("[","").replace("]","").equals(USER_INFO_ID));
                    if(str.replace("[","").replace("]","").equals(USER_INFO_ID)){
                        //배정 아이디가 포함되는 직원이 한명이라도 있을때는 업무 보고하기 버튼 보이기
                        a++;
                    }
                    if(a > 0){
                        binding.acceptBtnBox.setVisibility(View.VISIBLE);
                    }else{
                        binding.acceptBtnBox.setVisibility(View.GONE);
                    }
                }
            }
        }else if(approval_state.equals("2")){
            binding.acceptTv.setText("업무 보고하기");
            int a = 0;
            for(String str : user_id.split(",")){
                dlog.i("acceptTv str : " + str);
                dlog.i("USER_INFO_ID : " + USER_INFO_ID);
                dlog.i("str.equals(USER_INFO_ID) : " + str.replace("[","").replace("]","").equals(USER_INFO_ID));
                if(str.replace("[","").replace("]","").equals(USER_INFO_ID)){
                    //배정 아이디가 포함되는 직원이 한명이라도 있을때는 업무 보고하기 버튼 보이기
                    a++;
                }
                if(a > 0){
                    binding.acceptBtnBox.setVisibility(View.VISIBLE);
                }else{
                    binding.acceptBtnBox.setVisibility(View.GONE);
                }
            }
        }else{
            binding.acceptTv.setText("보고 확인하기");
        }

        inmember.addAll(Arrays.asList(user_id.split(",")));

        binding.title.setText(WorkTitle);
        binding.contents.setText(WorkContents);

        //반복요일 세팅
        List<String> getYoil = new ArrayList<>();
        if (Mon.equals("1")) {
            getYoil.add("월");
        }
        if (Tue.equals("1")) {
            getYoil.add("화");
        }
        if (Wed.equals("1")) {
            getYoil.add("수");
        }
        if (Thu.equals("1")) {
            getYoil.add("목");
        }
        if (Fri.equals("1")) {
            getYoil.add("금");
        }
        if (Sat.equals("1")) {
            getYoil.add("토");
        }
        if (Sun.equals("1")) {
            getYoil.add("일");
        }
        binding.startTime.setText(start_time);
        binding.endTime.setText(end_time);
        binding.taskKind.setText(TaskKind.equals("0")?"체크":"인증사진");
        item_user_id = new ArrayList<>();
        item_user_name = new ArrayList<>();
        item_user_img = new ArrayList<>();
        item_user_jikgup = new ArrayList<>();

        item_user_id.addAll(Arrays.asList(user_id.replace("[", "").replace("]", "").split(",")));
        item_user_name.addAll(Arrays.asList(usersn.replace("[", "").replace("]", "").split(",")));
        item_user_img.addAll(Arrays.asList(usersimg.replace("[", "").replace("]", "").split(",")));
        item_user_jikgup.addAll(Arrays.asList(usersjikgup.replace("[", "").replace("]", "").split(",")));

        shardpref.putString("item_user_id", String.valueOf(item_user_id));
        shardpref.putString("item_user_name", String.valueOf(item_user_name));
        shardpref.putString("item_user_img", String.valueOf(item_user_img));
        shardpref.putString("item_user_position", String.valueOf(item_user_jikgup));

        mem_mList = new ArrayList<>();
        mem_mAdapter = new MemberListPopAdapter(mContext, mem_mList, 1);
        binding.memberList.setAdapter(mem_mAdapter);
        binding.memberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

        if (user_id.isEmpty() || user_id.equals("0")) {
            dlog.i("getTaskContents getuser_id : " + item_user_id);
            dlog.i("getTaskContents getuser_name : " + item_user_name);
            dlog.i("getTaskContents getuser_img : " + item_user_img);
            item_user_id.clear();
            item_user_name.clear();
            item_user_img.clear();
            item_user_jikgup.clear();
        } else {
            dlog.i("getTaskContents item_user_id.size() : " + item_user_id.size());
            binding.memberCnt.setText(item_user_id.size() + "명");
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
        dlog.i("-----getTaskContents END-----");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setBtnEvent() {
        binding.acceptTv.setOnClickListener(v -> {
            if(approval_state.equals("3")){
                if(USER_INFO_AUTH.equals("0")){//0-관리자 / 1- 근로자
                    shardpref.putInt("make_kind",1);
                    shardpref.putInt("SELECT_POSITION", 1);
                    shardpref.putInt("SELECT_POSITION_sub", 0);
                    pm.addWorkGo(mContext);
                }else{
                    pm.TaskReport(mContext);
                }
            }else if(approval_state.equals("2")){
                pm.TaskReport(mContext);
            }else{
                pm.TaskReportDetail(mContext);
            }
        });
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });
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
}
