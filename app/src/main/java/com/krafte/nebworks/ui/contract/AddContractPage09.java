package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityContractAdd09Binding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

public class AddContractPage09 extends AppCompatActivity {
    private ActivityContractAdd09Binding binding;
    private final static String TAG = "AddContractPage08";
    private static final int SIGNING_BITMAP = 2022;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";
    String contract_id = "";
    String contract_email = "";

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
        binding = ActivityContractAdd09Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref       = new PreferenceHelper(mContext);
        place_id        = shardpref.getString("place_id","0");
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID","0");
        worker_id       = shardpref.getString("worker_id","0");
        contract_id     = shardpref.getString("contract_id","0");
        contract_email  = shardpref.getString("contract_email","0");
        setBtnEvent();
    }

    String kakaotitle = "";
    String emailtitle = "";
    private void setBtnEvent(){
        binding.next.setOnClickListener(v -> {
                if(!kakaotitle.isEmpty()){
                    //카카오 타이틀이 있을때
                    if(!emailtitle.isEmpty()){
                        //이메일 타이틀이 있을때
                        RemoveShared();
                        pm.ContractFragment(mContext);
                    }
                }else{
                    RemoveShared();
                    pm.ContractFragment(mContext);
                }
        });
    }

    private void RemoveShared(){
        shardpref.remove("worker_id");
        shardpref.remove("contract_id");
        shardpref.remove("contract_email");
    }
}
