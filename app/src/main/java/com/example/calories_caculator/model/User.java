package com.example.calories_caculator.model;

public class User {
    private String name;
    private String gender;
    private int age;
    private int height;
    private int weight;
    private String activity_level;
    private double activity_factor;
    private String profileImageUrl;
    private String goal;

    // Constructor rỗng cần thiết cho Firestore
    public User() {}

    public User(String name, int age, String gender, int weight, int height, String activity_level, double activity_factor) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.activity_level = activity_level;
        this.activity_factor = activity_factor;
    }

    // Overloaded constructor for backward compatibility
    public User(String name, int age, String gender, int weight, int height, String activity_level) {
        this(name, age, gender, weight, height, activity_level, 1.2); // Default to sedentary
    }

    // Getters và Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getActivityLevel() { return activity_level; }
    public void setActivityLevel(String activity_level) { this.activity_level = activity_level; }

    public double getActivityFactor() { return activity_factor; }
    public void setActivityFactor(double activity_factor) { this.activity_factor = activity_factor; }

    // Profile image URL getter and setter
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}