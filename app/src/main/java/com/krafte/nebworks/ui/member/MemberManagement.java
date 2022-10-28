package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkplaceMemberAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.databinding.ActivityMemberManageBinding;
import com.krafte.nebworks.util.DBConnection;
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

public class MemberManagement extends AppCompatActivity {
    private final static String TAG = "MemberManagement";
    private ActivityMemberManageBinding binding;
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID, USER_INFO_NAME, USER_INFO_AUTH, store_no;
    String returnPage;
    String place_owner_id;


    //Other
    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mList;
    WorkplaceMemberAdapter mAdapter = null;
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    PageMoveClass pm = new PageMoveClass();
    GetResultData resultData = new GetResultData();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();
    String group_yn = "";

    Handler mHandler;
    String sendTopic = "";
    String sendToken = "";
    boolean EmployeeChannelId1 = false;
    String click_action = "";
    String tap_kind = "";
    String message = "";
    String place_id = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_member_manage);
        binding = ActivityMemberManageBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);

        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        place_id = shardpref.getString("place_id", "0");
        place_owner_id = shardpref.getString("place_owner_id", "0");
        returnPage = shardpref.getString("returnPage", "");
        shardpref.putString("returnPage", TAG);
        dlog.i("USER_INFO_NAME:  " + USER_INFO_NAME);
        binding.bottomNavigation.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        SetAllMemberList();
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
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());

                            mList = new ArrayList<>();
                            mAdapter = new WorkplaceMemberAdapter(mContext, mList, getSupportFragmentManager());
                            binding.memberList.setAdapter(mAdapter);
                            binding.memberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            dlog.i("SetNoticeListview Thread run! ");

                            if (Response.length() == 0) {
                                binding.nodataMember.setVisibility(View.VISIBLE);
                                dlog.i("GET SIZE : " + Response.length());
                            } else {
                                binding.nodataMember.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new WorkPlaceMemberListData.WorkPlaceMemberListData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("name"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("account"),
                                            jsonObject.getString("employee_no"),
                                            jsonObject.getString("department"),
                                            jsonObject.getString("position"),
                                            jsonObject.getString("img_path")
                                    ));
                                }

                                mAdapter.notifyDataSetChanged();
                            }
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


    public void btnOnclick(View view) {
        if (view.getId() == R.id.out_store) {
            pm.PlaceListBack(mContext);
        } else if (view.getId() == R.id.notice) {
//            pm.EmployeeNotifyListL(mContext);
        } else if (view.getId() == R.id.bottom_navigation01) {
            pm.MainBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation02) {
            pm.PlaceWorkBack(mContext);
        } else if (view.getId() == R.id.bottom_navigation03) {
            pm.CalenderGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation04) {
            pm.WorkStateListGo(mContext);
        } else if (view.getId() == R.id.bottom_navigation05) {
            pm.MoreGo(mContext);
        }
    }


    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            pm.MainBack(mContext);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        pm.MainBack(mContext);
    }

}
