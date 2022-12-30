package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.ContractPayInterface;
import com.krafte.nebworks.dataInterface.ContractidInterface;
import com.krafte.nebworks.dataInterface.TermInputInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd05Binding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddContractPage05 extends AppCompatActivity {
    private ActivityContractAdd05Binding binding;
    private final static String TAG = "AddContractPage05";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractAdd05Binding.inflate(getLayoutInflater());
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

        setBtnEvent();
    }

    @Override
    public void onResume(){
        super.onResume();
        getContractId();
    }

    String paytype = "";

    String result = "";
    final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    String payment = "";

    String pay_conference = "0";
    String pay_loop = "";
    boolean bokji01 = false;
    boolean bokji02 = false;
    boolean bokji03 = false;
    boolean bokji04 = false;
    List<String> bokji = new ArrayList<>();
    String AddExp = "";
    private void setBtnEvent(){
        /*급여 지급방식*/
        ArrayList<String> stringCategory2 = new ArrayList<>();
        stringCategory2.add("근로자에게 직접지급");
        stringCategory2.add("근로자명의 예금통장에 입금");

        ArrayAdapter<String> select_filter2 = new ArrayAdapter<>(mContext, R.layout.dropdown_item_list, stringCategory2);
        binding.payType.setAdapter(select_filter2);
        binding.payType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                binding.pay.setText(stringCategory2.get(i));
                dlog.i("i : " + stringCategory2.get(i));
                paytype = String.valueOf(i);
                binding.payTypeTv.setText(stringCategory2.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                paytype = "0";
            }
        });

        /*급여액*/
        binding.payment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",", "")));
                    binding.payment.setText(result);
                    binding.payment.setSelection(result.length());
                }
                payment = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.payConference.setOnClickListener(v -> {
            if(pay_conference.equals("0")){
                pay_conference = "1";
                binding.payConferenceRound.setBackgroundResource(R.drawable.resize_service_on);
            }else{
                pay_conference = "0";
                binding.payConferenceRound.setBackgroundResource(R.drawable.resize_service_off);
            }
        });

        binding.bokjiBox01.setOnClickListener(v -> {
            if(!bokji01){
                bokji01 = true;
                binding.bokjiRound01.setBackgroundResource(R.drawable.ic_full_round);
                bokji.add("식사제공");
            }else{
                bokji01 = false;
                binding.bokjiRound01.setBackgroundResource(R.drawable.ic_empty_round);
                bokji.remove("식사제공");
            }
        });

        binding.bokjiBox02.setOnClickListener(v -> {
            if(!bokji02){
                bokji02 = true;
                binding.bokjiRound02.setBackgroundResource(R.drawable.ic_full_round);
                bokji.add("4대보험");
            }else{
                bokji02 = false;
                binding.bokjiRound02.setBackgroundResource(R.drawable.ic_empty_round);
                bokji.remove("4대보험");
            }
        });

        binding.bokjiBox03.setOnClickListener(v -> {
            if(!bokji03){
                bokji03 = true;
                binding.bokjiRound03.setBackgroundResource(R.drawable.ic_full_round);
                bokji.add("교통비지원");
            }else{
                bokji03 = false;
                binding.bokjiRound03.setBackgroundResource(R.drawable.ic_empty_round);
                bokji.remove("교통비지원");
            }
        });

        binding.bokjiBox04.setOnClickListener(v -> {
            if(!bokji04){
                bokji04 = true;
                binding.bokjiRound04.setBackgroundResource(R.drawable.ic_full_round);
                bokji.add("인센티브");
            }else{
                bokji04 = false;
                binding.bokjiRound04.setBackgroundResource(R.drawable.ic_empty_round);
                bokji.remove("인센티브");
            }
        });

        binding.next.setOnClickListener(v -> {
             if(DataCheck()){
                 SaveContractPay();
             }
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
    String strBokji = "";
    private boolean DataCheck(){
        payment = binding.payment.getText().toString();
        pay_loop = binding.payLoop.getText().toString();
        AddExp = binding.input01.getText().toString();
        strBokji = String.valueOf(bokji).replace("[","").replace("]","").replace(" ","");
        dlog.i("-----DataCheck-----");
        dlog.i("contract_id : "     + contract_id);
        dlog.i("paytype : "         + paytype);
        dlog.i("payment : "         + payment);
        dlog.i("pay_conference : "  + pay_conference);
        dlog.i("pay_loop : "        + pay_loop);
        dlog.i("strBokji : "        + strBokji);
        dlog.i("AddExp : "          + AddExp);
        dlog.i("-----DataCheck-----");
        if(paytype.isEmpty()){
            Toast_Nomal("급여지급방식을 선택해주세요.");
            return false;
        }else if(payment.isEmpty()){
            Toast_Nomal("급여 액수를 입력해주세요.");
            return false;
        }else if(pay_conference.isEmpty()){
            Toast_Nomal("급여 액수를 입력해주세요.");
            return false;
        }else if(pay_loop.isEmpty()){
            Toast_Nomal("급여 정산일을 입력해주세요.");
            return false;
        }else{
            return true;
        }
    }


    public void SaveContractPay() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractPayInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractPayInterface api = retrofit.create(ContractPayInterface.class);
        Call<String> call = api.getData(contract_id, paytype, payment, pay_conference, pay_loop, strBokji, AddExp);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("SaveWorkPartTime Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if(jsonResponse.replace("\"","").equals("success")){
                                    Toast_Nomal("급여 기본사항이 업데이트 완료되었습니다.");
                                    InputTerm("근로자가 무단 결근 2일 이상 하거나 월 2일 이상\n결근하는 경우 근로계약을 해지 할 수 있음");
                                    pm.AddContractPage06(mContext);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    String contract_id = "";
    public void getContractId() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractidInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractidInterface api = retrofit.create(ContractidInterface.class);
        Call<String> call = api.getData(place_id, worker_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("SaveWorkPartTime Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        try {
                            JSONArray Response = new JSONArray(response.body());
                            contract_id = Response.getJSONObject(0).getString("id");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    private void InputTerm(String write_term){
        dlog.i("------setTermList------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("write_term : " + write_term);
        dlog.i("------setTermList------");
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
//                            try {
//
//                            } catch(Exception e){
//                                e.printStackTrace();
//                            }
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

    @Override
    public void onBackPressed(){

        if(!shardpref.getString("progress_pos","").isEmpty()){
            pm.ContractFragment(mContext);
        }else{
            super.onBackPressed();
        }
        shardpref.remove("progress_pos");
    }

    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
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
