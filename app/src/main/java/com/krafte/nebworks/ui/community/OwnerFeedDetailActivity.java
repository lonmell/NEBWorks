package com.krafte.nebworks.ui.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.databinding.ActivityOwnerfeedDetailBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class OwnerFeedDetailActivity  extends AppCompatActivity {
    private ActivityOwnerfeedDetailBinding binding;
    private final static String TAG = "OwnerFeedDetailActivity";
    Context mContext;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    DateCurrent dc = new DateCurrent();
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    String oc_link = "";
    String oc_cate = "";
    String oc_title = "";

    @SuppressLint({"LongLogTag", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_community_add);
        binding = ActivityOwnerfeedDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContext = this;
        dlog.DlogContext(mContext);
        shardpref = new PreferenceHelper(mContext);
        oc_link = shardpref.getString("oc_link","");
        oc_cate = shardpref.getString("oc_cate","");
        oc_title = shardpref.getString("oc_title","");
    }

    @Override
    public void onResume() {
        super.onResume();
        GetCrawling();
    }


    Elements temele;
    Elements hash;
    String date = "";
    String department = "";
    String agency = "";
    String period = "";
    String overview = "";
    String hashtv = "";
    private void GetCrawling() {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            try {
                Document d = Jsoup.connect(oc_link).timeout(50000).get();    //URL 웹사이트에 있는 html 코드를 다 끌어오기
                dlog.i("oc_link : " + oc_link);
                temele = d.select(".sub_cont");
//                Pagewrap = d.select("#container").select("div.sub_cont").select("div.page_wrap").select("a");
                date = String.valueOf(d.select(".support_project_detail .top_info ul li")).replace("<li>","").replace("</li>","") ;
                department = String.valueOf(d.select(".view_cont").select("ul").select("li").get(0).select(".txt")).replace("<div class=\"txt\">","").replace("</div>","").replace(" ","");
                agency = String.valueOf(d.select(".view_cont").select("ul").select("li").get(1).select(".txt")).replace("<div class=\"txt\">","").replace("</div>","");
                period = String.valueOf(d.select(".view_cont").select("ul").select("li").get(2).select(".txt")).replace("<div class=\"txt\">","").replace("</div>","").replace("<span>","").replace("</span>","");
                overview = String.valueOf(d.select(".view_cont").select("ul").select("li").get(3).select(".txt").select("p")).replace("<p>","").replace("</p>","").trim().replace("<br>","").replace("</br>","");
                hash = d.select(".tag_list").select("ul").select("li");
                hashtv = String.valueOf(d.select(".tag_list").select("ul").select("li").select("span")).replace("<span>","").replace("</span>","").replace(" ",",").trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                setUIData();
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void setUIData(){
        binding.cate.setText(oc_cate);
        binding.title.setText(oc_title);
        binding.date.setText(date);
        binding.department.setText(department.trim());
        dlog.i("department : " + department.trim());
        binding.agency.setText(agency.trim());
        dlog.i("agency : " + agency.trim());
        binding.period.setText(period.trim());
        dlog.i("period : " + period.trim());
        binding.overview.setText(overview);
        dlog.i("overview : " + overview);
        binding.hashTag.setText(hashtv);
        dlog.i("hashTag ; " + hashtv.replace(" ",",").trim());
        binding.link.setText(oc_link);
    }
}
