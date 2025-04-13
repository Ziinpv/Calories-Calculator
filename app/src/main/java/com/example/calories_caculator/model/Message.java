package com.example.calories_caculator.model;

public class Message {
    private String role;     // "user" hoặc "assistant"
    private String content;  // nội dung tin nhắn

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }
}