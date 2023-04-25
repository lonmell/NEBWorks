package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.PlaceMemberSelectAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.databinding.ActivitySelectmemberpopBinding;
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

public class SelectMemberPop extends Activity {
    private ActivitySelectmemberpopBinding binding;
    private static final String TAG = "SelectMemberPop";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    GetResultData resultData = new GetResultData();
    Handler mHandler;
    String place_id = "";
    String place_owner_id = "";

    ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mList;
    PlaceMemberSelectAdapter mAdapter = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 화면이 투명해진다
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_tap1option);
        binding = ActivitySelectmemberpopBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        place_id = shardpref.getString("place_id","0");
        place_owner_id = shardpref.getString("place_owner_id","0");
    }

    @Override
    public void onResume(){
        super.onResume();
        SetAllMemberList();
    }

    /*직원 전체 리스트 START*/
    int total_member_cnt = 0;
    RetrofitConnect rc = new RetrofitConnect();
    public void SetAllMemberList() {
        total_member_cnt = 0;
        dlog.i("-----SetAllMemberList------");
        dlog.i("place_id : " + place_id);
        dlog.i("-----SetAllMemberList------");
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id,"");

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                        dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            mList = new ArrayList<>();
                            mAdapter = new PlaceMemberSelectAdapter(mContext, mList);
                            binding.allMemberlist.setAdapter(mAdapter);
                            binding.allMemberlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            if (Response.length() == 0) {
                                total_member_cnt = 0;
                                binding.nodataArea.setVisibility(View.VISIBLE);
                                binding.allMemberlist.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if(!place_owner_id.equals(jsonObject.getString("id"))){
                                        total_member_cnt ++;
                                        mAdapter.addItem(new WorkPlaceMemberListData.WorkPlaceMemberListData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("place_name"),
                                                jsonObject.getString("account"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("phone"),
                                                jsonObject.getString("gender"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("jumin"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("join_date"),
                                                jsonObject.getString("state"),
                                                jsonObject.getString("jikgup"),
                                                jsonObject.getString("paykind"),
                                                jsonObject.getString("pay"),
                                                jsonObject.getString("worktime"),
                                                jsonObject.getString("contract_cnt")
                                        ));
                                    }
                                }
                                if(total_member_cnt == 0){
                                    binding.nodataArea.setVisibility(View.VISIBLE);
                                    binding.allMemberlist.setVisibility(View.GONE);
                                }else{
                                    binding.nodataArea.setVisibility(View.GONE);
                                    binding.allMemberlist.setVisibility(View.VISIBLE);
                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener(new PlaceMemberSelectAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String user_id, String user_name) {
                                        shardpref.putString("item_user_id",user_id);
                                        shardpref.putString("item_user_name",user_name);
                                        closePop();
                                    }
                                });
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

    @Override
    public void onStop(){
        super.onStop();
        closePop();
    }
    private void closePop() {
        finish();
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        overridePendingTransition(0, R.anim.translate_down);
    }

}
