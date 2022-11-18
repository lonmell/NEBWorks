package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.WorkerListAdapter;
import com.krafte.nebworks.data.WorkerlistData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class WorkerListActivity extends BottomSheetDialogFragment {
    private static final String TAG = "StoreListBottomSheet";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    //XML ID
    RecyclerView worker_list;
    TextView no_data_txt,title;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    ArrayList<String> user_id;
    ArrayList<String> user_name;
    ArrayList<String> img_path;
    ArrayList<String> jikgup;
    ArrayList<String> worktime;
    ArrayList<String> workyoil;
    String getuser_id = "";
    String getuser_name = "";
    String getimg_path = "";
    String getjikgup = "";
    String getworktime = "";
    String getworkyoil = "";
    ArrayList<WorkerlistData.WorkerlistData_list> taskpointlist;
    WorkerListAdapter mAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_workerlist_menu, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        try{
            mContext = inflater.getContext();
            dlog.DlogContext(mContext);
            fragmentManager = getParentFragmentManager();

            worker_list = view.findViewById(R.id.worker_list);
            no_data_txt = view.findViewById(R.id.no_data_txt);
            title = view.findViewById(R.id.title);
            title.setText("근무직원");
            no_data_txt.setText("근무 배정된 직원이 없습니다.");
            user_id = new ArrayList<>();
            user_name = new ArrayList<>();
            img_path = new ArrayList<>();
            jikgup = new ArrayList<>();
            worktime = new ArrayList<>();
            workyoil = new ArrayList<>();

            shardpref = new PreferenceHelper(mContext);
            getuser_id = shardpref.getString("worker_user_id","").replace("[","").replace("]","");
            getuser_name = shardpref.getString("worker_user_name","").replace("[","").replace("]","");
            getimg_path = shardpref.getString("worker_img_path","").replace("[","").replace("]","");
            getjikgup = shardpref.getString("worker_jikgup","").replace("[","").replace("]","");
            getworktime = shardpref.getString("worker_worktime","").replace("[","").replace("]","");
            getworkyoil = shardpref.getString("worker_workyoil","").replace("[","").replace("]","");

            dlog.i("getuser_id : " + getuser_id);
            dlog.i("getuser_name : " + getuser_name);
            dlog.i("getimg_path : " + getimg_path);
            dlog.i("getjikgup : " + getjikgup);
            dlog.i("getworktime : " + getworktime);
            dlog.i("getworkyoil : " + getworkyoil);

            user_id.addAll(Arrays.asList(getuser_id.split(",")));
            user_name.addAll(Arrays.asList(getuser_name.split(",")));
            img_path.addAll(Arrays.asList(getimg_path.split(",")));
            jikgup.addAll(Arrays.asList(getjikgup.split(",")));
            worktime.addAll(Arrays.asList(getworktime.split(",")));
            workyoil.addAll(Arrays.asList(getworkyoil.split(",")));


            dlog.i("user_id : " + user_id.size());
            dlog.i("user_name : " + user_name);
            dlog.i("img_path : " + img_path);
            dlog.i("jikgup : " + jikgup);
            dlog.i("worktime : " + worktime);
            dlog.i("workyoil : " + workyoil);

            if(getuser_id.isEmpty()){
                no_data_txt.setVisibility(View.VISIBLE);
            }else{
                no_data_txt.setVisibility(View.GONE);
                taskpointlist = new ArrayList<>();
                mAdapter = new WorkerListAdapter(mContext, taskpointlist);
                worker_list.setAdapter(mAdapter);
                worker_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                taskpointlist.clear();
                for (int i = 0; i < user_id.size(); i++) {
                    mAdapter.addItem(new WorkerlistData.WorkerlistData_list(
                            user_id.get(i),
                            user_name.get(i),
                            img_path.get(i),
                            jikgup.get(i),
                            worktime.get(i),
                            workyoil.get(i)
                    ));
                }
                mAdapter.notifyDataSetChanged();
                shardpref.remove("worker_user_id");
                shardpref.remove("worker_user_name");
                shardpref.remove("worker_img_path");
                shardpref.remove("worker_jikgup");
                shardpref.remove("worker_worktime");
                shardpref.remove("worker_workyoil");
            }
            setBtnEvent();
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private void setBtnEvent(){

    }
    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onStop(){
        super.onStop();
        shardpref.remove("worker_user_id");
        shardpref.remove("worker_user_name");
        shardpref.remove("worker_img_path");
        shardpref.remove("worker_jikgup");
        shardpref.remove("worker_worktime");
        shardpref.remove("worker_workyoil");
    }

}

