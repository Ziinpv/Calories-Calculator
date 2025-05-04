package com.example.calories_caculator.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.calories_caculator.model.Meal;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Meal>> mealsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> targetCalories = new MutableLiveData<>(2000);

    public LiveData<List<Meal>> getMealsLiveData() {
        return mealsLiveData;
    }

    public LiveData<Integer> getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(int calories) {
        targetCalories.setValue(calories);
    }

    public void addOrUpdateMeal(Meal meal) {
        List<Meal> currentMeals = mealsLiveData.getValue();
        if (currentMeals != null) {
            boolean exists = false;
            for (Meal m : currentMeals) {
                if (m.getName().equals(meal.getName())) {
                    m.incrementQuantity();
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                currentMeals.add(meal);
            }
            mealsLiveData.setValue(new ArrayList<>(currentMeals));
        }
    }

    public void removeMeal(int position) {
        List<Meal> currentMeals = mealsLiveData.getValue();
        if (currentMeals != null && position >= 0 && position < currentMeals.size()) {
            currentMeals.remove(position);
            mealsLiveData.setValue(new ArrayList<>(currentMeals));
        }
    }

    public void clearMeals() {
        mealsLiveData.setValue(new ArrayList<>());
    }
}