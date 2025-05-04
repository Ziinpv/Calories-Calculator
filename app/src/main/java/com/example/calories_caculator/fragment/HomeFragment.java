package com.example.calories_caculator.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories_caculator.R;
import com.example.calories_caculator.adapter.FoodAdapter;
import com.example.calories_caculator.adapter.MealAdapter;
import com.example.calories_caculator.firebase.FirestoreHelper;
import com.example.calories_caculator.model.Food;
import com.example.calories_caculator.model.Meal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements MealAdapter.OnMealDeleteListener {

    private FirestoreHelper firestoreHelper;
    private TextView totalCalories, caloriesCount;
    private MaterialButton addFoodButton, statusButton;
    private RecyclerView mealsRecyclerView;

    private CircularProgressIndicator caloriesProgress;
    private LinearProgressIndicator caloriesBar;
    private List<Meal> meals;
    private MealAdapter mealAdapter;

    // Thay thế hằng số bằng biến
    private int targetCalories = 2000; // Giá trị mặc định
    private int warningCalories; // Sẽ được tính dựa trên targetCalories

    // SharedPreferences để lấy mục tiêu calories
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "CaloriesPrefs";
    private static final String KEY_TARGET_CALORIES = "targetCalories";
    private TextView tvMaxCalories; // TextView hiển thị giá trị max calories

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Lấy mục tiêu calories từ SharedPreferences
        loadTargetCalories();

        // Setup Firestore and adapters
        setupFirestore();
        setupAdapters();

        // Setup click listeners
        setupClickListeners();

        // Cập nhật UI với giá trị mục tiêu calories
        updateCaloriesUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại mục tiêu calories mỗi khi fragment được hiển thị
        // để đảm bảo cập nhật nếu người dùng thay đổi mục tiêu trong InfoFragment
        loadTargetCalories();
        updateCaloriesUI();
        updateTotalCalories(); // Cập nhật lại tổng calories hiển thị
    }

    private void initializeViews(View view) {
        totalCalories = view.findViewById(R.id.totalCalories);
        caloriesCount = view.findViewById(R.id.caloriesCount);
        caloriesProgress = view.findViewById(R.id.caloriesProgress);
        caloriesBar = view.findViewById(R.id.caloriesBar);
        addFoodButton = view.findViewById(R.id.addFoodButton);
        statusButton = view.findViewById(R.id.statusBotton);
        mealsRecyclerView = view.findViewById(R.id.mealsList);
        tvMaxCalories = view.findViewById(R.id.tvMaxCalories); // TextView hiển thị giá trị max calories
    }

    private void loadTargetCalories() {
        // Lấy mục tiêu calories từ SharedPreferences
        targetCalories = sharedPreferences.getInt(KEY_TARGET_CALORIES, 2000);
        // Tính toán ngưỡng cảnh báo (khoảng 60% của mục tiêu)
        warningCalories = (int) (targetCalories * 0.6);
    }

    private void updateCaloriesUI() {
        // Cập nhật giá trị tối đa cho các thanh tiến trình
        caloriesProgress.setMax(targetCalories);
        caloriesBar.setMax(targetCalories);

        // Cập nhật TextView hiển thị giá trị max calories
        if (tvMaxCalories != null) {
            tvMaxCalories.setText(String.valueOf(targetCalories));
        }

        // Cập nhật text hiển thị calories
        int currentCalories = 0;
        if (mealAdapter != null) {
            currentCalories = mealAdapter.getTotalCalories();
        }
        caloriesCount.setText(currentCalories + "/" + targetCalories);
    }

    private void setupFirestore() {
        firestoreHelper = new FirestoreHelper();
    }

    private void setupAdapters() {
        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(meals, this);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mealsRecyclerView.setAdapter(mealAdapter);
    }

    private void setupClickListeners() {
        addFoodButton.setOnClickListener(v -> showFoodSelectionDialog());

        statusButton.setOnClickListener(v -> {
            StatsBottomSheet statsBottomSheet = new StatsBottomSheet();
            statsBottomSheet.show(getParentFragmentManager(), "StatsBottomSheet");
        });
    }

    private void showFoodSelectionDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_food);

        RecyclerView recyclerView = dialog.findViewById(R.id.foodRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
        boolean mealExists = false;
        for (Meal meal : meals) {
            if (meal.getName().equals(food.getName())) {
                meal.incrementQuantity();
                mealExists = true;
                break;
            }
        }

        if (!mealExists) {
            meals.add(new Meal(food.getName(), 1, food.getCalories(), food.getImageUrl()));
        }

        mealAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMealDeleted(int position) {
        meals.remove(position);
        mealAdapter.notifyItemRemoved(position);
        updateTotalCalories();
    }

    private void updateTotalCalories() {
        int total = mealAdapter.getTotalCalories();
        totalCalories.setText("TỔNG CỘNG: " + total + " kcal");

        // Update progress indicators
        caloriesProgress.setProgress(total);
        caloriesBar.setProgress(total);
        caloriesCount.setText(total + "/" + targetCalories);

        // Update colors based on calories
        updateProgressColors(total);
    }

    private void updateProgressColors(int totalCalories) {
        int progressColor;
        if (totalCalories >= targetCalories) {
            progressColor = getResources().getColor(android.R.color.holo_red_dark);
        } else if (totalCalories >= warningCalories) {
            progressColor = getResources().getColor(android.R.color.holo_orange_dark);
        } else {
            progressColor = getResources().getColor(android.R.color.holo_green_dark);
        }

        caloriesProgress.setIndicatorColor(progressColor);
        caloriesBar.setIndicatorColor(progressColor);
    }
}