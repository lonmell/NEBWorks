package com.krafte.nebworks.ui.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.adapter.ListYoilStringAdapter;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.dataInterface.FobiddenInterface;
import com.krafte.nebworks.databinding.ActivityFobiddenListBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FobiddenListActivity extends AppCompatActivity {
    private ActivityFobiddenListBinding binding;
    private static final String TAG = "FobiddenListActivity";
    Context mContext;

    //Sharedf
    PreferenceHelper shardpref;

    Dlog dlog = new Dlog();
    String USER_INFO_ID = "";
    String place_owner_id = "";

    ArrayList<StringData.StringData_list> mList = null;
    ListYoilStringAdapter mAdapter;
    InputMethodManager imm;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "LongLogTag", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pushmanagement);
        binding = ActivityFobiddenListBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        binding.addWord.setOnClickListener(v -> {
            AddWord();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shardpref.remove("page_kind");
    }

    List<String> selectYoil = new ArrayList<>();
    RetrofitConnect rc = new RetrofitConnect();
    public void setRecyclerView() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FobiddenInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FobiddenInterface api = retrofit.create(FobiddenInterface.class);
        Call<String> call = api.getData("", "0");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("FobiddenList jsonResponse length : " + jsonResponse.length());
                    dlog.i("FobiddenList jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        mList = new ArrayList<>();
                        mAdapter = new ListYoilStringAdapter(mContext, mList, selectYoil);
                        binding.fobiddenlist.setAdapter(mAdapter);
                        binding.fobiddenlist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");

                        if (Response.length() == 0) {
                            binding.fobiddenlist.setVisibility(View.GONE);
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        } else {
                            binding.fobiddenlist.setVisibility(View.VISIBLE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);

                                mAdapter.addItem(new StringData.StringData_list(
                                        jsonObject.getString("word")
                                ));
                            }
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
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }

    private void AddWord(){
        dlog.i("----START AddWord-----");
        String fobiddenWord = binding.fobiddenInput.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FobiddenInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FobiddenInterface api = retrofit.create(FobiddenInterface.class);
        Call<String> call = api.getData(fobiddenWord , "1");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("AddWord jsonResponse length : " + jsonResponse.length());
                    dlog.i("AddWord jsonResponse : " + jsonResponse);
                    binding.fobiddenInput.setText("");
                    imm.hideSoftInputFromWindow(binding.fobiddenInput.getWindowToken(), 0);
                    setRecyclerView();
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
        dlog.i("----END AddWord-----");
    }
}
