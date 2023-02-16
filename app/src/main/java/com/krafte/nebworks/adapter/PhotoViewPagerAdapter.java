package com.krafte.nebworks.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;

import java.util.List;

public class PhotoViewPagerAdapter extends RecyclerView.Adapter<PhotoViewPagerAdapter.ViewHolder>{

    private List<String> Items;
    private Context mContext = null ;

    public PhotoViewPagerAdapter(List<String> Items, Context context) {
        this.Items = Items;
        mContext = context;
    }

    @NonNull
    @Override
    public PhotoViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photopop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewPagerAdapter.ViewHolder holder, int position) {
        Log.i("PhotoViewPagerAdapter","Items.get(position) : " + Items.get(position));
        Glide.with(mContext)
                .load(Items.get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(holder.pagerimg);

    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pagerimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pagerimg = itemView.findViewById(R.id.view_pager_img);

        }
    }
}
