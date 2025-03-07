package com.example.calories_caculator.model;

public class WorkoutVideo {
    private String title;
    private String videoId;
    private String description;
    private String thumbnailUrl;

    public WorkoutVideo(String title, String videoId, String description, String thumbnailUrl) {
        this.title = title;
        this.videoId = videoId;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}