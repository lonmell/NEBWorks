package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityContractAdd02Binding;
import com.krafte.nebworks.pop.TermViewActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class AddContractPage02 extends AppCompatActivity {
    private ActivityContractAdd02Binding binding;
    private final static String TAG = "AddContractPage01";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    boolean allcheck = false;
    boolean select01tf = false;
    boolean select02tf = false;
    boolean select03tf = false;
    boolean select04tf = false;
    boolean select05tf = false;

    Boolean CertiSuccessTF = false;

    //사용자 정보 체크
    TelephonyManager telephonyManager; //--통신사 체크
    String TelecomName = "";

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
        telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        TelecomName = telephonyManager.getNetworkOperatorName();
        dlog.i("통신사 : " + TelecomName);

        setBtnEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        String termaccept = shardpref.getString("termaccept","");
        if(!termaccept.isEmpty()){
            dlog.i("termaccept : " + termaccept);
            if(termaccept.equals("accept01")){
                select01tf = true;
                binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
                }
            }else if(termaccept.equals("accept02")){
                select02tf = true;
                binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
                }
            }else if(termaccept.equals("accept03")){
                select03tf = true;
                binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
                }

                select04tf = true;
                binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
                }
            }
        }
    }

    private void setBtnEvent(){
        binding.allAccept.setOnClickListener(v -> {
            if(!select05tf){
                select05tf = true;
                select01tf = true;
                select02tf = true;
                select03tf = true;
                select04tf = true;
                binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
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
        binding.acceptBox01.setOnClickListener(v -> {
            if(!select01tf){
                select01tf = true;
                binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
                }
            }else{
                select01tf = false;
                select05tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check01.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });
        binding.acceptBox02.setOnClickListener(v -> {
            if(!select02tf){
                select02tf = true;
                binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
                }
            }else{
                select02tf = false;
                select05tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check02.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });
        binding.acceptBox03.setOnClickListener(v -> {
            if(!select03tf){
                select03tf = true;
                binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
                }
            }else{
                select03tf = false;
                select05tf = false;
                binding.allCheck.setBackgroundResource(R.drawable.ic_empty_round);
                binding.check03.setBackgroundResource(R.drawable.ic_empty_round);
            }
        });
        binding.acceptBox04.setOnClickListener(v -> {
            if(!select04tf){
                select04tf = true;
                binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                if(select01tf && select02tf && select03tf && select04tf){
                    select05tf = true;
                    binding.allCheck.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check01.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check02.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check03.setBackgroundResource(R.drawable.ic_full_round_check);
                    binding.check04.setBackgroundResource(R.drawable.ic_full_round_check);
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


        binding.accept01Tv.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, TermViewActivity.class);
            intent.putExtra("data", TelecomName);
            intent.putExtra("flag", "1");
            startActivity(intent);
        });
        binding.accept02Tv.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, TermViewActivity.class);
            intent.putExtra("data", TelecomName);
            intent.putExtra("flag", "2");
            startActivity(intent);
        });
        binding.accept03Tv.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, TermViewActivity.class);
            intent.putExtra("data", TelecomName);
            intent.putExtra("flag", "3");
            startActivity(intent);
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

