package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.Tap2ListAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.bottomsheet.PaySelectMemberActivity;
import com.krafte.nebworks.bottomsheet.PaySelectPlaceActivity;
import com.krafte.nebworks.bottomsheet.TaskAddOption;
import com.krafte.nebworks.bottomsheet.WorkgotoBottomSheet;
import com.krafte.nebworks.data.CalendarSetData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.TodolistData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.TaskSelectWInterface;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkCalendersetData;
import com.krafte.nebworks.databinding.WorkgotofragmentBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Timer;

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
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;

    WorkCalenderAdapter mAdapter;
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList;
    //Task all data
    ArrayList<CalendarSetData.CalendarSetData_list> mList2 = new ArrayList<>();


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
    int rotate_addwork = 0;
    String tap_kind = "";
    Fragment fg;

    boolean chng_icon = false;
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
            place_id            = PlaceCheckData.getInstance().getPlace_id();
            place_name          = PlaceCheckData.getInstance().getPlace_name();
            place_owner_id      = PlaceCheckData.getInstance().getPlace_owner_id();
            place_owner_name    = PlaceCheckData.getInstance().getPlace_owner_name();
            place_address       = PlaceCheckData.getInstance().getPlace_address();
            place_latitude      = PlaceCheckData.getInstance().getPlace_latitude();
            place_longitude     = PlaceCheckData.getInstance().getPlace_longitude();
            place_start_time    = PlaceCheckData.getInstance().getPlace_start_time();
            place_end_time      = PlaceCheckData.getInstance().getPlace_end_time();
            place_img_path      = PlaceCheckData.getInstance().getPlace_img_path();
            place_start_date    = PlaceCheckData.getInstance().getPlace_start_date();
            place_created_at    = PlaceCheckData.getInstance().getPlace_created_at();

            USER_INFO_ID        = UserCheckData.getInstance().getUser_id();
            USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");
            USER_INFO_NAME      = UserCheckData.getInstance().getUser_name();
            return_page         = ReturnPageData.getInstance().getPage();

            //shardpref Area
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            setBtnEvent();

            change_place_id = place_id;
            change_member_id = "";
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("PlaceWorkActivity SELECT_POSITION_sub : " + SELECT_POSITION_sub);

            if (SELECT_POSITION_sub == 0) {
                chng_icon = false;
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                binding.selectArea.setVisibility(View.VISIBLE);
                if (USER_INFO_AUTH.isEmpty()) {
                    setDummyData();
                } else {
                    setRecyclerView();
                }
            } else if (SELECT_POSITION_sub == 1) {
                chng_icon = true;
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                binding.selectArea.setVisibility(View.GONE);
                SetCalenderData();
            }
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            //기존에 저장된 반복데이터 삭제
            shardpref.remove("return_page");
            shardpref.remove("SET_TASK_TIME_VALUE");
            shardpref.remove("yoillist");
            shardpref.remove("RepeatTF");
            shardpref.remove("RepeatKind");
            shardpref.remove("overdate");
            RemoveShared();
            setAddBtnSetting();
            setRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setDummyData() {
        binding.taskList.setVisibility(View.VISIBLE);
        Todo_mList = new ArrayList<>();
        Todo_mAdapter = new Tap2ListAdapter(mContext, Todo_mList, getParentFragmentManager(), 1);
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
        shardpref.remove("change_member_id");
        shardpref.remove("change_member_name");
        shardpref.remove("change_place_id");
        shardpref.remove("change_place_name");
    }

    public void setBtnEvent() {
        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        Day = toDay.substring(8, 10);
        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");

        binding.prevDate.setOnClickListener(v -> {
            try {
//                String getDate = binding.setdate.getText().toString().replace("년 ", "-").replace("월 ", "-").replace("일", "");
//                // 문자열 -> Date
//                Date date = sdf.parse(getDate);
//                dlog.i("Calendar.DATE : " + sdf.format(date));
                if (USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                    if (chng_icon) {
                        cal.add(Calendar.MONTH, -1);
                        toDay = sdf.format(cal.getTime());
                        Year = toDay.substring(0, 4);
                        Month = toDay.substring(5, 7);
                        Day = toDay.substring(8, 10);
                        getYMPicker = Year + "-" + Month;
                        binding.setdate.setText(Year + "년 " + Month + "월 ");
                    } else {
                        cal.add(Calendar.DATE, -1);
                        toDay = sdf.format(cal.getTime());
                        Year = toDay.substring(0, 4);
                        Month = toDay.substring(5, 7);
                        Day = toDay.substring(8, 10);
                        getYMPicker = Year + "-" + Month;
                        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    }
                    SetCalenderData();
                    setRecyclerView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            try {
//                String getDate = binding.setdate.getText().toString().replace("년 ", "-").replace("월 ", "-").replace("일", "");
//                // 문자열 -> Date
//                Date date = sdf.parse(getDate);
//                dlog.i("Calendar.DATE : " + sdf.format(date));
                if (USER_INFO_AUTH.isEmpty()) {
                    isAuth();
                } else {
                    if (chng_icon) {
                        cal.add(Calendar.MONTH, +1);
                        toDay = sdf.format(cal.getTime());
                        Year = toDay.substring(0, 4);
                        Month = toDay.substring(5, 7);
                        Day = toDay.substring(8, 10);
                        getYMPicker = Year + "-" + Month;
                        binding.setdate.setText(Year + "년 " + Month + "월 ");
                    } else {
                        cal.add(Calendar.DATE, +1);
                        toDay = sdf.format(cal.getTime());
                        Year = toDay.substring(0, 4);
                        Month = toDay.substring(5, 7);
                        Day = toDay.substring(8, 10);
                        getYMPicker = Year + "-" + Month;
                        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                    }
                    SetCalenderData();
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
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                getYMPicker = Year + "년 " + Month + "월 ";
                SetCalenderData();
                setRecyclerView();
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
                        }
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
                        SetCalenderData();
                        setRecyclerView();

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
                        SetCalenderData();
                        setRecyclerView();
                    }
                });
            }
        });

        binding.changeIcon.setOnClickListener(v -> {
            if (!chng_icon) {
                chng_icon = true;
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                binding.selectArea.setVisibility(View.GONE);
                binding.setdate.setText(Year + "년 " + Month + "월");
                SetCalenderData();
            } else {
                chng_icon = false;
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
                binding.selectArea.setVisibility(View.VISIBLE);
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                setRecyclerView();
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
    ArrayList<String> task_overdate = new ArrayList<>();

    ArrayList<String> write_name = new ArrayList<>();
    ArrayList<String> end = new ArrayList<>();

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
                            mList = new ArrayList<>();
                            mAdapter = new WorkCalenderAdapter(mContext, mList, mList2, place_id, USER_INFO_ID, select_date);
                            binding.createCalender.setAdapter(mAdapter);
                            binding.createCalender.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            dlog.i("SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                dlog.i("GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new WorkCalenderData.WorkCalenderData_list(
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
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener(new WorkCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        dlog.i("data :" + data);
                                        try {
                                            if (USER_INFO_AUTH.isEmpty()) {
                                                isAuth();
                                            } else {
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
                                                wgb.show(getChildFragmentManager(), "WorkgotoBottomSheet");
                                            }
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


    //미처리인 업무 세기
    int state_null = 0;
    String writer_id = "";

    public void setRecyclerView() {
        String select_date = binding.setdate.getText().toString().replace("년 ", "-").replace("월 ", "-").replace("일", "");
        if (change_member_id.isEmpty()) {
            change_member_id = USER_INFO_ID;
        }
        dlog.i("setTodoWList place_id : " + change_place_id);
        dlog.i("setTodoWList USER_INFO_ID : " + change_member_id);
        dlog.i("setTodoWList select_date : " + select_date);
        dlog.i("setTodoWList USER_INFO_AUTH : " + USER_INFO_AUTH);
        Todo_mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectWInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectWInterface api = retrofit.create(TaskSelectWInterface.class);
        Call<String> call = api.getData(change_place_id, change_member_id, select_date, USER_INFO_AUTH);
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
                        Todo_mAdapter = new Tap2ListAdapter(mContext, Todo_mList, getParentFragmentManager(), 1);
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
                                binding.line01.setVisibility(View.INVISIBLE);
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
                                Todo_mAdapter.setOnItemClickListener((v, position, Tcnt, Fcnt) -> {
                                    try {
                                        writer_id = Response.getJSONObject(position).getString("writer_id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
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

    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("할일추가");
        add_worktime_btn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                TaskAddOption to = new TaskAddOption();
                to.show(getChildFragmentManager(), "TaskAddOption");
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
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
