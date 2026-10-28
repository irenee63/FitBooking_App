package com.fitbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.R;

public class UserMenuActivity extends AppCompatActivity {

    Button btnMyUser, btnClasses, btnClose;
    ImageView ivBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        initViews();

        btnMyUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserMenuActivity.this, UserDetailActivity.class);
            startActivity(intent);
        });

        btnClasses.setOnClickListener(v -> {
            Intent intent = new Intent(UserMenuActivity.this, ClassCalendarActivity.class);
            startActivity(intent);
        });

        btnClose.setOnClickListener(v -> finish());

    }

    private void initViews(){
        btnClasses = findViewById(R.id.btnClasses);
        btnMyUser = findViewById(R.id.btnMyUser);
        btnClose = findViewById(R.id.btnClose);
        ivBg = findViewById(R.id.ivLogoBg);
    }
}