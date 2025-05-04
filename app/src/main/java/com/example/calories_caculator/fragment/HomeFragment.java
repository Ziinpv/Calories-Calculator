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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calories_caculator.R;
import com.example.calories_caculator.adapter.FoodAdapter;
import com.example.calories_caculator.adapter.MealAdapter;
import com.example.calories_caculator.firebase.FirestoreHelper;
import com.example.calories_caculator.model.Food;
import com.example.calories_caculator.model.Meal;
import com.example.calories_caculator.viewmodel.SharedViewModel;
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

    private SharedViewModel sharedViewModel;

    private int targetCalories = 2000;
    private int warningCalories;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "CaloriesPrefs";
    private static final String KEY_TARGET_CALORIES = "targetCalories";
    private TextView tvMaxCalories;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        initializeViews(view);
        loadTargetCalories();
        setupFirestore();
        setupAdapters();
        setupClickListeners();
        updateCaloriesUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTargetCalories();
        updateCaloriesUI();
        updateTotalCalories();
    }

    private void initializeViews(View view) {
        totalCalories = view.findViewById(R.id.totalCalories);
        caloriesCount = view.findViewById(R.id.caloriesCount);
        caloriesProgress = view.findViewById(R.id.caloriesProgress);
        caloriesBar = view.findViewById(R.id.caloriesBar);
        addFoodButton = view.findViewById(R.id.addFoodButton);
        statusButton = view.findViewById(R.id.statusBotton);
        mealsRecyclerView = view.findViewById(R.id.mealsList);
        tvMaxCalories = view.findViewById(R.id.tvMaxCalories);
    }

    private void loadTargetCalories() {
        targetCalories = sharedPreferences.getInt(KEY_TARGET_CALORIES, 2000);
        warningCalories = (int) (targetCalories * 0.6);
    }

    private void updateCaloriesUI() {
        caloriesProgress.setMax(targetCalories);
        caloriesBar.setMax(targetCalories);

        if (tvMaxCalories != null) {
            tvMaxCalories.setText(String.valueOf(targetCalories));
        }

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
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedViewModel.getMealsLiveData().observe(getViewLifecycleOwner(), updatedMeals -> {
            meals = updatedMeals;

            if (mealAdapter == null) {
                mealAdapter = new MealAdapter(meals, this);
                mealsRecyclerView.setAdapter(mealAdapter);
            } else
            {
                mealAdapter.setMeals(meals);
                mealAdapter.notifyDataSetChanged();
            }

            updateTotalCalories();
        });
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

    private void addOrUpdateMeal(Food food)
    {
        sharedViewModel.addOrUpdateMeal(new Meal(food.getName(), 1, food.getCalories(), food.getImageUrl()));
    }

    @Override
    public void onMealDeleted(int position) {
        sharedViewModel.removeMeal(position);
    }

    private void updateTotalCalories() {
        int total = 0;
        if (mealAdapter != null) {
            total = mealAdapter.getTotalCalories();
        }
        totalCalories.setText("TỔNG CỘNG: " + total + " kcal");
        caloriesProgress.setProgress(total);
        caloriesBar.setProgress(total);
        caloriesCount.setText(total + "/" + targetCalories);
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
