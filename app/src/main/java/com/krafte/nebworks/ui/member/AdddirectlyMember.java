package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.PlaceMemberAddDirectlyInterface;
import com.krafte.nebworks.databinding.ActivityAdddirectlyMemberBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 2022 11 03 직원 직접 등록 기능 페이지
 */

public class AdddirectlyMember extends AppCompatActivity {
    private ActivityAdddirectlyMemberBinding binding;
    private final static String TAG = "AdddirectlyMember";
    Context mContext;

    PreferenceHelper shardpref;

    //Shared
    String place_id = "";
    String USER_INFO_NAME = "";
    String USER_INFO_PHONE = "";
    String USER_LOGIN_METHOD = "";

    //other
    boolean check = false;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    DatePickerDialog datePickerDialog;

    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    int Hour = 0;
    int Minute = 0;
    int Sec = 0;
    String getDatePicker = "";
    String getYMPicker = "";

    int textlength01 = 0;
    int textlength02 = 0;

    //Parameter
    String name = "";
    String phone = "";
    String Jumin = "";
    String JoinDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_delete);
        binding = ActivityAdddirectlyMemberBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        place_id = shardpref.getString("place_id", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
        Log.i(TAG, "USER_INFO_NAME = " + USER_INFO_NAME);
        Log.i(TAG, "USER_INFO_PHONE = " + USER_INFO_PHONE);
        Log.i(TAG, "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        binding.inputbox04.setText(mYear + "-" + (String.valueOf(mMonth).length() == 1?"0"+mMonth:mMonth) + "-"
                + (String.valueOf(mDay).length() == 1?"0"+String.valueOf(mDay):String.valueOf(mDay)));

        datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month + 1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length() == 1 ? "0" + Day : Day;
                Month = Month.length() == 1 ? "0" + Month : Month;
                binding.inputbox04.setText(year + "-" + Month + "-" + Day);
                getYMPicker = binding.inputbox04.getText().toString().substring(0, 7);
            }
        }, mYear, mMonth, mDay);

        binding.inputbox01.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
            }
        });
        binding.inputbox02.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.inputbox02.isFocusable() && !s.toString().equals("")) {
                    try{
                        textlength01 = binding.inputbox02.getText().toString().length();
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        return;
                    }

                    if (textlength01 == 3 && before != 1) {
                        binding.inputbox02.setText(binding.inputbox02.getText().toString()+"-");
                        binding.inputbox02.setSelection(binding.inputbox02.getText().length());
                    }else if (textlength01 == 8 && before != 1){
                        binding.inputbox02.setText(binding.inputbox02.getText().toString()+"-");
                        binding.inputbox02.setSelection(binding.inputbox02.getText().length());
                    }else if(textlength01 == 9 && !binding.inputbox02.getText().toString().contains("-")){
                        binding.inputbox02.setText(binding.inputbox02.getText().toString().substring(0,3)+"-"+binding.inputbox02.getText().toString().substring(4,8)+"-"+binding.inputbox02.getText().toString().substring(9,11));
                        binding.inputbox02.setSelection(binding.inputbox02.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString().replace("-","");
            }
        });
        binding.inputbox03.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.inputbox03.isFocusable() && !s.toString().equals("")) {
                    try{
                        textlength02 = binding.inputbox03.getText().toString().length();
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        return;
                    }

                    if (textlength02 == 6 && before != 1) {
                        binding.inputbox03.setText(binding.inputbox03.getText().toString()+"-");
                        binding.inputbox03.setSelection(binding.inputbox03.getText().length());
                    }else if (textlength02 == 15 && before != 1){
                        binding.inputbox03.setText(binding.inputbox03.getText().toString().substring(0,6)+"-"+binding.inputbox03.getText().toString().substring(7,14));
                        binding.inputbox03.setSelection(binding.inputbox03.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Jumin = s.toString().replace("-","");
            }
        });

    }


    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

        binding.addMemberBtn.setOnClickListener(v -> {
            if (SaveCheck()) {
                dlog.i("addMemberBtn SaveCheck" + SaveCheck());
                AddPlaceMember();
            }
        });

        binding.inputbox04.setOnClickListener(v -> {
            datePickerDialog.show();
        });
    }

    private boolean SaveCheck() {
        JoinDate = binding.inputbox04.getText().toString();
        if(place_id.isEmpty()){
            Toast.makeText(mContext,"매장 ID가 저장되어있지 않습니다, 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
            pm.PlaceList(mContext);
            return false;
        }else if(name.isEmpty()){
            Toast.makeText(mContext,"이름을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(phone.isEmpty()){
            Toast.makeText(mContext,"전화번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(Jumin.isEmpty()){
            Toast.makeText(mContext,"주민번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else if(JoinDate.isEmpty()){
            Toast.makeText(mContext,"입사일자를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            dlog.i("------SaveCheck------");
            dlog.i("place_id : " + place_id);
            dlog.i("name : " + name);
            dlog.i("phone : " + phone);
            dlog.i("Jumin : " + Jumin);
            dlog.i("JoinDate : " + JoinDate);
            dlog.i("------SaveCheck------");
            return true;
        }
    }

    RetrofitConnect rc = new RetrofitConnect();
    public void AddPlaceMember() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberAddDirectlyInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberAddDirectlyInterface api = retrofit.create(PlaceMemberAddDirectlyInterface.class);
        Call<String> call = api.getData(place_id, "-99",name,phone,Jumin,"2",JoinDate);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            if (jsonResponse.replace("\"", "").equals("success")) {
                                dlog.i("매장 멤버 추가 완료");
                                Toast_Nomal("직원이 정상적으로 등록되었습니다.");
                                pm.MemberManagement(mContext);
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
    public void onBackPressed() {
        super.onBackPressed();
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
}
