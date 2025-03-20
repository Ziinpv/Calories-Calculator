package com.example.calories_caculator.model;

public class User {
    private String name;
    private String gender;
    private int age;
    private int height;
    private int weight;
    private String activity_level;

    // Constructor rỗng cần thiết cho Firestore
    public User() {}

    public User(String name, int age, String gender, int weight, int height, String activity_level) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.activity_level = activity_level;
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
}

