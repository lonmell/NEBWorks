package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityContractAdd01Binding;
import com.krafte.nebworks.pop.LawPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

public class AddContractPage01 extends AppCompatActivity {
    private ActivityContractAdd01Binding binding;
    private final static String TAG = "AddContractPage01";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractAdd01Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);
        setBtnEvent();
        //LawPopActivity
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setBtnEvent(){
        binding.writeContract.setOnClickListener(v -> {
            pm.AddContractPage02(mContext);
        });

        binding.select01.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LawPopActivity.class);
            intent.putExtra("flag", "1");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });
        binding.select02.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LawPopActivity.class);
            intent.putExtra("flag", "2");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });
        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("progress_pos");
            if(!shardpref.getString("progress_pos","").isEmpty()){
                pm.ContractFragment(mContext);
            }else{
                super.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(!shardpref.getString("progress_pos","").isEmpty()){
            pm.ContractFragment(mContext);
        }else{
            super.onBackPressed();
        }
        shardpref.remove("progress_pos");
    }
}
