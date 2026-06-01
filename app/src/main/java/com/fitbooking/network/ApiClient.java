package com.fitbooking.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private static final int TIMEOUT = 8000;

    //CONECTA AL SERVIDOR
    private static HttpURLConnection createConn(String urlString, String method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection)  url.openConnection();

        conn.setRequestMethod(method);
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);

        if (method.equalsIgnoreCase("POST")){
            conn.setDoOutput(true);
        }
        return conn;
    }

    //LEE LA RESPUESTA DEL SERVIDOR
    private static String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))){
            StringBuilder sb = new StringBuilder();
            String line;
            while ( (line = br.readLine()) !=null){
                sb.append(line);
            }
            br.close();
            return sb.toString();
        }
    }
    //ENVIAR-GUARDAR DATOS
    public static String post(String urlString, String parameters) throws Exception {
        HttpURLConnection conn = null;
        try {
            conn = createConn(urlString, "POST");

            // Enviar parámetros
            OutputStream os = conn.getOutputStream();
            os.write(parameters.getBytes());
            os.flush();
            os.close();

            //OBTENEMOS EL OK DEL SERVIDOR
            int responseCode =conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("API_ERROR", "Código HTTP: " + responseCode);
            }
            return readResponse(conn);

        }  finally {
            if (conn!=null) conn.disconnect();
        }
    }

    //Envio de JSON
    public static String postJSON(String urlString, String json) throws Exception {
        HttpURLConnection conn = null;
        try {
            conn = createConn(urlString, "POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // Enviar parámetros
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();

            //OBTENEMOS EL OK DEL SERVIDOR
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("API_ERROR", "Código HTTP: " + responseCode);
            }
            return readResponse(conn);

        }  finally {
            if (conn!=null) conn.disconnect();
        }
    }

    //CONSULTAR DATOS
    public static String get(String urlString) throws Exception {
        HttpURLConnection conn = null;
        try {
            //CONECTAMOS AL SERVIDOR
            conn = createConn(urlString, "GET");

            //OBTENEMOS EL OK DEL SERVIDOR
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("API_ERROR", "Código HTTP: " + responseCode);
            }
            return readResponse(conn);

        }  finally {
            if (conn!=null) conn.disconnect();
        }
    }
}