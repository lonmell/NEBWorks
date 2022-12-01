package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.pop.TwoButtonPopActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class PlaceSearchAdapter extends RecyclerView.Adapter<PlaceSearchAdapter.ViewHolder> {

    private static final String TAG = "PlaceSearchAdapter";
    private ArrayList<PlaceListData.PlaceListData_list> mData = null;
    private ArrayList<PlaceListData.PlaceListData_list> SavemData = null;
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
    private PlaceSearchAdapter.OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(PlaceSearchAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public PlaceSearchAdapter(Context context, ArrayList<PlaceListData.PlaceListData_list> data,ArrayList<PlaceListData.PlaceListData_list> SavemData) {
        this.mData = data;
        this.mContext = context;
        this.SavemData = SavemData;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴


    //--옵션창 열기
    public interface OnClickOptionListener {
        void onClick(View v);
    }

    private PlaceSearchAdapter.OnClickOptionListener Olistener = null;

    public void setOnClickOption(PlaceSearchAdapter.OnClickOptionListener Olistener) {
        this.Olistener = Olistener;
    }

    @NonNull
    @Override
    public PlaceSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.store_search_item, parent, false);
        PlaceSearchAdapter.ViewHolder vh = new PlaceSearchAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlaceSearchAdapter.ViewHolder holder, int position) {
        PlaceListData.PlaceListData_list item = mData.get(position);

        shardpref = new PreferenceHelper(mContext);
        USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
        dlog.DlogContext(mContext);

        Glide.with(mContext).load(item.getImg_path())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.store_thumnail);
        holder.item_store_name.setText(item.getName());
        holder.item_store_address.setText(item.getAddress());

        holder.applicant_storegroup.setOnClickListener(v -> {
            shardpref.putString("guin_store_no", item.getId());
            Intent intent = new Intent(mContext, TwoButtonPopActivity.class);
            intent.putExtra("data", "해당 매장에 근무신청을 보냅니다.");
            intent.putExtra("flag", "그룹신청");
            intent.putExtra("take_user_id", item.getOwner_id());
            intent.putExtra("left_btn_txt", "취소");
            intent.putExtra("right_btn_txt", "전송");
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });
    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<PlaceListData.PlaceListData_list> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView store_thumnail;
        TextView item_store_name, item_store_address;
        CardView applicant_storegroup;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            store_thumnail = itemView.findViewById(R.id.store_thumnail);
            item_store_name = itemView.findViewById(R.id.item_store_name);
            item_store_address = itemView.findViewById(R.id.item_store_address);
            applicant_storegroup = itemView.findViewById(R.id.applicant_storegroup);

            shardpref = new PreferenceHelper(mContext);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PlaceListData.PlaceListData_list item = mData.get(pos);

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
