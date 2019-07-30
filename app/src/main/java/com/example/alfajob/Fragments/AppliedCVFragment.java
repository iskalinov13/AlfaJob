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
import com.example.alfajob.Adapter.RVAdapterAppliedCV;
import com.example.alfajob.Objects.AppliedCV;
import com.example.alfajob.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AppliedCVFragment extends Fragment {
    View v;
    private RecyclerView myrecyclerView;

    private List<AppliedCV> listAppliedCV = new ArrayList<>();

    DatabaseReference mDatabaseAppliedcv;
    RVAdapterAppliedCV recyclerViewAdapter;
    PullRefreshLayout pullRefreshLayout;
    ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_appliedcv, container, false);
        myrecyclerView = (RecyclerView) view.findViewById(R.id.appliedcv_recyclerview);
        myrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RVAdapterAppliedCV(getContext(),listAppliedCV);
        myrecyclerView.setAdapter(recyclerViewAdapter);
        pullRefreshLayout= (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayoutAppliedCV);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                        myrecyclerView.getRecycledViewPool().clear();
                        initializeData();

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
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading ...");
        pd.show();
        initializeData();
    }

    public void initializeData(){
        mDatabaseAppliedcv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAppliedCV.clear();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()){
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

                    recyclerViewAdapter = new RVAdapterAppliedCV(getContext(),listAppliedCV);
                    myrecyclerView.setAdapter(recyclerViewAdapter);
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
