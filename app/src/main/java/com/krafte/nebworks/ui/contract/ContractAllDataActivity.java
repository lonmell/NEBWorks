package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ContractTermAdapter;
import com.krafte.nebworks.data.TermData;
import com.krafte.nebworks.dataInterface.ContractGetAllInterface;
import com.krafte.nebworks.dataInterface.TermGetInterface;
import com.krafte.nebworks.databinding.ActivityContractallDataBinding;
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

public class ContractAllDataActivity extends AppCompatActivity {
    private ActivityContractallDataBinding binding;
    private final static String TAG = "ContractAllDataActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String contract_id = "";

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    ArrayList<TermData.TermData_list> mList = null;
    ContractTermAdapter mAdapter = null;

    int select0102 = 1;
    int size010203 = 1;

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractallDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        contract_id = shardpref.getString("contract_id","");

        setBtnEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAllContract();
    }

    private void setBtnEvent(){
        binding.inoutPrint.setOnClickListener(v -> {
            String Contract_uri = "https://nepworks.net/NEBWorks/ContractPDF2.php?id="+contract_id;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Contract_uri));
            startActivity(intent);
        });
    }

    private void GetAllContract(){
        dlog.i("------GetAllContract------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("------GetAllContract------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractGetAllInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractGetAllInterface api = retrofit.create(ContractGetAllInterface.class);
        Call<String> call = api.getData(contract_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetAllContract jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetAllContract jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    String id               = Response.getJSONObject(0).getString("id");
                                    String place_id         = Response.getJSONObject(0).getString("place_id");
                                    String place_name       = Response.getJSONObject(0).getString("place_name");
                                    String owner_id         = Response.getJSONObject(0).getString("owner_id");
                                    String owner_name       = Response.getJSONObject(0).getString("owner_name");
                                    String worker_id        = Response.getJSONObject(0).getString("worker_id");
                                    String buisness_kind    = Response.getJSONObject(0).getString("buisness_kind");// 1 - 개인사업자 / 2 - 법인사업자 [page - 1]
                                    String registr_num      = Response.getJSONObject(0).getString("registr_num");
                                    String address          = Response.getJSONObject(0).getString("address");
                                    String address_detail   = Response.getJSONObject(0).getString("address_detail");
                                    String place_size       = Response.getJSONObject(0).getString("place_size"); //사업장 사이즈
                                    String owner_phone      = Response.getJSONObject(0).getString("owner_phone");
                                    String owner_email      = Response.getJSONObject(0).getString("owner_email");
                                    String contract_start   = Response.getJSONObject(0).getString("contract_start");
                                    String contract_end     = Response.getJSONObject(0).getString("contract_end");
                                    String contract_type    = Response.getJSONObject(0).getString("contract_type");// 기간의 정함이 없는 계약 / 0 - off / 1 - on [page - 2]
                                    String work_yoil        = Response.getJSONObject(0).getString("work_yoil");
                                    String rest_yoil        = Response.getJSONObject(0).getString("rest_yoil");
                                    String work_start       = Response.getJSONObject(0).getString("work_start");
                                    String work_end         = Response.getJSONObject(0).getString("work_end");
                                    String rest_start       = Response.getJSONObject(0).getString("rest_start");
                                    String rest_end         = Response.getJSONObject(0).getString("rest_end");
                                    String work_contents    = Response.getJSONObject(0).getString("work_contents");
                                    String pay_type         = Response.getJSONObject(0).getString("pay_type");//급여지급방식 0- 직접전달 / 1 - 급여통장 [page - 2]
                                    String payment          = Response.getJSONObject(0).getString("payment");
                                    String pay_conference   = Response.getJSONObject(0).getString("pay_conference");//협의 여부 / 1- 가능 / 0 - 불가능 [page - 2]
                                    String pay_loop         = Response.getJSONObject(0).getString("pay_loop");
                                    String insurance        = Response.getJSONObject(0).getString("insurance");
                                    String add_contents     = Response.getJSONObject(0).getString("add_contents");
                                    String add_terms        = Response.getJSONObject(0).getString("add_terms");
                                    String worker_name      = Response.getJSONObject(0).getString("worker_name");
                                    String worker_jumin     = Response.getJSONObject(0).getString("worker_jumin");
                                    String worker_address   = Response.getJSONObject(0).getString("worker_address");
                                    String worker_address_detail = Response.getJSONObject(0).getString("worker_address_detail");
                                    String worker_phone     = Response.getJSONObject(0).getString("worker_phone");
                                    String worker_email     = Response.getJSONObject(0).getString("worker_email");
                                    String owner_sign       = Response.getJSONObject(0).getString("owner_sign");
                                    String worker_sign      = Response.getJSONObject(0).getString("worker_sign");
                                    String created_at       = Response.getJSONObject(0).getString("created_at");
                                    String updated_at       = Response.getJSONObject(0).getString("updated_at");
                                    String test_period      = Response.getJSONObject(0).getString("test_period");
                                    ChangeSelect0102(Integer.parseInt(buisness_kind));
                                    ChangeSize010203(Integer.parseInt(place_size));
                                    binding.input01.setText(owner_name);
                                    binding.input02.setText(registr_num);
                                    binding.input04.setText(address);
                                    binding.input05.setText(address_detail);
                                    binding.input06.setText(owner_phone);
                                    binding.input07.setText(owner_email);
                                    binding.select01date.setText(contract_start);
                                    binding.select02date.setText(contract_end);

                                    if(contract_type.equals("0")){
                                        binding.select03Round.setBackgroundResource(R.drawable.ic_empty_round);
                                    }else if(contract_type.equals("1")){
                                        binding.select03Round.setBackgroundResource(R.drawable.ic_full_round);
                                    }
                                    binding.workyoilList.setText(work_yoil);
                                    binding.restyoilList.setText(rest_yoil);
                                    binding.wtime01time.setText(work_start);
                                    binding.wtime02time.setText(work_end);
                                    binding.resttime01time.setText(rest_start);
                                    binding.resttime02time.setText(rest_end);
                                    binding.input08.setText(work_contents);

                                    if(pay_type.equals("0")) {
                                        binding.payTypeTv.setText("근로자에게 직접지급");
                                    }else{
                                        binding.payTypeTv.setText("근로자명의 예금통장에 입금");
                                    }
                                    binding.payment.setText(payment);
                                    if(pay_conference.equals("0")){//0 - 불가능
                                        binding.payConferenceRound.setBackgroundResource(R.drawable.ic_empty_round);
                                    }else if(pay_conference.equals("1")){//1- 가능
                                        binding.payConferenceRound.setBackgroundResource(R.drawable.ic_full_round);
                                    }
                                    binding.payLoop.setText(pay_loop);
                                    for(String str : insurance.split(",")){
                                        if(str.equals("식사제공")){
                                            binding.bokjiRound01.setBackgroundResource(R.drawable.ic_full_round);
                                        }else if(str.equals("4대보험")){
                                            binding.bokjiRound02.setBackgroundResource(R.drawable.ic_full_round);
                                        }else if(str.equals("교통비지원")){
                                            binding.bokjiRound03.setBackgroundResource(R.drawable.ic_full_round);
                                        }else if(str.equals("인센티브")){
                                            binding.bokjiRound04.setBackgroundResource(R.drawable.ic_full_round);
                                        }
                                    }
                                    binding.input09.setText(add_contents);
                                    binding.input10.setText(worker_name);
                                    binding.input11.setText(worker_jumin);
                                    binding.input12.setText(worker_address);
                                    binding.input13.setText(worker_address_detail);
                                    binding.input14.setText(worker_phone);
                                    binding.input15.setText(worker_email);

                                    Glide.with(mContext).load(owner_sign)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .into(binding.ownerSign);
                                    Glide.with(mContext).load(worker_sign)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .into(binding.workerSign);
                                }
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
                                mAdapter = new ContractTermAdapter(mContext, mList,1);
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

    private void ChangeSelect0102(int i){
        binding.select01.setBackgroundResource(R.drawable.default_gray_round);
        binding.select01Round.setBackgroundResource(R.drawable.select_empty_round);
        binding.select01tv.setTextColor(Color.parseColor("#000000"));

        binding.select02.setBackgroundResource(R.drawable.default_gray_round);
        binding.select02Round.setBackgroundResource(R.drawable.select_empty_round);
        binding.select02tv.setTextColor(Color.parseColor("#000000"));
        select0102 = i;
        if(i == 1){
            binding.select01.setBackgroundResource(R.drawable.default_select_round);
            binding.select01Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select01tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }else if(i == 2){
            binding.select02.setBackgroundResource(R.drawable.default_select_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select02tv.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));

        }
    }
    private void ChangeSize010203(int i){
        binding.sizeBox01.setBackgroundResource(R.drawable.default_gray_round);
        binding.sizeRound01.setBackgroundResource(R.drawable.select_empty_round);
        binding.sizetv01.setTextColor(Color.parseColor("#000000"));

        binding.sizeBox02.setBackgroundResource(R.drawable.default_gray_round);
        binding.sizeRound02.setBackgroundResource(R.drawable.select_empty_round);
        binding.sizetv02.setTextColor(Color.parseColor("#000000"));

        binding.sizeBox03.setBackgroundResource(R.drawable.default_gray_round);
        binding.sizeRound03.setBackgroundResource(R.drawable.select_empty_round);
        binding.sizetv03.setTextColor(Color.parseColor("#000000"));
        size010203 = i;
        if(i == 1){
            binding.sizeBox01.setBackgroundResource(R.drawable.default_select_round);
            binding.sizeRound01.setBackgroundResource(R.drawable.ic_full_round);
            binding.sizetv01.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }else if(i == 2){
            binding.sizeBox02.setBackgroundResource(R.drawable.default_select_round);
            binding.sizeRound02.setBackgroundResource(R.drawable.ic_full_round);
            binding.sizetv02.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }else if(i == 3){
            binding.sizeBox03.setBackgroundResource(R.drawable.default_select_round);
            binding.sizeRound03.setBackgroundResource(R.drawable.ic_full_round);
            binding.sizetv03.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
        }
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
