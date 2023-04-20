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
import com.krafte.nebworks.adapter.TwoItemStringAdapter;
import com.krafte.nebworks.data.StringTwoData;
import com.krafte.nebworks.dataInterface.PlaceListInterface;
import com.krafte.nebworks.util.Dlog;
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

public class PaySelectPlaceActivity extends BottomSheetDialogFragment {
    private static final String TAG = "StoreListBottomSheet";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    //XML ID
    RecyclerView worker_list;
    TextView no_data_txt, title;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    ArrayList<StringTwoData.StringTwoData_list> mList;
    TwoItemStringAdapter mAdapter;
    String place_id = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_workerlist_menu, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        try {
            mContext = inflater.getContext();
            dlog.DlogContext(mContext);
            fragmentManager = getParentFragmentManager();

            worker_list = view.findViewById(R.id.worker_list);
            no_data_txt = view.findViewById(R.id.no_data_txt);
            title = view.findViewById(R.id.title);
            title.setText("매장명");
            no_data_txt.setText("조회된 매장이 없습니다.");


            shardpref = new PreferenceHelper(mContext);
            place_id = shardpref.getString("place_id", "");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");

            GetPlaceList();

            setBtnEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void setBtnEvent() {

    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //list_settingitem01
    public interface OnClickListener {
        void onClick(View v, String place_id, String place_name);
    }

    private OnClickListener mListener = null;

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
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
        Call<String> call = api.getData("", USER_INFO_ID, USER_INFO_AUTH);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = rc.getBase64decode(response.body());
                            dlog.i("jsonResponse length : " + jsonResponse.length());
                            dlog.i("jsonResponse : " + jsonResponse);
                            try {
                                //Array데이터를 받아올 때
                                JSONArray Response = new JSONArray(jsonResponse);
                                mList = new ArrayList<>();
                                mAdapter = new TwoItemStringAdapter(mContext, mList);
//                                mAdapter.addItem(new StringTwoData.StringTwoData_list(
//                                        "",
//                                        "전체매장"
//                                ));
                                worker_list.setAdapter(mAdapter);
                                worker_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                dlog.i("SIZE : " + Response.length());
                                if (jsonResponse.equals("[]")) {
                                    no_data_txt.setVisibility(View.VISIBLE);
                                    dlog.i("SetNoticeListview Thread run! ");
                                    dlog.i("GET SIZE : " + Response.length());
                                } else {
                                    no_data_txt.setVisibility(View.GONE);
                                    for (int i = 0; i < Response.length(); i++) {
                                        JSONObject jsonObject = Response.getJSONObject(i);
                                        mAdapter.addItem(new StringTwoData.StringTwoData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name")
                                        ));
                                    }
                                    mAdapter.setOnItemClickListener(new TwoItemStringAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View v, int position, String item1, String item2) {
                                            if (mListener != null) {
                                                try {
                                                    mListener.onClick(v, item1, item2);
                                                    dismiss();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });

                                }
                                mAdapter.notifyDataSetChanged();
                                dlog.i("SetNoticeListview Thread run! ");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

}
