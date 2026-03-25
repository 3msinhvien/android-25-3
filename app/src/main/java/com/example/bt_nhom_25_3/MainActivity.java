package com.example.bt_nhom_25_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt_nhom_25_3.data.AppDatabase;
import com.example.bt_nhom_25_3.ui.CategoryActivity;
import com.example.bt_nhom_25_3.ui.LoginActivity;
import com.example.bt_nhom_25_3.ui.ProductActivity;
import com.example.bt_nhom_25_3.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnLoginNavigate, btnLogout, btnProducts, btnCategories;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLoginNavigate = findViewById(R.id.btnLoginNavigate);
        btnLogout = findViewById(R.id.btnLogout);
        btnProducts = findViewById(R.id.btnProducts);
        btnCategories = findViewById(R.id.btnCategories);

        sessionManager = new SessionManager(this);

        // Khởi tạo database lần đầu để trigger Room onCreate (tạo seed data)
        AppDatabase db = AppDatabase.getDatabase(this);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.categoryDao().getAllCategories(); // Gọi query nhẹ để kích hoạt onCreate
        });

        btnLoginNavigate.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            updateUI();
        });

        btnProducts.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProductActivity.class));
        });

        btnCategories.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CategoryActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (sessionManager.isLoggedIn()) {
            tvWelcome.setText("Xin chào, " + sessionManager.getUsername() + "!");
            btnLoginNavigate.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvWelcome.setText("Chào mừng! Vui lòng Đăng nhập.");
            btnLoginNavigate.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }
}