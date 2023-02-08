package com.krafte.nebworks.ui.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.CommunityAdapter;
import com.krafte.nebworks.adapter.MyListCommentAdapter;
import com.krafte.nebworks.data.MyCommentData;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.dataInterface.MyListInterface;
import com.krafte.nebworks.databinding.ActivityMylistBinding;
import com.krafte.nebworks.pop.CommunityOptionActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyListActivity extends AppCompatActivity {
    private ActivityMylistBinding binding;
    private final static String TAG = "MyListActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    DateCurrent dc = new DateCurrent();
    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();
    Dlog dlog = new Dlog();

    //page_kind - 0
    ArrayList<PlaceNotiData.PlaceNotiData_list> mList = new ArrayList<>();
    CommunityAdapter mAdapter = null;

    ArrayList<MyCommentData.MyCommentData_list> mList2 = new ArrayList<>();
    MyListCommentAdapter mAdapter2 = null;

    //0 - 작성 게시글 확인 , 1 - 작성 댓글 확인 , 2 - 북마크
    int page_kind = 0;
    int listitemsize = 0;
    String USER_INFO_ID = "";


    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityMylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref       = new PreferenceHelper(mContext);
        page_kind       = shardpref.getInt("page_kind", 0);
        USER_INFO_ID    = shardpref.getString("USER_INFO_ID","");

        binding.backBtn.setOnClickListener(v -> {
            pm.MoreBack(mContext);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
        setRecyclerView(page_kind);
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        shardpref.remove("page_kind");
    }

    private void allClear(){
        mList.clear();
        mList2.clear();
        listitemsize = 0;
    }
    public void setRecyclerView(int i) {
        allClear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MyListInterface api = retrofit.create(MyListInterface.class);
        Call<String> call = api.getData(USER_INFO_ID, String.valueOf(i));
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "position 0 WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "position 0 response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("jsonResponse length : " + jsonResponse.length());
                    dlog.i("jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        if(i == 0){
                            binding.title.setText("작성 게시글 확인");
                            mList = new ArrayList<>();
                            mAdapter = new CommunityAdapter(mContext, mList, 1);
                            binding.myList.setAdapter(mAdapter);
                            binding.myList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();

                            if (Response.length() == 0) {
                                binding.noData.setVisibility(View.VISIBLE);
                                binding.myList.setVisibility(View.GONE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                binding.noData.setVisibility(View.GONE);
                                binding.myList.setVisibility(View.VISIBLE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);

                                    mAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("writer_name"),
                                            jsonObject.getString("writer_img_path"),
                                            jsonObject.getString("jikgup"),
                                            jsonObject.getString("view_cnt"),
                                            jsonObject.getString("comment_cnt"),
                                            jsonObject.getString("like_cnt"),
                                            jsonObject.getString("link"),
                                            jsonObject.getString("feed_img_path"),
                                            jsonObject.getString("created_at"),
                                            jsonObject.getString("updated_at"),
                                            jsonObject.getString("open_date"),
                                            jsonObject.getString("close_date"),
                                            jsonObject.getString("boardkind"),
                                            jsonObject.getString("category"),
                                            jsonObject.getString("mylikeyn"),
                                            jsonObject.getString("fix_yn")
                                    ));
                                }
                            }
                            mAdapter.notifyDataSetChanged();

                        }else if(i == 1){
                            binding.title.setText("작성 댓글 확인");
                            mList2 = new ArrayList<>();
                            mAdapter2 = new MyListCommentAdapter(mContext, mList2);
                            binding.myList.setAdapter(mAdapter2);
                            binding.myList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();

                            if (Response.length() == 0) {
                                binding.noData.setVisibility(View.VISIBLE);
                                binding.myList.setVisibility(View.GONE);
                            } else {
                                binding.noData.setVisibility(View.GONE);
                                binding.myList.setVisibility(View.VISIBLE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter2.addItem(new MyCommentData.MyCommentData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("feed_id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("comment"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("writer_name"),
                                            jsonObject.getString("writer_img_path"),
                                            jsonObject.getString("edit_yn"),
                                            jsonObject.getString("updated_at")
                                    ));
                                }
                            }
                            mAdapter2.notifyDataSetChanged();
                            mAdapter2.setOnItemClickListener(new MyListCommentAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position, String comment_id, String comment, String writer_name, String feed_id, String write_id, String title, String contents, String comment_contents, String write_date) {
                                    dlog.i("delete comment_id : " + comment_id);
                                    shardpref.putString("comment_id", comment_id);
                                    Intent intent = new Intent(mContext, CommunityOptionActivity.class);

                                    intent.putExtra("state", "EditComment");
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
                        }else if(i == 2){
                            binding.title.setText("북마크");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }
}
