package com.krafte.nebworks.ui.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MemberListPopAdapter;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.dataInterface.ScheduleAddInterface;
import com.krafte.nebworks.dataInterface.TaskreuseInputInterface;
import com.krafte.nebworks.dataInterface.TaskreuseUpInterface;
import com.krafte.nebworks.databinding.ActivityPlaceaddworkBinding;
import com.krafte.nebworks.pop.MemberListPop;
import com.krafte.nebworks.pop.RepeatSetPop;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 2022-11-24 방창배 작성 - 자주하는 업무 추가 페이지 - UI는 업무추가 XML 그대로 사용
 * */
public class TaskReuesAddActivity extends AppCompatActivity {
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

    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mem_mList;
    MemberListPopAdapter mem_mAdapter;

    DateCurrent dc = new DateCurrent();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    int total_member_cnt;
    int assignment_kind;
    String WorkTitle = "";
    String WorkContents = "";
    String writer_id = "";
    String WorkDay = "";
    int make_kind = 0;

    //반복설정
    String[] WorkAddSecond;


    //업무 종류
    String TaskKind = "1";
    //완료방법
    String complete_kind = "";

    //시작시간
    String start_time = "-99";

    //마감시간
    String end_time = "-99";
    String EndTime02 = "-99";

    String message = "업무가 배정되었습니다.";
    Drawable icon_on, icon_off;
    String searchDate = "";

    String Sun = "0", Mon = "0", Tue = "0", Wed = "0", Thu = "0", Fri = "0", Sat = "0";
    String toDay = "";
    String return_page = "";
    List<String> getYoil = new ArrayList<>();

    boolean NeedReportTF = false;

    int a = 0;
    List<String> inmember = new ArrayList<>();

    int timeStartHour = 0;
    int timeStartMin = 0;

    int timeEndHour = 0;
    int timeEndMin = 0;

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

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            //Singleton Area
            place_id            = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
            place_name          = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());
            place_owner_id      = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());
            place_owner_name    = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_name());
            place_address       = shardpref.getString("place_address", PlaceCheckData.getInstance().getPlace_address());
            place_latitude      = shardpref.getString("place_latitude", PlaceCheckData.getInstance().getPlace_latitude());
            place_longitude     = shardpref.getString("place_longitude", PlaceCheckData.getInstance().getPlace_longitude());
            place_start_time    = shardpref.getString("place_start_time", PlaceCheckData.getInstance().getPlace_start_time());
            place_end_time      = shardpref.getString("place_end_time", PlaceCheckData.getInstance().getPlace_end_time());
            place_img_path      = shardpref.getString("place_img_path", PlaceCheckData.getInstance().getPlace_img_path());
            place_start_date    = shardpref.getString("place_start_date", PlaceCheckData.getInstance().getPlace_start_date());
            place_created_at    = shardpref.getString("place_created_at", PlaceCheckData.getInstance().getPlace_created_at());
            return_page         = shardpref.getString("return_page", ReturnPageData.getInstance().getPage());

            USER_INFO_ID        = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
            USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");

            //shardpref Area
            shardpref.putInt("SELECT_POSITION_sub", 1);
            make_kind = shardpref.getInt("make_kind", 0);

            //캘린더에서 넘어온 경우 - 선택한 날짜를 가져옴
            searchDate = shardpref.getString("searchDate", "");

            channelId1 = shardpref.getBoolean("channelId1", false);
            channelId2 = shardpref.getBoolean("channelId2", false);
            WorkAddSecond = new String[7];

            //수정할때 필요
            task_no = shardpref.getString("task_no", "0");
            icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
            icon_on = getApplicationContext().getResources().getDrawable(R.drawable.ic_full_round_check);
            dlog.i("task_no : " + task_no);
            setBtnEvent();
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            dlog.i("TaskReuesAddActivity toDay : " + toDay);
            WorkDay = toDay;
            binding.storeName.setText(place_name);
            binding.memberSelectArea.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String change_place_id = "";
    String change_place_name = "";
    String change_place_owner_id = "";
    boolean SELECTDATE = false; //false - 01 / true - 02
    boolean SELECTTIME = false; //false - 01 / true - 02
    String starttime = "";
    String endtime = "";

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });
        binding.placeChangeArea.setOnClickListener(v -> {
            PlaceListBottomSheet plb = new PlaceListBottomSheet();
            plb.show(getSupportFragmentManager(), "PlaceListBottomSheet");
            plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                change_place_id = place_id;
                change_place_name = place_name;
                change_place_owner_id = place_owner_id;
                shardpref.putString("change_place_id", place_id);
                shardpref.putString("change_place_name", place_name);
                shardpref.putString("change_place_owner_id", place_owner_id);
                binding.storeName.setText(place_name);
            });
        });

        binding.selectRepeatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, RepeatSetPop.class);
            shardpref.putString("SET_TASK_TIME_VALUE", "3");
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
            if (!NeedReportTF) {
                NeedReportTF = true;
                binding.needReport.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.reportTv.setTextColor(Color.parseColor("#ffffff"));
                binding.reportVisible.setVisibility(View.VISIBLE);
                binding.reportBtn.setBackgroundResource(R.drawable.task_check_white);
                TaskKind = "1";
                binding.select01Box.setBackgroundResource(R.drawable.default_select_on_round);
                binding.select01.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.select01Img.setBackgroundResource(R.drawable.task_check_blue);
                binding.select02Box.setBackgroundResource(R.drawable.default_select_on_round_white);
                binding.select02.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                binding.select02Img.setBackgroundResource(R.drawable.task_check_none);
            } else {
                NeedReportTF = false;
                binding.needReport.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.reportTv.setTextColor(Color.parseColor("#000000"));
                binding.reportBtn.setBackgroundResource(R.drawable.task_check_none);
                binding.reportVisible.setVisibility(View.GONE);

                binding.select01Box.setBackgroundResource(R.drawable.default_select_on_round_white);
                binding.select01.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                binding.select01Img.setBackgroundResource(R.drawable.task_check_none);

                binding.select02Box.setBackgroundResource(R.drawable.default_select_on_round_white);
                binding.select02.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                binding.select02Img.setBackgroundResource(R.drawable.task_check_none);
            }
        });

        binding.select01Box.setOnClickListener(v -> {
            TaskKind = "1";
            dlog.i("select01Box click [TaskKind : " + TaskKind + "]");
            binding.select01Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select01.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select01Img.setBackgroundResource(R.drawable.task_check_blue);
            binding.select02Box.setBackgroundResource(R.drawable.default_select_on_round_white);
            binding.select02.setTextColor(Color.parseColor("#000000"));
            binding.select02Img.setBackgroundResource(R.drawable.task_check_none);
        });
        binding.select02Box.setOnClickListener(v -> {
            TaskKind = "0";
            dlog.i("select02Box click [TaskKind : " + TaskKind + "]");
            binding.select01Box.setBackgroundResource(R.drawable.default_select_on_round_white);
            binding.select01.setTextColor(Color.parseColor("#000000"));
            binding.select01Img.setBackgroundResource(R.drawable.task_check_none);
            binding.select02Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select02.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.select02Img.setBackgroundResource(R.drawable.task_check_blue);
        });
        binding.bottomBtnBox.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            if (SaveCheck()) {
                SaveAddWork();
            }
        });

        //근무날짜 입력 영역
        binding.inputDateBox01.setOnClickListener(v -> {
            SELECTDATE = false;
            binding.inputDateBox01.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputDateBox02.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputTimeBox01.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputTimeBox02.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker.setVisibility(View.GONE);
            binding.cvCalendar.setVisibility(View.VISIBLE);
        });
        binding.inputDateBox02.setOnClickListener(v -> {
            SELECTDATE = true;
            binding.inputDateBox01.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputDateBox02.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputTimeBox01.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputTimeBox02.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker.setVisibility(View.GONE);
            binding.cvCalendar.setVisibility(View.VISIBLE);
        });

        binding.inputTimeBox01.setOnClickListener(v -> {
            SELECTTIME = false;
            binding.inputTimeBox01.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputTimeBox02.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputDateBox01.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputDateBox02.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker.setVisibility(View.VISIBLE);
            binding.cvCalendar.setVisibility(View.GONE);
        });
        binding.inputTimeBox02.setOnClickListener(v -> {
            SELECTTIME = true;
            binding.inputTimeBox01.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputTimeBox02.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputDateBox01.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputDateBox02.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker.setVisibility(View.VISIBLE);
            binding.cvCalendar.setVisibility(View.GONE);
        });
        //시간입력
        cal = Calendar.getInstance();
        SimpleDateFormat getThisYear = new SimpleDateFormat("yyyy년");
        SimpleDateFormat default_date = new SimpleDateFormat("MM월 dd일 (EE)");
        SimpleDateFormat default_time = new SimpleDateFormat("a HH:mm", Locale.KOREA);
        String getYear = getThisYear.format(cal.getTime());
        binding.inputTime01.setText(default_time.format(cal.getTime()));
        binding.inputTime02.setText(default_time.format(cal.getTime()));
        binding.inputDate01.setText(default_date.format(cal.getTime()));
        binding.inputDate02.setText(default_date.format(cal.getTime()));

        String H = dc.GET_TIME.substring(0, 2);
        String M = dc.GET_TIME.substring(2, 4);

        starttime = (Integer.parseInt(H) < 12 ? "오전" : "오후") + " " + (H.trim().length() == 1 ? "0" + H : H) + ":" + (M.trim().length() == 1 ? "0" + M : M);
        endtime = ((Integer.parseInt(H) + 1) < 12 ? "오전" : "오후") + " " + String.valueOf(Integer.parseInt((H.trim().length() == 1 ? "0" + H : H)) + 1) + ":" + (M.trim().length() == 1 ? "0" + M : M);
        binding.inputTime01.setText(starttime);
        binding.inputTime02.setText(endtime);
        binding.timeSetpicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String HOUR = String.valueOf(hourOfDay == 0 ? 12 : hourOfDay);
                String MIN = String.valueOf(minute);
                binding.timeSetpicker.clearFocus();
                if (!SELECTTIME) {
                    starttime = HOUR + ":" + MIN;
                    binding.inputTime01.setText((hourOfDay < 12 ? "오전" : "오후") + " " + (HOUR.length() == 1 ? "0" + HOUR : HOUR) + ":" + (MIN.length() == 1 ? "0" + MIN : MIN));
                } else {
                    endtime = HOUR + ":" + MIN;
                    binding.inputTime02.setText((hourOfDay < 12 ? "오전" : "오후") + " " + (HOUR.length() == 1 ? "0" + HOUR : HOUR) + ":" + (MIN.length() == 1 ? "0" + MIN : MIN));
                }
            }
        });
        //날짜입력
        binding.cvCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                cal = Calendar.getInstance();
                SimpleDateFormat calendar_get_format_year = new SimpleDateFormat("yyyy년");
                SimpleDateFormat calendar_get_format_month = new SimpleDateFormat("MM월 dd일");
                SimpleDateFormat calendar_get_yoil = new SimpleDateFormat("EE");
                SimpleDateFormat save_date = new SimpleDateFormat("yyyy-MM-dd");
                String Year = calendar_get_format_year.format(date.getDate());
                String month = calendar_get_format_month.format(date.getDate());
                String yoil = calendar_get_yoil.format(date.getDate());

                if (Year.equals(getYear)) {
                    binding.inputDate01.setText(month + " " + "(" + yoil + ")");
                    binding.inputDate02.setText(month + " " + "(" + yoil + ")");
                } else {
                    binding.inputDate01.setText(Year + "\n" + month + " " + "(" + yoil + ")");
                    binding.inputDate02.setText(Year + "\n" + month + " " + "(" + yoil + ")");
                }
                getStartDate = save_date.format(date.getDate());
                getEndDate = save_date.format(date.getDate());

                dlog.i("binding.cvCalendar CalendarDay : " + Year + "\n" + month + "," + yoil);
            }
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
    InputMethodManager imm;

    Calendar cal;
    String format = "yyyy-MM";
    SimpleDateFormat sdf = new SimpleDateFormat(format);

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        try {
            //데이터 입력세팅
            imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            String thumnail_url = shardpref.getString("thumnail_url", "");
            String name = shardpref.getString("name", "");
            String writer_id = shardpref.getString("writer_id", "");
            SET_TASK_TIME_VALUE = shardpref.getString("SET_TASK_TIME_VALUE", "-1");

            //반복요일 세팅
            String yoillist_String = "";
            yoillist.clear();
            yoillist_String = shardpref.getString("yoillist", "").replace("[", "").replace("]", "").replace("  ", "");
            overdate = shardpref.getString("overdate", "");
            yoillist.addAll(Arrays.asList(yoillist_String.replace("[", "").replace("]", "").replace("  ", "").split(",")));
            dlog.i("yoillist : " + yoillist);

            picker_year = shardpref.getString("picker_year", "00");
            picker_month = shardpref.getString("picker_month", "00");
            picker_day = shardpref.getString("picker_day", "00");
            input_pop_time = shardpref.getString("input_pop_time", "");


            if (!String.valueOf(yoillist).equals("[]")) {
                RepeatCheck = true;
                binding.repeatBtn.setBackgroundResource(R.drawable.task_check_white);
                binding.selectRepeatBtn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.repeatTv.setTextColor(Color.parseColor("#ffffff"));
            } else {
                RepeatCheck = false;
                binding.repeatBtn.setBackgroundResource(R.drawable.task_check_none);
            }

            //캘린더 세팅
            binding.cvCalendar.setTitleFormatter(new TitleFormatter() {
                @Override
                public CharSequence format(CalendarDay day) {
                    cal = Calendar.getInstance();
                    SimpleDateFormat calendar_view_format = new SimpleDateFormat("yyyy년 MM월");
                    String monthAndYear = calendar_view_format.format(day.getDate());
                    return monthAndYear;
                }
            });

            //추가된 직원
            item_user_id = new ArrayList<>();
            item_user_name = new ArrayList<>();
            item_user_img = new ArrayList<>();
            item_user_jikgup = new ArrayList<>();

            String getuser_id = shardpref.getString("item_user_id", "").replace("  ", "").replace("[", "").replace("]", "");
            String getuser_name = shardpref.getString("item_user_name", "").replace("  ", "").replace("[", "").replace("]", "");
            String getuser_img = shardpref.getString("item_user_img", "").replace("  ", "").replace("[", "").replace("]", "");
            String getuser_position = shardpref.getString("item_user_position", "").replace("  ", "").replace("[", "").replace("]", "");
            item_user_id.removeAll(item_user_id);
            item_user_name.removeAll(item_user_name);
            item_user_img.removeAll(item_user_img);
            item_user_jikgup.removeAll(item_user_jikgup);

            if (!getuser_id.isEmpty()) {
                item_user_id.addAll(Arrays.asList(getuser_id.split(",")));
            }
            if (!getuser_name.isEmpty()) {
                item_user_name.addAll(Arrays.asList(getuser_name.split(",")));
            }
            if (!getuser_img.isEmpty()) {
                item_user_img.addAll(Arrays.asList(getuser_img.split(",")));
            }
            if (!getuser_position.isEmpty()) {
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
            mem_mAdapter = new MemberListPopAdapter(mContext, mem_mList, 1);
            binding.selectMemberList.setAdapter(mem_mAdapter);
            binding.selectMemberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

            if (getuser_id.isEmpty() || getuser_id.equals("0")) {
                dlog.i("getuser_id : " + getuser_id);
                dlog.i("getuser_name : " + getuser_name);
                dlog.i("getuser_img : " + getuser_img);
                dlog.i("getuser_position : " + getuser_position);
                item_user_id.clear();
                item_user_name.clear();
                item_user_img.clear();
                item_user_jikgup.clear();
            } else {
                for (int i = 0; i < item_user_id.size(); i++) {
                    dlog.i("item_user_id : " + item_user_id.get(i));
                    dlog.i("item_user_name : " + item_user_name.get(i));
                    dlog.i("item_user_img : " + item_user_img.get(i));
                    dlog.i("item_user_jikgup : " + item_user_jikgup.get(i));
                    if (!item_user_id.get(i).equals("0")) {
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
                                "",
                                ""
                        ));
                    }

                }
                mem_mAdapter.notifyDataSetChanged();
            }
            //추가된 직원

            String return_page = shardpref.getString("return_page", "");
            if ((!task_no.equals("0") || return_page.equals("task_reuse")) && a == 0) {
                a++;
                getTaskContents();
            }
            binding.inputWorktitle.clearFocus();
            binding.inputWorkcontents.clearFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTaskContents() {
        dlog.i("-----getTaskContents START-----");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        task_no = shardpref.getString("task_no", "0");
        writer_id = shardpref.getString("writer_id", "0");
        WorkTitle = shardpref.getString("title", "0");
        WorkContents = shardpref.getString("contents", "0");
        TaskKind = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
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

        String img_path = shardpref.getString("img_path", "0");
        String complete_yn = shardpref.getString("complete_yn", "n");// y:완료, n:미완료
        String incomplete_reason = shardpref.getString("incomplete_reason", "n"); // n: 미완료 사유

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

        message = "수정된 업무가 있습니다.";
        binding.workSave.setText("업무 수정");
        binding.inputWorktitle.setText(WorkTitle);
        binding.inputWorkcontents.setText(WorkContents);

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
        dlog.i("getYoil : " + getYoil);
        shardpref.putString("yoillist", String.valueOf(getYoil));
        String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        if (!String.valueOf(getYoil).equals("[]")) {
            RepeatCheck = true;
            binding.repeatBtn.setBackgroundResource(R.drawable.task_check_white);
            binding.selectRepeatBtn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.repeatTv.setTextColor(Color.parseColor("#ffffff"));
        } else {
            dlog.i("input_pop_time : " + input_pop_time);
            dlog.i("SET_TASK_TIME_VALUE : " + SET_TASK_TIME_VALUE);
            RepeatCheck = false;
            binding.repeatBtn.setBackgroundResource(R.drawable.task_check_none);

            if (return_page.equals("task_reuse")) {
                shardpref.putString("picker_year", today.substring(0, 4));
                shardpref.putString("picker_month", today.substring(5, 7));
                shardpref.putString("picker_day", today.substring(8, 10));
            } else {
                shardpref.putString("picker_year", start_time.substring(0, 4));
                shardpref.putString("picker_month", start_time.substring(5, 7));
                shardpref.putString("picker_day", start_time.substring(8, 10));
            }
        }

        mem_mList = new ArrayList<>();
        mem_mAdapter = new MemberListPopAdapter(mContext, mem_mList, 1);
        binding.selectMemberList.setAdapter(mem_mAdapter);
        binding.selectMemberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));


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
                    "",
                    ""
            ));
        }
        mem_mAdapter.notifyDataSetChanged();

        if (!TaskKind.isEmpty()) {
            dlog.i("if(!TaskKind) : " + TaskKind);
            NeedReportTF = true;
            binding.needReport.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.reportBtn.setBackgroundResource(R.drawable.task_check_white);
            binding.reportTv.setTextColor(Color.parseColor("#ffffff"));
            binding.reportVisible.setVisibility(View.VISIBLE);
            if (TaskKind.equals("0")) {
                TaskKind = "0";
                binding.select01Box.setBackgroundResource(R.drawable.default_select_on_round_white);
                binding.select01.setTextColor(Color.parseColor("#000000"));
                binding.select01Img.setBackgroundResource(R.drawable.task_check_none);
                binding.select02Box.setBackgroundResource(R.drawable.default_select_on_round);
                binding.select02.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.select02Img.setBackgroundResource(R.drawable.task_check_blue);
            } else if (TaskKind.equals("1")) {
                TaskKind = "1";
                binding.select01Box.setBackgroundResource(R.drawable.default_select_on_round);
                binding.select01.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.select01Img.setBackgroundResource(R.drawable.task_check_blue);
                binding.select02Box.setBackgroundResource(R.drawable.default_select_on_round_white);
                binding.select02.setTextColor(Color.parseColor("#000000"));
                binding.select02Img.setBackgroundResource(R.drawable.task_check_none);
            }

            // 할일 시간 세팅
            if (getYoil.isEmpty()) { // 반복 업무 X
                String[] startTimeSplit = start_time.split(" ");
                String[] splitStartTime = startTimeSplit[1].split(":");
                int startHour = Integer.parseInt(splitStartTime[0]);
                int startMin = Integer.parseInt(splitStartTime[1]);
                timeStartHour = startHour;
                timeStartMin = startMin;
                if (Integer.parseInt(splitStartTime[0]) < 12) {
                    binding.inputTime01.setText(String.format("오전 %02d:%02d", startHour, startMin));
                } else {
                    binding.inputTime01.setText(String.format("오후 %02d:%02d", startHour, startMin));
                }

                String[] endTimeSplit = end_time.split(" ");
                String[] splitEndTime = endTimeSplit[1].split(":");
                int endHour = Integer.parseInt(splitEndTime[0]);
                int endMin = Integer.parseInt(splitEndTime[1]);
                timeEndHour = endHour;
                timeEndMin = endMin;
                if (Integer.parseInt(splitEndTime[0]) < 12) {
                    binding.inputTime02.setText(String.format("오전 %02d:%02d", endHour, endMin));
                } else {
                    binding.inputTime02.setText(String.format("오후 %02d:%02d", endHour, endMin));
                }
            } else { // 반복 업무 O
                String[] splitStartTime = start_time.split(":");
                int startHour = Integer.parseInt(splitStartTime[0]);
                int startMin = Integer.parseInt(splitStartTime[1]);
                timeStartHour = startHour;
                timeStartMin = startMin;
                if (Integer.parseInt(splitStartTime[0]) < 12) {
                    binding.inputTime01.setText(String.format("오전 %02d:%02d", startHour, startMin));
                } else {
                    binding.inputTime01.setText(String.format("오후 %02d:%02d", startHour, startMin));
                }

                String[] splitEndTime = end_time.split(":");
                int endHour = Integer.parseInt(splitEndTime[0]);
                int endMin = Integer.parseInt(splitEndTime[1]);
                timeEndHour = endHour;
                timeEndMin = endMin;
                if (Integer.parseInt(splitEndTime[0]) < 12) {
                    binding.inputTime02.setText(String.format("오전 %02d:%02d", endHour, endMin));
                } else {
                    binding.inputTime02.setText(String.format("오후 %02d:%02d", endHour, endMin));
                }
            }
        }
        dlog.i("-----getTaskContents END-----");
    }

    public interface OnClickListener {
        void onClick();
    }

    private TaskAddWorkActivity.OnClickListener mListener = null;

    public void setOnClickListener(TaskAddWorkActivity.OnClickListener listener) {
        this.mListener = listener;
    }

    //업무 저장(추가)
    String[] splitStartSplit;
    String[] splitEndSplit;

    String getStartDate = "";
    String getEndDate = "";
    RetrofitConnect rc = new RetrofitConnect();
    private void SaveAddWork() {
        dlog.i("------------------SaveAddWork------------------");
        if (RepeatCheck) {
            start_time = starttime.replace("오전", "").replace("오후", "");
            end_time = endtime.replace("오전", "").replace("오후", "");
            splitStartSplit = start_time.split(":");
            splitEndSplit = end_time.split(":");
            if (splitStartSplit[0].equals("0") || splitStartSplit[0].equals("00")) {
                splitStartSplit[0] = "24";
            }
            if (splitEndSplit[0].equals("0") || splitEndSplit[0].equals("00")) {
                splitEndSplit[0] = "24";
            }
            start_time = String.format("%02d:%02d", Integer.parseInt(splitStartSplit[0]), Integer.parseInt(splitStartSplit[1]));
            end_time = String.format("%02d:%02d", Integer.parseInt(splitEndSplit[0]), Integer.parseInt(splitEndSplit[1]));
        } else {
            start_time = getStartDate + " " + starttime.replace("오전", "").replace("오후", "");
            end_time = getEndDate + " " + endtime.replace("오전", "").replace("오후", "");
            if (getStartDate.isEmpty() || getEndDate.isEmpty()) {
                splitStartSplit = start_time.split(":");
                splitEndSplit = end_time.split(":");
                if (splitStartSplit[0].equals("0") || splitStartSplit[0].equals("00")) {
                    splitStartSplit[0] = "24";
                }
                if (splitEndSplit[0].equals("0") || splitEndSplit[0].equals("00")) {
                    splitEndSplit[0] = "24";
                }
                start_time = String.format("%02d:%02d", Integer.parseInt(splitStartSplit[0]), Integer.parseInt(splitStartSplit[1]));
                end_time = String.format("%02d:%02d", Integer.parseInt(splitEndSplit[0]), Integer.parseInt(splitEndSplit[1]));
            } else {
                splitStartSplit = start_time.split(" ");
                String[] splitStartTime = splitStartSplit[1].split(":");
                splitEndSplit = end_time.split(" ");
                String[] splitEndTime = splitEndSplit[1].split(":");
                if (splitStartTime[0].equals("0") || splitStartTime[0].equals("00")) {
                    splitStartTime[0] = "24";
                }
                if (splitEndTime[0].equals("0") || splitEndTime[0].equals("00")) {
                    splitEndTime[0] = "24";
                }
                start_time = splitStartSplit[0] + " " + String.format("%02d:%02d", Integer.parseInt(splitStartTime[0]), Integer.parseInt(splitStartTime[1]));
                end_time = splitEndSplit[0] + " " + String.format("%02d:%02d", Integer.parseInt(splitEndTime[0]), Integer.parseInt(splitEndTime[1]));
            }
        }

        dlog.i("make_kind : " + make_kind);
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
        dlog.i("overdate : " + overdate);

        if (make_kind == 2) {
            if (task_no.equals("0")) {
                @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                    runOnUiThread(() -> {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(TaskreuseInputInterface.URL)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .build();
                        TaskreuseInputInterface api = retrofit.create(TaskreuseInputInterface.class);
                        //--반복 요일
                        dlog.i("------------------SaveAddWork12------------------");
                        Call<String> call = api.getData(place_id, USER_INFO_ID, WorkTitle, WorkContents, TaskKind
                                , toDay, start_time, end_time
                                , Sun, Mon, Tue, Wed, Thu, Fri, Sat,overdate);
                        call.enqueue(new Callback<String>() {
                            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                //반복되는 요일을 일시 초기화 해준다
                                dlog.e("SaveAddWork function START");
                                if (response.isSuccessful() && response.body() != null) {
                                    String jsonResponse = rc.getBase64decode(response.body());
                                    dlog.i("jsonResponse length : " + jsonResponse.length());
                                    dlog.i("jsonResponse : " + jsonResponse);
                                    if (jsonResponse.replace("\"", "").equals("success") || jsonResponse.replace("\"", "").equals("success")) {
                                        dlog.i("assignment_kind : " + assignment_kind);
                                        BtnOneCircleFun(true);
//                                        pm.TaskReuse(mContext);
                                        RemoveShared();
                                        finish();
                                    } else if (jsonResponse.replace("\"", "").equals("fail") || jsonResponse.replace("\"", "").equals("fail")) {
                                        BtnOneCircleFun(true);
                                        Toast.makeText(mContext, "동일한 업무가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        BtnOneCircleFun(true);
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

                        //--반복 요일
                        dlog.i("------------------UpdateWork------------------");
                        Call<String> call = api.getData(task_no, place_id, USER_INFO_ID, "0", WorkTitle, WorkContents, TaskKind
                                , WorkDay, start_time, end_time
                                , Sun, Mon, Tue, Wed, Thu, Fri, Sat);
                        call.enqueue(new Callback<String>() {
                            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                //반복되는 요일을 일시 초기화 해준다
                                dlog.e("UpdateWork function START");
                                if (response.isSuccessful() && response.body() != null) {
                                    String jsonResponse = rc.getBase64decode(response.body());
                                    dlog.i("jsonResponse length : " + jsonResponse.length());
                                    dlog.i("jsonResponse : " + jsonResponse);
                                    if (jsonResponse.replace("\"", "").equals("success") || jsonResponse.replace("\"", "").equals("success")) {
                                        dlog.i("assignment_kind : " + assignment_kind);
                                        pm.TaskReuse(mContext);
                                        dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
                                        RemoveShared();

                                    } else if (jsonResponse.replace("\"", "").equals("fail") || jsonResponse.replace("\"", "").equals("fail")) {
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
        } else if (make_kind == 4) {
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                runOnUiThread(() -> {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ScheduleAddInterface.URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                    ScheduleAddInterface api = retrofit.create(ScheduleAddInterface.class);
                    Call<String> call = api.getData(place_id, USER_INFO_ID, WorkTitle, WorkContents
                            , WorkDay, start_time, end_time
                            , Sun, Mon, Tue, Wed, Thu, Fri, Sat, "");
                    call.enqueue(new Callback<String>() {
                        @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            dlog.e("SaveAddWork function START make_kind =" + make_kind);
                            if (response.isSuccessful() && response.body() != null) {
                                String jsonResponse = rc.getBase64decode(response.body());
                                dlog.i("jsonResponse length : " + jsonResponse.length());
                                dlog.i("jsonResponse : " + jsonResponse);
                                if (jsonResponse.replace("\"", "").equals("success") || jsonResponse.replace("\"", "").equals("success")) {
                                    dlog.i("assignment_kind : " + assignment_kind);
                                    pm.CalenderBack(mContext);
                                    dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);

                                } else if (jsonResponse.replace("\"", "").equals("fail") || jsonResponse.replace("\"", "").equals("fail")) {
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

    private boolean SaveCheck() {
        String getYoil = shardpref.getString("yoillist", "").replace("  ", "").replace("[", "").replace("]", "");
        dlog.i("yoillist : " + yoillist);
        Sun = "0";
        Mon = "0";
        Tue = "0";
        Wed = "0";
        Thu = "0";
        Fri = "0";
        Sat = "0";//한번 초기화
        for (String str : getYoil.split(",")) {
            if (str.trim().equals("일")) {
                Sun = "1";
            } else if (str.trim().equals("월")) {
                Mon = "1";
            } else if (str.trim().equals("화")) {
                Tue = "1";
            } else if (str.trim().equals("수")) {
                Wed = "1";
            } else if (str.trim().equals("목")) {
                Thu = "1";
            } else if (str.trim().equals("금")) {
                Fri = "1";
            } else if (str.trim().equals("토")) {
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
        start_time = binding.inputTime01.getText().toString();
        end_time = binding.inputTime02.getText().toString();
        overdate = overdate.replace("년 ", "-").replace("월 ", "-").replace("일", "").trim();

        String[] splitStart = binding.inputTime01.getText().toString().split(" ");
        String[] splitEnd = binding.inputTime02.getText().toString().split(" ");

        String[] splitStartTime = start_time.split(" ")[1].split(":");
        String[] splitEndTime = end_time.split(" ")[1].split(":");
        String[] splitStartDate = binding.inputDate01.getText().toString().replace("월", "").replace("일", "").split(" ");
        String[] splitEndDate = binding.inputDate02.getText().toString().replace("월", "").replace("일", "").split(" ");

        if (WorkTitle.equals("")) {
            dlog.i("WorkTitle");
            BtnOneCircleFun(true);
            Toast.makeText(this, "할일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (WorkContents.isEmpty()) {
            dlog.i("WorkContents");
            BtnOneCircleFun(true);
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TaskKind.isEmpty() && make_kind == 1) {
            BtnOneCircleFun(true);
            Toast.makeText(this, "완료방법을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TaskKind.isEmpty() && make_kind == 4) {
            return true;
        } else if (WorkDay.isEmpty()) {
            BtnOneCircleFun(true);
            Toast.makeText(this, "업무날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (start_time.equals("-99")) {
            dlog.i("StarTime");
            BtnOneCircleFun(true);
            Toast.makeText(this, "시작시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (end_time.equals("-99")) {
            dlog.i("StarTime");
            BtnOneCircleFun(true);
            Toast.makeText(this, "마감시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(splitStartTime[0]) > Integer.parseInt(splitEndTime[0]) || Integer.parseInt(splitStartTime[1]) > Integer.parseInt(splitEndTime[1])) {
            if (Integer.parseInt(splitStartDate[0]) < Integer.parseInt(splitEndDate[0]) || Integer.parseInt(splitStartDate[1]) < Integer.parseInt(splitEndDate[1])) {
                return true;
            } else {
                BtnOneCircleFun(true);
                Toast_Nomal("시작 시간이가 종료 시간보다 큽니다. 다시 설정해주세요.");
                return false;
            }
        } else if (Integer.parseInt(splitStartDate[0]) > Integer.parseInt(splitEndDate[0]) || Integer.parseInt(splitStartDate[1]) > Integer.parseInt(splitEndDate[1])) {
            BtnOneCircleFun(true);
            Toast_Nomal("시작 날짜가 종료 날짜보다 큽니다. 다시 설정해주세요.");
            return false;
        } else {
            dlog.i("제목2 : " + WorkTitle);
            dlog.i("내용 : " + WorkContents);
            dlog.i("완료방법2 : " + complete_kind);
            dlog.i("마감시간2 : " + end_time + ":" + EndTime02);
            dlog.i("반복 : " + getYoil.toString().replace("[", "").replace("]", ""));
            dlog.i("작업날짜 : " + WorkDay);

            return true;
        }
    }

    private void RemoveShared() {
        shardpref.remove("return_page");
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

    private void BtnOneCircleFun(boolean tf){
        binding.bottomBtnBox.setClickable(tf);
        binding.bottomBtnBox.setEnabled(tf);
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
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        RemoveShared();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        pm.TaskReuse(mContext);
    }
}
