package com.planesa.fruittrace.model;

public class Cultivo {
    private int id_Cultivo;
    private String nombre_Cultivo;
    private String cultivo_NAV;
    private String cultivo_JP;
    private int id_Estado;

    // Getters y setters
    public int getId_Cultivo() {
        return id_Cultivo;
    }

    public void setId_Cultivo(int id_Cultivo) {
        this.id_Cultivo = id_Cultivo;
    }

    public String getNombre_Cultivo() {
        return nombre_Cultivo;
    }

    public void setNombre_Cultivo(String nombre_Cultivo) {
        this.nombre_Cultivo = nombre_Cultivo;
    }

    public String getCultivo_NAV() {
        return cultivo_NAV;
    }

    public void setCultivo_NAV(String cultivo_NAV) {
        this.cultivo_NAV = cultivo_NAV;
    }

    public String getCultivo_JP() {
        return cultivo_JP;
    }

    public void setCultivo_JP(String cultivo_JP) {
        this.cultivo_JP = cultivo_JP;
    }

    public int getId_Estado() {
        return id_Estado;
    }

    public void setId_Estado(int id_Estado) {
        this.id_Estado = id_Estado;
    }

    @Override
    public String toString() {
        return nombre_Cultivo;
    }
}
