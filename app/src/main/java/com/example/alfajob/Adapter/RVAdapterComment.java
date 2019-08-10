package com.example.alfajob.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Objects.Comment;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterComment extends RecyclerView.Adapter<RVAdapterComment.MyViewHolder> {

    Context mContext;
    List<Comment> mData;
    FirebaseUser firebaseUser;
    private OnItemClickListener clickListener;

    public RVAdapterComment(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public RVAdapterComment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_comment,viewGroup,false);

        final RVAdapterComment.MyViewHolder viewHolder = new RVAdapterComment.MyViewHolder(v);
        return viewHolder;

    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull RVAdapterComment.MyViewHolder holder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mData.get(i);
        holder.tv_comment.setText(comment.getComment());
        holder.tv_date.setText(comment.getDate());
        holder.getUserInfo(holder.civ_profilePhoto, holder.tv_userName, comment.getUserId());
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civ_profilePhoto;
        private TextView tv_userName;
        private TextView tv_comment;
        private TextView tv_date;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_profilePhoto = itemView.findViewById(R.id.civ_profile_image_comment);
            tv_userName  = itemView.findViewById(R.id.tv_username_comment);
            tv_comment = itemView.findViewById(R.id.tv_comment_comment);
            tv_date = itemView.findViewById(R.id.tv_comment_date_date);

        }

        private void getUserInfo(ImageView imageView, final TextView username, String userid){

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    //Glide.vith(mContext).load(user,getImmageURL()).into(imageView);
                    username.setText(user.getUserName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
