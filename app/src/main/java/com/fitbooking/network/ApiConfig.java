package com.fitbooking.network;

public class ApiConfig {

    public static final String BASE_URL = "http://fitbooking.atwebpages.com/";

    public static final String LOGIN = BASE_URL + "login.php";

    //USERS
    public static final String LIST_USERS = BASE_URL + "users/usersList.php";
    public static final String GET_USER = BASE_URL + "users/getUser.php";
    public static final String UPDATE_USER = BASE_URL+ "users/updateUser.php";
    public static final String CREATE_USER = BASE_URL + "users/createUser.php";
    public static final String DELETE_USER = BASE_URL +"users/deleteUser.php";
    public static final String RECOVER_PASSWORD = BASE_URL +"users/recoverPassword.php";

    //CLASSES
    public static final String LIST_CLASSES_BYDATE = BASE_URL + "classes/getClassDate.php";
    public static final String GET_CLASS = BASE_URL + "classes/getClassId.php";
    public static final String GET_USERS_IN_CLASS = BASE_URL + "classes/getUsersInClass.php";
    public static final String LIST_CLASSES_BYUSER = BASE_URL + "classes/getClassUser.php";
    public static final String DELETE_CLASS = BASE_URL + "classes/deleteClass.php";
    public static final String CREATE_CLASS = BASE_URL + "classes/createClass.php";

    //BOOKINGS
    public static final String CREATE_BOOKING = BASE_URL + "booking/createBooking.php";
    public static final String CHECK_BOOKING = BASE_URL + "booking/checkBooking.php";
    public static final String CANCEL_BOOKING = BASE_URL + "booking/cancelBooking.php";
    public static final String SAVE_ATTENDANCE = BASE_URL + "booking/saveAttendance.php";
}
