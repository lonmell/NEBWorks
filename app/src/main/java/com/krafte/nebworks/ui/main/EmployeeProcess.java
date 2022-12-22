package com.krafte.nebworks.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ParseException;

import com.krafte.nebworks.R;
import com.krafte.nebworks.bottomsheet.InoutPopActivity;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.databinding.ActivityEmployeeProcessBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.GpsTracker;
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

public class EmployeeProcess extends AppCompatActivity {
    private ActivityEmployeeProcessBinding binding;
    Context mContext;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

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
    String jongeob = "";
    String mem_name = "";
    Calendar cal;
    String today = "";
    String format = "HH:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(format);

    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate = new java.text.SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate_age = new java.text.SimpleDateFormat("yyyy");

    @SuppressLint("SimpleDateFormat")
    java.text.SimpleDateFormat simpleDate_time = new java.text.SimpleDateFormat("HH:mm:ss");

    String GET_TIME = simpleDate_time.format(mDate);
    String title = "";
    String io_state = "";
    String input_date = "";
    String getMySSID = "";

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
        try {
            place_id        = shardpref.getString("place_id", "0");
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID", "0");
            kind            = shardpref.getString("kind", "0");
            jongeob         = shardpref.getString("jongeob", "");
            mem_name        = shardpref.getString("mem_name", "");

            onBtnEvent();

            if (kind.equals("0")) {
                binding.ioBtn.setBackgroundResource(R.drawable.ic_in_btn_white);
            } else {
                binding.ioBtn.setBackgroundResource(R.drawable.workinout02);
            }

            binding.processDate.setText(dc.GET_YEAR + "년 " + dc.GET_MONTH + "월 " + dc.GET_DAY + "일");
            binding.processTitle.setText(mem_name + "님 오늘도 화이팅하세요!");

            binding.closeProcess.setOnClickListener(v -> {
                super.onBackPressed();
            });
            binding.closeBtn.setOnClickListener(v -> {
                super.onBackPressed();
            });

            cal = Calendar.getInstance();
            today = sdf.format(cal.getTime());
            dlog.i("오늘 :" + today);
            dlog.i("jongeob :" + jongeob.substring(3));

            getMySSID = getNetworkName(mContext).replace("\"","");
            if(getMySSID.equals("<unknown ssid>")){
                getMySSID = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPlaceData();
        MoveMyLocation();
        handler.postDelayed(() -> {
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
            dlog.i("거리 : " + getDistance);
            if (getDistance <= 40) {
                if (kind.equals("0")) {
                    title = "출근처리";
                } else {
                    title = "퇴근처리";
                }
                binding.storeDistance.setText("매장과 " + getDistance + "m 떨어져있습니다.");
                binding.inoutAble.setText(kind.equals("0") ? "출근처리가능" : "퇴근처리가능");
                binding.inoutAble.setTextColor(Color.parseColor("#6395EC"));
                dlog.i("binding.selectWorkse setOnClickListener kind : " + kind);
            } else {
                if (kind.equals("0")) {
                    title = "출근처리 불가";
                } else {
                    title = "퇴근근처리 불가";
                }
                binding.storeDistance.setText("매장과의 거리가 30미터 이상 입니다.");
                binding.inoutAble.setText("출근처리불가");
                binding.inoutAble.setTextColor(Color.parseColor("#DD6540"));
//            Toast_Nomal("매장 출근의 설정된 거리보다 멀리 있습니다.");
            }
        }, 1000); //0.5초 후 인트로 실행
    }

    public boolean compareDate2() throws ParseException {
        boolean returntf = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date1 = sdf.parse(today);
            Date date2 = sdf.parse(jongeob.substring(3));
            System.out.println(sdf.format(date1));
            System.out.println(sdf.format(date2));

            returntf =  date1.after(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returntf;
    }

    String change_place_id = "";
    String change_place_name = "";
    String change_place_owner_id = "";

    private void onBtnEvent() {
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
            dlog.i("getDistance : " + getDistance);
            dlog.i("kind : " + kind);

            if (kind.equals("0")) {
                if (getDistance <= 40) {
                    //가게 등록한 와이파이와 현재 디바이스에서 접속중인 와이파이 비교
                    if(getMySSID.equals(place_wifi_name)){
                        io_state = "출근처리";
                        InOutPop(GET_TIME, "1", place_name, io_state, "","0");
                    }else{
                        InOutPop(GET_TIME, "2", place_name, "출근처리 불가", "매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요","0");
                    }
                } else {
                    InOutPop(GET_TIME, "2", place_name, "출근처리 불가", "설정된 근무지에서만 출근이 가능합니다.\n" + "근무지와 너무 멀어 출근처리가 불가합니다.","0");
                }
            } else {
                if (getDistance <= 40) {
                    io_state = "퇴근처리";
                    dlog.i("compareDate2 :" +  compareDate2());
                    //가게 등록한 와이파이와 현재 디바이스에서 접속중인 와이파이 비교
                    if(getMySSID.equals(place_wifi_name)){
                        if(compareDate2()){
                            InOutPop(GET_TIME, "4", place_name, io_state, "","1");
                        }else{
                            InOutPop(GET_TIME, "3", place_name, io_state, "등록된 퇴근시간이 아닙니다.","1");//퇴근시간 전일때
                        }
                    }else{
                        InOutPop(GET_TIME, "2", place_name, "퇴근처리 불가", "매장에 설정된 와이파이가 아닙니다.\n" + "와이파이를 확인해주세요","1");
                    }
                } else {
                    InOutPop(GET_TIME, "2", place_name, "퇴근처리 불가", "설정된 근무지에서만 퇴근이 가능합니다.\n" + "근무지와 너무 멀어 퇴근처리가 불가합니다.","1");
                }
            }
        });

        binding.placeChangeArea.setOnClickListener(v -> {
            PlaceListBottomSheet plb = new PlaceListBottomSheet();
            plb.show(getSupportFragmentManager(), "PlaceListBottomSheet");
            plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                change_place_id = place_id;
                change_place_name = place_name;
                change_place_owner_id = place_owner_id;
                shardpref.putString("change_place_id", place_id);
                shardpref.putString("change_place_name", place_name);
                shardpref.putString("change_place_owner_id", place_owner_id);
                dlog.i("change_place_id : " + place_id);
                dlog.i("change_place_name : " + place_name);
                dlog.i("change_place_owner_id : " + place_owner_id);
                binding.storeName.setText(place_name);
            });
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
                                    shardpref.putString("place_wifi_name",place_wifi_name);
                                    shardpref.putString("place_latitude",String.valueOf(place_latitude));
                                    shardpref.putString("place_longitude",String.valueOf(place_longitude));
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

    private void InOutPop(String time, String state, String store_name, String inout_tv, String inout_tv2, String kind) {
        shardpref.putString("kind", kind);
        shardpref.putString("time", time);
        shardpref.putString("state", state);
        shardpref.putString("store_name", store_name);
        shardpref.putString("inout_tv", inout_tv);
        shardpref.putString("inout_tv2", inout_tv2);
        InoutPopActivity ipp = new InoutPopActivity();
        ipp.show(getSupportFragmentManager(),"InoutPopActivity");
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

    public String getNetworkName(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getSSID();
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
