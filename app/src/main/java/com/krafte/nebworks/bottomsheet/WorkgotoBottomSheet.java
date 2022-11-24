package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.krafte.nebworks.data.TodolistData;
import com.krafte.nebworks.dataInterface.TaskSelectWInterface;
import com.krafte.nebworks.databinding.ActivityPlacelistBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkgotoBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "WorkgotoBottomSheet";
    private ActivityPlacelistBinding binding;
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String place_id = "";
    String select_date = "";
    String getYMPicker = "";

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
            USER_INFO_ID = shardpref.getString("change_member_id", "");
            place_id = shardpref.getString("change_place_id", "");
            select_date = shardpref.getString("task_date", "");
            dlog.i("-----onCreateView-----");
            dlog.i("USER_INFO_ID : " + USER_INFO_ID);
            dlog.i("place_id : " + place_id);
            dlog.i("select_date : " + select_date);
            dlog.i("-----onCreateView-----");

            String year = select_date.substring(0,4);
            String month = select_date.substring(4,6);
            String day = select_date.substring(7,9);
            getYMPicker = year + "-" + month + "-" + day;
            binding.title.setText(month + "월 " + day + "일 할일 목록");
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
    private OnClickListener mListener01 = null ;
    public void setOnClickListener01(OnClickListener listener) {
        this.mListener01 = listener ;
    }

    private void setBtnEvent(){
    }


    public void setRecyclerView() {
        dlog.i("setTodoWList place_id : " + place_id);
        dlog.i("setTodoWList USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("setTodoWList select_date : " + getYMPicker);
        Todo_mList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TaskSelectWInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        TaskSelectWInterface api = retrofit.create(TaskSelectWInterface.class);
        Call<String> call = api.getData(place_id, USER_INFO_ID, getYMPicker);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment2 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                dlog.e("response 2: " + rc.getBase64decode(response.body()));
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + rc.getBase64decode(response.body()));
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(rc.getBase64decode(response.body()));
                        Todo_mList = new ArrayList<>();
                        Todo_mAdapter = new Tap2ListAdapter(mContext, Todo_mList, getParentFragmentManager(), 2);
                        binding.placeList.setAdapter(Todo_mAdapter);
                        binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                        Log.i(TAG, "SetNoticeListview Thread run! ");
                        Log.i(TAG, "GET SIZE : " + Response.length());
                        if (Response.length() == 0) {
                            binding.nodataArea.setVisibility(View.VISIBLE);
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            Log.i(TAG, "GET SIZE : " + Response.length());
//                            check_cnt.setText("0건");
//                            all_checkbox.setClickable(false);
//                            all_checkbox.setEnabled(false);
//                            all_checkbox.setBackgroundResource(R.drawable.checkbox_off);
                        } else {
                            binding.nodataArea.setVisibility(View.GONE);
                            for (int i = 0; i < Response.length(); i++) {
                                JSONObject jsonObject = Response.getJSONObject(i);
                                if(!jsonObject.getString("id").isEmpty() || !jsonObject.getString("id").equals("null")){
                                    Todo_mAdapter.addItem(new TodolistData.TodolistData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("kind"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("complete_kind"),
                                            Collections.singletonList(jsonObject.getString("users")),
                                            jsonObject.getString("task_date"),
                                            jsonObject.getString("start_time"),
                                            jsonObject.getString("end_time"),
                                            jsonObject.getString("sun"),
                                            jsonObject.getString("mon"),
                                            jsonObject.getString("tue"),
                                            jsonObject.getString("wed"),
                                            jsonObject.getString("thu"),
                                            jsonObject.getString("fri"),
                                            jsonObject.getString("sat"),
                                            jsonObject.getString("img_path"),
                                            jsonObject.getString("complete_yn"),
                                            jsonObject.getString("incomplete_reason"),
                                            jsonObject.getString("approval_state"),
                                            jsonObject.getString("task_overdate")
                                    ));
                                }
                            }
                            Todo_mAdapter.notifyDataSetChanged();
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

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

}
