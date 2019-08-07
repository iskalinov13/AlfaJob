package com.example.alfajob.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.example.alfajob.Adapter.RVAdapterHomeUser;
import com.example.alfajob.Objects.AppliedCV;
import com.example.alfajob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentHomeUser extends Fragment {
    private RecyclerView myrecyclerView;

    private List<AppliedCV> listAppliedCV = new ArrayList<>();
    private List<String> listOfCVId = new ArrayList<>();
    private DatabaseReference mDatabaseAppliedcv, mDatabaseSend;
    private  RVAdapterHomeUser recyclerViewAdapter;
    private PullRefreshLayout pullRefreshLayout;
    private ProgressDialog pd;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private Boolean isStarted = false;
    private Boolean isVisible = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);
        myrecyclerView = view.findViewById(R.id.homeuser_recyclerview);
        recyclerViewAdapter = new RVAdapterHomeUser(getContext(),listAppliedCV);
        myrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myrecyclerView.setAdapter(recyclerViewAdapter);

        pullRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutHomeuser);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                        myrecyclerView.getRecycledViewPool().clear();
                        initializeData();
                        System.out.println(listAppliedCV.size());

                    }
                }, 3000);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseAppliedcv = FirebaseDatabase.getInstance().getReference("appliedcv");
        mDatabaseSend = FirebaseDatabase.getInstance().getReference("send");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading ...");
        pd.show();
        initializeData();
    }


    private void  initializeData(){
        mDatabaseSend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfCVId.clear();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()){
                    String cvId = childSnap.getKey();
                    if(childSnap.hasChild(firebaseUser.getUid())){
                        listOfCVId.add(cvId);
                        System.out.println(cvId);
                    }
                }
                retrieveData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveData(){
        mDatabaseAppliedcv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAppliedCV.clear();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()){
                    if(listOfCVId.contains(childSnap.getKey())){
                        AppliedCV appliedCV;
                        appliedCV = new AppliedCV(childSnap.getKey(),
                                childSnap.child("cvTitle").getValue(String.class),
                                childSnap.child("cvSkills").getValue(String.class),
                                childSnap.child("cvEmail").getValue(String.class),
                                childSnap.child("cvPhone").getValue(String.class),
                                childSnap.child("cvUrl").getValue(String.class),
                                childSnap.child("cvStarCount").getValue(String.class),
                                childSnap.child("cvCommentCount").getValue(String.class));

                        listAppliedCV.add(appliedCV);
                        recyclerViewAdapter = new RVAdapterHomeUser(getContext(),listAppliedCV);
                        myrecyclerView.setAdapter(recyclerViewAdapter);
                    }
                }
                recyclerViewAdapter = new RVAdapterHomeUser(getContext(),listAppliedCV);
                myrecyclerView.setAdapter(recyclerViewAdapter);
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyDataSetChanged();
    }


}
