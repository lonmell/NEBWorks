package com.krafte.kogas.ui.navi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.kogas.adapter.PlaceNotiAdapter;
import com.krafte.kogas.adapter.ViewPagerFregmentAdapter;
import com.krafte.kogas.data.PlaceNotiData;
import com.krafte.kogas.dataInterface.FeedNotiInterface;
import com.krafte.kogas.dataInterface.FeedSearchInterface;
import com.krafte.kogas.databinding.WorkgotofragmentBinding;
import com.krafte.kogas.util.DateCurrent;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;
import com.krafte.kogas.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WorkgotoFragment extends Fragment {
    private final static String TAG = "WorkgotoFragment";
    private WorkgotofragmentBinding binding;
    Context mContext;

    Activity activity;

    //Other 클래스
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    PreferenceHelper shardpref;
    DateCurrent dc = new DateCurrent();
    Handler handler = new Handler();
    RetrofitConnect rc = new RetrofitConnect();
    ViewPagerFregmentAdapter viewPagerFregmentAdapter;

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
    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";

    String return_page = "";
    int SELECT_POSITION = 0;
    int rotate_addwork = 0;
    String tap_kind = "";

    public static WorkgotoFragment newInstance(int number) {
        WorkgotoFragment fragment = new WorkgotoFragment();
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


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.workgotofragment, container, false);
        binding = WorkgotofragmentBinding.inflate(inflater);
        mContext = inflater.getContext();
        //UI 데이터 세팅
        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

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
            SELECT_POSITION = shardpref.getInt("SELECT_POSITION", 0);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","-1"); //0-관리자 / 1- 근로자
            return_page = shardpref.getString("return_page","");
            setBtnEvent();

            final List<String> tabElement;
            dlog.i("USER_INFO_AUTH : " + USER_INFO_AUTH);
            if(USER_INFO_AUTH.equals("0")){
                binding.fragmentbtn1.setVisibility(View.VISIBLE);
                binding.fragmentbtn2.setVisibility(View.VISIBLE);
                binding.fragmentbtn3.setVisibility(View.VISIBLE);

            }else if(USER_INFO_AUTH.equals("1")){
                binding.fragmentbtn1.setVisibility(View.VISIBLE);
                binding.fragmentbtn2.setVisibility(View.VISIBLE);
                binding.fragmentbtn3.setVisibility(View.GONE);
            }

            binding.fragmentbtn1.setOnClickListener(v -> {
                binding.inputNotiarea.setVisibility(View.VISIBLE);
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#a9a9a9"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
                Page1Fragment();
                setRecyclerView1();
            });
            binding.fragmentbtn2.setOnClickListener(v -> {
                binding.inputNotiarea.setVisibility(View.GONE);
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#a9a9a9"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#ffffff"));
            });
            binding.fragmentbtn3.setOnClickListener(v -> {
                binding.inputNotiarea.setVisibility(View.GONE);
                binding.fragmentline1.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline2.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.fragmentline3.setBackgroundColor(Color.parseColor("#a9a9a9"));
            });
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
        Page1Fragment();
        setRecyclerView1();
    }

    private void setBtnEvent() {

    }


    String USER_INFO_EMAIL = "";
    String NotiSearch = "";
    public void Page1Fragment(){
        //공지사항
        dlog.i("Page1Fragment START");
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
            binding.userManualtv.setText("공지 사항 카테고리에 등록된 공지사항이 없습니다.\n + 를 터치하여 공지사항을 등록 해 보세요.");

            binding.inputNotitv.setImeOptions(EditorInfo.IME_ACTION_DONE);
            binding.inputNotitv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        NotiSearch = binding.inputNotitv.getText().toString();
                        if (NotiSearch.isEmpty()) {
                            setRecyclerView1();
                        } else {
                            setSearchNoti1();
                        }
                        return true;
                    }
                    return false;
                }
            });
            binding.inputNotitv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    NotiSearch = binding.inputNotitv.getText().toString();
                    if (s.length() == 0) {
                        setRecyclerView1();
                    }
                }
            });
        } catch (Exception e) {
            dlog.i("onCreate Exception : " + e);
        }
    }

    ArrayList<PlaceNotiData.PlaceNotiData_list> mList1;
    PlaceNotiAdapter mAdapter1 = null;
    int listitemsize = 0;

    public void setRecyclerView1() {
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
                            mList1 = new ArrayList<>();
                            mAdapter1 = new PlaceNotiAdapter(mContext, mList1);
                            binding.storeChecklist.setAdapter(mAdapter1);
                            binding.storeChecklist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();

                            if (Response.length() == 0) {
                                binding.nodataArea.setVisibility(View.VISIBLE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                binding.nodataArea.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter1.addItem(new PlaceNotiData.PlaceNotiData_list(
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
                                mAdapter1.notifyDataSetChanged();
                                mAdapter1.setOnItemClickListener(new PlaceNotiAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        mAdapter1.notifyDataSetChanged();
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
    public void setSearchNoti1() {
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
                            mList1 = new ArrayList<>();
                            mAdapter1 = new PlaceNotiAdapter(mContext, mList1);
                            binding.storeChecklist.setAdapter(mAdapter1);
                            binding.storeChecklist.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                            Log.i(TAG, "SetNoticeListview Thread run! ");
                            listitemsize = Response.length();

                            if (Response.length() == 0) {
                                binding.nodataArea.setVisibility(View.GONE);
                                Log.i(TAG, "GET SIZE : " + rc.placeNotiData_lists.size());
                            } else {
                                binding.nodataArea.setVisibility(View.GONE);
                                for (int i = 0; i < Response.length(); i++) {
                                    JSONObject jsonObject = Response.getJSONObject(i);
                                    mAdapter1.addItem(new PlaceNotiData.PlaceNotiData_list(
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
                                mAdapter1.notifyDataSetChanged();
                                mAdapter1.setOnItemClickListener(new PlaceNotiAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        mAdapter1.notifyDataSetChanged();
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
