package com.krafte.nebworks.ui.feed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.krafte.nebworks.adapter.FeedConfrimLogAdapter;
import com.krafte.nebworks.data.FeedConfirmData;
import com.krafte.nebworks.dataInterface.FeedConfrimInterface;
import com.krafte.nebworks.dataInterface.FeedConfrimlistInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.ActivityFeedDetailBinding;
import com.krafte.nebworks.pop.PhotoPopActivity;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FeedDetailActivity extends AppCompatActivity {
    private final static String TAG = "FeedDetailActivity";
    private ActivityFeedDetailBinding binding;
    Context mContext;
    int GALLEY_CODE = 10;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();

    //shared
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String feed_id = "";
    String USER_INFO_EMAIL = "";
    String state = "";
    String comment_no = "";
    String CommContnets = "";
    String place_name = "";
    String employee_no = "";
    String place_owner_id = "";
    String writer_id = "";

    int listitemsize = 0;

    Drawable icon_off;
    Drawable icon_on;

    InputMethodManager imm;
    ArrayList<FeedConfirmData.FeedConfirmData_list> mList;
    FeedConfrimLogAdapter mAdapter = null;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
        binding = ActivityFeedDetailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        try {
            mContext = this;
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "");
            place_id        = shardpref.getString("place_id", "0");
            feed_id         = shardpref.getString("feed_id", "0");
            state           = shardpref.getString("editstate", "");
            place_owner_id  = shardpref.getString("place_owner_id", "");
            writer_id       = shardpref.getString("writer_id", "");
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("place_id : " + place_id);
            dlog.i("feed_id : " + feed_id);
            dlog.i("place_owner_id : " + place_owner_id);

            icon_on = mContext.getResources().getDrawable(R.drawable.resize_service_on);
            icon_off = mContext.getResources().getDrawable(R.drawable.resize_service_off);
            imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            shardpref.putInt("SELECT_POSITION", 0);
            shardpref.putInt("SELECT_POSITION_sub", 0);
            setBtnEvent();
            GETFeed();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //UI 데이터 세팅
        try {
            if(!USER_INFO_ID.equals(writer_id)){
                UpdateWorkNotifyReadYn();
            }
            getCommentList();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setBtnEvent() {
        binding.backBtn.setOnClickListener(v -> {
            pm.FeedList(mContext);
        });
        binding.editGo.setOnClickListener(v -> {
            dlog.i("editGo");
            shardpref.putString("edit_feed_id", feed_id);
            pm.FeedEditGo(mContext);
        });
        binding.delGo.setOnClickListener(v -> {
            shardpref.putString("edit_feed_id", feed_id);
            Intent intent = new Intent(this, TwoButtonPopActivity.class);
            intent.putExtra("data", "해당 공지사항을 삭제하시겠습니까?");
            intent.putExtra("flag", "공지삭제2");
            intent.putExtra("left_btn_txt", "취소");
            intent.putExtra("right_btn_txt", "삭제");
            startActivity(intent);
            overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

    }

    String id = "";
    String title = "";
    String contents = "";
    String writer_name = "";
    String writer_img_path = "";

    String jikgup = "";
    String view_cnt = "";
    String comment_cnt = "";
    String link = "";
    String feed_img_path = "";
    String created_at = "";
    String updated_at = "";


    public void GETFeed() {
        dlog.i("GETFeed place_id : " + place_id);
        dlog.i("GETFeed feed_id : " + feed_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, feed_id, "", "1", USER_INFO_ID);
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
                                    if(Response.length() != 0){
                                        id = Response.getJSONObject(0).getString("id");
                                        place_id = Response.getJSONObject(0).getString("place_id");
                                        title = Response.getJSONObject(0).getString("title");
                                        contents = Response.getJSONObject(0).getString("contents");
                                        writer_id = Response.getJSONObject(0).getString("writer_id");
                                        writer_name = Response.getJSONObject(0).getString("writer_name");
                                        writer_img_path = Response.getJSONObject(0).getString("writer_img_path");

                                        jikgup = Response.getJSONObject(0).getString("jikgup");
                                        view_cnt = Response.getJSONObject(0).getString("view_cnt");
                                        comment_cnt = Response.getJSONObject(0).getString("comment_cnt");
                                        link = Response.getJSONObject(0).getString("link");
                                        feed_img_path = Response.getJSONObject(0).getString("feed_img_path");
                                        created_at = Response.getJSONObject(0).getString("created_at");
                                        updated_at = Response.getJSONObject(0).getString("updated_at");

                                        try {
                                            if(!writer_id.equals(USER_INFO_ID)){
                                                binding.detailEditArea.setVisibility(View.GONE);
                                            }
                                            binding.feedTitle.setText(title);
                                            if (place_owner_id.equals(writer_id)) {
                                                binding.userName.setText("매니저");
                                            } else {
                                                binding.userName.setText(writer_name + "(" + jikgup + ")");
                                            }

                                            if (link.isEmpty() || link.equals("null")) {
                                                binding.linkTxt.setVisibility(View.GONE);
                                                binding.moveLinkArea.setVisibility(View.GONE);
                                            } else {
                                                binding.linkTxt.setVisibility(View.VISIBLE);
                                                binding.moveLinkArea.setVisibility(View.VISIBLE);
                                            }
                                            Glide.with(mContext).load(writer_img_path)
                                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                    .skipMemoryCache(true)
                                                    .into(binding.profileImg);
                                            String updated_list = updated_at.substring(0, 10);
                                            List<String> updatedList = new ArrayList<>(Arrays.asList(updated_list.split("-")));
                                            dlog.i("updated_list : " + updated_list);
                                            dlog.i("updatedList : " + updatedList.get(0) + "년 " + updatedList.get(1) + "월 " + updatedList.get(2) + "일");

                                            binding.inputDate.setText(updated_at);

                                            binding.getWorkcontents.setText(contents);
                                            binding.getMovelink.setText(link);
                                            dlog.i("feed_img_path : " + feed_img_path);
                                            if (feed_img_path.isEmpty() || feed_img_path.equals("null")) {
                                                binding.notiSetimg.setVisibility(View.GONE);
                                            } else {
                                                binding.notiSetimg.setVisibility(View.VISIBLE);
                                                Glide.with(mContext).load(feed_img_path)
                                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                        .skipMemoryCache(true)
                                                        .placeholder(R.drawable.no_image)
                                                        .into(binding.notiSetimg);
                                            }
                                            place_name = shardpref.getString("place_name", "-1");


                                            StringBuilder total_watermark = new StringBuilder();
                                            for (int a = 0; a < 20; a++) {
                                                total_watermark.append(employee_no).append(" ");
                                                total_watermark.append(place_name).append(" ");
                                            }
                                            dlog.i("total_watermark : " + total_watermark.toString());

                                            binding.notiSetimg.setOnClickListener(v -> {
                                                Intent intent = new Intent(mContext, PhotoPopActivity.class);
                                                intent.putExtra("data", feed_img_path);
                                                mContext.startActivity(intent);
                                                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            });
                                        } catch (Exception e) {
                                            dlog.i("UserCheck Exception : " + e);
                                        }
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



    @SuppressLint("LongLogTag")
    private void UpdateWorkNotifyReadYn() {
        dlog.i("-----UpdateWorkNotifyReadYn-----");
        dlog.i("feed_id : " + feed_id);
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedConfrimInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedConfrimInterface api = retrofit.create(FeedConfrimInterface.class);
        Call<String> call = api.getData(feed_id, USER_INFO_ID);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String jsonResponse = rc.getBase64decode(response.body());
                                dlog.i("jsonResponse length : " + jsonResponse.length());
                                dlog.i("jsonResponse : " + jsonResponse);
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

    @SuppressLint("LongLogTag")
    private void getCommentList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedConfrimlistInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedConfrimlistInterface api = retrofit.create(FeedConfrimlistInterface.class);
        Call<String> call = api.getData(feed_id, USER_INFO_ID);
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
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(jsonResponse);

                                mList = new ArrayList<>();
                                mAdapter = new FeedConfrimLogAdapter(mContext, mList);
                                binding.commentList.setAdapter(mAdapter);
                                binding.commentList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                listitemsize = Response.length();

                                binding.confirmCnt.setText(Response.length() + "명");
                                if (Response.length() == 0) {
                                    dlog.i("SetNoticeListview Thread run! ");
                                    dlog.i("GET SIZE : " + Response.length());
                                    binding.noDataTxt.setVisibility(View.VISIBLE);
                                } else {
                                    binding.noDataTxt.setVisibility(View.GONE);
                                    for (int i = 0; i < Response.length(); i++) {
                                        JSONObject jsonObject = Response.getJSONObject(i);
                                        mAdapter.addItem(new FeedConfirmData.FeedConfirmData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("feed_id"),
                                                jsonObject.getString("write_id"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("img_path"),
                                                jsonObject.getString("created_at")
                                        ));
                                    }
                                    mAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        pm.FeedList(mContext);
    }
}
