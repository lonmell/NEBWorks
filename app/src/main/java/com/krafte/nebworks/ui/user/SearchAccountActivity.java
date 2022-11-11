package com.krafte.nebworks.ui.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkplaceListAdapter;
import com.krafte.nebworks.data.CertiNumData;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.UserNumSelectInterface;
import com.krafte.nebworks.databinding.ActivitySearchemailBinding;
import com.krafte.nebworks.pop.AlertPopActivity;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.HashCode;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RandomOut;
import com.krafte.nebworks.util.RetrofitConnect;
import com.krafte.nebworks.util.Sms_receiver;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SearchAccountActivity extends AppCompatActivity {
    private ActivitySearchemailBinding binding;
    private static final String TAG = "VerificationActivity";
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    GetResultData resultData = new GetResultData();
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();
    DBConnection dbConnection = new DBConnection();

    //Other 변수
    ArrayList<PlaceListData.PlaceListData_list> mList;
    WorkplaceListAdapter mAdapter = null;
    int listitemsize = 0;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_EMAIL = "";
    String findkind = "";

    //사용자 정보 체크
    String id = "";
    String name = "";
    String email = "";
    String employee_no = "";
    String department = "";
    String jikchk = "";
    String img_path = "";
    String return_page = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivitySearchemailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            findkind = shardpref.getString("findkind","");

            if(findkind.equals("email")){
                binding.tv04.setText("이메일 찾기");
            }else if(findkind.equals("password")){
                binding.tv04.setText("비밀번호 변경하기");
            }
            myTimer = new MyTimer(60000, 1000);

            onBtnEvent();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

    }


    String Uname = "";
    String UPhone = "";
    boolean allcheck = false;
    boolean Uservice01 = false;
    boolean Uservice02 = false;
    boolean Uservice03 = false;
    boolean Uservice04 = false;
    Boolean CertiSuccessTF = false;

    private void onBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            pm.Login(mContext);
        });

        binding.userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Uname = s.toString();
            }
        });
        binding.userPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                UPhone = s.toString();
            }
        });

        binding.getAuthResult.setOnClickListener(v -> {

            if (Uname.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "이름을 입력해 주세요.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            } else if (UPhone.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "전화번호를 입력해주세요.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            } else {
                binding.confirmNumCounting.setVisibility(View.VISIBLE);
                SendConfirmMessage();
            }
        });

        binding.confirmPhoneBtn.setOnClickListener(view -> {
            if (binding.editConfirmNum.getText().toString().isEmpty()) {
                Toast_Nomal("인증번호가 입력되지 않았습니다");
//                Toast.makeText(this, "인증번호가 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {
                if (Sms_receiver.receiverNum.equals(SND_NUM) && Sms_receiver.receiverNum.equals(binding.editConfirmNum.getText().toString())) {
                    CertiSuccessTF = true;
                    Toast_Nomal("인증번호가 확인되었습니다.");
//                    Toast.makeText(this, "인증번호가 확인되었습니다.", Toast.LENGTH_LONG).show();
                    binding.confirmPhoneBtn.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.confirmPhoneBtn.setText("인증완료");
                    binding.confirmPhoneBtn.setTextColor(Color.parseColor("#000000"));
                    binding.confirmPhoneBtn.setClickable(false);

                    binding.getAuthResult.setVisibility(View.INVISIBLE);

                    binding.userName.setEnabled(false);
                    binding.userName.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.userPhone.setEnabled(false);
                    binding.userPhone.setBackgroundColor(Color.parseColor("#dcdcdc"));

                    Thread th3 = new Thread(() -> {
                        String SND_PHONE = binding.userPhone.getText().toString();

                        dbConnection.ConfrimNumSave(SND_PHONE, "", "delete");

                        String getMessage = resultData.getRESULT().replaceAll("\"", "");
                        Log.i(TAG, "getMessage = " + getMessage);
                    });
                    th3.start();
                    try {
                        th3.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (binding.editConfirmNum.getText().toString().equals(CertiNum)) {
                    CertiSuccessTF = true;
                    Toast.makeText(this, "인증번호가 확인되었습니다.", Toast.LENGTH_LONG).show();
                    binding.confirmPhoneBtn.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.confirmPhoneBtn.setText("인증완료");
                    binding.confirmPhoneBtn.setTextColor(Color.parseColor("#000000"));
                    binding.confirmPhoneBtn.setClickable(false);

                    binding.getAuthResult.setVisibility(View.INVISIBLE);

                    binding.userName.setEnabled(false);
                    binding.userName.setBackgroundColor(Color.parseColor("#dcdcdc"));
                    binding.userPhone.setEnabled(false);
                    binding.userPhone.setBackgroundColor(Color.parseColor("#dcdcdc"));

                    Thread th3 = new Thread(() -> {
                        String SND_PHONE = binding.userPhone.getText().toString();

                        dbConnection.ConfrimNumSave(SND_PHONE, "", "delete");

                        String getMessage = resultData.getRESULT().replaceAll("\"", "");
                        Log.i(TAG, "getMessage = " + getMessage);
                    });
                    th3.start();
                    try {
                        th3.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    CertiSuccessTF = false;
                    Toast_Nomal("유효하지 않은 인증번호 입니다.");
//                    Toast.makeText(this, "유효하지 않은 인증번호 입니다.", Toast.LENGTH_LONG).show();
                }
            }
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

        binding.tv04.setOnClickListener(v -> {
            dlog.i("-----회원가입-----");
            dlog.i("이름 : " + Uname);
            dlog.i("번호 : " + UPhone);
            dlog.i("인증여부 : " + CertiSuccessTF);
            dlog.i("약관동의여부 1: " + Uservice01);
            dlog.i("약관동의여부 2: " + Uservice02);
            dlog.i("약관동의여부 3: " + Uservice03);
            dlog.i("약관동의여부 4: " + Uservice04);
            dlog.i("-----회원가입-----");
            if (Uname.isEmpty()) {
                Toast_Nomal("이름을 입력해주세요.");
//                Toast.makeText(mContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (!CertiSuccessTF) {
                Toast_Nomal("번호 인증이 필요합니다.");
//                Toast.makeText(mContext, "번호 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
            } else if (!Uservice01 || !Uservice02 || !Uservice03 || !Uservice04) {
                Toast_Nomal("필수약관에 동의해주세요");
//                Toast.makeText(mContext, "필수약관에 동의해주세요", Toast.LENGTH_SHORT).show();
            } else {
                UserCheck();
            }
        });
    }

    public void UserCheck() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserNumSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserNumSelectInterface api = retrofit.create(UserNumSelectInterface.class);
        Call<String> call = api.getData(Uname, UPhone);
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
                                    String account = Response.getJSONObject(0).getString("account");
                                    String password = Response.getJSONObject(0).getString("password");
                                    String phone = Response.getJSONObject(0).getString("phone");
                                    String gender = Response.getJSONObject(0).getString("gender");
                                    String img_path = Response.getJSONObject(0).getString("img_path");

                                    dlog.i("Find id : " + Response.getJSONObject(0).getString("id"));
                                    dlog.i("Find Email : " + Response.getJSONObject(0).getString("account"));
                                    shardpref.putString("USER_INFO_ID",id);
                                    shardpref.putString("USER_INFO_NAME",name);
                                    shardpref.putString("USER_INFO_EMAIL",account);
                                    shardpref.putString("USER_INFO_PW",password);
                                    shardpref.putString("USER_INFO_PHONE",phone);
                                    shardpref.putString("USER_INFO_GENDER",gender);
                                    shardpref.putString("USER_INFO_IMG",img_path);

                                    if(findkind.equals("email")){
                                        pm.FindEmail(mContext);
                                    }else if(findkind.equals("password")){
                                        pm.ChangePw(mContext);
                                    }
                                } else {
                                    dlog.i("Response 2: " + response.body().length());
                                    Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                                    intent.putExtra("data", "가입하지 않은 사용자입니다.");
                                    intent.putExtra("left_btn_txt", "닫기");
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    shardpref.putString("USER_INFO_NAME", Uname);
                                    shardpref.putString("USER_INFO_PHONE", UPhone);
                                    shardpref.putString("USER_LOGIN_METHOD", "NEB");
//                                    pm.Join(mContext);
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


    @Override
    protected void onResume() {
        super.onResume();

        SmsRetrieverClient client = SmsRetriever.getClient(this);   // this = context
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
            // retriever 성공
            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);    // SMS_RETRIEVED_ACTION 필수입니다.
            registerReceiver(smsReceiver, intentFilter);
            Log.i(TAG, "smsReceiver : " + Sms_receiver.receiverNum);
        });

        task.addOnFailureListener(e -> {
            // retriever 실패
        });

    }

    //사용자 확인하는 소스 여기에 넣고 전역변수로 구분할것
    MyTimer myTimer;
    Sms_receiver smsReceiver;
    RandomOut ro = new RandomOut();
    CertiNumData certiNumData = new CertiNumData();

    String getMessage = "";
    String SND_NUM = "";
    String CertiNum = "";

    public void SendConfirmMessage() {
        Thread th_check = new Thread(() -> {
            Log.i(TAG, "getMessage = " + getMessage);
        });
        th_check.start();
        try {
            th_check.join();
            if (getMessage.equals("1")) {
                Intent intent = new Intent(mContext, AlertPopActivity.class);
                intent.putExtra("data", "이미 가입한 내역이 있습니다.");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            } else {
                myTimer.start();
                Thread th = new Thread(() -> {
                    SND_NUM = ro.getRandomNum(7);

                    /*단말기별 해시코드*/
                    String DeviceHashCode = String.valueOf(HashCode.getAppSignatures(this));
                    String hash = DeviceHashCode.substring(DeviceHashCode.indexOf("[") + 1, DeviceHashCode.indexOf("]"));
                    Log.i(TAG, "hash : " + hash);
                    dbConnection.ConfrimNumSend(UPhone, Uname, SND_NUM, hash);
                });
                th.start();
                try {
                    th.join();
                    Thread th2 = new Thread(() -> {
                        dbConnection.ConfrimNumSave(UPhone, SND_NUM, "insert");

                        String getMessage = resultData.getRESULT().replaceAll("\"", "");
                        Log.i(TAG, "getMessage = " + getMessage);
                        if (getMessage.equals("success")) {
                            dbConnection.ConfrimNumSelect(UPhone, SND_NUM, "select");
                            CertiNum = certiNumData.getCerti_num();
                            binding.getAuthResult.setEnabled(false);
                            Log.i(TAG, "CertiNum : " + CertiNum);
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

    class MyTimer extends CountDownTimer {
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {

            smsReceiver = new Sms_receiver();
            binding.confirmNumCounting.setText(millisUntilFinished / 1000 + " 초");
            binding.confirmNumCounting.setVisibility(View.VISIBLE);

            if (!Sms_receiver.receiverNum.isEmpty()) {
                if (SND_NUM.equals(Sms_receiver.receiverNum)) {
                    Log.i(TAG, "SendConfirmMessage : " + Sms_receiver.receiverNum);
                    binding.confirmPhoneBtn.setBackgroundColor(Color.parseColor("#6395EC"));
                    binding.confirmPhoneBtn.setTextColor(Color.parseColor("#000000"));
                    binding.editConfirmNum.setText(Sms_receiver.receiverNum);
                    binding.confirmNumCounting.setVisibility(View.GONE);
                    binding.getAuthResult.setEnabled(false);
                    myTimer.cancel();
                }
            } else {
                Log.i(TAG, "SendConfirmMessage : " + Sms_receiver.receiverNum);
            }

        }

        @Override
        public void onFinish() {
            myTimer.cancel();
            binding.confirmNumCounting.setText("0 초");
            binding.confirmNumCounting.setVisibility(View.GONE);
            Toast_Nomal("인증번호의 유효기간이 만료되었습니다.");
            SND_NUM = "";
        }
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
