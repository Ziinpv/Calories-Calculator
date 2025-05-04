package com.example.calories_caculator.activity;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailReset;
    private Button btnSendReset,btnBack;
    private FirebaseAuth mAuth;

    Animation scaleUp, scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);

        scaleUp = AnimationUtils.loadAnimation(ForgotPasswordActivity.this,R.anim.scale_up_anim);
        scaleDown = AnimationUtils.loadAnimation(ForgotPasswordActivity.this,R.anim.scale_down_anim);

        // Ánh xạ view
        etEmailReset = findViewById(R.id.etEmailReset);
        btnSendReset = findViewById(R.id.btnSendReset);
        btnBack = findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện click nút gửi
        btnSendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailReset.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendPasswordResetEmail(email);
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

        // Thêm hiệu ứng cho nút (nếu muốn)
        btnSendReset.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btnSendReset.setTextColor(Color.parseColor("#4A26B2"));
                        btnSendReset.setBackground(getResources().getDrawable(R.drawable.custom_btn_onclick));
                        btnSendReset.startAnimation(scaleUp);
                        return true;
                    case MotionEvent.ACTION_UP:
                        btnSendReset.setTextColor(Color.parseColor("#FFFFFF"));
                        btnSendReset.setBackground(getResources().getDrawable(R.drawable.custom_btn));
                        btnSendReset.startAnimation(scaleDown);
                        v.performClick();
                        return true;
                }
                return false;
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        // Hiển thị progress bar hoặc loading (nếu cần)

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Ẩn progress bar (nếu có)

                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Đã gửi liên kết đặt lại mật khẩu đến email của bạn",
                                    Toast.LENGTH_LONG).show();
                            finish(); // Đóng activity sau khi gửi thành công
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Lỗi: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}