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
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.StringData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class PagingAdapter extends RecyclerView.Adapter<PagingAdapter.ViewHolder> {
    private static final String TAG = "PagingAdapter";
    private ArrayList<StringData.StringData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    public static Activity activity;

    String place_id = "";
    String USER_INFO_ID = "";
    Dlog dlog = new Dlog();

    String before_item = "1";

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private PagingAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(PagingAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public PagingAdapter(Context context, ArrayList<StringData.StringData_list> data,String before_item) {
        this.mData = data;
        this.mContext = context;
        this.before_item = before_item;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public PagingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.paging_item, parent, false);
        PagingAdapter.ViewHolder vh = new PagingAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PagingAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StringData.StringData_list item = mData.get(position);
        try{
            holder.item_name.setText(item.getItem());

            if(before_item.equals(item.getItem())){
                holder.item_area2.setCardBackgroundColor(Color.parseColor("#6395EC"));
            }else{
                holder.item_area2.setCardBackgroundColor(Color.parseColor("#dcdcdc"));
            }
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView item_area2,item_area1;
        TextView item_name;
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            item_name   = itemView.findViewById(R.id.item_name);
            item_area1  = itemView.findViewById(R.id.item_area1);
            item_area2  = itemView.findViewById(R.id.item_area2);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_id = shardpref.getString("place_id","");

            dlog.DlogContext(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    StringData.StringData_list item = mData.get(pos);
                    before_item = item.getItem();
                    if (mListener != null) {
                        mListener.onItemClick(view,pos);
                    }
                }
            });
        }
    }

    public void addItem(StringData.StringData_list data) {
        mData.add(data);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<StringData.StringData_list> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
