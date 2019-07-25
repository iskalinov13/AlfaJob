package com.example.alfajob.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.example.alfajob.Adapter.RecycleViewAdapterNewCV;
import com.example.alfajob.Objects.NewCV;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewCVFragment extends Fragment {
    View v;
    private RecyclerView myrecyclerView;

    private List<NewCV> listNewCV = new ArrayList<>();

    FirebaseFirestore db;
    RecycleViewAdapterNewCV recyclerViewAdapter;
    PullRefreshLayout pullRefreshLayout;
    ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_newcv, container, false);
        myrecyclerView = (RecyclerView) view.findViewById(R.id.newcv_recyclerview);
        myrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pullRefreshLayout= (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
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
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading ...");
        pd.show();
        initializeData();




    }

    public void initializeData(){
        db.collection("newcv")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        listNewCV.clear();
                        for(DocumentSnapshot doc : task.getResult()){
                            NewCV newCV = new NewCV(doc.getId(),
                                    doc.getString("cvTitle"),
                                    doc.getString("cvEmail"),
                                    doc.getString("cvPhone"),
                                    doc.getString("cvUrl"));

                            listNewCV.add(newCV);

                            recyclerViewAdapter = new RecycleViewAdapterNewCV(getContext(),listNewCV);
                            myrecyclerView.setAdapter(recyclerViewAdapter);
                        }
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }

                });
    }

    @Override
    public void onStart() {
        super.onStart();

    }




}
