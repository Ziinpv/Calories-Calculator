package com.example.calories_caculator.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.calories_caculator.model.Food;
import com.example.calories_caculator.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }
    public void addFoodList(FoodListCallback callback) {
        db.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Food> foodList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String imgUrl = document.getString("imgurl");
                        Long caloriesLong = document.getLong("calories");
                        int calories = (caloriesLong != null) ? caloriesLong.intValue() : 0;

                        foodList.add(new Food(name, calories, imgUrl));
                    }
                    callback.onSuccess(foodList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Lỗi khi lấy dữ liệu", e);
                    callback.onFailure(e);
                });
    }

    // Interface để callback khi dữ liệu được lấy thành công
    public interface FoodListCallback {
        void onSuccess(List<Food> foodList);
        void onFailure(Exception e);
    }
    public void loadUserInfo(String userId, UserCallback callback) {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String gender = document.getString("gender");
                        Long age = document.getLong("age");
                        Long weight = document.getLong("weight");
                        Long height = document.getLong("height");
                        String activityLevel = document.getString("activityLevel");

                        callback.onUserLoaded(name, gender, age, weight, height, activityLevel);
                    } else {
                        // Bổ sung dòng này
                        callback.onError("Không tìm thấy dữ liệu người dùng với ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi Firestore: ", e);
                    callback.onError("Lỗi tải dữ liệu Firestore!");
                });
    }

    public void saveUserInfo(Context context, String userId, int age, int weight, int height, String activityLevel, SaveCallback callback) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("age", age);
        updatedData.put("weight", weight);
        updatedData.put("height", height);
        updatedData.put("activityLevel", activityLevel);

        db.collection("user").document(userId).update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onError("Lỗi cập nhật!"));
    }
    public interface UserCallback {
        void onUserLoaded(String name, String gender, Long age, Long weight, Long height, String activityLevel);
        void onError(String errorMessage);
    }

    public interface SaveCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

}
