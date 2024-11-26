package com.planesa.fruittrace.model;

public class Destino {
    private Integer id_destino;
    private String nombre_destino;
    private String codigo_barra;
    private Integer id_estado;

    // Getters y setters
    public Integer getId_destino() {
        return id_destino;
    }

    public void setId_destino(Integer id_destino) {
        this.id_destino = id_destino;
    }

    public String getNombre_destino() {
        return nombre_destino;
    }

    public void setNombre_destino(String nombre_destino) {
        this.nombre_destino = nombre_destino;
    }

    public String getCodigo_barra() {
        return codigo_barra;
    }

    public void setCodigo_barra(String codigo_barra) {
        this.codigo_barra = codigo_barra;
    }

    public Integer getId_estado() {
        return id_estado;
    }

    public void setId_estado(Integer id_estado) {
        this.id_estado = id_estado;
    }

    @Override
    public String toString() {
        return nombre_destino;
    }
}
