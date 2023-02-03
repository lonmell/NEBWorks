package com.krafte.nebworks.ui.approval;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.bottomsheet.WorkgotoBottomSheet;
import com.krafte.nebworks.data.CalendarSetData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.TaskCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.TaskSapprovalInterface;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkCalendersetData;
import com.krafte.nebworks.databinding.ActivityTaskApprovalBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.OnSwipeTouchListener;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TaskApprovalFragment extends AppCompatActivity {
    private static final String TAG = "TaskApprovalFragment";
    private ActivityTaskApprovalBinding binding;

    Context mContext;
    Activity activity;
    DateCurrent dc = new DateCurrent();

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    int SELECT_POSITION = 0;
    String place_id;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    Drawable icon_off;
    Drawable icon_on;
    PageMoveClass pm = new PageMoveClass();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    int paging_position = 0;
    Dlog dlog = new Dlog();
    String return_page = "";

    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMPicker = "";
    String bYear = "";
    String bMonth = "";
    String bDay = "";

    boolean chng_icon = false;
    String change_place_id = "";
    String change_place_name = "";
    String change_member_id = "";
    String change_member_name = "";

    String Tap = "";
    ArrayList<TaskCheckData.TaskCheckData_list> mList;
    ArrayList<CalendarSetData.CalendarSetData_list> mList2 = new ArrayList<>();
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList3;
    ApprovalAdapter mAdapter = null;
    WorkCalenderAdapter mAdapter2 = null;


    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_task_approval);
        binding = ActivityTaskApprovalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        activity = (Activity) this;
        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
        icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);


        //Singleton Area
        USER_INFO_ID        = UserCheckData.getInstance().getUser_id();
        USER_INFO_NAME      = UserCheckData.getInstance().getUser_name();
        USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");
        place_id            = PlaceCheckData.getInstance().getPlace_id();

        //shardpref Area
        SELECT_POSITION     = shardpref.getInt("SELECT_POSITION", 0);
        wifi_certi_flag     = shardpref.getBoolean("wifi_certi_flag", false);
        gps_certi_flag      = shardpref.getBoolean("gps_certi_flag", false);
        return_page         = shardpref.getString("return_page","");
        ReturnPageData.getInstance().setPage("BusinessApprovalActivity");
//        shardpref.putString("returnPage", "BusinessApprovalActivity");

        change_place_id = place_id;
        change_member_id = "";

        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
//            if(USER_INFO_AUTH.equals("0")){
//                pm.Main(mContext);
//            }else{
//                pm.Main2(mContext);
//            }
        });
        setBtnEvent();
    }

    @Override
    public void onStart(){
        super.onStart();
        GetApprovalList("",select_date);
    }
    @Override
    public void onResume(){
        super.onResume();
        GetApprovalList("",select_date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        shardpref.putInt("SELECT_POSITION",0);
//        if(USER_INFO_AUTH.equals("0")){
//            pm.Main(mContext);
//        }else{
//            pm.Main2(mContext);
//        }
    }

    String select_date = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
    private void ChangeTap(String i){
        Tap = i;
        binding.select01tv.setTextColor(Color.parseColor("#949494"));
        binding.select02tv.setTextColor(Color.parseColor("#949494"));
        binding.select03tv.setTextColor(Color.parseColor("#949494"));
        binding.select04tv.setTextColor(Color.parseColor("#949494"));
        binding.select01line.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.select02line.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.select03line.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.select04line.setBackgroundColor(Color.parseColor("#FFFFFF"));
        select_date = Year + "-" + Month + "-" + Day;
        if(i.equals("")){
            binding.select01tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select01line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }else if(i.equals("0")){
            binding.select02tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select02line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }else if(i.equals("1")){
            binding.select03tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select03line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }else if(i.equals("2")){
            binding.select04tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select04line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }
    }
    private void setBtnEvent(){
        binding.select01.setOnClickListener(v -> {
            ChangeTap("");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap,select_date);
        });
        binding.select02.setOnClickListener(v -> {
            ChangeTap("0");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap,select_date);
        });
        binding.select03.setOnClickListener(v -> {
            ChangeTap("1");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap,select_date);
        });
        binding.select04.setOnClickListener(v -> {
            ChangeTap("2");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap,select_date);
        });

        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        binding.setdate.setText(toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        Day = toDay.substring(8,10);
        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");

        binding.prevDate.setOnClickListener(v -> {
            try {
//                String getDate = binding.setdate.getText().toString().replace("년 ","-").replace("월 ","-").replace("일","");
//                // 문자열 -> Date
//                Date date = sdf.parse(getDate);
//                dlog.i("Calendar.DATE : " + sdf.format(date));
                if (chng_icon) {
                    binding.noDataTxt.setVisibility(View.GONE);
                    cal.add(Calendar.MONTH, -1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0,4);
                    Month = toDay.substring(5,7);
                    Day = toDay.substring(8,10);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                    SetCalenderData();
                } else {
                    binding.noDataTxt.setVisibility(View.VISIBLE);
                    cal.add(Calendar.DATE, -1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0,4);
                    Month = toDay.substring(5,7);
                    Day = toDay.substring(8,10);
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    select_date = Year + "-" + Month + "-" + Day;
                    GetApprovalList(Tap,select_date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            try {
//                String getDate = binding.setdate.getText().toString().replace("년 ","-").replace("월 ","-").replace("일","");
//                // 문자열 -> Date
//                Date date = sdf.parse(getDate);
//                dlog.i("Calendar.DATE : " + sdf.format(date));
                if (chng_icon) {
                    binding.noDataTxt.setVisibility(View.GONE);
                    cal.add(Calendar.MONTH, +1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0,4);
                    Month = toDay.substring(5,7);
                    Day = toDay.substring(8,10);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                    SetCalenderData();
                } else {
                    binding.noDataTxt.setVisibility(View.VISIBLE);
                    cal.add(Calendar.DATE, +1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0,4);
                    Month = toDay.substring(5,7);
                    Day = toDay.substring(8,10);
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    select_date = Year + "-" + Month + "-" + Day;
                    GetApprovalList(Tap,select_date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month+1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length()==1?"0"+Day:Day;
                Month = Month.length()==1?"0"+Month:Month;
                if (chng_icon) {
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                } else {
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                }
                getYMPicker = binding.setdate.getText().toString().substring(0,7);
//                SetCalenderData();
//                setRecyclerView();
                select_date = Year + "-" + Month + "-" + Day;
                GetApprovalList(Tap,select_date);
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });

        binding.changePlace.setOnClickListener(v -> {
            PaySelectPlaceActivity psp = new PaySelectPlaceActivity();
            psp.show(getSupportFragmentManager(), "PaySelectPlaceActivity");
            psp.setOnClickListener(new PaySelectPlaceActivity.OnClickListener() {
                @Override
                public void onClick(View v, String getplace_id, String getplace_name) {
                    change_place_id = getplace_id;
                    change_place_name = getplace_name;
                    dlog.i("change_place_id : " + getplace_id);
                    dlog.i("change_place_name : " + getplace_name);
                    if (getplace_name.equals("전체매장")) {
                        binding.changePlaceTv.setText("전체매장");
                        change_place_id = place_id;
                        change_place_name = USER_INFO_ID;
                        shardpref.putString("change_place_id", place_id);
                        shardpref.putString("change_place_name", USER_INFO_ID);
                    } else {
                        binding.changePlaceTv.setText(getplace_name);
                        shardpref.putString("change_place_id", getplace_id);
                        shardpref.putString("change_place_name", getplace_name);
                    }
                    dlog.i("change_place_id : " + change_place_id);
                    dlog.i("change_place_name : " + change_place_name);
                    if(Tap.equals("")){
                        GetApprovalList(Tap,"");
                    }else{
                        GetApprovalList(Tap,select_date);
                    }
                    SetCalenderData();
                }
            });
        });

        binding.changeMember.setOnClickListener(v -> {
            PaySelectMemberActivity psm = new PaySelectMemberActivity();
            psm.show(getSupportFragmentManager(), "PaySelectMemberActivity");
            psm.setOnClickListener(new PaySelectMemberActivity.OnClickListener() {
                @Override
                public void onClick(View v, String user_id, String user_name) {
                    change_member_id = user_id;
                    change_member_name = user_name;
                    if (user_name.equals("전체직원")) {
                        binding.changeMemberTv.setText("전체직원");
                        change_member_id = "";
                        change_member_name = USER_INFO_NAME;
                        shardpref.putString("change_member_id", place_id);
                        shardpref.putString("change_member_name", USER_INFO_ID);
                    } else {
                        binding.changeMemberTv.setText(user_name);
                        change_member_id = user_id;
                        change_member_name = user_name;
                        shardpref.putString("change_member_id", user_id);
                        shardpref.putString("change_member_name", user_name);
                    }
                    dlog.i("change_member_id : " + user_id);
                    dlog.i("change_member_name : " + user_name);
                    if(Tap.equals("")){
                        GetApprovalList(Tap,"");
                    }else{
                        GetApprovalList(Tap,select_date);
                    }
                    SetCalenderData();
                }
            });
        });

        binding.changeIcon.setOnClickListener(v -> {
            if(!chng_icon){
                chng_icon = true;
                binding.noDataTxt.setVisibility(View.GONE);
                binding.tabLayout.setVisibility(View.GONE);
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                binding.selectArea.setVisibility(View.GONE);
                binding.setdate.setText(Year + "년 " + Month + "월 ");
                SetCalenderData();
            }else{
                chng_icon = false;
                binding.tabLayout.setVisibility(View.VISIBLE);
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                binding.selectArea.setVisibility(View.VISIBLE);
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                GetApprovalList(Tap,select_date);
            }
        });

        binding.createCalender.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @Override
            public void onSwipeLeft() {
//                super.onSwipeLeft();
                setCalender(1);
            }

            @Override
            public void onSwipeRight() {
//                super.onSwipeRight();
                setCalender(-1);
            }
        });
    }

    private void setCalender(int state) {
        if (chng_icon) {
            cal.add(Calendar.MONTH, state);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            Day = toDay.substring(8, 10);
            getYMPicker = Year + "-" + Month;
            binding.setdate.setText(Year + "년 " + Month + "월 ");
        } else {
            cal.add(Calendar.DATE, state);
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            Day = toDay.substring(8, 10);
            getYMPicker = Year + "-" + Month;
            binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
        }
        SetCalenderData();
    }

    RetrofitConnect rc = new RetrofitConnect();
    public void GetApprovalList(String state,String approval_date) {
        dlog.i("approval_date: " + approval_date);
        dlog.i("select_date : " + select_date);
        dlog.i("change_place_id : " + change_place_id);
        dlog.i("change_member_id : " + change_member_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSapprovalInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSapprovalInterface api = retrofit.create(TaskSapprovalInterface.class);
        Call<String> call = api.getData(change_place_id, state, approval_date);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetApprovalList function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null) {
//                    Log.e(TAG,"GetWorkStateInfo2 function onSuccess : " + jsonResponse);
                    try {
                        JSONArray Response = new JSONArray(rc.getBase64decode(response.body()));

                        mList = new ArrayList<>();
                        mAdapter = new ApprovalAdapter(mContext, mList, 3, 1);
                        binding.totalApploveList1.setAdapter(mAdapter);
                        binding.totalApploveList1.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");

                        if (Response.length() == 0) {
                            Log.i(TAG, "GET SIZE : " + Response.length());
                            binding.noDataTxt.setVisibility(View.VISIBLE);
                        } else {
                            //본인이 추가한 할일이 점주에게 보이지 안보이는지 회의 필요
                            binding.noDataTxt.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(!change_member_id.isEmpty()){
                                    if(jsonObject.getString("requester_id").equals(change_member_id)){
                                        mAdapter.addItem(new TaskCheckData.TaskCheckData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("state"),
                                                jsonObject.getString("request_task_no"),
                                                jsonObject.getString("requester_id"),
                                                jsonObject.getString("requester_name"),
                                                jsonObject.getString("requester_img_path"),
                                                jsonObject.getString("title"),
                                                jsonObject.getString("contents"),
                                                jsonObject.getString("complete_kind"),
                                                jsonObject.getString("start_time"),
                                                jsonObject.getString("end_time"),
                                                jsonObject.getString("complete_time"),
                                                jsonObject.getString("task_img_path"),
                                                jsonObject.getString("complete_yn"),
                                                jsonObject.getString("incomplete_reason"),
                                                jsonObject.getString("reject_reason"),
                                                jsonObject.getString("task_date"),
                                                jsonObject.getString("request_date"),
                                                jsonObject.getString("approval_date"),
                                                Collections.singletonList(jsonObject.getString("users"))
                                        ));
                                    }
                                }else{
                                    mAdapter.addItem(new TaskCheckData.TaskCheckData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("state"),
                                            jsonObject.getString("request_task_no"),
                                            jsonObject.getString("requester_id"),
                                            jsonObject.getString("requester_name"),
                                            jsonObject.getString("requester_img_path"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("complete_kind"),
                                            jsonObject.getString("start_time"),
                                            jsonObject.getString("end_time"),
                                            jsonObject.getString("complete_time"),
                                            jsonObject.getString("task_img_path"),
                                            jsonObject.getString("complete_yn"),
                                            jsonObject.getString("incomplete_reason"),
                                            jsonObject.getString("reject_reason"),
                                            jsonObject.getString("task_date"),
                                            jsonObject.getString("request_date"),
                                            jsonObject.getString("approval_date"),
                                            Collections.singletonList(jsonObject.getString("users"))
                                    ));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    private void SetCalenderData() {
        mList2.clear();
        getYMPicker = binding.setdate.getText().toString().replace("년 ","-").replace("월 ","-").replace("일","").substring(0,7);
        dlog.i("------SetCalenderData------");
        dlog.i("place_id : " + change_place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("select_date : " + getYMPicker.substring(0,7));
        dlog.i("select_date2 : " + binding.setdate.getText().toString().replace("년 ","-").replace("월 ","-").replace("일","").substring(0,7));
        dlog.i("------SetCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalendersetData api = retrofit.create(WorkCalendersetData.class);
        Call<String> call2 = api.getData(change_place_id, USER_INFO_ID, getYMPicker);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        dlog.i("SetCalenderData function START");
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                dlog.i("GET SIZE : " + Response2.length());
                                GetCalenderList(Year, Month, mList2, getYMPicker);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    mList2.add(new CalendarSetData.CalendarSetData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("task"))
                                    ));
                                }
                                GetCalenderList(Year, Month, mList2, getYMPicker);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }

    ArrayList<String> kind = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    public void GetCalenderList(String Year, String Month, ArrayList<CalendarSetData.CalendarSetData_list> mList2, String date) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalenderInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalenderInterface api = retrofit.create(WorkCalenderInterface.class);
        Call<String> call = api.getData(Year, Month);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetCalenderList function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        dlog.i("onResume place_id :" + place_id);
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
                        dlog.i("onResume getYMPicker :" + getYMPicker);
                        dlog.i("onResume mList2 :" + mList2);
                        try {
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            mList3 = new ArrayList<>();
                            mAdapter2 = new WorkCalenderAdapter(mContext, mList3, mList2, place_id, USER_INFO_ID, select_date, Month);
                            binding.createCalender.setAdapter(mAdapter2);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            dlog.i("SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                dlog.i("GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter2.addItem(new WorkCalenderData.WorkCalenderData_list(
                                            jsonObject.getString("ym"),
                                            jsonObject.getString("Sun"),
                                            jsonObject.getString("Mon"),
                                            jsonObject.getString("Tue"),
                                            jsonObject.getString("Wed"),
                                            jsonObject.getString("Thu"),
                                            jsonObject.getString("Fri"),
                                            jsonObject.getString("Sat")
                                    ));
                                }
                                mAdapter2.notifyDataSetChanged();
                                mAdapter2.setOnItemClickListener(new WorkCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        dlog.i("data :" + data);
                                        try {
                                                kind = new ArrayList<>();
                                                title = new ArrayList<>();
                                                for (int i = 0; i < mList2.size(); i++) {
                                                    if (data.equals(mList2.get(i).getDay().length() == 1 ? "0" + mList2.get(i).getDay() : mList2.get(i).getDay())) {
                                                        JSONArray Response = new JSONArray(mList2.get(i).getTask().toString().replace("[[", "[").replace("]]", "]"));
                                                        for (int i3 = 0; i3 < Response.length(); i3++) {
                                                            JSONObject jsonObject = Response.getJSONObject(i3);
                                                            kind.add(jsonObject.getString("kind"));
                                                            title.add(jsonObject.getString("title"));
                                                        }
                                                    }
                                                }
                                                shardpref.putString("task_date", WorkDay);
                                                dlog.i("WorkDay :" + WorkDay);

                                                shardpref.putString("change_place_id", change_place_id.isEmpty() ? place_id : change_place_id);
                                                shardpref.putString("change_member_id", change_member_id.isEmpty() ? "" : change_member_id);
//                                            Intent intent = new Intent(mContext, TaskListPopActivity.class);
//                                            intent.putStringArrayListExtra("kind", kind);
//                                            intent.putStringArrayListExtra("title", title);
//                                            intent.putExtra("date", data);
//                                            intent.putExtra("yoil", yoil);
//                                            intent.putExtra("write_name","");
//                                            startActivity(intent);
//                                            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                                WorkgotoBottomSheet wgb = new WorkgotoBottomSheet();
                                                wgb.show(getSupportFragmentManager(), "WorkgotoBottomSheet");

                                        } catch (Exception e) {
                                            dlog.i("onItemClick Exception :" + e);
                                        }

                                    }
                                });
//                                if (USER_INFO_AUTH.equals("0")) {
//                                    //관리자일때
//                                    binding.addWorkBtn.setVisibility(View.VISIBLE);
//                                } else {
//                                    //근로자일때때
//                                    binding.addWorkBtn.setVisibility(View.GONE);
//                                }
                            }
                        } catch (JSONException e) {
                            dlog.i("JSONException :" + e);
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }

}
