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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.krafte.nebworks.util.RetrofitConnect;
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

    boolean duplicateCheck = false;

    boolean allcheck = false;
    boolean Uservice01 = false;
    boolean Uservice02 = false;
    boolean Uservice03 = false;
    boolean Uservice04 = false;
    Boolean CertiSuccessTF = false;
    int last_length = 0;

    String emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


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

        binding.inputUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(last_length != s.toString().length()){
                    binding.tv06.setBackgroundColor(Color.parseColor("#6395EC"));
                    binding.tv06.setTextColor(Color.parseColor("#000000"));
                    binding.tv06.setClickable(true);
                    binding.tv06.setEnabled(true);
                }
                USER_INFO_EMAIL = s.toString();
                last_length = s.toString().length();
            }
        });

        /*비밀번호 입력 체크*/
        binding.editPw.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력하기 전에
                PWChangeText(charSequence.toString(),0);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //입력란에 변화가 있을때
                if (charSequence.length() == 0) {
                    binding.checkValidationTxt1.setVisibility(View.GONE);
                } else {
                    PWChangeText(charSequence.toString(),0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.editPwConfirm.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력하기 전에
                PWChangeText(charSequence.toString(),1);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //입력란에 변화가 있을때
                if (charSequence.length() == 0) {
                    binding.checkValidationTxt1.setVisibility(View.GONE);
                } else {
                    PWChangeText(charSequence.toString(),1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


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
            if(checkEmail()){
                UserCheck(1, binding.inputUserEmail.getText().toString());
            }else{
                Toast_Nomal("이메일 형식이 올바르지 않습니다.");
            }

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


    private boolean checkEmail(){
        String email = binding.inputUserEmail.getText().toString().trim(); //공백제거
        boolean p = Pattern.matches(emailValidation, email);// 서로 패턴이 맞닝?
        if (p) {
            //이메일 형태가 정상일 경우
            binding.inputUserEmail.setTextColor(Color.parseColor("#000000"));
            binding.emailCheck.setText("중복확인을 해주세요.");
            binding.emailCheck.setTextColor(Color.parseColor("#1483FE"));
            return true;
        } else {
            binding.inputUserEmail.setTextColor(-65536);
            binding.emailCheck.setText("정상적인 이메일 형태가 아닙니다. ");
            binding.emailCheck.setTextColor(Color.parseColor("#FF3D00"));
            //또는 questionEmail.setTextColor(R.color.red.toInt())
            return false;
        }
    }
    public boolean Join_Info_Check() {
        allcheck = true;
        Uservice01 = true;
        Uservice02 = true;
        Uservice03 = true;
        Uservice04 = true;

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
        }else if(!checkEmail()){
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "이메일 형식이 올바르지 않습니다.");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
            return false;
        } else if (!duplicateCheck) {
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "이메일 중복체크를 해주세요.");
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
    RetrofitConnect rc = new RetrofitConnect();
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
                Log.e(TAG, "WorkTapListFragment2 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                dlog.e("response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("UserCheck jsonResponse : " + jsonResponse);

                            if (!jsonResponse.equals("[]")) {
                                dlog.i("UserCheck length : " + jsonResponse.length());
                                cnt = jsonResponse.length();
//                                Toast.makeText(mContext,"이미 존재하는 이메일입니다.",Toast.LENGTH_SHORT).show();
                                binding.emailCheck.setText("중복되는 이메일입니다.");
                                binding.emailCheck.setTextColor(Color.parseColor("#FF3D00"));
                            } else {
                                binding.tv06.setBackgroundColor(Color.parseColor("#a9a9a9"));
                                binding.tv06.setTextColor(Color.parseColor("#ffffff"));
                                binding.tv06.setClickable(false);
                                binding.tv06.setEnabled(false);
                                binding.emailCheck.setText("사용할 수 있습니다.");
                                binding.emailCheck.setTextColor(Color.parseColor("#1483FE"));
                                duplicateCheck = true;
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
        Call<String> call = api.getData(USER_INFO_EMAIL, USER_INFO_NAME, "",USER_INFO_PW, USER_INFO_PHONE, USER_INFO_GENDER, "", "NEB");
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    try {
                        if (jsonResponse.replace("\"", "").equals("success")) {
                            shardpref.putBoolean("USER_LOGIN_CONFIRM", true);
                            shardpref.putString("USER_INFO_EMAIL", USER_INFO_EMAIL);
                            shardpref.remove("USER_INFO_NAME");
                            shardpref.remove("USER_INFO_PHONE");
                            shardpref.remove("USER_INFO_PW");
                            Toast.makeText(mContext,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
//                            shardpref.putString("USER_INFO_AUTH", "0");
//                            shardpref.putInt("SELECT_POSITION", 0);
//                            shardpref.putInt("SELECT_POSITION_sub", 0);
//                            pm.PlaceList(mContext);
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
        String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
//        String val_symbol = "[a-zA-Z0-9]*$";
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
    private void PWChangeText(String password, int pos) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
//        String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
//        Pattern pattern_alpha = Pattern.compile(val_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
//        Matcher matcher_alpha = pattern_alpha.matcher(password);

        if (pos == 0) {
            if (8 > password.length() || 16 < password.length()) {
                binding.checkValidationTxt1.setVisibility(View.VISIBLE);
                binding.checkValidationTxt3.setTextColor(Color.parseColor("#FF0000"));
                binding.checkValidationTxt1.setText("비밀번호 글자 수는 8~16자내로 입력해야 합니다.");
            } else if (!matcher_symbol.find()) {
                binding.checkValidationTxt1.setVisibility(View.VISIBLE);
                binding.checkValidationTxt3.setTextColor(Color.parseColor("#FF0000"));
                binding.checkValidationTxt1.setText("숫자,특수문자가 포함되어야 합니다.");
            } else {
                binding.checkValidationTxt1.setVisibility(View.VISIBLE);
                binding.checkValidationTxt1.setTextColor(Color.parseColor("#6395EC"));
                binding.checkValidationTxt1.setText("규칙에 맞는 비밀번호 입니다.");
            }
        } else if (pos == 1) {
            //입력이 끝났을때
            String pw1 = binding.editPw.getText().toString();
            String pw2 = binding.editPwConfirm.getText().toString();
            if (pw1.equals(pw2)) {
                binding.checkValidationTxt3.setVisibility(View.VISIBLE);
                binding.checkValidationTxt3.setTextColor(Color.parseColor("#6395EC"));
                binding.checkValidationTxt3.setText("비밀번호가 동일합니다.");
            } else {
                binding.checkValidationTxt3.setVisibility(View.VISIBLE);
                binding.checkValidationTxt3.setTextColor(Color.parseColor("#FF0000"));
                binding.checkValidationTxt3.setText("비밀번호가 동일하지 않습니다.");
            }
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
