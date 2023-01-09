package com.krafte.nebworks.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.JusoAdapter;
import com.krafte.nebworks.data.SearchAddressData;
import com.krafte.nebworks.data.SearchAddressDataDetail;
import com.krafte.nebworks.dataInterface.GuideSearchAPInterface;
import com.krafte.nebworks.databinding.ActivityWriteaddressBinding;
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

public class WriteAddressActivity extends Activity {
    private static final String TAG = "TaskReuseActivity";
    private ActivityWriteaddressBinding binding;
    Context mContext;

    //Other 클래스
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();

    //Other 변수

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
        binding = ActivityWriteaddressBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            //shardpref Area
            binding.searchAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    onClickSearchAddress();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            setBtnEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setBtnEvent() {

    }

    String mSearchAddrStr = "";
    ArrayList<SearchAddressData.SearchAddressData_list> mList = new ArrayList<>();
    ArrayList<SearchAddressDataDetail.SearchAddressDataDetaillist> mList2 = new ArrayList<>();
    JusoAdapter mAdapter;

    public void onClickSearchAddress() {
        mSearchAddrStr = binding.searchAddress.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GuideSearchAPInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        GuideSearchAPInterface api = retrofit.create(GuideSearchAPInterface.class);
        Call<String> call = api.getData("1", "20", mSearchAddrStr, "devU01TX0FVVEgyMDIzMDEwOTE0MzA1ODExMzQxMTM=", "json");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("GetInsurancePercent function START");
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + response.body());
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String results = jsonObject.getString("results");
                            JSONObject jsonObject2 = new JSONObject(results);
                            String common = jsonObject2.getString("common");
                            JSONObject jsonObject3 = new JSONObject(results);
                            String juso = jsonObject3.getString("juso");

                            dlog.i("juso : " + juso);
                            JSONArray jsonArray = new JSONArray("[" + common + "]");
                            String errorMessage = jsonArray.getJSONObject(0).getString("errorMessage");
                            dlog.i("errorMessage : " + errorMessage);
                            if (errorMessage.equals("정상")) {
                                JSONArray Response = new JSONArray(juso);

                                mList2 = new ArrayList<>();
                                mAdapter = new JusoAdapter(mContext, mList2);
                                binding.jusoList.setAdapter(mAdapter);
                                binding.jusoList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                Log.i(TAG, "SetNoticeListview Thread run! ");

                                if (Response.length() == 0) {
                                    Log.i(TAG, "GET SIZE : " + Response.length());
                                    binding.noDataTxt.setVisibility(View.VISIBLE);
                                } else {
                                    //본인이 추가한 할일이 점주에게 보이지 안보이는지 회의 필요
                                    binding.noDataTxt.setVisibility(View.GONE);
                                    for (int i = 0; i < Response.length(); i++) {
                                        JSONObject Result = Response.getJSONObject(i);
                                        mAdapter.addItem(new SearchAddressDataDetail.SearchAddressDataDetaillist(
                                                Result.getString("roadAddrPart1"),
                                                Result.getString("roadAddrPart2"),
                                                Result.getString("zipNo")
                                        ));
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.setOnItemClickListener(new JusoAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int position) {
                                            try{
                                                shardpref.putString("pin_store_address",Response.getJSONObject(position).getString("roadAddrPart1"));
                                                shardpref.putString("pin_store_addressdetail",Response.getJSONObject(position).getString("roadAddrPart2"));
                                                shardpref.putString("pin_zipcode",Response.getJSONObject(position).getString("zipNo"));
                                                finish();
                                            }catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } else {
                                //juso 가 null이거나 검색어가 잘못된경우
//                                Toast_Nomal(errorMessage);
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

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) binding.getRoot().findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }

}
