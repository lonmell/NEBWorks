package com.krafte.nebworks.ui.workstatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.WorkPartGetInterface;
import com.krafte.nebworks.dataInterface.WorkPartSaveInterface;
import com.krafte.nebworks.databinding.ActivityAddworkpartBinding;
import com.krafte.nebworks.pop.WorkTimePicker;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddWorkPartActivity extends AppCompatActivity {
    private com.krafte.nebworks.databinding.ActivityAddworkpartBinding binding;
    Context mContext;

    //Other
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();
    ArrayList<String> mList;
    Handler mHandler;
    RetrofitConnect rc = new RetrofitConnect();


    String Time01 = "-99";
    String Time02 = "-99";

    //Shared
    String USER_INFO_EMAIL = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String item_user_id = "";
    String item_jikgup = "";
    String item_state = "";
    String item_join_date = "";
    String item_user_name = "";

    String place_id = "";
    String setYoil = "";
    String sieob_get = "";
    String jong_eob_get = "";
    String total_work_time_get = "";
    String break_time_get01 = "";
    String break_time_get02 = "";
    String diff_break_time_get = "";
    //--매장 정보 수정할때

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityAddworkpartBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "0");
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("USER_INFO_EMAIL : " + USER_INFO_EMAIL);

            place_id = shardpref.getString("place_id","0");
            item_user_id = shardpref.getString("item_user_id","0");
            item_jikgup = shardpref.getString("item_jikgup","0");
            item_state = shardpref.getString("item_state","0");
            item_join_date = shardpref.getString("item_join_date","");
            item_user_name = shardpref.getString("item_user_name","");

            setBtnEvent();
            binding.name.setText(item_user_name);
            binding.joinDate.setText(item_join_date);
            binding.jikgup.setText(item_jikgup);
            binding.state.setText("현재 " + item_state);
            getPartTimeYoil("월");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        ChangeYoilBox(1);
    }

    @Override
    public void onResume(){
        super.onResume();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        dlog.i("kind : " + shardpref.getInt("timeSelect_flag", 0));
        dlog.i("Hour : " + shardpref.getInt("Hour", 0));
        dlog.i("Min : " + shardpref.getInt("Min", 0));
        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
        int hourOfDay = shardpref.getInt("Hour", 0);
        int minute = shardpref.getInt("Min", 0);


        if (timeSelect_flag == 1) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            if (hourOfDay != 0) {
                binding.selectTime01.setText(Time01 + ":" + Time02);
                imm.hideSoftInputFromWindow(binding.selectTime01.getWindowToken(), 0);
            }
        } else if (timeSelect_flag == 2) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            if (hourOfDay != 0) {
                binding.selectTime02.setText(Time01 + ":" + Time02);
                imm.hideSoftInputFromWindow(binding.selectTime02.getWindowToken(), 0);
            }
        } else if (timeSelect_flag == 3) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            if (hourOfDay != 0) {
                binding.selectTime03.setText(Time01 + ":" + Time02);
                imm.hideSoftInputFromWindow(binding.selectTime03.getWindowToken(), 0);
            }
        } else if (timeSelect_flag == 4) {
            Time01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            Time02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            if (hourOfDay != 0) {
                binding.selectTime04.setText(Time01 + ":" + Time02);
                imm.hideSoftInputFromWindow(binding.selectTime04.getWindowToken(), 0);
            }
        }

        SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.KOREA);
        try {
            sieob_get = binding.selectTime01.getText().toString();
            jong_eob_get = binding.selectTime02.getText().toString();
            if ((timeSelect_flag == 1 || timeSelect_flag == 2) && (!sieob_get.equals("") && !jong_eob_get.equals(""))) {
                dlog.i("근로시간 계산");
                //근로시간 계산
                Date d1 = f.parse(jong_eob_get);
                Date d2 = f.parse(sieob_get);

                long diff = d1.getTime() - d2.getTime();
                dlog.i("diff : " + diff);
                long min = diff / 60000;

                long getH = min / 60;
                long getM = min % 60;
                if (getM != 0) {
                    total_work_time_get = getH + "h " + getM + "m";
                } else {
                    total_work_time_get = getH + "h";
                }

            } else if ((timeSelect_flag == 3 || timeSelect_flag == 4) && (!break_time_get02.equals("") && !break_time_get01.equals(""))) {
                break_time_get01 = binding.selectTime03.getText().toString();
                break_time_get02 = binding.selectTime04.getText().toString();
                dlog.i("휴게시간 계산");
                //휴게시간 계산
                Date d3 = f.parse(break_time_get02);
                Date d4 = f.parse(break_time_get01);

                long diff2 = d3.getTime() - d4.getTime();
                dlog.i("diff : " + diff2);
                long min2 = diff2 / 60000;

                long getH2 = min2 / 60;
                long getM2 = min2 % 60;
                if (getM2 != 0) {
                    diff_break_time_get = getH2 + "h " + getM2 + "m";
                } else if (getH2 == 0) {
                    diff_break_time_get = getM2 + "m";
                } else {
                    diff_break_time_get = getH2 + "h";
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        shardpref.remove("item_user_id");
        shardpref.remove("item_jikgup");
        shardpref.remove("item_state");
        shardpref.remove("item_join_date");
        shardpref.remove("item_user_name");
    }

    private void setBtnEvent(){
        binding.yoil01.setOnClickListener(v -> {
            ChangeYoilBox(1);
        });
        binding.yoil02.setOnClickListener(v -> {
            ChangeYoilBox(2);
        });
        binding.yoil03.setOnClickListener(v -> {
            ChangeYoilBox(3);
        });
        binding.yoil04.setOnClickListener(v -> {
            ChangeYoilBox(4);
        });
        binding.yoil05.setOnClickListener(v -> {
            ChangeYoilBox(5);
        });
        binding.yoil06.setOnClickListener(v -> {
            ChangeYoilBox(6);
        });
        binding.yoil07.setOnClickListener(v -> {
            ChangeYoilBox(7);
        });

        binding.selectTime01.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.selectTime02.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 2);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.selectTime03.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 3);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.selectTime04.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 4);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.saveWorkpart.setOnClickListener(v -> {
            if(SaveCheck()){
                SaveWorkPartTime();
            }
        });
    }
    private boolean SaveCheck(){
        sieob_get = binding.selectTime01.getText().toString();
        jong_eob_get = binding.selectTime02.getText().toString();
        break_time_get01 = binding.selectTime03.getText().toString();
        break_time_get02 = binding.selectTime04.getText().toString();
        SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.KOREA);
        try {
            if (!sieob_get.isEmpty() && !jong_eob_get.isEmpty()) {
                dlog.i("근로시간 계산");
                //근로시간 계산
                Date d1 = f.parse(jong_eob_get);
                Date d2 = f.parse(sieob_get);

                long diff = d1.getTime() - d2.getTime();
                dlog.i("diff : " + diff);
                long min = diff / 60000;

                long getH = min / 60;
                long getM = min % 60;
                if (getM != 0) {
                    total_work_time_get = getH + "h " + getM + "m";
                } else {
                    total_work_time_get = getH + "h";
                }

            } else if (!break_time_get02.isEmpty() && !break_time_get01.isEmpty()) {
                dlog.i("휴게시간 계산");
                //휴게시간 계산
                Date d3 = f.parse(break_time_get02);
                Date d4 = f.parse(break_time_get01);

                long diff2 = d3.getTime() - d4.getTime();
                dlog.i("diff : " + diff2);
                long min2 = diff2 / 60000;

                long getH2 = min2 / 60;
                long getM2 = min2 % 60;
                if (getM2 != 0) {
                    diff_break_time_get = getH2 + "h " + getM2 + "m";
                } else if (getH2 == 0) {
                    diff_break_time_get = getM2 + "m";
                } else {
                    diff_break_time_get = getH2 + "h";
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        dlog.i("-----SaveCheck-----");
        dlog.i("yoil : " + setYoil);
        dlog.i("WorkStartTime : " + sieob_get);
        dlog.i("WorkEndTime : " + jong_eob_get);
        dlog.i("TotalWorkTime : " + total_work_time_get);
        dlog.i("RestStart : " + break_time_get01);
        dlog.i("RestEnd : " + break_time_get02);
        dlog.i("RestTotal : " + diff_break_time_get);
        dlog.i("-----SaveCheck-----");
        if(setYoil.isEmpty()){
            Toast_Nomal("요일을 선택해주세요.");
            return false;
        }else if(sieob_get.isEmpty()){
            Toast_Nomal("근무 시작시간을 선택해주세요.");
            return false;
        }else if(jong_eob_get.isEmpty()){
            Toast_Nomal("근무 종료시간을 선택해주세요.");
            return false;
        }else{
            return true;
        }

    }

    private void ChangeYoilBox(int i){
        binding.yoil01.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
        binding.yoil01Tv.setTextColor(Color.parseColor("#696969"));
        binding.yoil02.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
        binding.yoil02Tv.setTextColor(Color.parseColor("#696969"));
        binding.yoil03.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
        binding.yoil03Tv.setTextColor(Color.parseColor("#696969"));
        binding.yoil04.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
        binding.yoil04Tv.setTextColor(Color.parseColor("#696969"));
        binding.yoil05.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
        binding.yoil05Tv.setTextColor(Color.parseColor("#696969"));
        binding.yoil06.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
        binding.yoil06Tv.setTextColor(Color.parseColor("#696969"));
        binding.yoil07.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
        binding.yoil07Tv.setTextColor(Color.parseColor("#696969"));
        switch(i){
            case 1 :
                //월
                setYoil = "월";
                getPartTimeYoil(setYoil);
                binding.yoil01.setCardBackgroundColor(Color.parseColor("#6395EC"));
                binding.yoil01Tv.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 2 :
                //화
                setYoil = "화";
                getPartTimeYoil(setYoil);
                binding.yoil02.setCardBackgroundColor(Color.parseColor("#6395EC"));
                binding.yoil02Tv.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 3 :
                //수
                setYoil = "수";
                getPartTimeYoil(setYoil);
                binding.yoil03.setCardBackgroundColor(Color.parseColor("#6395EC"));
                binding.yoil03Tv.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 4 :
                //목
                setYoil = "목";
                getPartTimeYoil(setYoil);
                binding.yoil04.setCardBackgroundColor(Color.parseColor("#6395EC"));
                binding.yoil04Tv.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 5 :
                //금
                setYoil = "금";
                getPartTimeYoil(setYoil);
                binding.yoil05.setCardBackgroundColor(Color.parseColor("#6395EC"));
                binding.yoil05Tv.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 6 :
                //토
                setYoil = "토";
                getPartTimeYoil(setYoil);
                binding.yoil06.setCardBackgroundColor(Color.parseColor("#6395EC"));
                binding.yoil06Tv.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 7 :
                //일
                setYoil = "일";
                getPartTimeYoil(setYoil);
                binding.yoil07.setCardBackgroundColor(Color.parseColor("#6395EC"));
                binding.yoil07Tv.setTextColor(Color.parseColor("#ffffff"));
                break;
        }


    }

    public void SaveWorkPartTime() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkPartSaveInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkPartSaveInterface api = retrofit.create(WorkPartSaveInterface.class);
        Call<String> call = api.getData(place_id,item_user_id,setYoil,total_work_time_get,sieob_get,jong_eob_get,break_time_get01,break_time_get02,diff_break_time_get);
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
                                    Toast_Nomal("근무시간이 업데이트 되었습니다.");
//                                    getPartTimeYoil(setYoil);
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


    public void getPartTimeYoil(String yoil) {
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WorkPartGetInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            WorkPartGetInterface api = retrofit.create(WorkPartGetInterface.class);
            Call<String> call = api.getData(place_id,item_user_id,yoil);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    dlog.e("getPartTimeYoil");
                    dlog.e("response 1: " + response.isSuccessful());
                    dlog.e("response 2: " + response.body());
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            dlog.i("Response : " + Response);
                            if (!response.body().equals("[]")) {
                                binding.selectTime01.setText(Response.getJSONObject(0).getString("sieob"));
                                binding.selectTime02.setText(Response.getJSONObject(0).getString("jongeob"));
                                binding.selectTime03.setText(Response.getJSONObject(0).getString("breaktime01"));
                                binding.selectTime04.setText(Response.getJSONObject(0).getString("breaktime02"));
                                total_work_time_get = Response.getJSONObject(0).getString("workhour");
                                diff_break_time_get = Response.getJSONObject(0).getString("breaktime");
                            }else{
                                binding.selectTime01.setText("시간 선택");
                                binding.selectTime02.setText("시간 선택");
                                binding.selectTime03.setText("시간 선택");
                                binding.selectTime04.setText("시간 선택");
                                total_work_time_get = "";
                                diff_break_time_get = "";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Toast_Nomal(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup)findViewById(R.id.toast_layout));
        TextView toast_textview  = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
