package com.ticketeer.agent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

    private TextView tvAgentNom;
    private Button btnScanner;
    private SessionManager sessionManager;
    private String numeroTrain = "TGV001";
    private String currentUuid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        tvAgentNom = findViewById(R.id.tv_agent_nom);
        btnScanner = findViewById(R.id.btn_scanner);
        sessionManager = new SessionManager(this);

        tvAgentNom.setText("Agent : " + sessionManager.getAgentNom());
        btnScanner.setOnClickListener(v -> lancerScanner());
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
                currentUuid = result.getContents();
                android.util.Log.d("SCAN", "UUID scanné: " + currentUuid);
                validerBillet(currentUuid);
            } else {
                Toast.makeText(this, "Scan annulé", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void validerBillet(String uuid) {
        String token = "Bearer " + sessionManager.getToken();
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String heure = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        android.util.Log.d("SCAN", "Token: " + token);

        ValidationRequest request = new ValidationRequest(uuid, numeroTrain, date, heure);

        RetrofitClient.getApiService().validerBillet(token, request)
                .enqueue(new Callback<ValidationResponse>() {

                    @Override
                    public void onResponse(Call<ValidationResponse> call,
                                           Response<ValidationResponse> response) {
                        android.util.Log.d("SCAN", "Response code: " + response.code());

                        Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                        intent.putExtra("uuid", uuid);

                        if (response.isSuccessful() && response.body() != null) {
                            intent.putExtra("resultat", response.body().getResultat());
                            intent.putExtra("motif", response.body().getMotifRefus() != null
                                    ? response.body().getMotifRefus() : "");
                        } else {
                            intent.putExtra("resultat", "ACCEPTEE");
                            intent.putExtra("motif", "");
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ValidationResponse> call, Throwable t) {
                        android.util.Log.e("SCAN", "Erreur: " + t.getMessage());
                        Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                        intent.putExtra("uuid", uuid);
                        intent.putExtra("resultat", "ACCEPTEE");
                        intent.putExtra("motif", "");
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_deconnexion) {
            sessionManager.deconnexion();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}