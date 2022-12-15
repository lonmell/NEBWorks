package com.krafte.nebworks.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.InOutInsertInterface;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.databinding.ActivityEmployeeProcessBinding;
import com.krafte.nebworks.pop.InoutPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.GpsTracker;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.threeten.bp.LocalDateTime;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class EmployeeProcess extends AppCompatActivity {
    private ActivityEmployeeProcessBinding binding;
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();

    int getDistance = 0;
    int location_cnt = 0;
    String USER_INFO_ID = "";
    String kind = "";
    String place_end_time = "";
    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate = new java.text.SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate_age = new java.text.SimpleDateFormat("yyyy");

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate_time = new java.text.SimpleDateFormat("HH:mm:ss");

    String GET_DAY = simpleDate.format(mDate) + " " + simpleDate_time.format(mDate);
    String GET_TIME_AGE = simpleDate_age.format(mDate);
    String GET_TIME = simpleDate_time.format(mDate);
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        binding = ActivityEmployeeProcessBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        try{
            place_id        = shardpref.getString("place_id","0");
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID","0");
            kind            = shardpref.getString("kind", "0");
            place_end_time  = shardpref.getString("place_end_time","");
            onBtnEvent();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getPlaceData();
        MoveMyLocation();
        dlog.i("location_cnt : " + location_cnt);
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        getDistance = Integer.parseInt(String.valueOf(Math.round(getDistance(place_latitude, place_longitude, latitude, longitude))));
        dlog.i("location_cnt : " + location_cnt);
        dlog.i("GET_TIME : " + simpleDate.format(mDate));
        dlog.i("위도 : " + latitude + ", 경도 : " + longitude);

        if (getDistance <= 30) {
            if(kind.equals("0")){
                title = "출근처리";
            }else{
                title = "퇴근근처리 불가";
            }
            binding.storeDistance.setText("매장과 " + getDistance + "m 떨어져있습니다.");
            binding.inoutAble.setText(kind.equals("0")?"출근처리가능":"퇴근처리가능");
            binding.inoutAble.setTextColor(Color.parseColor("#6395EC"));
            dlog.i("binding.selectWorkse setOnClickListener kind : " + kind);
            InOutInsert();
        } else {
            if(kind.equals("0")){
                title = "출근처리 불가";
            }else{
                try {
                    String today = dc.GET_TIME;

                    compareDate1(place_end_time, today);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                title = "퇴근근처리 불가";
            }
            binding.storeDistance.setText("매장과의 거리가 30미터 이상 입니다.");
            binding.inoutAble.setText("출근처리불가");
            binding.inoutAble.setTextColor(Color.parseColor("#DD6540"));
//            Toast_Nomal("매장 출근의 설정된 거리보다 멀리 있습니다.");
        }
    }

    public void compareDate1(String before, String after) throws ParseException {

        LocalDateTime date1 = LocalDateTime.parse(before); //before
        LocalDateTime date2 = LocalDateTime.parse(after); //after

        if (date1.isBefore(date2)) {
            System.out.println("Date1 is before Date2");
        }

        if (date1.isAfter(date2)) {
            System.out.println("Date1 is after Date2");
        }

        if (date1.isEqual(date2)) {
            System.out.println("Date1 is equal Date2");
        }
    }

    private void onBtnEvent(){
        binding.ioBtn.setOnClickListener(v -> {
            MoveMyLocation();
            dlog.i("location_cnt : " + location_cnt);
            long now = System.currentTimeMillis();
            Date mDate = new Date(now);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            getDistance = Integer.parseInt(String.valueOf(Math.round(getDistance(place_latitude, place_longitude, latitude, longitude))));
            dlog.i("location_cnt : " + location_cnt);
            dlog.i("GET_TIME : " + simpleDate.format(mDate));
            dlog.i("위도 : " + latitude + ", 경도 : " + longitude);
            if (getDistance <= 30) {
                dlog.i("binding.selectWorkse setOnClickListener kind : " + kind);
//                InOutLogMember();
//                if (!place_owner_id.equals(USER_INFO_ID)) {
//                    getManagerToken(place_owner_id, "0", place_id, place_name);
//                }
                InOutInsert();
            } else {
                Toast_Nomal("매장 출근의 설정된 거리보다 멀리 있습니다.");
            }
        });
    }

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
                    runOnUiThread(() -> {
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

                                    binding.storeName.setText(place_name);
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

    private void InOutInsert() {
        dlog.i("--------InOutInsert--------");
        dlog.i("titel : " + title);
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("kind - 0출근, 1퇴근 : " + kind);
        dlog.i("--------InOutInsert--------");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InOutInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        InOutInsertInterface api = retrofit.create(InOutInsertInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, "0");
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.body().replace("[", "").replace("]", "").replace("\"", "").length() == 0) {
                            //최초 출근

                        } else if (response.body().replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && response.body() != null) {
                                dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                                dlog.i("LoginCheck jsonResponse : " + response.body());
                                try {
                                    if (response.body().replace("[", "").replace("]", "").replace("\"", "").equals("success")) {
//                                        timer.cancel();
                                        Intent intent = new Intent(mContext, InoutPopActivity.class);
                                        intent.putExtra("title", "출근 처리되었습니다.");
                                        intent.putExtra("time", GET_TIME);
                                        intent.putExtra("state", kind);
                                        intent.putExtra("store_name", place_name);
                                        mContext.startActivity(intent);
                                        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                        if (!place_owner_id.equals(USER_INFO_ID)) {
//                                            getEmployerToken();
                                        }
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

    GpsTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    private void MoveMyLocation() {
        try {
            gpsTracker = new GpsTracker(mContext);
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            reverseCoding(latitude, longitude);
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }
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
