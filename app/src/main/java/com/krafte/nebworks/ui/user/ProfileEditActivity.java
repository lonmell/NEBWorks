package com.krafte.nebworks.ui.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkplaceListAdapter;
import com.krafte.nebworks.data.CertiNumData;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.dataInterface.UserInsertInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.dataInterface.UserUpdateInterface;
import com.krafte.nebworks.databinding.ActivityProfileeditBinding;
import com.krafte.nebworks.pop.AlertPopActivity;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
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
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
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

public class ProfileEditActivity extends AppCompatActivity {
    private ActivityProfileeditBinding binding;
    private static final String TAG = "ProfileEditActivity";
    Context mContext;
    int GALLEY_CODE = 10;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();
    GetResultData resultData = new GetResultData();

    //Other 변수
    ArrayList<PlaceListData.PlaceListData_list> mList;
    WorkplaceListAdapter mAdapter = null;
    int listitemsize = 0;
    String place_id = "";
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_NAME = "";
    String USER_LOGIN_METHOD = "";
    String USER_INFO_GENDER = "0";
    String USER_INFO_AUTH = "";
    String user_name = "";
    String user_nickname = "";
    String platform = "";
    String phone = "";
    String editstate = "";


    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/nebworks/";
    String ProfileUrl = "";
    DBConnection dbConnection = new DBConnection();
    Boolean CertiSuccessTF = false;
    Drawable icon_off;
    Drawable icon_on;
    String mem_id = "";
    String mem_kind = "";
    String mem_name = "";
    String mem_nick = "";
    String mem_phone = "";
    String mem_gender = "";
    String mem_img_path = "";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityProfileeditBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        //shardpref Area
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "0");
        editstate = shardpref.getString("editstate", "");
        platform = shardpref.getString("platform", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "0");

        //Singleton Area
        place_id = PlaceCheckData.getInstance().getPlace_id();
        USER_INFO_ID = UserCheckData.getInstance().getUser_id();
        USER_INFO_EMAIL = UserCheckData.getInstance().getUser_account();
        USER_INFO_NAME = UserCheckData.getInstance().getUser_name();
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("USER_INFO_EMAIL : " + USER_INFO_EMAIL);

        setBtnEvent();
        if (USER_INFO_EMAIL.isEmpty() || USER_INFO_ID.isEmpty()) {
            Intent intent = new Intent(mContext, OneButtonPopActivity.class);
            intent.putExtra("data", "사용자정보를 찾을 수 없습니다, 다시 로그인해 주세요.");
            intent.putExtra("left_btn_txt", "닫기");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        }

        myTimer = new MyTimer(60000, 1000);
        UserCheck();
    }

    @Override
    public void onStart() {
        super.onStart();
        ImgfileMaker = ImageNameMaker();
    }

    @Override
    public void onResume() {
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

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("editstate");
            if (editstate.equals("insert")) {
                Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
                intent.putExtra("data", "로그아웃하시겠습니까?");
                intent.putExtra("flag", "로그아웃");
                intent.putExtra("left_btn_txt", "닫기");
                intent.putExtra("right_btn_txt", "로그아웃");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                super.onBackPressed();
            }
        });

        if (saveBitmap != null) {
            binding.clearImg.setVisibility(View.VISIBLE);
            binding.imgPlus.setVisibility(View.GONE);
        } else {
            binding.clearImg.setVisibility(View.GONE);
            binding.imgPlus.setVisibility(View.VISIBLE);
        }

        binding.clearImg.setOnClickListener(v -> {
            try {
                saveBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                saveBitmap.eraseColor(Color.TRANSPARENT);
                binding.profileSetimg.setImageBitmap(saveBitmap);
                binding.profileSetimg.setBackgroundResource(R.drawable.img_box_round);
                ProfileUrl = "";
                binding.clearImg.setVisibility(View.GONE);
                binding.imgPlus.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                dlog.i("clearImg Exception : " + e);
            }
        });

        //------매장 이미지 등록 / 갤러리 열기
        binding.profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLEY_CODE);
        });

        binding.SaveUserBtn.setOnClickListener(v -> {
            if (CheckData()) {
                if (editstate.equals("insert")) {
                    if (USER_INFO_EMAIL.equals("0")) {
                        Toast_Nomal("사용자 정보를 가져올 수 없습니다. 다시 시도해 주세요.");
                        pm.Login(mContext);
                    } else {
                        INPUT_JOIN_DATA();
                    }
                } else {
                    SaveUser();
                }
            }
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
                user_name = s.toString();
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
                phone = s.toString();
            }
        });

        binding.clearNick.setOnClickListener(v -> {
            binding.userNick.setText("");
        });
        binding.clearNick2.setOnClickListener(v -> {
            binding.userName.setText("");
        });
        binding.userNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                user_nickname = s.toString();
            }
        });

        binding.selectMan.setOnClickListener(v -> {
            USER_INFO_GENDER = "1";
            binding.manTxt.setTextColor(Color.parseColor("#ffffff"));
            binding.selectMan.setBackgroundResource(R.drawable.select_full_round);

            binding.womanTxt.setTextColor(Color.parseColor("#000000"));
            binding.selectWoman.setBackgroundResource(R.drawable.select_empty_round);
        });

        binding.selectWoman.setOnClickListener(v -> {
            USER_INFO_GENDER = "2";
            binding.manTxt.setTextColor(Color.parseColor("#000000"));
            binding.selectMan.setBackgroundResource(R.drawable.select_empty_round);

            binding.womanTxt.setTextColor(Color.parseColor("#ffffff"));
            binding.selectWoman.setBackgroundResource(R.drawable.select_full_round);
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
                phone = s.toString();
                if (mem_phone.equals(phone)) {
                    binding.getAuthResult.setVisibility(View.GONE);
                    binding.linear02.setVisibility(View.GONE);
                } else {
                    binding.getAuthResult.setVisibility(View.VISIBLE);
                    binding.linear02.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.getAuthResult.setOnClickListener(v -> {
            if (phone.isEmpty()) {
                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
                intent.putExtra("data", "전화번호를 입력해주세요.");
                intent.putExtra("left_btn_txt", "닫기");
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, 0);
            } else {
                binding.confirmNumCounting.setVisibility(View.VISIBLE);
                SendConfirmMessage();
            }

//            if (user_name.isEmpty()) {
//                Intent intent = new Intent(mContext, OneButtonPopActivity.class);
//                intent.putExtra("data", "이름을 입력해 주세요.");
//                intent.putExtra("left_btn_txt", "닫기");
//                startActivity(intent);
//                overridePendingTransition(R.anim.translate_up, 0);
//            } else
        });
        binding.confirmPhoneBtn.setOnClickListener(view -> {
            if (binding.editConfirmNum.getText().toString().isEmpty()) {
                Toast.makeText(this, "인증번호가 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {
                if (Sms_receiver.receiverNum.equals(SND_NUM) && Sms_receiver.receiverNum.equals(binding.editConfirmNum.getText().toString())) {
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
                    Toast.makeText(this, "유효하지 않은 인증번호 입니다.", Toast.LENGTH_LONG).show();
                }
            }
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
                    dbConnection.ConfrimNumSend(phone, user_name, SND_NUM, hash);
                });
                th.start();
                try {
                    th.join();
                    Thread th2 = new Thread(() -> {
                        dbConnection.ConfrimNumSave(phone, SND_NUM, "insert");
                        String getMessage = resultData.getRESULT().replaceAll("\"", "");
                        Log.i(TAG, "getMessage = " + getMessage);
                        if (getMessage.equals("success")) {
                            dbConnection.ConfrimNumSelect(phone, SND_NUM, "select");
                            CertiNum = certiNumData.getCerti_num();
                            binding.tv03.setText("인증번호 재발송");
                            binding.editConfirmNum.setText("");
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

    public void UserCheck() {
        if(editstate.equals("newPro")){
            String USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL","");
            dlog.i("---------UserCheck---------");
            dlog.i("USER_INFO_EMAIL : " + USER_INFO_EMAIL);
            dlog.i("getMonth : " + (dc.GET_MONTH.length() == 1 ? "0" + dc.GET_MONTH : dc.GET_MONTH));
            dlog.i("---------UserCheck---------");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UserSelectInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            UserSelectInterface api = retrofit.create(UserSelectInterface.class);
            Call<String> call = api.getData(USER_INFO_EMAIL);
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
                                    mem_id = Response.getJSONObject(0).getString("id");
                                    mem_name = Response.getJSONObject(0).getString("name");
                                    mem_nick = Response.getJSONObject(0).getString("nick_name");
                                    mem_phone = Response.getJSONObject(0).getString("phone");
                                    mem_gender = Response.getJSONObject(0).getString("gender");
                                    mem_img_path = Response.getJSONObject(0).getString("img_path");

                                    dlog.i("------UserCheck-------");
                                    USER_INFO_ID = mem_id;
                                    dlog.i("프로필 사진 url : " + mem_img_path);
                                    dlog.i("성명 : " + mem_name);
                                    dlog.i("성별 : " + mem_gender);
                                    dlog.i("닉네임 : " + mem_nick);
                                    dlog.i("전화번호 : " + mem_phone);
                                    dlog.i("------UserCheck-------");

                                    if (!mem_phone.isEmpty()) {
                                        CertiSuccessTF = true;
                                    }
                                    dlog.i("START profileSetimg1");
                                    binding.userName.setText(mem_name);
                                    binding.userNick.setText(mem_nick.isEmpty() ? "" : mem_nick);
                                    binding.userPhone.setText(mem_phone.isEmpty() ? "" : mem_phone);

                                    if (mem_img_path != null) {
                                        dlog.i("mem_img_path : " + mem_img_path.trim());
                                        Glide.with(mContext).load(mem_img_path.trim())
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .skipMemoryCache(true)
                                                .into(binding.profileSetimg);
                                        binding.imgPlus.setVisibility(View.GONE);
                                        ProfileUrl = mem_img_path;
                                    }

                                    if (mem_gender.equals("1")) {
                                        USER_INFO_GENDER = "1";
                                        binding.manTxt.setTextColor(Color.parseColor("#ffffff"));
                                        binding.selectMan.setBackgroundResource(R.drawable.select_full_round);

                                        binding.womanTxt.setTextColor(Color.parseColor("#A1887F"));
                                        binding.selectWoman.setBackgroundResource(R.drawable.select_empty_round);
                                    } else {
                                        USER_INFO_GENDER = "2";
                                        binding.manTxt.setTextColor(Color.parseColor("#A1887F"));
                                        binding.selectMan.setBackgroundResource(R.drawable.select_empty_round);

                                        binding.womanTxt.setTextColor(Color.parseColor("#ffffff"));
                                        binding.selectWoman.setBackgroundResource(R.drawable.select_full_round);
                                    }

                                } catch (Exception e) {
                                    dlog.i("UserCheck Exception : " + e);
                                }
                            } catch (Exception e) {
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
        }else{
            try {
                mem_id = UserCheckData.getInstance().getUser_id();
                mem_name = UserCheckData.getInstance().getUser_name();
                mem_nick = UserCheckData.getInstance().getUser_nick_name();
                mem_phone = UserCheckData.getInstance().getUser_phone();
                mem_gender = UserCheckData.getInstance().getUser_gender();
                mem_img_path = UserCheckData.getInstance().getUser_img_path();

                dlog.i("------UserCheck-------");
                USER_INFO_ID = mem_id;
                dlog.i("프로필 사진 url : " + mem_img_path);
                dlog.i("성명 : " + mem_name);
                dlog.i("성별 : " + mem_gender);
                dlog.i("닉네임 : " + mem_nick);
                dlog.i("전화번호 : " + mem_phone);
                dlog.i("------UserCheck-------");

                if (!mem_phone.isEmpty()) {
                    CertiSuccessTF = true;
                }
                dlog.i("START profileSetimg1");
                binding.userName.setText(mem_name);
                binding.userNick.setText(mem_nick.isEmpty() ? "" : mem_nick);
                binding.userPhone.setText(mem_phone.isEmpty() ? "" : mem_phone);

                if (mem_img_path != null) {
                    dlog.i("mem_img_path : " + mem_img_path.trim());
                    Glide.with(mContext).load(mem_img_path.trim())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.profileSetimg);
                    binding.imgPlus.setVisibility(View.GONE);
                    ProfileUrl = mem_img_path;
                }

                if (mem_gender.equals("1")) {
                    USER_INFO_GENDER = "1";
                    binding.manTxt.setTextColor(Color.parseColor("#ffffff"));
                    binding.selectMan.setBackgroundResource(R.drawable.select_full_round);

                    binding.womanTxt.setTextColor(Color.parseColor("#A1887F"));
                    binding.selectWoman.setBackgroundResource(R.drawable.select_empty_round);
                } else {
                    USER_INFO_GENDER = "2";
                    binding.manTxt.setTextColor(Color.parseColor("#A1887F"));
                    binding.selectMan.setBackgroundResource(R.drawable.select_empty_round);

                    binding.womanTxt.setTextColor(Color.parseColor("#ffffff"));
                    binding.selectWoman.setBackgroundResource(R.drawable.select_full_round);
                }

            } catch (Exception e) {
                dlog.i("UserCheck Exception : " + e);
            }
        }
    }

    private boolean CheckData() {
        user_name = binding.userName.getText().toString();
        user_nickname = binding.userNick.getText().toString();
        Log.d("user_nickname", user_nickname);
        shardpref.putString("USER_INFO_NICKNAME", user_nickname);
        phone = binding.userPhone.getText().toString();

        dlog.i("------CheckData-------");
        dlog.i("프로필 사진 url : " + ProfileUrl);
        dlog.i("성명 : " + user_name);
        dlog.i("닉네임 : " + user_nickname);
        dlog.i("전화번호 : " + phone);
        dlog.i("------CheckData-------");

        if (phone.isEmpty()) {
            Toast.makeText(mContext, "전화번호을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (ProfileUrl.isEmpty()) {
//            Toast.makeText(mContext, "프로필 사진을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!CertiSuccessTF) {
            Toast.makeText(mContext, "번호 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void SaveUser() {
        dlog.i("------SaveUser-------");
        dlog.i("USER ID : " + USER_INFO_ID);
        dlog.i("프로필 사진 url : " + ProfileUrl);
        dlog.i("이전 프로필 사진 : " + ProfileUrl);
        dlog.i("성명 : " + user_name);
        dlog.i("닉네임 : " + user_nickname);
        dlog.i("전화번호 : " + phone);
        shardpref.putString("name", user_name);
        dlog.i("------SaveUser-------");
        if (!USER_INFO_ID.equals("0")) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UserUpdateInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            UserUpdateInterface api = retrofit.create(UserUpdateInterface.class);
            Call<String> call = api.getData(USER_INFO_ID, (user_name.isEmpty() ? user_nickname : user_name), user_nickname, phone, USER_INFO_GENDER, ProfileUrl);
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
                                        if (!ProfileUrl.isEmpty() && saveBitmap != null) {
                                            saveBitmapAndGetURI();
                                        }
                                        Toast_Nomal("프로필 저장이 완료되었습니다.");
                                        String return_page = shardpref.getString("retrun_page", "");
                                        if (return_page.equals("MoreActivity")) {
                                            pm.MoreBack(mContext);
                                        } else {
                                            String USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL","");
//                                            UserCheck(USER_INFO_EMAIL);
                                            if(!USER_INFO_AUTH.equals("-1")){
                                                shardpref.putString("USER_INFO_AUTH",USER_INFO_AUTH);
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                pm.PlaceList(mContext);
                                            }else{
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                pm.AuthSelect(mContext);
                                            }
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
        } else {
            Toast.makeText(mContext, "사용자 정보를 가져 올수 없습니다.\n다시 로그인해주세요", Toast.LENGTH_SHORT).show();
            DataAllRemove();
        }

    }

    public void INPUT_JOIN_DATA() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserInsertInterface api = retrofit.create(UserInsertInterface.class);
        Call<String> call = api.getData(USER_INFO_EMAIL, user_name, user_nickname, "", phone, USER_INFO_GENDER, ProfileUrl, platform);
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
                            shardpref.remove("editstate");
                             kakaoChannel();
                            binding.loginAlertText.setVisibility(View.GONE);
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
                                    if (Response.length() != 0) {
                                        String id       = Response.getJSONObject(0).getString("id");
                                        String name     = Response.getJSONObject(0).getString("name");
                                        String phone    = Response.getJSONObject(0).getString("phone");
                                        String platform = Response.getJSONObject(0).getString("platform");
                                        String user_auth = Response.getJSONObject(0).getString("user_auth");
                                        try {
                                            dlog.i("------UserCheck-------");
                                            dlog.i("성명 : " + name);
                                            dlog.i("사용자 권한 : " + user_auth);
                                            dlog.i("------UserCheck-------");
                                            if(!user_auth.equals("-1")){
                                                shardpref.putString("USER_INFO_AUTH",user_auth);
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                pm.PlaceList(mContext);
                                            }else{
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                pm.AuthSelect(mContext);
                                            }
                                        } catch (Exception e) {
                                            dlog.i("UserCheck Exception : " + e);
                                        }
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

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;
    //카카오 로그인 콜백
    Function2<OAuthToken, Throwable, Unit> kakaoCallback = (oAuthToken, throwable) -> {
        if (oAuthToken != null) {
            Log.i(TAG, "kakaoCallback oAuthToken not null");
        }
        if (throwable != null) {
            Log.i(TAG, "kakaoCallback throwable not null");
            Log.i("Kakao", "Message : " + throwable.getLocalizedMessage());
            if (Objects.equals(throwable.getLocalizedMessage(), "user cancelled.")) {
            }
        }
        return null;
    };

    private void DataAllRemove() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        shardpref.clear();
        shardpref.putBoolean("USER_LOGIN_CONFIRM", false);
        shardpref.putString("USER_LOGIN_METHOD", USER_LOGIN_METHOD);
        shardpref.remove("ALARM_ONOFF");
        shardpref.remove("USER_LOGIN_METHOD");
        shardpref.putBoolean("isFirstLogin", true);

        if (USER_LOGIN_METHOD.equals("Google")) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, task -> {
                        pm.Login(mContext);
                    });
        } else if (USER_LOGIN_METHOD.equals("Kakao")) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        pm.Login(mContext);
                        return null;
                    }
                });
            }, 100); //0.5초 후 인트로 실행
        } else {
            pm.Login(mContext);
        }
        finish();
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
                    ProfileUrl = "http://krafte.net/NEBWorks/image/user_img/" + file_name;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                binding.imgPlus.setVisibility(View.GONE);
                binding.clearImg.setVisibility(View.GONE);
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    //절대경로를 구한다.
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        dlog.i("columnIndex = " + columnIndex);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);


        cursor.close();
        return url;
    }

    @SuppressLint({"SimpleDateFormat", "LongLogTag"})
    public Uri saveBitmapAndGetURI() {
        //Create Bitmap
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

            ProfileUrl = "http://krafte.net/NEBWorks/image/user_img/" + file_name;
            saveBitmapToFile(file);

            dlog.e("사인 저장 경로 : " + ProfileUrl);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            RetrofitInterface retrofitInterface = ApiClient.getApiClient().create(RetrofitInterface.class);
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
        @POST("upload_user_img.php")
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
                Log.i(TAG, "SendConfirmMessage : " + Sms_receiver.receiverNum);
                if (SND_NUM.equals(Sms_receiver.receiverNum)) {
                    Log.i(TAG, "SendConfirmMessage : " + Sms_receiver.receiverNum);
                    binding.confirmPhoneBtn.setBackgroundColor(Color.parseColor("#6395EC"));
                    binding.confirmPhoneBtn.setTextColor(Color.parseColor("#000000"));
                    binding.editConfirmNum.setText(Sms_receiver.receiverNum);
                    binding.confirmNumCounting.setVisibility(View.GONE);
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
            Toast.makeText(mContext, "인증번호의 유효기간이 만료되었습니다.", Toast.LENGTH_LONG).show();
            SND_NUM = "";
        }
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

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        shardpref.remove("editstate");
        if (editstate.equals("insert") || editstate.equals("newPro")) {
            Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
            intent.putExtra("data", "로그아웃하시겠습니까?");
            intent.putExtra("flag", "로그아웃");
            intent.putExtra("left_btn_txt", "닫기");
            intent.putExtra("right_btn_txt", "로그아웃");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            super.onBackPressed();
        }
    }

    public void kakaoChannel() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag", "채널");
        intent.putExtra("data", "카카오톡 채널을 추가하여 \n 다양한 소식을 접해보세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "추가하기");
        startActivity(intent);
        overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
