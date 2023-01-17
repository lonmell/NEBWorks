package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.GetConfirmPlaceInterface;
import com.krafte.nebworks.dataInterface.NonmemberInterface;
import com.krafte.nebworks.dataInterface.PlaceMemberAddInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.dataInterface.UserNumSelectInterface;
import com.krafte.nebworks.databinding.ActivityInviteMemberBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class InviteMemberActivity extends AppCompatActivity {
    private ActivityInviteMemberBinding binding;
    private final static String TAG = "InviteMemberActivity";
    Context mContext;

    PreferenceHelper shardpref;

    //Shared
    String place_id = "";
    String USER_INFO_NAME = "";
    String USER_INFO_PHONE = "";
    String USER_LOGIN_METHOD = "";

    //other
    boolean check = false;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    Handler mHandler;

    String INPUT_NAME = "";
    String INPUT_PHONE = "";
    String place_name = "";
    DBConnection dbConnection = new DBConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteMemberBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        try {
            mContext = this;
            setBtnEvent();
            dlog.DlogContext(mContext);
            //Singleton Area
            place_id        = PlaceCheckData.getInstance().getPlace_id();
            place_name      = PlaceCheckData.getInstance().getPlace_name();
            USER_INFO_NAME  = UserCheckData.getInstance().getUser_name();
            USER_INFO_PHONE = UserCheckData.getInstance().getUser_phone();
            
            dlog.i("USER_INFO_NAME      = " + USER_INFO_NAME);
            dlog.i("USER_INFO_PHONE     = " + USER_INFO_PHONE);
            dlog.i("place_name          = " + place_name);
            
            //shardpref Area
            shardpref           = new PreferenceHelper(mContext);
            USER_LOGIN_METHOD   = shardpref.getString("USER_LOGIN_METHOD", "");
            dlog.i("USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);
            
            binding.inputbox01.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    INPUT_NAME = s.toString();
                }
            });
            binding.inputbox02.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    INPUT_PHONE = s.toString();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        BtnOneCircleFun(true);
    }

    private void setBtnEvent() {
        binding.addMemberBtn.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            UserCheck();
        });

        binding.addMemberNodata.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            // String으로 받아서 넣기
            String sendMessage = "["+place_name+"] 사장님과 즐겁게 근무해요.\n" +
                    "\n" +
                    "[매장근무하기]\n" +
                    "\n" +
                    "1.사장님넵 다운로드 \n" +
                    "\n" +
                    "Android: https://play.google.com/store/apps/details?id=com.krafte.nebworks\n" +
                    "\n" +
                    "IOS:  https://apps.apple.com/apps/details?id=com.krafte.nebworks\n" +
                    "\n" +
                    "2.앱 설치후 회원가입 > 로그인 > 근무자님! 으로 이동 후 매장추가 버튼 터치! \n" +
                    "매장 찾기 후 사장님 번호로 매장 검색!\n" +
                    "근무 신청 터치!\n" +
                    "\n";
            intent.putExtra(Intent.EXTRA_TEXT, sendMessage);

            Intent shareIntent = Intent.createChooser(intent, "share");
            startActivity(shareIntent);
        });
    }

    RetrofitConnect rc = new RetrofitConnect();
    //해당 사용자가 가입되어있는 회원인지 아닌지를 구분하기 위함.
    public void UserCheck() {
        dlog.i("------UserCheck------");
        dlog.i("INPUT_NAME : " + INPUT_NAME);
        dlog.i("INPUT_PHONE : " + INPUT_PHONE);
        dlog.i("------UserCheck------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserNumSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserNumSelectInterface api = retrofit.create(UserNumSelectInterface.class);
        Call<String> call = api.getData(INPUT_NAME, INPUT_PHONE);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
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
                                    String id = Response.getJSONObject(0).getString("id");
                                    String name = Response.getJSONObject(0).getString("name");
                                    String phone = Response.getJSONObject(0).getString("phone");
                                    String join_date = "";

                                    Calendar c = Calendar.getInstance();
                                    int mYear = c.get(Calendar.YEAR);
                                    int mMonth = c.get(Calendar.MONTH) + 1;
                                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                                    dlog.i("mYear : " + mYear);
                                    dlog.i("mMonth : " + mMonth);
                                    dlog.i("mDay : " + mDay);
                                    join_date = mYear + "-" + (String.valueOf(mMonth).length() == 1 ? "0" + mMonth : mMonth) + "-"
                                            + (String.valueOf(mDay).length() == 1 ? "0" + String.valueOf(mDay) : String.valueOf(mDay));

                                    dlog.i("ConfrimPlaceMember(id) : " + ConfrimPlaceMember(id));

                                    if (ConfrimPlaceMember(id)) {
                                        AddPlaceMember(id, name, phone, "", join_date);
                                    } else {
                                        Toast_Nomal("이미 직원으로 등록된 사용자 입니다.");
                                    }

                                } else {
                                    dlog.i("Response 2: " + response.body().length());
                                    Toast_Nomal("가입하지 않은 사용자입니다.\n초대문구를 발송합니다.");
                                    PostNonmember();
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

    FeedTemplate feedTemplate = new FeedTemplate(
            new Content("NEBWorks 에서 사용자님을 초대합니다. 회원가입 후 초대된 매장에 자동 가입됩니다.",
                    "http://krafte.net/NEBWorks/identificon.png",
                    new Link("https://www.naver.com",
                            "https://www.naver.com"),
                    "#회원가입 #매장초대 #협업툴"
            ),
            new ItemContent("",
                    "",
                    "사장님!넵",
                    "http://krafte.net/NEBWorks/identificon.png",
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

    int cnt = 0;
    boolean cnttf = false;
    private boolean ConfrimPlaceMember(String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetConfirmPlaceInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        GetConfirmPlaceInterface api = retrofit.create(GetConfirmPlaceInterface.class);
        Call<String> call = api.getData(place_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("ConfrimPlaceMember jsonResponse length : " + jsonResponse.length());
                            dlog.i("ConfrimPlaceMember jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    cnt = Integer.parseInt(Response.getJSONObject(0).getString("cnt"));
                                    cnttf = (cnt == 0);
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
        return cnt == 0;
    }

    //직원한테
    public void AddPlaceMember(String user_id, String name, String phone, String Jumin, String JoinDate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberAddInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberAddInterface api = retrofit.create(PlaceMemberAddInterface.class);
        Call<String> call = api.getData(place_id, user_id, Jumin, "3", JoinDate);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("AddPlaceMember jsonResponse length : " + jsonResponse.length());
                            dlog.i("AddPlaceMember jsonResponse : " + response.body());
                            if (jsonResponse.replace("\"", "").equals("success")) {
                                dlog.i("매장 멤버 추가 완료");
                                Toast_Nomal("직원 초대가 완료되었습니다[승인 대기 중]");
                                shardpref.putInt("SELECT_POSITION", 2);
                                shardpref.putInt("SELECT_POSITION_sub", 0);
                                String message = "[" + place_name + "] 에서 초대가 도착했습니다.";
                                getUserToken(user_id,"1",message);
                                AddPush("직원초대",message,user_id);
                                pm.MemberManagement(mContext);
                            } else {
                                Toast_Nomal("이미 직원으로 등록된 사용자 입니다.");
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

    String message = "";
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
                dlog.i("getUserToken jsonResponse length : " + jsonResponse.length());
                dlog.i("getUserToken jsonResponse : " + jsonResponse);
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
    String place_owner_id = "";
    public void AddPush(String title, String content, String user_id) {
        place_owner_id = shardpref.getString("place_owner_id","");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(place_id, "", title, content, place_owner_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("AddPush jsonResponse length : " + jsonResponse.length());
                            dlog.i("AddPush jsonResponse : " + jsonResponse);
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

    String click_action = "";//점주 > 근로자
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "PlaceList1";//PlaceListActivity0은 점주권한 매장리스트, PlaceListActivity1은 근로자 권한 매장 리스트
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

    private void PostNonmember(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NonmemberInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        NonmemberInterface api = retrofit.create(NonmemberInterface.class);
        Call<String> call = api.getData(place_id, INPUT_NAME, INPUT_PHONE);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("PostNonmember jsonResponse length : " + jsonResponse.length());
                            dlog.i("PostNonmember jsonResponse : " + jsonResponse);
                            if (jsonResponse.replace("\"", "").equals("success")) {
                                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(mContext)) {
                                    kakaoLink();
                                } else {
                                    webKakaoLink();
                                }
                                shardpref.putInt("SELECT_POSITION", 0);
                                shardpref.putInt("SELECT_POSITION_sub", 0);
                                pm.Main(mContext);
                            } else {
                                Toast_Nomal("이미 직원으로 등록되었거나 초대중인 직원입니다.");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void BtnOneCircleFun(boolean tf){
        binding.addMemberNodata.setClickable(tf);
        binding.addMemberNodata.setEnabled(tf);

        binding.addMemberBtn.setClickable(tf);
        binding.addMemberBtn.setEnabled(tf);
    }
}
