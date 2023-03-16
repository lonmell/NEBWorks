package com.krafte.nebworks.ui.worksite;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MultiImageAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.dataInterface.PushLogInputInterface;
import com.krafte.nebworks.dataInterface.TaskApprovalInterface;
import com.krafte.nebworks.dataInterface.TaskSaveInterface;
import com.krafte.nebworks.databinding.ActivityTaskReportBinding;
import com.krafte.nebworks.ui.community.CommunityAddActivity;
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

/*
 * 2022-11-24 방창배 작성
 * USER_INFO_AUTH = 1 근로자가 업무보고작성하러 들어오는 페이지
 * */
public class TaskReportActivity extends AppCompatActivity {
    private static final String TAG = "TaskReportActivity";
    private ActivityTaskReportBinding binding;
    Context mContext;

    int GALLEY_CODE = 10;

    // shared 저장값
    PreferenceHelper shardpref;

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String place_management_office = "";
    String place_address = "";
    String place_latitude = "";
    String place_longitude = "";
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";
    String task_no = "";
    String reject_reason = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    PageMoveClass pm = new PageMoveClass();
    GetResultData resultData = new GetResultData();
    MultiImageAdapter adapter;

    Dlog dlog = new Dlog();
    String user_id = "";
    String usersn = "";
    String usersimg = "";
    String usersjikgup = "";
    String WorkTitle = "";
    String WorkContents = "";
    String writer_id = "";
    String WorkDay = "";
    String approval_state = "";
    int make_kind = 0;

    String TaskKind = "1";
    String start_time = "-99";
    String end_time = "-99";
    String Sun = "0", Mon = "0", Tue = "0", Wed = "0", Thu = "0", Fri = "0", Sat = "0";

    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    File file;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/krafte/";
    String return_page = "";
    String imagePath = "";

    List<String> ProfileUrl = new ArrayList<>();
    ArrayList<Uri> uriList = new ArrayList<>();// 이미지의 uri를 담을 ArrayList 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityTaskReportBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            //Singleton Area
            place_id            = shardpref.getString("place_id", PlaceCheckData.getInstance().getPlace_id());
            place_name          = shardpref.getString("place_name", PlaceCheckData.getInstance().getPlace_name());
            place_owner_id      = shardpref.getString("place_owner_id", PlaceCheckData.getInstance().getPlace_owner_id());
            place_owner_name    = shardpref.getString("place_owner_name", PlaceCheckData.getInstance().getPlace_owner_name());
            place_address       = shardpref.getString("place_address", PlaceCheckData.getInstance().getPlace_address());
            place_latitude      = shardpref.getString("place_latitude", PlaceCheckData.getInstance().getPlace_latitude());
            place_longitude     = shardpref.getString("place_longitude", PlaceCheckData.getInstance().getPlace_longitude());
            place_start_time    = shardpref.getString("place_state_time", PlaceCheckData.getInstance().getPlace_start_time());
            place_end_time      = shardpref.getString("place_end_time", PlaceCheckData.getInstance().getPlace_end_time());
            place_img_path      = shardpref.getString("place_img_path", PlaceCheckData.getInstance().getPlace_img_path());
            place_start_date    = shardpref.getString("place_state_date", PlaceCheckData.getInstance().getPlace_start_date());
            place_created_at    = shardpref.getString("place_created_at", PlaceCheckData.getInstance().getPlace_created_at());
            return_page         = shardpref.getString("return_page", ReturnPageData.getInstance().getPage());

            USER_INFO_ID        = shardpref.getString("USER_INFO_ID", UserCheckData.getInstance().getUser_id());
            USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");

            //shardpref Area
            make_kind = shardpref.getInt("make_kind", 0);
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 1);
            setBtnEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    InputMethodManager imm;

    @Override
    public void onResume() {
        super.onResume();
        dlog.i("-----getTaskContents START-----");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        task_no         = shardpref.getString("task_no", "0");
        reject_reason   = shardpref.getString("reject_reason", "0");
        writer_id       = shardpref.getString("writer_id", "0");
        WorkTitle       = shardpref.getString("title", "0");
        WorkContents    = shardpref.getString("contents", "0");
        TaskKind        = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
        user_id         = shardpref.getString("users", "0");
        usersn          = shardpref.getString("usersn", "0");
        usersimg        = shardpref.getString("usersimg", "0");
        usersjikgup     = shardpref.getString("usersjikgup", "0");
        WorkDay         = shardpref.getString("task_date", "0");
        start_time      = shardpref.getString("start_time", "0");
        end_time        = shardpref.getString("end_time", "0");
        Sun             = shardpref.getString("sun", "0");
        Mon             = shardpref.getString("mon", "0");
        Tue             = shardpref.getString("tue", "0");
        Wed             = shardpref.getString("wed", "0");
        Thu             = shardpref.getString("thu", "0");
        Fri             = shardpref.getString("fri", "0");
        Sat             = shardpref.getString("sat", "0");
        approval_state  = shardpref.getString("approval_state", "0");// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전

        dlog.i("getTaskContents task_no : " + task_no);
        dlog.i("getTaskContents users : " + user_id);
        dlog.i("getTaskContents usersn : " + usersn);
        dlog.i("getTaskContents usersimg : " + usersimg);
        dlog.i("getTaskContents complete_kind : " + TaskKind);
        dlog.i("getTaskContents Mon : " + Mon);
        dlog.i("getTaskContents Tue : " + Tue);
        dlog.i("getTaskContents Wed : " + Wed);
        dlog.i("getTaskContents Thu : " + Thu);
        dlog.i("getTaskContents Fri : " + Fri);
        dlog.i("getTaskContents Sat : " + Sat);
        dlog.i("getTaskContents Sun : " + Sun);
        dlog.i("getTaskContents start_time : " + start_time);
        dlog.i("getTaskContents end_time : " + end_time);
        dlog.i("getTaskContents approval_state : " + approval_state);// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전
        dlog.i("getTaskContents reject_reason : " + reject_reason);

        if(approval_state.equals("2")){
            binding.rejectStateArea.setVisibility(View.VISIBLE);
            binding.rejectReasonTv.setText(reject_reason);
        }else{
            binding.rejectStateArea.setVisibility(View.GONE);
        }
        if (end_time.length() >= 10) {
            //반복x ( 0000.00.00 00:00 )
            binding.writeTime.setText(dc.GET_TIME.substring(11, 16));
        } else {
            //반복o ( 00:00 )
            binding.writeTime.setText(dc.GET_TIME.replace("-","."));
        }
        if (TaskKind.equals("0")) {
            binding.taskKind00.setVisibility(View.VISIBLE);
            binding.taskKind01.setVisibility(View.GONE);
        } else {
            binding.taskKind00.setVisibility(View.GONE);
            binding.taskKind01.setVisibility(View.VISIBLE);
        }
        ImgfileMaker = ImageNameMaker();
        dlog.i("uriList : " + uriList);
        dlog.i("uriList size : " + uriList.size());
        if (String.valueOf(uriList).equals("[]")) {
            binding.clearImg.setVisibility(View.GONE);
        } else {
            binding.clearImg.setVisibility(View.VISIBLE);
        }
        dlog.i("-----getTaskContents END-----");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    DateCurrent dc = new DateCurrent();
    String complete_yn = "y";

    private void setBtnEvent() {

        binding.bottomBtn.setOnClickListener(v -> {
            String inputImage = String.valueOf(ProfileUrl).replace("[", "").replace("]", "").replace(" ", "");
            BtnOneCircleFun(false);
            String task_id = task_no;
            String task_date = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            String incomplete_reason = binding.contents.getText().toString();
            dlog.i("------binding.bottomBtn onClick Event------");
            dlog.i("task_id : "             + task_id);
            dlog.i("task_date : "           + task_date);
            dlog.i("incomplete_reason : "   + incomplete_reason);
            dlog.i("------binding.bottomBtn onClick Event------");
            //0:체크 1:사진
            if (TaskKind.equals("0")) {
                setSaveTask(task_id, task_date, inputImage, complete_yn, incomplete_reason);
            } else {
                if (incomplete_reason.isEmpty()) {
                    BtnOneCircleFun(true);
                    Toast.makeText(mContext, "보고사항을 추가해주세요.", Toast.LENGTH_SHORT).show();
                }else if (inputImage.isEmpty()) {
                    BtnOneCircleFun(true);
                    Toast.makeText(mContext, "보고할 사진을 추가해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    setSaveTask(task_id, task_date, inputImage, "y", incomplete_reason);
                }
            }

        });
//        binding.taskKind01.setOnClickListener(v -> {
//            permissionCheck();
//        });

        binding.select01Box.setOnClickListener(v -> {
            complete_yn = "y";
            binding.select01Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select01Box.setBackgroundResource(R.drawable.default_select_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select02Box.setBackgroundResource(R.drawable.default_gray_round);
        });
        binding.select02Box.setOnClickListener(v -> {
            complete_yn = "n";
            binding.select01Round.setBackgroundResource(R.drawable.ic_empty_round);
            binding.select01Box.setBackgroundResource(R.drawable.default_gray_round);
            binding.select02Round.setBackgroundResource(R.drawable.ic_full_round);
            binding.select02Box.setBackgroundResource(R.drawable.default_select_round);
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
                .setDeniedMessage("[설정] > [권한]에서 파일 이용권한이 필요합니다")
                .setPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public void setSaveTask(String task_id, String task_date, String img_path, String complete_yn, String incomplete_reason) {
        dlog.i("------setSaveTask------");
        dlog.i("task_id : " + task_id);
        dlog.i("task_title : " + WorkTitle);
        dlog.i("task_date : " + task_date);
        dlog.i("img_path : " + img_path);
        dlog.i("complete_yn : " + complete_yn);
        dlog.i("incomplete_reason : " + incomplete_reason);
        dlog.i("------setSaveTask------");
        img_path = img_path.equals("null") ? "" : img_path;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSaveInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSaveInterface api = retrofit.create(TaskSaveInterface.class);
        Call<String> call = api.getData(task_id, WorkTitle, task_date, img_path, complete_yn, incomplete_reason);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            runOnUiThread(() -> {
                                dlog.i("resultData : " + resultData.getRESULT());
                                if (jsonResponse.replace("\"", "").equals("success")) {
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
                                    setUpdateWorktodo(task_id);
//                                    //근로자일때 -- 저장할때는 알림 필요없음
//                                    topic = task_id;
//                                    message = "업무 결제요청이 도착하였습니다";
//                                    click_action = "PlaceWorkFragment";
//                                    Log.i(TAG, "task_input_id : " + writer_id);
//                                    Log.i(TAG, "task_conduct_id : " + USER_INFO_ID);
//                                    getPushBoolean();
                                } else {
                                    BtnOneCircleFun(true);
                                    Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
                                }
                            });
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
                        ProfileUrl.add("https://nepworks.net/NEBWorks/image/feed_img/" + file_name);
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
                                    ProfileUrl.add("https://nepworks.net/NEBWorks/image/feed_img/" + file_name);
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


    private void setUpdateWorktodo(String task_id) {
        dlog.i("setUpdateWorktodo user_id : " + task_id);
        String task_date = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskApprovalInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskApprovalInterface api = retrofit.create(TaskApprovalInterface.class);
        Call<String> call = api.getData(place_id, task_id, task_date, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            if (jsonResponse.replace("\"", "").equals("success")) {
                                Toast_Nomal("결재 요청이 완료되었습니다.");
                                String message = "["+WorkTitle+"]의 결재 요청이 도착했습니다.";
                                AddPush("업무보고",message,place_owner_id);
                                getUserToken(place_owner_id,"0",message);
                                shardpref.putInt("SELECT_POSITION", 1);
                                shardpref.putInt("SELECT_POSITION_sub", 0);
                                if(USER_INFO_AUTH.equals("0")){
                                    pm.PlaceWorkBack(mContext);
                                }else{
                                    shardpref.putInt("SELECT_POSITION", 1);
                                    shardpref.putInt("SELECT_POSITION_sub", 0);
                                    pm.Main2(mContext);
                                }

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


    //근로자 > 점주
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
                            PushFcmSend(id, "", message, token, "1", place_id);
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
        Call<String> call = api.getData(place_id, "", title, content, place_owner_id, user_id);
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

    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "TaskApprovalFragment";
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
    //절대경로를 구한다.
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
        String file_name = ProfileUrl.get(i).replace("https://nepworks.net/NEBWorks/image/feed_img/", "");
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
            CommunityAddActivity.RetrofitInterface retrofitInterface = CommunityAddActivity.ApiClient.getApiClient().create(CommunityAddActivity.RetrofitInterface.class);
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
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public static class ApiClient {
        private static final String BASE_URL = "https://nepworks.net/NEBWorks/image/";
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
        @POST("upload_task_img.php")
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
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
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

    RetrofitConnect rc = new RetrofitConnect();
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

    public void Toast_Nomal(String message) {
        BtnOneCircleFun(true);
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

    private void BtnOneCircleFun(boolean tf){
        binding.bottomBtn.setClickable(tf);
        binding.bottomBtn.setEnabled(tf);
    }
}
