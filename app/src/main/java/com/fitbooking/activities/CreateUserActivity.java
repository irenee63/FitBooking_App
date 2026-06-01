package com.fitbooking.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.fitbooking.R;
import com.fitbooking.network.*;
import com.fitbooking.utils.CustomToast;
import com.fitbooking.utils.DatePicker;
import com.fitbooking.utils.DateConverter;

import org.json.JSONObject;

import java.net.URLEncoder;

public class CreateUserActivity extends AppCompatActivity {
    EditText etName, etMail, etPassword, etBirthday, etTotalBalance, etAvailBalance;
    TextView twTotalBalance, twAvailBalance;
    Button btBack, btSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        initViews();

        btBack.setOnClickListener(v -> finish());
        btSave.setOnClickListener(v -> createUser());
    }

    private void initViews(){
        etName = findViewById(R.id.etName);
        etMail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etBirthday = findViewById(R.id.etBirthday);
        etTotalBalance = findViewById(R.id.etTotalBalance);
        etAvailBalance = findViewById(R.id.etAvailBalance);

        twTotalBalance = findViewById(R.id.twTotalBalance);
        twAvailBalance = findViewById(R.id.twAvailBalance);

        btBack = findViewById(R.id.btBack);
        btSave = findViewById(R.id.btSave);

        DatePicker.bithdatePicker(this, etBirthday);  //Abre DatePicker al seleccionar la fecha de nacimiento
    }

    private void createUser(){
        new Thread (() -> {
            try {
                //datos de los edits text
                String fullname = etName.getText().toString().trim();
                String email = etMail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String birthdayUI = etBirthday.getText().toString().trim();
                String totalBalance = etTotalBalance.getText().toString().trim();
                String availBalance = etAvailBalance.getText().toString().trim();

                //Completar datos obligatorios
                if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    runOnUiThread(() -> CustomToast.warning(CreateUserActivity.this, "Completa todos los campos"));
                    return;
                }

                //Convertir fecha en formato para mySQL
                String birthdayMySQL = "";
                if (!birthdayUI.isEmpty()) {
                    birthdayMySQL = DateConverter.toSQL(birthdayUI);
                }

                //Parámetros
                String params = "rol=user" +
                        "&fullname=" + URLEncoder.encode(fullname, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8") +
                        "&birthdate=" + URLEncoder.encode(birthdayMySQL, "UTF-8") +
                        "&totalBalance=" + URLEncoder.encode(totalBalance, "UTF-8") +
                        "&availBalance=" + URLEncoder.encode(availBalance, "UTF-8");

                //Enviar datos al servidor
                String response = ApiClient.post(ApiConfig.CREATE_USER, params);
                JSONObject obj = new JSONObject(response);
                String status = obj.optString("status");
                String msg = obj.optString("message");

                runOnUiThread(() -> {
                    CustomToast.success(CreateUserActivity.this, msg);
                    if (status.equals("success")) {
                        finish(); //Vuelve al listado
                    }
                });

            } catch (Exception e){
                runOnUiThread(() -> {
                    String msg = "Error: "+ e.getMessage();
                    CustomToast.error(CreateUserActivity.this,msg);
                });
            }
        }).start();
    }
}