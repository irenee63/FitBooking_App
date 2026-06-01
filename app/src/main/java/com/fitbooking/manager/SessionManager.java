package com.fitbooking.manager;

public class SessionManager {

    private static SessionManager instance;
    private int userId;
    private String rol;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }

    public void clear() {
        userId = 0;
        rol = null;
    }
}
