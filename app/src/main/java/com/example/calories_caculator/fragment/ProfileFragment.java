package com.example.calories_caculator.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.calories_caculator.R;
import com.example.calories_caculator.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements EditProfileBottomSheet.ProfileUpdateListener {

    private static final String TAG = "ProfileFragment";
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
        loadUserData();
    }

    private void initializeViews(View view) {
        cardHeader = view.findViewById(R.id.cardHeader);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvGender = view.findViewById(R.id.tvGender);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvAge = view.findViewById(R.id.tvAge);
        tvActivityLevel = view.findViewById(R.id.tvActivityLevel);
    }

    private void setupClickListeners() {
        cardHeader.setOnClickListener(v -> showEditProfileBottomSheet());
    }

    private void loadUserData() {
        displayAuthUserInfo();
        loadUserProfile();
    }

    private void displayAuthUserInfo() {
        if (currentUser != null) {
            // Display name with priority: Firestore > Auth > Email prefix > Default
            tvName.setText(getDisplayName());
            tvEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            loadProfileImage();
        }
    }

    private String getDisplayName() {
        // 1. Check Firestore first
        if (user != null && user.getName() != null && !user.getName().isEmpty()) {
            Log.d(TAG, "Using name from Firestore: " + user.getName());
            return user.getName();
        }

        // 2. Check Firebase Auth display name
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            Log.d(TAG, "Using name from Auth: " + currentUser.getDisplayName());
            return currentUser.getDisplayName();
        }

        // 3. Extract from email if available
        if (currentUser.getEmail() != null) {
            String emailPrefix = currentUser.getEmail().split("@")[0];
            Log.d(TAG, "Using name from email: " + emailPrefix);
            return emailPrefix;
        }

        // 4. Fallback to default
        Log.d(TAG, "Using default name");
        return "Người dùng";
    }

    private void loadProfileImage() {
        if (currentUser == null) {
            ivProfileImage.setImageResource(R.drawable.ic_profile);
            return;
        }

        // Try to get high-res Google photo first
        if (isGoogleUser() && currentUser.getPhotoUrl() != null) {
            String photoUrl = currentUser.getPhotoUrl().toString();
            photoUrl = photoUrl.replace("s96-c", "s400-c"); // Higher resolution

            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(ivProfileImage);
            return;
        }

        // Regular photo URL
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(ivProfileImage);
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_profile);
        }
    }

    private boolean isGoogleUser() {
        if (currentUser == null) return false;
        for (UserInfo profile : currentUser.getProviderData()) {
            if ("google.com".equals(profile.getProviderId())) {
                return true;
            }
        }
        return false;
    }

    private void loadUserProfile() {
        if (currentUser == null) return;

        Log.d(TAG, "Loading profile for UID: " + currentUser.getUid());

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Document data: " + document.getData());
                            user = document.toObject(User.class);
                            updateProfileUI(user);

                            // Sync name to Auth if needed
                            syncNameToAuthIfNeeded();
                        } else {
                            Log.d(TAG, "No document exists");
                            setEmptyProfileValues();
                        }
                    } else {
                        Log.e(TAG, "Failed to load profile", task.getException());
                        Toast.makeText(requireContext(), "Lỗi khi tải thông tin", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void syncNameToAuthIfNeeded() {
        if (user != null && user.getName() != null &&
                !user.getName().isEmpty() &&
                (currentUser.getDisplayName() == null ||
                        !currentUser.getDisplayName().equals(user.getName()))) {

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Auth profile name updated");
                        }
                    });
        }
    }

    private void setEmptyProfileValues() {
        tvGender.setText("Chưa cập nhật");
        tvWeight.setText("Chưa cập nhật");
        tvHeight.setText("Chưa cập nhật");
        tvAge.setText("Chưa cập nhật");
        tvActivityLevel.setText("Chưa cập nhật");

        TextView tvBmiStatus = getView() != null ? getView().findViewById(R.id.tvBmiStatus) : null;
        if (tvBmiStatus != null) {
            tvBmiStatus.setText("Tình trạng cân nặng: Chưa có dữ liệu");
        }
    }

    private void updateProfileUI(User user) {
        if (user == null) return;

        tvGender.setText(user.getGender() != null && !user.getGender().isEmpty() ? user.getGender() : "Chưa cập nhật");
        tvWeight.setText(user.getWeight() > 0 ? (int)user.getWeight() + " kg" : "Chưa cập nhật");
        tvHeight.setText(user.getHeight() > 0 ? (int)user.getHeight() + " cm" : "Chưa cập nhật");
        tvAge.setText(user.getAge() > 0 ? user.getAge() + " tuổi" : "Chưa cập nhật");
        tvActivityLevel.setText(user.getActivityLevel() != null && !user.getActivityLevel().isEmpty() ?
                user.getActivityLevel() : "Chưa cập nhật");

        updateBmiStatus(user);
    }

    private void updateBmiStatus(User user) {
        TextView tvBmiStatus = getView() != null ? getView().findViewById(R.id.tvBmiStatus) : null;
        if (tvBmiStatus == null) return;

        if (user.getHeight() > 0 && user.getWeight() > 0) {
            double heightInMeters = user.getHeight() / 100.0;
            double bmi = user.getWeight() / (heightInMeters * heightInMeters);
            String bmiStatus = getBmiStatus(bmi);
            tvBmiStatus.setText("Tình trạng cân nặng: " + bmiStatus + " (" + String.format("%.1f", bmi) + ")");
        } else {
            tvBmiStatus.setText("Tình trạng cân nặng: Chưa có dữ liệu");
        }
    }

    private String getBmiStatus(double bmi) {
        if (bmi < 18.5) return "Thiếu cân";
        if (bmi < 25) return "Bình thường";
        if (bmi < 30) return "Thừa cân";
        return "Béo phì";
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
        // Refresh name in case it was updated
        displayAuthUserInfo();
        if (currentUser != null) {
            tvName.setText(getDisplayName());
        }
    }
}