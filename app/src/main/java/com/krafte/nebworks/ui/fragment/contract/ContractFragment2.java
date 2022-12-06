package com.krafte.nebworks.ui.fragment.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krafte.nebworks.R;
import com.krafte.nebworks.databinding.ContractFragmentBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class ContractFragment2 extends Fragment {
    private ContractFragmentBinding binding;
    private static final String TAG = "ContractFragment1";
    DBConnection dbConnection = new DBConnection();
    Context mContext;
    Activity activity;

    //shared Data
    PreferenceHelper shardpref;
    public String USER_INFO_ID = "";

    //Other
    Drawable icon_on;
    Drawable icon_off;
    PageMoveClass pm = new PageMoveClass();

    public static ContractFragment2 newInstance(int number) {
        ContractFragment2 fragment = new ContractFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int num = getArguments().getInt("number");
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ContractFragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        icon_off = mContext.getResources().getDrawable(R.drawable.resize_service_off);
        icon_on = mContext.getResources().getDrawable(R.drawable.resize_login_002);

        //Shared
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

        setBtnEvent();
        return binding.getRoot();
    }

    private void setBtnEvent(){

    }

    @Override
    public void onResume(){
        super.onResume();
    }

}
