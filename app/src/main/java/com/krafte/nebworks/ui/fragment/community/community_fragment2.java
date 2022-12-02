package com.krafte.nebworks.ui.fragment.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import com.krafte.nebworks.adapter.OwnerCommunityAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.SecondTapCommunityData;
import com.krafte.nebworks.databinding.CommunityFragment2Binding;
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
    ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> mList2 = new ArrayList<>();
    OwnerCommunityAdapter mAdapter2 = null;
    String category = "";

    public static community_fragment2 newInstance() {
        return new community_fragment2();
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
            shardpref.putInt("SELECT_POSITION", 0);
            //-- 날짜 세팅
            dlog.i("place_owner_id : " + place_owner_id);
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

    private void setBtnEvent() {

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
                            mList2.clear();
                        } else {
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

}
