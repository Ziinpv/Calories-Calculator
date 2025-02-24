package com.example.calories_caculator;

public class WorkoutVideo {
    private String title;
    private String videoId;
    private String description;

    public WorkoutVideo(String title, String videoId, String description) {
        this.title = title;
        this.videoId = videoId;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getDescription() {
        return description;
    }
}