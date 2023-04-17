package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ContractTermAdapter;
import com.krafte.nebworks.data.TermData;
import com.krafte.nebworks.dataInterface.ContractGetAllInterface;
import com.krafte.nebworks.dataInterface.TermGetInterface;
import com.krafte.nebworks.databinding.ActivityContractallDataBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
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
    String USER_INFO_AUTH = "1";

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
        place_name = shardpref.getString("place_name", "-1");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");

        if(USER_INFO_AUTH.equals("0")){
            binding.editContractArea.setVisibility(View.VISIBLE);
        }

        Glide.with(this).load(R.raw.basic_loading)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.loadingView);
        binding.loginAlertText.setVisibility(View.GONE);

        setBtnEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAllContract();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sharedRemove();
    }

    private void sharedRemove(){
        shardpref.remove("worker_id");
        shardpref.remove("worker_name");
        shardpref.remove("worker_phone");
        shardpref.remove("worker_email");
        shardpref.remove("contract_place_id");
        shardpref.remove("contract_user_id");
        shardpref.remove("contract_id");
    }

    private void setBtnEvent(){
        binding.inoutPrint.setOnClickListener(v -> {
            requestCapture();
        });
        binding.inoutPrint2.setOnClickListener(v -> {
            requestCapture();
        });

        binding.editContract.setOnClickListener(v -> {
            shardpref.putString("worker_id",worker_id);
            shardpref.putString("worker_name",worker_name);
            shardpref.putString("worker_phone",worker_phone.replace("-",""));
            shardpref.putString("worker_email",worker_email);
            shardpref.putString("contract_place_id",place_id);
            shardpref.putString("contract_user_id",worker_id);
            shardpref.putString("contract_id",contract_id);
            Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
            intent.putExtra("data", "해당 직원의 근로계약서를 \n 다시 작성합니다");
            intent.putExtra("flag", "근로계약서수정");
            intent.putExtra("left_btn_txt", "닫기");
            intent.putExtra("right_btn_txt", "수정하기");
            startActivity(intent);
        });
    }


    String id = "";
    String place_id = "";
    String place_name = "";
    String owner_id = "";
    String owner_name = "";
    String worker_id = "";
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
    String worker_name = "";
    String worker_jumin = "";
    String worker_jumin2 = "";
    String worker_address = "";
    String worker_address_detail = "";
    String worker_phone = "";
    String worker_email = "";
    String owner_sign = "";
    String worker_sign = "";
    String created_at = "";
    String updated_at = "";
    String test_period = "";

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
                                    id               = Response.getJSONObject(0).getString("id");
                                    place_id         = Response.getJSONObject(0).getString("place_id");
                                    place_name       = Response.getJSONObject(0).getString("place_name");
                                    owner_id         = Response.getJSONObject(0).getString("owner_id");
                                    owner_name       = Response.getJSONObject(0).getString("owner_name");
                                    worker_id        = Response.getJSONObject(0).getString("worker_id");
                                    buisness_kind    = Response.getJSONObject(0).getString("buisness_kind");// 1 - 개인사업자 / 2 - 법인사업자 [page - 1]
                                    registr_num      = Response.getJSONObject(0).getString("registr_num");
                                    address          = Response.getJSONObject(0).getString("address");
                                    address_detail   = Response.getJSONObject(0).getString("address_detail");
                                    place_size       = Response.getJSONObject(0).getString("place_size"); //사업장 사이즈
                                    owner_phone      = Response.getJSONObject(0).getString("owner_phone");
                                    owner_email      = Response.getJSONObject(0).getString("owner_email");
                                    contract_start   = Response.getJSONObject(0).getString("contract_start");
                                    contract_end     = Response.getJSONObject(0).getString("contract_end");
                                    contract_type    = Response.getJSONObject(0).getString("contract_type");// 기간의 정함이 없는 계약 / 0 - off / 1 - on [page - 2]
                                    work_yoil        = Response.getJSONObject(0).getString("work_yoil");
                                    rest_yoil        = Response.getJSONObject(0).getString("rest_yoil");
                                    work_start       = Response.getJSONObject(0).getString("work_start");
                                    work_end         = Response.getJSONObject(0).getString("work_end");
                                    rest_start       = Response.getJSONObject(0).getString("rest_start");
                                    rest_end         = Response.getJSONObject(0).getString("rest_end");
                                    work_contents    = Response.getJSONObject(0).getString("work_contents");
                                    pay_type         = Response.getJSONObject(0).getString("pay_type");//급여지급방식 0- 직접전달 / 1 - 급여통장 [page - 2]
                                    payment          = Response.getJSONObject(0).getString("payment");
                                    pay_conference   = Response.getJSONObject(0).getString("pay_conference");//협의 여부 / 1- 가능 / 0 - 불가능 [page - 2]
                                    pay_loop         = Response.getJSONObject(0).getString("pay_loop");
                                    insurance        = Response.getJSONObject(0).getString("insurance");
                                    add_contents     = Response.getJSONObject(0).getString("add_contents");
                                    add_terms        = Response.getJSONObject(0).getString("add_terms");
                                    worker_name      = Response.getJSONObject(0).getString("worker_name");
                                    worker_jumin     = Response.getJSONObject(0).getString("worker_jumin");
                                    worker_jumin2     = Response.getJSONObject(0).getString("worker_jumin2");
                                    worker_address   = Response.getJSONObject(0).getString("worker_address");
                                    worker_address_detail = Response.getJSONObject(0).getString("worker_address_detail");
                                    worker_phone     = Response.getJSONObject(0).getString("worker_phone");
                                    worker_email     = Response.getJSONObject(0).getString("worker_email");
                                    owner_sign       = Response.getJSONObject(0).getString("owner_sign");
                                    worker_sign      = Response.getJSONObject(0).getString("worker_sign");
                                    created_at       = Response.getJSONObject(0).getString("created_at");
                                    updated_at       = Response.getJSONObject(0).getString("updated_at");
                                    test_period      = Response.getJSONObject(0).getString("test_period");

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
                                    binding.input11.setText(worker_jumin2);
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

                                    getContractImage();
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
            //파일을 생성하기전 해당 경로의 파일을 한번 삭제한다
            if (f.delete()) {
                System.out.println("파일이 삭제되었습니다.");
            } else {
                System.out.println("파일을 삭제할 수 없습니다.");
            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(SD_PATH, place_name + "_" + worker_name + "_근로계약서.pdf");
        Uri uri = FileProvider.getUriForFile(this, "com.krafte.nebworks.provider", file);
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        Intent chooser = Intent.createChooser(intent, "share");
        mContext.startActivity(chooser);

    }

    String SD_PATH = "/storage/emulated/0/Download";//--엑셀 파일 다운로드경로
    public void createExcel() {
        File file_path;

        try {
            file_path = new File(SD_PATH);
            if (!file_path.isDirectory()) {//해당 경로의 파일이 없으면 생성시켜준다
                file_path.mkdirs();
            }

            File sd = Environment.getExternalStorageDirectory();
            String csvFile = place_name + "_" + worker_name + "_근로계약서.xls";

            File directory = new File(sd.getAbsolutePath());

            //create directory if not exist
            if (!directory.isDirectory()) {
                directory.mkdirs();
            }

            //file path
            File file = new File(SD_PATH, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);

            //Excel sheetA first sheetA
            WritableSheet sheetA = workbook.createSheet("표준근로계약서", 0);

            //--타이틀 부분 START
            WritableFont title = new WritableFont(WritableFont.ARIAL, 17);
            WritableCellFormat cellFormat = new WritableCellFormat(title);
            title.setBoldStyle(WritableFont.BOLD);
            cellFormat.setAlignment(Alignment.CENTRE);
            cellFormat.setLocked(true);

            WritableFont font_1 = new WritableFont(WritableFont.ARIAL, 10);
            font_1.setColour(Colour.BLACK);
            WritableCellFormat cellFormat_1 = new WritableCellFormat(font_1);
            cellFormat_1.setAlignment(Alignment.LEFT);
            cellFormat_1.setWrap(true);
            cellFormat_1.setLocked(true);

            WritableFont font_2 = new WritableFont(WritableFont.ARIAL, 8);
            font_2.setColour(Colour.BLACK);
            WritableCellFormat cellFormat_2 = new WritableCellFormat(font_2);
            cellFormat_2.setAlignment(Alignment.LEFT);
            cellFormat_2.setWrap(true);
            cellFormat_2.setLocked(true);

            WritableFont font_3 = new WritableFont(WritableFont.ARIAL, 10);
            font_3.setColour(Colour.BLACK);
            WritableCellFormat cellFormat_3 = new WritableCellFormat(font_3);
            cellFormat_3.setAlignment(Alignment.CENTRE);
            cellFormat_3.setWrap(true);
            cellFormat_3.setLocked(true);

            CellView cellView = new CellView();
            cellView.setSize(50);
            sheetA.setColumnView(1, cellView); // 첫번째 열의 셀 높이 변경
            sheetA.setColumnView(2, cellView);
            sheetA.setColumnView(3, cellView);
            sheetA.setColumnView(4, cellView);
            sheetA.setColumnView(5, cellView);
            sheetA.setColumnView(6, cellView);
            sheetA.setColumnView(7, cellView);
            sheetA.setColumnView(8, cellView);
            sheetA.setColumnView(9, cellView);

            sheetA.setColumnView(1, 15);
            sheetA.setColumnView(2, 15);
            sheetA.setColumnView(3, 15);
            sheetA.setColumnView(4, 15);
            sheetA.setColumnView(5, 15);

            sheetA.mergeCells(1, 3, 4, 3);//--타이틀 합체
            Label label = new Label(1, 3, "표준근로계약서", cellFormat);
            sheetA.addCell(label);

            sheetA.mergeCells(1, 5, 4, 5);
            Label label_1 = new Label(1, 5, place_name + "(이하 \"사업주\"라함) 과(와) " + worker_name + "(이하 \"근로자\"라 함) 은(는) 다음과 같이 근로계약을 체결한다." , cellFormat_1);
            sheetA.setRowView(5, 550);
            sheetA.addCell(label_1);

            sheetA.mergeCells(1, 7, 4, 7);
            Label label_2 = new Label(1, 7, "1. 근로계약 기간: " + contract_start + "부터 " + contract_end + "까지" , cellFormat_1);
            sheetA.addCell(label_2);

            sheetA.mergeCells(1, 8, 4, 8);
            Label label_3 = new Label(1, 8, "※ 근로계약기간을 정하지 않는 경우에는 \"근로개시일\"만 기재" , cellFormat_2);
            sheetA.addCell(label_3);

            sheetA.mergeCells(1, 9, 4, 9);
            Label label_4 = new Label(1, 9, "2. 근무 장소: " + address + " " + address_detail , cellFormat_1);
            sheetA.addCell(label_4);

            sheetA.mergeCells(1, 10, 4, 10);
            Label label_5 = new Label(1, 10, "3. 업무의 내용: " + work_contents , cellFormat_1);
            sheetA.addCell(label_5);

            sheetA.mergeCells(1, 11, 4, 11);
            Label label_6 = new Label(1, 11, "4. 소정근로기간: " + work_start + "부터 " + work_end + "까지 " + "(휴게시간: " + rest_start + "~" + rest_end + ")", cellFormat_1);
            sheetA.addCell(label_6);

            sheetA.mergeCells(1, 12, 4, 12);
            String[] workYoil = work_yoil.split(",");
            Label label_7 = new Label(1, 12, "5. 근무일/휴일: 매주 " + workYoil.length + "일(또는 매일단위) 근무, 주휴일 매주 " + rest_yoil + "요일"  , cellFormat_1);
            sheetA.addCell(label_7);

            sheetA.mergeCells(1, 13, 4, 13);
            Label label_8 = new Label(1, 13, "6. 임금"  , cellFormat_1);
            sheetA.addCell(label_8);

            sheetA.mergeCells(1, 14, 4, 14);
            Label label_9 = new Label(1, 14, "- 월(일, 시간)급: " + payment + "원"  , cellFormat_1);
            sheetA.addCell(label_9);

            sheetA.mergeCells(1, 15, 4, 15);
            Label label_10 = new Label(1, 15, "- 상여금: 있음 ( ), 없음 (○)");
            sheetA.addCell(label_10);

            sheetA.mergeCells(1, 16, 4, 16);
            Label label_11 = new Label(1, 16, "- 기타 급여 (재 수당 등): 있음 ( )                 원, 없음 (○)", cellFormat_1);
            sheetA.addCell(label_11);

            sheetA.mergeCells(1, 17, 4, 17);
            Label label_12 = new Label(1, 17, "-                   원,                     원", cellFormat_1);
            sheetA.addCell(label_12);

            sheetA.mergeCells(1, 18, 4, 18);
            Label label_12_1 = new Label(1, 18, "-                 원,                     원", cellFormat_1);
            sheetA.addCell(label_12_1);

            sheetA.mergeCells(1, 19, 4, 19);
            Label label_13 = new Label(1, 19, "- 임금지급일: 매월 (매주 또는 매일) " + pay_loop + "일 (휴일의 경우 전일 지급)"  , cellFormat_1);
            sheetA.addCell(label_13);

            sheetA.mergeCells(1, 20, 4, 20);
            Label label_14;
            if (pay_type.equals("0")) {
                label_14 = new Label(1, 20, "- 지급방법: 근로자에게 직접 지급(○), 근로자 명의 예금통장에 입금(  )"  , cellFormat_1);
            } else {
                label_14 = new Label(1, 20, "- 지급방법: 근로자에게 직접 지급(  ), 근로자 명의 예금통장에 입금(○)"  , cellFormat_1);
            }
            sheetA.addCell(label_14);

            sheetA.mergeCells(1, 21, 4, 21);
            Label label_15 = new Label(1, 21, "7. 연차유급휴가"  , cellFormat_1);
            sheetA.addCell(label_15);

            sheetA.mergeCells(1, 22, 4, 22);
            Label label_16 = new Label(1, 22, "- 연차유급휴가는 근로기준법에서 정하는 바에 따라 부여함"  , cellFormat_1);
            sheetA.addCell(label_16);

            sheetA.mergeCells(1, 23, 4, 23);
            Label label_17 = new Label(1, 23, "8. 사회보험 적용여부(해당란에 체크)"  , cellFormat_1);
            sheetA.addCell(label_17);

            sheetA.mergeCells(1, 24, 4, 24);
            Label label_18;
            if (insurance.contains("4대보험")) {
                label_18 = new Label(1, 24, "■ 고용보험 ■ 산재보험 ■ 국민연금 ■ 건강보험"  , cellFormat_1);
            } else {
                label_18 = new Label(1, 24, "□ 고용보험 □ 산재보험 □ 국민연금 □ 건강보험"  , cellFormat_1);
            }
            sheetA.addCell(label_18);

            sheetA.mergeCells(1, 25, 4, 25);
            Label label_19 = new Label(1, 25, "9. 근로계약서 교부"  , cellFormat_1);
            sheetA.addCell(label_19);

            sheetA.mergeCells(1, 26, 4, 26);
            Label label_20 = new Label(1, 26, "- 사업주는 근로계약을 체결함과 동시에 본 계약서를 사본하여 근로자의 교부요구와 관계없이 근로자에게 교부함 (근로기준법 제17조) 이행"  , cellFormat_1);
            sheetA.setRowView(26, 550);
            sheetA.addCell(label_20);

            sheetA.mergeCells(1, 27, 4, 27);
            Label label_21 = new Label(1, 27, "10. 기타"  , cellFormat_1);
            sheetA.addCell(label_21);

            sheetA.mergeCells(1, 28, 4, 28);
            Label label_22 = new Label(1, 28, "- 이 계약에 정함이 없는 사항은 근로기준법령에 의함"  , cellFormat_1);
            sheetA.addCell(label_22);

            sheetA.mergeCells(1, 31, 4, 31);
            Label label_23 = new Label(1, 31, created_at, cellFormat_3);
            sheetA.addCell(label_23);

            sheetA.mergeCells(1, 33, 4, 33);
            Label label_24 = new Label(1, 33, "(사업주) 사업체 명: " + place_name + " (전화: " + owner_phone + ")", cellFormat_1);
            sheetA.addCell(label_24);

            sheetA.mergeCells(1, 34, 4, 34);
            Label label_25 = new Label(1, 34, "주소: " + address + " " + address_detail , cellFormat_1);
            sheetA.addCell(label_25);

            sheetA.mergeCells(1, 35, 2, 35);
            Label label_26 = new Label(1, 35, "대표자: " + owner_name , cellFormat_1);
            sheetA.addCell(label_26);

            Label label_26_1 = new Label(3, 35, "(서명)", cellFormat_1);
            sheetA.addCell(label_26_1);

            sheetA.mergeCells(1, 36, 4, 36);
            sheetA.setRowView(36, 1000);
            new Thread(() -> {
                try {
                    URL url = new URL(owner_sign);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    File imageFile = BitmapConvertFile(bitmap, SD_PATH, "owner_sign");
                    WritableImage image = new WritableImage(1, 36, 2, 1, imageFile);
                    sheetA.addImage(image);
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            sheetA.mergeCells(1, 37, 4, 37);
            Label label_27 = new Label(1, 37, "(근로자) 주소: " + worker_address + " " + worker_address_detail, cellFormat_1);
            sheetA.addCell(label_27);

            sheetA.mergeCells(1, 38, 4, 38);
            Label label_28 = new Label(1, 38, "연락처: " + worker_phone, cellFormat_1);
            sheetA.addCell(label_28);

            sheetA.mergeCells(1, 39, 2, 39);
            Label label_29 = new Label(1, 39, "성명: " + worker_name, cellFormat_1);
            sheetA.addCell(label_29);

            Label label_29_1 = new Label(3, 39, "(서명)", cellFormat_1);
            sheetA.addCell(label_29_1);

            sheetA.mergeCells(1, 40, 4, 40);
            sheetA.setRowView(40, 1000);
            Formula formula = new Formula(1, 40, "InsertWebImage Sheet1.Range(\"B41\"),\"" + worker_sign + "\"");
            sheetA.addCell(formula);
//            new Thread(() -> {
//                try {
//                    URL url2 = new URL(worker_sign);
//                    HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
//                    connection2.setDoInput(true);
//                    connection2.connect();
//                    InputStream input2 = connection2.getInputStream();
//                    Bitmap bitmap2 = BitmapFactory.decodeStream(input2);
//                    File imageFile2 = BitmapConvertFile(bitmap2, SD_PATH, "worker_sign");
//                    WritableImage image2 = new WritableImage(1, 40, 2, 1, imageFile2);
//                    sheetA.addImage(image2);
//                    if (imageFile2.exists()) {
//                        imageFile2.delete();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();


            // close workbook
            workbook.write();
            workbook.close();

            Toast_Nomal("다운로드 폴더에 Excel파일이 생성되었습니다.");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private File BitmapConvertFile(Bitmap bitmap, String strFilePath, String name) {
        File file = new File(strFilePath, name + ".png");

        OutputStream out = null;
        try {
            // 파일 초기화
            file.createNewFile();
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
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
