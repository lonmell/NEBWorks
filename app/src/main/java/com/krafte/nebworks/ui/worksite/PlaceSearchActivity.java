package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.PlaceSearchAdapter;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.databinding.ActivityPlaceSearchBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.GpsTracker;
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

public class PlaceSearchActivity  extends AppCompatActivity {
    private ActivityPlaceSearchBinding binding;
    private static final String TAG = "StoreSearchActivity";
    Context mContext;
    Thread th;
    DBConnection dbConnection;

    GpsTracker gpsTracker;
    double latitude = 0;
    double longitude = 0;
    String sido = "";
    String gugun = "";
    String division = "";
    String Setaddress = "";

    //Other
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();
    RelativeLayout login_alert_text;
    ImageView loading_view;
    ArrayList<PlaceListData.PlaceListData_list> mList;
    ArrayList<PlaceListData.PlaceListData_list> searchmList;
    PlaceSearchAdapter mAdapter = null;
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();

    //Sharedf
    String USER_INFO_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_place_search);
        binding = ActivityPlaceSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        try{
            mContext = this;
            dbConnection = new DBConnection();
            dlog.DlogContext(mContext);

            setBtnEvent();

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            gpsTracker = new GpsTracker(this);
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
//        Glide.with(this).load(R.raw.walk_loading2).into(loading_view);
            SetWorkplaceList();

            binding.searchStore.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    searchFilter(s.toString());
                }
            });
        }catch (Exception e){
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
            binding.searchCnt.setText(searchmList.size());
            mAdapter.filterList(searchmList);
            mAdapter.notifyDataSetChanged();
        }else{
            dlog.i("searchFilter 2");
            binding.searchCnt.setText(mList.size());
            mAdapter.filterList(mList);
            mAdapter.notifyDataSetChanged();
        }
    }



    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            pm.PlaceList(mContext);
        });
        binding.searchBtn.setOnClickListener(v -> {
            Setaddress = binding.searchStore.getText().toString();
            searchFilter(Setaddress);
        });

        binding.searchStore.setImeOptions(EditorInfo.IME_ACTION_DONE); //키보드 다음 버튼을 완료 버튼으로 바꿔줌
        binding.searchStore.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //키보드에 완료버튼을 누른 후 수행할 것
                Setaddress = binding.searchStore.getText().toString();
                searchFilter(Setaddress);
                return true;
            }
            return false;
        });


    }

    public void SetWorkplaceList() {
//        login_alert_text.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData("-99","");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"NotifyDataSetChanged", "LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("SetWorkplaceList function START");
                dlog.e("response 1: " + response.isSuccessful());
//                dlog.e("response 2: " + response.body());
//                dlog.e("response 2: " + rc.getBase64decode(response.body()));
//                login_alert_text.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        mList = new ArrayList<>();
                        searchmList = new ArrayList<>();
                        mAdapter = new PlaceSearchAdapter(mContext, mList,searchmList);
                        binding.addressList.setAdapter(mAdapter);
                        binding.addressList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        for (int i = 0; i < Response.length(); i++) {
                            JSONObject jsonObject = Response.getJSONObject(i);

                            mAdapter.addItem(new PlaceListData.PlaceListData_list(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("owner_id"),
                                    jsonObject.getString("owner_name"),
                                    jsonObject.getString("registr_num"),
                                    jsonObject.getString("store_kind"),
                                    jsonObject.getString("address"),
                                    jsonObject.getString("latitude"),
                                    jsonObject.getString("longitude"),
                                    jsonObject.getString("pay_day"),
                                    jsonObject.getString("test_period"),
                                    jsonObject.getString("vacation_select"),
                                    jsonObject.getString("insurance"),
                                    jsonObject.getString("start_time"),
                                    jsonObject.getString("end_time"),
                                    jsonObject.getString("save_kind"),
                                    jsonObject.getString("img_path"),
                                    jsonObject.getString("total_cnt"),
                                    jsonObject.getString("i_cnt"),
                                    jsonObject.getString("o_cnt"),
                                    jsonObject.getString("created_at")
                            ));
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        pm.PlaceList(mContext);
    }

}
