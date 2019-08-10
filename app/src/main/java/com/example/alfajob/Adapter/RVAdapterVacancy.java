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
import com.example.alfajob.Objects.User;
import com.example.alfajob.Objects.Vacancy;
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

public class RVAdapterVacancy extends RecyclerView.Adapter<RVAdapterVacancy.MyViewHolder>{

    private List<Vacancy> listVacancy;
    private Context mContext;
    private OnItemClickListener clickListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    public RVAdapterVacancy(List<Vacancy> listVacancy, Context mContext) {
        this.listVacancy = listVacancy;
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
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_vacancy, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Vacancy vacancy = listVacancy.get(position);
        holder.tv_username.setText(vacancy.getUserName());
        holder.tv_title.setText(vacancy.getVacancyTitle());
        holder.tv_date.setText(vacancy.getVacancyDate());
        holder.setDeleteBtn(firebaseUser.getUid());
    }

    @Override
    public int getItemCount() {
        return listVacancy.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView civ_profile;
        ImageView iv_delete;
        TextView tv_username, tv_title, tv_date;
        Button btn_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_profile = itemView.findViewById(R.id.civ_profile_image_vacancy);
            iv_delete = itemView.findViewById(R.id.iv_delete_vacancy);
            tv_username = itemView.findViewById(R.id.tv_username_vacancy);
            tv_title = itemView.findViewById(R.id.tv_jobTitle_vacancy);
            tv_date = itemView.findViewById(R.id.tv_date_vacancy);
            btn_view = itemView.findViewById(R.id.btn_view_vacancy);
            btn_view.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
        }

        public void setDeleteBtn(final String uId){
            mReferenceUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        if(user.getUserEmail().equals("recruiteralfabank@gmail.com")){
                            if(user.getUserId().equals(uId)){
                                iv_delete.setVisibility(View.INVISIBLE);

                            }
                        }

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
