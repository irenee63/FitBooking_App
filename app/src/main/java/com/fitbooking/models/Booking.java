package com.fitbooking.models;

public class Booking {
    int id;
    int users_id;
    int classes_id;
    String booking_date;
    int attendance;
    String fullname;
    public Booking(int id, int users_id, int classes_id, String booking_date, int attendance) {
        this.id = id;
        this.users_id = users_id;
        this.classes_id = classes_id;
        this.booking_date = booking_date;
        this.attendance = attendance;
    }

    public Booking() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    public int getClasses_id() {
        return classes_id;
    }

    public void setClasses_id(int classes_id) {
        this.classes_id = classes_id;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
