package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.MainTaskData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class MainTaskLAdapter extends RecyclerView.Adapter<MainTaskLAdapter.ViewHolder> {
    private static final String TAG = "MainTaskLAdapter";
    private ArrayList<MainTaskData.MainTaskData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    public static Activity activity;

    String place_id = "";
    String USER_INFO_ID = "";
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private MainTaskLAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(MainTaskLAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public MainTaskLAdapter(Context context, ArrayList<MainTaskData.MainTaskData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public MainTaskLAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_task_item, parent, false);
        MainTaskLAdapter.ViewHolder vh = new MainTaskLAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MainTaskLAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MainTaskData.MainTaskData_list item = mData.get(position);
        try{
            dlog.i("mData item : " + mData.get(position));
            holder.title.setText(item.getTitle());
            String endhour = "";
            String endmin = "";
            if(!item.getEnd_hour().isEmpty()){
                endhour = item.getEnd_hour() + "시";
            }
            if(!item.getEnd_min().isEmpty()){
                endmin = item.getEnd_min() + "분";
            }
            holder.date.setText(item.getEnd_date() + " | " + endhour + endmin);

            if(position == 0){
                holder.itemline.setVisibility(View.GONE);
            }else {
                holder.itemline.setVisibility(View.VISIBLE);
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

        TextView title,date;
        LinearLayout itemline;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            title         = itemView.findViewById(R.id.title);
            date          = itemView.findViewById(R.id.date);
            itemline      = itemView.findViewById(R.id.itemline);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_id = shardpref.getString("place_id","");

            dlog.DlogContext(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener.onItemClick(view,pos);
                    }
                }
            });
        }
    }

    public void addItem(MainTaskData.MainTaskData_list data) {
        mData.add(data);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<MainTaskData.MainTaskData_list> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
