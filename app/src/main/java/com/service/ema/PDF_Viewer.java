package com.service.ema;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.util.Objects;

public class PDF_Viewer extends AppCompatActivity {

    Toolbar toolbar;
    String toolbarTitle;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        Bundle bundle=getIntent().getExtras();
        String title=bundle.getString("Title");
        String pdf_Path=bundle.getString("PDF_Path");


        toolbar=findViewById(R.id.pdf_viewer_toolbar);
        pdfView=findViewById(R.id.system_pdf_view);

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pdfView.fromAsset(pdf_Path)
                .pages(0,2,1,3,3,3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(0)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();

    }
}