package com.example.calories_caculator.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories_caculator.fragment.UserPopupDialog;
import com.example.calories_caculator.model.Food;
import com.example.calories_caculator.adapter.FoodAdapter;
import com.example.calories_caculator.model.Meal;
import com.example.calories_caculator.adapter.MealAdapter;
import com.example.calories_caculator.R;
import com.example.calories_caculator.fragment.StatsBottomSheet;
import com.example.calories_caculator.firebase.FirestoreHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirestoreHelper firestoreHelper;
    private EditText heightInput,weightInput;
    private TextView bmiResult,totalCalories,caloriesCount;
    private FloatingActionButton addFoodButton,workoutButton,statusButton;
    private ListView mealsList;
    private ImageView imgUserProfile;
    private String userId;
    private CircularProgressIndicator caloriesProgress;
    private List<Meal> meals;
    private MealAdapter mealAdapter;
    private static final int MAX_CALORIES = 3000;
    private static final int WARNING_CALORIES = 1750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        bmiResult = findViewById(R.id.bmiResult);
        addFoodButton = findViewById(R.id.addFoodButton);
        workoutButton = findViewById(R.id.workoutButton);


        statusButton = findViewById(R.id.statusBotton);

        mealsList = findViewById(R.id.mealsList);
        totalCalories = findViewById(R.id.totalCalories);
        caloriesCount = findViewById(R.id.caloriesCount);
        caloriesProgress = findViewById(R.id.caloriesProgress);

        imgUserProfile = findViewById(R.id.imgUserAvatar);

        firestoreHelper = new FirestoreHelper();

        // Initialize meals list and adapter
        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(meals);
        mealsList.setAdapter(mealAdapter);

        // Add text change listeners for BMI calculation
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateBMI();
            }
        };

        heightInput.addTextChangedListener(textWatcher);
        weightInput.addTextChangedListener(textWatcher);

        // Set up add food button
        addFoodButton.setOnClickListener(v -> showFoodSelectionDialog());

        // Set up workout button
        workoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
            startActivity(intent);
        });
        // Set up status button (Xem thống kê calo)
        statusButton.setOnClickListener(v -> {
            StatsBottomSheet statsBottomSheet = new StatsBottomSheet();
            statsBottomSheet.show(getSupportFragmentManager(), "StatsBottomSheet");
        });

        //Nhấp vào imageView thì hiện ra thông tin người dùng
        //ImageView imageView = findViewById(R.id.imgUserAvatar);

//        imageView.setOnClickListener(v -> {
//            // Giả sử userId của người dùng là "user123"
//            UserInfoDialogFragment bottomSheet = UserInfoDialogFragment.newInstance("user1");
//            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
//        });
        ImageView imageView = findViewById(R.id.imgUserAvatar);
        imageView.setOnClickListener(v -> {
            UserPopupDialog userPopupDialog = new UserPopupDialog(MainActivity.this, "user1");
            userPopupDialog.show();
        });


    }


    private void showFoodSelectionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_food);

        RecyclerView recyclerView = dialog.findViewById(R.id.foodRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Food> foodList = new ArrayList<>();
        FoodAdapter adapter = new FoodAdapter(foodList, food -> {
            addOrUpdateMeal(food);
            updateTotalCalories();
            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);
        firestoreHelper.addFoodList(new FirestoreHelper.FoodListCallback() {
            @Override
            public void onSuccess(List<Food> foods) {
                foodList.clear();
                foodList.addAll(foods);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu", e);
            }
        });

        dialog.show();
    }

    private void addOrUpdateMeal(Food food) {
        // Check if meal already exists
        boolean mealExists = false;
        for (Meal meal : meals) {
            if (meal.getName().equals(food.getName())) {
                meal.incrementQuantity();
                mealExists = true;
                break;
            }
        }

        // If meal doesn't exist, add new meal
        if (!mealExists) {
            meals.add(new Meal(food.getName(), 1, food.getCalories(), food.getImageUrl()));
        }

        mealAdapter.notifyDataSetChanged();
    }

    private void updateTotalCalories() {
        int total = mealAdapter.getTotalCalories();
        totalCalories.setText("TOTAL: " + total + " kcal");

        // Update progress and color based on calories
        caloriesProgress.setProgress(total);
        caloriesCount.setText(total + "/" + MAX_CALORIES);

        if (total >= MAX_CALORIES) {
            caloriesProgress.setIndicatorColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (total >= WARNING_CALORIES) {
            caloriesProgress.setIndicatorColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            caloriesProgress.setIndicatorColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

//    private List<Food> getSampleFoodList() {
//        List<Food> foodList = new ArrayList<>();
//        foodList.add(new Food("Cơm gà", 450, "https://example.com/com-ga.jpg"));
//        foodList.add(new Food("Phở bò", 400, "https://example.com/pho-bo.jpg"));
//        foodList.add(new Food("Bún chả", 550, "https://example.com/bun-cha.jpg"));
//        foodList.add(new Food("Bánh mì", 350, "https://example.com/banh-mi.jpg"));
//        return foodList;
//    }

    private void calculateBMI() {
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            try {
                float height = Float.parseFloat(heightStr) / 100; // Convert cm to m
                float weight = Float.parseFloat(weightStr);

                if (height > 0 && weight > 0) {
                    float bmi = weight / (height * height);
                    String bmiCategory = getBMICategory(bmi);
                    bmiResult.setText(String.format("%.1f\n%s", bmi, bmiCategory));
                }
            } catch (NumberFormatException e) {
                bmiResult.setText("Invalid input");
            }
        } else {
            bmiResult.setText("");
        }
    }

    private String getBMICategory(float bmi) {
        if (bmi < 18.5) {
            return "Thiếu cân";
        } else if (bmi < 25) {
            return "Bình thường";
        } else if (bmi < 30) {
            return "Thừa cân";
        } else {
            return "Béo phì";
        }
    }
//    public void refreshUserData() {
//        firestoreHelper.getUserInfo(userId, user -> {
//            if (user != null) {
//                // Cập nhật UI với dữ liệu mới
//                txtAge.setText(String.valueOf(user.getAge()));
//                txtWeight.setText(String.valueOf(user.getWeight()));
//                txtHeight.setText(String.valueOf(user.getHeight()));
//                txtActivityLevel.setText(user.getActivityLevel());
//            } else {
//                Log.e("MainActivity", "Không tìm thấy thông tin user!");
//            }
//        });
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        refreshUserData();
//    }
}