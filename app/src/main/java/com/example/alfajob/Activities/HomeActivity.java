package com.example.alfajob.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.alfajob.Fragments.ApprovedFragment;
import com.example.alfajob.Fragments.FragmentHome;
import com.example.alfajob.Fragments.FragmentHomeUser;
import com.example.alfajob.Fragments.FragmentVacancies;
import com.example.alfajob.Fragments.NewCVFragment;
import com.example.alfajob.Notifications.Token;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.widget.TextView;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvNavHeaderUsername, tvNavHeaderEmail;
    private MaterialSearchView searchView;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser currentUser;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        fab = findViewById(R.id.fab);
        final Intent intentVacancy =  new Intent(this, NewVacancyActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(intentVacancy);

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        tvNavHeaderUsername = (TextView) headerView.findViewById(R.id.tv_nav_header_username);
        tvNavHeaderEmail = (TextView) headerView.findViewById(R.id.tv_nav_header_email);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        Intent intent = getIntent();

        Fragment fragment = null;
        if(currentUser!=null){
            updateToken(FirebaseInstanceId.getInstance().getToken());
            if(currentUser.getEmail().toString().equals("recruiteralfabank@gmail.com")){
                fab.hide();
                fragment = new FragmentHome();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
            else{
                fab.show();
                fragment = new FragmentHomeUser();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        }
        else{
            sentToLoginActivity();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null) {

            final String userEmail = currentUser.getEmail().toString();
            tvNavHeaderEmail.setText(userEmail);
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        User user = ds.getValue(User.class);

                        if (user.getUserEmail().equals(tvNavHeaderEmail.getText().toString())){

                            tvNavHeaderUsername.setText(user.getUserName());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            sentToLoginActivity();
        }


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        //searchView.setMenuItem(item);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //creating fragment object
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle(item.getTitle());

            if(currentUser.getEmail().toString().equals("recruiteralfabank@gmail.com")){
                fragment = new FragmentHome();

            }
            else{
                fragment = new FragmentHomeUser();
                fab.show();

            }

        } else if (id == R.id.nav_vacancy) {
            getSupportActionBar().setTitle(item.getTitle());
            fragment = new FragmentVacancies();
            fab.hide();

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, AccountSettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_aproved_resumes) {
            getSupportActionBar().setTitle(item.getTitle());
            fragment = new ApprovedFragment();
            fab.hide();

        } else if (id == R.id.nav_sign_out) {
            getSupportActionBar().setTitle(item.getTitle());
            mAuth.signOut();
            sentToLoginActivity();
        }
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void sentToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void updateToken(String token){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token t= new Token(token);
        reference.child(firebaseUser.getUid()).setValue(t);
    }
}
