package com.example.uniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.uniapp.R;
import com.github.barteksc.pdfviewer.PDFView;

public class biopdf extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biopdf);

        PDFView bioPDFview = findViewById(R.id.pdfBio);
        bioPDFview.fromAsset("EBTY183L.pdf").load();
    }
}
