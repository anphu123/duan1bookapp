package com.example.duan1bookapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.duan1bookapp.BooksUserFragment;
import com.example.duan1bookapp.BooksUserFragment2;
import com.example.duan1bookapp.activities.AllBooksActivity;

import com.example.duan1bookapp.activities.DashboardAdminActivity;
import com.example.duan1bookapp.adapters.AdapterCategory;
import com.example.duan1bookapp.adapters.AdapterPdfUser;
import com.example.duan1bookapp.adapters.AdapterPdfUser2;
import com.example.duan1bookapp.databinding.FragmentHomeBinding;
import com.example.duan1bookapp.models.ModelCategory;

import com.example.duan1bookapp.models.ModelPdf;
import com.example.duan1bookapp.models.ModelPdf2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment_Home extends Fragment{
    // view biding
    private FragmentHomeBinding binding;

    private static final String TAG = "BOOKS_USER_TAG";

    //to show in tabs
    public ArrayList<ModelCategory> categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;
    AdapterPdfUser adapterPdfUser;

    private ArrayList<ModelPdf> pdfArrayList;




    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(LayoutInflater.from(getContext()),container,false);


        setupViewPagerAdapter(binding.viewpager);
        binding.tabLayout.setupWithViewPager(binding.viewpager);

        loadMostViewedDownloadedBooks("viewsCount");
        loadAllBooks();

        //search user
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // called as and when user type any letter
                try {
                    adapterPdfUser.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //handle click all Books
        binding.tvAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllBooksActivity.class));
            }
        });
        return binding.getRoot();
    }
        private void setupViewPagerAdapter(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, getContext());
        categoryArrayList = new ArrayList<>();




        // load categories from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories"); // be careful of spelling
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                // clear before adding to list
                categoryArrayList.clear();

                ModelCategory modelAll = new ModelCategory("01","All","",1);

                //add models to list
                categoryArrayList.add(modelAll);

                viewPagerAdapter.addFragment(BooksUserFragment2.newInstance(
                        "" + modelAll.getId(),
                        "" + modelAll.getCategory(),
                        "" + modelAll.getUid()
                ), modelAll.getCategory());

                // Now Load from firebase
                for (DataSnapshot ds: snapshot.getChildren()) {
                    // get data
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    // add data to list
                    categoryArrayList.add(model);
                    // add data to viewPagerAdapter
                    viewPagerAdapter.addFragment(BooksUserFragment2.newInstance(
                            "" + model.getId(),
                            "" + model.getCategory(),
                            "" + model.getUid()), model.getCategory());
                    // refresh list
                    viewPagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        // set adapter to view pager
        viewPager.setAdapter(viewPagerAdapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<BooksUserFragment2> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter( FragmentManager fm, int behavior,Context context) {
            super(fm, behavior);
            this.context =context;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        private void addFragment(BooksUserFragment2 fragment, String title){
            // add fragment passed as parameter in fragmentList
            fragmentList.add(fragment);
            // add title passed as parameter in fragmentTitleList
            fragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    private void loadMostViewedDownloadedBooks(String oderBy) {
        // inti list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(oderBy).limitToLast(10) // load 10 most viewed or downloaded books
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            // get data
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            // add to list
                            pdfArrayList.add(model);
                        }
                        //set layout recycler
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        binding.booksRv.setLayoutManager(linearLayoutManager);
                        // setup adapter
                        adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                        // set adapter to recyclerview
                        binding.booksRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



    private void loadAllBooks() {
        // inti list
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear list before starting adding data into it
                pdfArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // get data
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    // add to list
                    pdfArrayList.add(model);
                }
                //set layout recycler
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                binding.booksRv.setLayoutManager(linearLayoutManager);
                // setup adapter
                adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
                // set adapter to recyclerview
                binding.booksRv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
