package com.krafte.nebworks.ui.feed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MultiImageAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FeedNotiEditInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.databinding.ActivityPlacenotiAddBinding;
import com.krafte.nebworks.pop.PhotoPopActivity;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class FeedEditActivity extends AppCompatActivity {
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
    String feed_id = "";

    ArrayList<Uri> uriList = new ArrayList<>();// 이미지의 uri를 담을 ArrayList 객체
    MultiImageAdapter adapter;

    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/krafte/";
    List<String> ProfileUrl = new ArrayList<>();
    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    Drawable icon_off;
    Drawable icon_on;

    String Time01 = "-99";
    String Time02 = "-99";

    //Check Data
    String noti_title, noti_contents, noti_link, noti_event_start, noti_event_end;

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

        icon_on = mContext.getResources().getDrawable(R.drawable.ic_full_round_check);
        icon_off = mContext.getResources().getDrawable(R.drawable.resize_service_off);
        shardpref.putInt("SELECT_POSITION", 0);
        setBtnEvent();

        //UI 데이터 세팅
        try {
            //Singleton Area
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
            place_id        = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());

            //shardpref Area
            feed_id = shardpref.getString("edit_feed_id", "0");

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            UserCheck();
            GETFeed();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        ImgfileMaker = ImageNameMaker();
        if (String.valueOf(uriList).equals("[]")) {
            binding.clearImg.setVisibility(View.GONE);
        } else {
            binding.clearImg.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        pm.FeedList(mContext);
    }

    String toDay = "";
    String Year = "";
    String Month = "";
    String Day = "";
    String getDatePicker = "";
    String getYMPicker = "";
    boolean FIXYN = false;

    public void setBtnEvent() {
        binding.closeBtn.setOnClickListener(v -> {
            pm.FeedList(mContext);
        });

        binding.workSave.setText("공지 수정하기");
        binding.workSave.setOnClickListener(v -> {
            dlog.i("DataCheck() : " + DataCheck());
            BtnOneCircleFun(false);
            if (DataCheck()) {
                AddStroeNoti();
            }
        });

        binding.closeBtn.setOnClickListener(v -> {
            pm.FeedList(mContext);
        });

        binding.clearImg.setOnClickListener(v -> {
            saveBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
            saveBitmap.eraseColor(Color.TRANSPARENT);
            ProfileUrl.clear();
            uriList.clear();
            adapter.notifyDataSetChanged();
            if (uriList.size() == 0) {
                binding.clearImg.setVisibility(View.GONE);
            } else {
                binding.clearImg.setVisibility(View.VISIBLE);
            }
        });

        //------게시글 이미지 등록 / 갤러리 열기
        binding.limitImg.setOnClickListener(v -> {
            permissionCheck();
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
            }
        });

        binding.selectFix.setOnClickListener(v -> {
            if(!FIXYN){
                FIXYN = true;
                binding.selectFixRound.setBackgroundResource(R.drawable.ic_full_round_check);
                binding.selectFix.setBackgroundResource(R.drawable.default_select_on_round);
                binding.selectFixTv.setTextColor(Color.parseColor("#000000"));
            }else{
                FIXYN = false;
                binding.selectFixRound.setBackgroundResource(R.drawable.ic_empty_round);
                binding.selectFix.setBackgroundResource(R.drawable.default_gray_round);
                binding.selectFixTv.setTextColor(Color.parseColor("#54585A"));
            }
        });
    }


    private void permissionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLEY_CODE);
                dlog.i("permissionCheck() : Permission Granted");
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(mContext, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                dlog.i("permissionCheck() : Permission Denied");
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("파일 이용권한을 허용해주세요")
                .setPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
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
        try{
            mem_id = UserCheckData.getInstance().getUser_id();
            mem_name = UserCheckData.getInstance().getUser_name();
            mem_phone = UserCheckData.getInstance().getUser_phone();
            mem_gender = UserCheckData.getInstance().getUser_gender();
            mem_img_path = UserCheckData.getInstance().getUser_img_path();
            mem_jumin = UserCheckData.getInstance().getUser_jumin();
            mem_kind = UserCheckData.getInstance().getUser_kind();
            mem_join_date = UserCheckData.getInstance().getUser_join_date();
            mem_state = UserCheckData.getInstance().getUser_state();
            mem_jikgup = UserCheckData.getInstance().getUser_jikgup();
            mem_pay = UserCheckData.getInstance().getUser_pay();

            dlog.i("------UserCheck-------");
            USER_INFO_ID = mem_id;
            dlog.i("프로필 사진 url : " + mem_img_path);
            dlog.i("성명 : " + mem_name);
            dlog.i("부서 : " + mem_jikgup);
            dlog.i("급여 : " + mem_pay);
            dlog.i("------UserCheck-------");

            binding.userName.setText(mem_name + "|" + mem_jikgup);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void GETFeed() {
        dlog.i("GETFeed place_id : " + place_id);
        dlog.i("GETFeed feed_id : " + feed_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, feed_id, "","1",USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "CheckResult"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse!!: " + jsonResponse);
                            dlog.i("GETFeed jsonResponse length : " + response.body().length());
                            dlog.i("GETFeed jsonResponse : " + response.body());
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    String id = Response.getJSONObject(0).getString("id");
                                    String place_id = Response.getJSONObject(0).getString("place_id");
                                    String title = Response.getJSONObject(0).getString("title");
                                    String contents = Response.getJSONObject(0).getString("contents");
                                    String writer_id = Response.getJSONObject(0).getString("writer_id");
                                    String writer_name = Response.getJSONObject(0).getString("writer_name");
                                    String writer_img_path = Response.getJSONObject(0).getString("writer_img_path");

                                    String jikgup = Response.getJSONObject(0).getString("jikgup");
                                    String view_cnt = Response.getJSONObject(0).getString("view_cnt");
                                    String comment_cnt = Response.getJSONObject(0).getString("comment_cnt");
                                    String link = Response.getJSONObject(0).getString("link");
                                    String feed_img_path = Response.getJSONObject(0).getString("feed_img_path");
                                    String created_at = Response.getJSONObject(0).getString("created_at");
                                    String updated_at = Response.getJSONObject(0).getString("updated_at");

                                    String open_date = Response.getJSONObject(0).getString("open_date");
                                    String close_date = Response.getJSONObject(0).getString("close_date");

                                    String fix_yn = Response.getJSONObject(0).getString("fix_yn");
                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                                    requestOptions.skipMemoryCache(false);
                                    requestOptions.placeholder(R.drawable.no_image);
                                    requestOptions.signature(new ObjectKey(System.currentTimeMillis()));

                                    try {
                                        new Thread(() -> {
                                            saveBitmap = getBitmap(feed_img_path);
                                        }).start();
                                        runOnUiThread(() -> {
                                            binding.inputWorktitle.setText(title);
                                            binding.userName.setText(writer_name + "| " + jikgup);

                                            Glide.with(mContext).load(writer_img_path)
                                                    .apply(requestOptions)
                                                    .into(binding.profileImg);

                                            binding.inputWorkcontents.setText(contents);
                                            binding.inputMovelink.setText(link);

                                            for (String s : feed_img_path.split(",")) {
                                                uriList.add(Uri.parse(s));
                                            }
                                            adapter = new MultiImageAdapter(uriList, getApplicationContext());
                                            binding.imgList.setAdapter(adapter);
                                            binding.imgList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                            adapter.setOnClickListener(new MultiImageAdapter.OnClickListener() {
                                                @Override
                                                public void onClick(View v, int position) {
                                                    Intent intent = new Intent(mContext, PhotoPopActivity.class);
                                                    intent.putExtra("data", String.valueOf(uriList));
                                                    intent.putExtra("pos", position);
                                                    mContext.startActivity(intent);
                                                    ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                }
                                            });

                                            binding.eventStarttime.setText(open_date);
                                            binding.eventEndttime.setText(close_date);

                                            if(fix_yn.equals("y")){
                                                FIXYN = true;
                                                binding.selectFixRound.setBackgroundResource(R.drawable.ic_full_round_check);
                                                binding.selectFix.setBackgroundResource(R.drawable.default_select_on_round);
                                                binding.selectFixTv.setTextColor(Color.parseColor("#000000"));
                                            }else{
                                                FIXYN = false;
                                                binding.selectFixRound.setBackgroundResource(R.drawable.ic_empty_round);
                                                binding.selectFix.setBackgroundResource(R.drawable.default_gray_round);
                                                binding.selectFixTv.setTextColor(Color.parseColor("#54585A"));
                                            }
                                        });
                                    } catch (Exception e) {
                                        dlog.i("UserCheck Exception : " + e);
                                    }
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

    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;

        try {
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }

    public void AddStroeNoti() {
        String fix_yn = "";
        String inputImage = String.valueOf(ProfileUrl).replace("[", "").replace("]", "").replace(" ", "");
        dlog.i("-----AddStroeNoti Check-----");
        dlog.i("title : " + noti_title);
        dlog.i("content : " + noti_contents);
        dlog.i("link : " + noti_link);
        dlog.i("Profile Url : " + inputImage);
        dlog.i("EventStart : " + binding.eventStarttime.getText().toString());
        dlog.i("EventEnd : " + binding.eventEndttime.getText().toString());
        dlog.i("fix_yn : " + (FIXYN?"y":"n"));
        dlog.i("-----AddStroeNoti Check-----");
        fix_yn = (FIXYN?"y":"n");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiEditInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiEditInterface api = retrofit.create(FeedNotiEditInterface.class);
        Call<String> call = api.getData(feed_id, "",noti_title, noti_contents, USER_INFO_ID, noti_link, inputImage, "",noti_event_start,noti_event_end,"1","","","",fix_yn);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("AddStroeNoti Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                if (!jsonResponse.equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
                                    if (!ProfileUrl.isEmpty()) {
                                        if (uriList.size() == 1) {
                                            if (!uriList.get(0).toString().equals("")) {
                                                saveBitmapAndGetURI(0);
                                            }
                                        } else if (uriList.size() == 2) {
                                            if (!uriList.get(0).toString().equals("")) {
                                                saveBitmapAndGetURI(0);
                                            }
                                            if (!uriList.get(1).toString().equals("")) {
                                                saveBitmapAndGetURI(1);
                                            }
                                        } else if (uriList.size() == 3) {
                                            if (!uriList.get(0).toString().equals("")) {
                                                saveBitmapAndGetURI(0);
                                            }
                                            if (!uriList.get(1).toString().equals("")) {
                                                saveBitmapAndGetURI(1);
                                            }
                                            if (!uriList.get(2).toString().equals("")) {
                                                saveBitmapAndGetURI(2);
                                            }
                                        }

                                    }
                                    Toast.makeText(mContext, "매장 공지사항 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    BtnOneCircleFun(true);
                                    finish();
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
            BtnOneCircleFun(true);
            Toast.makeText(mContext, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (noti_contents.isEmpty()) {
            BtnOneCircleFun(true);
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
    List<String> cursor_url = new ArrayList<>();
    private Bitmap saveBitmap1;//첫번째 사진
    private Bitmap saveBitmap2;//두번째 사진
    private Bitmap saveBitmap3;//세번째 사진

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_CODE) {
            if (resultCode == RESULT_OK) {
                ProfileUrl.clear();
                uriList.clear();
                String imagePath1 = "";
                String imagePath2 = "";
                String imagePath3 = "";
                if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                    Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                } else {   // 이미지를 하나라도 선택한 경우
                    if (data.getClipData() == null) {     // 이미지를 하나만 선택한 경우
                        Log.e("single choice: ", String.valueOf(data.getData()));
                        ImgfileMaker = ImageNameMaker();
                        uriList = new ArrayList<>();
                        Uri imageUri = data.getData();
                        imagePath1 = data.getDataString();
                        uriList.add(imageUri);

                        adapter = new MultiImageAdapter(uriList, getApplicationContext());
                        binding.imgList.setAdapter(adapter);
                        binding.imgList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                        Glide.with(this)
                                .load(imagePath1)
                                .into(binding.hideimg01);
                        Glide.with(getApplicationContext()).asBitmap().load(imagePath1).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                saveBitmap1 = resource;
                            }
                        });

                        //파일 이름 만들기
                        final String IMG_FILE_EXTENSION = ".JPEG";
                        String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
                        ProfileUrl.add("http://krafte.net/NEBWorks/image/feed_img/" + file_name);
                        dlog.i("한개일때uriList : " + uriList);
                        dlog.i("한개일때 imgUrl: " + ProfileUrl);
                    } else {      // 이미지를 여러장 선택한 경우
                        ClipData clipData = data.getClipData();
                        Log.e("clipData", String.valueOf(clipData.getItemCount()));

                        if (clipData.getItemCount() > 3) {   // 선택한 이미지가 3장 이상인 경우
                            Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                        } else {   // 선택한 이미지가 1장 이상 10장 이하인 경우
                            Log.e(TAG, "multiple choice");
                            uriList = new ArrayList<>();

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                try {
                                    uriList.add(imageUri);  //uri를 list에 담는다.
                                    final String IMG_FILE_EXTENSION = ".JPEG";
                                    int ImgNum = Integer.parseInt(ImgfileMaker) + i;
                                    String file_name = USER_INFO_ID + "_" + ImgNum + IMG_FILE_EXTENSION;
                                    dlog.i("multiple choice file_name : " + file_name);
                                    ProfileUrl.add("http://krafte.net/NEBWorks/image/feed_img/" + file_name);
                                } catch (Exception e) {
                                    Log.e(TAG, "File select error", e);
                                }
                            }
                            dlog.i("여러개일때 uriList : " + uriList);
                            dlog.i("여러개일때 imgUrl : " + ProfileUrl);
                            dlog.i("여러개일때 ProfileUrl size : " + ProfileUrl.size());
                            if (uriList.size() == 1) {
                                if (!uriList.get(0).toString().equals("")) {
                                    Glide.with(this)
                                            .load(uriList.get(0).toString())
                                            .into(binding.hideimg01);
                                    Glide.with(getApplicationContext()).asBitmap().load(uriList.get(0).toString()).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            saveBitmap1 = resource;
                                        }
                                    });
                                }
                            } else if (uriList.size() == 2) {
                                if (!uriList.get(0).toString().equals("")) {
                                    Glide.with(this)
                                            .load(uriList.get(0).toString())
                                            .into(binding.hideimg01);
                                    Glide.with(getApplicationContext()).asBitmap().load(uriList.get(0).toString()).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            saveBitmap1 = resource;
                                        }
                                    });
                                }
                                if (!uriList.get(1).toString().equals("")) {
                                    Glide.with(this)
                                            .load(uriList.get(1).toString())
                                            .into(binding.hideimg02);
                                    Glide.with(getApplicationContext()).asBitmap().load(uriList.get(1).toString()).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            saveBitmap2 = resource;
                                        }
                                    });
                                }
                            } else if (uriList.size() == 3) {
                                if (!uriList.get(0).toString().equals("")) {
                                    Glide.with(this)
                                            .load(uriList.get(0).toString())
                                            .into(binding.hideimg01);
                                    Glide.with(getApplicationContext()).asBitmap().load(uriList.get(0).toString()).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            saveBitmap1 = resource;
                                        }
                                    });
                                }
                                if (!uriList.get(1).toString().equals("")) {
                                    Glide.with(this)
                                            .load(uriList.get(1).toString())
                                            .into(binding.hideimg02);
                                    Glide.with(getApplicationContext()).asBitmap().load(uriList.get(1).toString()).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            saveBitmap2 = resource;
                                        }
                                    });
                                }
                                if (!uriList.get(2).toString().equals("")) {
                                    Glide.with(this)
                                            .load(uriList.get(2).toString())
                                            .into(binding.hideimg03);
                                    Glide.with(getApplicationContext()).asBitmap().load(uriList.get(2).toString()).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            saveBitmap3 = resource;
                                        }
                                    });
                                }
                            }


                            adapter = new MultiImageAdapter(uriList, getApplicationContext());
                            binding.imgList.setAdapter(adapter);
                            binding.imgList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                        }
                    }
                }
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
    public Uri saveBitmapAndGetURI(int i) {
        //Create Bitmap
        binding.loginAlertText.setVisibility(View.VISIBLE);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        if (i == 0) {
            saveBitmap1.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        } else if (i == 1) {
            saveBitmap2.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        } else if (i == 2) {
            saveBitmap3.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        }
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        //Create Bitmap -> File
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String file_name = ProfileUrl.get(i).replace("http://krafte.net/NEBWorks/image/feed_img/", "");
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
            dlog.i("(saveBitmapAndGetURI)file_name : " + ProfileUrl.get(i));
            file = new File(file_path, file_name);
            FileOutputStream out = new FileOutputStream(file);
            if (i == 0) {
                saveBitmap1.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } else if (i == 1) {
                saveBitmap2.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } else if (i == 2) {
                saveBitmap3.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }

            saveBitmapToFile(file);

            dlog.e("사인 저장 경로 : " + ProfileUrl);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            FeedAddActivity.RetrofitInterface retrofitInterface = FeedAddActivity.ApiClient.getApiClient().create(FeedAddActivity.RetrofitInterface.class);
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

    private void BtnOneCircleFun(boolean tf){
        binding.workSave.setClickable(tf);
        binding.workSave.setEnabled(tf);
    }
}
