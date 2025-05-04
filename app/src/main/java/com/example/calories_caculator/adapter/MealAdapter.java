package com.example.calories_caculator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calories_caculator.model.Meal;
import com.example.calories_caculator.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> mealList;
    private OnMealDeleteListener deleteListener;

    public void setMeals(List<Meal> meals)
    {
        this.mealList = meals;
        notifyDataSetChanged();
    }

    public interface OnMealDeleteListener {
        void onMealDeleted(int position);
    }

    public MealAdapter(List<Meal> mealList, OnMealDeleteListener listener) {
        this.mealList = mealList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meal_item, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.bind(meal, position);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public int getTotalCalories() {
        int total = 0;
        for (Meal meal : mealList) {
            total += meal.getTotalCalories();
        }
        return total;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;
        TextView mealQuantity;
        TextView mealCalories;
        ImageButton btnDeleteMeal;
        private List<Meal> mealList;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.mealName);
            mealQuantity = itemView.findViewById(R.id.mealQuantity);
            mealCalories = itemView.findViewById(R.id.mealCalories);
            btnDeleteMeal = itemView.findViewById(R.id.btnDeleteMeal);
        }

        public void bind(Meal meal, int position) {
            Picasso.get().load(meal.getImageUrl()).into(mealImage);
            mealName.setText(meal.getName());
            mealQuantity.setText("Số lượng: " + meal.getQuantity());
            mealCalories.setText(meal.getTotalCalories() + " kcal");

            btnDeleteMeal.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onMealDeleted(position);
                }
            });
        }
        // Trả về danh sách món ăn hiện tại
        public List<Meal> getMeals() {
            return mealList;
        }


    }
}