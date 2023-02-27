package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

import com.krafte.nebworks.databinding.ActivityFeedreportBinding;
import com.krafte.nebworks.util.PreferenceHelper;

public class feedReportPopActivity extends Activity {
    private ActivityFeedreportBinding binding;
    private static final String TAG = "feedReportPopActivity";
    Context mContext;

    String title            = "";
    String data             = "";
    String left_btn_txt     = "";
    String right_btn_txt    = "";

    String USER_INFO_ID = "";

    //----
    Intent intent;
    PreferenceHelper shardpref;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_feedreport);
        binding = ActivityFeedreportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;

        //데이터 가져오기
        intent = getIntent();
        data             = intent.getStringExtra("data");
        left_btn_txt     = intent.getStringExtra("left_btn_txt");
        right_btn_txt    = intent.getStringExtra("right_btn_txt");

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

    }

    //확인 버튼 클릭
    private void setBtnEvent() {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }


    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
    }
}
