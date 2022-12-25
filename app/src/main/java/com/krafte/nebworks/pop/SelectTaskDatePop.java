package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkCalenderEmptyAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.databinding.ActivitySelecttaskDateBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SelectTaskDatePop extends Activity {
    private ActivitySelecttaskDateBinding binding;
    private static final String TAG = "SelectTaskDatePop";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    GetResultData resultData = new GetResultData();
    Handler mHandler;
    String storenoti_no = "";

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

    String getYear = "";
    String getMonth = "";
    String getDay = "";
    String getYoil = "";
    String USER_INFO_ID = "";
    String SET_TASK_TIME_VALUE = "";
    String GetTime = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivitySelecttaskDateBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");
        //0 - 시작시간 / 1 - 마감시간
        SET_TASK_TIME_VALUE = shardpref.getString("SET_TASK_TIME_VALUE","0");

        if(SET_TASK_TIME_VALUE.equals("0")){
            binding.title.setText("시작 일시 선택");
            binding.title2.setText("시작 날짜 선택");
            binding.title3.setText("시작 시간 입력");
        }else{
            binding.title.setText("마감 일시 선택");
            binding.title2.setText("마감 날짜 선택");
            binding.title3.setText("마감 시간 입력");
        }

        binding.backBtn.setOnClickListener(v -> {
            closePop();
        });

        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        binding.setdate.setText(toDay);
        Year = toDay.substring(0, 4);
        Month = toDay.substring(5, 7);
        binding.setdate.setText(Year + "년 " + Month + "월");
        GetCalenderList(Year,Month);

        binding.prevDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, -1);
            toDay = sdf.format(cal.getTime());
            shardpref.putString("FtoDay",toDay);
            Year = toDay.substring(0,4);
            Month = toDay.substring(5,7);
            binding.setdate.setText("   " + Year + "년 " + Month + "월   ");
            if(!Year.equals(Year) || !bMonth.equals(Month)){
                dlog.i("Year : " + Year);
                dlog.i("bYear : " + bYear);
                dlog.i("Month : " + Month);
                dlog.i("bMonth : " + bMonth);
                bYear = Year;
                bMonth = Month;
                GetCalenderList(Year,Month);
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, +1);
            toDay = sdf.format(cal.getTime());
            shardpref.putString("FtoDay",toDay);
            Year = toDay.substring(0,4);
            Month = toDay.substring(5,7);
            binding.setdate.setText("   " + Year + "년 " + Month + "월   ");
            if(!Year.equals(Year) || !bMonth.equals(Month)){
                dlog.i("Year : " + Year);
                dlog.i("bYear : " + bYear);
                dlog.i("Month : " + Month);
                dlog.i("bMonth : " + bMonth);
                bYear = Year;
                bMonth = Month;
                GetCalenderList(Year,Month);
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
                binding.setdate.setText("   " + Year + "년 " + Month + "월   ");
                getYMPicker = binding.setdate.getText().toString().substring(0,7);
                GetCalenderList(String.valueOf(year),Month);
            }
        }, mYear, mMonth, mDay);

        binding.setdate.setOnClickListener(view -> {
            if (binding.setdate.isClickable()) {
                datePickerDialog.show();
            }
        });

        binding.selectTimetv.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePickerPop.class);
            intent.putExtra("timeSelect_flag", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });

        binding.savetimeBtn.setOnClickListener(v -> {
            if(binding.selectTimetv.getText().toString().isEmpty()){
                Toast_Nomal("시간을 입력해주세요.");
            }else{
                closePop();
            }
        });
    }

    String Time01 = "-99";
    String Time02 = "-99";

    @Override
    public void onResume(){
        super.onResume();
        //반복요일 세팅
        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
        int hourOfDay = shardpref.getInt("Hour", 0);
        int minute = shardpref.getInt("Min", 0);
        String GetTime = "";
        dlog.i("------------------Data Check onResume------------------");
        dlog.i("timeSelect_flag : " + timeSelect_flag);
        dlog.i("GetTime : " + GetTime);
        dlog.i("------------------Data Check onResume------------------");

        if (timeSelect_flag == 1) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("hourOfDay");
            shardpref.remove("minute");
            GetTime = Time01 + ":" + Time02;
            shardpref.putString("input_pop_time",GetTime);
            if (hourOfDay != 0) {
                binding.selectTimetv.setText(GetTime);
            }
        }
    }

    private void closePop() {
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }

    WorkCalenderEmptyAdapter mAdapter;
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList;
    public void GetCalenderList(String Year, String Month) {
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
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
                        dlog.i("onResume getYMPicker :" + getYMPicker);
                        try{
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(response.body());
                            mList = new ArrayList<>();
                            mAdapter = new WorkCalenderEmptyAdapter(mContext, mList);
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
                                mAdapter.setOnItemClickListener(new WorkCalenderEmptyAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        getYear = WorkDay.substring(0, 4);
                                        getMonth = WorkDay.substring(4, 6);
                                        getDay = data;
                                        dlog.i("getYear : " + getYear);
                                        dlog.i("getMonth : " + getMonth);
                                        dlog.i("yoil : " + yoil);
                                        dlog.i("WorkDay : " + WorkDay);
                                        dlog.i("getDay : " + data);

                                        shardpref.putString("picker_year", getYear);
                                        shardpref.putString("picker_month", getMonth);
                                        shardpref.putString("picker_day", getDay);
                                        binding.selectDatetv.setText(getYear + "-" + getMonth + "-" +getDay);
                                        mAdapter.notifyDataSetChanged();
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
}
