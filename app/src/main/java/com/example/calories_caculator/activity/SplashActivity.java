package com.example.calories_caculator.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calories_caculator.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        WebView webView = findViewById(R.id.webView);
        configureWebView(webView);

        // Load GIF từ thư mục res/drawable
        String html = "<html><body style='margin:0;padding:0;background-color:transparent;'>" +
                "<img src='file:///android_res/drawable/splash_anim.gif' " +
                "style='width:100%;height:100%;object-fit:contain;'/>" +
                "</body></html>";

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 2500);
    }

    private void configureWebView(WebView webView) {
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings settings = webView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(false);

        // Tối ưu hiệu suất
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDestroy() {
        WebView webView = findViewById(R.id.webView);
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}