package com.krafte.nebworks.ui.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.MultiImageAdapter;
import com.krafte.nebworks.adapter.WorkCommentListAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.data.WorkCommentData;
import com.krafte.nebworks.dataInterface.AddLikeInterface;
import com.krafte.nebworks.dataInterface.FeedCommentEidtInterface;
import com.krafte.nebworks.dataInterface.FeedCommentInsertInterface;
import com.krafte.nebworks.dataInterface.FeedCommentListInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.dataInterface.UpdateViewInterfcae;
import com.krafte.nebworks.databinding.ActivityCommunityDetailBinding;
import com.krafte.nebworks.pop.CommunityOptionActivity;
import com.krafte.nebworks.pop.PhotoPopActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class CommunityDetailActivity extends AppCompatActivity {
    private ActivityCommunityDetailBinding binding;
    private final static String TAG = "CommunityDetailActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    int SELECTED_POSITION = 0;
    String USER_INFO_NICKNAME = "";
    String USER_INFO_PROFILE = "";
    String place_id = "";
    String USER_INFO_EMAIL = "";
    String state = "";
    String comment_id = "";
    String CommContnets = "";

    //Community SharedData
    String feed_id = "";
    String title = "";
    String contents = "";
    String writer_id = "";
    String writer_name = "";
    String writer_img_path = "";
    String jikgup = "";
    String view_cnt = "";
    String comment_cnt = "";
    String like_cnt = "";
    String category = "";
    String updated_at = "";
    String mylikeyn = "";
    String feed_img_path = "";

    //Other
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    RetrofitConnect rc = new RetrofitConnect();
    MultiImageAdapter adapter;

    Drawable icon_off;
    Drawable icon_on;

    int listitemsize = 0;
    String click_htn = "";
    String comment_txt = "";
    String WriteName = "";
    boolean like_int = false;
    InputMethodManager imm;
    ArrayList<WorkCommentData.WorkCommentData_list> mList;
    WorkCommentListAdapter mAdapter = null;
    ArrayList<Uri> uriList = new ArrayList<>();// 이미지의 uri를 담을 ArrayList 객체

    String subcomment_id = "";
    String subcomment_name = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityCommunityDetailBinding.inflate(getLayoutInflater());
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
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //Singleton Area
        USER_INFO_ID = UserCheckData.getInstance().getUser_id();
        USER_INFO_NAME = UserCheckData.getInstance().getUser_name();
        USER_INFO_NICKNAME = UserCheckData.getInstance().getUser_nick_name();
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
        place_id = PlaceCheckData.getInstance().getPlace_id();
        USER_INFO_PROFILE = UserCheckData.getInstance().getUser_img_path();

        //shardpref Area
        SELECTED_POSITION = shardpref.getInt("SELECTED_POSITION", 0);
        feed_id = shardpref.getString("feed_id", "");
        title = shardpref.getString("title", "");
        contents = shardpref.getString("contents", "");
        writer_id = shardpref.getString("writer_id", "");
        writer_name = shardpref.getString("writer_name", "");
        writer_img_path = shardpref.getString("writer_img_path", "");
        jikgup = shardpref.getString("jikgup", "");
        view_cnt = shardpref.getString("view_cnt", "");
        comment_cnt = shardpref.getString("comment_cnt", "");
        like_cnt = shardpref.getString("like_cnt", "");
        category = shardpref.getString("category", "");
        updated_at = shardpref.getString("updated_at", "");
        mylikeyn = shardpref.getString("mylikeyn", "");
        feed_img_path = shardpref.getString("feed_img_path", "");

        setBtnEvent();
        DataCheck();
        UpdateView(feed_id);

        binding.backBtn.setOnClickListener(v -> {
            RemoveShared();
            super.onBackPressed();
        });
        if (!writer_id.equals(USER_INFO_ID)) {
            binding.listSetting.setVisibility(View.GONE);
        } else {
            binding.listSetting.setVisibility(View.VISIBLE);
        }

        binding.likeCnt.setText(like_cnt);
        if (mylikeyn.equals("0")) {
            like_int = false;
            binding.likeIcon.setBackgroundResource(R.drawable.ic_like_off);
        } else {
            like_int = true;
            binding.likeIcon.setBackgroundResource(R.drawable.ic_like_on);
        }
        binding.likeArea.setOnClickListener(v -> {
            if (!like_int) {
                like_int = true;
                binding.likeIcon.setBackgroundResource(R.drawable.ic_like_on);
            } else {
                like_int = false;
                binding.likeIcon.setBackgroundResource(R.drawable.ic_like_off);
            }
            AddLike(feed_id);
        });

//        binding.feedImg.setOnClickListener(v -> {
//            Intent intent = new Intent(mContext, PhotoPopActivity.class);
//            intent.putExtra("data", feed_img_path);
//            mContext.startActivity(intent);
//            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        });

        binding.listSetting.setOnClickListener(v -> {
            shardpref.putString("feed_id", feed_id);
            shardpref.putString("place_id", place_id);
            shardpref.putString("title", title);
            shardpref.putString("contents", contents);
            shardpref.putString("writer_id", writer_id);
            shardpref.putString("writer_name", writer_name);
            shardpref.putString("writer_img_path", writer_img_path);
            shardpref.putString("feed_img_path", feed_img_path);
            shardpref.putString("jikgup", jikgup);
            shardpref.putString("view_cnt", view_cnt);
            shardpref.putString("comment_cnt", comment_cnt);
            shardpref.putString("category", category);
            shardpref.putString("state", "EditCommunity");
            Intent intent = new Intent(mContext, CommunityOptionActivity.class);
            intent.putExtra("state", "EditCommunity");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RemoveShared();
    }

    @Override
    public void onStart() {
        super.onStart();
        GetCommentList(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //UI 데이터 세팅
        try {
            GETFeed();

            dlog.i("onResume state : " + shardpref.getString("editstate", ""));
            if (shardpref.getString("editstate", "").equals("EditComment")) {
                dlog.i("----------댓글 수정의 경우----------");
                comment_id = shardpref.getString("comment_id", "");
                CommContnets = shardpref.getString("comment_contents", "");
                binding.addCommentTxt.setText(CommContnets);
                dlog.i("EditComment state : " + state);
                dlog.i("EditComment comment_id : " + comment_id);
                dlog.i("EditComment CommContnets : " + CommContnets);
                dlog.i("----------댓글 수정의 경우----------");
                shardpref.remove("editstate");
                GetCommentList(true);
            } else if (shardpref.getString("editstate", "").equals("DelComment")) {
                shardpref.remove("editstate");
                GetCommentList(true);
            } else {
                GetCommentList(false);
            }

            dlog.i("USER_INFO_EMAIL : " + USER_INFO_EMAIL);
            UserCheck();
            Glide.with(mContext).load(USER_INFO_PROFILE)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.myprofileImg);

        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
    }

    public void GETFeed() {
        uriList.clear();
        dlog.i("GETFeed place_id : " + place_id);
        dlog.i("GETFeed feed_id : " + feed_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, feed_id, "", "2", USER_INFO_ID);
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
                            try {
                                if (!jsonResponse.equals("[]")) {
                                    JSONArray Response = new JSONArray(jsonResponse);
                                    if (Response.length() != 0) {
                                        place_id = Response.getJSONObject(0).getString("place_id");
                                        title = Response.getJSONObject(0).getString("title");
                                        contents = Response.getJSONObject(0).getString("contents");
                                        writer_id = Response.getJSONObject(0).getString("writer_id");
                                        writer_name = Response.getJSONObject(0).getString("writer_name");
                                        writer_img_path = Response.getJSONObject(0).getString("writer_img_path");

                                        jikgup = Response.getJSONObject(0).getString("jikgup");
                                        view_cnt = Response.getJSONObject(0).getString("view_cnt");
                                        comment_cnt = Response.getJSONObject(0).getString("comment_cnt");
                                        feed_img_path = Response.getJSONObject(0).getString("feed_img_path");
                                        updated_at = Response.getJSONObject(0).getString("updated_at");

                                        try {
                                            Resources res = getResources();
                                            List<String> forbiList = Arrays.asList(res.getStringArray(R.array.forbidden_word));
                                            dlog.i("String xml Forbidden Word : " + forbiList);
                                            String titleForbidden = "";
                                            String contentForbidden = "";
                                            for (int i = 0; i < forbiList.size(); i++) {
                                                if (title.contains(forbiList.get(i))) {
                                                    titleForbidden = title.replace(forbiList.get(i), " ○○○ ");
                                                }
                                                if (contents.contains(forbiList.get(i))) {
                                                    contentForbidden = contents.replace(forbiList.get(i), " ○○○ ");
                                                }
                                            }

                                            dlog.d("forbidden: " + title);
                                            dlog.d("forbidden: " + titleForbidden);
                                            dlog.d("forbidden: " + contents);
                                            dlog.d("forbidden: " + contentForbidden);

                                            binding.title.setText(titleForbidden.equals("") ? title : titleForbidden);
                                            Glide.with(mContext).load(writer_img_path)
                                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                    .placeholder(R.drawable.certi01)
                                                    .skipMemoryCache(true)
                                                    .into(binding.profileImg);

//                                            Glide.with(mContext).load(feed_img_path)
//                                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                                    .skipMemoryCache(true)
//                                                    .into(binding.feedImg);
                                            dlog.i("feed_img_path : " + feed_img_path);
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
                                            binding.writerName.setText(writer_name);
                                            binding.date.setText(updated_at);
                                            binding.contents.setText(contentForbidden.equals("") ? contents : contentForbidden);
                                            binding.cate.setText("#" + category.replace("#", ""));
                                            binding.viewCom.setText("조회수 " + view_cnt + " / 댓글 " + comment_cnt);

                                        } catch (Exception e) {
                                            dlog.i("UserCheck Exception : " + e);
                                        }
                                    }
                                } else {
                                    shardpref.putInt("SELECT_POSITION", 3);
                                    if (USER_INFO_AUTH.equals("0")) {
                                        pm.Main(mContext);
                                    } else {
                                        pm.Main2(mContext);
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

    private boolean DataCheck() {
        comment_txt = binding.addCommentTxt.getText().toString();
        return !comment_txt.isEmpty();
    }

    public void EditComment(String comt_id, String comment) {
        dlog.i("-----AddStroeNoti Check-----");
        dlog.i("feed_id : " + comt_id);
        dlog.i("comment : " + comment);
        dlog.i("-----AddStroeNoti Check-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedCommentEidtInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedCommentEidtInterface api = retrofit.create(FeedCommentEidtInterface.class);
        Call<String> call = api.getData(comt_id, comment);
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
                                    dlog.i("Comment Edit success");
                                    shardpref.remove("comment_id");
                                    shardpref.remove("comment_contents");
                                    binding.addCommentTxt.setText("");
                                    binding.addCommentTxt.clearFocus();
                                    imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
                                    GetCommentList(true);
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

    public void UpdateView(String feed_id) {
        dlog.i("-----UpdateView Check-----");
        dlog.i("feed_id : " + feed_id);
        dlog.i("-----UpdateView Check-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UpdateViewInterfcae.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UpdateViewInterfcae api = retrofit.create(UpdateViewInterfcae.class);
        Call<String> call = api.getData(feed_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("UpdateView Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
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

    public void AddLike(String feed_id) {
        dlog.i("-----UpdateView Check-----");
        dlog.i("feed_id : " + feed_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("-----UpdateView Check-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AddLikeInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        AddLikeInterface api = retrofit.create(AddLikeInterface.class);
        Call<String> call = api.getData(feed_id, "", USER_INFO_ID, "0");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("UpdateView Callback : " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            like_cnt = jsonResponse.replace("\"", "");
                            binding.likeCnt.setText(like_cnt);
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

    public void AddComment(String feed_id, String comment, String writer_id) {
//        comment = comment.replace(subcomment_name, "");
        dlog.i("-----AddStroeNoti Check-----");
        dlog.i("feed_id : " + feed_id);
        dlog.i("comment : " + comment);
        dlog.i("writer_id : " + writer_id);
        dlog.i("-----AddStroeNoti Check-----");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedCommentInsertInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedCommentInsertInterface api = retrofit.create(FeedCommentInsertInterface.class);
        Call<String> call = api.getData(feed_id, comment, writer_id, subcomment_id);
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
                                    dlog.i("Comment insert success");
                                    binding.addCommentTxt.setText("");
                                    binding.addCommentTxt.clearFocus();
                                    imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
                                    GetCommentList(true);
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
    public void onPause() {
        super.onPause();
    }

    String comment = "";

    private void setBtnEvent() {
        binding.addCommentBtn.setOnClickListener(v -> {
            comment = binding.addCommentTxt.getText().toString();
            if (comment_id.isEmpty()) {
                AddComment(feed_id, comment, USER_INFO_ID);
            } else {
                EditComment(comment_id, comment);
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
        try {
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
            dlog.i("프로필 사진 url : " + mem_img_path);
            dlog.i("직원소속구분분 : " + (mem_kind.equals("0") ? "정직원" : "협력업체"));
            dlog.i("성명 : " + mem_name);
            dlog.i("부서 : " + mem_jikgup);
            dlog.i("급여 : " + mem_pay);
            dlog.i("USER_INFO_PROFILE : " + USER_INFO_PROFILE);
            dlog.i("------UserCheck-------");
        } catch (Exception e) {
            dlog.i("UserCheck Exception : " + e);
        }
    }

    //댓글 리스트 조회
    public void GetCommentList(boolean opnclo) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedCommentListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedCommentListInterface api = retrofit.create(FeedCommentListInterface.class);
        Call<String> call = api.getData(feed_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetCommentList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetCommentList jsonResponse : " + jsonResponse);
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(jsonResponse);

                                mList = new ArrayList<>();
                                mAdapter = new WorkCommentListAdapter(mContext, mList, mem_id, opnclo);
                                binding.commentList.setAdapter(mAdapter);
                                binding.commentList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                listitemsize = Response.length();

                                if (Response.length() == 0) {
                                    dlog.i("SetNoticeListview Thread run! ");
                                    dlog.i("GET SIZE : " + Response.length());
                                    binding.noDataTxt.setVisibility(View.VISIBLE);
                                } else {
                                    binding.noDataTxt.setVisibility(View.GONE);
                                    for (int i = 0; i < Response.length(); i++) {
                                        JSONObject jsonObject = Response.getJSONObject(i);
                                        //작업 일자가 없으면 표시되지 않음.
                                        mAdapter.addItem(new WorkCommentData.WorkCommentData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("feed_id"),
                                                jsonObject.getString("comment_id"),
                                                jsonObject.getString("comment"),
                                                Collections.singletonList(jsonObject.getString("reply")),
                                                jsonObject.getString("writer_id"),
                                                jsonObject.getString("writer_name"),
                                                jsonObject.getString("writer_img_path"),
                                                jsonObject.getString("edit_yn"),
                                                jsonObject.getString("delete_yn"),
                                                jsonObject.getString("created_at"),
                                                jsonObject.getString("updated_at"),
                                                jsonObject.getString("comment_cnt"),
                                                jsonObject.getString("like_cnt"),
                                                jsonObject.getString("mylike_cnt")
                                        ));
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.setOnItemClickListener(new WorkCommentListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int position, String comment_id, String comment, String writer_name
                                                , String feed_id, String write_id, String title, String contents, String comment_contents, String write_date) {
                                            binding.addCommentTxt.clearFocus();
                                            imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
                                            dlog.i("delete comment_id : " + comment_id);
                                            shardpref.putString("comment_id", comment_id);
                                            Intent intent = new Intent(mContext, CommunityOptionActivity.class);
                                            WriteName = writer_name;

                                            intent.putExtra("state", "EditComment2");
                                            intent.putExtra("feed_id", feed_id);
                                            intent.putExtra("comment_id", comment_id);
                                            intent.putExtra("write_id", write_id);
                                            intent.putExtra("writer_name", writer_name);
                                            intent.putExtra("title", title);
                                            intent.putExtra("contents", contents);
                                            intent.putExtra("comment_contents", comment_contents);
                                            intent.putExtra("write_date", write_date);
                                            intent.putExtra("write_nickname", "");

                                            mContext.startActivity(intent);
                                            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        }
                                    });
                                    mAdapter.setOnItemClickListener2(new WorkCommentListAdapter.OnItemClickListener2() {
                                        @Override
                                        public void onItemClick2(View v, int position, String writer_id, String writer_name) {
                                            subcomment_id = writer_id;
                                            subcomment_name = "@" + writer_name + "  ";
                                            binding.addCommentTxt.setText(subcomment_name);
                                        }
                                    });
                                }
                                dlog.i("SetNoticeListview Thread run! ");
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

    private void RemoveShared() {
        shardpref.remove("feed_id");
        shardpref.remove("title");
        shardpref.remove("contents");
        shardpref.remove("writer_id");
        shardpref.remove("writer_name");
        shardpref.remove("writer_img_path");
        shardpref.remove("jikgup");
        shardpref.remove("view_cnt");
        shardpref.remove("comment_cnt");
        shardpref.remove("category");
        shardpref.remove("updated_at");
    }
}
