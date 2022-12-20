package com.krafte.nebworks.ui.worksite;

import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.dataInterface.FCMSelectInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.dataInterface.TaskApprovalInterface;
import com.krafte.nebworks.dataInterface.TaskSaveInterface;
import com.krafte.nebworks.databinding.ActivityTaskReportBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

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

/*
 * 2022-11-24 방창배 작성
 * USER_INFO_AUTH = 1 근로자가 업무보고작성하러 들어오는 페이지
 * */
public class TaskReportActivity extends AppCompatActivity {
    private static final String TAG = "TaskReportActivity";
    private ActivityTaskReportBinding binding;
    Context mContext;

    int GALLEY_CODE = 10;
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private static final int PINSELECT_LOCATION_ACTIVITY = 20000;

    // shared 저장값
    PreferenceHelper shardpref;
    boolean channelId1 = false;
    boolean channelId2 = false;
    boolean EmployeeChannelId1 = false;

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
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    PageMoveClass pm = new PageMoveClass();
    GetResultData resultData = new GetResultData();

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

    String message = "업무가 배정되었습니다.";
    Drawable icon_on, icon_off;
    String searchDate = "";

    String fileName = "";
    private Bitmap saveBitmap;
    String ImgfileMaker = "";
    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/nebworks/";
    String ProfileUrl = "";
    String return_page = "";

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

            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            shardpref.putInt("SELECT_POSITION_sub", 1);
            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            place_owner_name = shardpref.getString("place_owner_name", "0");
            place_management_office = shardpref.getString("place_management_office", "0");
            place_address = shardpref.getString("place_address", "0");
            place_latitude = shardpref.getString("place_latitude", "0");
            place_longitude = shardpref.getString("place_longitude", "0");
            place_start_time = shardpref.getString("place_start_time", "0");
            place_end_time = shardpref.getString("place_end_time", "0");
            place_img_path = shardpref.getString("place_img_path", "0");
            place_start_date = shardpref.getString("place_start_date", "0");
            place_created_at = shardpref.getString("place_created_at", "0");
            return_page = shardpref.getString("return_page", "0");
            make_kind = shardpref.getInt("make_kind", 0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-1");
            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 1);

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
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
        task_no = shardpref.getString("task_no", "0");
        writer_id = shardpref.getString("writer_id", "0");
        WorkTitle = shardpref.getString("title", "0");
        WorkContents = shardpref.getString("contents", "0");
        TaskKind = shardpref.getString("complete_kind", "0");            // 0:체크, 1:사진
        user_id = shardpref.getString("users", "0");
        usersn = shardpref.getString("usersn", "0");
        usersimg = shardpref.getString("usersimg", "0");
        usersjikgup = shardpref.getString("usersjikgup", "0");
        WorkDay = shardpref.getString("task_date", "0");
        start_time = shardpref.getString("start_time", "0");
        end_time = shardpref.getString("end_time", "0");
        Sun = shardpref.getString("sun", "0");
        Mon = shardpref.getString("mon", "0");
        Tue = shardpref.getString("tue", "0");
        Wed = shardpref.getString("wed", "0");
        Thu = shardpref.getString("thu", "0");
        Fri = shardpref.getString("fri", "0");
        Sat = shardpref.getString("sat", "0");
        approval_state = shardpref.getString("approval_state", "0");// 0: 결재대기, 1:승인, 2:반려, 3:결재요청 전


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

        if (end_time.length() >= 10) {
            //반복x ( 0000.00.00 00:00 )
            binding.writeTime.setText(dc.GET_TIME.substring(11, 16));
        } else {
            //반복o ( 00:00 )
            binding.writeTime.setText(dc.GET_TIME.replace("-","."));
        }
        if (TaskKind.equals("0")) {
            binding.taskKind00.setVisibility(View.VISIBLE);
        } else {
            binding.taskKind01.setVisibility(View.VISIBLE);
        }
        ImgfileMaker = ImageNameMaker();
        dlog.i("saveBitmap : " + saveBitmap);
        if (saveBitmap != null) {
            binding.clearImg.setVisibility(View.VISIBLE);
        } else {
            binding.clearImg.setVisibility(View.GONE);
        }
        binding.clearImg.setOnClickListener(v -> {
            try {
                saveBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                saveBitmap.eraseColor(Color.TRANSPARENT);
                binding.taskKind01img.setImageBitmap(saveBitmap);
                ProfileUrl = "";
                binding.clearImg.setVisibility(View.GONE);
            } catch (Exception e) {
                dlog.i("clearImg Exception : " + e);
            }
        });
        dlog.i("-----getTaskContents END-----");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    DateCurrent dc = new DateCurrent();
    String complete_yn = "y";

    private void setBtnEvent() {
//        binding.bottomBtn.setOnClickListener(v -> {
//            Toast_Nomal("업무보고");
//        });
        binding.bottomBtn.setOnClickListener(v -> {
            String task_id = task_no;
            String task_date = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            String reject_reason = binding.contents.getText().toString();
            //0:체크 1:사진
            if (TaskKind.equals("0")) {
                setSaveTask(task_id, task_date, ProfileUrl, complete_yn, reject_reason);
            } else {
                if (ProfileUrl != null) {
                    setSaveTask(task_id, task_date, ProfileUrl, "y", reject_reason);
                } else {
                    if (!reject_reason.isEmpty()) {
                        setSaveTask(task_id, task_date, ProfileUrl, "y", reject_reason);
                    } else {
                        Toast.makeText(mContext, "매장 사진을 추가해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        binding.taskKind01.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLEY_CODE);
        });

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
    }

    public void setSaveTask(String task_id, String task_date, String img_path, String complete_yn, String reject_reason) {
        dlog.i("------setSaveTask------");
        dlog.i("task_id : " + task_id);
        dlog.i("task_date : " + task_date);
        dlog.i("img_path : " + img_path);
        dlog.i("complete_yn : " + complete_yn);
        dlog.i("reject_reason : " + reject_reason);
        dlog.i("------setSaveTask------");
        img_path = img_path.equals("null") ? "" : img_path;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSaveInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSaveInterface api = retrofit.create(TaskSaveInterface.class);
        Call<String> call = api.getData(task_id, task_date, img_path, complete_yn, reject_reason);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            runOnUiThread(() -> {
                                dlog.i("resultData : " + resultData.getRESULT());
                                if (response.body().replace("\"", "").equals("success")) {
                                    dlog.i("ProfileUrl : " + ProfileUrl);
                                    dlog.i("saveBitmap : " + saveBitmap);
                                    if (!ProfileUrl.isEmpty() && saveBitmap != null) {
                                        saveBitmapAndGetURI();
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
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dlog.i("resultCode : " + resultCode);
        dlog.i("RESULT_OK : " + RESULT_OK);
        if (requestCode == GALLEY_CODE) {
            if (resultCode == RESULT_OK) {

                String imagePath = "";
                try {
                    //1) data의 주소 사용하는 방법
                    imagePath = data.getDataString(); // "content://media/external/images/media/7215"

                    Glide.with(this)
                            .load(imagePath)
                            .into(binding.taskKind01img);

                    Glide.with(getApplicationContext()).asBitmap().load(imagePath).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            saveBitmap = resource;
                        }
                    });
                    final String IMG_FILE_EXTENSION = ".JPEG";
                    String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
                    ProfileUrl = "http://krafte.net/NEBWorks/image/task_img/" + file_name;
//                    if (saveBitmap != null) {
//                        binding.clearImg.setVisibility(View.VISIBLE);
//                    } else {
//                        binding.clearImg.setVisibility(View.GONE);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                binding.clearImg.setVisibility(View.VISIBLE);
            } else if (resultCode == RESULT_CANCELED) {
                binding.clearImg.setVisibility(View.GONE);
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
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
                            dlog.i("setUpdateWorktodo jsonResponse length : " + response.body().length());
                            dlog.i("setUpdateWorktodo jsonResponse : " + response.body());
//                            dlog.i("http://krafte.net/kogas/task_approval/post.php?place_id="+place_id+"&task_id="+task_id+"&task_date="+task_date+"&user_id="+USER_INFO_ID);
                            if (response.body().replace("\"", "").equals("success")) {
                                Toast_Nomal("결재 요청이 완료되었습니다.");
                                String message = "["+WorkTitle+"]의 결재 요청이 도착했습니다.";
                                getUserToken(place_owner_id,"0",message);
                                shardpref.putInt("SELECT_POSITION", 1);
                                shardpref.putInt("SELECT_POSITION_sub", 0);
                                if(USER_INFO_AUTH.equals("0")){
                                    pm.PlaceWorkBack(mContext);
                                }else{
                                    pm.TaskList(mContext);
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


    //근로자 > 점주 ( 초대수락 FCM )
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
                dlog.i("Response Result : " + response.body());
                try {
                    JSONArray Response = new JSONArray(response.body());
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


    DBConnection dbConnection = new DBConnection();
    String click_action = "";

    private void PushFcmSend(String topic, String title, String message, String token, String tag, String place_id) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "PlaceListActivity";
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

            ProfileUrl = "http://krafte.net/NEBWorks/image/task_img/" + file_name;
            saveBitmapToFile(file);

            dlog.e("사인 저장 경로 : " + ProfileUrl);
//            binding.loCanvas.setImageBitmap(ProfileUrl);
            Glide.with(mContext).load(ProfileUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(binding.taskKind01img);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            PlaceWorkDetailActivity.RetrofitInterface retrofitInterface = PlaceWorkDetailActivity.ApiClient.getApiClient().create(PlaceWorkDetailActivity.RetrofitInterface.class);
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
