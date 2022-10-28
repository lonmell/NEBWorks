package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.CertiNumData;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.UserInsertInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityJoinBinding;
import com.krafte.nebworks.pop.AlertPopActivity;
import com.krafte.nebworks.pop.JoinPopActivity;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.pop.TermViewActivity;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.util.AES256Util;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.HashCode;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RandomOut;
import com.krafte.nebworks.util.Sms_receiver;
import com.krafte.nebworks.util.disconnectHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class JoinActivity extends AppCompatActivity {
    private ActivityJoinBinding binding;
    private final static String TAG = "JoinActivity";
    Context mContext;


    // shared 저장값
    String USER_INFO_NAME = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_PHONE = "";
    String USER_INFO_PW = "";
    String USER_INFO_AGENCY = "";
    String USER_INFO_BIRTH = "";
    String USER_INFO_SEX = "1";
    String USER_INFO_JOIN_DATE = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_SERVICE = "";
    String USER_LOGIN_METHOD = "NEP";
    String USER_INFO_ID = "";

    //Other
    RandomOut ro = new RandomOut();
    private Boolean drop_updown = true;
    DateCurrent dc = new DateCurrent();
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();

    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    CertiNumData certiNumData = new CertiNumData();
    String CertiNum = "";
    Boolean CertiSuccessTF = false;
    AES256Util aes256Util;
    String agency_select_tv = "";
    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    Intent intent;
    Sms_receiver smsReceiver;
    String SND_NUM = "";
    String getMessage = "";
    MyTimer myTimer;
    boolean confirmEmail = false;
    int inputEmailCnt = 0;

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
            aes256Util = new AES256Util("kraftmysecretkey");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /*STRING 가져옴*/
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        USER_INFO_PW = shardpref.getString("USER_INFO_PW", "");
        USER_INFO_AGENCY = shardpref.getString("USER_INFO_AGENCY", "");
        USER_INFO_SEX = shardpref.getString("USER_INFO_SEX", "1");
        USER_INFO_BIRTH = shardpref.getString("USER_INFO_BIRTH", "");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

        String getName =  shardpref.getString("inputUserName","");
        String getPhone = shardpref.getString("editPhone","");
        boolean getRadio04 =  shardpref.getBoolean("serviceRadio04",false);
        String getPw =  shardpref.getString("editPw","");

//        if(!getName.isEmpty()){
//            binding.inputUserName.setText(getName);
//        }
//
//
//        if(!getPhone.isEmpty()){
//            binding.editPhone.setText(getPhone);
//        }
//        if(getRadio04){
//            binding.serviceRadio04.setChecked(true);
//            binding.serviceRadio01.setChecked(true);
//            binding.serviceRadio02.setChecked(true);
//            binding.serviceRadio03.setChecked(true);
//        }
//        if(!getPw.isEmpty()){
//            binding.editPw.setText(getPw);
//        }


        /*STRING 저장함*/

        shardpref.putString("GET_TIME", dc.GET_TIME);

        Log.i(TAG, "현재시간 : " + dc.GET_TIME);
        binding.manTxt.setTextColor(Color.parseColor("#1E90FF"));
        binding.selectMan.setBackgroundColor(Color.parseColor("#1E90FF"));
        

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        setBtnEvent();
        NetworkStates();
        myTimer = new MyTimer(60000, 1000);


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
                if(charSequence.length() == 0){
                    binding.checkValidationTxt1.setVisibility(View.GONE);
                }else{
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
            
        binding.getAuthResult.setOnClickListener(v -> {
            shardpref.putString("inputUserName",binding.inputUserName.getText().toString());
            shardpref.putString("agency_select_tv",agency_select_tv);
            shardpref.putString("editPhone",binding.editPhone.getText().toString());
            shardpref.putBoolean("serviceRadio04",binding.serviceRadio04.isChecked());
            shardpref.putString("editPw",binding.editPw.getText().toString());

            if(binding.inputUserName.getText().toString().isEmpty()){
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "이름을 입력해 주세요.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            }else if(binding.editPhone.getText().toString().isEmpty()){
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "전화번호를 입력해주세요.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            }else{
                binding.confirmNumCounting.setVisibility(View.VISIBLE);
                SendConfirmMessage();
            }
        });


        binding.backBtn.setOnClickListener(v -> {
            myTimer.cancel();
            RemoveSharedData();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        binding.selectMan.setOnClickListener(v -> {
            binding.manTxt.setTextColor(Color.parseColor("#1E90FF"));
            binding.selectMan.setBackgroundColor(Color.parseColor("#1E90FF"));

            binding.selectWoman.setBackgroundColor(Color.parseColor("#1E90FF"));
            binding.womanTxt.setTextColor(Color.parseColor("#A1887F"));
        });
        binding.selectMan.setOnClickListener(v -> {
            USER_INFO_SEX = "1";
            binding.manTxt.setTextColor(Color.parseColor("#1E90FF"));
            binding.manTxt.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.selectMan.setBackgroundColor(Color.parseColor("#1E90FF"));

            binding.womanTxt.setTextColor(Color.parseColor("#A1887F"));
            binding.womanTxt.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.selectWoman.setBackgroundColor(Color.parseColor("#a9a9a9"));
        });

        binding.selectWoman.setOnClickListener(v -> {
            USER_INFO_SEX = "2";
            binding.manTxt.setTextColor(Color.parseColor("#A1887F"));
            binding.manTxt.setBackgroundColor(Color.parseColor("#f2f2f2"));
            binding.selectMan.setBackgroundColor(Color.parseColor("#a9a9a9"));

            binding.womanTxt.setTextColor(Color.parseColor("#1E90FF"));
            binding.womanTxt.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.selectWoman.setBackgroundColor(Color.parseColor("#1E90FF"));
        });

        binding.updownArrow.setOnClickListener(v -> {
            /*
             *  UP = TRUE = 화살표 위로
             *  DOWN = FALSE = 화살표 아래로
             */
            if (drop_updown) {
                drop_updown = false;
                binding.updownArrowImg.setBackgroundResource(R.drawable.down_arrow);
                binding.serviceTv01.setVisibility(View.GONE);
                binding.serviceTv02.setVisibility(View.GONE);
                binding.serviceTv03.setVisibility(View.GONE);
            } else {
                drop_updown = true;
                binding.updownArrowImg.setBackgroundResource(R.drawable.up_arrow);
                binding.serviceTv01.setVisibility(View.VISIBLE);
                binding.serviceTv02.setVisibility(View.VISIBLE);
                binding.serviceTv03.setVisibility(View.VISIBLE);
            }
        });

        binding.serviceTv01.setOnClickListener(v -> {
            if (binding.serviceRadio01.isChecked()) {
                binding.serviceRadio04.setChecked(false);
                binding.serviceRadio01.setChecked(false);
            } else {
                binding.serviceRadio01.setChecked(true);
                binding.serviceRadio04.setChecked(binding.serviceRadio01.isChecked() && binding.serviceRadio02.isChecked() && binding.serviceRadio03.isChecked());
            }
        });

        binding.serviceTv02.setOnClickListener(v -> {
            if (binding.serviceRadio02.isChecked()) {
                binding.serviceRadio04.setChecked(false);
                binding.serviceRadio02.setChecked(false);
            } else {
                binding.serviceRadio02.setChecked(true);
                binding.serviceRadio04.setChecked(binding.serviceRadio01.isChecked() && binding.serviceRadio02.isChecked() && binding.serviceRadio03.isChecked());
            }
        });

        binding.serviceTv03.setOnClickListener(v -> {
            if (binding.serviceRadio03.isChecked()) {
                binding.serviceRadio04.setChecked(false);
                binding.serviceRadio03.setChecked(false);
            } else {
                binding.serviceRadio03.setChecked(true);
                binding.serviceRadio04.setChecked(binding.serviceRadio01.isChecked() && binding.serviceRadio02.isChecked() && binding.serviceRadio03.isChecked());
            }

        });

        binding.serviceTv04.setOnClickListener(v -> {
            if (binding.serviceRadio04.isChecked()) {
                binding.serviceRadio01.setChecked(false);
                binding.serviceRadio02.setChecked(false);
                binding.serviceRadio03.setChecked(false);
                binding.serviceRadio04.setChecked(false);
            } else {
                binding.serviceRadio01.setChecked(true);
                binding.serviceRadio02.setChecked(true);
                binding.serviceRadio03.setChecked(true);
                binding.serviceRadio04.setChecked(true);
            }
        });

        binding.JoinBtn.setOnClickListener(v1 -> {
            Log.i(TAG, "Click binding.JoinBtn");
            Log.i(TAG,"CertiSuccessTF : " + CertiSuccessTF);
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

        binding.inputUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(confirmEmail && (s.length() != inputEmailCnt)){
                    confirmEmail = false;
                    binding.confirmEmailBtn.setEnabled(true);
                    binding.confirmEmailBtn.setText("이메일 중복체크");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.confirmEmailBtn.setOnClickListener(v -> {
            ConfirmEmail();
        });


        binding.privacyTerms.setOnClickListener(v -> {
            Intent intent;
            if (agency_select_tv.isEmpty()) {
                intent = new Intent(this, TermViewActivity.class);
                intent.putExtra("data", "SKT");
            } else {
                intent = new Intent(this, TermViewActivity.class);
                intent.putExtra("data", agency_select_tv);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);

        });
        binding.identificationTerms.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermViewActivity.class);
            intent.putExtra("data", "IDEN");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        });

        binding.confirmPhoneBtn.setOnClickListener(view -> {
            if(binding.editConfirmNum.getText().toString().isEmpty()){
                Toast.makeText(this,"인증번호가 입력되지 않았습니다.",Toast.LENGTH_LONG).show();
            }else{
                if(Sms_receiver.receiverNum.equals(SND_NUM) && Sms_receiver.receiverNum.equals(binding.editConfirmNum.getText().toString())){
                    CertiSuccessTF = true;
                    Toast.makeText(this,"인증번호가 확인되었습니다.",Toast.LENGTH_LONG).show();
                    binding.confirmPhoneBtn.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.confirmPhoneBtn.setText("인증완료");
                    binding.confirmPhoneBtn.setTextColor(Color.parseColor("#000000"));
                    binding.confirmPhoneBtn.setClickable(false);

                    binding.getAuthResult.setVisibility(View.INVISIBLE);

                    binding.inputUserName.setEnabled(false);
                    binding.inputUserName.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.editPhone.setEnabled(false);
                    binding.editPhone.setBackgroundColor(Color.parseColor("#dcdcdc"));

                    Thread th3 = new Thread(() -> {
                        String SND_PHONE = binding.editPhone.getText().toString();

                        dbConnection.ConfrimNumSave(SND_PHONE,"","delete");

                        String getMessage = resultData.getRESULT().replaceAll("\"", "");
                        Log.i(TAG, "getMessage = " + getMessage);
                    });
                    th3.start();
                    try {
                        th3.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else if(binding.editConfirmNum.getText().toString().equals(CertiNum)){
                    CertiSuccessTF = true;
                    Toast.makeText(this,"인증번호가 확인되었습니다.",Toast.LENGTH_LONG).show();
                    binding.confirmPhoneBtn.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.confirmPhoneBtn.setText("인증완료");
                    binding.confirmPhoneBtn.setTextColor(Color.parseColor("#000000"));
                    binding.confirmPhoneBtn.setClickable(false);

                    binding.getAuthResult.setVisibility(View.INVISIBLE);

                    binding.inputUserName.setEnabled(false);
                    binding.inputUserName.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.editPhone.setEnabled(false);
                    binding.editPhone.setBackgroundColor(Color.parseColor("#dcdcdc"));

                    Thread th3 = new Thread(() -> {
                        String SND_PHONE = binding.editPhone.getText().toString();

                        dbConnection.ConfrimNumSave(SND_PHONE,"","delete");

                        String getMessage = resultData.getRESULT().replaceAll("\"", "");
                        Log.i(TAG, "getMessage = " + getMessage);
                    });
                    th3.start();
                    try {
                        th3.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    CertiSuccessTF = false;
                    Toast.makeText(this,"유효하지 않은 인증번호 입니다.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean Join_Info_Check() {

        Log.e(TAG,"confirmEmail : " + confirmEmail);
        if (binding.inputUserName.getText().toString().isEmpty()) {
            //데이터 담아서 팝업(액티비티) 호출
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "이름을 입력해주세요.");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
            return false;
        }else if(!confirmEmail){
            Intent intent = new Intent(mContext, AlertPopActivity.class);
            intent.putExtra("data", "이메일 중복체크가 필요합니다.");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        }else if (binding.editPhone.getText().toString().isEmpty()) {
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "전화번호를 기기에서 가져올 수 없습니다.\n 직접 입력해주세요.");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
            binding.editPhone.setEnabled(true);
            return false;
        }  else if(!CertiSuccessTF){
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "본인인증을 완료해주세요");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);

            return false;
        }else if (!binding.serviceRadio04.isChecked()) {
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
        }if(!check_validation(binding.editPw.getText().toString())){
//            Toast.makeText(this, "비밀번호로 부적절합니다", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "비밀번호로 부적절합니다.");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);

            return false;
        }else if(binding.inputUserEmail.getText().toString().isEmpty() || binding.inputUserEmail.getText().toString().length() == 0){
            Intent intent = new Intent(this, JoinPopActivity.class);
            intent.putExtra("data", "이메일 주소를 입력해주세요");
            intent.putExtra("left_btn_txt", "뒤로가기");
            intent.putExtra("right_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);

            return false;
        }else {
            return true;
        }


    }

    public void SAVE_USER_IFNO() {
        NetworkStates();
        USER_INFO_NAME = binding.inputUserName.getText().toString();
        USER_INFO_PHONE = binding.editPhone.getText().toString();
        USER_INFO_PW = binding.editPw.getText().toString();
        USER_INFO_ID = binding.inputUserEmail.getText().toString();
        Log.i(TAG, "USER_INFO_PHONE : " + USER_INFO_PHONE);
        if (USER_INFO_PHONE.contains("+82")) {
            USER_INFO_PHONE = USER_INFO_PHONE.replace("+82", "0");
        }

        USER_INFO_JOIN_DATE = dc.GET_TIME;
        //앱 사용자는 권한 9
        USER_INFO_AUTH = "2";
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
            Log.i(TAG, "모바일로 연결됨");
        } else if (status == disconnectHandler.TYPE_WIFI) {
            Log.i(TAG, "무선랜으로 연결됨");
        } else {
            Intent intent = new Intent(mContext, OneButtonPopActivity.class);
            intent.putExtra("data", "네트워크가 \n 연결되지 않았습니다.");
            intent.putExtra("left_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.i(TAG, "연결 안됨.");
        }
    }

    int UserCheckCnt = 0;
    public void ConfirmEmail(){
        Thread th = new Thread(() -> {
            String USER_ID = binding.inputUserEmail.getText().toString();
            inputEmailCnt = USER_ID.length();

            if(USER_ID.isEmpty()){
                Intent intent = new Intent(mContext, AlertPopActivity.class);
                intent.putExtra("data", "이메일을 입력해주세요");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            }else{
                UserCheckCnt = UserCheck(USER_ID);
                dlog.i("ConfirmEmail USER_ID : " + USER_ID);
                dlog.i("UserCheckCnt : " + UserCheckCnt);

                //사용자 체크
                runOnUiThread(() -> {
                    if(UserCheckCnt == 1) {
                        Intent intent = new Intent(mContext, AlertPopActivity.class);
                        intent.putExtra("data", "이미 가입한 내역이 있습니다.");
                        startActivity(intent);
                        overridePendingTransition(R.anim.translate_up, 0);
                    }else{
                        binding.confirmEmailBtn.setText("가입 가능한 이메일 입니다.");
                        binding.confirmEmailBtn.setEnabled(false);
                        confirmEmail = true;
                    }
                });
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    int cnt = 0;
    public int UserCheck(String account) {
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
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
//                                    kind = Response.getJSONObject(0).getString("kind");
//                                    employee_no = Response.getJSONObject(0).getString("employee_no");
                                    dlog.i("UserCheck length : " + Response.length());
                                    cnt = Response.length();
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
        return cnt;
    }
    //사용자 확인하는 소스 여기에 넣고 전역변수로 구분할것
    public void SendConfirmMessage(){
        Thread th_check = new Thread(() -> {
            Log.i(TAG, "getMessage = " + getMessage);
        });
        th_check.start();
        try {
            th_check.join();
            if(getMessage.equals("1")) {
                Intent intent = new Intent(mContext, AlertPopActivity.class);
                intent.putExtra("data", "이미 가입한 내역이 있습니다.");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            }else{
                myTimer.start();
                Thread th = new Thread(() -> {
                    String SND_PHONE = binding.editPhone.getText().toString();
                    String SND_NAME = binding.inputUserName.getText().toString();
                    SND_NUM = ro.getRandomNum(7);

                    /*단말기별 해시코드*/
                    String DeviceHashCode = String.valueOf(HashCode.getAppSignatures(this));
                    String hash = DeviceHashCode.substring(DeviceHashCode.indexOf("[")+1,DeviceHashCode.indexOf("]"));
                    Log.i(TAG,"hash : " + hash);
                    dbConnection.ConfrimNumSend(SND_PHONE,SND_NAME,SND_NUM,hash);
                });
                th.start();
                try {
                    th.join();
                    Thread th2 = new Thread(() -> {
                        String SND_PHONE = binding.editPhone.getText().toString();
                        dbConnection.ConfrimNumSave(SND_PHONE,SND_NUM,"insert");

                        String getMessage = resultData.getRESULT().replaceAll("\"", "");
                        Log.i(TAG, "getMessage = " + getMessage);
                        if(getMessage.equals("success")){
                            dbConnection.ConfrimNumSelect(SND_PHONE,SND_NUM,"select");
                            CertiNum = certiNumData.getCerti_num();
                            binding.getAuthResult.setEnabled(false);
                            Log.i(TAG,"CertiNum : " + CertiNum);
                        }
                    });
                    th2.start();
                    try {
                        th2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void INPUT_JOIN_DATA() {
        USER_INFO_EMAIL = binding.inputUserEmail.getText().toString();
        USER_INFO_NAME = binding.inputUserName.getText().toString();
        USER_INFO_PHONE = binding.editPhone.getText().toString();
        USER_INFO_PW = binding.editPw.getText().toString();
        USER_INFO_ID = binding.inputUserEmail.getText().toString();

        dlog.i("-----INPUT_JOIN_DATA-----");
        dlog.i("account : " + USER_INFO_EMAIL);
        dlog.i("name : " + USER_INFO_NAME);
        dlog.i("phone : " + USER_INFO_PHONE);
        dlog.i("gender : " + USER_INFO_SEX);
        dlog.i("UserCheckCnt : " + UserCheckCnt);
        dlog.i("-----INPUT_JOIN_DATA-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserInsertInterface api = retrofit.create(UserInsertInterface.class);
        Call<String> call = api.getData(USER_INFO_EMAIL, USER_INFO_NAME, USER_INFO_PW,USER_INFO_PHONE,USER_INFO_SEX,"","NEB");
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dlog.e("ConnectThread_UserInfo onSuccess not base64 : " + response.body().replace("\"", ""));
                    try {
                        if (response.body().replace("\"", "").equals("success")) {
                            shardpref.putBoolean("USER_LOGIN_CONFIRM",true);
                            shardpref.putString("USER_INFO_EMAIL",USER_INFO_EMAIL);
                            shardpref.remove("USER_INFO_NAME");
                            shardpref.remove("USER_INFO_PHONE");
                            shardpref.remove("USER_INFO_PW");
                            pm.PlaceListGo(mContext);
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
    public void RemoveSharedData(){

    }


    private Boolean check_validation(String password) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
//        String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
//        Pattern pattern_alpha = Pattern.compile(val_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
//        Matcher matcher_alpha = pattern_alpha.matcher(password);

        if(8 <= password.length() && password.length() <= 16){
            if (matcher_symbol.find()) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }

    }

    @SuppressLint("SetTextI18n")
    private void PWChangeText(String password){
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
//        String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
//        Pattern pattern_alpha = Pattern.compile(val_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
//        Matcher matcher_alpha = pattern_alpha.matcher(password);

        if(8 > password.length() || 16 < password.length()){
            binding.checkValidationTxt1.setVisibility(View.VISIBLE);
            binding.checkValidationTxt1.setText("비밀번호 글자 수는 8~16자내로 입력해야 합니다.");
        }else if (!matcher_symbol.find()) {
            binding.checkValidationTxt1.setVisibility(View.VISIBLE);
            binding.checkValidationTxt1.setText("숫자,특수문자가 포함되어야 합니다.");
        }else{
            binding.checkValidationTxt1.setVisibility(View.GONE);
        }

//        else if(!matcher_alpha.find()){
//            binding.checkValidationTxt1.setVisibility(View.VISIBLE);
//            binding.checkValidationTxt1.setText("대소문자가 적어도 하나씩은 포함되어야 합니다.");
//        }
    }

    class MyTimer extends CountDownTimer {
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {

            smsReceiver = new Sms_receiver();
            binding.confirmNumCounting.setText(millisUntilFinished/1000 + " 초");
            binding.confirmNumCounting.setVisibility(View.VISIBLE);

            if(!Sms_receiver.receiverNum.isEmpty()){
                if(SND_NUM.equals(Sms_receiver.receiverNum)){
                    Log.i(TAG,"SendConfirmMessage : " + Sms_receiver.receiverNum);
                    binding.editConfirmNum.setText(Sms_receiver.receiverNum);
                    binding.confirmNumCounting.setVisibility(View.GONE);
                    binding.getAuthResult.setEnabled(false);
                    myTimer.cancel();
                }
            }else{
                Log.i(TAG,"SendConfirmMessage : " + Sms_receiver.receiverNum);
            }

        }

        @Override
        public void onFinish() {
            myTimer.cancel();
            binding.confirmNumCounting.setText("0 초");
            binding.confirmNumCounting.setVisibility(View.GONE);
            Toast.makeText(mContext,"인증번호의 유효기간이 만료되었습니다.",Toast.LENGTH_LONG).show();
            SND_NUM = "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SmsRetrieverClient client = SmsRetriever.getClient(this);   // this = context
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
            // retriever 성공
            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);    // SMS_RETRIEVED_ACTION 필수입니다.
            registerReceiver(smsReceiver, intentFilter);
            Log.i(TAG,"smsReceiver : " + Sms_receiver.receiverNum);
//            Log.i(TAG,"onResume : " + smsReceiver.receiverNum);
        });

        task.addOnFailureListener(e -> {
            // retriever 실패
        });

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
