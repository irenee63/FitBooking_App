package com.fitbooking.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitbooking.R;

public class CustomToast {
    private static void show (Context context, String status, String msg){
        LayoutInflater infl = LayoutInflater.from(context);
        View layout = infl.inflate(R.layout.custom_toast, null);

        TextView tv = layout.findViewById(R.id.toastText);
        ImageView icon = layout.findViewById(R.id.toastIcon);
        tv.setText(msg);

        switch (status){
            case("success"):
                icon.setImageResource(R.drawable.ic_toast_success);
                break;

            case("error"):
                icon.setImageResource(R.drawable.ic_toast_error);
                break;

            case("warning"):
                icon.setImageResource(R.drawable.ic_toast_warning);
                break;

            default:
                icon.setImageResource(R.drawable.ic_fitbooking_logo);
        }
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void success(Context context, String msg){
        show(context, "success", msg);
    }

    public static void error(Context context, String msg){
        show(context, "error", msg);
    }

    public static void warning(Context context, String msg){
        show(context, "warning", msg);
    }
}