package com.fitbooking.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.R;
import com.fitbooking.network.ApiClient;
import com.fitbooking.network.ApiConfig;
import com.fitbooking.utils.*;

import org.json.JSONObject;

import java.net.URLEncoder;

public class RecoveryPasswordActivity extends AppCompatActivity {

    EditText etUser, etBirthdate;
    TextView tvNewPassword, tvNewPass;
    Button btBack, btRecPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        initViews();

        btRecPass.setOnClickListener(v -> recoverPassword());
        btBack.setOnClickListener(v -> finish());
    }

    private void initViews(){
        etUser = findViewById(R.id.etUser);
        etBirthdate = findViewById(R.id.etBirthdate);
        btBack = findViewById(R.id.btBack);
        btRecPass = findViewById(R.id.btRecPass);
        tvNewPassword = findViewById(R.id.tvNewPassword);
        tvNewPass = findViewById(R.id.tvNewPass);

        //Abre DatePicker al seleccionar la fecha de nacimiento
        DatePicker.bithdatePicker(this, etBirthdate);
    }

    private void recoverPassword(){
        new Thread(() -> {
            try {
                String user = etUser.getText().toString().trim();
                String birthdateUI = etBirthdate.getText().toString().trim();

                if (user.isEmpty() || birthdateUI.isEmpty()) {
                    CustomToast.error(this, "Completa todos los campos");
                    return;
                }

                //Convertir fecha en formato para mySQL
                String birthdayMySQL = "";
                birthdayMySQL = DateConverter.toSQL(birthdateUI);

                String params = "?user=" + URLEncoder.encode((user), "UTF-8") +
                        "&birth=" + URLEncoder.encode(birthdayMySQL, "UTF-8");

                String response = ApiClient.get(ApiConfig.RECOVER_PASSWORD + params);
                Log.d("RECOVERY_RAW", response);
                JSONObject obj = new JSONObject(response);

                runOnUiThread(() -> {
                    if (obj.optString("status").equals("success")) {
                        CustomToast.success(RecoveryPasswordActivity.this,"Contraseña actualizada");
                        String newPass = obj.optString("new_password");
                        showNewPassword(newPass);
                    } else {
                        CustomToast.error(RecoveryPasswordActivity.this, obj.optString("message"));
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    String msg = "Error: "+ e.getMessage();
                    CustomToast.error(RecoveryPasswordActivity.this,msg);
                });
            }
        }).start();
    }

    private void showNewPassword(String newPass){
        tvNewPass.setVisibility(View.VISIBLE);
        tvNewPassword.setVisibility(View.VISIBLE);
        tvNewPass.setText(newPass);
    }
}