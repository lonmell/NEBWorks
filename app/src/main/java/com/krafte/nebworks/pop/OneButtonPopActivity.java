package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.annotation.RequiresApi;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityOnebuttonPopBinding;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.ui.worksite.PlaceListActivity;

public class OneButtonPopActivity  extends Activity {
    private ActivityOnebuttonPopBinding binding;
    private static final String TAG = "OneButtonPopActivity";
    Context mContext;


    private String title            = "";
    private String data             = "";
    private String left_btn_txt     = "";
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
//        setContentView(R.layout.activity_onebutton_pop);
        binding = ActivityOnebuttonPopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        mContext = this;

        //데이터 가져오기
        intent = getIntent();
        data             = intent.getStringExtra("data");
        left_btn_txt     = intent.getStringExtra("left_btn_txt");

        setBtnEvent();

        if (title.equals("알림")){
            binding.txtText.setVisibility(View.INVISIBLE);
        }
        if(data.equals("Kakao")){
            binding.txtText.setText("계정연동 끊기안내\n "
                    +"더보기 > 설정 > \n"
                    +"개인/보안 > 카카오 계정 >\n 연결된 서비스 관리 > "
                    +"외부서비스 > HEYPASS 선택 후 모든정보 삭제 / 연동 끊기");
        }else{
            binding.txtText.setText(data);
        }

        binding.popLeftTxt.setText(left_btn_txt);

    }


    //확인 버튼 클릭
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnEvent() {
        binding.popLeftTxt.setOnClickListener(v -> {
            if(data.equals("비밀번호가 성공적으로 변경되었습니다.") || data.equals("카카오 로그인에 문제가 생겼습니다.")){
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }else if(data.equals("최신버전의 앱이 있습니다.\n 업데이트가 필요합니다.")){
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id="+ getPackageName()));
                this.startActivity(intent);
            }else if(data.equals("GPS 설정이 필요합니다.")){
                //GPS 설정화면으로 이동
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
            }else if(data.equals("최신버전 앱으로 업데이트를 위해\n스토어로 이동합니다")){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                mContext.startActivity(intent);
            }else if(left_btn_txt.equals("로그인하기") || left_btn_txt.equals("뒤로가기")){
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.translate_down);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }else if(left_btn_txt.equals("회원정보 변경이 완료되었습니다.") || left_btn_txt.equals("매장 추가가 완료되었습니다.")){
                intent = new Intent(mContext, PlaceListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.translate_down);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }else if(data.equals("사용자정보를 찾을 수 없습니다, 다시 로그인해 주세요.")){
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
