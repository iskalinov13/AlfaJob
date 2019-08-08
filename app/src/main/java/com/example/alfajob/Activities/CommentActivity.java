package com.example.alfajob.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfajob.Adapter.RVAdapterComment;
import com.example.alfajob.Objects.Comment;
import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    EditText et_addComment;
    CircleImageView civ_profile;
    TextView tv_post;

    String cvId, userId, fragmentName;

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseComments, mDatabaseUsers, mDatabaseAppliedcv, mDatabaseApproved;

    RecyclerView mRecyclerView;
    public List<Comment> listComments;
    public RVAdapterComment rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //init rv,adapter
        mRecyclerView = findViewById(R.id.rv_comment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // list init
        listComments = new ArrayList<>();
        rvAdapter = new RVAdapterComment(CommentActivity.this, listComments);
        mRecyclerView.setAdapter(rvAdapter);
        //rvAdapter.setClickListener(this);

        //Init views
        et_addComment = findViewById(R.id.et_addcomment_comment);
        civ_profile = findViewById(R.id.civ_applycv_profile);
        tv_post = findViewById(R.id.tv_post_comment);

        //init Intent and get extras
        Intent intent= getIntent();
        cvId = intent.getStringExtra("cvId");
        userId = intent.getStringExtra("userId");
        fragmentName = intent.getStringExtra("fragmentName");

        //init db, user
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mDatabaseAppliedcv = FirebaseDatabase.getInstance().getReference("appliedcv");
        mDatabaseComments = FirebaseDatabase.getInstance().getReference("comments").child(cvId);
        mDatabaseApproved = FirebaseDatabase.getInstance().getReference("approved");

        // Action bat style
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorActionBar)));
        bar.setTitle("Comment");
        bar.setDisplayHomeAsUpEnabled(true);
        
        //Onclick
        tv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_addComment.getText().toString().equals("")){
                    Toast.makeText(CommentActivity.this, "You can't send emty comment.", Toast.LENGTH_SHORT).show();
                }
                else{
                    addComment(fragmentName);
                }
            }
        });

        initializeComments();
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
        super.onBackPressed();
    }

    public void initializeComments(){

        mDatabaseComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComments.clear();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                    Comment comment = childSnap.getValue(Comment.class);
                    listComments.add(comment);
                    System.out.println(comment.getComment());
                }

                rvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addComment(final String fragmentName){

        mDatabaseComments = FirebaseDatabase.getInstance().getReference("comments").child(cvId);
        String commentId = mDatabaseComments.push().getKey();
        String comment = et_addComment.getText().toString();
        String userId = firebaseUser.getUid();

        Comment c = new Comment(commentId, comment, userId);
        mDatabaseComments.child(commentId).setValue(c);
        et_addComment.setText("");

        mDatabaseComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(fragmentName.equals("AppliedCV")){
                    mDatabaseAppliedcv.child(cvId).child("cvCommentCount").setValue(dataSnapshot.getChildrenCount()+"");
                }
                else if(fragmentName.equals("ApprovedCV")){
                    mDatabaseApproved.child(cvId).child("cvCommentCount").setValue(dataSnapshot.getChildrenCount()+"");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getImage(){

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                //Glide.vith(getApplicationContext()).load(user.getImageUrl()).into(civ_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
