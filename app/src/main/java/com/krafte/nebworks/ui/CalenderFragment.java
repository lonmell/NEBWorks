package com.krafte.nebworks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.CalendarDayAdaper;
import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.adapter.WorkStatusCalenderAdapter;
import com.krafte.nebworks.bottomsheet.WorkgotoBottomSheet;
import com.krafte.nebworks.bottomsheet.WorkstatusBottomSheet;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkGetallData;
import com.krafte.nebworks.dataInterface.PayGetallInterface;
import com.krafte.nebworks.dataInterface.WorkStatusGetallInterface;
import com.krafte.nebworks.dataInterface.WorkTaskGetallInterface;
import com.krafte.nebworks.databinding.FragmentCalenderBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CalenderFragment extends Fragment {
    private final static String TAG = "CalenderFragment";
    Timer timer = new Timer();

    String USER_INFO_ID = "";
    String place_id = "";
    DateCurrent dc = new DateCurrent();

    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    WorkCalenderAdapter workCalenderAdapter;
    WorkStatusCalenderAdapter workStatusCalenderAdapter;
    Dlog dlog = new Dlog();
    private FragmentCalenderBinding binding = null;

    Context mContext;
    Activity activity;
    String year;
    String month;
    int state;

    String change_place_id = "";
    String change_member_id = "";

    //근무현황 어댑터
    CalendarDayAdaper cadayAdapter1, cadayAdapter2, cadayAdapter3, cadayAdapter4, cadayAdapter5, cadayAdapter6, cadayAdapter7;
    ArrayList<WorkGetallData.WorkGetallData_list> sendList = new ArrayList<>();

    List<String> allDate = new ArrayList<>();
    //월
    List<String> monDate = new ArrayList<>();
    //화
    List<String> tueDate = new ArrayList<>();
    //수
    List<String> wedDate = new ArrayList<>();
    //목
    List<String> thuDate = new ArrayList<>();
    //금
    List<String> friDate = new ArrayList<>();
    //토
    List<String> satDate = new ArrayList<>();
    //일
    List<String> sunDate = new ArrayList<>();
    String SetDay = "";


    // state 1: WorkGoto
    public CalenderFragment(String year, String month, int state, ArrayList<WorkGetallData.WorkGetallData_list> sendList) {
        this.year = year;
        this.month = month;
        this.state = state;
        this.sendList = sendList;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalenderBinding.inflate(inflater);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        USER_INFO_ID = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
        place_id = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());

        shardpref.putString("calendar_year", year);
        shardpref.putString("calendar_month", month);

        binding.calendarYear.setText(year + "년");
        binding.calendarMonth.setText(month + "월");

        dlog.i("onCreateView sendList : " + sendList);

        return binding.getRoot();
    }

    private int getWeekOfYear(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        dlog.i("getWeekOfYear date : " + String.valueOf(year) + "-" + (String.valueOf(month).length() == 1 ? "0" + String.valueOf(month) : String.valueOf(month)) + "-" + (String.valueOf(day).length() == 1 ? "0" + String.valueOf(day) : String.valueOf(day)));
        calendar.set(year, month - 1, Integer.parseInt(String.valueOf(day).length() == 1 ? "0" + String.valueOf(day) : String.valueOf(day)));
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    @Override
    public void onStart() {
        super.onStart();

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
        // 3. 숫자 요일 구하기
        dlog.i(month + "월은 : 1 ~ " + cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "까지");
        for (int i = 0; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            allDate.add(i, String.valueOf(i + 1));
        }

        monDate.add(0, "");
        tueDate.add(0, "");
        wedDate.add(0, "");
        thuDate.add(0, "");
        friDate.add(0, "");
        satDate.add(0, "");
        sunDate.add(0, "");

        for (int i = 0; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(allDate.get(i)));
//            System.out.println(date);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            int dayOfWeekNumber = dayOfWeek.getValue();
            switch (dayOfWeekNumber) {
                case 1:
                    SetDay = year + "-" + month + "-" + allDate.get(i);
                    dlog.i("monDate SetDay : " + SetDay);
                    dlog.i("monDate getWeekOfYear(SetDay) : " + getWeekOfYear(SetDay));
                    monDate.add(getWeekOfYear(SetDay) - 1, allDate.get(i));
                    break;
                case 2:
                    SetDay = year + "-" + month + "-" + allDate.get(i);
                    tueDate.add(getWeekOfYear(SetDay) - 1, allDate.get(i));
                    break;
                case 3:
                    SetDay = year + "-" + month + "-" + allDate.get(i);
                    wedDate.add(getWeekOfYear(SetDay) - 1, allDate.get(i));
                    break;
                case 4:
                    SetDay = year + "-" + month + "-" + allDate.get(i);
                    thuDate.add(getWeekOfYear(SetDay) - 1, allDate.get(i));
                    break;
                case 5:
                    SetDay = year + "-" + month + "-" + allDate.get(i);
                    friDate.add(getWeekOfYear(SetDay) - 1, allDate.get(i));
                    break;
                case 6:
                    SetDay = year + "-" + month + "-" + allDate.get(i);
                    satDate.add(getWeekOfYear(SetDay) - 1, allDate.get(i));
                    break;
                case 7:
                    SetDay = year + "-" + month + "-" + allDate.get(i);
                    sunDate.add(getWeekOfYear(SetDay) - 1, allDate.get(i));
                    break;
            }
        }
        dlog.i("state: " + state);
        SetCalendar();
    }


    @Override
    public void onResume(){
        super.onResume();
        dlog.i("CalenderFragment onResume state : " + state);
        if(sendList.size() == 0){
            switch (state) {
                case 1:
                case 3:
                    SetWorkGotoCalenderData();
                    break;
                case 2:
                    SetWorkStatusCalenderData();
                    break;
                case 4:
                    SetPayCalenderData();
                    break;
            }
            SetCalendar();
        }

    }

    private void SetClickEvent(String WorkDay) {
        switch (state) {
            case 1:
                try {
                    shardpref.putString("task_date", WorkDay);
                    Log.i(TAG, "WorkDay :" + WorkDay);

                    shardpref.putString("change_place_id", change_place_id.isEmpty() ? place_id : change_place_id);
                    shardpref.putString("change_member_id", change_member_id.isEmpty() ? "" : change_member_id);
                    if (!WorkDay.contains("null")) {
                        WorkgotoBottomSheet wgb = new WorkgotoBottomSheet();
                        wgb.show(getChildFragmentManager(), "WorkgotoBottomSheet");
                    }
                } catch (Exception e) {
                    Log.i(TAG, "onItemClick Exception :" + e);
                }
                break;
            case 2:
                shardpref.putString("FtoDay", WorkDay);
                if (!WorkDay.contains("null")) {
                    WorkstatusBottomSheet wgb = new WorkstatusBottomSheet();
                    wgb.show(getChildFragmentManager(), "WorkstatusBottomSheet");
                }
                break;
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        monDate.clear();
        tueDate.clear();
        wedDate.clear();
        thuDate.clear();
        friDate.clear();
        satDate.clear();
        sunDate.clear();
    }
    //--근무현황 캘린더 만들기
    private void SetCalendar() {
        dlog.i("sunDate : " + sunDate);
        dlog.i("monDate : " + String.valueOf(monDate));
       
        cadayAdapter1 = new CalendarDayAdaper(mContext, sunDate, month, year, sendList,state);
        binding.sunList.setAdapter(cadayAdapter1);
        binding.sunList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        cadayAdapter1.notifyDataSetChanged();
        cadayAdapter1.setOnItemClickListener(new CalendarDayAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                dlog.i("cadayAdapter1 onItemClick");
                SetClickEvent(WorkDay);
            }
        });

        cadayAdapter2 = new CalendarDayAdaper(mContext, monDate, month, year, sendList,state);
        binding.monList.setAdapter(cadayAdapter2);
        binding.monList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        cadayAdapter2.notifyDataSetChanged();
        cadayAdapter2.setOnItemClickListener(new CalendarDayAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                SetClickEvent(WorkDay);
            }
        });

        cadayAdapter3 = new CalendarDayAdaper(mContext, tueDate, month, year, sendList,state);
        binding.tueList.setAdapter(cadayAdapter3);
        binding.tueList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        cadayAdapter3.notifyDataSetChanged();
        cadayAdapter3.setOnItemClickListener(new CalendarDayAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                SetClickEvent(WorkDay);
            }
        });

        cadayAdapter4 = new CalendarDayAdaper(mContext, wedDate, month, year, sendList,state);
        binding.wedList.setAdapter(cadayAdapter4);
        binding.wedList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        cadayAdapter4.notifyDataSetChanged();
        cadayAdapter4.setOnItemClickListener(new CalendarDayAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                SetClickEvent(WorkDay);
            }
        });

        cadayAdapter5 = new CalendarDayAdaper(mContext, thuDate, month, year, sendList,state);
        binding.thuList.setAdapter(cadayAdapter5);
        binding.thuList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        cadayAdapter5.notifyDataSetChanged();
        cadayAdapter5.setOnItemClickListener(new CalendarDayAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                SetClickEvent(WorkDay);
            }
        });

        cadayAdapter6 = new CalendarDayAdaper(mContext, friDate, month, year, sendList,state);
        binding.friList.setAdapter(cadayAdapter6);
        binding.friList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        cadayAdapter6.notifyDataSetChanged();
        cadayAdapter6.setOnItemClickListener(new CalendarDayAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                SetClickEvent(WorkDay);
            }
        });
        
        cadayAdapter7 = new CalendarDayAdaper(mContext, satDate, month, year, sendList,state);
        binding.satList.setAdapter(cadayAdapter7);
        binding.satList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        cadayAdapter7.notifyDataSetChanged();
        cadayAdapter7.setOnItemClickListener(new CalendarDayAdaper.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                SetClickEvent(WorkDay);
            }
        });
    }



    //-- 할일 다시 조회
    private void SetWorkGotoCalenderData() {
        String USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
        Log.i(TAG, "------SetWorkGotoCalenderData------");
        Log.i(TAG, "place_id : " + place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + dc.GET_YEAR);
        Log.i(TAG, "------SetWorkGotoCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkTaskGetallInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkTaskGetallInterface api = retrofit.create(WorkTaskGetallInterface.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, dc.GET_YEAR,USER_INFO_AUTH);
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
                            if (Response2.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response2.length());
//                                GetWorkGotoCalenderList(year, month, workGotoList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    sendList.add(new WorkGetallData.WorkGetallData_list(
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

    //-- 근무현황 다시 조회
    private void SetWorkStatusCalenderData() {
        sendList.clear();
        String USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        String getYMDate = year + "-" + month;
        Log.i(TAG, "------SetWorkStatusCalenderData------");
        Log.i(TAG, "place_id : " + place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + getYMDate);
        Log.i(TAG, "------SetWorkStatusCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkStatusGetallInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkStatusGetallInterface api = retrofit.create(WorkStatusGetallInterface.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, getYMDate, USER_INFO_AUTH);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        Log.i(TAG, "SetWorkStatusCalenderData jsonResponse length : " + jsonResponse.length());
                        Log.i(TAG, "SetWorkStatusCalenderData jsonResponse : " + jsonResponse);
                        Log.i(TAG, "SetWorkStatusCalenderData function START");
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response2.length());
//                                GetWorkGotoCalenderList(year, month, workGotoList2);
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    sendList.add(new WorkGetallData.WorkGetallData_list(
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

    //급여 현황 다시 조회
    private void SetPayCalenderData() {
        sendList.clear();
        String USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        String getYMDate = year + "-" + month;
        Log.i(TAG, "------SetPayCalenderData------");
        Log.i(TAG, "place_id : " + place_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        Log.i(TAG, "select_date : " + getYMDate);
        Log.i(TAG, "------SetPayCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PayGetallInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PayGetallInterface api = retrofit.create(PayGetallInterface.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, getYMDate, USER_INFO_AUTH);
        call2.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call2, @NonNull Response<String> response2) {
                activity.runOnUiThread(() -> {
                    //캘린더 내용 (업무가) 있을때
                    if (response2.isSuccessful() && response2.body() != null) {
                        String jsonResponse = rc.getBase64decode(response2.body());
                        dlog.i("SetPayCalenderData jsonResponse length : " + jsonResponse.length());
                        dlog.i("SetPayCalenderData jsonResponse : " + jsonResponse);
                        try {
                            JSONArray Response2 = new JSONArray(jsonResponse);
                            if (Response2.length() == 0) {
                                dlog.i("SetPayCalenderData GET SIZE : " + Response2.length());
                            } else {
                                for (int i = 0; i < Response2.length(); i++) {
                                    JSONObject jsonObject = Response2.getJSONObject(i);
                                    sendList.add(new WorkGetallData.WorkGetallData_list(
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
}