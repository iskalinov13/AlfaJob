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
import com.example.alfajob.Adapter.RVAdapterNewCV;
import com.example.alfajob.Interface.JsonPlaceHolderApi;
import com.example.alfajob.Objects.NewCV;
import com.example.alfajob.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewCVFragment extends Fragment {
    private RecyclerView myrecyclerView;

    private List<NewCV> listNewCV = new ArrayList<>();
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    private DatabaseReference mDatabaseNewcv;
    private RVAdapterNewCV recyclerViewAdapter;
    private PullRefreshLayout pullRefreshLayout;
    private ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_newcv, container, false);
        myrecyclerView = (RecyclerView) view.findViewById(R.id.newcv_recyclerview);
        myrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RVAdapterNewCV(getContext(),listNewCV);
        myrecyclerView.setAdapter(recyclerViewAdapter);
        pullRefreshLayout= (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(false);
                        myrecyclerView.getRecycledViewPool().clear();
                        createPost();
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
        mDatabaseNewcv = FirebaseDatabase.getInstance().getReference("newcv");
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading ...");
        pd.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://script.google.com/macros/s/AKfycbxQPoyJydCAZZLnx0X2l4X2HSdxV0VylNwqkgww6v7Qu3_TX66f/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        createPost();
        initializeData();

    }

    private void createPost(){

        Call<Void> call  = jsonPlaceHolderApi.getPost();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    return;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void initializeData(){

        mDatabaseNewcv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listNewCV.clear();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()){
                    NewCV newCV = new NewCV(childSnap.getKey(),
                            childSnap.child("cvTitle").getValue().toString(),
                            childSnap.child("cvEmail").getValue().toString(),
                            childSnap.child("cvPhone").getValue().toString(),
                            childSnap.child("cvUrl").getValue().toString());
                    listNewCV.add(newCV);

                    recyclerViewAdapter = new RVAdapterNewCV(getContext(),listNewCV);
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
