package com.krafte.kogas.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.krafte.kogas.R;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PreferenceHelper;

public class DatePickerymdActivity extends Activity {
    private static final String TAG = "DatePickerActivity";
    Context mContext;

    //XML ID
    DatePicker vDatePicker;
    TextView confirm_btn,cancel_btn;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();

    //Intent data
    String data = "";
    String getYear,getMonth,getDay;
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
        setContentView(R.layout.datepicker_item);
        mContext = this;
        dlog.DlogContext(mContext);

        //데이터 가져오기
        intent = getIntent();
        data = intent.getStringExtra("data");

        shardpref = new PreferenceHelper(mContext);

        setContentLayout();
        setBtnEvent();

    }

    private void setContentLayout(){
        vDatePicker = findViewById(R.id.vDatePicker);
        confirm_btn = findViewById(R.id.confirm_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
    }


    private void setBtnEvent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    getYear = String.valueOf(year+1).length() == 1?"0"+ year:String.valueOf(year);
                    getMonth = String.valueOf(monthOfYear+1).length() == 1?"0"+ String.valueOf(monthOfYear+1):String.valueOf(monthOfYear+1);
                    getDay = String.valueOf(dayOfMonth).length() == 1?"0"+ dayOfMonth:String.valueOf(dayOfMonth);
                    getDate = getYear + "-" + getMonth + "-" + getDay;
//                    dlog.d(getDate);
                }
            });
        }
        confirm_btn.setOnClickListener(v -> {
            dlog.d(getDate);
            shardpref.putString("getYear",getYear);
            shardpref.putString("getMonth",getMonth);
            shardpref.putString("getDay",getDay);
            finish();
            overridePendingTransition(0, R.anim.translate_down);
        });

        cancel_btn.setOnClickListener(v -> {
            shardpref.remove("getYear");
            shardpref.remove("getMonth");
            shardpref.remove("getDay");
            finish();
            overridePendingTransition(0, R.anim.translate_down);
        });

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
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
