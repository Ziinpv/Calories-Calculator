package com.example.calories_caculator.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CloudinaryHelper {
    private static final String TAG = "CloudinaryHelper";
    private static boolean isInitialized = false;

    public interface CloudinaryUploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
        void onProgress(int progress);
    }

    public static void init(Context context) {
        if (isInitialized) return;

        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "duzetdysn"); // Thay thế bằng cloud name của bạn
            config.put("api_key", "344533425368319");       // Thay thế bằng API key của bạn
            config.put("api_secret", "2SQan7K7zD602EMGbE-DVqxsPAA"); // Thay thế bằng API secret của bạn
            config.put("secure", "true");

            MediaManager.init(context, config);
            isInitialized = true;
            Log.d(TAG, "Cloudinary initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Cloudinary", e);
        }
    }

    public static void uploadImage(Context context, Uri imageUri, String userId, CloudinaryUploadCallback callback) {
        if (!isInitialized) {
            init(context);
        }

        // Tạo tên file duy nhất
        String requestId = MediaManager.get().upload(imageUri)
                .option("folder", "profile_images")
                .option("public_id", userId + "_" + UUID.randomUUID().toString())
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        int progress = (int) ((bytes * 100) / totalBytes);
                        Log.d(TAG, "Upload progress: " + progress + "%");
                        callback.onProgress(progress);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Upload successful: " + imageUrl);
                        callback.onSuccess(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload error: " + error.getDescription());
                        callback.onFailure(error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d(TAG, "Upload rescheduled");
                    }
                })
                .dispatch();
    }

    public static void uploadBitmap(Context context, Bitmap bitmap, String userId, CloudinaryUploadCallback callback) {
        if (!isInitialized) {
            init(context);
        }

        try {
            // Tạo file tạm thời từ bitmap
            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile("image_", ".jpg", outputDir);

            FileOutputStream fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();

            // Upload file
            String requestId = MediaManager.get().upload(Uri.fromFile(outputFile))
                    .option("folder", "profile_images")
                    .option("public_id", userId + "_" + UUID.randomUUID().toString())
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            int progress = (int) ((bytes * 100) / totalBytes);
                            Log.d(TAG, "Upload progress: " + progress + "%");
                            callback.onProgress(progress);
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String imageUrl = (String) resultData.get("secure_url");
                            Log.d(TAG, "Upload successful: " + imageUrl);
                            callback.onSuccess(imageUrl);

                            // Xóa file tạm
                            outputFile.delete();
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Upload error: " + error.getDescription());
                            callback.onFailure(error.getDescription());

                            // Xóa file tạm
                            outputFile.delete();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.d(TAG, "Upload rescheduled");
                        }
                    })
                    .dispatch();
        } catch (IOException e) {
            Log.e(TAG, "Error creating temp file", e);
            callback.onFailure("Lỗi xử lý ảnh: " + e.getMessage());
        }
    }
}
