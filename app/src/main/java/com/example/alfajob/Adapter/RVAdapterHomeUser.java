package com.example.alfajob.Adapter;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Activities.CommentActivity;
import com.example.alfajob.Activities.HomeActivity;
import com.example.alfajob.Objects.AppliedCV;
import com.example.alfajob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class RVAdapterHomeUser extends RecyclerView.Adapter<RVAdapterHomeUser.MyViewHolder> {

    private Context mContext;
    public List<AppliedCV> mData;
    private FirebaseFirestore db;
    boolean starClicked;

    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public DatabaseReference mDatabaseStar, mDatabaseAppliedcv, mDatabaseComments, mDatabaseSendToUsers;
    public String userId;


    public RVAdapterHomeUser(Context mContext, List<AppliedCV> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.list_item_homeuser, viewGroup, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        // init db, user
        db = FirebaseFirestore.getInstance();
        mDatabaseStar = FirebaseDatabase.getInstance().getReference().child("Stars");
        mDatabaseAppliedcv = FirebaseDatabase.getInstance().getReference().child("appliedcv");
        mDatabaseComments = FirebaseDatabase.getInstance().getReference().child("comments");
        mDatabaseSendToUsers = FirebaseDatabase.getInstance().getReference().child("send");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();


        viewHolder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseSendToUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(mData.get(viewHolder.getAdapterPosition()).getId()).hasChild(mAuth.getCurrentUser().getUid())) {

                                if (dataSnapshot.child(mData.get(viewHolder.getAdapterPosition()).getId()).
                                        child(mAuth.getCurrentUser().getUid()).getValue(Boolean.class)){
                                    mDatabaseSendToUsers.child(mData.get(viewHolder.getAdapterPosition()).getId()).child(mAuth.getCurrentUser().getUid()).setValue(false);
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(viewHolder.getAdapterPosition()).getCvUrl()));
                                    mContext.startActivity(intent);

                                }
                                else{
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(viewHolder.getAdapterPosition()).getCvUrl()));
                                    mContext.startActivity(intent);
                                }

                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        viewHolder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("cvId", mData.get(viewHolder.getAdapterPosition()).getId());
                intent.putExtra("userId", userId);
                mContext.startActivity(intent);
            }
        });
        viewHolder.iv_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                starClicked = true;

                mDatabaseStar.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (starClicked) {

                            if (dataSnapshot.child(mData.get(viewHolder.getAdapterPosition()).getId()).hasChild(mAuth.getCurrentUser().getUid())) {

                                mDatabaseStar.child(mData.get(viewHolder.getAdapterPosition()).getId()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                starClicked = false;

                                int starCount = Integer.parseInt(mData.get(viewHolder.getAdapterPosition()).getStarCount());
                                if (starCount != 0) {
                                    starCount = starCount - 1;
                                }
                                mData.get(viewHolder.getAdapterPosition()).setStarCount(starCount + "");
                                notifyItemChanged(viewHolder.getAdapterPosition());
                                mDatabaseAppliedcv.child(mData.get(viewHolder.getAdapterPosition()).getId()).child("cvStarCount").setValue(starCount + "");

                            } else {

                                mDatabaseStar.child(mData.get(viewHolder.getAdapterPosition()).getId()).child(mAuth.getCurrentUser().getUid()).setValue(userId);
                                starClicked = false;

                                int starCount = Integer.parseInt(mData.get(viewHolder.getAdapterPosition()).getStarCount()) + 1;
                                mData.get(viewHolder.getAdapterPosition()).setStarCount(starCount + "");
                                notifyItemChanged(viewHolder.getAdapterPosition());
                                mDatabaseAppliedcv.child(mData.get(viewHolder.getAdapterPosition()).getId()).child("cvStarCount").setValue(starCount + "");
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        System.out.println(mData.size()+ "SIZE");
        holder.tv_title.setText(mData.get(i).getCvTitle()+"POSITION"+i);
        holder.tv_skills.setText(mData.get(i).getCvSkills());
        holder.tv_star_count.setText(mData.get(i).getStarCount());
        holder.tv_comment_count.setText(mData.get(i).getCommentCount());
        holder.setStarBtn(mData.get(i).getId());
        //holder.setNotification(mAuth.getCurrentUser().getUid(),mData.get(i).getId(),mContext);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }

        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_skills;
        private TextView tv_star_count;
        private TextView tv_comment_count;
        private ImageView iv_star;
        private ImageView iv_comment;
        private ImageView iv_notification;
        private Button btn_view;

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabaseStars, mDatabaseSend;
        private  Context mContext;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            mDatabaseStars = FirebaseDatabase.getInstance().getReference().child("Stars");
            mDatabaseSend = FirebaseDatabase.getInstance().getReference().child("send");

            tv_title = (TextView) itemView.findViewById(R.id.tv_title_homeusercv);
            tv_skills = (TextView) itemView.findViewById(R.id.tv_skills_homeuser);
            tv_star_count = (TextView) itemView.findViewById(R.id.tv_starcount_homeuser);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_commentcount_homeuser);
            iv_star = (ImageView) itemView.findViewById(R.id.iv_star_homeuser);
            iv_comment = (ImageView) itemView.findViewById(R.id.iv_comment_homeuser);
            iv_notification = itemView.findViewById(R.id.iv_notification_homeuser);
            btn_view = (Button) itemView.findViewById(R.id.btn_view_homeuser);


        }

        public void setStarBtn(final String cvId) {

            mDatabaseStars.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(cvId).hasChild(mAuth.getCurrentUser().getUid())) {

                        iv_star.setImageResource(R.drawable.ic_star_clciked_24px);
                    } else {

                        iv_star.setImageResource(R.drawable.ic_star_24px_outlined);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        public void setNotification(final String userId, final String cvId, final Context context){
            mDatabaseSend.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(cvId).child(mAuth.getCurrentUser().getUid()).getValue(Boolean.class)) {
                        Animation animScale = AnimationUtils.loadAnimation(context,R.anim.anim_scale);
                        iv_notification.setImageResource(R.drawable.ic_notifications_on_24px);
                        iv_notification.startAnimation(animScale);

                    }
                    else {

                        iv_notification.setImageResource(R.drawable.ic_notifications_24px);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }






    }
}