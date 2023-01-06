package com.krafte.nebworks.ui.fragment.career;

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
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.databinding.CareerFragment1Binding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class CareerFragment1  extends Fragment {
    private CareerFragment1Binding binding;
    private static final String TAG = "CareerFragment1";
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

    public static CareerFragment1 newInstance(int number) {
        CareerFragment1 fragment = new CareerFragment1();
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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.career_fragment1 , container, false);
        binding = CareerFragment1Binding.inflate(inflater);
        mContext = inflater.getContext();

        icon_off = mContext.getResources().getDrawable(R.drawable.resize_service_off);
        icon_on = mContext.getResources().getDrawable(R.drawable.resize_login_002);

        //Shared
        shardpref = new PreferenceHelper(mContext);
//        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_ID = UserCheckData.getInstance().getUser_id();

        setBtnEvent();
        return binding.getRoot();
    }

    private void setBtnEvent(){

    }

    @Override
    public void onResume(){
        super.onResume();
        getIntroduce("2");
    }


    private void getIntroduce(String flag) {
//        @SuppressLint({"NotifyDataSetChanged", "LongLogTag", "SetTextI18n"}) Thread th = new Thread(() -> {
//            dbConnection.introduceData_lists.clear();
//            dbConnection.IntroduceManagement(flag, "", USER_INFO_ID, ""
//                    , "", "", "","", "", "", ""
//                    , "", "", ""
//                    , 0, 0, 0,0, 0, 0, 0);
//            activity.runOnUiThread(() -> {
//                if (dbConnection.introduceData_lists.size() == 0) {
//                    Log.i(TAG, "GET SIZE : " + dbConnection.introduceData_lists.size());
//                } else {
//                    if(dbConnection.introduceData_lists.get(0).getGujik_yn().equals("0")){
//                        gujik_on_bt.setChecked(false);
//                        gujik_txt.setText("구직중 아님");
//                    }else{
//                        gujik_on_bt.setChecked(true);
//                        gujik_txt.setText("구직 중");
//                    }
//                    name_open_yn.setVisibility(View.GONE);
//                    birth_open_yn.setVisibility(View.GONE);
//                    phone_open_yn.setVisibility(View.GONE);
//                    mail_open_yn.setVisibility(View.GONE);
//                    address_open_yn.setVisibility(View.GONE);
//                    hope_open_yn.setVisibility(View.GONE);
//
////                dbConnection.introduceData_lists.get(0)
//                    if (dbConnection.introduceData_lists.get(0).getName_yn().equals("0")) {
//                        name_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_off, null);
//                    } else {
//                        name_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_on, null);
//                    }
//                    if (dbConnection.introduceData_lists.get(0).getBrith_yn().equals("0")) {
//                        birth_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_off, null);
//                    } else {
//                        birth_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_on, null);
//                    }
//                    if (dbConnection.introduceData_lists.get(0).getPhone_yn().equals("0")) {
//                        phone_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_off, null);
//                    } else {
//                        phone_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_on, null);
//                    }
//                    if (dbConnection.introduceData_lists.get(0).getMail_yn().equals("0")) {
//                        mail_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_off, null);
//                    } else {
//                        mail_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_on, null);
//                    }
//                    if (dbConnection.introduceData_lists.get(0).getAddress_yn().equals("0")) {
//                        address_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_off, null);
//                    } else {
//                        address_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_on, null);
//                    }
//                    if (dbConnection.introduceData_lists.get(0).getHope_yn().equals("0")) {
//                        hope_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_off, null);
//                    } else {
//                        hope_open_yn.setCompoundDrawablesWithIntrinsicBounds(null, null, icon_on, null);
//                    }
//                    Log.i(TAG, "no2 : " + dbConnection.introduceData_lists.get(0).getNo());
//                    Log.i(TAG, "getKor_name : " + dbConnection.introduceData_lists.get(0).getKor_name());
//                    Log.i(TAG, "getEng_name : " + dbConnection.introduceData_lists.get(0).getEng_name());
//
//
//                    if(dbConnection.introduceData_lists.get(0).getWrite_id().equals(USER_INFO_ID)){
//                        kor_name.setText(dbConnection.introduceData_lists.get(0).getName_yn().equals("0")?dbConnection.introduceData_lists.get(0).getKor_name() + " (비공개)" : "(한글) "+dbConnection.introduceData_lists.get(0).getKor_name());
//                        eng_name.setText(dbConnection.introduceData_lists.get(0).getName_yn().equals("0")?dbConnection.introduceData_lists.get(0).getEng_name() + " (비공개)" : "(영문) "+dbConnection.introduceData_lists.get(0).getEng_name());
//                        birth.setText(dbConnection.introduceData_lists.get(0).getBirth().equals("0")?dbConnection.introduceData_lists.get(0).getBirth() + " (비공개)" : "(영문) "+dbConnection.introduceData_lists.get(0).getBirth());
//                        user_phone.setText(dbConnection.introduceData_lists.get(0).getPhone_yn().equals("0")?dbConnection.introduceData_lists.get(0).getCall_phone() + " (비공개)" : dbConnection.introduceData_lists.get(0).getCall_phone());
//                        user_mail.setText(dbConnection.introduceData_lists.get(0).getMail_yn().equals("0")?dbConnection.introduceData_lists.get(0).getEmail() + " (비공개)" : dbConnection.introduceData_lists.get(0).getEmail());
//                        user_address.setText(dbConnection.introduceData_lists.get(0).getAddress_yn().equals("0")?dbConnection.introduceData_lists.get(0).getAddress() + " (비공개)" : dbConnection.introduceData_lists.get(0).getAddress());
//
//                        if(dbConnection.introduceData_lists.get(0).getHope_yn().equals("0")){
//                            input_hope01.setText(dbConnection.introduceData_lists.get(0).getHope_money() + " (면접 후 협의)");
//                            input_hope02.setText(dbConnection.introduceData_lists.get(0).getHope_workdate() + " (면접 후 협의)");
//                            input_hope03.setText(dbConnection.introduceData_lists.get(0).getHope_work_yoil() + " (면접 후 협의)");
//                            input_hope04.setText(dbConnection.introduceData_lists.get(0).getHope_work_time() + " (면접 후 협의)");
//                        }else{
//                            input_hope01.setText(dbConnection.introduceData_lists.get(0).getHope_money());
//                            input_hope02.setText(dbConnection.introduceData_lists.get(0).getHope_workdate());
//                            input_hope03.setText(dbConnection.introduceData_lists.get(0).getHope_work_yoil());
//                            input_hope04.setText(dbConnection.introduceData_lists.get(0).getHope_work_time());
//                        }
//                    }else{
//                        kor_name.setText(dbConnection.introduceData_lists.get(0).getName_yn().equals("0")?"(비공개)" : "(한글) "+dbConnection.introduceData_lists.get(0).getKor_name());
//                        eng_name.setText(dbConnection.introduceData_lists.get(0).getName_yn().equals("0")?"(비공개)" : "(영문) "+dbConnection.introduceData_lists.get(0).getEng_name());
//                        user_phone.setText(dbConnection.introduceData_lists.get(0).getPhone_yn().equals("0")?"(비공개)" : dbConnection.introduceData_lists.get(0).getCall_phone());
//                        user_mail.setText(dbConnection.introduceData_lists.get(0).getMail_yn().equals("0")?"(비공개)" : dbConnection.introduceData_lists.get(0).getEmail());
//                        user_address.setText(dbConnection.introduceData_lists.get(0).getAddress_yn().equals("0")?"(비공개)" : dbConnection.introduceData_lists.get(0).getAddress());
//
//                        if(dbConnection.introduceData_lists.get(0).getHope_yn().equals("0")){
//                            input_hope01.setText("면접 후 협의");
//                            input_hope02.setText("면접 후 협의");
//                            input_hope03.setText("면접 후 협의");
//                            input_hope04.setText("면접 후 협의");
//                        }else{
//                            input_hope01.setText(dbConnection.introduceData_lists.get(0).getHope_money());
//                            input_hope02.setText(dbConnection.introduceData_lists.get(0).getHope_workdate());
//                            input_hope03.setText(dbConnection.introduceData_lists.get(0).getHope_work_yoil());
//                            input_hope04.setText(dbConnection.introduceData_lists.get(0).getHope_work_time());
//                        }
//                    }
//                }
//            });
//
//        });
//        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
