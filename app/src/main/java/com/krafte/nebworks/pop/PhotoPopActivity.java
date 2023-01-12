package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityPhotopopBinding;
import com.krafte.nebworks.util.PreferenceHelper;

public class PhotoPopActivity extends Activity {
    private ActivityPhotopopBinding binding;
    private static final String TAG = "PhotoPopActivity";
    Context mContext;

    String title            = "";
    String data             = "";
    Intent intent;
    PreferenceHelper shardpref;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_photopop);
        binding = ActivityPhotopopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = this;
        shardpref = new PreferenceHelper(mContext);

        //데이터 가져오기
        intent = getIntent();
        data             = intent.getStringExtra("data");
        Log.i(TAG,"data : " + data);

        Glide.with(mContext).load(data)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.thumnailIn);

        binding.closeBtn.setOnClickListener(v -> {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
        });

    }
}
