package com.example.calories_caculator;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText heightInput;
    private EditText weightInput;
    private TextView bmiResult;
    private FloatingActionButton addFoodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        bmiResult = findViewById(R.id.bmiResult);
        addFoodButton = findViewById(R.id.addFood);

        // Add text change listeners for BMI calculation
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateBMI();
            }
        };

        heightInput.addTextChangedListener(textWatcher);
        weightInput.addTextChangedListener(textWatcher);

        // Set up add food button
        addFoodButton.setOnClickListener(v -> showFoodSelectionDialog());
    }

    private void showFoodSelectionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_food);

        RecyclerView recyclerView = dialog.findViewById(R.id.foodRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Food> foodList = getSampleFoodList(); // Replace with your actual food data
        FoodAdapter adapter = new FoodAdapter(foodList, food -> {
            // Handle food selection
            dialog.dismiss();
            // Add the selected food to your meals list
        });

        recyclerView.setAdapter(adapter);
        dialog.show();
    }

    private List<Food> getSampleFoodList() {
        List<Food> foodList = new ArrayList<>();
        foodList.add(new Food("Cơm gà", 450, "https://example.com/com-ga.jpg"));
        foodList.add(new Food("Phở bò", 400, "https://example.com/pho-bo.jpg"));
        foodList.add(new Food("Bún chả", 550, "https://example.com/bun-cha.jpg"));
        foodList.add(new Food("Bánh mì", 350, "https://example.com/banh-mi.jpg"));
        return foodList;
    }

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
}