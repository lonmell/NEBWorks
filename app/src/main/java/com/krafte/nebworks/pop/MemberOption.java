package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityBottomMemberoptionBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;

public class MemberOption extends Activity {
    private ActivityBottomMemberoptionBinding binding;
    private static final String TAG = "AlertPopActivity";
    Context mContext;

    //Other
    String btn01            = "";
    String btn02            = "";
    String data             = "";
    Intent intent;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityBottomMemberoptionBinding.inflate(getLayoutInflater());
//        setContentView(R.layout.activity_onebutton_pop);
        setContentView(binding.getRoot());
        mContext = this;
        dlog.DlogContext(mContext);

        //데이터 가져오기
        intent = getIntent();
        data   = intent.getStringExtra("data");
        btn01  = intent.getStringExtra("btn01");
        btn02  = intent.getStringExtra("btn02");

        binding.directlyAdd.setText(btn01);
        binding.invateAdd.setText(btn02);

        setBtnEvent();
        Log.i(TAG,"data : " + data);

    }

    //확인 버튼 클릭
    private void setBtnEvent() {
        binding.directlyAdd.setOnClickListener(v -> {
            if(data.equals("직원등록")){
                pm.DirectAddMember(mContext);
            }
        });
        binding.invateAdd.setOnClickListener(v -> {
            if(data.equals("직원등록")){

            }
        });
        binding.cancel.setOnClickListener(v -> {
            ClosePop();
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

    private void ClosePop(){
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }
}
