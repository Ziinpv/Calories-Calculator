package com.example.calories_caculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories_caculator.BuildConfig;
import com.example.calories_caculator.R;
import com.example.calories_caculator.adapter.ChatAdapter;
import com.example.calories_caculator.api.ChatGPTApi;
import com.example.calories_caculator.model.ChatRequest;
import com.example.calories_caculator.model.ChatResponse;
import com.example.calories_caculator.model.Message;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatbotFragment extends Fragment {

    private RecyclerView chatRecycler;
    private EditText inputField;
    private Button sendBtn;
    private List<Message> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private ChatGPTApi api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        chatRecycler = view.findViewById(R.id.chatRecycler);
        inputField = view.findViewById(R.id.inputField);
        sendBtn = view.findViewById(R.id.sendButton);

        // Setup RecyclerView
        adapter = new ChatAdapter(messages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecycler.setAdapter(adapter);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ChatGPTApi.class);

        // Set click listener for send button
        sendBtn.setOnClickListener(v -> {
            String userMessage = inputField.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addUserMessage(userMessage);
                callGPT();
            }
        });
    }

    private void addUserMessage(String message) {
        messages.add(new Message("user", message));
        adapter.notifyItemInserted(messages.size() - 1);
        inputField.setText("");
        chatRecycler.scrollToPosition(messages.size() - 1);
    }

    private void addAssistantMessage(String message) {
        messages.add(new Message("assistant", message));
        adapter.notifyItemInserted(messages.size() - 1);
        chatRecycler.scrollToPosition(messages.size() - 1);
    }

    private void callGPT() {
        ChatRequest request = new ChatRequest(messages);
        String authHeader = "Bearer " + BuildConfig.OPENAI_API_KEY;

        api.getChatResponse(request, authHeader).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message reply = response.body().choices.get(0).message;
                    addAssistantMessage(reply.getContent());
                } else {
                    addAssistantMessage("Lỗi server hoặc sai API key!");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                addAssistantMessage("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}