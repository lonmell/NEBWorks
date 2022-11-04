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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.krafte.nebworks.R;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

public class StoreListBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "StoreListBottomSheet";
    Context mContext;
    private View view;

    Activity activity;
    FragmentManager fragmentManager;

    //XML ID
    LinearLayout close_btn;
    LinearLayout list_settingitem01,list_settingitem02,list_settingitem03;

    // shared 저장값
    PreferenceHelper shardpref;

    //Other
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_storelist_menu, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        try{
            mContext = inflater.getContext();
            dlog.DlogContext(mContext);
            fragmentManager = getParentFragmentManager();

            shardpref = new PreferenceHelper(mContext);

            close_btn = view.findViewById(R.id.close_btn);

            list_settingitem01 = view.findViewById(R.id.list_settingitem01);
            list_settingitem02 = view.findViewById(R.id.list_settingitem02);
            list_settingitem03 = view.findViewById(R.id.list_settingitem03);

            setBtnEvent();
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    //list_settingitem01
    public interface OnClickListener {
        void onClick(View v) ;
    }
    private OnClickListener mListener01 = null ;
    public void setOnClickListener01(OnClickListener listener) {
        this.mListener01 = listener ;
    }

    //list_settingitem02
    private OnClickListener mListener02 = null ;
    public void setOnClickListener02(OnClickListener listener) {
        this.mListener02 = listener ;
    }

    //list_settingitem03
    private OnClickListener mListener03 = null ;
    public void setOnClickListener03(OnClickListener listener) {
        this.mListener03 = listener ;
    }

    private void setBtnEvent(){
        list_settingitem01.setOnClickListener(v -> {
            if (mListener01 != null) {
                mListener01.onClick(v) ;
            }
            dismiss();
        });
        list_settingitem02.setOnClickListener(v -> {
            if (mListener02 != null) {
                mListener02.onClick(v) ;
            }
            dismiss();
        });
        list_settingitem03.setOnClickListener(v -> {
            if (mListener03 != null) {
                mListener03.onClick(v) ;
            }
            dismiss();
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
