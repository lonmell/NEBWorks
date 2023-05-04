package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkGetallData;
import com.krafte.nebworks.util.Dlog;

import java.util.ArrayList;

public class CalendarDayAdaper2 extends RecyclerView.Adapter<CalendarDayAdaper2.ViewHolder> {
    private static final String TAG = "CalendarDayAdaper2";
    private ArrayList<WorkGetallData.WorkGetallData_list> mData = null;
    Context mContext;
    Activity activity;
    Dlog dlog = new Dlog();
    String[] items;
    StringBuilder stringBuilder;

    public CalendarDayAdaper2(Context context, ArrayList<WorkGetallData.WorkGetallData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public CalendarDayAdaper2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.task_listitem, parent, false);
        CalendarDayAdaper2.ViewHolder vh = new CalendarDayAdaper2.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CalendarDayAdaper2.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WorkGetallData.WorkGetallData_list item = mData.get(position);
//        dlog.i("------CalendarDayAdaper2------");
//        dlog.i("mData size :" + mData.size());
//        dlog.i("mData title :" + item.getTitle());
//        dlog.i("mData kind :" + item.getKind());
//        dlog.i("------CalendarDayAdaper2------");
        try{
            if(item.getKind().equals("holiday")){
                holder.task_tv1.setText(item.getTitle());
                holder.task_tv1.setTextColor(Color.parseColor("#FF687A"));
            }else{
                holder.task_tv1.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            if(!item.getTitle().equals("null")){
                items = item.getTitle().split(",");
                stringBuilder = new StringBuilder();
                for (String i : items) {
                    stringBuilder.append(i).append("\n");
                }
                String result = stringBuilder.toString();
                // 결과 출력
                holder.task_tv1.setText(result);
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
        LinearLayout item_total;
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            task_tv1 = itemView.findViewById(R.id.task_tv1);
//            task_tv2 = itemView.findViewById(R.id.task_tv2);
//            task_tv3 = itemView.findViewById(R.id.task_tv3);
//            task_tv4 = itemView.findViewById(R.id.task_tv4);
//            task_tv5 = itemView.findViewById(R.id.task_tv5);
//            item_total = itemView.findViewById(R.id.item_total);

            dlog.DlogContext(mContext);
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                }
            });
        }
    }

    public void addItem(WorkGetallData.WorkGetallData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
