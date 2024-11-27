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
        String sql = "SELECT * FROM tbl_presentacion WHERE visible = 1";

        try (Connection cn = con.conectar(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
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
        }
        return presentacionList;
    }
}
