package com.example.alfajob.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener  {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mDatabase;


    private EditText editUserName, editEmail, editPassword, editConfirmPassword;
    private Button btnSignUp;
    private TextView textLogIn;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        editUserName = (EditText) findViewById(R.id.reg_editText_username);
        editEmail = (EditText) findViewById(R.id.reg_editText_email);
        editPassword = (EditText) findViewById(R.id.reg_editText_password);
        editConfirmPassword = (EditText) findViewById(R.id.reg_editText_confirm_password);
        btnSignUp = (Button) findViewById(R.id.button_signup);
        textLogIn = (TextView) findViewById(R.id.textView_signin);

        btnSignUp.setOnClickListener(this);
        textLogIn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = Objects.requireNonNull(currentUser).getUid();
        }
    }

    @Override
    public void onClick(View v) {

        if(v==btnSignUp) {
            final String userName = editUserName.getText().toString();
            final String userEmail = editEmail.getText().toString();
            final String userPassword = editPassword.getText().toString();
            final String userConfirmPassword = editConfirmPassword.getText().toString();

            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userEmail)&& !TextUtils.isEmpty(userPassword)&& !TextUtils.isEmpty(userConfirmPassword)) {
                if(userPassword.equals(userConfirmPassword)){
                    if(userPassword.length()>=6){
                        mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {


                                           // String userID = mDatabase.push().getKey();
                                            String userID = mAuth.getCurrentUser().getUid();
                                            User user = new User(userId ,userName, userEmail, userPassword);
                                            mDatabase.child(userID).setValue(user);

                                            FirebaseUser fireuser = mAuth.getCurrentUser();
                                            fireuser.sendEmailVerification();
                                            sendToLoginActivity();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        // ...
                                    }
                                });
                    }
                    else{
                        Toast.makeText(RegistrationActivity.this, "Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                    }


                }
                else{
                    Toast.makeText(this, "Password and confirm password mismatched", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Username/email/password  shoud not be empty", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v==textLogIn){
            sendToLoginActivity();
        }
    }

    private void sendToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}
