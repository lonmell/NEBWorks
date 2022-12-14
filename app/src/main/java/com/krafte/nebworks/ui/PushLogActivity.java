package com.krafte.nebworks.ui;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.databinding.ActivityPushBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class PushLogActivity  extends AppCompatActivity {
    private ActivityPushBinding binding;

    private static final String TAG = "PushLogActivity";
    Context mContext;
    Handler mHandler;

    //Sharedf
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();


    Dlog dlog = new Dlog();
    String USER_INFO_ID = "";
    String place_id = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "LongLogTag", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pushmanagement);
        binding = ActivityPushBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mContext = this;
        dlog.DlogContext(mContext);

        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        place_id = shardpref.getString("place_id", "");


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    private void setBtnEvent() {

    }


}

