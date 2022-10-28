package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.FeedCommentDelInterface;
import com.krafte.nebworks.dataInterface.FeedDelInterface;
import com.krafte.nebworks.dataInterface.UserDelInterface;
import com.krafte.nebworks.databinding.ActivityTwobuttonPopBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TwoButtonPopActivity extends Activity {
    private ActivityTwobuttonPopBinding binding;
    private static final String TAG = "TwoButtonPopActivity";

    Context mContext;
    private String flag = "";
    private String title = "";
    private String data = "";
    private String left_btn_txt = "";
    private String right_btn_txt = "";
    private String take_user_id = "";

    Intent intent;

    //shared Data
    PreferenceHelper shardpref;
    String store_no = "";
    String USER_INFO_ID = "";
    String USER_LOGIN_METHOD = "";

    //Other
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    String message = "";
    String topic = "";
    String click_action = "";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_twobutton_pop);
        binding = ActivityTwobuttonPopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2


        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        //데이터 가져오기
        intent = getIntent();
        data = intent.getStringExtra("data");
        flag = intent.getStringExtra("flag");
        take_user_id = intent.getStringExtra("take_user_id");
        left_btn_txt = intent.getStringExtra("left_btn_txt");
        right_btn_txt = intent.getStringExtra("right_btn_txt");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        setBtnEvent();


        store_no = shardpref.getString("store_no","0");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD","");

        if (title.equals("알림")) {
            binding.txtText.setVisibility(View.INVISIBLE);
        } else {
            binding.txtText.setText(data);
        }

        binding.popLeftTxt.setText(left_btn_txt);
        binding.popRightTxt.setText(right_btn_txt);

    }



    //확인 버튼 클릭
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setBtnEvent() {
        binding.popRightTxt.setOnClickListener(v -> {
            //데이터 전달하기
            if(flag.equals("로그아웃")){
//                FirebaseMessaging.getInstance().subscribeToTopic(USER_INFO_ID).isCanceled();
//                FirebaseMessaging.getInstance().subscribeToTopic("TEST").isCanceled();
                shardpref.clear();
                shardpref.putBoolean("USER_LOGIN_CONFIRM", false);
                shardpref.putString("USER_LOGIN_METHOD", USER_LOGIN_METHOD);
                shardpref.remove("ALARM_ONOFF");
                shardpref.remove("USER_LOGIN_METHOD");
                shardpref.putBoolean("isFirstLogin", true);

                if (USER_LOGIN_METHOD.equals("Google")) {
                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(this, task -> {
                                pm.LoginBack(mContext);
                            });
                } else if(USER_LOGIN_METHOD.equals("Kakao")){
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                pm.LoginBack(mContext);
                                return null;
                            }
                        });
                    }, 100); //0.5초 후 인트로 실행
                }else{
                    pm.LoginBack(mContext);
                }
                finish();
            }else if(flag.equals("회원탈퇴")){
                UserDelete(USER_INFO_ID);
            }else if (flag.equals("댓글삭제")) {
                String comment_no = "0";
                comment_no = shardpref.getString("comment_no","");
                CommentDelete(comment_no);
                ClosePop();
            } else if (flag.equals("공지삭제")) {
                String feed_id = "0";
                feed_id = shardpref.getString("edit_feed_id","");
                FeedDelete(feed_id);
            } else if(flag.equals("종료")){
                finish();

                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
            }
        });
        binding.popLeftTxt.setOnClickListener(v -> {
            ClosePop();
        });
    }
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

    private void ClosePop(){
        runOnUiThread(() -> {
            finish();
            Intent intent = new Intent();
            intent.putExtra("result", "Close Popup");
            setResult(RESULT_OK, intent);
            overridePendingTransition(0, R.anim.translate_down);
            super.onBackPressed();
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //액티비티(팝업) 닫기
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }


    //--Rtrofit Area
    public void UserDelete(String id) {
        dlog.i("UserDelete id : " + id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserDelInterface api = retrofit.create(UserDelInterface.class);
        Call<String> call = api.getData(id);
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
                                if(response.body().replace("\"","").equals("success")){
                                    Toast.makeText(mContext,"회원 탈퇴가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                    pm.LoginBack(mContext);
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

    //댓글삭제
    public void CommentDelete(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedCommentDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedCommentDelInterface api = retrofit.create(FeedCommentDelInterface.class);
        Call<String> call = api.getData(id);
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
                                if(response.body().replace("\"","").equals("")){
                                    ClosePop();
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

    //피드삭제
    public void FeedDelete(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedDelInterface api = retrofit.create(FeedDelInterface.class);
        Call<String> call = api.getData(id);
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
                                if(response.body().replace("\"","").equals("success")){
                                    ClosePop();
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

}
