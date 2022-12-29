package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
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
import com.krafte.nebworks.dataInterface.DelWorkhourInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FeedCommentDelInterface;
import com.krafte.nebworks.dataInterface.FeedDelInterface;
import com.krafte.nebworks.dataInterface.MemberOutPlaceInterface;
import com.krafte.nebworks.dataInterface.PlaceDelInterface;
import com.krafte.nebworks.dataInterface.PlaceMemberAddInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.dataInterface.UserDelInterface;
import com.krafte.nebworks.databinding.ActivityTwobuttonPopBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import org.json.JSONArray;
import org.json.JSONException;

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
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_LOGIN_METHOD = "";
    String USER_INFO_PHONE = "";
    String place_id = "";
    String mem_id = "";

    //Other
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    String message = "";
    String topic = "";
    String click_action = "";
    DateCurrent dc = new DateCurrent();

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    //Naver
    String ClientID = "cN1sIOhyOshPLKgNL4Sj";
    String ClientSecret = "iFS5etlgYt";
    String ClientName = "넵";
    NaverIdLoginSDK naverIdLoginSDK = NaverIdLoginSDK.INSTANCE;

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

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        //Naver
        naverIdLoginSDK.initialize(TwoButtonPopActivity.this, ClientID, ClientSecret, ClientName);
        naverIdLoginSDK.setShowMarketLink(true);
        naverIdLoginSDK.setShowBottomTab(true);

        setBtnEvent();


        USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD","");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        place_id = shardpref.getString("place_id", "-1");
        mem_id = shardpref.getString("mem_id","");

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
                shardpref.remove("ALARM_ONOFF");
                shardpref.remove("USER_LOGIN_METHOD");
                shardpref.remove("USER_INFO_EMAIL");
                shardpref.putBoolean("isFirstLogin", true);
                shardpref.putBoolean("USER_LOGIN_CONFIRM", false);

                if (USER_LOGIN_METHOD.equals("Google")) {
                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(this, task -> {
                                pm.Login(mContext);
                            });
                } else if(USER_LOGIN_METHOD.equals("Kakao")){
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
                } else if(USER_LOGIN_METHOD.equals("Naver")) {
                    naverIdLoginSDK.logout();
                    naverIdLoginSDK.authenticate(TwoButtonPopActivity.this, oAuthLoginCallback); //연결해제
                    pm.Login(mContext);
                }else{
                    pm.Login(mContext);
                }
                finish();
            }else if(flag.equals("회원탈퇴")){
                UserDelete(USER_INFO_ID);
            }else if (flag.equals("댓글삭제")) {
                String comment_id = "0";
                comment_id = shardpref.getString("comment_id","");
                CommentDelete(comment_id);
                ClosePop();
            } else if (flag.equals("공지삭제") || flag.equals("공지삭제2")) {
                String feed_id = "0";
                feed_id = shardpref.getString("edit_feed_id","");
                FeedDelete(feed_id);
            } else if(flag.equals("종료")){
                finish();
                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
            } else if(flag.equals("매장삭제")){
                PlaceDel(place_id);
            } else if(flag.equals("직원삭제")){
                TaskDel();
            }  else if(flag.equals("근무정보삭제")){
                WorkHourDel();
            } else if(flag.equals("그룹신청")){
                message = "새로운 근무지원 신청이 도착했습니다.";
                click_action = "MemberManagement";
                dlog.i(message);
                String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                AddPlaceMember(USER_INFO_ID, USER_INFO_NAME, USER_INFO_PHONE, "",today);
            } else if (flag.equals("작성여부")) {
                shardpref.putInt("SELECT_POSITION",3);
                pm.Main(mContext);
                ClosePop();
            } else if(flag.equals("닉네임없음")){
                pm.ProfileEdit(mContext);
                ClosePop();
            } else if(flag.equals("게시글삭제")){
                String feed_id = "0";
                feed_id = shardpref.getString("feed_id","");
                FeedDelete(feed_id);
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

//                    Toast.makeText(getApplicationContext(),"$response",Toast.LENGTH_SHORT).show();

                    // 토큰 삭제 및 로그아웃 코드
                    nidOAuthLogin.callDeleteTokenApi(getApplicationContext(), new OAuthLoginCallback() {

                        @Override
                        public void onSuccess() {
                            //서버에서 토큰 삭제에 성공한 상태입니다.
                            pm.Login(mContext);
                        }

                        @Override
                        public void onFailure(int i, @NonNull String s) {
                            // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                            // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                            Log.d("TAG", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}");
                            Log.d("TAG", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}");
                        }

                        @Override
                        public void onError(int i, @NonNull String s) {
                            onFailure(i, s);
                        }
                    });

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

    private void ClosePop(){
        runOnUiThread(() -> {
            if(flag.equals("공지삭제2")){
                finish();
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                overridePendingTransition(0, R.anim.translate_down);
                pm.FeedList(mContext);
            }else{
                finish();
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                overridePendingTransition(0, R.anim.translate_down);
                super.onBackPressed();
            }
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
                                    Toast_Nomal("회원 탈퇴가 완료되었습니다.");
                                    if(USER_LOGIN_METHOD.equals("Naver")){
                                        naverIdLoginSDK.authenticate(TwoButtonPopActivity.this, oAuthLoginCallback); //연결해제
                                    }
                                    pm.Login(mContext);
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
                            dlog.i("CommentDelete jsonResponse length : " + response.body().length());
                            dlog.i("CommentDelete jsonResponse : " + response.body());
                            try {
                                if(response.body().replace("\"","").equals("")){
                                    shardpref.putString("editstate","DelComment");
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
    public void PlaceDel(String id) {
        dlog.i("PlaceDel id : " + id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceDelInterface api = retrofit.create(PlaceDelInterface.class);
        Call<String> call = api.getData(id);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("TaskDel jsonResponse length : " + response.body().length());
                            dlog.i("TaskDel jsonResponse : " + response.body());
                            try {
                                if(response.body().replace("\"","").equals("success")){
                                    Toast_Nomal("해당 매장이 삭제완료되었습니다.");
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

    public void TaskDel() {
//        매장 멤버 삭제 (매장에서 나가기, 매장에서 내보내기)
//        http://krafte.net/kogas/place/delete_member.php?place_id=28&user_id=24
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MemberOutPlaceInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MemberOutPlaceInterface api = retrofit.create(MemberOutPlaceInterface.class);
        Call<String> call = api.getData(place_id, mem_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("TaskDel jsonResponse length : " + response.body().length());
                            dlog.i("TaskDel jsonResponse : " + response.body());
                            try {
                                if (response.body().replace("\"", "").equals("success")) {
                                    Toast_Nomal("해당 직원의 데이터 삭제가 완료되었습니다.");
                                    shardpref.remove("remote");
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

    public void WorkHourDel() {
//        매장 멤버 삭제 (매장에서 나가기, 매장에서 내보내기)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DelWorkhourInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        DelWorkhourInterface api = retrofit.create(DelWorkhourInterface.class);
        Call<String> call = api.getData(place_id,mem_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("TaskDel jsonResponse length : " + response.body().length());
                            dlog.i("TaskDel jsonResponse : " + response.body());
                            try {
                                if (response.body().replace("\"", "").equals("success")) {
                                    Toast_Nomal("해당 직원의 근무 데이터 삭제가 완료되었습니다.");
                                    shardpref.remove("remote");
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

    /*지원한 매장의 점주에게 이력서 발송 + 지원요청할 지원자에게 점주가 매장정보 발송(스카우트)*/
    public void AddPlaceMember(String user_id, String name, String phone, String Jumin, String JoinDate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberAddInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberAddInterface api = retrofit.create(PlaceMemberAddInterface.class);
        Call<String> call = api.getData(place_id, user_id,Jumin,"0",JoinDate);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("AddPlaceMember jsonResponse length : " + response.body().length());
                    dlog.i("AddPlaceMember jsonResponse : " + response.body());
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {

                            if (response.body().replace("\"", "").equals("success")) {
                                Toast_Nomal("근무신청이 완료되었습니다.");
                                String place_owner_id = shardpref.getString("place_owner_id", "");
                                String message = "새로운 근무 신청이 도착했습니다.";
                                getUserToken(place_owner_id,"0",message);
                                AddPush("근무신청",message,place_owner_id);
                                ClosePop();
                            }else{
                                Toast_Nomal("이미 직원으로 등록된 사용자 입니다.");
                            }
                        }else{
                            dlog.i(response.body());
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


    //근로자 > 점주 ( 초대수락 FCM )
    public void getUserToken(String user_id, String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + user_id);
        dlog.i("type : " + type);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(user_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel4").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "4", place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }
    public void AddPush(String title, String content, String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, USER_INFO_ID, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("AddStroeNoti jsonResponse length : " + response.body().length());
                            dlog.i("AddStroeNoti jsonResponse : " + response.body());
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

    DBConnection dbConnection = new DBConnection();

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "Member0";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
