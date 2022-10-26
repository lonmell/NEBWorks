package com.krafte.kogas.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.krafte.kogas.R;

public class OneButtonTItlePopActivity extends Activity {

    private static final String TAG = "OneButtonTItlePopActivity";
    Context mContext;
    TextView pop_title,txtText,txtText02;
    TextView pop_left_txt,pop_right_txt;
    RelativeLayout pop_left_btn,pop_right_btn;


    private String title            = "";
    private String data             = "";
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_onebutton_pop2);

        mContext = this;

        //데이터 가져오기
        intent = getIntent();
        title         = intent.getStringExtra("title");
        data             = intent.getStringExtra("data");

        setContentLayout();
        setBtnEvent();

        pop_title.setText(title);
        txtText.setText(data);

    }
    private void setContentLayout() {
        //UI 객체생성
        txtText         = findViewById(R.id.txtText);
        pop_title       = findViewById(R.id.pop_title);
        pop_left_btn    = findViewById(R.id.pop_left_btn);
        pop_left_txt    = findViewById(R.id.pop_left_txt);
    }
    //확인 버튼 클릭
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnEvent() {
        pop_left_btn.setOnClickListener(v -> {
            //데이터 전달하기
            //액티비티(팝업) 닫기
            finish();
            overridePendingTransition(0, R.anim.translate_down);
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
