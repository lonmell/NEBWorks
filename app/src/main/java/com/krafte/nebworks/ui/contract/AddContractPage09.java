package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.KakaoCustomTabsClient;
import com.kakao.sdk.link.LinkClient;
import com.kakao.sdk.link.WebSharerClient;
import com.kakao.sdk.template.model.Content;
import com.kakao.sdk.template.model.FeedTemplate;
import com.kakao.sdk.template.model.ItemContent;
import com.kakao.sdk.template.model.Link;
import com.kakao.sdk.template.model.Social;
import com.kakao.sdk.user.UserApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd09Binding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddContractPage09 extends AppCompatActivity {
    private ActivityContractAdd09Binding binding;
    private final static String TAG = "AddContractPage08";
    private static final int SIGNING_BITMAP = 2022;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";
    String contract_id = "";
    String place_owner_id = "";
    String contract_email = "";
    String worker_name = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    String title = "";
    String content = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContractAdd09Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        //Singleton Area


        //shardpref Area
        shardpref       = new PreferenceHelper(mContext);
        worker_id       = shardpref.getString("worker_id","0");
        contract_id     = shardpref.getString("contract_id","0");
        contract_email  = shardpref.getString("contract_email","0");
        worker_name     = shardpref.getString("worker_name", "");
        place_id        = shardpref.getString("place_id", "");
        place_owner_id  = shardpref.getString("place_owner_id", "");
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID", "");

        // Kakao SDK 객체 초기화
        KakaoSdk.init(this, getString(R.string.kakao_native_key));
        setBtnEvent();
    }

    String kakaotitle = "";
    String emailtitle = "";
    private void setBtnEvent(){
        binding.next.setOnClickListener(v -> {
            kakaotitle = binding.input01.getText().toString();
            emailtitle = binding.input02.getText().toString();
            String message = "["+worker_name+"]근로자의 근로계약서 사인이 도착했습니다.";
            getUserToken(place_owner_id,"0",message);
            AddPush("근로계약서",message,place_owner_id);
                if(!kakaotitle.isEmpty()){
                    //카카오 타이틀이 있을때
                    if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(mContext)) {
                        kakaoLink();
                    } else {
                        webKakaoLink();
                    }
                    if(!emailtitle.isEmpty()){
                        //이메일 타이틀이 있을때
                    }
                }
            pm.ContractFragment(mContext);
            RemoveShared();
        });

        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("progress_pos");
            if(!shardpref.getString("progress_pos","").isEmpty()){
                pm.ContractFragment(mContext);
            }else{
                super.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed(){

        if(!shardpref.getString("progress_pos","").isEmpty()){
            pm.ContractFragment(mContext);
        }else{
            super.onBackPressed();
        }
        shardpref.remove("progress_pos");
    }

    private void RemoveShared(){
        shardpref.remove("worker_id");
        shardpref.remove("contract_id");
        shardpref.remove("contract_email");
        shardpref.remove("worker_name");
    }

    FeedTemplate feedTemplate = new FeedTemplate(
            new Content(kakaotitle,
                    "https://nepworks.net/NEBWorks/identificon.png",
                    new Link("https://www.naver.com",
                            "https://www.naver.com"),
                    "#근로계약서"
            ),
            new ItemContent("",
                    "",
                    "사장님!넵",
                    "https://nepworks.net/NEBWorks/identificon.png",
                    "협업 툴"
            ),
            new Social(1004, 1004, 1004),
            Arrays.asList(new com.kakao.sdk.template.model.Button("앱 다운로드", new Link("https://www.naver.com", "https://www.naver.com")))
    );

    public void kakaoLink() {
        String TAG = "kakaoLink()";
        // 카카오톡으로 카카오링크 공유 가능
        LinkClient.getInstance().defaultTemplate(mContext, feedTemplate, null, (linkResult, error) -> {
            if (error != null) {
                Log.e("TAG", "카카오링크 보내기 실패", error);
            } else if (linkResult != null) {
                Log.d(TAG, "카카오링크 보내기 성공 ${linkResult.intent}");
                mContext.startActivity(linkResult.getIntent());

                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                Log.w("TAG", "Warning Msg: " + linkResult.getWarningMsg());
                Log.w("TAG", "Argument Msg: " + linkResult.getArgumentMsg());
            }
            return null;
        });
    }

    public void webKakaoLink() {
        String TAG = "webKakaoLink()";

        // 카카오톡 미설치: 웹 공유 사용 권장
        // 웹 공유 예시 코드
        Uri sharerUrl = WebSharerClient.getInstance().defaultTemplateUri(feedTemplate);

        // CustomTabs으로 웹 브라우저 열기
        // 1. CustomTabs으로 Chrome 브라우저 열기
        try {
            KakaoCustomTabsClient.INSTANCE.openWithDefault(mContext, sharerUrl);
        } catch (UnsupportedOperationException e) {
            // Chrome 브라우저가 없을 때 예외처리
        }

        // 2. CustomTabs으로 디바이스 기본 브라우저 열기
        try {
            KakaoCustomTabsClient.INSTANCE.open(mContext, sharerUrl);
        } catch (ActivityNotFoundException e) {
            // 인터넷 브라우저가 없을 때 예외처리
        }
    }


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
                        boolean channelId1 = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "2", place_id);
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
        Call<String> call = api.getData(place_id, "", title, content, user_id, place_owner_id);
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

    String click_action = "";
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "contract0";
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
//            activity.runOnUiThread(() -> {
//            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
