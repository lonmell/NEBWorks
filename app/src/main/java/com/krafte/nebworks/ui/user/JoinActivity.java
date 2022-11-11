package com.krafte.nebworks.ui.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.UserInsertInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityJoinBinding;
import com.krafte.nebworks.pop.JoinPopActivity;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.util.AES256Util;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.disconnectHandler;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
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

public class JoinActivity extends AppCompatActivity {
    private ActivityJoinBinding binding;
//    private final static String TAG = "JoinActivity";
    private static final String TAG = "AES256Util";
    Context mContext;

    // shared 저장값
    String USER_INFO_NAME = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_PHONE = "";
    String USER_INFO_PW = "";
    String USER_INFO_GENDER = "0";
    String USER_INFO_JOIN_DATE = "";
    String USER_INFO_SERVICE = "";
    String USER_LOGIN_METHOD = "NEP";
    String USER_INFO_ID = "";

    //Other
    //    RandomOut ro = new RandomOut();
    //    private Boolean drop_updown = true;
    DateCurrent dc = new DateCurrent();
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    AES256Util aes256Util;
    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    Intent intent;
    boolean confirmEmail = false;

    boolean allcheck = false;
    boolean Uservice01 = false;
    boolean Uservice02 = false;
    boolean Uservice03 = false;
    boolean Uservice04 = false;
    Boolean CertiSuccessTF = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_join);
        binding = ActivityJoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        CertiSuccessTF = false;
        //데이터 가져오기(본인 인증 후 데이터 가져오기)
        intent = getIntent();
        dlog.DlogContext(mContext);
        try {
            aes256Util = new AES256Util("dkwj12fisne349vnlkw904mlk13490nv");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        USER_INFO_PW = binding.editPw.getText().toString();
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        shardpref.putString("GET_TIME", dc.GET_TIME);

        dlog.i( "현재시간 : " + dc.GET_TIME);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        setBtnEvent();
        NetworkStates();

        binding.inputUserName.setText(USER_INFO_NAME);
        binding.inputUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                USER_INFO_EMAIL = s.toString();
            }
        });

        /*비밀번호 입력 체크*/
        binding.editPw.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력란에 변화가 있을때
                PWChangeText(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을때
                if (charSequence.length() == 0) {
                    binding.checkValidationTxt1.setVisibility(View.GONE);
                } else {
                    PWChangeText(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에
            }
        });
    }


    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            RemoveSharedData();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.tv06.setOnClickListener(v -> {
            UserCheck(1, binding.inputUserEmail.getText().toString());
        });
        binding.selectMan.setOnClickListener(v -> {
            binding.manTxt.setTextColor(Color.parseColor("#1E90FF"));
            binding.selectMan.setBackgroundColor(Color.parseColor("#1E90FF"));

            binding.selectWoman.setBackgroundColor(Color.parseColor("#1E90FF"));
            binding.womanTxt.setTextColor(Color.parseColor("#A1887F"));
        });
        binding.selectMan.setOnClickListener(v -> {
            USER_INFO_GENDER = "1";
            binding.manTxt.setTextColor(Color.parseColor("#1E90FF"));
            binding.manTxt.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.selectMan.setBackgroundColor(Color.parseColor("#1E90FF"));

            binding.womanTxt.setTextColor(Color.parseColor("#A1887F"));
            binding.womanTxt.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.selectWoman.setBackgroundColor(Color.parseColor("#a9a9a9"));
        });

        binding.selectWoman.setOnClickListener(v -> {
            USER_INFO_GENDER = "2";
            binding.manTxt.setTextColor(Color.parseColor("#A1887F"));
            binding.manTxt.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.selectMan.setBackgroundColor(Color.parseColor("#a9a9a9"));

            binding.womanTxt.setTextColor(Color.parseColor("#1E90FF"));
            binding.womanTxt.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.selectWoman.setBackgroundColor(Color.parseColor("#1E90FF"));
        });

        binding.linear03.setOnClickListener(v -> {
            if (!allcheck) {
                allcheck = true;
                Uservice01 = true;
                Uservice02 = true;
                Uservice03 = true;
                Uservice04 = true;
                binding.linear04.setBackgroundResource(R.drawable.resize_service_on);
                binding.serviceRadio01.setBackgroundResource(R.drawable.resize_service_on);
                binding.serviceRadio02.setBackgroundResource(R.drawable.resize_service_on);
                binding.serviceRadio03.setBackgroundResource(R.drawable.resize_service_on);
                binding.serviceRadio04.setBackgroundResource(R.drawable.resize_service_on);
            } else {
                allcheck = false;
                Uservice01 = false;
                Uservice02 = false;
                Uservice03 = false;
                Uservice04 = false;
                binding.linear04.setBackgroundResource(R.drawable.select_empty_round);
                binding.serviceRadio01.setBackgroundResource(R.drawable.select_empty_round);
                binding.serviceRadio02.setBackgroundResource(R.drawable.select_empty_round);
                binding.serviceRadio03.setBackgroundResource(R.drawable.select_empty_round);
                binding.serviceRadio04.setBackgroundResource(R.drawable.select_empty_round);
            }
        });

        binding.serviceTv01.setOnClickListener(v -> {
            if (!Uservice01) {
                Uservice01 = true;
                binding.serviceRadio01.setBackgroundResource(R.drawable.resize_service_on);
                if (Uservice01 && Uservice02 && Uservice03 && Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.resize_service_on);
                }
            } else {
                Uservice01 = false;
                binding.serviceRadio01.setBackgroundResource(R.drawable.select_empty_round);
                if (!Uservice01 || !Uservice02 || !Uservice03 || !Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.select_empty_round);
                }
            }
        });

        binding.serviceTv02.setOnClickListener(v -> {
            if (!Uservice02) {
                Uservice02 = true;
                binding.serviceRadio02.setBackgroundResource(R.drawable.resize_service_on);
                if (!Uservice01 && !Uservice02 && !Uservice03 && !Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.resize_service_on);
                }
            } else {
                Uservice02 = false;
                binding.serviceRadio02.setBackgroundResource(R.drawable.select_empty_round);
                if (!Uservice01 || !Uservice02 || !Uservice03 || !Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.select_empty_round);
                }
            }
        });

        binding.serviceTv03.setOnClickListener(v -> {
            if (!Uservice03) {
                Uservice03 = true;
                binding.serviceRadio03.setBackgroundResource(R.drawable.resize_service_on);
                if (!Uservice01 && !Uservice02 && !Uservice03 && !Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.resize_service_on);
                }
            } else {
                Uservice03 = false;
                binding.serviceRadio03.setBackgroundResource(R.drawable.select_empty_round);
                if (!Uservice01 || !Uservice02 || !Uservice03 || !Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.select_empty_round);
                }
            }
        });

        binding.serviceTv04.setOnClickListener(v -> {
            if (!Uservice04) {
                Uservice04 = true;
                binding.serviceRadio04.setBackgroundResource(R.drawable.resize_service_on);
                if (!Uservice01 && !Uservice02 && !Uservice03 && !Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.resize_service_on);
                }
            } else {
                Uservice04 = false;
                binding.serviceRadio04.setBackgroundResource(R.drawable.select_empty_round);
                binding.linear04.setBackgroundResource(R.drawable.select_empty_round);
                if (!Uservice01 || !Uservice02 || !Uservice03 || !Uservice04) {
                    binding.linear04.setBackgroundResource(R.drawable.select_empty_round);
                }
            }
        });

        binding.joinBtn.setOnClickListener(v1 -> {
            dlog.i( "Click binding.JoinBtn");
            if (Join_Info_Check()) {
                SAVE_USER_IFNO();
            }
        });

        binding.editPw.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }});

        binding.editPw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
    }

    public boolean Join_Info_Check() {
        Log.e(TAG, "confirmEmail : " + confirmEmail);
        if (!Uservice01 || !Uservice02 || !Uservice03 || !Uservice04) {
            Toast.makeText(mContext, "필수약관에 동의해주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "이용약관 체크를 완료해주세요");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);

            return false;
        } else if (binding.editPw.getText().toString().isEmpty()) {
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "비밀번호를 입력해주세요.");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);

            return false;
        }
        if (!check_validation(binding.editPw.getText().toString())) {
//            Toast.makeText(this, "비밀번호로 부적절합니다", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "비밀번호로 부적절합니다.");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);

            return false;
        } else {
            return true;
        }
    }

    public void SAVE_USER_IFNO() {
        NetworkStates();
        dlog.i( "USER_INFO_PHONE : " + USER_INFO_PHONE);
        if (USER_INFO_PHONE.contains("+82")) {
            USER_INFO_PHONE = USER_INFO_PHONE.replace("+82", "0");
        }

        USER_INFO_JOIN_DATE = dc.GET_TIME;
        //앱 사용자는 권한 9
//        USER_INFO_AUTH = "2";
        //HAYPASS 앱 사용자는 SERVICE 데이터가 Standard[ S ]
        //전용 앱의 경우 Premium[ P ]
        USER_INFO_SERVICE = "S";

        /*STRING 저장함*/
        shardpref.putString("USER_INFO_NAME", USER_INFO_NAME);
        shardpref.putString("USER_INFO_PHONE", USER_INFO_PHONE);
        shardpref.putString("USER_LOGIN_METHOD", USER_LOGIN_METHOD);
        INPUT_JOIN_DATA();
    }

    public void NetworkStates() {
        int status = disconnectHandler.getConnectivityStatus(getApplicationContext());
        if (status == disconnectHandler.TYPE_MOBILE) {
            dlog.i( "모바일로 연결됨");
        } else if (status == disconnectHandler.TYPE_WIFI) {
            dlog.i( "무선랜으로 연결됨");
        } else {
            Intent intent = new Intent(mContext, OneButtonPopActivity.class);
            intent.putExtra("data", "네트워크가 \n 연결되지 않았습니다.");
            intent.putExtra("left_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dlog.i( "연결 안됨.");
        }
    }

    int UserCheckCnt = 0;
    int cnt = 0;

    public int UserCheck(int i, String account) {
        dlog.i("UserCheck account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSelectInterface api = retrofit.create(UserSelectInterface.class);
        Call<String> call = api.getData(account);
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

                            if (!response.body().equals("[]")) {
                                dlog.i("UserCheck length : " + response.body().length());
                                cnt = response.body().length();
                                Toast.makeText(mContext,"이미 존재하는 이메일입니다.",Toast.LENGTH_SHORT).show();
                            } else {
                                binding.tv06.setBackgroundColor(Color.parseColor("#a9a9a9"));
                                binding.tv06.setTextColor(Color.parseColor("#ffffff"));
                                binding.tv06.setClickable(false);
                                binding.tv06.setEnabled(false);
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
        return cnt;
    }

    public void INPUT_JOIN_DATA() {
        USER_INFO_PW = binding.editPw.getText().toString();
        Log.i(TAG,"인코딩 전 USER_INFO_PW : " + USER_INFO_PW);
        try {
            USER_INFO_PW = aes256Util.encode("kraftmysecretkey" + USER_INFO_PW + "nrkwl3nkv54");
//            USER_INFO_PW = aes256Util.encode(USER_INFO_PW);
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException
                | NoSuchPaddingException | IllegalBlockSizeException
                | NoSuchAlgorithmException | BadPaddingException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        dlog.i("-----INPUT_JOIN_DATA-----");
        dlog.i("account : " + USER_INFO_EMAIL);
        dlog.i("pw : " + USER_INFO_PW);
        Log.i(TAG,"인코딩 후 USER_INFO_PW : " + USER_INFO_PW);
        dlog.i("name : " + USER_INFO_NAME);
        dlog.i("phone : " + USER_INFO_PHONE);
        dlog.i("gender : " + USER_INFO_GENDER);
        dlog.i("UserCheckCnt : " + UserCheckCnt);
        dlog.i("-----INPUT_JOIN_DATA-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserInsertInterface api = retrofit.create(UserInsertInterface.class);
        Call<String> call = api.getData(USER_INFO_EMAIL, USER_INFO_NAME, USER_INFO_PW, USER_INFO_PHONE, USER_INFO_GENDER, "", "NEB");
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dlog.e("ConnectThread_UserInfo onSuccess not base64 : " + response.body().replace("\"", ""));
                    try {
                        if (response.body().replace("\"", "").equals("success")) {
                            shardpref.putBoolean("USER_LOGIN_CONFIRM", true);
                            shardpref.putString("USER_INFO_EMAIL", USER_INFO_EMAIL);
                            shardpref.remove("USER_INFO_NAME");
                            shardpref.remove("USER_INFO_PHONE");
                            shardpref.remove("USER_INFO_PW");
                            Toast.makeText(mContext,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                            pm.AuthSelect(mContext);
                        }
                    } catch (Exception e) {
                        dlog.i("Exception : " + e);
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    public void RemoveSharedData() {

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

    @SuppressLint("SetTextI18n")
    private void PWChangeText(String password) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
//        String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
//        Pattern pattern_alpha = Pattern.compile(val_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
//        Matcher matcher_alpha = pattern_alpha.matcher(password);

        if (8 > password.length() || 16 < password.length()) {
            binding.checkValidationTxt1.setVisibility(View.VISIBLE);
            binding.checkValidationTxt1.setText("비밀번호 글자 수는 8~16자내로 입력해야 합니다.");
        } else if (!matcher_symbol.find()) {
            binding.checkValidationTxt1.setVisibility(View.VISIBLE);
            binding.checkValidationTxt1.setText("숫자,특수문자가 포함되어야 합니다.");
        } else {
            binding.checkValidationTxt1.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        RemoveSharedData();
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

}
