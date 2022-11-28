package com.krafte.nebworks.ui.feed;

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
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.FeedConfrimLogAdapter;
import com.krafte.nebworks.data.FeedConfirmData;
import com.krafte.nebworks.dataInterface.FeedCommentEidtInterface;
import com.krafte.nebworks.dataInterface.FeedCommentInsertInterface;
import com.krafte.nebworks.dataInterface.FeedConfrimInterface;
import com.krafte.nebworks.dataInterface.FeedConfrimlistInterface;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.dataInterface.FeedViewcntInterface;
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

    //navbar.xml
    DrawerLayout drawerLayout;
    View drawerView;
    ImageView close_btn;

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

    int listitemsize = 0;
    String click_htn = "";
    String comment_txt = "";
    String WriteName = "";

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//캡쳐막기
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
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            place_id = shardpref.getString("place_id", "0");
            feed_id = shardpref.getString("feed_id", "0");
            state = shardpref.getString("editstate", "");
            place_owner_id = shardpref.getString("place_owner_id", "");

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
            dlog.i("onResume state : " + shardpref.getString("editstate", ""));
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
            UpdateWorkNotifyReadYn();
            getWorkNotifyReadYn();
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
        Call<String> call = api.getData(place_id, feed_id, "","1");
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

                                    jikgup = Response.getJSONObject(0).getString("jikgup");
                                    view_cnt = Response.getJSONObject(0).getString("view_cnt");
                                    comment_cnt = Response.getJSONObject(0).getString("comment_cnt");
                                    link = Response.getJSONObject(0).getString("link");
                                    feed_img_path = Response.getJSONObject(0).getString("feed_img_path");
                                    created_at = Response.getJSONObject(0).getString("created_at");
                                    updated_at = Response.getJSONObject(0).getString("updated_at");

                                    try {
                                        binding.feedTitle.setText(title);
                                        if (place_owner_id.equals(writer_id)) {
                                            binding.userName.setText(jikgup);
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
                                        SETFeedViewcnt();
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
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(FeedCommentListInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        FeedCommentListInterface api = retrofit.create(FeedCommentListInterface.class);
//        Call<String> call = api.getData(feed_id);
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful() && response.body() != null) {
////                            String jsonResponse = rc.getBase64decode(response.body());
//                            dlog.i("GetCommentList jsonResponse length : " + response.body().length());
//                            dlog.i("GetCommentList jsonResponse : " + response.body());
//                            try {
//                                //Array데이터를 받아올 때
//                                JSONArray Response = new JSONArray(response.body());
//
//                                mList = new ArrayList<>();
//                                mAdapter = new WorkCommentListAdapter(mContext, mList,writer_id);
//                                binding.commentList.setAdapter(mAdapter);
//                                binding.commentList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//                                listitemsize = Response.length();
//
//                                if (Response.length() == 0) {
//                                    dlog.i("SetNoticeListview Thread run! ");
//                                    dlog.i("GET SIZE : " + Response.length());
//                                    binding.noDataTxt.setVisibility(View.VISIBLE);
//                                } else {
//                                    binding.noDataTxt.setVisibility(View.GONE);
//                                    for (int i = 0; i < Response.length(); i++) {
//                                        JSONObject jsonObject = Response.getJSONObject(i);
//                                        //작업 일자가 없으면 표시되지 않음.
//                                        mAdapter.addItem(new WorkCommentData.WorkCommentData_list(
//                                                jsonObject.getString("id"),
//                                                jsonObject.getString("feed_id"),
//                                                jsonObject.getString("comment"),
//                                                jsonObject.getString("writer_id"),
//                                                jsonObject.getString("writer_name"),
//                                                jsonObject.getString("writer_img_path"),
//                                                jsonObject.getString("edit_yn"),
//                                                jsonObject.getString("delete_yn"),
//                                                jsonObject.getString("created_at"),
//                                                jsonObject.getString("updated_at")
//                                        ));
//                                    }
//                                    mAdapter.notifyDataSetChanged();
//                                    mAdapter.setOnItemClickListener(new WorkCommentListAdapter.OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(View v, int position) {
//                                            try {
//                                                binding.addCommentTxt.clearFocus();
//                                                imm.hideSoftInputFromWindow(binding.addCommentTxt.getWindowToken(), 0);
//                                                shardpref.putString("comment_no",Response.getJSONObject(position).getString("id"));
//                                                Intent intent = new Intent(mContext, CommunityOptionActivity.class);
//                                                WriteName = Response.getJSONObject(position).getString("writer_name").isEmpty() ?
//                                                        Response.getJSONObject(position).getString("writer_name") : "";
//
//                                                intent.putExtra("state", "EditComment");
//                                                intent.putExtra("feed_id", Response.getJSONObject(position).getString("feed_id"));
//                                                intent.putExtra("comment_no", Response.getJSONObject(position).getString("id"));
//                                                intent.putExtra("write_id", Response.getJSONObject(position).getString("writer_id"));
//                                                intent.putExtra("writer_name", WriteName);
//                                                intent.putExtra("title", "");
//                                                intent.putExtra("contents", "");
//                                                intent.putExtra("comment_contents", Response.getJSONObject(position).getString("comment"));
//                                                intent.putExtra("write_date", Response.getJSONObject(position).getString("created_at"));
//                                                intent.putExtra("write_nickname","");
//
//                                                mContext.startActivity(intent);
//                                                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                }
//                                dlog.i("SetNoticeListview Thread run! ");
//                            } catch (JSONException e) {
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
//                            String jsonResponse = rc.getBase64decode(response.body());
                            try {
                                dlog.i("UpdateWorkNotifyReadYn jsonResponse length : " + response.body().length());
                                dlog.i("UpdateWorkNotifyReadYn jsonResponse : " + response.body());
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
    private void getWorkNotifyReadYn() {
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
//                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetCommentList jsonResponse length : " + response.body().length());
                            dlog.i("GetCommentList jsonResponse : " + response.body());
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(response.body());

                                mList = new ArrayList<>();
                                mAdapter = new FeedConfrimLogAdapter(mContext, mList);
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
