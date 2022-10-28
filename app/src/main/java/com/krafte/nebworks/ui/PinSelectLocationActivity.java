package com.krafte.nebworks.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.util.helper.Utility;
import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ActivityPinselectBinding;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.GpsTracker;
import com.krafte.nebworks.util.PreferenceHelper;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PinSelectLocationActivity  extends AppCompatActivity implements MapView.MapViewEventListener {
    private static final String TAG = "EmployerAddStoreActivity";
    private ActivityPinselectBinding binding;
    Context mContext;

    private String GEOCODE_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json?";
    private String GEOCODE_USER_INFO = "KakaoAK{d2522a2e1d58aea1c2535f7ef0aecd14}";//
    //xml
    LinearLayout mylocation;

    MapView mapView;
    String KakaoMap_url = "";
    ViewGroup mapViewContainer;
    TextView address01,address02,sendAddress;

    // shared 저장값
    PreferenceHelper shardpref;


    //Other
    Dlog dlog = new Dlog();
    GpsTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    String sido = "";
    String gugun = "";
    String division = "";
    String Setaddress = "";
    Handler mHandler;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    ArrayList<MapPOIItem> markerArray;

    Double lat, len;
    Double slat, slen;
    String address;
    String StoreUrl = "";
    String getLatitude = "";
    String getLongitude = "";
    String Location_Name = "";
    String Location_Address = "";
    Thread th;
    LocationManager locationManager;
    public String KAKAO_keyHash = "";

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "LongLogTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pinselect);
        binding = ActivityPinselectBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);

        // Kakao SDK 등록
        KakaoSdk.init(this, getString(R.string.kakao_native_key));
        KAKAO_keyHash = Utility.getKeyHash(this);
        String Release_keyHash = com.kakao.util.maps.helper.Utility.getKeyHash(this /* context */);
        setContentLayout();
        setBtnEvent();

        try {
            mapView = new MapView(this);
            mapViewContainer = findViewById(R.id.map_view);

            if (!checkLocationServicesStatus()) {
                showDialogForLocationServiceSetting();
            } else {
                checkRunTimePermission();
//            mapView.setShowCurrentLocationMarker(true);
//            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
//            mapView.setPOIItemEventListener(this);
            }
            MoveMyLocation();
            mapView.setMapViewEventListener(this);
        } catch (Exception e) {
            dlog.i( "Exception : " + e);
        }


    }

    public void setPOIItemEventListener(MapView.POIItemEventListener poiItemEventListener){

    }
    private void setContentLayout() {
        address01 = findViewById(R.id.address01);
        address02 = findViewById(R.id.address02);
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

        binding.sendAddress.setOnClickListener(v -> {
            super.onBackPressed();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(0, R.anim.translate_down);
        });
    }

    private void MoveMyLocation() {
        try {
            gpsTracker = new GpsTracker(this);
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            mapViewContainer.addView(mapView);

            /*현재 내 위치로 지도 중앙을 이동, 위치 트래킹 기능 on*/
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 2, true);
            mapView.setZoomLevel(0, true);
            mapView.zoomIn(true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            reverseCoding(latitude, longitude);
        } catch (Exception e) {
            Log.i(TAG, "Exception : " + e);
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
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
                Log.i(TAG, "hasFineLocationPermission PERMISSION_GRANTED AND hasCoarseLocationPermission PERMISSION_GRANTED");

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

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        dlog.i("mapView : " + mapView.getMapCenterPoint().getMapPointGeoCoord().latitude + "," + mapView.getMapCenterPoint().getMapPointGeoCoord().longitude
                + " / mapPoint : " + mapPoint.getMapPointGeoCoord().latitude + "," + mapPoint.getMapPointGeoCoord().longitude);
        reverseCoding(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude, mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    //--MapViewEventListener END

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

                //MainAddrerss
                address01.setText(Setaddress);

                //subAddress
                address02.setText("[지번] " + subaddresslines);
                shardpref.putString("pin_store_address",Setaddress);
                shardpref.putString("pin_store_addressdetail",subaddresslines);
                shardpref.putString("pin_zipcode",postalCode);
                shardpref.putString("pin_latitude",String.valueOf(latitude));
                shardpref.putString("pin_longitube",String.valueOf(longitube));
            }
        }
    }
    //역 지오코딩 ( 위,경도 >> 주소 ) END

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        shardpref.remove("pin_store_address");
        shardpref.remove("pin_zipcode");
        shardpref.remove("pin_store_addressdetail");
        shardpref.remove("pin_latitude");
        shardpref.remove("pin_longitube");
    }
}
