package com.krafte.nebworks.ui.approval;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.ui.fragment.approval.ApprovalFragment1;
import com.krafte.nebworks.ui.fragment.approval.ApprovalFragment2;
import com.krafte.nebworks.ui.fragment.approval.ApprovalFragment3;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskApprovalFragment extends AppCompatActivity {
    private static final String TAG = "TaskApprovalFragment";
    Context mContext;

    private final DateCurrent dc = new DateCurrent();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    //XML ID
    ViewPager2 viewPager;
    TabLayout tabLayout;
    ImageView menu;

    // shared 저장값
    PreferenceHelper shardpref;
//    ArrayList<WorkCheckListData.WorkCheckListData_list> mList;
    ApprovalAdapter mAdapter = null;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    int SELECT_POSITION = 0;
    String store_no;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    GetResultData resultData = new GetResultData();
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
        setContentView(R.layout.activity_task_approval);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_gray_bar);
        icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.menu_blue_bar);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
        wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
        gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
        return_page = shardpref.getString("return_page","");
        store_no = shardpref.getString("store_no", "");
        shardpref.putString("returnPage", "BusinessApprovalActivity");

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        menu = findViewById(R.id.menu);

        final List<String> tabElement;
        tabElement = Arrays.asList("처리중", "승인", "반려");
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(ApprovalFragment1.newInstance(0));
        fragments.add(ApprovalFragment2.newInstance(1));
        fragments.add(ApprovalFragment3.newInstance(2));

        viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);
        viewPager.setAdapter(viewPagerFregmentAdapter);
//        viewPager.setUserInputEnabled(false);

        //ViewPager2와 TabLayout을 연결
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            TextView textView = new TextView(TaskApprovalFragment.this);
            textView.setText(tabElement.get(position));
            textView.setTextColor(Color.parseColor("#696969"));
            textView.setGravity(Gravity.CENTER);
            tab.setCustomView(textView);
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.getTabAt(SELECT_POSITION).select();
                    }
                }, 100);

        if (SELECT_POSITION != -99) {
            viewPager.setCurrentItem(SELECT_POSITION);
        }

        menu.setOnClickListener(v -> {
            if(USER_INFO_AUTH.equals("0")){
                pm.Main(mContext);
            }else{
                pm.Main2(mContext);
            }
        });
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        shardpref.putInt("SELECT_POSITION",0);
        if(USER_INFO_AUTH.equals("0")){
            pm.Main(mContext);
        }else{
            pm.Main2(mContext);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
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
