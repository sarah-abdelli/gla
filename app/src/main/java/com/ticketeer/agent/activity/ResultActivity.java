package com.ticketeer.agent.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ticketeer.agent.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvIcone      = findViewById(R.id.tv_icone);
        TextView tvResultat   = findViewById(R.id.tv_resultat);
        TextView tvMotif      = findViewById(R.id.tv_motif);
        TextView tvUuid       = findViewById(R.id.tv_uuid);
        TextView tvTrain      = findViewById(R.id.tv_train);
        LinearLayout layoutHeader = findViewById(R.id.layout_header);
        Button btnNouveauScan = findViewById(R.id.btn_nouveau_scan);

        String resultat    = getIntent().getStringExtra("resultat");
        String uuid        = getIntent().getStringExtra("uuid");
        String motif       = getIntent().getStringExtra("motif");
        String numeroTrain = getIntent().getStringExtra("numeroTrain");

        tvUuid.setText(uuid != null ? uuid : "");
        tvTrain.setText(numeroTrain != null ? numeroTrain : "");

        if ("ACCEPTEE".equals(resultat)) {
            tvIcone.setText("✅");
            tvResultat.setText("BILLET VALIDE");
            tvResultat.setTextColor(Color.parseColor("#15803D"));
            tvMotif.setText("");
            layoutHeader.setBackgroundColor(Color.parseColor("#F0FDF4"));
        } else {
            tvIcone.setText("❌");
            tvResultat.setText("BILLET REFUSÉ");
            tvResultat.setTextColor(Color.parseColor("#DC2626"));
            tvMotif.setText(motif != null ? motif : "");
            tvMotif.setTextColor(Color.parseColor("#EF4444"));
            layoutHeader.setBackgroundColor(Color.parseColor("#FEF2F2"));
        }

        btnNouveauScan.setOnClickListener(v -> {
            startActivity(new Intent(this, ScanActivity.class));
            finish();
        });
    }
}