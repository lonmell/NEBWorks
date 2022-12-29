package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class PlaceNameListAdapter extends RecyclerView.Adapter<PlaceNameListAdapter.ViewHolder> {

    private static final String TAG = "PlaceNameListAdapter";
    private ArrayList<PlaceListData.PlaceListData_list> mData = null;
    Context mContext;
    FragmentManager fragmentManager;
    PreferenceHelper shardpref;
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    Activity activity;
    RetrofitConnect rc = new RetrofitConnect();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private PlaceNameListAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(PlaceNameListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public PlaceNameListAdapter(Context context, ArrayList<PlaceListData.PlaceListData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴


    //--옵션창 열기
    public interface OnClickOptionListener {
        void onClick(View v);
    }

    private PlaceNameListAdapter.OnClickOptionListener Olistener = null;

    public void setOnClickOption(PlaceNameListAdapter.OnClickOptionListener Olistener) {
        this.Olistener = Olistener;
    }

    @NonNull
    @Override
    public PlaceNameListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_string_item, parent, false);
        PlaceNameListAdapter.ViewHolder vh = new PlaceNameListAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlaceNameListAdapter.ViewHolder holder, int position) {
        PlaceListData.PlaceListData_list item = mData.get(position);

        try {
            holder.item_name.setText(item.getName());
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_name;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            item_name = itemView.findViewById(R.id.item_name);

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PlaceListData.PlaceListData_list item = mData.get(pos);
                    Log.i("PlaceNameListAdapter", "pos : " + pos);
                    shardpref.putString("place_id", item.getId());
                    shardpref.putString("place_owner_id", item.getOwner_id());
                    if (mListener != null) {
                        mListener.onItemClick(view, pos);
                    }
                }
            });

        }
    }

    public void addItem(PlaceListData.PlaceListData_list workPlaceListData_list) {
        mData.add(workPlaceListData_list);
    }

}
