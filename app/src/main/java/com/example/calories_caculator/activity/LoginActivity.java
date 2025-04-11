package com.example.calories_caculator.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calories_caculator.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    FirebaseAuth mAuth;

    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        scaleUp = AnimationUtils.loadAnimation(LoginActivity.this,R.anim.scale_up_anim);
        scaleDown = AnimationUtils.loadAnimation(LoginActivity.this,R.anim.scale_down_anim);
        // Ánh xạ các thành phần
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister= findViewById(R.id.btnRegister);
        mAuth = FirebaseAuth.getInstance();

//        btnLogin.setEnabled(false);


        // Đổi màu nút Đăng nhập thành RGB(255, 193, 180)
        //btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 193, 180)));
//
        // Xử lý sự kiện đăng nhập
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
        btnLogin.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btnLogin.setTextColor(Color.parseColor("#4A26B2"));
                        btnLogin.setBackground(getResources().getDrawable(R.drawable.custom_btn_onclick));
                        btnLogin.startAnimation(scaleUp);
                        return true;
                    case MotionEvent.ACTION_UP:
                        btnLogin.setTextColor(Color.parseColor("#FFFFFF"));
                        btnLogin.setBackground(getResources().getDrawable(R.drawable.custom_btn));
                        btnLogin.startAnimation(scaleDown);
                        v.performClick();
                        return true;
                }
                return false;
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    // Chuyển sang MainActivity
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " +
                                            task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

//    // Hàm kiểm tra đăng nhập (giả lập)
//    private boolean checkLogin(String username, String password) {
//        String validUsername = "user123";
//        String validPassword = "pass123";
//        return username.equals(validUsername) && password.equals(validPassword);
//    }
}
