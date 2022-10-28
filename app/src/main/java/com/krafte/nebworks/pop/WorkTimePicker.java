package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityTimepickerPopBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

public class WorkTimePicker extends Activity {
    private ActivityTimepickerPopBinding binding;
    Context mContext;

    //Other
    Intent intent;

    Dlog dlog = new Dlog();
    int data;
    int Hour = 0;
    int Min = 0;
    PreferenceHelper shardpref;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_timepicker_pop);
        binding = ActivityTimepickerPopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        
        mContext = this;
        
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        intent = getIntent();
        data   = intent.getIntExtra("timeSelect_flag",0);

        dlog.i("data : " + data);

        setBtnEvent();

        binding.timeSetpicker.setIs24HourView(false);
    }
   
    //확인 버튼 클릭
    @SuppressLint("ObsoleteSdkInt")
    private void setBtnEvent() {
        binding.saveBtn.setOnClickListener(v -> {
            binding.timeSetpicker.clearFocus();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Hour = binding.timeSetpicker.getHour();
                Min = binding.timeSetpicker.getMinute();
            }else{
                Hour = binding.timeSetpicker.getCurrentHour();
                Min = binding.timeSetpicker.getCurrentMinute();
            }
            shardpref.putInt("timeSelect_flag",data);
            shardpref.putInt("Hour",Hour);
            shardpref.putInt("Min",Min);
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


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
//    }


    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
    }


}
