package com.krafte.nebworks.ui.career;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.MakeFileNameInterface;
import com.krafte.nebworks.databinding.ActivityCareerBinding;
import com.krafte.nebworks.ui.fragment.career.CareerFragment1;
import com.krafte.nebworks.ui.fragment.career.CareerFragment2;
import com.krafte.nebworks.ui.fragment.career.CareerFragment3;
import com.krafte.nebworks.util.AES256Util;
import com.krafte.nebworks.util.DBConnection;
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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

public class CareerActivity extends AppCompatActivity {
    private ActivityCareerBinding binding;
    private final static String TAG = "CareerActivity";
    Context mContext;
    int GALLEY_CODE = 10;

    //navbar.xml
    DrawerLayout drawerLayout;
    View drawerView;
    ImageView close_btn;


    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_PW = "";
    String USER_INFO_NAME = "";
    String USER_INFO_PHONE = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_GENDER = "";
    String USER_INFO_NICKNAME = "";
    String USER_INFO_NO = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_PROFILE;
    String store_name_txt;
    String store_no;

    //Other
    DBConnection dbConnection = new DBConnection();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    PageMoveClass pm = new PageMoveClass();

    Handler mHandler;
    Dlog dlog = new Dlog();

    File file;
    SimpleDateFormat dateFormat;
    @SuppressLint("SdCardPath")
    String BACKUP_PATH = "/sdcard/Download/krafte/";
    String fileName = "";
    String ProfileUrl = "";

    int paging_position = 0;
    private Bitmap saveBitmap;
    String ImgfileMaker = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_career);
        binding = ActivityCareerBinding.inflate(getLayoutInflater());
        setTheme(R.style.Theme_Kogas);
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);

        shardpref = new PreferenceHelper(mContext);
        store_no = shardpref.getString("store_no","");
//        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_ID = UserCheckData.getInstance().getUser_id();
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME","");
        USER_INFO_PW = shardpref.getString("USER_INFO_PW","");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
        USER_INFO_PHONE = shardpref.getString("USER_INFO_PHONE","");
        USER_INFO_NO = shardpref.getString("USER_INFO_NO","");
        USER_INFO_PROFILE = shardpref.getString("USER_INFO_PROFILE", "");
        USER_INFO_NICKNAME = shardpref.getString("USER_INFO_NICKNAME", "");
        USER_INFO_GENDER = shardpref.getString("USER_INFO_GENDER","");
        USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL","");

        setNavBarBtnEvent();
        setBtnEvent();

        binding.name.setText(USER_INFO_NAME);
        drawerLayout.addDrawerListener(listener);
        drawerView.setOnTouchListener((v, event) -> true);
        try {
            aes256Util = new AES256Util("dkwj12fisne349vnlkw904mlk13490nv");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final List<String> tabElement = Arrays.asList("기본정보", "이력정보", "지원가능매장");
        //before 전체피드 / 랭킹 / 매장 / 구인구직
        //after 매장 / 전체피드 / 구인구직 / 랭킹
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(CareerFragment1.newInstance(0));
        fragments.add(CareerFragment2.newInstance(1));
        fragments.add(CareerFragment3.newInstance(2));

        viewPagerFregmentAdapter = new ViewPagerFregmentAdapter(this, fragments);
        binding.viewPager.setAdapter(viewPagerFregmentAdapter);
        binding.viewPager.setUserInputEnabled(false);

        //ViewPager2와 TabLayout을 연결
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            TextView textView = new TextView(CareerActivity.this);
            textView.setText(tabElement.get(position));
            textView.setTextColor(Color.parseColor("#696969"));
            textView.setGravity(Gravity.CENTER);
            tab.setCustomView(textView);
        }).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                paging_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        Glide.with(mContext).load(USER_INFO_PROFILE).circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(binding.profileImg);

        binding.viewPager.setCurrentItem(1);

        ImgfileMaker = ImageNameMaker();

        if (saveBitmap != null) {
            binding.clearImg.setVisibility(View.VISIBLE);
        } else {
            binding.clearImg.setVisibility(View.GONE);
        }
        binding.clearImg.setOnClickListener(v -> {
            try {
                saveBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
                saveBitmap.eraseColor(Color.TRANSPARENT);
                binding.profileImg.setImageBitmap(saveBitmap);
                binding.profileImg.setBackgroundResource(R.drawable.img_box_round);
                ProfileUrl = "";
                binding.clearImg.setVisibility(View.GONE);
            } catch (Exception e) {
                dlog.i("clearImg Exception : " + e);
            }
        });
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            //슬라이드 했을때
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            //Drawer가 오픈된 상황일때 호출
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            // 닫힌 상황일 때 호출
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            // 특정상태가 변결될 때 호출
        }
    };
    private void setBtnEvent() {
        binding.profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLEY_CODE);
        });

    }

    //이미지 업로드에 필요한 소스 START
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_CODE) {
            if (resultCode == RESULT_OK) {
                String imagePath = "";
                try {
                    String imageUrl = "0";

                    InputStream in = getContentResolver().openInputStream(data.getData());
                    imageUrl = getRealPathFromUri(data.getData());
                    dlog.i( "imageUrl = " + imageUrl);
                    saveBitmap = BitmapFactory.decodeStream(in);
                    in.close();

                    imagePath = data.getDataString(); // "content://media/external/images/media/7215"

                    Glide.with(this)
                            .load(saveBitmap)
                            .into(binding.profileImg);
                    binding.clearImg.setVisibility(View.VISIBLE);

                    final String IMG_FILE_EXTENSION = ".JPEG";
                    String file_name = USER_INFO_ID + "_" + ImgfileMaker + IMG_FILE_EXTENSION;
                    ProfileUrl = "http://krafte.net/NEBWorks/image/user_img/" + file_name;
                    SaveUser();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

            ProfileUrl = "http://krafte.net/NEBWorks/image/user_img/" + file_name;
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
        @POST("upload_user_img.php")
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

    @SuppressLint("LongLogTag")
    public void setNavBarBtnEvent() {
        dlog.i("setNavBarBtnEvent store_name : " + store_name_txt);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer2);
        close_btn = findViewById(R.id.close_btn);

        ImageView user_profile = findViewById(R.id.user_profile);
//        store_nameview.setText("영코바이크");

        Glide.with(mContext).load(shardpref.getString("store_thumnail_path",""))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(user_profile);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
    }

    AES256Util aes256Util;
    public void SaveUser() {
        try {
            USER_INFO_PW = aes256Util.encode("kraftmysecretkey" + USER_INFO_PW + "nrkwl3nkv54");
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException
                | NoSuchPaddingException | IllegalBlockSizeException
                | NoSuchAlgorithmException | BadPaddingException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        dlog.i("------SaveUser-------");
        dlog.i("USER ID : " + USER_INFO_ID);
        dlog.i("프로필 사진 url : " + ProfileUrl);
        dlog.i("성명 : " + USER_INFO_NAME);
        dlog.i("닉네임 : " + USER_INFO_NAME);
        dlog.i("비밀번호 : " + USER_INFO_PW);
        dlog.i("휴대폰 : " + USER_INFO_PHONE);
        dlog.i("성별 : " + USER_INFO_GENDER);
        dlog.i("------SaveUser-------");

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(UserSaveInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        UserSaveInterface api = retrofit.create(UserSaveInterface.class);
//        Call<String> call = api.getData(USER_INFO_ID, USER_INFO_NAME, USER_INFO_NAME, USER_INFO_PW, USER_INFO_PHONE, USER_INFO_GENDER, ProfileUrl);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful() && response.body() != null) {
////                            String jsonResponse = rc.getBase64decode(response.body());
//                            dlog.i("SaveUser jsonResponse length : " + response.body().length());
//                            dlog.i("SaveUser jsonResponse : " + response.body());
//                            try {
//                                if (!response.body().equals("[]") && jsonResponse.replace("\"", "").equals("success")) {
//                                    if(!ProfileUrl.isEmpty()){
//                                        saveBitmapAndGetURI();
//                                        shardpref.putString("USER_INFO_PROFILE",ProfileUrl);
//                                    }
//                                    Toast_Nomal("프로필 변경이 완료되었습니다.");
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                dlog.e("에러1 = " + t.getMessage());
//            }
//        });
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
