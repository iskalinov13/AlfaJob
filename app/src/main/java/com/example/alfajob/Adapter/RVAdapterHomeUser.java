package com.example.alfajob.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Activities.CommentActivity;
import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Objects.AppliedCV;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RVAdapterHomeUser extends RecyclerView.Adapter<RVAdapterHomeUser.MyViewHolder> {

    private Context mContext;
    public List<AppliedCV> mData;
    private FirebaseFirestore db;
    boolean starClicked;
    private OnItemClickListener clickListener;

    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public DatabaseReference mDatabaseStar, mDatabaseAppliedcv, mDatabaseComments, mDatabaseSendToUsers;
    public String userId;
    private Dialog dialogView;


    public RVAdapterHomeUser(Context mContext, List<AppliedCV> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
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
                if(isValid(mData.get(viewHolder.getAdapterPosition()).getCvUrl())){
                    System.out.println(mData.get(viewHolder.getAdapterPosition()).getCvUrl());

                    mDatabaseSendToUsers.child(mData.get(viewHolder.getAdapterPosition()).getId()).child(mAuth.getCurrentUser().getUid()).setValue(true);
                    notifyItemChanged(viewHolder.getAdapterPosition());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(viewHolder.getAdapterPosition()).getCvUrl()));
                    mContext.startActivity(intent);
                }
                else{
                    Toast.makeText(mContext, "Not valid url.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewHolder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("cvId", mData.get(viewHolder.getAdapterPosition()).getId());
                intent.putExtra("userId", userId);
                intent.putExtra("fragmentName", "AppliedCV");
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

        viewHolder.tv_star_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeStars(viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;

    }

    private static boolean isValid(String url){
        try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private void seeStars(int position){

        dialogView = new Dialog(mContext);
        dialogView.setContentView(R.layout.dialog_star);
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        List<User> listUsers = getUsersList(getUsersId(mData.get(position).getId()));

        RecyclerView recyclerViewStars = dialogView.findViewById(R.id.rv_stars);
        TextView tv_no_data = dialogView.findViewById(R.id.tv_star_no_data);
        Button btn_ok = dialogView.findViewById(R.id.btn_ok);

        RVAdapterStar rvAdapterStar = new RVAdapterStar(mContext, listUsers);
        recyclerViewStars.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewStars.setAdapter(rvAdapterStar);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
            }
        });
        dialogView.show();
    }
    private List<User> getUsersList(final List<String> list){

        final List<User> listOfUsers = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(list.contains(user.getUserId())){
                        listOfUsers.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return listOfUsers;
    }
    private List<String> getUsersId(String cvId){

        final List<String> list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stars").child(cvId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return list;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        System.out.println(mData.size());
        holder.tv_title.setText(mData.get(i).getCvTitle());
        holder.tv_skills.setText(mData.get(i).getCvSkills());
        holder.tv_star_count.setText(mData.get(i).getStarCount());
        holder.tv_comment_count.setText(mData.get(i).getCommentCount());
        holder.tv_notification.setText(mData.get(i).getCvStatus());
        holder.setStarBtn(mData.get(i).getId());
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }

        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_title;
        private TextView tv_skills;
        private TextView tv_star_count;
        private TextView tv_comment_count;
        private ImageView iv_star;
        private ImageView iv_comment;
        private TextView tv_notification;
        private Button btn_view;

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabaseStars, mDatabaseSend;
        private  Context mContext;

        public MyViewHolder(@NonNull View itemView)  {
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
            tv_notification = itemView.findViewById(R.id.tv_notification_homeuser);
            btn_view = (Button) itemView.findViewById(R.id.btn_view_homeuser);


        }
        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                System.out.println("hello");
                clickListener.onClick(view, getAdapterPosition());
            }
            else{
                System.out.println("by");
            }

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







    }
}