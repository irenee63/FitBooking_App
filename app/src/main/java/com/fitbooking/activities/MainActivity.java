package com.fitbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.R;
import com.fitbooking.network.*;
import com.fitbooking.utils.CustomToast;
import com.fitbooking.manager.SessionManager;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPass;
    Button btnAccept;
    TextView tvError, tvForgotPass;
    ImageView ivBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        btnAccept.setOnClickListener(v -> {
            final String email = etEmail.getText().toString().trim();
            final String pass = etPass.getText().toString().trim();

            String control = fieldsValidation(email, pass);  //Valida la información introducida

            if (control != null ){
                Toast.makeText(MainActivity.this, control, Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> login(email, pass)).start();
        });

        tvForgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecoveryPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void initViews(){
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);
        btnAccept = findViewById(R.id.btnAccept);
        tvError = findViewById(R.id.tvError);
        ivBg = findViewById(R.id.ivLogoBg);
        tvForgotPass = findViewById(R.id.tvForgotPass);
    }

    //Al volver a la página los campos esten vacios
    @Override
    protected void onResume() {
        super.onResume();
        etEmail.setText("");
        etPass.setText("");
        SessionManager.getInstance().clear();
    }

    private String fieldsValidation (String email, String password){
        if (email.isEmpty() || password.isEmpty()) {
            return "Complete todos los campos";
        }

        if (password.length() < 4 || password.length() > 8){
            return "La contraseña debe tener entre 4 y 8 caracteres";
        }
        return null;
    }

    private void ErrorConnectionServer() {
        runOnUiThread(() -> {
            tvError.setText("No se pudo conectar al servidor");
            tvError.setVisibility(View.VISIBLE);
        });
    }

    private void login (String email, String password) {
        try{
            //PARÁMETROS
            String params = "email=" + URLEncoder.encode(email, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
            String reply = ApiClient.post(ApiConfig.LOGIN, params);  //Envia los parametros y recibe una respuesta del servidor
            Log.d("REPLY_DEBUG", "[" + reply + "]");

            runOnUiThread(() -> {
                String msg;
                Intent intent = null;

                //Recibe: LOGIN_OK_user#7
                String[] parts = reply.split("#");
                String status = parts[0];   // LOGIN_OK_user
                int userId = -1;

                if (parts.length > 1) {
                    userId = Integer.parseInt(parts[1]);  // 7
                }

                //Realiza una acción según la respuesta obtenida del servidor
                switch (status) {
                    case "LOGIN_OK_admin":
                        msg = "Login Administrador";
                        CustomToast.success(MainActivity.this, msg);
                        SessionManager.getInstance().setUserId(userId);
                        SessionManager.getInstance().setRol("admin");
                        intent = new Intent(MainActivity.this, AdminMenuActivity.class);
                        break;

                    case "LOGIN_OK_user":
                        msg = "Login Usuario";
                        CustomToast.success(MainActivity.this, msg);
                        SessionManager.getInstance().setUserId(userId);
                        SessionManager.getInstance().setRol("user");
                        intent = new Intent(MainActivity.this, UserMenuActivity.class);
                        break;

                    case "USER_NF":
                        msg = "Usuario no encontrado, contacta con tu centro";
                        CustomToast.warning(MainActivity.this,msg);
                        break;

                    case "PASSWORD_INCORRECT":
                        msg = "Contraseña incorrecta";
                        CustomToast.warning(MainActivity.this, msg);
                        break;

                    default:
                        msg = "Error";
                        CustomToast.warning(MainActivity.this,msg);
                }

                if (intent != null) {
                    startActivity(intent);
                }
            });

        } catch (Exception e){
            ErrorConnectionServer();
        }
    }
}