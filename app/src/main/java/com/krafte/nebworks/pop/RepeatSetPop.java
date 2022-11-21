package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityRepeatsetpopBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.Calendar;

public class RepeatSetPop extends Activity {
    private ActivityRepeatsetpopBinding binding;
    private static final String TAG = "SelectTaskDatePop";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();

    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String USER_INFO_ID = "";
    String SET_TASK_TIME_VALUE = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityRepeatsetpopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");
        //0 - 시작시간 / 1 - 마감시간
        SET_TASK_TIME_VALUE = shardpref.getString("SET_TASK_TIME_VALUE","0");

        binding.backBtn.setOnClickListener(v -> {
            closePop();
        });

    }

    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getYMPicker = "";
    String bYear = "";
    String bMonth = "";
    @Override
    public void onResume(){
        super.onResume();
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
                binding.eventStarttime.setText("   " + Year + "년 " + Month + "월   ");
                getYMPicker = binding.eventStarttime.getText().toString().substring(0,7);
            }
        }, mYear, mMonth, mDay);

        binding.eventStarttime.setOnClickListener(view -> {
            if (binding.eventStarttime.isClickable()) {
                datePickerDialog.show();
            }
        });
    }

    private void closePop() {
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }
}
