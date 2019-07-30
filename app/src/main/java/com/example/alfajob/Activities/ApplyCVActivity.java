package com.example.alfajob.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.alfajob.Adapter.RVAdapterSendToUser;
import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplyCVActivity extends AppCompatActivity implements OnItemClickListener {
    public String cvId, cvTitle, cvEmail, cvPhone, cvUrl, cvSkills, cvCommentCount;
    public Bundle extras;
    public List<User> listUsers;
    public RVAdapterSendToUser rvAdapter;
    public DatabaseReference mDatabaseAppliedcv, mDatabaseSendToUsers, mDatabaseUsers, mDatabaseNewcv;

    public EditText et_cvTitle, et_cvSkills, et_cvPhone;
    public EditText et_addComment, et_search;
    public Button btn_sendAll, btn_choose;
    public RecyclerView mRecyclerView;
    public LinearLayout ll_list_invisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_cv);
        // view
        et_cvTitle = findViewById(R.id.et_applycv_title);
        et_cvSkills = findViewById(R.id.et_applycv_skills);
        et_cvPhone = findViewById(R.id.et_applycv_phone);
        et_addComment = findViewById(R.id.et_applycv_addcomment);
        et_search = findViewById(R.id.et_applycv_search);
        btn_sendAll = findViewById(R.id.btn_applycv_sendAll);
        btn_choose = findViewById(R.id.btn_applycv_choose);
        mRecyclerView = findViewById(R.id.rv_apply_send);
        ll_list_invisible = findViewById(R.id.ll_applycv_invisible);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Enable button listener
        et_cvSkills.addTextChangedListener(textWatcher);

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_sendAll.setEnabled(false);
                btn_sendAll.setTextColor(getResources().getColor(R.color.grey));
                btn_choose.setTextColor(getResources().getColor(R.color.colorWhite));
                ll_list_invisible.setVisibility(View.VISIBLE);
            }
        });

        btn_sendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_choose.setEnabled(false);
                btn_choose.setTextColor(getResources().getColor(R.color.grey));
                btn_sendAll.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_send_clicked));
                btn_sendAll.setTextColor(Color.parseColor("#000000"));
                btn_sendAll.setText("Sent");
                ll_list_invisible.setVisibility(View.INVISIBLE);
            }
        });

        // list init
        listUsers = new ArrayList<>();
        rvAdapter = new RVAdapterSendToUser(ApplyCVActivity.this, listUsers);
        mRecyclerView.setAdapter(rvAdapter);
        rvAdapter.setClickListener(this);

        // db init
        mDatabaseAppliedcv = FirebaseDatabase.getInstance().getReference("appliedcv");
        mDatabaseSendToUsers = FirebaseDatabase.getInstance().getReference().child("send");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseNewcv= FirebaseDatabase.getInstance().getReference().child("newcv");

        // Action bat style
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorActionBar)));
        bar.setTitle("Apply CV");

        // Retrieving data from adapter ...
        if (savedInstanceState == null){
            extras = getIntent().getExtras();
            if(extras == null){
                cvId = null;
                cvTitle = null;
                cvEmail = null;
                cvPhone = null;
                cvUrl = null;

            }
            else{
                cvId = extras.getString("CV ID");
                cvTitle = extras.getString("CV TITLE");
                cvEmail = extras.getString("CV EMAIL");
                cvPhone = extras.getString("CV PHONE");
                cvUrl = extras.getString("CV URL");
            }

        }

        //Assigning to views ...
        if(cvTitle != null){ et_cvTitle.setText(cvTitle);}
        if(cvPhone != null){ et_cvPhone.setText(cvPhone);}
        initializeUsers();

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            cvTitle = et_cvTitle.getText().toString().trim();
            cvSkills = et_cvSkills.getText().toString().trim();
            cvPhone = et_cvPhone.getText().toString().trim();
            if(!cvTitle.isEmpty() && !cvSkills.isEmpty() && !cvPhone.isEmpty()){

                btn_sendAll.setEnabled(true);
                btn_sendAll.setTextColor(getResources().getColor(R.color.colorWhite));

                btn_choose.setEnabled(true);
                btn_choose.setTextColor(getResources().getColor(R.color.colorWhite));

            }
            else{
                ll_list_invisible.setVisibility(View.INVISIBLE);
                btn_sendAll.setEnabled(false);
                btn_sendAll.setTextColor(getResources().getColor(R.color.grey));
                btn_choose.setEnabled(false);
                btn_choose.setTextColor(getResources().getColor(R.color.grey));


            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View view, int position) {
        String cvUserId = listUsers.get(position).getUserId();
        System.out.println(cvUserId+"hello!!!");
        switch(view.getId()) {
            case R.id.btn_applycv_send:
                Button btn_send = view.findViewById(R.id.btn_applycv_send);
                btn_send.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_send_clicked));
                btn_send.setTextColor(Color.parseColor("#000000"));
                btn_send.setText("Sent");
                saveappliedCV(cvUserId);
                break;
        }

    }

    public void saveappliedCV(String userId){
        mDatabaseSendToUsers
                .child(cvId)
                .child(userId)
                .setValue(userId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Success", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Fail", "Error writing document!", e);
                    }
                });

    }


    public void initializeUsers(){

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUsers.clear();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                    User user = childSnap.getValue(User.class);
                    System.out.println(user.getUserEmail()+"hello");
                    listUsers.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void callBack(){
        if(!et_addComment.getText().toString().trim().equals("")){
            cvCommentCount = "1";
        }
        else{
            cvCommentCount  = "0";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ApplyCVActivity.this);
        builder.setTitle("Save");
        builder.setMessage("Do you want to save changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference rootAppliedcv = mDatabaseAppliedcv.child(cvId);
                System.out.println(cvTitle+cvSkills+cvEmail+cvPhone+cvUrl+cvCommentCount);
                rootAppliedcv.child("cvTitle").setValue(cvTitle);
                rootAppliedcv.child("cvSkills").setValue(cvSkills);
                rootAppliedcv.child("cvEmail").setValue(cvEmail);
                rootAppliedcv.child("cvPhone").setValue(cvPhone);
                rootAppliedcv.child("cvUrl").setValue(cvUrl);
                rootAppliedcv.child("cvCommentCount").setValue(cvCommentCount);
                rootAppliedcv.child("cvStarCount").setValue("0");

                mDatabaseNewcv.child(cvId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Log.d("Success", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Fail", "Error deleting document", e);
                            }
                        });

                dialog.dismiss();
                finish();
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseAppliedcv.child(cvId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Success", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Fail", "Error deleting document", e);
                            }
                        });
                mDatabaseSendToUsers.child(cvId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Success", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Fail", "Error deleting document", e);
                            }
                        });

                dialog.dismiss();
                ApplyCVActivity.this.finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        callBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                callBack();
                return true;
        }
        return false;
    }
}
