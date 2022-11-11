package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.pop.CommunityOptionActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

/*
 * 2022-10-07 방창배 작성
 * */
public class PlaceNotiAdapter extends RecyclerView.Adapter<PlaceNotiAdapter.ViewHolder> {
    private static final String TAG = "PlaceNotiAdapter";
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

    private PlaceNotiAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(PlaceNotiAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public PlaceNotiAdapter(Context context, ArrayList<PlaceNotiData.PlaceNotiData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public PlaceNotiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.storenoti_listitem, parent, false);
        PlaceNotiAdapter.ViewHolder vh = new PlaceNotiAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlaceNotiAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlaceNotiData.PlaceNotiData_list item = mData.get(position);

        try{
            //이벤트의 경우 종료일을 지정하지 않으면 삭제할때까지 계속 보여짐
            if(!item.getOpen_date().isEmpty()){
                holder.title.setText("[이벤트] " + item.getTitle() );
            }else{
                holder.title.setText(item.getTitle());
            }

            String year = item.getCreated_at().substring(0,4);
            String month = item.getCreated_at().substring(5,7);
            String day = item.getCreated_at().substring(8,10);

            holder.date.setText(year + "년 " + month + "월 " + day + "일");
            holder.writer_jikgup.setText(item.getJikgup());

            holder.list_edit_area.setOnClickListener(v -> {
                dlog.i("list_edit_area Click!!");
                shardpref.putString("edit_feed_id",item.getId());
                Intent intent = new Intent(mContext, CommunityOptionActivity.class);
                intent.putExtra("state", "EditFeed");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });

            holder.area_box.setOnClickListener(v -> {
                shardpref.putString("feed_id",item.getId());
                pm.FeedDetailGo(mContext);
            });
//            if (loadlist == 0) {
//                //--아이템에 나타나기 애니메이션 줌
//                holder.item_total.setTranslationY(150);
//                holder.item_total.setAlpha(0.f);
//                holder.item_total.animate().translationY(0).alpha(1.f)
//                        .setStartDelay(delayEnterAnimation ? 20 * (position) : 0) // position 마다 시간차를 조금 주고..
//                        .setInterpolator(new DecelerateInterpolator(2.f))
//                        .setDuration(300)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                animationsLocked = true; // 진입시에만 animation 하도록 하기 위함
//                            }
//                        });
//                loadlist++;
//            }
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, date, writer_jikgup;
        TextView comment_cnt;
        RelativeLayout list_edit_area,area_box;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            title           = itemView.findViewById(R.id.title);
            date            = itemView.findViewById(R.id.date);
            writer_jikgup   = itemView.findViewById(R.id.writer_jikgup);
            list_edit_area  = itemView.findViewById(R.id.list_edit_area);
            area_box        = itemView.findViewById(R.id.area_box);

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
