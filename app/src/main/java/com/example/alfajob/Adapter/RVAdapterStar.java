package com.example.alfajob.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterStar extends RecyclerView.Adapter<RVAdapterStar.MyViewHolder>  {

    private Context mContext;
    List<User> mData;
    FirebaseFirestore db;
    private OnItemClickListener clickListener;


    public RVAdapterStar(Context mContext, List<User> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RVAdapterStar.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_who_starred,viewGroup,false);

        final RVAdapterStar.MyViewHolder viewHolder = new RVAdapterStar.MyViewHolder(v);
        db = FirebaseFirestore.getInstance();

        return viewHolder;

    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }



    @Override
    public void onBindViewHolder(@NonNull RVAdapterStar.MyViewHolder holder, int i) {

        // holder.civ_profilePhoto.setImageDrawable();
        holder.tv_userName.setText(mData.get(i).getUserName());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView civ_profilePhoto;
        private TextView tv_userName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_profilePhoto = itemView.findViewById(R.id.civ_star_profile);
            tv_userName  = itemView.findViewById(R.id.tv_star_username);
            //btn_send.setOnClickListener(this);


        }
        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }

    }
}