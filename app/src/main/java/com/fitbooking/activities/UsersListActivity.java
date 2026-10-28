package com.fitbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.R;
import com.fitbooking.models.User;
import com.fitbooking.network.ApiClient;
import com.fitbooking.network.ApiConfig;
import com.fitbooking.utils.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UsersListActivity extends AppCompatActivity {

    ListView lvUsers;
    ProgressBar progressBar;
    ArrayAdapter<User> adapter;
    List<User> usersList = new ArrayList<>();

    Button btBack,btAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        initViews();

        new Thread(() -> loadUsers()).start();

        //Botón back, vuelve atrás, al login
        btBack.setOnClickListener(v -> finish());

        btAddUser.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(UsersListActivity.this, CreateUserActivity.class);
            startActivity(intent);
        });

        //Al seleccionar un usuario se abre una nueva activity con los detalles, pasando el id
        lvUsers.setOnItemClickListener((parent, view, position, id) -> {

            User user = usersList.get(position);

            Intent intent = new Intent(UsersListActivity.this, UserDetailActivity.class);
            intent.putExtra("user_id", user.getId());
            startActivity(intent);
        });
    }

    public void initViews(){
        lvUsers = findViewById(R.id.lvUsers);
        progressBar = findViewById(R.id.progressBar);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersList);
        lvUsers.setAdapter(adapter);
        btBack = findViewById(R.id.btBack);
        btAddUser = findViewById(R.id.btAddUser);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> loadUsers()).start();
    }

    private void loadUsers() {
        try {
            String response = ApiClient.get(ApiConfig.LIST_USERS);

            //Guarda la respuesta en un array
            JSONArray arr = new JSONObject(response).getJSONArray("data");

            final List<User> newList = new ArrayList<>();   //Crea una lista de usuarios
            for (int i=0; i< arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                User user = new User(
                        obj.optInt("id"),
                        obj.optString("fullname"));
                newList.add(user);
            }

            //Muestra los usuarios en la lista
            runOnUiThread(() -> {
                usersList.clear();
                usersList.addAll(newList);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                //Si la lista esta vacia aparece el mensaje
                if (usersList.isEmpty()){
                    String msg = "No hay usuarios registrados";
                    CustomToast.warning(UsersListActivity.this, msg);
                }
            });

        } catch (Exception e) {
            final String msg = "Error al obtener usuarios: " + e.getMessage();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                CustomToast.error(UsersListActivity.this, msg);
                Log.d("ERROR USUARIOS", "[" + msg + "]");
            });
        }
    }
}