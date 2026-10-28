package com.fitbooking.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogs {
    public static void confirmDeleteClass(Context context, Runnable onConfirm){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar clase");
        builder.setMessage("¿Seguro que deseas eliminar esta clase? Esta acción no se puede deshacer.");

        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            if (onConfirm != null){
                onConfirm.run();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void confirmExitAttendance(Context context, Runnable onSave, Runnable onExit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cambios sin guardar");
        builder.setMessage("Asistencia modificada. ¿Guardar antes de salir?");

        builder.setPositiveButton("Guardar", (dialog, which) -> onSave.run());
        builder.setNegativeButton("Salir sin guardar", (dialog, which) -> onExit.run());
        builder.setNeutralButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}