package com.planesa.fruittrace.dao;

import com.planesa.fruittrace.data.Conexion;
import com.planesa.fruittrace.model.Destino;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAODestino {

    public List<Destino> listarDestino() throws Exception {
        List<Destino> destinoList = new ArrayList<>();
        Conexion con = new Conexion();
        Connection cn = null;
        Statement st = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM tbl_destino";

        try {
            cn = con.conectar();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Destino destino = new Destino();
                destino.setId_destino(rs.getInt(1));
                destino.setCodigo_barra(rs.getString(2));
                destino.setNombre_destino(rs.getString(3));
                destino.setId_estado(rs.getInt(4));
                destinoList.add(destino);
            }
        } catch (Exception e) {
            throw new Exception("Error al listar destinos: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (cn != null) cn.close();
        }
        return destinoList;
    }
}
