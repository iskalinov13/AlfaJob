package com.example.alfajob.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Activities.ApplyCVActivity;
import com.example.alfajob.Objects.NewCV;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RVAdapterNewCV extends RecyclerView.Adapter<RVAdapterNewCV.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback  {
    Context mContext;
    List<NewCV> mData;
    FirebaseFirestore db;
    String phoneNumber;

    private static final int  REQUEST_CALL = 1;

    public RVAdapterNewCV(Context mContext, List<NewCV> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.list_item_newcv,viewGroup,false);
        final MyViewHolder viewHolder = new MyViewHolder(v);
        db = FirebaseFirestore.getInstance();


          viewHolder.btn_view.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(viewHolder.getAdapterPosition()).getCvUrl()));
                  mContext.startActivity(intent);
              }
          });

          viewHolder.btn_apply.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  Intent intent = new Intent((Activity)mContext, ApplyCVActivity.class);

                  String strId = mData.get(viewHolder.getAdapterPosition()).getId();
                  String strTitle = mData.get(viewHolder.getAdapterPosition()).getCvTitle();
                  String strEmail = mData.get(viewHolder.getAdapterPosition()).getUserEmail();
                  String strPhone = mData.get(viewHolder.getAdapterPosition()).getUserPhone();
                  String strCVUrl = mData.get(viewHolder.getAdapterPosition()).getCvUrl();

                  intent.putExtra("CV ID", strId);
                  intent.putExtra("CV TITLE", strTitle);
                  intent.putExtra("CV EMAIL", strEmail);
                  intent.putExtra("CV PHONE", strPhone);
                  intent.putExtra("CV URL", strCVUrl);

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

                          db.collection("newcv").document(mData.get(position).getId())
                                  .delete()
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if(mData.size()!=0){
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

          viewHolder.ll_email.setOnClickListener(new View.OnClickListener() {
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

          viewHolder.ll_phone.setOnClickListener(new View.OnClickListener() {
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
        holder.tv_email.setText(mData.get(i).getUserEmail());
        holder.tv_phone.setText(mData.get(i).getUserPhone());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        private TextView tv_title;
        private TextView tv_email;
        private TextView tv_phone;
        private ImageView iv_email;
        private ImageView iv_phone;
        private ImageView iv_delete;
        private Button btn_view;
        private Button btn_apply;
        private LinearLayout ll_email, ll_phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = (TextView)itemView.findViewById(R.id.tv_title_newcv);
            tv_email = (TextView)itemView.findViewById(R.id.tv_email_newcv);
            tv_phone = (TextView)itemView.findViewById(R.id.tv_phone_newcv);
            iv_email = (ImageView)itemView.findViewById(R.id.iv_email_newcv);
            iv_phone = (ImageView)itemView.findViewById(R.id.iv_phone_newcv);
            iv_delete = (ImageView)itemView.findViewById(R.id.iv_delete_newcv);
            btn_view = (Button)itemView.findViewById(R.id.btn_view_newcv);
            btn_apply = (Button)itemView.findViewById(R.id.btn_apply_newcv);
            ll_email = (LinearLayout)itemView.findViewById(R.id.ll_email_newcv);
            ll_phone = (LinearLayout)itemView.findViewById(R.id.ll_phone_newcv);

        }
    }



}