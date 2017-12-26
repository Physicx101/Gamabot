package com.example.prabowo.gamabot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForumActivity extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference commentReference = databaseReference.child(ForumFragment.COMMENT_ROOT);
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private EditText ETtambahkomentar;
    private Button BTtambahkomentar;
    private TextView tv_content;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tv_like;
    private String feedId;
    private CircleImageView iv_avatar;
    CommentModel comment = new CommentModel();
    ForumModel feed = new ForumModel();
    final String key = databaseReference.push().getKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        /*Bundle bundle = getIntent().getExtras();
        feedId = bundle.getString("feedId");*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Forum");

        ETtambahkomentar = findViewById(R.id.ETtambahkomentar);
        BTtambahkomentar = findViewById(R.id.BTmasukkankomentar);
        tv_content = findViewById(R.id.tv_content);
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        iv_avatar = findViewById(R.id.iv_avatar);
        tv_like = findViewById(R.id.tv_like);

        BTtambahkomentar.setOnClickListener(this);


        initPost();
        initComment();
    }

    private void initPost() {
        DatabaseReference mref = databaseReference.child("feed").child(feedId);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tv_content.setText(feed.getText());
                //tv_time.setText(feed.getTime());
                tv_name.setText(feed.getName());
                Glide.with(ForumActivity.this)
                        .load(feed.getPhotoAvatar())
                        .into(iv_avatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        final String userComment = ETtambahkomentar.getText().toString();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        commentReference.addValueEventListener(new ValueEventListener() {

                                                   @Override
                                                   public void onDataChange(DataSnapshot snapshot) {
                                                       comment.setComment(userComment);
                                                       //comment.setCommentId(snapshot.getValue().toString());

                                                       commentReference.child(feedId).setValue(comment);
                                                   }


                                                   @Override
                                                   public void onCancelled(DatabaseError databaseError) {

                                                   }

                                               }
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void initComment() {
        RecyclerView commentRecyclerView = findViewById(R.id.comment_list);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(ForumActivity.this));

        FirebaseRecyclerAdapter<CommentModel, CommentHolder> commentAdapter = new FirebaseRecyclerAdapter<CommentModel, CommentHolder>(
                CommentModel.class,
                R.layout.comment_item,
                CommentHolder.class,
                commentReference
        ) {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, CommentModel model, int position) {
                //viewHolder.setUsername(model.getUser());
                viewHolder.commentTextView.setText(comment.getComment());
                //viewHolder.setTime(DateUtils.getRelativeTimeSpanString(model.getTimeCreated()));

                /*Glide.with(ForumActivity.this)
                        .load(model.getUser().getPhotoUrl())
                        .into(viewHolder.commentOwnerDisplay);*/
            }
        };

        commentRecyclerView.setAdapter(commentAdapter);
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        ImageView commentOwnerDisplay;
        TextView usernameTextView;
        TextView timeTextView;
        TextView commentTextView;

        public CommentHolder(View itemView) {
            super(itemView);
            commentOwnerDisplay = (ImageView) itemView.findViewById(R.id.iv_avatar);
            usernameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            commentTextView = (TextView) itemView.findViewById(R.id.tv_content);
        }


    }

    @Override
    public void onClick(View view) {
        postComment();
    }
}
