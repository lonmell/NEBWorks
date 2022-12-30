package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MemberListPopAdapter;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.ScheduleAddInterface;
import com.krafte.nebworks.dataInterface.TaskInputInterface;
import com.krafte.nebworks.dataInterface.TaskUpdateInterface;
import com.krafte.nebworks.databinding.ActivityPlaceaddworkBinding;
import com.krafte.nebworks.pop.MemberListPop;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.pop.RepeatSetPop;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.json.JSONArray;
import org.json.JSONException;

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
 * 해당 매장의 할일 추가
 * */
public class TaskAddWorkActivity extends AppCompatActivity {
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
    String user_id = "";
    String usersn = "";
    String usersimg = "";
    String usersjikgup = "";
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

            //캘린더에서 넘어온 경우 - 선택한 날짜를 가져옴
            searchDate = shardpref.getString("searchDate", "");

            channelId1 = shardpref.getBoolean("channelId1", false);
            channelId2 = shardpref.getBoolean("channelId2", false);
            WorkAddSecond = new String[7];

            //수정할때 필요
            task_no = shardpref.getString("task_no", "0");
            icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
            icon_on = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_on);
            //--처음에는 공통임무로 설정된채로 시작
            if (task_no.equals("0")) {
                user_id = "";
            }
            dlog.i("task_no : " + task_no);
            setBtnEvent();
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            WorkDay = toDay;
            binding.storeName.setText(place_name);
            //권한에 따른 ui 변동
            if (USER_INFO_AUTH.equals("1")) {
                //근로자일때 일부 UI 안보이게 해야함
                binding.memberSelectArea.setVisibility(View.GONE);
            }
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
                binding.needReport.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.reportTv.setTextColor(Color.parseColor("#ffffff"));
                binding.reportVisible.setVisibility(View.VISIBLE);
                TaskKind = "1";
                binding.select01Box.setBackgroundResource(R.drawable.default_select_on_round);
                binding.select01.setTextColor(Color.parseColor("#ffffff"));
            } else {
                NeedReportTF = false;
                binding.needReport.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.reportTv.setTextColor(Color.parseColor("#000000"));
                binding.reportVisible.setVisibility(View.GONE);
            }
        });

        binding.select01Box.setOnClickListener(v -> {
            TaskKind = "1";
            dlog.i("select01Box click [TaskKind : " + TaskKind + "]");
            binding.select01Box.setBackgroundResource(R.drawable.default_select_on_round);
            binding.select01.setTextColor(Color.parseColor("#ffffff"));
            binding.select02Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select02.setTextColor(Color.parseColor("#000000"));
        });
        binding.select02Box.setOnClickListener(v -> {
            TaskKind = "0";
            dlog.i("select02Box click [TaskKind : " + TaskKind + "]");
            binding.select01Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select01.setTextColor(Color.parseColor("#000000"));
            binding.select02Box.setBackgroundResource(R.drawable.default_select_on_round);
            binding.select02.setTextColor(Color.parseColor("#ffffff"));
        });
        binding.bottomBtnBox.setOnClickListener(v -> {
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

        binding.timeSetpicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String HOUR = String.valueOf(hourOfDay);
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
    String format2 = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
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
                binding.repeatBtn.setBackgroundResource(R.drawable.ic_service_white);
                binding.selectRepeatBtn.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.repeatTv.setTextColor(Color.parseColor("#ffffff"));
                binding.inputDateBox01.setVisibility(View.GONE);
                binding.inputDateBox02.setVisibility(View.GONE);
            } else {
                RepeatCheck = false;
                binding.repeatBtn.setBackgroundResource(R.drawable.resize_service_off);
                binding.inputDateBox01.setVisibility(View.VISIBLE);
                binding.inputDateBox02.setVisibility(View.VISIBLE);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    String today = "";
    private void getTaskContents() {
        dlog.i("-----getTaskContents START-----");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        task_no = shardpref.getString("task_no", "0");
        writer_id = shardpref.getString("writer_id", "");
        WorkTitle = shardpref.getString("title", "");
        WorkContents = shardpref.getString("contents", "");
        TaskKind = shardpref.getString("complete_kind", "");            // 0:체크, 1:사진
        user_id = shardpref.getString("users", "");
        usersn = shardpref.getString("usersn", "");
        usersimg = shardpref.getString("usersimg", "");
        usersjikgup = shardpref.getString("usersjikgup", "");
        WorkDay = shardpref.getString("task_date", "");
        start_time = shardpref.getString("start_time", "");
        end_time = shardpref.getString("end_time", "");
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

        dlog.i("getTaskContents task_no : " + task_no);
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
        inmember.addAll(Arrays.asList(user_id.split(",")));

        message = "수정된 업무가 있습니다.";
        binding.workSave.setText("업무 수정");
        binding.inputWorktitle.setText(WorkTitle.equals("0")?"":WorkTitle);
        binding.inputWorkcontents.setText(WorkContents.equals("0")?"":WorkContents);

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
        today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
        if (!String.valueOf(getYoil).equals("[]")) {
            RepeatCheck = true;
            binding.repeatBtn.setBackgroundResource(R.drawable.ic_service_white);
            binding.selectRepeatBtn.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.repeatTv.setTextColor(Color.parseColor("#ffffff"));
        } else {
            dlog.i("input_pop_time : " + input_pop_time);
            dlog.i("SET_TASK_TIME_VALUE : " + SET_TASK_TIME_VALUE);
            RepeatCheck = false;
            binding.repeatBtn.setBackgroundResource(R.drawable.resize_service_off);

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

        if (!TaskKind.isEmpty()) {
            dlog.i("if(!TaskKind) : " + TaskKind);
            NeedReportTF = true;
            binding.needReport.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.reportTv.setTextColor(Color.parseColor("#ffffff"));
            binding.reportVisible.setVisibility(View.VISIBLE);
            if (TaskKind.equals("0")) {
                TaskKind = "0";
                binding.select01Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.select01.setTextColor(Color.parseColor("#000000"));
                binding.select02Box.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.select02.setTextColor(Color.parseColor("#ffffff"));
            } else if (TaskKind.equals("1")) {
                TaskKind = "1";
                binding.select01Box.setBackgroundColor(Color.parseColor("#6395EC"));
                binding.select01.setTextColor(Color.parseColor("#ffffff"));
                binding.select02Box.setBackgroundColor(Color.parseColor("#F5F6F8"));
                binding.select02.setTextColor(Color.parseColor("#000000"));
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
    String getStartDate = "";
    String getEndDate = "";
    RetrofitConnect rc = new RetrofitConnect();
    private void SaveAddWork() {
        dlog.i("------------------SaveAddWork------------------");
        dlog.i("RepeatCheck : " + RepeatCheck);
        today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;

        if (RepeatCheck) {
            start_time = starttime.isEmpty()?binding.inputTime01.getText().toString().replace("오전","").replace("오후","").trim():starttime;
            end_time = endtime.isEmpty()?binding.inputTime02.getText().toString().replace("오전","").replace("오후","").trim():endtime;
        } else {
            start_time = (getStartDate.isEmpty()?today:getStartDate) + " " + (starttime.isEmpty()?binding.inputTime01.getText().toString().replace("오전","").replace("오후","").trim():starttime);
            end_time = (getEndDate.isEmpty()?today:getEndDate) + " " + (endtime.isEmpty()?binding.inputTime02.getText().toString().replace("오전","").replace("오후","").trim():endtime);
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
        dlog.i("users : " + user_id);
        dlog.i("overdate : " + overdate);
//        if(overdate.isEmpty()){
//            cal.add(Calendar.DATE, +1);
//            String toDay = sdf.format(cal.getTime());
//            String Year = toDay.substring(0,4);
//            String Month = toDay.substring(5,7);
//            String Day = toDay.substring(8,10);
//            overdate = Year + "-" + Month + "-" + Day;
//            dlog.i("overdate 2 : " + overdate);
//        }
        if (USER_INFO_AUTH.equals("1")) {
            user_id = USER_INFO_ID;
        }
        if (make_kind == 1) {
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
                                , Sun, Mon, Tue, Wed, Thu, Fri, Sat, overdate
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
                                    String jsonResponse = rc.getBase64decode(response.body());
                                    dlog.i("jsonResponse length : " + jsonResponse.length());
                                    dlog.i("jsonResponse : " + jsonResponse);
                                    if (jsonResponse.replace("\"", "").equals("success") || jsonResponse.replace("\"", "").equals("success")) {
                                        dlog.i("assignment_kind : " + assignment_kind);
                                        dlog.i("SelectEmployeeid : " + user_id);

                                        if (USER_INFO_AUTH.equals("0")) {
                                            if (!user_id.equals("")) {
                                                SendUserCheck(1);
                                                if (return_page.equals("TaskCalenderActivity")) {
                                                    pm.CalenderBack(mContext);
                                                } else {
                                                    shardpref.putInt("SELECT_POSITION", 1);
                                                    shardpref.putInt("SELECT_POSITION_sub", 0);
                                                    pm.Main(mContext);
                                                }

                                                click_action = "PlaceWorkFragment";
                                                dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
                                                RemoveShared();
                                            }
                                        } else {
                                            shardpref.putInt("SELECT_POSITION", 1);
                                            shardpref.putInt("SELECT_POSITION_sub", 0);
                                            pm.Main2(mContext);
                                        }


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
            } else {
                @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                    runOnUiThread(() -> {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(TaskUpdateInterface.URL)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .build();
                        TaskUpdateInterface api = retrofit.create(TaskUpdateInterface.class);

                        //--반복 요일
                        dlog.i("------------------UpdateWork------------------");
                        Call<String> call = api.getData(task_no, place_id, USER_INFO_ID, "0", WorkTitle, WorkContents, TaskKind
                                , WorkDay, start_time, end_time
                                , Sun, Mon, Tue, Wed, Thu, Fri, Sat, overdate
                                , user_id);
                        call.enqueue(new Callback<String>() {
                            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                //반복되는 요일을 일시 초기화 해준다
                                dlog.e("UpdateWork function START");
                                dlog.e("response 1: " + response.isSuccessful());
                                dlog.e("response 2: " + response.body());
                                if (response.isSuccessful() && response.body() != null) {
                                    String jsonResponse = response.body();
                                    if (jsonResponse.replace("\"", "").equals("success") || jsonResponse.replace("\"", "").equals("success")) {
                                        dlog.i("assignment_kind : " + assignment_kind);
                                        dlog.i("SelectEmployeeid : " + user_id);
                                        String return_page = shardpref.getString("return_page", "");

                                        if (!user_id.equals("")) {
                                            SendUserCheck(2); //수정할때
                                            if (return_page.equals("TaskCalenderActivity")) {
                                                pm.CalenderBack(mContext);
                                            } else if (return_page.equals("return_page")) {
                                                pm.TaskReuse(mContext);
                                            } else {
                                                shardpref.putInt("SELECT_POSITION", 1);
                                                if (USER_INFO_AUTH.equals("0")) {
                                                    pm.Main(mContext);
                                                } else {
                                                    pm.Main2(mContext);
                                                }
                                            }
                                            click_action = "PlaceWorkFragment";
                                            dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
                                            RemoveShared();
                                        }

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
                                if (jsonResponse.replace("\"", "").equals("success") || jsonResponse.replace("\"", "").equals("success")) {
                                    dlog.i("assignment_kind : " + assignment_kind);
                                    dlog.i("SelectEmployeeid : " + user_id);
                                    if (!user_id.equals("")) {
                                        SendUserCheck(1);
                                        pm.CalenderBack(mContext);
                                        click_action = "PlaceWorkFragment";
                                        dlog.i("EmployeeChannelId1 : " + EmployeeChannelId1);
                                    }

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
        if(WorkDay.isEmpty()){
            WorkDay = toDay;
        }
        dlog.i("WorkDay : " + WorkDay);
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
        user_id = String.valueOf(item_user_id).replace("[", "").replace("]", "").replace(" ", "").trim();
        overdate = overdate.replace("년 ", "-").replace("월 ", "-").replace("일", "").trim();

        if (WorkTitle.equals("")) {
            dlog.i("WorkTitle");
            Toast.makeText(this, "할일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (WorkContents.isEmpty()) {
            dlog.i("WorkContents");
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TaskKind.isEmpty() && make_kind == 1) {
            Toast.makeText(this, "완료방법을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TaskKind.isEmpty() && make_kind == 4) {
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
                if (USER_INFO_AUTH.equals("1")) {
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

            }
        } else {
            dlog.i("제목2 : " + WorkTitle);
            dlog.i("내용 : " + WorkContents);
            dlog.i("완료방법2 : " + complete_kind);
            dlog.i("시작시간 : " + start_time);
            dlog.i("종료시간 : " + end_time);
            dlog.i("반복 : " + getYoil.toString().replace("[", "").replace("]", ""));
            dlog.i("작업날짜 : " + WorkDay);
            if (USER_INFO_AUTH.equals("1")) {
                dlog.i("선택된 직원 : " + USER_INFO_ID);
            } else {
                dlog.i("선택된 직원 : " + user_id);
            }

            return true;
        }
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

    /* -- 할일 추가 FCM 전송 영역 */
    private void SendUserCheck(int flag) {
        List<String> member = new ArrayList<>();
        dlog.i("보내야 하는 직원 배열 :" + user_id);
        member.addAll(Arrays.asList(user_id.split(",")));
        dlog.i("보내야 하는 직원 수 :" + member.size());
        dlog.i("보내야 하는 직원 List<String>  :" + member);
        if (flag == 1) {
            message = "[배정업무] :" + binding.title.getText().toString();
        } else if (flag == 2) {
            message = "[배정업무수정] :" + binding.title.getText().toString();
        }
        for (int a = 0; a < member.size(); a++) {
            getManagerToken(member.get(a), "1", place_id, place_name, item_user_name.get(a));
        }
    }

    public void getManagerToken(String user_id, String type, String place_id, String place_name, String user_name) {
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
                            String message = "[" + user_name + "] 님 " + place_name + "에서 업무가 배정되었습니다.";
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


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        shardpref.putInt("SELECT_POSITION", 1);
//        shardpref.putInt("SELECT_POSITION_sub", 0);
//        String return_page = shardpref.getString("return_page", "");
//        if (return_page.equals("return_page")) {
//            pm.TaskReuse(mContext);
//        } else {
//            if (USER_INFO_AUTH.equals("0")) {
//                pm.Main(mContext);
//            } else {
//                pm.Main2(mContext);
//            }
//        }
    }
}
