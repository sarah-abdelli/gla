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

        TextView tvResultat = findViewById(R.id.tv_resultat);
        TextView tvUuid = findViewById(R.id.tv_uuid);
        TextView tvMotif = findViewById(R.id.tv_motif);
        Button btnNouveauScan = findViewById(R.id.btn_nouveau_scan);
        LinearLayout layoutResult = findViewById(R.id.layout_result);

        String resultat = getIntent().getStringExtra("resultat");
        String uuid = getIntent().getStringExtra("uuid");
        String motif = getIntent().getStringExtra("motif");

        tvUuid.setText("UUID : " + uuid);

        if ("ACCEPTEE".equals(resultat)) {
            tvResultat.setText("✅ BILLET VALIDE");
            tvResultat.setTextColor(Color.parseColor("#4CAF50"));
            layoutResult.setBackgroundColor(Color.parseColor("#E8F5E9"));
            tvMotif.setText("");
        } else {
            tvResultat.setText("❌ BILLET REFUSÉ");
            tvResultat.setTextColor(Color.parseColor("#F44336"));
            layoutResult.setBackgroundColor(Color.parseColor("#FFEBEE"));
            tvMotif.setText(motif != null ? "Motif : " + motif : "");
        }

        btnNouveauScan.setOnClickListener(v -> {
            startActivity(new Intent(this, ScanActivity.class));
            finish();
        });
    }
}