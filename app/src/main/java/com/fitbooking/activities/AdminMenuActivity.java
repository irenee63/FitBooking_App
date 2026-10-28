package com.fitbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.R;

public class AdminMenuActivity extends AppCompatActivity {

    Button btnManageUsers, btnManageClasses, btnClose;
    ImageView ivBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        initViews();

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, UsersListActivity.class);
            startActivity(intent);
        });

        btnManageClasses.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, ClassCalendarActivity.class);
            startActivity(intent);
        });

        btnClose.setOnClickListener(v -> finish());
    }

    private void initViews() {
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageClasses = findViewById(R.id.btnManageClasses);
        btnClose = findViewById(R.id.btnClose);
        ivBg = findViewById(R.id.ivLogoBg);
    }
}