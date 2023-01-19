package com.krafte.nebworks.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.user.UserApiClient;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMCrerateInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FCMUpdateInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.ActivityMainfragmentBinding;
import com.krafte.nebworks.ui.naviFragment.CommunityFragment;
import com.krafte.nebworks.ui.naviFragment.HomeFragment2;
import com.krafte.nebworks.ui.naviFragment.MoreFragment;
import com.krafte.nebworks.ui.naviFragment.WorkgotoFragment;
import com.krafte.nebworks.ui.naviFragment.WorkstatusFragment;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 근로자용 메인페이지 프래그먼트
 * */
public class MainFragment2 extends AppCompatActivity {
    private static final String TAG = "MainFragment2";
    private ActivityMainfragmentBinding binding;
    Context mContext;
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    //BottomNavigation
    ImageView bottom_icon01, bottom_icon02, bottom_icon03, bottom_icon04, bottom_icon05;
    TextView bottom_icon01tv, bottom_icon02tv, bottom_icon03tv, bottom_icon04tv, bottom_icon05tv;

    // shared 저장값
    PreferenceHelper shardpref;
    ApprovalAdapter mAdapter = null;
    String USER_INFO_ID      = "";
    String USER_INFO_NAME    = "";
    String USER_INFO_AUTH    = "";
    String USER_INFO_EMAIL   = "";
    String USER_LOGIN_METHOD = "";
    String place_id          = "";
    String place_name        = "";
    String place_imgpath     = "";
    String place_owner_id    = "";

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
    String store_no;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    PageMoveClass pm = new PageMoveClass();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    int paging_position = 0;
    Dlog dlog = new Dlog();
    String return_page = "";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mainfragment);
        binding = ActivityMainfragmentBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        try {
            icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
            icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID        = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME      = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_EMAIL     = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION     = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            USER_LOGIN_METHOD   = shardpref.getString("USER_LOGIN_METHOD", "");
            place_id            = shardpref.getString("place_id", "");
            place_name          = shardpref.getString("place_name", "");
            place_imgpath       = shardpref.getString("place_imgpath", "");
            wifi_certi_flag     = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag      = shardpref.getBoolean("gps_certi_flag", false);
            return_page         = shardpref.getString("return_page", "");
            store_no            = shardpref.getString("store_no", "");
            place_owner_id      = shardpref.getString("place_owner_id", "0");
            shardpref.putString("returnPage", "MainFragment2");


            bottom_icon01 = binding.getRoot().findViewById(R.id.bottom_icon01);
            bottom_icon02 = binding.getRoot().findViewById(R.id.bottom_icon02);
            bottom_icon03 = binding.getRoot().findViewById(R.id.bottom_icon03);
            bottom_icon04 = binding.getRoot().findViewById(R.id.bottom_icon04);
            bottom_icon05 = binding.getRoot().findViewById(R.id.bottom_icon05);
            bottom_icon01tv = binding.getRoot().findViewById(R.id.bottom_icon01tv);
            bottom_icon02tv = binding.getRoot().findViewById(R.id.bottom_icon02tv);
            bottom_icon03tv = binding.getRoot().findViewById(R.id.bottom_icon03tv);
            bottom_icon04tv = binding.getRoot().findViewById(R.id.bottom_icon04tv);
            bottom_icon05tv = binding.getRoot().findViewById(R.id.bottom_icon05tv);

            drawerLayout = findViewById(R.id.drawer_layout);
            drawerView = findViewById(R.id.drawer2);
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("place_name : " + place_name);

            ChangePosition(0);

            final List<String> tabElement;
            tabElement = Arrays.asList("홈", "할일", "근무현황", "커뮤니티", "더보기");
            ArrayList<Fragment> fragments = new ArrayList<>();
            bottom_icon01tv.setText(tabElement.get(0));
            bottom_icon02tv.setText(tabElement.get(1));
            bottom_icon03tv.setText(tabElement.get(2));
            bottom_icon04tv.setText(tabElement.get(3));
            bottom_icon05tv.setText(tabElement.get(4));

            //점주일때
            fragments.add(HomeFragment2.newInstance(0));
            fragments.add(WorkgotoFragment.newInstance(1));
            fragments.add(WorkstatusFragment.newInstance(2));
            fragments.add(CommunityFragment.newInstance(3));
            fragments.add(MoreFragment.newInstance(4));
            viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);

            binding.viewPager.setAdapter(viewPagerFregmentAdapter);

            //ViewPager2와 TabLayout을 연결
            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                TextView textView = new TextView(MainFragment2.this);
                textView.setText(tabElement.get(position));
                textView.setTextColor(Color.parseColor("#696969"));
                textView.setGravity(Gravity.CENTER);
                tab.setCustomView(textView);
            }).attach();

            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    binding.viewPager.setCurrentItem(tab.getPosition(), false);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            binding.viewPager.setUserInputEnabled(false);
            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    paging_position = position;
                    ChangePosition(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            binding.tabLayout.getTabAt(SELECT_POSITION).select();
                        }
                    }, 100);

            if (SELECT_POSITION != -99) {
                binding.viewPager.setCurrentItem(SELECT_POSITION);
                binding.tabLayout.getTabAt(SELECT_POSITION).select();
            }

            drawerLayout.addDrawerListener(listener);
            drawerView.setOnTouchListener((v, event) -> false);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mAuth = FirebaseAuth.getInstance();

            binding.notiArea.setOnClickListener(v -> {
                pm.FeedList(mContext);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            //슬라이드 했을때
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            //Drawer가 오픈된 상황일때 호출
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            // 닫힌 상황일 때 호출
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            // 특정상태가 변결될 때 호출
        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (paging_position == 0) {
//            shardpref.putString("event","backpressed");
            shardpref.remove("event");
            pm.PlaceList(mContext);
        } else {
            binding.tabLayout.getTabAt(0).select();
        }
    }

    /*본인 정보 START*/
    String name = "";
    String img_path = "";
    String getjikgup = "";
    RetrofitConnect rc = new RetrofitConnect();

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
                            if(Response.length() != 0){
                                name = Response.getJSONObject(0).getString("name");
                                img_path = Response.getJSONObject(0).getString("img_path");
                                getjikgup = Response.getJSONObject(0).getString("jikgup");

                                user_name.setText(name);
                                if (!getjikgup.equals("null")) {
                                    jikgup.setText(getjikgup);
                                } else {
                                    jikgup.setText("미정");
                                }
                                Glide.with(mContext).load(img_path)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(user_profile);
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

    /*본인 정보 START*/
    //navbar.xml
    DrawerLayout drawerLayout;
    View drawerView;
    LinearLayout area03, area04, area06;
    ImageView close_btn, user_profile, my_setting;
    TextView user_name, jikgup, store_name,select_nav09,select_nav05;

    @SuppressLint("LongLogTag")
    public void setNavBarBtnEvent() {
        drawerView      = findViewById(R.id.drawer2);
        close_btn       = findViewById(R.id.close_btn);
        user_profile    = findViewById(R.id.user_profile);
        my_setting      = findViewById(R.id.my_setting);
        user_name       = findViewById(R.id.user_name);
        jikgup          = findViewById(R.id.jikgup);
        store_name      = findViewById(R.id.store_name);
        area03          = findViewById(R.id.area03);
        area04          = findViewById(R.id.area04);
        area06          = findViewById(R.id.area06);
        select_nav09    = findViewById(R.id.select_nav09);
        select_nav05    = findViewById(R.id.select_nav05);

        area04.setVisibility(View.GONE);
        area06.setVisibility(View.GONE);
        select_nav09.setVisibility(View.GONE);
        select_nav05.setVisibility(View.GONE);

        store_name.setText(place_name);
        close_btn.setOnClickListener(v -> {
            drawerLayout.closeDrawer(drawerView);
        });
    }

    Timer timer = new Timer();
    @Override
    public void onResume() {
        super.onResume();
        setNavBarBtnEvent();
        UserCheck();
        SetAllMemberList();
        getNotReadFeedcnt();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    DBConnection dbc = new DBConnection();
    /*
     * 20230105 HomFragment에서만 한번 사용자 id , 매장 id를 사용해
     * 사용자 정보를 체크, 이후 다른 페이지에서는 Singleton 전역변수로 사용
     * */
    public void UserCheck() {
        Thread th = new Thread(() -> {
            dbc.UserCheck(place_id, USER_INFO_ID);
            runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join(); // 작동한 스레드의 종료까지 대기 후 메인 스레드 실행
            getPlaceData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getPlaceData() {
        Thread th = new Thread(() -> {
            dbc.PlacegetData(place_id);
            runOnUiThread(() -> {
                getFCMToken();
                String user_id = UserCheckData.getInstance().getUser_id();
                String owner_id = PlaceCheckData.getInstance().getPlace_id();
                if(user_id.equals(owner_id)){
                    UserCheckData.getInstance().setUser_auth("0");
                }else{
                    UserCheckData.getInstance().setUser_auth("1");
                }
                shardpref.putString("place_id",PlaceCheckData.getInstance().getPlace_id());
                shardpref.putString("place_owner_id",PlaceCheckData.getInstance().getPlace_owner_id());
            });
        });
        th.start();
        try {
            th.join(); // 작동한 스레드의 종료까지 대기 후 메인 스레드 실행
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    String id = "";
    String user_id = "";
    String type = "";
    String get_token = "";
    String channel1 = "1";
    String channel2 = "1";
    String channel3 = "1";
    String channel4 = "1";


    //본인 토큰 생성
    @SuppressLint("LongLogTag")
    public void getFCMToken() {
        type = PlaceCheckData.getInstance().getPlace_owner_id().equals(USER_INFO_ID) ? "0" : "1";

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
//                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d("TAG", msg);
                    dlog.i("getFCMToken token : " + token);
                    FcmStateSelect(token);
                });

    }

    private void FcmStateSelect(String token) {
        //메인페이지 처음 들어왔을때 생성 - 본인

        dlog.i("-----FcmStateSelect-----");
        dlog.i("place_owner_id : " + place_owner_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("type : " + type);
        dlog.i("-----FcmStateSelect-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, type);
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

                                if (jsonResponse.replace("[", "").replace("]", "").length() == 0) {
                                    id = place_id;
                                    user_id = USER_INFO_ID;
                                    get_token = "";
                                    type = place_owner_id.equals(USER_INFO_ID) ? "0" : "1";
                                    channel1 = "1";
                                    channel2 = "1";
                                    channel3 = "1";
                                    channel4 = "1";
                                } else {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    id = Response.getJSONObject(0).getString("id");
                                    user_id = Response.getJSONObject(0).getString("user_id");
                                    type = Response.getJSONObject(0).getString("type");
                                    get_token = Response.getJSONObject(0).getString("token");
                                    channel1 = Response.getJSONObject(0).getString("channel1");
                                    channel2 = Response.getJSONObject(0).getString("channel2");
                                    channel3 = Response.getJSONObject(0).getString("channel3");
                                    channel4 = Response.getJSONObject(0).getString("channel4");

                                    shardpref.putString("token", token);
                                    shardpref.putString("type", type);
                                    shardpref.putBoolean("channelId1", channel1.equals("1"));
                                    shardpref.putBoolean("channelId2", channel2.equals("1"));
                                    shardpref.putBoolean("channelId3", channel3.equals("1"));
                                    shardpref.putBoolean("channelId4", channel4.equals("1"));

                                    dlog.i("channel1 : " + channel1);
                                    dlog.i("channel2 : " + channel2);
                                    dlog.i("channel3 : " + channel3);
                                    dlog.i("channel4 : " + channel4);
                                }
                                if (get_token.isEmpty()) {
                                    dlog.i("getFCMToken FcmTokenCreate");
                                    FcmTokenCreate(token);
                                } else {
                                    dlog.i("getFCMToken FcmTokenUpdate");
                                    FcmTokenUpdate(token);
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

    private void FcmTokenCreate(String token) {
        //메인페이지 처음 들어왔을때 생성 - 본인
        dlog.i("------FcmTokenCreate-------");
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("type :" + type);
        dlog.i("token :" + token);
        dlog.i("channel1 :" + channel1);
        dlog.i("channel2 :" + channel2);
        dlog.i("channel3 :" + channel3);
        dlog.i("channel4 :" + channel4);
        dlog.i("------FcmTokenCreate-------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMCrerateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMCrerateInterface api = retrofit.create(FCMCrerateInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, type, token, channel1, channel2, channel3, channel4);
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

    public void FcmTokenUpdate(String token) {
        dlog.i("------FcmTokenUpdate-------");
        dlog.i("USER_INFO_ID :" + USER_INFO_ID);
        dlog.i("type :" + type);
        dlog.i("token :" + token);
        dlog.i("channel1 :" + channel1);
        dlog.i("channel2 :" + channel2);
        dlog.i("channel3 :" + channel3);
        dlog.i("channel4 :" + channel4);
        dlog.i("------FcmTokenUpdate-------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMUpdateInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMUpdateInterface api = retrofit.create(FCMUpdateInterface.class);
        Call<String> call = api.getData(id, token, channel1, channel2, channel3, channel4);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                if (jsonResponse.replace("\"", "").equals("success")) {
                    dlog.i("FcmTokenUpdate jsonResponse length : " + jsonResponse.length());
                    dlog.i("FcmTokenUpdate jsonResponse : " + jsonResponse);
                } else {
                    Toast.makeText(mContext, "네트워크가 정상적이지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }
    public void btnOnclick(View view) {
        if (view.getId() == R.id.menu) {
            drawerLayout.openDrawer(drawerView);
        } else if (view.getId() == R.id.out_store) {
            pm.PlaceList(mContext);
            drawerLayout.closeDrawer(drawerView);
            shardpref.putString("event","out_store");
        } else if (view.getId() == R.id.bottom_navigation01) {
            dlog.i("메인 Click!");
            binding.title.setText("");
            binding.tabLayout.getTabAt(0).select();
            shardpref.putInt("SELECT_POSITION",0);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.bottom_navigation02) {
            dlog.i("급여관리 Click!");
            shardpref.putString("Tap", "0");
            binding.title.setText("할일");
            binding.tabLayout.getTabAt(1).select();
            shardpref.putInt("SELECT_POSITION", 1);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.bottom_navigation03) {
            dlog.i("캘린더 Click!");
            binding.title.setText("근무현황");
            binding.tabLayout.getTabAt(2).select();
            shardpref.putInt("SELECT_POSITION", 2);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.bottom_navigation04) {
            dlog.i("커뮤니티 Click!");
            binding.title.setText("커뮤니티");
            binding.tabLayout.getTabAt(3).select();
            shardpref.putInt("SELECT_POSITION", 3);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.bottom_navigation05) {
            dlog.i("더보기 Click!");
            binding.title.setText("더보기");
            binding.tabLayout.getTabAt(4).select();
            shardpref.putInt("SELECT_POSITION", 4);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav01) {
            drawerLayout.closeDrawer(drawerView);
            shardpref.putString("event", "out_store");
            pm.PlaceList(mContext);
        } else if (view.getId() == R.id.select_nav02) {
            pm.PlaceAddGo(mContext);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav03) {
            pm.MemberManagement(mContext);
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav04) {
            drawerLayout.closeDrawer(drawerView);
        } else if (view.getId() == R.id.select_nav05) {
//            shardpref.putString("Tap", "0");
//            pm.PayManagement(mContext);
            shardpref.putString("Tap", "0");
            binding.title.setText("급여관리");
            binding.tabLayout.getTabAt(1).select();
            shardpref.putInt("SELECT_POSITION", 1);
        } else if (view.getId() == R.id.select_nav06) {
            shardpref.putString("Tap", "1");
            binding.title.setText("급여관리");
            pm.PayManagement(mContext);
        } else if (view.getId() == R.id.select_nav07) {//캘린더보기 | 할일페이지
//            pm.Main2(mContext);
            drawerLayout.closeDrawer(drawerView);
            shardpref.putString("Tap", "0");
            binding.title.setText("할일");
            binding.tabLayout.getTabAt(1).select();
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.putInt("SELECT_POSITION_sub", 1);
        } else if (view.getId() == R.id.select_nav08) {//할일추가하기 - 작성페이지로
            drawerLayout.closeDrawer(drawerView);
            shardpref.putInt("make_kind", 1);
            pm.addWorkGo(mContext);
        } else if (view.getId() == R.id.select_nav09) {
            drawerLayout.closeDrawer(drawerView);
            pm.Approval(mContext);
        } else if (view.getId() == R.id.select_nav12) {
            drawerLayout.closeDrawer(drawerView);
            dlog.i("커뮤니티 Click!");
            binding.title.setText("커뮤니티");
            binding.tabLayout.getTabAt(3).select();
            shardpref.putInt("SELECT_POSITION", 3);
        } else if (view.getId() == R.id.select_nav10) {
            drawerLayout.closeDrawer(drawerView);
            dlog.i("근로계약서 전체 관리");
            pm.ContractFragment(mContext);
        } else if (view.getId() == R.id.select_nav12_1) {
            drawerLayout.closeDrawer(drawerView);
            dlog.i("출결관리/근로자 상세");
            pm.MemberDetail(mContext);
        } else if (view.getId() == R.id.select_nav14) {
            shardpref.clear();
            shardpref.remove("ALARM_ONOFF");
            shardpref.remove("USER_LOGIN_METHOD");
            shardpref.putBoolean("isFirstLogin", true);

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
            }else{
                pm.Login(mContext);
            }
        }
    }

    public void getNotReadFeedcnt() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData("", "", "","1",USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("getNotReadFeedcnt");
                dlog.e( "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        if(!jsonResponse.equals("[]") && Response.length() != 0){
                            String NotRead = Response.getJSONObject(0).getString("notread_feed");
                            if(NotRead.equals("0") || NotRead.isEmpty()){
                                binding.notiRed.setVisibility(View.INVISIBLE);
                            }else{
                                binding.notiRed.setVisibility(View.VISIBLE);
                            }
                        }else{
                            binding.notiRed.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e( "에러 = " + t.getMessage());
            }
        });
    }



    private void ChangePosition(int i) {
        bottom_icon01.setBackgroundResource(R.drawable.ic_main_off);
        bottom_icon02.setBackgroundResource(R.drawable.ic_task_off);
        bottom_icon03.setBackgroundResource(R.drawable.ic_member_off);
        bottom_icon04.setBackgroundResource(R.drawable.ic_community_off);
        bottom_icon05.setBackgroundResource(R.drawable.ic_more_off);
        bottom_icon01tv.setTextColor(Color.parseColor("#C3C3C3"));
        bottom_icon02tv.setTextColor(Color.parseColor("#C3C3C3"));
        bottom_icon03tv.setTextColor(Color.parseColor("#C3C3C3"));
        bottom_icon04tv.setTextColor(Color.parseColor("#C3C3C3"));
        bottom_icon05tv.setTextColor(Color.parseColor("#C3C3C3"));

        if (i == 0) {
            bottom_icon01.setBackgroundResource(R.drawable.ic_main_on);
            bottom_icon01tv.setTextColor(Color.parseColor("#6395EC"));
        } else if (i == 1) {
            bottom_icon02.setBackgroundResource(R.drawable.ic_task_on);
            bottom_icon02tv.setTextColor(Color.parseColor("#6395EC"));
        } else if (i == 2) {
            bottom_icon03.setBackgroundResource(R.drawable.ic_member_on);
            bottom_icon03tv.setTextColor(Color.parseColor("#6395EC"));
        } else if (i == 3) {
            bottom_icon04.setBackgroundResource(R.drawable.ic_community_on);
            bottom_icon04tv.setTextColor(Color.parseColor("#6395EC"));
        } else if (i == 4) {
            bottom_icon05.setBackgroundResource(R.drawable.ic_more_on);
            bottom_icon05tv.setTextColor(Color.parseColor("#6395EC"));
        }
    }
//    //-------몰입화면 설정
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }
//
//    private void hideSystemUI() {
//        // Enables regular immersive mode.
//        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
//        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
//    }
//    //-------몰입화면 설정
}