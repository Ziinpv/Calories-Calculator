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

    public void addFoodList(FoodListCallback callback) //Lấy danh sách thực phẩm từ Firestore
    {
        db.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->
                {   //Lấy tất cả tài liệu từ collection foods.
                    List<Food> foodList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots)
                    {
                        String name = document.getString("name");
                        String imgUrl = document.getString("imgurl");
                        Long caloriesLong = document.getLong("calories");
                        int calories = (caloriesLong != null) ? caloriesLong.intValue() : 0;
                        //calories cần ép kiểu từ Long sang int.

                        foodList.add(new Food(name, calories, imgUrl));
                    }
                    callback.onSuccess(foodList);
                    //Gọi callback.onSuccess() nếu thành công, hoặc onFailure() nếu có lỗi
                })
                .addOnFailureListener(e ->
                {
                    Log.e("Firebase", "Lỗi khi lấy dữ liệu", e);
                    callback.onFailure(e);
                });
    }

    // Interface để callback khi dữ liệu được lấy thành công
    public interface FoodListCallback //Cho phép xử lý kết quả sau khi load danh sách thực phẩm
    {
        void onSuccess(List<Food> foodList);
        void onFailure(Exception e);
    }
    public void loadUserInfo(String userId, UserCallback callback)
    {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(document ->
                {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String gender = document.getString("gender");
                        Long age = document.getLong("age");
                        Long weight = document.getLong("weight");
                        Long height = document.getLong("height");
                        String activityLevel = document.getString("activityLevel");

                        callback.onUserLoaded(name, gender, age, weight, height, activityLevel);
                    }
                })
                .addOnFailureListener(e -> callback.onError("Lỗi tải dữ liệu!"));
    }
    public void saveUserInfo(Context context, String userId, int age, int weight, int height, String activityLevel, SaveCallback callback) {
        Map<String, Object> updatedData = new HashMap<>();//Tạo HashMap để chứa dữ liệu người dùng cần cập nhật.
        updatedData.put("age", age);
        updatedData.put("weight", weight);
        updatedData.put("height", height);
        updatedData.put("activityLevel", activityLevel);

        db.collection("user").document(userId).update(updatedData)
                //Gửi dữ liệu mới lên Firestore để cập nhật.
                .addOnSuccessListener(aVoid ->
                {   //Hiện Toast nếu thành công và gọi callback.onSuccess().
                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onError("Lỗi cập nhật!"));
    }
    public interface UserCallback
        //Được sử dụng khi gọi loadUserInfo().
    {
        void onUserLoaded(String name, String gender, Long age, Long weight, Long height, String activityLevel);
        void onError(String errorMessage);
    }

    public interface SaveCallback
        //Dùng trong saveUserInfo() để báo thành công hoặc thất bại.
    {
        void onSuccess();
        void onError(String errorMessage);
    }

}
