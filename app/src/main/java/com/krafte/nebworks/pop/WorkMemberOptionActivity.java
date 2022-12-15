package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityMemberoptionBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.DecimalFormat;

public class WorkMemberOptionActivity extends Activity {
    private ActivityMemberoptionBinding binding;
    private static final String TAG = "WorkMemberOptionActivity";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    // shared 저장값
    PreferenceHelper shardpref;

    String place_id = "";
    String user_id = "";
    Intent intent;

    String USER_INFO_ID = "";
    String mem_id = "";
    String mem_name = "";
    String remote = "";

    //Other
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result = "";
    GetResultData resultData = new GetResultData();
    Handler mHandler;
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityMemberoptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_memberoption);
        mContext = this;

        dlog.DlogContext(mContext);

        //데이터 가져오기
        intent = getIntent();
        place_id = intent.getStringExtra("place_id");
        user_id = intent.getStringExtra("user_id");

        shardpref = new PreferenceHelper(mContext);
        mem_id = shardpref.getString("mem_id", "");
        mem_name = shardpref.getString("mem_name", "");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        remote = shardpref.getString("remote", "");

        Log.i(TAG, "place_id : " + place_id);
        Log.i(TAG, "user_id : " + user_id);
        Log.i(TAG, "mem_id : " + mem_id);
        Log.i(TAG, "USER_INFO_ID : " + USER_INFO_ID);
        if (mem_id.equals(USER_INFO_ID)) {
            binding.listSettingitem02.setVisibility(View.GONE);
        } else {
            binding.listSettingitem02.setVisibility(View.VISIBLE);
        }
        setBtnEvent();
    }

    private void setBtnEvent() {

        binding.listSettingitem01.setOnClickListener(v -> {
            pm.AddMemberDetail(mContext);
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });

        binding.listSettingitem02.setOnClickListener(v -> {
            if (mem_id.equals(USER_INFO_ID)) {
                Toast_Nomal("관리자계정은 삭제할수 없습니다.");
            } else {
                if(remote.equals("workhour")){
                    Intent intent = new Intent(this, TwoButtonPopActivity.class);
                    intent.putExtra("data", "[" + mem_name + "]님의 근무정보를 삭제하시겠습니까?");
                    intent.putExtra("flag", "근무정보삭제");
                    intent.putExtra("left_btn_txt", "취소");
                    intent.putExtra("right_btn_txt", "삭제");
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }else{
                    Intent intent = new Intent(this, TwoButtonPopActivity.class);
                    intent.putExtra("data", "[" + mem_name + "]님의 정보를 삭제하시겠습니까?");
                    intent.putExtra("flag", "직원삭제");
                    intent.putExtra("left_btn_txt", "취소");
                    intent.putExtra("right_btn_txt", "삭제");
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }

                closePop();
            }
        });

        binding.closeBtn.setOnClickListener(v -> {
            closePop();
        });
    }

    private void closePop() {
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("mem_id");
        shardpref.remove("mem_name");
        shardpref.remove("mem_phone");
        shardpref.remove("mem_gender");
        shardpref.remove("mem_jumin");
        shardpref.remove("mem_kind");
        shardpref.remove("mem_join_date");
        shardpref.remove("mem_state");
        shardpref.remove("mem_jikgup");
        shardpref.remove("mem_pay");
    }



    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
