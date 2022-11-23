package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.UsersData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class AdaperInMemberAdapter extends RecyclerView.Adapter<AdaperInMemberAdapter.ViewHolder> {
    private static final String TAG = "AdaperInMemberAdapter";
    private ArrayList<UsersData.UsersData_list> mData = null;
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

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public AdaperInMemberAdapter(Context context, ArrayList<UsersData.UsersData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public AdaperInMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_member_item, parent, false);
        AdaperInMemberAdapter.ViewHolder vh = new AdaperInMemberAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdaperInMemberAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            UsersData.UsersData_list item = mData.get(position);
            if (mData.size() == 1){
                Glide.with(mContext).load(mData.get(0).getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.workimg1);
                holder.workimg_url02.setVisibility(View.GONE);
                holder.workimg_url03.setVisibility(View.GONE);
                holder.workimg_url04.setVisibility(View.GONE);
            }else if (mData.size() == 2){
                Glide.with(mContext).load(mData.get(0).getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.workimg1);
                Glide.with(mContext).load(mData.get(1).getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.workimg2);
                holder.workimg_url03.setVisibility(View.GONE);
                holder.workimg_url04.setVisibility(View.GONE);
            }else if (mData.size() == 3){
                Glide.with(mContext).load(mData.get(0).getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.workimg1);
                Glide.with(mContext).load(mData.get(1).getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.workimg2);
                Glide.with(mContext).load(mData.get(2).getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.workimg3);
                holder.workimg_url04.setVisibility(View.GONE);
            }else if (mData.size() > 3){
                int Cnt = mData.size() - 3;
                holder.other_cnt.setText("+" + Cnt);
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
        CardView workimg_url01,workimg_url02,workimg_url03,workimg_url04;
        ImageView workimg1,workimg2,workimg3;
        TextView other_cnt;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            workimg_url01 = itemView.findViewById(R.id.workimg_url01);
            workimg_url02 = itemView.findViewById(R.id.workimg_url02);
            workimg_url03 = itemView.findViewById(R.id.workimg_url03);
            workimg_url04 = itemView.findViewById(R.id.workimg_url04);

            workimg1      = itemView.findViewById(R.id.workimg1);
            workimg2      = itemView.findViewById(R.id.workimg2);
            workimg3      = itemView.findViewById(R.id.workimg3);
            other_cnt     = itemView.findViewById(R.id.other_cnt);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_id = shardpref.getString("place_id","");
            token = shardpref.getString("token", "");

            dlog.DlogContext(mContext);

        }
    }

    public void addItem(UsersData.UsersData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    String click_action = "";
/* -- fcm완료되면 Retrofit으로 작성
    @SuppressLint("LongLogTag")
    private void FcmTestFunctionCall(String token_get,String title, String store_name) {
        @SuppressLint("SetTextI18n")
        Thread th = new Thread(() -> {
            click_action = "EmployeeNotifyListActivity";
            String SendTitle = "(재발송)새로운 매장 공지가 작성되었습니다.";
            dbConnection.FcmTestFunctionResend(topic, store_name, title, token_get, click_action, "5", topic);
            activity.runOnUiThread(() -> {

            });
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/

}
