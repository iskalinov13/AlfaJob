package com.example.alfajob.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Activities.CommentActivity;
import com.example.alfajob.Objects.AppliedCV;
import com.example.alfajob.Objects.ApprovedCV;
import com.example.alfajob.Objects.Comment;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RVAdapterAppliedCV extends RecyclerView.Adapter<RVAdapterAppliedCV.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback  {
    Context mContext;
    List<AppliedCV> mData;
    FirebaseFirestore db;
    String phoneNumber;
    boolean starClicked;
    boolean[] actuallyClicked = new boolean[1];

    private static final int  REQUEST_CALL = 1;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public DatabaseReference mDatabaseStar, mDatabaseAppliedcv, mDatabaseSendToUsers, mDatabaseComments;
    public String userId;
    private Dialog dialogView, dialogApprove;

    public RVAdapterAppliedCV(Context mContext, List<AppliedCV> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        actuallyClicked[0] = false;

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.list_item_appliedcv,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        // init db, user
        db = FirebaseFirestore.getInstance();
        mDatabaseStar = FirebaseDatabase.getInstance().getReference().child("Stars");
        mDatabaseAppliedcv = FirebaseDatabase.getInstance().getReference().child("appliedcv");
        mDatabaseSendToUsers = FirebaseDatabase.getInstance().getReference().child("send");
        mDatabaseComments = FirebaseDatabase.getInstance().getReference().child("comments");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        viewHolder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(viewHolder.getAdapterPosition()).getCvUrl()));
                mContext.startActivity(intent);
            }
        });

        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this cv?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final int position = viewHolder.getAdapterPosition();
                        final String cvId = mData.get(position).getId();

                        deleteAppliedCVStars(position, cvId);
                        deleteAppliedCVComment(position, cvId);
                        deleteAppliedCVSend(position, cvId);
                        deleteAppliedCV(position, cvId);

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });

        viewHolder.iv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mData.get(viewHolder.getAdapterPosition()).getUserEmail()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, mData.get(viewHolder.getAdapterPosition()).getCvTitle());
                mContext.startActivity(Intent.createChooser(emailIntent, "Choose an Email Client ..."));

            }
        });

        viewHolder.iv_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogView = new Dialog(mContext);
                dialogView.setContentView(R.layout.dialog_approve);
                dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialogView.show();

                Button btn_yes = dialogView.findViewById(R.id.btn_yes_dialog);
                Button btn_no = dialogView.findViewById(R.id.btn_no_dialog);
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        approveCV(mData.get(viewHolder.getAdapterPosition()));
                        dialogView.dismiss();
                    }
                });
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogView.dismiss();
                    }
                });
            }
        });

        viewHolder.iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Call");
                builder.setMessage("Do you want to call to him/her ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phoneNumber = mData.get(viewHolder.getAdapterPosition()).getUserPhone();
                        makePhoneCall(phoneNumber);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
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
                            if(starClicked){

                                if(dataSnapshot.child(mData.get(viewHolder.getAdapterPosition()).getId()).hasChild(mAuth.getCurrentUser().getUid())){

                                    mDatabaseStar.child(mData.get(viewHolder.getAdapterPosition()).getId()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    starClicked = false;

                                    int starCount = Integer.parseInt(mData.get(viewHolder.getAdapterPosition()).getStarCount());
                                    if(starCount!=0){
                                        starCount = starCount - 1;
                                    }
                                    mData.get(viewHolder.getAdapterPosition()).setStarCount(starCount+"");
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                    mDatabaseAppliedcv.child(mData.get(viewHolder.getAdapterPosition()).getId()).child("cvStarCount").setValue(starCount+"");

                                }
                                else{

                                    mDatabaseStar.child(mData.get(viewHolder.getAdapterPosition()).getId()).child(mAuth.getCurrentUser().getUid()).setValue(userId);
                                    starClicked = false;

                                    int starCount = Integer.parseInt(mData.get(viewHolder.getAdapterPosition()).getStarCount())+1;
                                    mData.get(viewHolder.getAdapterPosition()).setStarCount(starCount+"");
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                    mDatabaseAppliedcv.child(mData.get(viewHolder.getAdapterPosition()).getId()).child("cvStarCount").setValue(starCount+"");
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
    private void deleteAppliedCVStars(final int position, final String cvId){
        mDatabaseStar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(cvId)){
                    mDatabaseStar.child(cvId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ///deleteAppliedCVComment(position, cvId);
                        }
                    });
                }
                else{
                    //deleteAppliedCVComment(position, cvId);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteAppliedCVComment(final int position, final String cvId){
        mDatabaseComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(cvId)){
                    mDatabaseComments.child(cvId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //deleteAppliedCVSend(position, cvId);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteAppliedCVSend(final int position,final String cvId){
        mDatabaseSendToUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(cvId)){
                    mDatabaseSendToUsers.child(cvId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //deleteAppliedCV(position, cvId);
                        }
                    });
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void deleteAppliedCV(final int position, final String cvId){
        mDatabaseAppliedcv.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(cvId)){
                    mDatabaseAppliedcv.child(cvId).removeValue();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void approveCV(AppliedCV cv){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("approved").child(cv.getId());
        ApprovedCV approvedCV = new ApprovedCV(cv.getId(), cv.getCvTitle(), cv.getCvSkills(), cv.getUserEmail(), cv.getUserPhone(), cv.getCvUrl(), cv.getStarCount(), cv.getCommentCount());
        reference.setValue(approvedCV);


        mDatabaseAppliedcv.child(cv.getId()).removeValue();
        mDatabaseSendToUsers.child(cv.getId()).removeValue();
        notifyItemRemoved(mData.indexOf(cv));
        mData.remove(cv);
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

    private void makePhoneCall(String phonenumber){
        String phoneN = phonenumber;
        phoneNumber = "";
        if(phoneN.trim().length()>0){

            if(ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions((Activity)mContext,
                        new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{

                String dial = "tel:"+phoneN;
                mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{

            Toast.makeText(mContext, "Empty phone", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                makePhoneCall(phoneNumber);
                phoneNumber = "";
            }
            else{
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.tv_title.setText(mData.get(i).getCvTitle());
        holder.tv_skills.setText(mData.get(i).getCvSkills());
        holder.tv_star_count.setText(mData.get(i).getStarCount());
        holder.tv_comment_count.setText(mData.get(i).getCommentCount());
        holder.setStarBtn(mData.get(i).getId());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {

        private TextView tv_title;
        private TextView tv_skills;
        private TextView tv_email;
        private TextView tv_phone;
        private TextView tv_star_count;
        private TextView tv_comment_count;
        private ImageView iv_star;
        private ImageView iv_comment;
        private ImageView iv_email;
        private ImageView iv_phone;
        private ImageView iv_delete;
        private ImageView iv_approve;
        private Button btn_view;

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabaseStars;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mDatabaseStars = FirebaseDatabase.getInstance().getReference().child("Stars");

            tv_title = (TextView)itemView.findViewById(R.id.tv_title_appliedcv);
            tv_skills = (TextView)itemView.findViewById(R.id.tv_skills_appliedcv);
            tv_email = (TextView)itemView.findViewById(R.id.tv_email_appliedcv);
            tv_phone = (TextView)itemView.findViewById(R.id.tv_phone_appliedcv);
            tv_star_count = (TextView)itemView.findViewById(R.id.tv_starcount_appliedcv);
            tv_comment_count = (TextView)itemView.findViewById(R.id.tv_commentcount_appliedcv);
            iv_email = (ImageView)itemView.findViewById(R.id.iv_email_appliedcv);
            iv_phone = (ImageView)itemView.findViewById(R.id.iv_phone_appliedcv);
            iv_star = (ImageView)itemView.findViewById(R.id.iv_star_appliedcv);
            iv_comment = (ImageView)itemView.findViewById(R.id.iv_comment_appliedcv);
            iv_delete = (ImageView)itemView.findViewById(R.id.iv_delete_appliedcv);
            iv_approve = itemView.findViewById(R.id.iv_approve_appliedcv);
            btn_view = (Button)itemView.findViewById(R.id.btn_view_appliedcv);

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
    }



}