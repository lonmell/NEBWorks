package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krafte.nebworks.R;
import com.krafte.nebworks.bottomsheet.SelectStringBottomSheet;
import com.krafte.nebworks.bottomsheet.StoreDivisionPopActivity;
import com.krafte.nebworks.bottomsheet.WorkTimePicker;
import com.krafte.nebworks.dataInterface.ConfrimNumInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.dataInterface.PlaceEditInterface;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.dataInterface.RegistrSearchInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityAddplaceBinding;
import com.krafte.nebworks.ui.PinSelectLocationActivity;
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
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    File file;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/nebworks/";
    String ProfileUrl = "";

    Geocoder geocoder;

    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    String b_stt = "";
    String tax_type = "";

    GpsTracker gpsTracker;
    //    double latitude = 0.1;
//    double longitube = 0.1;
    String zipcode = "0";


    //시작시간
    int SelectStartTime = 1;
    String StartTime01 = "-99";
    String StartTime02 = "-99";

    //마감시간
    int SelectEndTime = 1;
    String EndTime01 = "-99";
    String EndTime02 = "-99";

    //Shared
    String USER_INFO_EMAIL = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";

    //CheckData Param
    String place_id = "";
    String placeName = "";
    String placeAddress = "";
    String placeDtailAddress = "";
    String place_starttime = "";
    String place_endtime = "";
    String payday = "";
    String test_day = "";
    String restday = "";
    String accept_state = "";
    String placeAddress_get = "";
    boolean registrTF = false;

    String name = "";
    String registr_num = "";
    String store_kind = "";
    String address = "";
    String address_detail = "";
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
    String SSIDName = "";
    String GetTime = "";

    List<String> boheom = new ArrayList<>();
    //--매장 정보 수정할때

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

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            place_id = shardpref.getString("place_id", "-99");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "-99");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "-99");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-99");

            dlog.i("place_id : " + place_id);
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("USER_INFO_EMAIL : " + USER_INFO_EMAIL);
            gpsTracker = new GpsTracker(mContext);
            geocoder = new Geocoder(mContext);

            setBtnEvent();
            if (USER_INFO_EMAIL.equals("0")) {
                Toast.makeText(mContext, "사용자 정보를 가져오지 못했습니다.\n다시 로그인하세요.", Toast.LENGTH_SHORT).show();
                pm.Login(mContext);
            } else {
                UserCheck(USER_INFO_EMAIL);
            }
            if (USER_INFO_AUTH.equals("1")) {
                binding.area02.setVisibility(View.GONE);

            }
            getPlaceContents();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    boolean boheom01TF = false;
    boolean boheom02TF = false;
    boolean boheom03TF = false;
    String Time01 = "-99";
    String Time02 = "-99";

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            pm.PlaceList(mContext);
        });


        binding.addPlaceBtn.setOnClickListener(v -> {
            if (CheckData()) {
                AddPlace(1);
            }
        });
        binding.save2btn.setOnClickListener(v -> {
            if (CheckData()) {
                AddPlace(0);
            }
        });

        binding.searchLocation.setOnClickListener(v -> {
            Intent i = new Intent(this, PinSelectLocationActivity.class);
            startActivityForResult(i, PINSELECT_LOCATION_ACTIVITY);
            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        });

        //사업자번호 체크
        binding.confirmRegistrnum.setOnClickListener(v -> {
            if(binding.inputbox02.getText().toString().isEmpty() || binding.inputbox02.getText().toString().equals("")){
                Toast_Nomal("사업자 번호가 입력되지 않았습니다.");
            }else{
                SearchRestrnum(binding.inputbox02.getText().toString());
            }
        });

        //매장분류
        binding.inputbox03.setOnClickListener(v -> {
            dlog.i("area03 click!");
            StoreDivisionPopActivity storedivi = new StoreDivisionPopActivity();
            storedivi.show(getSupportFragmentManager(), "StoreDivisionPopActivity");
            storedivi.setOnItemClickListener((v1, category) -> binding.inputbox03.setText(category));
        });
        //급여정산일
        binding.inputbox05.setOnClickListener(v -> {

        });

        binding.boheomarea01.setOnClickListener(v -> {
            if (!boheom01TF) {
                boheom01TF = true;
                boheom03TF = false;
                boheom.add("4대보험");
                boheom.remove("없음");
                binding.boheom01.setBackgroundResource(R.drawable.select_full_round);
                binding.boheom03.setBackgroundResource(R.drawable.select_empty_round);
            } else {
                boheom01TF = false;
                boheom.remove("4대보험");
                binding.boheom01.setBackgroundResource(R.drawable.select_empty_round);
            }
        });

        binding.boheomarea02.setOnClickListener(v -> {
            if (!boheom02TF) {
                boheom02TF = true;
                boheom03TF = false;
                boheom.add("3.3%소득세");
                boheom.remove("없음");
                binding.boheom02.setBackgroundResource(R.drawable.select_full_round);
                binding.boheom03.setBackgroundResource(R.drawable.select_empty_round);
            } else {
                boheom02TF = false;
                boheom.remove("3.3%소득세");
                binding.boheom02.setBackgroundResource(R.drawable.select_empty_round);
            }
        });

        binding.boheomarea03.setOnClickListener(v -> {
            if (!boheom03TF) {
                boheom01TF = false;
                boheom02TF = false;
                boheom03TF = true;
                boheom.clear();
                boheom.add("없음");
                binding.boheom01.setBackgroundResource(R.drawable.select_empty_round);
                binding.boheom02.setBackgroundResource(R.drawable.select_empty_round);
                binding.boheom03.setBackgroundResource(R.drawable.select_full_round);
            } else {
                boheom03TF = false;
                boheom.clear();
                binding.boheom03.setBackgroundResource(R.drawable.select_empty_round);
            }
        });

        binding.inputbox08.setOnClickListener(v -> {
            WorkTimePicker wtp = new WorkTimePicker();
            wtp.show(getSupportFragmentManager(),"WorkTimePicker");
            wtp.setOnClickListener(new WorkTimePicker.OnClickListener() {
                @Override
                public void onClick(View v, String hour, String min) {
                    Time01 = String.valueOf(hour).length() == 1 ? "0" + String.valueOf(hour) : String.valueOf(hour);
                    Time02 = String.valueOf(min).length() == 1 ? "0" + String.valueOf(min) : String.valueOf(min);
                    shardpref.remove("timeSelect_flag");
                    shardpref.remove("hourOfDay");
                    shardpref.remove("minute");
                    GetTime = Time01 + ":" + Time02;
                    shardpref.putString("input_pop_time",GetTime);
                    if (!hour.equals("0")) {
                        binding.inputbox08.setText(GetTime);
                    }
                }
            });
//            Intent intent = new Intent(this, WorkTimePicker.class);
//            intent.putExtra("timeSelect_flag", 4);
//            startActivity(intent);
//            overridePendingTransition(R.anim.translate_up, 0);

        });
        binding.inputbox09.setOnClickListener(v -> {
            WorkTimePicker wtp = new WorkTimePicker();
            wtp.show(getSupportFragmentManager(),"WorkTimePicker");
            wtp.setOnClickListener(new WorkTimePicker.OnClickListener() {
                @Override
                public void onClick(View v, String hour, String min) {
                    Time01 = String.valueOf(hour).length() == 1 ? "0" + String.valueOf(hour) : String.valueOf(hour);
                    Time02 = String.valueOf(min).length() == 1 ? "0" + String.valueOf(min) : String.valueOf(min);
                    shardpref.remove("timeSelect_flag");
                    shardpref.remove("hourOfDay");
                    shardpref.remove("minute");
                    GetTime = Time01 + ":" + Time02;
                    shardpref.putString("input_pop_time",GetTime);
                    if (!hour.equals("0")) {
                        binding.inputbox09.setText(GetTime);
                    }
                }
            });

        });
    }

    private void getPlaceContents() {
        dlog.i("-----getPlaceContents-----");
        dlog.i("place_id : " + place_id);
        dlog.i("owner_id : " + USER_INFO_ID);
        dlog.i("-----getPlaceContents-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(jsonResponse);
                                name = Response.getJSONObject(0).getString("name");
                                registr_num = Response.getJSONObject(0).getString("registr_num");
                                store_kind = Response.getJSONObject(0).getString("store_kind");
                                address = Response.getJSONObject(0).getString("address");
                                address_detail = Response.getJSONObject(0).getString("address_detail");
                                latitude = Response.getJSONObject(0).getString("latitude");
                                longitude = Response.getJSONObject(0).getString("longitude");

                                pay_day = Response.getJSONObject(0).getString("pay_day");

                                test_period = Response.getJSONObject(0).getString("test_period");
                                test_day = Response.getJSONObject(0).getString("test_period");

                                vacation_select = Response.getJSONObject(0).getString("vacation_select");
                                restday = Response.getJSONObject(0).getString("vacation_select");

                                insurance = Response.getJSONObject(0).getString("insurance");

                                start_time = Response.getJSONObject(0).getString("start_time");
                                place_starttime = Response.getJSONObject(0).getString("start_time");
                                end_time = Response.getJSONObject(0).getString("end_time");
                                place_endtime = Response.getJSONObject(0).getString("end_time");

                                save_kind = Response.getJSONObject(0).getString("save_kind");
                                img_path = Response.getJSONObject(0).getString("img_path").equals("null") ? "" : Response.getJSONObject(0).getString("img_path");
                                start_date = Response.getJSONObject(0).getString("start_date");
                                SSIDName = Response.getJSONObject(0).getString("wifi_name");
                                binding.inputbox01.setText(name);
                                binding.inputbox02.setText(registr_num);
                                binding.inputbox03.setText(store_kind);
                                binding.inputbox04.setText(address);
                                binding.inputbox041.setText(address_detail);
                                binding.inputbox07.setText(vacation_select);
                                dlog.i("insurance : " + insurance.replace(" ", ""));

                                for (String str : insurance.replace(" ", "").split(",")) {
                                    boheom.add(str.replace(" ", ""));
                                    if (str.contains("4대보험")) {
                                        boheom01TF = true;
                                        boheom03TF = false;
                                        binding.boheom01.setBackgroundResource(R.drawable.resize_service_on);
                                        binding.boheom03.setBackgroundResource(R.drawable.select_empty_round);
                                    } else if (str.contains("3.3%소득세")) {
                                        boheom02TF = true;
                                        boheom03TF = false;
                                        binding.boheom02.setBackgroundResource(R.drawable.resize_service_on);
                                        binding.boheom03.setBackgroundResource(R.drawable.select_empty_round);
                                    } else if (str.contains("없음")) {
                                        boheom03TF = true;
                                        boheom01TF = false;
                                        boheom02TF = false;
                                        binding.boheom01.setBackgroundResource(R.drawable.select_empty_round);
                                        binding.boheom02.setBackgroundResource(R.drawable.select_empty_round);
                                        binding.boheom03.setBackgroundResource(R.drawable.resize_service_on);
                                    }
                                }
                                binding.inputbox08.setText(start_time);
                                binding.inputbox09.setText(end_time);
                                spinnerSetData();
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

    private void spinnerSetData() {

        /*급여 정산날짜*/
        binding.inputbox05.setText(pay_day);
        binding.inputbox05.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pay_day = s.toString();
            }
        });

        /*수습기간*/
        binding.inputbox06.setText(test_day);
        binding.inputbox06.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                test_day = s.toString();
            }
        });


        /*휴가*/
        binding.inputbox07.setOnClickListener(v -> {
            shardpref.putInt("SelectKind", 4);
            SelectStringBottomSheet ssb = new SelectStringBottomSheet();
            ssb.show(getSupportFragmentManager(), "selectVacation");
            ssb.setOnItemClickListener(new SelectStringBottomSheet.OnItemClickListener() {
                @Override
                public void onItemClick(View v, String category) {
                    binding.inputbox07.setText(category);
                }
            });
        });
        binding.downArrow.setOnClickListener(v -> {
            shardpref.putInt("SelectKind", 4);
            SelectStringBottomSheet ssb = new SelectStringBottomSheet();
            ssb.show(getSupportFragmentManager(), "selectVacation");
            ssb.setOnItemClickListener(new SelectStringBottomSheet.OnItemClickListener() {
                @Override
                public void onItemClick(View v, String category) {
                    binding.inputbox07.setText(category);
                }
            });
        });
    }

    public void UserCheck(String account) {
        dlog.i("UserCheck account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSelectInterface api = retrofit.create(UserSelectInterface.class);
        Call<String> call = api.getData(account);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("UserCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    String id = Response.getJSONObject(0).getString("id");
                                    String name = Response.getJSONObject(0).getString("name");
                                    String img_path = Response.getJSONObject(0).getString("img_path");

                                    try {
                                        dlog.i("------UserCheck-------");
                                        dlog.i("프로필 사진 url : " + img_path);
                                        dlog.i("이메일 : " + account);
                                        dlog.i("성명 : " + name);
                                        dlog.i("------UserCheck-------");
                                    } catch (Exception e) {
                                        dlog.i("UserCheck Exception : " + e);
                                    }
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

    public void Registrnum_Confirm(String num) {
        dlog.i("Registrnum_Confirm num : " + num);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConfrimNumInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ConfrimNumInterface api = retrofit.create(ConfrimNumInterface.class);
        Call<String> call = api.getData(num);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(jsonResponse.replace("\"","").equals("success")){
                                    binding.registrNumState.setText("정상적으로 등록된 사업자 번호입니다.");
                                    binding.registrNumState.setTextColor(R.color.blue);
                                    registrTF = true;
                                    binding.inputbox02.setTextColor(R.color.blue);
                                }else{
                                    if(!registr_num.isEmpty() && registr_num.equals(num)){
                                        binding.registrNumState.setText("정상적으로 등록된 사업자 번호입니다.");
                                        binding.registrNumState.setTextColor(R.color.blue);
                                        registrTF = true;
                                        binding.inputbox02.setTextColor(R.color.blue);
                                    }else{
                                        binding.registrNumState.setText("중복된 사업자 번호 입니다.");
                                        binding.registrNumState.setTextColor(R.color.red);
                                        registrTF = false;
                                        binding.inputbox02.setTextColor(R.color.red);
                                    }
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

    private void SearchRestrnum(String registr_num) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RegistrSearchInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        RegistrSearchInterface api = retrofit.create(RegistrSearchInterface.class);
        Call<String> call = api.getData(registr_num);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dlog.e("SearchRestrnum function START");
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(String.valueOf(response.body()));
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        b_stt = Response.getJSONObject(0).getString("b_stt");
                        tax_type = Response.getJSONObject(0).getString("tax_type");
                        dlog.i("response.body() : " + response.body());
                        dlog.i("Response : " + Response);
                        dlog.i("b_no : " + Response.getJSONObject(0).getString("b_no"));
                        dlog.i("b_stt : " + Response.getJSONObject(0).getString("b_stt"));
                        dlog.i("b_stt_cd : " + Response.getJSONObject(0).getString("b_stt_cd"));
                        dlog.i("tax_type : " + Response.getJSONObject(0).getString("tax_type"));
                        dlog.i("tax_type_cd : " + Response.getJSONObject(0).getString("tax_type_cd"));
                        dlog.i("end_dt : " + Response.getJSONObject(0).getString("end_dt"));
                        dlog.i("utcc_yn : " + Response.getJSONObject(0).getString("utcc_yn"));
                        dlog.i("tax_type_change_dt : " + Response.getJSONObject(0).getString("tax_type_change_dt"));
                        dlog.i("invoice_apply_dt : " + Response.getJSONObject(0).getString("invoice_apply_dt"));

                        if (tax_type.equals("국세청에 등록되지 않은 사업자등록번호입니다.")) {
                            binding.registrNumState.setText("국세청에 등록되지 않은 사업자등록번호입니다.");
                            binding.registrNumState.setTextColor(R.color.red);
                            registrTF = false;
                            binding.inputbox02.setTextColor(R.color.red);
                        } else {
                            Registrnum_Confirm(registr_num);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }

        });
    }


    private boolean CheckData() {
        dlog.i("----------CheckData----------");
        placeName = binding.inputbox01.getText().toString();
        placeAddress = binding.inputbox04.getText().toString();
        placeDtailAddress = binding.inputbox041.getText().toString();
        payday = binding.inputbox05.getText().toString();
        test_day = binding.inputbox06.getText().toString();
        restday = binding.inputbox07.getText().toString();
        placeAddress_get = placeAddress + " " + placeDtailAddress;
        registr_num = binding.inputbox02.getText().toString();
        accept_state = binding.inputbox03.getText().toString();

        SearchRestrnum(binding.inputbox02.getText().toString());

        if (boheom.size() == 0) {
            boheom.add("없음");
        }
        dlog.i("매장이미지 : " + ProfileUrl);
        dlog.i("매장명 : " + placeName);
        dlog.i("매장주소 : " + placeAddress);
        dlog.i("매장 상세주소 : " + placeDtailAddress);
        dlog.i("매장분류 : " + accept_state);
        dlog.i("위도 : " + latitude);
        dlog.i("경도 : " + longitude);
        dlog.i("급여정산일 : " + payday);
        dlog.i("수습기간 : " + test_day);
        dlog.i("휴가기간 : " + restday);
        dlog.i("보험 : " + boheom);
        dlog.i("주 운영 시작시간 : " + place_starttime);
        dlog.i("주 운영 마감시간 : " + place_endtime);
        dlog.i("----------CheckData----------");

        if (placeName.isEmpty()) {
            Toast.makeText(mContext, "매장 명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (placeAddress.isEmpty()) {
            Toast.makeText(mContext, "매장 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (payday.isEmpty()) {
            Toast.makeText(mContext, "급여정산일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (test_day.isEmpty()) {
            Toast.makeText(mContext, "수습기간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (restday.isEmpty()) {
            Toast.makeText(mContext, "휴가기간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (place_starttime.isEmpty()) {
            Toast.makeText(mContext, "주 운영 시작시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (place_endtime.isEmpty()) {
            Toast.makeText(mContext, "주 운영 마감시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (USER_INFO_ID.equals("0")) {
            Toast.makeText(mContext, "사용자 정보를 가져올수 없습니다.", Toast.LENGTH_SHORT).show();
            pm.Login(mContext);
            return false;
        } else {
            return true;
        }
    }

    public void AddPlace(int i) {
        //i = 0:임시저장 / 1:저장
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceEditInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceEditInterface api = retrofit.create(PlaceEditInterface.class);
        Call<String> call = api.getData(place_id, placeName, registr_num, accept_state, placeAddress, placeDtailAddress
                , String.valueOf(latitude), String.valueOf(longitude), payday, test_day, restday
                , (String.valueOf(boheom).replace("[", "").replace("]", "")), place_starttime, place_endtime, ProfileUrl, String.valueOf(i)
                , SSIDName);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {

                                if (!jsonResponse.equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
//                                    if (saveBitmap != null) {
//                                        saveBitmapAndGetURI();
//                                    }
                                    if (i == 0) {
                                        Toast_Nomal("임시저장 완료되었습니다.");
                                    }
                                    pm.PlaceEdit2Go(mContext);
//                                    pm.PlaceList(mContext);
                                } else if (!jsonResponse.equals("[]") && jsonResponse.replace("\"", "").equals("duplicate")) {
                                    Toast_Nomal("중복되는 데이터가 있습니다.");
                                } else {
                                    Toast_Nomal("추가 매장을 생성하지 못했습니다.");
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
    public void onResume() {
        super.onResume();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        dlog.i("onResume Area");
        String getlatitude = shardpref.getString("pin_latitude", "0.0");
        String getlongitube = shardpref.getString("pin_longitube", "0.0");

        String getzipcode = shardpref.getString("pin_zipcode", "");
        String getstore_address = shardpref.getString("pin_store_address", "");
        String getstore_addressdetail = shardpref.getString("pin_store_addressdetail", "");

        if (!getstore_address.isEmpty()) {
            binding.inputbox04.setText(getstore_address);
            binding.inputbox041.setText(getstore_addressdetail);

            latitude = getlatitude;
            longitude = getlongitube;

            shardpref.remove("pin_store_address");
            shardpref.remove("pin_zipcode");
            shardpref.remove("pin_store_addressdetail");
            shardpref.remove("pin_latitude");
            shardpref.remove("pin_longitube");
        }

    }


    @Override
    public void onBackPressed() {
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
                    ProfileUrl = "http://krafte.net/NEBWorks/image/place_img/" + file_name;
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
                    binding.inputbox04.setText(GetData.substring(7));
//                    latitude = findGeoPoint_lat(mContext, binding.inputbox04.getText().toString() + " " + PlaceAddressDetail);
//                    longitube = findGeoPoint_lon(mContext, binding.inputbox04.getText().toString() + " " + PlaceAddressDetail);
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

            ProfileUrl = "http://krafte.net/NEBWorks/image/place_img/" + file_name;
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
        private static final String BASE_URL = "http://krafte.net/NEBWorks/image/";
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
