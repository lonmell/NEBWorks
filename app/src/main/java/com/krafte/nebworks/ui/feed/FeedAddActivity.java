package com.krafte.nebworks.ui.feed;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krafte.nebworks.R;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.FeedNotiAddInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.databinding.ActivityPlacenotiAddBinding;
import com.krafte.nebworks.util.DBConnection;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

public class FeedAddActivity extends AppCompatActivity {
    private final static String TAG = "PlaceNotiAddActivity";
    private ActivityPlacenotiAddBinding binding;
    Context mContext;
    int GALLEY_CODE = 10;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    //navbar.xml
    DrawerLayout drawerLayout;
    View drawerView;
    ImageView close_btn;

    //shared
    String USER_INFO_ID = "";
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String USER_INFO_EMAIL = "";

    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/nebworks/";
    String ProfileUrl = "";
    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    Drawable icon_off;
    Drawable icon_on;

    String Time01 = "-99";
    String Time02 = "-99";

    //Check Data
    String noti_title, noti_contents, noti_link, noti_event_start, noti_event_end;

    int a = 0;//정직원 수
    int b = 0;//협력업체 직원 수
    List<String> inmember = new ArrayList<>();
    List<String> othermember = new ArrayList<>();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityPlacenotiAddBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        icon_on = mContext.getResources().getDrawable(R.drawable.resize_service_on);
        icon_off = mContext.getResources().getDrawable(R.drawable.resize_service_off);

        setBtnEvent();

        //UI 데이터 세팅
        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            place_id = shardpref.getString("place_id", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            UserCheck();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        ImgfileMaker = ImageNameMaker();
        SetAllMemberList();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        String thumnail_url = shardpref.getString("thumnail_url", "");
        String name = shardpref.getString("name", "");
        String writer_id = shardpref.getString("writer_id", "");
        int timeSelect_flag = shardpref.getInt("timeSelect_flag", 0);

        dlog.i("------------------Data Check onResume------------------");
        dlog.i("thumnail_url : " + thumnail_url);
        dlog.i("name : " + name);
        dlog.i("writer_id : " + writer_id);
        dlog.i("vDateGetDate : " + shardpref.getString("vDateGetDate", ""));
        dlog.i("timeSelect_flag : " + timeSelect_flag);
        dlog.i("------------------Data Check onResume------------------");

        final String GetTime = shardpref.getString("vDateGetDate", "");

        if (timeSelect_flag == 1) {
            Time01 = GetTime;
            shardpref.remove("vDateGetDate");
            binding.eventStarttime.setText(GetTime);
            binding.inputWorktitle.clearFocus();
            binding.inputWorkcontents.clearFocus();
        } else if (timeSelect_flag == 2) {
            Time02 = GetTime;
            shardpref.remove("vDateGetDate");
            binding.eventEndttime.setText(GetTime);
            binding.inputWorktitle.clearFocus();
            binding.inputWorkcontents.clearFocus();
        }
    }

    public void SetAllMemberList() {
        dlog.i("SetAllMemberList place_id : " + place_id);
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id,"");

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            dlog.i("SetAllMemberList response.body() length : " + response.body());
                            if (Response.length() == 0) {
                                dlog.i("ZERO SIZE : " + Response.length());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    //정직원만
                                    if (jsonObject.getString("kind").equals("0")) {
                                        a++;
                                        inmember.add(jsonObject.getString("id"));
                                    }

                                }

                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    //외부협력업체 직원
                                    if (jsonObject.getString("kind").equals("1")) {
                                        b++;
                                        othermember.add(jsonObject.getString("id"));
                                    }

                                }

                                inmember.addAll(othermember);
                                dlog.i("총 직원 배열 : " + inmember);
                                dlog.i("정직원 수 : " + a + "/ 협력업체 직원 수 : " + b);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getDatePicker = "";
    String getYMPicker = "";

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        pm.PlaceWorkBack(mContext);
    }

    public void setBtnEvent() {
        binding.closeBtn.setOnClickListener(v -> {
            pm.PlaceWorkBack(mContext);
        });

        binding.workSave.setOnClickListener(v -> {
            dlog.i("DataCheck() : " + DataCheck());
            if (DataCheck()) {
                AddStroeNoti();
            }
        });

        binding.notiSetimg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLEY_CODE);
        });

        binding.closeBtn.setOnClickListener(v -> {
            super.onBackPressed();
        });


        if (saveBitmap != null) {
            binding.clearImg.setVisibility(View.VISIBLE);
            binding.imgPlus.setVisibility(View.GONE);
        } else {
            binding.clearImg.setVisibility(View.GONE);
            binding.imgPlus.setVisibility(View.VISIBLE);
        }

        binding.clearImg.setOnClickListener(v -> {
            try {
                saveBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                saveBitmap.eraseColor(Color.TRANSPARENT);
                binding.notiSetimg.setImageBitmap(saveBitmap);
                binding.notiSetimg.setBackgroundResource(R.drawable.img_box_round);
                ProfileUrl = "";
                binding.clearImg.setVisibility(View.GONE);
                binding.imgPlus.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                dlog.i("clearImg Exception : " + e);
            }
        });

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month+1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length()==1?"0"+Day:Day;
                Month = Month.length()==1?"0"+Month:Month;
                binding.eventStarttime.setText(year +"-" + Month + "-" + Day);
                getYMPicker = binding.eventStarttime.getText().toString().substring(0,7);
            }
        }, mYear, mMonth, mDay);

        binding.eventStarttime.setOnClickListener(v -> {
            if(binding.eventStarttime.getText().toString().isEmpty()){
                String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                binding.eventStarttime.setText(today);
            }else{
                shardpref.putInt("timeSelect_flag", 1);
                if (binding.eventStarttime.isClickable()) {
                    datePickerDialog.show();
                }
//                Intent intent = new Intent(this, DatePickerActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.translate_up, 0);
            }
        });

        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Year = String.valueOf(year);
                Month = String.valueOf(month+1);
                Day = String.valueOf(dayOfMonth);
                Day = Day.length()==1?"0"+Day:Day;
                Month = Month.length()==1?"0"+Month:Month;
                binding.eventEndttime.setText(year +"-" + Month + "-" + Day);
                getYMPicker = binding.eventEndttime.getText().toString().substring(0,7);
            }
        }, mYear, mMonth, mDay);
        binding.eventEndttime.setOnClickListener(v -> {
            if(binding.eventEndttime.getText().toString().isEmpty()){
                String today = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
                binding.eventEndttime.setText(today);
            }else{
                shardpref.putInt("timeSelect_flag", 2);
                if (binding.eventEndttime.isClickable()) {
                    datePickerDialog2.show();
                }
//                Intent intent = new Intent(this, DatePickerActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.translate_up, 0);
            }
        });
    }

    String mem_id = "";
    String mem_kind = "";
    String mem_name = "";
    String mem_phone = "";
    String mem_gender = "";
    String mem_jumin = "";
    String mem_join_date = "";
    String mem_state = "";
    String mem_jikgup = "";
    String mem_pay = "";
    String mem_img_path = "";
    String io_state = "";

    public void UserCheck() {
        dlog.i("---------UserCheck---------");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("getMonth : " + (dc.GET_MONTH.length() == 1 ? "0" + dc.GET_MONTH : dc.GET_MONTH));
        dlog.i("---------UserCheck---------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AllMemberInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        AllMemberInterface api = retrofit.create(AllMemberInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.e("UserCheck function START");
                dlog.e("response 1: " + response.isSuccessful());
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());

                            try {
                                mem_id = Response.getJSONObject(0).getString("id");
                                mem_name = Response.getJSONObject(0).getString("name");
                                mem_phone = Response.getJSONObject(0).getString("phone");
                                mem_gender = Response.getJSONObject(0).getString("gender");
                                mem_img_path = Response.getJSONObject(0).getString("img_path");
                                mem_jumin = Response.getJSONObject(0).getString("jumin");
                                mem_kind = Response.getJSONObject(0).getString("kind");
                                mem_join_date = Response.getJSONObject(0).getString("join_date");
                                mem_state = Response.getJSONObject(0).getString("state");
                                mem_jikgup = Response.getJSONObject(0).getString("jikgup");
                                mem_pay = Response.getJSONObject(0).getString("pay");

                                dlog.i("------UserCheck-------");
                                USER_INFO_ID = mem_id;
                                dlog.i("프로필 사진 url : " + mem_img_path);
                                dlog.i("직원소속구분분 : " + (mem_kind.equals("0") ? "정직원" : "협력업체"));
                                dlog.i("성명 : " + mem_name);
                                dlog.i("부서 : " + mem_jikgup);
                                dlog.i("급여 : " + mem_pay);
                                dlog.i("------UserCheck-------");

                                binding.userName.setText(mem_name + "|" + mem_jikgup);

                            } catch (Exception e) {
                                dlog.i("UserCheck Exception : " + e);
                            }
                        } catch (Exception e) {
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

    public void AddStroeNoti() {
        dlog.i("-----AddStroeNoti Check-----");
        dlog.i("title : " + noti_title);
        dlog.i("content : " + noti_contents);
        dlog.i("link : " + noti_link);
        dlog.i("Profile Url : " + ProfileUrl);
        dlog.i("EventStart : " + binding.eventStarttime.getText().toString());
        dlog.i("EventEnd : " + binding.eventEndttime.getText().toString());
        dlog.i("-----AddStroeNoti Check-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiAddInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiAddInterface api = retrofit.create(FeedNotiAddInterface.class);
        Call<String> call = api.getData(place_id, noti_title, noti_contents, USER_INFO_ID, noti_link, ProfileUrl, "",noti_event_start,noti_event_end,"1","","","");
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
                            try {
                                if (!response.body().equals("[]") && response.body().replace("\"", "").equals("success")) {
                                    if (!ProfileUrl.isEmpty()) {
                                        saveBitmapAndGetURI();
                                    }
                                    dlog.i("inmember size : " + inmember.size());
                                    if(inmember.size() > 0){
                                        SendUserCheck();
                                    }
                                    Toast.makeText(mContext, "매장 공지사항 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    pm.FeedList(mContext);
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

    private boolean DataCheck() {
        /*
            1. 제목
            2. 내용
            3. 링크
            4. 사진
       * */
        noti_title = binding.inputWorktitle.getText().toString();
        noti_contents = binding.inputWorkcontents.getText().toString();
        noti_link = binding.inputMovelink.getText().toString();
        noti_event_start = binding.eventStarttime.getText().toString();
        noti_event_end = binding.eventEndttime.getText().toString();

        if (noti_title.isEmpty()) {
            Toast.makeText(mContext, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (noti_contents.isEmpty()) {
            Toast.makeText(mContext, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
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


    //이미지 업로드에 필요한 소스 START
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_CODE) {
            if (resultCode == RESULT_OK) {

                String imagePath = "";
                try {
                    //1) data의 주소 사용하는 방법
                    imagePath = data.getDataString(); // "content://media/external/images/media/7215"

                    Glide.with(this)
                            .load(imagePath)
                            .into(binding.notiSetimg);
                    binding.clearImg.setVisibility(View.VISIBLE);
                    binding.imgPlus.setVisibility(View.GONE);

                    Glide.with(getApplicationContext()).asBitmap().load(imagePath).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            saveBitmap = resource;
                        }
                    });

                    final String IMG_FILE_EXTENSION = ".JPEG";
                    String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
                    ProfileUrl = "http://krafte.net/NEBWorks/image/feed_img/" + file_name;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                binding.imgPlus.setVisibility(View.VISIBLE);
                binding.clearImg.setVisibility(View.GONE);
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    //절대경로를 구한다.
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        dlog.i("columnIndex = " + columnIndex);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);


        cursor.close();
        return url;
    }

    @SuppressLint({"SimpleDateFormat", "LongLogTag"})
    public Uri saveBitmapAndGetURI() {
        //Create Bitmap
        binding.loginAlertText.setVisibility(View.VISIBLE);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        //Create Bitmap -> File
        final String IMG_FILE_EXTENSION = ".JPEG";
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
        String fullFileName = BACKUP_PATH;

        dlog.i("(saveBitmapAndGetURI)ex_storage : " + ex_storage);
        dlog.i("(saveBitmapAndGetURI)USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("(saveBitmapAndGetURI)fullFileName : " + fullFileName);

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

            ProfileUrl = "http://krafte.net/NEBWorks/image/feed_img/" + file_name;
            saveBitmapToFile(file);

            dlog.e("사인 저장 경로 : " + ProfileUrl);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            RetrofitInterface retrofitInterface = ApiClient.getApiClient().create(RetrofitInterface.class);
            Call<String> call = retrofitInterface.request(body);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("uploaded_file()", "성공 : call = " + call + "response = " + response);

                    if (fileDelete(String.valueOf(file))) {
                        Log.e("uploaded_file()", "기존 이미지 삭제 완료");
                    } else {
                        Log.e("uploaded_file()", "이미지 삭제 오류");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("uploaded_file()", "에러 : " + t.getMessage());
                }
            });
            Log.d("(saveBitmapAndGetURI)이미지 경로 : ", Uri.fromFile(file).toString());

            out.close();
            binding.loginAlertText.setVisibility(View.GONE);
            dlog.i("(saveBitmapAndGetURI)file : " + file);
        } catch (FileNotFoundException exception) {
            dlog.e("FileNotFoundException : " + exception.getMessage());
        } catch (IOException exception) {
            dlog.e("IOException : " + exception.getMessage());
        }
        return null;
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
            Log.e("PlaceAddActivity fileDelete", e.getMessage());
        }
        return false;
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
        @POST("upload_feed_img.php")
        Call<String> request(@Part MultipartBody.Part file);
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

    /* -- 할일 추가 FCM 전송 영역 */
    String message = "";
    String click_action = "";
    String tag = "";
    private void SendUserCheck() {
        List<String> member = new ArrayList<>();
        dlog.i("보내야 하는 직원 배열 :" + inmember);
//        member.addAll(Arrays.asList(user_id.split(",")));
        dlog.i("보내야 하는 직원 수 :" + inmember.size());
        dlog.i("보내야 하는 직원 List<String>  :" + inmember);

        message = "[공지사항] :" + binding.title.getText().toString();
        click_action = "PlaceWorkFragment";
        tag = "0";
        for (int a = 0; a < inmember.size(); a++) {
            if (place_owner_id.equals(inmember.get(a))) {
                getManagerToken(inmember.get(a), "0", place_id, place_name);
            } else {
                getManagerToken(inmember.get(a), "1", place_id, place_name);
            }
        }
    }

    public void getManagerToken(String user_id, String type, String place_id, String place_name) {
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
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
                    if (Response.length() > 0) {
                        dlog.i("-----getManagerToken-----");
                        dlog.i("user_id : " + Response.getJSONObject(0).getString("user_id"));
                        dlog.i("token : " + Response.getJSONObject(0).getString("token"));
                        String id = Response.getJSONObject(0).getString("id");
                        String token = Response.getJSONObject(0).getString("token");
                        String department = shardpref.getString("USER_INFO_SOSOK", "");
                        String position = shardpref.getString("USER_INFO_JIKGUP", "");
                        String name = shardpref.getString("USER_INFO_NAME", "");
                        dlog.i("-----getManagerToken-----");
                        boolean channelId = Response.getJSONObject(0).getString("channel2").equals("1");
                        if (!token.isEmpty() && channelId) {
                            PushFcmSend(id, "", message, token, tag, place_id);
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

    DBConnection dbConnection = new DBConnection();
    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            dbConnection.FcmTestFunction(topic, title, message, token, click_action, tag, place_id);
            runOnUiThread(() -> {
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /* -- 할일 추가 FCM 전송 영역 */
}
