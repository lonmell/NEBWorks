package com.krafte.nebworks.ui.fragment.career;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

public class CareerFragment3  extends Fragment {
    private static final String TAG = "WorkplaceEmployeeFragment2";
    DBConnection dbConnection = new DBConnection();
    Context mContext;

    //XML ID
    RecyclerView on_store_list;

    //shardpref
    String USER_INFO_ID = "";
    String USER_INFO_NAME = "";
    String USER_INFO_AUTH = "";
    String store_no = "";

    //Other
    Activity activity;


    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    GetResultData resultData = new GetResultData();
//    ArrayList<GongGoData.GongGoData_list> STORE_mList;
//    GuinStoreAdapter store_mAdapter = null;
    RetrofitConnect rc = new RetrofitConnect();

    public static CareerFragment3 newInstance(int number) {
        CareerFragment3 fragment = new CareerFragment3();
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String shared = "heypass";

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.career_fragment3, container, false);
        mContext = inflater.getContext();
        dlog.DlogContext(mContext);

        //XML
        on_store_list = rootView.findViewById(R.id.on_store_list);


        //Shared
        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");
        USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
        shardpref.remove("returnpage");
//        SetWorkplaceList(USER_INFO_ID);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        SetWorkplaceList(USER_INFO_ID);
    }

    /*매장전체 리스트 START*/
    public void SetWorkplaceList(String user_id) {
//        dlog.i("user_id:" + user_id);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(GonggoInterface.URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        GonggoInterface api = retrofit.create(GonggoInterface.class);
//        Call<String> call = api.getStoreList("","");
//        call.enqueue(new Callback<String>() {
//            @SuppressLint({"NotifyDataSetChanged", "LongLogTag", "SetTextI18n"})
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                dlog.e("SetWorkplaceList function START");
//                dlog.e("response 1: " + response.isSuccessful());
//                dlog.e("response 2: " + rc.getBase64decode(response.body()));
//                if (response.isSuccessful() && response.body() != null) {
//                    String jsonResponse = rc.getBase64decode(response.body());
//                    try {
//
//                        STORE_mList = new ArrayList<>();
//                        store_mAdapter = new GuinStoreAdapter(mContext, STORE_mList);
//                        on_store_list.setAdapter(store_mAdapter);
//                        on_store_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//                        //Array데이터를 받아올 때
//                        JSONArray Response = new JSONArray(jsonResponse);
//                        for (int i = 0; i < Response.length(); i++) {
//                            JSONObject jsonObject = Response.getJSONObject(i);
//
//                            store_mAdapter.addItem(new GongGoData.GongGoData_list(
//                                    jsonObject.getString("no"),
//                                    jsonObject.getString("store_no"),
//                                    jsonObject.getString("write_id"),
//                                    jsonObject.getString("owner_img"),
//                                    jsonObject.getString("store_name"),
//                                    jsonObject.getString("store_img"),
//                                    jsonObject.getString("title"),
//                                    jsonObject.getString("store_address"),
//                                    jsonObject.getString("gender"),
//                                    jsonObject.getString("workkind"),
//                                    jsonObject.getString("workstate"),
//                                    jsonObject.getString("sun"),
//                                    jsonObject.getString("mon"),
//                                    jsonObject.getString("tue"),
//                                    jsonObject.getString("wed"),
//                                    jsonObject.getString("thu"),
//                                    jsonObject.getString("fri"),
//                                    jsonObject.getString("sat"),
//                                    jsonObject.getString("starttime"),
//                                    jsonObject.getString("endtime"),
//                                    jsonObject.getString("timeConference"),
//                                    jsonObject.getString("paykind"),
//                                    jsonObject.getString("pay"),
//                                    jsonObject.getString("payConference"),
//                                    jsonObject.getString("welfare01"),
//                                    jsonObject.getString("welfare02"),
//                                    jsonObject.getString("welfare03"),
//                                    jsonObject.getString("welfare04"),
//                                    jsonObject.getString("othertx"),
//                                    jsonObject.getString("input_date"),
//                                    jsonObject.getString("state")
//                            ));
//                        }
//                        store_mAdapter.notifyDataSetChanged();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                dlog.e("에러 = " + t.getMessage());
//            }
//        });
    }
}
