package com.example.calories_caculator.fragment;

import android.app.Dialog;
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
    private static final int MAX_CALORIES = 3000;
    private static final int WARNING_CALORIES = 1750;

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

        // Setup Firestore and adapters
        setupFirestore();
        setupAdapters();

        // Setup click listeners
        setupClickListeners();

        // Initialize progress indicators
        caloriesProgress.setMax(MAX_CALORIES);
        caloriesBar.setMax(MAX_CALORIES);
    }

    private void initializeViews(View view) {
        totalCalories = view.findViewById(R.id.totalCalories);
        caloriesCount = view.findViewById(R.id.caloriesCount);
        caloriesProgress = view.findViewById(R.id.caloriesProgress);
        caloriesBar = view.findViewById(R.id.caloriesBar);
        addFoodButton = view.findViewById(R.id.addFoodButton);
        statusButton = view.findViewById(R.id.statusBotton);
        mealsRecyclerView = view.findViewById(R.id.mealsList);




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
        caloriesCount.setText(total + "/" + MAX_CALORIES);

        // Update colors based on calories
        updateProgressColors(total);
    }

    private void updateProgressColors(int totalCalories) {
        int progressColor;
        if (totalCalories >= MAX_CALORIES) {
            progressColor = getResources().getColor(android.R.color.holo_red_dark);
        } else if (totalCalories >= WARNING_CALORIES) {
            progressColor = getResources().getColor(android.R.color.holo_orange_dark);
        } else {
            progressColor = getResources().getColor(android.R.color.holo_green_dark);
        }

        caloriesProgress.setIndicatorColor(progressColor);
        caloriesBar.setIndicatorColor(progressColor);
    }




}