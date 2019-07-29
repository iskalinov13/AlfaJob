package com.example.alfajob.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.alfajob.Objects.User;
import com.example.alfajob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class FragmentHome extends Fragment {

    FrameLayout newCV, appliedCV;
    View view1, view2;
    TextView tvnewCV, tvappliedCV;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_home = inflater.inflate(R.layout.fragment_home, container, false);

        //INIT VIEWS
        init(fragment_home);

        //SET TABS ONCLICK
        newCV.setOnClickListener(clik);
        appliedCV.setOnClickListener(clik);

        //LOAD PAGE FOR FIRST
        loadPage(new NewCVFragment());
        tvnewCV.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

        mAuth = FirebaseAuth.getInstance();

        return fragment_home;
    }

    public void init(View v){
        newCV = v.findViewById(R.id.newCV);
        appliedCV = v.findViewById(R.id.appliedCV);
        view1 = v.findViewById(R.id.view1);
        view2 = v.findViewById(R.id.view2);
        tvnewCV = v.findViewById(R.id.tvnewCV);
        tvappliedCV = v.findViewById(R.id.tvappliedCV);
    }

    //ONCLICK LISTENER
    public View.OnClickListener clik = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.newCV:
                    //ONSELLER CLICK
                    //LOAD SELLER FRAGMENT CLASS
                    loadPage(new NewCVFragment());

                    //WHEN CLICK TEXT COLOR CHANGED
                    tvnewCV.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    tvappliedCV.setTextColor(getActivity().getResources().getColor(R.color.grey));

                    //VIEW VISIBILITY WHEN CLICKED
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.INVISIBLE);
                    break;
                case R.id.appliedCV:
                    //ONBUYER CLICK
                    //LOAD BUYER FRAGMENT CLASS
                    loadPage(new AppliedCVFragment());

                    //WHEN CLICK TEXT COLOR CHANGED
                    tvnewCV.setTextColor(getActivity().getResources().getColor(R.color.grey));
                    tvappliedCV.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

                    //VIEW VISIBILITY WHEN CLICKED
                    view1.setVisibility(View.INVISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    //LOAD PAGE FRAGMENT METHOD
    private boolean loadPage(Fragment fragment) {

        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerpage, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail().toString();

        if (userEmail.equals("recruiteralfabank@gmail.com")) {

        }




    }
}
