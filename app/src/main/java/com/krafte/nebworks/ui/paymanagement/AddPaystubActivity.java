package com.krafte.nebworks.ui.paymanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.dataInterface.paymanaInterface;
import com.krafte.nebworks.databinding.ActivityAddpaystubalbaBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddPaystubActivity extends AppCompatActivity {
    private ActivityAddpaystubalbaBinding binding;
    private static final String TAG = "AddPaystubActivityAlba";
    Context mContext;

    private final DateCurrent dc = new DateCurrent();

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String place_owner_id = "";

    String select_month = "";
    String select_user_id = "";
    String select_place_id = "";
    String select_user_name = "";
    String select_total_payment = "";
    String select_workday = "";
    String select_total_workhour = "";
    String select_payment = "";
    String select_GET_DATE = "";
    

    float insurance01p = 0;//국민연금 퍼센트
    float insurance02p = 0;//건강보험 퍼센트
    float insurance03p = 0;//고용보험 퍼센트
    float insurance04p = 0;//장기요양보험료 퍼센트

    String insurance1 = "";
    String insurance2 = "";
    String insurance3 = "";
    String insurance4 = "";

    //Other
    /*라디오 버튼들 boolean*/
    Drawable icon_off;
    Drawable icon_on;
    PageMoveClass pm = new PageMoveClass();
    int paging_position = 0;
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    String get_edit_expenses = "0";
    String get_edit_overwork = "0";
    String get_edit_overworkhour = "0";
    String get_edit_mealpay = "0";

    String select0102 = "직접입력";
    String select0304 = "포함";
    String select0506 = "공제";

    String get_employee_memo = "";

    String result = "";
    final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    String sendTopic = "";
    String sendToken = "";
    boolean rcvchannelId2 = false;
    Handler mHandler;
    DBConnection dbConnection = new DBConnection();
    String click_action = "";
    String message = "";
    String topic = "";
    int AllPayment = 0;


    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_addpaystubalba);
        binding = ActivityAddpaystubalbaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        icon_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_empty_round);
        icon_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_full_round);

        //Singleton Area
        USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
        USER_INFO_NAME  = UserCheckData.getInstance().getUser_name();
        USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","");
        place_owner_id  = PlaceCheckData.getInstance().getPlace_owner_id();

        //shardpref Area

        //-------------------
        select_month = shardpref.getString("select_month","");
        select_user_id = shardpref.getString("select_user_id","");
        select_place_id = shardpref.getString("select_place_id","");
        select_user_name = shardpref.getString("select_user_name","");
        select_total_payment = shardpref.getString("select_total_payment","");
        select_workday = shardpref.getString("select_workday","");
        select_total_workhour = shardpref.getString("select_total_workhour","");
        select_payment = shardpref.getString("select_payment","");
        select_GET_DATE = shardpref.getString("select_GET_DATE","");

        shardpref.putString("returnPage", "BusinessApprovalActivity");
        dlog.i("기본급 : " + select_payment);

        setBtnEvent();
        GetInsurancePercent();

        binding.name.setText(select_user_name);
        binding.selectMonth.setText(select_month.substring(6,8) + "월");
        binding.totalWorkDay.setText(select_workday);
        binding.totalWorkHour.setText(select_total_workhour);
        binding.minimumPay.setText("9,160");

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String basic_paytv = myFormatter.format(Integer.parseInt(select_total_payment));
        binding.basicPayTv.setText(basic_paytv);
//        String pay = myFormatter.format(Integer.parseInt(select_total_payment));

        AllPayment = Integer.parseInt(select_total_payment);
        insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
        insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
        insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
        insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));
        binding.insurance01.setHint("자동계산시 " + insurance1);//국민연금
        binding.insurance02.setHint("자동계산시 " + insurance2);//건강보험
        binding.insurance03.setHint("자동계산시 " + insurance3);//고용보험
        binding.insurance04.setHint("자동계산시 " + insurance4);//장기요양보험료

        AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                + Integer.parseInt(insurance2.replace(",",""))
                + Integer.parseInt(insurance3.replace(",",""))
                + Integer.parseInt(insurance4.replace(",","")));

        String pay = myFormatter.format(AllPayment);
        binding.allPayment.setText(pay);

        binding.editExpenses.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",","")));
                    binding.editExpenses.setText(result);
                    binding.editExpenses.setSelection(result.length());
                }
                get_edit_expenses = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals("")){
                    AllPayment = Integer.parseInt(select_total_payment) + Integer.parseInt(get_edit_expenses.replace(",","").equals("")?"0":get_edit_expenses.replace(",",""))
                            + Integer.parseInt(get_edit_overwork.replace(",","").equals("")?"0":get_edit_overwork.replace(",",""));
                    dlog.i("AllPayment : " + AllPayment);

                    insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
                    insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
                    insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
                    insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));
                    binding.insurance01.setHint("자동계산시 " + insurance1);//국민연금
                    binding.insurance02.setHint("자동계산시 " + insurance2);//건강보험
                    binding.insurance03.setHint("자동계산시 " + insurance3);//고용보험
                    binding.insurance04.setHint("자동계산시 " + insurance4);//장기요양보험료

                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                            + Integer.parseInt(insurance2.replace(",",""))
                            + Integer.parseInt(insurance3.replace(",",""))
                            + Integer.parseInt(insurance4.replace(",","")));

                    String pay = myFormatter.format(AllPayment);
                    binding.allPayment.setText(pay);
                }
            }
        });

        binding.editOverworkhour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s.toString())) {
                    get_edit_overworkhour = binding.editOverworkhour.getText().toString();
                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    String overwork = binding.editOverworkhour.getText().toString().isEmpty() ? "0" : binding.editOverworkhour.getText().toString();
                    String pay = myFormatter.format(Integer.parseInt(overwork) * (9160 * 1.5));
                    get_edit_overwork = pay;
                    binding.editOverwork.setText(get_edit_overwork);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals("")){
                    AllPayment = Integer.parseInt(select_total_payment) + Integer.parseInt(get_edit_expenses.replace(",","").equals("")?"0":get_edit_expenses.replace(",",""))
                            + Integer.parseInt(get_edit_overwork.replace(",","").equals("")?"0":get_edit_overwork.replace(",",""));


                    insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
                    insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
                    insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
                    insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));
                    binding.insurance01.setHint("자동계산시 " + insurance1);//국민연금
                    binding.insurance02.setHint("자동계산시 " + insurance2);//건강보험
                    binding.insurance03.setHint("자동계산시 " + insurance3);//고용보험
                    binding.insurance04.setHint("자동계산시 " + insurance4);//장기요양보험료

                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                            + Integer.parseInt(insurance2.replace(",",""))
                            + Integer.parseInt(insurance3.replace(",",""))
                            + Integer.parseInt(insurance4.replace(",","")));

                    String pay = myFormatter.format(AllPayment);
                    binding.allPayment.setText(pay);
                }
            }
        });

        binding.editOverwork.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",","")));
                    binding.editOverwork.setText(result);
                    binding.editOverwork.setSelection(result.length());
                }
                get_edit_overwork = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals("")){
                    AllPayment = Integer.parseInt(select_total_payment) + Integer.parseInt(get_edit_expenses.replace(",","").equals("")?"0":get_edit_expenses.replace(",",""))
                            + Integer.parseInt(get_edit_overwork.replace(",","").equals("")?"0":get_edit_overwork.replace(",",""));


                    insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
                    insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
                    insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
                    insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));
                    binding.insurance01.setHint("자동계산시 " + insurance1);//국민연금
                    binding.insurance02.setHint("자동계산시 " + insurance2);//건강보험
                    binding.insurance03.setHint("자동계산시 " + insurance3);//고용보험
                    binding.insurance04.setHint("자동계산시 " + insurance4);//장기요양보험료

                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                            + Integer.parseInt(insurance2.replace(",",""))
                            + Integer.parseInt(insurance3.replace(",",""))
                            + Integer.parseInt(insurance4.replace(",","")));

                    String pay = myFormatter.format(AllPayment);
                    binding.allPayment.setText(pay);
                }
            }
        });

        binding.employeeMemo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                get_employee_memo = binding.employeeMemo.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.editMealPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",","")));
                    binding.editMealPay.setText(result);
                    binding.editMealPay.setSelection(result.length());
                }
                get_edit_mealpay = s.toString().replace(",","");

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals("")){
                    AllPayment = Integer.parseInt(select_total_payment) + Integer.parseInt(get_edit_expenses.replace(",","").equals("")?"0":get_edit_expenses.replace(",",""))
                            + Integer.parseInt(get_edit_overwork.replace(",","").equals("")?"0":get_edit_overwork.replace(",",""))
                            + Integer.parseInt(get_edit_mealpay.replace(",","").equals("")?"0":get_edit_mealpay.replace(",",""));


                    insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
                    insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
                    insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
                    insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));
                    binding.insurance01.setHint("자동계산시 " + insurance1);//국민연금
                    binding.insurance02.setHint("자동계산시 " + insurance2);//건강보험
                    binding.insurance03.setHint("자동계산시 " + insurance3);//고용보험
                    binding.insurance04.setHint("자동계산시 " + insurance4);//장기요양보험료

                    AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                            + Integer.parseInt(insurance2.replace(",",""))
                            + Integer.parseInt(insurance3.replace(",",""))
                            + Integer.parseInt(insurance4.replace(",","")));

                    String pay = myFormatter.format(AllPayment);
                    binding.allPayment.setText(pay);
                }
            }
        });
    }

    private void setBtnEvent() {
        binding.menu.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PayManagementActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.translate_right2, R.anim.translate_left2);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });


        //초과근무시간 입력여부 true - 직접입력 / false - 시간입력
        binding.select01Box.setOnClickListener(v -> {
            select0102 = "직접입력";
            binding.editOverwork.setClickable(true);
            binding.editOverwork.setEnabled(true);
            binding.editOverworkhour.setClickable(false);
            binding.editOverworkhour.setEnabled(false);
            binding.editOverwork.setText("0");
            binding.overWorkhourArea.setVisibility(View.GONE);
            binding.editOverwork.setVisibility(View.VISIBLE);
            binding.select01.setCompoundDrawablesWithIntrinsicBounds(icon_on,null,null,null);
            binding.select02.setCompoundDrawablesWithIntrinsicBounds(icon_off,null,null,null);
        });
        binding.select02Box.setOnClickListener(v -> {
            select0102 = "시간입력";
            binding.editOverwork.setClickable(false);
            binding.editOverwork.setEnabled(false);
            binding.editOverworkhour.setClickable(true);
            binding.editOverworkhour.setEnabled(true);
            binding.editOverwork.setText("0");
            binding.overWorkhourArea.setVisibility(View.VISIBLE);
            binding.editOverwork.setVisibility(View.VISIBLE);
            binding.select01.setCompoundDrawablesWithIntrinsicBounds(icon_off,null,null,null);
            binding.select02.setCompoundDrawablesWithIntrinsicBounds(icon_on,null,null,null);
        });

        //식대 true - 포함 / false - 미포함
        binding.select03Box.setOnClickListener(v -> {
            select0304 = "포함";
            binding.editMealPay.setVisibility(View.VISIBLE);
            binding.select03.setCompoundDrawablesWithIntrinsicBounds(icon_on,null,null,null);
            binding.select04.setCompoundDrawablesWithIntrinsicBounds(icon_off,null,null,null);

            AllPayment = Integer.parseInt(select_total_payment) + Integer.parseInt(get_edit_expenses.replace(",","").equals("")?"0":get_edit_expenses.replace(",",""))
                    + Integer.parseInt(get_edit_overwork.replace(",","").equals("")?"0":get_edit_overwork.replace(",",""))
                    + Integer.parseInt(get_edit_mealpay.replace(",","").equals("")?"0":get_edit_mealpay.replace(",",""));

            DecimalFormat myFormatter = new DecimalFormat("###,###");
            insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
            insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
            insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
            insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));
            binding.insurance01.setHint("자동계산시 " + insurance1);//국민연금
            binding.insurance02.setHint("자동계산시 " + insurance2);//건강보험
            binding.insurance03.setHint("자동계산시 " + insurance3);//고용보험
            binding.insurance04.setHint("자동계산시 " + insurance4);//장기요양보험료

            AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                    + Integer.parseInt(insurance2.replace(",",""))
                    + Integer.parseInt(insurance3.replace(",",""))
                    + Integer.parseInt(insurance4.replace(",","")));

            String pay = myFormatter.format(AllPayment);
            binding.allPayment.setText(pay);
        });
        binding.select04Box.setOnClickListener(v -> {
            select0304 = "미포함";
            binding.editMealPay.setText("0");
            get_edit_mealpay = "0";
            binding.editMealPay.setVisibility(View.GONE);
            binding.select03.setCompoundDrawablesWithIntrinsicBounds(icon_off,null,null,null);
            binding.select04.setCompoundDrawablesWithIntrinsicBounds(icon_on,null,null,null);
            AllPayment = AllPayment + Integer.parseInt(get_edit_mealpay);
        });

        //4대보험 true - 공제 / false - 미공제
        binding.select05Box.setOnClickListener(v -> {
            select0506 = "공제";
            binding.select05.setCompoundDrawablesWithIntrinsicBounds(icon_on,null,null,null);
            binding.select06.setCompoundDrawablesWithIntrinsicBounds(icon_off,null,null,null);
            binding.gongjeArea.setVisibility(View.VISIBLE);
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                    + Integer.parseInt(insurance2.replace(",",""))
                    + Integer.parseInt(insurance3.replace(",",""))
                    + Integer.parseInt(insurance4.replace(",","")));
            String pay = myFormatter.format(AllPayment);
            binding.allPayment.setText(pay);
        });
        binding.select06Box.setOnClickListener(v -> {
            select0506 = "미공제";
            binding.select05.setCompoundDrawablesWithIntrinsicBounds(icon_off,null,null,null);
            binding.select06.setCompoundDrawablesWithIntrinsicBounds(icon_on,null,null,null);
            binding.gongjeArea.setVisibility(View.GONE);
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            AllPayment = Integer.parseInt(select_total_payment) + Integer.parseInt(get_edit_expenses.replace(",","").equals("")?"0":get_edit_expenses.replace(",",""))
                    + Integer.parseInt(get_edit_overwork.replace(",","").equals("")?"0":get_edit_overwork.replace(",",""));
            String pay = myFormatter.format(AllPayment);
            binding.allPayment.setText(pay);
        });

        binding.sendPaystub.setOnClickListener(v -> {
            BtnOneCircleFun(false);
            DataCheck();
        });
    }


    private void DataCheck(){
        dlog.i("----------------AddPaystubActivityAlba DataCheck----------------");
        get_edit_expenses = get_edit_expenses.replace(",","");
        get_edit_overwork = get_edit_overwork.replace(",","");
        AllPayment = Integer.parseInt(String.valueOf(AllPayment).replace(",",""));
        dlog.i("edit_expenses : " + get_edit_expenses);
        dlog.i("edit_overwork : " + get_edit_overwork);
        dlog.i("edit_overworkhour : " + get_edit_overworkhour);
        dlog.i("select0102 : " + select0102);
        dlog.i("select0304 : " + select0304);
        dlog.i("select0506 : " + select0506);
        dlog.i("employee_memo : " + get_employee_memo);
        dlog.i("ALL PAYMENT 0 : " + Integer.parseInt(select_total_payment) +"+"+ Integer.parseInt(get_edit_expenses.replace(",","")) +"+"+ Integer.parseInt(get_edit_overwork.replace(",","")));
        dlog.i("insurance01p : " + insurance01p);
        dlog.i("insurance02p : " + insurance02p);
        dlog.i("insurance03p : " + insurance03p);
        dlog.i("insurance04p : " + insurance04p);
        dlog.i("binding.insurance01(국민연금) : " + AllPayment * (insurance01p/100));
        dlog.i("binding.insurance02(건강보험) : " + AllPayment * (insurance02p/100));
        dlog.i("binding.insurance03(고용보험) : " + AllPayment * (insurance03p/100));
        dlog.i("binding.insurance04(장기요양보험) : " + AllPayment * (insurance04p/100));

        dlog.i("----------------AddPaystubActivityAlba DataCheck----------------");

        AddPayStubAlba(String.valueOf(AllPayment));
    }

    // 직원 급여 명세서 리스트
    public void AddPayStubAlba(String allPayment) {
        boolean meal_allowance = select0304.equals("포함");
        boolean store_insurance = select0506.equals("공제");

        rc.paymentData_lists.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(paymanaInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        paymanaInterface api = retrofit.create(paymanaInterface.class);
        Call<String> call = api.getData("2",select_place_id, "", select_user_id,select_total_payment,get_edit_expenses,get_edit_overworkhour
                ,get_edit_overwork,String.valueOf(meal_allowance),get_edit_mealpay,String.valueOf(store_insurance),get_employee_memo,allPayment,select_GET_DATE);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                Log.e(TAG, "response 2: " + rc.getBase64decode(response.body()));
                Log.e(TAG, "response 2-1: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(response.body());
//                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + jsonResponse);
                    if(response.body().replace("\"","").equals("success")){
                        pm.PayManagement(mContext);

                        shardpref.remove("select_month");
                        shardpref.remove("select_user_id");
                        shardpref.remove("select_place_id");
                        shardpref.remove("select_user_name");
                        shardpref.remove("select_total_payment");
                        shardpref.remove("select_workday");
                        shardpref.remove("select_total_workhour");
                        shardpref.remove("select_payment");
                        shardpref.remove("select_GET_DATE");
                        String message = "["+select_user_name+"] 님의 " + select_month.substring(6,8) + "월 급여명세서가 도착했습니다.";
                        getUserToken(select_user_id,"1",message);
                        AddPush("급여명세서",message,select_user_id);
                    }else{
                        BtnOneCircleFun(true);
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

    //점주 > 근로자자
   public void getUserToken(String user_id, String type, String message) {
        dlog.i("-----getManagerToken-----");
        dlog.i("user_id : " + user_id);
        dlog.i("type : " + type);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCMSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FCMSelectInterface api = retrofit.create(FCMSelectInterface.class);
        Call<String> call = api.getData(user_id, type);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String jsonResponse = rc.getBase64decode(response.body());
                dlog.i("jsonResponse length : " + jsonResponse.length());
                dlog.i("jsonResponse : " + jsonResponse);
                try {
                    JSONArray Response = new JSONArray(jsonResponse);
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId1 = Response.getJSONObject(0).getString("channel1").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "1", select_place_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
    }
    public void AddPush(String title, String content, String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PushLogInputInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PushLogInputInterface api = retrofit.create(PushLogInputInterface.class);
        Call<String> call = api.getData(select_place_id, "", title, content, place_owner_id, user_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("AddStroeNoti jsonResponse length : " + response.body().length());
                            dlog.i("AddStroeNoti jsonResponse : " + response.body());
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

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "Payment0";
            dlog.i("-----PushFcmSend-----");
            dlog.i("topic : " + topic);
            dlog.i("title : " + title);
            dlog.i("message : " + message);
            dlog.i("token : " + token);
            dlog.i("click_action : " + click_action);
            dlog.i("tag : " + tag);
            dlog.i("place_id : " + place_id);
            dlog.i("-----PushFcmSend-----");
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
//            activity.runOnUiThread(() -> {
//            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void GetInsurancePercent() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(paymanaInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        paymanaInterface api = retrofit.create(paymanaInterface.class);
        Call<String> call = api.getData("4","", "", "","","","","","","","","","","");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("GetInsurancePercent function START");
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + rc.getBase64decode(response.body()));
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
//                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + jsonResponse);
                        try {
                            JSONArray Response = new JSONArray(jsonResponse);

                            insurance01p = Float.parseFloat(Response.getJSONObject(0).getString("insurance01"));//국민연금 퍼센트
                            insurance02p = Float.parseFloat(Response.getJSONObject(0).getString("insurance02"));//건강보험 퍼센트
                            insurance03p = Float.parseFloat(Response.getJSONObject(0).getString("insurance03"));//고용보험 퍼센트
                            insurance04p = Float.parseFloat(Response.getJSONObject(0).getString("insurance04"));//장기요양보험료 퍼센트

                            DecimalFormat myFormatter = new DecimalFormat("###,###");
//        String pay = myFormatter.format(Integer.parseInt(select_total_payment));

                            AllPayment = Integer.parseInt(select_total_payment);
                            insurance1 = myFormatter.format(Math.round((AllPayment * insurance01p)/100));
                            insurance2 = myFormatter.format(Math.round((AllPayment * insurance02p)/100));
                            insurance3 = myFormatter.format(Math.round((AllPayment * insurance03p)/100));
                            insurance4 = myFormatter.format(Math.round((Math.round((AllPayment * insurance02p)/100) * insurance04p)/100));
                            binding.insurance01.setHint("자동계산시 " + insurance1);//국민연금
                            binding.insurance02.setHint("자동계산시 " + insurance2);//건강보험
                            binding.insurance03.setHint("자동계산시 " + insurance3);//고용보험
                            binding.insurance04.setHint("자동계산시 " + insurance4);//장기요양보험료

                            AllPayment = AllPayment - (Integer.parseInt(insurance1.replace(",",""))
                                    + Integer.parseInt(insurance2.replace(",",""))
                                    + Integer.parseInt(insurance3.replace(",",""))
                                    + Integer.parseInt(insurance4.replace(",","")));

                            String pay = myFormatter.format(AllPayment);
                            binding.allPayment.setText(pay);
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

    private void BtnOneCircleFun(boolean tf){
        binding.sendPaystub.setClickable(tf);
        binding.sendPaystub.setEnabled(tf);
    }

}
