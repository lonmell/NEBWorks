package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.net.ParseException;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.InOutInsertInterface;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/*
* 2023-03-03 방창배 수정 / 사용자 현재 위치 와 매장 거리로 출퇴근 제한 삭제 /
* */
public class InoutPopActivity extends BottomSheetDialogFragment {
    private static final String TAG = "InoutPopActivity";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();

    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";

    String time = "";
    String state = "";
    String store_name = "";
    String inout_tv = "";
    String inout_tv2 = "";
    String place_end_time = "";
    String mem_name = "";
    String kind = "";

    String place_id = "";
    String place_owner_id = "";
    String place_name = "";
    Double place_latitude = 0.0;
    Double place_longitude = 0.0;
    String place_pay_day = "";
    String place_test_period = "";
    String place_vacation_select = "";
    String place_insurance = "";
    String place_wifi_name = "";
    String place_iomethod = "";

    //xml 
    ImageView inout_icon;
    TextView Setinout_tv, Setinout_tv2, in_time, close_btn, inout_insert, inout_settv;
    LinearLayout time_area;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_inout_pop, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        try {
            mContext = inflater.getContext();
            dlog.DlogContext(mContext);
            fragmentManager = getParentFragmentManager();

            shardpref = new PreferenceHelper(mContext);

            place_id = shardpref.getString("place_id", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            mem_name = shardpref.getString("USER_INFO_NAME", "");

            //데이터 가져오기
            place_end_time = shardpref.getString("place_end_time", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            time = shardpref.getString("time", "");
            state = shardpref.getString("state", "");
            store_name = shardpref.getString("store_name", "");
            inout_tv = shardpref.getString("inout_tv", "");
            inout_tv2 = shardpref.getString("inout_tv2", "");
            kind = shardpref.getString("kind", "");
            place_end_time = place_end_time.substring(0, 5);
            place_wifi_name = shardpref.getString("place_wifi_name", "");
            place_latitude = Double.parseDouble(shardpref.getString("place_latitude", ""));
            place_longitude = Double.parseDouble(shardpref.getString("place_longitude", ""));
            place_owner_id = shardpref.getString("place_owner_id", "");
            place_name = shardpref.getString("place_name", "");

            inout_icon = view.findViewById(R.id.inout_icon);
            Setinout_tv = view.findViewById(R.id.inout_tv);
            Setinout_tv2 = view.findViewById(R.id.inout_tv2);
            in_time = view.findViewById(R.id.in_time);
            close_btn = view.findViewById(R.id.close_btn);
            inout_insert = view.findViewById(R.id.inout_insert);
            time_area = view.findViewById(R.id.time_area);
            inout_settv = view.findViewById(R.id.inout_settv);

            setBtnEvent();

            //state
            /*
             * 1 - 출근처리
             * 2 - 출근처리 불가
             * 3 - 퇴근처리 하시겠습니까? - 등록된 퇴근시간 아닐때
             * 4 - 퇴근처리
             * * */
            switch (state) {
                case "1":
                case "3":
                case "4":
                    inout_insert.setText("확인");
                    time_area.setVisibility(View.VISIBLE);
                    inout_icon.setBackgroundResource(R.drawable.ic_inout_ok);
                    break;
                case "2":
                    inout_insert.setText("재시도");
                    Setinout_tv2.setVisibility(View.VISIBLE);
                    inout_icon.setBackgroundResource(R.drawable.ic_in_enable);
                    break;
//                case "3":
//                    inout_insert.setText("확인");
//                    Setinout_tv2.setVisibility(View.VISIBLE);
//                    inout_icon.setBackgroundResource(R.drawable.ic_out_ok);
//                    break;
            }
            Setinout_tv.setText(inout_tv);
            Setinout_tv2.setText(inout_tv2);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        retry();
    }


    public interface OnClickListener {
        void onClick(View v, String kind) ;
    }
    private OnClickListener mListener = null ;
    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("change_place_id");
        shardpref.remove("kind");
    }

    double latitude = 0;
    double longitude = 0;

    private void setBtnEvent() {
        close_btn.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(v,kind);
            }
            dismiss();
        });
        inout_insert.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(v,kind);
            }
            if (!state.equals("2")) {
                if (state.equals("4")) {
                    dlog.i("setBtnEvent kind : " + kind);
                    if (kind.equals("0")) {
                        dlog.i("ssid tf : " + getMySSID.equals(place_wifi_name));
                        if (place_iomethod.equals("y")) {
                            if (getMySSID.equals(place_wifi_name)) {
                                InOutInsert();
                            } else {
                                retry();
                                Toast_Nomal("매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요");
                            }
                        }else if(place_iomethod.equals("n")){
                            InOutInsert();
                        }
                    } else {
                        dlog.i("ssid tf : " + getMySSID.equals(place_wifi_name));
                        if (place_iomethod.equals("y")) {
                            if (getMySSID.equals(place_wifi_name)) {
                                InOutInsert();
                            } else {
                                retry();
                                Toast_Nomal("매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요");
                            }
                        }else if(place_iomethod.equals("n")){
                            InOutInsert();
                        }
                    }
                } else {
                    InOutInsert();
                }
            } else {
                dlog.i("setBtnEvent kind : " + kind);
                retry();
            }
        });
    }

    public String getNetworkName(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getSSID();
    }

    private void retry() {
        getPlaceData();
//        MoveMyLocation();
//        dlog.i("location_cnt : " + location_cnt);
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");
//        latitude = gpsTracker.getLatitude();
//        longitude = gpsTracker.getLongitude();
//        getDistance = Integer.parseInt(String.valueOf(Math.round(getDistance(place_latitude, place_longitude, latitude, longitude))));
//        dlog.i("location_cnt : " + location_cnt);
        dlog.i("GET_TIME : " + simpleDate.format(mDate));
        dlog.i("retry kind : " + kind);
        getMySSID = getNetworkName(mContext).replace("\"", "");
        if (getMySSID.equals("<unknown ssid>")) {
            getMySSID = "";
        }
        dlog.i("retry place_latitude : " + place_latitude);
        dlog.i("retry place_longitude : " + place_longitude);
        dlog.i("retry latitude : " + latitude);
        dlog.i("retry longitude : " + longitude);
//        dlog.i("retry getDistance : " + getDistance);
        dlog.i("retry getMySSID : " + getMySSID);
        dlog.i("retry place_wifi_name : " + place_wifi_name);
        dlog.i("retry ssid tf : " + getMySSID.equals(place_wifi_name));

        if (kind.equals("0")) {
            //가게 등록한 와이파이와 현재 디바이스에서 접속중인 와이파이 비교
            if(place_iomethod.equals("y")){
                if (getMySSID.equals(place_wifi_name)) {
                    state = "1";
                    inout_insert.setText("확인");
                    time_area.setVisibility(View.VISIBLE);
                    Setinout_tv2.setVisibility(View.GONE);
                    inout_icon.setBackgroundResource(R.drawable.ic_inout_ok);
                    inout_tv = "출근처리";
                    inout_tv2 = "";
                    inout_settv.setText("출근시간");
                    String dcin_time = dc.GET_TIME.substring(11);
                    in_time.setText(dcin_time);
                } else {
                    state = "2";
                    kind = "0";
                    inout_insert.setText("재시도");
                    Setinout_tv2.setVisibility(View.VISIBLE);
                    time_area.setVisibility(View.GONE);
                    inout_icon.setBackgroundResource(R.drawable.ic_in_enable);
                    inout_tv = "출근처리 불가";
                    inout_tv2 = "매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요";
                }
            }else if(place_iomethod.equals("n")){
                state = "1";
                inout_insert.setText("확인");
                time_area.setVisibility(View.VISIBLE);
                Setinout_tv2.setVisibility(View.GONE);
                inout_icon.setBackgroundResource(R.drawable.ic_inout_ok);
                inout_tv = "출근처리";
                inout_tv2 = "";
                inout_settv.setText("출근시간");
                String dcin_time = dc.GET_TIME.substring(11);
                in_time.setText(dcin_time);
            }
        } else {
            if(place_iomethod.equals("y")){
                inout_settv.setText("퇴근시간");
                String dcin_time = dc.GET_TIME.substring(11);
                in_time.setText(dcin_time);
                io_state = "퇴근처리";
                dlog.i("compareDate2 :" + compareDate2());
                //가게 등록한 와이파이와 현재 디바이스에서 접속중인 와이파이 비교
                if (getMySSID.equals(place_wifi_name)) {
                    if (compareDate2()) {
                        state = "4";
                        inout_insert.setText("확인");
                        Setinout_tv2.setVisibility(View.VISIBLE);
                        inout_icon.setBackgroundResource(R.drawable.ic_out_ok);
                        inout_tv = "퇴근처리";
                        inout_tv2 = "";
                    } else {
                        state = "3";
                        inout_insert.setText("확인");
                        Setinout_tv2.setVisibility(View.VISIBLE);
                        inout_icon.setBackgroundResource(R.drawable.ic_out_ok);
                        inout_tv = "퇴근처리";
                        inout_tv2 = "등록된 퇴근시간이 아닙니다.";
                    }
                } else {
                    state = "2";
                    kind = "1";
                    inout_insert.setText("재시도");
                    Setinout_tv2.setVisibility(View.VISIBLE);
                    inout_icon.setBackgroundResource(R.drawable.ic_in_enable);
                    inout_tv = "퇴근처리 불가";
                    inout_tv2 = "매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요";
                }
            }else if(place_iomethod.equals("n")){
                io_state = "퇴근처리";
                inout_settv.setText("퇴근시간");
                String dcin_time = dc.GET_TIME.substring(11);
                in_time.setText(dcin_time);
                if (compareDate2()) {
                    state = "4";
                    inout_insert.setText("확인");
                    Setinout_tv2.setVisibility(View.VISIBLE);
                    inout_icon.setBackgroundResource(R.drawable.ic_out_ok);
                    inout_tv = "퇴근처리";
                    inout_tv2 = "";
                } else {
                    state = "3";
                    inout_insert.setText("확인");
                    Setinout_tv2.setVisibility(View.VISIBLE);
                    inout_icon.setBackgroundResource(R.drawable.ic_out_ok);
                    inout_tv = "퇴근처리";
                    inout_tv2 = "등록된 퇴근시간이 아닙니다.";
                }
            }
        }
        Setinout_tv.setText(inout_tv);
        Setinout_tv2.setText(inout_tv2);
        in_time.setText(time);
    }

    private void ClosePop() {
        //데이터 전달하기
        shardpref.remove("change_place_id");
        shardpref.remove("mem_name");
        shardpref.remove("time");
        shardpref.remove("state");
        shardpref.remove("store_name");
        shardpref.remove("inout_tv");
        shardpref.remove("inout_tv2");
        shardpref.remove("jongeob");
        shardpref.putInt("SELECT_POSITION", 0);
        shardpref.putInt("SELECT_POSITION_sub", 0);
//        pm.Main2(mContext);
        dismiss();
    }


    Handler handler = new Handler();
    long now = System.currentTimeMillis();
    Date mDate = new Date(now);

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate_time = new java.text.SimpleDateFormat("HH:mm:ss");

    String GET_TIME = simpleDate_time.format(mDate);
    String title = "";
    String io_state = "";
    String input_date = "";
    String getMySSID = "";

    String jongeob = "";
    Calendar cal;
    String today = "";
    String format = "HH:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(format);


    public boolean compareDate2() throws ParseException {
        today = dc.GET_TIME;
        boolean returntf = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date1 = sdf.parse(today);
            Date date2 = sdf.parse(jongeob.substring(3));
//            System.out.println(sdf.format(date1));
//            System.out.println(sdf.format(date2));

            returntf = date1.after(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returntf;
    }

    RetrofitConnect rc = new RetrofitConnect();

    private void InOutInsert() {
        dismiss();
        String change_place_id = "";
        change_place_id = shardpref.getString("change_place_id", "");
        if (!change_place_id.isEmpty()) {
            place_id = change_place_id;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InOutInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        InOutInsertInterface api = retrofit.create(InOutInsertInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, kind);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        if (jsonResponse.replace("[", "").replace("]", "").replace("\"", "").length() == 0) {
                            //최초 출근

                        } else if (jsonResponse.replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && response.body() != null) {
                                dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                                dlog.i("LoginCheck jsonResponse : " + response.body());
                                try {
                                    if (jsonResponse.replace("[", "").replace("]", "").replace("\"", "").equals("success")) {
                                        /*
                                         * 1 - 출근처리
                                         * 2 - 출근처리 불가
                                         * 3 - 퇴근처리 하시겠습니까? - 등록된 퇴근시간 아닐때
                                         * 4 - 퇴근처리
                                         * * */
                                        if (kind.equals("0")) {
                                            String input_date = dc.GET_YEAR + "." + dc.GET_MONTH + "." + dc.GET_DAY;
                                            String in_time = dc.GET_TIME.substring(11);
                                            shardpref.putString("input_date", input_date);
                                            shardpref.putString("in_time", in_time);
                                            message = "[" + place_name + "] 매장에서 [" + mem_name + "] 님의 출근처리가 완료되었습니다.";
                                        } else {
                                            inout_settv.setText("퇴근시간");
                                            String input_date = dc.GET_YEAR + "." + dc.GET_MONTH + "." + dc.GET_DAY;
                                            String in_time = dc.GET_TIME.substring(11);
                                            shardpref.remove("input_date");
                                            shardpref.putString("input_date", input_date);
                                            shardpref.putString("in_time", in_time);
                                            message = "[" + place_name + "] 매장에서 [" + mem_name + "] 님의 퇴근처리가 완료되었습니다.";
                                        }
                                        getUserToken("0", message);
                                        AddPush("출퇴근 알림", message);
                                        ClosePop();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    String message = "";

    //근로자 > 점주 ( 초대수락 FCM )
    public void getUserToken(String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + place_owner_id);
        dlog.i("type : " + type);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(place_owner_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "1", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    public void AddPush(String title, String content) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, USER_INFO_ID, place_owner_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    //근로자 > 점주 ( 초대수락 FCM )
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "status1";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            activity.runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getPlaceData() {
        dlog.i("PlaceCheck place_id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceThisDataInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceThisDataInterface api = retrofit.create(PlaceThisDataInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    place_name = Response.getJSONObject(0).getString("name");
                                    place_owner_id = Response.getJSONObject(0).getString("owner_id");
                                    place_latitude = Double.parseDouble(Response.getJSONObject(0).getString("latitude"));
                                    place_longitude = Double.parseDouble(Response.getJSONObject(0).getString("longitude"));
                                    place_pay_day = Response.getJSONObject(0).getString("pay_day");
                                    place_test_period = Response.getJSONObject(0).getString("test_period");
                                    place_vacation_select = Response.getJSONObject(0).getString("vacation_select");
                                    place_insurance = Response.getJSONObject(0).getString("insurance");
                                    place_wifi_name = Response.getJSONObject(0).getString("wifi_name");
                                    place_iomethod = Response.getJSONObject(0).getString("io_method");
                                    dlog.i("place_iomethod : " + place_iomethod);
                                }
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

    //역 지오코딩 ( 위,경도 >> 주소 ) START
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    public void reverseCoding(double latitude, double longitube) { // 위도 경도 넣어서 역지오코딩 주소값 뽑아낸다
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> gList = null;
        String Setaddress = "";
        dlog.i("(reverseCoding)latitude,longitube : " + latitude + "," + longitube);
        try {
            gList = geocoder.getFromLocation(latitude, longitube, 6);
        } catch (IOException e) {
            e.printStackTrace();
            dlog.e("setMaskLocation() - 서버에서 주소변환시 에러발생");
            // Fragment1 으로 강제이동 시키기
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Toast.makeText(mContext, " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();
            } else {
                Address address = gList.get(0);
                Address address1 = gList.get(1);
                Address address2 = gList.get(2);
                Address address3 = gList.get(3);
                dlog.i("address : " + address);
                dlog.i("address1 : " + address1);
                dlog.i("address2 : " + address2);
                dlog.i("address3 : " + address3);
                String addresslines = address.getAddressLine(0);
                String subaddresslines = address1.getAddressLine(0);

                Setaddress = addresslines.replace("대한민국", "").trim();
                String dong = address1.getThoroughfare() == null ? "" : address1.getThoroughfare();
                String jibun = address1.getFeatureName() == null ? "" : address1.getFeatureName();
                String postalCode = address1.getPostalCode() == null ? "" : address1.getPostalCode();
                subaddresslines = dong + " " + jibun;
                dlog.i("Setaddress : " + Setaddress);
                dlog.i("subaddresslines : " + subaddresslines);

                shardpref.putString("pin_store_address", Setaddress);
                shardpref.putString("pin_store_addressdetail", subaddresslines);
                shardpref.putString("pin_zipcode", postalCode);
                shardpref.putString("pin_latitude", String.valueOf(latitude));
                shardpref.putString("pin_longitube", String.valueOf(longitube));
            }
        }
    }

    /**
     * Returns The approximate distance in meters between this
     * location and the given location. Distance is defined using
     * the WGS84 ellipsoid.
     *
     * @param //dest the destination location
     * @return the approximate distance in meters
     */
    //설정된 매장과 현재 내 위치의 거리를 재고 작업시작/종료 버튼의 활성화 비활성화 목적
    @SuppressLint("LongLogTag")
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;
        dlog.i("매장 위치 : " + lat1 + "," + lng1);
        dlog.i("현재 위치 : " + lat2 + "," + lng2);

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, null);
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

}
