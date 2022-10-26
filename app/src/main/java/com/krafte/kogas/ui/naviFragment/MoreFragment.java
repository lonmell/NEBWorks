package com.krafte.kogas.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import com.krafte.kogas.databinding.MorefragmentBinding;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

public class MoreFragment extends Fragment {
    private final static String TAG = "MoreFragment";
    private MorefragmentBinding binding;
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

    public static MoreFragment newInstance(int number) {
        MoreFragment fragment = new MoreFragment();
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
        binding = MorefragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();

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
        binding.settingList02Txt.setOnClickListener(v -> {
            shardpref.putString("retrun_page","MoreActivity");
            pm.ProfileEditGo(mContext);
        });
        binding.settingList03Txt.setOnClickListener(v -> {
            pm.Push(mContext);
        });

        binding.settingList05Txt.setOnClickListener(v -> {
            pm.MyPlsceGo(mContext);
        });

        binding.settingList06Txt.setOnClickListener(v -> {
            pm.UserDel(mContext);
        });
    }
}
