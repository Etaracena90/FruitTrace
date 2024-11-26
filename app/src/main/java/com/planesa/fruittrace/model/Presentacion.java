package com.planesa.fruittrace.model;

public class Presentacion {
    private Integer id_presentacion;
    private String nombre_Presentacion;
    private String codigo_barras;
    private Integer id_estado;

    // Getters y setters
    public Integer getId_presentacion() {
        return id_presentacion;
    }

    public void setId_presentacion(Integer id_presentacion) {
        this.id_presentacion = id_presentacion;
    }

    public String getNombre_Presentacion() {
        return nombre_Presentacion;
    }

    public void setNombre_Presentacion(String nombre_Presentacion) {
        this.nombre_Presentacion = nombre_Presentacion;
    }

    public String getCodigo_barras() {
        return codigo_barras;
    }

    public void setCodigo_barras(String codigo_barras) {
        this.codigo_barras = codigo_barras;
    }

    public Integer getId_estado() {
        return id_estado;
    }

    public void setId_estado(Integer id_estado) {
        this.id_estado = id_estado;
    }

    @Override
    public String toString() {
        return nombre_Presentacion;
    }
}
