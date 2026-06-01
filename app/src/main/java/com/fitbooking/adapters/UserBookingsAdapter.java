package com.fitbooking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fitbooking.models.Classes;
import com.fitbooking.R;
import com.fitbooking.utils.DateConverter;

import java.util.List;

public class UserBookingsAdapter extends RecyclerView.Adapter<UserBookingsAdapter.ViewHolder> {

    private List<Classes> list;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Classes item);
    }

    public UserBookingsAdapter(List<Classes> list, OnBookingClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvBookingDate);
            tvTime = itemView.findViewById(R.id.tvBookingTime);
        }
    }

    @NonNull
    @Override
    public UserBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserBookingsAdapter.ViewHolder holder, int position) {
        Classes item = list.get(position);

        String rawTime = item.getClass_time();
        String time = rawTime.substring(0, 5); //Mostrar la hora 00:00
        holder.tvTime.setText(time);

        String longDate  = DateConverter.toLongDisplay(item.getClass_date());
        holder.tvDate.setText(longDate);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBookingClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
