package com.fitbooking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fitbooking.models.Booking;
import com.fitbooking.R;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder>{

    private final List<Booking> list;
    private final String fullDateTime;

    public AttendanceAdapter(List<Booking> list, String fullDateTime) {
        this.list = list;
        this.fullDateTime = fullDateTime;
    }

    private boolean classPassed() {
        try {
            Date classDate  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(fullDateTime);
            return classDate.getTime() < System.currentTimeMillis();
        }catch (Exception e){
            return false;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        MaterialButtonToggleGroup attGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            attGroup = itemView.findViewById(R.id.attGroup);
        }
    }

    @NonNull
    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_attendance, parent, false);
        return new ViewHolder(view);
    }

    public interface OnAttendanceChangeListener {
        void onAttendanceChanged();
    }

    private OnAttendanceChangeListener listener;

    public void setOnAttendanceChangeListener(OnAttendanceChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, int position) {
        Booking item = list.get(position);
        holder.tvUserName.setText(item.getFullname());

        boolean isClassPassed = classPassed();
        if(isClassPassed){
            holder.attGroup.setVisibility(View.VISIBLE);
        } else {
            holder.attGroup.setVisibility(View.GONE);
        }

        //Quitar listeners antes de restaurar estado
        holder.attGroup.clearOnButtonCheckedListeners();

        switch (item.getAttendance()){
            case 0: holder.attGroup.check(R.id.btnUnknown); break;
            case 1: holder.attGroup.check(R.id.btnPresent);break;
            case 2: holder.attGroup.check(R.id.btnAbsent); break;
        }

        holder.attGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (!isChecked) return;

                if (checkedId == R.id.btnUnknown) {
                    item.setAttendance(0);
                } else if (checkedId == R.id.btnPresent) {
                    item.setAttendance(1);
                } else if (checkedId == R.id.btnAbsent) {
                    item.setAttendance(2);
                }
                if (listener != null) listener.onAttendanceChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}