package com.example.calories_caculator;

import android.app.Application;

import com.example.calories_caculator.api.CloudinaryHelper;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo Cloudinary
        CloudinaryHelper.init(this);
    }
}