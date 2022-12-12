package com.krafte.nebworks.ui.fragment.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
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

import com.krafte.nebworks.adapter.TaxListAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.TaxMemberData;
import com.krafte.nebworks.dataInterface.TaxMemListInterface;
import com.krafte.nebworks.databinding.CommunityFragment3Binding;
import com.krafte.nebworks.util.DateCurrent;
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

public class community_fragment3 extends Fragment {
    private CommunityFragment3Binding binding;
    private final static String TAG = "WorkStatusSubFragment1";
    Context mContext;
    Activity activity;

    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String place_id = "";
    String place_owner_id = "";

    //Other
    ArrayList<TaxMemberData.TaxMemberData_list> mList = new ArrayList<>();
    ArrayList<TaxMemberData.TaxMemberData_list> mList2 = new ArrayList<>();
    TaxListAdapter mAdapter = null;
    TaxListAdapter mAdapter2 = null;

    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();
    int listitemsize = 0;
    Dlog dlog = new Dlog();
    int total_member_cnt = 0;
    String toDay = "";
    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);

    public static community_fragment3 newInstance() {
        return new community_fragment3();
    }

    String str;

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
        binding = CommunityFragment3Binding.inflate(inflater);
        mContext = inflater.getContext();

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //Shared
        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            place_id = shardpref.getString("place_id", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            shardpref.putInt("SELECT_POSITION", 0);
            //-- 날짜 세팅
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
    public void onResume() {
        super.onResume();
        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        toDay = shardpref.getString("FtoDay",toDay);
        setRecyclerView();
    }

    private void setBtnEvent() {

    }
    private void allClear() {
        mList.clear();
        mList2.clear();
    }

    int semu_cnt = 0;
    int nomu_cnt = 0;

    public void setRecyclerView() {
        allClear();
        dlog.i("position 0 setRecyclerView place_id : " + place_id);
        mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaxMemListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaxMemListInterface api = retrofit.create(TaxMemListInterface.class);
        Call<String> call = api.getData(place_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("semu/nomu jsonResponse length : " + jsonResponse.length());
                    dlog.i("semu/nomu jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        mList = new ArrayList<>();
                        mAdapter = new TaxListAdapter(mContext, mList);
                        binding.semuList.setAdapter(mAdapter);
                        binding.semuList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");

                        for (int i = 0; i < Response.length(); i++) {
                            JSONObject jsonObject = Response.getJSONObject(i);
                            if(jsonObject.getString("kind").equals("1")){
                                semu_cnt ++ ;
                            }else if(jsonObject.getString("kind").equals("2")){
                                nomu_cnt ++ ;
                            }
                        }

                        if (Response.length() == 0) {
                            Log.i(TAG, "GET SIZE : " + mList.size());
                        } else {
                            if(semu_cnt > 3){
                                for (int i = 0; i < 4; i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if(jsonObject.getString("kind").equals("1")){
                                        mAdapter.addItem(new TaxMemberData.TaxMemberData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("place_id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("address"),
                                                jsonObject.getString("contact_num"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("created_at"),
                                                jsonObject.getString("updated_at")
                                        ));
                                    }

                                }
                            }else{
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if(jsonObject.getString("kind").equals("1")){
                                        mAdapter.addItem(new TaxMemberData.TaxMemberData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("place_id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("address"),
                                                jsonObject.getString("contact_num"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("created_at"),
                                                jsonObject.getString("updated_at")
                                        ));
                                    }

                                }
                            }

                            mAdapter.notifyDataSetChanged();
                        }


                        mList2 = new ArrayList<>();
                        mAdapter2 = new TaxListAdapter(mContext, mList2);
                        binding.nomuList.setAdapter(mAdapter2);
                        binding.nomuList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");

                        if (Response.length() == 0) {
                            Log.i(TAG, "GET SIZE : " + mList2.size());
                        } else {
                            if(nomu_cnt > 4){
                                for (int i = 0; i < 5; i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if(jsonObject.getString("kind").equals("2")){
                                        mAdapter2.addItem(new TaxMemberData.TaxMemberData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("place_id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("address"),
                                                jsonObject.getString("contact_num"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("created_at"),
                                                jsonObject.getString("updated_at")
                                        ));
                                    }

                                }
                            }else{
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if(jsonObject.getString("kind").equals("2")){
                                        mAdapter2.addItem(new TaxMemberData.TaxMemberData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("place_id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("address"),
                                                jsonObject.getString("contact_num"),
                                                jsonObject.getString("kind"),
                                                jsonObject.getString("created_at"),
                                                jsonObject.getString("updated_at")
                                        ));
                                    }

                                }
                            }
                            mAdapter2.notifyDataSetChanged();
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
