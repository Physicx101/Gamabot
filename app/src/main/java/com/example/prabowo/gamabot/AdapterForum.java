package com.example.prabowo.gamabot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Alessandro on 04/03/2017.
 */

public class AdapterForum extends RecyclerView.Adapter<AdapterForum.MyViewHolder> {

    private List<ForumModel> mList;
    //private OnClickItemFeed onClickItemFeed;
    private Context context;
    ForumModel feed;



    public AdapterForum(List<ForumModel> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        feed = mList.get(position);
        holder.root.setTag(feed);
        holder.setTvName(feed.getName());
        holder.setTvContent(feed.getText());
        //holder.setTvTime( feed.getTime() );
        holder.setIvAvatar(feed.getPhotoAvatar());
        holder.setIvContent(feed.getPhotoFeed());

        holder.changeLikeImg(feed.getIdFeed());


    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivAvatar, ivLike;
        private CustomImageView ivContent;
        private TextView tvName, tvTime, tvContent, tvLike;
        View root;

        public MyViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            root.setClickable(true);
            root.setOnClickListener(this);

            itemView.setOnClickListener(this);

            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvLike = (TextView) itemView.findViewById(R.id.tv_like);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivContent = (CustomImageView) itemView.findViewById(R.id.iv_feed);
            ivLike = (ImageView) itemView.findViewById(R.id.iv_like);
            ivLike.setOnClickListener(this);

        }

        public void setIvAvatar(String url) {
            if (ivAvatar == null) return;
            if (url.equals("default_uri")) {
                Glide.with(ivAvatar.getContext())
                        .load(R.mipmap.ic_launcher)
                        .centerCrop()
                        .into(ivAvatar);
            } else {
                Glide.with(ivAvatar.getContext())
                        .load(url)
                        .centerCrop()
                        .into(ivAvatar);
            }
        }

        public void setIvContent(String url) {
            if (ivContent == null) return;

            Glide.with(ivContent.getContext()).load(url).centerCrop().into(ivContent);

        }

        public void setTvName(String text) {
            if (tvName == null) return;
            tvName.setText(text);
        }

        public void setTvTime(String text) {
            if (tvTime == null) return;
        }

        public void setTvContent(String text) {
            if (tvContent == null) return;
            tvContent.setText(text);
        }

        public void changeLikeImg(final String feedKey) {
            final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(ForumFragment.LIKE_ROOT);
            final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            referenceLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long totalLike = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(feedKey)) {
                            totalLike = snapshot.getChildrenCount();
                            break;
                        }
                    }

                    if (dataSnapshot.child(feedKey).hasChild(auth.getUid())) {
                        ivLike.setImageResource(R.drawable.thumb_up);
                    } else {
                        ivLike.setImageResource(R.drawable.thumb_up_grey);
                    }
                    tvLike.setText(totalLike + " likes");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        @Override
        public void onClick(View view) {
           Intent intent = new Intent(context, DummyActivity.class);
           view.getContext().startActivity(intent);
            /*ForumModel feed = (ForumModel) view.getTag();
            Intent intent = new Intent(context, ForumActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtra("feedId", feed.getIdFeed());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);*/
        }
    }
}

/**
 * Click item list
 */



