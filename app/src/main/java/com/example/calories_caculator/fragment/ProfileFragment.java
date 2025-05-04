package com.example.calories_caculator.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.calories_caculator.R;
import com.example.calories_caculator.activity.LoginActivity;
import com.example.calories_caculator.model.User;
import com.example.calories_caculator.api.CloudinaryHelper;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements EditProfileBottomSheet.ProfileUpdateListener {

    private static final String TAG = "ProfileFragment";
    private static final int PICK_IMAGE_REQUEST = 1;

    private CardView cardHeader;
    private ImageView ivProfileImage;
    private TextView tvName, tvEmail,tvGoal;
    private TextView tvGender, tvWeight, tvHeight, tvAge, tvActivityLevel;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User user;
    private ProgressDialog progressDialog;

    private Button btnLogout;

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
        btnLogout = view.findViewById(R.id.btnLogout);
        tvGoal = view.findViewById(R.id.tvGoal);


        // Khởi tạo ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Đang tải ảnh lên");
        progressDialog.setMessage("Vui lòng đợi...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
    }

    private void setupClickListeners() {
        cardHeader.setOnClickListener(v -> showEditProfileBottomSheet());

        // Thêm click listener cho ảnh đại diện
        ivProfileImage.setOnClickListener(v -> showImagePickerOptions());

        btnLogout.setOnClickListener(v -> logoutUser());
        tvGoal.setOnClickListener(v -> showGoalDialog());
    }

    private void showImagePickerOptions() {
        if (getContext() == null) return;

        String[] options = {"Chụp ảnh", "Chọn từ thư viện", "Hủy"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn ảnh đại diện");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Camera
                    ImagePicker.with(this)
                            .cameraOnly()
                            .cropSquare()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .start(PICK_IMAGE_REQUEST);
                    break;
                case 1: // Gallery
                    ImagePicker.with(this)
                            .galleryOnly()
                            .cropSquare()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .start(PICK_IMAGE_REQUEST);
                    break;
                case 2: // Cancel
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                uploadImageToCloudinary(imageUri);
            }
        }
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        if (currentUser == null) return;

        // Hiển thị dialog tiến trình
        progressDialog.setProgress(0);
        progressDialog.show();

        try {
            // Nén ảnh trước khi upload
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            bitmap = getResizedBitmap(bitmap, 800); // Giảm kích thước xuống 800px

            // Upload bitmap lên Cloudinary
            CloudinaryHelper.uploadBitmap(getContext(), bitmap, currentUser.getUid(), new CloudinaryHelper.CloudinaryUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    progressDialog.dismiss();

                    // Cập nhật URL ảnh trong Firebase Auth
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(imageUrl))
                            .build();

                    currentUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile image updated in Auth");

                                    // Cập nhật URL ảnh trong Firestore
                                    updateProfileImageInFirestore(imageUrl);
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error updating profile in Auth", task.getException());
                                }
                            });
                }

                @Override
                public void onFailure(String error) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error uploading to Cloudinary: " + error);
                }

                @Override
                public void onProgress(int progress) {
                    progressDialog.setProgress(progress);
                }
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error processing image", e);
        }
    }

    // Phương thức để thay đổi kích thước bitmap
    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void updateProfileImageInFirestore(String imageUrl) {
        if (currentUser == null || getContext() == null) return;

        // 1. Đảm bảo Firestore instance hợp lệ
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        // 2. Tạo payload
        Map<String, Object> updates = new HashMap<>();
        updates.put("profileImageUrl", imageUrl);

        // 3. Dùng set() + merge thay vì update()
        db.collection("users")
                .document(currentUser.getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Firestore updated successfully");
                    if (user != null) user.setProfileImageUrl(imageUrl);
                    loadProfileImage();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore error: " + e.getMessage());
                    // Hiển thị chi tiết lỗi
                    if (e instanceof FirebaseFirestoreException) {
                        FirebaseFirestoreException ex = (FirebaseFirestoreException) e;
                        Toast.makeText(getContext(),
                                "Lỗi Firestore: " + ex.getCode(),
                                Toast.LENGTH_LONG).show();
                    }
                });
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

        // Kiểm tra xem có ảnh Cloudinary trong Firestore không
        if (user != null && user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(ivProfileImage);
            return;
        }

        // Thử lấy ảnh Google độ phân giải cao
        if (isGoogleUser() && currentUser.getPhotoUrl() != null) {
            String photoUrl = currentUser.getPhotoUrl().toString();
            photoUrl = photoUrl.replace("s96-c", "s400-c"); // Độ phân giải cao hơn

            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(ivProfileImage);
            return;
        }

        // URL ảnh thông thường
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

                            // Hiển thị mục tiêu nếu có
                            if (user != null && user.getGoal() != null) {
                                tvGoal.setText(user.getGoal());
                            }

                            // Sync name to Auth if needed
                            syncNameToAuthIfNeeded();

                            // Load profile image again in case Firestore has a custom image URL
                            loadProfileImage();
                        } else {
                            Log.d(TAG, "No document exists");
                            setEmptyProfileValues();
                        }
                    } else {
                        Log.e(TAG, "Failed to load profile", task.getException());
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi khi tải thông tin", Toast.LENGTH_SHORT).show();
                        }
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
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
    // Thêm phương thức hiển thị dialog
    private void showGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nhập mục tiêu của bạn");

        // Tạo EditText
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(tvGoal.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newGoal = input.getText().toString();
            tvGoal.setText(newGoal);
            saveGoalToFirestore(newGoal);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Lưu mục tiêu vào Firestore
    private void saveGoalToFirestore(String goal) {
        if (currentUser == null || getContext() == null) return;

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang lưu mục tiêu...");
        progressDialog.show();

        db.collection("users")
                .document(currentUser.getUid())
                .update("goal", goal)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã lưu mục tiêu", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi lưu mục tiêu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}