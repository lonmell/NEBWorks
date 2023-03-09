package com.krafte.nebworks.ui.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.dataInterface.UserInsertInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityLoginBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.AES256Util;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.DeviceInfoUtil;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.HashCode;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
/*
 * 2022-10-04 방창배 작성 카카오 로그인 추가
 * 2022-10-05 방창배 수정 구글 로그인 추가
 *
 * */
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();
    AES256Util aes256Util;
    DeviceInfoUtil diu = new DeviceInfoUtil();

    //Other 변수
    int turnvisible = 0;
    String keyHash = "";

    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    // shared 저장값

    //Login_Method => 가입한 루트를 표시 ( HEYPASS - 그냥 앱 자체 기능으로 가입 / Kakao - 카카오로 가입 )
    public String USER_LOGIN_METHOD = "0";
    public boolean USER_LOGIN_CONFIRM = false;


    //Kakao 가입/로그인시 불러오는 값
    String GET_NAME = "";
    String GET_NICKNAME = "";
    String GET_ACCOUNT_EMAIL = "";
    String GET_PROFILE_URL = "";
    String GET_USER_PHONE = "";
    String GET_USER_AGENCY = "";
    String GET_USER_BIRTH = "";
    String GET_USER_AGEROUNGE = "";
    String GET_USER_SEX = "";
    String GET_USER_PW = "";
    String GET_USER_JOIN_DATE = "";
    String GET_USER_AUTH = "9";
    String GET_USER_SERVICE = "S";
    boolean GET_JOIN_CONFIRM = false;
    String USER_INFO_PW = "";

    //Naver
    String ClientID = "cN1sIOhyOshPLKgNL4Sj";
    String ClientSecret = "iFS5etlgYt";
    String ClientName = "넵";
    NaverIdLoginSDK naverIdLoginSDK = NaverIdLoginSDK.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        try {
            aes256Util = new AES256Util("dkwj12fisne349vnlkw904mlk13490nv");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        permissionCheck();
        KakaoSetting();
        GoogleSetting();
        NaverSetting();
        onEvent();
        if (!GET_ACCOUNT_EMAIL.isEmpty() && (!USER_LOGIN_METHOD.isEmpty() && USER_LOGIN_METHOD.equals("NEB"))) {
            binding.deviceNumEdit.setText(GET_ACCOUNT_EMAIL);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //shardpref Area
        GET_ACCOUNT_EMAIL   = shardpref.getString("USER_INFO_EMAIL","");
        USER_INFO_PW        = shardpref.getString("USER_INFO_PW","");
        USER_LOGIN_METHOD   = shardpref.getString("USER_LOGIN_METHOD", "-99");

        if (!USER_LOGIN_METHOD.equals("-99")) {
            if (!GET_ACCOUNT_EMAIL.equals("-99")) {
                if (!GET_ACCOUNT_EMAIL.isEmpty() && (!USER_LOGIN_METHOD.isEmpty() && USER_LOGIN_METHOD.equals("NEB"))) {
                    binding.deviceNumEdit.setText(GET_ACCOUNT_EMAIL);
                }
            }
            if (!USER_INFO_PW.equals("-99")) {
                binding.pwdEdit.setText(USER_INFO_PW);
            }
            if (!GET_ACCOUNT_EMAIL.equals("-99") && !USER_INFO_PW.equals("-99")) {
                LoginCheck(GET_ACCOUNT_EMAIL, USER_INFO_PW, "NEB");
            }
        } else {
            shardpref.clear();
        }
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    @Override
    public void onResume(){
        super.onResume();
        BtnOneCircleFun(true);
    }

    private void KakaoSetting() {
        keyHash = Utility.INSTANCE.getKeyHash(this);
        dlog.i("SMS에서 사용할 HASH : " + HashCode.getAppSignatures(this));
        String Release_keyHash = com.kakao.util.helper.Utility.getKeyHash(this /* context */);

        dlog.i("KAKAO keyHash = " + keyHash);
        dlog.i("Release_keyHash = " + Release_keyHash);

        // Kakao SDK 객체 초기화
        KakaoSdk.init(this, getString(R.string.kakao_native_key));

        binding.kakaoLoginArea.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            binding.loginAlertText.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.raw.neb_loding_whtie)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(binding.loadingView);
            shardpref.putString("USER_LOGIN_METHOD", "Kakao");
            handler.postDelayed(() -> {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(mContext)) {
                    dlog.i("카카오톡 앱이 설치 되어있을때 : kakaoCallback = " + kakaoCallback);
                    UserApiClient.getInstance().loginWithKakaoTalk(mContext, kakaoCallback);
                } else {
                    dlog.i("카카오톡 앱이 설치 안 되어있을때 : kakaoCallback = " + kakaoCallback);
                    UserApiClient.getInstance().loginWithKakaoAccount(mContext, kakaoCallback);
                }
            }, 1000); //0.5초 후 인트로 실행
        });
    }


    private void NaverSetting() {
        try {
            naverIdLoginSDK.initialize(LoginActivity.this, ClientID, ClientSecret, ClientName);
            naverIdLoginSDK.logout();
            naverIdLoginSDK.setShowMarketLink(true);
            naverIdLoginSDK.setShowBottomTab(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OAuthLoginCallback oAuthLoginCallback = new OAuthLoginCallback() {
        @Override
        public void onSuccess() {
            NidOAuthLogin nidOAuthLogin = new NidOAuthLogin();

            nidOAuthLogin.callProfileApi(new NidProfileCallback<NidProfileResponse>() {

                @Override
                public void onSuccess(NidProfileResponse nidProfileResponse) {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    dlog.i("NaverSetting onSuccess");
                    dlog.i("NaverSetting getAccessToken: " + naverIdLoginSDK.getAccessToken());
                    dlog.i("NaverSetting getRefreshToken: " + naverIdLoginSDK.getRefreshToken());

                    //프로필 가져오는 코드
                    NidProfileCallback<NidProfileResponse> profileCallback = new NidProfileCallback<NidProfileResponse>() {
                        @Override
                        public void onSuccess(NidProfileResponse nidProfileResponse) {
//                            Toast.makeText(getApplicationContext(), "$response", Toast.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                GET_NAME = nidProfileResponse.getProfile().getName();
                                GET_NICKNAME = nidProfileResponse.getProfile().getNickname();
                                GET_ACCOUNT_EMAIL = nidProfileResponse.getProfile().getEmail();
                                GET_PROFILE_URL = nidProfileResponse.getProfile().getProfileImage();
                                GET_USER_PHONE = "";
                                GET_USER_AGENCY = "";
                                GET_USER_BIRTH = "";
                                GET_USER_AGEROUNGE = "";
                                GET_USER_SEX = nidProfileResponse.getProfile().getGender();
                                GET_USER_PW = "";
                                GET_USER_JOIN_DATE = dc.GET_TIME;
                                GET_USER_AUTH = "9";
                                GET_USER_SERVICE = "S";
                                USER_LOGIN_METHOD = "Naver";
                                GET_JOIN_CONFIRM = !String.valueOf(nidProfileResponse.getProfile().getId()).isEmpty();

                                shardpref.putString("USER_LOGIN_METHOD", USER_LOGIN_METHOD);
                                shardpref.putBoolean("USER_LOGIN_CONFIRM", true);

                                dlog.i( "Kakao id =" + nidProfileResponse.getProfile().getId());
                                dlog.i( "GET_NAME =" + GET_NAME);
                                dlog.i( "GET_NICKNAME =" + GET_NICKNAME);
                                dlog.i( "GET_ACCOUNT_EMAIL =" + GET_ACCOUNT_EMAIL);
                                dlog.i( "GET_PROFILE_URL =" + GET_PROFILE_URL);
                                dlog.i( "GET_USER_PHONE =" + GET_USER_PHONE);
                                dlog.i( "GET_USER_BIRTH =" + GET_USER_BIRTH);
                                dlog.i( "GET_USER_AGEROUNGE =" + GET_USER_AGEROUNGE);
                                dlog.i( "GET_USER_SEX =" + GET_USER_SEX);
                                dlog.i( "GET_JOIN_CONFIRM = " + GET_JOIN_CONFIRM);
                                dlog.i( "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

                                if (GET_ACCOUNT_EMAIL.isEmpty()) {
                                    Toast.makeText(mContext, "네트워크 통신연결이 불안정 합니다.", Toast.LENGTH_SHORT).show();
                                } else {
            //                        UserCheckData.getInstance().setUser_id(getid);
                                    UserCheckData.getInstance().setUser_name(GET_NAME);
                                    UserCheckData.getInstance().setUser_nick_name(GET_NICKNAME);
                                    UserCheckData.getInstance().setUser_account(GET_ACCOUNT_EMAIL);
                                    UserCheckData.getInstance().setUser_password("");
                                    UserCheckData.getInstance().setUser_gender(GET_USER_SEX);
                                    UserCheckData.getInstance().setUser_phone(GET_USER_PHONE);
                                    UserCheckData.getInstance().setUser_img_path(GET_PROFILE_URL);
                                    UserCheckData.getInstance().setUser_platform(USER_LOGIN_METHOD);
                                    LoginCheck(GET_ACCOUNT_EMAIL, "", USER_LOGIN_METHOD);
                                }

                            }, 1000); //1초 후 인트로 실행
                        }

                        @Override
                        public void onFailure(int i, @NonNull String s) {
                            String errorCode = naverIdLoginSDK.getLastErrorCode().getCode();
                            String errorDescription = naverIdLoginSDK.getLastErrorDescription();
                            Toast.makeText(getApplicationContext(), "errorCode: $errorCode, errorDesc: $errorDesc", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int i, @NonNull String s) {
                            onFailure(i, s);
                        }
                    };


                    nidOAuthLogin.callProfileApi(profileCallback);
                }

                @Override
                public void onFailure(int i, @NonNull String s) {
                    String errorCode = naverIdLoginSDK.getLastErrorCode().getCode();
                    String errorDescription = naverIdLoginSDK.getLastErrorDescription();
                    Toast.makeText(getApplicationContext(), "errorCode: $errorCode, errorDesc: $errorDesc", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int i, @NonNull String s) {

                }
            });
        }

        @Override
        public void onFailure(int i, @NonNull String s) {

        }

        @Override
        public void onError(int i, @NonNull String s) {

        }
    };

    private void GoogleSetting() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        binding.googleLoginArea.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            Glide.with(this).load(R.raw.neb_loding_whtie)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(binding.loadingView);
            shardpref.putString("USER_LOGIN_METHOD", "Google");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void onEvent() {
        binding.buttonOAuthLoginImg.setOnClickListener(v -> {
            BtnOneCircleFun(false);
//            int sdkinfo = diu.getDeviceSdk();
//            if(sdkinfo > 31){
//                Toast_Nomal("현재 버전에서는 사용할 수 없는 기능입니다.");
//            }else{
//                naverIdLoginSDK.authenticate(LoginActivity.this, oAuthLoginCallback);
//            }
            naverIdLoginSDK.authenticate(LoginActivity.this, oAuthLoginCallback);
        });

        binding.loginBtn.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            String email = binding.deviceNumEdit.getText().toString();
            String pw = binding.pwdEdit.getText().toString();
            LoginCheck(email, pw, "NEB");
//            if(email.equals("guest")){
//                LoginCheck(email);
//            }else{
//                LockTost();
//            }
        });

        binding.joinBtn.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            pm.JoinBefore(mContext);
        });
        binding.turnPwdChar.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            if (turnvisible == 0) {
                turnvisible = 1;
                binding.pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                binding.turnEye.setBackgroundResource(R.drawable.eye);
            } else {
                turnvisible = 0;
                binding.turnEye.setBackgroundResource(R.drawable.visible);
                binding.pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        binding.findEmail.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            shardpref.putString("findkind", "email");
            pm.SearchEmail(mContext);
        });

        binding.findPw.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            shardpref.putString("findkind", "password");
            pm.SearchEmail(mContext);
        });

        binding.ltdTv.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            clickcnt++;
            if (clickcnt == 3) {
                clickcnt = 0;

            }
        });
    }

    int clickcnt = 0;

    private void LockTost() {
        Toast.makeText(mContext, "잠겨있는 기능입니다.", Toast.LENGTH_SHORT).show();
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
                                                UserCheckData.getInstance().setUser_id(id);
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                if (name.isEmpty() || phone.isEmpty()) {
                                                    shardpref.putString("editstate","newPro");
                                                    pm.ProfileEdit(mContext);
                                                } else {
                                                    getPlaceList(id, user_auth);
                                                }
                                            }else{
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                if (name.isEmpty() || phone.isEmpty()) {
                                                    shardpref.putString("editstate","newPro");
                                                    pm.ProfileEdit(mContext);
                                                } else {
                                                    pm.AuthSelect(mContext);
                                                }
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

    int store_cnt = 0;
    public void getPlaceList(String id, String user_auth) {
        dlog.i("------GetPlaceList------");
        dlog.i("USER_INFO_ID : " + id);
        dlog.i("USER_INFO_AUTH : " + user_auth);
        dlog.i("------GetPlaceList------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData("", id, user_auth);
        //
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                            try {
                                //Array데이터를 받아올 때
                                store_cnt = 0;
                                JSONArray Response = new JSONArray(jsonResponse);
                                dlog.i("SIZE INTRO : " + Response.length());
//                                if (Response.length() == 0) {
//                                    pm.AuthSelect(mContext);
//                                }
                                if (Response.length() == 1) {
                                    try {
                                        dlog.i("place_latitude : " + shardpref.getString("place_latitude", ""));
                                        dlog.i("place_longitude : " + shardpref.getString("place_longitude", ""));
                                        String owner_id = Response.getJSONObject(0).getString("owner_id");
                                        String place_name = Response.getJSONObject(0).getString("name");
                                        String myid = shardpref.getString("USER_INFO_ID", "0");
                                        String place_id = Response.getJSONObject(0).getString("id");
                                        String save_kind = Response.getJSONObject(0).getString("save_kind");
                                        String accept_state = Response.getJSONObject(0).getString("accept_state");
                                        String place_imgpath = Response.getJSONObject(0).getString("img_path");
                                        dlog.i("owner_id : " + owner_id);
                                        dlog.i("place_name : " + place_name);
                                        dlog.i("myid : " + myid);
                                        dlog.i("place_id : " + place_id);
                                        dlog.i("save_kind : " + save_kind);
                                        dlog.i("accept_state : " + accept_state);
                                        dlog.i("place_imgpath : " + place_imgpath);

                                        shardpref.putString("place_id", place_id);
                                        shardpref.putString("place_name", place_name);
                                        shardpref.putString("place_imgpath", place_imgpath);
                                        if (save_kind.equals("0")) {
                                            //임시저장된 매장
                                            pm.PlaceEditGo(mContext);
                                        } else {
                                            //저장된 매장
                                            if (accept_state.equals("null")) {
                                                if (!owner_id.equals(id)) {
                                                    accept_state = "1";
                                                } else {
                                                    accept_state = "0";
                                                }
                                            }
                                            shardpref.putInt("accept_state", Integer.parseInt(accept_state));
                                            // ConfirmUserPlacemember(place_id, myid, owner_id, place_name);
                                            shardpref.putInt("SELECT_POSITION", 0);
                                            if (user_auth.equals("0")) {
                                                pm.Main(mContext);
                                            } else {
                                                pm.Main2(mContext);
                                            }
//                                                    }
                                        }
                                    } catch (JSONException e) {
                                        dlog.i("GetPlaceList OnItemClickListener Exception :" + e);
                                    }
                                } else {
                                    shardpref.remove("event");
                                    pm.PlaceList(mContext);
                                }
                                dlog.i("SetNoticeListview Thread run! ");
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

    private void getKaKaoProfile() {
        UserApiClient.getInstance().me((user, throwable) -> {
            if (user != null) {
                binding.loginAlertText.setVisibility(View.VISIBLE);
                dlog.i("!USER_LOGIN_METHOD.equals(NEB)");

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    GET_NAME = Objects.requireNonNull(Objects.requireNonNull(user.getKakaoAccount()).getProfile()).getNickname();
                    GET_ACCOUNT_EMAIL = Objects.requireNonNull(user.getKakaoAccount()).getEmail();
                    GET_PROFILE_URL = Objects.requireNonNull(user.getKakaoAccount().getProfile()).getProfileImageUrl();
                    GET_USER_PHONE = "";
                    GET_USER_AGENCY = "";
                    GET_USER_BIRTH = user.getKakaoAccount().getBirthyear() + "/" + user.getKakaoAccount().getBirthday();
                    GET_USER_AGEROUNGE = String.valueOf(user.getKakaoAccount().getAgeRange());
                    GET_USER_SEX = String.valueOf(user.getKakaoAccount().getGender());
                    GET_USER_PW = "";
                    GET_USER_JOIN_DATE = dc.GET_TIME;
                    GET_USER_AUTH = "9";
                    GET_USER_SERVICE = "S";
                    USER_LOGIN_METHOD = "Kakao";
                    GET_JOIN_CONFIRM = !String.valueOf(user.getId()).isEmpty();

                    shardpref.putString("USER_LOGIN_METHOD", USER_LOGIN_METHOD);
                    shardpref.putBoolean("USER_LOGIN_CONFIRM", true);

                    dlog.i( "Kakao id =" + user.getId());
                    dlog.i( "GET_NAME =" + GET_NAME);
                    dlog.i( "GET_ACCOUNT_EMAIL =" + GET_ACCOUNT_EMAIL);
                    dlog.i( "GET_PROFILE_URL =" + GET_PROFILE_URL);
                    dlog.i( "GET_USER_PHONE =" + GET_USER_PHONE);
                    dlog.i( "GET_USER_BIRTH =" + GET_USER_BIRTH);
                    dlog.i( "GET_USER_AGEROUNGE =" + GET_USER_AGEROUNGE);
                    dlog.i( "GET_USER_SEX =" + GET_USER_SEX);
                    dlog.i( "GET_JOIN_CONFIRM = " + GET_JOIN_CONFIRM);
                    dlog.i( "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

                    if (GET_ACCOUNT_EMAIL.isEmpty()) {
                        Toast.makeText(mContext, "네트워크 통신연결이 불안정 합니다.", Toast.LENGTH_SHORT).show();
                    } else {
//                        UserCheckData.getInstance().setUser_id(getid);
                        UserCheckData.getInstance().setUser_name(GET_NAME);
                        UserCheckData.getInstance().setUser_nick_name(GET_NAME);
                        UserCheckData.getInstance().setUser_account(GET_ACCOUNT_EMAIL);
                        UserCheckData.getInstance().setUser_password("");
                        UserCheckData.getInstance().setUser_gender(GET_USER_SEX);
                        UserCheckData.getInstance().setUser_phone(GET_USER_PHONE);
                        UserCheckData.getInstance().setUser_img_path(GET_PROFILE_URL);
                        UserCheckData.getInstance().setUser_platform(USER_LOGIN_METHOD);
                        LoginCheck(GET_ACCOUNT_EMAIL, "", USER_LOGIN_METHOD);
                    }

                }, 1000); //1초 후 인트로 실행
            }

            if (throwable != null) {
                dlog.i( "invoke: " + throwable.getLocalizedMessage());
            }
            return null;
        });
    }

    private void updateUI(FirebaseUser user) {
        shardpref.remove("task_no");
        dlog.i("----------Success Google Login Data----------");
        dlog.i("getEmail : " + user.getEmail());
        dlog.i("getPhoneNumber : " + user.getPhoneNumber());
        dlog.i("getPhotoUrl : " + user.getPhotoUrl());
        dlog.i("getPhotoUrl : " + user.getDisplayName());
        dlog.i("----------Success Google Login Data----------");
        binding.loginAlertText.setVisibility(View.VISIBLE);
        dlog.i("!USER_LOGIN_METHOD.equals(NEB)");

        shardpref.putString("USER_LOGIN_METHOD", "Google");
        shardpref.putBoolean("USER_LOGIN_CONFIRM", true);
        if (user.getEmail().isEmpty()) {
            Toast.makeText(mContext, "네트워크 통신연결이 불안정 합니다.", Toast.LENGTH_SHORT).show();
        } else {
            UserCheckData.getInstance().setUser_name(user.getDisplayName());
            UserCheckData.getInstance().setUser_nick_name(user.getDisplayName());
            UserCheckData.getInstance().setUser_account(user.getEmail());
            UserCheckData.getInstance().setUser_password("");
            UserCheckData.getInstance().setUser_gender("");
            UserCheckData.getInstance().setUser_phone(user.getPhoneNumber());
            UserCheckData.getInstance().setUser_img_path(String.valueOf(user.getPhotoUrl()));
            UserCheckData.getInstance().setUser_platform("Google");
            LoginCheck(user.getEmail(), "", "Google");
        }
        GET_ACCOUNT_EMAIL = user.getEmail();
        GET_NAME = user.getDisplayName();
        GET_PROFILE_URL = String.valueOf(user.getPhotoUrl());
    }

    public void LoginCheck(String account, String pw, String platform) {
        shardpref.putString("USER_INFO_EMAIL", account);
        shardpref.putString("platform", platform);
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
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("LoginCheck jsonResponse length : " + jsonResponse.length());
                            dlog.i("LoginCheck jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    if (Response.length() != 0) {
                                        String getid = Response.getJSONObject(0).getString("id");
                                        String getname = Response.getJSONObject(0).getString("name");
                                        String getaccount = Response.getJSONObject(0).getString("account");
                                        String getPassword = Response.getJSONObject(0).getString("password");
                                        String getphone = Response.getJSONObject(0).getString("phone");
                                        String getgender = Response.getJSONObject(0).getString("gender");
                                        String getimg_path = Response.getJSONObject(0).getString("img_path");
                                        String getPlatform = Response.getJSONObject(0).getString("platform");

                                        String decodePw = "";
                                        dlog.i("LoginCheck platform : " + platform);
                                        try {
                                            //-- 확인할때 사용
//                                            try {
//                                                USER_INFO_PW = aes256Util.encode("[kraftmysecretkey]" + "onon0817!!" + "["+R.string.kakao_native_key+"]");
//                                                dlog.i("USER_INFO_PW ; " + USER_INFO_PW);
//                                            } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException
//                                                    | NoSuchPaddingException | IllegalBlockSizeException
//                                                    | NoSuchAlgorithmException | BadPaddingException
//                                                    | InvalidKeyException e) {
//                                                e.printStackTrace();
//                                            }
                                            String repalcekey0 = "kraftmysecretkey";
                                            String replacekey1 = "nrkwl3nkv54";
                                            decodePw = aes256Util.decode(getPassword).replace(repalcekey0, "").replace(replacekey1, "");
                                            shardpref.putString("USER_INFO_ID", getid);
                                            shardpref.putString("USER_INFO_NAME", getname);
                                            shardpref.putString("USER_INFO_NICKNAME", getname);
                                            shardpref.putString("USER_INFO_EMAIL", getaccount);
                                            shardpref.putString("USER_INFO_PW", decodePw);
                                            shardpref.putString("USER_INFO_PHONE", getphone);
                                            shardpref.putString("USER_INFO_GENDER", getgender);
                                            shardpref.putString("USER_INFO_PROFILE", getimg_path);
                                            shardpref.putString("USER_LOGIN_METHOD", getPlatform);
                                            UserCheckData.getInstance().setUser_id(getid);
                                            UserCheckData.getInstance().setUser_name(getname);
                                            UserCheckData.getInstance().setUser_nick_name(getname);
                                            UserCheckData.getInstance().setUser_account(getaccount);
                                            UserCheckData.getInstance().setUser_password(decodePw);
                                            UserCheckData.getInstance().setUser_gender(getgender);
                                            UserCheckData.getInstance().setUser_phone(getphone);
                                            UserCheckData.getInstance().setUser_img_path(getimg_path);
                                            UserCheckData.getInstance().setUser_platform(getPlatform);

                                        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException
                                                | NoSuchPaddingException | IllegalBlockSizeException
                                                | NoSuchAlgorithmException | BadPaddingException
                                                | InvalidKeyException e) {
                                            e.printStackTrace();
                                        }
                                        dlog.i("LoginCheck decodePw : " + decodePw);
                                        dlog.i("LoginCheck pw : " + pw);
                                        if (platform.equals("NEB")) {
                                            if (getaccount.equals(account) && decodePw.equals(pw)) {
                                                UserCheck(getaccount);
                                            } else {
                                                Toast_Nomal("이메일 혹은 비밀번호를 확인해주세요.");
                                            }
                                        } else {
                                            UserCheck(getaccount);
                                        }
                                        binding.loginAlertText.setVisibility(View.GONE);
                                    } else {
                                        shardpref.remove("USER_INFO_ID");
                                        shardpref.remove("USER_INFO_NAME");
                                        shardpref.remove("USER_INFO_EMAIL");
                                        shardpref.remove("USER_INFO_PW");
                                        shardpref.remove("USER_INFO_PHONE");
                                        shardpref.remove("USER_INFO_GENDER");
                                        shardpref.remove("USER_INFO_PROFILE");
                                        shardpref.remove("USER_INFO_METHOD");
                                        shardpref.remove("USER_LOGIN_METHOD");
                                        binding.deviceNumEdit.setText("");
                                        binding.pwdEdit.setText("");
                                    }
                                } else {
                                    if (GET_ACCOUNT_EMAIL.isEmpty()) {
                                        Toast_Nomal("이메일을 읽을 수 없습니다.");
                                    } else {
                                        if (!platform.equals("NEB")) {
                                            UserCheck(GET_ACCOUNT_EMAIL);
                                            INPUT_JOIN_DATA(GET_ACCOUNT_EMAIL, GET_NAME, GET_PROFILE_URL, platform);
                                        } else {
                                            Toast_Nomal("아이디 혹은 비밀번호를 확인하세요");
                                            shardpref.remove("USER_INFO_ID");
                                            shardpref.remove("USER_INFO_NAME");
                                            shardpref.remove("USER_INFO_EMAIL");
                                            shardpref.remove("USER_INFO_PW");
                                            shardpref.remove("USER_INFO_PHONE");
                                            shardpref.remove("USER_INFO_GENDER");
                                            shardpref.remove("USER_INFO_PROFILE");
                                            shardpref.remove("USER_INFO_METHOD");
                                            shardpref.remove("USER_LOGIN_METHOD");
                                            binding.deviceNumEdit.setText("");
                                            binding.pwdEdit.setText("");
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

    public void INPUT_JOIN_DATA(String USER_INFO_EMAIL, String USER_INFO_NAME, String imgpath, String platform) {
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
        dlog.i("인코딩 후 USER_INFO_PW : " + USER_INFO_PW);
        dlog.i("name : " + USER_INFO_NAME);
        dlog.i("-----INPUT_JOIN_DATA-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserInsertInterface api = retrofit.create(UserInsertInterface.class);
        Call<String> call = api.getData(USER_INFO_EMAIL, USER_INFO_NAME, "", USER_INFO_PW, "", "", imgpath, platform);
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
                            UserCheck(USER_INFO_EMAIL);
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

    private void permissionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();
                dlog.i("permissionCheck() : Permission Granted");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(mContext, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                dlog.i("permissionCheck() : Permission Denied");
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(
                        "권한을 거부할 경우 서비스 이용에\n제한이 있을 수 있습니다" +
                                "\n" +
                                "[설정] > [권한]에서 권한을 켜주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        //팝업 send Result 결과
//        if (requestCode == REQUEST_CODE) {
//            if (resultCode != Activity.RESULT_OK) {
//                return;
//            }
//            String sendText = data.getExtras().getString("sendText");
//            dlog.i("onActivityResult : " + sendText);
//        } else
        if (requestCode == RC_SIGN_IN) {
            //구글 로그인 반환값
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                dlog.d("firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                shardpref.remove("USER_LOGIN_METHOD");
                Log.w("LoginActivity", "Google sign in failed", e);
            }
        }
    }


    //----콜백 영역 START
    //카카오 로그인 콜백
    Function2<OAuthToken, Throwable, Unit> kakaoCallback = (oAuthToken, throwable) -> {
        if (oAuthToken != null) {
            dlog.i("kakaoCallback oAuthToken not null");
            getKaKaoProfile();
        }
        if (throwable != null) {
            dlog.i("kakaoCallback throwable not null");
            dlog.i( "Message : " + throwable.getLocalizedMessage());

            if (Objects.equals(throwable.getLocalizedMessage(), "user cancelled.")) {
                binding.loginAlertText.setVisibility(View.GONE);
            }
        }
        return null;
    };

    //구글 로그인 콜백
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            dlog.d("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    //----콜백 영역 END

    // *** 스틱코드 등록 코드 ***
    public static String getHash(String str) {
        String digest = "";
        try {
            //암호화
            MessageDigest sh = MessageDigest.getInstance("SHA-256"); // SHA-256 해시함수를 사용
            sh.update(str.getBytes()); // str의 문자열을 해싱하여 sh에 저장
            byte byteData[] = sh.digest(); // sh 객체의 다이제스트를 얻는다.

            //얻은 결과를 string으로 변환
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            digest = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            digest = null;
        }
        return digest;
    }
    // *** 스틱코드 등록 코드 ***

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("data", "앱을 종료 하시겠습니까?");
        intent.putExtra("flag", "종료");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "종료");
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    public void Toast_Nomal(String message) {
        BtnOneCircleFun(true);
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

    private void BtnOneCircleFun(boolean tf){
        binding.loginBtn.setClickable(tf);
        binding.loginBtn.setEnabled(tf);

        binding.joinBtn.setClickable(tf);
        binding.joinBtn.setEnabled(tf);

        binding.findEmail.setClickable(tf);
        binding.findEmail.setEnabled(tf);

        binding.findPw.setClickable(tf);
        binding.findPw.setEnabled(tf);

        binding.naverLogin.setClickable(tf);
        binding.naverLogin.setEnabled(tf);

        binding.kakaoLoginArea.setClickable(tf);
        binding.kakaoLoginArea.setEnabled(tf);

        binding.googleLoginArea.setClickable(tf);
        binding.googleLoginArea.setEnabled(tf);
    }
}
