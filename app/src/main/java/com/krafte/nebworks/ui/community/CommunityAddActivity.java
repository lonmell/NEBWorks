package com.krafte.nebworks.ui.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.krafte.nebworks.bottomsheet.SelectStringBottomSheet;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FeedNotiAddInterface;
import com.krafte.nebworks.dataInterface.FeedNotiEditInterface;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.databinding.ActivityCommunityAddBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
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
import java.util.Arrays;
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

public class CommunityAddActivity extends AppCompatActivity {
    private ActivityCommunityAddBinding binding;
    private final static String TAG = "WorkCommunityWriteAcitivy";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    int SELECTED_POSITION = 0;
    String USER_INFO_NICKNAME = "";
    String place_id = "";
    String feed_id = "";

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();

    Drawable icon_off;
    Drawable icon_on;
    int like_state = 0;
    String CommTitle = "";
    String CommContnets = "";
    int nickname_select = 1;

    //--EditData
    String state_txt = "";
    String write_id_txt = "";
    String boardkind = "";
    String category = "";
    String user_input_name = "";
    String write_nickname = "";
    String feed_img = "";


    private Bitmap saveBitmap;
    File file;
    SimpleDateFormat dateFormat;
    int GALLEY_CODE = 10;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/heypass/";
    String ProfileUrl = "";
    String feed_thumnail_path = "";
    String ImgfileMaker = "";
    String state = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityCommunityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        icon_off = getApplicationContext().getResources().getDrawable(R.drawable.resize_service_off);
        icon_on = getApplicationContext().getResources().getDrawable(R.drawable.ic_full_round_check);


        //Singleton Area
        USER_INFO_ID        = UserCheckData.getInstance().getUser_id();
        USER_INFO_NAME      = UserCheckData.getInstance().getUser_name();
        USER_INFO_NICKNAME  = UserCheckData.getInstance().getUser_nick_name();
        USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH","");
        place_id            = PlaceCheckData.getInstance().getPlace_id();

        //shardpref Area
        USER_INFO_AUTH      = shardpref.getString("USER_INFO_AUTH", "");
        SELECTED_POSITION   = shardpref.getInt("SELECTED_POSITION", 0);
        state               = shardpref.getString("state", "");

        /*작성자가 수정 버튼을 눌렀을때 가져옴*/
        state_txt           = shardpref.getString("state", "");
        write_id_txt        = shardpref.getString("write_id", "");
        write_nickname      = shardpref.getString("write_nickname", "");

        user_input_name = USER_INFO_NAME;
        binding.addcommunityBtn.setText("등록");
        feed_thumnail_path = feed_img;

        dlog.i("user_input_name : " + user_input_name);
        dlog.i("state : " + state);
        //-------------------------------
        //-- 게시글 수정할때
        feed_id                 = shardpref.getString("feed_id","");
        String title            = shardpref.getString("title","");
        String contents         = shardpref.getString("contents","");
        String feed_img_path    = shardpref.getString("feed_img_path","");
        String category         = shardpref.getString("category","");

        binding.selectCategoryTxt.setText(category.isEmpty()?"#공유해요":category);
        binding.writeTitle.setText(title);
        binding.writeContents.setText(contents);
        Glide.with(mContext).load(feed_img_path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.limitImg);

        nickname_select = 1;
        user_input_name = USER_INFO_NICKNAME;

        setBtnEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        ImgfileMaker = ImageNameMaker();
    }

    @SuppressLint("LongLogTag")
    private void setBtnEvent() {
        binding.selectCategoryTxt.setOnClickListener(v -> {
            shardpref.putInt("SelectKind", 1);
            SelectStringBottomSheet ssb = new SelectStringBottomSheet();
            ssb.show(getSupportFragmentManager(), "selectBoardkindTxt");
            ssb.setOnItemClickListener(new SelectStringBottomSheet.OnItemClickListener() {
                @Override
                public void onItemClick(View v, String result) {
                    binding.selectCategoryTxt.setText(result);
                    category = result;
                }
            });
        });


        binding.backBtn.setOnClickListener(v -> {
            BackMove();
        });


        binding.addcommunityBtn.setOnClickListener(v -> {
            if(ForbiddenWordCheck()){
                dlog.i("3 state_txt : " + state_txt);
                if (nickname_select == 1 && USER_INFO_NICKNAME.isEmpty()) {
                    shardpref.putString("returnPage", TAG);
                    Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
                    intent.putExtra("data", "저장된 닉네임이 없습니다\n닉네임설정으로 이동합니다.");
                    intent.putExtra("flag", "닉네임없음");
                    intent.putExtra("left_btn_txt", "취소");
                    intent.putExtra("right_btn_txt", "확인");
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                } else if (DataCheck().equals("title")) {
                    Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (DataCheck().equals("contents")) {
                    Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (DataCheck().equals("name")) {
                    Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (DataCheck().equals("category")) {
                    Toast.makeText(this, "카테고리을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if (DataCheck().equals("true")) {
                    if (state_txt.equals("EditCommunity")) {
                        EditStroeNoti();
                    } else {
                        AddFeedCommunity();
                    }
                }
            }

        });

//        binding.writerName.setOnClickListener(v -> {
//            if (USER_INFO_NICKNAME.isEmpty() && nickname_select == 0) {
//                shardpref.putString("returnPage", TAG);
//                Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
//                intent.putExtra("data", "저장된 닉네임이 없습니다\n닉네임설정으로 이동합니다.");
//                intent.putExtra("flag", "닉네임없음");
//                intent.putExtra("left_btn_txt", "취소");
//                intent.putExtra("right_btn_txt", "확인");
//                mContext.startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            } else {
//                if (nickname_select == 0) {
//                    nickname_select = 1;
//                    user_input_name = USER_INFO_NICKNAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
//                    binding.writerName.setBackgroundColor(Color.parseColor("#E0EAFB"));
//                } else {
//                    nickname_select = 0;
//                    user_input_name = USER_INFO_NAME;
//                    binding.writerName.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
//                    binding.writerName.setBackgroundColor(Color.parseColor("#ffffff"));
//                }
//            }
//
//        });

        //------게시글 이미지 등록 / 갤러리 열기
        binding.limitImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLEY_CODE);
        });
    }

    @SuppressLint("LongLogTag")
    private String DataCheck() {
        if (nickname_select == 0) {
            user_input_name = USER_INFO_NICKNAME;
        } else {
            user_input_name = USER_INFO_NAME;
        }
        CommTitle = binding.writeTitle.getText().toString();
        CommContnets = binding.writeContents.getText().toString();

        dlog.i("-----------------DataCheck------------------");
        dlog.i("CommTitle : " + CommTitle);
        dlog.i("CommContnets : " + CommContnets);
        dlog.i("boardkind : " + boardkind);
        dlog.i("category : " + category);
        dlog.i("user_input_name : " + user_input_name);
        dlog.i("feed_thumnail_path : " + feed_thumnail_path);
        dlog.i("-----------------DataCheck------------------");


        if (CommTitle.isEmpty()) {
            return "title";
        } else if (CommContnets.isEmpty()) {
            return "contents";
        } else if (user_input_name.isEmpty()) {
            return "name";
        } else if (category.isEmpty()) {
            return "category";
        } else {
            return "true";
        }

        //if (!CommTitle.isEmpty() && !CommContnets.isEmpty() && !user_input_name.isEmpty() && !category.isEmpty()) {
        //    return true;
        //} else {
        //  return false;
        //}

    }
    String title = "";
    String content = "";
    Resources res;
    private boolean ForbiddenWordCheck(){
        title = binding.writeTitle.getText().toString();
        content = binding.writeContents.getText().toString();
        res = getResources();
        List<String> forbiList = new ArrayList<>(Arrays.asList(Arrays.toString(res.getStringArray(R.array.forbidden_word)).replace("[","").replace("]","").split(",")));
//        dlog.i("String xml Forbidden Word : " + forbiList);
        for(int i = 0; i < forbiList.size(); i++){
            if(title.contains(forbiList.get(i))){
                title = title.replace(forbiList.get(i)," ○○○ ");
            }
            if(content.contains(forbiList.get(i))){
                content = content.replace(forbiList.get(i)," ○○○ ");
            }
        }
//        dlog.i("ForbiddenWordCheck title : " + title);
//        dlog.i("ForbiddenWordCheck contents : " + content);
        return true;
    }
    //피드 게시글 업로드
    public void AddFeedCommunity() {
        boardkind = "자유게시판";

        dlog.i("-----AddStroeNoti Check-----");
        dlog.i("title : " + title);
        dlog.i("content : " + content);
        dlog.i("Profile Url : " + ProfileUrl);
        dlog.i("BoardKind : " + boardkind);
        dlog.i("category : " + category);
        dlog.i("nickname_select : " + nickname_select);
        dlog.i("-----AddStroeNoti Check-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiAddInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiAddInterface api = retrofit.create(FeedNotiAddInterface.class);
        Call<String> call = api.getData(place_id, title, content, USER_INFO_ID, "", ProfileUrl, "", "", "", "2", boardkind, category, String.valueOf(nickname_select));
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
                            dlog.i("ProfileUrl : " + ProfileUrl);
                            try {
                                if (!jsonResponse.equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
                                    if (!ProfileUrl.isEmpty()) {
                                        saveBitmapAndGetURI();
                                    }
                                    Toast_Nomal("게시글 저장이 완료되었습니다.");
                                    shardpref.putInt("SELECT_POSITION", 3);
                                    pm.Main(mContext);
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

    public void EditStroeNoti() {
        String title = binding.writeTitle.getText().toString();
        String content = binding.writeContents.getText().toString();
        boardkind = "자유게시판";
        category = binding.selectCategoryTxt.getText().toString();

        dlog.i("-----AddStroeNoti Check-----");
        dlog.i("title : " + title);
        dlog.i("content : " + content);
        dlog.i("Profile Url : " + ProfileUrl);
        dlog.i("BoardKind : " + boardkind);
        dlog.i("category : " + category);
        dlog.i("nickname_select : " + nickname_select);
        dlog.i("-----AddStroeNoti Check-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiEditInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiEditInterface api = retrofit.create(FeedNotiEditInterface.class);
        Call<String> call = api.getData(feed_id, place_id, title, content, USER_INFO_ID, "", ProfileUrl, "", "", "", "2", boardkind, category, String.valueOf(nickname_select));
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
                                        saveBitmapAndGetURI();
                                    }
                                    sharedRemove();
                                    Toast.makeText(mContext, "글 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStop() {
        super.onStop();
    }

    private void sharedRemove(){
        shardpref.remove("writer_name");
        shardpref.remove("write_nickname");
        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("write_date");
        shardpref.remove("view_cnt");
        shardpref.remove("like_cnt");
        shardpref.remove("categoryItem");
        shardpref.remove("TopFeed");

        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("writer_id");
        shardpref.remove("writer_img_path");
        shardpref.remove("feed_img_path");
        shardpref.remove("jikgup");
        shardpref.remove("view_cnt");
        shardpref.remove("comment_cnt");
        shardpref.remove("category");
    }
    private void BackMove() {
        sharedRemove();
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("data", "작성을 종료하시겠습니까?\n편집한 내용이 저장되지 않습니다.");
        intent.putExtra("flag", "작성여부");
        intent.putExtra("left_btn_txt", "계속작성");
        intent.putExtra("right_btn_txt", "작성종료");
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


    @Override
    public void onBackPressed() {
//       super.onBackPressed();
        BackMove();
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
                            .into(binding.limitImg);

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
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return url;
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

    @SuppressLint({"SimpleDateFormat", "LongLogTag"})
    public Uri saveBitmapAndGetURI() {
        //Create Bitmap
//            saveBitmap = CanvasIO.openBitmap(mContext);
        binding.loginAlertText.setVisibility(View.VISIBLE);
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


            ProfileUrl = "http://krafte.net/NEBWorks/image/feedimg/" + file_name;
            feed_thumnail_path = "http://krafte.net/NEBWorks/image/feedimg/" + file_name;
            saveBitmapToFile(file);

            dlog.e("이미지 저장경로 : " + ProfileUrl);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_name, requestFile);
            RetrofitInterface retrofitInterface = ApiClient.getApiClient().create(RetrofitInterface.class);
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
            binding.loginAlertText.setVisibility(View.GONE);
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
        @POST("upload_feed_img.php")
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
