package com.krafte.nebworks.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.ActivityMainfragmentBinding;
import com.krafte.nebworks.ui.naviFragment.CommunityFragment;
import com.krafte.nebworks.ui.naviFragment.HomeFragment;
import com.krafte.nebworks.ui.naviFragment.MoreFragment;
import com.krafte.nebworks.ui.naviFragment.WorkgotoFragment;
import com.krafte.nebworks.ui.naviFragment.WorkstatusFragment;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 점주용 메인페이지 프래그먼트
 * */
public class MainFragment extends AppCompatActivity {
    private static final String TAG = "TaskApprovalFragment";
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
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_EMAIL = "";
    String place_id = "";
    String place_name = "";
    String place_imgpath = "";

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
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            place_id = shardpref.getString("place_id", "");
            place_name = shardpref.getString("place_name", "");
            place_imgpath = shardpref.getString("place_imgpath", "");
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            return_page = shardpref.getString("return_page", "");
            store_no = shardpref.getString("store_no", "");
            shardpref.putString("returnPage", "BusinessApprovalActivity");


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

            SetAllMemberList();
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("place_name : " + place_name);

            bottom_icon02.setBackgroundResource(R.drawable.ic_task_off);
            bottom_icon03.setBackgroundResource(R.drawable.ic_member_off);
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
            fragments.add(HomeFragment.newInstance(0));
            fragments.add(WorkgotoFragment.newInstance(1));
            fragments.add(WorkstatusFragment.newInstance(2));
            fragments.add(CommunityFragment.newInstance(3));
            fragments.add(MoreFragment.newInstance(4));
            viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);

            binding.viewPager.setAdapter(viewPagerFregmentAdapter);

            //ViewPager2와 TabLayout을 연결
            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                TextView textView = new TextView(MainFragment.this);
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
            }

            drawerLayout.addDrawerListener(listener);
            drawerView.setOnTouchListener((v, event) -> false);

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
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            name = Response.getJSONObject(0).getString("name");
                            img_path = Response.getJSONObject(0).getString("img_path");
                            getjikgup = Response.getJSONObject(0).getString("jikgup");

                            user_name.setText(name);
                            jikgup.setText(getjikgup);
                            Glide.with(mContext).load(img_path)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(user_profile);
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
    ImageView close_btn, user_profile, my_setting;
    TextView user_name, jikgup, store_name;

    @SuppressLint("LongLogTag")
    public void setNavBarBtnEvent() {
        drawerView = findViewById(R.id.drawer2);
        close_btn = findViewById(R.id.close_btn);
        user_profile = findViewById(R.id.user_profile);
        my_setting = findViewById(R.id.my_setting);
        user_name = findViewById(R.id.user_name);
        jikgup = findViewById(R.id.jikgup);
        store_name = findViewById(R.id.store_name);


        dlog.i("name : " + name);
        dlog.i("img_path : " + img_path);
        dlog.i("getjikgup : " + getjikgup);

        store_name.setText(place_name);

        close_btn.setOnClickListener(v -> {
            drawerLayout.closeDrawer(drawerView);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setNavBarBtnEvent();
        getNotReadFeedcnt();
    }

    public void btnOnclick(View view) {
        if (view.getId() == R.id.menu) {
            drawerLayout.openDrawer(drawerView);
        } else if (view.getId() == R.id.out_store) {
            pm.PlaceList(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            dlog.i("메인 Click!");
            binding.title.setText("");
            binding.tabLayout.getTabAt(0).select();
        } else if (view.getId() == R.id.bottom_navigation02) {
            dlog.i("할일 Click!");
            binding.title.setText("");
            binding.tabLayout.getTabAt(1).select();
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.putInt("SELECT_POSITION_sub", 0);
        } else if (view.getId() == R.id.bottom_navigation03) {
            dlog.i("근무현황 Click!");
            binding.title.setText("근무현황");
            binding.tabLayout.getTabAt(2).select();
        } else if (view.getId() == R.id.bottom_navigation04) {
            dlog.i("커뮤니티 Click!");
            binding.title.setText("커뮤니티");
            binding.tabLayout.getTabAt(3).select();
        } else if (view.getId() == R.id.bottom_navigation05) {
            dlog.i("더보기 Click!");
            binding.title.setText("더보기");
            binding.tabLayout.getTabAt(4).select();
        } else if (view.getId() == R.id.select_nav01) {
            pm.PlaceList(mContext);
        } else if (view.getId() == R.id.select_nav02) {
            pm.PlaceAddGo(mContext);
        } else if (view.getId() == R.id.select_nav03) {
            pm.MemberManagement(mContext);
        } else if (view.getId() == R.id.select_nav04) {
            shardpref.putInt("SELECT_POSITION", 2);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.Main(mContext);
        } else if (view.getId() == R.id.select_nav05) {
            shardpref.putString("Tap", "0");
            pm.PayManagement(mContext);
        } else if (view.getId() == R.id.select_nav06) {
            shardpref.putString("Tap", "1");
            pm.PayManagement(mContext);
        } else if (view.getId() == R.id.select_nav07) {//캘린더보기 | 할일페이지
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.Main(mContext);
        } else if (view.getId() == R.id.select_nav08) {//할일추가하기 - 작성페이지로
            pm.addWorkGo(mContext);
        } else if (view.getId() == R.id.select_nav09) {
            pm.Approval(mContext);
        } else if (view.getId() == R.id.select_nav12) {
            dlog.i("커뮤니티 Click!");
            binding.title.setText("커뮤니티");
            binding.tabLayout.getTabAt(3).select();
        } else if (view.getId() == R.id.select_nav10) {
            dlog.i("근로계약서 전체 관리");
            pm.ContractFragment(mContext);
        }

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
                dlog.e( "WorkTapListFragment1 / setRecyclerView");
                dlog.e( "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    dlog.e( "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        String NotRead = Response.getJSONObject(0).getString("notread_feed");
                        if(NotRead.equals("0")){
                            binding.notiRed.setVisibility(View.INVISIBLE);
                        }else{
                            binding.notiRed.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
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
