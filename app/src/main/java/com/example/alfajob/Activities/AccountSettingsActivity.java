package com.example.alfajob.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private static int REQUESCODE = 1;
    private CircleImageView ImgUserPhoto;
    static int PReqCode=1;
    private Uri pickedImgUri;
    private EditText ev_userName, ev_userEmail, ev_password, ev_confirm_password;
    private TextView tv_username;
    private DatabaseReference mReferenceUsers;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userName, userEmail, oldPassword, newPassword, newPasswordConfirmed;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // DB
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference("users").child(firebaseUser.getUid());

        // Action bat style
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorActionBar)));
        bar.setTitle("Настройки аккаунта");
        bar.setDisplayHomeAsUpEnabled(true);

        tv_username = findViewById(R.id.tv_settings_name);
        ev_userName = findViewById(R.id.et_settings_username);
        ev_userEmail = findViewById(R.id.et_settings_email);
        ev_userEmail.setEnabled(false);
        ev_password = findViewById(R.id.et_settings_newpassword);
        ev_confirm_password = findViewById(R.id.et_settings_newpassword);
        btn_save = findViewById(R.id.btn_settings_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = ev_userName.getText().toString().trim();
                newPassword = ev_password.getText().toString().trim();
                newPasswordConfirmed = ev_confirm_password.getText().toString().trim();
                if(checkUserDetails(userName, newPassword, newPasswordConfirmed)){
                    change_password(newPassword,userName);
                }
            }
        });

        // Set values
        mReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tv_username.setText(user.getUserName());
                ev_userName.setText(user.getUserName());
                ev_userEmail.setText(user.getUserEmail());
                userEmail = user.getUserEmail();
                oldPassword = user.getUserPassword();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImgUserPhoto = findViewById(R.id.civ_settings_imageView);
        ImgUserPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();

                }else{
                    openGallery();
                }
            }

        });
    }
    private void change_password(final String newPass, final String userName){
        AuthCredential credential = EmailAuthProvider.getCredential(userEmail,oldPassword);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(AccountSettingsActivity.this, "Что-то пошло не так. Пожалуйста, попробуйте позже.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mReferenceUsers.child("userPassword").setValue(newPass);
                                mReferenceUsers.child("userName").setValue(userName);
                                Toast.makeText(AccountSettingsActivity.this, "Пароль успешно изменен", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                sentToLoginActivity();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(AccountSettingsActivity.this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void openGallery() {
        Intent galleryInent= new Intent(Intent.ACTION_GET_CONTENT);
        galleryInent.setType("image/*");
        startActivityForResult(galleryInent,REQUESCODE);
    }
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AccountSettingsActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(AccountSettingsActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(AccountSettingsActivity.this,"Пожалуйста, примите для получения необходимого разрешения", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(AccountSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }else{
            openGallery();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==REQUESCODE && data != null){
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    private boolean checkUserDetails(String userName, String newPassword, String newPasswordConfirmed){

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(newPassword)&& !TextUtils.isEmpty(newPasswordConfirmed)) {

            if(newPassword.equals(newPasswordConfirmed)){

                if(newPassword.length()>=6){
                    return true;
                }
                else{
                    Toast.makeText(this, "Пароль должен содержать не менее 6 символов",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else{
                Toast.makeText(this, "Пароль и подтверждение пароля не совпадают", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Toast.makeText(this, "Имя пользователя / адрес электронной почты / пароль не должны быть пустыми", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void sentToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

}
