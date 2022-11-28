package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
    private static final String TAG = "CommunityAdapter";
    private ArrayList<PlaceNotiData.PlaceNotiData_list> mData = null;
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

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private CommunityAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(CommunityAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public CommunityAdapter(Context context, ArrayList<PlaceNotiData.PlaceNotiData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public CommunityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.communityadapter_item, parent, false);
        CommunityAdapter.ViewHolder vh = new CommunityAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlaceNotiData.PlaceNotiData_list item = mData.get(position);

        try{
            holder.rank_tv.setText(String.valueOf(position));
            holder.title.setText(item.getTitle());
            holder.write_date.setText(item.getCreated_at());
            holder.name.setText(item.getWriter_name());
            holder.contents.setText(item.getContents());
            holder.categorytv.setText("#" + item.getCategory());
            holder.view_com.setText("조회수 " + item.getView_cnt() + " / 댓글 " + item.getComment_cnt());
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank_tv,title,name,write_date,contents,categorytv,view_com,like_cnt;
        RelativeLayout list_setting;


        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            rank_tv         = itemView.findViewById(R.id.rank_tv);
            title           = itemView.findViewById(R.id.title);
            name            = itemView.findViewById(R.id.name);
            write_date      = itemView.findViewById(R.id.write_date);
            contents        = itemView.findViewById(R.id.contents);
            categorytv      = itemView.findViewById(R.id.categorytv);
            view_com        = itemView.findViewById(R.id.view_com);
            like_cnt        = itemView.findViewById(R.id.like_cnt);
            list_setting    = itemView.findViewById(R.id.list_setting);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_id = shardpref.getString("place_id","");
            token = shardpref.getString("token", "");

            dlog.DlogContext(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PlaceNotiData.PlaceNotiData_list item = mData.get(pos);

                }
            });
        }
    }

    public void addItem(PlaceNotiData.PlaceNotiData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
