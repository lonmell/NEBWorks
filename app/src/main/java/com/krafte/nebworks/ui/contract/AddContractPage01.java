package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityContractAdd01Binding;
import com.krafte.nebworks.pop.LawPopActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class AddContractPage01 extends AppCompatActivity {
    private ActivityContractAdd01Binding binding;
    private final static String TAG = "AddContractPage01";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

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
    }

    @Override
    public void onResume() {
        super.onResume();
        String termaccept = shardpref.getString("termaccept","");
        if(!termaccept.equals("")){
            if(termaccept.equals("01")){
                binding.arrow1.setBackgroundResource(R.drawable.ic_full_round_check);
                binding.arrow1.setRotation(-90);
            }else if(termaccept.equals("02")){
                binding.arrow2.setBackgroundResource(R.drawable.ic_full_round_check);
                binding.arrow2.setRotation(-90);
            }
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        shardpref.remove("termaccept");
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
