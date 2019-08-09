package com.example.alfajob.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.alfajob.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class WelcomeActivity extends AppCompatActivity {
    private CircleImageView imageView;
    private Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        imageView = findViewById(R.id.civ_splash);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        final Intent intent = new Intent(this, HomeActivity.class);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imageView.startAnimation(animation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setLayerType(View.LAYER_TYPE_NONE, null);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animation);
    }
}
