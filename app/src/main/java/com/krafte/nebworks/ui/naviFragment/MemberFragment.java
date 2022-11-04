package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.MemberfragmentBinding;
import com.krafte.nebworks.pop.MemberOption;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment1;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment2;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment3;
import com.krafte.nebworks.ui.fragment.member.MemberSubFragment4;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

public class MemberFragment extends Fragment {
    private final static String TAG = "MoreFragment";
    private MemberfragmentBinding binding;
    Context mContext;
    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    ImageView more_icon;
    TextView more_tv;

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

    int SELECT_POSITION = 0;
    int SELECT_POSITION_sub = 0;

    Fragment fg;

    public static MemberFragment newInstance(int number) {
        MemberFragment fragment = new MemberFragment();
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
        binding = MemberfragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();

        //UI 데이터 세팅
        try {
            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
//            place_owner_id = shardpref.getString("place_owner_id", "0");
//            place_owner_name = shardpref.getString("place_owner_name", "0");
//            place_management_office = shardpref.getString("place_management_office", "0");
//            place_address = shardpref.getString("place_address", "0");
//            place_latitude = shardpref.getString("place_latitude", "0");
//            place_longitude = shardpref.getString("place_longitude", "0");
//            place_start_time = shardpref.getString("place_start_time", "0");
//            place_end_time = shardpref.getString("place_end_time", "0");
//            place_img_path = shardpref.getString("place_img_path", "0");
//            place_start_date = shardpref.getString("place_start_date", "0");
//            place_created_at = shardpref.getString("place_created_at", "0");
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub",0);

            if(SELECT_POSITION_sub == 0){
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = MemberSubFragment1.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 1){
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = MemberSubFragment2.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 2){
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = MemberSubFragment3.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 3){
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
                fg = MemberSubFragment4.newInstance();
                setChildFragment(fg);
            }

            binding.fragmentbtn1.setOnClickListener(v -> {
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = MemberSubFragment1.newInstance();
                setChildFragment(fg);
            });
            binding.fragmentbtn2.setOnClickListener(v -> {
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = MemberSubFragment2.newInstance();
                setChildFragment(fg);
            });
            binding.fragmentbtn3.setOnClickListener(v -> {
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = MemberSubFragment3.newInstance();
                setChildFragment(fg);
            });
            binding.fragmentbtn4.setOnClickListener(v -> {
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
                fg = MemberSubFragment4.newInstance();
                setChildFragment(fg);
            });

            binding.addMember.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MemberOption.class);
                intent.putExtra("data", "직원등록");
                intent.putExtra("btn01", "직접등록");
                intent.putExtra("btn02", "초대메세지 발송");
                startActivity(intent);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setBtnEvent() {

    }
    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            childFt.replace(R.id.child_fragment_container, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }
}
