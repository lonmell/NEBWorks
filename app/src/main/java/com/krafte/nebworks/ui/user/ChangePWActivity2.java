package com.krafte.nebworks.ui.user;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.krafte.nebworks.databinding.ActivityFindemailBinding;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class ChangePWActivity2 extends AppCompatActivity {
    private ActivityFindemailBinding binding;
    private final static String TAG = "ChangePWActivity2";
    Context mContext;

    PreferenceHelper shardpref;;

    //Shared
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_PHONE = "";
    String USER_LOGIN_METHOD = "";

    //other
    boolean check = false;
    ClipboardManager clipboard;
    Handler mHandler;
    PageMoveClass pm = new PageMoveClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_delete);
        binding = ActivityFindemailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
        Log.i(TAG, "USER_INFO_EMAIL = " + USER_INFO_EMAIL);
        Log.i(TAG, "USER_INFO_PHONE = " + USER_INFO_PHONE);

        binding.findEmailImg.setBackgroundResource(R.drawable.change_pw_end);
        binding.copyemailArea.setVisibility(View.GONE);
        binding.findPwtv.setText("이메일도 찾기");
    }


    private void setBtnEvent(){
        binding.goLogin.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            pm.Login(mContext);
        });

        binding.findPwBtn.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            pm.FindEmail(mContext);
        });
    }
    public void Toast_Nomal(String message){
        BtnOneCircleFun(true);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup)findViewById(R.id.toast_layout));
        TextView toast_textview  = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    private void BtnOneCircleFun(boolean tf){
        binding.goLogin.setClickable(tf);
        binding.goLogin.setEnabled(tf);

        binding.findPwBtn.setClickable(tf);
        binding.findPwBtn.setEnabled(tf);
    }
}
