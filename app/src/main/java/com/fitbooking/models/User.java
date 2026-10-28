package com.fitbooking.models;
import androidx.annotation.NonNull;

public class User {
    private int id;
    private String rol;
    private String email;
    private String password;
    private String fullname;
    private String birthday;
    private int totalBalance;
    private int availBalance;

    public User(int id, String rol, String email, String password, String fullname, String birthday, int totalBalance, int availBalance) {
        this.id = id;
        this.rol = rol;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.birthday = birthday;
        this.totalBalance = totalBalance;
        this.availBalance = availBalance;
    }

    public User(int id, String fullname) {
        this.id = id;
        this.fullname = fullname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRol() {
        return rol;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getTotalBalance() {
        return totalBalance;
    }

    public int getAvailBalance() {
        return availBalance;
    }

    @NonNull
    @Override
    public String toString() {
        return fullname;
    }
}
