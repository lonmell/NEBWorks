package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ListYoilStringAdapter;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.databinding.ActivitySelectyoilBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SelectYoilActivity extends BottomSheetDialogFragment {
    private ActivitySelectyoilBinding binding;
    private static final String TAG = "SelectYoilActivity";
    Context mContext;
    View view;
    Activity activity;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    List<String> setItem = new ArrayList<>();
    ArrayList<StringData.StringData_list> mList;
    ArrayList<StringData.StringData_list> searchmList;
    List<String> selectYoil = new ArrayList<>();
    ListYoilStringAdapter mAdapter;

    String dayOfWeek = "";

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_storedivisionpop, container, false);
        binding = ActivitySelectyoilBinding.inflate(getLayoutInflater(), container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        setItem.add("월요일");
        setItem.add("화요일");
        setItem.add("수요일");
        setItem.add("목요일");
        setItem.add("금요일");
        setItem.add("토요일");
        setItem.add("일요일");

        dayOfWeek = shardpref.getString("select_yoil", "");
        if (!dayOfWeek.equals("")) {
            String[] dayOfWeekSplit = dayOfWeek.split(",");
            for (int i = 0; i < dayOfWeekSplit.length; i++) {
                dayOfWeekSplit[i] = dayOfWeekSplit[i] + "요일";
            }
            selectYoil.addAll(Arrays.asList(dayOfWeekSplit));
            dlog.i("selectYoil: " + selectYoil);
        }


        mList = new ArrayList<>();
        searchmList = new ArrayList<>();
        mAdapter = new ListYoilStringAdapter(mContext, mList, selectYoil);
        binding.categoryList.setAdapter(mAdapter);
        binding.categoryList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        for (int i = 0; i < setItem.size(); i++) {
            mAdapter.addItem(new StringData.StringData_list(
                    setItem.get(i)
            ));
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClickListener(new ListYoilStringAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                dlog.i("Get onItem : " + mList.get(position));
                if(selectYoil.contains(mList.get(position).getItem())){
                    selectYoil.remove(mList.get(position).getItem());
                }else{
                    selectYoil.add(mList.get(position).getItem());
                }
            }
        });
        binding.saveYoilBtn.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(v, String.valueOf(selectYoil));
            }
            dismiss();
        });

        return binding.getRoot();
    }

    /*Fragment 콜백함수*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, String category);
    }
}
