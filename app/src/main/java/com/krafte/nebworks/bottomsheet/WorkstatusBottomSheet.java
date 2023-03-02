package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.Tap2ListAdapter;
import com.krafte.nebworks.adapter.WorkTapMemberAdapter;
import com.krafte.nebworks.data.PlaceCheckData;
import com.krafte.nebworks.data.TodolistData;
import com.krafte.nebworks.data.WorkStatusTapData;
import com.krafte.nebworks.dataInterface.WorkStatusTapInterface;
import com.krafte.nebworks.databinding.ActivityPlacelistBinding;
import com.krafte.nebworks.pop.WorkMemberOptionActivity;
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

public class WorkstatusBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "WorkgotoBottomSheet";
    private ActivityPlacelistBinding binding;
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
    String place_id = "";
    String select_date = "";
    String getYMPicker = "";
    String FtoDay = "";
    String change_place_id = "";

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    ArrayList<TodolistData.TodolistData_list> Todo_mList = new ArrayList<>();
    Tap2ListAdapter Todo_mAdapter = null;
    RetrofitConnect rc = new RetrofitConnect();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_placelist, container, false);
        binding = ActivityPlacelistBinding.inflate(getLayoutInflater(), container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        try{
            mContext = inflater.getContext();
            dlog.DlogContext(mContext);
            fragmentManager = getParentFragmentManager();


            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "");
            place_id        = shardpref.getString("place_id", "");
            FtoDay          = shardpref.getString("FtoDay", "");
            change_place_id = shardpref.getString("change_place_id","").isEmpty()? PlaceCheckData.getInstance().getPlace_id():shardpref.getString("change_place_id","");

            dlog.i("-----onCreateView-----");
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("place_id : " + place_id);
            dlog.i("select_date : " + select_date);
            dlog.i("FtoDay : " + FtoDay);
            dlog.i("-----onCreateView-----");

            String year = FtoDay.substring(0,4);
            String month = FtoDay.substring(5,7);
            String day = FtoDay.substring(8,10);
            getYMPicker = year + "-" + month + "-" + day;
            binding.title.setText(month + "월 " + day + "일 근무자");
            setBtnEvent();
            setRecyclerView();
        }catch (Exception e){
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    //list_settingitem01
    public interface OnClickListener {
        void onClick(View v, String place_id, String place_name, String place_owner_id) ;
    }
    private WorkgotoBottomSheet.OnClickListener mListener01 = null ;
    public void setOnClickListener01(WorkgotoBottomSheet.OnClickListener listener) {
        this.mListener01 = listener ;
    }

    private void setBtnEvent(){
    }

    ArrayList<WorkStatusTapData.WorkStatusTapData_list> mList;
    WorkTapMemberAdapter mAdapter = null;

    public void setRecyclerView() {
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WorkStatusTapInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            WorkStatusTapInterface api = retrofit.create(WorkStatusTapInterface.class);
            Call<String> call = api.getData(place_id,USER_INFO_ID,"99",FtoDay);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(jsonResponse);
                            mList = new ArrayList<>();
                            mAdapter = new WorkTapMemberAdapter(mContext, mList, "99",getParentFragmentManager());
                            binding.placeList.setAdapter(mAdapter);
                            binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

                            if (Response.length() == 0) {
                                binding.nodataArea.setVisibility(View.VISIBLE);
                                binding.placeList.setVisibility(View.GONE);
                            } else {
                                binding.nodataArea.setVisibility(View.GONE);
                                binding.placeList.setVisibility(View.VISIBLE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new WorkStatusTapData.WorkStatusTapData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("place_name"),
                                            jsonObject.getString("user_id"),
                                            jsonObject.getString("name"),
                                            jsonObject.getString("account"),
                                            jsonObject.getString("img_path"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("jikgup"),
                                            jsonObject.getString("join_date"),
                                            jsonObject.getString("yoil"),
                                            jsonObject.getString("io_date"),
                                            jsonObject.getString("io_time"),
                                            jsonObject.getString("in_time"),
                                            jsonObject.getString("out_time"),
                                            jsonObject.getString("worktime"),
                                            jsonObject.getString("commuting"),
                                            jsonObject.getString("vaca_accept"),
                                            jsonObject.getString("hdd")
                                    ));
                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener((v, position) -> {
                                    shardpref.putString("status_id", mList.get(position).getId());
                                    shardpref.putString("mem_id", mList.get(position).getUser_id());
                                    shardpref.putString("mem_name", mList.get(position).getName());
                                    shardpref.putString("remote", "workhour");
                                    Intent intent = new Intent(mContext, WorkMemberOptionActivity.class);
                                    intent.putExtra("place_id", change_place_id);
                                    intent.putExtra("user_id", mList.get(position).getId());
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    dlog.e("에러 = " + t.getMessage());
                }
            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

}
