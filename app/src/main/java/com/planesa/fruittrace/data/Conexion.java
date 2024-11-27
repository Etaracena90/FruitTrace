package com.planesa.fruittrace.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Conexion {
    private static final String cadenaConexion = "jdbc:jtds:sqlserver://143.208.182.173:11433/BD-APPS_PLANESA;user=sa;password=Sp3c1@l$$";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Connection con = null;

    public Connection conectar() throws SQLException {
        if (con == null || con.isClosed()) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                con = DriverManager.getConnection(cadenaConexion);
                Log.d("Conexion", "Conexión exitosa");
            } catch (ClassNotFoundException e) {
                Log.e("Conexion", "Driver no encontrado: " + e.getMessage(), e);
            } catch (SQLException e) {
                Log.e("Conexion", "Error de conexión: " + e.getMessage(), e);
                throw e;
            }
        }
        return con;
    }

    public void cerrar() {
        if (con != null) {
            try {
                con.close();
                con = null;
                Log.d("Conexion", "Conexión cerrada");
            } catch (SQLException e) {
                Log.e("Conexion", "Error al cerrar la conexión: " + e.getMessage(), e);
            }
        }
    }

    public void probarConexionAsync(Runnable onSuccess, Runnable onFailure) {
        executorService.execute(() -> {
            try {
                conectar();
                cerrar();
                mainHandler.post(onSuccess);
            } catch (Exception e) {
                Log.e("Conexion", "Error durante la prueba de conexión: " + e.getMessage(), e);
                mainHandler.post(onFailure);
            }
        });
    }
}
