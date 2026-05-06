package com.ticketeer.agent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ticketeer.agent.R;
import com.ticketeer.agent.model.LoginRequest;
import com.ticketeer.agent.model.LoginResponse;
import com.ticketeer.agent.network.RetrofitClient;
import com.ticketeer.agent.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin, etMotDePasse;
    private Button btnLogin;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.et_login);
        etMotDePasse = findViewById(R.id.et_mot_de_passe);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        sessionManager = new SessionManager(this);

        if (sessionManager.estConnecte()) {
            allerAuScan();
            return;
        }

        btnLogin.setOnClickListener(v -> tenterConnexion());
    }

    private void tenterConnexion() {
        String login = etLogin.getText().toString().trim();
        String mdp = etMotDePasse.getText().toString().trim();

        if (login.isEmpty() || mdp.isEmpty()) {
            Toast.makeText(this, "Remplis tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(login, mdp);
        RetrofitClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse body = response.body();
                    sessionManager.sauvegarderSession(body.getToken(), body.getAgentNom());
                    Toast.makeText(LoginActivity.this,
                            "Bienvenue " + body.getAgentNom(), Toast.LENGTH_SHORT).show();
                    allerAuScan();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Login ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void allerAuScan() {
        startActivity(new Intent(this, ScanActivity.class));
        finish();
    }
}