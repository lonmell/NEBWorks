package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private static final String TAG = "WifiAdapter";
    private List<String> mData = null;
    Context mContext;
    PreferenceHelper shardpref;

    //Shared
    Dlog dlog = new Dlog();
    int lastPos = 0;

    public WifiAdapter(Context context, List<String> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WifiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.wifi_list_item, parent, false);
        WifiAdapter.ViewHolder vh = new WifiAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"SetTextI18n", "LongLogTag"})
    @Override
    public void onBindViewHolder(@NonNull WifiAdapter.ViewHolder holder, int position) {
        String item = mData.get(position);

        try {
            holder.wifi_name.setText(item);
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }
        if(lastPos != position){
            holder.wifi_icon.setBackgroundResource(R.drawable.wifi);
            holder.wifi_name.setTextColor(R.color.black);
            holder.wifi_select_icon.setVisibility(View.INVISIBLE);
        }
        holder.wifi_name.setOnClickListener(v -> {
            dlog.i("lastPos :" + lastPos);
            dlog.i("position :" + position);
            holder.wifi_icon.setBackgroundResource(R.drawable.wifi_on);
            holder.wifi_name.setTextColor(R.color.blue);
            holder.wifi_select_icon.setVisibility(View.VISIBLE);
            lastPos = position;
            if (mListener != null) {
                mListener.onItemClick(v, position);
            }

        });
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView wifi_name;
        ImageView wifi_icon,wifi_select_icon;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            wifi_name = itemView.findViewById(R.id.wifi_name);
            wifi_icon = itemView.findViewById(R.id.wifi_icon);
            wifi_select_icon = itemView.findViewById(R.id.wifi_select_icon);

            shardpref = new PreferenceHelper(mContext);
            dlog.DlogContext(mContext);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                    // 리스너 객체의 메서드 호출.
//                    String item = mData.get(pos);
//                    dlog.i("Click wifi name : " + item);
//                    if (mListener != null) {
//                        mListener.onItemClick(v, pos);
//                    }
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

    public interface OnItemClickListener {
        void onItemClick(View v, int position);

    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
