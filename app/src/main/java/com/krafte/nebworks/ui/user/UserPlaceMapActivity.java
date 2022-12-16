package com.krafte.nebworks.ui.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.InOutInsertInterface;
import com.krafte.nebworks.dataInterface.InOutLogInterface;
import com.krafte.nebworks.dataInterface.PlaceMemberAddInterface;
import com.krafte.nebworks.databinding.ActivityUserplacemapBinding;
import com.krafte.nebworks.bottomsheet.InoutPopActivity;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.GpsTracker;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 2022-10-06 방창배 작업 시작
 * 2022-10-07 위치 트래킹 및 현재 위치 표시, 매장과 사용자 거리 계산 후 작업시작 기능 추가
 *
 * */
public class UserPlaceMapActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
    private ActivityUserplacemapBinding binding;
    Context mContext;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    LocationManager locationManager;
    GpsTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;

    MapView mapView;
    ViewGroup mapViewContainer;
    MapCircle circle1;
    public String KAKAO_keyHash = "";
    Timer timer;

    String io_state = "";
    String kind = "";
    String place_id = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_EMAIL = "";
    Double place_latitude = 0.0;
    Double place_longitude = 0.0;
    String place_name = "";
    String place_owner_id = "";

    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate_age = new SimpleDateFormat("yyyy");

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDate_time = new SimpleDateFormat("HH:mm:ss");

    String GET_DAY = simpleDate.format(mDate) + " " + simpleDate_time.format(mDate);
    String GET_TIME_AGE = simpleDate_age.format(mDate);
    String GET_TIME = simpleDate_time.format(mDate);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityUserplacemapBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        // Kakao SDK 등록
//        KakaoSdk.init(this, getString(R.string.kakao_native_key));
//        KAKAO_keyHash = Utility.getKeyHash(this);
        gpsTracker = new GpsTracker(this);

        try {
            place_id = shardpref.getString("place_id", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            place_latitude = Double.parseDouble(shardpref.getString("place_latitude", "0"));
            place_longitude = Double.parseDouble(shardpref.getString("place_longitude", "0"));
            place_name = shardpref.getString("place_management_office", "");
            dlog.i("------onCreate DataCheck-----");
            dlog.i("place_id : " + place_id);
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("place_latitude : " + place_latitude);
            dlog.i("place_longitude : " + place_longitude);
            dlog.i("------onCreate DataCheck-----");

            setBtnEvent();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
//            AddPlaceMember(place_id, USER_INFO_ID);
            InOutLogMember();

            try {
                mapView = new MapView(this);
                mapViewContainer = findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);
            } catch (Exception e) {
                dlog.i("Exception : " + e);
            }
            if (!checkLocationServicesStatus()) {
                showDialogForLocationServiceSetting();
            } else {
                checkRunTimePermission();
                mapView.setShowCurrentLocationMarker(true);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//            mapView.setPOIItemEventListener(this);
            }
            setBtnEvent();
            MoveMyLocation();

            timer = new Timer();

            TimerTask TT = new TimerTask() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    // 반복실행할 구문
                    runOnUiThread(() -> {
                        long now = System.currentTimeMillis();
                        Date mDate = new Date(now);
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm");

                        dlog.i("GET_TIME : " + simpleDate.format(mDate));
                        binding.timeSet.setText(simpleDate.format(mDate));
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();
                        binding.lonLat.setText("위도 : " + latitude + ", 경도 : " + longitude);
                        binding.distance.setText("작업장과의 거리 : " + Integer.parseInt(String.valueOf(Math.round(getDistance(place_latitude, place_longitude, latitude, longitude)))));
                    });
                }
            };
            timer.schedule(TT, 1000, 1000); //Timer 실행

            circle1 = new MapCircle(
                    MapPoint.mapPointWithGeoCoord(place_latitude, place_longitude), // center
                    30, // radius
                    Color.argb(50, 153, 204, 255), // strokeColor
                    Color.argb(50, 153, 204, 255) // fillColor
            );
            circle1.setTag(1234);
            mapView.addCircle(circle1);

            shardpref.remove("place_latitude");
            shardpref.remove("place_longitude");

        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }


        mapView.setMapViewEventListener(this);
    }

    public void AddPlaceMember(String place_id, String account) {
        dlog.i("UserCheck account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberAddInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberAddInterface api = retrofit.create(PlaceMemberAddInterface.class);
//        Call<String> call = api.getData(place_id, account,"","","");
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"LongLogTag", "SetTextI18n"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful() && response.body() != null) {
////                            String jsonResponse = rc.getBase64decode(response.body());
//                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
//                            dlog.i("UserCheck jsonResponse : " + response.body());
//                            if (!response.body().replace("\"", "").equals("success")) {
//                                dlog.i("매장 멤버 추가 완료");
//                            }
//                        }
//                    });
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                dlog.e("에러1 = " + t.getMessage());
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.removeAllCircles();
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
//        mapView.setShowCurrentLocationMarker(false);
        timer.cancel();//타이머 종료
    }

    private void setBtnEvent() {
        binding.mylocation.setOnClickListener(v -> {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            /*현재 내 위치로 지도 중앙을 이동, 위치 트래킹 기능 on*/
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 2, true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            reverseCoding(latitude, longitude);
        });

        binding.selectWorkse.setOnClickListener(v -> {
            int int_distance = Integer.parseInt(String.valueOf(Math.round(getDistance(place_latitude, place_longitude, latitude, longitude))));
            if (kind.equals("-1") || kind.equals("0")) {
                if (kind.equals("-1")) {
                    kind = "0";
                } else {
                    kind = "1";
                }
                if (int_distance > 30) {
                    binding.loginAlertText.setVisibility(View.GONE);
                    Intent intent = new Intent(this, OneButtonPopActivity.class);
                    intent.putExtra("data", "작업장과의 거리가 멀어서 작업을 시작 할 수 없습니다.");
                    intent.putExtra("left_btn_txt", "확인");
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_up, 0);
                } else {
                    dlog.i("binding.selectWorkse setOnClickListener kind : " + kind);
                    binding.loginAlertText.setVisibility(View.VISIBLE);
                    InOutLogMember();
                    if (!place_owner_id.equals(USER_INFO_ID)) {
                        getManagerToken(place_owner_id, "0", place_id, place_name);
                    }
                    InOutInsert(kind);
                }
            }else {
                if(USER_INFO_AUTH.equals("0")){
                    pm.Main(mContext);
                }else{
                    pm.Main2(mContext);
                }
            }

        });

        binding.close.setOnClickListener(v -> {
            if(USER_INFO_AUTH.equals("0")){
                pm.Main(mContext);
            }else{
                pm.Main2(mContext);
            }
        });

        binding.outStore.setOnClickListener(v -> {
            pm.PlaceList(mContext);
        });
    }

    public void getManagerToken(String user_id, String type, String place_id, String place_name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(user_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());

                    String id = Response.getJSONObject(0).getString("id");
                    String token = Response.getJSONObject(0).getString("token");
                    String department = shardpref.getString("USER_INFO_SOSOK", "");
                    String position = shardpref.getString("USER_INFO_JIKGUP", "");
                    String name = shardpref.getString("USER_INFO_NAME", "");
                    dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                    dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                    dlog.i("department : " + Response.getJSONObject(0).getString("department"));
                    dlog.i("position : " + Response.getJSONObject(0).getString("position"));

                    boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                    if (!token.isEmpty() && channelId1) {
                        String workse = kind.equals("0") ? "작업종료" : "작업시작"; // 현작 작업 시작, 퇴근
                        String message = department + " " + position + " " + name + " 님이 " + place_name + " 매장 " + workse + "했습니다.";
                        PushFcmSend(id, "", message, token, "1", place_id);
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

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "PlaceListActivity";
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void MoveMyLocation() {
        try {
            gpsTracker = new GpsTracker(this);
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            mapViewContainer.addView(mapView);

            /*현재 내 위치로 지도 중앙을 이동, 위치 트래킹 기능 on*/
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 1, true);
            mapView.setZoomLevel(0, true);
            mapView.zoomIn(true);
            reverseCoding(latitude, longitude);
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }
    }

    public void InOutLogMember() {
        dlog.i("--------InOutLogMember--------");
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("--------InOutLogMember--------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InOutLogInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        InOutLogInterface api = retrofit.create(InOutLogInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.body().replace("[", "").replace("]", "").length() == 0) {
                            //그날 최초 출근
                            kind = "-1";
//                            InOutInsert("0");
                        } else if (response.body().replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && response.body() != null) {
                                dlog.i("InOutLogMember jsonResponse length : " + response.body().length());
                                dlog.i("InOutLogMember jsonResponse : " + response.body());
                                try {
                                    JSONArray Response = new JSONArray(response.body());
                                    kind = Response.getJSONObject(0).getString("kind");
                                    dlog.i("InOutLogMember kind : " + kind);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (kind.equals("-1")) {
                            binding.selectWorkse.setText("작업시작");
                        } else if (kind.equals("0")) {
                            binding.selectWorkse.setText("작업종료");
                        } else {
                            binding.selectWorkse.setText("확인");
                            binding.closeArea.setVisibility(View.GONE);
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

    private void InOutInsert(String kind) {
        dlog.i("--------InOutLogMember--------");
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("kind - 0출근, 1퇴근 : " + kind);
        dlog.i("--------InOutLogMember--------");

        if (kind.equals("0")) {
            io_state = "출근";
        } else {
            io_state = "퇴근";
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
                    runOnUiThread(() -> {
                        if (response.body().replace("[", "").replace("]", "").replace("\"", "").length() == 0) {
                            //최초 출근

                        } else if (response.body().replace("[", "").replace("]", "").length() > 0) {
                            if (response.isSuccessful() && response.body() != null) {
                                dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                                dlog.i("LoginCheck jsonResponse : " + response.body());
                                try {
                                    if (response.body().replace("[", "").replace("]", "").replace("\"", "").equals("success")) {
//                                        Intent intent = new Intent(mContext, InoutPopActivity.class);
//                                        intent.putExtra("title", io_state + " 처리되었습니다.");
//                                        intent.putExtra("time", GET_TIME);
//                                        intent.putExtra("state", "1");
//                                        intent.putExtra("store_name", place_name);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.translate_up, 0);
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

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("취소", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {//사용자가 GPS 활성 시켰는지 검사
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                    checkRunTimePermission();
                }
            }
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void checkRunTimePermission() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        //GPS가 켜져있는지 체크
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(this, OneButtonPopActivity.class);
            intent.putExtra("data", "GPS 설정이 필요합니다.");
            intent.putExtra("left_btn_txt", "확인");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        } else {
            //런타임 퍼미션 처리
            // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                // 2. 이미 퍼미션을 가지고 있다면
                // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
                // 3.  위치 값을 가져올 수 있음
                dlog.i("hasFineLocationPermission PERMISSION_GRANTED AND hasCoarseLocationPermission PERMISSION_GRANTED");

            } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
                // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                    Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                } else {
                    // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }
            }
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

//                String city = address.getLocality() == null ? "" : address.getLocality();
//                String state = address.getAdminArea() == null ? "" : address.getAdminArea();
//
//                String country = address.getCountryName() == null ? "" : address.getCountryName();
//                String jibun = address.getFeatureName() == null ? "" : address.getFeatureName();
//                String postalCode = address.getPostalCode() == null ? "" : address.getPostalCode();
//                String roadAddress = address.getSubAdminArea() == null ? "" : address.getSubAdminArea();

                Setaddress = addresslines.replace("대한민국", "").trim();
                String dong = address1.getThoroughfare() == null ? "" : address1.getThoroughfare();
                String jibun = address1.getFeatureName() == null ? "" : address1.getFeatureName();
                String postalCode = address1.getPostalCode() == null ? "" : address1.getPostalCode();
                subaddresslines = dong + " " + jibun;
                dlog.i("Setaddress : " + Setaddress);
                dlog.i("subaddresslines : " + subaddresslines);

//                //MainAddrerss
//                address01.setText(Setaddress);
//
//                //subAddress
//                address02.setText("[지번] " + subaddresslines);
                shardpref.putString("pin_store_address", Setaddress);
                shardpref.putString("pin_store_addressdetail", subaddresslines);
                shardpref.putString("pin_zipcode", postalCode);
                shardpref.putString("pin_latitude", String.valueOf(latitude));
                shardpref.putString("pin_longitube", String.valueOf(longitube));
            }
        }
    }
    //역 지오코딩 ( 위,경도 >> 주소 ) END

    //--MapViewEventListener START
    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        //맵 중심의 드래그한 위도경도 가져옴
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
//        dlog.i("mapView : " + mapView.getMapCenterPoint().getMapPointGeoCoord().latitude + "," + mapView.getMapCenterPoint().getMapPointGeoCoord().longitude
//                + " / mapPoint : " + mapPoint.getMapPointGeoCoord().latitude + "," + mapPoint.getMapPointGeoCoord().longitude);
//        reverseCoding(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude, mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    //--MapViewEventListener END


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        pm.PlaceList(mContext);
        shardpref.remove("pin_store_address");
        shardpref.remove("pin_zipcode");
        shardpref.remove("pin_store_addressdetail");
        shardpref.remove("pin_latitude");
        shardpref.remove("pin_longitube");
    }


    //지도에 현재 위치의 동그라미가 표시되도록 하고 싶다면 넣어야함 (implements MapView.POIItemEventListener )
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            String value = extras.getString(USER_INFO_ID);
        }
    }
}
