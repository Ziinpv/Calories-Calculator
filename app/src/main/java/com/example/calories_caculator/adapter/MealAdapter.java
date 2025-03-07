package com.example.calories_caculator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calories_caculator.model.Meal;
import com.example.calories_caculator.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MealAdapter extends BaseAdapter {
    private List<Meal> mealList;

    public MealAdapter(List<Meal> mealList) {
        this.mealList = mealList;
    }

    @Override
    public int getCount() {
        return mealList.size();
    }

    @Override
    public Object getItem(int position) {
        return mealList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.meal_item, parent, false);
        }

        Meal meal = mealList.get(position);

        ImageView mealImage = convertView.findViewById(R.id.mealImage);
        TextView mealName = convertView.findViewById(R.id.mealName);
        TextView mealQuantity = convertView.findViewById(R.id.mealQuantity);
        TextView mealCalories = convertView.findViewById(R.id.mealCalories);

        Picasso.get().load(meal.getImageUrl()).into(mealImage);
        mealName.setText(meal.getName());
        mealQuantity.setText("Số lượng: " + meal.getQuantity());
        mealCalories.setText(meal.getTotalCalories() + " kcal");

        return convertView;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public int getTotalCalories() {
        int total = 0;
        for (Meal meal : mealList) {
            total += meal.getTotalCalories();
        }
        return total;
    }
}
