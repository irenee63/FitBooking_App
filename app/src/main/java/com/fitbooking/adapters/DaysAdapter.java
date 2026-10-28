package com.fitbooking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fitbooking.R;

import java.util.Calendar;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {

    private final Calendar baseCalendar = Calendar.getInstance();
    private int selPos = 0;
    public final OnDayClickListener listener;

    private static final String[] DAYS = { "dom","lun", "mar", "mié", "jue", "vie", "sáb"};

    private final int DAYS_BACK = 15;
    private final int DAYS_FORWARD = 30;
    boolean isAdmin;

    public interface OnDayClickListener {
        void onDaySelected(Calendar cal); //Pasa la fecha completa para actulizar también el mes
    }

    public int getSelPos(){
        return selPos;
    }

    public void setSelPos(int pos){
        int old = selPos;
        selPos = pos;
        notifyItemChanged(old);
        notifyItemChanged(pos);
    }

    public int getTodayPosition(){
        if (isAdmin){
            return DAYS_BACK; //Si es Admin la posicion hoy es la misma que DAYS_BACK
        } else {
            return 0;
        }
    }

    public DaysAdapter(OnDayClickListener listener, boolean isAdmin) {
        this.listener = listener;
        this.isAdmin = isAdmin;
        this.selPos = getTodayPosition();
    }

    private Calendar getDate(int position){
        Calendar cal = (Calendar)  baseCalendar.clone(); //empieza desde hoy

        int movPos = position;
        if (isAdmin) {
            movPos = position - DAYS_BACK; //Permite volver atrás
        }

        int added = 0;

        if (movPos>=0){
            //Si mueve la posición hacia delante suma dias
            while (added < movPos){
                cal.add(Calendar.DAY_OF_MONTH, 1);  //avanza 1 dia

                //Salta los viernes, sabado y domingos
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek != Calendar.FRIDAY &&
                        dayOfWeek != Calendar.SATURDAY &&
                        dayOfWeek != Calendar.SUNDAY) {
                    added++;
                }
            }
        } else {
            //Si mueve la posición hacia atrás retrocede dias
            while (added > movPos){
                cal.add(Calendar.DAY_OF_MONTH, -1);  //retrocede 1 dia

                //Salta los viernes, sabado y domingos
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek != Calendar.FRIDAY &&
                        dayOfWeek != Calendar.SATURDAY &&
                        dayOfWeek != Calendar.SUNDAY) {
                    added--;
                }
            }
        }
        return cal;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber;

        public ViewHolder (View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
        }
    }

    @NonNull
    @Override
    public DaysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaysAdapter.ViewHolder holder, int position) {
        Calendar cal = getDate(position);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        holder.tvDayName.setText(DAYS[dayOfWeek - 1]);
        holder.tvDayNumber.setText(String.valueOf(day));

        // selección
        if (position == selPos) {
            holder.tvDayNumber.setBackgroundResource(R.drawable.bg_day_selected);
        } else {
            holder.tvDayNumber.setBackground(null);
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION){ return; }
            int oldPos = selPos;
            selPos = pos;
            notifyItemChanged(oldPos);
            notifyItemChanged(selPos);
            Calendar cal1 = getDate(pos);  //Calcula la fecha según la posicion
            listener.onDaySelected(cal1);
        });
    }

    @Override
    public int getItemCount() {
        if (isAdmin){
            return DAYS_BACK + DAYS_FORWARD;
        } else {
            return DAYS_FORWARD;
        }
    }
}