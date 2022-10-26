package com.krafte.kogas.ui.worksite;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.krafte.kogas.R;
import com.krafte.kogas.adapter.AssignmentMemberAdapter;
import com.krafte.kogas.adapter.AssignmentMemberAdapter2;
import com.krafte.kogas.data.GetResultData;
import com.krafte.kogas.data.PlaceMemberListData;
import com.krafte.kogas.data.YoilList;
import com.krafte.kogas.dataInterface.FCMSelectInterface;
import com.krafte.kogas.dataInterface.PlaceMemberallInterface;
import com.krafte.kogas.dataInterface.ScheduleAddInterface;
import com.krafte.kogas.dataInterface.TaskInputInterface;
import com.krafte.kogas.dataInterface.TaskUpdateInterface;
import com.krafte.kogas.dataInterface.TaskreuseInputInterface;
import com.krafte.kogas.dataInterface.TaskreuseUpInterface;
import com.krafte.kogas.databinding.ActivityPlaceaddworkBinding;
import com.krafte.kogas.pop.DatePickerActivity;
import com.krafte.kogas.pop.OneButtonPopActivity;
import com.krafte.kogas.pop.WorkTimePicker;
import com.krafte.kogas.util.DBConnection;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PlaceAddWorkActivity extends AppCompatActivity {
    private static final String TAG = "EmployerAddWorkActivity";
    Context mContext;
    private ActivityPlaceaddworkBinding binding;

    // shared 저장값
    PreferenceHelper shardpref;
    boolean channelId1 = false;
    boolean channelId2 = false;
    boolean EmployeeChannelId1 = false;
    String USER_INFO_ID;

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

    //Other
    AssignmentMemberAdapter mAdapter;
    ArrayList<PlaceMemberListData.PlaceMemberListData_list> mList = new ArrayList<>();

    AssignmentMemberAdapter2 mAdapter2;
    ArrayList<PlaceMemberListData.PlaceMemberListData_list> mList2 = new ArrayList<>();

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
    String Sun = "", Mon = "", Tue = "", Wed = "", Thu = "", Fri = "", Sat = "";
    /*-------------------*/

    /*--------------------*/
    String toDay = "";
    String toDayYoil = "";
    int dayOfWeek = 0;
    String toDayFromMon = "";
    String toDayFromTue = "";
    String toDayFromWed = "";
    String toDayFromThu = "";
    String toDayFromFri = "";
    String toDayFromSat = "";
    String toDayFromSun = "";
    String return_page = "";
    List<YoilList> Yoils = new ArrayList<>();
    List<String> getYoil = new ArrayList<>();
    String[] setYoil = new String[7];

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
            SetAllMemberList();
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            binding.inputWorkstartDay.setText(toDay);
            dlog.i("------------------Data Check onCreate------------------");
            dlog.i("tap_kind : " + tap_kind);
            dlog.i("make_kind : " + make_kind);
            dlog.i("searchDate : " + searchDate);
            dlog.i("today : " + toDay);
            dlog.i("------------------Data Check onCreate------------------");
            if(make_kind == 1 || make_kind == 2){
                binding.title.setText("할일 생성");
            }else{
                binding.title.setText("일정 생성");
            }

            //--searchDate값이 없을 때는 반복업무 영역 보이기, 있을때는 캘린더에서 해당 날짜에 추가
            if (searchDate.isEmpty()) {
                binding.firstSelect.setVisibility(View.VISIBLE);
                binding.secondSelect.setVisibility(View.VISIBLE);
                binding.linearLayout2.setVisibility(View.VISIBLE);
            } else {
                binding.firstSelect.setVisibility(View.GONE);
                binding.secondSelect.setVisibility(View.GONE);
                binding.linearLayout2.setVisibility(View.GONE);
            }
            //--처음에는 공통임무로 설정된채로 시작
            user_id = "";
            if (!task_no.equals("0")) {
                getTaskContents();
            }

            TaskKind = String.valueOf(make_kind);
            dlog.i("onCreate make_kind : " + make_kind);
            if (make_kind == 1) {//할일 배정,공통 할일,개인일정
                SelectKind = String.valueOf(make_kind);
                user_id = "";
                shardpref.putInt("SELECT_POSITION", 1);
                binding.linearLayout10.setVisibility(View.VISIBLE);
                binding.memberListArea.setVisibility(View.VISIBLE);
                binding.otherMemberArea.setVisibility(View.VISIBLE);
            } else if (make_kind == 2) {//반복업무 생성
                user_id = "";
                binding.linearLayout7.setVisibility(View.GONE);
                binding.linearLayout10.setVisibility(View.GONE);
                binding.memberListArea.setVisibility(View.GONE);
                binding.otherMemberArea.setVisibility(View.GONE);
                shardpref.putInt("SELECT_POSITION", 2);
            } else if (make_kind == 4) {
                binding.linearLayout4.setVisibility(View.GONE);
                binding.linearLayout10.setVisibility(View.GONE);
                binding.otherMemberArea.setVisibility(View.GONE);
            } else {
                binding.linearLayout10.setVisibility(View.VISIBLE);
                binding.memberListArea.setVisibility(View.VISIBLE);
                binding.otherMemberArea.setVisibility(View.VISIBLE);
            }

            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            Calendar c3 = Calendar.getInstance();
            Calendar c4 = Calendar.getInstance();
            Calendar c5 = Calendar.getInstance();
            Calendar c6 = Calendar.getInstance();
            Calendar c7 = Calendar.getInstance();
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            c1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c2.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
            c3.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            c4.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
            c5.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            c6.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            c7.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            toDayFromMon = formatter.format(c1.getTime());
            toDayFromTue = formatter.format(c2.getTime());
            toDayFromWed = formatter.format(c3.getTime());
            toDayFromThu = formatter.format(c4.getTime());
            toDayFromFri = formatter.format(c5.getTime());
            toDayFromSat = formatter.format(c6.getTime());
            toDayFromSun = formatter.format(c7.getTime());
            dlog.i("toDayFromMon : " + toDayFromMon);
            dlog.i("toDayFromTue : " + toDayFromTue);
            dlog.i("toDayFromWed : " + toDayFromWed);
            dlog.i("toDayFromThu : " + toDayFromThu);
            dlog.i("toDayFromFri : " + toDayFromFri);
            dlog.i("toDayFromSat : " + toDayFromSat);
            dlog.i("toDayFromSun : " + toDayFromSun);

            switch (dayOfWeek) {
                case 1:
                    toDayYoil = "월";
                    break;
                case 2:
                    toDayYoil = "화";
                    break;
                case 3:
                    toDayYoil = "수";
                    break;
                case 4:
                    toDayYoil = "목";
                    break;
                case 5:
                    toDayYoil = "금";
                    break;
                case 6:
                    toDayYoil = "토";
                    break;
                case 7:
                    toDayYoil = "일";
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setBtnEvent() {
        binding.closeBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

        binding.inputWorkstartDay.setOnClickListener(v -> {
            if (binding.inputWorkstartDay.getText().toString().length() == 0) {
                String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                binding.inputWorkstartDay.setText(today);
            } else {
                shardpref.putInt("timeSelect_flag", 6);
                Intent intent = new Intent(this, DatePickerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            }
        });

        //--완료방법 라디오 버튼
        binding.workaddTv01.setOnClickListener(view -> {
            complete_kind = "1";
            binding.workaddTv01.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.workaddTv01.setTextColor(Color.parseColor("#ffffff"));
            binding.workaddTv02.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.workaddTv02.setTextColor(Color.parseColor("#696969"));
//            workadd_radio01.setChecked(true);
//            workadd_radio02.setChecked(false);
        });
        binding.workaddTv02.setOnClickListener(view -> {
            complete_kind = "0";
            binding.workaddTv01.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.workaddTv01.setTextColor(Color.parseColor("#696969"));
            binding.workaddTv02.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.workaddTv02.setTextColor(Color.parseColor("#ffffff"));
//            workadd_radio01.setChecked(false);
//            workadd_radio02.setChecked(true);
        });

        //--반복설정
        binding.repeatBtn01.setOnClickListener(view -> {
            if (yoil01) {
                setYoil[0] = "월";
                yoil01 = false;
                binding.repeatBtn01.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn01.setTextColor(Color.parseColor("#ffffff"));

                yoil08 = true;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            } else {
                setYoil[0] = null;
                yoil01 = true;
                binding.repeatBtn01.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn01.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });
        binding.repeatBtn02.setOnClickListener(view -> {
            if (yoil02) {
                setYoil[1] = "화";
                yoil02 = false;
                binding.repeatBtn02.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn02.setTextColor(Color.parseColor("#ffffff"));

                yoil08 = true;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            } else {
                setYoil[1] = null;
                yoil02 = true;
                binding.repeatBtn02.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn02.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });
        binding.repeatBtn03.setOnClickListener(view -> {
            if (yoil03) {
                setYoil[2] = "수";
                yoil03 = false;
                binding.repeatBtn03.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn03.setTextColor(Color.parseColor("#ffffff"));

                yoil08 = true;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            } else {
                setYoil[2] = null;
                yoil03 = true;
                binding.repeatBtn03.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn03.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });
        binding.repeatBtn04.setOnClickListener(view -> {
            if (yoil04) {
                setYoil[3] = "목";
                yoil04 = false;
                binding.repeatBtn04.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn04.setTextColor(Color.parseColor("#ffffff"));

                yoil08 = true;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            } else {
                setYoil[3] = null;
                yoil04 = true;
                binding.repeatBtn04.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn04.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });
        binding.repeatBtn05.setOnClickListener(view -> {
            if (yoil05) {
                setYoil[4] = "금";
                yoil05 = false;
                binding.repeatBtn05.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn05.setTextColor(Color.parseColor("#ffffff"));

                yoil08 = true;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            } else {
                setYoil[4] = null;
                yoil05 = true;
                binding.repeatBtn05.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn05.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });
        binding.repeatBtn06.setOnClickListener(view -> {
            if (yoil06) {
                setYoil[5] = "토";
                yoil06 = false;
                binding.repeatBtn06.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn06.setTextColor(Color.parseColor("#ffffff"));

                yoil08 = true;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            } else {
                setYoil[5] = null;
                yoil06 = true;
                binding.repeatBtn06.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn06.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });
        binding.repeatBtn07.setOnClickListener(view -> {
            if (yoil07) {
                setYoil[6] = "일";
                yoil07 = false;
                binding.repeatBtn07.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn07.setTextColor(Color.parseColor("#ffffff"));

                yoil08 = true;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            } else {
                setYoil[6] = null;
                yoil07 = true;
                binding.repeatBtn07.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn07.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });

        binding.repeatBtn08.setOnClickListener(view -> {
            getYoil.clear();

            binding.repeatBtn01.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.repeatBtn01.setTextColor(Color.parseColor("#696969"));
            binding.repeatBtn02.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.repeatBtn02.setTextColor(Color.parseColor("#696969"));
            binding.repeatBtn03.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.repeatBtn03.setTextColor(Color.parseColor("#696969"));
            binding.repeatBtn04.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.repeatBtn04.setTextColor(Color.parseColor("#696969"));
            binding.repeatBtn05.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.repeatBtn05.setTextColor(Color.parseColor("#696969"));
            binding.repeatBtn06.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.repeatBtn06.setTextColor(Color.parseColor("#696969"));
            binding.repeatBtn07.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.repeatBtn07.setTextColor(Color.parseColor("#696969"));
            if (yoil08) {
                yoil01 = true;
                yoil02 = true;
                yoil03 = true;
                yoil04 = true;
                yoil05 = true;
                yoil06 = true;
                yoil07 = true;
                yoil08 = false;
                setYoil[0] = "월";
                setYoil[1] = "화";
                setYoil[2] = "수";
                setYoil[3] = "목";
                setYoil[4] = "금";
                setYoil[5] = "토";
                setYoil[6] = "일";
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#1483FE"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#ffffff"));
            } else {
                yoil01 = false;
                yoil02 = false;
                yoil03 = false;
                yoil04 = false;
                yoil05 = false;
                yoil06 = false;
                yoil07 = false;
                yoil08 = true;
                setYoil[0] = null;
                setYoil[1] = null;
                setYoil[2] = null;
                setYoil[3] = null;
                setYoil[4] = null;
                setYoil[5] = null;
                setYoil[6] = null;
                binding.repeatBtn08.setBackgroundColor(Color.parseColor("#f2f2f2"));
                binding.repeatBtn08.setTextColor(Color.parseColor("#696969"));
            }
            ChangeYoilDate();
        });
        //--반복설정


        binding.inputEmployeeStarttime.setOnClickListener(view -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 4);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.inputEmployeeTime.setOnClickListener(view -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 5);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });

        binding.workSave.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onClick();
                dlog.i("binding.workSave click");
            }

            if (make_kind == 0 || make_kind == 1 || make_kind == 4) {//할일 배정,공통 할일,공개일정
                if (SaveCheck()) {
                    SaveAddWork();
                }
            } else if (make_kind == 2) {//반복업무 생성
                if (SaveCheck2()) {
                    SaveAddWork2();
                }
            }

        });
        binding.cancel.setOnClickListener(v -> {
            //데이터 전달하기
            //액티비티(팝업) 닫기
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });

    }

    private void ChangeYoilDate() {
        getYoil.clear();
        for (String str : setYoil) {
            if (str != null) {
                getYoil.add(str);
            }
        }
        dlog.i("getYoil : " + getYoil.get(0));
        if (!getYoil.get(0).equals(toDayYoil)) {
            if (getYoil.get(0).equals("월")) {
                binding.inputWorkstartDay.setText(toDayFromMon);
            } else if (getYoil.get(0).equals("화")) {
                binding.inputWorkstartDay.setText(toDayFromTue);
            } else if (getYoil.get(0).equals("수")) {
                binding.inputWorkstartDay.setText(toDayFromWed);
            } else if (getYoil.get(0).equals("목")) {
                binding.inputWorkstartDay.setText(toDayFromThu);
            } else if (getYoil.get(0).equals("금")) {
                binding.inputWorkstartDay.setText(toDayFromFri);
            } else if (getYoil.get(0).equals("토")) {
                binding.inputWorkstartDay.setText(toDayFromSat);
            } else if (getYoil.get(0).equals("일")) {
                binding.inputWorkstartDay.setText(toDayFromSun);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        String thumnail_url = shardpref.getString("thumnail_url", "");
        String name = shardpref.getString("name", "");
        String writer_id = shardpref.getString("writer_id", "");
        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
        int hourOfDay = shardpref.getInt("Hour", 0);
        int minute = shardpref.getInt("Min", 0);

        dlog.i("------------------Data Check onResume------------------");
        dlog.i("thumnail_url : " + thumnail_url);
        dlog.i("name : " + name);
        dlog.i("writer_id : " + writer_id);
        dlog.i("kind : " + shardpref.getInt("timeSelect_flag", 0));
        dlog.i("Hour : " + shardpref.getInt("Hour", 0));
        dlog.i("Min : " + shardpref.getInt("Min", 0));
        dlog.i("timeSelect_flag : " + timeSelect_flag);
        dlog.i("------------------Data Check onResume------------------");

        final String GetTime = (String.valueOf(hourOfDay).length() == 1 ? "0" + hourOfDay : String.valueOf(hourOfDay))
                + ":" +
                (String.valueOf(minute).length() == 1 ? "0" + minute : String.valueOf(minute));
        if (timeSelect_flag == 4) {
//            start_time = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
//            StartTime02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            start_time = GetTime;
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            binding.inputEmployeeStarttime.setText(GetTime);
            binding.inputWorktitle.clearFocus();
            binding.inputWorkcontents.clearFocus();
        } else if (timeSelect_flag == 5) {
//            end_time = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
//            EndTime02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            end_time = GetTime;
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            binding.inputEmployeeTime.setText(GetTime);
            binding.inputWorktitle.clearFocus();
            binding.inputWorkcontents.clearFocus();
        } else if (timeSelect_flag == 6) {
            //-- DatePickerActivity에서 받아오는 값
            String getDatePicker = shardpref.getString("vDateGetDate", "");
            binding.inputWorkstartDay.setText(getDatePicker);
            binding.inputWorktitle.clearFocus();
            binding.inputWorkcontents.clearFocus();
            shardpref.remove("vDateGetDate");
            shardpref.remove("timeSelect_flag");
        }


    }

    public void SetAllMemberList() {
        total_member_cnt = 0;

        dlog.i("SetAllMemberList place_id : " + place_id);
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(PlaceMemberallInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            PlaceMemberallInterface api = retrofit.create(PlaceMemberallInterface.class);
            Call<String> call = api.getData(place_id);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            dlog.i("SetAllMemberList response.body() length : " + response.body());
                            if (Response.length() == 0) {
                                binding.noMember.setVisibility(View.VISIBLE);
                                binding.recyclerView2.setVisibility(View.INVISIBLE);
                                binding.recyclerView2.setVisibility(View.GONE);
                            } else {
                                binding.noMember.setVisibility(View.INVISIBLE);
                                binding.recyclerView2.setVisibility(View.VISIBLE);
                                total_member_cnt = Response.length();
                                mList = new ArrayList<>();
                                mAdapter = new AssignmentMemberAdapter(mContext, mList);
                                binding.recyclerView2.setHasFixedSize(true);
                                binding.recyclerView2.setAdapter(mAdapter);
                                binding.recyclerView2.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                RecyclerView.ItemAnimator animator = binding.recyclerView2.getItemAnimator();
                                if (animator instanceof SimpleItemAnimator) {
                                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                                }
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    //정직원만
                                    if(jsonObject.getString("kind").equals("0")){
                                        a++;
                                        inmember.add(jsonObject.getString("id"));
                                        mAdapter.addItem(new PlaceMemberListData.PlaceMemberListData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("account"),
                                                jsonObject.getString("employee_no"),
                                                jsonObject.getString("department"),
                                                jsonObject.getString("position"),
                                                jsonObject.getString("img_path")
                                        ));
                                    }

                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener((v, position, memberArray) -> {
                                    try {
                                        dlog.i("Select Member id :" + Response.getJSONObject(position).getString("id"));
                                        user_id = Response.getJSONObject(position).getString("id");
                                        EmployeeSelect = 1;
//                                        dlog.i("memberArray :" + memberArray);
//                                        Ac_memberArray = memberArray.stream().distinct().collect(Collectors.toList());
                                        Ac_memberArray = memberArray.stream().distinct().collect(Collectors.toList());
                                        dlog.i("Ac_memberArray :" + Ac_memberArray);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });

                                binding.recyclerView3.setVisibility(View.VISIBLE);
                                mList2 = new ArrayList<>();
                                mAdapter2 = new AssignmentMemberAdapter2(mContext, mList2);
                                binding.recyclerView3.setHasFixedSize(true);
                                binding.recyclerView3.setAdapter(mAdapter2);
                                binding.recyclerView3.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                RecyclerView.ItemAnimator animator2 = binding.recyclerView3.getItemAnimator();
                                if (animator2 instanceof SimpleItemAnimator) {
                                    ((SimpleItemAnimator) animator2).setSupportsChangeAnimations(false);
                                }
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    //외부협력업체 직원
                                    if(jsonObject.getString("kind").equals("1")){
                                        b++;
                                        othermember.add(jsonObject.getString("id"));
                                        mAdapter2.addItem(new PlaceMemberListData.PlaceMemberListData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("account"),
                                                jsonObject.getString("employee_no"),
                                                jsonObject.getString("department"),
                                                jsonObject.getString("position"),
                                                jsonObject.getString("img_path")
                                        ));
                                    }

                                }
                                mAdapter2.notifyDataSetChanged();
                                mAdapter2.setOnItemClickListener((v, position, memberArray) -> {
                                    try {
                                        dlog.i("Select Member id :" + Response.getJSONObject(position).getString("id"));
                                        user_id = Response.getJSONObject(position).getString("id");
                                        EmployeeSelect = 1;
                                        Ac_memberArray2 = memberArray.stream().distinct().collect(Collectors.toList());
                                        dlog.i("Ac_memberArray2 :" + Ac_memberArray2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                                inmember.addAll(othermember);
                                dlog.i("총 직원 배열 : " + inmember);
                                dlog.i("정직원 수 : " + a + "/ 협력업체 직원 수 : " + b);
                                if(a == 0){
                                    binding.linearLayout10.setVisibility(View.GONE);
                                    binding.memberListArea.setVisibility(View.GONE);
                                }
                                if(b == 0){
                                    binding.otherMemberArea.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        message = "수정된 업무가 있습니다.";
        binding.workSave.setText("업무 수정");
        binding.inputWorktitle.setText(WorkTitle);
        binding.inputWorkcontents.setText(WorkContents);

        if (TaskKind.equals("1")) {
            complete_kind = "1";
            binding.workaddTv01.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.workaddTv01.setTextColor(Color.parseColor("#ffffff"));
            binding.workaddTv02.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.workaddTv02.setTextColor(Color.parseColor("#696969"));
        } else {
            complete_kind = "0";
            binding.workaddTv01.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.workaddTv01.setTextColor(Color.parseColor("#696969"));
            binding.workaddTv02.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.workaddTv02.setTextColor(Color.parseColor("#ffffff"));
        }

        binding.inputWorkstartDay.setText(WorkDay);

        if (!start_time.isEmpty()) {
            String ampm = "";
            if (Integer.parseInt(start_time.substring(0, 2)) <= 12) {
                ampm = " AM";
                SelectEndTime = 1;
            } else {
                ampm = " PM";
                SelectEndTime = 2;
            }
            binding.inputEmployeeStarttime.setText(start_time + " " + ampm);
        }
        if (!end_time.isEmpty()) {

            String ampm = "";
            if (Integer.parseInt(end_time.substring(0, 2)) <= 12) {
                ampm = " AM";
                SelectEndTime = 1;
            } else {
                ampm = " PM";
                SelectEndTime = 2;
            }
            binding.inputEmployeeTime.setText(end_time + " " + ampm);
        }


        if (!Mon.equals("0")) {
            yoil01 = false;
            setYoil[0] = "월";
            binding.repeatBtn01.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.repeatBtn01.setTextColor(Color.parseColor("#ffffff"));
        }
        if (!Tue.equals("0")) {
            yoil02 = false;
            setYoil[1] = "화";
            binding.repeatBtn02.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.repeatBtn02.setTextColor(Color.parseColor("#ffffff"));
        }
        if (!Wed.equals("0")) {
            yoil03 = false;
            setYoil[2] = "수";
            binding.repeatBtn03.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.repeatBtn03.setTextColor(Color.parseColor("#ffffff"));
        }
        if (!Thu.equals("0")) {
            yoil04 = false;
            setYoil[3] = "목";
            binding.repeatBtn04.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.repeatBtn04.setTextColor(Color.parseColor("#ffffff"));
        }
        if (!Fri.equals("0")) {
            yoil05 = false;
            setYoil[4] = "금";
            binding.repeatBtn05.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.repeatBtn05.setTextColor(Color.parseColor("#ffffff"));
        }
        if (!Sat.equals("0")) {
            yoil06 = false;
            setYoil[5] = "토";
            binding.repeatBtn06.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.repeatBtn06.setTextColor(Color.parseColor("#ffffff"));
        }
        if (!Sun.equals("0")) {
            yoil07 = false;
            setYoil[6] = "일";
            binding.repeatBtn07.setBackgroundColor(Color.parseColor("#1483FE"));
            binding.repeatBtn07.setTextColor(Color.parseColor("#ffffff"));
        }
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
        dlog.i("kind : " + make_kind);
        dlog.i("title : " + WorkTitle);
        dlog.i("contents : " + WorkContents);
        dlog.i("complete_kind : " + String.valueOf(TaskKind));
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
                        dlog.i("------------------SaveAddWork------------------");
                        Call<String> call = api.getData(place_id, USER_INFO_ID, String.valueOf(make_kind), WorkTitle, WorkContents, complete_kind
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
                                            SendUserCheck(1);
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
            } else {
                @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
                    runOnUiThread(() -> {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(TaskUpdateInterface.URL)
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .build();
                        TaskUpdateInterface api = retrofit.create(TaskUpdateInterface.class);

                        //--반복 요일
                        dlog.i("------------------SaveAddWork------------------");
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
        WorkDay = binding.inputWorkstartDay.getText().toString();

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

        user_id = Ac_memberArray.toString().replace(" ", "").replace("[", "").replace("]", "").trim();
        total_member_cnt = Ac_memberArray.size() + Ac_memberArray2.size();

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
        WorkDay = binding.inputWorkstartDay.getText().toString();
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
        pm.PlaceWorkBack(mContext);
    }
}
