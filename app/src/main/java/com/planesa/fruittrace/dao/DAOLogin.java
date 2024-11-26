package com.planesa.fruittrace.dao;

import com.planesa.fruittrace.data.Conexion;
import com.planesa.fruittrace.model.Cargo;
import com.planesa.fruittrace.model.Users;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import android.util.Log;

public class DAOLogin {

    public Users identificar(Users user) {
        Users usu = null;
        Conexion con = new Conexion();
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = "SELECT u.Id, c.Nombre_Cargo FROM tbl_users u INNER JOIN tbl_cargo c ON u.Tipo = c.Id_Cargo " +
                "WHERE u.Estado = 1 AND u.UserName = '" + user.getUsers() + "' AND u.Password = '" + user.getPassword() + "' AND u.Estado = 1";

        try {
            cn = con.conectar();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                usu = new Users();
                usu.setId(rs.getInt("Id"));
                usu.setUsers(user.getUsers());
                usu.setCargo(new Cargo());
                usu.getCargo().setNombreCargo(rs.getString("Nombre_Cargo"));
                usu.setEstado(1);
            }
        } catch (Exception e) {
            Log.e("DAOLogin", "Error: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
                if (st != null && !st.isClosed()) st.close();
                if (cn != null && !cn.isClosed()) cn.close();
            } catch (SQLException e) {
                Log.e("DAOLogin", "Error al cerrar recursos: " + e.getMessage(), e);
            }
        }
        return usu;
    }

    public int validar(String usuario, String contra) throws SQLException, ClassNotFoundException {
        int tipo = 0;
        Conexion con = new Conexion();
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = "SELECT tipo FROM tbl_users WHERE UserName = '" + usuario + "' AND Password = '" + contra + "' AND Estado = 1";

        try {
            cn = con.conectar();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                tipo = rs.getInt(1);
            }
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
                if (st != null && !st.isClosed()) st.close();
                if (cn != null && !cn.isClosed()) cn.close();
            } catch (SQLException e) {
                Log.e("DAOLogin", "Error al cerrar recursos: " + e.getMessage(), e);
            }
        }
        return tipo;
    }
}
