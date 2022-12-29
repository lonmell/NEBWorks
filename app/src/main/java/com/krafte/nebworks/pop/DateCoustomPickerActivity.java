package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.DatePickerAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkCalenderData;
import com.krafte.nebworks.dataInterface.WorkCalenderInterface;
import com.krafte.nebworks.databinding.ActivityDatecoustompickerBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DateCoustomPickerActivity extends Activity {
    private static final String TAG = "CommunityOptionActivity";
    private ActivityDatecoustompickerBinding binding;
    Context mContext;
    private View view;
    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_NO = "";
    String USER_INFO_ID = "";

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

    String getYear = "";
    String getMonth = "";
    String getDay = "";
    String getYoil = "";

    int data;
    int Hour = 0;
    int Min = 0;

    //Other
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    Dlog dlog = new Dlog();
    Intent intent;

    private String result = "";
    GetResultData resultData = new GetResultData();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_datecoustompicker);
        binding = ActivityDatecoustompickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try{
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

            setBtnEvent();

            cal = Calendar.getInstance();
            toDay = sdf.format(cal.getTime());
            dlog.i("오늘 :" + toDay);
            shardpref.putString("FtoDay",toDay);
            gYear = toDay.substring(0,4);
            gMonth = toDay.substring(5,7);
            binding.setdate.setText("   " + gYear + "년 " + gMonth + "월   ");
            GetCalenderList(gYear,gMonth);
            binding.timeSetpicker.setIs24HourView(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setBtnEvent() {
        binding.prevDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, -1);
            toDay = sdf.format(cal.getTime());
            shardpref.putString("FtoDay",toDay);
            gYear = toDay.substring(0,4);
            gMonth = toDay.substring(5,7);
            binding.setdate.setText("   " + gYear + "년 " + gMonth + "월   ");
            if(!gYear.equals(gYear) || !bMonth.equals(gMonth)){
                dlog.i("gYear : " + gYear);
                dlog.i("bYear : " + bYear);
                dlog.i("gMonth : " + gMonth);
                dlog.i("bMonth : " + bMonth);
                bYear = gYear;
                bMonth = gMonth;
                GetCalenderList(gYear,gMonth);
            }
        });
        binding.nextDate.setOnClickListener(v -> {
            cal.add(Calendar.DATE, +1);
            toDay = sdf.format(cal.getTime());
            shardpref.putString("FtoDay",toDay);
            gYear = toDay.substring(0,4);
            gMonth = toDay.substring(5,7);
            binding.setdate.setText("   " + gYear + "년 " + gMonth + "월   ");
            if(!gYear.equals(gYear) || !bMonth.equals(gMonth)){
                dlog.i("gYear : " + gYear);
                dlog.i("bYear : " + bYear);
                dlog.i("gMonth : " + gMonth);
                dlog.i("bMonth : " + bMonth);
                bYear = gYear;
                bMonth = gMonth;
                GetCalenderList(gYear,gMonth);
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

        binding.confirmBtn.setOnClickListener(v -> {
            binding.timeSetpicker.clearFocus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Hour = binding.timeSetpicker.getHour();
                Min = binding.timeSetpicker.getMinute();
            }else{
                Hour = binding.timeSetpicker.getCurrentHour();
                Min = binding.timeSetpicker.getCurrentMinute();
            }
            shardpref.putInt("picker_Hour",Hour);
            shardpref.putInt("picker_Min",Min);
            dlog.i("WorkTimePicker Hour : " + Hour);
            dlog.i("WorkTimePicker Min : " + Min);
            //데이터 전달하기
            //액티비티(팝업) 닫기
            finish();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);

        });
        binding.cancelBtn.setOnClickListener(v -> {
            //데이터 전달하기
            //액티비티(팝업) 닫기
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });
    }

    DatePickerAdapter mAdapter;
    ArrayList<WorkCalenderData.WorkCalenderData_list> mList;
    RetrofitConnect rc = new RetrofitConnect();
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
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        dlog.i("onResume USER_INFO_ID :" + USER_INFO_ID);
                        dlog.i("onResume getYMPicker :" + getYMPicker);
                        try{
                            String select_date = Year + "-" + Month;
                            JSONArray Response = new JSONArray(jsonResponse);
                            mList = new ArrayList<>();
                            mAdapter = new DatePickerAdapter(mContext, mList);
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
                                mAdapter.setOnItemClickListener(new DatePickerAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String data, String yoil, String WorkDay) {
                                        dlog.i("data : " + data);
                                        dlog.i("yoil : " + yoil);
                                        dlog.i("WorkDay : " + WorkDay);
                                        getYear = WorkDay.substring(0,4);
                                        getMonth = WorkDay.substring(4,6);
                                        getDay = data;
                                        shardpref.putString("picker_year",getYear);
                                        shardpref.putString("picker_month",getMonth);
                                        shardpref.putString("picker_day",getDay);
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
}
