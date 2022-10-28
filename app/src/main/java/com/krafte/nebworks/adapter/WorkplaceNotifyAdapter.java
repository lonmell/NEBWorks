package com.krafte.nebworks.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkPlaceEmloyeeNotifyData;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class WorkplaceNotifyAdapter extends RecyclerView.Adapter<WorkplaceNotifyAdapter.ViewHolder> {
    private ArrayList<WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    PageMoveClass pm = new PageMoveClass();

    //shared Data
    String USER_INFO_AUTH;
    String USER_INFO_ID;
    String place_name = "";
    String place_management_office = "";

    //Other
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    Dlog dlog = new Dlog();
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    int loadlist = 0;

    public WorkplaceNotifyAdapter(Context context, ArrayList<WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkplaceNotifyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workplace_notify_item, parent, false);
        WorkplaceNotifyAdapter.ViewHolder vh = new WorkplaceNotifyAdapter.ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @Override
    public void onBindViewHolder(@NonNull WorkplaceNotifyAdapter.ViewHolder holder, int position) {
        WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list item = mData.get(position);

        try{
            holder.item_notify_name.setText(place_management_office);
            holder.item_notify_contents.setText(item.getTitle());
            holder.item_notify_date.setText(item.getPush_date());


            // 0:출퇴근, 1:할일, 2:결재, 3:공지사항
            String noti_mothod = "";
            switch(item.getKind()){
                case "0" :
                    noti_mothod = "출퇴근";
                    break;
                case "1" :
                    noti_mothod = "할일";
                    break;
                case "2" :
                    noti_mothod = "결재";
                    break;
                case "3" :
                    noti_mothod = "공지사항";
                    break;
            }

            holder.item_notify_readyn.setText(noti_mothod);
            if(loadlist == 0){
                //--아이템에 나타나기 애니메이션 줌
                holder.item_total.setTranslationY(150);
                holder.item_total.setAlpha(0.f);
                holder.item_total.animate().translationY(0).alpha(1.f)
                        .setStartDelay(delayEnterAnimation ? 20 * (position) : 0) // position 마다 시간차를 조금 주고..
                        .setInterpolator(new DecelerateInterpolator(2.f))
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animationsLocked = true; // 진입시에만 animation 하도록 하기 위함
                            }
                        });
                loadlist++;
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

        TextView item_notify_name;
        TextView item_notify_contents;
        TextView item_notify_date;
        TextView item_notify_readyn;
        RelativeLayout item_total;

        @SuppressLint("LongLogTag")
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            item_notify_name = itemView.findViewById(R.id.item_notify_name);
            item_notify_contents = itemView.findViewById(R.id.item_notify_contents);
            item_notify_date = itemView.findViewById(R.id.item_notify_date);
            item_notify_readyn = itemView.findViewById(R.id.item_notify_readyn);
            item_total = itemView.findViewById(R.id.item_total);
            dlog.DlogContext(mContext);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH","");
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_name = shardpref.getString("place_name","");
            place_management_office = shardpref.getString("place_management_office","");
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    if (mListener != null) {
                        mListener.onItemClick(view, pos);
                    }
                }

            });
        }
    }

    public void addItem(WorkPlaceEmloyeeNotifyData.WorkPlaceEmloyeeNotifyData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    private OnItemClickListener mListener = null ;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


}
