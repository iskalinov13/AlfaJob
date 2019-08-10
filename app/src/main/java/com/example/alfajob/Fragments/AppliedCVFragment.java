package com.example.alfajob.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
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
    private RecyclerView myrecyclerView;
    private List<AppliedCV> listAppliedCV = new ArrayList<>();

    private DatabaseReference mDatabaseAppliedcv;
    private RVAdapterAppliedCV recyclerViewAdapter;
    private PullRefreshLayout pullRefreshLayout;
    private ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_appliedcv, container, false);
        myrecyclerView = view.findViewById(R.id.appliedcv_recyclerview);
        myrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RVAdapterAppliedCV(getContext(),listAppliedCV);
        myrecyclerView.setAdapter(recyclerViewAdapter);
        pullRefreshLayout= view.findViewById(R.id.swipeRefreshLayoutAppliedCV);
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
        setHasOptionsMenu(true);
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

    private void initializeData(){
        mDatabaseAppliedcv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAppliedCV.clear();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()){
                    if(childSnap.child("cvTitle").getValue(String.class) != null){
                        AppliedCV appliedCV;
                        appliedCV = new AppliedCV(childSnap.getKey(),
                                childSnap.child("cvTitle").getValue(String.class),
                                childSnap.child("cvSkills").getValue(String.class),
                                childSnap.child("cvEmail").getValue(String.class),
                                childSnap.child("cvPhone").getValue(String.class),
                                childSnap.child("cvUrl").getValue(String.class),
                                childSnap.child("cvStarCount").getValue(String.class),
                                childSnap.child("cvCommentCount").getValue(String.class),
                                childSnap.child("cvStatus").getValue(String.class));
                        listAppliedCV.add(appliedCV);
                    }
                    else{
                        childSnap.getRef().removeValue();
                    }
                }
                recyclerViewAdapter = new RVAdapterAppliedCV(getContext(),listAppliedCV);
                myrecyclerView.setAdapter(recyclerViewAdapter);
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search){
            //TODO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu) ;
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void search(String text){
        final String s =text.toLowerCase();
        mDatabaseAppliedcv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAppliedCV.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if ( dataSnapshot1.child("cvSkills").getValue().toString().toLowerCase().contains(s) || (dataSnapshot1.child("cvTitle").getValue().toString().toLowerCase().contains(s))){
                        AppliedCV appliedCV;
                        appliedCV = new AppliedCV(dataSnapshot1.getKey(),
                                dataSnapshot1.child("cvTitle").getValue(String.class),
                                dataSnapshot1.child("cvSkills").getValue(String.class),
                                dataSnapshot1.child("cvEmail").getValue(String.class),
                                dataSnapshot1.child("cvPhone").getValue(String.class),
                                dataSnapshot1.child("cvUrl").getValue(String.class),
                                dataSnapshot1.child("cvStarCount").getValue(String.class),
                                dataSnapshot1.child("cvCommentCount").getValue(String.class),
                                dataSnapshot1.child("cvStatus").getValue(String.class));
                        listAppliedCV.add(appliedCV);

                        recyclerViewAdapter = new RVAdapterAppliedCV(getContext(),listAppliedCV);
                        myrecyclerView.setAdapter(recyclerViewAdapter);

                    }

                }
                recyclerViewAdapter = new RVAdapterAppliedCV(getContext(),listAppliedCV);
                myrecyclerView.setAdapter(recyclerViewAdapter);
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
