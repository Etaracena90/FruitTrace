package com.planesa.fruittrace.dao;

import com.planesa.fruittrace.data.Conexion;
import com.planesa.fruittrace.model.Etiqueta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOEtiqueta {

    public List<Etiqueta> listarEtiqueta() throws Exception {
        List<Etiqueta> etiquetaList = new ArrayList<>();
        Conexion con = new Conexion();
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM tbl_etiqueta";

        try {
            cn = con.conectar();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Etiqueta etiqueta = new Etiqueta();
                etiqueta.setId_etiqueta(rs.getInt(1));
                etiqueta.setNombre_etiqueta(rs.getString(2));
                etiqueta.setId_estado(rs.getInt(3));
                etiqueta.setCodigo_barras(rs.getString(4));
                etiquetaList.add(etiqueta);
            }
        } catch (Exception e) {
            throw new Exception("Error al listar etiquetas: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (cn != null) cn.close();
        }
        return etiquetaList;
    }
}
