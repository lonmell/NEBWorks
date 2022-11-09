package com.krafte.nebworks.ui.fragment.member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.WorkplaceMemberAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.databinding.MembersubFragment1Binding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MemberSubFragment3 extends Fragment {
    private MembersubFragment1Binding binding;
    private final static String TAG = "Page1Fragment";
    Context mContext;
    Activity activity;

    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";

    //Other
    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mList;
    WorkplaceMemberAdapter mAdapter = null;
    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    int listitemsize = 0;
    Dlog dlog = new Dlog();


//    public static MemberSubFragment3 newInstance(){
//        return new MemberSubFragment3();
//    }

    public static MemberSubFragment3 newInstance(int number) {
        MemberSubFragment3 fragment = new MemberSubFragment3();
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

    String NotiSearch = "";


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.membersub_fragment1, container, false);
        binding = MembersubFragment1Binding.inflate(inflater);
        mContext = inflater.getContext();

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //Shared
        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            place_id = shardpref.getString("place_id", "0");
            shardpref.putInt("SELECT_POSITION", 0);

            setBtnEvent();
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
        SetAllMemberList();

    }

    private void setBtnEvent() {

    }

    /*직원 전체 리스트 START*/
    public void SetAllMemberList() {
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id,"");
//            @Field("flag") int flag,
//            @Field("place_id") String place_id,
//            @Field("user_id") String user_id,
//            @Field("getMonth") String getMonth
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());

                            mList = new ArrayList<>();
                            mAdapter = new WorkplaceMemberAdapter(mContext, mList, getParentFragmentManager());
                            binding.allMemberlist.setAdapter(mAdapter);
                            binding.allMemberlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            if (Response.length() == 0) {
                                binding.nodataArea.setVisibility(View.VISIBLE);
                                binding.allMemberlist.setVisibility(View.GONE);
                            } else {
                                binding.nodataArea.setVisibility(View.GONE);
                                binding.allMemberlist.setVisibility(View.VISIBLE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (jsonObject.getString("state").equals("null") || jsonObject.getString("state").equals("0") ) {
                                        mAdapter.addItem(new WorkPlaceMemberListData.WorkPlaceMemberListData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("phone"),
                                                jsonObject.getString("gender"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("jumin"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("join_date"),
                                                jsonObject.getString("state"),
                                                jsonObject.getString("jikgup"),
                                                jsonObject.getString("pay"),
                                                jsonObject.getString("worktime")
                                        ));
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*직원 전체 리스트 END*/
}
