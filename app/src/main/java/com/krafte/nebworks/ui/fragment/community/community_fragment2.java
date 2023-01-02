package com.krafte.nebworks.ui.fragment.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
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
import com.krafte.nebworks.adapter.CateAdapter;
import com.krafte.nebworks.adapter.OwnerCommunityAdapter;
import com.krafte.nebworks.adapter.PagingAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.SecondTapCommunityData;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.databinding.CommunityFragment2Binding;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class community_fragment2  extends Fragment {
    private CommunityFragment2Binding binding;
    private final static String TAG = "WorkStatusSubFragment1";
    Context mContext;
    Activity activity;

    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String place_owner_id = "";

    //Other
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

    //position 1
    ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> mList = new ArrayList<>();
    ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> searchmList = new ArrayList<>();
    OwnerCommunityAdapter mAdapter = null;

    ArrayList<StringData.StringData_list> subcateList = new ArrayList<>();
    ArrayList<String> subCate = new ArrayList<>();
    CateAdapter subcateAdapter = null;

    public static community_fragment2 newInstance() {
        return new community_fragment2();
    }

    String category = "전체";
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
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.membersub_fragment1, container, false);
        binding = CommunityFragment2Binding.inflate(inflater);
        mContext = inflater.getContext();

        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //Shared
        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            place_id = shardpref.getString("place_id", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");
            shardpref.putInt("SELECT_POSITION", 0);
            //-- 날짜 세팅
            dlog.i("place_owner_id : " + place_owner_id);

            subCate.add("전체");
            subCate.add("금융");
            subCate.add("기술");
            subCate.add("인력");
            subCate.add("수출");
            subCate.add("내수");
            subCate.add("창업");
            subCate.add("경영");
            subCate.add("기타");

            subcateList = new ArrayList<>();
            subcateAdapter = new CateAdapter(mContext, subcateList);
            binding.subCateList.setAdapter(subcateAdapter);
            binding.subCateList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
            for (int i = 0; i < subCate.size(); i++) {
                subcateAdapter.addItem(new StringData.StringData_list(
                        subCate.get(i)
                ));
            }
            subcateAdapter.notifyDataSetChanged();
            subcateAdapter.setOnItemClickListener(new CateAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    if (USER_INFO_AUTH.isEmpty()) {
                        isAuth();
                    } else {
                        subcateAdapter.notifyDataSetChanged();
                        dlog.i("select item : " + subCate.get(position));
                        category = subCate.get(position);
                        binding.searchTv.setText("");
                        setRecyclerViewpo2();
                    }
                }
            });


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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        cal = Calendar.getInstance();
        toDay = sdf.format(cal.getTime());
        dlog.i("오늘 :" + toDay);
        toDay = shardpref.getString("FtoDay",toDay);
        Handler handler = new Handler();
        handler.postDelayed(this::GetCrawling, 0);// 0.8초
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
                    searchmList.add(mList.get(i));
                }
            }
            mAdapter.filterList(searchmList);
            mAdapter.notifyDataSetChanged();
        }else{
            dlog.i("searchFilter 2");
            mAdapter.filterList(mList);
            mAdapter.notifyDataSetChanged();
        }
    }

    String search = "";
    private void setBtnEvent() {
        binding.searchBtn.setOnClickListener(v -> {
            if (USER_INFO_AUTH.isEmpty()) {
                isAuth();
            } else {
                search = binding.searchTv.getText().toString();
                searchFilter(search);
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
    ArrayList<StringData.StringData_list> mPage = new ArrayList<>();
    PagingAdapter mPAdapter;
    boolean isEmpty;
    Elements temele;
    Elements Pagewrap;
    String page_num = "1";

    private void GetCrawling() {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            try {
                stclist.clear();
                String URL = "https://www.bizinfo.go.kr/web/lay1/bbs/S1T122C128/AS/74/list.do?rows=15&cpage=" + page_num; // 지원리스트
                Document d = Jsoup.connect(URL).timeout(50000).get();    //URL 웹사이트에 있는 html 코드를 다 끌어오기
                temele = d.select("#articleSearchForm").select("div.support_project").select("div.table_Type_1").select("table").select("tbody").select("tr");
                Pagewrap = d.select("#container").select(".sub_cont").select(".page_wrap").select("a");
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
                    paging.add(Pagewrap.get(a).attr("title").replace("페이지","").replace(" ",""));
                    dlog.i("Pagewrap.get(" + a + ") : " + Pagewrap.get(a).attr("title"));
                }
                paging.remove("이전");
                paging.remove("처음");
                paging.remove("다음");
                paging.remove("마지막");
                dlog.i("paging : " + paging.toString().replace("페이지","").replace(" ",""));
            } catch (IOException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(this::setRecyclerViewpo2);
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

                    //Array데이터를 받아올 때 -- 페이징 번호
                    mPage = new ArrayList<>();
                    mPAdapter = new PagingAdapter(mContext, mPage,page_num);
                    binding.pageingList1.setAdapter(mPAdapter);
                    binding.pageingList1.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                    Log.i(TAG, "SetNoticeListview Thread run!");
                    for (int i = 0; i < paging.size(); i++) {
                        mPAdapter.addItem(new StringData.StringData_list(
                                paging.get(i)
                        ));
                    }
                    mPAdapter.notifyDataSetChanged();
                    mPAdapter.setOnItemClickListener(new PagingAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            if (USER_INFO_AUTH.isEmpty()) {
                                isAuth();
                            } else {
                                mPAdapter.notifyDataSetChanged();
                                page_num = paging.get(position);
                                dlog.i("page_num : " + page_num);
                                GetCrawling();
                            }
                        }
                    });

                    //Array데이터를 받아올 때 -- 리스트 내용
                    mList = new ArrayList<>();
                    mAdapter = new OwnerCommunityAdapter(mContext, mList, 1);
                    binding.allList2.setAdapter(mAdapter);
                    binding.allList2.smoothScrollToPosition(0);
                    binding.allList2.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    Log.i(TAG, "SetNoticeListview Thread run!");
                    dlog.i("temele : " + temele);
                    dlog.i("isEmpty : " + isEmpty);
                    dlog.i("id : " + id);
                    dlog.i("cate : " + cate);
                    dlog.i("title : " + title);
                    if(temele != null){
                        if (isEmpty) {
                            mList.clear();
                        } else {
                            if(category.equals("전체")){
                                for (int i = 0; i < temele.size(); i++) {
                                    mAdapter.addItem(new SecondTapCommunityData.SecondTapCommunityData_list(
                                            id.get(i),
                                            cate.get(i),
                                            title.get(i),
                                            gigan.get(i),
                                            location.get(i),
                                            date.get(i),
                                            view_cnt.get(i),
                                            link.get(i)
                                    ));
                                }
                            }else{
                                for (int i = 0; i < temele.size(); i++) {
                                    if(category.equals(cate.get(i))){
                                        mAdapter.addItem(new SecondTapCommunityData.SecondTapCommunityData_list(
                                                id.get(i),
                                                cate.get(i),
                                                title.get(i),
                                                gigan.get(i),
                                                location.get(i),
                                                date.get(i),
                                                view_cnt.get(i),
                                                link.get(i)
                                        ));
                                    }
                                }
                            }

                            mAdapter.notifyDataSetChanged();
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

    public void isAuth() {
        Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
        intent.putExtra("flag","더미");
        intent.putExtra("data","먼저 매장등록을 해주세요!");
        intent.putExtra("left_btn_txt", "닫기");
        intent.putExtra("right_btn_txt", "매장추가");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.translate_left, R.anim.translate_right);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
