package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.ContractGetAllInterface;
import com.krafte.nebworks.dataInterface.ContractWorkerInterface;
import com.krafte.nebworks.dataInterface.ContractidInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd07Binding;
import com.krafte.nebworks.pop.WriteAddressActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddContractPage07 extends AppCompatActivity {
    private ActivityContractAdd07Binding binding;
    private final static String TAG = "AddContractPage07";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String worker_name = "";
    String worker_phone = "";
    String worker_email = "";

    String USER_INFO_ID = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    //Param
    String name = "";
    String jumin = "";
    String address = "";
    String address_detail = "";
    String phone = "";
    String email = "";
    String zipcode = "";
    String contract_place_id = "";
    String contract_user_id = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContractAdd07Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        //Singleton Area

        //shardpref Area
        shardpref           = new PreferenceHelper(mContext);
        worker_id           = shardpref.getString("worker_id","0");
        worker_name         = shardpref.getString("worker_name","0");
        worker_phone        = shardpref.getString("worker_phone","0");
        worker_email        = shardpref.getString("worker_email","0");

        contract_id         = shardpref.getString("contract_id","0");
        contract_place_id   = shardpref.getString("contract_place_id","0");
        contract_user_id    = shardpref.getString("contract_user_id","0");
        place_id            = shardpref.getString("place_id","0");
        USER_INFO_ID        = shardpref.getString("USER_INFO_ID","0");

        setBtnEvent();
    }

    int textlength01 = 0;
    @Override
    public void onDestroy(){
        super.onDestroy();
        shardpref.remove("pin_store_address");
        shardpref.remove("pin_zipcode");
        shardpref.remove("pin_store_addressdetail");
        shardpref.remove("pin_latitude");
        shardpref.remove("pin_longitube");
    }

    @Override
    public void onResume(){
        super.onResume();
        getContractId();
        UserCheck();
        String pin_store_address = shardpref.getString("pin_store_address", "");
        String pin_store_addressdetail = shardpref.getString("pin_store_addressdetail", "");
        zipcode = shardpref.getString("pin_zipcode", "");
        if(!pin_store_address.isEmpty()){
            String location = pin_store_address + " " + pin_store_addressdetail;
            GeoCoding(location);
            dlog.i("RESULT_OK 1 : " + pin_store_address);
            dlog.i("RESULT_OK 2 : " + pin_store_addressdetail);
            binding.input03.setText(pin_store_address);
            binding.input04.setText(pin_store_addressdetail);
        }
        GetWorkerContract();
    }

    //지오코딩 ( 주소 >> 위도경도 )
    List<Address> list = new ArrayList<>();
    double latitude = 0;
    double longitude = 0;
    public void GeoCoding(String getLocation) {
        Geocoder geocoder = new Geocoder(mContext);
        try {
            list = geocoder.getFromLocationName(getLocation, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            String city = "";
            String country = "";
            Address address = list.get(0);
            double lat = address.getLatitude();
            double lon = address.getLongitude();
            dlog.i("GeoCoding : lat["+lat+"] / lon["+lon+"]");

            latitude = lat;
            longitude = lon;
        }
    }

    private void setBtnEvent(){
        binding.next.setOnClickListener(v -> {
           if(DataCheck()){
               SaveWorkerInfo();
           }
        });
        binding.searchBtn.setOnClickListener(v -> {
//            Intent i = new Intent(this, WebViewActivity.class);
//            startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
            Intent intent = new Intent(mContext, WriteAddressActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });
        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("progress_pos");
            if(!shardpref.getString("progress_pos","").isEmpty()){
                pm.ContractFragment(mContext);
            }else{
                super.onBackPressed();
            }
        });

        binding.input02.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.input02.isFocusable() && !s.toString().equals("")) {
                    try {
                        textlength01 = binding.input02.getText().toString().length();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (textlength01 == 6 && before != 1) {
                        binding.input02.setText(binding.input02.getText().toString() + "-");
                        binding.input02.setSelection(binding.input02.getText().length());
                    } else if (textlength01 == 8 && !binding.input02.getText().toString().contains("-")) {
                        binding.input02.setText(binding.input02.getText().toString().substring(0, 6) + "-" + binding.input02.getText().toString().substring(7, 13));
                        binding.input02.setSelection(binding.input02.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //이미지 업로드에 필요한 소스 START
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ADDRESS_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String GetData = data.getExtras().getString("data");
                if (GetData != null) {
//                         data의 정보를 각각 우편번호와 실주소로 나누어 EditText에 표시
                    dlog.i("RESULT_OK 1 : " + GetData.substring(0, 5));
                    dlog.i("RESULT_OK 2 : " + GetData.substring(7));
//                    zipcode = GetData.substring(0, 5);
//                    binding.input03.setText(GetData.substring(0, 5));
                    binding.input03.setText(GetData.substring(7));
                }
            }
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    public void UserCheck() {
        try{
            worker_name         = shardpref.getString("worker_name","0");
            worker_phone        = shardpref.getString("worker_phone","0");
            worker_email        = shardpref.getString("worker_email","0");

            binding.input01.setText(worker_name.equals("null")?"":worker_name);
            binding.input05.setText(worker_phone.equals("null")?"":worker_phone);
            binding.input06.setText(worker_email.equals("null")?"":worker_email);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean DataCheck(){
        name = binding.input01.getText().toString();
        jumin = binding.input02.getText().toString();
        address = binding.input03.getText().toString();
        address_detail = binding.input04.getText().toString();
        phone = binding.input05.getText().toString();
        email = binding.input06.getText().toString();

        dlog.i("-----DataCheck-----");
        dlog.i("name : "            + name);
        dlog.i("jumin : "           + jumin);
        dlog.i("address : "         + address);
        dlog.i("address_detail : "  + address_detail);
        dlog.i("phone : "           + phone);
        dlog.i("email : "           + email);
        dlog.i("-----DataCheck-----");
        if(name.isEmpty()){
            Toast_Nomal("근무자 이름을 입력해주세요.");
            return false;
        } else if(jumin.isEmpty()){
            Toast_Nomal("근무자 주민번호를 입력해주세요.");
            return false;
        } else if(address.isEmpty()){
            Toast_Nomal("근무자 주소를 입력해주세요.");
            return false;
        } else if(phone.isEmpty()){
            Toast_Nomal("근무자 휴대폰 번호를 입력해주세요.");
            return false;
        } else if(email.isEmpty()){
            Toast_Nomal("근무자 이메일을 입력해주세요.");
            return false;
        } else{
            return true;
        }
    }

    private void SaveWorkerInfo(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractWorkerInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractWorkerInterface api = retrofit.create(ContractWorkerInterface.class);
        Call<String> call = api.getData(contract_id, name, jumin, address, address_detail, phone, email);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("SaveWorkPartTime Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(jsonResponse.replace("\"","").equals("success")){
                                    shardpref.putString("contract_email",email);
                                    pm.AddContractPage08(mContext);
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
    public void onBackPressed(){

        if(!shardpref.getString("progress_pos","").isEmpty()){
            pm.ContractFragment(mContext);
        }else{
            super.onBackPressed();
        }
        shardpref.remove("progress_pos");
    }

    public void Toast_Nomal(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup)findViewById(R.id.toast_layout));
        TextView toast_textview  = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

    String contract_id = "";
    public void getContractId() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractidInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractidInterface api = retrofit.create(ContractidInterface.class);
        Call<String> call = api.getData(place_id, worker_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("SaveWorkPartTime Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        try {
                            JSONArray Response = new JSONArray(response.body());
                            contract_id = Response.getJSONObject(0).getString("id");
                        } catch (Exception e) {
                            e.printStackTrace();
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


    String id = "";
    String worker_jumin = "";
    String worker_address = "";
    String worker_address_detail = "";


    private void GetWorkerContract(){
        dlog.i("------GetAllContract------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("------GetAllContract------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractGetAllInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractGetAllInterface api = retrofit.create(ContractGetAllInterface.class);
        Call<String> call = api.getData(contract_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetAllContract jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetAllContract jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    id                      = Response.getJSONObject(0).getString("id");
                                    worker_name             = Response.getJSONObject(0).getString("worker_name").equals("null")?"":Response.getJSONObject(0).getString("worker_name");
                                    worker_jumin            = Response.getJSONObject(0).getString("worker_jumin").equals("null")?"":Response.getJSONObject(0).getString("worker_jumin");
                                    worker_address          = Response.getJSONObject(0).getString("worker_address").equals("null")?"":Response.getJSONObject(0).getString("worker_address");
                                    worker_address_detail   = Response.getJSONObject(0).getString("worker_address_detail").equals("null")?"":Response.getJSONObject(0).getString("worker_address_detail");
                                    worker_phone            = Response.getJSONObject(0).getString("worker_phone").equals("null")?"":Response.getJSONObject(0).getString("worker_phone");
                                    worker_email            = Response.getJSONObject(0).getString("worker_email").equals("null")?"":Response.getJSONObject(0).getString("worker_email");

                                    if(!worker_jumin.equals("")){
                                        binding.input02.setText(worker_jumin);
                                    } else if(!worker_address.equals("")){
                                        binding.input03.setText(worker_address);
                                    } else if(!worker_address_detail.equals("")){
                                        binding.input04.setText(worker_address_detail);
                                    }
                                }
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure (@NonNull Call< String > call, @NonNull Throwable t){
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

}
