package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.FragmentStateAdapter;
import com.krafte.nebworks.adapter.WorkStatusCalenderAdapter;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.data.CalendarSetStatusData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.MainContentsInterface;
import com.krafte.nebworks.databinding.WorkstatusfragmentBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment1;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment2;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment3;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment4;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;
import com.krafte.nebworks.util.SwipeFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkstatusFragment extends Fragment {
    private final static String TAG = "WorkstatusFragment";
    private WorkstatusfragmentBinding binding;
    Context mContext;
    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    FragmentStateAdapter fragmentStateAdapter;
    WorkStatusCalenderAdapter mAdapter;
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList;
    //Task all data
    ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mList2 = new ArrayList<>();

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
    boolean chng_icon = false;
    Fragment fg;
    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMPicker = "";

    public static WorkstatusFragment newInstance(int number) {
        WorkstatusFragment fragment = new WorkstatusFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
            Log.i(TAG, "num : " + num);
        }
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.morefragment, container, false);
        binding = WorkstatusfragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);


        //UI 데이터 세팅
        try {
            //Singleton Area
            place_id        = PlaceCheckData.getInstance().getPlace_id();
            place_name      = PlaceCheckData.getInstance().getPlace_name();
            place_owner_id  = PlaceCheckData.getInstance().getPlace_owner_id();
            USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","");
            shardpref.remove("change_place_id");
            //shardpref Area
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);

            if (USER_INFO_AUTH.equals("1")) {
                if (!place_owner_id.equals(USER_INFO_ID)) {
                    binding.addBtn.getRoot().setVisibility(View.GONE);
                }
            }
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            SELECT_POSITION = 1;
            fg = WorkStatusSubFragment1.newInstance();

            fragmentStateAdapter = new FragmentStateAdapter(requireActivity(), 2);
//            calenderFragment.CalenderContext(mContext);
            binding.calenderViewpager.setAdapter(fragmentStateAdapter);
            binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);

            setBtnEvent();
            setAddBtnSetting();
            SendToday();

        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
        return binding.getRoot();
//        return rootView;
    }

    private void SendToday() {
        shardpref.putString("FtoDay", toDay);

        if (SELECT_POSITION_sub == 0) {
            binding.statusFragmentline1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
            SELECT_POSITION = 1;
            fg = WorkStatusSubFragment1.newInstance();
            setChildFragment(fg);
        } else if (SELECT_POSITION_sub == 1) {
            binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
            SELECT_POSITION = 2;
            fg = WorkStatusSubFragment2.newInstance();
            setChildFragment(fg);
        } else if (SELECT_POSITION_sub == 2) {
            binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline3.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
            SELECT_POSITION = 3;
            fg = WorkStatusSubFragment3.newInstance();
            setChildFragment(fg);
        } else if (SELECT_POSITION_sub == 3) {
            binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline4.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
            SELECT_POSITION = 4;
            fg = WorkStatusSubFragment4.newInstance();
            setChildFragment(fg);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        binding.setdate.setText(toDay);
        shardpref.putString("FtoDay", toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        Day = toDay.substring(8, 10);
        getYMPicker = Year + "-" + Month;
        if (chng_icon) {
            binding.setdate.setText(Year + "년 " + Month + "월 ");
        } else {
            binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
        }

        shardpref.putString("commute_date", Year + "년 " + Month + "월 " + Day + "일");

//        SetCalenderData(Year, Month);

        if (USER_INFO_AUTH.isEmpty()) {
            binding.cnt01.setText("10");
            binding.cnt02.setText("2");
            binding.cnt03.setText("5");
            binding.cnt04.setText("3");
        } else {
            PlaceWorkCheck(place_id);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        shardpref.remove("FtoDay");
    }

    public void setBtnEvent() {
        binding.statusFragmentbtn1.setOnClickListener(v -> {
            if(USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                binding.statusFragmentline1.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                SELECT_POSITION = 1;
                fg = WorkStatusSubFragment1.newInstance();
                setChildFragment(fg);
            }
        });
        binding.statusFragmentbtn2.setOnClickListener(v -> {
            if(USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                SELECT_POSITION = 2;
                fg = WorkStatusSubFragment2.newInstance();
                setChildFragment(fg);
            }
        });
        binding.statusFragmentbtn3.setOnClickListener(v -> {
            if(USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                SELECT_POSITION = 3;
                fg = WorkStatusSubFragment3.newInstance();
                setChildFragment(fg);
            }
        });
        binding.statusFragmentbtn4.setOnClickListener(v -> {
            if(USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(ContextCompat.getColor(mContext, R.color.new_blue));
                SELECT_POSITION = 4;
                fg = WorkStatusSubFragment4.newInstance();
                setChildFragment(fg);
            }
        });


        binding.prevDate.setOnClickListener(v -> {
            if(USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (chng_icon) {
                    cal.add(Calendar.MONTH, -1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    getYMPicker = Year + "-" + Month;
                    shardpref.putString("commute_date", Year + "년 " + Month + "월 " + Day + "일");
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                } else {
                    cal.add(Calendar.DATE, -1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    getYMPicker = Year + "-" + Month;
                    shardpref.putString("commute_date", Year + "년 " + Month + "월 " + Day + "일");
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                }
//                SetCalenderData(Year, Month);
                SendToday();
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            if(USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (chng_icon) {
                    cal.add(Calendar.MONTH, +1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    getYMPicker = Year + "-" + Month;
                    shardpref.putString("commute_date", Year + "년 " + Month + "월 " + Day + "일");
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                } else {
                    cal.add(Calendar.DATE, +1);
                    toDay = sdf.format(cal.getTime());
                    Year = toDay.substring(0, 4);
                    Month = toDay.substring(5, 7);
                    Day = toDay.substring(8, 10);
                    getYMPicker = Year + "-" + Month;
                    shardpref.putString("commute_date", Year + "년 " + Month + "월 " + Day + "일");
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                }
//                SetCalenderData(Year, Month);
                SendToday();
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
                    cal.add(Calendar.MONTH, - (Integer.parseInt(Month) - (month + 1)));
                    cal.add(Calendar.DAY_OF_MONTH, - (Integer.parseInt(Day) - (dayOfMonth)));
                } else {
                    cal.add(Calendar.MONTH, ((month + 1)  - Integer.parseInt(Month)));
                    cal.add(Calendar.DAY_OF_MONTH, ((dayOfMonth)  - Integer.parseInt(Day)));
                }
                dlog.i("getCalenderTime: " + cal.getTime()) ;
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                if (chng_icon) {
                    binding.calenderViewpager.setSaveFromParentEnabled(false);
                    fragmentStateAdapter = new FragmentStateAdapter(requireActivity(), true, Year, Month, 2);
                    binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                    binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                } else {
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                }
                getYMPicker = Year + "년 " + Month + "월 ";
                toDay = Year + "-" + Month + "-" + Day;
                SendToday();
//                SetCalenderData(String.valueOf(year), Month);
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if(USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (binding.setdate.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        binding.dateSelect.setOnClickListener(view -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (binding.setdate.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        if (USER_INFO_AUTH.isEmpty()) {
            // dummy
            binding.inoutName.setText("나의 매장 출퇴근");
        } else {
            binding.inoutName.setText(place_name + " 출퇴근");
        }
        binding.changeIcon.setOnClickListener(v -> {
            if (!chng_icon) {
                chng_icon = true;
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                binding.dateLayout.setVisibility(View.GONE);
                binding.dateSelect.setVisibility(View.VISIBLE);
                binding.setdate.setText(Year + "년 " + Month + "월");
//                SetCalenderData(Year, Month);
            } else {
                chng_icon = false;
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                binding.dateLayout.setVisibility(View.VISIBLE);
                binding.dateSelect.setVisibility(View.GONE);
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
            }
        });

        binding.selectPlace.setText(place_name);
        binding.changePlace.setTag(place_name);

        binding.changePlace.setOnClickListener(v -> {
            PlaceListBottomSheet plb = new PlaceListBottomSheet();
            plb.show(getChildFragmentManager(),"PlaceListBottomSheet");
            plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                shardpref.putString("change_place_id",place_id);
                dlog.i("change_place_id : " + place_id);
                binding.selectPlace.setText(place_name);
                binding.changePlace.setTag(place_name);
//                SetCalenderData(Year, Month);
                PlaceWorkCheck(place_id);
                if(SELECT_POSITION == 1){
                    fg = WorkStatusSubFragment1.newInstance();
                    setChildFragment(fg);
                } else  if(SELECT_POSITION == 2){
                    fg = WorkStatusSubFragment2.newInstance();
                    setChildFragment(fg);
                } else  if(SELECT_POSITION == 3){
                    fg = WorkStatusSubFragment3.newInstance();
                    setChildFragment(fg);
                } else  if(SELECT_POSITION == 4){
                    fg = WorkStatusSubFragment4.newInstance();
                    setChildFragment(fg);
                }
            });
        });

//        binding.createCalender.setOnTouchListener(new OnSwipeTouchListener(mContext) {
//            @Override
//            public void onSwipeLeft() {
////                super.onSwipeLeft();
//                setCalender(1);
//            }
//
//            @Override
//            public void onSwipeRight() {
////                super.onSwipeRight();
//                setCalender(-1);
//            }
//        });

        binding.statusChildFragmentContainer.setOnSwipeListener(new SwipeFrameLayout.OnSwipeListener() {
            @Override
            public void onSwipe(View view, int direction) {
                if (direction == 1)
                    setCalender(direction);
                else if (direction == -1)
                    setCalender(direction);
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
//        SetCalenderData(Year, Month);
        SendToday();
    }

    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.status_child_fragment_container, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }

//    public void MainWorkCnt(String place_id, String user_id) {
//        dlog.i("MainWorkCnt place_id : " + place_id);
//        dlog.i("MainWorkCnt user_id : " + user_id);
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(MainWorkCntInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        MainWorkCntInterface api = retrofit.create(MainWorkCntInterface.class);
//        Call<String> call = api.getData(place_id, user_id);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
//                    dlog.i("MainWorkCnt jsonResponse length : " + jsonResponse.length());
//                    dlog.i("MainWorkCnt jsonResponse : " + jsonResponse);
//                    Log.e("onSuccess : ", response.body());
//                    try {
//                        //Array데이터를 받아올 때
//                        JSONArray Response = new JSONArray(jsonResponse);
//                        if (Response.length() != 0) {
//                            String task_complete_cnt = Response.getJSONObject(0).getString("task_complete_cnt");
//                            String task_incomplete_cnt = Response.getJSONObject(0).getString("task_incomplete_cnt");
//                            String approval_total_cnt = Response.getJSONObject(0).getString("approval_total_cnt");
//                            String waiting_cnt = Response.getJSONObject(0).getString("waiting_cnt");
//                            String approval_cnt = Response.getJSONObject(0).getString("approval_cnt");
//                            String reject_cnt = Response.getJSONObject(0).getString("reject_cnt");
//                            String contract_cnt = Response.getJSONObject(0).getString("contract_cnt");
//                            String owner_sign_id = Response.getJSONObject(0).getString("owner_sign_id");
//                            String worker_sign_id = Response.getJSONObject(0).getString("worker_sign_id");
//
//                            binding.cnt01.setText(Response.getJSONObject(0).getString("i_cnt"));
//                            if(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) == 0){
//                                binding.cnt02.setText(Response.getJSONObject(0).getString("absence_cnt"));
//                            } else if(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) > 0){
//                                binding.cnt02.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt"))
//                                        -Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
//                            }
//
//                            binding.cnt03.setText(Response.getJSONObject(0).getString("o_cnt"));
////                            binding.cnt04.setText(Response.getJSONObject(0).getString("vaca_cnt"));.
//                            binding.cnt04.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("rest_cnt"))
//                                    + Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
//                            dlog.i("------PlaceWorkCheck------");
//                            dlog.i("i_cnt : " + Response.getJSONObject(0).getString("i_cnt"));
//                            dlog.i("o_cnt : " + Response.getJSONObject(0).getString("o_cnt"));
//                            dlog.i("absence_cnt : " + Response.getJSONObject(0).getString("absence_cnt"));
//                            dlog.i("vaca_cnt : " + Response.getJSONObject(0).getString("vaca_cnt"));
//                            shardpref.putString("i_cnt", Response.getJSONObject(0).getString("i_cnt"));
//                            dlog.i("------PlaceWorkCheck-------");
//
//                            dlog.i("-----MainWorkCnt-----");
//                            dlog.i("task_complete_cnt : " + task_complete_cnt);
//                            dlog.i("task_incomplete_cnt : " + task_incomplete_cnt);
//                            dlog.i("waiting_cnt : " + waiting_cnt);
//                            dlog.i("approval_cnt : " + approval_cnt);
//                            dlog.i("reject_cnt : " + reject_cnt);
//                            dlog.i("contract_cnt : " + contract_cnt);
//                            dlog.i("owner_sign_id : " + owner_sign_id);
//                            dlog.i("worker_sign_id : " + worker_sign_id);
//                            dlog.i("-----MainWorkCnt-----");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                dlog.e("에러 = " + t.getMessage());
//            }
//        });
//    }
    public void PlaceWorkCheck(String place_id) {
        dlog.i("PlaceWorkCheck place_id : " + place_id);
        dlog.i("PlaceWorkCheck USER_INFO_ID : " + USER_INFO_ID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainContentsInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainContentsInterface api = retrofit.create(MainContentsInterface.class);
        Call<String> call = api.getData(place_id, "0", USER_INFO_ID, "0");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("PlaceWorkCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("PlaceWorkCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    try {
                                        binding.cnt01.setText(Response.getJSONObject(0).getString("i_cnt"));
                                        //결근 숫자에서 휴가숫자는 빠지지 않기때문에 결근-휴가수를 빼줘야한다
                                        if(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) == 0){
                                            binding.cnt02.setText(Response.getJSONObject(0).getString("absence_cnt"));
                                        } else if(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt")) > 0){
                                            binding.cnt02.setText(String.valueOf(Integer.parseInt(Response.getJSONObject(0).getString("absence_cnt"))
                                                    -Integer.parseInt(Response.getJSONObject(0).getString("vaca_cnt"))));
                                        }
                                        binding.cnt03.setText(Response.getJSONObject(0).getString("o_cnt"));
                                        binding.cnt04.setText(Response.getJSONObject(0).getString("vaca_cnt"));

                                        dlog.i("------PlaceWorkCheck2------");
                                        dlog.i("i_cnt : " + Response.getJSONObject(0).getString("i_cnt"));
                                        dlog.i("o_cnt : " + Response.getJSONObject(0).getString("o_cnt"));
                                        dlog.i("absence_cnt : " + Response.getJSONObject(0).getString("absence_cnt"));
                                        dlog.i("vaca_cnt : " + Response.getJSONObject(0).getString("vaca_cnt"));
                                        dlog.i("rest_cnt : " + Response.getJSONObject(0).getString("rest_cnt"));
                                        shardpref.putString("i_cnt", Response.getJSONObject(0).getString("i_cnt"));
                                        dlog.i("------PlaceWorkCheck2-------");
                                    } catch (Exception e) {
                                        dlog.i("PlaceWorkCheck Exception : " + e);
                                    }
                                }else{
//                                    MainWorkCnt(place_id,USER_INFO_ID);
                                    shardpref.putString("i_cnt","0");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private boolean isDragging = false;

    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("근무추가");
        add_worktime_btn.setVisibility(place_owner_id.equals(USER_INFO_ID) ? View.VISIBLE : View.GONE);

        // Set OnTouchListener to ImageView
        add_worktime_btn.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            int newX;
            int newY;
            private int lastnewX = 0;
            private int lastnewY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = v.getLeft();
                        initialY = v.getTop();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        isDragging = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!isDragging) {
                            isDragging = true;
                        }

                        int dx = (int) (event.getRawX() - initialTouchX);
                        int dy = (int) (event.getRawY() - initialTouchY);

                        newX = initialX + dx;
                        newY = initialY + dy;

                        if(lastnewX == 0){ lastnewX = newX; }
                        if(lastnewY == 0){ lastnewY = newY; }

                        int parentWidth = ((ViewGroup) v.getParent()).getWidth();
                        int parentHeight = ((ViewGroup) v.getParent()).getHeight();
                        int childWidth = v.getWidth();
                        int childHeight = v.getHeight();

                        newX = Math.max(0, Math.min(newX, parentWidth - childWidth));
                        newY = Math.max(0, Math.min(newY, parentHeight - childHeight));

                        // Update the position of the ImageView
                        v.layout(newX, newY, newX + v.getWidth(), newY + v.getHeight());
                        break;

                    case MotionEvent.ACTION_UP:
                        lastAction = MotionEvent.ACTION_UP;
                        int Xdistance = (newX - lastnewX);
                        int Ydistance = (newY - lastnewY);
                        if (Math.abs(Xdistance) < 10 && Math.abs(Ydistance) < 10) {
                            if (USER_INFO_AUTH.isEmpty()) {
                                isAuth();
                            } else {
                                pm.AddWorkPart(mContext);
                            }
                        }else{
                            lastnewX = newX;
                            lastnewY = newY;
                        }
                        isDragging = false;
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
