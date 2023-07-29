package com.example.duan1bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1bookapp.MyApplication;
import com.example.duan1bookapp.activities.PdfDetailActivity;
import com.example.duan1bookapp.databinding.RowPdfUser2Binding;
import com.example.duan1bookapp.databinding.RowPdfUserBinding;
import com.example.duan1bookapp.filters.FilterPdfUser;
import com.example.duan1bookapp.filters.FilterPdfUser2;
import com.example.duan1bookapp.models.ModelPdf;
import com.example.duan1bookapp.models.ModelPdf2;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUser2 extends RecyclerView.Adapter<AdapterPdfUser2.HolderPdfUser> implements Filterable {

    private Context context;
    public ArrayList<ModelPdf2> pdfArrayList, filterList;

    private FilterPdfUser2 filter;

    private RowPdfUser2Binding binding;

    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfUser2(Context context, ArrayList<ModelPdf2> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowPdfUser2Binding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfUser2.HolderPdfUser holder, int position) {


        ModelPdf2 model = pdfArrayList.get(position);
        String bookId = model.getId();
        String title = model.getTitle();
        String description = model.getDescription();;
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        long timestamp = model.getTimestamp();

        // convert time
        String date = MyApplication.formatTimestamp(timestamp);

        // set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
//        holder.dateTv.setText(date);

        // we dont need page number here,pass null
        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                holder.pdfView,
                holder.progressBar,
                null
        );
        MyApplication.loadCategory(
                "" + categoryId,
                holder.categoryTv
        );
        MyApplication.loadPdfSize(
                "" + pdfUrl,
                "" + title,
                holder.sizeTv
        );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size(); // return list size || number of records
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPdfUser2(filterList, this);
        }
        return filter;
    }

    class HolderPdfUser extends RecyclerView.ViewHolder {

        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        PDFView pdfView;
        ProgressBar progressBar;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);

            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
//            categoryTv = binding.categoryTv;
//            sizeTv = binding.sizeTv;
//            dateTv = binding.dateTv;

            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
        }
    }
}
