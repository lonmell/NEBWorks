package com.krafte.nebworks.ui;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FCMUpdateInterface;
import com.krafte.nebworks.databinding.ActivityPushBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PushActivity extends AppCompatActivity {
    private ActivityPushBinding binding;

    private static final String TAG = "PushManagementActivity";
    Context mContext;
    private NotificationManager notificationManager;
    Handler mHandler;

    //Sharedf
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();
    boolean channelId1 = true;
    boolean channelId2 = true;
    boolean channelId3 = true;
    boolean channelId4 = true;
    boolean channelId5 = true;

    Dlog dlog = new Dlog();
    String USER_INFO_ID = "";
    String place_owner_id = "";

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
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();
        //Singleton Area
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
        place_owner_id  = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());

        //shardpref Area
        type = shardpref.getString("type", "");
        channelId1 = shardpref.getBoolean("channelId1", false);
        channelId2 = shardpref.getBoolean("channelId2", false);
        channelId3 = shardpref.getBoolean("channelId3", false);
        channelId4 = shardpref.getBoolean("channelId4", false);
        token = shardpref.getString("token", "");


        getPushBoolean();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    private void setBtnEvent() {
        binding.saveBtn.setOnClickListener(v -> {
            dlog.i("channel1 : " + binding.activeOnBt01.isChecked());
            dlog.i("channel2 : " + binding.activeOnBt02.isChecked());
            dlog.i("channel3 : " + binding.activeOnBt03.isChecked());
            dlog.i("channel4 : " + binding.activeOnBt04.isChecked());
            dlog.i("channel5 : " + binding.activeOnBt05.isChecked());
            setUserTokenDB(token, binding.activeOnBt01.isChecked(), binding.activeOnBt02.isChecked(), binding.activeOnBt03.isChecked(), binding.activeOnBt04.isChecked(), binding.activeOnBt05.isChecked());
        });
        binding.backBtn.setOnClickListener(v -> {
//            pm.MoreBack(mContext);
            finish();
        });

        binding.activeOnArea01.setOnClickListener(v -> {
            dlog.i("Change channel1 check");
            if (binding.activeOnBt01.isChecked()) {
                binding.activeOnBt01.setChecked(false);
                channelId1 = false;
            } else {
                binding.activeOnBt01.setChecked(true);
                channelId1 = true;
            }
        });

        binding.activeOnArea02.setOnClickListener(v -> {
            dlog.i("Change channel2 check");
            if (binding.activeOnBt02.isChecked()) {
                binding.activeOnBt02.setChecked(false);
                channelId2 = false;
            } else {
                binding.activeOnBt02.setChecked(true);
                channelId2 = true;
            }
        });

        binding.activeOnArea03.setOnClickListener(v -> {
            if (binding.activeOnBt03.isChecked()) {
                binding.activeOnBt03.setChecked(false);
                channelId3 = false;
            } else {
                binding.activeOnBt03.setChecked(true);
                channelId3 = true;
            }
        });

        binding.activeOnArea04.setOnClickListener(v -> {
            if (binding.activeOnBt04.isChecked()) {
                binding.activeOnBt04.setChecked(false);
                channelId4 = false;
            } else {
                binding.activeOnBt04.setChecked(true);
                channelId4 = true;
            }
        });

        binding.activeOnArea05.setOnClickListener(v -> {
            if (binding.activeOnBt05.isChecked()) {
                binding.activeOnBt05.setChecked(false);
                channelId5 = false;
            } else {
                binding.activeOnBt05.setChecked(true);
                channelId5 = true;
            }
        });
    }


    String id = "";
    String user_id = "";
    String type = "";
    String token = "";
    String channel1 = "";
    String channel2 = "";
    String channel3 = "";
    String channel4 = "";
    String channel5 = "";

    RetrofitConnect rc = new RetrofitConnect();
    public void getPushBoolean() {
        type = USER_INFO_ID.equals(PlaceCheckData.getInstance().getPlace_owner_id())?"0":"1";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    Log.i(TAG, "user_id : " + Response.getJSONObject(0).getString("user_id"));
                    Log.i(TAG, "token : " + Response.getJSONObject(0).getString("token"));
                    id = Response.getJSONObject(0).getString("id");
                    user_id = Response.getJSONObject(0).getString("user_id");
                    type = Response.getJSONObject(0).getString("type");
                    token = Response.getJSONObject(0).getString("token");
                    channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                    channelId2 = Response.getJSONObject(0).getString("channel2").equals("1");
                    channelId3 = Response.getJSONObject(0).getString("channel3").equals("1");
                    channelId4 = Response.getJSONObject(0).getString("channel4").equals("1");
                    channelId5 = Response.getJSONObject(0).getString("channel5").equals("1");

                    binding.activeOnBt01.setChecked(channelId1);
                    binding.activeOnBt02.setChecked(channelId2);
                    binding.activeOnBt03.setChecked(channelId3);
                    binding.activeOnBt04.setChecked(channelId4);
                    binding.activeOnBt05.setChecked(channelId5);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    public void setUserTokenDB(String token, boolean channel1, boolean channel2, boolean channel3, boolean channel4, boolean channel5) {
        dlog.i("------setUserTokenDB------");
        dlog.i("id : " + id);
        dlog.i("channelId1 : " + String.valueOf(channel1));
        dlog.i("channelId2 : " + String.valueOf(channel2));
        dlog.i("channelId3 : " + String.valueOf(channel3));
        dlog.i("channelId4 : " + String.valueOf(channel4));
        dlog.i("channelId5 : " + String.valueOf(channel5));
        dlog.i("------setUserTokenDB------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMUpdateInterface api = retrofit.create(FCMUpdateInterface.class);
        Call<String> call = api.getData(id, token, channel1?"1":"0", channel2?"1":"0", channel3?"1":"0", channel4?"1":"0", channel5?"1":"0");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                if (jsonResponse.replace("\"", "").equals("success")) {
                    Log.i(TAG, "channelId1 : " + channel1);
                    Log.i(TAG, "channelId2 : " + channel2);
                    Log.i(TAG, "channelId3 : " + channel3);
                    Log.i(TAG, "channelId4 : " + channel4);
                    Log.i(TAG, "channelId5 : " + channel5);

                    shardpref.putBoolean("channelId1", channel1);
                    shardpref.putBoolean("channelId2", channel2);
                    shardpref.putBoolean("channelId3", channel3);
                    shardpref.putBoolean("channelId4", channel4);
                    shardpref.putBoolean("channelId5", channel5);
//                    pm.MoreBack(mContext);
                    finish();
                } else {
                    Toast.makeText(mContext, "네트워크가 정상적이지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }
}
