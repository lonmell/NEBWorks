package com.krafte.nebworks.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.adapter.ListStringAdapter;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.databinding.ActivityStoredivisionpopBinding;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StoreDivisionPopActivity extends BottomSheetDialogFragment {
    private ActivityStoredivisionpopBinding binding;
    private static final String TAG = "StoreDivisionPopActivity";
    Context mContext;
    View view;
    Activity activity;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    DateCurrent dc = new DateCurrent();
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    int setSelectPicker = 0;
    Dlog dlog = new Dlog();
    String SetItem = "";
    List<String> setItem = new ArrayList<>();
    ArrayList<StringData.StringData_list> mList;
    ArrayList<StringData.StringData_list> searchmList;

    ListStringAdapter mAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_storedivisionpop, container, false);
        binding = ActivityStoredivisionpopBinding.inflate(getLayoutInflater(), container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        mContext = inflater.getContext();
        shardpref = new PreferenceHelper(mContext);
        dlog.DlogContext(mContext);

        setItem.add("일반음식점");
        setItem.add("편의점");
        setItem.add("의류 / 잡화 / 쥬얼리 매장");
        setItem.add("뷰티 / 헬스 스토어");
        setItem.add("휴대폰 / 전자기기 매장");
        setItem.add("가구 / 침구 / 인테리어");
        setItem.add("서점 / 문구 / 팬시");
        setItem.add("놀이공원 / 테마파크");
        setItem.add("호텔 / 리조트 / 숙박");
        setItem.add("영화관 / 공연장");
        setItem.add("주유 / 세차");
        setItem.add("패밀리 레스토랑");
        setItem.add("배달대행 / 음식배달");
        setItem.add("패스트푸드점");
        setItem.add("피자전문점");
        setItem.add("커피전문점");
        setItem.add("아이스크림 / 디저트");
        setItem.add("베이커리 / 도넛 / 떡");
        setItem.add("호프 / 일반주점");
        setItem.add("도시락 / 반찬");
        setItem.add("백화점 / 쇼핑몰");
        setItem.add("유통점 / 마트");
        setItem.add("PC방");

        mList = new ArrayList<>();
        searchmList = new ArrayList<>();
        mAdapter = new ListStringAdapter(mContext, mList);
        binding.categoryList.setAdapter(mAdapter);
        binding.categoryList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        for (int i = 0; i < setItem.size(); i++) {
            mAdapter.addItem(new StringData.StringData_list(
                    setItem.get(i)
            ));
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClickListener(new ListStringAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                dlog.i("Get onItem : " + mList.get(position));
                if (mListener != null) {
                    mListener.onItemClick(v, mList.get(position).getItem());
                }
                dismiss();
            }
        });

        binding.searchCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchFilter(s.toString());
            }
        });
        binding.searchCategory.setImeOptions(EditorInfo.IME_ACTION_DONE); //키보드 다음 버튼을 완료 버튼으로 바꿔줌
        binding.searchCategory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //키보드에 완료버튼을 누른 후 수행할 것
                SetItem = binding.searchCategory.getText().toString();
                searchFilter(SetItem);
                return true;
            }
            return false;
        });
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchFilter(String searchText) {
        if(searchText.length() != 0 && mList.size() != 0){
            searchmList.clear();
            dlog.i("searchFilter 1");
            dlog.i("mList.size() : " + mList.size());
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getItem().toLowerCase().contains(searchText.toLowerCase())) {
                    dlog.i("searchFilter contain : " + mList.get(i).getItem() + "/" + mList.get(i).getItem().toLowerCase().contains(searchText.toLowerCase()));
//                    mList.clear();
                    searchmList.add(mList.get(i));
//                    break;
                }
            }
            mAdapter.filterList(searchmList);
            mAdapter.notifyDataSetChanged();
        }else{
            dlog.i("searchFilter 2");
            mAdapter.filterList(mList);
            mAdapter.notifyDataSetChanged();
        }
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
