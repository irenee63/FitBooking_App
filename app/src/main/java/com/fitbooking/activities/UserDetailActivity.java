package com.fitbooking.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.R;
import com.fitbooking.models.User;
import com.fitbooking.network.*;
import com.fitbooking.utils.CustomToast;
import com.fitbooking.utils.DatePicker;
import com.fitbooking.utils.DateConverter;
import com.fitbooking.manager.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.net.URLEncoder;

public class UserDetailActivity extends AppCompatActivity {

    EditText etName, etMail, etPassword, etBirthday, etTotalBalance, etAvailBalance;
    TextView twTotalBalance, twAvailBalance;
    Button btBack;
    MaterialButton btEditSave;
    ImageButton btDeleteUser;
    int userId;
    User loadedUser;
    boolean edit = false;
    String rol;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        initViews();

        //Abre DatePicker al seleccionar la fecha de nacimiento
        DatePicker.bithdatePicker(this, etBirthday);

        initRol();

        btBack.setOnClickListener(v -> finish());
        btDeleteUser.setOnClickListener(v -> deleteUser(userId));

        //Botón editar o guardar
        btEditSave.setOnClickListener(v -> {
            if (!edit) { enableEdit(); }
            else {
                disableEdit();
                updateUserDetail(userId);
            }
        });

        new Thread(() -> loadUserDetail(userId)).start();
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
        btEditSave = findViewById(R.id.btEditSave);
        btDeleteUser = findViewById(R.id.btDeleteUser);
    }

    private void initRol(){
        //Obtener rol del login
        rol = SessionManager.getInstance().getRol();
        isAdmin = "admin".equals(rol);

        if (isAdmin){
            //Si es admin obtiene el id pasado desde userslistactivity
            userId = getIntent().getIntExtra("user_id", -1);
        } else {
            //Si es usuario lo obtiene de sessionmanager
            userId = SessionManager.getInstance().getUserId();
        }
    }

    private void enableEdit(){
        etName.setEnabled(true);
        etMail.setEnabled(true);
        etBirthday.setEnabled(true);
        btEditSave.setIconResource(R.drawable.ic_save);

        if(isAdmin){
            etTotalBalance.setEnabled(true);
            etAvailBalance.setEnabled(true);
            btDeleteUser.setVisibility(View.VISIBLE);

            if (userId == SessionManager.getInstance().getUserId()) {
                etPassword.setEnabled(true);
                btDeleteUser.setVisibility(View.GONE);
            }
        } else {
            etPassword.setEnabled(true);
        }

        String saveText = "Guardar";
        btEditSave.setText(saveText);
        edit = true;
    }

    private void disableEdit(){
        etName.setEnabled(false);
        etMail.setEnabled(false);
        etBirthday.setEnabled(false);
        etPassword.setEnabled(false);
        etTotalBalance.setEnabled(false);
        etAvailBalance.setEnabled(false);
        String editText = "Editar";
        btEditSave.setText(editText);
        btEditSave.setIconResource(R.drawable.ic_user_edit);
        btDeleteUser.setVisibility(View.GONE);
        edit=false;
    }

    //Cargar la información del usuario por el id recibido
    private void loadUserDetail(int userId){
        try{
            String url = ApiConfig.GET_USER + "?id=" + userId;
            String response = ApiClient.get(url);

            JSONObject obj = new JSONObject(response).getJSONObject("data");
            Log.d("JSON_DEBUG", response);
            Log.d("USER_ID", String.valueOf(userId));

            User user = new User(
                    obj.optInt("id"),
                    obj.optString("rol"),
                    obj.optString("email"),
                    obj.optString("password"),
                    obj.optString("fullname"),
                    obj.optString("birthdate"),
                    obj.optInt("totalBalance"),
                    obj.optInt("availBalance")
            );
            loadedUser = user;

            runOnUiThread(() -> {
                etName.setText(user.getFullname());
                etMail.setText(user.getEmail());
                etTotalBalance.setText(String.valueOf(user.getTotalBalance()));
                etAvailBalance.setText(String.valueOf(user.getAvailBalance()));

                //Si es usuario, o el admin en su perfil muestra la contraseña
                if (!isAdmin || userId == SessionManager.getInstance().getUserId()) {
                    etPassword.setText(user.getPassword());
                }

                //Si en la bd existe la fecha de nacimiento la convierte a formato dd/mm/yyyy para mostrarla
                String birthdate = user.getBirthday();
                if (birthdate !=null && !birthdate.isEmpty()){
                    etBirthday.setText(DateConverter.toDisplay(birthdate));
                } else {
                    etBirthday.setText("");
                }
            });

        } catch (Exception e) {
            showError("Error al obtener usuario: " + e.getMessage());
        }
    }

    //ACTUALIZAR LA INFORMACIÍN DE USUARIO SEGÚN EL ROL
    private void updateUserDetail (int userId){
        new Thread (() -> {
            try{
                String fullname = etName.getText().toString().trim();
                String email = etMail.getText().toString().trim();
                String birthdayUI = etBirthday.getText().toString().trim();
                String newPasswordInput = etPassword.getText().toString().trim();

                String password;
                String totalBalance;
                String availBalance;

                if (isAdmin){
                    if (newPasswordInput.isEmpty()) {
                        password = loadedUser.getPassword();
                    } else {
                        password = newPasswordInput;   // Admin cambia su contraseña
                    }
                    totalBalance = etTotalBalance.getText().toString().trim();
                    availBalance = etAvailBalance.getText().toString().trim();
                } else {
                    password = etPassword.getText().toString().trim(); //User cambia la contraseña
                    //Usuario no cambia el saldo
                    totalBalance = String.valueOf(loadedUser.getTotalBalance());
                    availBalance = String.valueOf(loadedUser.getAvailBalance());
                }

                //Convertir fecha en formato para mySQL
                String birthdayMySQL = "";
                if (!birthdayUI.isEmpty()) {
                    birthdayMySQL = DateConverter.toSQL(birthdayUI);
                }

                //parámetros
                String params = "id=" + URLEncoder.encode(String.valueOf(userId), "UTF-8") +
                        "&fullname=" + URLEncoder.encode(fullname, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8") +
                        "&birthdate=" + URLEncoder.encode(birthdayMySQL, "UTF-8") +
                        "&totalBalance=" + URLEncoder.encode(totalBalance, "UTF-8") +
                        "&availBalance=" + URLEncoder.encode(availBalance, "UTF-8");

                //enviar datos al servidor
                String response = ApiClient.post(ApiConfig.UPDATE_USER, params);
                Log.d("UPDATE_RESPONSE", response);
                JSONObject obj = new JSONObject(response);

                runOnUiThread(() -> {
                    if (obj.optString("status").equals("success")) {
                        CustomToast.success(UserDetailActivity.this,"Usuario actualizado");
                    } else {
                        CustomToast.error(UserDetailActivity.this,"Error al actualizar usuario");
                    }
                });
            } catch (Exception e) {
                showError("Error al guardar usuario: " + e.getMessage());
            }
        }).start();
    }

    //Eliminar usuario
    private void deleteUser (int userId){
        new Thread (() -> {
            try{
                //enviar datos al servidor
                String response = ApiClient.post(ApiConfig.DELETE_USER, "id=" + userId );
                JSONObject obj = new JSONObject(response);

                runOnUiThread(() -> {
                    if (obj.optString("status").equals("success")) {
                        CustomToast.success(UserDetailActivity.this,"Usuario eliminado");
                        //Cierra UserDetailActivity y recarga la lista de usuarios
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        CustomToast.error(UserDetailActivity.this,"Error al eliminar usuario");
                    }
                });
            } catch (Exception e) {
                showError("Error al eliminar usuario: " + e.getMessage());
            }
        }).start();
    }

    //MENSAJE DE ERROR
    private void showError(String msg) {
        runOnUiThread(() -> CustomToast.error(UserDetailActivity.this,msg));
    }
}