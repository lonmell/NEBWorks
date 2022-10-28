package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.krafte.nebworks.R;

public class AlertPopActivity extends Activity {

    private static final String TAG = "AlertPopActivity";
    Context mContext;

    //XML ID
    TextView txtText;
    RelativeLayout pop_left_btn;


    //Other
    String data             = "";
    Intent intent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_onebutton_pop);
        mContext = this;

        //데이터 가져오기
        intent = getIntent();
        data   = intent.getStringExtra("data");


        setContentLayout();
        setBtnEvent();
        Log.i(TAG,"data : " + data);
        txtText.setText(data);
    }
    private void setContentLayout() {
        //UI 객체생성
        txtText  = findViewById(R.id.txtText);
        pop_left_btn    = findViewById(R.id.pop_left_btn);

    }
    //확인 버튼 클릭
    private void setBtnEvent() {
        pop_left_btn.setOnClickListener(v -> {
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
