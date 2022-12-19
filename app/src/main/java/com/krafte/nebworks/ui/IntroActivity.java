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
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityIntroBinding;
import com.krafte.nebworks.pop.OneButtonPopActivity;
import com.krafte.nebworks.ui.login.LoginActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.HashCode;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;
import com.krafte.nebworks.util.disconnectHandler;

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
    //Login_Method => 가입한 루트를 표시 ( KOGAS - 그냥 앱 자체 기능으로 가입 / Kakao - 카카오로 가입 )
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
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","0");

        anim_FadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_intro_fadein);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel("1", "매장 알림", NotificationManager.IMPORTANCE_DEFAULT));
        notificationManager.createNotificationChannel(new NotificationChannel("2", "결재 알림", NotificationManager.IMPORTANCE_DEFAULT));
        notificationManager.createNotificationChannel(new NotificationChannel("3", "근무시간 알림", NotificationManager.IMPORTANCE_DEFAULT));
        notificationManager.createNotificationChannel(new NotificationChannel("4", "이력서/면접", NotificationManager.IMPORTANCE_DEFAULT));

        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "null");
        dlog.i("USER_LOGIN_METHOD : " + USER_LOGIN_METHOD);
        shardpref.putInt("SELECT_POSITION", 0);
        shardpref.putInt("SELECT_POSITION_sub", 0);
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

        handler.postDelayed(() -> {
            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(mContext)) {
                dlog.i("카카오톡 앱이 설치 되어있을때 : kakaoCallback = " + kakaoCallback);
                UserApiClient.getInstance().loginWithKakaoTalk(mContext, kakaoCallback);
            } else {
                dlog.i("카카오톡 앱이 설치 안 되어있을때 : kakaoCallback = " + kakaoCallback);
                UserApiClient.getInstance().loginWithKakaoAccount(mContext, kakaoCallback);
            }
        }, 1000); //0.5초 후 인트로 실행

    }

    private void GoogleSetting() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Glide.with(this).load(R.drawable.identificon)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.loadingView);
        shardpref.putString("USER_LOGIN_METHOD", "Google");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

    private void initView(){
        String url = getIntent().getStringExtra("click_action");
        if(url != null){
            dlog.i("url : " + url);
            if(url.equals("MainActivity")){
                shardpref.putInt("SELECT_POSITION", 0);
                if (USER_INFO_AUTH.equals("0")) {
                    pm.Main(mContext);
                } else {
                    pm.Main2(mContext);
                }
            }
        }

//        if(bundle != null){
//            dlog.i("bundle : " + bundle);
//        }
//        dlog.i("remoteMessage : " + "12314123");
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
                binding.loginAlertText.setVisibility(View.VISIBLE);
                Glide.with(this).load(R.drawable.identificon)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(binding.loadingView);
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
    public void getUpdateConfirm(String lastVersion){
        PackageInfo pi;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pi.versionCode;
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(versionCode == Integer.parseInt(lastVersion)){
            updateconfirm = true;
            Log.i(TAG,"업데이트 필요 X");
            if (USER_LOGIN_METHOD.equals("Kakao")) {
                KakaoSetting();
            } else if (USER_LOGIN_METHOD.equals("Google")) {
                GoogleSetting();
            } else {
                handler = new Handler();
                handler.postDelayed(() -> {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent); //인트로 실행 후 바로 넘어감.
                    finish();
                }, 1000); //1초 후 인트로 실행
            }
        }else if(versionCode < Integer.parseInt(lastVersion)){
            updateconfirm = false;
            Log.i(TAG, "업데이트 필요 0");
            Intent intent = new Intent(mContext, OneButtonPopActivity.class);
            intent.putExtra("data", "최신버전 앱으로 업데이트를 위해\n스토어로 이동합니다");
            intent.putExtra("left_btn_txt", "확인");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_up, 0);
        }else if(lastVersion.equals("")){
            updateconfirm = true;
            Log.i(TAG,"최신버전이 안가져와짐");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        createNotificationChannel();
        Handler handler = new Handler();
        handler.postDelayed(this::getLastVersion,100); //0.5초 후 인트로 실행
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
                                    binding.loginAlertText.setVisibility(View.GONE);
//                                    shardpref.putString("USER_INFO_AUTH", "0");
//                                    shardpref.putInt("SELECT_POSITION", 0);
//                                    shardpref.putInt("SELECT_POSITION_sub", 0);
//                                    pm.PlaceList(mContext);
                                    pm.AuthSelect(mContext);
                                }else{
                                    binding.loginAlertText.setVisibility(View.GONE);
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