package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityContractAdd02Binding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

public class AddContractPage02 extends AppCompatActivity {
    private ActivityContractAdd02Binding binding;
    private final static String TAG = "AddContractPage01";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    boolean select01tf = false;
    boolean select02tf = false;
    boolean select03tf = false;
    boolean select04tf = false;
    boolean select05tf = false;

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractAdd02Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        setBtnEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setBtnEvent(){
        binding.allAccept.setOnClickListener(v -> {
            if(!select05tf){
                select05tf = true;
                select01tf = true;
                select02tf = true;
                select03tf = true;
                select04tf = true;
                binding.allCheck.setBackgroundResource(R.drawable.resize_service_on);
                binding.check01.setBackgroundResource(R.drawable.resize_service_on);
                binding.check02.setBackgroundResource(R.drawable.resize_service_on);
                binding.check03.setBackgroundResource(R.drawable.resize_service_on);
                binding.check04.setBackgroundResource(R.drawable.resize_service_on);
            }else{
                select05tf = false;
                select01tf = false;
                select02tf = false;
                select03tf = false;
                select04tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check01.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check02.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check03.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check04.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });
        binding.accept01.setOnClickListener(v -> {
            if(!select01tf){
                select01tf = true;
                binding.check01.setBackgroundResource(R.drawable.resize_service_on);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check01.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check02.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check03.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check04.setBackgroundResource(R.drawable.resize_service_on);
                }
            }else{
                select01tf = false;
                select05tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check01.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });
        binding.accept02.setOnClickListener(v -> {
            if(!select02tf){
                select02tf = true;
                binding.check02.setBackgroundResource(R.drawable.resize_service_on);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check01.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check02.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check03.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check04.setBackgroundResource(R.drawable.resize_service_on);
                }
            }else{
                select02tf = false;
                select05tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check02.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });
        binding.accept03.setOnClickListener(v -> {
            if(!select03tf){
                select03tf = true;
                binding.check03.setBackgroundResource(R.drawable.resize_service_on);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check01.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check02.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check03.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check04.setBackgroundResource(R.drawable.resize_service_on);
                }
            }else{
                select03tf = false;
                select05tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check03.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });
        binding.accept04.setOnClickListener(v -> {
            if(!select04tf){
                select04tf = true;
                binding.check03.setBackgroundResource(R.drawable.resize_service_on);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check01.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check02.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check03.setBackgroundResource(R.drawable.resize_service_on);
                    binding.check04.setBackgroundResource(R.drawable.resize_service_on);
                }
            }else{
                select04tf = false;
                select05tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check04.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });

        binding.next.setOnClickListener(v -> {
            if(select05tf){
                pm.AddContractPage03(mContext);
            }else{
                Toast_Nomal("동의하지 않은 필수항목이 있습니다.");
            }
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

    public void Toast_Nomal(String message){
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
}

