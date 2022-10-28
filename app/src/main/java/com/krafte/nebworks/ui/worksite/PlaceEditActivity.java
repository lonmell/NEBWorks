package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.dataInterface.PlaceEditInterface;
import com.krafte.nebworks.dataInterface.PlaceThisDataInterface;
import com.krafte.nebworks.databinding.ActivityAddplaceBinding;
import com.krafte.nebworks.pop.DatePickerActivity;
import com.krafte.nebworks.pop.WorkTimePicker;
import com.krafte.nebworks.ui.PinSelectLocationActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.GpsTracker;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class PlaceEditActivity extends AppCompatActivity {
    private ActivityAddplaceBinding binding;
    Context mContext;
    int GALLEY_CODE = 10;
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private static final int PINSELECT_LOCATION_ACTIVITY = 20000;

    //Other
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();
    ArrayList<String> mList;
    Handler mHandler;
    RetrofitConnect rc = new RetrofitConnect();

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/heypass/";
    String ProfileUrl = "";

    Geocoder geocoder;

    LocationManager locationManager;
    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    String[] test = new String[6];
    String b_stt = "";
    String tax_type = "";

    GpsTracker gpsTracker;
    String SSID = "";
    String ConnectNetwork = "";
    double latitude = 0.1;
    double longitube = 0.1;
    String zipcode = "0";

    //시작시간
    int SelectStartTime = 1;
    String StartTime01 = "-99";
    String StartTime02 = "-99";

    //마감시간
    int SelectEndTime = 1;
    String EndTime01 = "-99";
    String EndTime02 = "-99";

    //CheckData Param
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

    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_AUTH = "";


    //--현장 정보 수정할때

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityAddplaceBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try{
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            place_id = shardpref.getString("place_id","0");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL","0");

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub",1);

            gpsTracker = new GpsTracker(mContext);
            geocoder = new Geocoder(mContext);

            setBtnEvent();
            getPlaceData();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setBtnEvent(){
        binding.backBtn.setOnClickListener(v -> {
            pm.PlaceListBack(mContext);
        });

        //------매장 이미지 등록 / 갤러리 열기
        binding.profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLEY_CODE);
        });

        binding.addPlaceBtn.setOnClickListener(v -> {
            if(CheckData()){
                AddPlace();
            }
        });


        binding.searchLocation.setOnClickListener(v -> {
            Intent i = new Intent(this, PinSelectLocationActivity.class);
            startActivityForResult(i, PINSELECT_LOCATION_ACTIVITY);
            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        });

        binding.inputWorkstartDay.setOnClickListener(v -> {
            if(binding.inputWorkstartDay.getText().toString().length() == 0){
                String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                binding.inputWorkstartDay.setText(today);
            }else{
                shardpref.putInt("timeSelect_flag",6);
                Intent intent = new Intent(this, DatePickerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            }
        });

        binding.worktime01Select.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 4);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });
        binding.worktime02Select.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkTimePicker.class);
            intent.putExtra("timeSelect_flag", 5);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });

        if(saveBitmap != null){
            binding.clearImg.setVisibility(View.VISIBLE);
            binding.imgPlus.setVisibility(View.GONE);
        }else{
            binding.clearImg.setVisibility(View.GONE);
            binding.imgPlus.setVisibility(View.VISIBLE);
        }
        binding.clearImg.setOnClickListener(v -> {
            try{
                saveBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                saveBitmap.eraseColor(Color.TRANSPARENT);
                binding.profileSetimg.setImageBitmap(saveBitmap);
                binding.profileSetimg.setBackgroundResource(R.drawable.img_box_round);
                ProfileUrl = "";
                binding.clearImg.setVisibility(View.GONE);
                binding.imgPlus.setVisibility(View.VISIBLE);
            }catch (Exception e){
                dlog.i("clearImg Exception : " + e);
            }

        });
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
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("getPlaceData jsonResponse length : " + response.body().length());
                            dlog.i("getPlaceData jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    shardpref.putString("place_name", Response.getJSONObject(0).getString("name"));
                                    shardpref.putString("place_owner_id", Response.getJSONObject(0).getString("owner_id"));
                                    shardpref.putString("place_owner_name", Response.getJSONObject(0).getString("owner_name"));
                                    shardpref.putString("place_management_office", Response.getJSONObject(0).getString("management_office"));
                                    shardpref.putString("place_address", Response.getJSONObject(0).getString("address"));
                                    shardpref.putString("place_latitude", Response.getJSONObject(0).getString("latitude"));
                                    shardpref.putString("place_longitude", Response.getJSONObject(0).getString("longitude"));
                                    shardpref.putString("place_start_time", Response.getJSONObject(0).getString("start_time"));
                                    shardpref.putString("place_end_time", Response.getJSONObject(0).getString("end_time"));
                                    shardpref.putString("place_img_path", Response.getJSONObject(0).getString("img_path"));
                                    shardpref.putString("place_start_date", Response.getJSONObject(0).getString("start_date"));
                                    shardpref.putString("place_created_at", Response.getJSONObject(0).getString("created_at"));

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
                                    USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
                                    USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");

                                    if(!place_img_path.isEmpty()){
                                        binding.clearImg.setVisibility(View.VISIBLE);
                                        binding.imgPlus.setVisibility(View.GONE);
                                    }else{
                                        binding.clearImg.setVisibility(View.GONE);
                                        binding.imgPlus.setVisibility(View.VISIBLE);
                                    }

                                    Glide.with(mContext).load(place_img_path)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .placeholder(R.drawable.no_image)
                                            .skipMemoryCache(true)
                                            .into(binding.profileSetimg);

                                    if (USER_INFO_ID.equals(place_owner_id)) {
                                        USER_INFO_AUTH = "0";
                                    } else {
                                        USER_INFO_AUTH = "1";
                                    }

                                    dlog.i("place_owner_id : " + place_owner_id);
                                    dlog.i("USER_INFO_ID : " + USER_INFO_ID);
                                    dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
                                    shardpref.putString("USER_INFO_AUTH", USER_INFO_AUTH);

                                    ProfileUrl = place_img_path;
                                    binding.storeNameInputBox.setText(place_name);
                                    binding.storeNameInputBox1.setText(place_management_office);
                                    binding.popText14.setText(place_address);
                                    binding.inputWorkstartDay.setText(place_start_date);
                                    latitude = Double.parseDouble(place_latitude);
                                    longitube = Double.parseDouble(place_longitude);
                                    binding.worktime01Select.setText(place_start_time.length() >= 5?place_start_time.substring(0,5):place_start_time);
                                    binding.worktime02Select.setText(place_end_time.length() >= 5?place_end_time.substring(0,5):place_end_time);

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

    public void AddPlace() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceEditInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceEditInterface api = retrofit.create(PlaceEditInterface.class);
        String placeAddress_get = place_address;
        Call<String> call = api.getData(place_id,place_name,place_management_office,placeAddress_get,String.valueOf(latitude),String.valueOf(longitube),place_start_time,place_end_time,ProfileUrl,place_start_date);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                            dlog.i("LoginCheck jsonResponse : " + response.body());
                            try {
                                if(!response.body().equals("[]") && response.body().replace("\"","").equals("success")){
                                    if(!ProfileUrl.isEmpty() && saveBitmap != null){
                                        saveBitmapAndGetURI();
                                    }
                                    Toast.makeText(mContext,"현장 수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                    pm.PlaceListBack(mContext);
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

    @Override
    public void onResume(){
        super.onResume();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        ImgfileMaker = ImageNameMaker();

        dlog.i("kind : " + shardpref.getInt("timeSelect_flag", 0));
        dlog.i("Hour : " + shardpref.getInt("Hour", 0));
        dlog.i("Min : " + shardpref.getInt("Min", 0));
        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);
        int hourOfDay = shardpref.getInt("Hour", 0);
        int minute = shardpref.getInt("Min", 0);

        dlog.i("timeSelect_flag : " + timeSelect_flag);
        if (timeSelect_flag == 4) {
            StartTime01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            StartTime02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            if (hourOfDay != 0) {
                String ampm = "";
                if (Integer.parseInt(StartTime01) < 12) {
                    ampm = " AM";
                    SelectStartTime = 1;
                } else {
                    ampm = " PM";
                    SelectStartTime = 2;
                }
                binding.worktime01Select.setText(StartTime01 + ":" + StartTime02 + ampm);
                place_start_time = StartTime01 + ":" + StartTime02;
                imm.hideSoftInputFromWindow(binding.worktime01Select.getWindowToken(), 0);
            }
        } else if (timeSelect_flag == 5) {
            EndTime01 = String.valueOf(hourOfDay).length() == 1 ? "0" + String.valueOf(hourOfDay) : String.valueOf(hourOfDay);
            EndTime02 = String.valueOf(minute).length() == 1 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            shardpref.remove("timeSelect_flag");
            shardpref.remove("Hour");
            shardpref.remove("Min");
            if (hourOfDay != 0) {
                String ampm = "";
                if (Integer.parseInt(EndTime01) < 12) {
                    ampm = " AM";
                    SelectEndTime = 1;
                } else {
                    ampm = " PM";
                    SelectEndTime = 2;
                }
                binding.worktime02Select.setText(EndTime01 + ":" + EndTime02 + ampm);
                place_end_time = EndTime01 + ":" + EndTime02;
                imm.hideSoftInputFromWindow(binding.worktime02Select.getWindowToken(), 0);
            }

        }else if (timeSelect_flag == 6) {
            //-- DatePickerActivity에서 받아오는 값
            String getDatePicker = shardpref.getString("vDateGetDate","");
            binding.inputWorkstartDay.setText(getDatePicker);
            shardpref.remove("vDateGetDate");
        }

        dlog.i("onResume Area");
        String getlatitude = shardpref.getString("pin_latitude", "0.0");
        String getlongitube = shardpref.getString("pin_longitube", "0.0");

        String getzipcode = shardpref.getString("pin_zipcode", "");
        String getstore_address = shardpref.getString("pin_store_address", "");
        String getstore_addressdetail = shardpref.getString("pin_store_addressdetail", "");

        if(!getstore_address.isEmpty()){
            binding.popText14.setText(getstore_address);
            binding.popText15.setText(getstore_addressdetail);

            latitude = Double.parseDouble(getlatitude);
            longitube = Double.parseDouble(getlongitube);

            shardpref.remove("pin_store_address");
            shardpref.remove("pin_zipcode");
            shardpref.remove("pin_store_addressdetail");
            shardpref.remove("pin_latitude");
            shardpref.remove("pin_longitube");
        }

    }

    private boolean CheckData(){
        dlog.i("----------CheckData----------");
        place_management_office = binding.storeNameInputBox.getText().toString();
        place_name = binding.storeNameInputBox1.getText().toString();
        place_address = binding.popText14.getText().toString();
        place_start_date = binding.inputWorkstartDay.getText().toString();
        dlog.i("ProfileUrl : " + ProfileUrl);
        dlog.i("manageplaceName : " + place_management_office);
        dlog.i("placeName : " + place_name);
        dlog.i("placeAddress : " + place_address);
        dlog.i("latitude : " + latitude);
        dlog.i("longitube : " + longitube);
        dlog.i("place_starttime : " + place_start_time);
        dlog.i("place_endtime : " + place_end_time);
        dlog.i("start_date : " + place_start_date);
        dlog.i("----------CheckData----------");

        if(place_management_office.isEmpty()){
            Toast.makeText(mContext,"관리소 명을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(place_name.isEmpty()){
            Toast.makeText(mContext,"현장 명을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(place_address.isEmpty()){
            Toast.makeText(mContext,"현장 주소를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(place_start_date.isEmpty()){
            Toast.makeText(mContext,"작업 시작일을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    //이미지 업로드에 필요한 소스 START
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_CODE) {
            if (resultCode == RESULT_OK) {

                String imagePath = "";
                try {
                    //1) data의 주소 사용하는 방법
                    imagePath = data.getDataString(); // "content://media/external/images/media/7215"

                    Glide.with(this)
                            .load(imagePath)
                            .into(binding.profileSetimg);
                    binding.clearImg.setVisibility(View.VISIBLE);
                    binding.imgPlus.setVisibility(View.GONE);

                    Glide.with(getApplicationContext()).asBitmap().load(imagePath).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            saveBitmap = resource;
                        }
                    });

                    final String IMG_FILE_EXTENSION = ".JPEG";
                    String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
                    ProfileUrl = "http://krafte.net/kogas/image/place_img/" + file_name;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                binding.imgPlus.setVisibility(View.VISIBLE);
                binding.clearImg.setVisibility(View.GONE);
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == SEARCH_ADDRESS_ACTIVITY) {
            binding.loginAlertText.setVisibility(View.VISIBLE);
            if (resultCode == RESULT_OK) {
                String GetData = data.getExtras().getString("data");
                if (GetData != null) {
//                         data의 정보를 각각 우편번호와 실주소로 나누어 EditText에 표시
                    dlog.i("RESULT_OK 1 : " + GetData.substring(0, 5));
                    dlog.i("RESULT_OK 2 : " + GetData.substring(7));
                    zipcode = GetData.substring(0, 5);
                    binding.popText14.setText(GetData.substring(7));
                    latitude = findGeoPoint_lat(mContext, binding.popText14.getText().toString());
                    longitube = findGeoPoint_lon(mContext, binding.popText14.getText().toString());
                }
            }
            binding.loginAlertText.setVisibility(View.GONE);
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }


    private String ImageNameMaker() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MakeFileNameInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MakeFileNameInterface api = retrofit.create(MakeFileNameInterface.class);
        Call<String> call = api.getData("");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());

                        if (!Response.toString().equals("[]")) {
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                ImgfileMaker = jsonObject.getString("id");
                                dlog.i("ImgfileMaker : " + ImgfileMaker);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
        return ImgfileMaker;
    }

    //절대경로를 구한다.
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return url;
    }

    @SuppressLint({"SimpleDateFormat", "LongLogTag"})
    public Uri saveBitmapAndGetURI() {
        //Create Bitmap
//            saveBitmap = CanvasIO.openBitmap(mContext);
        binding.loginAlertText.setVisibility(View.VISIBLE);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        //Create Bitmap -> File
        final String IMG_FILE_EXTENSION = ".JPEG";
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
        String fullFileName = BACKUP_PATH;

        dlog.i("(saveBitmapAndGetURI)ex_storage : " + ex_storage);
        dlog.i("(saveBitmapAndGetURI)USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("(saveBitmapAndGetURI)fullFileName : " + fullFileName);

        File file_path;
        try {
            file_path = new File(fullFileName);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            dlog.i("(saveBitmapAndGetURI)file_path : " + file_path);
            dlog.i("(saveBitmapAndGetURI)file_name : " + file_name);
            file = new File(file_path, file_name);
            FileOutputStream out = new FileOutputStream(file);
            saveBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            ProfileUrl = "http://krafte.net/kogas/image/place_img/" + file_name;
            saveBitmapToFile(file);

            dlog.e("사인 저장 경로 : " + ProfileUrl);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            PlaceAddActivity.RetrofitInterface retrofitInterface = PlaceAddActivity.ApiClient.getApiClient().create(PlaceAddActivity.RetrofitInterface.class);
            Call<String> call = retrofitInterface.request(body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("uploaded_file()", "성공 : call = " + call + "response = " + response);

                    if (fileDelete(String.valueOf(file))) {
                        Log.e("uploaded_file()", "기존 이미지 삭제 완료");
                    } else {
                        Log.e("uploaded_file()", "이미지 삭제 오류");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("uploaded_file()", "에러 : " + t.getMessage());
                }
            });
            Log.d("(saveBitmapAndGetURI)이미지 경로 : ", Uri.fromFile(file).toString());

            out.close();
            binding.loginAlertText.setVisibility(View.GONE);
            dlog.i("(saveBitmapAndGetURI)file : " + file);
//            mHandler = new Handler(Looper.getMainLooper());
//            mHandler.postDelayed(this::setUpdateUserStoreThumnail, 0);
        } catch (FileNotFoundException exception) {
            dlog.e("FileNotFoundException : " + exception.getMessage());
        } catch (IOException exception) {
            dlog.e("IOException : " + exception.getMessage());
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static boolean fileDelete(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                return true;
            }
        } catch (Exception e) {
            Log.e("PlaceAddActivity fileDelete", e.getMessage());
        }
        return false;
    }

    public static class ApiClient {
        private static final String BASE_URL = "http://krafte.net/kogas/image/";
        private static Retrofit retrofit;

        public static Retrofit getApiClient() {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
            return retrofit;
        }

    }

    public interface RetrofitInterface {
        //api를 관리해주는 인터페이스
        @Multipart
        @POST("upload_place_img.php")
        Call<String> request(@Part MultipartBody.Part file);
    }

    public File saveBitmapToFile(File file) {
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 8;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 50;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 8 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 8 >= REQUIRED_SIZE) {
                scale *= 8;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    /*지오 코딩용 소스 (예비용) - 주소 >> 위도,경도로 변경하는 소스  START*/
    public static double findGeoPoint_lat(Context mcontext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정

        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            Address lating = addr.get(0);
            double lat = lating.getLatitude(); // 위도가져오기
            loc.setLatitude(lat);
            return lat;
        }
        return 0;
    }

    public static double findGeoPoint_lon(Context mcontext, String address) {
        Geocoder coder = new Geocoder(mcontext);
        List<Address> addr = null;// 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정

        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            Address lating = addr.get(0);
            return lating.getLongitude();
        }
        return 0;
    }
    /*지오 코딩용 소스 (예비용) - 주소 >> 위도,경도로 변경하는 소스  END*/
}
