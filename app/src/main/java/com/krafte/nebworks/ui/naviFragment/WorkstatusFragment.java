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
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkStatusCalenderAdapter;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.bottomsheet.WorkstatusBottomSheet;
import com.krafte.nebworks.data.CalendarSetStatusData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.MainContentsInterface;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkstatusCalendersetData;
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
            binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
            binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
            SELECT_POSITION = 1;
            fg = WorkStatusSubFragment1.newInstance();
            setChildFragment(fg);
        } else if (SELECT_POSITION_sub == 1) {
            binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
            binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
            SELECT_POSITION = 2;
            fg = WorkStatusSubFragment2.newInstance();
            setChildFragment(fg);
        } else if (SELECT_POSITION_sub == 2) {
            binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
            binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
            SELECT_POSITION = 3;
            fg = WorkStatusSubFragment3.newInstance();
            setChildFragment(fg);
        } else if (SELECT_POSITION_sub == 3) {
            binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
            SELECT_POSITION = 4;
            fg = WorkStatusSubFragment4.newInstance();
            setChildFragment(fg);
        }
    }

    private void SetCalenderData(String Year, String Month) {
        mList2.clear();
        dlog.i("------SetCalenderData------");
        dlog.i("place_id :" + place_id);
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("getYMPicker :" + getYMPicker);
        dlog.i("------SetCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkstatusCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkstatusCalendersetData api = retrofit.create(WorkstatusCalendersetData.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, Year, Month);
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
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                dlog.i("GET SIZE : " + Response2.length());
                                GetCalenderList(Year, Month, mList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    mList2.add(new CalendarSetStatusData.CalendarSetStatusData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("users"))
                                    ));
                                }
                                GetCalenderList(Year, Month, mList2);
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

    public void GetCalenderList(String Year, String Month, ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mList2) {
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
//                        String jsonResponse = rc.getBase64decode(response.body());
//                        dlog.i("jsonResponse length : " + jsonResponse.length());
//                        dlog.i("jsonResponse : " + jsonResponse);
                        dlog.i("onResume place_id :" + place_id);
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
                        dlog.i("onResume getYMPicker :" + getYMPicker);
                        dlog.i("onResume mList2 :" + mList2);
                        try {
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            mList = new ArrayList<>();
                            mAdapter = new WorkStatusCalenderAdapter(mContext, mList, mList2, place_id, USER_INFO_ID, select_date);
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
                                mAdapter.setOnItemClickListener(new WorkStatusCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        try {
                                            if(USER_INFO_AUTH.isEmpty()) {
                                                isAuth();
                                            } else {
                                                dlog.i("onItemClick WorkDay :" + WorkDay);
                                                shardpref.putString("FtoDay", WorkDay);
                                                WorkstatusBottomSheet wsb = new WorkstatusBottomSheet();
                                                wsb.show(getChildFragmentManager(), "WorkstatusBottomSheet");
                                            }
                                        } catch (Exception e) {
                                            dlog.i("onItemClick Exception :" + e);
                                        }

                                    }
                                });
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
        binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");

        SetCalenderData(Year, Month);

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
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
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
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
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
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
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
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
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
                SetCalenderData(Year, Month);
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
                SetCalenderData(Year, Month);
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
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.setdate.setText(Year + "년 " + Month + "월 " + Day + "일");
                getYMPicker = Year + "년 " + Month + "월 ";
                SendToday();
                SetCalenderData(String.valueOf(year), Month);
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
                binding.setdate.setText(Year + "년 " + Month + "월");
                SetCalenderData(Year, Month);
            } else {
                chng_icon = false;
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
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
                SetCalenderData(Year, Month);
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
    }

    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.status_child_fragment_container, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }


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
                                        binding.cnt02.setText(Response.getJSONObject(0).getString("absence_cnt"));
                                        binding.cnt03.setText(Response.getJSONObject(0).getString("o_cnt"));
                                        binding.cnt04.setText(Response.getJSONObject(0).getString("rest_cnt"));
                                        dlog.i("-----MainData-----");
                                        dlog.i("i_cnt : " + Response.getJSONObject(0).getString("i_cnt"));
                                        dlog.i("o_cnt : " + Response.getJSONObject(0).getString("o_cnt"));
                                        dlog.i("absence_cnt : " + Response.getJSONObject(0).getString("absence_cnt"));
                                        dlog.i("rest_cnt : " + Response.getJSONObject(0).getString("rest_cnt"));
                                        shardpref.putString("i_cnt", Response.getJSONObject(0).getString("i_cnt"));
                                        dlog.i("------PlaceWorkCheck-------");
                                    } catch (Exception e) {
                                        dlog.i("UserCheck Exception : " + e);
                                    }
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

    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("근무추가");
        add_worktime_btn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                pm.AddWorkPart(mContext);
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
