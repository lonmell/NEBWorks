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
import com.krafte.nebworks.adapter.ListStringAdapter;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.databinding.ActivitySelectCateBinding;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectStringBottomSheet extends BottomSheetDialogFragment {
    private ActivitySelectCateBinding binding;
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
    String place_id = "";
    String change_place_id = "";
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";
    int SelectKind = 0;

    List<String> setItem = new ArrayList<>();
    ArrayList<StringData.StringData_list> mList;
    ArrayList<StringData.StringData_list> searchmList;
    ListStringAdapter mAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.activity_select_cate, container, false);
        binding = ActivitySelectCateBinding.inflate(getLayoutInflater());
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        try {
            mContext = inflater.getContext();
            dlog.DlogContext(mContext);
            fragmentManager = getParentFragmentManager();

            shardpref = new PreferenceHelper(mContext);
            place_id = shardpref.getString("place_id", "");
            change_place_id = shardpref.getString("change_place_id","");
            USER_INFO_ID    = shardpref.getString("USER_INFO_ID", "");
            USER_INFO_AUTH  = shardpref.getString("USER_INFO_AUTH", "");
            SelectKind      = shardpref.getInt("SelectKind", 0);

            SetAllMemberList(SelectKind);

            setBtnEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding.getRoot();
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
        shardpref.remove("SelectKind");
    }

    public void SetAllMemberList(int kind) {
        // kind == 0 : 게시판 종류 // 1 : 카테고리 // 4 : 휴가선택
        if(kind == 0){
            setItem.add("자유게시판");
            setItem.add("사장님페이지");
            setItem.add("세금/노무");
        }else if(kind == 1){
            setItem.add("정보에요");
            setItem.add("화나요");
            setItem.add("억울해요");
            setItem.add("자랑해요");
            setItem.add("점주가 말한다");
            setItem.add("알바가 말한다");
            setItem.add("이런 사람 조심하세요");
            setItem.add("이런 상황 조심하세요");
        }else if(kind == 2){
            setItem.add("소상공인지원금");
            setItem.add("융자지원");
            setItem.add("교육/컨설팅");
            setItem.add("기타");
        }else if(kind == 3){
            setItem.add("전국");
            setItem.add("경기도");
            setItem.add("경상남도");
            setItem.add("경상북도");
            setItem.add("광주광역시");
            setItem.add("대구광역시");
            setItem.add("대전광역시");
            setItem.add("부산광역시");
            setItem.add("서울특별시");
            setItem.add("세종특별자치시");
            setItem.add("울산광역시");
            setItem.add("인천광역시");
            setItem.add("전라남도");
            setItem.add("전라북도");
            setItem.add("제주특별자치도");
            setItem.add("충청남도");
            setItem.add("충청북도");
        }else if(kind == 4){
            setItem.add("없음");
            setItem.add("자유");
            setItem.add("월차");
            setItem.add("연차");
        }

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

