package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.PlaceNameListAdapter;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.databinding.ActivityPlacelistBinding;
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

public class PlaceListBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "PlaceListBottomSheet";
    private ActivityPlacelistBinding binding;
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    //XML ID
    LinearLayout close_btn;
    LinearLayout list_settingitem01,list_settingitem02,list_settingitem03;

    // shared 저장값
    PreferenceHelper shardpref;
    String USER_INFO_EMAIL = "";
    String USER_INFO_NAME = "";
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    ArrayList<PlaceListData.PlaceListData_list> mList;
    PlaceNameListAdapter mAdapter = null;

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
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "-99");// 0:점주 / 1:근로자
            setBtnEvent();
            GetPlaceList();
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
//        list_settingitem03.setOnClickListener(v -> {
//            if (mListener03 != null) {
//                mListener03.onClick(v) ;
//            }
//            dismiss();
//        });
    }


    RetrofitConnect rc = new RetrofitConnect();
    public void GetPlaceList() {
        dlog.i("------GetPlaceList------");
        dlog.i("USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
        dlog.i("------GetPlaceList------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceListInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        PlaceListInterface api = retrofit.create(PlaceListInterface.class);
        Call<String> call = api.getData("", USER_INFO_ID,USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("GetPlaceList jsonResponse length : " + jsonResponse.length());
                            dlog.i("GetPlaceList jsonResponse : " + jsonResponse);
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(jsonResponse);
                                mList = new ArrayList<>();
                                mAdapter = new PlaceNameListAdapter(binding.getRoot().getContext(), mList);
                                binding.placeList.setAdapter(mAdapter);
                                binding.placeList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                dlog.i("SIZE : " + Response.length());
                                if (jsonResponse.equals("[]")) {
                                    binding.noData.setVisibility(View.VISIBLE);
                                    dlog.i("SetNoticeListview Thread run! ");
                                    dlog.i("GET SIZE : " + Response.length());
                                } else {
                                    binding.noData.setVisibility(View.GONE);
                                    for (int i = 0; i < Response.length(); i++) {
                                        JSONObject jsonObject = Response.getJSONObject(i);
                                        if(jsonObject.getString("save_kind").equals("1")){
                                            mAdapter.addItem(new PlaceListData.PlaceListData_list(
                                                    jsonObject.getString("id"),
                                                    jsonObject.getString("name"),
                                                    jsonObject.getString("owner_id"),
                                                    jsonObject.getString("owner_name"),
                                                    jsonObject.getString("owner_phone"),
                                                    jsonObject.getString("registr_num"),
                                                    jsonObject.getString("store_kind"),
                                                    jsonObject.getString("address"),
                                                    jsonObject.getString("latitude"),
                                                    jsonObject.getString("longitude"),
                                                    jsonObject.getString("pay_day"),
                                                    jsonObject.getString("test_period"),
                                                    jsonObject.getString("vacation_select"),
                                                    jsonObject.getString("insurance"),
                                                    jsonObject.getString("start_time"),
                                                    jsonObject.getString("end_time"),
                                                    jsonObject.getString("save_kind"),
                                                    jsonObject.getString("img_path"),
                                                    jsonObject.getString("accept_state"),
                                                    jsonObject.getString("total_cnt"),
                                                    jsonObject.getString("i_cnt"),
                                                    jsonObject.getString("o_cnt"),
                                                    jsonObject.getString("created_at")
                                            ));
                                        }
                                    }
                                }

                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener(new PlaceNameListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        try {
                                            dlog.i("place_latitude : " + shardpref.getString("place_latitude", ""));
                                            dlog.i("place_longitude : " + shardpref.getString("place_longitude", ""));
                                            String owner_id = Response.getJSONObject(pos).getString("owner_id");
                                            String place_name = Response.getJSONObject(pos).getString("name");
                                            String myid = shardpref.getString("USER_INFO_ID", "0");
                                            String place_id = Response.getJSONObject(pos).getString("id");
                                            String save_kind = Response.getJSONObject(pos).getString("save_kind");
                                            String accept_state = Response.getJSONObject(pos).getString("accept_state");
                                            String place_imgpath = Response.getJSONObject(pos).getString("img_path");
                                            dlog.i("owner_id : " + owner_id);
                                            dlog.i("place_name : " + place_name);
                                            dlog.i("myid : " + myid);
                                            dlog.i("place_id : " + place_id);
                                            dlog.i("save_kind : " + save_kind);
                                            dlog.i("accept_state : " + accept_state);
                                            dlog.i("place_imgpath : " + place_imgpath);

//                                            shardpref.putString("place_id", place_id);
//                                            shardpref.putString("place_name", place_name);
//                                            shardpref.putString("place_imgpath", place_imgpath);
                                            if (mListener01 != null) {
                                                mListener01.onClick(v,place_id,place_name,owner_id);
                                            }
                                            dismiss();
                                        } catch (JSONException e) {
                                            dlog.i("GetPlaceList OnItemClickListener Exception :" + e);
                                        }
                                    }
                                });
                                dlog.i("SetNoticeListview Thread run! ");
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure (@NonNull Call< String > call, @NonNull Throwable t){
                dlog.e("에러1 = " + t.getMessage());
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
