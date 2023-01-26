package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class CateAdapter extends RecyclerView.Adapter<CateAdapter.ViewHolder> {
    private static final String TAG = "MainCateAdapter";
    private ArrayList<StringData.StringData_list> mData = null;
    public static Activity activity;

    Context mContext;
    PreferenceHelper shardpref;

    Dlog dlog = new Dlog();

    int before_pos = 0;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public CateAdapter(Context context, ArrayList<StringData.StringData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public CateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cate_item, parent, false);
        CateAdapter.ViewHolder vh = new CateAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CateAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StringData.StringData_list item = mData.get(position);
        try {
            dlog.i("mData item : " + mData.get(position));
            if(position == before_pos){
                holder.cate_back.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_200));
                holder.cate_back1.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_200));
                holder.cate.setTextColor(ContextCompat.getColor(mContext, R.color.new_blue));
            }else{
                holder.cate_back.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
                holder.cate_back1.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
                holder.cate.setTextColor(ContextCompat.getColor(mContext, R.color.gray_500));
            }

            holder.cate.setText(item.getItem());

            holder.cate_back.setOnClickListener(v -> {
                before_pos = position;
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
            });
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cate_back, cate_back1;
        TextView cate;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            cate_back   = itemView.findViewById(R.id.cate_back);
            cate_back1  = itemView.findViewById(R.id.cate_back1);
            cate        = itemView.findViewById(R.id.cate);

            shardpref = new PreferenceHelper(mContext);

            dlog.DlogContext(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                }
            });
        }
    }

    public void addItem(StringData.StringData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}