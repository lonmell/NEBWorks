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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.FragmentStateAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.data.CalendarSetData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.TaskCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.data.WorkGetallData;
import com.krafte.nebworks.dataInterface.TaskSapprovalInterface;
import com.krafte.nebworks.dataInterface.WorkTaskGetallInterface;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

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
    String place_id = "";
    String place_name = "";
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

    FragmentStateAdapter fragmentStateAdapter;
    TaskApprovalFragment thisActivity = this;

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
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", UserCheckData.getInstance().getUser_name());
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        place_id = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
        place_name = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());

        //shardpref Area
        SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
        wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
        gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
        return_page = shardpref.getString("return_page", "");
        shardpref.putString("return_page", "BusinessApprovalActivity");

        fragmentStateAdapter = new FragmentStateAdapter(this, 1, workGotoList2);
        binding.calenderViewpager.setAdapter(fragmentStateAdapter);
        binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
        binding.calenderViewpager.setOffscreenPageLimit(1);

        change_place_id = place_id;
        change_member_id = "";

        Glide.with(this).load(R.raw.basic_loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.loadingView);
        binding.loginAlertText.setVisibility(View.GONE);

        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });
        if(!CheckingData()){
            Toast_Nomal("사용자 데이터를 찾을 수 없습니다\n다시 시도해주세요");
            pm.PlaceList(mContext);
        }
        setBtnEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        GetApprovalList("", select_date);
        SetWorkGotoCalenderData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        shardpref.remove("calendar_year");
        shardpref.remove("calendar_month");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    String select_date = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;

    private void ChangeTap(String i) {
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
        if (i.equals("")) {
            binding.select01tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select01line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        } else if (i.equals("0")) {
            binding.select02tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select02line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        } else if (i.equals("1")) {
            binding.select03tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select03line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        } else if (i.equals("2")) {
            binding.select04tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select04line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }
    }

    private void setBtnEvent() {
        binding.select01.setOnClickListener(v -> {
            ChangeTap("");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap, select_date);
        });
        binding.select02.setOnClickListener(v -> {
            ChangeTap("0");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap, select_date);
        });
        binding.select03.setOnClickListener(v -> {
            ChangeTap("1");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap, select_date);
        });
        binding.select04.setOnClickListener(v -> {
            ChangeTap("2");
            dlog.i("select_date : " + select_date);
            dlog.i("change_place_id : " + change_place_id);
            dlog.i("change_member_id : " + change_member_id);
            GetApprovalList(Tap, select_date);
        });

        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        binding.setdate.setText(toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        Day = toDay.substring(8, 10);
        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
        shardpref.putString("calendar_year", Year);
        shardpref.putString("calendar_month", Month);

        binding.prevDate.setOnClickListener(v -> {
            try {
                if (chng_icon) {
                    cal.add(Calendar.MONTH, -1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                    shardpref.putString("calendar_year", Year);
                    shardpref.putString("calendar_month", Month);
                    int currentPosition = binding.calenderViewpager.getCurrentItem();
                    binding.calenderViewpager.setCurrentItem(currentPosition - 1, true);
                    binding.calenderViewpager.setOffscreenPageLimit(1);
                } else {
                    cal.add(Calendar.DATE, -1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    select_date = Year + "-" + Month + "-" + Day;
                    GetApprovalList(Tap, select_date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            try {
                if (chng_icon) {
                    cal.add(Calendar.MONTH, +1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                    shardpref.putString("calendar_year", Year);
                    shardpref.putString("calendar_month", Month);
                    int currentPosition = binding.calenderViewpager.getCurrentItem();
                    binding.calenderViewpager.setCurrentItem(currentPosition + 1, true);
                    binding.calenderViewpager.setOffscreenPageLimit(1);
                } else {
                    cal.add(Calendar.DATE, +1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    select_date = Year + "-" + Month + "-" + Day;
                    GetApprovalList(Tap, select_date);
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
                if (month < Integer.parseInt(Month)) {
                    cal.add(Calendar.MONTH, -(Integer.parseInt(Month) - (month + 1)));
                    cal.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(Day) - (dayOfMonth)));
                } else {
                    cal.add(Calendar.MONTH, ((month + 1) - Integer.parseInt(Month)));
                    cal.add(Calendar.DAY_OF_MONTH, ((dayOfMonth) - Integer.parseInt(Day)));
                }
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                shardpref.putString("calendar_year", Year);
                shardpref.putString("calendar_month", Month);
                binding.noDataTxt.setVisibility(View.GONE);
                if (chng_icon) {
                    binding.calenderViewpager.setSaveFromParentEnabled(false);
                    fragmentStateAdapter = new FragmentStateAdapter(thisActivity, true, Year, Month, 1);
                    binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                    binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                    binding.calenderViewpager.setOffscreenPageLimit(1);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                } else {
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    GetApprovalList(Tap, select_date);
                }
                getYMPicker = binding.setdate.getText().toString().substring(0, 7);
                select_date = Year + "-" + Month + "-" + Day;
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });

        binding.dateSelect.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });

        FragmentActivity activity = this;
        if (activity == null) {
            throw new IllegalStateException("Activity cannot be null");
        }

        binding.changePlace.setOnClickListener(v -> {
            PaySelectPlaceActivity psp = new PaySelectPlaceActivity();
            psp.show(getSupportFragmentManager(), "PaySelectPlaceActivity");
            psp.setOnClickListener((v1, getplace_id, getplace_name) -> {

                cal = Calendar.getInstance();
                toDay = sdf.format(cal.getTime());
                dlog.i("오늘 :" + toDay);
                binding.setdate.setText(toDay);
                Year = toDay.substring(0, 4);
                Month = toDay.substring(5, 7);
                Day = toDay.substring(8, 10);
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                chng_icon = false;

                change_place_id = getplace_id;
                change_place_name = getplace_name.isEmpty() ? place_name : getplace_name;
                SetWorkGotoCalenderData();

                dlog.i("change_place_id : " + getplace_id);
                dlog.i("change_place_name : " + getplace_name);
                fragmentStateAdapter = new FragmentStateAdapter(thisActivity, 1, workGotoList2);
                binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                binding.calenderViewpager.setOffscreenPageLimit(1);

                binding.changePlaceTv.setText(getplace_name);
                shardpref.putString("change_place_id", getplace_id);
                shardpref.putString("change_place_name", getplace_name);
                dlog.i("change_place_id : " + change_place_id);
                dlog.i("change_place_name : " + change_place_name);

                if (Tap.equals("")) {
                    GetApprovalList(Tap, "");
                } else {
                    GetApprovalList(Tap, select_date);
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
                    if (Tap.equals("")) {
                        GetApprovalList(Tap, "");
                    } else {
                        GetApprovalList(Tap, select_date);
                    }
                }
            });
        });

        binding.changeIcon.setOnClickListener(v -> {
            cal = Calendar.getInstance();
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            Day = toDay.substring(8, 10);

            binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
            if (!chng_icon) {
                chng_icon = true;
                cal = Calendar.getInstance();
                toDay = sdf.format(cal.getTime());
                dlog.i("오늘 :" + toDay);
                binding.setdate.setText(toDay);
                shardpref.putString("FtoDay", toDay);
                Year = toDay.substring(0, 4);
                Month = toDay.substring(5, 7);
                Day = toDay.substring(8, 10);
                getYMPicker = Year + "-" + Month;
                binding.tabLayout.setVisibility(View.GONE);
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                binding.selectArea.setVisibility(View.GONE);
                binding.dateLayout.setVisibility(View.VISIBLE);
                binding.dateSelect.setVisibility(View.GONE);
                binding.noDataTxt.setVisibility(View.GONE);
                binding.setdate.setText(Year + "년 " + Month + "월 ");
            } else {
                chng_icon = false;
                cal = Calendar.getInstance();
                toDay = sdf.format(cal.getTime());
                dlog.i("오늘 :" + toDay);
                binding.setdate.setText(toDay);
                shardpref.putString("FtoDay", toDay);
                Year = toDay.substring(0, 4);
                Month = toDay.substring(5, 7);
                Day = toDay.substring(8, 10);
                getYMPicker = Year + "-" + Month;
                binding.tabLayout.setVisibility(View.VISIBLE);
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                binding.selectArea.setVisibility(View.VISIBLE);
                binding.dateLayout.setVisibility(View.VISIBLE);
                binding.dateSelect.setVisibility(View.GONE);
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                GetApprovalList(Tap, select_date);
            }
        });

        binding.totalApploveList1.setOnTouchListener(new OnSwipeTouchListener(mContext) {
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

        binding.totalApploveList1.setOnTouchListener(new OnSwipeTouchListener(mContext) {
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
    private boolean CheckingData(){
        if(USER_INFO_ID.isEmpty()){
            return false;
        }else if(USER_INFO_NAME.isEmpty()){
            return false;
        }else if(USER_INFO_AUTH.isEmpty()){
            return false;
        }else if(place_id.isEmpty()){
            return false;
        }else if(place_name.isEmpty()){
            return false;
        }else{
            return true;
        }
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
            select_date = Year + "-" + Month + "-" + Day;
            GetApprovalList(Tap, select_date);
        }
    }

    RetrofitConnect rc = new RetrofitConnect();

    public void GetApprovalList(String state, String approval_date) {
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
                    try {
                        JSONArray Response = new JSONArray(rc.getBase64decode(response.body()));

                        mList = new ArrayList<>();
                        mAdapter = new ApprovalAdapter(mContext, mList, 3, 1);
                        binding.totalApploveList1.setAdapter(mAdapter);
                        binding.totalApploveList1.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");

                        if (Response.length() == 0) {
                            Log.i(TAG, "GET SIZE : " + Response.length());
                            if (!chng_icon)
                                binding.noDataTxt.setVisibility(View.VISIBLE);
                        } else {
                            //본인이 추가한 할일이 점주에게 보이지 안보이는지 회의 필요
                            binding.noDataTxt.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if (!change_member_id.isEmpty()) {
                                    if (jsonObject.getString("requester_id").equals(change_member_id)) {
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
                                } else {
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

    ArrayList<WorkGetallData.WorkGetallData_list> workGotoList2 = new ArrayList<>();

    private void SetWorkGotoCalenderData() {
        workGotoList2.clear();
        String USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        String getYMDate = Year + "-" + Month;
        Log.i(TAG, "------SetWorkGotoCalenderData------");
        Log.i(TAG, "place_id : " + change_place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + getYMDate);
        Log.i(TAG, "------SetWorkGotoCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkTaskGetallInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkTaskGetallInterface api = retrofit.create(WorkTaskGetallInterface.class);
        Call<String> call2 = api.getData(change_place_id, USER_INFO_ID, getYMDate, USER_INFO_AUTH);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        Log.i(TAG, "jsonResponse length : " + jsonResponse.length());
                        Log.i(TAG, "jsonResponse : " + jsonResponse);
                        Log.i(TAG, "SetWorkGotoCalenderData function START");
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            fragmentStateAdapter = new FragmentStateAdapter(thisActivity, 1, workGotoList2);
                            binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                            binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                            binding.calenderViewpager.setOffscreenPageLimit(1);
                            if (Response2.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response2.length());
//                                GetWorkGotoCalenderList(year, month, workGotoList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    workGotoList2.add(new WorkGetallData.WorkGetallData_list(
                                            jsonObject.getString("task_month"),
                                            jsonObject.getString("day"),
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("task_date")
                                    ));
                                }
                            }
                            fragmentStateAdapter.notifyDataSetChanged();
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
