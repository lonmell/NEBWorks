package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.Tap2ListAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.TodolistData;
import com.krafte.nebworks.databinding.WorkgotofragmentBinding;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class WorkgotoFragment extends Fragment {
    private final static String TAG = "WorkgotoFragment";
    private WorkgotofragmentBinding binding;
    Context mContext;

    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;

    ArrayList<TodolistData.TodolistData_list> mList;
    Tap2ListAdapter mAdapter = null;

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
    int SELECT_POSITION_sub = 0;
    int rotate_addwork = 0;
    String tap_kind = "";
    Fragment fg;

    public static WorkgotoFragment newInstance(int number) {
        WorkgotoFragment fragment = new WorkgotoFragment();
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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.workgotofragment, container, false);
        binding = WorkgotofragmentBinding.inflate(inflater);
        mContext = inflater.getContext();
        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

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
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub",0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","-1"); //0-관리자 / 1- 근로자
            return_page = shardpref.getString("return_page","");
            setBtnEvent();


            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            dlog.i("PlaceWorkActivity SELECT_POSITION_sub : " + SELECT_POSITION_sub);
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setBtnEvent() {
        binding.addWorkBtn.setOnClickListener(v -> {
            //버튼 회전 애니메이션
            Animation anim = AnimationUtils.loadAnimation(
                    mContext,
                    R.anim.pen_scale_anim);
            binding.wirteAddImg.startAnimation(anim);
//            make_icon01Icon.startAnimation(anim);
//            make_icon02Icon.startAnimation(anim);
            if (rotate_addwork == 1) {
                binding.wirteAddImg.setBackgroundResource(R.drawable.plus);
                Animation fade_out = AnimationUtils.loadAnimation(
                        mContext,
                        R.anim.push_right_out);
                binding.makeWorkMenu.startAnimation(fade_out);
                rotate_addwork = 0;
                binding.makeWorkMenu.setVisibility(View.GONE);
                binding.menuVisible.setVisibility(View.GONE);
            } else {
                binding.wirteAddImg.setBackgroundResource(R.drawable.drawing_pen);
                Animation fade_in = AnimationUtils.loadAnimation(
                        mContext,
                        R.anim.push_left_in);
                binding.makeWorkMenu.startAnimation(fade_in);
                rotate_addwork = 1;
                binding.makeWorkMenu.setVisibility(View.VISIBLE);
                binding.menuVisible.setVisibility(View.VISIBLE);
            }
        });

        if(USER_INFO_AUTH.equals("0")){
            binding.makeWork01.setVisibility(View.VISIBLE);
        }else{
            binding.makeWork01.setVisibility(View.GONE);
        }
        binding.makeWork01.setOnClickListener(v -> {
            //즐겨찾기 , 반복업무
            shardpref.putInt("make_kind", 2);
            shardpref.putInt("SELECT_POSITION", 2);
            shardpref.putString("return_page", "PlaceWorkFragment");
            Animation fade_out = AnimationUtils.loadAnimation(
                    mContext,
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
                    mContext,
                    R.anim.push_right_out);
            binding.makeWorkMenu.startAnimation(fade_out);
            rotate_addwork = 0;
            binding.makeWorkMenu.setVisibility(View.GONE);
            binding.menuVisible.setVisibility(View.GONE);
            pm.addWorkGo(mContext);
        });

        binding.makeWork03.setOnClickListener(v -> {
            //공지등록
            shardpref.putInt("make_kind", 1);
            shardpref.putInt("assignment_kind", 2);
            shardpref.putInt("SELECT_POSITION", 0);
            Animation fade_out = AnimationUtils.loadAnimation(
                    mContext,
                    R.anim.push_right_out);
            binding.makeWorkMenu.startAnimation(fade_out);
            rotate_addwork = 0;
            binding.makeWorkMenu.setVisibility(View.GONE);
            binding.menuVisible.setVisibility(View.GONE);
            pm.addNotiGo(mContext);
        });
    }

}
