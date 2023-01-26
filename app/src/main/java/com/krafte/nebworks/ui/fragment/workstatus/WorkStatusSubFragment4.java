package com.krafte.nebworks.ui.fragment.workstatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
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

import com.krafte.nebworks.adapter.WorkTapMemberAdapter;
import com.krafte.nebworks.bottomsheet.CommuteBottomSheet;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkStatusTapData;
import com.krafte.nebworks.dataInterface.WorkStatusTapInterface;
import com.krafte.nebworks.databinding.MembersubFragment1Binding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkStatusSubFragment4 extends Fragment {
    private MembersubFragment1Binding binding;
    private final static String TAG = "WorkStatusSubFragment4";
    Context mContext;
    Activity activity;

    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String change_place_id = "";

    //Other
    ArrayList<WorkStatusTapData.WorkStatusTapData_list> mList;
    WorkTapMemberAdapter mAdapter = null;
    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    int listitemsize = 0;
    Dlog dlog = new Dlog();

    public static WorkStatusSubFragment4 newInstance() {
        return new WorkStatusSubFragment4();
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
    String place_owner_id = "";

    String toDay = "";
    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    int total_member_cnt = 0;

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
            //Singleton Area
            USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
            USER_INFO_EMAIL = UserCheckData.getInstance().getUser_account();
            place_id        = PlaceCheckData.getInstance().getPlace_id();
            place_owner_id  = PlaceCheckData.getInstance().getPlace_owner_id();
            change_place_id = place_id;

            //shardpref Area
            shardpref.putInt("SELECT_POSITION", 0);

            dlog.i("place_owner_id : " + place_owner_id);
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
    public void onDestroy(){
        super.onDestroy();
        shardpref.remove("FtoDay");
        shardpref.remove("change_place_id");
    }

    @Override
    public void onResume() {
        super.onResume();
        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        toDay = shardpref.getString("FtoDay",toDay);
        change_place_id = shardpref.getString("change_place_id","").isEmpty()?PlaceCheckData.getInstance().getPlace_id():shardpref.getString("change_place_id","");
        dlog.i("change_place_id :"  + change_place_id);
        SetAllMemberList();
    }

    private void setBtnEvent() {

    }

    /*직원 전체 리스트 START*/
    public void SetAllMemberList() {
        total_member_cnt = 0;
        dlog.i("------SetAllMemberList------");
        dlog.i("change_place_id :"  + change_place_id);
        dlog.i("USER_INFO_ID :"     + USER_INFO_ID);
        dlog.i("toDay :"            + toDay);
        dlog.i("------SetAllMemberList------");
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WorkStatusTapInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            WorkStatusTapInterface api = retrofit.create(WorkStatusTapInterface.class);
            Call<String> call = api.getData(change_place_id,USER_INFO_ID,"",toDay);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);

                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);

                            mList = new ArrayList<>();
                            mAdapter = new WorkTapMemberAdapter(mContext, mList, "",getParentFragmentManager());
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
                                    dlog.i("commuting : " + jsonObject.getString("commuting"));
                                    if(jsonObject.getString("commuting").equals("휴무")){
                                        mAdapter.addItem(new WorkStatusTapData.WorkStatusTapData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("place_id"),
                                                jsonObject.getString("place_name"),
                                                jsonObject.getString("user_id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("account"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("jikgup"),
                                                jsonObject.getString("join_date"),
                                                jsonObject.getString("yoil"),
                                                jsonObject.getString("io_date"),
                                                jsonObject.getString("io_time"),
                                                jsonObject.getString("in_time"),
                                                jsonObject.getString("out_time"),
                                                jsonObject.getString("worktime"),
                                                jsonObject.getString("commuting")
                                        ));
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener2((v, position) -> {
                                    try{
                                        shardpref.putString("commute_place_id", Response.getJSONObject(position).getString("place_id"));
                                        shardpref.putString("commute_user_id", Response.getJSONObject(position).getString("user_id"));
                                        shardpref.putString("commute_name", Response.getJSONObject(position).getString("name"));
                                        shardpref.putString("commute_work_time", Response.getJSONObject(position).getString("worktime"));
                                        shardpref.putString("commute_in_time", Response.getJSONObject(position).getString("in_time"));
                                        shardpref.putString("commute_out_time", Response.getJSONObject(position).getString("out_time"));
                                        shardpref.putString("commute_date",Response.getJSONObject(position).getString("io_date"));
                                        CommuteBottomSheet cb = new CommuteBottomSheet();
                                        cb.show(getParentFragmentManager(), "commuteBottomSheet");
                                        cb.setOnItemClickListener(v1 -> {
                                            Handler handler = new Handler();
                                            handler.postDelayed(() -> {
                                                mList.clear();
                                                SetAllMemberList();
                                            }, 500); //0.5초 후
                                        });
                                    }catch (JSONException e){
                                        e.printStackTrace();
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
}
