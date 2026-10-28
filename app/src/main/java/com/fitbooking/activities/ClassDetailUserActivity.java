package com.fitbooking.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.models.Classes;
import com.fitbooking.R;
import com.fitbooking.network.*;
import com.fitbooking.utils.CustomToast;
import com.fitbooking.utils.DateConverter;
import com.fitbooking.manager.SessionManager;

import org.json.JSONObject;

public class ClassDetailUserActivity extends AppCompatActivity {

    TextView tvDate, tvTime, tvCapacity;
    Button btnReserve, btnBack, btnCancel;
    GradientDrawable bg;
    int userId, classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail_user);

        initViews();

        //Datos recibidos de la actividad anterior
        Classes item = (Classes) getIntent().getSerializableExtra("classItem");
        classId = item.getId();
        userId = SessionManager.getInstance().getUserId();

        classViewInfo(item);
        classDetail(); //CARGA LOS DETALLES DE LA CLASE

        btnReserve.setOnClickListener(v -> reserveClass());
        btnCancel.setOnClickListener(v -> cancelBooking());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews(){
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        tvCapacity = findViewById(R.id.tvCapacity);
        tvCapacity.setBackgroundResource(R.drawable.bg_capacity_text);
        bg = (GradientDrawable) tvCapacity.getBackground();

        btnReserve = findViewById(R.id.btReserve);
        btnCancel = findViewById(R.id.btCancel);
        btnBack = findViewById(R.id.btBack);
    }

    private void classViewInfo(Classes item){
        String longDate  = DateConverter.toLongDisplay(item.getClass_date());
        String time = item.getClass_time();
        tvDate.setText(longDate);
        tvTime.setText(time.substring(0,5));
    }

    private void reserveClass () {
        new Thread(() -> {
            try {
                String params = "users_id=" + userId + "&classes_id=" + classId;
                String response = ApiClient.post(ApiConfig.CREATE_BOOKING, params);
                JSONObject obj = new JSONObject(response);

                runOnUiThread(() -> {
                    if (obj.optString("status").equals("success")) {
                        CustomToast.success(ClassDetailUserActivity.this, "Reserva realizada");
                        classDetail();
                    } else {
                        String msg = obj.optString("message");
                        CustomToast.error(ClassDetailUserActivity.this, msg);
                    }
                });
            } catch (Exception e){
                runOnUiThread(() -> {
                    String msg = "Error: " + e.getMessage();
                    CustomToast.error(ClassDetailUserActivity.this, msg);
                });
            }
        }).start();
    }

    private void classDetail (){
        new Thread(() -> {
            try {
                String url = ApiConfig.GET_CLASS + "?id=" + classId;
                String response = ApiClient.get(url);

                JSONObject obj = new JSONObject(response);

                if (!obj.optString("status").equals("success")) {
                    throw new Exception(obj.optString("message"));
                }

                JSONObject data = obj.getJSONObject("data");
                int reserved = data.getInt("reserved");
                int capacity = data.getInt("capacity");

                runOnUiThread(() -> {
                    Log.d("CHECKBOOKING", "userId=" + userId + " classId=" + classId);
                    checkBooking(reserved, capacity);
                });

            } catch (Exception e) {
                runOnUiThread(() -> CustomToast.error(ClassDetailUserActivity.this,"Error al actualizar la clase"));
            }
        }).start();
    }

    private void checkBooking(int reserved, int capacity){
        new Thread(() -> {
            try {
                String url = ApiConfig.CHECK_BOOKING + "?users_id=" + userId + "&classes_id=" + classId;
                String response = ApiClient.get(url);
                JSONObject obj = new JSONObject(response);
                String status = obj.optString("status");

                runOnUiThread(() -> updateUI (reserved, capacity, status.equals("exists")));

            } catch (Exception e){
                runOnUiThread(() -> CustomToast.error(ClassDetailUserActivity.this, "Error al comprobar reserva"));
            }
        }).start();
    }

    public void cancelBooking(){
        new Thread(() -> {
            try{
                String url = ApiConfig.CANCEL_BOOKING + "?users_id=" + userId + "&classes_id=" + classId;
                String response = ApiClient.get(url);
                JSONObject obj = new JSONObject(response);

                runOnUiThread(() -> {
                    if (obj.optString("status").equals("success")) {
                        CustomToast.success(ClassDetailUserActivity.this, "Reserva cancelada");
                        classDetail(); //Actualiza la disponibilidad de la clase
                    } else {
                        String msg = obj.optString("message");
                        CustomToast.error(ClassDetailUserActivity.this, msg);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> CustomToast.error(ClassDetailUserActivity.this, "Error al cancelar reserva"));
            }
        }).start();
    }

    private void updateUI(int reserved, int capacity, boolean userReserved){
        if (userReserved){
            bg.setColor(Color.parseColor("#B3E5FC"));
            String booked = reserved + " / " + capacity + "     RESERVADA";
            tvCapacity.setText(booked);
            btnReserve.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);
            return;
        }
        boolean available = reserved < capacity;

        if (available) {
            bg.setColor(Color.parseColor("#C8E6C9"));
            String avail = reserved + " / " + capacity + "     DISPONIBLE";
            tvCapacity.setText(avail);
            btnReserve.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.GONE);
        } else {
            bg.setColor(Color.parseColor("#FFCDD2"));
            String complete = reserved + " / " + capacity + "     COMPLETA";
            tvCapacity.setText(complete);
            btnReserve.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    }
}