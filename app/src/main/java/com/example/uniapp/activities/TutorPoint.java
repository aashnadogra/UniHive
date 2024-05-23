package com.example.uniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.uniapp.R;
import com.github.barteksc.pdfviewer.PDFView;

public class TutorPoint extends AppCompatActivity {

    TextView daa, cn, os, cyber, bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_point);

        daa = findViewById(R.id.daa);
        cn = findViewById(R.id.cn);
        os = findViewById(R.id.os);
        bio = findViewById(R.id.bio);
        cyber = findViewById(R.id.cyber);
        bio.setOnClickListener(view -> {
            setContentView(R.layout.activity_biopdf);

            PDFView bioPDFview = findViewById(R.id.pdfBio);
            bioPDFview.fromAsset("EBTY183L.pdf").load();

        });

    }
}