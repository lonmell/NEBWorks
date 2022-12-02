package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkStatusCalenderAdapter;
import com.krafte.nebworks.data.CalendarSetStatusData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.MainWorkCntInterface;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkstatusCalendersetData;
import com.krafte.nebworks.databinding.WorkstatusfragmentBinding;
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
    String gYear = "";
    String gMonth = "";
    String bYear = "";
    String bMonth = "";

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

        setBtnEvent();

        //UI 데이터 세팅
        try {
            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub",0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");

            PlaceWorkCheck(place_id);

            if(SELECT_POSITION_sub == 0){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment1.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 1){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment2.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 2){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment3.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 3){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
                fg = WorkStatusSubFragment4.newInstance();
                setChildFragment(fg);
            }

            binding.statusFragmentbtn1.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment1.newInstance();
                setChildFragment(fg);
            });
            binding.statusFragmentbtn2.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment2.newInstance();
                setChildFragment(fg);
            });
            binding.statusFragmentbtn3.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment3.newInstance();
                setChildFragment(fg);
            });
            binding.statusFragmentbtn4.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
                fg = WorkStatusSubFragment4.newInstance();
                setChildFragment(fg);
            });

            cal = Calendar.getInstance();
            toDay = sdf.format(cal.getTime());
            dlog.i("오늘 :" + toDay);
            binding.setdate.setText(toDay);
            shardpref.putString("FtoDay",toDay);
            gYear = toDay.substring(0,4);
            gMonth = toDay.substring(5,7);
            SetCalenderData(gYear,gMonth);

            binding.prevDate.setOnClickListener(v -> {
                cal.add(Calendar.DATE, -1);
                toDay = sdf.format(cal.getTime());
                binding.setdate.setText(toDay);
                shardpref.putString("FtoDay",toDay);
                gYear = toDay.substring(0,4);
                gMonth = toDay.substring(5,7);
                if(!gYear.equals(gYear) || !bMonth.equals(gMonth)){
                    dlog.i("gYear : " + gYear);
                    dlog.i("bYear : " + bYear);
                    dlog.i("gMonth : " + gMonth);
                    dlog.i("bMonth : " + bMonth);
                    bYear = gYear;
                    bMonth = gMonth;
                    SetCalenderData(gYear,gMonth);
                }
            });
            binding.nextDate.setOnClickListener(v -> {
                cal.add(Calendar.DATE, +1);
                toDay = sdf.format(cal.getTime());
                binding.setdate.setText(toDay);
                shardpref.putString("FtoDay",toDay);
                gYear = toDay.substring(0,4);
                gMonth = toDay.substring(5,7);
                if(!gYear.equals(gYear) || !bMonth.equals(gMonth)){
                    dlog.i("gYear : " + gYear);
                    dlog.i("bYear : " + bYear);
                    dlog.i("gMonth : " + gMonth);
                    dlog.i("bMonth : " + bMonth);
                    bYear = gYear;
                    bMonth = gMonth;
                    SetCalenderData(gYear,gMonth);
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
                    binding.setdate.setText(year +"-" + Month + "-" + Day);
                    getYMPicker = binding.setdate.getText().toString().substring(0,7);
                    shardpref.putString("FtoDay",toDay);
                    SetCalenderData(String.valueOf(year),Month);
                }
            }, mYear, mMonth, mDay);

            binding.setdate.setOnClickListener(view -> {
                if (binding.setdate.isClickable()) {
                    datePickerDialog.show();
                }
            });
            binding.addWorktimeBtn.setOnClickListener(v -> {
                pm.AddWorkPart(mContext);
            });
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
        return binding.getRoot();
//        return rootView;
    }

    private void SetCalenderData(String Year,String Month){
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
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, Year,Month);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                Log.e(TAG, "SetCalenderData function START");
                Log.e(TAG, "response 1: " + response2.isSuccessful());
                Log.e(TAG, "response 2: " + (response2.body() != null ? response2.body().length() : 0));
                Log.e(TAG, "response 3: " + response2.body());
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        try {
                            JSONArray Response2 = new JSONArray(response2.body());
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

    ArrayList<String> user_id = new ArrayList<>();
    ArrayList<String> user_name = new ArrayList<>();
    ArrayList<String> img_path = new ArrayList<>();
    ArrayList<String> jikgup = new ArrayList<>();
    ArrayList<String> worktime = new ArrayList<>();
    ArrayList<String> workyoil = new ArrayList<>();
    public void GetCalenderList(String Year, String Month, ArrayList<CalendarSetStatusData.CalendarSetStatusData_list> mList2) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalenderInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalenderInterface api = retrofit.create(WorkCalenderInterface.class);
        Call<String> call = api.getData(Year,Month);
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
                        try{
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
                            }
                        }catch (JSONException e){
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("FtoDay");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setBtnEvent() {
        binding.changeIcon.setOnClickListener(v -> {
            if(!chng_icon){
                chng_icon = true;
                binding.calendarArea.setVisibility(View.VISIBLE);
                binding.changeIcon.setBackgroundResource(R.drawable.list_up_icon);
                SetCalenderData(gYear,gMonth);
            }else{
                chng_icon = false;
                binding.calendarArea.setVisibility(View.GONE);
                binding.changeIcon.setBackgroundResource(R.drawable.calendar_resize);
            }
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
                .baseUrl(MainWorkCntInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainWorkCntInterface api = retrofit.create(MainWorkCntInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
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
//                                    i_cnt;                // 출근 count(퇴근한 인원은 제외)
//                                    o_cnt;                // 퇴근 count
//                                    task_total_cnt;       // 할일 전체
//                                    task_complete_cnt;    // 완료된 업무
//                                    task_incomplete_cnt;  // 미완료 업무
//                                    approval_total_cnt;   // 결재 전체
//                                    waiting_cnt;          // 결재 대기
//                                    approval_cnt;         // 결재 승인
//                                    reject_cnt;           // 결재 반려
//                                    rest_cnt              // 휴무 직원 수
//                                     absence_cnt           // 결석
                                    try {
                                        String i_cnt = Response.getJSONObject(0).getString("i_cnt");
                                        String o_cnt = Response.getJSONObject(0).getString("o_cnt");
                                        String task_total_cnt = Response.getJSONObject(0).getString("task_total_cnt");
                                        String task_complete_cnt = Response.getJSONObject(0).getString("task_complete_cnt"); //-- 가입할때의 게정
                                        String task_incomplete_cnt = Response.getJSONObject(0).getString("task_incomplete_cnt"); //-- 사번
                                        String approval_total_cnt = Response.getJSONObject(0).getString("approval_total_cnt");
                                        String waiting_cnt = Response.getJSONObject(0).getString("waiting_cnt");
                                        String approval_cnt = Response.getJSONObject(0).getString("approval_cnt");
                                        String reject_cnt = Response.getJSONObject(0).getString("reject_cnt");
                                        String rest_cnt = Response.getJSONObject(0).getString("rest_cnt");
                                        String absence_cnt = Response.getJSONObject(0).getString("absence_cnt");

                                        dlog.i("------PlaceWorkCheck-------");
                                        dlog.i("출근 count(퇴근한 인원은 제외) : " + i_cnt);
                                        dlog.i("퇴근 count : " + o_cnt);
                                        dlog.i("할일 전체 : " + task_total_cnt);
                                        dlog.i("완료된 업무 : " + task_complete_cnt);
                                        dlog.i("미완료 업무 : " + task_incomplete_cnt);
                                        dlog.i("결재 전체 : " + approval_total_cnt);
                                        dlog.i("결재 대기 : " + waiting_cnt);
                                        dlog.i("결재 승인 : " + approval_cnt);
                                        dlog.i("결재 반려 : " + reject_cnt);
                                        dlog.i("휴무 : " + rest_cnt);
                                        dlog.i("결석/미출근 : " + absence_cnt);
                                        int total_cnt = 0;
                                        total_cnt = Integer.parseInt(i_cnt) + Integer.parseInt(o_cnt) + Integer.parseInt(task_total_cnt)
                                                + Integer.parseInt(task_complete_cnt) + Integer.parseInt(task_incomplete_cnt) + Integer.parseInt(approval_total_cnt)
                                                + Integer.parseInt(waiting_cnt) + Integer.parseInt(approval_cnt) + Integer.parseInt(reject_cnt);

                                        binding.cnt01.setText(i_cnt);
                                        binding.cnt02.setText(absence_cnt);
                                        binding.cnt03.setText(o_cnt);
                                        binding.cnt04.setText(rest_cnt);
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
}
