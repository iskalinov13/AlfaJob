package com.example.alfajob.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.alfajob.Adapter.RVAdapterSendToUser;
import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Interface.APIService;
import com.example.alfajob.Notifications.Client;
import com.example.alfajob.Notifications.Data;
import com.example.alfajob.Notifications.MyResponse;
import com.example.alfajob.Notifications.Sender;
import com.example.alfajob.Notifications.Token;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyCVActivity extends AppCompatActivity implements OnItemClickListener {
    public String cvId, cvTitle, cvEmail, cvPhone, cvUrl, cvSkills, cvCommentCount, userid;
    public Bundle extras;
    public List<User> listUsers;
    public RVAdapterSendToUser rvAdapter;
    public DatabaseReference mDatabaseAppliedcv, mDatabaseSendToUsers, mDatabaseUsers, mDatabaseNewcv, mDatabaseComments;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

    public EditText et_cvTitle, et_cvSkills, et_cvPhone;
    public EditText et_addComment;
    public Button btn_sendAll, btn_choose;
    public RecyclerView mRecyclerView;
    public LinearLayout ll_list_invisible;
    APIService apiService;
    boolean notify = false;
    private int cvCommentcount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_cv);
        // view
        et_cvTitle = findViewById(R.id.et_applycv_title);
        et_cvSkills = findViewById(R.id.et_applycv_skills);
        et_cvPhone = findViewById(R.id.et_applycv_phone);
        et_addComment = findViewById(R.id.et_applycv_addcomment);
        btn_sendAll = findViewById(R.id.btn_applycv_sendAll);
        btn_choose = findViewById(R.id.btn_applycv_choose);
        mRecyclerView = findViewById(R.id.rv_apply_send);
        ll_list_invisible = findViewById(R.id.ll_applycv_invisible);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //api
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
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
                sendCVToAllUsers(cvId);
            }
        });

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
                sendCVToUser(cvUserId);
                break;
        }

    }

    public void sendCVToAllUsers(final String cvId){
        btn_sendAll.setActivated(true);
        setAppliedCV();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnap: dataSnapshot.getChildren()){
                    User user = childSnap.getValue(User.class);
                    mDatabaseSendToUsers
                            .child(cvId)
                            .child(user.getUserId())
                            .setValue(false);
                    sendNotification(firebaseUser.getUid(), user.getUserId(), cvSkills, cvTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCommentCount(final String cvId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(cvId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDatabaseAppliedcv.child(cvId).child("cvCommentCount").setValue(dataSnapshot.getChildrenCount()+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setStarCount(final String cvId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stars").child(cvId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDatabaseAppliedcv.child(cvId).child("cvStarCount").setValue(dataSnapshot.getChildrenCount()+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addComment(String comment){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(cvId);
        String commentId = reference.push().getKey();
        String userID = firebaseUser.getUid();
        String pattern = " dd, yyyy 'at' HH:mm:ss";
        Calendar calendar = Calendar.getInstance();
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = month + simpleDateFormat.format(new Date());
        Comment c = new Comment(commentId, comment, userID, date);
        reference.child(commentId).setValue(c);
    }

    private void setAppliedCV(){
        if (!et_addComment.getText().toString().trim().equals("")) {
            addComment(et_addComment.getText().toString().trim());
            et_addComment.setText("");
        }

        DatabaseReference rootAppliedcv = mDatabaseAppliedcv.child(cvId);
        rootAppliedcv.child("cvTitle").setValue(cvTitle);
        rootAppliedcv.child("cvSkills").setValue(cvSkills);
        rootAppliedcv.child("cvEmail").setValue(cvEmail);
        rootAppliedcv.child("cvPhone").setValue(cvPhone);
        rootAppliedcv.child("cvUrl").setValue(cvUrl);
        setCommentCount(cvId);
        setStarCount(cvId);
        rootAppliedcv.child("cvStatus").setValue("Не прочитано");
    }

    public void sendCVToUser(final String userId) {
        btn_choose.setActivated(true);
        setAppliedCV();
        sendNotification(firebaseUser.getUid(), userId, cvSkills, cvTitle);
        mDatabaseSendToUsers
                .child(cvId)
                .child(userId)
                .setValue(false)
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
                    listUsers.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void callBack(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ApplyCVActivity.this);
        builder.setTitle("Save");
        builder.setMessage("Do you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(btn_choose.isActivated() || btn_sendAll.isActivated()){
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
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                else{
                    dialog.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();
                }

            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    private void sendNotification(final String sender, final String receiver, final String cvTitle, final String cvSkills){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.ic_logo_alfabank3,cvSkills, cvTitle,receiver);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(!response.isSuccessful()){
                                            Toast.makeText(ApplyCVActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
