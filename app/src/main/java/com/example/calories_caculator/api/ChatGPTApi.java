package com.example.calories_caculator.api;

import com.example.calories_caculator.model.ChatRequest;
import com.example.calories_caculator.model.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatGPTApi {

    @POST("v1/chat/completions")
    Call<ChatResponse> getChatResponse(
            @Body ChatRequest request,
            @Header("Authorization") String authHeader // ✅ header động
    );
}