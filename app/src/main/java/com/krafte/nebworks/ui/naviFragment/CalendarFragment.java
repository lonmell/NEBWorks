package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkCalenderAdapter;
import com.krafte.nebworks.data.CalendarSetData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkCalendersetData;
import com.krafte.nebworks.databinding.CalendarfragmentBinding;
import com.krafte.nebworks.pop.TaskListPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

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

public class CalendarFragment extends Fragment {
    private final static String TAG = "CalendarFragment";
    private CalendarfragmentBinding binding;
    Context mContext;
    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();

    //shared
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getDatePicker = "";
    String getYMPicker = "";
    int rotate_addwork = 0;
    WorkCalenderAdapter mAdapter;
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList;
    //Task all data
    ArrayList<CalendarSetData.CalendarSetData_list> mList2 = new ArrayList<>();

    public static CalendarFragment newInstance(int number) {
        CalendarFragment fragment = new CalendarFragment();
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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.calendarfragment, container, false);
        binding = CalendarfragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
        //UI 데이터 세팅
        try {
            toDay = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            getYMPicker = dc.GET_YEAR + "-" + dc.GET_MONTH;
            binding.selectdate.setText(toDay);
//            binding.selectdate.setOnClickListener(v -> {
//                shardpref.putInt("timeSelect_flag", 7);
//                Intent intent = new Intent(mContext, DatePickerymdActivity.class);
//                startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//            });
            //--selectdate 변경
//                shardpref.putInt("timeSelect_flag", 6);

//                Intent intent = new Intent(mContext, DatePickerActivity.class);
//                startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);

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
                    binding.selectdate.setText(year +"-" + Month + "-" + Day);
                    getYMPicker = binding.selectdate.getText().toString().substring(0,7);
                    SetCalenderData(String.valueOf(year),Month);
                }
            }, mYear, mMonth, mDay);

            binding.selectdate.setOnClickListener(view -> {
                if (binding.selectdate.isClickable()) {
                    datePickerDialog.show();
                }
            });
            //--selectdate 변경

            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            place_id = shardpref.getString("place_id", "0");
            shardpref.putInt("SELECT_POSITION", 0);

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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        toDay = (Year.isEmpty()?dc.GET_YEAR:Year) + "-" + (Month.isEmpty()?dc.GET_MONTH:Month) + "-" + (Day.isEmpty()?dc.GET_DAY:Day);
        getYMPicker = (Year.isEmpty()?dc.GET_YEAR:Year) + "-" + (Month.isEmpty()?dc.GET_MONTH:Month);
        binding.selectdate.setText(toDay);
        SetCalenderData((Year.isEmpty()?dc.GET_YEAR:Year), (Month.isEmpty()?dc.GET_MONTH:Month));
//        dlog.i("onResume timeSelect_flag :" + shardpref.getInt("timeSelect_flag", 0));
//        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
//        if (timeSelect_flag == 7) {
//            //-- DatePickerActivity에서 받아오는 값
//            String getYear = shardpref.getString("getYear", "");
//            String getMonth = shardpref.getString("getMonth", "");
//            String getDay = shardpref.getString("getDay", "");
//            getYMPicker = getYear + "-" + getMonth;
//            dlog.i("onResume getYear :" + shardpref.getString("getYear", ""));
//            dlog.i("onResume getMonth :" + shardpref.getString("getMonth", ""));
//            dlog.i("onResume getDay :" + shardpref.getString("getDay", ""));
//            getDatePicker = getYear + "-" + getMonth + "-" + getDay;
//
//            if(getDatePicker.equals("--")){
//                getDatePicker = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
//            }
//            dlog.i("onResume getDatePicker :" + getDatePicker);
//            binding.selectdate.setText(getDatePicker);
//            shardpref.remove("timeSelect_flag");
//            SetCalenderData();
//        }
//
//        if (binding.selectdate.getText().toString().isEmpty()) {
//            String getYear = shardpref.getString("getYear", "");
//            String getMonth = shardpref.getString("getMonth", "");
//            String getDay = shardpref.getString("getDay", "");
//            getDatePicker = getYear + "-" + getMonth + "-" + getDay;
//            getYMPicker = getYear + "-" + getMonth;
//            if (getDatePicker.length() < 3) {
//                binding.selectdate.setText(toDay);
//            } else {
//                binding.selectdate.setText(getDatePicker);
//            }
//            SetCalenderData();
//        }else{
//            getYMPicker = binding.selectdate.getText().toString().substring(0,7);
//            dlog.i("binding.selectdate ym : " + getYMPicker);
//            SetCalenderData();
//        }
    }

    private void SetCalenderData(String Year,String Month){
        mList2.clear();
        dlog.i("------SetCalenderData------");
        dlog.i("place_id :" + place_id);
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("getYMPicker :" + getYMPicker);
        dlog.i("------SetCalenderData------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkCalendersetData.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkCalendersetData api = retrofit.create(WorkCalendersetData.class);
        Call<String> call2 = api.getData(place_id, USER_INFO_ID, getYMPicker);
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
                                    mList2.add(new CalendarSetData.CalendarSetData_list(
                                            jsonObject.getString("day"),
                                            jsonObject.getString("week"),
                                            Collections.singletonList(jsonObject.getString("task"))
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
    ArrayList<String> kind = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> write_name = new ArrayList<>();
    ArrayList<String> end = new ArrayList<>();
    public void GetCalenderList(String Year, String Month, ArrayList<CalendarSetData.CalendarSetData_list> mList2) {
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
                                        try{
                                            kind = new ArrayList<>();
                                            title = new ArrayList<>();
                                            for (int i = 0; i < mList2.size(); i++) {
                                                if (data.equals(mList2.get(i).getDay().length() == 1?"0"+mList2.get(i).getDay():mList2.get(i).getDay())) {
                                                    JSONArray Response = new JSONArray(mList2.get(i).getTask().toString().replace("[[", "[").replace("]]", "]"));
                                                    for (int i3 = 0; i3 < Response.length(); i3++) {
                                                        JSONObject jsonObject = Response.getJSONObject(i3);
                                                        kind.add(jsonObject.getString("kind"));
                                                        title.add(jsonObject.getString("title"));
                                                    }
                                                }
                                            }
                                            shardpref.putString("task_date",WorkDay);
                                            dlog.i("WorkDay :" + WorkDay);
                                            Intent intent = new Intent(mContext, TaskListPopActivity.class);
                                            intent.putStringArrayListExtra("kind", kind);
                                            intent.putStringArrayListExtra("title", title);
                                            intent.putExtra("date", data);
                                            intent.putExtra("yoil", yoil);
                                            intent.putExtra("write_name","");
                                            startActivity(intent);
                                            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                        }catch (Exception e){
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

                                binding.addWorkBtn.setOnClickListener(v -> {
                                    //버튼 회전 애니메이션
                                    Animation anim = AnimationUtils.loadAnimation(
                                            mContext,
                                            R.anim.pen_scale_anim);
                                    binding.wirteAddImg.startAnimation(anim);
                                    //            make_icon01Icon.startAnimation(anim);
                                    //            make_icon02Icon.startAnimation(anim);
                                    if (rotate_addwork == 1) {
                                        binding.wirteAddImg.setBackgroundResource(R.drawable.plus);
                                        Animation fade_out = AnimationUtils.loadAnimation(
                                                mContext,
                                                R.anim.push_right_out);
                                        binding.makeWorkMenu.startAnimation(fade_out);
                                        rotate_addwork = 0;
                                        binding.makeWorkMenu.setVisibility(View.GONE);
                                        binding.menuVisible.setVisibility(View.GONE);
                                    } else {
                                        binding.wirteAddImg.setBackgroundResource(R.drawable.drawing_pen);
                                        Animation fade_in = AnimationUtils.loadAnimation(
                                                mContext,
                                                R.anim.push_left_in);
                                        binding.makeWorkMenu.startAnimation(fade_in);
                                        rotate_addwork = 1;
                                        binding.makeWorkMenu.setVisibility(View.VISIBLE);
                                        binding.menuVisible.setVisibility(View.VISIBLE);
                                    }
                                });

                                binding.makeWork02.setOnClickListener(v -> {
                                    // 배정업무 등록
                                    shardpref.putInt("make_kind", 1);
                                    shardpref.putInt("assignment_kind", 2);
                                    shardpref.putString("return_page","TaskCalenderActivity");
                                    Animation fade_out = AnimationUtils.loadAnimation(
                                            mContext,
                                            R.anim.push_right_out);
                                    binding.makeWorkMenu.startAnimation(fade_out);
                                    rotate_addwork = 0;
                                    binding.makeWorkMenu.setVisibility(View.GONE);
                                    binding.menuVisible.setVisibility(View.GONE);
                                    pm.addWorkGo(mContext);
                                });

                                binding.makeWork03.setOnClickListener(v -> {
                                    //개인일정
                                    shardpref.putInt("make_kind", 4);
                                    shardpref.putInt("assignment_kind", 3);
                                    shardpref.putString("return_page","TaskCalenderActivity");
                                    Animation fade_out = AnimationUtils.loadAnimation(
                                            mContext,
                                            R.anim.push_right_out);
                                    binding.makeWorkMenu.startAnimation(fade_out);
                                    rotate_addwork = 0;
                                    binding.makeWorkMenu.setVisibility(View.GONE);
                                    binding.menuVisible.setVisibility(View.GONE);
                                    pm.addWorkGo(mContext);
                                });

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

    @Override
    public void onDestroy(){
        super.onDestroy();
        shardpref.remove("task_date");
    }
}
