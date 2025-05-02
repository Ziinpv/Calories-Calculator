package com.example.calories_caculator.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.calories_caculator.R;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.calories_caculator.model.User;

public class ProfileFragment extends Fragment implements EditProfileBottomSheet.ProfileUpdateListener {

    private CardView cardHeader;
    private ImageView ivProfileImage;
    private TextView tvName, tvEmail;
    private TextView tvGender, tvWeight, tvHeight, tvAge, tvActivityLevel;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        cardHeader = view.findViewById(R.id.cardHeader);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvGender = view.findViewById(R.id.tvGender);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvAge = view.findViewById(R.id.tvAge);
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel);

        // Set click listener for header to open edit bottom sheet
        cardHeader.setOnClickListener(v -> showEditProfileBottomSheet());

        // Display authenticated user info
        displayAuthUserInfo();

        // Load additional user profile data from Firestore
        loadUserProfile();
    }

    private void displayAuthUserInfo() {
        if (currentUser != null) {
            // Display user name
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                tvName.setText(currentUser.getDisplayName());
            } else {
                tvName.setText("Người dùng");
            }

            // Display user email
            tvEmail.setText(currentUser.getEmail());

            // Display user profile image if available
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(ivProfileImage);
            }
        }
    }

    private void loadUserProfile() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = document.toObject(User.class);
                            updateProfileUI(user);
                        } else {
                            // No profile exists yet - show empty/default values
                            setEmptyProfileValues();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi tải thông tin: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setEmptyProfileValues() {
        tvGender.setText("Chưa cập nhật");
        tvWeight.setText("Chưa cập nhật");
        tvHeight.setText("Chưa cập nhật");
        tvAge.setText("Chưa cập nhật");
        tvActivityLevel.setText("Chưa cập nhật");

        // If you have a BMI status TextView
        if (getView() != null && getView().findViewById(R.id.tvBmiStatus) != null) {
            TextView tvBmiStatus = getView().findViewById(R.id.tvBmiStatus);
            tvBmiStatus.setText("Tình trạng cân nặng: Chưa có dữ liệu");
        }
    }

    private void updateProfileUI(User user) {
        if (user == null) return;

        // Update profile fields if they exist
        if (user.getGender() != null && !user.getGender().isEmpty()) {
            tvGender.setText(user.getGender());
        } else {
            tvGender.setText("Chưa cập nhật");
        }

        if (user.getWeight() > 0) {
            tvWeight.setText((int)user.getWeight() + " kg");
        } else {
            tvWeight.setText("Chưa cập nhật");
        }

        if (user.getHeight() > 0) {
            tvHeight.setText((int)user.getHeight() + " cm");
        } else {
            tvHeight.setText("Chưa cập nhật");
        }

        if (user.getAge() > 0) {
            tvAge.setText(user.getAge() + " tuổi");
        } else {
            tvAge.setText("Chưa cập nhật");
        }

        if (user.getActivityLevel() != null && !user.getActivityLevel().isEmpty()) {
            tvActivityLevel.setText(user.getActivityLevel());
        } else {
            tvActivityLevel.setText("Chưa cập nhật");
        }

        // Calculate and display BMI if height and weight are available
        if (user.getHeight() > 0 && user.getWeight() > 0) {
            double heightInMeters = user.getHeight() / 100.0;
            double bmi = user.getWeight() / (heightInMeters * heightInMeters);
            String bmiStatus = getBmiStatus(bmi);

            // If you have a BMI status TextView
            if (getView() != null && getView().findViewById(R.id.tvBmiStatus) != null) {
                TextView tvBmiStatus = getView().findViewById(R.id.tvBmiStatus);
                tvBmiStatus.setText("Tình trạng cân nặng: " + bmiStatus + " (" + String.format("%.1f", bmi) + ")");
            }
        } else if (getView() != null && getView().findViewById(R.id.tvBmiStatus) != null) {
            TextView tvBmiStatus = getView().findViewById(R.id.tvBmiStatus);
            tvBmiStatus.setText("Tình trạng cân nặng: Chưa có dữ liệu");
        }
    }

    private String getBmiStatus(double bmi) {
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

    private void showEditProfileBottomSheet() {
        EditProfileBottomSheet bottomSheet = EditProfileBottomSheet.newInstance(user);
        bottomSheet.setProfileUpdateListener(this);
        bottomSheet.show(getChildFragmentManager(), "EditProfileBottomSheet");
    }

    @Override
    public void onProfileUpdated(User updatedUser) {
        this.user = updatedUser;
        updateProfileUI(updatedUser);
    }
}