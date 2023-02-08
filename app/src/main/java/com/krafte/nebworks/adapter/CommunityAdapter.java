package com.krafte.nebworks.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceNotiData;
import com.krafte.nebworks.pop.CommunityOptionActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    int loadlist = 0;

    String place_id = "";
    String USER_INFO_ID = "";
    String token = "";
    Dlog dlog = new Dlog();
    int kind = 0;
    Boolean isMain = false;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private CommunityAdapter.OnItemClickListener mListener = null;

    public void isMain(Boolean isMain) {
        this.isMain = isMain;
    }

    public void setOnItemClickListener(CommunityAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public CommunityAdapter(Context context, ArrayList<PlaceNotiData.PlaceNotiData_list> data, int kind) {
        this.mData = data;
        this.mContext = context;
        this.kind = kind;
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
        try{
            PlaceNotiData.PlaceNotiData_list item = mData.get(position);

            if(kind == 0){//인기글
                holder.rank_area.setVisibility(View.VISIBLE);
                holder.profile_area.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);
                holder.write_date.setVisibility(View.VISIBLE);
                holder.category_box.setVisibility(View.VISIBLE);
                holder.line01.setVisibility(View.GONE);
                holder.view_com.setVisibility(View.GONE);
                holder.like_area.setVisibility(View.GONE);
            }else if(kind == 1){//전체게시글
                holder.rank_area.setVisibility(View.GONE);
                holder.profile_area.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);
                holder.write_date.setVisibility(View.VISIBLE);
                holder.category_box.setVisibility(View.VISIBLE);
                holder.line01.setVisibility(View.VISIBLE);
            }

            holder.rank_tv.setText(String.valueOf(position+1));

            Resources res = mContext.getResources();
            List<String> forbiList = Arrays.asList(res.getStringArray(R.array.forbidden_word));
//            List<String> forbiList = new ArrayList<>(Arrays.asList(Arrays.toString(res.getStringArray(R.array.forbidden_word)).replace("[","").replace("]","").split(",")));
            dlog.i("String xml Forbidden Word : " + forbiList);
            String title = "";
            String content = "";
            for(int i = 0; i < forbiList.size(); i++){
                if(item.getTitle().contains(forbiList.get(i))){
                    title = item.getTitle().replace(forbiList.get(i)," ○○○ ");
                }
                if(item.getContents().contains(forbiList.get(i))){
                    content = item.getContents().replace(forbiList.get(i)," ○○○ ");
                }
            }
            holder.title.setText(title.equals("")?item.getTitle():title);

            holder.write_date.setText(item.getCreated_at().equals("null")?"":item.getCreated_at());

            Glide.with(mContext).load(item.getWriter_img_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.certi02)
                    .skipMemoryCache(true)
                    .into(holder.profile_img);
            holder.name.setText(item.getWriter_name().equals("null")?"":item.getWriter_name());

            holder.contents.setText(content.equals("")?item.getContents():content);
            if(!item.getCategory().isEmpty()){
                holder.categorytv.setText("#" + item.getCategory().replace("#", ""));
            }else{
                holder.category_box.setVisibility(View.GONE);
            }

            holder.view_com.setText("조회수 " + item.getView_cnt() + " / 댓글 " + item.getComment_cnt());

            holder.like_cnt.setText(item.getLike_cnt());

            if(item.getMylikeyn().equals("0")){
                holder.like_icon.setBackgroundResource(R.drawable.ic_like_off);
            }else{
                holder.like_icon.setBackgroundResource(R.drawable.ic_like_on);
            }

            if(USER_INFO_ID.equals(item.getWriter_id())){
                holder.list_setting.setVisibility(View.VISIBLE);
                holder.list_setting.setClickable(true);
                holder.list_setting.setEnabled(true);
            }else{
                holder.list_setting.setVisibility(View.INVISIBLE);
                holder.list_setting.setClickable(false);
                holder.list_setting.setEnabled(false);
            }
            holder.list_setting.setOnClickListener(v -> {
                shardpref.putString("feed_id",item.getId());
                shardpref.putString("place_id",item.getPlace_id());
                shardpref.putString("title",item.getTitle());
                shardpref.putString("contents",item.getContents());
                shardpref.putString("writer_id",item.getWriter_id());
                shardpref.putString("writer_name",item.getWriter_name());
                shardpref.putString("writer_img_path",item.getWriter_img_path());
                shardpref.putString("feed_img_path",item.getFeed_img_path());
                shardpref.putString("jikgup",item.getJikgup());
                shardpref.putString("view_cnt",item.getView_cnt());
                shardpref.putString("comment_cnt",item.getComment_cnt());
                shardpref.putString("category",item.getCategory());
                shardpref.putString("state","EditCommunity");
                Intent intent = new Intent(mContext, CommunityOptionActivity.class);
                intent.putExtra("state", "EditCommunity");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });

            if (loadlist == 0) {
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
        if (mData.size() < 3) {
            return mData.size();
        } else {
            return isMain? 3 : mData.size();
        }
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank_tv,title,name,write_date,contents,categorytv,view_com,like_cnt;
        RelativeLayout list_setting,item_total;

        LinearLayout rank_area;
        CardView profile_area;
        LinearLayout category_box, line01, like_area;
        ImageView profile_img, like_icon;

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
            rank_area       = itemView.findViewById(R.id.rank_area);
            profile_area    = itemView.findViewById(R.id.profile_area);
            category_box    = itemView.findViewById(R.id.category_box);
            profile_img     = itemView.findViewById(R.id.profile_img);
            like_icon       = itemView.findViewById(R.id.like_icon);
            item_total      = itemView.findViewById(R.id.item_total);
            line01          = itemView.findViewById(R.id.line01);
            like_area       = itemView.findViewById(R.id.like_area);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID","");
            place_id = shardpref.getString("place_id","");
            token = shardpref.getString("token", "");

            dlog.DlogContext(mContext);
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PlaceNotiData.PlaceNotiData_list item = mData.get(pos);
                    if (mListener != null) {
                        mListener.onItemClick(view,pos);
                    }
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
