package com.example.calories_caculator.firebase;

import android.util.Log;

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
    // Lấy thông tin user từ Firestore
//    public void getUserInfo(String userId, final OnUserDataReceivedListener listener) {
//        db.collection("user").document(userId).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        User user = documentSnapshot.toObject(User.class);
//                        listener.onUserDataReceived(user);
//                    } else {
//                        listener.onUserDataReceived(null);
//                    }
//                })
//                .addOnFailureListener(e -> listener.onUserDataReceived(null));
//    }
    public void getUserInfo(String userId, final OnUserDataReceivedListener listener) {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String gender = documentSnapshot.getString("gender");
                        String activityLevel = documentSnapshot.getString("acivity_level"); // Lưu ý lỗi chính tả
                        Long ageLong = documentSnapshot.getLong("age");
                        Long heightLong = documentSnapshot.getLong("height");
                        Long weightLong = documentSnapshot.getLong("wieght"); // Lưu ý lỗi chính tả

                        int age = (ageLong != null) ? ageLong.intValue() : 0;
                        int height = (heightLong != null) ? heightLong.intValue() : 0;
                        int weight = (weightLong != null) ? weightLong.intValue() : 0;

                        User user = new User(name, age, gender, height, weight, activityLevel);
                        listener.onUserDataReceived(user);
                    } else {
                        listener.onUserDataReceived(null);
                    }
                })
                .addOnFailureListener(e -> listener.onUserDataReceived(null));
    }



    // Cập nhật thông tin user lên Firestore
    public void updateUserInfo(String userId, int age, float weight, float height, String activityLevel, final OnDataUpdateListener listener) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("age", age);
        updatedData.put("wieght", weight);
        updatedData.put("height", height);
        updatedData.put("activity_level", activityLevel);

        db.collection("user").document(userId).update(updatedData)
                .addOnSuccessListener(aVoid -> listener.onDataUpdate(true))
                .addOnFailureListener(e -> listener.onDataUpdate(false));
    }

    // Interface callback khi lấy dữ liệu
    public interface OnUserDataReceivedListener {
        void onUserDataReceived(User user);
    }

    // Interface callback khi cập nhật dữ liệu
    public interface OnDataUpdateListener {
        void onDataUpdate(boolean success);
    }
}
