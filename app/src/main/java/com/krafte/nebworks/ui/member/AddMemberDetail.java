package com.krafte.nebworks.ui.member;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.krafte.nebworks.dataInterface.GetMemberDetailInterface;
import com.krafte.nebworks.dataInterface.GetMemberOtherInterface;
import com.krafte.nebworks.dataInterface.PlaceMemberInsertDetail;
import com.krafte.nebworks.dataInterface.PlaceMemberInsertOther;
import com.krafte.nebworks.dataInterface.PlaceMemberUpdateBasic;
import com.krafte.nebworks.databinding.ActivityAddmemberDetailBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddMemberDetail extends AppCompatActivity {
    private ActivityAddmemberDetailBinding binding;
    private final static String TAG = "AddMemberDetail";
    Context mContext;

    PreferenceHelper shardpref;

    //Shared
    String place_id = "";
    String USER_INFO_NAME = "";
    String USER_INFO_PHONE = "";
    String USER_LOGIN_METHOD = "";
    String USER_INFO_ID = "";

    //other
    boolean check = false;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    DatePickerDialog datePickerDialog;

    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    int Hour = 0;
    int Minute = 0;
    int Sec = 0;
    String getDatePicker = "";
    String getYMPicker = "";
    String result = "";

    //Parameter
    //--BasicInfo
    String mem_id = "";
    String mem_account = "";
    String mem_name = "";
    String mem_phone = "";
    String mem_gender = "";
    String mem_jumin = "";
    String mem_kind = "";
    String mem_join_date = "";

    //--DetailInfo
    String mem_state = "";
    String mem_jikgup = "";
    String mem_paykind = "";
    String mem_pay = "";
    String mem_worktime = "";
    String mem_workhour = "0";
    String mem_task = "";
    String mem_age = "";
    String mem_email = "";
    String mem_address = "";
    String mem_introduce = "";
    String mem_career = "";

    //점주의 직접입력인지 초대된 직원의 상세정보 입력인지 구분
    int input_kind = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_account_delete);
        binding = ActivityAddmemberDetailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        setBtnEvent();

        shardpref = new PreferenceHelper(mContext);
        place_id = shardpref.getString("place_id", "");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE", "");
        USER_LOGIN_METHOD = shardpref.getString("USER_LOGIN_METHOD", "");
        Log.i(TAG, "USER_INFO_NAME = " + USER_INFO_NAME);
        Log.i(TAG, "USER_INFO_PHONE = " + USER_INFO_PHONE);
        Log.i(TAG, "USER_LOGIN_METHOD = " + USER_LOGIN_METHOD);

        mem_id = shardpref.getString("mem_id", "");
        mem_name = shardpref.getString("mem_name", "");
        mem_account = shardpref.getString("mem_account", "");
        mem_phone = shardpref.getString("mem_phone", "");
        mem_gender = shardpref.getString("mem_gender", "");
        mem_jumin = shardpref.getString("mem_jumin", "");
        mem_kind = shardpref.getString("mem_kind", "");
        mem_join_date = shardpref.getString("mem_join_date", "");
        mem_state = shardpref.getString("mem_state", "");
        mem_jikgup = shardpref.getString("mem_jikgup", "");
        mem_pay = shardpref.getString("mem_pay", "");

        dlog.i("mem_phone : " + mem_phone);
        dlog.i("mem_account : " + mem_account);
        if (mem_account.equals(mem_phone)) {
            //직접추가한 직원
            input_kind = 0;
        } else {
            //초대로 추가한 회원인 직원
            input_kind = 1;
        }
        setInputSetting();
        GetDetailInfo();
        GetOtherInfo();
        setBasicInfo();
        setDetailInfo();
        setOtherInfo();
    }

    private void setInputSetting() {
        if(mem_id.equals(USER_INFO_ID)){
            //작성할 데이터가 관리자 본인일 경우 - 상세,기타데이터 입력 필요 없음
            binding.detailInfoArea.setVisibility(View.GONE);
            binding.otherInfoArea.setVisibility(View.GONE);
            binding.area04.setVisibility(View.GONE);
            //이름
            binding.inputbox01.setEnabled(true);
            binding.inputbox01.setClickable(true);
            //연락처
            binding.inputbox02.setEnabled(true);
            binding.inputbox02.setClickable(true);
            //주민등록번호
            binding.inputbox03.setEnabled(true);
            binding.inputbox03.setClickable(true);
        }else{
            //이름
            binding.inputbox01.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.inputbox01.setEnabled(false);
            binding.inputbox01.setClickable(false);
            //연락처
            binding.inputbox02.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.inputbox02.setEnabled(false);
            binding.inputbox02.setClickable(false);
            //주민등록번호
            binding.inputbox03.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.inputbox03.setEnabled(false);
            binding.inputbox03.setClickable(false);
            //입사날짜
            binding.inputbox04.setBackgroundResource(R.drawable.grayback_gray_round);
            binding.inputbox04.setEnabled(false);
            binding.inputbox04.setClickable(false);
            if(input_kind == 1){
                //--초대로 추가한 회원인 직원의 경우 개인정보는 수정할 수 없도록 입력을 막는다
                //나이
                binding.inputbox07.setBackgroundResource(R.drawable.grayback_gray_round);
                binding.inputbox07.setEnabled(false);
                binding.inputbox07.setClickable(false);
                //이메일
                binding.inputbox08.setBackgroundResource(R.drawable.grayback_gray_round);
                binding.inputbox08.setEnabled(false);
                binding.inputbox08.setClickable(false);
                //주소
                binding.inputbox09.setBackgroundResource(R.drawable.grayback_gray_round);
                binding.inputbox09.setEnabled(false);
                binding.inputbox09.setClickable(false);
                //자기소개
                binding.inputbox10.setBackgroundResource(R.drawable.grayback_gray_round);
                binding.inputbox10.setEnabled(false);
                binding.inputbox10.setClickable(false);
                //경력및학력
                binding.inputbox11.setBackgroundResource(R.drawable.grayback_gray_round);
                binding.inputbox11.setEnabled(false);
                binding.inputbox11.setClickable(false);
            }else{
                //나이
                binding.inputbox07.setBackgroundResource(R.drawable.default_gray_round);
                binding.inputbox07.setEnabled(true);
                binding.inputbox07.setClickable(true);
                //이메일
                binding.inputbox08.setBackgroundResource(R.drawable.default_gray_round);
                binding.inputbox08.setEnabled(true);
                binding.inputbox08.setClickable(true);
                //주소
                binding.inputbox09.setBackgroundResource(R.drawable.default_gray_round);
                binding.inputbox09.setEnabled(true);
                binding.inputbox09.setClickable(true);
                //자기소개
                binding.inputbox10.setBackgroundResource(R.drawable.default_gray_round);
                binding.inputbox10.setEnabled(true);
                binding.inputbox10.setClickable(true);
                //경력및학력
                binding.inputbox11.setBackgroundResource(R.drawable.default_gray_round);
                binding.inputbox11.setEnabled(true);
                binding.inputbox11.setClickable(true);
            }

        }
    }

    private void setBasicInfo() {
        //---기본정보
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(mContext, (view, year, month, dayOfMonth) -> {
            Year = String.valueOf(year);
            Month = String.valueOf(month + 1);
            Day = String.valueOf(dayOfMonth);
            Day = Day.length() == 1 ? "0" + Day : Day;
            Month = Month.length() == 1 ? "0" + Month : Month;
            binding.inputbox04.setText(year + "-" + Month + "-" + Day);
            getYMPicker = binding.inputbox04.getText().toString().substring(0, 7);
        }, mYear, mMonth, mDay);

        binding.inputbox01.setText(mem_name);
        binding.inputbox01.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_name = s.toString();
            }
        });

        binding.inputbox02.setText(mem_phone);
        binding.inputbox02.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_phone = s.toString();
            }
        });

        binding.inputbox03.setText(mem_jumin.equals("null")?"":mem_jumin);
        binding.inputbox03.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_jumin = s.toString();
            }
        });

        binding.inputbox04.setText(mem_join_date);
        binding.inputbox04.setOnClickListener(v -> {
            datePickerDialog.show();
        });
        //---기본정보
    }

    boolean select01TF = false;
    boolean select02TF = false;

    final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    private void setDetailInfo() {
        binding.select01Box.setOnClickListener(v -> {
            binding.select02Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select02.setBackgroundResource(R.drawable.select_empty_round);
            if (!select01TF) {
                mem_state = "1";
                binding.select01Box.setBackgroundResource(R.drawable.default_select_round);
                binding.select01.setBackgroundResource(R.drawable.select_full_round);
            } else {
                mem_state = "0";
                binding.select01Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select01.setBackgroundResource(R.drawable.select_empty_round);
            }
        });
        binding.select02Box.setOnClickListener(v -> {
            binding.select01.setBackgroundResource(R.drawable.select_empty_round);
            binding.select01Box.setBackgroundResource(R.drawable.default_gray_round);
            if (!select02TF) {
                mem_state = "2";
                binding.select02Box.setBackgroundResource(R.drawable.default_select_round);
                binding.select02.setBackgroundResource(R.drawable.select_full_round);
            } else {
                mem_state = "0";
                binding.select02Box.setBackgroundResource(R.drawable.default_gray_round);
                binding.select02.setBackgroundResource(R.drawable.select_empty_round);
            }
        });

        /*직급*/
        ArrayList<String> stringCategory1 = new ArrayList<>();
        stringCategory1.add("알바");
        stringCategory1.add("정직원");
        stringCategory1.add("매니저");
        stringCategory1.add("기타");

        ArrayAdapter<String> select_filter1 = new ArrayAdapter<>(mContext, R.layout.dropdown_item_list, stringCategory1);
        binding.jikgupSpinner.setAdapter(select_filter1);
        binding.jikgupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                binding.jikgup.setText(stringCategory1.get(i));
                dlog.i("i : " + stringCategory1.get(i));
                mem_jikgup = stringCategory1.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                binding.jikgup.setText("알바");
                mem_jikgup = "알바";
            }
        });

        /*급여 지급방식*/
        ArrayList<String> stringCategory2 = new ArrayList<>();
        stringCategory2.add("일급");
        stringCategory2.add("시급");
        stringCategory2.add("주급");
        stringCategory2.add("월급");

        ArrayAdapter<String> select_filter2 = new ArrayAdapter<>(mContext, R.layout.dropdown_item_list, stringCategory2);
        binding.paySpinner.setAdapter(select_filter2);
        binding.paySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                binding.pay.setText(stringCategory2.get(i));
                dlog.i("i : " + stringCategory2.get(i));
                mem_paykind = stringCategory2.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                binding.pay.setText("근로자에게 직접지급");
                mem_paykind = "선택";
            }
        });

        /*급여액*/
        binding.inputbox05.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(result)) {
                    result = decimalFormat.format(Double.parseDouble(s.toString().replaceAll(",", "")));
                    binding.inputbox05.setText(result);
                    binding.inputbox05.setSelection(result.length());
                }
                mem_pay = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*근무시간*/
        binding.selectArea01.setOnClickListener(v -> {
            mem_worktime = binding.select01tvtime.getText().toString();
            ChangeSelectTime(1);
        });
        binding.selectArea02.setOnClickListener(v -> {
            mem_worktime = binding.select02tvtime.getText().toString();
            ChangeSelectTime(2);
        });
        binding.selectArea03.setOnClickListener(v -> {
            mem_worktime = binding.select03tvtime.getText().toString();
            ChangeSelectTime(3);
        });

        /*주요직무*/
        binding.inputbox06.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_task = s.toString();
            }
        });

    }

    private void setOtherInfo() {
        //-- 기타정보 시작
        /*나이*/
        binding.inputbox07.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_age = s.toString();
            }
        });

        /*이메일*/
        binding.inputbox08.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_email = s.toString();
            }
        });

        /*주소*/
        binding.inputbox09.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_address = s.toString();
            }
        });


        /*자기소개*/
        binding.inputbox10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_introduce = s.toString();
            }
        });

        /*경력 및 학력*/
        binding.inputbox11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mem_career = s.toString();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        shardpref.remove("mem_id");
        shardpref.remove("mem_name");
        shardpref.remove("mem_phone");
        shardpref.remove("mem_gender");
        shardpref.remove("mem_jumin");
        shardpref.remove("mem_kind");
        shardpref.remove("mem_join_date");
        shardpref.remove("mem_state");
        shardpref.remove("mem_jikgup");
        shardpref.remove("mem_pay");
    }

    private void ChangeSelectTime(int i){
        binding.select01time.setBackgroundResource(R.drawable.select_empty_round);
        binding.select02time.setBackgroundResource(R.drawable.select_empty_round);
        binding.select03time.setBackgroundResource(R.drawable.select_empty_round);

        binding.selectArea01.setBackgroundResource(R.drawable.default_gray_round);
        binding.selectArea02.setBackgroundResource(R.drawable.default_gray_round);
        binding.selectArea03.setBackgroundResource(R.drawable.default_gray_round);

        if(i == 1){
            binding.selectArea01.setBackgroundResource(R.drawable.default_select_round);
            binding.select01time.setBackgroundResource(R.drawable.select_full_round);
        }else if(i == 2){
            binding.selectArea02.setBackgroundResource(R.drawable.default_select_round);
            binding.select02time.setBackgroundResource(R.drawable.select_full_round);
        }else if(i == 3){
            binding.selectArea03.setBackgroundResource(R.drawable.default_select_round);
            binding.select03time.setBackgroundResource(R.drawable.select_full_round);
        }
    }

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });

        binding.addMemberTopBtn.setOnClickListener(v -> {
            if(input_kind == 0){
                dlog.i("mem_id.equals(USER_INFO_ID)2 : " + mem_id.equals(USER_INFO_ID));
                if (SaveCheck() && SaveCheckOtherInfo()) {
                    dlog.i("addMemberBtn SaveCheck 1 : " + SaveCheck());
                    UpdateDirectMemberBasic();
                }
            }else{
                dlog.i("mem_id.equals(USER_INFO_ID) : " + mem_id.equals(USER_INFO_ID));
                if(mem_id.equals(USER_INFO_ID)){
                    if (SaveCheck() && SaveCheckOtherInfo()) {
                        dlog.i("addMemberBtn SaveCheck 2 : " + SaveCheck());
                        UpdateDirectMemberBasic();
                    }
                }else{
                    if(SaveCheckDetail()){
                        AddMemberDetail();
                    }
                }

            }
        });

        binding.addMemberBtn.setOnClickListener(v -> {
            if(input_kind == 0){
                if (SaveCheck() && SaveCheckOtherInfo()) {
                    dlog.i("addMemberBtn SaveCheck 1 : " + SaveCheck());
                    UpdateDirectMemberBasic();
                }
            }else{
                if(mem_id.equals(USER_INFO_ID)){
                    if (SaveCheck() && SaveCheckOtherInfo()) {
                        dlog.i("addMemberBtn SaveCheck 2 : " + SaveCheck());
                        UpdateDirectMemberBasic();
                    }
                }else{
                    if(SaveCheckDetail()){
                        AddMemberDetail();
                    }
                }

            }
        });

        binding.inputbox04.setOnClickListener(v -> {
            datePickerDialog.show();
        });
    }

    private boolean SaveCheck() {
        //회원 기본정보 체크
        mem_join_date = binding.inputbox04.getText().toString();
        if(mem_id.equals(USER_INFO_ID)){
            //상세정보 체크
            dlog.i("------SaveCheck------");
            dlog.i("매장ID : " + place_id);
            dlog.i("이름 : " + mem_name);
            dlog.i("전화번호 : " + mem_phone);
            dlog.i("주민번호 : " + mem_jumin);
            dlog.i("초대 승인상태 : " + mem_kind);
            dlog.i("입사날짜 : " + mem_join_date);
            dlog.i("------SaveCheck------");
            return true;
        }else{
            if (place_id.isEmpty()) {
                Toast.makeText(mContext, "매장 ID가 저장되어있지 않습니다, 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                pm.PlaceList(mContext);
                return false;
            } else if (mem_name.isEmpty()) {
                Toast.makeText(mContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            } else if (mem_phone.isEmpty()) {
                Toast.makeText(mContext, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            } else if (mem_jumin.isEmpty()) {
                Toast.makeText(mContext, "주민번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            } else if (mem_join_date.isEmpty()) {
                Toast.makeText(mContext, "입사일자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                //상세정보 체크
                dlog.i("------SaveCheck------");
                dlog.i("매장ID : " + place_id);
                dlog.i("이름 : " + mem_name);
                dlog.i("전화번호 : " + mem_phone);
                dlog.i("주민번호 : " + mem_jumin);
                dlog.i("초대 승인상태 : " + mem_kind);
                dlog.i("입사날짜 : " + mem_join_date);
                dlog.i("------SaveCheck------");
                return true;
            }
        }
    }
    private boolean SaveCheckOtherInfo(){
        dlog.i("------SaveCheckOtherInfo------");
        dlog.i("나이 : " + mem_age);
        dlog.i("이메일 : " + mem_email);
        dlog.i("주소 : " + mem_address);
        dlog.i("자기소개 : " + mem_introduce);
        dlog.i("경력및학력 : " + mem_career);
        dlog.i("------SaveCheckOtherInfo------");
        return true;
    }
    private boolean SaveCheckDetail() {
        mem_workhour = binding.inputbox12.getText().toString();
        dlog.i("------SaveCheckDetail------");
        dlog.i("매장ID : " + place_id);
        dlog.i("재직상태 : " + mem_state);
        dlog.i("직급 : " + mem_jikgup);
        dlog.i("급여지급방식 : " + mem_paykind);
        dlog.i("급여액 : " + mem_pay);
        dlog.i("주야간 : " + mem_worktime);
        dlog.i("근무시간 : " + mem_workhour);
        dlog.i("주요직무 : " + mem_task);
        dlog.i("------SaveCheckDetail------");
        if(mem_paykind.equals("선택")){
            Toast_Nomal("급여지급 방식을 선택해주세요.");
            return false;
        } else if(mem_worktime.isEmpty()){
            Toast_Nomal("주야간을 입력해주세요.");
            return false;
        } else if(mem_workhour.equals("0")){
            Toast_Nomal("근무시간을 입력해주세요.");
            return false;
        } else{
            return true;
        }
    }
    private void UpdateDirectMemberBasic(){
        //직접 입력직원 기본정보 업데이트
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberUpdateBasic.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberUpdateBasic api = retrofit.create(PlaceMemberUpdateBasic.class);
        Call<String> call = api.getData(place_id, mem_id, mem_name, mem_phone, mem_jumin,mem_kind, mem_join_date);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UpdateDirectMemberBasic jsonResponse length : " + response.body().length());
                            dlog.i("UpdateDirectMemberBasic jsonResponse : " + response.body());
                            if (response.body().replace("\"", "").equals("success")) {
                                if(!mem_id.equals(USER_INFO_ID)){
                                    AddDirectMemberOther();
                                }else{
                                    Toast_Nomal("관리자정보가 업데이트 되었습니다.");
                                    pm.MemberManagement(mContext);
                                }
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast_Nomal("기본정보 업데이트 에러  = " + t.getMessage());
            }
        });
    }

    private void AddDirectMemberOther(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberInsertOther.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberInsertOther api = retrofit.create(PlaceMemberInsertOther.class);
        Call<String> call = api.getData(place_id, mem_id, mem_age, mem_email, mem_address, mem_introduce, mem_career);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("AddDirectMemberOther jsonResponse length : " + response.body().length());
                            dlog.i("AddDirectMemberOther jsonResponse : " + response.body());
                            if (response.body().replace("\"", "").equals("success")) {
                                if(SaveCheckDetail()){
                                    AddMemberDetail();
                                }
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast_Nomal("기타정보 업데이트 에러  = " + t.getMessage());
            }
        });
    }

    private void AddMemberDetail() {
        dlog.i("-----AddMemberDetail------");
        dlog.i("place_id : " + place_id);
        dlog.i("mem_id : " + mem_id);
        dlog.i("mem_state : " + mem_state);
        dlog.i("mem_jikgup : " + mem_jikgup);
        dlog.i("mem_paykind : " + mem_paykind);
        dlog.i("mem_pay : " + mem_pay);
        dlog.i("mem_worktime : " + mem_worktime);
        dlog.i("mem_workhour : " + mem_workhour);
        dlog.i("mem_task : " + mem_task);
        dlog.i("-----AddMemberDetail------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceMemberInsertDetail.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceMemberInsertDetail api = retrofit.create(PlaceMemberInsertDetail.class);
        Call<String> call = api.getData(place_id, mem_id, mem_state, mem_jikgup, mem_paykind, mem_pay, mem_worktime, mem_workhour, mem_task);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("AddMemberDetail jsonResponse length : " + response.body().length());
                            dlog.i("AddMemberDetail jsonResponse : " + response.body());
                            if (response.body().replace("\"", "").equals("success")) {
                                Toast_Nomal("직원정보가 업데이트 되었습니다.");
                                pm.MemberManagement(mContext);
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast_Nomal("상세정보 업데이트 에러  = " + t.getMessage());
            }
        });
    }

    private void GetDetailInfo(){
        dlog.i("------GetDetailInfo------");
        dlog.i("place_id : " + place_id);
        dlog.i("mem_id : " + mem_id);
        dlog.i("------GetDetailInfo------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetMemberDetailInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        GetMemberDetailInterface api = retrofit.create(GetMemberDetailInterface.class);
        Call<String> call = api.getData(place_id,mem_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetDetailInfo jsonResponse length : " + response.body().length());
                            dlog.i("GetDetailInfo jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    String state    = Response.getJSONObject(0).getString("state").trim();
                                    String jikgup   = Response.getJSONObject(0).getString("jikgup").trim();
                                    String paykind  = Response.getJSONObject(0).getString("paykind").trim();
                                    String pay      = Response.getJSONObject(0).getString("pay").trim();
                                    String worktime = Response.getJSONObject(0).getString("worktime").trim();
                                    String workhour = Response.getJSONObject(0).getString("workhour").trim();
                                    String task     = Response.getJSONObject(0).getString("task").trim();

                                    dlog.i("GetDetailInfo state : " + state);
                                    dlog.i("GetDetailInfo jikgup : " + jikgup);
                                    dlog.i("GetDetailInfo paykind : " + paykind);
                                    dlog.i("GetDetailInfo pay : " + pay);
                                    dlog.i("GetDetailInfo worktime : " + worktime);
                                    dlog.i("GetDetailInfo workhour : " + workhour);
                                    dlog.i("GetDetailInfo task : " + task);
                                    if(state.equals("1")){
                                        mem_state = "1";
                                        select01TF = false;
                                        binding.select01.setBackgroundResource(R.drawable.select_full_round);
                                        binding.select01Box.setBackgroundResource(R.drawable.default_select_round);
                                    }else if(state.equals("2")){
                                        mem_state = "2";
                                        select02TF = false;
                                        binding.select02.setBackgroundResource(R.drawable.select_full_round);
                                        binding.select02Box.setBackgroundResource(R.drawable.default_select_round);
                                    }

                                    switch (jikgup){
                                        case "알바" :
                                            binding.jikgupSpinner.setSelection(0);
                                            break;
                                        case "정직원" :
                                            binding.jikgupSpinner.setSelection(1);
                                            break;
                                        case "매니저" :
                                            binding.jikgupSpinner.setSelection(2);
                                            break;
                                        case "기타" :
                                            binding.jikgupSpinner.setSelection(3);
                                            break;
                                    }

                                    switch (paykind){
                                        case "일급" :
                                            binding.paySpinner.setSelection(0);
                                            break;
                                        case "시급" :
                                            binding.paySpinner.setSelection(1);
                                            break;
                                        case "주급" :
                                            binding.paySpinner.setSelection(2);
                                            break;
                                        case "월급" :
                                            binding.paySpinner.setSelection(3);
                                            break;
                                    }

                                    binding.inputbox05.setText(pay);

                                    switch (worktime){
                                        case "주간" :
                                            mem_worktime = "주간";
                                            ChangeSelectTime(1);
                                            break;
                                        case "야간" :
                                            mem_worktime = "야간";
                                            ChangeSelectTime(2);
                                            break;
                                        case "종일" :
                                            mem_worktime = "종일";
                                            ChangeSelectTime(3);
                                            break;
                                    }
                                    binding.inputbox06.setText(task);
                                    mem_workhour = workhour;
                                    binding.inputbox12.setText(workhour);
                                }
                            } catch (JSONException e) {
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

    private void GetOtherInfo(){
        dlog.i("------GetDetailInfo------");
        dlog.i("place_id : " + place_id);
        dlog.i("mem_id : " + mem_id);
        dlog.i("------GetDetailInfo------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetMemberOtherInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        GetMemberOtherInterface api = retrofit.create(GetMemberOtherInterface.class);
        Call<String> call = api.getData(place_id,mem_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetDetailInfo jsonResponse length : " + response.body().length());
                            dlog.i("GetDetailInfo jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    String age          = Response.getJSONObject(0).getString("age");
                                    String email        = Response.getJSONObject(0).getString("email");
                                    String address      = Response.getJSONObject(0).getString("address");
                                    String introduce    = Response.getJSONObject(0).getString("introduce");
                                    String career       = Response.getJSONObject(0).getString("career");

                                    binding.inputbox07.setText(age);
                                    binding.inputbox08.setText(email);
                                    binding.inputbox09.setText(address);
                                    binding.inputbox10.setText(introduce);
                                    binding.inputbox11.setText(career);

                                }
                            } catch (JSONException e) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
