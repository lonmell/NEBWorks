package com.krafte.kogas.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.krafte.kogas.R;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PreferenceHelper;

import java.util.Calendar;

public class DatePickerYearActivity extends Activity {
    private static final String TAG = "DatePickerActivity";
    Context mContext;
    View view;
    Activity activity;

    private static final int MAX_YEAR = 2300;
    private static final int MIN_YEAR = 2020;

    public Calendar cal = Calendar.getInstance();


    //XML ID
    TextView btn_confirm;
    TextView btn_cancel;
    NumberPicker picker_year;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    DateCurrent dc = new DateCurrent();
    int setSelectPicker = 0;
    Dlog dlog = new Dlog();

    //Intent data
    String data = "";
    String getYear, getMonth, getDay;
    String getDate;


    //Other
    Intent intent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.datepickeryear_item);
        mContext = this;
        dlog.DlogContext(mContext);

        //데이터 가져오기
        intent = getIntent();
        data = intent.getStringExtra("data");

        shardpref = new PreferenceHelper(mContext);

        setContentLayout();
        setBtnEvent();

    }

    private void setContentLayout() {
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);
        picker_year = findViewById(R.id.picker_year);
    }


    private void setBtnEvent() {
        btn_cancel.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, R.anim.translate_down);
        });
        btn_confirm.setOnClickListener(v -> {
            picker_year.clearFocus();
            dlog.i("picker_year : " + picker_year.getValue());
            shardpref.putString("picker_year",String.valueOf(picker_year.getValue()));
            finish();
            overridePendingTransition(0, R.anim.translate_down);
        });

        int year = cal.get(Calendar.YEAR);
        picker_year.setMinValue(MIN_YEAR);
        picker_year.setMaxValue(MAX_YEAR);
        picker_year.setValue(year);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.translate_down);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 아래로 내려감
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
            return true;
        }
        return true;
    }

}

