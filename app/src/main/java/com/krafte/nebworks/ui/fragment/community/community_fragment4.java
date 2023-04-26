package com.krafte.nebworks.ui.fragment.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.CommunityAdapter;
import com.krafte.nebworks.adapter.SelectCateAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.dataInterface.FobiddenInterface;
import com.krafte.nebworks.databinding.CommunityFragment1Binding;
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
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/*
* 2023-03-28 방창배 작성 - 근로자 게시판
*
* */
public class community_fragment4 extends Fragment {
    private CommunityFragment1Binding binding;
    private final static String TAG = "community_fragment4";
    Context mContext;
    Activity activity;

    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String place_id = "";
    String place_owner_id = "";
    String USER_INFO_AUTH = "";

    //Other
    //position 0
    ArrayList<PlaceNotiData.PlaceNotiData_list> searchmList = new ArrayList<>();
    ArrayList<PlaceNotiData.PlaceNotiData_list> BestmList = new ArrayList<>();
    CommunityAdapter BestmAdapter = null;
    ArrayList<PlaceNotiData.PlaceNotiData_list> mList = new ArrayList<>();
    CommunityAdapter mAdapter = null;

    ArrayList<String> setCate = new ArrayList<>();
    ArrayList<StringData.StringData_list> mData = new ArrayList<>();
    SelectCateAdapter cateAdapter = null;

    List<String> getWord = new ArrayList<>();
    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    DateCurrent dc = new DateCurrent();
    int listitemsize = 0;
    Dlog dlog = new Dlog();
    int total_member_cnt = 0;
    String toDay = "";
    Calendar cal;
    String format = "yyyy-MM-dd";
    SimpleDateFormat sdf = new SimpleDateFormat(format);

    public static community_fragment4 newInstance() {
        return new community_fragment4();
    }

    String str;

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int num = getArguments().getInt("number");
            Log.i(TAG, "num : " + num);
        }
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CommunityFragment1Binding.inflate(inflater);
        mContext = inflater.getContext();

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //Shared
        try {
            //Singleton Area
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID","");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL","");
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH","");
            place_id        = shardpref.getString("place_id","");
            place_owner_id  = shardpref.getString("place_owner_id","");
            
            binding.title.setText("알바끼리 잡담");

            //shardpref Area
            shardpref.putInt("SELECT_POSITION", 0);

            //-- 날짜 세팅
            setBtnEvent();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        toDay = shardpref.getString("FtoDay", toDay);

        setCateList();
        getFobiddenWord();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setBtnEvent() {

        //position 0
        binding.more01.setOnClickListener(v -> {
            //인기게시글 전체보기
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putInt("com_kind", 0);
                pm.MoreListCommunity(mContext);
            }
        });
        binding.more02.setOnClickListener(v -> {
            //전체게시글 전체보기
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                shardpref.putInt("com_kind", 1);
                pm.MoreListCommunity(mContext);
            }
        });

        binding.searchCommunity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchFilter(s.toString());
            }
        });
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
                binding.selectCatelist.setText(selectCate);
                dlog.i("onItemClick : " + selectCate);
                setRecyclerView2();
                cateAdapter.notifyDataSetChanged();
            }
        });
    }

    /*List 전체 초기화*/
    private void allClear() {
        mList.clear();
        BestmList.clear();
        getWord.clear();
    }

    public void getFobiddenWord() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FobiddenInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FobiddenInterface api = retrofit.create(FobiddenInterface.class);
        Call<String> call = api.getData("", "0");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("FobiddenList jsonResponse length : " + jsonResponse.length());
                    dlog.i("FobiddenList jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        mList = new ArrayList<>();
                        for(int i = 0;i < Response.length(); i++){
                            getWord.add(Response.getJSONObject(i).getString("word"));
                        }
                        shardpref.putString("FobiddenWord",String.valueOf(getWord));
                        shardpref.putString("boardkind","근로자게시판");
                        setRecyclerView();
                        setRecyclerView2();
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
        Call<String> call = api.getData(place_id, "", "3", "2", USER_INFO_ID,"근로자게시판");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "position 0 WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "position 0 response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("community_fragment4 jsonResponse length : " + jsonResponse.length());
                    dlog.i("community_fragment4 jsonResponse : " + jsonResponse);
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(jsonResponse);
                        BestmList = new ArrayList<>();
                        BestmAdapter = new CommunityAdapter(mContext, BestmList, 0);
                        binding.bestList.setAdapter(BestmAdapter);
                        binding.bestList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();

                        if (USER_INFO_AUTH.isEmpty()) {
                            binding.bestListTitle.setVisibility(View.VISIBLE);
                            binding.bestList.setVisibility(View.VISIBLE);
                            binding.bestUnderLine.setVisibility(View.VISIBLE);
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
                                    "n",
                                    "n"
                            ));
                        } else {
                            if (Response.length() == 0) {
                                binding.bestListTitle.setVisibility(View.GONE);
                                binding.bestList.setVisibility(View.GONE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                binding.bestListTitle.setVisibility(View.VISIBLE);
                                binding.bestList.setVisibility(View.VISIBLE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    if (Integer.parseInt(jsonObject.getString("view_cnt")) > 50
                                            && jsonObject.getString("boardkind").equals("근로자게시판")) {
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
                                                jsonObject.getString("mylikeyn"),
                                                jsonObject.getString("fix_yn")
                                        ));
                                    }
                                }
                            }
                            if (total_cnt == 0) {
                                binding.bestListTitle.setVisibility(View.GONE);
                                binding.bestList.setVisibility(View.GONE);
                            }
                            BestmAdapter.notifyDataSetChanged();
                            String searchTv = binding.searchCommunity.getText().toString();
                            if(searchTv.length() > 0){
                                searchFilter(searchTv);
                            }
                        }
                        BestmAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                if (USER_INFO_AUTH.isEmpty()) {
                                    isAuth();
                                } else {
                                    shardpref.putString("feed_id", BestmList.get(position).getId());
                                    shardpref.putString("title", BestmList.get(position).getTitle());
                                    shardpref.putString("contents", BestmList.get(position).getContents());
                                    shardpref.putString("writer_id", BestmList.get(position).getWriter_id());
                                    shardpref.putString("writer_name", BestmList.get(position).getWriter_name());
                                    shardpref.putString("writer_img_path", BestmList.get(position).getWriter_img_path());
                                    shardpref.putString("feed_img_path", BestmList.get(position).getFeed_img_path());
                                    shardpref.putString("jikgup", BestmList.get(position).getJikgup());
                                    shardpref.putString("view_cnt", BestmList.get(position).getView_cnt());
                                    shardpref.putString("comment_cnt", BestmList.get(position).getComment_cnt());
                                    shardpref.putString("like_cnt", BestmList.get(position).getLike_cnt());
                                    shardpref.putString("category", BestmList.get(position).getCategory());
                                    shardpref.putString("updated_at", BestmList.get(position).getUpdated_at());
                                    shardpref.putString("mylikeyn", BestmList.get(position).getMylikeyn());
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
        Call<String> call = api.getData(place_id, "", "2", "2", USER_INFO_ID,"근로자게시판");
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
                        dlog.i("SetNoticeListview Thread run! ");
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
                                    "n",
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
                                    if (jsonObject.getString("boardkind").equals("근로자게시판")) {
                                        if(!selectCate.isEmpty()){
                                            dlog.i("selectCate : " + selectCate);
                                            dlog.i("category : " + jsonObject.getString("category"));
                                            total_cnt2++;
                                            if(jsonObject.getString("category").equals(selectCate)){
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
                                                    jsonObject.getString("mylikeyn"),
                                                    jsonObject.getString("fix_yn")
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
                            String searchTv = binding.searchCommunity.getText().toString();
                            if(searchTv.length() > 0){
                                searchFilter(searchTv);
                            }
                        }
                        mAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {

                                if (USER_INFO_AUTH.isEmpty()) {
                                    isAuth();
                                } else {
                                    if(mList.size() != 0){
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
                                    }else{
                                        setRecyclerView2();
                                    }
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

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag", "더미");
        intent.putExtra("data", "먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchFilter(String searchText) {
        if(searchText.length() != 0 && mList.size() != 0){
            searchmList.clear();
            dlog.i("searchFilter 1");
            dlog.i("mList.size() : " + mList.size());
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    dlog.i("searchFilter contain : " + mList.get(i).getTitle() + "/" + mList.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase()));
//                    mList.clear();
                    searchmList.add(mList.get(i));
//                    break;
                }
            }
            mAdapter.filterList(searchmList);
            mAdapter.notifyDataSetChanged();
        }else{
            dlog.i("searchFilter 2");
            mAdapter.filterList(mList);
            mAdapter.notifyDataSetChanged();
        }
        if(searchText.length() != 0 && BestmList.size() != 0){
            searchmList.clear();
            dlog.i("searchFilter 1");
            dlog.i("mList.size() : " + BestmList.size());
            for (int i = 0; i < BestmList.size(); i++) {
                if (BestmList.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    dlog.i("searchFilter contain : " + BestmList.get(i).getTitle() + "/" + BestmList.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase()));
                    searchmList.add(mList.get(i));
                }
            }
            mAdapter.filterList(searchmList);
            mAdapter.notifyDataSetChanged();
        }else{
            dlog.i("searchFilter 2");
            mAdapter.filterList(BestmList);
            mAdapter.notifyDataSetChanged();
        }
    }
}
