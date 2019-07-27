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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.alfajob.Adapter.RVAdapterNewCV;
import com.example.alfajob.Adapter.RVAdapterSendToUser;
import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Objects.NewCV;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplyCVActivity extends AppCompatActivity implements OnItemClickListener {
    public String cvId, cvTitle, cvEmail, cvPhone, cvUrl, cvSkills, cvCommentCount;
    public Bundle extras;
    public List<User> listUsers;
    public RVAdapterSendToUser rvAdapter;
    public FirebaseFirestore db;

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
                btn_sendAll.setTextColor(getResources().getColor(R.color.colorWhite));
                ll_list_invisible.setVisibility(View.INVISIBLE);
            }
        });

        // list init
        listUsers = new ArrayList<>();
        rvAdapter = new RVAdapterSendToUser(ApplyCVActivity.this, listUsers);
        mRecyclerView.setAdapter(rvAdapter);
        rvAdapter.setClickListener(this);

        // db init
        db = FirebaseFirestore.getInstance();

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
            if(!et_addComment.getText().toString().trim().equals("")){
                cvCommentCount = "1";
            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View view, int position) {
        String cvUserId = listUsers.get(position).getUserId();
        switch(view.getId()) {
            case R.id.btn_applycv_send:
                Button btn_send = view.findViewById(R.id.btn_applycv_send);
                btn_send.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_send_clicked));
                btn_send.setTextColor(Color.parseColor("#000000"));
                btn_send.setText("Sent");
                saveappliedCV(cvUserId);

                break;
        }

        System.out.println(view.getId());
    }

    public void saveappliedCV(String userId){
        Map<String, String> mUid = new HashMap<>();
        mUid.put("userId", userId);

        Map<String, String> mCv = new HashMap<>();
        mCv.put("cvTitle", cvTitle);
        mCv.put("cvScills", cvSkills);
        mCv.put("cvEmail", cvEmail);
        mCv.put("cvPhone", cvPhone);
        mCv.put("cvUrl", cvUrl);
        mCv.put("cvCommentCount", cvCommentCount);
        db.collection("appliedcv")
                .document(cvId)
                .collection("userId")
                .document(userId)
                .set(mUid)
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
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        listUsers.clear();
                        for(DocumentSnapshot doc : task.getResult()){
                            User user = new User(doc.getId(),
                                    doc.getString("userName"),
                                    doc.getString("userEmail"),
                                    doc.getString("userPassword"));

                            listUsers.add(user);


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }

                });
    }
    public void callBack(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ApplyCVActivity.this);
        builder.setTitle("Save");
        builder.setMessage("Do you want to save changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> mCv = new HashMap<>();
                mCv.put("cvTitle", cvTitle);
                mCv.put("cvScills", cvSkills);
                mCv.put("cvEmail", cvEmail);
                mCv.put("cvPhone", cvPhone);
                mCv.put("cvUrl", cvUrl);
                mCv.put("cvCommentCount", cvCommentCount);
                mCv.put("cvStarCount", "0");

                db.collection("appliedcv")
                        .document(cvId)
                        .set(mCv)
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
                db.collection("newcv")
                        .document(cvId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("appliedcv")
                        .document(cvId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
}
