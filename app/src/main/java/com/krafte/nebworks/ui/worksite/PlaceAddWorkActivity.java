package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.AssignmentMemberAdapter;
import com.krafte.nebworks.adapter.AssignmentMemberAdapter2;
import com.krafte.nebworks.adapter.MemberListPopAdapter;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceMemberListData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.data.YoilList;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.ScheduleAddInterface;
import com.krafte.nebworks.dataInterface.TaskInputInterface;
import com.krafte.nebworks.dataInterface.TaskUpdateInterface;
import com.krafte.nebworks.dataInterface.TaskreuseInputInterface;
import com.krafte.nebworks.dataInterface.TaskreuseUpInterface;
import com.krafte.nebworks.databinding.ActivityPlaceaddworkBinding;
import com.krafte.nebworks.pop.MemberListPop;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.pop.RepeatSetPop;
import com.krafte.nebworks.pop.SelectTaskDatePop;
import com.krafte.nebworks.pop.WorkTimePicker;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
* 해당 매장의 할일 추가
* */
public class PlaceAddWorkActivity extends AppCompatActivity {
    private static final String TAG = "EmployerAddWorkActivity";
    Context mContext;
    private ActivityPlaceaddworkBinding binding;

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

    //Other
    AssignmentMemberAdapter mAdapter;
    ArrayList<PlaceMemberListData.PlaceMemberListData_list> mList = new ArrayList<>();

    AssignmentMemberAdapter2 mAdapter2;
    ArrayList<PlaceMemberListData.PlaceMemberListData_list> mList2 = new ArrayList<>();

    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mem_mList;
    MemberListPopAdapter mem_mAdapter;

    //    DBConnection dbConnection = new DBConnection();
    DateCurrent dc = new DateCurrent();
    ArrayList<String> SetMemberList;
    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    int total_member_cnt;
    int assignment_kind;
    int EmployeeSelect = -99;
    String Employee_id = "";
    String user_id = "";
    String WorkTitle = "";
    String WorkContents = "";
    String writer_id = "";
    String WorkDay = "";
    int make_kind = 0;

    //반복설정
    int WorkAddSecondNum = 0;
    String[] WorkAddSecond;


    //업무 종류
    String TaskKind = "1";
    //완료방법
    String complete_kind = "";

    //시작시간
    int SelectStartTime = 1;
    String start_time = "-99";
    String StartTime02 = "-99";

    //마감시간
    int SelectEndTime = 1;
    String end_time = "-99";
    String EndTime02 = "-99";

    String message = "업무가 배정되었습니다.";
    String topic = "";

    Handler mHandler;
    String sendTopic = "";
    String sendToken = "";
    String tap_kind = "";

    Drawable icon_on, icon_off;
    String SelectKind = "0";
    String searchDate = "";
    List<String> Ac_memberArray = new ArrayList<>();
    List<String> Ac_memberArray2 = new ArrayList<>();

    /*-------------------*/
    boolean yoil01 = true, yoil02 = true, yoil03 = true, yoil04 = true, yoil05 = true, yoil06 = true, yoil07 = true, yoil08 = true;
    String Sun = "0", Mon = "0", Tue = "0", Wed = "0", Thu = "0", Fri = "0", Sat = "0";
    /*-------------------*/

    /*--------------------*/
    String toDay = "";
    String return_page = "";
    List<YoilList> Yoils = new ArrayList<>();
    List<String> getYoil = new ArrayList<>();
    String[] setYoil = new String[7];

    boolean NeedReportTF = false;

    int a = 0;//정직원 수
    int b = 0;//협력업체 직원 수
    List<String> inmember = new ArrayList<>();
    List<String> othermember = new ArrayList<>();

    /*--------------------*/
    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat", "LongLogTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_placeaddwork);
        binding = ActivityPlaceaddworkBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try{
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            shardpref.putInt("SELECT_POSITION_sub",1);
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
            return_page = shardpref.getString("return_page","0");
            make_kind = shardpref.getInt("make_kind", 0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-1");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub",1);

            //캘린더에서 넘어온 경우 - 선택한 날짜를 가져옴
            searchDate = shardpref.getString("searchDate", "");

            channelId1 = shardpref.getBoolean("channelId1", false);
            channelId2 = shardpref.getBoolean("channelId2", false);
            WorkAddSecond = new String[7];

            //수정할때 필요
            task_no = shardpref.getString("task_no", "0");
            icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
            icon_on = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_on);

            setBtnEvent();
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;

            binding.storeName.setText(place_name);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    String change_place_id = "";
    String change_place_name = "";
    String change_place_owner_id = "";

    private void setBtnEvent() {
        binding.placeChangeArea.setOnClickListener(v -> {
            PlaceListBottomSheet plb = new PlaceListBottomSheet();
            plb.show(getSupportFragmentManager(),"PlaceListBottomSheet");
            plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                change_place_id = place_id;
                change_place_name = place_name;
                change_place_owner_id = place_owner_id;
                shardpref.putString("change_place_id",place_id);
                shardpref.putString("change_place_name",place_name);
                shardpref.putString("change_place_owner_id",place_owner_id);
                dlog.i("change_place_id : " + place_id);
                dlog.i("change_place_name : " + place_name);
                dlog.i("change_place_owner_id : " + place_owner_id);
                binding.storeName.setText(place_name);
            });
        });

        binding.eventStarttime.setOnClickListener(v -> {
            if(RepeatCheck){
                Intent intent = new Intent(mContext, WorkTimePicker.class);
                intent.putExtra("timeSelect_flag", 2);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }else{
                Intent intent = new Intent(mContext, SelectTaskDatePop.class);
                shardpref.putString("SET_TASK_TIME_VALUE","0");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });

        binding.eventEndttime.setOnClickListener(v -> {
            if(RepeatCheck){
                Intent intent = new Intent(mContext, WorkTimePicker.class);
                intent.putExtra("timeSelect_flag", 3);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }else{
                Intent intent = new Intent(mContext, SelectTaskDatePop.class);
                shardpref.putString("SET_TASK_TIME_VALUE","1");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });

        binding.selectRepeatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, RepeatSetPop.class);
            shardpref.putString("SET_TASK_TIME_VALUE","3");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.emptySelectMember.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MemberListPop.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.needReport.setOnClickListener(v -> {
            if(!NeedReportTF){
                NeedReportTF = true;
                binding.repeatBtn.setBackgroundResource(R.drawable.resize_service_on);
                binding.needReport.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.reportTv.setTextColor(Color.parseColor("#ffffff"));
                binding.reportVisible.setVisibility(View.VISIBLE);
                TaskKind = "1";
                binding.select01Box.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.select01.setTextColor(Color.parseColor("#ffffff"));
            }else{
                NeedReportTF = false;
                binding.repeatBtn.setBackgroundResource(R.drawable.resize_service_off);
                binding.needReport.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.reportTv.setTextColor(Color.parseColor("#000000"));
                binding.reportVisible.setVisibility(View.GONE);
            }
        });

        binding.select01Box.setOnClickListener(v -> {
            TaskKind = "1";
            binding.select01Box.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.select01.setTextColor(Color.parseColor("#ffffff"));
            binding.select02Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
            binding.select02.setTextColor(Color.parseColor("#000000"));
        });
        binding.select02Box.setOnClickListener(v -> {
            TaskKind = "0";
            binding.select01Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
            binding.select01.setTextColor(Color.parseColor("#000000"));
            binding.select02Box.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.select02.setTextColor(Color.parseColor("#ffffff"));
        });
        binding.bottomBtnBox.setOnClickListener(v -> {
            SaveAddWork();
        });
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            String thumnail_url = shardpref.getString("thumnail_url", "");
            String name = shardpref.getString("name", "");
            String writer_id = shardpref.getString("writer_id", "");
            SET_TASK_TIME_VALUE = shardpref.getString("SET_TASK_TIME_VALUE", "-1");

            //반복요일 세팅
            String yoillist_String = "";
            yoillist.clear();
            yoillist_String = shardpref.getString("yoillist","");
            overdate = shardpref.getString("overdate","");
            yoillist.addAll(Arrays.asList(yoillist_String.split(",")));

            picker_year = shardpref.getString("picker_year", "00");
            picker_month = shardpref.getString("picker_month", "00");
            picker_day = shardpref.getString("picker_day", "00");
            input_pop_time = shardpref.getString("input_pop_time","");

            if(!String.valueOf(yoillist).equals("[]")){
                RepeatCheck = true;
                binding.repeatBtn.setBackgroundResource(R.drawable.ic_service_white);
                binding.selectRepeatBtn.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.repeatTv.setTextColor(Color.parseColor("#ffffff"));

                binding.startCalendar.setBackgroundResource(R.drawable.ic_time);
                binding.endCalendar.setBackgroundResource(R.drawable.ic_time);
                binding.eventStarttime.setHint("시간을 선택해주세요");
                binding.eventEndttime.setHint("시간을 선택해주세요");

                //반복요일 세팅
                int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
                int hourOfDay = shardpref.getInt("Hour", 0);
                int minute = shardpref.getInt("Min", 0);

                dlog.i("------------------Data Check onResume------------------");
                dlog.i("kind : " + shardpref.getInt("timeSelect_flag", 0));
                dlog.i("Hour : " + shardpref.getInt("Hour", 0));
                dlog.i("Min : " + shardpref.getInt("Min", 0));
                dlog.i("timeSelect_flag : " + timeSelect_flag);
                dlog.i("------------------Data Check onResume------------------");


                if (timeSelect_flag == 2) {
                    Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
                    Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
                    shardpref.remove("timeSelect_flag");
                    shardpref.remove("Hour");
                    shardpref.remove("Min");
                    if (hourOfDay != 0) {
                        binding.eventStarttime.setText(Time01 + ":" + Time02);
                        imm.hideSoftInputFromWindow(binding.inputWorktitle.getWindowToken(), 0);
                        imm.hideSoftInputFromWindow(binding.inputWorkcontents.getWindowToken(), 0);
                    }
                } else if (timeSelect_flag == 3) {
                    Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
                    Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
                    shardpref.remove("timeSelect_flag");
                    shardpref.remove("Hour");
                    shardpref.remove("Min");
                    if (hourOfDay != 0) {
                        binding.eventEndttime.setText(Time01 + ":" + Time02);
                        imm.hideSoftInputFromWindow(binding.inputWorktitle.getWindowToken(), 0);
                        imm.hideSoftInputFromWindow(binding.inputWorkcontents.getWindowToken(), 0);
                    }
                }
            }else{

                dlog.i("input_pop_time : " + input_pop_time);
                dlog.i("SET_TASK_TIME_VALUE : " + SET_TASK_TIME_VALUE);
                RepeatCheck = false;
                binding.repeatBtn.setBackgroundResource(R.drawable.resize_service_off);
                binding.startCalendar.setBackgroundResource(R.drawable.calendar_resize);
                binding.endCalendar.setBackgroundResource(R.drawable.calendar_resize);
                binding.eventStarttime.setHint("날짜를 선택해주세요");
                binding.eventEndttime.setHint("날짜를 선택해주세요");

                if(SET_TASK_TIME_VALUE.equals("0")){
                    binding.eventStarttime.setText(picker_year + "-" + picker_month + "-" + picker_day + " " + input_pop_time);
                }else if(SET_TASK_TIME_VALUE.equals("1")){
                    binding.eventEndttime.setText(picker_year + "-" + picker_month + "-" + picker_day + " " + input_pop_time);
                }
            }

            //추가된 직원
            item_user_id = new ArrayList<>();
            item_user_name = new ArrayList<>();
            item_user_img = new ArrayList<>();
            item_user_jikgup = new ArrayList<>();

            String getuser_id = shardpref.getString("item_user_id","");
            String getuser_name = shardpref.getString("item_user_name","");
            String getuser_img = shardpref.getString("item_user_img","");
            String getuser_position = shardpref.getString("item_user_position","");
            item_user_id.clear();
            item_user_name.clear();
            item_user_img.clear();
            item_user_jikgup.clear();

            if(!getuser_id.isEmpty()){
                item_user_id.addAll(Arrays.asList(getuser_id.split(",")));
            }
            if(!getuser_name.isEmpty()){
                item_user_name.addAll(Arrays.asList(getuser_name.split(",")));
            }
            if(!getuser_img.isEmpty()){
                item_user_img.addAll(Arrays.asList(getuser_img.split(",")));
            }
            if(!getuser_position.isEmpty()){
                item_user_jikgup.addAll(Arrays.asList(getuser_position.split(",")));
            }
            dlog.i("getuser_id : " + getuser_id);
            dlog.i("getuser_name : " + getuser_name);
            dlog.i("getuser_img : " + getuser_img);
            dlog.i("getuser_position : " + getuser_position);
            dlog.i("item_user_id.size() : " + item_user_id.size());
            dlog.i("item_user_name.size() : " + item_user_name.size());
            dlog.i("item_user_img.size() : " + item_user_img.size());
            dlog.i("item_user_jikgup.size() : " + item_user_jikgup.size());

            mem_mList = new ArrayList<>();
            mem_mAdapter = new MemberListPopAdapter(mContext, mem_mList,1);
            binding.selectMemberList.setAdapter(mem_mAdapter);
            binding.selectMemberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

            if (getuser_id.isEmpty()) {
                dlog.i("getuser_id : " + getuser_id);
                dlog.i("getuser_name : " + getuser_name);
                dlog.i("getuser_img : " + getuser_img);
                dlog.i("getuser_position : " + getuser_position);
            } else {
                for (int i = 0; i < item_user_id.size(); i++) {
                    dlog.i("item_user_id : " + item_user_id.get(i));
                    dlog.i("item_user_name : " + item_user_name.get(i));
                    dlog.i("item_user_img : " + item_user_img.get(i));
                    dlog.i("item_user_jikgup : " + item_user_jikgup.get(i));
                    mem_mAdapter.addItem(new WorkPlaceMemberListData.WorkPlaceMemberListData_list(
                            item_user_id.get(i).trim(),
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
                            ""
                    ));
                }
                mem_mAdapter.notifyDataSetChanged();
            }
            //추가된 직원
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void getTaskContents() {

        task_no = shardpref.getString("task_no", "0");
        String writer_id = shardpref.getString("writer_id", "0");
        String kind = shardpref.getString("kind", "0");        // 0:할일, 1:일정
        WorkTitle = shardpref.getString("title", "0");
        WorkContents = shardpref.getString("contents", "0");
        TaskKind = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
        user_id = shardpref.getString("users", "0");
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
        String img_path = shardpref.getString("img_path", "0");
        String complete_yn = shardpref.getString("complete_yn", "n");// y:완료, n:미완료
        String incomplete_reason = shardpref.getString("incomplete_reason", "n"); // n: 미완료 사유

        dlog.i("getTaskContents users : " + user_id);
        dlog.i("getTaskContents Mon : " + Mon);
        dlog.i("getTaskContents Tue : " + Tue);
        dlog.i("getTaskContents Wed : " + Wed);
        dlog.i("getTaskContents Thu : " + Thu);
        dlog.i("getTaskContents Fri : " + Fri);
        dlog.i("getTaskContents Sat : " + Sat);
        dlog.i("getTaskContents Sun : " + Sun);
        inmember.addAll(Arrays.asList(user_id.split(",")));

        message = "수정된 업무가 있습니다.";
        binding.workSave.setText("업무 수정");
        binding.inputWorktitle.setText(WorkTitle);
        binding.inputWorkcontents.setText(WorkContents);

    }


    public interface OnClickListener {
        void onClick();
    }

    private PlaceAddWorkActivity.OnClickListener mListener = null;

    public void setOnClickListener(PlaceAddWorkActivity.OnClickListener listener) {
        this.mListener = listener;
    }

    //업무 저장(추가)
    private void SaveAddWork() {
        String getYoil = shardpref.getString("yoillist","").replace(" ","");
        dlog.i("yoillist : " + yoillist);
        for(String str : getYoil.split(",")){
            if(str.trim().equals("일")){
                Sun = "1";
            }else if(str.trim().equals("월")){
                Mon = "1";
            }else if(str.trim().equals("화")){
                Tue = "1";
            }else if(str.trim().equals("수")){
                Wed = "1";
            }else if(str.trim().equals("목")){
                Thu = "1";
            }else if(str.trim().equals("금")){
                Fri = "1";
            }else if(str.trim().equals("토")){
                Sat = "1";
            }
        }
        if (searchDate.isEmpty()) {
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        } else {
            toDay = searchDate;
        }
        WorkTitle = binding.inputWorktitle.getText().toString();
        WorkContents = binding.inputWorkcontents.getText().toString();
        start_time = binding.eventStarttime.getText().toString();
        end_time = binding.eventEndttime.getText().toString();
        user_id = String.valueOf(item_user_id).replace("[","").replace("]","").replace(" ","").trim();
        dlog.i("------------------SaveAddWork------------------");
        dlog.i("task_no : " + task_no);
        dlog.i("place_id : " + place_id);
        dlog.i("writer_id : " + USER_INFO_ID);
        dlog.i("kind : " + make_kind);
        dlog.i("title : " + WorkTitle);
        dlog.i("contents : " + WorkContents);
        dlog.i("complete_kind : " + String.valueOf(TaskKind));
        dlog.i("task_date : " + toDay);
        dlog.i("start_time : " + start_time);
        dlog.i("end_time : " + end_time);
        dlog.i("sun : " + Sun);
        dlog.i("mon : " + Mon);
        dlog.i("tue : " + Tue);
        dlog.i("wed : " + Wed);
        dlog.i("thu : " + Thu);
        dlog.i("fri : " + Fri);
        dlog.i("sat : " + Sat);
        dlog.i("users : " + user_id);
        dlog.i("overdate : " + overdate);
        if(make_kind == 1){
            if (task_no.equals("0")) {
                @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                    runOnUiThread(() -> {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(TaskInputInterface.URL)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .build();
                        TaskInputInterface api = retrofit.create(TaskInputInterface.class);
                        //--반복 요일
                        dlog.i("------------------SaveAddWork12------------------");
                        Call<String> call = api.getData(place_id, USER_INFO_ID, WorkTitle, WorkContents, TaskKind
                                , toDay, start_time, end_time
                                , Sun, Mon, Tue, Wed, Thu, Fri, Sat,overdate
                                , String.valueOf(item_user_id).replace("[","").replace("]","").trim());
                        call.enqueue(new Callback<String>() {
                            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                //반복되는 요일을 일시 초기화 해준다
                                dlog.e("SaveAddWork function START");
                                dlog.e("response 1: " + response.isSuccessful());
                                dlog.e("response 2: " + response.body());
                                if (response.isSuccessful() && response.body() != null) {
                                    String jsonResponse = response.body();
                                    if (jsonResponse.replace("\"", "").equals("success") || response.body().replace("\"", "").equals("success")) {
                                        dlog.i("assignment_kind : " + assignment_kind);
                                        dlog.i("SelectEmployeeid : " + user_id);

                                        if (!user_id.equals("")) {
                                            SendUserCheck(1);
                                            if(return_page.equals("TaskCalenderActivity")){
                                                pm.CalenderBack(mContext);
                                            }else{
                                                shardpref.putInt("SELECT_POSITION",1);
                                                if(USER_INFO_AUTH.equals("0")){
                                                    pm.Main(mContext);
                                                }else{
                                                    pm.Main2(mContext);
                                                }
                                            }

                                            click_action = "PlaceWorkFragment";
                                            dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
//                                            getEmployeeoken(SelectEmployeeid);
//                                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
//                                                dlog.i( "token : " + token);
//                                                FcmTestFunctionCall();
//                                            });

                                        }

                                    } else if (jsonResponse.replace("\"", "").equals("fail") || response.body().replace("\"", "").equals("fail")) {
                                        Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "서버입력 오류! 데이터를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @SuppressLint("LongLogTag")
                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                dlog.e("에러 = " + t.getMessage());
                            }
                        });
                    });
                });
                th.start();
                try {
                    th.join();
//            getFCMToken();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                    runOnUiThread(() -> {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(TaskUpdateInterface.URL)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .build();
                        TaskUpdateInterface api = retrofit.create(TaskUpdateInterface.class);

                        //--반복 요일
                        dlog.i("------------------SaveAddWork22------------------");
                        Call<String> call = api.getData(task_no, place_id, USER_INFO_ID, "0", WorkTitle, WorkContents, complete_kind
                                , WorkDay, start_time, end_time
                                , Sun, Mon, Tue, Wed, Thu, Fri, Sat
                                , user_id);
                        call.enqueue(new Callback<String>() {
                            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                //반복되는 요일을 일시 초기화 해준다
                                dlog.e("SaveAddWork function START");
                                dlog.e("response 1: " + response.isSuccessful());
                                dlog.e("response 2: " + response.body());
                                if (response.isSuccessful() && response.body() != null) {
                                    String jsonResponse = response.body();
                                    if (jsonResponse.replace("\"", "").equals("success") || response.body().replace("\"", "").equals("success")) {
                                        dlog.i("assignment_kind : " + assignment_kind);
                                        dlog.i("SelectEmployeeid : " + user_id);

                                        if (!user_id.equals("")) {
                                            SendUserCheck(2); //수정할때
                                            if(return_page.equals("TaskCalenderActivity")){
                                                pm.CalenderBack(mContext);
                                            }else{
                                                pm.PlaceWorkBack(mContext);
                                            }
                                            click_action = "PlaceWorkFragment";
                                            dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
//                                            getEmployeeoken(SelectEmployeeid);
//                                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
//                                                dlog.i( "token : " + token);
//                                                FcmTestFunctionCall();
//                                            });

                                        }

                                    } else if (jsonResponse.replace("\"", "").equals("fail") || response.body().replace("\"", "").equals("fail")) {
                                        Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "서버입력 오류! 데이터를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @SuppressLint("LongLogTag")
                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                dlog.e("에러 = " + t.getMessage());
                            }
                        });
                    });
                });
                th.start();
                try {
                    th.join();
//            getFCMToken();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else if(make_kind == 4){
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                runOnUiThread(() -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ScheduleAddInterface.URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                    ScheduleAddInterface api = retrofit.create(ScheduleAddInterface.class);
                    Call<String> call = api.getData(place_id, USER_INFO_ID, WorkTitle, WorkContents
                            , WorkDay, start_time, end_time
                            , Sun, Mon, Tue, Wed, Thu, Fri, Sat
                            , user_id);
                    call.enqueue(new Callback<String>() {
                        @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            dlog.e("SaveAddWork function START make_kind =" + make_kind);
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                String jsonResponse = response.body();
                                if (jsonResponse.replace("\"", "").equals("success") || response.body().replace("\"", "").equals("success")) {
                                    dlog.i("assignment_kind : " + assignment_kind);
                                    dlog.i("SelectEmployeeid : " + user_id);
                                    if (!user_id.equals("")) {
                                        SendUserCheck(1);
                                        pm.CalenderBack(mContext);
                                        click_action = "PlaceWorkFragment";
                                        dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
                                    }

                                } else if (jsonResponse.replace("\"", "").equals("fail") || response.body().replace("\"", "").equals("fail")) {
                                    Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "서버입력 오류! 데이터를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            dlog.e("에러 = " + t.getMessage());
                        }
                    });
                });
            });
            th.start();
            try {
                th.join();
//            getFCMToken();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void SaveAddWork2() {

        /*
         * 일요일 1
         * 월요일 2
         * 화요일 3
         * 수요일 4
         * 목요일 5
         * 금요일 6
         * 토요일 7
         * */
        if (yoil08) {
            //매일 버튼 선택 아닐때
            Sun = yoil07 ? "0" : "1";
            Mon = yoil01 ? "0" : "1";
            Tue = yoil02 ? "0" : "1";
            Wed = yoil03 ? "0" : "1";
            Thu = yoil04 ? "0" : "1";
            Fri = yoil05 ? "0" : "1";
            Sat = yoil06 ? "0" : "1";
        } else {
            //매일 버튼이 선택됬을때
            Sun = "1";
            Mon = "1";
            Tue = "1";
            Wed = "1";
            Thu = "1";
            Fri = "1";
            Sat = "1";
        }

        if (searchDate.isEmpty()) {
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        } else {
            toDay = searchDate;
        }
        dlog.i("------------------SaveAddWork------------------");
        dlog.i("place_id : " + place_id);
        dlog.i("writer_id : " + USER_INFO_ID);
        dlog.i("kind : 0");
        dlog.i("title : " + WorkTitle);
        dlog.i("contents : " + WorkContents);
        dlog.i("complete_kind : " + complete_kind);
        dlog.i("task_date : " + WorkDay);
        dlog.i("start_time : " + start_time);
        dlog.i("end_time : " + end_time);
        dlog.i("sun : " + Sun);
        dlog.i("mon : " + Mon);
        dlog.i("tue : " + Tue);
        dlog.i("wed : " + Wed);
        dlog.i("thu : " + Thu);
        dlog.i("fri : " + Fri);
        dlog.i("sat : " + Sat);
        dlog.i("users : " + user_id);
        if (task_no.equals("0")) {
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                runOnUiThread(() -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(TaskreuseInputInterface.URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                    TaskreuseInputInterface api = retrofit.create(TaskreuseInputInterface.class);

                    Call<String> call = api.getData(place_id, USER_INFO_ID, WorkTitle, WorkContents, complete_kind
                            , start_time, end_time
                            , Sun, Mon, Tue, Wed, Thu, Fri, Sat
                    );
                    call.enqueue(new Callback<String>() {
                        @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            //반복되는 요일을 일시 초기화 해준다
                            dlog.e("SaveAddWork2 function START");
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                String jsonResponse = response.body();
                                if (jsonResponse.replace("\"", "").equals("success") || response.body().replace("\"", "").equals("success")) {
                                    dlog.i("assignment_kind : " + assignment_kind);
                                    dlog.i("SelectEmployeeid : " + user_id);

                                    pm.PlaceWorkBack(mContext);
                                    click_action = "PlaceWorkFragment";
                                    dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);

                                } else if (jsonResponse.replace("\"", "").equals("fail") || response.body().replace("\"", "").equals("fail")) {
                                    Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "서버입력 오류! 데이터를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            dlog.e("에러 = " + t.getMessage());
                        }
                    });
                });
            });
            th.start();
            try {
                th.join();
//            getFCMToken();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                runOnUiThread(() -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(TaskreuseUpInterface.URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                    TaskreuseUpInterface api = retrofit.create(TaskreuseUpInterface.class);
                    Call<String> call = api.getData(task_no, USER_INFO_ID, WorkTitle, WorkContents, complete_kind
                            , start_time, end_time
                            , Sun, Mon, Tue, Wed, Thu, Fri, Sat
                    );
                    call.enqueue(new Callback<String>() {
                        @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            //반복되는 요일을 일시 초기화 해준다
                            dlog.e("SaveAddWork22 function START");
                            dlog.e("response 1: " + response.isSuccessful());
                            dlog.e("response 2: " + response.body());
                            if (response.isSuccessful() && response.body() != null) {
                                String jsonResponse = response.body();
                                if (jsonResponse.replace("\"", "").equals("success") || response.body().replace("\"", "").equals("success")) {
                                    dlog.i("assignment_kind : " + assignment_kind);
                                    dlog.i("SelectEmployeeid : " + user_id);

                                    if(!user_id.isEmpty()){
                                        SendUserCheck(1);
                                    }
                                    pm.PlaceWorkBack(mContext);
                                    click_action = "PlaceWorkFragment";
                                    dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
//                                            getEmployeeoken(SelectEmployeeid);
//                                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
//                                                dlog.i( "token : " + token);
//                                                FcmTestFunctionCall();
//                                            });
                                } else if (jsonResponse.replace("\"", "").equals("fail") || response.body().replace("\"", "").equals("fail")) {
                                    Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "서버입력 오류! 데이터를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            dlog.e("에러 = " + t.getMessage());
                        }
                    });
                });
            });
            th.start();
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean SaveCheck() {
        WorkContents = binding.inputWorkcontents.getText().toString();
        WorkTitle = binding.inputWorktitle.getText().toString();

        TaskKind = String.valueOf(make_kind);
        String users = shardpref.getString("users", "0");
        String users2 = shardpref.getString("users2", "0");
        if (Ac_memberArray.size() == 0) {
            Ac_memberArray.addAll(Arrays.asList(users.split(", ")));
        }
        if (Ac_memberArray2.size() == 0) {
            Ac_memberArray2.addAll(Arrays.asList(users2.split(", ")));
        }
        Ac_memberArray.remove("0");
        Ac_memberArray2.remove("0");

        Ac_memberArray.addAll(Ac_memberArray2);

        if(Ac_memberArray.size() != 0 && Ac_memberArray2.size() != 0){
            user_id = Ac_memberArray.toString().replace(" ", "").replace("[", "").replace("]", "").trim();
            total_member_cnt = Ac_memberArray.size() + Ac_memberArray2.size();
        }else{
            List<String> user_cnt = new ArrayList<>(Arrays.asList(user_id.split(",")));
            dlog.i("user_id : " + user_id);
            dlog.i("user_cnt.size() : " + user_cnt.size());
            total_member_cnt = user_cnt.size();
        }

        dlog.i("EmployeeSelect : " + EmployeeSelect);
        dlog.i("SelectKind : " + SelectKind);
        if (EmployeeSelect == -99 && SelectKind.equals("1")) {
            if (total_member_cnt == 0) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "등록된 직원이 없습니다. 직원을 추가 후 이용해 주세요.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            } else {
                Toast.makeText(this, "업무를 배정할 직원을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
            return false;
        } else if (WorkTitle.equals("")) {
            dlog.i("WorkTitle");
            Toast.makeText(this, "할일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (WorkContents.isEmpty()) {
            dlog.i("WorkContents");
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (complete_kind.isEmpty() && make_kind == 1) {
            Toast.makeText(this, "완료방법을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (complete_kind.isEmpty() && make_kind == 4) {
            return true;
        } else if (WorkDay.isEmpty()) {
            Toast.makeText(this, "업무날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (start_time.equals("-99")) {
            dlog.i("StarTime");
            Toast.makeText(this, "시작시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (end_time.equals("-99")) {
            dlog.i("StarTime");
            Toast.makeText(this, "마감시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_id.equals("")) {
            if (make_kind == 2) {
                return true;
            } else {
                if (total_member_cnt == 0) {
                    Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                    intent.putExtra("data", "등록된 직원이 없습니다. 직원을 추가 후 이용해 주세요.");
                    intent.putExtra("left_btn_txt", "닫기");
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_up, 0);
                } else {
                    Toast.makeText(this, "업무를 배정할 직원을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        } else {
            dlog.i("제목2 : " + WorkTitle);
            dlog.i("내용 : " + WorkContents);
            dlog.i("완료방법2 : " + complete_kind);
            dlog.i("마감시간2 : " + end_time + ":" + EndTime02);
            dlog.i("반복 : " + getYoil.toString().replace("[", "").replace("]", ""));
            dlog.i("작업날짜 : " + WorkDay);
            dlog.i("선택된 직원 : " + user_id);
            return true;
        }
    }

    private boolean SaveCheck2() {
        WorkContents = binding.inputWorkcontents.getText().toString();
        WorkTitle = binding.inputWorktitle.getText().toString();
        TaskKind = String.valueOf(make_kind);


        if (WorkTitle.equals("")) {
            dlog.i("WorkTitle");
            Toast.makeText(this, "할일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (WorkContents.isEmpty()) {
            dlog.i("WorkContents");
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (complete_kind.isEmpty()) {
            dlog.i("WorkAddFirst");
            Toast.makeText(this, "완료방법을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (WorkDay.isEmpty()) {
            Toast.makeText(this, "업무날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (start_time.equals("-99")) {
            dlog.i("StarTime");
            Toast.makeText(this, "시작시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (end_time.equals("-99")) {
            dlog.i("StarTime");
            Toast.makeText(this, "마감시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            dlog.i("제목2 : " + WorkTitle);
            dlog.i("내용 : " + WorkContents);
            dlog.i("완료방법2 : " + complete_kind);
            dlog.i("마감시간2 : " + end_time + ":" + EndTime02);
            dlog.i("반복 : " + getYoil.toString().replace("[", "").replace("]", ""));
            dlog.i("작업날짜 : " + WorkDay);
            dlog.i("선택된 직원 : " + user_id);
            return true;
        }
    }

    /* -- 할일 추가 FCM 전송 영역 */
    private void SendUserCheck(int flag){
        List<String> member = new ArrayList<>();
        dlog.i("보내야 하는 직원 배열 :" + user_id);
        member.addAll(Arrays.asList(user_id.split(",")));
        dlog.i("보내야 하는 직원 수 :" + member.size());
        dlog.i("보내야 하는 직원 List<String>  :" + member);
        if(flag == 1){
            message = "[배정업무] :" + binding.title.getText().toString();
        }else if(flag == 2){
            message = "[배정업무수정] :" + binding.title.getText().toString();
        }
        for(int a = 0; a < member.size(); a++){
            if(place_owner_id.equals(member.get(a))){
                getManagerToken(member.get(a),"0", place_id,place_name);
            }else{
                getManagerToken(member.get(a),"1", place_id,place_name);
            }
        }
    }
    public void getManagerToken(String user_id, String type, String place_id, String place_name) {
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
                    if(Response.length() > 0){
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        String department = shardpref.getString("USER_INFO_SOSOK","");
                        String position = shardpref.getString("USER_INFO_JIKGUP","");
                        String name = shardpref.getString("USER_INFO_NAME","");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            String message = department + " " + position + " " + name + " 님 " + place_name + "에서 업무가 배정되었습니다.";
                            PushFcmSend(id, "", message, token, "1", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e( "에러 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    String click_action = "";
    private void PushFcmSend(String topic,String title, String message, String token,String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "PlaceWorkFragment";
            dbConnection.FcmTestFunction(topic,title,message,token,click_action,tag,place_id);
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


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        shardpref.putInt("SELECT_POSITION",1);
        shardpref.putInt("SELECT_POSITION_sub", 0);
        if(USER_INFO_AUTH.equals("0")){
            pm.Main(mContext);
        }else{
            pm.Main2(mContext);
        }

    }
}
