package com.fitbooking.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    private static final SimpleDateFormat MYSQL_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    //Formato de fecha de bd a mostrar dd/mm/yyyy
    public static String toDisplay(String mysqlDate) {
        try {
            Date date = MYSQL_FORMAT.parse(mysqlDate);
            assert date != null;
            return DISPLAY_FORMAT.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    //Formato de fecha para enviar a base de datos yyyy-mm-dd
    public static String toSQL(String inputDate) {
        try {
            Date date = DISPLAY_FORMAT.parse(inputDate);
            assert date != null;
            return MYSQL_FORMAT.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    //Formato largo: dia de la semana, dia, mes y año
    public static String toLongDisplay(String mysqlDate) {
        try {
            //Convertir string en date
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(mysqlDate);

            //Sábado, 2 mayo 2026
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("es", "ES"));
            String longFormat = outputFormat.format(parsedDate);

            //Primera letra mayúscula
            longFormat = longFormat.substring(0,1).toUpperCase() + longFormat.substring(1);

            return longFormat;

        } catch (Exception e) {
            return mysqlDate;
        }
    }
}
