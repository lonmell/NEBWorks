package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.PreferenceHelper;

public class JoinPopActivity extends Activity {

    private static final String TAG = "JoinPopActivity";
    Context mContext;
    TextView txtText;
    TextView pop_left_txt,pop_right_txt;
    RelativeLayout pop_left_btn,pop_right_btn;

    String title            = "";
    String data             = "";
    String left_btn_txt     = "";
    String right_btn_txt    = "";

    String USER_INFO_ID = "";
    String store_no = "";

    //----ContractActivity02
    String employer_id = "";
    String employment_name = "";
    String no = "";
    //----
    Intent intent;
    GetResultData resultData = new GetResultData();
    DBConnection dbConnection = new DBConnection();
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
        setContentView(R.layout.activity_twobutton_pop);
        mContext = this;

        //데이터 가져오기
        intent = getIntent();
        data             = intent.getStringExtra("data");
        left_btn_txt     = intent.getStringExtra("left_btn_txt");
        right_btn_txt    = intent.getStringExtra("right_btn_txt");

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        store_no = shardpref.getString("store_no","");
        employer_id = shardpref.getString("employer_id","");
        employment_name = shardpref.getString("employment_name", "");
        no              = shardpref.getString("no","");
        setContentLayout();
        setBtnEvent();

        if (title.equals("알림")){
            txtText.setVisibility(View.INVISIBLE);
        }
        if(data.equals("Kakao")){
            txtText.setText("계정연동 끊기안내\n "
                    +"더보기 > 설정 > \n"
                    +"개인/보안 > 카카오 계정 >\n 연결된 서비스 관리 > "
                    +"외부서비스 > HEYPASS 선택 후 모든정보 삭제 / 연동 끊기");
        }else{
            txtText.setText(data);
        }

        pop_left_txt.setText(left_btn_txt);
        pop_right_txt.setText(right_btn_txt);

    }
    private void setContentLayout() {
        //UI 객체생성
        txtText         = findViewById(R.id.txtText);
        pop_left_btn    = findViewById(R.id.pop_left_btn);
        pop_right_btn   = findViewById(R.id.pop_right_btn);
        pop_left_txt    = findViewById(R.id.pop_left_txt);
        pop_right_txt   = findViewById(R.id.pop_right_txt);
    }
    //확인 버튼 클릭
    private void setBtnEvent() {

        pop_right_txt.setOnClickListener(v -> {
            if(left_btn_txt.equals("로그인하기")){
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.translate_down);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }else{
                //데이터 전달하기
                //액티비티(팝업) 닫기
                finish();
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                overridePendingTransition(0, R.anim.translate_down);

            }

        });

        pop_left_btn.setOnClickListener(v -> {
            switch (left_btn_txt) {
                case "종료":
//                super.onBackPressed();
                    finish();
                    moveTaskToBack(true); // 태스크를 백그라운드로 이동
                    finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                    android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
//                moveTaskToBack(true); // 태스크를 백그라운드로 이동
//                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
//                System.exit(0);
                    break;
                case "뒤로가기":
                    super.onBackPressed();
                    break;
                case "로그인하기":
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, R.anim.translate_down);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                case "탈퇴하기":
                    UserInfoDelete();
                    if (resultData.getRESULT().equals("success")) {
                        Toast.makeText(this, "탈퇴가 완료되었습니다.", Toast.LENGTH_LONG).show();
                        shardpref.clear();
                        intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.translate_down);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    } else {
                        Toast.makeText(this, "계정을 찾을 수 없습니다.\n고객센터에 문의하세요.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case "ID/PW 찾기":
//                    intent = new Intent(mContext, FindAccountActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(0, R.anim.translate_down);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    break;
                case "삭제" :
//                    Setworking_hoursList(3);
                    //데이터 전달하기
//                    Intent intent = new Intent();
//                    intent.putExtra("result", "Close Popup");
//                    setResult(RESULT_OK, intent);
//                    overridePendingTransition(0, R.anim.translate_down);
//                    //액티비티(팝업) 닫기
//                    finish();
                    break;
            }
        });


    }

    private void UserInfoDelete() {
//        Thread th = new Thread(() -> {
//            dbConnection.UserInfoDelete(USER_INFO_ID);
//            runOnUiThread(() -> Log.i(TAG, "Result = " + resultData.getRESULT()));
//        });
//        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

//    /*근로계약서 작성여부 확인 START*/
//    public void Setworking_hoursList(int flag) {
//        @SuppressLint({"LongLogTag", "NotifyDataSetChanged"}) Thread th = new Thread(() -> {
//            dbConnection.contractWorkHourData_lists.clear();
//            dbConnection.SojeongGeunlosigan(flag,no,store_no, employer_id, employment_name
//                    ,"", "", "", "", "");
//
//            Log.e(TAG,"ResultData = " + resultData);
//        });
//        th.start();
//        try {
//            th.join();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//    /*근로계약서 작성여부 확인 START*/

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
    }
}
