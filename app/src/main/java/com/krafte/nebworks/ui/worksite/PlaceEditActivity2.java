package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WifiAdapter;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.PlaceEditInterface;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.databinding.ActivityAddplace2Binding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PlaceEditActivity2 extends AppCompatActivity {
    private ActivityAddplace2Binding binding;
    Context mContext;

    //Other
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    WifiManager wifiManager;
    WifiAdapter mAdapter;

    ArrayList<PlaceListData.PlaceListData_list> mList;

    String place_id = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_name = "";
    String place_owner_id = "";

    String name = "";
    String registr_num = "";
    String store_kind = "";
    String address = "";
    String latitude = "";
    String longitude = "";
    String pay_day = "";
    String test_period = "";
    String vacation_select = "";
    String insurance = "";
    String start_time = "";
    String end_time = "";
    String save_kind = "";
    String img_path = "";
    String start_date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityAddplace2Binding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);
//
        try {
            binding.noData.setVisibility(View.VISIBLE);
            place_id = shardpref.getString("place_id", "-99");
            place_name = shardpref.getString("place_name", "-99");
            place_owner_id = shardpref.getString("place_owner_id", "-99");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "-99");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-99");
            getPlaceContents();
            wifiScan();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }



    private void getPlaceContents() {
        dlog.i("-----getPlaceContents-----");
        dlog.i("place_id : " + place_id);
        dlog.i("owner_id : " + USER_INFO_ID);
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("-----getPlaceContents-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID,USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + response.body().length());
                            dlog.i("GetPlaceList jsonResponse : " + response.body());
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(response.body());
                                name = Response.getJSONObject(0).getString("name");
                                registr_num = Response.getJSONObject(0).getString("registr_num");
                                store_kind = Response.getJSONObject(0).getString("store_kind");
                                address = Response.getJSONObject(0).getString("address");
                                latitude = Response.getJSONObject(0).getString("latitude");
                                longitude = Response.getJSONObject(0).getString("longitude");
                                pay_day = Response.getJSONObject(0).getString("pay_day");
                                test_period = Response.getJSONObject(0).getString("test_period");
                                vacation_select = Response.getJSONObject(0).getString("vacation_select");
                                insurance = Response.getJSONObject(0).getString("insurance");
                                start_time = Response.getJSONObject(0).getString("start_time");
                                end_time = Response.getJSONObject(0).getString("end_time");
                                save_kind = Response.getJSONObject(0).getString("save_kind");
                                img_path = Response.getJSONObject(0).getString("img_path").equals("null")?"":Response.getJSONObject(0).getString("img_path");
                                start_date = Response.getJSONObject(0).getString("start_date");
                                SSIDName = Response.getJSONObject(0).getString("wifi_name");

                                binding.updateWifi01.setVisibility(View.VISIBLE);
                                binding.updateWifi02.setVisibility(View.VISIBLE);
                                binding.wifiName.setText(SSIDName);

                                binding.save2btn.setOnClickListener(v -> {
                                    UpdatePlace(0);
                                });
                                binding.addPlaceBtn.setOnClickListener(v -> {
                                    UpdatePlace(1);
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    public void wifiScan() {
        wifiManager = (WifiManager)
                mContext.getSystemService(WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
    }

    String SSIDName = "";

    private void scanSuccess() {
//        dlog.i("scanSuccess : " + wifiManager.getScanResults());
        // 스캔 성공시 저장된 list를 recycler view를 통해 보여줌
        List<ScanResult> results = wifiManager.getScanResults();
        List<String> SSID = new ArrayList<>();
        StringBuffer st = new StringBuffer();
        for (ScanResult r : results) {
            dlog.i("wifi : " + r);
            if (!r.SSID.isEmpty()) {
                SSID.add(r.SSID);
            }
        }
        dlog.i("ScanResult wifi : " + SSID);
        if (SSID.size() == 0) {
            binding.noData.setVisibility(View.VISIBLE);
            binding.noDataTxt.setText("검색된 Wifi가 없습니다.");
        } else {
            binding.noData.setVisibility(View.GONE);
            mAdapter = new WifiAdapter(mContext, SSID);
            binding.wifiList.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    mAdapter.notifyDataSetChanged();
                    dlog.i("SELECT Wifi Name : " + SSID.get(position));
                    SSIDName = SSID.get(position);
                }
            });
            mAdapter.notifyDataSetChanged();
        }

    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        Log.d("wifi", "scanFailure");
        Toast_Nomal("wifi scan에 실패하였습니다.");
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        // potentially use older scan results ...
    }

    public void UpdatePlace(int i) {
        //i = 0:임시저장 / 1:저장
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceEditInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceEditInterface api = retrofit.create(PlaceEditInterface.class);
        Call<String> call = api.getData(place_id,name,registr_num,store_kind,address
                ,String.valueOf(latitude),String.valueOf(longitude),pay_day,test_period,vacation_select
                ,insurance,start_time,end_time,img_path,String.valueOf(i),start_date,SSIDName);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("UpdatePlace jsonResponse length : " + response.body().length());
                            dlog.i("UpdatePlace jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]") && response.body().replace("\"", "").equals("success")) {
                                    shardpref.putString("place_id", place_id);
                                    shardpref.putString("place_owner_id", USER_INFO_ID);
                                    if(i == 0){
                                        Toast_Nomal("임시저장 완료되었습니다.");
                                    }else{
                                        Toast_Nomal("매장정보가 업데이트 되었습니다.");
                                    }
                                    pm.workCompletion(mContext);
                                }else{
                                    Toast_Nomal("추가 매장을 생성하지 못했습니다. Error : " + response.body());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
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
