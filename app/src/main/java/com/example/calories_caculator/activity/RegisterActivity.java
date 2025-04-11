package com.example.calories_caculator.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.calories_caculator.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnBack;
    private FirebaseAuth mAuth;
    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Đảm bảo đúng tên file layout

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        scaleUp = AnimationUtils.loadAnimation(RegisterActivity.this,R.anim.scale_up_anim);
        scaleDown = AnimationUtils.loadAnimation(RegisterActivity.this,R.anim.scale_down_anim);

        btnRegister.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btnRegister.setTextColor(Color.parseColor("#4A26B2"));
                        btnRegister.setBackground(getResources().getDrawable(R.drawable.custom_btn_onclick));
                        btnRegister.startAnimation(scaleUp);
                        return true;
                    case MotionEvent.ACTION_UP:
                        btnRegister.setTextColor(Color.parseColor("#FFFFFF"));
                        btnRegister.setBackground(getResources().getDrawable(R.drawable.custom_btn));
                        btnRegister.startAnimation(scaleDown);
                        v.performClick();
                        return true;
                }
                return false;
            }
        });
        btnBack.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btnBack.setTextColor(Color.parseColor("#4A26B2"));
                        btnBack.setBackground(getResources().getDrawable(R.drawable.custom_btn_onclick));
                        btnBack.startAnimation(scaleUp);
                        return true;
                    case MotionEvent.ACTION_UP:
                        btnBack.setTextColor(Color.parseColor("#FFFFFF"));
                        btnBack.setBackground(getResources().getDrawable(R.drawable.custom_btn));
                        btnBack.startAnimation(scaleDown);
                        v.performClick();
                        return true;
                }
                return false;
            }
        });

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Xử lý sự kiện
        btnRegister.setOnClickListener(v -> registerUser());
        btnBack.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu cần ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng ký Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
//                        Intent intent = new Intent(RegisterActivity.this, UserInfoActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
