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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.WorkstatusDataListAdapter;
import com.krafte.nebworks.data.WorkStatusData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.WorkStatusDataInterface;
import com.krafte.nebworks.databinding.WorkstatusfragmentBinding;
import com.krafte.nebworks.util.DateCurrent;
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

public class WorkstatusFragment extends Fragment {
    private final static String TAG = "WorkstatusFragment";
    private WorkstatusfragmentBinding binding;
    Context mContext;
    Activity activity;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String place_id = "";

    int SELECTED_POSITION = 0;
    boolean wifi_certi_flag = false;
    boolean gps_certi_flag = false;

    //Other
    Handler mHandler;
    DateCurrent dc = new DateCurrent();
    RetrofitConnect rc = new RetrofitConnect();

    ArrayList<WorkStatusData.WorkStatusData_list> mList;
    ArrayList<WorkStatusData.WorkStatusData_list> mList2;
    WorkstatusDataListAdapter mAdapter = null; // -- 근무중 commute 0
    WorkstatusDataListAdapter mAdapter2 = null; // -- 퇴근 commute 1
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    int topNum1 = 0;
    int topNum2 = 0;
    int topNum3 = 0;
    int topNum4 = 0;

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
//      ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.workstatusfragment, container, false);
        binding = WorkstatusfragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            dlog.i("place_id : "+place_id);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
            place_id = shardpref.getString("place_id", "");

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
        topNum1 = 0;
        topNum2 = 0;
        topNum3 = 0;
        topNum4 = 0;
        SetAllMemberList();
        GetWorkStateInfo(place_id);
    }

    private void setBtnEvent() {

    }
    /*직원 전체 리스트 START*/
    public void SetAllMemberList() {
        dlog.i("---------SetAllMemberList Check---------");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("getMonth : " + (dc.GET_MONTH.length() == 1 ? "0" + dc.GET_MONTH : dc.GET_MONTH));
        dlog.i("---------SetAllMemberList Check---------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AllMemberInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        AllMemberInterface api = retrofit.create(AllMemberInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("GetWorkStateInfo function START");
                dlog.e("response 1: " + response.isSuccessful());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            binding.topNum01.setText(String.valueOf(Response.length()) + "\n전체인원");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }

    public void GetWorkStateInfo(String place_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WorkStatusDataInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        WorkStatusDataInterface api = retrofit.create(WorkStatusDataInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "GetWorkStateInfo function START");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + response.body());
                activity.runOnUiThread(() -> {

                    if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                        try {
                            JSONArray Response = new JSONArray(response.body());
                            mList = new ArrayList<>();
                            mAdapter = new WorkstatusDataListAdapter(mContext, mList);
                            binding.StatusList01.setAdapter(mAdapter);
                            binding.StatusList01.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            mList2 = new ArrayList<>();
                            mAdapter2 = new WorkstatusDataListAdapter(mContext, mList2);
                            binding.StatusList02.setAdapter(mAdapter2);
                            binding.StatusList02.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (jsonObject.getString("commute").equals("0")){
                                        mAdapter.addItem(new WorkStatusData.WorkStatusData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("department"),
                                                jsonObject.getString("position"),
                                                jsonObject.getString("commute")
                                        ));
                                    }else if(jsonObject.getString("commute").equals("1") ){
                                        mAdapter2.addItem(new WorkStatusData.WorkStatusData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("department"),
                                                jsonObject.getString("position"),
                                                jsonObject.getString("commute")
                                        ));
                                    }
                                    if (jsonObject.getString("commute").equals("0")) {
                                        topNum2 += 1;
                                    }
                                    if(jsonObject.getString("commute").equals("1")){
                                        topNum3 += 1;
                                    }
                                }

                                Log.i(TAG, "근무 중 : " + topNum2);
                                Log.i(TAG, "퇴근 : " + topNum3);
                                binding.topNum02.setText(topNum2 + "\n근무중");
                                binding.topNum03.setText(topNum3 + "\n퇴근");
                                mAdapter.notifyDataSetChanged();
                            }
//                            topNum1 = topNum2 + topNum3 + topNum4;
//                            top_num01.setText(topNum1 + "\n전체인원");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            @SuppressLint("LongLogTag")
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러2 = " + t.getMessage());
            }
        });
    }
}
