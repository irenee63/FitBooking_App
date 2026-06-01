package com.fitbooking.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fitbooking.models.Classes;
import com.fitbooking.R;

import java.util.List;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {

    private List<Classes> list;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(Classes item);
    }

    public ClassesAdapter(List<Classes> list, OnClassClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvCapacity, tvStatus;
        ImageView imgStatus;
        public ViewHolder(View itemView){
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgStatus = itemView.findViewById(R.id.imgStatus);
        }
    }

    @NonNull
    @Override
    public ClassesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassesAdapter.ViewHolder holder, int position) {
        Classes item = list.get(position);

        String rawTime = item.getClass_time();
        String time = rawTime.substring(0, 5); //Mostrar la hora 00:00
        holder.tvTime.setText(time);
        holder.tvCapacity.setText(item.getReserved() + " / " + item.getCapacity());

        boolean available = item.getReserved() < item.getCapacity();
        if (available) {
            holder.imgStatus.setImageResource(R.drawable.ic_available);
            holder.tvStatus.setText("Disponible");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_full);
            holder.tvStatus.setText("Clase completa");
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClassClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}