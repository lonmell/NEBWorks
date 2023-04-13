package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.kakao.sdk.common.KakaoSdk;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.ContractGetAllInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.databinding.ActivityContractAdd09Binding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AddContractPage09 extends AppCompatActivity {
    private ActivityContractAdd09Binding binding;
    private final static String TAG = "AddContractPage08";
    private static final int SIGNING_BITMAP = 2022;

    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String place_id = "";
    String worker_id = "";
    String USER_INFO_ID = "";
    String contract_id = "";
    String place_owner_id = "";
    String contract_email = "";
    String worker_name = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    String title = "";
    String content = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContractAdd09Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        //Singleton Area


        //shardpref Area
        shardpref = new PreferenceHelper(mContext);
        worker_id = shardpref.getString("worker_id", "0");
        contract_id = shardpref.getString("contract_id", "0");
        contract_email = shardpref.getString("contract_email", "0");
        worker_name = shardpref.getString("worker_name", "");
        place_id = shardpref.getString("place_id", "");
        place_owner_id = shardpref.getString("place_owner_id", "");
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

        Glide.with(this).load(R.raw.basic_loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.loadingView);
        binding.loginAlertText.setVisibility(View.GONE);

        // Kakao SDK 객체 초기화
        KakaoSdk.init(this, getString(R.string.kakao_native_key));
        setBtnEvent();
        GetAllContract();
    }

    String kakaotitle = "";
    String emailtitle = "";

    private void setBtnEvent() {
        binding.next.setOnClickListener(v -> {
            onPageOver();
        });

        binding.pdfDownload.setOnClickListener(v-> {
            requestCapture();
        });
    }

    private void onPageOver() {
        // kakaotitle = binding.input01.getText().toString();
        // emailtitle = binding.input02.getText().toString();
        String message = "[" + worker_name + "]근로자의 근로계약서 사인이 도착했습니다.";
        getUserToken(place_owner_id, "0", message);
        AddPush("근로계약서", message, place_owner_id);
        RemoveShared();
        pm.ContractFragment(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shardpref.remove("worker_id");
        shardpref.remove("worker_name");
        shardpref.remove("worker_phone");
        shardpref.remove("worker_email");
    }

    @Override
    public void onBackPressed() {
    }

    private void RemoveShared() {
        shardpref.remove("worker_id");
        shardpref.remove("contract_id");
        shardpref.remove("contract_email");
        shardpref.remove("worker_name");
    }

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
                        boolean channelId1 = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId1) {
                            PushFcmSend(id, "", message, token, "2", place_id);
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
        Call<String> call = api.getData(place_id, "", title, content, user_id, place_owner_id);
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

    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "contract0";
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


    String id = "";
    String place_name = "";
    String owner_id = "";
    String owner_name = "";
    String buisness_kind = "";
    String registr_num = "";
    String address = "";
    String address_detail = "";
    String place_size = "";
    String owner_phone = "";
    String owner_email = "";
    String contract_start = "";
    String contract_end = "";
    String contract_type = "";
    String work_yoil = "";
    String rest_yoil = "";
    String work_start = "";
    String work_end = "";
    String rest_start = "";
    String rest_end = "";
    String work_contents = "";
    String pay_type = "";
    String payment = "";
    String pay_conference = "";
    String pay_loop = "";
    String insurance = "";
    String add_contents = "";
    String add_terms = "";
    String worker_jumin = "";
    String worker_address = "";
    String worker_address_detail = "";
    String worker_phone = "";
    String worker_email = "";
    String owner_sign = "";
    String worker_sign = "";
    String created_at = "";
    String updated_at = "";
    String test_period = "";

    private void GetAllContract() {
        binding.loginAlertText.setVisibility(View.VISIBLE);
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
                                    id = Response.getJSONObject(0).getString("id");
                                    place_id = Response.getJSONObject(0).getString("place_id");
                                    place_name = Response.getJSONObject(0).getString("place_name");
                                    owner_id = Response.getJSONObject(0).getString("owner_id");
                                    owner_name = Response.getJSONObject(0).getString("owner_name");
                                    worker_id = Response.getJSONObject(0).getString("worker_id");
                                    buisness_kind = Response.getJSONObject(0).getString("buisness_kind");// 1 - 개인사업자 / 2 - 법인사업자 [page - 1]
                                    registr_num = Response.getJSONObject(0).getString("registr_num");
                                    address = Response.getJSONObject(0).getString("address");
                                    address_detail = Response.getJSONObject(0).getString("address_detail");
                                    place_size = Response.getJSONObject(0).getString("place_size"); //사업장 사이즈
                                    owner_phone = Response.getJSONObject(0).getString("owner_phone");
                                    owner_email = Response.getJSONObject(0).getString("owner_email");
                                    contract_start = Response.getJSONObject(0).getString("contract_start");
                                    contract_end = Response.getJSONObject(0).getString("contract_end");
                                    contract_type = Response.getJSONObject(0).getString("contract_type");// 기간의 정함이 없는 계약 / 0 - off / 1 - on [page - 2]
                                    work_yoil = Response.getJSONObject(0).getString("work_yoil");
                                    rest_yoil = Response.getJSONObject(0).getString("rest_yoil");
                                    work_start = Response.getJSONObject(0).getString("work_start");
                                    work_end = Response.getJSONObject(0).getString("work_end");
                                    rest_start = Response.getJSONObject(0).getString("rest_start");
                                    rest_end = Response.getJSONObject(0).getString("rest_end");
                                    work_contents = Response.getJSONObject(0).getString("work_contents");
                                    pay_type = Response.getJSONObject(0).getString("pay_type");//급여지급방식 0- 직접전달 / 1 - 급여통장 [page - 2]
                                    payment = Response.getJSONObject(0).getString("payment");
                                    pay_conference = Response.getJSONObject(0).getString("pay_conference");//협의 여부 / 1- 가능 / 0 - 불가능 [page - 2]
                                    pay_loop = Response.getJSONObject(0).getString("pay_loop");
                                    insurance = Response.getJSONObject(0).getString("insurance");
                                    add_contents = Response.getJSONObject(0).getString("add_contents");
                                    add_terms = Response.getJSONObject(0).getString("add_terms");
                                    worker_name = Response.getJSONObject(0).getString("worker_name");
                                    worker_jumin = Response.getJSONObject(0).getString("worker_jumin");
                                    worker_address = Response.getJSONObject(0).getString("worker_address");
                                    worker_address_detail = Response.getJSONObject(0).getString("worker_address_detail");
                                    worker_phone = Response.getJSONObject(0).getString("worker_phone");
                                    worker_email = Response.getJSONObject(0).getString("worker_email");
                                    owner_sign = Response.getJSONObject(0).getString("owner_sign");
                                    worker_sign = Response.getJSONObject(0).getString("worker_sign");
                                    created_at = Response.getJSONObject(0).getString("created_at");
                                    updated_at = Response.getJSONObject(0).getString("updated_at");
                                    test_period = Response.getJSONObject(0).getString("test_period");

                                    getContractImage();
                                }
                                binding.loginAlertText.setVisibility(View.GONE);
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
                binding.loginAlertText.setVisibility(View.GONE);
            }
        });
    }

    public void getContractImage() {
        binding.subTitle.setText(place_name + "(이하 \"사업주\"라함) 과(와) " + worker_name + "(이하 \"근로자\"라 함) 은(는) 다음과 같이 근로계약을 체결한다.");
        binding.contract1.setText("1. 근로계약 기간: " + contract_start + "부터 " + contract_end + "까지");
        binding.contract2.setText("2. 근무 장소: " + address + address_detail);
        binding.contract3.setText("3. 업무의 내용; " + work_contents);
        binding.contract4.setText("4. 소정근로기간: " + work_start + "부터 " + work_end + "까지 " + "(휴게시간: " + rest_start + "~" + rest_end + ")");

        String[] workYoil = work_yoil.split(",");
        binding.contract5.setText("5. 근무일/휴일: 매주 " + workYoil.length + "일(또는 매일단위) 근무, 주휴일 매주 " + rest_yoil + "요일");
        binding.contract61.setText("- 월(일, 시간)급: " + payment + "원");
        binding.contract66.setText("- 임금지급일: 매월 (매주 또는 매일) " + pay_loop + "일 (휴일의 경우 전일 지급)");
        if (pay_type.equals("0")) {
            binding.contract67.setText("- 지급방법: 근로자에게 직접 지급(○), 근로자 명의 예금통장에 입금(  )");
        } else {
            binding.contract67.setText("- 지급방법: 근로자에게 직접 지급(  ), 근로자 명의 예금통장에 입금(○)");
        }

        if (insurance.contains("4대보험")) {
            binding.contract81.setText("■ 고용보험 ■ 산재보험 ■ 국민연금 ■ 건강보험");
        } else {
            binding.contract81.setText("□ 고용보험 □ 산재보험 □ 국민연금 □ 건강보험");
        }

        binding.contractDate.setText(created_at);
        binding.contractOwner1.setText("(사업주) 사업체 명: " + place_name + " (전화: " + owner_phone + ")");
        binding.contractOwner2.setText("주소: " + address + " " + address_detail);
        binding.contractOwner3.setText("대표자: " + owner_name);

        binding.contractWorker1.setText("(근로자) 주소: " + worker_address + " " + worker_address_detail);
        binding.contractWorker2.setText("연락처: " + worker_phone);
        binding.contractWorker3.setText("성명: " + worker_name);

        Glide.with(mContext).load(owner_sign)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.contractOwnerSignImg);
        Glide.with(mContext).load(worker_sign)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.contractWorkerSignImg);
    }

    @SuppressLint("SdCardPath")
    String SD_PATH = "/storage/emulated/0/Download";//--엑셀 파일 다운로드경로

    public void requestCapture() {
        View view = binding.contractImageLayout;
        Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(SD_PATH, place_name + "_" + worker_name + "_근로계약서.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(SD_PATH + "/" + place_name + "_" + worker_name + "_근로계약서.pdf"));
            document.open();

            Image image = Image.getInstance(f.toString());
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.getWidth()) * 100;
            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(image);
            document.close();
//            Toast.makeText(this, "PDF 파일 저장성공", Toast.LENGTH_SHORT).show();

            f.delete();
            File file = new File(SD_PATH, place_name + "_" + worker_name + "_근로계약서.pdf");
            Uri uri = FileProvider.getUriForFile(this, "com.krafte.nebworks.provider", file);
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            Intent chooser = Intent.createChooser(intent, "share");
            mContext.startActivity(chooser);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private File BitmapConvertFile(Bitmap bitmap, String strFilePath, String name) {
        File file = new File(strFilePath, name);

        OutputStream out = null;
        try {
            // 파일 초기화
            file.createNewFile();
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
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
