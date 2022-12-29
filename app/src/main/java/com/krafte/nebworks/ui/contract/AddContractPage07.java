package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.ContractPagePosUp;
import com.krafte.nebworks.dataInterface.ContractWorkerInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd07Binding;
import com.krafte.nebworks.ui.WebViewActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
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

public class AddContractPage07 extends AppCompatActivity {
    private ActivityContractAdd07Binding binding;
    private final static String TAG = "AddContractPage07";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";
    String contract_id = "";

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
        shardpref       = new PreferenceHelper(mContext);
        place_id        = shardpref.getString("place_id","0");
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID","0");
        worker_id       = shardpref.getString("worker_id","0");
        contract_id     = shardpref.getString("contract_id","0");
        contract_place_id   = shardpref.getString("contract_place_id","0");
        contract_user_id    = shardpref.getString("contract_user_id","0");

        setBtnEvent();
        dlog.i("contract_place_id : " + contract_place_id);
        dlog.i("contract_user_id : " + contract_user_id);
    }

    @Override
    public void onResume(){
        super.onResume();
        UserCheck();
    }

    private void setBtnEvent(){
        binding.next.setOnClickListener(v -> {
           if(DataCheck()){
               SaveWorkerInfo();
           }
        });
        binding.searchBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
        });
        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("progress_pos");
            if(!shardpref.getString("progress_pos","").isEmpty()){
                pm.ContractFragment(mContext);
            }else{
                super.onBackPressed();
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
        dlog.i("---------UserCheck---------");
        dlog.i("---------UserCheck---------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AllMemberInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        AllMemberInterface api = retrofit.create(AllMemberInterface.class);
        Call<String> call = api.getData(contract_place_id,contract_user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("UserCheck function START");
                dlog.e("response 1: " + response.isSuccessful());
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            try {
                                if(Response.length() != 0){
                                    String name     = Response.getJSONObject(0).getString("name");
                                    String phone    = Response.getJSONObject(0).getString("phone");
                                    String account  = Response.getJSONObject(0).getString("account");
                                    binding.input01.setText(name);
                                    binding.input05.setText(phone);
                                    binding.input06.setText(account);
                                }
                            } catch (Exception e) {
                                dlog.i("UserCheck Exception : " + e);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
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
                                if(!jsonResponse.equals("null") || !jsonResponse.equals("[]") || !jsonResponse.isEmpty()){
                                    shardpref.putString("contract_email",email);
                                    UpdatePagePos(contract_id);
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

    private void UpdatePagePos(String contract_id){
        dlog.i("------UpdatePagePos------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("------UpdatePagePos------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractPagePosUp.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractPagePosUp api = retrofit.create(ContractPagePosUp.class);
        Call<String> call = api.getData(contract_id,"5");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {

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
