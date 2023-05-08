package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.FragmentStateAdapter;
import com.krafte.nebworks.adapter.Tap2ListAdapter;
import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.bottomsheet.TaskAddOption;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.TodolistData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.data.WorkGetallData;
import com.krafte.nebworks.dataInterface.TaskSelectWInterface;
import com.krafte.nebworks.dataInterface.WorkTaskGetallInterface;
import com.krafte.nebworks.databinding.WorkgotofragmentBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.Dlog;
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

public class WorkgotoFragment extends Fragment {
    private final static String TAG = "WorkgotoFragment";
    private WorkgotofragmentBinding binding;
    Context mContext;

    Activity activity;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    FragmentStateAdapter fragmentStateAdapter;
    WorkCalenderAdapter mAdapter;
    ArrayList<TodolistData.TodolistData_list> Todo_mList = new ArrayList<>();
    Tap2ListAdapter Todo_mAdapter = null;

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
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_NAME = "";

    String return_page = "";
    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;

    boolean chng_icon;
    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMPicker = "";

    String change_place_id = "";
    String change_place_name = "";
    String change_member_id = "";
    String change_member_name = "";

    public static WorkgotoFragment newInstance(int number) {
        WorkgotoFragment fragment = new WorkgotoFragment();
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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.workgotofragment, container, false);
        binding = WorkgotofragmentBinding.inflate(inflater);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);
        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            //Singleton Area
            place_id = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
            place_name = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());
            place_owner_id = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());
            place_owner_name = shardpref.getString("place_owner_name", PlaceCheckData.getInstance().getPlace_owner_name());
            place_address = shardpref.getString("place_address", PlaceCheckData.getInstance().getPlace_address());
            place_latitude = shardpref.getString("place_latitude", PlaceCheckData.getInstance().getPlace_latitude());
            place_longitude = shardpref.getString("place_longitude", PlaceCheckData.getInstance().getPlace_longitude());
            place_start_time = shardpref.getString("place_start_time", PlaceCheckData.getInstance().getPlace_start_time());
            place_end_time = shardpref.getString("place_end_time", PlaceCheckData.getInstance().getPlace_end_time());
            place_img_path = shardpref.getString("place_img_path", PlaceCheckData.getInstance().getPlace_img_path());
            place_start_date = shardpref.getString("place_start_date", PlaceCheckData.getInstance().getPlace_start_date());
            place_created_at = shardpref.getString("place_created_at", PlaceCheckData.getInstance().getPlace_created_at());

            USER_INFO_ID = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", UserCheckData.getInstance().getUser_name());
            return_page = shardpref.getString("return_page", ReturnPageData.getInstance().getPage());

            //shardpref Area
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            setBtnEvent();

            change_place_id = place_id;
            change_member_id = "";
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("SELECT_POSITION_sub : " + SELECT_POSITION_sub);

            fragmentStateAdapter = new FragmentStateAdapter(requireActivity(), 1, workGotoList2);
            binding.calenderViewpager.setAdapter(fragmentStateAdapter);
            binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
            binding.calenderViewpager.setOffscreenPageLimit(1);

        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        try {
            cal = Calendar.getInstance();
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            Day = toDay.substring(8, 10);
            binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
            shardpref.putString("calendar_year", Year);
            shardpref.putString("calendar_month", Month);

            dlog.i("onStart chng_icon : " + chng_icon);
            if (chng_icon) {
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                binding.selectArea.setVisibility(View.GONE);
                binding.dateLayout.setVisibility(View.VISIBLE);
                binding.setdate.setText(Year + "년 " + Month + "월");
                binding.line01.setVisibility(View.GONE);
                SetWorkGotoCalenderData();
            } else {
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                binding.selectArea.setVisibility(View.VISIBLE);
                binding.dateLayout.setVisibility(View.VISIBLE);
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                setRecyclerView();
            }
            setAddBtnSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RemoveShared();
    }


    public void setDummyData() {
        binding.taskList.setVisibility(View.VISIBLE);
        Todo_mList = new ArrayList<>();
        Todo_mAdapter = new Tap2ListAdapter(mContext, Todo_mList, 1);
        binding.taskList.setAdapter(mAdapter);
        binding.taskList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mAdapter.addItem(new WorkCalenderData.WorkCalenderData_list(
                "2023년 01월 02일",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1",
                "0"
        ));
    }

    private void RemoveShared() {
        shardpref.remove("return_page");
        shardpref.remove("SET_TASK_TIME_VALUE");
        shardpref.remove("yoillist");
        shardpref.remove("RepeatTF");
        shardpref.remove("RepeatKind");
        shardpref.remove("overdate");
        shardpref.remove("task_no");
        shardpref.remove("picker_year");
        shardpref.remove("picker_month");
        shardpref.remove("picker_day");
        shardpref.remove("input_pop_time");
        shardpref.remove("SET_TASK_TIME_VALUE");
        shardpref.remove("yoillist");
        shardpref.remove("overdate");
        shardpref.remove("item_user_id");
        shardpref.remove("item_user_name");
        shardpref.remove("item_user_img");
        shardpref.remove("item_user_position");
        shardpref.remove("change_place_id");
        shardpref.remove("change_place_name");
        shardpref.remove("calendar_year");
        shardpref.remove("calendar_month");
    }

    int before_pos = 0;
    int calPos = 0;
    int currentPosition = 0;

    private void ScrollState(int kind) {
//        SetWorkGotoCalenderData();
        if (kind == 0) {
            //왼쪽으로 슬라이드 - 버튼으로
            before_pos = 0;
            cal.add(Calendar.MONTH, -1);
            currentPosition = binding.calenderViewpager.getCurrentItem();
            binding.calenderViewpager.setCurrentItem(currentPosition - 1, true);
        } else if (kind == 1) {
            //오른쪽으로 슬라이드 - 버튼으로
            before_pos = 0;
            cal.add(Calendar.MONTH, +1);
            currentPosition = binding.calenderViewpager.getCurrentItem();
            binding.calenderViewpager.setCurrentItem(currentPosition + 1, true);
        } else if (kind == 3) {
            //왼쪽으로 슬라이드
            cal.add(Calendar.MONTH, -1);
        } else if (kind == 4) {
            //왼쪽으로 슬라이드
            cal.add(Calendar.MONTH, +1);
        }
        toDay = sdf.format(cal.getTime());
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        Day = toDay.substring(8, 10);
        getYMPicker = Year + "-" + Month;
        shardpref.putString("calendar_year", Year);
        shardpref.putString("calendar_month", Month);
        binding.setdate.setText(Year + "년 " + Month + "월 ");
        binding.calenderViewpager.setOffscreenPageLimit(1);
    }

    public void setBtnEvent() {

        binding.calenderViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 슬라이드가 끝난 후 작동할 이벤트
//                if (before_pos != position) {
//                    if (before_pos != 0) {
//                        calPos = position - before_pos;
//                        if (calPos > 0) {
//                            ScrollState(4);
//                        } else {
//                            ScrollState(3);
//                        }
//                    }
//                    before_pos = position;
//                }
                binding.calenderViewpager.setUserInputEnabled(false);
            }
        });

        binding.prevDate.setOnClickListener(v -> {
            try {
                if (USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                    if (chng_icon) {
                        ScrollState(0);
                    } else {
                        cal.add(Calendar.DATE, -1);
                        toDay = sdf.format(cal.getTime());
                        Year = toDay.substring(0, 4);
                        Month = toDay.substring(5, 7);
                        Day = toDay.substring(8, 10);
                        getYMPicker = Year + "-" + Month;
                        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    }
                    setRecyclerView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            try {
                if (USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                    if (chng_icon) {
                        ScrollState(1);
                    } else {
                        cal.add(Calendar.DATE, +1);
                        toDay = sdf.format(cal.getTime());
                        Year = toDay.substring(0, 4);
                        Month = toDay.substring(5, 7);
                        Day = toDay.substring(8, 10);
                        getYMPicker = Year + "-" + Month;
                        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    }
                    setRecyclerView();
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
                getYMPicker = Year + "-" + Month;
                shardpref.putString("calendar_year", Year);
                shardpref.putString("calendar_month", Month);
                if (chng_icon) {
                    binding.calenderViewpager.setSaveFromParentEnabled(false);
                    fragmentStateAdapter = new FragmentStateAdapter(requireActivity(), true, Year, Month, 1);
                    binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                    binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                    binding.setdate.setText(Year + "년 " + Month + "월 ");
                } else {
                    binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    setRecyclerView();
                }
                getYMPicker = Year + "년 " + Month + "월 ";
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (binding.setdate.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        binding.changePlaceTv.setText(place_name);
        binding.changePlace.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                PaySelectPlaceActivity psp = new PaySelectPlaceActivity();
                psp.show(getChildFragmentManager(), "PaySelectPlaceActivity");
                psp.setOnClickListener(new PaySelectPlaceActivity.OnClickListener() {
                    @Override
                    public void onClick(View v, String getplace_id, String getplace_name) {
                        if (USER_INFO_AUTH.isEmpty()) {
                            isAuth();
                        } else {
                            toDay = sdf.format(cal.getTime());
                            Year = toDay.substring(0, 4);
                            Month = toDay.substring(5, 7);
                            Day = toDay.substring(8, 10);
                            getYMPicker = Year + "-" + Month + "월 " + Day + "일";
                            change_place_id = getplace_id;
                            change_place_name = getplace_name.isEmpty() ? place_name : getplace_name;
                            SetWorkGotoCalenderData();

                            fragmentStateAdapter = new FragmentStateAdapter(requireActivity(), 1, workGotoList2);
                            binding.calenderViewpager.setAdapter(fragmentStateAdapter);
                            binding.calenderViewpager.setCurrentItem(fragmentStateAdapter.returnPosition(), false);
                            binding.calenderViewpager.setOffscreenPageLimit(1);

                            binding.changePlaceTv.setText(getplace_name);
                            shardpref.putString("change_place_id", getplace_id);
                            shardpref.putString("change_place_name", getplace_name);
                            setRecyclerView();
                            Month = String.valueOf(Integer.parseInt(Month)).length() == 1 ? "0" + String.valueOf(Integer.parseInt(Month)) : String.valueOf(Integer.parseInt(Month));
                            binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                        }


                    }
                });
            }
        });

        binding.changeMember.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                PaySelectMemberActivity psm = new PaySelectMemberActivity();
                psm.show(getParentFragmentManager(), "PaySelectMemberActivity");
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
                            shardpref.putString("change_member_id", user_id);
                            shardpref.putString("change_member_name", user_name);
                        }
                        dlog.i("change_member_id : " + user_id);
                        dlog.i("change_member_name : " + user_name);
                        setRecyclerView();
                    }
                });
            }
        });

        binding.changeIcon.setOnClickListener(v -> {
            cal = Calendar.getInstance();
            toDay = sdf.format(cal.getTime());
            Year = toDay.substring(0, 4);
            Month = toDay.substring(5, 7);
            Day = toDay.substring(8, 10);

            binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
//            binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
            if (!chng_icon) {
                chng_icon = true;
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                binding.selectArea.setVisibility(View.GONE);
                binding.dateLayout.setVisibility(View.VISIBLE);
                binding.setdate.setText(Year + "년 " + Month + "월");
                binding.line01.setVisibility(View.GONE);
            } else {
                chng_icon = false;
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                binding.selectArea.setVisibility(View.VISIBLE);
                binding.dateLayout.setVisibility(View.VISIBLE);
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                setRecyclerView();
            }
        });


//        binding.taskList.setOnTouchListener(new OnSwipeTouchListener(mContext) {
//            @Override
//            public void onSwipeLeft() {
//                setCalender(1);
//            }
//
//            @Override
//            public void onSwipeRight() {
//                setCalender(-1);
//            }
//        });
    }

    //미처리인 업무 세기
    int state_null = 0;

    public void setRecyclerView() {
        String select_date = binding.setdate.getText().toString().replace("년 ", "-").replace("월 ", "-").replace("일", "");
        if (change_member_id.isEmpty()) {
            change_member_id = USER_INFO_ID;
        }
        if (change_place_id.isEmpty()) {
            change_place_id = place_id;
        }
        if (chng_icon && select_date.length() > 7) {
            select_date = select_date.substring(0, 7);
        }
        dlog.i("----------setRecyclerView setTodoWList----------");
        dlog.i("setTodoWList place_id : " + change_place_id);
        dlog.i("setTodoWList USER_INFO_ID : " + change_member_id);
        dlog.i("setTodoWList select_date : " + select_date);
        dlog.i("setTodoWList USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("----------setRecyclerView setTodoWList----------");

        Todo_mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectWInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectWInterface api = retrofit.create(TaskSelectWInterface.class);
        Call<String> call = api.getData(change_place_id, change_member_id, select_date.replace("월", ""), USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment2 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                dlog.e("response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(rc.getBase64decode(response.body()));
                        Todo_mList = new ArrayList<>();
                        Todo_mAdapter = new Tap2ListAdapter(mContext, Todo_mList, 1);
                        binding.taskList.setAdapter(Todo_mAdapter);
                        binding.taskList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        Log.i(TAG, "GET SIZE : " + Response.length());

                        if (USER_INFO_AUTH.isEmpty()) {
                            binding.line01.setVisibility(View.VISIBLE);
                            binding.nodataArea.setVisibility(View.GONE);
                            Todo_mAdapter.addItem(new TodolistData.TodolistData_list(
                                    "121",
                                    "143",
                                    "0",
                                    "할 일",
                                    "할일에 대한 설명이에요",
                                    "1",
                                    new ArrayList<String>(Collections.singleton("[{\"user_id\":\"77\",\"user_name\":\"김준호\",\"img_path\":\"null\",\"jikgup\":\"\\uc815\\uc9c1\\uc6d0\"},{\"user_id\":\"89\",\"user_name\":\"박찬성\",\"img_path\":\"null\",\"jikgup\":\"\\ub9e4\\ub2c8\\uc800\"},{\"user_id\":\"102\",\"user_name\":\"최치호\",\"img_path\":null,\"jikgup\":\"\\ub9e4\\ub2c8\\uc800\"},{\"user_id\":\"115\",\"user_name\":\"\\ud06c\\ub798\\ud504\\ud2b8\\uace0\\uac1d\\uc9c0\\uc6d0\\ud300\",\"img_path\":\"null\",\"jikgup\":null}],\"task_date\":\"2023-01-02\",\"start_time\":\"2023-01-02 6:9\",\"end_time\":\"2023-01-02 18:09\",\"sun\":\"0\",\"mon\":\"0\",\"tue\":\"0\",\"wed\":\"0\",\"thu\":\"0\",\"fri\":\"0\",\"sat\":\"0\",\"img_path\":null,\"complete_yn\":null,\"incomplete_reason\":null,\"approval_state\":\"3\",\"task_overdate\":\"\",\"reject_reason\":null,\"updated_at\":null}]")),
                                    "2023-01-01",
                                    "2023-01-01 01:00",
                                    "2023-01-01 23:00",
                                    "0",
                                    "1",
                                    "1",
                                    "1",
                                    "1",
                                    "1",
                                    "0",
                                    "",
                                    "y",
                                    "",
                                    "0",
                                    "2023-01-01",
                                    "0",
                                    "2023-01-01"
                            ));
                        } else {
                            if (Response.length() == 0) {
                                binding.nodataArea.setVisibility(View.VISIBLE);
                                binding.line01.setVisibility(View.GONE);
                                Log.i(TAG, "SetNoticeListview Thread run! ");
                                Log.i(TAG, "GET SIZE : " + Response.length());
                            } else {
                                binding.line01.setVisibility(View.VISIBLE);
                                binding.nodataArea.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (USER_INFO_AUTH.equals("0")) {
                                        Todo_mAdapter.addItem(new TodolistData.TodolistData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("writer_id"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("title"),
                                                jsonObject.getString("contents"),
                                                jsonObject.getString("complete_kind"),
                                                Collections.singletonList(jsonObject.getString("users")),
                                                jsonObject.getString("task_date"),
                                                jsonObject.getString("start_time"),
                                                jsonObject.getString("end_time"),
                                                jsonObject.getString("sun"),
                                                jsonObject.getString("mon"),
                                                jsonObject.getString("tue"),
                                                jsonObject.getString("wed"),
                                                jsonObject.getString("thu"),
                                                jsonObject.getString("fri"),
                                                jsonObject.getString("sat"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("complete_yn"),
                                                jsonObject.getString("incomplete_reason"),
                                                jsonObject.getString("approval_state"),
                                                jsonObject.getString("task_overdate"),
                                                jsonObject.getString("reject_reason"),
                                                jsonObject.getString("updated_at")
                                        ));
                                    } else {
                                        if (!jsonObject.getString("id").isEmpty() || !jsonObject.getString("id").equals("null")) {
                                            Todo_mAdapter.addItem(new TodolistData.TodolistData_list(
                                                    jsonObject.getString("id"),
                                                    jsonObject.getString("writer_id"),
                                                    jsonObject.getString("kind"),
                                                    jsonObject.getString("title"),
                                                    jsonObject.getString("contents"),
                                                    jsonObject.getString("complete_kind"),
                                                    Collections.singletonList(jsonObject.getString("users")),
                                                    jsonObject.getString("task_date"),
                                                    jsonObject.getString("start_time"),
                                                    jsonObject.getString("end_time"),
                                                    jsonObject.getString("sun"),
                                                    jsonObject.getString("mon"),
                                                    jsonObject.getString("tue"),
                                                    jsonObject.getString("wed"),
                                                    jsonObject.getString("thu"),
                                                    jsonObject.getString("fri"),
                                                    jsonObject.getString("sat"),
                                                    jsonObject.getString("img_path"),
                                                    jsonObject.getString("complete_yn"),
                                                    jsonObject.getString("incomplete_reason"),
                                                    jsonObject.getString("approval_state"),
                                                    jsonObject.getString("task_overdate"),
                                                    jsonObject.getString("reject_reason"),
                                                    jsonObject.getString("updated_at")
                                            ));
                                        }
                                    }
                                }
                                for (int a = 0; a < Response.length(); a++) {
                                    dlog.i("approval_state 1 : " + Response.getJSONObject(a).getString("approval_state"));
                                    if (Response.getJSONObject(a).getString("approval_state").equals("3")
                                            || Response.getJSONObject(a).getString("approval_state").equals("null")) {
                                        if (!Response.getJSONObject(a).getString("id").isEmpty() || !Response.getJSONObject(a).getString("id").equals("null")) {
                                            state_null++;
                                        }
                                    }
                                }
                                dlog.i("state_null : " + state_null);
                                Todo_mAdapter.notifyDataSetChanged();
                            }
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

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private boolean isDragging = false;

    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("할일추가");

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
                        dlog.i("MotionEvent ACTION_DOWN");
                        initialX = v.getLeft();
                        initialY = v.getTop();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        isDragging = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        dlog.i("MotionEvent ACTION_MOVE");
                        if (!isDragging) {
                            isDragging = true;
                        }

                        int dx = (int) (event.getRawX() - initialTouchX);
                        int dy = (int) (event.getRawY() - initialTouchY);

                        newX = initialX + dx;
                        newY = initialY + dy;

                        if (lastnewX == 0) {
                            lastnewX = newX;
                        }
                        if (lastnewY == 0) {
                            lastnewY = newY;
                        }

                        dlog.i("newX : " + newX);
                        dlog.i("newY : " + newY);

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
                        dlog.i("MotionEvent ACTION_UP");
                        lastAction = MotionEvent.ACTION_UP;
                        int Xdistance = (newX - lastnewX);
                        int Ydistance = (newY - lastnewY);
                        if (Math.abs(Xdistance) < 10 && Math.abs(Ydistance) < 10) {
                            if (USER_INFO_AUTH.isEmpty()) {
                                isAuth();
                            } else {
                                TaskAddOption to = new TaskAddOption();
                                to.show(getChildFragmentManager(), "TaskAddOption");
                            }
                        } else {
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


    ArrayList<WorkGetallData.WorkGetallData_list> workGotoList2 = new ArrayList<>();
    private void SetWorkGotoCalenderData() {
        workGotoList2.clear();
        String USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        String getYMDate = Year + "-" + Month;
        Log.i(TAG, "------SetWorkGotoCalenderData------");
        Log.i(TAG, "change_place_id : " + change_place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + getYMDate);
        Log.i(TAG, "USER_INFO_AUTH : " + USER_INFO_AUTH);
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
                            fragmentStateAdapter = new FragmentStateAdapter(requireActivity(), 1, workGotoList2);
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

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, null);
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag", "더미");
        intent.putExtra("data", "먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

//    @Override
//    public void onButtonClicked(int kind) {
//        dlog.i("kind : " + kind);
//        int currentPosition = binding.calenderViewpager.getCurrentItem();
//        if (kind == -1) {
//            // ViewPager2를 오른쪽으로 이동
//            binding.calenderViewpager.setCurrentItem(currentPosition - 1, true);
//        } else {
//            // ViewPager2를 오른쪽으로 이동
//            binding.calenderViewpager.setCurrentItem(currentPosition + 1, true);
//        }
//    }
}