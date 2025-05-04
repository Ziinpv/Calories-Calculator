package com.example.calories_caculator.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.calories_caculator.R;
import com.example.calories_caculator.activity.MainActivity;
import com.example.calories_caculator.model.User;
import com.example.calories_caculator.api.CaloriesCalculator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class InfoFragment extends Fragment {
    private static final String TAG = "InfoFragment";

    // UI Components
    private TextView tvWelcomeMessage;
    private LinearLayout layoutWorkoutModes;
    private CardView cardBulking, cardCutting, cardMaintenance;
    private TextView tvBulkingDescription, tvCuttingDescription, tvMaintenanceDescription;
    private Button btnCalculate,btnLater,btnUpdate;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // User data
    private User userProfile;

    // Shared Preferences for storing calories data
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "CaloriesPrefs";
    private static final String KEY_TARGET_CALORIES = "targetCalories";
    private static final String KEY_SELECTED_GOAL = "selectedGoal";

    // Selected goal
    private String selectedGoal = null;

    // Formatter for numbers
    private DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
        loadUserData();

        // Khôi phục mục tiêu đã chọn trước đó (nếu có)
        selectedGoal = sharedPreferences.getString(KEY_SELECTED_GOAL, null);
        updateSelectedGoalUI();
    }

    private void initializeViews(View view) {
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        layoutWorkoutModes = view.findViewById(R.id.layoutWorkoutModes);

        cardBulking = view.findViewById(R.id.cardBulking);
        cardCutting = view.findViewById(R.id.cardCutting);
        cardMaintenance = view.findViewById(R.id.cardMaintenance);

        tvBulkingDescription = view.findViewById(R.id.tvBulkingDescription);
        tvCuttingDescription = view.findViewById(R.id.tvCuttingDescription);
        tvMaintenanceDescription = view.findViewById(R.id.tvMaintenanceDescription);

        btnCalculate = view.findViewById(R.id.btnCalculate);
    }

    private void setupClickListeners() {
        cardBulking.setOnClickListener(v -> {
            selectedGoal = CaloriesCalculator.GOAL_BULKING;
            updateSelectedGoalUI();
        });

        cardCutting.setOnClickListener(v -> {
            selectedGoal = CaloriesCalculator.GOAL_CUTTING;
            updateSelectedGoalUI();
        });

        cardMaintenance.setOnClickListener(v -> {
            selectedGoal = CaloriesCalculator.GOAL_MAINTENANCE;
            updateSelectedGoalUI();
        });

        btnCalculate.setOnClickListener(v -> {
            if (selectedGoal == null) {
                Toast.makeText(getContext(), "Vui lòng chọn một mục tiêu", Toast.LENGTH_SHORT).show();
                return;
            }

            calculateAndSaveCalories();
        });
    }

    private void updateSelectedGoalUI() {
        // Reset tất cả các card về trạng thái không được chọn
        cardBulking.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardCutting.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardMaintenance.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        tvBulkingDescription.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvCuttingDescription.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvMaintenanceDescription.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Đánh dấu card được chọn
        if (selectedGoal != null) {
            switch (selectedGoal) {
                case CaloriesCalculator.GOAL_BULKING:
                    cardBulking.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tvBulkingDescription.setTextColor(getResources().getColor(android.R.color.white));
                    break;
                case CaloriesCalculator.GOAL_CUTTING:
                    cardCutting.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tvCuttingDescription.setTextColor(getResources().getColor(android.R.color.white));
                    break;
                case CaloriesCalculator.GOAL_MAINTENANCE:
                    cardMaintenance.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tvMaintenanceDescription.setTextColor(getResources().getColor(android.R.color.white));
                    break;
            }
        }
    }

    private void loadUserData() {
        if (currentUser == null) {
            showProfileIncompleteDialog();
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userProfile = document.toObject(User.class);

                            // Kiểm tra xem profile đã có thông tin cơ bản chưa
                            boolean isProfileComplete = userProfile != null &&
                                    userProfile.getHeight() > 0 &&
                                    userProfile.getWeight() > 0 &&
                                    userProfile.getAge() > 0 &&
                                    userProfile.getGender() != null &&
                                    !userProfile.getGender().isEmpty() &&
                                    userProfile.getActivityLevel() != null &&
                                    !userProfile.getActivityLevel().isEmpty();

                            if (isProfileComplete) {
                                // Người dùng đã có thông tin profile
                                showWorkoutModes();
                            } else {
                                // Người dùng chưa có thông tin profile đầy đủ
                                showProfileIncompleteDialog();
                            }
                        } else {
                            // Không tìm thấy document - người dùng mới
                            showProfileIncompleteDialog();
                        }
                    } else {
                        Log.e(TAG, "Error loading user data", task.getException());
                        showProfileIncompleteDialog();
                    }
                });
    }

    private void showWorkoutModes() {
        if (tvWelcomeMessage != null) {
            tvWelcomeMessage.setText("Chào " + (userProfile.getName() != null ? userProfile.getName() : "bạn") +
                    "! Hãy chọn mục tiêu tập luyện của bạn.");
        }

        if (layoutWorkoutModes != null) {
            layoutWorkoutModes.setVisibility(View.VISIBLE);
        }
    }

    private void showProfileIncompleteDialog() {
        if (getContext() == null || getActivity() == null || getActivity().isFinishing()) return;

        // Ẩn giao diện chọn chế độ luyện tập
        if (layoutWorkoutModes != null) {
            layoutWorkoutModes.setVisibility(View.GONE);
        }

        if (tvWelcomeMessage != null) {
            tvWelcomeMessage.setText("Bạn cần cập nhật thông tin cá nhân trước khi bắt đầu.");
        }

        View customView = getLayoutInflater().inflate(R.layout.dialog_profile_incomplete, null);
        btnUpdate = customView.findViewById(R.id.btnUpdateProfile);
        btnLater = customView.findViewById(R.id.btnLater);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(customView)
                .setCancelable(false)
                .create();

        btnUpdate.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToProfileTab();
        });

        btnLater.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void navigateToProfileTab() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            // Sử dụng ID của menu item thay vì vị trí số
            ((MainActivity) getActivity()).navigateToTab(R.id.profile);
        }
    }

    private void calculateAndSaveCalories() {
        if (userProfile == null || selectedGoal == null) {
            Toast.makeText(getContext(), "Không thể tính toán. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tính toán các giá trị
        double bmr = CaloriesCalculator.calculateBMR(userProfile);
        double tdee = CaloriesCalculator.calculateTDEE(userProfile);
        double targetCalories = CaloriesCalculator.calculateTargetCalories(userProfile, selectedGoal);
        double[] macros = CaloriesCalculator.calculateMacros(userProfile, selectedGoal);
        double[] cyclingCalories = CaloriesCalculator.calculateCaloriesCycling(userProfile, selectedGoal);

        // Lưu mục tiêu calories vào SharedPreferences để HomeFragment có thể sử dụng
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_TARGET_CALORIES, (int) targetCalories);
        editor.putString(KEY_SELECTED_GOAL, selectedGoal);
        editor.apply();

        // Hiển thị kết quả tính toán
        showCalculationResultDialog(bmr, tdee, targetCalories, macros, cyclingCalories);
    }

    private void showCalculationResultDialog(double bmr, double tdee, double targetCalories,
                                             double[] macros, double[] cyclingCalories) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View customView = getLayoutInflater().inflate(R.layout.dialog_caculation_result, null);

        // Tìm các TextView trong layout
        TextView tvBmrValue = customView.findViewById(R.id.tvBmrValue);
        TextView tvTdeeValue = customView.findViewById(R.id.tvTdeeValue);
        TextView tvTargetCalories = customView.findViewById(R.id.tvTargetCalories);
        TextView tvProtein = customView.findViewById(R.id.tvProtein);
        TextView tvCarbs = customView.findViewById(R.id.tvCarbs);
        TextView tvFat = customView.findViewById(R.id.tvFat);
        TextView tvTrainingDay = customView.findViewById(R.id.tvTrainingDay);
        TextView tvRestDay = customView.findViewById(R.id.tvRestDay);
        Button btnOk = customView.findViewById(R.id.btnOk);

        // Hiển thị các giá trị
        tvBmrValue.setText(df.format(bmr) + " calo");
        tvTdeeValue.setText(df.format(tdee) + " calo");
        tvTargetCalories.setText(df.format(targetCalories) + " calo");

        tvProtein.setText(df.format(macros[0]) + "g");
        tvCarbs.setText(df.format(macros[1]) + "g");
        tvFat.setText(df.format(macros[2]) + "g");

        tvTrainingDay.setText(df.format(cyclingCalories[0]) + " calo");
        tvRestDay.setText(df.format(cyclingCalories[1]) + " calo");

        AlertDialog dialog = builder.setView(customView).create();

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(getContext(), "Đã lưu mục tiêu calories: " + df.format(targetCalories) + " calo", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}