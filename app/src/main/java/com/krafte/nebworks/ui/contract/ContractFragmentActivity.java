package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.databinding.ActivityContractFragmentBinding;
import com.krafte.nebworks.ui.fragment.contract.ContractFragment1;
import com.krafte.nebworks.ui.fragment.contract.ContractFragment2;
import com.krafte.nebworks.ui.fragment.contract.ContractFragment3;
import com.krafte.nebworks.ui.fragment.contract.ContractFragment4;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContractFragmentActivity extends AppCompatActivity {
    private static final String TAG = "MemberManagement";
    private ActivityContractFragmentBinding binding;
    Context mContext;

    //Other
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();

    int paging_position = 0;

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContractFragmentBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        try {
            final List<String> tabElement = Arrays.asList("전체", "서명대기중", "완료", "미작성");
            //before 전체피드 / 랭킹 / 매장 / 구인구직
            //after 매장 / 전체피드 / 구인구직 / 랭킹
            ArrayList<Fragment> fragments = new ArrayList<>();
            fragments.add(ContractFragment1.newInstance(0));
            fragments.add(ContractFragment2.newInstance(1));
            fragments.add(ContractFragment3.newInstance(2));
            fragments.add(ContractFragment4.newInstance(3));

            viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);
            binding.viewPager.setAdapter(viewPagerFregmentAdapter);
            binding.viewPager.setUserInputEnabled(false);

            //ViewPager2와 TabLayout을 연결
            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                TextView textView = new TextView(ContractFragmentActivity.this);
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) binding.getRoot().findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
