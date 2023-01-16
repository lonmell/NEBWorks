package com.krafte.nebworks.ui.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.UserSaveInterface;
import com.krafte.nebworks.databinding.ActivityChangepwBinding;
import com.krafte.nebworks.util.AES256Util;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangePWActivity extends AppCompatActivity {
    private ActivityChangepwBinding binding;
    private final static String TAG = "ChangePWActivity";
    Context mContext;

    PreferenceHelper shardpref;;

    //Shared
    String USER_INFO_ID = "";
    String USER_INFO_PHONE = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_NAME = "";
    String USER_INFO_PW = "";
    String USER_INFO_GENDER = "";
    String USER_INFO_IMG = "";

    String changePw1 = "";
    String changePw2 = "";
    String returnPage = "";
    boolean ConfirmPw = false;

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    AES256Util aes256Util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_delete);
        binding = ActivityChangepwBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        setBtnEvent();
        try {
            aes256Util = new AES256Util("dkwj12fisne349vnlkw904mlk13490nv");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        dlog.DlogContext(mContext);
        
        //shardpref Area
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID        = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_PHONE     = shardpref.getString("USER_INFO_PHONE", "");
        USER_INFO_EMAIL     = shardpref.getString("USER_INFO_EMAIL", "");
        USER_INFO_NAME      = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_PW        = shardpref.getString("USER_INFO_PW", "");
        USER_INFO_GENDER    = shardpref.getString("USER_INFO_GENDER", "");
        USER_INFO_IMG       = shardpref.getString("USER_INFO_IMG", "");
        returnPage          = shardpref.getString("returnPage", "");
        
        dlog.i("USER_INFO_ID = " + USER_INFO_ID);
        dlog.i("USER_INFO_PHONE = " + USER_INFO_PHONE);
        dlog.i("USER_INFO_EMAIL = " + USER_INFO_EMAIL);
        dlog.i("USER_INFO_NAME = " + USER_INFO_NAME);
        dlog.i("USER_INFO_PW = " + USER_INFO_PW);
        dlog.i("USER_INFO_GENDER = " + USER_INFO_GENDER);
        dlog.i("USER_INFO_IMG = " + USER_INFO_IMG);
        dlog.i("returnPage = " + returnPage);
    }


    @Override
    public void onResume(){
        super.onResume();
        BtnOneCircleFun(true);
    }

    private void setBtnEvent(){
        binding.backBtn.setOnClickListener(v -> {
           super.onBackPressed();
        });
        binding.inputPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                changePw1 = s.toString();
                if(!changePw1.equals(changePw2)){
                    ConfirmPw = false;
                    binding.confirmPwTv.setTextColor(Color.parseColor("#FF3636"));
                    binding.confirmPwTv.setText("비밀번호가 동일하지 않습니다.");
                }else{
                    if(!check_validation(changePw2)){
                        ConfirmPw = false;
                        binding.confirmPwTv.setTextColor(Color.parseColor("#FF3636"));
                        binding.confirmPwTv.setText("영문,숫자 조합 8자리 이상이어야합니다.");
                    }else{
                        ConfirmPw = true;
                        binding.confirmPwTv.setTextColor(Color.parseColor("#68B0FF"));
                        binding.confirmPwTv.setText("비밀번호가 동일합니다.");
                    }
                }
            }
        });
        binding.pwConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                changePw2 = s.toString();
                if(!changePw1.equals(changePw2)){
                    ConfirmPw = false;
                    binding.confirmPwTv.setTextColor(Color.parseColor("#FF3636"));
                    binding.confirmPwTv.setText("비밀번호가 동일하지 않습니다.");
                }else{
                    if(!check_validation(changePw2)){
                        ConfirmPw = false;
                        binding.confirmPwTv.setTextColor(Color.parseColor("#FF3636"));
                        binding.confirmPwTv.setText("영문,숫자 조합 8자리 이상이어야합니다.");
                    }else{
                        ConfirmPw = true;
                        binding.confirmPwTv.setTextColor(Color.parseColor("#68B0FF"));
                        binding.confirmPwTv.setText("비밀번호가 동일합니다.");
                    }
                }
            }
        });

        binding.joinconfirm.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            SaveUser();
        });
    }

    private Boolean check_validation(String password) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
//        String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        String val_symbol = "[a-zA-Z0-9]*$";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
//        String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
//        Pattern pattern_alpha = Pattern.compile(val_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
//        Matcher matcher_alpha = pattern_alpha.matcher(password);

        if (8 <= password.length() && password.length() <= 16) {
            if (matcher_symbol.find()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    RetrofitConnect rc = new RetrofitConnect();
    public void SaveUser() {
        USER_INFO_PW = changePw2;
        try {
            USER_INFO_PW = aes256Util.encode("kraftmysecretkey" + USER_INFO_PW + "nrkwl3nkv54");
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException
                | NoSuchPaddingException | IllegalBlockSizeException
                | NoSuchAlgorithmException | BadPaddingException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        dlog.i("------SaveUser-------");
        dlog.i("USER ID : " + USER_INFO_ID);
        dlog.i("프로필 사진 url : " + USER_INFO_IMG);
        dlog.i("성명 : " + USER_INFO_NAME);
        dlog.i("비밀번호 : " + changePw2);
        dlog.i("휴대폰 : " + USER_INFO_PHONE);
        dlog.i("성별 : " + USER_INFO_GENDER);
        dlog.i("------SaveUser-------");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSaveInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSaveInterface api = retrofit.create(UserSaveInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, USER_INFO_NAME, USER_INFO_EMAIL, USER_INFO_PW, USER_INFO_PHONE, USER_INFO_GENDER, USER_INFO_IMG);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("SaveUser jsonResponse length : " + response.body().length());
                            dlog.i("SaveUser jsonResponse : " + response.body());
                            try {
                                if (!jsonResponse.equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
//                                    Toast_Nomal("비밀번호 변경이 완료되었습니다.");
//                                    Toast.makeText(mContext, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    shardpref.remove("USER_INFO_NAME");
                                    shardpref.remove("USER_INFO_PW");
                                    shardpref.remove("USER_INFO_GENDER");
                                    shardpref.remove("USER_INFO_IMG");
                                    pm.ChangePW2(mContext);
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

    public void Toast_Nomal(String message){
        BtnOneCircleFun(true);
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

    private void BtnOneCircleFun(boolean tf){
        binding.joinconfirm.setClickable(false);
        binding.joinconfirm.setEnabled(false);
    }
}
