package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.CommunityAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.ActivityAuthselectBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.ui.naviFragment.CommunityFragment;
import com.krafte.nebworks.ui.naviFragment.HomeFragment2;
import com.krafte.nebworks.ui.naviFragment.MoreFragment;
import com.krafte.nebworks.ui.naviFragment.WorkgotoFragment;
import com.krafte.nebworks.ui.naviFragment.WorkstatusFragment;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AuthSelectActivity extends AppCompatActivity {
    private ActivityAuthselectBinding binding;
    private final static String TAG = "AuthSelectActivity";
    Context mContext;

    PreferenceHelper shardpref;

    int listitemsize = 0;
    int SELECT_POSITION = 0;
    int paging_position = 0;

    Dlog dlog = new Dlog();
    CommunityAdapter mAdapter = null;
    RetrofitConnect rc = new RetrofitConnect();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;

    ArrayList<PlaceNotiData.PlaceNotiData_list> mList = new ArrayList<>();

    //Shared
    String USER_INFO_NAME = "";
    String USER_INFO_PHONE = "";
    String USER_INFO_ID = "";
    String USER_LOGIN_METHOD = "";

    //BottomNavigation
    ImageView bottom_icon01, bottom_icon02, bottom_icon03, bottom_icon04, bottom_icon05;
    TextView bottom_icon01tv, bottom_icon02tv, bottom_icon03tv, bottom_icon04tv, bottom_icon05tv;

    //other
    boolean check = false;
    PageMoveClass pm = new PageMoveClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_delete);
        binding = ActivityAuthselectBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        setBtnEvent();

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

        final List<String> tabElement;
        tabElement = Arrays.asList("홈", "할일", "근무현황", "커뮤니티", "더보기");
        ArrayList<Fragment> fragments = new ArrayList<>();
        bottom_icon01tv.setText(tabElement.get(0));
        bottom_icon02tv.setText(tabElement.get(1));
        bottom_icon03tv.setText(tabElement.get(2));
        bottom_icon04tv.setText(tabElement.get(3));
        bottom_icon05tv.setText(tabElement.get(4));

        fragments.add(HomeFragment2.newInstance(0));
        fragments.add(WorkgotoFragment.newInstance(1));
        fragments.add(WorkstatusFragment.newInstance(2));
        fragments.add(CommunityFragment.newInstance(3));
        fragments.add(MoreFragment.newInstance(4));
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
                }, 500);

        if (SELECT_POSITION != -99) {
            binding.viewPager.setCurrentItem(SELECT_POSITION);
            binding.tabLayout.getTabAt(SELECT_POSITION).select();
        }

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
        Log.i(TAG, "USER_INFO_NAME = " + USER_INFO_NAME);
        Log.i(TAG, "USER_INFO_PHONE = " + USER_INFO_PHONE);
        Log.i(TAG, "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
//            USER_INFO_ID = UserCheckData.getInstance().getUser_id();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        setRecyclerView();
    }


    private void setBtnEvent() {
        binding.goOwner.setOnClickListener(v -> {
            shardpref.putString("USER_INFO_AUTH", "0");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.PlaceList(mContext);
        });
        binding.ownerButton.setOnClickListener(v -> {
            shardpref.putString("USER_INFO_AUTH", "0");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.PlaceList(mContext);
        });
        binding.goWorker.setOnClickListener(v -> {
            shardpref.putString("USER_INFO_AUTH", "1");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.PlaceList(mContext);
        });
        binding.workerButton.setOnClickListener(v -> {
            shardpref.putString("USER_INFO_AUTH", "1");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            pm.PlaceList(mContext);
        });
    }

    private void allClear() {
        mList.clear();
    }

    public void setRecyclerView() {
        allClear();
        mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData("", "", "3", "2",USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "position 0 WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "position 0 response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        mList = new ArrayList<>();
                        mAdapter = new CommunityAdapter(mContext, mList, 0);
                        binding.communityRecyclerView.setAdapter(mAdapter);
                        binding.communityRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();

                        if (Response.length() == 0) {
                            binding.noData.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        } else {
                            binding.noData.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(Integer.parseInt(jsonObject.getString("view_cnt")) > 50
                                        && jsonObject.getString("boardkind").equals("자유게시판")) {
                                    mAdapter.isMain(true);
                                    mAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("writer_name"),
                                            jsonObject.getString("writer_img_path"),
                                            jsonObject.getString("jikgup"),
                                            jsonObject.getString("view_cnt"),
                                            jsonObject.getString("comment_cnt"),
                                            jsonObject.getString("like_cnt"),
                                            jsonObject.getString("link"),
                                            jsonObject.getString("feed_img_path"),
                                            jsonObject.getString("created_at"),
                                            jsonObject.getString("updated_at"),
                                            jsonObject.getString("open_date"),
                                            jsonObject.getString("close_date"),
                                            jsonObject.getString("boardkind"),
                                            jsonObject.getString("category"),
                                            jsonObject.getString("mylikeyn")
                                    ));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    shardpref.putString("feed_id",mList.get(position).getId());
                                    shardpref.putString("title",mList.get(position).getTitle());
                                    shardpref.putString("contents",mList.get(position).getContents());
                                    shardpref.putString("writer_id",mList.get(position).getWriter_id());
                                    shardpref.putString("writer_name",mList.get(position).getWriter_name());
                                    shardpref.putString("writer_img_path",mList.get(position).getWriter_img_path());
                                    shardpref.putString("feed_img_path",mList.get(position).getFeed_img_path());
                                    shardpref.putString("jikgup",mList.get(position).getJikgup());
                                    shardpref.putString("view_cnt",mList.get(position).getView_cnt());
                                    shardpref.putString("comment_cnt",mList.get(position).getComment_cnt());
                                    shardpref.putString("like_cnt",mList.get(position).getLike_cnt());
                                    shardpref.putString("category",mList.get(position).getCategory());
                                    shardpref.putString("updated_at",mList.get(position).getUpdated_at());
                                    shardpref.putString("mylikeyn",mList.get(position).getMylikeyn());
                                    pm.CommunityDetail(mContext);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }


    String AuthState = "";
    @Override
    protected void onResume() {
        super.onResume();
        AuthState = shardpref.getString("AuthState","");
        shardpref.remove("USER_INFO_AUTH");
        if(AuthState.equals("더미")){
            if(binding.viewPager.getVisibility() == View.VISIBLE){
                binding.authScrollView.setVisibility(View.VISIBLE);
                binding.viewPager.setVisibility(View.GONE);
                binding.tabLayout.setVisibility(View.GONE);
            }else{
                binding.authScrollView.setVisibility(View.VISIBLE);
                binding.viewPager.setVisibility(View.GONE);
                binding.tabLayout.setVisibility(View.GONE);
            }
            shardpref.remove("AuthState");
        }
    }

    public void btnOnclick(View view) {
       if (view.getId() == R.id.bottom_navigation01) {
            binding.authScrollView.setVisibility(View.GONE);
            binding.viewPager.setVisibility(View.VISIBLE);
            binding.tabLayout.setVisibility(View.VISIBLE);
            dlog.i("메인 Click!");
            binding.tabLayout.getTabAt(0).select();
            shardpref.putInt("SELECT_POSITION", 0);
        } else if (view.getId() == R.id.bottom_navigation02) {
           binding.authScrollView.setVisibility(View.GONE);
           binding.viewPager.setVisibility(View.VISIBLE);
           binding.tabLayout.setVisibility(View.VISIBLE);
           dlog.i("할일 Click!");
            binding.tabLayout.getTabAt(1).select();
            shardpref.putInt("SELECT_POSITION", 1);
        } else if (view.getId() == R.id.bottom_navigation03) {
           binding.authScrollView.setVisibility(View.GONE);
           binding.viewPager.setVisibility(View.VISIBLE);
           binding.tabLayout.setVisibility(View.VISIBLE);
           dlog.i("근무현황 Click!");
            binding.tabLayout.getTabAt(2).select();
            shardpref.putInt("SELECT_POSITION", 2);
        } else if (view.getId() == R.id.bottom_navigation04) {
           binding.authScrollView.setVisibility(View.GONE);
           binding.viewPager.setVisibility(View.VISIBLE);
           binding.tabLayout.setVisibility(View.VISIBLE);
           dlog.i("커뮤니티 Click!");
            binding.tabLayout.getTabAt(3).select();
            shardpref.putInt("SELECT_POSITION", 3);
        } else if (view.getId() == R.id.bottom_navigation05) {
           binding.authScrollView.setVisibility(View.GONE);
           binding.viewPager.setVisibility(View.VISIBLE);
           binding.tabLayout.setVisibility(View.VISIBLE);
           dlog.i("더보기 Click!");
            binding.tabLayout.getTabAt(4).select();
            shardpref.putInt("SELECT_POSITION", 4);
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("data", "로그아웃하시겠습니까?");
        intent.putExtra("flag", "로그아웃");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "로그아웃");
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
