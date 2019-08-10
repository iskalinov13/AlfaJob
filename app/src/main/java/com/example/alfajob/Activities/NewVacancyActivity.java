package com.example.alfajob.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfajob.Objects.User;
import com.example.alfajob.Objects.Vacancy;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewVacancyActivity extends AppCompatActivity {

    private EditText et_jobTitle, et_jobDescription;
    private Button btn_create, btn_cancel;
    private TextView tv_date;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceVacancy, mReferenceUsers;
    private String title, description, date, userId, userName, userPhoto;
    private String vId, uId, uName, photoUrl, vTitle, vDescription, vDate;
    private String hrId;
    private Bundle extras;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vacancy);

        // Action bat style
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorActionBar)));
        bar.setTitle("Cоздать вакансию");
        bar.setDisplayHomeAsUpEnabled(true);

        //View
        et_jobTitle = findViewById(R.id.et_newvacancy_jobtitle);
        et_jobDescription = findViewById(R.id.et_newvacancy_jobdescription);
        tv_date = findViewById(R.id.tv_date_newvacancy);
        btn_create = findViewById(R.id.btn_create_newvacancy);
        btn_cancel = findViewById(R.id.btn_cancel_newvacancy);

        //DB
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceVacancy = mDatabase.getReference("vacancy");
        mReferenceUsers = mDatabase.getReference("users");

        //Date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date d = new Date();
        tv_date.setText(formatter.format(d));

        if (savedInstanceState == null){
            extras = getIntent().getExtras();
            if(extras != null){
                vId = extras.getString("VACANCY ID");
                uId = extras.getString("USER ID");
                uName = extras.getString("USER NAME");
                photoUrl = "photo_url";
                vTitle = extras.getString("VACANCY TITLE");
                vDescription = extras.getString("VACANCY DESCRIPTION");
                vDate = extras.getString("VACANCY DATE");

                et_jobTitle.setText(vTitle);
                et_jobDescription.setText(vDescription);
                tv_date.setText(vDate);
            }

        }

        //Vacancy
        date  = tv_date.getText().toString().trim();
        userId = firebaseUser.getUid();
        userPhoto = "photo_url";
        mReferenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user  = dataSnapshot.getValue(User.class);
                userName = user.getUserName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Click Listener
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = et_jobTitle.getText().toString().trim();
                description = et_jobDescription.getText().toString().trim();
                if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(description)){
                    if(extras != null){
                        setVacancy(uId, uName, photoUrl, title, description, vDate, vId);
                    }
                    else{
                        createNewVacancy(title, description, date, userId, userName, userPhoto);
                    }
                }
                else{
                    Toast.makeText(NewVacancyActivity.this, "Title and Description must not be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        callBack();
    }

    public void callBack(){

        AlertDialog.Builder builder = new AlertDialog.Builder(NewVacancyActivity.this);
        builder.setTitle("Cancel");
        builder.setMessage("Do you want to cancel?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_jobDescription.setText("");
                et_jobTitle.setText("");
                dialog.dismiss();
                finish();
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

    private void setVacancy(String uId, String userName, String photoUrl, String vTitle, String vDescription, String vDate, String vId){
        Vacancy vacancy = new Vacancy(uId, userName, photoUrl, vTitle, vDescription, vDate, vId);
        mReferenceVacancy.child(uId).child(vId).setValue(vacancy);
        sendToHR(vId,vacancy);

        et_jobDescription.setText("");
        et_jobTitle.setText("");
        finish();
    }

    private void createNewVacancy(final String title, final String description, final String date,
                                  final String userId, String userName, String userPhoto){
        String vacancyId = mReferenceVacancy.push().getKey();
        Vacancy vacancy = new Vacancy(userId, userName, userPhoto, title, description, date, vacancyId);
        mReferenceVacancy.child(userId).child(vacancyId).setValue(vacancy);
        sendToHR(vacancyId, vacancy);

        et_jobDescription.setText("");
        et_jobTitle.setText("");
        finish();

    };

    private void sendToHR(final String vacancyId, final Vacancy vacancy){
        mReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    if(user.getUserEmail().equals("recruiteralfabank@gmail.com")){
                        hrId = user.getUserId();
                        mReferenceVacancy.child(hrId).child(vacancyId).setValue(vacancy);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
