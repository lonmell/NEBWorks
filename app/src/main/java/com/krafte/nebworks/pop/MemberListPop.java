package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MemberListPopAdapter;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.databinding.ActivityMemberlistPopBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MemberListPop extends Activity {
    private ActivityMemberlistPopBinding binding;
    private static final String TAG = "SelectTaskDatePop";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    String USER_INFO_ID = "";
    String place_id = "";

    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mList;
    MemberListPopAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityMemberlistPopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        place_id = shardpref.getString("place_id", "0");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");

        //-- 기본데이터 세팅 END
        setBtnEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        SetAllMemberList();
    }

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            closePop();
        });

        binding.bottomBtnBox.setOnClickListener(v -> {
            closePop();
        });
    }

    /*직원 전체 리스트 START*/
    public void SetAllMemberList() {
        dlog.i("-----MemberListPop-----");
        dlog.i("place_id : " + place_id);
        dlog.i("-----MemberListPop-----");
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id, "");

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("onSuccess : ", response.body());
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            mList = new ArrayList<>();
                            mAdapter = new MemberListPopAdapter(mContext, mList,0);
                            binding.allMemberList.setAdapter(mAdapter);
                            binding.allMemberList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            if (Response.length() == 0) {
                                binding.nodataArea.setVisibility(View.VISIBLE);
                                binding.allMemberList.setVisibility(View.GONE);
                            } else {
                                binding.nodataArea.setVisibility(View.GONE);
                                binding.allMemberList.setVisibility(View.VISIBLE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
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

    private void closePop() {
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }
}
