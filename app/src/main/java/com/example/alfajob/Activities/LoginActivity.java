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

import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private EditText editEmail, editPassword;
    private Button btnLogIn;
    private TextView textViewSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editEmail = (EditText) findViewById(R.id.login_editText_email);
        editPassword = (EditText) findViewById(R.id.login_editText_password);
        btnLogIn = (Button) findViewById(R.id.login_button_login);
        textViewSignUp = (TextView) findViewById(R.id.login_textView_register);

        btnLogIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null&& currentUser.isEmailVerified()) {
            sendToHomeActivity();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btnLogIn){

            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if(user != null){

                            if (user.isEmailVerified()) {

                                if (task.isSuccessful()) {

                                    sendToHomeActivity();

                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed."  , Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Verify your email or you haven't yet registered" , Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "You haven't yet registered" , Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
            else {
                Toast.makeText(this, "Empty email and password", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v==textViewSignUp){

            startActivity(new Intent(this, RegistrationActivity.class));
            overridePendingTransition(0, 0);
        }
    }

    private void sendToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}
