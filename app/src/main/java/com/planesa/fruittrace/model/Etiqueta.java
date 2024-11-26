package com.planesa.fruittrace.model;

public class Etiqueta {
    private Integer id_etiqueta;
    private String nombre_etiqueta;
    private String codigo_barras;
    private Integer id_estado;

    // Getters y setters
    public Integer getId_etiqueta() {
        return id_etiqueta;
    }

    public void setId_etiqueta(Integer id_etiqueta) {
        this.id_etiqueta = id_etiqueta;
    }

    public String getNombre_etiqueta() {
        return nombre_etiqueta;
    }

    public void setNombre_etiqueta(String nombre_etiqueta) {
        this.nombre_etiqueta = nombre_etiqueta;
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
        return nombre_etiqueta;
    }
}
