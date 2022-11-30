package com.krafte.nebworks.ui.naviFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.CommunityAdapter;
import com.krafte.nebworks.adapter.OwnerCommunityAdapter;
import com.krafte.nebworks.adapter.ViewPagerFregmentAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.data.SecondTapCommunityData;
import com.krafte.nebworks.dataInterface.FeedNotiInterface;
import com.krafte.nebworks.databinding.CommunityfragmentBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CommunityFragment extends Fragment {
    private final static String TAG = "MoreFragment";
    private CommunityfragmentBinding binding;
    Context mContext;
    Activity activity;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String returnPage = "";
    String place_id = "";
    String place_owner_id = "";

    //Other
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    int paging_position = 0;
    int listitemsize = 0;

    //position 0
    ArrayList<PlaceNotiData.PlaceNotiData_list> BestmList = new ArrayList<>();
    CommunityAdapter BestmAdapter = null;
    ArrayList<PlaceNotiData.PlaceNotiData_list> mList = new ArrayList<>();
    CommunityAdapter mAdapter = null;

    //position 1
    ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> mList2 = new ArrayList<>();
    OwnerCommunityAdapter mAdapter2 = null;
    String category = "";

    public static CommunityFragment newInstance(int number) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("number", number);
        fragment.setArguments(bundle);
        return fragment;
    }

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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.morefragment, container, false);
        binding = CommunityfragmentBinding.inflate(inflater);
        mContext = inflater.getContext();

        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);

        setBtnEvent();

        //UI 데이터 세팅
        try {
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            returnPage = shardpref.getString("returnPage", "");
            place_id = shardpref.getString("place_id", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            Log.i(TAG, "USER_INFO_AUTH : " + USER_INFO_AUTH);
            ChangePage(0);
            setAddBtnSetting();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

        return binding.getRoot();
//        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            setRecyclerView();
            setRecyclerView2();
            GetCrawling();
        }, 800);// 0.8초
    }

    public void setBtnEvent() {
        binding.selectFragmentbtn1.setOnClickListener(v -> {
            ChangePage(0);
        });
        binding.selectFragmentbtn2.setOnClickListener(v -> {
            ChangePage(1);
        });
        binding.selectFragmentbtn3.setOnClickListener(v -> {
            ChangePage(2);
        });

        //position 0
        binding.more01.setOnClickListener(v -> {
            //인기게시글 전체보기
            shardpref.putInt("com_kind", 0);
            pm.MoreListCommunity(mContext);
        });
        binding.more02.setOnClickListener(v -> {
            //전체게시글 전체보기
            shardpref.putInt("com_kind", 1);
            pm.MoreListCommunity(mContext);
        });

        //position 2
    }

    private void ChangePage(int i) {
        binding.communityFragment1.setVisibility(View.GONE);
        binding.communityFragment2.setVisibility(View.GONE);
        binding.communityFragment3.setVisibility(View.GONE);

        binding.selectFragmenttv1.setTextColor(Color.parseColor("#696969"));
        binding.selectFragmentline1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.selectFragmenttv2.setTextColor(Color.parseColor("#696969"));
        binding.selectFragmentline2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        binding.selectFragmenttv3.setTextColor(Color.parseColor("#696969"));
        binding.selectFragmentline3.setBackgroundColor(Color.parseColor("#FFFFFF"));

        paging_position = i;
        if (i == 0) {
            binding.communityFragment1.setVisibility(View.VISIBLE);
            binding.selectFragmenttv1.setTextColor(Color.parseColor("#6395EC"));
            binding.selectFragmentline1.setBackgroundColor(Color.parseColor("#6395EC"));
        } else if (i == 1) {
            binding.communityFragment2.setVisibility(View.VISIBLE);
            binding.selectFragmenttv2.setTextColor(Color.parseColor("#6395EC"));
            binding.selectFragmentline2.setBackgroundColor(Color.parseColor("#6395EC"));
        } else if (i == 2) {
            binding.communityFragment3.setVisibility(View.VISIBLE);
            binding.selectFragmenttv3.setTextColor(Color.parseColor("#6395EC"));
            binding.selectFragmentline3.setBackgroundColor(Color.parseColor("#6395EC"));
        }
    }

    CardView add_worktime_btn;
    TextView addbtn_tv;

    private void setAddBtnSetting() {
        add_worktime_btn = binding.getRoot().findViewById(R.id.add_worktime_btn);
        addbtn_tv = binding.getRoot().findViewById(R.id.addbtn_tv);
        addbtn_tv.setText("게시글 작성");
        add_worktime_btn.setOnClickListener(v -> {
            if (paging_position == 0) {
                pm.CommunityAdd(mContext);
            } else if (paging_position == 1) {
                pm.OwnerFeedAdd(mContext);
            } else if (paging_position == 2) {
                Toast_Nomal("세금/노무");
            }
        });
    }

    /*List 전체 초기화*/
    private void allClear() {
        mList.clear();
        BestmList.clear();
    }

    //position 0
    public void setRecyclerView() {
        //Best
        allClear();
        dlog.i("position 0 setRecyclerView place_id : " + place_id);
        BestmList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "", "3", "2");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "position 0 WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "position 0 response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        BestmList = new ArrayList<>();
                        BestmAdapter = new CommunityAdapter(mContext, BestmList, 0);
                        binding.bestList.setAdapter(BestmAdapter);
                        binding.bestList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();

                        if (Response.length() == 0) {
                            binding.noDataTxt.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        } else {
                            binding.noDataTxt.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
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
                                        jsonObject.getString("link"),
                                        jsonObject.getString("feed_img_path"),
                                        jsonObject.getString("created_at"),
                                        jsonObject.getString("updated_at"),
                                        jsonObject.getString("open_date"),
                                        jsonObject.getString("close_date"),
                                        jsonObject.getString("boardkind"),
                                        jsonObject.getString("category")
                                ));
                            }
                            BestmAdapter.notifyDataSetChanged();
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

    public void setRecyclerView2() {
        //전체
        mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "", "2", "2");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "position 0 WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "position 0 response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "position 0 GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        mList = new ArrayList<>();
                        mAdapter = new CommunityAdapter(mContext, mList, 1);
                        binding.allList.setAdapter(mAdapter);
                        binding.allList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        listitemsize = Response.length();

                        if (Response.length() == 0) {
                            binding.noDataTxt2.setVisibility(View.VISIBLE);
                            Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                        } else {
                            binding.noDataTxt2.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if (jsonObject.getString("boardkind").equals("자유게시판")) {
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
                                            jsonObject.getString("link"),
                                            jsonObject.getString("feed_img_path"),
                                            jsonObject.getString("created_at"),
                                            jsonObject.getString("updated_at"),
                                            jsonObject.getString("open_date"),
                                            jsonObject.getString("close_date"),
                                            jsonObject.getString("boardkind"),
                                            jsonObject.getString("category")
                                    ));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
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


    //position 2
    ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> stclist = new ArrayList<>();
    List<String> id;
    List<String> cate;
    List<String> title;
    List<String> gigan;
    List<String> location;
    List<String> date;
    List<String> view_cnt;
    List<String> link;
    List<String> paging;
    boolean isEmpty;
    Elements temele;
    Elements Pagewrap;

    private void GetCrawling() {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            try {
                stclist.clear();
                String URL = "https://www.bizinfo.go.kr/web/lay1/bbs/S1T122C128/AS/74/list.do?rows=15&cpage=1"; // 지원리스트
                Document d = Jsoup.connect(URL).timeout(50000).get();    //URL 웹사이트에 있는 html 코드를 다 끌어오기
                temele = d.select("#articleSearchForm").select("div.support_project").select("div.table_Type_1").select("table").select("tbody").select("tr");
                Pagewrap = d.select("#container").select("div.sub_cont").select("div.page_wrap").select("a");
                isEmpty = temele.isEmpty(); //빼온 값 null체크
                id = new ArrayList<>();
                cate = new ArrayList<>();
                title = new ArrayList<>();//#articleSearchForm > div.support_project > div.table_Type_1 > table > tbody > tr:nth-child(1) > td.txt_l > a
                gigan = new ArrayList<>();
                location = new ArrayList<>();
                date = new ArrayList<>();
                view_cnt = new ArrayList<>();
                link = new ArrayList<>();
                paging = new ArrayList<>();
                for (int i = 0; i < temele.size(); i++) {
                    id.add(temele.get(i).select("td").get(0).toString().replace("<td>","").replace("</td>",""));
                    cate.add(temele.get(i).select("td").get(1).toString().replace("<td>","").replace("</td>",""));
                    title.add(temele.get(i).select("td").get(2).select("a").attr("title").toString().replace("<td>","").replace("</td>",""));
                    gigan.add(temele.get(i).select("td").get(3).toString().replace("<td>","").replace("</td>",""));
                    location.add(temele.get(i).select("td").get(4).toString().replace("<td>","").replace("</td>",""));
                    date.add(temele.get(i).select("td").get(5).toString().replace("<td>","").replace("</td>",""));
                    view_cnt.add(temele.get(i).select("td").get(6).toString().replace("<td>","").replace("</td>",""));
                    link.add(temele.get(i).select("td").get(2).select("a").attr("href").toString().replace("<td>","").replace("</td>",""));
                }

                for(int a = 0; a < Pagewrap.size(); a++){
                    paging.add(Pagewrap.get(a).data());
                }
                dlog.i("paging : " + paging);
            } catch (IOException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(() -> {
                setRecyclerViewpo2();
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setRecyclerViewpo2() {
        try {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                try{
                    //Array데이터를 받아올 때
                    mList2 = new ArrayList<>();
                    mAdapter2 = new OwnerCommunityAdapter(mContext, mList2, 1);
                    binding.allList2.setAdapter(mAdapter2);
                    binding.allList2.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    Log.i(TAG, "SetNoticeListview Thread run!");
                    dlog.i("temele : " + temele);
                    dlog.i("isEmpty : " + isEmpty);
                    dlog.i("id : " + id);
                    dlog.i("cate : " + cate);
                    dlog.i("title : " + title);
                    if(temele != null){
                        if (isEmpty) {
                            binding.noDataTxt2.setVisibility(View.VISIBLE);
                            mList2.clear();
                        } else {
                            binding.noDataTxt2.setVisibility(View.GONE);
                            for (int i = 0; i < temele.size(); i++) {
                                mAdapter2.addItem(new SecondTapCommunityData.SecondTapCommunityData_list(
                                        id.get(0),
                                        cate.get(0),
                                        title.get(0),
                                        gigan.get(0),
                                        location.get(0),
                                        date.get(0),
                                        view_cnt.get(0),
                                        link.get(0)
                                ));
                            }
                            mAdapter2.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }, 1000); //1초 후 인트로 실행

        } catch (Exception e) {
            e.printStackTrace();
        }
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
