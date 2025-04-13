package com.example.calories_caculator.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
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

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecycler;
    private EditText inputField;
    private Button sendBtn;
    private List<Message> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private ChatGPTApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecycler = findViewById(R.id.chatRecycler);
        inputField = findViewById(R.id.inputField);
        sendBtn = findViewById(R.id.sendButton);

        adapter = new ChatAdapter(messages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/") // ✅ Base OpenAI URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ChatGPTApi.class);

        sendBtn.setOnClickListener(v -> {
            String userMessage = inputField.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                messages.add(new Message("user", userMessage));
                adapter.notifyItemInserted(messages.size() - 1);
                inputField.setText("");
                callGPT();
            }
        });
    }

    private void callGPT() {
        ChatRequest request = new ChatRequest(messages);
        String authHeader = "Bearer " + BuildConfig.OPENAI_API_KEY; // ✅ Secure header

        api.getChatResponse(request, authHeader).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Message reply = response.body().choices.get(0).message;
                    messages.add(reply);
                    adapter.notifyItemInserted(messages.size() - 1);
                    chatRecycler.scrollToPosition(messages.size() - 1);
                } else {
                    messages.add(new Message("assistant", "Lỗi server hoặc sai API key!"));
                    adapter.notifyItemInserted(messages.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                messages.add(new Message("assistant", "Lỗi kết nối: " + t.getMessage()));
                adapter.notifyItemInserted(messages.size() - 1);
            }
        });
    }
}