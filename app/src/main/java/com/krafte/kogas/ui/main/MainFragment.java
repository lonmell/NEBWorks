package com.krafte.kogas.ui.main;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.kogas.R;
import com.krafte.kogas.adapter.ApprovalAdapter;
import com.krafte.kogas.adapter.ViewPagerFregmentAdapter;
import com.krafte.kogas.databinding.ActivityMainfragmentBinding;
import com.krafte.kogas.ui.fragment.approval.ApprovalFragment1;
import com.krafte.kogas.ui.naviFragment.CalendarFragment;
import com.krafte.kogas.ui.naviFragment.HomeFragment;
import com.krafte.kogas.ui.naviFragment.MoreFragment;
import com.krafte.kogas.ui.naviFragment.WorkgotoFragment;
import com.krafte.kogas.ui.naviFragment.WorkstatusFragment;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends AppCompatActivity {
    private static final String TAG = "TaskApprovalFragment";
    private ActivityMainfragmentBinding binding;
    Context mContext;
    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    //BottomNavigation
    ImageView home_icon,worktogo_icon,workstatus_icon,more_icon;
    TextView home_tv,worktogo_tv,workstatus_tv,more_tv;

    // shared 저장값
    PreferenceHelper shardpref;
    ApprovalAdapter mAdapter = null;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
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

        try{
            icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
            icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub =  shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            return_page = shardpref.getString("return_page", "");
            store_no = shardpref.getString("store_no", "");
            shardpref.putString("returnPage", "BusinessApprovalActivity");

            home_icon = binding.getRoot().findViewById(R.id.home_icon);
            worktogo_icon = binding.getRoot().findViewById(R.id.worktogo_icon);
            workstatus_icon = binding.getRoot().findViewById(R.id.workstatus_icon);
            more_icon = binding.getRoot().findViewById(R.id.more_icon);

            home_tv = binding.getRoot().findViewById(R.id.home_tv);
            worktogo_tv = binding.getRoot().findViewById(R.id.worktogo_tv);
            workstatus_tv = binding.getRoot().findViewById(R.id.workstatus_tv);
            more_tv = binding.getRoot().findViewById(R.id.more_tv);

            final List<String> tabElement;
            tabElement = Arrays.asList("홈", "할일", "캘린더", "근무현황", "더보기");
            ArrayList<Fragment> fragments = new ArrayList<>();
            fragments.add(HomeFragment.newInstance(0));
            fragments.add(WorkgotoFragment.newInstance(1));
            fragments.add(CalendarFragment.newInstance(2));
            fragments.add(WorkstatusFragment.newInstance(3));
            fragments.add(MoreFragment.newInstance(4));

            viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);
            binding.viewPager.setAdapter(viewPagerFregmentAdapter);
//        viewPager.setUserInputEnabled(false);

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
                    binding.viewPager.setCurrentItem(tab.getPosition(),false);
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(paging_position == 0){
            pm.PlaceListBack(mContext);
        }else{
            binding.tabLayout.getTabAt(0).select();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void btnOnclick(View view) {
        if (view.getId() == R.id.out_store) {
            pm.PlaceListBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            binding.tabLayout.getTabAt(0).select();
        } else if (view.getId() == R.id.bottom_navigation02) {
            binding.tabLayout.getTabAt(1).select();
            shardpref.putInt("SELECT_POSITION",1);
            shardpref.putInt("SELECT_POSITION_sub",0);
        } else if (view.getId() == R.id.bottom_navigation03) {
            binding.tabLayout.getTabAt(2).select();
        } else if (view.getId() == R.id.bottom_navigation04) {
            binding.tabLayout.getTabAt(3).select();
        } else if (view.getId() == R.id.bottom_navigation05) {
            binding.tabLayout.getTabAt(4).select();
        }
    }

    private void ChangePosition(int i){
        home_icon.setBackgroundResource(R.drawable.home_resize);
        home_tv.setTextColor(Color.parseColor("#000000"));

        worktogo_icon.setBackgroundResource(R.drawable.worktogo_resize);
        worktogo_tv.setTextColor(Color.parseColor("#000000"));

        workstatus_icon.setBackgroundResource(R.drawable.workstatus_resize);
        workstatus_tv.setTextColor(Color.parseColor("#000000"));

        more_icon.setBackgroundResource(R.drawable.more_icon_resize);
        more_tv.setTextColor(Color.parseColor("#000000"));
        if(i == 0){
            home_icon.setBackgroundResource(R.drawable.home_on_resize);
            home_tv.setTextColor(Color.parseColor("#68B0FF"));
        }else if(i == 1){
            worktogo_icon.setBackgroundResource(R.drawable.worktogo_on_resize);
            worktogo_tv.setTextColor(Color.parseColor("#68B0FF"));
        }else if(i == 2){
           dlog.i("calendar view");
        }else if(i == 3){
            workstatus_icon.setBackgroundResource(R.drawable.workstatus_on_resize);
            workstatus_tv.setTextColor(Color.parseColor("#68B0FF"));
        }else if(i == 4){
            more_icon.setBackgroundResource(R.drawable.more_icon_on_resize);
            more_tv.setTextColor(Color.parseColor("#68B0FF"));
        }
    }

    @Override
    public void onDestroy(){
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
