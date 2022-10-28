package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.pop.PlaceBottomNaviActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

/*
 * 2022-10-04 방창배 작성
 * */
public class WorkplaceListAdapter extends RecyclerView.Adapter<WorkplaceListAdapter.ViewHolder> {

    private static final String TAG = "WorkplaceListAdapter";
    private ArrayList<PlaceListData.PlaceListData_list> mData = null;
    Context mContext;
    FragmentManager fragmentManager;
    PreferenceHelper shardpref;
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public WorkplaceListAdapter(Context context, ArrayList<PlaceListData.PlaceListData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴


    //--옵션창 열기
    public interface OnClickOptionListener{
        void onClick(View v);
    }
    private OnClickOptionListener Olistener = null;
    public void setOnClickOption(OnClickOptionListener Olistener){this.Olistener = Olistener;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workplace_item, parent, false);
        WorkplaceListAdapter.ViewHolder vh = new WorkplaceListAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaceListData.PlaceListData_list item = mData.get(position);

        try{
            Glide.with(mContext).load(item.getImg_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.identificon)
                    .into(holder.store_thumnail);

            holder.title.setText(item.getName());
            holder.name.setText(item.getOwner_name());
            holder.date.setText(item.getStart_date());

            if(item.getOwner_id().equals(USER_INFO_ID)){
                //관리자일경우
                holder.list_img_area.setVisibility(View.VISIBLE);
            }else{
                holder.list_img_area.setVisibility(View.GONE);
            }

            holder.item_peoplecnt.setText(item.getTotal_cnt());
            holder.list_setting.setOnClickListener(v -> {
                shardpref.putString("place_id", item.getId());
                Intent intent = new Intent(mContext, PlaceBottomNaviActivity.class);
                intent.putExtra("left_btn_txt", "닫기");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }


    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView store_thumnail;
        TextView title,place_state_tv,name,date;
        TextView item_peoplecnt;
        CardView place_state,goto_place;
        RelativeLayout list_setting,list_img_area;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            store_thumnail = itemView.findViewById(R.id.store_thumnail);
            title = itemView.findViewById(R.id.title);
            place_state_tv = itemView.findViewById(R.id.place_state_tv);
            item_peoplecnt = itemView.findViewById(R.id.item_peoplecnt);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            place_state = itemView.findViewById(R.id.place_state);
            goto_place = itemView.findViewById(R.id.goto_place);
            list_setting = itemView.findViewById(R.id.list_setting);
            list_img_area= itemView.findViewById(R.id.list_img_area);

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PlaceListData.PlaceListData_list item = mData.get(pos);
                    Log.i("WorkplaceListAdapter", "pos : " + pos);

                    shardpref.putString("place_id", item.getId());
                    shardpref.putString("place_name", item.getName());
                    shardpref.putString("place_owner_id", item.getOwner_id());
                    shardpref.putString("place_owner_name", item.getOwner_name());
                    shardpref.putString("place_management_office", item.getManagement_office());
                    shardpref.putString("place_address", item.getAddress());
                    shardpref.putString("place_latitude", item.getLatitude());
                    shardpref.putString("place_longitude",item.getLongitude());
                    shardpref.putString("place_start_time", item.getStart_time());
                    shardpref.putString("place_end_time", item.getEnd_time());
                    shardpref.putString("place_img_path", item.getImg_path());
                    shardpref.putString("place_start_date", item.getStart_date());
                    shardpref.putString("place_created_at", item.getCreated_at());

                    if (mListener != null) {
                        mListener.onItemClick(view, pos);
                    }

//                    pm.EmployerStoreSetting(mContext);
                }
            });

        }
    }

    public void addItem(PlaceListData.PlaceListData_list workPlaceListData_list) {
        mData.add(workPlaceListData_list);
    }

}
