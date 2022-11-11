package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.GetConfirmPlaceInterface;
import com.krafte.nebworks.dataInterface.PlaceMemberAddInterface;
import com.krafte.nebworks.dataInterface.UserNumSelectInterface;
import com.krafte.nebworks.databinding.ActivityInviteMemberBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class InviteMemberActivity extends AppCompatActivity {
    private ActivityInviteMemberBinding binding;
    private final static String TAG = "InviteMemberActivity";
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

    String INPUT_NAME = "";
    String INPUT_PHONE = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteMemberBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        try{
            mContext = this;
            setBtnEvent();
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            place_id = shardpref.getString("place_id", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
            USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
            Log.i(TAG, "USER_INFO_NAME = " + USER_INFO_NAME);
            Log.i(TAG, "USER_INFO_PHONE = " + USER_INFO_PHONE);
            Log.i(TAG, "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

            binding.inputbox01.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    INPUT_NAME = s.toString();
                }
            });
            binding.inputbox02.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    INPUT_PHONE = s.toString();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void setBtnEvent() {
        binding.addMemberBtn.setOnClickListener(v -> {
            UserCheck();
        });
    }

    public void UserCheck() {
        dlog.i("------UserCheck------");
        dlog.i("INPUT_NAME : " + INPUT_NAME);
        dlog.i("INPUT_PHONE : " + INPUT_PHONE);
        dlog.i("------UserCheck------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserNumSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserNumSelectInterface api = retrofit.create(UserNumSelectInterface.class);
        Call<String> call = api.getData(INPUT_NAME, INPUT_PHONE);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    String id = Response.getJSONObject(0).getString("id");
                                    String name = Response.getJSONObject(0).getString("name");
                                    String phone = Response.getJSONObject(0).getString("phone");
                                    String join_date = "";

                                    Calendar c = Calendar.getInstance();
                                    int mYear = c.get(Calendar.YEAR);
                                    int mMonth = c.get(Calendar.MONTH);
                                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                                    join_date = mYear + "-" + (String.valueOf(mMonth).length() == 1?"0"+mMonth:mMonth) + "-"
                                            + (String.valueOf(mDay).length() == 1?"0"+String.valueOf(mDay):String.valueOf(mDay));

                                    if(ConfrimPlaceMember(id)){
                                        AddPlaceMember(id, name, phone, "", join_date);
                                    }else{
                                        Toast_Nomal("이미 직원으로 등록된 사용자 입니다.");
                                    }
                                } else {
                                    dlog.i("Response 2: " + response.body().length());
                                    Toast_Nomal("가입하지 않은 사용자입니다.");
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

    int cnt = 0;
    private boolean ConfrimPlaceMember(String user_id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetConfirmPlaceInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        GetConfirmPlaceInterface api = retrofit.create(GetConfirmPlaceInterface.class);
        Call<String> call = api.getData(place_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    cnt = Integer.parseInt(Response.getJSONObject(0).getString("cnt"));
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
        return cnt == 0;
    }
    public void AddPlaceMember(String user_id, String name, String phone, String Jumin, String JoinDate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberAddInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberAddInterface api = retrofit.create(PlaceMemberAddInterface.class);
        Call<String> call = api.getData(place_id, user_id,name,phone,Jumin,"0",JoinDate);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("AddPlaceMember jsonResponse length : " + response.body().length());
                            dlog.i("AddPlaceMember jsonResponse : " + response.body());
                            if (response.body().replace("\"", "").equals("success")) {
                                dlog.i("매장 멤버 추가 완료");
                                Toast_Nomal("직원 초대가 완료되었습니다[승인 대기 중]");
                                shardpref.putInt("SELECT_POSITION", 2);
                                shardpref.putInt("SELECT_POSITION_sub",0);
                                pm.MemberGo(mContext);
                            }else{
                                Toast_Nomal("이미 직원으로 등록된 사용자 입니다.");
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
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
