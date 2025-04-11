package com.example.calories_caculator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import com.example.calories_caculator.R;
public class UserInfoActivity extends AppCompatActivity {

    EditText etFullname, etAge, etWeight, etHeight;
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale;
    Spinner spActivityLevel;
    Button btnFinish;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Ánh xạ view
        etFullname = findViewById(R.id.etFullname);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        spActivityLevel = findViewById(R.id.spinnerActivityLevel);
        btnFinish = findViewById(R.id.btnFinish);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
    }

    private void saveUserInfo() {
        String fullname = etFullname.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String gender = "";

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rbMale) {
            gender = "Nam";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Nữ";
        }

        String activityLevel = spActivityLevel.getSelectedItem().toString();  // Không dùng ArrayAdapter vẫn lấy được nội dung

        if (fullname.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        float weight = Float.parseFloat(weightStr);
        float height = Float.parseFloat(heightStr);

        // Lưu lên Firestore
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullname", fullname);
        userMap.put("age", age);
        userMap.put("gender", gender);
        userMap.put("weight", weight);
        userMap.put("height", height);
        userMap.put("activityLevel", activityLevel);

        db.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserInfoActivity.this, "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                    // Quay về form đăng nhập
                    Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserInfoActivity.this, "Lưu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
