package com.fitbooking.models;

import java.io.Serializable;

public class Classes implements Serializable{
    private int id;
    private String class_date;
    private String class_time;
    private int capacity;
    private int reserved;

    public Classes(int id, String class_date, String class_time, int capacity, int reserved) {
        this.id = id;
        this.class_date = class_date;
        this.class_time = class_time;
        this.capacity = capacity;
        this.reserved = reserved;
    }

    public Classes(int id, String class_date, String class_time) {
        this.id = id;
        this.class_date = class_date;
        this.class_time = class_time;
    }

    public Classes (String class_time, int reserved, int capacity){
        this.class_time = class_time;
        this.reserved = reserved;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClass_date() {
        return class_date;
    }

    public void setClass_date(String class_date) {
        this.class_date = class_date;
    }

    public String getClass_time() {

        return class_time;
    }

    public void setClass_time(String class_time) {
        this.class_time = class_time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }
}
