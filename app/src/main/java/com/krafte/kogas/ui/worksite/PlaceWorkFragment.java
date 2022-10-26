package com.krafte.kogas.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.kogas.R;
import com.krafte.kogas.adapter.ViewPagerFregmentAdapter;
import com.krafte.kogas.databinding.ActivityPlaceworkBinding;
import com.krafte.kogas.fragment.placework.Page1Fragment;
import com.krafte.kogas.fragment.placework.Page2Fragment;
import com.krafte.kogas.fragment.placework.Page3Fragment;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceWorkFragment extends AppCompatActivity {

    private ActivityPlaceworkBinding binding;
    Context mContext;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    ViewPagerFregmentAdapter viewPagerFregmentAdapter;

    TextView worktogo_tv;
    ImageView worktogo_icon;

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
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    String return_page = "";
    int SELECT_POSITION = 0;
    int rotate_addwork = 0;
    String tap_kind = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityPlaceworkBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        //UI 데이터 세팅
        try {
            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            place_owner_name = shardpref.getString("place_owner_name", "0");
            place_management_office = shardpref.getString("place_management_office", "0");
            place_address = shardpref.getString("place_address", "0");
            place_latitude = shardpref.getString("place_latitude", "0");
            place_longitude = shardpref.getString("place_longitude", "0");
            place_start_time = shardpref.getString("place_start_time", "0");
            place_end_time = shardpref.getString("place_end_time", "0");
            place_img_path = shardpref.getString("place_img_path", "0");
            place_start_date = shardpref.getString("place_start_date", "0");
            place_created_at = shardpref.getString("place_created_at", "0");
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","-1"); //0-관리자 / 1- 근로자
            return_page = shardpref.getString("return_page","");

            worktogo_tv = findViewById(R.id.worktogo_tv);
            worktogo_icon = findViewById(R.id.worktogo_icon);

            worktogo_icon.setBackgroundResource(R.drawable.worktogo_on_resize);
            worktogo_tv.setTextColor(Color.parseColor("#6395EC"));
            setBtnEvent();

            final List<String> tabElement;

            if(USER_INFO_AUTH.equals("0")){
                tabElement = Arrays.asList("공지사항", "작업배정", "즐겨찾기");

                ArrayList<Fragment> fragments = new ArrayList<>();
                fragments.add(Page1Fragment.newInstance(0));
                fragments.add(Page2Fragment.newInstance(1));
                fragments.add(Page3Fragment.newInstance(2));

                viewPagerFregmentAdapter =new ViewPagerFregmentAdapter(this, fragments);
                binding.viewPager.setAdapter(viewPagerFregmentAdapter);

                //ViewPager2와 TabLayout을 연결
                new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                    TextView textView = new TextView(this);
                    textView.setText(tabElement.get(position));
                    textView.setTextColor(Color.parseColor("#696969"));
                    textView.setGravity(Gravity.CENTER);
                    tab.setCustomView(textView);
                }).attach();
            }else if(USER_INFO_AUTH.equals("1")){
                tabElement = Arrays.asList("공지사항", "작업배정");

                ArrayList<Fragment> fragments = new ArrayList<>();
                fragments.add(Page1Fragment.newInstance(0));
                fragments.add(Page2Fragment.newInstance(1));

                viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);
                binding.viewPager.setAdapter(viewPagerFregmentAdapter);

                //ViewPager2와 TabLayout을 연결
                new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                    TextView textView = new TextView(this);
                    textView.setText(tabElement.get(position));
                    textView.setTextColor(Color.parseColor("#696969"));
                    textView.setGravity(Gravity.CENTER);
                    tab.setCustomView(textView);
                }).attach();
//                binding.viewPager.setVerticalScrollbarPosition(SELECT_POSITION);
            }
            dlog.i("PlaceWorkActivity SELECT_POSITION : " + SELECT_POSITION);
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            binding.tabLayout.getTabAt(SELECT_POSITION ).select();
                        }
                    }, 100);

            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    if (rotate_addwork == 1) {
                        Animation fade_out = AnimationUtils.loadAnimation(
                                getApplicationContext(),
                                R.anim.push_right_out);
                        binding.makeWorkMenu.startAnimation(fade_out);
                        rotate_addwork = 0;
                        binding.makeWorkMenu.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tap_kind = String.valueOf(position);
                    dlog.i("tap_kind : " + position);
                    //Adds the left container's fragment
                    if (rotate_addwork == 1) {
                        Animation fade_out = AnimationUtils.loadAnimation(
                                getApplicationContext(),
                                R.anim.push_right_out);
                        binding.makeWorkMenu.startAnimation(fade_out);
                        rotate_addwork = 0;
                        binding.makeWorkMenu.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });


        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
    }

    public void btnOnclick(View view) {
        if (view.getId() == R.id.out_store) {
            pm.PlaceListBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            pm.MainBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation02) {
            dlog.i("Work List Page");
        } else if (view.getId() == R.id.bottom_navigation03) {
            pm.CalenderGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation04) {
            pm.WorkStateListGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation05) {
            pm.MoreGo(mContext);
        }
    }

    public void setBtnEvent() {
        binding.outStore.setOnClickListener(v -> {
            pm.PlaceListBack(mContext);
        });

        binding.addWorkBtn.setOnClickListener(v -> {
            //버튼 회전 애니메이션
            Animation anim = AnimationUtils.loadAnimation(
                    getApplicationContext(),
                    R.anim.pen_scale_anim);
            binding.wirteAddImg.startAnimation(anim);
//            make_icon01Icon.startAnimation(anim);
//            make_icon02Icon.startAnimation(anim);
            if (rotate_addwork == 1) {
                binding.wirteAddImg.setBackgroundResource(R.drawable.plus);
                Animation fade_out = AnimationUtils.loadAnimation(
                        getApplicationContext(),
                        R.anim.push_right_out);
                binding.makeWorkMenu.startAnimation(fade_out);
                rotate_addwork = 0;
                binding.makeWorkMenu.setVisibility(View.GONE);
                binding.menuVisible.setVisibility(View.GONE);
            } else {
                binding.wirteAddImg.setBackgroundResource(R.drawable.drawing_pen);
                Animation fade_in = AnimationUtils.loadAnimation(
                        getApplicationContext(),
                        R.anim.push_left_in);
                binding.makeWorkMenu.startAnimation(fade_in);
                rotate_addwork = 1;
                binding.makeWorkMenu.setVisibility(View.VISIBLE);
                binding.menuVisible.setVisibility(View.VISIBLE);
            }
        });
        binding.makeWork01.setOnClickListener(v -> {
            //즐겨찾기 , 반복업무
            shardpref.putInt("make_kind", 2);
            shardpref.putInt("SELECT_POSITION", 2);
            shardpref.putString("return_page", "PlaceWorkFragment");
            Animation fade_out = AnimationUtils.loadAnimation(
                    getApplicationContext(),
                    R.anim.push_right_out);

            binding.makeWorkMenu.startAnimation(fade_out);
            rotate_addwork = 0;
            binding.makeWorkMenu.setVisibility(View.GONE);
            binding.menuVisible.setVisibility(View.GONE);
            pm.addWorkGo(mContext);
        });

        binding.makeWork02.setOnClickListener(v -> {
            // 배정업무 등록
            shardpref.putInt("make_kind", 1);
            shardpref.putInt("assignment_kind", 2);
            shardpref.putInt("SELECT_POSITION", 1);
            shardpref.putString("return_page", "PlaceWorkFragment");
            Animation fade_out = AnimationUtils.loadAnimation(
                    getApplicationContext(),
                    R.anim.push_right_out);
            binding.makeWorkMenu.startAnimation(fade_out);
            rotate_addwork = 0;
            binding.makeWorkMenu.setVisibility(View.GONE);
            binding.menuVisible.setVisibility(View.GONE);
            pm.addWorkGo(mContext);
        });

        binding.makeWork03.setOnClickListener(v -> {
            shardpref.putInt("make_kind", 1);
            shardpref.putInt("assignment_kind", 2);
            shardpref.putInt("selectposition", 0);
            Animation fade_out = AnimationUtils.loadAnimation(
                    getApplicationContext(),
                    R.anim.push_right_out);
            binding.makeWorkMenu.startAnimation(fade_out);
            rotate_addwork = 0;
            binding.makeWorkMenu.setVisibility(View.GONE);
            binding.menuVisible.setVisibility(View.GONE);
            pm.addNotiGo(mContext);
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(return_page.equals("WorkState2Activity")){
            pm.WorkStateDetailGo(mContext);
        }else{
            pm.MainBack(mContext);
        }
    }
}
