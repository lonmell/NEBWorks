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
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.krafte.kogas.R;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;

public class InoutPopActivity extends Activity {

    private static final String TAG = "OneButtonPopActivity";
    Context mContext;

    ImageView inout_icon;
    TextView pop_title,inout_tv,inout_tv2,close_btn;

    private String title            = "";
    private String time             = "";
    private String state            = "";
    private String store_name       = "";

    Intent intent;
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inout_pop);

        mContext = this;
        shardpref = new PreferenceHelper(mContext);

        //데이터 가져오기
        intent = getIntent();
        title       = intent.getStringExtra("title");
        time        = intent.getStringExtra("time");
        state       = intent.getStringExtra("state");
        store_name  = intent.getStringExtra("store_name");

        setContentLayout();
        setBtnEvent();

        if(state.equals("1")){
            inout_icon.setBackgroundResource(R.drawable.in_icon);
        }else{
            inout_icon.setBackgroundResource(R.drawable.out_icon);
        }

        pop_title.setText(time);
        inout_tv.setText(title);
        inout_tv2.setText(store_name);

    }
    private void setContentLayout() {
        //UI 객체생성
        inout_icon   = findViewById(R.id.inout_icon);
        pop_title    = findViewById(R.id.pop_title);
        inout_tv     = findViewById(R.id.inout_tv);
        inout_tv2    = findViewById(R.id.inout_tv2);
        close_btn    = findViewById(R.id.close_btn);

    }
    //확인 버튼 클릭
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnEvent() {
        close_btn.setOnClickListener(v -> {
            //데이터 전달하기
//            Intent intent = new Intent();
//            intent.putExtra("result", "Close Popup");
//            setResult(RESULT_OK, intent);
//            overridePendingTransition(R.anim.translate_down, 0);
            //액티비티(팝업) 닫기
//            finish();
            pm.MainGo(mContext);
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
