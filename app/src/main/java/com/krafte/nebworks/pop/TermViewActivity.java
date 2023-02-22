package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;

import com.krafte.nebworks.databinding.ActivityTermviewBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TermViewActivity extends Activity {
    private ActivityTermviewBinding binding;
    private final static String TAG = "TermViewActivity";
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    Context mContext;
    Intent intent;
    String data = "";
    String flag = "0";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityTermviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //데이터 가져오기
        intent = getIntent();
        data   = intent.getStringExtra("data");
        flag   = intent.getStringExtra("flag");
        setBtnEvent();

        if(flag.equals("1")){
            binding.title.setText("휴대폰 본인 인증 서비스\n이용약관동의");
            loadPolicy("verification01.txt");
        }else if(flag.equals("2")){
            binding.title.setText("휴대폰 통신사 이용약관 동의");
            if(data.contains("SKT")){
                loadPolicy("verification02_skt.txt");
            }
            else if(data.contains("KT")){
                loadPolicy("verification02_kt.txt");
            }
            else if(data.contains("LGU+")){
                loadPolicy("verification02_lgu.txt");
            }
            else if(data.contains("IDEN")){
                loadPolicy("verification02_iden.txt");
            }
        }else if(flag.equals("3")){
            binding.title.setText("개인정보 제공 및 이용동의");
            loadPolicy("verification03.txt");
        }else if(flag.equals("4")){
            binding.title.setText("고유식별정보 처리");
            loadPolicy("verification03.txt");
        }



    }


    private void setBtnEvent(){
        binding.close.setOnClickListener(v -> {
            if(flag.equals("1")){
                shardpref.putString("termaccept","accept01");
            } else if(flag.equals("2")){
                shardpref.putString("termaccept","accept02");
            } else if(flag.equals("3")){
                shardpref.putString("termaccept","accept03");
            }
            finish();
        });
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(flag.equals("1")){
            shardpref.putString("termaccept","refusal01");
        } else if(flag.equals("2")){
            shardpref.putString("termaccept","refusal02");
        } else if(flag.equals("3")){
            shardpref.putString("termaccept","refusal03");
        }
    }

    private void loadPolicy(String title) {
        try {
            String policy = readFromAssets(title);
            binding.termViewTv.setText(Html.fromHtml(policy));

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