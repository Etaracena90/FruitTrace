package com.planesa.fruittrace.dao;

import com.planesa.fruittrace.data.Conexion;
import com.planesa.fruittrace.model.Cultivo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOCultivo {

    public List<Cultivo> listarCultivo() throws Exception {
        List<Cultivo> cultivoList = new ArrayList<>();
        Conexion con = new Conexion();
        String sql = "SELECT Id_Cultivo, Nombre_Cultivo, Cultivo_NAV, Cultivo_JP, Id_Estado FROM tbl_cultivo WHERE Id_Estado = 1";

        try (Connection cn = con.conectar(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cultivo cultivo = new Cultivo();
                cultivo.setId_Cultivo(rs.getInt("Id_Cultivo"));
                cultivo.setNombre_Cultivo(rs.getString("Nombre_Cultivo"));
                cultivo.setCultivo_NAV(rs.getString("Cultivo_NAV"));
                cultivo.setCultivo_JP(rs.getString("Cultivo_JP"));
                cultivo.setId_Estado(rs.getInt("Id_Estado"));
                cultivoList.add(cultivo);
            }
        } catch (Exception e) {
            throw new Exception("Error al listar cultivos: " + e.getMessage(), e);
        }
        return cultivoList;
    }
}
