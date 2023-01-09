package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.SearchAddressDataDetail;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class JusoAdapter extends RecyclerView.Adapter<JusoAdapter.ViewHolder> {
    private static final String TAG = "JusoAdapter";
    private ArrayList<SearchAddressDataDetail.SearchAddressDataDetaillist> mData = null;
    Context mContext;
    int user_kind;
    String TaskKind = "";
    int setKind;
    PreferenceHelper shardpref;
    Drawable icon_off;
    Drawable icon_on;

    //Shared Data
    int categoryItem = 0;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private JusoAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(JusoAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public JusoAdapter(Context context, ArrayList<SearchAddressDataDetail.SearchAddressDataDetaillist> data) {
        this.mData = data;
        mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public JusoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.juso_item, parent, false);
        JusoAdapter.ViewHolder vh = new JusoAdapter.ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull JusoAdapter.ViewHolder holder, int position) {
        SearchAddressDataDetail.SearchAddressDataDetaillist item = mData.get(position);
        holder.roadAddrPart1tv.setText(item.getRoadAddrPart1());
        holder.roadAddrPart2tv.setText(item.getRoadAddrPart2());
        holder.zipNotv.setText(item.getZipNo());

    } // getItemCount : 전체 데이터의 개수를

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roadAddrPart1tv;
        TextView roadAddrPart2tv;
        TextView zipNotv;

        @SuppressLint("UseCompatLoadingForDrawables")
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            roadAddrPart1tv = itemView.findViewById(R.id.roadAddrPart1tv);
            roadAddrPart2tv = itemView.findViewById(R.id.roadAddrPart2tv);
            zipNotv = itemView.findViewById(R.id.zipNotv);

            shardpref = new PreferenceHelper(mContext);
            categoryItem = shardpref.getInt("categoryItem",0);


            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    SearchAddressDataDetail.SearchAddressDataDetaillist item = mData.get(pos);
                    if (mListener != null) {
                        mListener.onItemClick(view, pos) ;
                    }
                }
            });

        }
    }

    public void addItem(SearchAddressDataDetail.SearchAddressDataDetaillist data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
