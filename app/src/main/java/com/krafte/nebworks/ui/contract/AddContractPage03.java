package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.ContractBasicInterface;
import com.krafte.nebworks.dataInterface.ContractidInterface;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.dataInterface.RegistrSearchInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd03Binding;
import com.krafte.nebworks.ui.WebViewActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddContractPage03 extends AppCompatActivity {
    private ActivityContractAdd03Binding binding;
    private final static String TAG = "AddContractPage01";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";
    String contract_place_id = "";
    String contract_user_id = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();


    String store_address = "";
    String store_addressdetail = "";
    String zipcode = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractAdd03Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        place_id = shardpref.getString("place_id", "0");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
        worker_id = shardpref.getString("worker_id", "0");
        contract_place_id = shardpref.getString("contract_place_id", "0");
        contract_user_id = shardpref.getString("contract_user_id", "0");

        setBtnEvent();
        dlog.i("contract_place_id : " + contract_place_id);
        dlog.i("contract_user_id : " + contract_user_id);
        //basic setting
        ChangeSelect0102(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserCheck();
        GetPlaceList();
    }

    int select0102 = 1;
    int size010203 = 1;
    String owner_name = "";
    String owner_registrnum = "";
    String owner_address = "";
    String owner_address_detail = "";
    String owner_phone = "";
    String owner_email = "";
    int textlength01 = 0;

    private void setBtnEvent() {
        //사업자 구분
        binding.select01.setOnClickListener(v -> {
            ChangeSelect0102(1);
        });
        binding.select02.setOnClickListener(v -> {
            ChangeSelect0102(2);
        });

        //주소
        binding.searchBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
        });

        //사업장 규모
        binding.sizeBox01.setOnClickListener(v -> {
            ChangeSize010203(1);
        });
        binding.sizeBox02.setOnClickListener(v -> {
            ChangeSize010203(2);
        });
        binding.sizeBox03.setOnClickListener(v -> {
            ChangeSize010203(3);
        });

        binding.input02.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.input02.isFocusable() && !s.toString().equals("")) {
                    try {
                        textlength01 = binding.input02.getText().toString().length();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (textlength01 == 3 && before != 1) {
                        binding.input02.setText(binding.input02.getText().toString() + "-");
                        binding.input02.setSelection(binding.input02.getText().length());
                    } else if (textlength01 == 6 && before != 1) {
                        binding.input02.setText(binding.input02.getText().toString() + "-");
                        binding.input02.setSelection(binding.input02.getText().length());
                    } else if (textlength01 == 10 && !binding.input02.getText().toString().contains("-")) {
                        binding.input02.setText(binding.input02.getText().toString().substring(0, 3) + "-" + binding.input02.getText().toString().substring(4, 6) + "-" + binding.input02.getText().toString().substring(6, 10));
                        binding.input02.setSelection(binding.input02.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.next.setOnClickListener(v -> {
            if (DataCheck()) {
                SaveContractBasic();
            }
        });
        binding.backBtn.setOnClickListener(v -> {
            shardpref.remove("progress_pos");
            if (!shardpref.getString("progress_pos", "").isEmpty()) {
                pm.ContractFragment(mContext);
            } else {
                super.onBackPressed();
            }
        });
    }

    private void ChangeSelect0102(int i) {
        binding.select01.setBackgroundResource(R.drawable.default_gray_round);
        binding.select01Round.setBackgroundResource(R.drawable.select_empty_round);
        binding.select01tv.setTextColor(Color.parseColor("#000000"));

        binding.select02.setBackgroundResource(R.drawable.default_gray_round);
        binding.select02Round.setBackgroundResource(R.drawable.select_empty_round);
        binding.select02tv.setTextColor(Color.parseColor("#000000"));
        select0102 = i;
        if (i == 1) {
            binding.select01.setBackgroundResource(R.drawable.default_select_round);
            binding.select01Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select01tv.setTextColor(Color.parseColor("#6395EC"));
        } else if (i == 2) {
            binding.select02.setBackgroundResource(R.drawable.default_select_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select02tv.setTextColor(Color.parseColor("#6395EC"));

        }
    }

    private void ChangeSize010203(int i) {
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
        if (i == 1) {
            binding.sizeBox01.setBackgroundResource(R.drawable.default_select_round);
            binding.sizeRound01.setBackgroundResource(R.drawable.ic_full_round);
            binding.sizetv01.setTextColor(Color.parseColor("#6395EC"));
        } else if (i == 2) {
            binding.sizeBox02.setBackgroundResource(R.drawable.default_select_round);
            binding.sizeRound02.setBackgroundResource(R.drawable.ic_full_round);
            binding.sizetv02.setTextColor(Color.parseColor("#6395EC"));
        } else if (i == 3) {
            binding.sizeBox03.setBackgroundResource(R.drawable.default_select_round);
            binding.sizeRound03.setBackgroundResource(R.drawable.ic_full_round);
            binding.sizetv03.setTextColor(Color.parseColor("#6395EC"));
        }
    }

    //이미지 업로드에 필요한 소스 START
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ADDRESS_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String GetData = data.getExtras().getString("data");
                if (GetData != null) {
//                         data의 정보를 각각 우편번호와 실주소로 나누어 EditText에 표시
                    dlog.i("RESULT_OK 1 : " + GetData.substring(0, 5));
                    dlog.i("RESULT_OK 2 : " + GetData.substring(7));
                    zipcode = GetData.substring(0, 5);
                    binding.input03.setText(GetData.substring(0, 5));
                    binding.input04.setText(GetData.substring(7));
                }
            }
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    public void GetPlaceList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData(contract_place_id, USER_INFO_ID, "0");
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
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(jsonResponse);
                                if (Response.length() != 0) {
                                    String owner_name = Response.getJSONObject(0).getString("owner_name");
                                    String registr_num = Response.getJSONObject(0).getString("registr_num");
                                    String address = Response.getJSONObject(0).getString("address");
                                    String address_detail = Response.getJSONObject(0).getString("address_detail");

                                    binding.input01.setText(owner_name);
                                    binding.input02.setText(registr_num);
                                    binding.input04.setText(address);
                                    binding.input05.setText(address_detail);
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

    public void UserCheck() {
//        dlog.i("---------UserCheck---------");
//        dlog.i("---------UserCheck---------");
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(AllMemberInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        AllMemberInterface api = retrofit.create(AllMemberInterface.class);
//        Call<String> call = api.getData(contract_place_id, USER_INFO_ID);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                dlog.e("UserCheck function START");
//                dlog.e("response 1: " + response.isSuccessful());
//                runOnUiThread(() -> {
//                    if (response.isSuccessful() && response.body() != null) {
//                        String jsonResponse = rc.getBase64decode(response.body());
//                        dlog.i("jsonResponse length : " + jsonResponse.length());
//                        dlog.i("jsonResponse : " + jsonResponse);
//                        try {
//                            //Array데이터를 받아올 때
//                            JSONArray Response = new JSONArray(jsonResponse);
//                            try {
//                                if (Response.length() != 0) {
//                                    String phone = Response.getJSONObject(0).getString("phone");
//                                    String account = Response.getJSONObject(0).getString("account");
//                                    binding.input06.setText(phone);
//                                    binding.input07.setText(account);
//                                }
//                            } catch (Exception e) {
//                                dlog.i("UserCheck Exception : " + e);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//            }
//
//            @Override
//            @SuppressLint("LongLogTag")
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e(TAG, "에러2 = " + t.getMessage());
//            }
//        });
        //Array데이터를 받아올 때
        try {
            String phone = UserCheckData.getInstance().getUser_phone();
            String account = UserCheckData.getInstance().getUser_account();
            binding.input06.setText(phone);
            binding.input07.setText(account);
        } catch (Exception e) {
            dlog.i("UserCheck Exception : " + e);
        }
    }

    private boolean DataCheck() {
        owner_name = binding.input01.getText().toString();
        owner_registrnum = binding.input02.getText().toString();
        owner_address = binding.input04.getText().toString();
        owner_address_detail = binding.input05.getText().toString();
        owner_phone = binding.input06.getText().toString();
        owner_email = binding.input07.getText().toString();
        dlog.i("-----DataCheck-----");
        dlog.i("select0102 : " + select0102);
        dlog.i("owner_name : " + owner_name);
        dlog.i("owner_registrnum : " + owner_registrnum);
        dlog.i("zipcode : " + zipcode);
        dlog.i("owner_address : " + owner_address);
        dlog.i("owner_address_detail : " + owner_address_detail);
        dlog.i("owner_phone : " + owner_phone);
        dlog.i("owner_email : " + owner_email);
        dlog.i("-----DataCheck-----");
        if (owner_name.isEmpty()) {
            Toast_Nomal("사업주 명을 입력해주세요");
            return false;
        } else if (owner_registrnum.isEmpty()) {
            Toast_Nomal("사업자번호를 입력해주세요");
            return false;
        } else if (owner_address.isEmpty()) {
            Toast_Nomal("주소를 입력해주세요");
            return false;
        } else if (owner_phone.isEmpty()) {
            Toast_Nomal("전화번호를 입력해주세요");
            return false;
        } else if (owner_email.isEmpty()) {
            Toast_Nomal("이메일을 입력해주세요");
            return false;
        } else {
            return SearchRestrnum(owner_registrnum.replace("-", ""));
        }
    }

    String b_stt = "";
    String tax_type = "";
    boolean registrTF = false;

    private boolean SearchRestrnum(String registr_num) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RegistrSearchInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        RegistrSearchInterface api = retrofit.create(RegistrSearchInterface.class);
        Call<String> call = api.getData(registr_num);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dlog.e("SearchRestrnum function START");
                dlog.e("response 1: " + response.isSuccessful());
                dlog.e("response 2: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = rc.getBase64decode(String.valueOf(response.body()));
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        b_stt = Response.getJSONObject(0).getString("b_stt");
                        tax_type = Response.getJSONObject(0).getString("tax_type");
                        dlog.i("response.body() : " + response.body());
                        dlog.i("Response : " + Response);
                        dlog.i("b_no : " + Response.getJSONObject(0).getString("b_no"));
                        dlog.i("b_stt : " + Response.getJSONObject(0).getString("b_stt"));
                        dlog.i("b_stt_cd : " + Response.getJSONObject(0).getString("b_stt_cd"));
                        dlog.i("tax_type : " + Response.getJSONObject(0).getString("tax_type"));
                        dlog.i("tax_type_cd : " + Response.getJSONObject(0).getString("tax_type_cd"));
                        dlog.i("end_dt : " + Response.getJSONObject(0).getString("end_dt"));
                        dlog.i("utcc_yn : " + Response.getJSONObject(0).getString("utcc_yn"));
                        dlog.i("tax_type_change_dt : " + Response.getJSONObject(0).getString("tax_type_change_dt"));
                        dlog.i("invoice_apply_dt : " + Response.getJSONObject(0).getString("invoice_apply_dt"));

                        if (tax_type.equals("국세청에 등록되지 않은 사업자등록번호입니다.")) {
                            Toast_Nomal("국세청에 등록되지 않은 사업자등록번호입니다.");
                            binding.registrNumState.setText("국세청에 등록되지 않은 사업자등록번호입니다.");
                            binding.registrNumState.setTextColor(R.color.red);
                            registrTF = true;//--테스트 후 false로
                        } else {
                            registrTF = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }

        });
//        return tax_type.equals("국세청에 등록되지 않은 사업자등록번호입니다.");
        return true;
    }

    public void SaveContractBasic() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractBasicInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractBasicInterface api = retrofit.create(ContractBasicInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, worker_id, String.valueOf(select0102), owner_name
                , owner_registrnum, zipcode, owner_address, owner_address_detail, String.valueOf(size010203), owner_phone, owner_email);
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
                                getContractId();
                                pm.AddContractPage04(mContext);
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

    @Override
    public void onBackPressed() {

        if (!shardpref.getString("progress_pos", "").isEmpty()) {
            pm.ContractFragment(mContext);
        } else {
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
