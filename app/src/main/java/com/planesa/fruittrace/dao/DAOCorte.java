package com.planesa.fruittrace.dao;

import com.planesa.fruittrace.data.Conexion;
import com.planesa.fruittrace.model.Corte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOCorte {
    private Conexion con;
    private Connection cn;
    private PreparedStatement ps;
    private ResultSet rs;

    public List<Corte> listarCortePorUsuario(String usuario) throws Exception {
        List<Corte> listcorte = new ArrayList<>();
        con = new Conexion();

        String sql = "SELECT c.id_enc_corte, " +
                "FORMAT(c.fecha, 'yyyy/MM/dd') AS fecha, " +
                "c.apuntador, f.Nombre_Finca, p.Nombre_presentacion, " +
                "c.cultivo, de.descripcion, et.Nombre_etiqueta " +
                "FROM [dbo].[tbl_enc_corte] c " +
                "INNER JOIN tbl_finca f ON f.Id_Finca = c.finca " +
                "INNER JOIN tbl_presentacion p ON p.codigo_barrap = c.presentacion " +
                "INNER JOIN tbl_destino de ON de.codigo_barrad = c.destino " +
                "INNER JOIN tbl_etiqueta et ON et.codigo_barrae = c.etiqueta " +
                "WHERE c.Fecha <= CONVERT(DATE, GETDATE()) AND " +
                "c.apuntador = ? AND c.Estado = 4 AND " +
                "Nombre_presentacion NOT LIKE '%GRANEL%'";

        try {
            cn = con.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, usuario);
            rs = ps.executeQuery();

            while (rs.next()) {
                Corte corte = new Corte();
                corte.setId_enc_corte(rs.getInt("id_enc_corte"));
                corte.setFecha(rs.getString("fecha"));
                corte.setApuntador(rs.getString("apuntador"));
                corte.setNombrefinca(rs.getString("Nombre_Finca"));
                corte.setPresentacion(rs.getString("Nombre_presentacion"));
                corte.setNombre_cultivo(rs.getString("cultivo"));
                corte.setDestino(rs.getString("descripcion"));
                corte.setEtiqueta(rs.getString("Nombre_etiqueta"));
                listcorte.add(corte);
            }
        } catch (Exception e) {
            throw new Exception("Error al listar cortes: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (cn != null) cn.close();
        }
        return listcorte;
    }

    public void registrar(Corte corte) throws Exception {
        String sql = "INSERT INTO [dbo].[tbl_enc_corte] " +
                "([fecha], [apuntador], [finca], [presentacion], [destino], [etiqueta], [cultivo], [dia], [Estado]) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";

        con = new Conexion();
        Connection cn = null;
        PreparedStatement ps = null;

        try {
            cn = con.conectar();
            ps = cn.prepareStatement(sql);

            ps.setString(1, corte.getFecha());
            ps.setString(2, corte.getApuntador());
            ps.setInt(3, corte.getFinca());
            ps.setString(4, corte.getPresentacion());
            ps.setString(5, corte.getDestino());
            ps.setString(6, corte.getEtiqueta());
            ps.setString(7, corte.getCultivo());
            ps.setString(8, corte.getDia());
            ps.setInt(9, corte.getEstado());


            ps.executeUpdate();
            System.out.println("Registro insertado exitosamente.");

        } catch (SQLException e) {
            throw new Exception("Error al registrar el corte: " + e.getMessage(), e);
        } finally {
            if (ps != null) ps.close();
            if (cn != null) cn.close();
        }
    }


    public int RegistrarEscanCajasCorte(Corte corte) {
        int resultado = 0; // Variable de retorno
        Conexion con = new Conexion(); // Instancia de conexión
        Connection cn = null;
        PreparedStatement ps = null;

        String sql = "INSERT INTO [dbo].[tbl_det_corte](" +
                "[Id_enc_corte], [fecha], [lote], [finca], [cultivo], " +
                "[codigo_cortador], [codigo_clasificador], [destino], [presentacion], " +
                "[etiqueta], [variedad], [cantida], [unidad_medida], [Dia]) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CASE " +
                "WHEN DATENAME(dw, ?) IN ('Saturday', 'Sunday') THEN 'FESTIVO' " +
                "ELSE 'NORMAL' END)";

        try {
            cn = con.conectar();
            ps = cn.prepareStatement(sql);

            // Agregar datos de prueba
            ps.setInt(1, corte.getId_enc_corte()); // Id_enc_corte (dato de prueba)
            ps.setString(2, corte.getFecha()); // Fecha (dato de prueba)
            ps.setString(3, corte.getLote()); // Lote (dato del objeto Corte)
            ps.setString(4, corte.getNombrefinca()); // Finca (dato de prueba)
            ps.setString(5, corte.getNombre_cultivo()); // Cultivo (dato de prueba)
            ps.setString(6, corte.getCodigo_cortador()); // Código Cortador (dato del objeto Corte)
            ps.setString(7, corte.getCodigo_clasificador()); // Código Clasificador (dato del objeto Corte)
            ps.setString(8, corte.getDestino()); // Destino (dato de prueba)
            ps.setString(9, corte.getPresentacion()); // Presentación (dato de prueba)
            ps.setString(10, corte.getEtiqueta()); // Etiqueta (dato de prueba)
            ps.setString(11, corte.getVariedad()); // Variedad (dato del objeto Corte)
            ps.setDouble(12, corte.getCantidad()); // Cantidad (dato de prueba)
            ps.setString(13, corte.getUnidad_medida()); // Unidad de Medida (dato de prueba)
            ps.setString(14, "2024-11-20"); // Fecha para determinar el Día (dato de prueba)

            int filasAfectadas = ps.executeUpdate();
            resultado = (filasAfectadas > 0) ? 1 : 0; // Indicar éxito o fallo

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (cn != null) cn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return resultado;
    }

    public Corte leercorte(int idCorte, String apuntador) throws Exception {
        Corte corte = null;
        String sql = "SELECT a.id_enc_corte, FORMAT(a.fecha, 'yyyy/MM/dd') AS fecha, a.apuntador, " +
                "f.Nombre_Finca, SUM(CASE WHEN b.unidad_medida != 'LIBRA' THEN b.cantida ELSE 0 END) AS cantidad, " +
                "a.presentacion, a.destino, a.etiqueta, a.cultivo, a.dia, a.codigo_pte " +
                "FROM [dbo].[tbl_enc_corte] a " +
                "LEFT JOIN [dbo].[tbl_det_corte] b ON a.id_enc_corte = b.Id_enc_corte " +
                "INNER JOIN tbl_finca f ON f.Id_Finca = a.finca " +
                "WHERE a.id_enc_corte = ? AND a.apuntador = ? " +
                "GROUP BY a.id_enc_corte, a.fecha, a.apuntador, f.Nombre_Finca, a.presentacion, " +
                "a.destino, a.etiqueta, a.cultivo, a.dia, a.codigo_pte";

        try (Connection cn = new Conexion().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCorte);
            ps.setString(2, apuntador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    corte = new Corte();
                    corte.setId_enc_corte(rs.getInt("id_enc_corte"));
                    corte.setFecha(rs.getString("fecha"));
                    corte.setApuntador(rs.getString("apuntador"));
                    corte.setNombrefinca(rs.getString("Nombre_Finca"));
                    corte.setTotal_cajas(rs.getString("cantidad"));
                    corte.setPresentacion(rs.getString("presentacion"));
                    corte.setDestino(rs.getString("destino"));
                    corte.setEtiqueta(rs.getString("etiqueta"));
                    corte.setNombre_cultivo(rs.getString("cultivo"));
                    corte.setDia(rs.getString("dia"));
                    corte.setCodigo_pte(rs.getString("codigo_pte"));
                }
            }
        }
        return corte;
    }

    public void actualizarEstadoCorte(int idCorte, int nuevoEstado) throws Exception {
        String sql = "UPDATE tbl_enc_corte SET Estado = ? WHERE id_enc_corte = ?";
        try (Connection cn = new Conexion().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nuevoEstado);
            ps.setInt(2, idCorte);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar el estado del corte: " + e.getMessage(), e);
        }
    }

    public boolean validarLote(String codigoLote) throws Exception {
        boolean existe = false;
        String sql = "SELECT COUNT(*) AS cantidad FROM tbl_lote_e WHERE Codigo_Lote_e = ?";
        try (Connection cn = new Conexion().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoLote);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("cantidad") > 0) {
                    existe = true;
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al validar el lote: " + e.getMessage(), e);
        }
        return existe;
    }

    public boolean validarCortador(String codigoCortador) throws Exception {
        boolean cortadorValido = false;
        String sql = "SELECT COUNT(*) FROM [BD-APPS_PLANESA].[dbo].[tbl_empleado] WHERE Codigo_empleado = ? AND Grupo = 'CORTE'";
        Conexion con = new Conexion();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = con.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, codigoCortador);
            rs = ps.executeQuery();

            if (rs.next()) {
                cortadorValido = rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new Exception("Error al validar el cortador: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (cn != null) cn.close();
        }
        return cortadorValido;
    }

    public boolean validarVariedad(String codigoVariedad) throws Exception {
        boolean variedadValido = false;
        String sql = "SELECT COUNT(*) FROM [BD-APPS_PLANESA].[dbo].[tbl_vcultivo] where vcb = ?";
        Conexion con = new Conexion();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = con.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, codigoVariedad);
            rs = ps.executeQuery();

            if (rs.next()) {
                variedadValido = rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new Exception("Error al validar el cortador: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (cn != null) cn.close();
        }
        return variedadValido;
    }

    public boolean validarClasificador(String codigoClasificador) throws Exception {
        boolean validarClasificador = false;
        String sql = "SELECT COUNT(*) FROM [BD-APPS_PLANESA].[dbo].[tbl_empleado] WHERE Codigo_empleado = ? AND Grupo = 'CLASIFICADOR'";
        Conexion con = new Conexion();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            cn = con.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, codigoClasificador);
            rs = ps.executeQuery();

            if (rs.next()) {
                validarClasificador = rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new Exception("Error al validar el cortador: " + e.getMessage(), e);
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (cn != null) cn.close();
        }
        return validarClasificador;
    }

    public Double obtenerCantidadCajas(int idCorte) throws Exception {
        double cantidadCajas = 0.0; // Inicializar la cantidad de cajas
        Conexion conexion = new Conexion(); // Asegúrate de inicializar la conexión aquí
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT SUM(cantida) AS cantidad FROM [BD-APPS_PLANESA].[dbo].[tbl_det_corte] WHERE Id_enc_corte = ?";

        try {
            cn = conexion.conectar(); // Crear la conexión
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idCorte); // Pasar el ID del documento como parámetro
            rs = ps.executeQuery();

            if (rs.next()) {
                cantidadCajas = rs.getDouble("cantidad"); // Obtener la cantidad de cajas
                if (rs.wasNull()) {
                    cantidadCajas = 0.0; // Asegurar que no se asignen valores nulos
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener la cantidad de cajas: " + e.getMessage(), e);
        } finally {
            // Cerrar los recursos
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ps != null) try {
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cn != null) try {
                cn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cantidadCajas;
    }

    public List<Corte> listarCajasEscaneadasPorCortador(int idEncCorte) throws Exception {
        List<Corte> lista = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT " +
                "det.codigo_cortador, " +
                "emp.Nombre AS nombre_cortador, " +
                "pre.Nombre_presentacion, " +
                "dest.descripcion AS destino, " +
                "det.unidad_medida, " +
                "SUM(det.cantida) AS cantidad, " +
                "l.Codigo_Lote_e AS lote, " +
                "vc.Nombre_VCultivo AS variedad, " +
                "det.codigo_clasificador " +
                "FROM tbl_det_corte det " +
                "INNER JOIN tbl_empleado emp ON det.codigo_cortador = emp.Codigo_empleado " +
                "INNER JOIN tbl_presentacion pre ON det.presentacion = pre.codigo_barrap " +
                "INNER JOIN tbl_destino dest ON det.destino = dest.codigo_barrad " +
                "INNER JOIN tbl_lote_e l ON l.Codigo_Lote_e = det.lote " +
                "INNER JOIN tbl_vcultivo vc ON vc.vcb = det.variedad " +
                "WHERE det.id_enc_corte = ? AND det.unidad_medida = 'CJ' " +
                "GROUP BY det.codigo_cortador, emp.Nombre, pre.Nombre_presentacion, " +
                "dest.descripcion, det.unidad_medida, l.Codigo_Lote_e, vc.Nombre_VCultivo, det.codigo_clasificador";

        try {
            cn = conexion.conectar(); // Crear la conexión
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idEncCorte); // Pasar el ID del documento como parámetro
            rs = ps.executeQuery();

            while (rs.next()) {
                Corte corte = new Corte();
                corte.setCodigo_cortador(rs.getString("codigo_cortador"));
                corte.setNombre_cortador(rs.getString("nombre_cortador"));
                corte.setPresentacion(rs.getString("Nombre_presentacion"));
                corte.setDestino(rs.getString("destino"));
                corte.setUnidad_medida(rs.getString("unidad_medida"));
                corte.setCantidad(rs.getDouble("cantidad"));
                corte.setLote(rs.getString("lote"));
                corte.setVariedad(rs.getString("variedad"));
                corte.setCodigo_clasificador(rs.getString("codigo_clasificador"));

                lista.add(corte);
            }
        } catch (Exception e) {
            throw new Exception("Error al listar cajas escaneadas: " + e.getMessage(), e);
        } finally {
            // Cerrar los recursos
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (cn != null) cn.close();
        }

        return lista;
    }

    public void actualizarEstado(Corte corte) throws Exception {
        Conexion conexion = new Conexion();
        Connection cn = null;
        Statement st = null;

        String sql = "UPDATE [dbo].[tbl_enc_corte] " +
                "SET [estado] = '" + corte.getEstado() + "', " +
                "[estado_envio] = '" + corte.getEstado_envio() + "' " +
                "WHERE id_enc_corte = " + corte.getId_enc_corte() + " AND estatus_planilla IS NULL";

        try {
            cn = conexion.conectar();
            st = cn.createStatement();
            st.executeUpdate(sql);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el estado: " + e.getMessage(), e);
        } finally {
            if (st != null && !st.isClosed()) st.close();
            if (cn != null && !cn.isClosed()) cn.close();
        }
    }

    public Corte obtenerDetalleCorteCompleto(int idCorte) throws SQLException {
        Corte corte = null;
        String sql = "SELECT " +
                "a.id_enc_corte, " +
                "(SELECT FORMAT(a.fecha, 'yyyy/MM/dd')) AS fecha, " +
                "b.cultivo, " +
                "b.lote, " +
                "l.Nombre_Lote_e, " +
                "d.Nombre_VCultivo, " +
                "de.descripcion AS destino, " +
                "et.Nombre_etiqueta AS etiqueta, " +
                "p.Nombre_presentacion AS presentacion, " +
                "c.Nombre_Finca AS finca, " +
                "b.unidad_medida, " +
                "SUM(b.cantida) AS cantidad, " +
                "a.apuntador " +
                "FROM tbl_enc_corte a " +
                "INNER JOIN tbl_det_corte b ON a.id_enc_corte = b.Id_enc_corte " +
                "INNER JOIN tbl_finca c ON a.finca = c.Id_Finca " +
                "INNER JOIN tbl_vcultivo d ON d.vcb = b.variedad " +
                "INNER JOIN tbl_presentacion p ON p.codigo_barrap = b.presentacion " +
                "INNER JOIN tbl_lote_e l ON l.Codigo_Lote_e = b.lote " +
                "INNER JOIN tbl_destino de ON de.codigo_barrad = b.destino " +
                "INNER JOIN tbl_empleado e ON e.Codigo_empleado = b.codigo_cortador AND Grupo = 'CORTE' " +
                "INNER JOIN tbl_etiqueta et ON et.codigo_barrae = a.etiqueta " +
                "WHERE a.id_enc_corte = ? AND a.estado = 3 " +
                "GROUP BY a.id_enc_corte, a.fecha, p.Nombre_presentacion, c.Nombre_Finca, b.cultivo, " +
                "d.Nombre_VCultivo, b.unidad_medida, b.lote, l.Nombre_Lote_e, de.descripcion, " +
                "et.Nombre_etiqueta, a.apuntador " +
                "ORDER BY de.descripcion, b.cultivo, d.Nombre_VCultivo, et.Nombre_etiqueta";

        try (Connection cn = new Conexion().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCorte); // Pasar el parámetro id_corte
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    corte = new Corte();
                    corte.setId_enc_corte(rs.getInt("id_enc_corte"));
                    corte.setFecha(rs.getString("fecha"));
                    corte.setCultivo(rs.getString("cultivo"));
                    corte.setLote(rs.getString("lote"));
                    corte.setNombre_lote(rs.getString("Nombre_Lote_e"));
                    corte.setVariedad(rs.getString("Nombre_VCultivo"));
                    corte.setDestino(rs.getString("destino"));
                    corte.setEtiqueta(rs.getString("etiqueta"));
                    corte.setPresentacion(rs.getString("presentacion"));
                    corte.setNombrefinca(rs.getString("finca"));
                    corte.setUnidad_medida(rs.getString("unidad_medida"));
                    corte.setCantidad(rs.getDouble("cantidad"));
                    corte.setApuntador(rs.getString("apuntador"));
                }
            }
        }
        return corte;
    }

    public List<Corte> obtenerDetallesCorte(int idCorte) throws Exception {
        List<Corte> listaDetalles = new ArrayList<>();

        String sql = "SELECT " +
                "l.Codigo_Lote_e AS lote, " +
                "l.Nombre_Lote_e AS nombre_lote, " +
                "vc.Nombre_VCultivo AS variedad, " +
                "et.Nombre_etiqueta AS etiqueta, " +
                "det.unidad_medida, " +
                "SUM(det.cantida) AS cantidad " +
                "FROM tbl_det_corte det " +
                "INNER JOIN tbl_lote_e l ON l.Codigo_Lote_e = det.lote " +
                "INNER JOIN tbl_vcultivo vc ON vc.vcb = det.variedad " +
                "INNER JOIN tbl_etiqueta et ON et.codigo_barrae = det.etiqueta " +
                "WHERE det.Id_enc_corte = ? " +
                "GROUP BY l.Codigo_Lote_e, l.Nombre_Lote_e, vc.Nombre_VCultivo, et.Nombre_etiqueta, det.unidad_medida";

        try (Connection cn = new Conexion().conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCorte);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Corte detalle = new Corte();
                    detalle.setLote(rs.getString("lote"));
                    detalle.setNombre_lote(rs.getString("nombre_lote"));
                    detalle.setVariedad(rs.getString("variedad"));
                    detalle.setEtiqueta(rs.getString("etiqueta"));
                    detalle.setUnidad_medida(rs.getString("unidad_medida"));
                    detalle.setCantidad(rs.getDouble("cantidad"));
                    listaDetalles.add(detalle);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener los detalles del corte: " + e.getMessage(), e);
        }

        return listaDetalles;
    }


}
