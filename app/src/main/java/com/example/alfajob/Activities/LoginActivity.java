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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private EditText editEmail, editPassword;
    private Button btnLogIn;
    private TextView textViewSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.login_editText_email);
        editPassword = findViewById(R.id.login_editText_password);
        btnLogIn = findViewById(R.id.login_button_login);
        textViewSignUp = findViewById(R.id.login_textView_register);

        btnLogIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

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
                                    Toast.makeText(LoginActivity.this, "Ошибка аутентификации."  , Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Подтвердите адрес электронной почты или вы еще не зарегистрировались" , Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Вы еще не зарегистрированы" , Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Адрес электронной почты/пароль не должны быть пустыми.", Toast.LENGTH_SHORT).show();
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
