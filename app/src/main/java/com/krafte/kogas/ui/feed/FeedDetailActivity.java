package com.krafte.kogas.ui.feed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.kogas.R;
import com.krafte.kogas.adapter.WorkCommentListAdapter;
import com.krafte.kogas.data.WorkCommentData;
import com.krafte.kogas.dataInterface.FeedCommentEidtInterface;
import com.krafte.kogas.dataInterface.FeedCommentInsertInterface;
import com.krafte.kogas.dataInterface.FeedCommentListInterface;
import com.krafte.kogas.dataInterface.FeedNotiInterface;
import com.krafte.kogas.dataInterface.FeedViewcntInterface;
import com.krafte.kogas.dataInterface.UserSelectInterface;
import com.krafte.kogas.databinding.ActivityFeedDetailBinding;
import com.krafte.kogas.pop.CommunityOptionActivity;
import com.krafte.kogas.pop.PhotoPopActivity;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FeedDetailActivity extends AppCompatActivity {
    private final static String TAG = "PlaceNotiAddActivity";
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

    //navbar.xml
    DrawerLayout drawerLayout;
    View drawerView;
    ImageView close_btn;

    //shared
    String USER_INFO_ID = "";
    String place_id = "";
    String feed_id = "";
    String USER_INFO_EMAIL = "";
    String state = "";
    String comment_no = "";
    String CommContnets = "";
    String place_name = "";
    String employee_no = "";

    int listitemsize = 0;
    String click_htn = "";
    String comment_txt = "";
    String WriteName = "";

    Drawable icon_off;
    Drawable icon_on;

    InputMethodManager imm;
    ArrayList<WorkCommentData.WorkCommentData_list> mList;
    WorkCommentListAdapter mAdapter = null;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
        binding = ActivityFeedDetailBinding.inflate(getLayoutInflater()); // 1
        setContentView(binding.getRoot()); // 2
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
        USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
        place_id = shardpref.getString("place_id", "0");
        feed_id = shardpref.getString("feed_id", "0");
        state = shardpref.getString("editstate", "");

        icon_on = mContext.getResources().getDrawable(R.drawable.resize_service_on);
        icon_off = mContext.getResources().getDrawable(R.drawable.resize_service_off);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        shardpref.putInt("selectposition", 0);
        setBtnEvent();
        UserCheck(USER_INFO_EMAIL);
        GETFeed();
    }

    @Override
    public void onResume() {
        super.onResume();
        //UI 데이터 세팅
        try {
            dlog.i("onResume state : " + shardpref.getString("editstate",""));
            if (state.equals("EditComment")) {
                dlog.i("----------댓글 수정의 경우----------");
                comment_no = shardpref.getString("comment_no", "");
                CommContnets = shardpref.getString("comment_contents", "");
                binding.addCommentTxt.setText(CommContnets);
                dlog.i("EditComment state : " + state);
                dlog.i("EditComment comment_no : " + comment_no);
                dlog.i("EditComment CommContnets : " + CommContnets);
                dlog.i("----------댓글 수정의 경우----------");
                shardpref.remove("editstate");
            }
            dlog.i("USER_INFO_EMAIL : " + USER_INFO_EMAIL);
            GetCommentList();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setBtnEvent() {
        binding.editGo.setOnClickListener(v -> {
            dlog.i("editGo");
            pm.FeedEditGo(mContext);
        });

        binding.addCommentTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력란에 변화가 있을때
                if (charSequence.length() > 0) {
                    binding.addCommentBtn.setVisibility(View.VISIBLE);
                    binding.addCommentBtn.setEnabled(true);
                } else {
                    binding.addCommentBtn.setVisibility(View.INVISIBLE);
                    binding.addCommentBtn.setEnabled(false);
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력이 끝났을때

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //입력하기 전에
            }
        });

        binding.addCommentBtn.setOnClickListener(v -> {
            click_htn = "add_comment_btn";
            imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
            binding.commentList.scrollToPosition(listitemsize);
            String in_comment = binding.addCommentTxt.getText().toString();
            comment_no = shardpref.getString("comment_no", "");
            if (DataCheck()) {
                if (comment_no.equals("")) {
                    AddComment(feed_id, in_comment, USER_INFO_ID);
                } else {
                    EditComment(comment_no, in_comment);
                }
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
                            dlog.i("AddStroeNoti jsonResponse length : " + response.body().length());
                            dlog.i("AddStroeNoti jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]") && response.body().replace("\"", "").equals("success")) {
                                    dlog.i("Comment Edit success");
                                    shardpref.remove("comment_no");
                                    binding.addCommentTxt.setText("");
                                    binding.addCommentTxt.clearFocus();
                                    imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
                                    GetCommentList();
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

    public void AddComment(String feed_id, String comment, String writer_id) {
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
        Call<String> call = api.getData(feed_id, comment, writer_id);
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
                                    dlog.i("Comment insert success");
                                    binding.addCommentTxt.setText("");
                                    binding.addCommentTxt.clearFocus();
                                    imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
                                    GetCommentList();
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



    String id = "";
    String title = "";
    String kind = "";
    String contents = "";
    String writer_id = "";
    String writer_name = "";
    String writer_img_path = "";

    String writer_department = "";
    String writer_position = "";
    String view_cnt = "";
    String comment_cnt = "";
    String link = "";
    String feed_img_path = "";
    String created_at = "";
    String updated_at = "";

    public void UserCheck(String account) {
        dlog.i("UserCheck account : " + account);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserSelectInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        UserSelectInterface api = retrofit.create(UserSelectInterface.class);
        Call<String> call = api.getData(account);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    kind = Response.getJSONObject(0).getString("kind");
                                    employee_no = Response.getJSONObject(0).getString("employee_no");
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

    public void GETFeed() {
        dlog.i("GETFeed place_id : " + place_id);
        dlog.i("GETFeed feed_id : " + feed_id);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, feed_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("UserCheck jsonResponse length : " + response.body().length());
                            dlog.i("UserCheck jsonResponse : " + response.body());
                            try {
                                if (!response.body().equals("[]")) {
                                    JSONArray Response = new JSONArray(response.body());
                                    id = Response.getJSONObject(0).getString("id");
                                    place_id = Response.getJSONObject(0).getString("place_id");
                                    title = Response.getJSONObject(0).getString("title");
                                    contents = Response.getJSONObject(0).getString("contents");
                                    writer_id = Response.getJSONObject(0).getString("writer_id");
                                    writer_name = Response.getJSONObject(0).getString("writer_name");
                                    writer_img_path = Response.getJSONObject(0).getString("writer_img_path");

                                    writer_department = Response.getJSONObject(0).getString("writer_department");
                                    writer_position = Response.getJSONObject(0).getString("writer_position");
                                    view_cnt = Response.getJSONObject(0).getString("view_cnt");
                                    comment_cnt = Response.getJSONObject(0).getString("comment_cnt");
                                    link = Response.getJSONObject(0).getString("link");
                                    feed_img_path = Response.getJSONObject(0).getString("feed_img_path");
                                    created_at = Response.getJSONObject(0).getString("created_at");
                                    updated_at = Response.getJSONObject(0).getString("updated_at");

                                    try {
                                        binding.feedTitle.setText(title);
                                        binding.userName.setText(writer_name + "(" + writer_department + " " + writer_position + ")");

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
                                        SETFeedViewcnt();
                                        place_name = shardpref.getString("place_name","-1");

                                        dlog.i("kind : " + kind);
                                        StringBuilder total_watermark = new StringBuilder();
                                        for(int a = 0; a < 20; a++){
                                            total_watermark.append(employee_no).append(" ");
                                            total_watermark.append(place_name).append(" ");
                                        }
                                        if(kind.equals("0")){
                                            binding.waterMark.setVisibility(View.GONE);
                                        }
                                        binding.waterMark.setText(total_watermark.toString());

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

    public void SETFeedViewcnt() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedViewcntInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedViewcntInterface api = retrofit.create(FeedViewcntInterface.class);
        Call<String> call = api.getData(feed_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            try {
                                dlog.i("UserCheck jsonResponse length : " + response.body().length());
                                dlog.i("UserCheck jsonResponse : " + response.body());
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

    //댓글 리스트 조회
    public void GetCommentList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedCommentListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedCommentListInterface api = retrofit.create(FeedCommentListInterface.class);
        Call<String> call = api.getData(feed_id);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetCommentList jsonResponse length : " + response.body().length());
                            dlog.i("GetCommentList jsonResponse : " + response.body());
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(response.body());

                                mList = new ArrayList<>();
                                mAdapter = new WorkCommentListAdapter(mContext, mList,writer_id);
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
                                                jsonObject.getString("comment"),
                                                jsonObject.getString("writer_id"),
                                                jsonObject.getString("writer_name"),
                                                jsonObject.getString("writer_img_path"),
                                                jsonObject.getString("writer_department"),
                                                jsonObject.getString("writer_position"),
                                                jsonObject.getString("edit_yn"),
                                                jsonObject.getString("delete_yn"),
                                                jsonObject.getString("created_at"),
                                                jsonObject.getString("updated_at")
                                        ));
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.setOnItemClickListener(new WorkCommentListAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int position) {
                                            try {
                                                binding.addCommentTxt.clearFocus();
                                                imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
                                                shardpref.putString("comment_no",Response.getJSONObject(position).getString("id"));
                                                Intent intent = new Intent(mContext, CommunityOptionActivity.class);
                                                WriteName = Response.getJSONObject(position).getString("writer_name").isEmpty() ?
                                                        Response.getJSONObject(position).getString("writer_name") : "";

                                                intent.putExtra("state", "EditComment");
                                                intent.putExtra("feed_id", Response.getJSONObject(position).getString("feed_id"));
                                                intent.putExtra("comment_no", Response.getJSONObject(position).getString("id"));
                                                intent.putExtra("write_id", Response.getJSONObject(position).getString("writer_id"));
                                                intent.putExtra("writer_name", WriteName);
                                                intent.putExtra("title", "");
                                                intent.putExtra("contents", "");
                                                intent.putExtra("comment_contents", Response.getJSONObject(position).getString("comment"));
                                                intent.putExtra("write_date", Response.getJSONObject(position).getString("created_at"));
                                                intent.putExtra("write_nickname","");

                                                mContext.startActivity(intent);
                                                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
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
    public void hideKeypad(){

    }
}
