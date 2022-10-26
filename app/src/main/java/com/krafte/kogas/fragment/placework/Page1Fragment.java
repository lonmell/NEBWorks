package com.krafte.kogas.fragment.placework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.R;
import com.krafte.kogas.adapter.PlaceNotiAdapter;
import com.krafte.kogas.data.GetResultData;
import com.krafte.kogas.data.PlaceNotiData;
import com.krafte.kogas.dataInterface.FeedNotiInterface;
import com.krafte.kogas.dataInterface.FeedSearchInterface;
import com.krafte.kogas.ui.worksite.PlaceAddWorkActivity;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 2022-10-07 현장 공지사항 방창배 작성
 * */
public class Page1Fragment extends Fragment {
    private final static String TAG = "Page1Fragment";

    Context mContext;
    Activity activity;

    //XML ID
    RecyclerView store_checklist;
    LinearLayout nodata_area, search_date;
    RelativeLayout login_alert_text, search_icon;
    ImageView loading_view, user_manualimg;
    TextView user_manualtv;
    EditText input_notitv;

    //sharedPreferences
    PreferenceHelper shardpref;
    String USER_INFO_ID = "";
    String USER_INFO_EMAIL = "";

    //Other
    ArrayList<PlaceNotiData.PlaceNotiData_list> imgmList;
    ArrayList<PlaceNotiData.PlaceNotiData_list> mList;
    PlaceNotiAdapter mAdapter = null;
    RetrofitConnect rc = new RetrofitConnect();
    GetResultData resultData = new GetResultData();
    PageMoveClass pm = new PageMoveClass();
    int listitemsize = 0;
    Dlog dlog = new Dlog();

    public static Page1Fragment newInstance(int number) {
        Page1Fragment fragment = new Page1Fragment();
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
            Log.i(TAG, "num : " + num);
        }
    }

    //shared
    String place_id = "";
    String place_name = "";
    String place_owner_id = "";
    String place_owner_name = "";
    String place_management_office = "";
    String place_address = "";
    String place_latitude = "";
    String place_longitude = "";
    String place_start_time = "";
    String place_end_time = "";
    String place_img_path = "";
    String place_start_date = "";
    String place_created_at = "";

    String NotiSearch = "";


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.worktap_fragment1, container, false);
        mContext = inflater.getContext();

        //XML
        store_checklist = rootView.findViewById(R.id.store_checklist);
        login_alert_text = rootView.findViewById(R.id.login_alert_text);
        loading_view = rootView.findViewById(R.id.loading_view);
        nodata_area = rootView.findViewById(R.id.nodata_area);
        user_manualimg = rootView.findViewById(R.id.user_manualimg);
        user_manualtv = rootView.findViewById(R.id.user_manualtv);
        input_notitv = rootView.findViewById(R.id.input_notitv);
        search_icon = rootView.findViewById(R.id.search_icon);
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        //Shared
        try {
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "0");
            USER_INFO_EMAIL = shardpref.getString("USER_INFO_EMAIL", "0");
            place_id = shardpref.getString("place_id", "0");
            place_name = shardpref.getString("place_name", "0");
            place_owner_id = shardpref.getString("place_owner_id", "0");
            place_owner_name = shardpref.getString("place_owner_name", "0");
            place_management_office = shardpref.getString("place_management_office", "0");
            place_address = shardpref.getString("place_address", "0");
            place_latitude = shardpref.getString("place_latitude", "0");
            place_longitude = shardpref.getString("place_longitude", "0");
            place_start_time = shardpref.getString("place_start_time", "0");
            place_end_time = shardpref.getString("place_end_time", "0");
            place_img_path = shardpref.getString("place_img_path", "0");
            place_start_date = shardpref.getString("place_start_date", "0");
            place_created_at = shardpref.getString("place_created_at", "0");
            shardpref.putInt("selectposition", 0);
            user_manualtv.setText("공지 사항 카테고리에 등록된 공지사항이 없습니다.\n + 를 터치하여 공지사항을 등록 해 보세요.");

            input_notitv.setImeOptions(EditorInfo.IME_ACTION_DONE);
            input_notitv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        NotiSearch = input_notitv.getText().toString();
                        if (NotiSearch.isEmpty()) {
                            setRecyclerView();
                        } else {
                            setSearchNoti();
                        }
                        return true;
                    }
                    return false;
                }
            });
            input_notitv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    NotiSearch = input_notitv.getText().toString();
                    if (s.length() == 0) {
                        setRecyclerView();
                    }
                }
            });

            setBtnEvent();
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }

//        PlaceAddWorkActivity ema = new PlaceAddWorkActivity();
//        ema.setOnClickListener(this::setRecyclerView);

        return rootView;
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
        setRecyclerView();
    }

    private void setBtnEvent() {
        dlog.i("NotiSearch : " + NotiSearch);
        search_icon.setOnClickListener(v -> {
            if (NotiSearch.isEmpty()) {
                setRecyclerView();
            } else {
                setSearchNoti();
            }
        });
    }

    public void setRecyclerView() {
        dlog.i("setRecyclerView place_id : " + place_id);
        rc.placeNotiData_lists.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedNotiInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedNotiInterface api = retrofit.create(FeedNotiInterface.class);
        Call<String> call = api.getData(place_id, "");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setRecyclerView");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null && response.body().length() != 0) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때
                        JSONArray Response = new JSONArray(response.body());
                        if (listitemsize != Response.length()) {
                            mList = new ArrayList<>();
                            mAdapter = new PlaceNotiAdapter(mContext, mList);
                            store_checklist.setAdapter(mAdapter);
                            store_checklist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();

                            if (Response.length() == 0) {
                                nodata_area.setVisibility(View.VISIBLE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                nodata_area.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("writer_name"),
                                            jsonObject.getString("writer_img_path"),
                                            jsonObject.getString("writer_department"),
                                            jsonObject.getString("writer_position"),
                                            jsonObject.getString("view_cnt"),
                                            jsonObject.getString("comment_cnt"),
                                            jsonObject.getString("link"),
                                            jsonObject.getString("feed_img_path"),
                                            jsonObject.getString("created_at"),
                                            jsonObject.getString("updated_at")
                                    ));
                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener(new PlaceNotiAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
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

    public void setSearchNoti() {
        dlog.i("setSearchNoti place_id : " + place_id);
        rc.placeNotiData_lists.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FeedSearchInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        FeedSearchInterface api = retrofit.create(FeedSearchInterface.class);
        Call<String> call = api.getData(place_id, NotiSearch);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.e(TAG, "WorkTapListFragment1 / setSearchNoti");
                Log.e(TAG, "response 1: " + response.isSuccessful());
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "GetWorkStateInfo function onSuccess : " + response.body());
                    try {
                        //Array데이터를 받아올 때

                        JSONArray Response = new JSONArray(response.body());
                        if (listitemsize != Response.length()) {
                            mList = new ArrayList<>();
                            mAdapter = new PlaceNotiAdapter(mContext, mList);
                            store_checklist.setAdapter(mAdapter);
                            store_checklist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();

                            if (Response.length() == 0) {
                                nodata_area.setVisibility(View.GONE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                nodata_area.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter.addItem(new PlaceNotiData.PlaceNotiData_list(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("place_id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("contents"),
                                            jsonObject.getString("writer_id"),
                                            jsonObject.getString("writer_name"),
                                            jsonObject.getString("writer_img_path"),
                                            jsonObject.getString("writer_department"),
                                            jsonObject.getString("writer_position"),
                                            jsonObject.getString("view_cnt"),
                                            jsonObject.getString("comment_cnt"),
                                            jsonObject.getString("link"),
                                            jsonObject.getString("feed_img_path"),
                                            jsonObject.getString("created_at"),
                                            jsonObject.getString("updated_at")
                                    ));
                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setOnItemClickListener(new PlaceNotiAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
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
}
