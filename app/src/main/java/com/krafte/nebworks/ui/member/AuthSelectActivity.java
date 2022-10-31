package com.krafte.nebworks.ui.member;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.databinding.ActivityAuthselectBinding;
import com.krafte.nebworks.util.PreferenceHelper;

public class AuthSelectActivity extends AppCompatActivity {
    private ActivityAuthselectBinding binding;
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
        binding = ActivityAuthselectBinding.inflate(getLayoutInflater()); // 1
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
    }


    private void setBtnEvent(){

    }
}
