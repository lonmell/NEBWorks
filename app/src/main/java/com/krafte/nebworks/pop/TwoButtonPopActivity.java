package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.kakao.sdk.common.util.KakaoCustomTabsClient;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.DelWorkhourInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FcmTokenDelInterface;
import com.krafte.nebworks.dataInterface.FeedCommentDelInterface;
import com.krafte.nebworks.dataInterface.FeedDelInterface;
import com.krafte.nebworks.dataInterface.MemberOutPlaceInterface;
import com.krafte.nebworks.dataInterface.MemberOutPlaceInterface2;
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
import com.krafte.nebworks.util.RetrofitConnect;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import org.json.JSONArray;
import org.json.JSONException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TwoButtonPopActivity extends Activity {
    private ActivityTwobuttonPopBinding binding;

    Context mContext;
    String flag = "";
    String title = "";
    String data = "";
    String left_btn_txt = "";
    String right_btn_txt = "";
    String take_user_id = "";

    Intent intent;

    //shared Data
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_LOGIN_METHOD = "";
    String USER_INFO_PHONE = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String mem_id = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;

    //Other
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
    NaverIdLoginSDK naverIdLoginSDK;

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
        naverIdLoginSDK = NaverIdLoginSDK.INSTANCE;

        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
        if (USER_LOGIN_METHOD.equals("Google")) {
            dlog.i("USER_LOGIN_METHOD : " + USER_LOGIN_METHOD);
            //Google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mAuth = FirebaseAuth.getInstance();
        } else if (USER_LOGIN_METHOD.equals("Kakao")) {
            dlog.i("USER_LOGIN_METHOD : " + USER_LOGIN_METHOD);
        } else if (USER_LOGIN_METHOD.equals("Naver")) {
            dlog.i("USER_LOGIN_METHOD : " + USER_LOGIN_METHOD);
            //Naver
            naverIdLoginSDK.initialize(TwoButtonPopActivity.this, ClientID, ClientSecret, ClientName);
            naverIdLoginSDK.setShowMarketLink(true);
            naverIdLoginSDK.setShowBottomTab(true);
        }

        setBtnEvent();

        USER_INFO_ID        = shardpref.getString("USER_INFO_ID","");
        USER_LOGIN_METHOD   = shardpref.getString("USER_LOGIN_METHOD","");
        USER_INFO_PHONE     = shardpref.getString("USER_INFO_PHONE", "");
        place_id            = shardpref.getString("place_id", "-1");
        mem_id              = shardpref.getString("mem_id","");
        USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");
        SELECT_POSITION     = shardpref.getInt("SELECT_POSITION", 0);
        SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);

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
            try{
                //데이터 전달하기
                if(flag.equals("로그아웃")){
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
//                    FcmTokenDel();
                    finish();
                    shardpref.remove("USER_INFO_AUTH");
                }else if(flag.equals("회원탈퇴")){
                    shardpref.remove("USER_INFO_AUTH");
                    UserDelete();
                    FcmTokenDel();
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
                    PlaceDel();
                } else if(flag.equals("직원삭제")){
                    TaskDel();
                } else if(flag.equals("직원삭제2")){
                    TaskDel2();
                } else if(flag.equals("근무정보삭제")){
                    WorkHourDel();
                } else if(flag.equals("그룹신청")){
                    message = "새로운 근무지원 신청이 도착했습니다.";
                    click_action = "MemberManagement";
                    dlog.i(message);
                    String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                    dlog.i("-----그룹신청-----");
                    dlog.i("today : " + today);
                    dlog.i("take_user_id : " + take_user_id);
                    dlog.i("type : 0");
                    dlog.i("message : " + message);
                    dlog.i("-----그룹신청-----");
                    getUserToken(take_user_id,"0",message);
                    AddPush("근무신청",message,take_user_id);
                    AddPlaceMember("",today);
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
                } else if (flag.equals("더미")) {
                    shardpref.putString("AuthState","더미");
                    pm.AuthSelect(mContext);
//                    shardpref.putString("USER_INFO_AUTH", "1");
                    shardpref.putInt("SELECT_POSITION", 0);
                    shardpref.putInt("SELECT_POSITION_sub", 0);
                } else if (flag.equals("채널")) {
                    Uri url = TalkApiClient.getInstance().channelChatUrl("_rTkJxj");
                    KakaoCustomTabsClient instance = KakaoCustomTabsClient.INSTANCE;
                    instance.openWithDefault(mContext, url);
                    pm.AuthSelect(mContext);
                }else if (flag.equals("직원미입력")) {
                    shardpref.putInt("SELECT_POSITION",SELECT_POSITION);
                    shardpref.putInt("SELECT_POSITION_sub",SELECT_POSITION_sub);
                    pm.Main(mContext);
                } else if (flag.equals("할일")) {
                    shardpref.putInt("SELECT_POSITION",SELECT_POSITION);
                    shardpref.putInt("SELECT_POSITION_sub",SELECT_POSITION_sub);
                    if (USER_INFO_AUTH.equals("0")) {
                        pm.Main(mContext);
                    } else {
                        pm.Main2(mContext);
                    }
                } else if (flag.equals("업데이트")){
                    shardpref.putString("UPDATEYN","N");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                    mContext.startActivity(intent);

                    finish();
                    intent = new Intent();
                    intent.putExtra("result", "Close Popup");
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(0, R.anim.translate_down);
                    super.onBackPressed();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        binding.popLeftTxt.setOnClickListener(v -> {
                shardpref.remove("AuthState");
                ClosePop();
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
            if(flag.equals("공지삭제2")) {
                finish();
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                overridePendingTransition(0, R.anim.translate_down);
                pm.FeedList(mContext);
            }else if (flag.equals("채널")) {
                pm.AuthSelect(mContext);
            }else if (flag.equals("게시글삭제2")) {
                shardpref.putInt("SELECT_POSITION", 3);
                if(USER_INFO_AUTH.equals("0")){
                    pm.Main(mContext);
                }else{
                    pm.Main2(mContext);
                }
            } else if (flag.equals("업데이트")){
                shardpref.putString("UPDATEYN","N");
                finish();
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                overridePendingTransition(0, R.anim.translate_down);
                super.onBackPressed();
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
    RetrofitConnect rc = new RetrofitConnect();
    public void UserDelete() {
        dlog.i("UserDelete id : " + USER_INFO_ID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserDelInterface api = retrofit.create(UserDelInterface.class);
        Call<String> call = api.getData(USER_INFO_ID);
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
                                if(jsonResponse.replace("\"","").equals("success")){
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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(jsonResponse.replace("\"","").equals("")){
                                    Toast_Nomal(shardpref.getString("comment_title","") + " 댓글 삭제가 완료되었습니다");
                                    shardpref.putString("editstate","DelComment");
                                    shardpref.putInt("page_kind",1);
                                    shardpref.remove("comment_title");
                                    shardpref.remove("comment_id");
                                    shardpref.remove("comment_contents");
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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(jsonResponse.replace("\"","").equals("success")){
                                    shardpref.remove("feed_id");
                                    shardpref.remove("title");
                                    shardpref.remove("contents");
                                    shardpref.remove("writer_id");
                                    shardpref.remove("writer_name");
                                    shardpref.remove("writer_img_path");
                                    shardpref.remove("feed_img_path");
                                    shardpref.remove("view_cnt");
                                    shardpref.remove("comment_cnt");
                                    shardpref.remove("category");
                                    shardpref.remove("state");
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
    public void PlaceDel() {
        dlog.i("PlaceDel id : " + place_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceDelInterface api = retrofit.create(PlaceDelInterface.class);
        Call<String> call = api.getData(place_id);
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
                                if(jsonResponse.replace("\"","").equals("success")){
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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if (jsonResponse.replace("\"", "").equals("success")) {
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

    private void TaskDel2(){
        dlog.i("TaskDel2 place_id : " + place_id);
        dlog.i("TaskDel2 mem_id : " + mem_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MemberOutPlaceInterface2.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MemberOutPlaceInterface2 api = retrofit.create(MemberOutPlaceInterface2.class);
        Call<String> call = api.getData(place_id, mem_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("TaskDel2 jsonResponse length : " + jsonResponse.length());
                            dlog.i("TaskDel2 jsonResponse : " + jsonResponse);
                            try {
                                if (jsonResponse.replace("\"", "").equals("success")) {
                                    Toast_Nomal("해당 직원의 데이터 모두 삭제가 완료되었습니다.");
                                    shardpref.remove("remote");
                                    shardpref.remove("mem_id");
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
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if (jsonResponse.replace("\"", "").equals("success")) {
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
    public void AddPlaceMember(String Jumin, String JoinDate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberAddInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberAddInterface api = retrofit.create(PlaceMemberAddInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID ,Jumin,"0",JoinDate);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            if (jsonResponse.replace("\"", "").equals("success")) {
                                shardpref.remove("event");
                                Toast_Nomal("근무신청이 완료되었습니다.");
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
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel4").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "4");
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
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("AddStroeNoti jsonResponse length : " + jsonResponse.length());
                            dlog.i("AddStroeNoti jsonResponse : " + jsonResponse);
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

    private void PushFcmSend(String topic, String title, String message, String token, String tag) {
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

    //더이상 알람이 오지 않도록 로그아웃이나 회월탈퇴 했을때는 토큰을 지워준다.
    private void FcmTokenDel(){
        dlog.i("-----FcmTokenDel-----");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("-----FcmTokenDel-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FcmTokenDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FcmTokenDelInterface api = retrofit.create(FcmTokenDelInterface.class);
        Call<String> call = api.getData(USER_INFO_ID,USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("FcmTokenDel jsonResponse length : " + jsonResponse.length());
                            dlog.i("FcmTokenDel jsonResponse : " + jsonResponse);
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
