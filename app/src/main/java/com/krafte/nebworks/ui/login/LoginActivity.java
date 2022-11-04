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
import android.view.View;
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
import com.krafte.nebworks.dataInterface.UserInsertInterface;
import com.krafte.nebworks.dataInterface.UserSelectInterface;
import com.krafte.nebworks.databinding.ActivityLoginBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.AES256Util;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.HashCode;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

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
 * 2022-10-05 방창배 수정 구글 로그인 추가 - 인증진행중
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
            aes256Util = new AES256Util("kraftmysecretkey");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        GET_ACCOUNT_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
        onEvent();
        permissionCheck();
        KakaoSetting();
        GoogleSetting();
        if (!GET_ACCOUNT_EMAIL.isEmpty()) {
            binding.deviceNumEdit.setText(GET_ACCOUNT_EMAIL);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GET_ACCOUNT_EMAIL = shardpref.getString("USER_INFO_EMAIL", "-99");
        USER_INFO_PW = shardpref.getString("USER_INFO_PW", "-99");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "-99");
        if (!USER_LOGIN_METHOD.equals("-99")) {
            if (!GET_ACCOUNT_EMAIL.equals("-99")) {
                binding.deviceNumEdit.setText(GET_ACCOUNT_EMAIL);
            }
            if (!USER_INFO_PW.equals("-99")) {
                binding.pwdEdit.setText(USER_INFO_PW);
            }
            if (!GET_ACCOUNT_EMAIL.equals("-99") && !USER_INFO_PW.equals("-99")) {
                LoginCheck(GET_ACCOUNT_EMAIL, USER_INFO_PW, "NEB");
            }
        }
        // Check if user is signed in (non-null) and update UI accordingly.
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
            binding.loginAlertText.setVisibility(View.VISIBLE);
            Glide.with(this).load(R.drawable.identificon)
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

    private void GoogleSetting() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        binding.googleLoginArea.setOnClickListener(v -> {
            Glide.with(this).load(R.drawable.identificon)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(binding.loadingView);
            shardpref.putString("USER_LOGIN_METHOD", "Google");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void onEvent() {
//        binding.deviceNumEdit.setOnClickListener(v -> {
//            LockTost();
//        });
//
//        binding.pwdEdit.setOnClickListener(v -> {
//            LockTost();
//        });

        binding.loginBtn.setOnClickListener(v -> {
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
            pm.JoinBefore(mContext);
        });
        binding.turnPwdChar.setOnClickListener(v -> {
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
            shardpref.putString("findkind", "email");
            pm.SearchEmail(mContext);
        });

        binding.findPw.setOnClickListener(v -> {
            shardpref.putString("findkind", "password");
            pm.SearchEmail(mContext);
        });
    }

    private void LockTost() {
        Toast.makeText(mContext, "잠겨있는 기능입니다.", Toast.LENGTH_SHORT).show();
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

                    shardpref.putString("USER_LOGIN_METHOD", "Kakao");
                    shardpref.putBoolean("USER_LOGIN_CONFIRM", true);

                    Log.i("Kakao", "Kakao id =" + user.getId());
                    Log.i("Kakao", "GET_NAME =" + GET_NAME);
                    Log.i("Kakao", "GET_ACCOUNT_EMAIL =" + GET_ACCOUNT_EMAIL);

                    Log.i("Kakao", "GET_PROFILE_URL =" + GET_PROFILE_URL);
                    Log.i("Kakao", "GET_USER_PHONE =" + GET_USER_PHONE);
                    Log.i("Kakao", "GET_USER_BIRTH =" + GET_USER_BIRTH);
                    Log.i("Kakao", "GET_USER_AGEROUNGE =" + GET_USER_AGEROUNGE);
                    Log.i("Kakao", "GET_USER_SEX =" + GET_USER_SEX);
                    Log.i("Kakao", "GET_JOIN_CONFIRM = " + GET_JOIN_CONFIRM);
                    Log.i("Kakao", "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

                    if (GET_ACCOUNT_EMAIL.isEmpty()) {
                        Toast.makeText(mContext, "네트워크 통신연결이 불안정 합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        LoginCheck(GET_ACCOUNT_EMAIL, "", "Kakao");
                    }

                }, 1000); //1초 후 인트로 실행
            }

            if (throwable != null) {
                Log.i("Kakao", "invoke: " + throwable.getLocalizedMessage());
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
            LoginCheck(user.getEmail(), "", "Google");
        }
        GET_ACCOUNT_EMAIL = user.getEmail();
        GET_NAME = user.getDisplayName();
        GET_PROFILE_URL = String.valueOf(user.getPhotoUrl());
    }


    public void INPUT_JOIN_DATA(String account, String name, String img_path, String platform) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserInsertInterface api = retrofit.create(UserInsertInterface.class);
        Call<String> call = api.getData(account, name, "", "", "", img_path, platform);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dlog.e("ConnectThread_UserInfo onSuccess not base64 : " + response.body().replace("\"", ""));
                    try {
                        if (response.body().replace("\"", "").equals("success")) {
                            USER_LOGIN_CONFIRM = true;
                            shardpref.putString("USER_INFO_EMAIL", account);
                            pm.AuthSelect(mContext);
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


    public void LoginCheck(String account, String pw, String platform) {
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
                            dlog.i("LoginCheck jsonResponse length : " + response.body().length());
                            dlog.i("LoginCheck jsonResponse : " + response.body());
                            try {

                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
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
//                                            try {
//                                                USER_INFO_PW = aes256Util.encode("[kraftmysecretkey]" + "onon0817!!" + "["+R.string.kakao_native_key+"]");
//                                                dlog.i("USER_INFO_PW ; " + USER_INFO_PW);
//                                            } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException
//                                                    | NoSuchPaddingException | IllegalBlockSizeException
//                                                    | NoSuchAlgorithmException | BadPaddingException
//                                                    | InvalidKeyException e) {
//                                                e.printStackTrace();
//                                            }

                                            String repalcekey0 = "[kraftmysecretkey]";
                                            String replacekey1 = "["+R.string.kakao_native_key+"]";
                                            decodePw = aes256Util.decode(getPassword).replace(repalcekey0,"").replace(replacekey1,"");
                                            shardpref.putString("USER_INFO_ID", getid);
                                            shardpref.putString("USER_INFO_NAME", getname);
                                            shardpref.putString("USER_INFO_EMAIL", getaccount);
                                            shardpref.putString("USER_INFO_PW", decodePw);
                                            shardpref.putString("USER_INFO_PHONE", getphone);
                                            shardpref.putString("USER_INFO_GENDER", getgender);
                                            shardpref.putString("USER_INFO_PROFILE", getimg_path);
                                            shardpref.putString("USER_INFO_METHOD", getimg_path);
                                            shardpref.putString("USER_LOGIN_METHOD", getPlatform);
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
                                                pm.AuthSelect(mContext);
                                            }
                                        } else {
                                            pm.AuthSelect(mContext);
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
                                        Toast.makeText(mContext, "통신연결이 불안정합니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (!platform.equals("NEB")) {
                                            INPUT_JOIN_DATA(GET_ACCOUNT_EMAIL, GET_NAME, GET_PROFILE_URL, platform);
                                        }else{
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
                        "권한을 거부할 경우 본 서비스를 이용하실 수 없습니다.\n" +
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
            Log.i("Kakao", "Message : " + throwable.getLocalizedMessage());

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
}
