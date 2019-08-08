package com.example.alfajob.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alfajob.Activities.CommentActivity;
import com.example.alfajob.Adapter.RVAdapterApprovedCV;
import com.example.alfajob.Adapter.RVAdapterStar;
import com.example.alfajob.Adapter.RVAdapterVacancy;
import com.example.alfajob.Interface.OnItemClickListener;
import com.example.alfajob.Objects.ApprovedCV;
import com.example.alfajob.Objects.ApprovedCV;
import com.example.alfajob.Objects.User;
import com.example.alfajob.Objects.Vacancy;
import com.example.alfajob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ApprovedFragment extends Fragment implements OnItemClickListener, ActivityCompat.OnRequestPermissionsResultCallback  {

    private RecyclerView recyclerView;
    private List<ApprovedCV> approvedCVList;
    private RVAdapterApprovedCV adapterApproved;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceApproved, mReferenceStars, mReferenceComments;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView tv_no_data;
    private Dialog dialogView;
    private static final int  REQUEST_CALL = 1;
    private boolean starClicked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_approved, container, false);

        //view
        tv_no_data = view.findViewById(R.id.tv_no_data_approvedcv);

        //DB
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceApproved = mDatabase.getReference("approved");
        mReferenceStars = mDatabase.getReference("Stars");
        mReferenceComments = mDatabase.getReference("Comments");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        approvedCVList= new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_approvedcv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterApproved = new RVAdapterApprovedCV(approvedCVList, getContext());
        recyclerView.setAdapter(adapterApproved);
        adapterApproved.setClickListener(this);

        initializeData();
        checkIfempty();

        return view;
    }
    private void checkIfempty(){
        if(approvedCVList.size()==0){
            recyclerView.setVisibility(View.INVISIBLE);
            tv_no_data.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeData(){
        mReferenceApproved.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                approvedCVList.add(dataSnapshot.getValue(ApprovedCV.class));
                adapterApproved.notifyDataSetChanged();
                checkIfempty();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ApprovedCV approvedCV = dataSnapshot.getValue(ApprovedCV.class);
                int index = getItemIndex(approvedCV);
                approvedCVList.set(index, approvedCV);
                adapterApproved.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                ApprovedCV approvedCV = dataSnapshot.getValue(ApprovedCV.class);
                int index = getItemIndex(approvedCV);
                approvedCVList.remove(index);
                adapterApproved.notifyItemRemoved(index);
                checkIfempty();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(ApprovedCV approvedCV){

        int index = -1;

        for(int i = 0; i < approvedCVList.size(); i++){
            if(approvedCVList.get(i).getCvId().equals(approvedCV.getCvId())){
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity() !=null){
            getActivity().setTitle("Approve CV");
        }

    }

    @Override
    public void onClick(View view, int position) {
        switch(view.getId()) {
            case R.id.iv_delete_approvedcv:
                removeApprovedCV(position);
                break;
            case R.id.btn_view_approvedcv:
                viewApprovedCV(position);
                break;
            case R.id.iv_email_approvedcv:
                emailCV(position);
                break;
            case R.id.iv_phone_approvedcv:
                phoneCV(position);
                break;
            case R.id.iv_comment_approvedcv:
                commentApprovedCV(position);
                break;
            case R.id.iv_star_approvedcv:
                starApprovedCV(position);
                break;
            case R.id.tv_starcount_approvedcv:
                seeStarsApprovedCV(position);
                break;
        }

    }

    private void seeStarsApprovedCV(int position){
        dialogView = new Dialog(getContext());
        dialogView.setContentView(R.layout.dialog_star);
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        List<User> listUsers = getUsersList(getUsersId(approvedCVList.get(position).getCvId()));

        RecyclerView recyclerViewStars = dialogView.findViewById(R.id.rv_stars);
        TextView tv_no_data = dialogView.findViewById(R.id.tv_star_no_data);
        Button btn_ok = dialogView.findViewById(R.id.btn_ok);

        RVAdapterStar rvAdapterStar = new RVAdapterStar(getContext(), listUsers);
        recyclerViewStars.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewStars.setAdapter(rvAdapterStar);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
            }
        });
        dialogView.show();
    }

    private List<User> getUsersList(final List<String> list){

        final List<User> listOfUsers = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(list.contains(user.getUserId())){
                        listOfUsers.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return listOfUsers;
    }
    private List<String> getUsersId(String cvId){

        final List<String> list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stars").child(cvId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return list;
    }
    private void starApprovedCV(final int position){

        starClicked = true;
        mReferenceStars.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(starClicked){
                    if(dataSnapshot.child(approvedCVList.get(position).getCvId()).hasChild(firebaseUser.getUid())){

                        mReferenceStars.child(approvedCVList.get(position).getCvId()).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                        starClicked = false;
                        int starCount = Integer.parseInt(approvedCVList.get(position).getCvStarCount());
                        if(starCount!=0){
                            starCount = starCount - 1;
                        }
                        approvedCVList.get(position).setCvStarCount(starCount+"");
//                        notifyItemChanged(position);
                        mReferenceApproved.child(approvedCVList.get(position).getCvId()).child("cvStarCount").setValue(starCount+"");

                    }
                    else{

                        mReferenceStars.child(approvedCVList.get(position).getCvId()).child(firebaseUser.getUid()).setValue(firebaseUser.getUid());
                        starClicked = false;
                        int starCount = Integer.parseInt(approvedCVList.get(position).getCvStarCount())+1;
                        approvedCVList.get(position).setCvStarCount(starCount+"");
                        // notifyItemChanged(viewHolder.getAdapterPosition());
                        mReferenceApproved.child(approvedCVList.get(position).getCvId()).child("cvStarCount").setValue(starCount+"");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void commentApprovedCV(int position){
        Intent intent = new Intent(getContext(), CommentActivity.class);
        intent.putExtra("cvId", approvedCVList.get(position).getCvId());
        intent.putExtra("userId", firebaseUser.getUid());
        intent.putExtra("fragmentName", "ApprovedCV");
        getContext().startActivity(intent);
    }

    private void phoneCV(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Call");
        builder.setMessage("Do you want to call to him/her ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makePhoneCall(approvedCVList.get(position).getCvUserPhone());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void makePhoneCall(String phonenumber){
        String phoneN = phonenumber;

        if(phoneN.trim().length()>0){
            if(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions((Activity)getContext(),
                        new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{

                String dial = "tel:"+phoneN;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{

            Toast.makeText(getContext(), "Empty phone", Toast.LENGTH_SHORT).show();
        }
    }

    private void emailCV(int position){

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{approvedCVList.get(position).getCvUserEmail()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, approvedCVList.get(position).getCvTitle());
        startActivity(Intent.createChooser(emailIntent, "Choose an Email Client ..."));
    }

    private void  removeApprovedCV(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this cv?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String cvId = approvedCVList.get(position).getCvId();
                deleteApprovedCVStars(position, cvId);
            }

        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void deleteApprovedCVStars(final int position, final String cvId){
        mReferenceStars.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(cvId)){
                    mReferenceStars.child(cvId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deleteApprovedCVComments(position, cvId);
                        }
                    });
                }
                else{
                    deleteApprovedCVComments(position, cvId);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteApprovedCVComments(final int position, final String cvId){
        mReferenceComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(cvId)){
                    mReferenceComments.child(cvId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mReferenceApproved.child(approvedCVList.get(position).getCvId()).removeValue();
                        }
                    });
                }
                else{
                    mReferenceApproved.child(approvedCVList.get(position).getCvId()).removeValue();
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void viewApprovedCV(int position){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(approvedCVList.get(position).getCvUrl()));
        startActivity(intent);
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
