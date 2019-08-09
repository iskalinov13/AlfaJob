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
import com.example.alfajob.Objects.ApprovedCV;

import com.example.alfajob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class RVAdapterApprovedCV  extends RecyclerView.Adapter<RVAdapterApprovedCV.MyViewHolder>{

    private List<ApprovedCV> approvedCVList;
    private Context mContext;
    private OnItemClickListener clickListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabaseStars;

    public RVAdapterApprovedCV(List<ApprovedCV> approvedCVList, Context mContext) {
        this.approvedCVList = approvedCVList;
        this.mContext = mContext;
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference("users");
        mDatabaseStars = mDatabase.getReference("Stars");
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_approved, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        ApprovedCV approvedCV = approvedCVList.get(position);
        holder.tv_title.setText(approvedCV.getCvTitle());
        System.out.println(approvedCV.getCvUrl()+"hello");
        holder.tv_skills.setText(approvedCV.getCvSkills());
        holder.tv_star_count.setText(approvedCV.getCvStarCount());
        holder.tv_comment_count.setText(approvedCV.getCvCommentCount());
        holder.setStarBtn(approvedCV.getCvId());

    }

    @Override
    public int getItemCount() {
        return approvedCVList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView iv_delete, iv_phone, iv_star, iv_comment, iv_email;
        TextView tv_title, tv_skills, tv_email, tv_phone, tv_star_count, tv_comment_count;
        Button btn_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = (TextView)itemView.findViewById(R.id.tv_title_approvedcv);
            tv_skills = (TextView)itemView.findViewById(R.id.tv_skills_approvedcv);
            tv_email = (TextView)itemView.findViewById(R.id.tv_email_approvedcv);
            tv_phone = (TextView)itemView.findViewById(R.id.tv_phone_approvedcv);
            tv_star_count = (TextView)itemView.findViewById(R.id.tv_starcount_approvedcv);
            tv_comment_count = (TextView)itemView.findViewById(R.id.tv_commentcount_approvedcv);
            iv_email = (ImageView)itemView.findViewById(R.id.iv_email_approvedcv);
            iv_phone = (ImageView)itemView.findViewById(R.id.iv_phone_approvedcv);
            iv_star = (ImageView)itemView.findViewById(R.id.iv_star_approvedcv);
            iv_comment = (ImageView)itemView.findViewById(R.id.iv_comment_approvedcv);
            iv_delete = (ImageView)itemView.findViewById(R.id.iv_delete_approvedcv);
            btn_view = (Button)itemView.findViewById(R.id.btn_view_approvedcv);

            iv_email.setOnClickListener(this);
            iv_phone.setOnClickListener(this);
            iv_star.setOnClickListener(this);
            iv_comment.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
            btn_view.setOnClickListener(this);
            tv_star_count.setOnClickListener(this);
            tv_comment_count.setOnClickListener(this);
        }

        public void setStarBtn(final String cvId){

            mDatabaseStars.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(cvId).hasChild(mAuth.getCurrentUser().getUid())){
                            iv_star.setImageResource(R.drawable.ic_star_clciked_24px);
                        }
                        else{
                            iv_star.setImageResource(R.drawable.ic_star_24px_outlined);
                        }
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }
}
