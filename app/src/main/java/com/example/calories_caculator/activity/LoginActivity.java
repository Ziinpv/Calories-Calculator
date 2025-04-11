package com.example.calories_caculator.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calories_caculator.R;

public class LoginActivity extends AppCompatActivity
{
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ các thành phần
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Đổi màu nút Đăng nhập thành RGB(247, 207, 216)
        btnLogin.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(247, 207, 216)));
        // Đổi màu nút Đăng ký thành một màu khác (ví dụ: màu xanh nhạt)
        btnRegister.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(247, 207, 216)));

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty())
                {   //Kiểm tra nếu người dùng bỏ trống ô nào thì hiển thị cảnh báo.
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
                else
                {   //Nếu có nhập đủ, gọi hàm checkLogin() để kiểm tra thông tin
                    boolean isLoginSuccess = checkLogin(username, password);
                    //Nếu đúng tài khoản, hiển thị thông báo thành công, chuyển sang MainActivity và đóng màn hình đăng nhập.
                    if (isLoginSuccess)
                    {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển sang màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Đóng LoginActivity để không quay lại sau khi đăng nhập
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Xử lý sự kiện đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Chuyển sang màn hình đăng ký
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // Hàm kiểm tra đăng nhập (giả lập)
    private boolean checkLogin(String username, String password) {
        String validUsername = "user123";
        String validPassword = "pass123";
        return username.equals(validUsername) && password.equals(validPassword);
    }
}