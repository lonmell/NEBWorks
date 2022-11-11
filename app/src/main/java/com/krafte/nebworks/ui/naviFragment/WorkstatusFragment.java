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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkStatusCalenderAdapter;
import com.krafte.nebworks.bottomsheet.WorkerListActivity;
import com.krafte.nebworks.data.CalendarSetStatusData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.dataInterface.WorkstatusCalendersetData;
import com.krafte.nebworks.databinding.WorkstatusfragmentBinding;
import com.krafte.nebworks.pop.MemberOption;
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
                Intent intent = new Intent(mContext, MemberOption.class);
                intent.putExtra("data", "직원등록");
                intent.putExtra("btn01", "직접등록");
                intent.putExtra("btn02", "초대메세지 발송");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                                mAdapter.setOnItemClickListener(new WorkStatusCalenderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        dlog.i("data :" + data);
                                        try{
                                            user_id = new ArrayList<>();
                                            user_name = new ArrayList<>();
                                            img_path = new ArrayList<>();
                                            jikgup = new ArrayList<>();
                                            worktime = new ArrayList<>();
                                            workyoil = new ArrayList<>();
                                            for (int i = 0; i < mList2.size(); i++) {
                                                if (data.equals(mList2.get(i).getDay().length() == 1?"0"+mList2.get(i).getDay():mList2.get(i).getDay())) {
                                                    JSONArray Response = new JSONArray(mList2.get(i).getUsers().toString().replace("[[", "[").replace("]]", "]"));
                                                    for (int i3 = 0; i3 < Response.length(); i3++) {
                                                        JSONObject jsonObject = Response.getJSONObject(i3);
                                                        user_id.add(jsonObject.getString("user_id"));
                                                        user_name.add(jsonObject.getString("user_name"));
                                                        img_path.add(jsonObject.getString("img_path"));
                                                        jikgup.add(jsonObject.getString("jikgup"));
                                                        worktime.add(jsonObject.getString("worktime"));
                                                        workyoil.add(jsonObject.getString("workyoil"));
                                                    }
                                                }
                                            }
                                            shardpref.putString("task_date",WorkDay);
                                            dlog.i("WorkDay :" + WorkDay);
                                            shardpref.putString("worker_user_id", String.valueOf(user_id));
                                            shardpref.putString("worker_user_name", String.valueOf(user_name));
                                            shardpref.putString("worker_img_path", String.valueOf(img_path));
                                            shardpref.putString("worker_jikgup", String.valueOf(jikgup));
                                            shardpref.putString("worker_worktime", String.valueOf(worktime));
                                            shardpref.putString("worker_workyoil", String.valueOf(workyoil));
                                            WorkerListActivity wla = new WorkerListActivity();
                                            wla.show(getParentFragmentManager(),"WorkerListActivity");
                                        }catch (Exception e){
                                            dlog.i("onItemClick Exception :" + e);
                                        }

                                    }
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
}
