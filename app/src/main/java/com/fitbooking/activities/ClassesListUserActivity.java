package com.fitbooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitbooking.models.Classes;
import com.fitbooking.R;
import com.fitbooking.adapters.UserBookingsAdapter;
import com.fitbooking.network.*;
import com.fitbooking.utils.CustomToast;
import com.fitbooking.manager.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClassesListUserActivity extends AppCompatActivity {
    TextView tvEmpty;
    Button btnBack;
    RecyclerView rvUserBookings;
    UserBookingsAdapter adapter;
    ArrayList<Classes> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_list_user);

        int userId = SessionManager.getInstance().getUserId();

        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        initRecyclerBookings();
        loadBookings(userId);

        btnBack.setOnClickListener(v -> finish());
    }

    private void initRecyclerBookings(){
        rvUserBookings = findViewById(R.id.rvUserBookings);
        rvUserBookings.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserBookingsAdapter(list, new UserBookingsAdapter.OnBookingClickListener() {
            @Override
            public void onBookingClick(Classes item) {
                Intent intent = new Intent(ClassesListUserActivity.this, ClassDetailUserActivity.class);
                intent.putExtra("classItem", item);
                startActivity(intent);
            }
        });

        rvUserBookings.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = SessionManager.getInstance().getUserId();
        loadBookings(userId);
    }

    private void loadBookings(int userId){
        new Thread(() -> {
            try{
                String url = ApiConfig.LIST_CLASSES_BYUSER + "?users_id=" + userId;
                String response = ApiClient.get(url);
                JSONObject obj = new JSONObject(response);

                if (obj.getString("status").equals("success")){
                    JSONArray arr = obj.getJSONArray("data");
                    list.clear();
                    for(int i=0; i<arr.length(); i++){
                        JSONObject item = arr.getJSONObject(i);

                        Classes classes = new Classes(
                                item.optInt("id"),
                                item.optString("classes_date"),
                                item.optString("classes_time")
                        );
                        list.add(classes);
                    }

                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        if (list.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            rvUserBookings.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            rvUserBookings.setVisibility(View.VISIBLE);
                        }
                    });
                }

            } catch (Exception e){
                runOnUiThread(() -> CustomToast.error(ClassesListUserActivity.this, "Error al cargar reservas"));
            }
        }).start();
    }
}