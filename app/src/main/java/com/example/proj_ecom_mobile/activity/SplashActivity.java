package com.example.proj_ecom_mobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proj_ecom_mobile.R;
import com.example.proj_ecom_mobile.activity.admin.AdminMainActivity;
import com.example.proj_ecom_mobile.activity.auth.LoginActivity;
import com.example.proj_ecom_mobile.activity.user.MainActivity;
import com.example.proj_ecom_mobile.database.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Bạn cần có file xml này, nếu chưa có hãy tạo nó

        new Handler().postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(getApplicationContext());

            if (sessionManager.isLoggedIn()) {
                String role = sessionManager.getKeyRole();
                if ("admin".equals(role)) {
                    // Nếu là Admin -> Vào trang Admin
                    startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                } else {
                    // Nếu là User -> Vào trang User
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            } else {
                // Chưa đăng nhập -> Vào Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000); // Chờ 2 giây
    }
}