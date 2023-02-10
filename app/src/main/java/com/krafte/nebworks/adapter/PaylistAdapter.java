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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.CalendarPayData;
import com.krafte.nebworks.util.Dlog;

import java.util.ArrayList;

public class PaylistAdapter extends RecyclerView.Adapter<PaylistAdapter.ViewHolder> {
    private static final String TAG = "PaylistAdapter";
    private ArrayList<CalendarPayData.CalendarPayData_list> mData = null;
    Context mContext;
    Activity activity;
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public PaylistAdapter(Context context, ArrayList<CalendarPayData.CalendarPayData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public PaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.task_listitem2, parent, false);
        PaylistAdapter.ViewHolder vh = new PaylistAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PaylistAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CalendarPayData.CalendarPayData_list item = mData.get(position);

        try{
            holder.task_tv1.setText(mData.get(0).getUser_name());
            if(mData.get(0).getUser_id().equals("holiday")){
                holder.task_tv1.setTextColor(Color.parseColor("#FF687A"));
            }else{
                holder.task_tv1.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            dlog.i("mData getKind 1:" + mData.get(1).getUser_name());
            holder.task_tv2.setText(mData.get(1).getUser_name());
            if(mData.get(1).getUser_id().equals("holiday")){
                holder.task_tv2.setTextColor(Color.parseColor("#FF687A"));
            }else{
                holder.task_tv2.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            dlog.i("mData getKind 2:" + mData.get(2).getUser_name());
            holder.task_tv3.setText(mData.get(2).getUser_name());
            if(mData.get(2).getUser_id().equals("holiday")){
                holder.task_tv3.setTextColor(Color.parseColor("#FF687A"));
            }else{
                holder.task_tv3.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            dlog.i("mData getKind 3:" + mData.get(3).getUser_name());
            holder.task_tv4.setText(mData.get(3).getUser_name());
            if(mData.get(3).getUser_id().equals("holiday")){
                holder.task_tv4.setTextColor(Color.parseColor("#FF687A"));
            }else{
                holder.task_tv4.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            dlog.i("mData getKind 4:" + mData.get(4).getUser_name());
            holder.task_tv5.setText(mData.get(4).getUser_name());
            holder.task_tv5.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            if(mData.get(4).getUser_id().equals("holiday")){
                holder.task_tv5.setTextColor(Color.parseColor("#FF687A"));
            }else{
                holder.task_tv5.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        }catch (Exception e){
            dlog.i("Exception :" + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView task_tv1,task_tv2,task_tv3,task_tv4,task_tv5;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            task_tv1 = itemView.findViewById(R.id.task_tv1);
            task_tv2 = itemView.findViewById(R.id.task_tv2);
            task_tv3 = itemView.findViewById(R.id.task_tv3);
            task_tv4 = itemView.findViewById(R.id.task_tv4);
            task_tv5 = itemView.findViewById(R.id.task_tv5);

            dlog.DlogContext(mContext);

        }
    }

    public void addItem(CalendarPayData.CalendarPayData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
