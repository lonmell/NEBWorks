package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter2;
import com.krafte.nebworks.databinding.WorkstatusfragmentBinding;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment1;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment2;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment3;
import com.krafte.nebworks.ui.fragment.workstatus.WorkStatusSubFragment4;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

public class WorkstatusFragment extends Fragment {
    private final static String TAG = "MoreFragment";
    private WorkstatusfragmentBinding binding;
    Context mContext;
    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    ViewPagerFregmentAdapter2 viewPagerFregmentAdapter;
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

    public static WorkstatusFragment newInstance(int number) {
        WorkstatusFragment fragment = new WorkstatusFragment();
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
        binding = WorkstatusfragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();

        //UI 데이터 세팅
        try {

            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            SELECT_POSITION_sub = shardpref.getInt("SELECT_POSITION_sub",0);

            if(SELECT_POSITION_sub == 0){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment1.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 1){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment2.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 2){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment3.newInstance();
                setChildFragment(fg);
            }else if(SELECT_POSITION_sub == 3){
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
                fg = WorkStatusSubFragment4.newInstance();
                setChildFragment(fg);
            }

            binding.statusFragmentbtn1.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment1.newInstance();
                setChildFragment(fg);
            });
            binding.statusFragmentbtn2.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment2.newInstance();
                setChildFragment(fg);
            });
            binding.statusFragmentbtn3.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#8EB3FC"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#ffffff"));
                fg = WorkStatusSubFragment3.newInstance();
                setChildFragment(fg);
            });
            binding.statusFragmentbtn4.setOnClickListener(v -> {
                binding.statusFragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.statusFragmentline4.setBackgroundColor(Color.parseColor("#8EB3FC"));
                fg = WorkStatusSubFragment4.newInstance();
                setChildFragment(fg);
            });

//            binding.addMember.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, MemberOption.class);
//                intent.putExtra("data", "직원등록");
//                intent.putExtra("btn01", "직접등록");
//                intent.putExtra("btn02", "초대메세지 발송");
//                startActivity(intent);
//                mContext.startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            });
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
            childFt.replace(R.id.status_child_fragment_container, child);
            childFt.addToBackStack(null);
            childFt.commit();
        }
    }
}
