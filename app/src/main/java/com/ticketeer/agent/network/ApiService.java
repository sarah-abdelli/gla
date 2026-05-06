package com.ticketeer.agent.network;

import com.ticketeer.agent.model.LoginRequest;
import com.ticketeer.agent.model.LoginResponse;
import com.ticketeer.agent.model.ValidationRequest;
import com.ticketeer.agent.model.ValidationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    // POST /api/auth/login
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // POST /api/validations/validate
    @POST("api/validations/validate")
    Call<ValidationResponse> validerBillet(
            @Header("Authorization") String bearerToken,
            @Body ValidationRequest request
    );
}
