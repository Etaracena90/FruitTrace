package com.planesa.fruittrace.dao;

import com.planesa.fruittrace.data.Conexion;
import com.planesa.fruittrace.model.Presentacion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOPresentacion {

    public List<Presentacion> listarPresentacion() throws Exception {
        List<Presentacion> presentacionList = new ArrayList<>();
        Conexion con = new Conexion();
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM tbl_presentacion WHERE visible = 1";

        try {
            cn = con.conectar();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Presentacion presentacion = new Presentacion();
                presentacion.setId_presentacion(rs.getInt(1));
                presentacion.setNombre_Presentacion(rs.getString(2));
                presentacion.setId_estado(rs.getInt(3));
                presentacion.setCodigo_barras(rs.getString(4));
                presentacionList.add(presentacion);
            }
        } catch (Exception e) {
            throw new Exception("Error al listar presentaciones: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (cn != null) cn.close();
        }
        return presentacionList;
    }
}
