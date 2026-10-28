package com.fitbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitbooking.manager.SessionManager;
import com.fitbooking.models.Classes;
import com.fitbooking.R;
import com.fitbooking.adapters.ClassesAdapter;
import com.fitbooking.adapters.DaysAdapter;
import com.fitbooking.network.*;
import com.fitbooking.utils.*;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassCalendarActivity extends AppCompatActivity {
    RecyclerView rvDays, rvClasses;
    TextView tvMonth, tvYear;
    Button btnBack, btnToday;
    MaterialButton btnAction;
    ClassesAdapter classesAdapter;
    List<Classes> classesList = new ArrayList<>();
    LinearLayout emptyClasses;
    private boolean isAdmin;
    DaysAdapter daysAdapter;
    private Calendar selectedDay = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_calendar);

        //Lee si es admin o user para abrir los detalles de las clases
        String rol = SessionManager.getInstance().getRol();
        isAdmin = "admin".equals(rol);

        initViews();
        initRecyclerCalendar();
        initRecyclerClasses();

        //Actualizar mes y año al iniciar
        daysAdapter.listener.onDaySelected(Calendar.getInstance());
        daysAdapter.notifyItemChanged(0);

        actionButton();

        btnToday.setOnClickListener(v -> {
            int todayPos = daysAdapter.getTodayPosition();
            rvDays.smoothScrollToPosition(todayPos); //Mueve el recycler al dia de hoy
            Calendar today = Calendar.getInstance();
            daysAdapter.listener.onDaySelected(today);
            daysAdapter.setSelPos(todayPos);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews(){
        rvClasses = findViewById(R.id.rvClasses);
        rvDays = findViewById(R.id.rvDays);
        tvMonth = findViewById(R.id.tvMonth);
        tvYear = findViewById(R.id.tvYear);
        emptyClasses = findViewById(R.id.emptyClasses);

        btnBack = findViewById(R.id.btnBack);
        btnToday = findViewById(R.id.btnToday);
        btnAction = findViewById(R.id.btnAction);
    }

    private void initRecyclerCalendar(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvDays.setLayoutManager(layoutManager);
        daysAdapter = new DaysAdapter(new DaysAdapter.OnDayClickListener() {
            @Override
            public void onDaySelected(Calendar cal) {
                //Recuerda el dia seleccionado
                selectedDay = (Calendar) cal.clone();
                //MOSTRAR MES EN ESPAÑOL
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new Locale("es", "ES"));
                //Primera letra en mayúscula
                String month = monthFormat.format(cal.getTime());
                month = month.substring(0,1).toUpperCase() + month.substring(1);
                tvMonth.setText(month);
                //AÑO
                tvYear.setText(String.valueOf(cal.get(Calendar.YEAR)));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String date = sdf.format(cal.getTime());
                listClasses(date);
            }
        }, isAdmin);

        rvDays.setAdapter(daysAdapter);
        rvDays.scrollToPosition(daysAdapter.getSelPos());
    }

    private void initRecyclerClasses(){
        classesAdapter = new ClassesAdapter(classesList, new ClassesAdapter.OnClassClickListener(){
            @Override
            public void onClassClick(Classes item) {
                Intent intent;
                if (!isAdmin){
                    intent = new Intent(ClassCalendarActivity.this, ClassDetailUserActivity.class);
                } else {
                    intent = new Intent(ClassCalendarActivity.this, ClassDetailAdminActivity.class);
                }
                intent.putExtra("classItem", item);
                startActivity(intent);
            }
        });

        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        rvClasses.setAdapter(classesAdapter);
    }

    private void actionButton(){
        if (isAdmin){
            String newClass = "Crear clase";
            btnAction.setText(newClass);
            btnAction.setIconResource(R.drawable.ic_class_new);
            btnAction.setOnClickListener(v -> {
                Intent intent = new Intent(ClassCalendarActivity.this, CreateClassActivity.class);
                startActivity(intent);
            });
        }else{
            String myBookings = "Mis reservas";
            btnAction.setText(myBookings);
            btnAction.setIconResource(R.drawable.ic_class_bookings);
            btnAction.setOnClickListener(v -> {
                Intent intent = new Intent(ClassCalendarActivity.this, ClassesListUserActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(selectedDay.getTime());
        listClasses(date);
    }

    private void listClasses(String date){
        new Thread(() -> {
            try {
                String url = ApiConfig.LIST_CLASSES_BYDATE + "?date=" + date;
                String response = ApiClient.get(url);
                JSONArray arr = new JSONObject(response).getJSONArray("data");
                final List<Classes> newList = new ArrayList<>();

                for (int i=0; i<arr.length(); i++){
                    JSONObject obj = arr.getJSONObject(i);

                    Classes classes = new Classes(
                            obj.optInt("id"),
                            obj.optString("classes_date"),
                            obj.optString("classes_time"),
                            obj.optInt("capacity"),
                            obj.optInt("reserved")
                    );
                    newList.add(classes);
                }

                runOnUiThread(() -> {
                    classesList.clear();
                    classesList.addAll(newList);

                    if (classesList.isEmpty()){
                        emptyClasses.setVisibility(View.VISIBLE);
                        rvClasses.setVisibility(View.GONE);
                    } else {
                        emptyClasses.setVisibility(View.GONE);
                        rvClasses.setVisibility(View.VISIBLE);
                    }
                    classesAdapter.notifyDataSetChanged();   //Refresca la lista
                });

            } catch (Exception e){
                final String msg = "Error al obtener classes: " + e.getMessage();
                runOnUiThread(() -> {
                    CustomToast.error(ClassCalendarActivity.this, msg);
                    Log.d("ERROR CLASES", "[" + msg + "]");
                });
            }
        }).start();
    }
}