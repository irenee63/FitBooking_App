package com.fitbooking.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class DatePicker {
    public static void bithdatePicker(Context context, EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            //Convierte el formato de la fecha seleccionada en datepicker en dd/mm/yyyy
            DatePickerDialog dialog = new DatePickerDialog(
                    context,
                    (android.widget.DatePicker view, int y, int m, int d) -> {
                        String formatted = String.format("%02d/%02d/%04d", d, m + 1, y);
                        editText.setText(formatted);
                    },
                    year, month, day
            );

            //No permite seleccionar fechas futuras
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });
    }

    public static void classDatePicker(Context context, EditText editText){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //Convierte el formato de la fecha seleccionada en datepicker en dd/mm/yyyy
                DatePickerDialog dialog = new DatePickerDialog(
                        context,
                        (android.widget.DatePicker view, int y, int m, int d) -> {
                            Calendar selected = Calendar.getInstance();
                            selected.set(y, m, d);
                            int dow = selected.get(Calendar.DAY_OF_WEEK);

                            //No permite seleccionar viernes, sábado ni domingo.
                            if (dow == Calendar.FRIDAY ||
                                    dow == Calendar.SATURDAY ||
                                    dow == Calendar.SUNDAY) {

                                Toast.makeText(context, "Solo puedes seleccionar lunes a jueves", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String formatted = String.format("%02d/%02d/%04d", d, m + 1, y);
                            editText.setText(formatted);
                        },
                        year, month, day
                );

                //No permite seleccionar fechas pasadas
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });
    }
}