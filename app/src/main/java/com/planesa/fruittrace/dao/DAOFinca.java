package com.planesa.fruittrace.dao;

import com.planesa.fruittrace.data.Conexion;
import com.planesa.fruittrace.model.Finca;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOFinca {

    public List<Finca> listarFinca() throws Exception {
        List<Finca> fincaList = new ArrayList<>();
        Conexion con = new Conexion();
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = "SELECT Id_finca, Nombre_Finca, Area_Finca, cc_NAV, cc_JP, Estado FROM tbl_finca WHERE estado = 1";

        try {
            cn = con.conectar();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Finca finca = new Finca();
                finca.setId_Finca(rs.getInt("Id_finca"));
                finca.setNombre_Finca(rs.getString("Nombre_Finca"));
                finca.setArea_Finca(rs.getDouble("Area_Finca"));
                finca.setCultivo_NAV(rs.getString("cc_NAV"));
                finca.setCultivo_JP(rs.getString("cc_JP"));
                finca.setEstado(rs.getInt("Estado"));
                fincaList.add(finca);
            }
        } catch (Exception e) {
            throw new Exception("Error al listar fincas: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (cn != null) cn.close();
        }
        return fincaList;
    }
}
