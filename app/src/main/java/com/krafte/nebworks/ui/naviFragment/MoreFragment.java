package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kakao.sdk.common.util.KakaoCustomTabsClient;
import com.kakao.sdk.talk.TalkApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.databinding.MorefragmentBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MoreFragment extends Fragment {
    private final static String TAG = "MoreFragment";
    private MorefragmentBinding binding;
    Context mContext;
    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    ImageView more_icon;
    TextView more_tv;
    String USER_INFO_ID = "";
    String USER_LOGIN_METHOD = "";
    String USER_INFO_AUTH = "";

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String place_management_office = "";
    String place_address = "";
    String place_latitude = "";
    String place_longitude = "";
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";

    public static MoreFragment newInstance(int number) {
        MoreFragment fragment = new MoreFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
            Log.i(TAG, "num : " + num);
        }
    }



    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.morefragment, container, false);
        binding = MorefragmentBinding.inflate(inflater);
        mContext = inflater.getContext();
        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            //Singleton Area
            place_id            = PlaceCheckData.getInstance().getPlace_id();
            place_name          = PlaceCheckData.getInstance().getPlace_name();
            place_owner_id      = PlaceCheckData.getInstance().getPlace_owner_id();
            place_owner_name    = PlaceCheckData.getInstance().getPlace_owner_name();
            place_address       = PlaceCheckData.getInstance().getPlace_address();
            place_latitude      = PlaceCheckData.getInstance().getPlace_latitude();
            place_longitude     = PlaceCheckData.getInstance().getPlace_longitude();
            place_start_time    = PlaceCheckData.getInstance().getPlace_start_time();
            place_end_time      = PlaceCheckData.getInstance().getPlace_end_time();
            place_img_path      = PlaceCheckData.getInstance().getPlace_img_path();
            place_start_date    = PlaceCheckData.getInstance().getPlace_start_date();
            place_created_at    = PlaceCheckData.getInstance().getPlace_created_at();

            USER_INFO_ID        = UserCheckData.getInstance().getUser_id();
            USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");

            //shardpref Area
            USER_LOGIN_METHOD   = shardpref.getString("USER_LOGIN_METHOD","");

            if (!USER_INFO_AUTH.isEmpty()) {
                if(!USER_LOGIN_METHOD.equals("NEB")){
                    binding.settingList05Txt.setText("연결해제");
                    binding.loginMethodIcon.setVisibility(View.VISIBLE);
                    if(USER_LOGIN_METHOD.equals("Google")){
                        binding.loginMethodIcon.setBackgroundResource(R.drawable.google);
                    } else if(USER_LOGIN_METHOD.equals("Kakao")){
                        binding.loginMethodIcon.setBackgroundResource(R.drawable.kakao);
                    } else if(USER_LOGIN_METHOD.equals("Naver")){
                        binding.loginMethodIcon.setBackgroundResource(R.drawable.naver_icon);
                    }
                }
            }
            setBtnEvent();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
//        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!USER_INFO_ID.isEmpty() && !place_id.isEmpty()){
            SetAllMemberList();
        }
    }

    public void setBtnEvent() {
        binding.settingList01Txt.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putString("retrun_page", "MoreActivity");
                shardpref.putString("editstate", "edit");
                pm.ProfileEdit(mContext);
            }
        });
        binding.settingList02Txt.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                pm.NotifyList(mContext);
            }
        });
        binding.settingList03Txt.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                pm.Push(mContext);
            }
        });

        binding.settingList04Txt.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                kakaoChannel();
            }
        });

        binding.settingList05Txt.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                pm.UserDel(mContext);
            }
        });
    }

    public void SetAllMemberList() {
        dlog.i("-----SetAllMemberList-----");
        dlog.i("place_id : " + place_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("-----SetAllMemberList-----");
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id, USER_INFO_ID);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        Log.e("SetAllMemberList onSuccess : ", jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            String name = Response.getJSONObject(0).getString("name");
                            String img_path = Response.getJSONObject(0).getString("img_path");
                            String getjikgup = Response.getJSONObject(0).getString("jikgup");

                            if (USER_INFO_AUTH.isEmpty()) {
                                binding.userName.setText("김이름");
                                binding.jikgup.setText("미정");
                                binding.userProfile.setBackgroundColor(Color.GRAY);
                            } else {
                                binding.userName.setText(name);
                                binding.jikgup.setText((getjikgup.equals("null")?"미정":getjikgup));
                                Glide.with(mContext).load(img_path)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(binding.userProfile);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    TalkApiClient talkApiClient = TalkApiClient.getInstance();
    private void kakaoChannel() {
        Uri url = talkApiClient.channelChatUrl("_rTkJxj");
        KakaoCustomTabsClient instance = KakaoCustomTabsClient.INSTANCE;
        instance.openWithDefault(mContext, url);
    }
}
