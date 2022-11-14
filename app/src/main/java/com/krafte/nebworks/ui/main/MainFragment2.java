package com.krafte.nebworks.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.databinding.ActivityMainfragmentBinding;
import com.krafte.nebworks.ui.fragment.approval.ApprovalFragment1;
import com.krafte.nebworks.ui.naviFragment.CalendarFragment;
import com.krafte.nebworks.ui.naviFragment.HomeFragment2;
import com.krafte.nebworks.ui.naviFragment.MoreFragment;
import com.krafte.nebworks.ui.naviFragment.WorkgotoFragment;
import com.krafte.nebworks.ui.naviFragment.WorkstatusFragment;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

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
 * 근로자용 메인페이지 프래그먼트
 * */
public class MainFragment2 extends AppCompatActivity {
    private static final String TAG = "TaskApprovalFragment";
    private ActivityMainfragmentBinding binding;
    Context mContext;
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    //BottomNavigation
    ImageView bottom_icon01, bottom_icon02, bottom_icon03, bottom_icon04, bottom_icon05;

    // shared 저장값
    PreferenceHelper shardpref;
    ApprovalAdapter mAdapter = null;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    int accept_state = 0;
    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;
    String place_id;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    String place_name = "";
    String place_imgpath = "";

    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    PageMoveClass pm = new PageMoveClass();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    int paging_position = 0;
    Dlog dlog = new Dlog();
    String return_page = "";


    ApprovalFragment1 af1 = new ApprovalFragment1();

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
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            return_page = shardpref.getString("return_page", "");
            place_id = shardpref.getString("place_id", "");
            accept_state = shardpref.getInt("accept_state",-99);
            shardpref.putString("returnPage", "BusinessApprovalActivity");
            place_name = shardpref.getString("place_name", "");
            place_imgpath = shardpref.getString("place_imgpath", "");


            bottom_icon01 = binding.getRoot().findViewById(R.id.bottom_icon01);
            bottom_icon02 = binding.getRoot().findViewById(R.id.bottom_icon02);
            bottom_icon03 = binding.getRoot().findViewById(R.id.bottom_icon03);
            bottom_icon04 = binding.getRoot().findViewById(R.id.bottom_icon04);
            bottom_icon05 = binding.getRoot().findViewById(R.id.bottom_icon05);

            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);


            final List<String> tabElement;
            tabElement = Arrays.asList("홈", "할일", "캘린더", "근무현황", "더보기");
            ArrayList<Fragment> fragments = new ArrayList<>();
            //근로자일때
            fragments.add(HomeFragment2.newInstance(0));
            fragments.add(WorkgotoFragment.newInstance(1));
            fragments.add(WorkstatusFragment.newInstance(2));
            fragments.add(CalendarFragment.newInstance(3));
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
            }

            binding.drawerLayout.addDrawerListener(listener);
            binding.drawerLayout.setOnTouchListener((v, event) -> true);
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


    /*본인 정보 START*/
    String name ="";
    String img_path ="";
    String getjikgup ="";
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
            Call<String> call = api.getData(place_id,USER_INFO_ID);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            name = Response.getJSONObject(0).getString("name");
                            img_path = Response.getJSONObject(0).getString("img_path");
                            getjikgup = Response.getJSONObject(0).getString("jikgup");

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
    ImageView close_btn,user_profile,my_setting;
    TextView user_name,jikgup,store_name;
    TextView select_nav01,select_nav02;

    @SuppressLint("LongLogTag")
    public void setNavBarBtnEvent() {
        drawerView          = findViewById(R.id.drawer2);
        close_btn           = findViewById(R.id.close_btn);
        user_profile        = findViewById(R.id.user_profile);
        my_setting          = findViewById(R.id.my_setting);
        user_name           = findViewById(R.id.user_name);
        jikgup              = findViewById(R.id.jikgup);
        store_name          = findViewById(R.id.store_name);
        select_nav01        = findViewById(R.id.select_nav01);
        select_nav02        = findViewById(R.id.select_nav02);
        SetAllMemberList();
        dlog.i("name : " + name);
        dlog.i("img_path : " + img_path);
        dlog.i("getjikgup : " + getjikgup);

        user_name.setText(name);
        jikgup.setText(getjikgup);
        Glide.with(mContext).load(img_path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(user_profile);

        store_name.setText(place_name);

        select_nav01.setOnClickListener(v -> {
            pm.PlaceList(mContext);
        });
        select_nav02.setOnClickListener(v -> {
            pm.PlaceAddGo(mContext);
        });
        close_btn.setOnClickListener(v -> {
            drawerLayout.closeDrawer(drawerView);
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (paging_position == 0) {
            pm.PlaceList(mContext);
        } else {
            binding.tabLayout.getTabAt(0).select();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setNavBarBtnEvent();
    }

    public void btnOnclick(View view) {
        if (view.getId() == R.id.menu) {
            drawerLayout.openDrawer(drawerView);
        } else if (view.getId() == R.id.out_store) {
            pm.PlaceList(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            dlog.i("메인 Click!");
            binding.tabLayout.getTabAt(0).select();
        } else if (view.getId() == R.id.bottom_navigation02) {
            dlog.i("할일 Click!");
            binding.tabLayout.getTabAt(1).select();
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.putInt("SELECT_POSITION_sub", 0);
        } else if (view.getId() == R.id.bottom_navigation03) {
            dlog.i("직원관리 Click!");
            binding.tabLayout.getTabAt(2).select();
        } else if (view.getId() == R.id.bottom_navigation04) {
            dlog.i("커뮤니티 Click!");
            binding.tabLayout.getTabAt(3).select();
        } else if (view.getId() == R.id.bottom_navigation05) {
            dlog.i("더보기 Click!");
            binding.tabLayout.getTabAt(4).select();
        }
    }

    private void ChangePosition(int i) {
        bottom_icon01.setBackgroundResource(R.drawable.bottom_icon01);
        bottom_icon02.setBackgroundResource(R.drawable.bottom_icon02);
        bottom_icon03.setBackgroundResource(R.drawable.bottom_icon03);
        bottom_icon04.setBackgroundResource(R.drawable.bottom_icon04);
        bottom_icon05.setBackgroundResource(R.drawable.bottom_icon05);

        if (i == 0) {
            bottom_icon01.setBackgroundResource(R.drawable.bottom_icon01_on);
        } else if (i == 1) {
            bottom_icon02.setBackgroundResource(R.drawable.bottom_icon02_on);
        } else if (i == 2) {
            bottom_icon03.setBackgroundResource(R.drawable.bottom_icon03_on);
        } else if (i == 3) {
            bottom_icon04.setBackgroundResource(R.drawable.bottom_icon04_on);
        } else if (i == 4) {
            bottom_icon05.setBackgroundResource(R.drawable.bottom_icon05_on);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
