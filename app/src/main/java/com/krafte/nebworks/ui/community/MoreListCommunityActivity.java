package com.krafte.nebworks.ui.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.CommunityAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.ActivityCommunityAllBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
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


public class MoreListCommunityActivity extends AppCompatActivity {
    private ActivityCommunityAllBinding binding;
    private final static String TAG = "WorkCommunityActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String returnPage = "";
    String place_id = "";

    ArrayList<PlaceNotiData.PlaceNotiData_list> mList = new ArrayList<>();
    CommunityAdapter mAdapter = null;

    //Other
    DateCurrent dc = new DateCurrent();
    RetrofitConnect rc = new RetrofitConnect();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    PageMoveClass pm = new PageMoveClass();
    int paging_position = 0;
    int listitemsize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_work_community);
        binding = ActivityCommunityAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;

        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "");
        returnPage      = shardpref.getString("returnPage", "");
        place_id        = shardpref.getString("place_id", "");
        Log.i(TAG, "USER_INFO_AUTH : " + USER_INFO_AUTH);

    }

    @Override
    public void onResume(){
        super.onResume();
        setRecyclerView();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setBtnEvent() {
    }

    public void setRecyclerView() {
        //전체
        mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "", "2","2");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        mList = new ArrayList<>();
                        mAdapter = new CommunityAdapter(mContext, mList,shardpref.getInt("com_kind",1));
                        binding.allList.setAdapter(mAdapter);
                        binding.allList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();

                        if (Response.length() == 0) {
                            binding.noDataTxt2.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        } else {
                            binding.noDataTxt2.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(jsonObject.getString("boardkind").equals("자유게시판")){
                                    mAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("writer_name"),
                                            jsonObject.getString("writer_img_path"),
                                            jsonObject.getString("jikgup"),
                                            jsonObject.getString("view_cnt"),
                                            jsonObject.getString("comment_cnt"),
                                            jsonObject.getString("link"),
                                            jsonObject.getString("feed_img_path"),
                                            jsonObject.getString("created_at"),
                                            jsonObject.getString("updated_at"),
                                            jsonObject.getString("open_date"),
                                            jsonObject.getString("close_date"),
                                            jsonObject.getString("boardkind"),
                                            jsonObject.getString("category")
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

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }
}
