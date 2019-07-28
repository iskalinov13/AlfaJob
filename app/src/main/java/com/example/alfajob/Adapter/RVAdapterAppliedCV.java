package com.example.alfajob.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;
import com.example.alfajob.Objects.AppliedCV;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public DatabaseReference mDatabaseStar;
    public String userId;
    public String cvId;
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
                        Toast.makeText(mContext, position+"", Toast.LENGTH_SHORT).show();

                        db.collection("appliedcv").document(mData.get(position).getId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(mData.size()!=0){
                                            mDatabaseStar.child(mData.get(position).getId()).removeValue();
                                            mData.remove(position);
                                            notifyDataSetChanged();



                                        }
                                        Toast.makeText(mContext, "Successfully deleted ...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

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
                                    db.collection("appliedcv")
                                            .document(mData.get(viewHolder.getAdapterPosition()).getId())
                                            .update("cvStarCount", starCount+"");
                                }
                                else{

                                    mDatabaseStar.child(mData.get(viewHolder.getAdapterPosition()).getId()).child(mAuth.getCurrentUser().getUid()).setValue(userId);
                                    starClicked = false;

                                    int starCount = Integer.parseInt(mData.get(viewHolder.getAdapterPosition()).getStarCount())+1;
                                    mData.get(viewHolder.getAdapterPosition()).setStarCount(starCount+"");
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                    db.collection("appliedcv")
                                            .document(mData.get(viewHolder.getAdapterPosition()).getId())
                                            .update("cvStarCount", starCount + "");
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