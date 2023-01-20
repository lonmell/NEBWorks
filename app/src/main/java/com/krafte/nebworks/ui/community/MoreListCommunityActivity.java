package com.krafte.nebworks.ui.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.CommunityAdapter;
import com.krafte.nebworks.adapter.SelectCateAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.data.ReturnPageData;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.data.UserCheckData;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.ActivityCommunityAllBinding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
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


public class MoreListCommunityActivity extends AppCompatActivity {
    private ActivityCommunityAllBinding binding;
    private final static String TAG = "WorkCommunityActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String returnPage = "";
    String place_id = "";
    int com_kind = 0;

    ArrayList<PlaceNotiData.PlaceNotiData_list> BestmList = new ArrayList<>();
    CommunityAdapter BestmAdapter = null;
    ArrayList<PlaceNotiData.PlaceNotiData_list> mList = new ArrayList<>();
    CommunityAdapter mAdapter = null;

    ArrayList<String> setCate = new ArrayList<>();
    ArrayList<StringData.StringData_list> mData = new ArrayList<>();
    SelectCateAdapter cateAdapter = null;

    //Other
    RetrofitConnect rc = new RetrofitConnect();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    int paging_position = 0;
    int listitemsize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_work_community);
        binding = ActivityCommunityAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;

        setBtnEvent();
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        //Singleton Area
        USER_INFO_ID    = UserCheckData.getInstance().getUser_id();
        returnPage      = ReturnPageData.getInstance().getPage();
        place_id        = PlaceCheckData.getInstance().getPlace_id();

        //shardpref Area
        com_kind        = shardpref.getInt("com_kind",0); // -- 0 : 인기게시글 / 1 : 전체게시글
        returnPage      = shardpref.getString("returnPage", "");
        USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","");

        binding.pageTitle.setText((com_kind == 0)?"인기게시글":"전체게시글");
        binding.selectCategory.setVisibility((com_kind == 0)?View.GONE:View.VISIBLE);
        setAddBtnSetting();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(com_kind == 0){
            setRecyclerView();
        }else if(com_kind == 1){
            setCateList();
            setRecyclerView2();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setBtnEvent() {
    }

    String selectCate = "";
    private void setCateList(){
        mData.clear();
        setCate.add("#전체보기");
        setCate.add("#정보에요");
        setCate.add("#화나요");
        setCate.add("#억울해요");
        setCate.add("#자랑해요");
        setCate.add("#점주가 말한다");
        setCate.add("#알바가 말한다");
        setCate.add("#이런 사람 조심하세요");
        setCate.add("#이런 상황 조심하세요");

        mData = new ArrayList<>();
        dlog.i("setCate : " + setCate);
        cateAdapter = new SelectCateAdapter(mContext,mData);
        binding.selectCategory.setAdapter(cateAdapter);
        binding.selectCategory.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        for(String str : setCate.toString().replace("[","").replace("]","").trim().split(",")){
            cateAdapter.addItem(new StringData.StringData_list(
                    str
            ));
        }
        cateAdapter.notifyDataSetChanged();
        cateAdapter.setOnItemClickListener(new SelectCateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selectCate = setCate.get(position);
                if(selectCate.equals("#전체보기")){
                    selectCate = "";
                }
                dlog.i("onItemClick : " + selectCate);
                setRecyclerView2();
            }
        });
    }

    /*List 전체 초기화*/
    private void allClear() {
        mList.clear();
        BestmList.clear();
    }

    int total_cnt = 0;
    public void setRecyclerView() {
        //Best List
        allClear();
        dlog.i("position 0 setRecyclerView place_id : " + place_id);
        BestmList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "", "3", "2", USER_INFO_ID);
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
                        BestmList = new ArrayList<>();
                        BestmAdapter = new CommunityAdapter(mContext, BestmList, 0);
                        binding.allList.setAdapter(BestmAdapter);
                        binding.allList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();

                        if (USER_INFO_AUTH.isEmpty()) {
                            BestmAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                    "",
                                    "",
                                    "인기 게시글",
                                    "인기 게시글의 내용",
                                    "",
                                    "김닉넴",
                                    "",
                                    "",
                                    "20,300",
                                    "1,300",
                                    "12,000",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "n"
                            ));
                        } else {
                            if (Response.length() == 0) {
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (Integer.parseInt(jsonObject.getString("view_cnt")) > 50
                                            && jsonObject.getString("boardkind").equals("자유게시판")) {
                                        total_cnt++;
                                        BestmAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
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
                                                jsonObject.getString("mylikeyn")
                                        ));
                                    }
                                }
                            }
                            BestmAdapter.notifyDataSetChanged();
                        }
                        BestmAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                if (USER_INFO_AUTH.isEmpty()) {
                                    isAuth();
                                } else {
                                    shardpref.putString("feed_id", mList.get(position).getId());
                                    shardpref.putString("title", mList.get(position).getTitle());
                                    shardpref.putString("contents", mList.get(position).getContents());
                                    shardpref.putString("writer_id", mList.get(position).getWriter_id());
                                    shardpref.putString("writer_name", mList.get(position).getWriter_name());
                                    shardpref.putString("writer_img_path", mList.get(position).getWriter_img_path());
                                    shardpref.putString("feed_img_path", mList.get(position).getFeed_img_path());
                                    shardpref.putString("jikgup", mList.get(position).getJikgup());
                                    shardpref.putString("view_cnt", mList.get(position).getView_cnt());
                                    shardpref.putString("comment_cnt", mList.get(position).getComment_cnt());
                                    shardpref.putString("like_cnt", mList.get(position).getLike_cnt());
                                    shardpref.putString("category", mList.get(position).getCategory());
                                    shardpref.putString("updated_at", mList.get(position).getUpdated_at());
                                    shardpref.putString("mylikeyn", mList.get(position).getMylikeyn());
                                    pm.CommunityDetail(mContext);
                                }
                            }
                        });
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

    int total_cnt2 = 0;
    public void setRecyclerView2() {
        //전체
        mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "", "2", "2", USER_INFO_ID);
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
                        mList = new ArrayList<>();
                        mAdapter = new CommunityAdapter(mContext, mList, 1);
                        binding.allList.setAdapter(mAdapter);
                        binding.allList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        listitemsize = Response.length();

                        if (USER_INFO_AUTH.isEmpty()) {
                            binding.noDataTxt2.setVisibility(View.GONE);
                            mAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                    "",
                                    "",
                                    "게시글",
                                    "게시글의 내용",
                                    "",
                                    "김닉넴",
                                    "",
                                    "",
                                    "300",
                                    "12",
                                    "34",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "n"
                            ));
                        } else {
                            if (Response.length() == 0) {
                                binding.noDataTxt2.setVisibility(View.VISIBLE);
                                dlog.i("GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                binding.noDataTxt2.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (jsonObject.getString("boardkind").equals("자유게시판")) {
                                        if(!selectCate.isEmpty()){
                                            dlog.i("selectCate : " + selectCate);
                                            dlog.i("category : " + jsonObject.getString("category"));
                                            total_cnt2++;
                                            if(jsonObject.getString("category").equals(selectCate.replace("#","").trim())){
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
                                                        jsonObject.getString("mylikeyn")
                                                ));
                                            }
                                        }else{
                                            total_cnt2++;
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
                                                    jsonObject.getString("mylikeyn")
                                            ));
                                        }
                                    }
                                }
                            }
                            selectCate = "";
                            mAdapter.notifyDataSetChanged();
                            if (total_cnt2 == 0) {
                                binding.allList.setVisibility(View.GONE);
                                binding.noDataTxt2.setVisibility(View.VISIBLE);
                            } else {
                                binding.allList.setVisibility(View.VISIBLE);
                                binding.noDataTxt2.setVisibility(View.GONE);
                            }
                        }
                        mAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                if (USER_INFO_AUTH.isEmpty()) {
                                    isAuth();
                                } else {
                                    shardpref.putString("feed_id", mList.get(position).getId());
                                    shardpref.putString("title", mList.get(position).getTitle());
                                    shardpref.putString("contents", mList.get(position).getContents());
                                    shardpref.putString("writer_id", mList.get(position).getWriter_id());
                                    shardpref.putString("writer_name", mList.get(position).getWriter_name());
                                    shardpref.putString("writer_img_path", mList.get(position).getWriter_img_path());
                                    shardpref.putString("feed_img_path", mList.get(position).getFeed_img_path());
                                    shardpref.putString("jikgup", mList.get(position).getJikgup());
                                    shardpref.putString("view_cnt", mList.get(position).getView_cnt());
                                    shardpref.putString("comment_cnt", mList.get(position).getComment_cnt());
                                    shardpref.putString("like_cnt", mList.get(position).getLike_cnt());
                                    shardpref.putString("category", mList.get(position).getCategory());
                                    shardpref.putString("updated_at", mList.get(position).getUpdated_at());
                                    shardpref.putString("mylikeyn", mList.get(position).getMylikeyn());
                                    pm.CommunityDetail(mContext);
                                }
                            }
                        });
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

    CardView add_worktime_btn;
    TextView addbtn_tv;
    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("게시글 작성");
        add_worktime_btn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                if (paging_position == 0) {
                    shardpref.putString("state","AddCommunity");
                    pm.CommunityAdd(mContext);
                } else if (paging_position == 1) {
                    Toast_Nomal("사장님 게시글");
                } else if (paging_position == 2) {
                    Toast_Nomal("세금/노무");
                }
            }
        });
    }

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag", "더미");
        intent.putExtra("data", "먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    public void Toast_Nomal(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_normal_toast, (ViewGroup) binding.getRoot().findViewById(R.id.toast_layout));
        TextView toast_textview = layout.findViewById(R.id.toast_textview);
        toast_textview.setText(String.valueOf(message));
        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); //TODO 메시지가 표시되는 위치지정 (가운데 표시)
        //toast.setGravity(Gravity.TOP, 0, 0); //TODO 메시지가 표시되는 위치지정 (상단 표시)
        toast.setGravity(Gravity.BOTTOM, 0, 0); //TODO 메시지가 표시되는 위치지정 (하단 표시)
        toast.setDuration(Toast.LENGTH_SHORT); //메시지 표시 시간
        toast.setView(layout);
        toast.show();
    }
}
