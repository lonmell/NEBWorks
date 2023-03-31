package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.CalendarSetData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class CalendarDayAdaper extends RecyclerView.Adapter<CalendarDayAdaper.ViewHolder> {
    private static final String TAG = "CalendarDayAdaper";
    private List<String> mData = null;
    private ArrayList<CalendarSetData.CalendarSetData_list> mList;
    public static Activity activity;

    Context mContext;
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public CalendarDayAdaper(Context context, List<String> data, ArrayList<CalendarSetData.CalendarSetData_list> mList) {
        this.mData = data;
        this.mContext = context;
        this.mList = mList;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public CalendarDayAdaper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_calendar, parent, false);
        CalendarDayAdaper.ViewHolder vh = new CalendarDayAdaper.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CalendarDayAdaper.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String item = mData.get(position);
        try {
            holder.tv_date.setText(item);
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            tv_date   = itemView.findViewById(R.id.tv_date);

            shardpref = new PreferenceHelper(mContext);

            dlog.DlogContext(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                }
            });
        }
    }

    public void addItem(String data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
