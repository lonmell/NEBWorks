package com.krafte.nebworks.ui.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.ContractGetAllInterface;
import com.krafte.nebworks.dataInterface.ContractWorkerSignInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.databinding.ActivityContractworkerSignBinding;
import com.krafte.nebworks.pop.SignPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class ContractWorkerSignActivity extends AppCompatActivity {
    private ActivityContractworkerSignBinding binding;
    private final static String TAG = "ContractWorkerSignActivity";
    private static final int SIGNING_BITMAP = 2022;
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String contract_id = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";

    //Other
    DateCurrent dc = new DateCurrent();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    private Bitmap saveBitmap;
    File file;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/NEBWorks/";
    String worker_sign_url = "";
    String feed_thumnail_path = "";
    String ImgfileMaker = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityContractworkerSignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        //Singleton Area
        USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
        USER_INFO_AUTH  = UserCheckData.getInstance().getUser_auth();

        //shardpref Area
        shardpref = new PreferenceHelper(mContext);
        contract_id = shardpref.getString("contract_id","");

        setBtnEvent();

        //UI Setting
        if(USER_INFO_AUTH.equals("0")){
            binding.loCanvas.setVisibility(View.GONE);
            binding.nexttv.setText("확인");
        }else{
            binding.workerSign.setVisibility(View.GONE);
            binding.nexttv.setText("저장");
            MakeDirs();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAllContract();
        ImgfileMaker = ImageNameMaker();
    }

    private void setBtnEvent(){
        binding.select01.setOnClickListener(v -> {
            if(binding.select01box.getVisibility() == View.GONE){
                binding.select01box.setVisibility(View.VISIBLE);
            }else{
                binding.select01box.setVisibility(View.GONE);
            }
        });
        binding.select02.setOnClickListener(v -> {
            if(binding.select02box.getVisibility() == View.GONE){
                binding.select02box.setVisibility(View.VISIBLE);
            }else{
                binding.select02box.setVisibility(View.GONE);
            }
        });
        binding.select03.setOnClickListener(v -> {
            if(binding.select03box.getVisibility() == View.GONE){
                binding.select03box.setVisibility(View.VISIBLE);
            }else{
                binding.select03box.setVisibility(View.GONE);
            }
        });
        binding.select04.setOnClickListener(v -> {
            if(binding.select04box.getVisibility() == View.GONE){
                binding.select04box.setVisibility(View.VISIBLE);
            }else{
                binding.select04box.setVisibility(View.GONE);
            }
        });
        binding.select05.setOnClickListener(v -> {
            if(binding.select05box.getVisibility() == View.GONE){
                binding.select05box.setVisibility(View.VISIBLE);
            }else{
                binding.select05box.setVisibility(View.GONE);
            }
        });
        binding.select06.setOnClickListener(v -> {
            if(binding.select06box.getVisibility() == View.GONE){
                binding.select06box.setVisibility(View.VISIBLE);
            }else{
                binding.select06box.setVisibility(View.GONE);
            }
        });
        binding.select07.setOnClickListener(v -> {
            if(binding.select07box.getVisibility() == View.GONE){
                binding.select07box.setVisibility(View.VISIBLE);
            }else{
                binding.select07box.setVisibility(View.GONE);
            }
        });
        binding.select08.setOnClickListener(v -> {
            if(binding.select08box.getVisibility() == View.GONE){
                binding.select08box.setVisibility(View.VISIBLE);
            }else{
                binding.select08box.setVisibility(View.GONE);
            }
        });
        binding.select09.setOnClickListener(v -> {
            if(binding.select09box.getVisibility() == View.GONE){
                binding.select09box.setVisibility(View.VISIBLE);
            }else{
                binding.select09box.setVisibility(View.GONE);
            }
        });

        binding.loCanvas.setOnClickListener(v -> {
            if(USER_INFO_AUTH.equals("0")){
                pm.ContractFragment(mContext);
            }else{
                Intent i = new Intent(this, SignPopActivity.class);
                startActivityForResult(i, SIGNING_BITMAP);
            }

        });

        binding.next.setOnClickListener(v -> {
            if(saveBitmap == null){
                Toast_Nomal("고용주 서명이 입력되지 않았습니다.");
            }else{
                UpdateWorkSign();
            }
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
                                    String buisness_kind    = Response.getJSONObject(0).getString("buisness_kind");
                                    String registr_num      = Response.getJSONObject(0).getString("registr_num");
                                    String address          = Response.getJSONObject(0).getString("address");
                                    String address_detail   = Response.getJSONObject(0).getString("address_detail");
                                    String place_size       = Response.getJSONObject(0).getString("place_size");
                                    String owner_phone      = Response.getJSONObject(0).getString("owner_phone");
                                    String owner_email      = Response.getJSONObject(0).getString("owner_email");
                                    String contract_start   = Response.getJSONObject(0).getString("contract_start");
                                    String contract_end     = Response.getJSONObject(0).getString("contract_end");
                                    String contract_type    = Response.getJSONObject(0).getString("contract_type");
                                    String work_yoil        = Response.getJSONObject(0).getString("work_yoil");
                                    String rest_yoil        = Response.getJSONObject(0).getString("rest_yoil");
                                    String work_start       = Response.getJSONObject(0).getString("work_start");
                                    String work_end         = Response.getJSONObject(0).getString("work_end");
                                    String rest_start       = Response.getJSONObject(0).getString("rest_start");
                                    String rest_end         = Response.getJSONObject(0).getString("rest_end");
                                    String work_contents    = Response.getJSONObject(0).getString("work_contents");
                                    String pay_type         = Response.getJSONObject(0).getString("pay_type");
                                    String payment          = Response.getJSONObject(0).getString("payment");
                                    String pay_conference   = Response.getJSONObject(0).getString("pay_conference");
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

                                    //대표자 정보
                                    binding.placeNametv.setText(place_name);
                                    binding.ownerNametv.setText(owner_name);
                                    binding.placeAddresstv.setText(address + " " + address_detail);
                                    binding.ownerPhonetv.setText(owner_phone);

                                    //근로자 정보
                                    binding.workerNametv.setText(worker_name);
                                    binding.workerJumin.setText(worker_jumin);
                                    binding.workerAddresstv.setText(worker_address + " " + worker_address_detail);
                                    binding.workerPhonetv.setText(worker_phone);

                                    Glide.with(mContext).load(owner_sign)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .into(binding.ownerSign);
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

    private String ImageNameMaker() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MakeFileNameInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MakeFileNameInterface api = retrofit.create(MakeFileNameInterface.class);
        Call<String> call = api.getData("");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
//                    dlog.i("jsonResponse length : " + jsonResponse.length());
//                    dlog.i("jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        if (!Response.toString().equals("[]")) {
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                ImgfileMaker = jsonObject.getString("id");
                                dlog.i("ImgfileMaker : " + ImgfileMaker);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러 = " + t.getMessage());
            }
        });
        return ImgfileMaker;
    }

    private void UpdateWorkSign(){
        worker_sign_url = "http://krafte.net/NEBWorks/image/sign_img/" + USER_INFO_ID + "_" + ImgfileMaker + ".JPEG";
        dlog.i("------UpdatePagePos------");
        dlog.i("contract_id : " + contract_id);
        dlog.i("worker_sign_url : " + worker_sign_url);
        dlog.i("------UpdatePagePos------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ContractWorkerSignInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        ContractWorkerSignInterface api = retrofit.create(ContractWorkerSignInterface.class);
        Call<String> call = api.getData(contract_id,worker_sign_url);
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
                                    saveBitmapAndGetURI();
                                    pm.AddContractPage09(mContext);
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

    //이미지 업로드에 필요한 소스 START
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGNING_BITMAP){
            if (resultCode == RESULT_OK) {
                if(data.hasExtra("signing")) {
                    ImageView previewThumbnail = new ImageView(this);
                    saveBitmap = BitmapFactory.decodeByteArray(
                            data.getByteArrayExtra("signing"),0,data.getByteArrayExtra("signing").length);
                    previewThumbnail.setImageBitmap(saveBitmap);
                    binding.loCanvas.setImageBitmap(saveBitmap);
                }
            }
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("LongLogTag")
    private void MakeDirs(){
        File dir = new File(BACKUP_PATH);
        dlog.i("BACKUP_PATH : " + BACKUP_PATH);
        try{
            if(!dir.exists()){
                dir.mkdirs();
                dlog.i("==============폴더 미존재==============");
            }else{
                dlog.i("==============폴더 이미 존재==============");
            }

            if(dir.exists()) {
                dlog.i( "==============백업 폴더 생성완료==============");
            }else{
                dlog.i( "==============폴더 생성실패==============");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint({"SimpleDateFormat", "LongLogTag"})
    public Uri saveBitmapAndGetURI() {
        //Create Bitmap
//            saveBitmap = CanvasIO.openBitmap(mContext);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        //Create Bitmap -> File
        final String IMG_FILE_EXTENSION = ".JPEG";
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String inputDate = dc.GET_YEAR + dc.GET_MONTH + dc.GET_DAY;
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
        String fullFileName = BACKUP_PATH;

        dlog.i("(saveBitmapAndGetURI)ex_storage : " + ex_storage);
        dlog.i("(saveBitmapAndGetURI)USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("(saveBitmapAndGetURI)file_name : " + file_name);

        File file_path;
        try {
            file_path = new File(fullFileName);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            dlog.i("(saveBitmapAndGetURI)file_path : " + file_path);
            dlog.i("(saveBitmapAndGetURI)file_name : " + file_name);
            file = new File(file_path, file_name);
            FileOutputStream out = new FileOutputStream(file);
            saveBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            worker_sign_url = "http://krafte.net/NEBWorks/image/sign_img/" + file_name;
            feed_thumnail_path = "http://krafte.net/NEBWorks/image/sign_img/" + file_name;
            saveBitmapToFile(file);

            dlog.e("이미지 저장경로 : " + worker_sign_url);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            AddContractPage08.RetrofitInterface retrofitInterface = AddContractPage08.ApiClient.getApiClient().create(AddContractPage08.RetrofitInterface.class);
            Call<String> call = retrofitInterface.request(body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("uploaded_file()", "성공 : call = " + call + "response = " + response);
                    Log.e(TAG, "response.body() : " + response.body());

                    if (fileDelete(String.valueOf(file))) {
                        Log.e("uploaded_file()", "기존 이미지 삭제 완료");
                    } else {
                        Log.e("uploaded_file()", "이미지 삭제 오류");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    dlog.e("uploaded_file() 에러 : " + t.getMessage());
                }
            });
            dlog.d("(saveBitmapAndGetURI)이미지 경로 : " + Uri.fromFile(file).toString());

            out.close();
            dlog.i("(saveBitmapAndGetURI)file : " + file);
//            mHandler = new Handler(Looper.getMainLooper());
//            mHandler.postDelayed(this::setUpdateUserStoreThumnail, 0);
        } catch (FileNotFoundException exception) {
            dlog.e("FileNotFoundException : " + exception.getMessage());
        } catch (IOException exception) {
            dlog.e("IOException : " + exception.getMessage());
        }
        return null;
    }

    public static class ApiClient {
        private static final String BASE_URL = "http://krafte.net/NEBWorks/image/";
        private static Retrofit retrofit;

        public static Retrofit getApiClient() {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
            return retrofit;
        }

    }

    public interface RetrofitInterface {
        //api를 관리해주는 인터페이스
        @Multipart
        @POST("upload_sign_img.php")
        Call<String> request(@Part MultipartBody.Part file);
    }

    @SuppressLint("LongLogTag")
    public static boolean fileDelete(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public File saveBitmapToFile(File file) {
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 8;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 50;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 8 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 8 >= REQUIRED_SIZE) {
                scale *= 8;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return file;
        } catch (Exception e) {
            return null;
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
}

