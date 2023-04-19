package com.krafte.nebworks.ui.place;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.databinding.ActivityPlaceaddCompletionBinding;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class PlaceAddCompletion extends AppCompatActivity {
    private ActivityPlaceaddCompletionBinding binding;
    Context mContext;

    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    String USER_INFO_AUTH = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityPlaceaddCompletionBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
            setBtnEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setBtnEvent() {
        binding.placeListGo.setOnClickListener(v -> {
            pm.PlaceList(mContext);
        });

        binding.goThisPlace.setOnClickListener(v -> {
            if(USER_INFO_AUTH.equals("0")){
                pm.Main(mContext);
            }else{
                pm.Main2(mContext);
            }
        });
    }














    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
