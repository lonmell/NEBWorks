package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.CommunityfragmentBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.ui.fragment.community.community_fragment1;
import com.krafte.nebworks.ui.fragment.community.community_fragment2;
import com.krafte.nebworks.ui.fragment.community.community_fragment3;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

public class CommunityFragment extends Fragment {
    private final static String TAG = "MoreFragment";
    private CommunityfragmentBinding binding;
    Context mContext;
    Activity activity;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String returnPage = "";
    String place_id = "";
    String place_owner_id = "";

    //Other
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    int paging_position = 0;
    int listitemsize = 0;
    Fragment fg;

    public static CommunityFragment newInstance(int number) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
            Log.i(TAG, "num : " + num);
        }
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.morefragment, container, false);
        binding = CommunityfragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();

        //UI 데이터 세팅
        try {
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            returnPage = shardpref.getString("returnPage", "");
            place_id = shardpref.getString("place_id", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            Log.i(TAG, "USER_INFO_AUTH : " + USER_INFO_AUTH);
            ChangePage(0);
            setAddBtnSetting();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
//        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedRemove();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setBtnEvent() {
        binding.selectFragmentbtn1.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                ChangePage(0);
            }
        });
        binding.selectFragmentbtn2.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                ChangePage(1);
            }
        });
        binding.selectFragmentbtn3.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                ChangePage(2);
            }
        });
    }

    private void ChangePage(int i) {
        binding.selectFragmenttv1.setTextColor(Color.parseColor("#696969"));
        binding.selectFragmentline1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.selectFragmenttv2.setTextColor(Color.parseColor("#696969"));
        binding.selectFragmentline2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.selectFragmenttv3.setTextColor(Color.parseColor("#696969"));
        binding.selectFragmentline3.setBackgroundColor(Color.parseColor("#FFFFFF"));

        binding.addBtn.addWorktimeBtn.setVisibility(View.GONE);

        paging_position = i;
        if (i == 0) {
            binding.selectFragmenttv1.setTextColor(Color.parseColor("#6395EC"));
            binding.selectFragmentline1.setBackgroundColor(Color.parseColor("#6395EC"));
            binding.addBtn.addWorktimeBtn.setVisibility(View.VISIBLE);
            fg = community_fragment1.newInstance();
            setChildFragment(fg);
        } else if (i == 1) {
            binding.selectFragmenttv2.setTextColor(Color.parseColor("#6395EC"));
            binding.selectFragmentline2.setBackgroundColor(Color.parseColor("#6395EC"));
            fg = community_fragment2.newInstance();
            setChildFragment(fg);
        } else if (i == 2) {
            binding.selectFragmenttv3.setTextColor(Color.parseColor("#6395EC"));
            binding.selectFragmentline3.setBackgroundColor(Color.parseColor("#6395EC"));
            fg = community_fragment3.newInstance();
            setChildFragment(fg);
        }
    }


    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.status_child_fragment_container, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("게시글 작성");
        add_worktime_btn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (paging_position == 0) {
                    shardpref.putString("state","AddCommunity");
                    pm.CommunityAdd(mContext);
                } else if (paging_position == 1) {
                    Toast_Nomal("사장님 게시글");
                } else if (paging_position == 2) {
                    Toast_Nomal("세금/노무");
                }
            }
        });
    }


    private void sharedRemove(){
        shardpref.remove("writer_name");
        shardpref.remove("write_nickname");
        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("write_date");
        shardpref.remove("view_cnt");
        shardpref.remove("like_cnt");
        shardpref.remove("categoryItem");
        shardpref.remove("TopFeed");

        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("writer_id");
        shardpref.remove("writer_img_path");
        shardpref.remove("feed_img_path");
        shardpref.remove("jikgup");
        shardpref.remove("view_cnt");
        shardpref.remove("comment_cnt");
        shardpref.remove("category");
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

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

}
