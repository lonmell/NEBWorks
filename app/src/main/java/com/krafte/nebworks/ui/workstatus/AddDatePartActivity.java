package com.krafte.nebworks.ui.workstatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.InOutInsert2Interface;
import com.krafte.nebworks.databinding.ActivityAddworkpartBinding;
import com.krafte.nebworks.pop.SelectMemberPop;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddDatePartActivity extends AppCompatActivity {
    private ActivityAddworkpartBinding binding;
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
    String USER_INFO_NAME = "";
    String place_owner_id = "";
    String FtoDay = "";

    String place_id = "";
    String place_name = "";
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
            //Singleton Area
            USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
            USER_INFO_NAME  = UserCheckData.getInstance().getUser_name();
            USER_INFO_EMAIL = UserCheckData.getInstance().getUser_account();
            USER_INFO_AUTH  = UserCheckData.getInstance().getUser_auth();
            place_owner_id  = PlaceCheckData.getInstance().getPlace_owner_id();
            place_id        = PlaceCheckData.getInstance().getPlace_id();
            place_name      = PlaceCheckData.getInstance().getPlace_name();

            //shardpref Area
            shardpref   = new PreferenceHelper(mContext);
            FtoDay      = shardpref.getString("FtoDay", "0");

            binding.storeName.setText(place_name);
            binding.line01.setVisibility(View.GONE);
            binding.breakTimeArea.setVisibility(View.GONE);
            setBtnEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    String item_user_id = "";
    String item_user_name = "";

    @Override
    public void onResume() {
        super.onResume();
        dlog.i("-----onResume-----");
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("place_owner_id : " + place_owner_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);

        if(!place_owner_id.equals(USER_INFO_ID)){
            //근로자가 추가할때
            binding.memName.setVisibility(View.VISIBLE);
            binding.memCnt.setVisibility(View.GONE);
            binding.memSelect.setVisibility(View.GONE);
            item_user_id = USER_INFO_ID;
            item_user_name = USER_INFO_NAME;
            dlog.i("item_user_id : " + item_user_id);
            dlog.i("item_user_name : " + item_user_name);
            binding.memName.setText(item_user_name);
        }else{
            //점주가 추가할때
            //부여할 사용자 가져오기
            binding.memName.setVisibility(View.GONE);
            binding.memCnt.setVisibility(View.VISIBLE);
            binding.memSelect.setVisibility(View.VISIBLE);
            item_user_id = shardpref.getString("item_user_id", "");
            item_user_name = shardpref.getString("item_user_name", "");
            if (!item_user_id.isEmpty() && !item_user_name.isEmpty()) {
                binding.memName.setVisibility(View.VISIBLE);
                binding.memCnt.setVisibility(View.GONE);
                binding.memSelect.setVisibility(View.GONE);
                binding.memName.setText(item_user_name);
            } else {
                binding.memName.setVisibility(View.GONE);
                binding.memCnt.setVisibility(View.VISIBLE);
                binding.memSelect.setVisibility(View.VISIBLE);
            }
        }

        //시간 지정하기
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
        int hourOfDay = shardpref.getInt("Hour", 0);
        int minute = shardpref.getInt("Min", 0);
        String GetTime = "";
        dlog.i("------------------Data Check onResume------------------");
        dlog.i("timeSelect_flag : " + timeSelect_flag);
        dlog.i("GetTime : " + GetTime);
        dlog.i("------------------Data Check onResume------------------");


        dlog.i("-----onResume-----");
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    boolean SELECTTIME01 = false; // 근무시간 false - 시작시간 / true - 종료시간
    boolean SELECTTIME02 = false; // 휴식시간 false - 시작시간 / true - 종료시간
    String GetTime = "";
    List<String> resultYoil = new ArrayList<>();
    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });
        binding.yoil.setCardBackgroundColor(Color.parseColor("#ffffff"));
        binding.yoilTv.setText(FtoDay);

        binding.selectMem.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SelectMemberPop.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.selectTime01.setText("시간선택");
        binding.selectTime02.setText("시간선택");
        binding.selectTime03.setText("시간선택");
        binding.selectTime04.setText("시간선택");
        binding.inputbox01box.setOnClickListener(v -> {
            SELECTTIME01 = false;
            binding.inputbox01box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputbox02box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker1.setVisibility(View.VISIBLE);
            binding.timeSetpicker2.setVisibility(View.GONE);
        });
        binding.inputbox02box.setOnClickListener(v -> {
            SELECTTIME01 = true;
            binding.inputbox01box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputbox02box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.timeSetpicker1.setVisibility(View.VISIBLE);
            binding.timeSetpicker2.setVisibility(View.GONE);
        });
        binding.inputbox03box.setOnClickListener(v -> {
            SELECTTIME02 = false;
            binding.inputbox03box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.inputbox04box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.timeSetpicker1.setVisibility(View.GONE);
            binding.timeSetpicker2.setVisibility(View.VISIBLE);
        });
        binding.inputbox04box.setOnClickListener(v -> {
            SELECTTIME02 = true;
            binding.inputbox03box.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.inputbox04box.setCardBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.timeSetpicker1.setVisibility(View.GONE);
            binding.timeSetpicker2.setVisibility(View.VISIBLE);
        });



        binding.timeSetpicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String HOUR = String.valueOf(hourOfDay);
                String MIN = String.valueOf(minute);
                binding.timeSetpicker1.clearFocus();
                if(!SELECTTIME01){
                    sieob_get = HOUR + ":" + MIN;
                    binding.selectTime01.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                }else{
                    jong_eob_get = HOUR + ":" + MIN;
                    binding.selectTime02.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                }
            }
        });

        binding.timeSetpicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String HOUR = String.valueOf(hourOfDay);
                String MIN = String.valueOf(minute);
                binding.timeSetpicker2.clearFocus();
                if(!SELECTTIME02){
                    break_time_get01 = HOUR + ":" + MIN;
                    binding.selectTime03.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                }else{
                    break_time_get02 = HOUR + ":" + MIN;
                    binding.selectTime04.setText((hourOfDay < 12?"오전":"오후") + " " + (HOUR.length() == 1?"0"+HOUR:HOUR) + ":" + (MIN.length() == 1?"0"+MIN:MIN));
                }
            }
        });
        binding.saveWorkpart.setOnClickListener(v -> {
            if (SaveCheck()) {
                InOutInsert("0",FtoDay,sieob_get);
                InOutInsert("1",FtoDay,jong_eob_get);
            }
        });
    }

    private boolean SaveCheck() {
        if(sieob_get.equals("시간선택") || jong_eob_get.equals("시간선택")){
            Toast_Nomal("입력되지 않은 시간이 있습니다.");
            return false;
        }else{
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
            dlog.i("yoil : " + FtoDay);
            dlog.i("item_user_id : " + item_user_id);
            dlog.i("WorkStartTime : " + sieob_get);
            dlog.i("WorkEndTime : " + jong_eob_get);
            dlog.i("TotalWorkTime : " + total_work_time_get);
            dlog.i("RestStart : " + break_time_get01);
            dlog.i("RestEnd : " + break_time_get02);
            dlog.i("RestTotal : " + diff_break_time_get);
            dlog.i("-----SaveCheck-----");
            if (FtoDay.isEmpty()) {
                Toast_Nomal("근무날짜가 데이터가 없습니다.");
                return false;
            } else if(item_user_id.isEmpty()){
                Toast_Nomal("근무자를 배정해주세요.");
                return false;
            } else if (sieob_get.isEmpty()) {
                Toast_Nomal("근무 시작시간을 선택해주세요.");
                return false;
            } else if (jong_eob_get.isEmpty()) {
                Toast_Nomal("근무 종료시간을 선택해주세요.");
                return false;
            } else if (break_time_get01.isEmpty()) {
                Toast_Nomal("휴게시작시간을 선택해주세요.");
                return false;
            } else if (break_time_get02.isEmpty()) {
                Toast_Nomal("휴게종료시간을 선택해주세요.");
                return false;
            } else {
                return true;
            }
        }
    }

    private void InOutInsert(String kind, String date, String time) {
        String change_place_id = "";
        change_place_id = shardpref.getString("change_place_id", "");
        if (!change_place_id.isEmpty()) {
            place_id = change_place_id;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InOutInsert2Interface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        InOutInsert2Interface api = retrofit.create(InOutInsert2Interface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, kind, date, time);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.body().replace("[", "").replace("]", "").replace("\"", "").length() == 0) {
                            //최초 출근

                        } else if (response.body().replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && response.body() != null) {
                                dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                                dlog.i("LoginCheck jsonResponse : " + response.body());
                                try {
                                    if (response.body().replace("[", "").replace("]", "").replace("\"", "").equals("success")) {
                                        Toast_Nomal("근무시간이 업데이트 되었습니다.");
                                        shardpref.remove("item_user_id");
                                        shardpref.remove("item_user_name");
                                        shardpref.putInt("SELECT_POSITION", 2);
                                        pm.Main(mContext);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
