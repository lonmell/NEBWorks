package com.krafte.nebworks.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityAccountDeleteBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.PreferenceHelper;

/*
 * 2022-10-07 방창배 작성
 * */
public class DeleteUserActivity extends AppCompatActivity {
    private ActivityAccountDeleteBinding binding;
    private final static String TAG = "DeleteUserActivity";
    Context mContext;

    PreferenceHelper shardpref;;

    //Shared
    String USER_INFO_NAME = "";
    String USER_INFO_PHONE = "";
    String USER_LOGIN_METHOD = "";

    //other
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_delete);
        binding = ActivityAccountDeleteBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
        Log.i(TAG, "USER_INFO_NAME = " + USER_INFO_NAME);
        Log.i(TAG, "USER_INFO_PHONE = " + USER_INFO_PHONE);
        Log.i(TAG, "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);
        binding.acceptDel.setBackgroundResource(R.drawable.resize_service_off);
    }


    private void setBtnEvent(){

        binding.acceptDelArea.setOnClickListener(v -> {
            if(!check){
                check = true;
                binding.acceptDel.setBackgroundResource(R.drawable.resize_service_on);
            }else{
                check = false;
                binding.acceptDel.setBackgroundResource(R.drawable.resize_service_off);
            }
        });

        binding.delUser.setOnClickListener(v -> {
            if(!check){
                Toast_Nomal("안내사항 확인 동의에 체크해주세요.");
            }else{
                Intent intent = new Intent(this, TwoButtonPopActivity.class);
                intent.putExtra("data", "회원탈퇴 하시겠습니까?\n 모든 정보가 삭제됩니다.");
                intent.putExtra("flag", "회원탈퇴");
                intent.putExtra("left_btn_txt", "닫기");
                intent.putExtra("right_btn_txt", "탈퇴하기");
                startActivity(intent);
            }

        });

        binding.backBtn.setOnClickListener( v-> {
            onBackPressed();
        });
    }
    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) binding.getRoot().findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
