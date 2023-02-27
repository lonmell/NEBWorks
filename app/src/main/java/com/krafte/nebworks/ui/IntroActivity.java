package com.krafte.nebworks.ui;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityIntroBinding;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.HashCode;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;
import com.krafte.nebworks.util.disconnectHandler;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class IntroActivity extends AppCompatActivity {
    private static final String TAG = "IntroActivity";
    private ActivityIntroBinding binding;
    Context mContext;

    //Other 클래스
    PreferenceHelper shardpref;
    Animation anim_FadeIn;
    NotificationManager notificationManager;
    Dlog dlog = new Dlog();
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    PageMoveClass pm = new PageMoveClass();

    //Other 변수
    // shared 저장값
    String KAKAO_keyHash = "";
    //Login_Method => 가입한 루트를 표시 ( NEB - 그냥 앱 자체 기능으로 가입 / Kakao - 카카오로 가입 )
    String USER_LOGIN_METHOD = "0";

    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    //Kakao 가입/로그인시 불러오는 값
    String GET_KAKAO_NAME = "";
    String GET_KAKAO_ACCOUNT_EMAIL = "";
    String GET_KAKAO_PROFILE_URL = "";
    String GET_KAKAO_USER_PHONE = "";
    String GET_KAKAO_USER_AGENCY = "";
    String GET_KAKAO_USER_BIRTH = "";
    String GET_KAKAO_USER_AGEROUNGE = "";
    String GET_KAKAO_USER_SEX = "";
    String GET_KAKAO_USER_PW = "";
    String GET_KAKAO_USER_JOIN_DATE = "";
    String GET_KAKAO_USER_AUTH = "9";
    String GET_KAKAO_USER_SERVICE = "S";
    boolean GET_JOIN_CONFIRM = false;

    String USER_INFO_AUTH = "";

    int versionCode = 0;
    String versionName = "";
    String LastVersion = "";
    DBConnection dbConnection = new DBConnection();

    //Naver
    String ClientID = "cN1sIOhyOshPLKgNL4Sj";
    String ClientSecret = "iFS5etlgYt";
    String ClientName = "넵";
    NaverIdLoginSDK naverIdLoginSDK = NaverIdLoginSDK.INSTANCE;

    String UPDATEYN = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_intro); //xml , java 소스 연결
        binding = ActivityIntroBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        anim_FadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_intro_fadein);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel("1", "매장 알림", NotificationManager.IMPORTANCE_DEFAULT));
        notificationManager.createNotificationChannel(new NotificationChannel("2", "결재 알림", NotificationManager.IMPORTANCE_DEFAULT));
        notificationManager.createNotificationChannel(new NotificationChannel("3", "근무시간 알림", NotificationManager.IMPORTANCE_DEFAULT));
        notificationManager.createNotificationChannel(new NotificationChannel("4", "이력서/면접", NotificationManager.IMPORTANCE_DEFAULT));
        notificationManager.createNotificationChannel(new NotificationChannel("5", "근로계약서 및 기타 알림", NotificationManager.IMPORTANCE_DEFAULT));

        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
        USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "-99");// 0:점주 / 1:근로자

        dlog.i("USER_LOGIN_METHOD : " + USER_LOGIN_METHOD);
        shardpref.putInt("SELECT_POSITION", 0);
        shardpref.putInt("SELECT_POSITION_sub", 0);

        Glide.with(this).load(R.raw.neb_loding_whtie)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.loadingView);
        binding.loginAlertText.setVisibility(View.VISIBLE);
        initView();

        //사용자 ID로 FCM 보낼수 있도록 토픽 세팅
        FirebaseMessaging.getInstance().subscribeToTopic("NEBWorks").addOnCompleteListener(task -> {
            String msg = getString(R.string.msg_subscribed);
            if (!task.isSuccessful()) {
                msg = getString(R.string.msg_subscribe_failed);
            }
            dlog.i("msg : " + msg);
        });

    }

    private void KakaoSetting() {
        KAKAO_keyHash = Utility.INSTANCE.getKeyHash(this);
        dlog.i("SMS에서 사용할 HASH : " + HashCode.getAppSignatures(this));
        String Release_keyHash = com.kakao.util.helper.Utility.getKeyHash(this /* context */);

        Log.i(TAG, "KAKAO keyHash = " + KAKAO_keyHash);
        dlog.i("Release_keyHash = " + Release_keyHash);
        // Kakao SDK 객체 초기화
        KakaoSdk.init(this, getString(R.string.kakao_native_key));

//        handler.postDelayed(() -> {
//            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(mContext)) {
//                dlog.i("카카오톡 앱이 설치 되어있을때 : kakaoCallback = " + kakaoCallback);
//                UserApiClient.getInstance().loginWithKakaoTalk(mContext, kakaoCallback);
//            } else {
//                dlog.i("카카오톡 앱이 설치 안 되어있을때 : kakaoCallback = " + kakaoCallback);
//                UserApiClient.getInstance().loginWithKakaoAccount(mContext, kakaoCallback);
//            }
//        }, 1000); //0.5초 후 인트로 실행
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(mContext)) {
            dlog.i("카카오톡 앱이 설치 되어있을때 : kakaoCallback = " + kakaoCallback);
            UserApiClient.getInstance().loginWithKakaoTalk(mContext, kakaoCallback);
        } else {
            dlog.i("카카오톡 앱이 설치 안 되어있을때 : kakaoCallback = " + kakaoCallback);
            UserApiClient.getInstance().loginWithKakaoAccount(mContext, kakaoCallback);
        }

    }

    private void GoogleSetting() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        shardpref.putString("USER_LOGIN_METHOD", "Google");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void NaverSetting() {
        try {
            naverIdLoginSDK.initialize(IntroActivity.this, ClientID, ClientSecret, ClientName);
            naverIdLoginSDK.logout();
            naverIdLoginSDK.setShowMarketLink(true);
            naverIdLoginSDK.setShowBottomTab(true);

            naverIdLoginSDK.authenticate(IntroActivity.this, oAuthLoginCallback);
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.i("Kakao", "Message : " + throwable.getLocalizedMessage());
//            if(throwable.getLocalizedMessage().equals("user cancelled.")){
//                login_alert_text.setVisibility(View.GONE);
//            }
            if (Objects.equals(throwable.getLocalizedMessage(), "user cancelled.")) {
                binding.loginAlertText.setVisibility(View.GONE);
            }
        }
        return null;
    };

    private void initView() {
        String url = getIntent().getStringExtra("click_action");
        if (url != null) {
            dlog.i("url : " + url);
            if (url.equals("MainActivity")) {
                shardpref.putInt("SELECT_POSITION", 0);
                if (USER_INFO_AUTH.equals("0")) {
                    pm.Main(mContext);
                } else {
                    pm.Main2(mContext);
                }
            }
        }
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
                Log.w("IntroActivity", "Google sign in failed", e);
            }
        }
    }

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

    private void getKaKaoProfile() {
        UserApiClient.getInstance().me((user, throwable) -> {
            if (user != null) {
                shardpref.putString("USER_LOGIN_METHOD", "Kakao");

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    GET_KAKAO_NAME = Objects.requireNonNull(Objects.requireNonNull(user.getKakaoAccount()).getProfile()).getNickname();
                    GET_KAKAO_ACCOUNT_EMAIL = Objects.requireNonNull(user.getKakaoAccount()).getEmail();
                    GET_KAKAO_PROFILE_URL = Objects.requireNonNull(user.getKakaoAccount().getProfile()).getProfileImageUrl();
                    GET_KAKAO_USER_PHONE = "";
                    GET_KAKAO_USER_AGENCY = "";
                    GET_KAKAO_USER_BIRTH = user.getKakaoAccount().getBirthyear() + "/" + user.getKakaoAccount().getBirthday();
                    GET_KAKAO_USER_AGEROUNGE = String.valueOf(user.getKakaoAccount().getAgeRange());
                    GET_KAKAO_USER_SEX = String.valueOf(user.getKakaoAccount().getGender());
                    GET_KAKAO_USER_PW = "";
                    GET_KAKAO_USER_JOIN_DATE = dc.GET_TIME;
                    GET_KAKAO_USER_AUTH = "9";
                    GET_KAKAO_USER_SERVICE = "S";
                    USER_LOGIN_METHOD = "Kakao";
                    GET_JOIN_CONFIRM = !String.valueOf(user.getId()).isEmpty();

                    shardpref.putString("USER_INFO_NAME", GET_KAKAO_NAME);
                    shardpref.putString("USER_INFO_EMAIL", GET_KAKAO_ACCOUNT_EMAIL);
                    shardpref.putString("USER_INFO_BIRTH", GET_KAKAO_USER_BIRTH);
                    shardpref.putString("USER_INFO_PHONE", GET_KAKAO_USER_PHONE);
                    shardpref.putString("USER_INFO_AGENCY", GET_KAKAO_USER_AGENCY);
                    shardpref.putString("USER_INFO_JOIN_DATE", GET_KAKAO_USER_JOIN_DATE);
                    shardpref.putString("USER_INFO_GENDER", GET_KAKAO_USER_SEX);
                    shardpref.putString("USER_INFO_SERVICE", GET_KAKAO_USER_SERVICE);
                    shardpref.putString("USER_INFO_PROFILE", GET_KAKAO_PROFILE_URL);
                    shardpref.putString("USER_INFO_AGEROUNGE", GET_KAKAO_USER_AGEROUNGE);
                    shardpref.putString("USER_LOGIN_METHOD", "Kakao");
                    shardpref.putBoolean("USER_LOGIN_CONFIRM", true);

                    Log.i("Kakao", "Kakao id =" + user.getId());
                    Log.i("Kakao", "GET_KAKAO_NAME =" + GET_KAKAO_NAME);
                    Log.i("Kakao", "GET_KAKAO_ACCOUNT_EMAIL =" + GET_KAKAO_ACCOUNT_EMAIL);
                    Log.i("Kakao", "GET_KAKAO_PROFILE_URL =" + GET_KAKAO_PROFILE_URL);
                    Log.i("Kakao", "GET_KAKAO_USER_PHONE =" + GET_KAKAO_USER_PHONE);
                    Log.i("Kakao", "GET_KAKAO_USER_BIRTH =" + GET_KAKAO_USER_BIRTH);
                    Log.i("Kakao", "GET_KAKAO_USER_AGEROUNGE =" + GET_KAKAO_USER_AGEROUNGE);
                    Log.i("Kakao", "GET_KAKAO_USER_SEX =" + GET_KAKAO_USER_SEX);
                    Log.i("Kakao", "GET_JOIN_CONFIRM = " + GET_JOIN_CONFIRM);
                    Log.i("Kakao", "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

                    UserCheckData.getInstance().setUser_name(GET_KAKAO_NAME);
                    UserCheckData.getInstance().setUser_nick_name(GET_KAKAO_NAME);
                    UserCheckData.getInstance().setUser_account(GET_KAKAO_ACCOUNT_EMAIL);
                    UserCheckData.getInstance().setUser_gender(GET_KAKAO_USER_SEX);
                    UserCheckData.getInstance().setUser_img_path(GET_KAKAO_PROFILE_URL);
                    UserCheckData.getInstance().setUser_phone(GET_KAKAO_USER_PHONE);
                    UserCheckData.getInstance().setUser_platform("Kakao");
//                    INPUT_JOIN_DATA("Kakao", GET_KAKAO_NAME, GET_KAKAO_USER_PHONE, GET_KAKAO_USER_AGENCY
//                            , GET_KAKAO_USER_BIRTH, GET_KAKAO_USER_SEX, GET_KAKAO_USER_PW, "", GET_KAKAO_USER_JOIN_DATE
//                            , "2", GET_KAKAO_USER_SERVICE, GET_KAKAO_ACCOUNT_EMAIL, GET_KAKAO_PROFILE_URL, "Kakao");
                    UserCheck(GET_KAKAO_ACCOUNT_EMAIL);

                }, 1000); //1초 후 인트로 실행
            }

            if (throwable != null) {
                Log.i("Kakao", "invoke: " + throwable.getLocalizedMessage());
            }
            return null;
        });
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
                                USER_LOGIN_METHOD = "Naver";
                                GET_JOIN_CONFIRM = !String.valueOf(nidProfileResponse.getProfile().getId()).isEmpty();

                                shardpref.putString("USER_LOGIN_METHOD", USER_LOGIN_METHOD);
                                shardpref.putBoolean("USER_LOGIN_CONFIRM", true);

                                if (nidProfileResponse.getProfile().getEmail().isEmpty()) {
                                    pm.Login(mContext);
                                } else {
                                    UserCheck(nidProfileResponse.getProfile().getEmail());
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

    private void updateUI(FirebaseUser user) {
        dlog.i("----------Success Google Login Data----------");
        dlog.i("getEmail : " + user.getEmail());
        dlog.i("getPhoneNumber : " + user.getPhoneNumber());
        dlog.i("getPhotoUrl : " + user.getPhotoUrl());
        dlog.i("getPhotoUrl : " + user.getDisplayName());
        dlog.i("----------Success Google Login Data----------");
        binding.loginAlertText.setVisibility(View.VISIBLE);
        dlog.i("!USER_LOGIN_METHOD.equals(NEB)");

        shardpref.putString("USER_INFO_NAME", user.getDisplayName());
        shardpref.putString("USER_INFO_EMAIL", user.getEmail());
        shardpref.putString("USER_INFO_BIRTH", "");
        shardpref.putString("USER_INFO_AGENCY", "");
        shardpref.putString("USER_INFO_JOIN_DATE", "");
        shardpref.putString("USER_INFO_GENDER", "");
        shardpref.putString("USER_INFO_SERVICE", "");
        shardpref.putString("USER_INFO_PROFILE", String.valueOf(user.getPhotoUrl()));
        shardpref.putString("USER_INFO_AGEROUNGE", "");
        shardpref.putString("USER_LOGIN_METHOD", "Google");
        shardpref.putBoolean("USER_LOGIN_CONFIRM", true);
        UserCheckData.getInstance().setUser_nick_name(user.getDisplayName());
        UserCheckData.getInstance().setUser_account(user.getEmail());
        UserCheckData.getInstance().setUser_img_path(String.valueOf(user.getPhotoUrl()));
        UserCheckData.getInstance().setUser_platform("Google");

        UserCheck(user.getEmail());

//        INPUT_JOIN_DATA("Google", user.getDisplayName(), user.getPhoneNumber(), ""
//                , "", "", "", "", ""
//                , "2", "", user.getEmail(), String.valueOf(user.getPhotoUrl()), "Google");

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

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

    private void getLastVersion() {
        NetworkStates();

        Thread th = new Thread(() -> {
            Log.i(TAG, "dbConnection.GetLastAPPVersionCode START");
            dbConnection.GetLastAPPVersionCode("android");

            runOnUiThread(() -> {
                Log.i(TAG, "lastVersion code = " + dbConnection.lastVersion.getLast_version());
                LastVersion = dbConnection.lastVersion.getLast_version();
                getUpdateConfirm(dbConnection.lastVersion.getLast_version());
            });
        });
        th.start();
        try {
            th.join(); // 작동한 스레드의 종료까지 대기 후 메인 스레드 실행

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean updateconfirm = false;

    public void getUpdateConfirm(String lastVersion) {
        dlog.i("lastVersion : " + lastVersion);
        PackageInfo pi;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pi.versionCode;
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!lastVersion.equals("")){
            dlog.i("versionCode == Integer.parseInt(lastVersion) : " + (versionCode == Integer.parseInt(lastVersion)));
            dlog.i("versionCode < Integer.parseInt(lastVersion) : " + (versionCode < Integer.parseInt(lastVersion)));
            dlog.i("versionCode : " + versionCode);
            dlog.i("lastVersion : " + lastVersion);
            if (versionCode == Integer.parseInt(lastVersion)) {
                updateconfirm = true;
                Log.i(TAG, "업데이트 필요 X");
                if (USER_LOGIN_METHOD.equals("Kakao")) {
                    KakaoSetting();
                } else if (USER_LOGIN_METHOD.equals("Google")) {
                    GoogleSetting();
                } else if (USER_LOGIN_METHOD.equals("Naver")) {
                    NaverSetting();
                } else {
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent); //인트로 실행 후 바로 넘어감.
                        finish();
                    }, 1000); //1초 후 인트로 실행
                }
            } else if (versionCode != Integer.parseInt(lastVersion)) {
                if (!UPDATEYN.equals("N")) {
                    updateconfirm = false;
                    Log.i(TAG, "업데이트 필요 0");
                    Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
                    intent.putExtra("flag", "업데이트");
                    intent.putExtra("data", "최신버전 앱으로 업데이트를 위해\n스토어로 이동합니다");
                    intent.putExtra("left_btn_txt", "나중에");
                    intent.putExtra("right_btn_txt", "업데이트");
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_up, 0);
                } else {
                    updateconfirm = true;
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent); //인트로 실행 후 바로 넘어감.
                        finish();
                    }, 1000); //1초 후 인트로 실행
                }
            } else if (lastVersion.equals("")) {
                updateconfirm = true;
                Log.i(TAG, "최신버전이 안가져와짐");
            }
        }

    }


    @Override
    public void onStart(){
        super.onStart();
        UPDATEYN = shardpref.getString("UPDATEYN", "Y");
        dlog.i("UPDATEYN : " + UPDATEYN);
        createNotificationChannel();
        getReleaseHashKey();
        Handler handler = new Handler();
        handler.postDelayed(this::getLastVersion, 1000); //1초 후 인트로 실행
    }

    @Override
    public void onResume() {
        super.onResume();
}

    private void getReleaseHashKey() {
        byte[] sha1 = {
                (byte) 0xB1, 0x1E, 0x2B, (byte) 0x9C, (byte) 0x97, (byte) 0xDA, (byte) 0xCD, (byte) 0xA2, (byte) 0xE1, (byte) 0x9B, 0x40, 0x72, (byte) 0xAF, (byte) 0xA8, 0x55, 0x7E, 0x37, 0x62, (byte) 0xE0, 0x42
        };
        dlog.i("getReleaseHashKey : " + Base64.encodeToString(sha1, Base64.NO_WRAP));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_1);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    RetrofitConnect rc = new RetrofitConnect();
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
                                        String id = Response.getJSONObject(0).getString("id");
                                        String name = Response.getJSONObject(0).getString("name");
                                        String phone = Response.getJSONObject(0).getString("phone");
                                        String platform = Response.getJSONObject(0).getString("platform");
                                        String user_auth = Response.getJSONObject(0).getString("user_auth");
                                        try {
                                            dlog.i("------UserCheck-------");
                                            dlog.i("성명 : " + name);
                                            dlog.i("사용자 권한 : " + user_auth);
                                            dlog.i("------UserCheck-------");
                                            if (!user_auth.equals("-1")) {
                                                shardpref.putString("USER_INFO_AUTH", user_auth);
                                                UserCheckData.getInstance().setUser_id(id);
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                if (name.isEmpty() || phone.isEmpty()) {
                                                    shardpref.putString("editstate", "newPro");
                                                    pm.ProfileEdit(mContext);
                                                } else {
                                                    getPlaceList(id, user_auth);
                                                }
                                            } else {
                                                binding.loginAlertText.setVisibility(View.GONE);
                                                if (name.isEmpty() || phone.isEmpty()) {
                                                    shardpref.putString("editstate", "newPro");
                                                    pm.ProfileEdit(mContext);
                                                } else {
                                                    pm.AuthSelect(mContext);
                                                }
                                            }
                                        } catch (Exception e) {
                                            dlog.i("UserCheck Exception : " + e);
                                        }
                                    }
                                }else{
                                    Toast_Nomal("사용자 데이터를 찾을 수 없습니다.");
                                    pm.Login(mContext);
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

                                        dlog.i("owner_id : "        + owner_id);
                                        dlog.i("place_name : "      + place_name);
                                        dlog.i("myid : "            + myid);
                                        dlog.i("place_id : "        + place_id);
                                        dlog.i("save_kind : "       + save_kind);
                                        dlog.i("accept_state : "    + accept_state);
                                        dlog.i("place_imgpath : "   + place_imgpath);

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
                                            shardpref.putInt("SELECT_POSITION", 0);

                                            if (user_auth.equals("0")) {
                                                pm.Main(mContext);
                                            } else {
                                                pm.Main2(mContext);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        dlog.i("GetPlaceList OnItemClickListener Exception :" + e);
                                    }
                                } else {
                                    try {
                                        shardpref.remove("event");
                                        pm.PlaceList(mContext);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
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

    //-------몰입화면 설정
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    //-------몰입화면 설정
}