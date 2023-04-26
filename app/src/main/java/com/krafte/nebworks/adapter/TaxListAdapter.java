package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.krafte.nebworks.data.TaxMemberData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;

public class TaxListAdapter extends RecyclerView.Adapter<TaxListAdapter.ViewHolder> {
    private static final String TAG = "TaxListAdapter";
    private ArrayList<TaxMemberData.TaxMemberData_list> mData = null;
    Context mContext;
    PageMoveClass pm = new PageMoveClass();
    PreferenceHelper shardpref;
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();
    Activity activity;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    int loadlist = 0;
    Dlog dlog = new Dlog();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private TaxListAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(TaxListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public TaxListAdapter(Context context, ArrayList<TaxMemberData.TaxMemberData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public TaxListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.taxlist_item, parent, false);
        TaxListAdapter.ViewHolder vh = new TaxListAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaxListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaxMemberData.TaxMemberData_list item = mData.get(position);

        try {
            holder.profile_tv.setVisibility(View.GONE);
            holder.profile_tv2.setVisibility(View.VISIBLE);

            holder.name.setText(item.getName());
            holder.address.setText(item.getAddress());
            holder.phone.setText(item.getContact_num());

            Glide.with(mContext).load(item.getImg_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.certi02)
                    .skipMemoryCache(true)
                    .into(holder.profile_tv2);

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
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, phone, profile_tv;
        CardView item_total, profile_img;
        ImageView profile_tv2;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            name        = itemView.findViewById(R.id.name);
            address     = itemView.findViewById(R.id.address);
            phone       = itemView.findViewById(R.id.phone);
            item_total  = itemView.findViewById(R.id.item_total);
            profile_img = itemView.findViewById(R.id.profile_img);
            profile_tv  = itemView.findViewById(R.id.profile_tv);
            profile_tv2  = itemView.findViewById(R.id.profile_tv2);

            shardpref = new PreferenceHelper(mContext);
            dlog.DlogContext(mContext);
            dlog.i("mData : " + mData.size());

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TaxMemberData.TaxMemberData_list item = mData.get(pos);
                    // 전화 걸기
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+item.getContact_num()  ));
                    mContext.startActivity(mIntent);
                }
            });
        }
    }

    public void addItem(TaxMemberData.TaxMemberData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
