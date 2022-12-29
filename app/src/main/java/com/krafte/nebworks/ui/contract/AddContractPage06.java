package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ContractTermAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.TermData;
import com.krafte.nebworks.dataInterface.ContractPagePosUp;
import com.krafte.nebworks.dataInterface.TermDelInterface;
import com.krafte.nebworks.dataInterface.TermGetInterface;
import com.krafte.nebworks.dataInterface.TermInputInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd06Binding;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddContractPage06 extends AppCompatActivity {
    private ActivityContractAdd06Binding binding;
    private final static String TAG = "AddContractPage04";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";
    String contract_id = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();
    List<String> terml = new ArrayList<>();
    ArrayList<TermData.TermData_list> mList = new ArrayList<>();
    ContractTermAdapter mAdapter;

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractAdd06Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref       = new PreferenceHelper(mContext);
        place_id        = shardpref.getString("place_id","0");
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID","0");
        worker_id       = shardpref.getString("worker_id","0");
        contract_id     = shardpref.getString("contract_id","0");

        setBtnEvent();
    }

    @Override
    public void onResume(){
        super.onResume();
        setTermList();
    }


    public void setTermList() {
        dlog.i("------setTermList------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("------setTermList------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TermGetInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TermGetInterface api = retrofit.create(TermGetInterface.class);
        Call<String> call = api.getData(contract_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(jsonResponse);
                                mList = new ArrayList<>();
                                mAdapter = new ContractTermAdapter(mContext, mList,0);
                                binding.termList.setAdapter(mAdapter);
                                binding.termList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                dlog.i("SIZE : " + Response.length());
                                if (jsonResponse.equals("[]")) {
                                    dlog.i("SIZE : " + Response.length());
                                } else {
                                    for (int i = 0; i < Response.length(); i++) {
                                        JSONObject jsonObject = Response.getJSONObject(i);
                                        mAdapter.addItem(new TermData.TermData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("contract_id"),
                                                jsonObject.getString("term")
                                        ));
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener(new ContractTermAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        DelTerm(mList.get(position).getId());
                                    }
                                });
                                dlog.i("SetNoticeListview Thread run! ");
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure (@NonNull Call< String > call, @NonNull Throwable t){
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    private void setBtnEvent(){
        binding.addTerm.setOnClickListener(v -> {
            String write_term = "근로자가 무단 결근 2일 이상 하거나 월 2일 이상\n" +
                    "결근하는 경우 근로계약을 해지 할 수 있음";
            InputTerm(write_term);
            binding.newTerm.setText("");
        });

        binding.next.setOnClickListener(v -> {
            UpdatePagePos(contract_id);
        });

        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("progress_pos");
            if(!shardpref.getString("progress_pos","").isEmpty()){
                pm.ContractFragment(mContext);
            }else{
                super.onBackPressed();
            }
        });
    }

    private void InputTerm(String write_term){
        dlog.i("------InputTerm------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("write_term : " + write_term);
        dlog.i("------InputTerm------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TermInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TermInputInterface api = retrofit.create(TermInputInterface.class);
        Call<String> call = api.getData(contract_id,write_term);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(!jsonResponse.equals("null") || !jsonResponse.equals("[]") || !jsonResponse.isEmpty()){
                                    setTermList();
                                }else{
                                    Toast_Nomal("특약이 저장되지 않았슶니다.");
                                }
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure (@NonNull Call< String > call, @NonNull Throwable t){
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    private void DelTerm(String id){
        dlog.i("------DelTerm------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("id : " + id);
        dlog.i("------DelTerm------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TermDelInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TermDelInterface api = retrofit.create(TermDelInterface.class);
        Call<String> call = api.getData(id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(jsonResponse.replace("\"","").equals("success")){
                                    setTermList();
                                }else{
                                    Toast_Nomal("특약이 삭제되지 않았습니다.");
                                }
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure (@NonNull Call< String > call, @NonNull Throwable t){
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }


    private void UpdatePagePos(String contract_id){
        dlog.i("------UpdatePagePos------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("------UpdatePagePos------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractPagePosUp.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractPagePosUp api = retrofit.create(ContractPagePosUp.class);
        Call<String> call = api.getData(contract_id,"4");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {

                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure (@NonNull Call< String > call, @NonNull Throwable t){
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed(){

        if(!shardpref.getString("progress_pos","").isEmpty()){
            pm.ContractFragment(mContext);
        }else{
            super.onBackPressed();
        }
        shardpref.remove("progress_pos");
    }

    public void Toast_Nomal(String message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup)findViewById(R.id.toast_layout));
        TextView toast_textview  = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
