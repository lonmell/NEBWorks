package com.krafte.nebworks.ui.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MemberListPopAdapter;
import com.krafte.nebworks.adapter.MultiImageAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.databinding.ActivityTaskReportDetailBinding;
import com.krafte.nebworks.pop.PhotoPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
* 작성자 방창배
* 점주가 근로자가 작성한 업무를 확인하러 오는 페이지
* */
public class TaskReportDetailActivity extends AppCompatActivity {
    private static final String TAG = "TaskReportDetailActivity";
    Context mContext;
    private ActivityTaskReportDetailBinding binding;

    // shared 저장값
    PreferenceHelper shardpref;

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
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

    ArrayList<Uri> uriList = new ArrayList<>();// 이미지의 uri를 담을 ArrayList 객체
    MultiImageAdapter adapter;

    DateCurrent dc = new DateCurrent();
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
    String incomplete_reason = "";
    int make_kind = 0;
    String img_path = "";
    String TaskKind = "1";
    String complete_kind = "";
    String start_time = "-99";
    String end_time = "-99";
    String complete_yn = "";
    String reject_reason = "";
    String updated_at = "";

    String Sun = "0", Mon = "0", Tue = "0", Wed = "0", Thu = "0", Fri = "0", Sat = "0";
    String toDay = "";
    String return_page = "";

    Drawable check_on;
    Drawable check_off;
    Drawable x_on;
    Drawable x_off;

    boolean NeedReportTF = false;

    int a = 0;
    List<String> inmember = new ArrayList<>();

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat", "LongLogTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_placeaddwork);
        binding = ActivityTaskReportDetailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            //Singleton Area
            place_id            = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
            place_name          = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());
            place_owner_id      = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());
            place_owner_name    = shardpref.getString("place_owner_name", PlaceCheckData.getInstance().getPlace_owner_name());
            place_address       = shardpref.getString("place_address", PlaceCheckData.getInstance().getPlace_address());
            place_latitude      = shardpref.getString("place_latitude", PlaceCheckData.getInstance().getPlace_latitude());
            place_longitude     = shardpref.getString("place_longitude", PlaceCheckData.getInstance().getPlace_longitude());
            place_start_time    = shardpref.getString("place_state_time", PlaceCheckData.getInstance().getPlace_start_time());
            place_end_time      = shardpref.getString("place_end_time", PlaceCheckData.getInstance().getPlace_end_time());
            place_img_path      = shardpref.getString("place_img_path" ,PlaceCheckData.getInstance().getPlace_img_path());
            place_start_date    = shardpref.getString("place_start_date", PlaceCheckData.getInstance().getPlace_start_date());
            place_created_at    = shardpref.getString("place_created", PlaceCheckData.getInstance().getPlace_created_at());
            return_page         = shardpref.getString("return_page", ReturnPageData.getInstance().getPage());

            USER_INFO_ID        = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
            USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");

            //shardpref Area
            shardpref.putInt("SELECT_POSITION_sub", 1);
            make_kind = shardpref.getInt("make_kind", 0);

            check_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_blue_check);
            check_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_circle_gray_check);
            x_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_red_x);
            x_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_white_x);

            setBtnEvent();
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            WorkDay = toDay;


        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.title.setText("결재 상세");
    }


    private void setBtnEvent() {

        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });
        binding.bottomBtnBox.setOnClickListener(v -> {
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.remove("SELECT_POSITION_sub");
            pm.Main(mContext);
        });

        binding.goApproval.setOnClickListener(v -> {
            pm.Approval(mContext);
        });


    }


    String overdate = "";
    List<String> item_user_id;
    List<String> item_user_name;
    List<String> item_user_img;
    List<String> item_user_jikgup;
    InputMethodManager imm;
    String decision = "";

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        try {
            uriList.clear();
            imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            dlog.i("-----getTaskContents START-----");
            task_no             = shardpref.getString("task_no", "0");
            writer_id           = shardpref.getString("writer_id", "0");
            WorkTitle           = shardpref.getString("title", "0");
            WorkContents        = shardpref.getString("contents", "0");
            TaskKind            = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
            user_id             = shardpref.getString("users", "0");
            usersn              = shardpref.getString("usersn", "0");
            usersimg            = shardpref.getString("usersimg", "0");
            usersjikgup         = shardpref.getString("usersjikgup", "0");
            WorkDay             = shardpref.getString("task_date", "0");
            start_time          = shardpref.getString("start_time", "0");
            end_time            = shardpref.getString("end_time", "0");
            Sun                 = shardpref.getString("sun", "0");
            Mon                 = shardpref.getString("mon", "0");
            Tue                 = shardpref.getString("tue", "0");
            Wed                 = shardpref.getString("wed", "0");
            Thu                 = shardpref.getString("thu", "0");
            Fri                 = shardpref.getString("fri", "0");
            Sat                 = shardpref.getString("sat", "0");
            overdate            = shardpref.getString("overdate", "0");
            img_path            = shardpref.getString("img_path", "0");
            approval_state      = shardpref.getString("approval_state", "0");
            complete_yn         = shardpref.getString("complete_yn", "n");
            reject_reason       = shardpref.getString("reject_reason", "n");
            incomplete_reason   = shardpref.getString("incomplete_reason", "n");
            updated_at          = shardpref.getString("updated_at", "0");

            dlog.i("TaskReportDetail img_path ; " + img_path);
            dlog.i("TaskReportDetail TaskKind ; " + TaskKind);
            dlog.i("TaskReportDetail complete_yn ; " + complete_yn);
            binding.workSave.setText("목록으로");
            binding.inputWorktitle.setText(WorkTitle);
            binding.inputWorkcontents.setText(WorkContents);
            binding.taskKind.setText(TaskKind.equals("0")?"체크":"인증사진");
            binding.startTime.setText(start_time);
            binding.endTime.setText(end_time);
            binding.reportTime.setText(updated_at);

            binding.bottomBtnBox2.setVisibility(View.GONE);
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
            binding.selectMemberList.setAdapter(mem_mAdapter);
            binding.selectMemberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

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

            dlog.i("feed_img_path : " + img_path);
            for (String s : img_path.split(",")) {
                uriList.add(Uri.parse(s));
            }
            adapter = new MultiImageAdapter(uriList, getApplicationContext());
            binding.imgList.setAdapter(adapter);
            binding.imgList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            adapter.setOnClickListener(new MultiImageAdapter.OnClickListener() {
                @Override
                public void onClick(View v, int position) {
                    Intent intent = new Intent(mContext, PhotoPopActivity.class);
                    intent.putExtra("data", String.valueOf(uriList));
                    intent.putExtra("pos", position);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
            });

            if(!incomplete_reason.equals("null")){
                binding.incompleteArea.setVisibility(View.VISIBLE);
                binding.incompleteTitle.setText(incomplete_reason);
            }

            //--approval_state -- // 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전
            if(approval_state.equals("0") || approval_state.equals("1") || approval_state.equals("3")){
                binding.approvalState.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
                if(approval_state.equals("0")){
                    binding.approvalState.setText("결재대기중");
                }else if(approval_state.equals("1")){
                    binding.approvalState.setText("승인");
                }
                binding.rejectArea.setVisibility(View.GONE);
            }else{
                binding.rejectArea.setVisibility(View.VISIBLE);
                binding.rejectTv.setText(reject_reason);
                binding.approvalState.setTextColor(Color.parseColor("#FF0000"));
                binding.approvalState.setText("반려");
                if(reject_reason.length() > 0){
                    binding.rejectTitle.setVisibility(View.VISIBLE);
                }else{
                    binding.rejectTitle.setVisibility(View.GONE);
                }
            }

            dlog.i("-----getTaskContents END-----");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RemoveShared();
    }

    private void RemoveShared() {
        shardpref.remove("task_no");
        shardpref.remove("writer_id");
        shardpref.remove("kind");
        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("complete_kind");       // 0:체크, 1:사진
        shardpref.remove("users");
        shardpref.remove("usersn");
        shardpref.remove("usersimg");
        shardpref.remove("usersjikgup");
        shardpref.remove("task_date");
        shardpref.remove("start_time");
        shardpref.remove("end_time");
        shardpref.remove("sun");
        shardpref.remove("mon");
        shardpref.remove("tue");
        shardpref.remove("wed");
        shardpref.remove("thu");
        shardpref.remove("fri");
        shardpref.remove("sat");
        shardpref.remove("img_path");
        shardpref.remove("complete_yn");
        shardpref.remove("incomplete_reason");
        shardpref.remove("approval_state");
        shardpref.remove("overdate");
        shardpref.remove("make_kind");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        shardpref.putInt("SELECT_POSITION", 1);
        shardpref.remove("SELECT_POSITION_sub");
        if(USER_INFO_AUTH.equals("0")){
            pm.Main(mContext);
        }else if(USER_INFO_AUTH.equals("1")){
            pm.Main2(mContext);
        }
    }
}