package com.example.duan1bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.duan1bookapp.adapters.AdapterPdfViewsHistoryBooksAll;
import com.example.duan1bookapp.databinding.ActivityViewHistoryBooksAllBinding;
import com.example.duan1bookapp.models.ModelPdfViewsBooksAll;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewsHistoryBooksAll extends AppCompatActivity {
    private ActivityViewHistoryBooksAllBinding binding;
    //firebase auth,for leading user data using user uid
    private FirebaseAuth firebaseAuth;
    //reading books
    //arrayList to hold the books
    private ArrayList<ModelPdfViewsBooksAll> pdfArrayList;
    //adapter to set in recyclerView
    private AdapterPdfViewsHistoryBooksAll adapterPdfViewsHistoryBooksAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewHistoryBooksAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();

        loadReadingBooks();

        //handle click back
        binding.imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // called as and when user type any letter
                try {
                    adapterPdfViewsHistoryBooksAll.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("TAG", "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    private void loadReadingBooks(){
        pdfArrayList=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("ReadingBooks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            //we will only get the bookId here and we got other details in adapter using that bookId
                            String bookId=""+ds.child("bookId").getValue();
                            //set id to model
                            ModelPdfViewsBooksAll modelPdf=new ModelPdfViewsBooksAll();
                            modelPdf.setId(bookId);
                            //add model to list
                            pdfArrayList.add(modelPdf);
                        }
                        //set LinearLayout Manager
                        GridLayoutManager gridLayoutManager=new GridLayoutManager(ViewsHistoryBooksAll.this,2);
                        binding.booksRv.setLayoutManager(gridLayoutManager);

                        //setup adapter
                        adapterPdfViewsHistoryBooksAll=new AdapterPdfViewsHistoryBooksAll(ViewsHistoryBooksAll.this,pdfArrayList);
                        //set Adapter to recyclerView
                        binding.booksRv.setAdapter(adapterPdfViewsHistoryBooksAll);
//                        adapterPdfReadingBooks.loaddata(pdfArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}