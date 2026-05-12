package com.ticketeer.agent.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ticketeer.agent.R;
import com.ticketeer.agent.model.ValidationRequest;
import com.ticketeer.agent.model.ValidationResponse;
import com.ticketeer.agent.network.RetrofitClient;
import com.ticketeer.agent.utils.SessionManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {

    private TextView tvAgentNom, tvDernierScan, tvDeconnexion;
    private Button btnScanner;
    private EditText etNumeroTrain;
    private LinearLayout layoutDernierScan;
    private SessionManager sessionManager;
    private SharedPreferences prefs;

    private static final String PREF_NUMERO_TRAIN = "dernier_train";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        tvAgentNom      = findViewById(R.id.tv_agent_nom);
        btnScanner      = findViewById(R.id.btn_scanner);
        etNumeroTrain   = findViewById(R.id.et_numero_train);
        tvDernierScan   = findViewById(R.id.tv_dernier_scan);
        layoutDernierScan = findViewById(R.id.layout_dernier_scan);
        tvDeconnexion   = findViewById(R.id.tv_deconnexion);
        sessionManager  = new SessionManager(this);
        prefs           = getSharedPreferences("TicketeerAgent", MODE_PRIVATE);

        tvAgentNom.setText("Agent : " + sessionManager.getAgentNom());

        // Restaurer le dernier numéro de train utilisé
        String dernierTrain = prefs.getString(PREF_NUMERO_TRAIN, "");
        if (!dernierTrain.isEmpty()) {
            etNumeroTrain.setText(dernierTrain);
        }

        btnScanner.setOnClickListener(v -> {
            String numeroTrain = etNumeroTrain.getText().toString().trim().toUpperCase();
            if (numeroTrain.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir le numéro du train", Toast.LENGTH_SHORT).show();
                return;
            }
            // Sauvegarder le numéro de train pour le prochain scan
            prefs.edit().putString(PREF_NUMERO_TRAIN, numeroTrain).apply();
            lancerScanner();
        });

        tvDeconnexion.setOnClickListener(v -> {
            sessionManager.deconnexion();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void lancerScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scannez le QR Code du billet");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String uuid = result.getContents();
                android.util.Log.d("SCAN", "UUID scanné: " + uuid);
                validerBillet(uuid);
            } else {
                Toast.makeText(this, "Scan annulé", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void validerBillet(String uuid) {
        String token       = "Bearer " + sessionManager.getToken();
        String date        = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String heure       = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String numeroTrain = etNumeroTrain.getText().toString().trim().toUpperCase();

        ValidationRequest request = new ValidationRequest(uuid, numeroTrain, date, heure);

        RetrofitClient.getApiService().validerBillet(token, request)
                .enqueue(new Callback<ValidationResponse>() {

                    @Override
                    public void onResponse(Call<ValidationResponse> call,
                                           Response<ValidationResponse> response) {

                        // Afficher le dernier UUID scanné
                        layoutDernierScan.setVisibility(View.VISIBLE);
                        tvDernierScan.setText(uuid.substring(0, 8) + "...");

                        Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                        intent.putExtra("uuid", uuid);
                        intent.putExtra("numeroTrain", numeroTrain);

                        if (response.isSuccessful() && response.body() != null) {
                            intent.putExtra("resultat", response.body().getResultat());
                            intent.putExtra("motif", response.body().getMotifRefus() != null
                                    ? response.body().getMotifRefus() : "");
                        } else {
                            intent.putExtra("resultat", "REFUSEE");
                            intent.putExtra("motif", "Erreur serveur (" + response.code() + ")");
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ValidationResponse> call, Throwable t) {
                        Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                        intent.putExtra("uuid", uuid);
                        intent.putExtra("numeroTrain", numeroTrain);
                        intent.putExtra("resultat", "REFUSEE");
                        intent.putExtra("motif", "Impossible de contacter le serveur");
                        startActivity(intent);
                    }
                });
    }
}