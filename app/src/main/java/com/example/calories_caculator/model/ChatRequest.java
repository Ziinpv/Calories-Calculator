package com.example.calories_caculator.model;

import java.util.List;

public class ChatRequest {
    private String model = "gpt-3.5-turbo";
    private List<Message> messages;

    public ChatRequest(List<Message> messages) {
        this.messages = messages;
    }
}