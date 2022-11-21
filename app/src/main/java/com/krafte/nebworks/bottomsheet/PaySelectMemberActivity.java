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
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.TwoItemStringAdapter;
import com.krafte.nebworks.data.StringTwoData;
import com.krafte.nebworks.dataInterface.AllMemberInterface;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PaySelectMemberActivity extends BottomSheetDialogFragment {
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
    String change_place_id = "";
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
            change_place_id = shardpref.getString("change_place_id","");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");


            SetAllMemberList(change_place_id.equals("")?place_id:change_place_id);

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
        shardpref.remove("change_place_id");
    }

    //list_settingitem01
    public interface OnClickListener {
        void onClick(View v, String user_id, String user_name);
    }

    private OnClickListener mListener = null;

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    public void SetAllMemberList(String place_id) {
        dlog.i("SetAllMemberList place_id : " + place_id);
        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AllMemberInterface.URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            AllMemberInterface api = retrofit.create(AllMemberInterface.class);
            Call<String> call = api.getData(place_id,"");
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            //Array데이터를 받아올 때
                            JSONArray Response = new JSONArray(response.body());
                            dlog.i("SetAllMemberList response.body() length : " + response.body());
                            if (Response.length() == 0) {
                                no_data_txt.setVisibility(View.VISIBLE);
                            } else {
                                no_data_txt.setVisibility(View.INVISIBLE);
                                worker_list.setVisibility(View.VISIBLE);
                                mList = new ArrayList<>();
                                mAdapter = new TwoItemStringAdapter(mContext, mList);
                                mAdapter.addItem(new StringTwoData.StringTwoData_list(
                                        "",
                                        "전체직원"
                                ));
                                worker_list.setHasFixedSize(true);
                                worker_list.setAdapter(mAdapter);
                                worker_list.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                                RecyclerView.ItemAnimator animator = worker_list.getItemAnimator();
                                if (animator instanceof SimpleItemAnimator) {
                                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                                }
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    //정직원만
                                    if (!jsonObject.getString("kind").equals("0")) {
                                        mAdapter.addItem(new StringTwoData.StringTwoData_list(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("name")
                                        ));
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
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

}
