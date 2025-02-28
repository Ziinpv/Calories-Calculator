package com.example.calories_caculator;

public class Meal {
    private String name;
    private int quantity;
    private int calories;
    private String imageUrl;

    public Meal(String name, int quantity, int calories, String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getCalories() {
        return calories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getTotalCalories() {
        return calories * quantity;
    }

    public void incrementQuantity() {
        quantity++;
    }
}