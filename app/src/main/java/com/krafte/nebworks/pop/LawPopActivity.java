package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.Window;

import androidx.annotation.RequiresApi;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityLawPopBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LawPopActivity extends Activity {
    private ActivityLawPopBinding binding;
    private static final String TAG = "LawPopActivity";
    Context mContext;

    Intent intent;
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    String flag = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityLawPopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = this;
        shardpref = new PreferenceHelper(mContext);

        //데이터 가져오기
        intent = getIntent();
        flag  = intent.getStringExtra("flag");

        setBtnEvent();
        setContents();
    }
    private void setContents(){
        if(flag.equals("1")){
            loadPolicy("law_pop1.txt");
        }else{
            loadPolicy("law_pop2.txt");
        }
    }
    //확인 버튼 클릭
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnEvent() {
        binding.closeBtn.setOnClickListener(v -> {
            //데이터 전달하기
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(R.anim.translate_down, 0);
            finish();
            //액티비티(팝업) 닫기
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

    private void loadPolicy(String title) {
        try {
            String policy = readFromAssets(title);
            binding.contents.setText(Html.fromHtml(policy));

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String readFromAssets(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while(line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
}
