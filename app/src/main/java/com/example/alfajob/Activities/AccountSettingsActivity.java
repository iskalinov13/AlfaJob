package com.example.alfajob.Activities;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.alfajob.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private static int REQUESCODE = 1;
    private CircleImageView ImgUserPhoto;
    static int PReqCode=1;
    private Uri pickedImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Action bat style
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorActionBar)));
        bar.setTitle("Account Settings");
        bar.setDisplayHomeAsUpEnabled(true);

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
    private void openGallery() {
        Intent galleryInent= new Intent(Intent.ACTION_GET_CONTENT);
        galleryInent.setType("image/*");
        startActivityForResult(galleryInent,REQUESCODE);
    }
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AccountSettingsActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(AccountSettingsActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(AccountSettingsActivity.this,"Plase accept for required permission", Toast.LENGTH_SHORT).show();
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

}
