package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.YoilStringAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.dataInterface.ContractWorkInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd04Binding;
import com.krafte.nebworks.pop.WorkTimePicker;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddContractPage04 extends AppCompatActivity {
    private ActivityContractAdd04Binding binding;
    private final static String TAG = "AddContractPage04";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";
    String contract_id = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    List<String> yoil = new ArrayList<>();
    ArrayList<StringData.StringData_list> workmList = new ArrayList<>();
    YoilStringAdapter workmAdapter = null;

    ArrayList<StringData.StringData_list> restmList = new ArrayList<>();
    YoilStringAdapter restmAdapter = null;

    String workYoil = "";
    String restYoil = "";

    DatePickerDialog datePickerDialog;
    DatePickerDialog datePickerDialog2;
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMStart = "";
    String getYMEnd = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractAdd04Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref       = new PreferenceHelper(mContext);
        place_id        = shardpref.getString("place_id","0");
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID","0");
        worker_id       = shardpref.getString("worker_id","0");
        contract_id     = shardpref.getString("contract_id","0");

        setBtnEvent();

        //basic setting
        yoil.add("월");
        yoil.add("화");
        yoil.add("수");
        yoil.add("목");
        yoil.add("금");
        yoil.add("토");
        yoil.add("일");
        //근무요일
        workmList = new ArrayList<>();
        workmAdapter = new YoilStringAdapter(mContext, workmList);
        binding.workyoilList.setAdapter(workmAdapter);
        binding.workyoilList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        for (int i = 0; i < yoil.size(); i++) {
            workmAdapter.addItem(new StringData.StringData_list(
                    yoil.get(i)
            ));
        }
        workmAdapter.notifyDataSetChanged();
        workmAdapter.setOnItemClickListener((v, position, yoil) -> {
            dlog.i("Get onItem : " + yoil);
            workYoil = yoil;
        });

        //휴무일
        restmList = new ArrayList<>();
        restmAdapter = new YoilStringAdapter(mContext, restmList);
        binding.restyoilList.setAdapter(restmAdapter);
        binding.restyoilList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        for (int i = 0; i < yoil.size(); i++) {
            restmAdapter.addItem(new StringData.StringData_list(
                    yoil.get(i)
            ));
        }
        restmAdapter.notifyDataSetChanged();
        restmAdapter.setOnItemClickListener((v, position, yoil) -> {
            dlog.i("Get onItem : " + restmList.get(position));
            restYoil = yoil;
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
                binding.wtime01time.setText(GetTime);
            }
        } else if (timeSelect_flag == 2) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("hourOfDay");
            shardpref.remove("minute");
            GetTime = Time01 + ":" + Time02;
            shardpref.putString("input_pop_time",GetTime);
            if (hourOfDay != 0) {
                binding.wtime02time.setText(GetTime);
            }
        } else if (timeSelect_flag == 3) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("hourOfDay");
            shardpref.remove("minute");
            GetTime = Time01 + ":" + Time02;
            shardpref.putString("input_pop_time",GetTime);
            if (hourOfDay != 0) {
                binding.resttime01time.setText(GetTime);
            }
        } else if (timeSelect_flag == 4) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("hourOfDay");
            shardpref.remove("minute");
            GetTime = Time01 + ":" + Time02;
            shardpref.putString("input_pop_time",GetTime);
            if (hourOfDay != 0) {
                binding.resttime02time.setText(GetTime);
            }
        }
    }

    String contract_start = "";
    String contract_end = "";
    String contract_type = "0";
    String wstarttime = "";
    String wendtime = "";
    String reststarttime = "";
    String restendtime = "";
    String work_contents = "";

    private void setBtnEvent(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.select01date.setText(year + "-" + Month + "-" + Day);
                getYMStart = binding.select01date.getText().toString().substring(0, 7);
            }
        }, mYear, mMonth, mDay);

        binding.select01date.setOnClickListener(v -> {
            if(binding.select01date.getText().toString().length() == 0){
                binding.select01date.setText(mYear + "-" + (String.valueOf(mMonth).length() == 1?"0"+mMonth:mMonth) + "-"
                        + (String.valueOf(mDay).length() == 1?"0"+String.valueOf(mDay):String.valueOf(mDay)));
            }else{
                datePickerDialog.show();
            }
        });

        datePickerDialog2 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.select02date.setText(year + "-" + Month + "-" + Day);
                getYMEnd = binding.select02date.getText().toString().substring(0, 7);
            }
        }, mYear, mMonth, mDay);

        binding.select02date.setOnClickListener(v -> {
            if(binding.select02date.getText().toString().length() == 0){
                binding.select02date.setText(mYear + "-" + (String.valueOf(mMonth).length() == 1?"0"+mMonth:mMonth) + "-"
                        + (String.valueOf(mDay).length() == 1?"0"+String.valueOf(mDay):String.valueOf(mDay)));
            }else{
                datePickerDialog2.show();
            }
        });

        binding.select03.setOnClickListener(v -> {
            if(contract_type.equals("0")){
                contract_type = "1";
                binding.select03Round.setBackgroundResource(R.drawable.resize_service_on);
            }else{
                contract_type = "0";
                binding.select03Round.setBackgroundResource(R.drawable.resize_service_off);
            }
        });

        binding.wtime01time.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.wtime02time.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 2);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.resttime01time.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 3);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.resttime02time.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 4);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });

        binding.next.setOnClickListener(v -> {
            if(DataCheck()){
                SaveContractWork();
            }
        });
    }

    private boolean DataCheck(){
        contract_start  = binding.select01date.getText().toString();
        contract_end    = binding.select02date.getText().toString();
        workYoil        = workYoil.replace("[","").replace("]","").replace(" ","");
        restYoil        = restYoil.replace("[","").replace("]","").replace(" ","");
        wstarttime      = binding.wtime01time.getText().toString();
        wendtime        = binding.wtime02time.getText().toString();
        reststarttime   = binding.resttime01time.getText().toString();
        restendtime     = binding.resttime02time.getText().toString();
        work_contents   = binding.input01.getText().toString();
        dlog.i("-----DataCheck-----");
        dlog.i("contract_start : "      + contract_start);
        dlog.i("contract_end : "        + contract_end);
        dlog.i("contract_type : "       + contract_type);
        dlog.i("workYoil : "            + workYoil);
        dlog.i("restYoil : "            + restYoil);
        dlog.i("wstarttime : "          + wstarttime);
        dlog.i("wendtime : "            + wendtime);
        dlog.i("reststarttime : "       + reststarttime);
        dlog.i("restendtime : "         + restendtime);
        dlog.i("work_contents : "       + work_contents);
        dlog.i("-----DataCheck-----");
        if(contract_start.isEmpty()){
            Toast_Nomal("계약 시작날짜를 입력해주세요");
            return false;
        } else if(contract_end.isEmpty()){
            Toast_Nomal("계약 종료날짜를 입력해주세요");
            return false;
        } else if(workYoil.isEmpty()){
            Toast_Nomal("근무요일을 선택하세요.");
            return false;
        } else if(restYoil.isEmpty()){
            Toast_Nomal("휴무일을 선택하세요.");
            return false;
        } else if(wstarttime.isEmpty()){
            Toast_Nomal("근무시작시간을 입력해주세요.");
            return false;
        } else if(wendtime.isEmpty()){
            Toast_Nomal("근무종료시간을 입력해주세요.");
            return false;
        } else if(reststarttime.isEmpty()){
            Toast_Nomal("휴게시작시간을 입력해주세요.");
            return false;
        } else if(restendtime.isEmpty()){
            Toast_Nomal("휴게종료시간을 입력해주세요.");
            return false;
        } else if(work_contents.isEmpty()){
            Toast_Nomal("업무내용을 입력해주세요.");
            return false;
        } else{
            return true;
        }
    }

    public void SaveContractWork() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractWorkInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractWorkInterface api = retrofit.create(ContractWorkInterface.class);
        Call<String> call = api.getData(contract_id, contract_start, contract_end, contract_type, workYoil, restYoil, wstarttime, wendtime, reststarttime, restendtime, work_contents);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("SaveWorkPartTime Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("SaveWorkPartTime jsonResponse length : " + response.body().length());
                            dlog.i("SaveWorkPartTime jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]") && response.body().replace("\"", "").equals("success")) {
                                    Toast_Nomal("근무 기본사항이 업데이트 완료되었습니다.");
                                    pm.AddContractPage05(mContext);
                                }
                            } catch (Exception e) {
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

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
