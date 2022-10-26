package com.krafte.kogas.ui.BottomNavi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.kogas.R;
import com.krafte.kogas.databinding.ActivityMoreBinding;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

public class MoreActivity extends AppCompatActivity {

    private ActivityMoreBinding binding;
    Context mContext;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    ImageView more_icon;
    TextView more_tv;


    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String place_management_office = "";
    String place_address = "";
    String place_latitude = "";
    String place_longitude = "";
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMoreBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();

        //UI 데이터 세팅
        try {
            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            place_owner_name = shardpref.getString("place_owner_name", "0");
            place_management_office = shardpref.getString("place_management_office", "0");
            place_address = shardpref.getString("place_address", "0");
            place_latitude = shardpref.getString("place_latitude", "0");
            place_longitude = shardpref.getString("place_longitude", "0");
            place_start_time = shardpref.getString("place_start_time", "0");
            place_end_time = shardpref.getString("place_end_time", "0");
            place_img_path = shardpref.getString("place_img_path", "0");
            place_start_date = shardpref.getString("place_start_date", "0");
            place_created_at = shardpref.getString("place_created_at", "0");

            more_icon = findViewById(R.id.more_icon);
            more_tv = findViewById(R.id.more_tv);

            more_icon.setBackgroundResource(R.drawable.more_icon_on_resize);
            more_tv.setTextColor(Color.parseColor("#6395EC"));
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void btnOnclick(View view) {
        if (view.getId() == R.id.out_store) {
            pm.PlaceListBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            pm.MainBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation02) {
            pm.PlaceWorkGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation03) {
            pm.CalenderGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation04) {
            pm.WorkStateListGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation05) {
            dlog.i("More Page");
        }
    }

    public void setBtnEvent() {
        binding.outStore.setOnClickListener(v -> {
            pm.PlaceListBack(mContext);
        });

        binding.settingList02Txt.setOnClickListener(v -> {
            shardpref.putString("retrun_page","MoreActivity");
            pm.ProfileEditGo(mContext);
        });
        binding.settingList03Txt.setOnClickListener(v -> {
            pm.Push(mContext);
        });

        binding.settingList05Txt.setOnClickListener(v -> {
            pm.MyPlsceGo(mContext);
        });

        binding.settingList06Txt.setOnClickListener(v -> {
            pm.UserDel(mContext);
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        pm.PlaceListBack(mContext);
    }
}
