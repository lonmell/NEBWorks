package com.krafte.nebworks.ui.member;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ApprovalAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.bottomsheet.MemberOption;
import com.krafte.nebworks.bottomsheet.PlaceListBottomSheet;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.databinding.ActivityMemberManageBinding;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment1;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment2;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment3;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment4;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MemberManagement extends AppCompatActivity {
    private static final String TAG = "MemberManagement";
    private ActivityMemberManageBinding binding;
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
    String place_id = "";
    String place_owner_id = "";

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

    int total_member_cnt = 0;

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mainfragment);
        binding = ActivityMemberManageBinding.inflate(getLayoutInflater()); // 1
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
            place_id = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub", 0);
            wifi_certi_flag = shardpref.getBoolean("wifi_certi_flag", false);
            gps_certi_flag = shardpref.getBoolean("gps_certi_flag", false);
            return_page = shardpref.getString("return_page", "");
            store_no = shardpref.getString("store_no", "");
            shardpref.putString("returnPage", "BusinessApprovalActivity");

            final List<String> tabElement;
            tabElement = Arrays.asList("전체", "등록", "대기", "휴직");
            ArrayList<Fragment> fragments = new ArrayList<>();
            //점주일때
            fragments.add(MemberSubFragment1.newInstance(0));
            fragments.add(MemberSubFragment2.newInstance(1));
            fragments.add(MemberSubFragment3.newInstance(2));
            fragments.add(MemberSubFragment4.newInstance(3));
            viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);

            binding.viewPager.setAdapter(viewPagerFregmentAdapter);

            //ViewPager2와 TabLayout을 연결
            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                TextView textView = new TextView(MemberManagement.this);
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

            binding.addMemberBtn.setOnClickListener(v -> {
                MemberOption mo = new MemberOption();
                mo.show(getSupportFragmentManager(),"MemberOption");
            });

            binding.changePlace.setOnClickListener(v -> {
                PlaceListBottomSheet plb = new PlaceListBottomSheet();
                plb.show(getSupportFragmentManager(),"PlaceListBottomSheet");
                plb.setOnClickListener01((v1, place_id, place_name, place_owner_id) -> {
                    shardpref.putString("change_place_id",place_id);
                    dlog.i("change_place_id : " + place_id);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(USER_INFO_AUTH.equals("0")){
            pm.Main(mContext);
        }else{
            pm.Main2(mContext);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dlog.i("binding.tabLayout.getSelectedTabPosition() :" + binding.tabLayout.getSelectedTabPosition());
        SetAllMemberList(binding.tabLayout.getSelectedTabPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*직원 전체 리스트 START*/
    public void SetAllMemberList(int i) {
        total_member_cnt = 0;
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id,"");

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            if (Response.length() == 0) {
                                total_member_cnt = 0;
                                binding.tabLayout.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if(!place_owner_id.equals(jsonObject.getString("id"))){
                                        if(i != 0){
                                            if (jsonObject.getString("state").equals(String.valueOf(i))) {
                                                total_member_cnt ++;
                                            }
                                        }else{
                                            total_member_cnt ++;
                                        }
                                    }
                                }
                                dlog.i("total_member_cnt ; " + total_member_cnt);
                                if(total_member_cnt == 0){
                                    binding.tabLayout.setVisibility(View.GONE);
                                }
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
    /*직원 전체 리스트 END*/

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
