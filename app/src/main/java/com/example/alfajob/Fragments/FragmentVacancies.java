package com.example.alfajob.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Adapter.RVAdapterVacancy;
import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Objects.Vacancy;
import com.example.alfajob.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FragmentVacancies extends Fragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private List<Vacancy> vacancyList;
    private RVAdapterVacancy adapterVacancy;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceVacancy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_vacancies, container, false);

        //DB
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceVacancy = mDatabase.getReference("vacancy");

        vacancyList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_vacancy);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterVacancy = new RVAdapterVacancy(vacancyList, getContext());
        recyclerView.setAdapter(adapterVacancy);
        adapterVacancy.setClickListener(this);

        initializeData();

        return view;
    }

    private void initializeData(){
        mReferenceVacancy.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                vacancyList.add(dataSnapshot.getValue(Vacancy.class));
                adapterVacancy.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Vacancy vacancy = dataSnapshot.getValue(Vacancy.class);
                int index = getItemIndex(vacancy);
                vacancyList.set(index, vacancy);
                adapterVacancy.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Vacancy vacancy = dataSnapshot.getValue(Vacancy.class);
                int index = getItemIndex(vacancy);
                vacancyList.remove(index);
                adapterVacancy.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(Vacancy vacancy){

        int index = -1;

        for(int i = 0; i < vacancyList.size(); i++){
            if(vacancyList.get(i).getVacancyId().equals(vacancy.getVacancyId())){
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Vacancies");
    }

    @Override
    public void onClick(View view, int position) {
        switch(view.getId()) {
            case R.id.iv_delete_vacancy:
                removeVacancy(position);
                break;
            case R.id.btn_view_vacancy:
                viewVacancy(position);
                break;
        }

    }

    private void  removeVacancy(int position){
        mReferenceVacancy.child(vacancyList.get(position).getVacancyId()).removeValue();
    }

    private void viewVacancy(int position){
        System.out.println(vacancyList.get(position).getVacancyDescription());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search){
            //TODO
            System.out.println("hello world");
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
                //firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //firebaseSearch(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
