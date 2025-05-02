package com.example.calories_caculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.calories_caculator.R;
import com.example.calories_caculator.model.User;

import java.util.HashMap;
import java.util.Map;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    private Spinner spinnerGender; // Thay thế EditText bằng Spinner
    private EditText etWeight, etHeight, etAge;
    private Spinner spinnerActivityLevel;
    private TextView tvActivityDescription;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User user;
    private ProfileUpdateListener listener;

    // Activity level options with their factors and descriptions
    private final String[] activityLevels = {
            "Ít vận động",
            "Vận động nhẹ",
            "Vận động vừa phải",
            "Vận động nhiều",
            "Rất nhiều vận động"
    };

    private final double[] activityFactors = {
            1.2,    // Ít vận động
            1.375,  // Vận động nhẹ
            1.55,   // Vận động vừa phải
            1.725,  // Vận động nhiều
            1.9     // Rất nhiều vận động
    };

    private final String[] activityDescriptions = {
            "Chỉ ngồi làm việc, ít hoặc không tập thể dục",
            "Tập thể dục nhẹ (1-3 lần/tuần)",
            "Tập thể dục vừa phải (3-5 lần/tuần)",
            "Tập thể dục nặng (6-7 lần/tuần)",
            "Công việc thể chất nặng hoặc tập luyện rất cường độ cao"
    };

    // Gender options
    private final String[] genders = {"Nam", "Nữ"};

    // Map to store activity level to factor mapping
    private final Map<String, Double> activityFactorMap = new HashMap<>();

    public interface ProfileUpdateListener {
        void onProfileUpdated(User user);
    }

    public EditProfileBottomSheet() {
        // Required empty constructor
        // Initialize the activity factor map
        for (int i = 0; i < activityLevels.length; i++) {
            activityFactorMap.put(activityLevels[i], activityFactors[i]);
        }
    }

    public static EditProfileBottomSheet newInstance(User user) {
        EditProfileBottomSheet fragment = new EditProfileBottomSheet();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.user = user;
        return fragment;
    }

    public void setProfileUpdateListener(ProfileUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        spinnerGender = view.findViewById(R.id.spinnerGender); // Khởi tạo Spinner giới tính
        etWeight = view.findViewById(R.id.etWeight);
        etHeight = view.findViewById(R.id.etHeight);
        etAge = view.findViewById(R.id.etAge);
        spinnerActivityLevel = view.findViewById(R.id.spinnerActivityLevel);
        tvActivityDescription = view.findViewById(R.id.tvActivityDescription);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Set up spinners
        setupGenderSpinner(); // Thiết lập Spinner giới tính
        setupActivityLevelSpinner();

        // Populate fields with existing data
        populateFields();

        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genders
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Chọn giới tính hiện tại nếu có
        if (user != null && user.getGender() != null) {
            if ("Nam".equalsIgnoreCase(user.getGender())) {
                spinnerGender.setSelection(0);
            } else if ("Nữ".equalsIgnoreCase(user.getGender())) {
                spinnerGender.setSelection(1);
            }
        }
    }

    private void setupActivityLevelSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                activityLevels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(adapter);

        // Set listener to update description when selection changes
        spinnerActivityLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvActivityDescription.setText(activityDescriptions[position]);
                tvActivityDescription.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvActivityDescription.setVisibility(View.GONE);
            }
        });
    }

    private void populateFields() {
        if (user != null) {
            // Giới tính đã được xử lý trong setupGenderSpinner()

            if (user.getWeight() > 0) {
                etWeight.setText(String.valueOf((int) user.getWeight()));
            }

            if (user.getHeight() > 0) {
                etHeight.setText(String.valueOf((int) user.getHeight()));
            }

            if (user.getAge() > 0) {
                etAge.setText(String.valueOf(user.getAge()));
            }

            // Set activity level in spinner if it exists
            if (user.getActivityLevel() != null && !user.getActivityLevel().isEmpty()) {
                for (int i = 0; i < activityLevels.length; i++) {
                    if (activityLevels[i].equals(user.getActivityLevel())) {
                        spinnerActivityLevel.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void saveProfile() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Bạn cần đăng nhập để lưu thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get values from inputs
        String gender = spinnerGender.getSelectedItem().toString(); // Lấy giá trị từ Spinner
        int selectedPosition = spinnerActivityLevel.getSelectedItemPosition();
        String activityLevel = activityLevels[selectedPosition];
        double activityFactor = activityFactors[selectedPosition];

        // Parse numeric values with validation
        int weight = 0;
        int height = 0;
        int age = 0;

        try {
            if (!etWeight.getText().toString().isEmpty()) {
                weight = Integer.parseInt(etWeight.getText().toString());
                if (weight <= 0 || weight > 300) {
                    Toast.makeText(requireContext(), "Cân nặng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!etHeight.getText().toString().isEmpty()) {
                height = Integer.parseInt(etHeight.getText().toString());
                if (height <= 0 || height > 250) {
                    Toast.makeText(requireContext(), "Chiều cao không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!etAge.getText().toString().isEmpty()) {
                age = Integer.parseInt(etAge.getText().toString());
                if (age <= 0 || age > 120) {
                    Toast.makeText(requireContext(), "Tuổi không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Vui lòng nhập đúng định dạng số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create or update user
        User updatedUser;
        if (user == null) {
            String userName = currentUser.getDisplayName() != null ?
                    currentUser.getDisplayName() : "Người dùng";
            updatedUser = new User(
                    userName,
                    age,
                    gender,
                    weight,
                    height,
                    activityLevel,
                    activityFactor
            );
        } else {
            user.setGender(gender);
            user.setWeight(weight);
            user.setHeight(height);
            user.setAge(age);
            user.setActivityLevel(activityLevel);
            user.setActivityFactor(activityFactor);
            updatedUser = user;
        }

        // Tạo map chứa các trường cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("gender", gender);
        updates.put("weight", weight);
        updates.put("height", height);
        updates.put("age", age);
        updates.put("activityLevel", activityLevel);
        updates.put("activityFactor", activityFactor);

        // Cập nhật chỉ các trường được chỉ định, không ghi đè toàn bộ document
        db.collection("users")
                .document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onProfileUpdated(updatedUser);
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // Nếu update thất bại do document chưa tồn tại, thử tạo mới
                    if (e.getMessage() != null && e.getMessage().contains("No document to update")) {
                        db.collection("users")
                                .document(currentUser.getUid())
                                .set(updatedUser)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(requireContext(), "Tạo mới thông tin thành công", Toast.LENGTH_SHORT).show();
                                    if (listener != null) {
                                        listener.onProfileUpdated(updatedUser);
                                    }
                                    dismiss();
                                })
                                .addOnFailureListener(e1 -> {
                                    Toast.makeText(requireContext(), "Lỗi: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}