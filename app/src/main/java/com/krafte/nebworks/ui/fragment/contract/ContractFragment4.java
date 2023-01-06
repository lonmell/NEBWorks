package com.krafte.nebworks.ui.fragment.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ContractListAdapter;
import com.krafte.nebworks.data.ContractData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.ContractListInterface;
import com.krafte.nebworks.databinding.ContractFragmentBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ContractFragment4 extends Fragment {
    private ContractFragmentBinding binding;
    private static final String TAG = "CareerFragment1";
    DBConnection dbConnection = new DBConnection();
    Context mContext;
    Activity activity;

    //shared Data
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_id = "";

    //Other
    Drawable icon_on;
    Drawable icon_off;
    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();
    Dlog dlog = new Dlog();

    ArrayList<ContractData.ContractData_list> mList = new ArrayList<>();
    ArrayList<ContractData.ContractData_list> searchmList = new ArrayList<>();
    ContractListAdapter mAdapter;

    Timer timer;

    public static ContractFragment4 newInstance(int number) {
        ContractFragment4 fragment = new ContractFragment4();
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
        shardpref = new PreferenceHelper(mContext);
        //Singleton Area
        USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
        USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","0");
        place_id        = PlaceCheckData.getInstance().getPlace_id();

        //shardpref Area

        dlog.DlogContext(mContext);
        setBtnEvent();

        timer = new Timer();

        return binding.getRoot();
    }

    private void setBtnEvent(){

    }

    String searchNmae = "";
    Handler mHandler;
    int Cnt = 0;
    int Cnt1 = 0;
    @Override
    public void onResume() {
        super.onResume();
        mList = new ArrayList<>();
        searchmList = new ArrayList<>();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //5초마다 실행
                searchNmae = shardpref.getString("searchName","");
                if(!searchNmae.isEmpty() && !mList.isEmpty()){
                    Cnt = 0;
                    dlog.i("searchName : " + searchNmae);
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(() -> {
                        Cnt1 ++;
                        searchFilter(searchNmae);
                    }, 0);
                }else{
                    if(Cnt == 0){
                        Cnt1 = 0;
                        Cnt++;
                        SetContractList();
                        shardpref.remove("searchName");
                    }
                }
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    @Override
    public void onStop(){
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timer.cancel();
        shardpref.remove("searchName");
    }



    public void SetContractList() {
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ContractListInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            ContractListInterface api = retrofit.create(ContractListInterface.class);
            Call<String> call = api.getData(place_id);

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
                            mAdapter = new ContractListAdapter(mContext, mList);
                            binding.contractList.setAdapter(mAdapter);
                            binding.contractList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            if (Response.length() == 0) {
                                binding.nodataArea.setVisibility(View.VISIBLE);
                            } else {
                                binding.nodataArea.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);

                                    if(jsonObject.getString("contract_yn").equals("0")){
                                        if(USER_INFO_AUTH.equals("0")){
                                            mAdapter.addItem(new ContractData.ContractData_list(
                                                    jsonObject.getString("id"),
                                                    jsonObject.getString("place_id"),
                                                    jsonObject.getString("user_id"),
                                                    jsonObject.getString("name"),
                                                    jsonObject.getString("contract_yn"),
                                                    jsonObject.getString("img_path"),
                                                    jsonObject.getString("jumin"),
                                                    jsonObject.getString("kind"),
                                                    jsonObject.getString("join_date"),
                                                    jsonObject.getString("owner_sign_id"),
                                                    jsonObject.getString("worker_sign_id"),
                                                    jsonObject.getString("progress_pos"),
                                                    jsonObject.getString("contract_id")
                                            ));
                                        }else{
                                            if(jsonObject.getString("user_id").equals(USER_INFO_ID)){
                                                mAdapter.addItem(new ContractData.ContractData_list(
                                                        jsonObject.getString("id"),
                                                        jsonObject.getString("place_id"),
                                                        jsonObject.getString("user_id"),
                                                        jsonObject.getString("name"),
                                                        jsonObject.getString("contract_yn"),
                                                        jsonObject.getString("img_path"),
                                                        jsonObject.getString("jumin"),
                                                        jsonObject.getString("kind"),
                                                        jsonObject.getString("join_date"),
                                                        jsonObject.getString("owner_sign_id"),
                                                        jsonObject.getString("worker_sign_id"),
                                                        jsonObject.getString("progress_pos"),
                                                        jsonObject.getString("contract_id")
                                                ));
                                            }
                                        }
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

    @SuppressLint("NotifyDataSetChanged")
    public void searchFilter(String searchText) {
        if(searchText.length() != 0 && mList.size() != 0){
            searchmList.clear();
            dlog.i("searchFilter 1");
            dlog.i("mList.size() : " + mList.size());
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getName().toLowerCase().contains(searchText.toLowerCase())) {
                    dlog.i("searchFilter contain : " + mList.get(i).getName() + "/" + mList.get(i).getName().toLowerCase().contains(searchText.toLowerCase()));
//                    mList.clear();
                    searchmList.add(mList.get(i));
//                    break;
                }
            }
            mAdapter.filterList(searchmList);
            mAdapter.notifyDataSetChanged();
        }else{
            dlog.i("searchFilter 2");
            mAdapter.filterList(mList);
            mAdapter.notifyDataSetChanged();
        }
    }
}
