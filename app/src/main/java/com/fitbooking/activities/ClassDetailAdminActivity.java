package com.fitbooking.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitbooking.models.Booking;
import com.fitbooking.models.Classes;
import com.fitbooking.R;
import com.fitbooking.adapters.AttendanceAdapter;
import com.fitbooking.network.*;
import com.fitbooking.utils.AlertDialogs;
import com.fitbooking.utils.CustomToast;
import com.fitbooking.utils.DateConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassDetailAdminActivity extends AppCompatActivity {
    TextView tvDate, tvTime, tvCapacity, tvEmptyUsers;
    Button btnSaveAtt, btnBack;
    ImageButton btnDeleteClass;
    GradientDrawable bg;
    int classId;
    RecyclerView rvUsersInClass;
    AttendanceAdapter attendanceAdapter;
    List<Booking> bookingList = new ArrayList<>();
    private String fullDateTime;
    private boolean attChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail_admin);

        initViews();

        //Datos recibidos de la actividad anterior
        Classes classes = (Classes) getIntent().getSerializableExtra("classItem");
        classId = classes.getId();

        dateInfo(classes);
        initRecycler();
        classDetail(); //CARGA LOS DETALLES DE LA CLASE
        listUsers(); //CARGA EL LISTADO DE USUARIOS

        updateUIAttendance(fullDateTime);

        btnDeleteClass.setOnClickListener(v -> {
            AlertDialogs.confirmDeleteClass(
                    ClassDetailAdminActivity.this,
                    () -> deleteClass()
            );
        });

        btnBack.setOnClickListener(v -> exit());
        btnSaveAtt.setOnClickListener(v -> saveAttendance());

        attendanceAdapter.setOnAttendanceChangeListener(() -> {
            attChange = true;
        });
    }

    private void initViews(){
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvEmptyUsers = findViewById(R.id.tvEmptyUsers);

        tvCapacity = findViewById(R.id.tvCapacity);
        tvCapacity.setBackgroundResource(R.drawable.bg_capacity_text);
        bg = (GradientDrawable) tvCapacity.getBackground();

        btnDeleteClass = findViewById(R.id.btDeleteClass);
        btnSaveAtt = findViewById(R.id.btSaveAtt);
        btnBack = findViewById(R.id.btBack);
    }

    private void initRecycler(){
        rvUsersInClass = findViewById(R.id.rvUsersInClass);
        rvUsersInClass.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new AttendanceAdapter(bookingList, fullDateTime);
        rvUsersInClass.setAdapter(attendanceAdapter);
    }

    private void dateInfo(Classes classes){
        String shortDate = classes.getClass_date();
        String longDate  = DateConverter.toLongDisplay(classes.getClass_date());
        String time = classes.getClass_time();
        fullDateTime = shortDate + " " + time;

        tvDate.setText(longDate);
        tvTime.setText(time.substring(0,5));
    }

    private void listUsers(){
        new Thread(() -> {
            try {
                String url = ApiConfig.GET_USERS_IN_CLASS + "?classes_id=" + classId;
                String response = ApiClient.get(url);
                JSONArray arr = new JSONObject(response).getJSONArray("data");

                final List<Booking> newList = new ArrayList<>();   //Crea una lista de usuarios
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    Booking booking = new Booking();
                    booking.setId(obj.optInt("booking_id"));
                    booking.setUsers_id(obj.optInt("user_id"));
                    booking.setFullname(obj.optString("fullname"));
                    booking.setAttendance(obj.optInt("attendance"));
                    newList.add(booking);
                }

                runOnUiThread(() -> {
                    bookingList.clear();
                    bookingList.addAll(newList);
                    attendanceAdapter.notifyDataSetChanged();

                    //Si la lista esta vacia aparece el mensaje
                    if (bookingList.isEmpty()) {
                        tvEmptyUsers.setVisibility(View.VISIBLE);
                        rvUsersInClass.setVisibility(View.GONE);
                    } else {
                        tvEmptyUsers.setVisibility(View.GONE);
                        rvUsersInClass.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                final String msg = "Error al obtener usuarios: " + e.getMessage();
                runOnUiThread(() -> {
                    CustomToast.error(ClassDetailAdminActivity.this, "Error al obtener usuarios");
                    Log.d("ERROR USUARIOS", "[" + msg + "]");
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CHECKBOOKING", "classId=" + classId);
                        updateUIAvailability(reserved, capacity);
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> CustomToast.error(ClassDetailAdminActivity.this,"Error al actualizar clase"));
            }
        }).start();
    }

    private void updateUIAvailability(int reserved, int capacity){
        boolean available = reserved < capacity;
        if (available) {
            bg.setColor(Color.parseColor("#C8E6C9"));
            String avail = reserved + " / " + capacity + "     DISPONIBLE";
            tvCapacity.setText(avail);
        } else {
            String full = reserved + " / " + capacity + "     COMPLETA";
            bg.setColor(Color.parseColor("#FFCDD2"));
            tvCapacity.setText(full);
        }
    }

    private void updateUIAttendance(String fullDateTime){
        try {
            Date classDate  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(fullDateTime);
            boolean classPassed = classDate.getTime() < System.currentTimeMillis();
            if (classPassed){
                btnSaveAtt.setVisibility(View.VISIBLE);
            } else {
                btnSaveAtt.setVisibility(View.GONE);
            }
        } catch (Exception e){
            Log.e("DATE", e.getMessage());
        }
    }

    private void saveAttendance(){
        new Thread(() -> {
            try{
                JSONArray arrayAtt = new JSONArray();

                for (Booking b : bookingList) {
                    JSONObject obj = new JSONObject();
                    obj.put("booking_id", b.getId());
                    obj.put("state", b.getAttendance());
                    arrayAtt.put(obj);
                }

                JSONObject finalJson = new JSONObject();
                finalJson.put("attendance", arrayAtt);

                String response = ApiClient.postJSON(ApiConfig.SAVE_ATTENDANCE, finalJson.toString());

                runOnUiThread(() -> {
                    try {
                        JSONObject resp = new JSONObject(response);

                        if (resp.getString("status").equals("success")) {
                            CustomToast.success(ClassDetailAdminActivity.this, "Asistencia actualizada");
                            finish();
                        } else {
                            String msg = resp.getString("message");
                            CustomToast.error(ClassDetailAdminActivity.this, msg);
                        }
                    } catch (JSONException e) {
                        CustomToast.error(ClassDetailAdminActivity.this, "Error al guardar asistencia");
                    }
                });
            } catch (Exception e){
                runOnUiThread(() -> CustomToast.error(ClassDetailAdminActivity.this, "Error"));
            }
        }).start();
    }

    private void deleteClass(){
        new Thread(() -> {
            try {
                String url = ApiConfig.DELETE_CLASS + "?classes_id=" + classId;
                String response = ApiClient.get(url);
                JSONObject obj = new JSONObject(response);
                runOnUiThread(() -> {
                    if (obj.optString("status").equals("success")) {
                        CustomToast.success(ClassDetailAdminActivity.this, "Clase eliminada");
                        finish();
                    } else {
                        String msg = obj.optString("message");
                        CustomToast.error(ClassDetailAdminActivity.this, msg);
                    }
                });
            } catch (Exception e){
                runOnUiThread(() -> CustomToast.error(ClassDetailAdminActivity.this, "Error al eliminar la clase"));
            }
        }).start();
    }

    private void exit(){
        if (attChange) {
            AlertDialogs.confirmExitAttendance(
                    ClassDetailAdminActivity.this,
                    () -> saveAttendance(),
                    () -> finish());
        } else {
            finish();
        }
    }
}