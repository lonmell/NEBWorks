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
import com.krafte.nebworks.data.SecondTapCommunityData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class OwnerCommunityAdapter  extends RecyclerView.Adapter<OwnerCommunityAdapter.ViewHolder> {
    private static final String TAG = "OwnerCommunityAdapter";
    private ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    public static Activity activity;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    int TouchItemPos = -1;
    int loadlist = 0;
    String TodayTxt = "";
    String topic = "";

    String place_id = "";
    String USER_INFO_ID = "";
    String token = "";
    Dlog dlog = new Dlog();
    int kind = 0;
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OwnerCommunityAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(OwnerCommunityAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public OwnerCommunityAdapter(Context context, ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> data, int kind) {
        this.mData = data;
        this.mContext = context;
        this.kind = kind;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public OwnerCommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.owner_community_item, parent, false);
        OwnerCommunityAdapter.ViewHolder vh = new OwnerCommunityAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OwnerCommunityAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SecondTapCommunityData.SecondTapCommunityData_list item = mData.get(position);

        try{
            holder.cate.setText(item.getCategory());
            holder.title.setText(item.getTitle());
            holder.location.setText(item.getLocation());
            holder.gigan.setText(item.getGigan());
            holder.over_date.setText("");
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cate, title, location, gigan, over_date;

        ViewHolder(View itemView) {
            super(itemView);
            cate       = itemView.findViewById(R.id.cate);
            title      = itemView.findViewById(R.id.title);
            location   = itemView.findViewById(R.id.location);
            gigan      = itemView.findViewById(R.id.gigan);
            over_date  = itemView.findViewById(R.id.over_date);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_id = shardpref.getString("place_id","");
            token = shardpref.getString("token", "");

            dlog.DlogContext(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    SecondTapCommunityData.SecondTapCommunityData_list item = mData.get(pos);
                    String oc_link = "https://www.bizinfo.go.kr/web/lay1/bbs/S1T122C128/AS/74/" + item.getLink();
                    shardpref.putString("oc_link",oc_link);
                    shardpref.putString("oc_cate",item.getCategory());
                    shardpref.putString("oc_title",item.getTitle());
                    dlog.i("Item Click link : " + oc_link);
                    pm.OwnerFeedDetail(mContext);
                }
            });
        }
    }

    public void addItem(SecondTapCommunityData.SecondTapCommunityData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<SecondTapCommunityData.SecondTapCommunityData_list> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

}
