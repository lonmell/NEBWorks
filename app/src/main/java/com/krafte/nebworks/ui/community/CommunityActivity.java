package com.krafte.nebworks.ui.community;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityWorkCommunityBinding;
import com.krafte.nebworks.ui.fragment.community.WorkCommunityFragment1;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {
    private ActivityWorkCommunityBinding binding;
    private final static String TAG = "WorkCommunityActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String store_insurance = "";
    String returnPage = "";
    String store_name = "";
    int SELECTED_POSITION = 0;

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    PageMoveClass pm = new PageMoveClass();
    int paging_position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_work_community);
        binding = ActivityWorkCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;

        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        returnPage = shardpref.getString("returnPage", "");
        Log.i(TAG, "USER_INFO_AUTH : " + USER_INFO_AUTH);


        final List<String> tabElement;
        tabElement = Arrays.asList("자유게시판", "사장님페이지", "세금/노무");

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(WorkCommunityFragment1.newInstance(0));
        fragments.add(WorkCommunityFragment1.newInstance(1));
        fragments.add(WorkCommunityFragment1.newInstance(2));

        viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);
        binding.viewPager.setAdapter(viewPagerFregmentAdapter);
        binding.viewPager.setUserInputEnabled(false);

        //ViewPager2와 TabLayout을 연결
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            TextView textView = new TextView(CommunityActivity.this);
            textView.setText(tabElement.get(position));
            textView.setTextColor(Color.parseColor("#696969"));
            textView.setGravity(Gravity.CENTER);
            tab.setCustomView(textView);
        }).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                paging_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        setAddBtnSetting();
//        menu.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, EmployerMainActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            super.onBackPressed();
//        });
    }

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("게시글 작성");
        add_worktime_btn.setOnClickListener(v -> {
            pm.CommunityAdd(mContext);
        });
    }

    public void btnOnclick(View view) {
//        if (view.getId() == R.id.bottom_navigation01) {
//            pm.EmployerMainL(mContext);
//        } else if (view.getId() == R.id.bottom_navigation02) {
//            pm.EmployerWorkListR(mContext);
//        } else if (view.getId() == R.id.bottom_navigation03) {
//            pm.EmployerCalenderL(mContext);
//        } else if (view.getId() == R.id.bottom_navigation04) {
////            pm.EmployerWorkGotoListL(mContext);
//        } else if (view.getId() == R.id.bottom_navigation05) {
//            pm.EmployerWorkMoreR(mContext);
//        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setBtnEvent() {
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
