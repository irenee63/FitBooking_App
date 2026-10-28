package com.fitbooking.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fitbooking.R;
import com.fitbooking.network.ApiClient;
import com.fitbooking.network.ApiConfig;
import com.fitbooking.utils.CustomToast;
import com.fitbooking.utils.DateConverter;
import com.fitbooking.utils.DatePicker;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CreateClassActivity extends AppCompatActivity {

    private TextView tvTime, tvEndDateText;
    private EditText etCapacity, etStartDate, etEndDate;
    private CheckBox cbMon, cbTue, cbWed, cbThu, cbSingleClass;
    private Button btBack, btCreateClass;
    private String selectedTime = null;
    private LinearLayout containerDays;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        initViews();

        tvTime.setOnClickListener(v -> showTimePicker());

        cbSingleClass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Bloquear fecha fin
                    String selectDate = "Seleccionar fecha";
                    etEndDate.setText(selectDate);
                    etEndDate.setVisibility(View.GONE);
                    tvEndDateText.setVisibility(View.GONE);

                    //Limpiar días
                    cbMon.setChecked(false);
                    cbTue.setChecked(false);
                    cbWed.setChecked(false);
                    cbThu.setChecked(false);
                    containerDays.setVisibility(View.GONE);
                } else {
                    containerDays.setVisibility(View.VISIBLE);
                    etEndDate.setVisibility(View.VISIBLE);
                    tvEndDateText.setVisibility(View.VISIBLE);
                }
            }
        });

        btBack.setOnClickListener(v -> finish());
        btCreateClass.setOnClickListener(v -> createClasses());
    }

    private void initViews() {
        etStartDate = findViewById(R.id.etStartDate);
        tvEndDateText = findViewById(R.id.tvEndDateText);
        etEndDate = findViewById(R.id.etEndDate);
        tvTime = findViewById(R.id.tvTime);
        etCapacity = findViewById(R.id.etCapacity);
        containerDays = findViewById(R.id.containerDays);
        cbMon = findViewById(R.id.cbMon);
        cbTue = findViewById(R.id.cbTue);
        cbWed = findViewById(R.id.cbWed);
        cbThu = findViewById(R.id.cbThu);
        btBack = findViewById(R.id.btBack);
        btCreateClass = findViewById(R.id.btCreateClass);
        cbSingleClass = findViewById(R.id.cbSingleClass);
        progressBar = findViewById(R.id.progressBar);

        DatePicker.classDatePicker(this, etStartDate);
        DatePicker.classDatePicker(this, etEndDate);
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        int hour   = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(
                this,
                (view, h, m) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", h, m);
                    tvTime.setText(selectedTime);
                },
                hour, minute, true
        );
        tpd.show();
    }

    private void createClasses(){
        String startDate = etStartDate.getText().toString();
        String endDate = etEndDate.getText().toString();
        String capacityStr = etCapacity.getText().toString().trim();
        boolean isSingleClass = cbSingleClass.isChecked();

        if (startDate.equals("Seleccionar fecha") || selectedTime == null || capacityStr.isEmpty()) {
            CustomToast.warning(CreateClassActivity.this,"Completa todos los campos");
            return;
        }

        if (!isSingleClass && endDate.equals("Seleccionar fecha")) {
            CustomToast.warning(CreateClassActivity.this,"Selecciona la fecha fin");
            return;
        }

        int capacity = Integer.parseInt(capacityStr);

        //Crea ArrayList con los dias seleccionados
        ArrayList<String> days = new ArrayList<>();
        if (cbMon.isChecked()) days.add("mon");
        if (cbTue.isChecked()) days.add("tue");
        if (cbWed.isChecked()) days.add("wed");
        if (cbThu.isChecked()) days.add("thu");

        if (!isSingleClass && days.isEmpty()) {
            CustomToast.warning(CreateClassActivity.this,"Selecciona al menos un día");
            return;
        }

        //Convertir fechas al formato MySQL
        String startDateSQL = DateConverter.toSQL(startDate);
        String endDateSQL   = DateConverter.toSQL(endDate);

        progressBar.setVisibility(View.VISIBLE);
        btCreateClass.setEnabled(false);

        new Thread(() -> {
            try{
                String params = "start_date=" + URLEncoder.encode(startDateSQL, "UTF-8") +
                        "&time=" + URLEncoder.encode(selectedTime,"UTF-8") +
                        "&capacity=" + URLEncoder.encode(String.valueOf(capacity), "UTF-8");
                if (!isSingleClass){
                    params += "&end_date=" + URLEncoder.encode(endDateSQL, "UTF-8");
                    for (int i=0; i < days.size(); i++){
                        String d = days.get(i);
                        params += "&days[]=" + URLEncoder.encode(d, "UTF-8");
                    }
                }

                String response = ApiClient.post(ApiConfig.CREATE_CLASS, params);
                JSONObject obj = new JSONObject(response);
                String status = obj.optString("status");
                String msg = obj.optString("message");
                String num = obj.optString("error_code");

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btCreateClass.setEnabled(true);

                    if (status.equals("success")) {
                        CustomToast.success(CreateClassActivity.this, msg);
                        finish(); //Vuelve al listado
                    }
                    if (num.equals("1062")){
                        CustomToast.warning(CreateClassActivity.this,"Clase duplicada, ya hay una clase creada con esos datos");
                    }
                });

            } catch (Exception e){
                String msg = e.getMessage();
                runOnUiThread(() -> CustomToast.error(CreateClassActivity.this, msg));
            }
        }).start();
    }
}