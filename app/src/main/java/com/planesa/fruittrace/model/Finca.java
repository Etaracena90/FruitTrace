package com.planesa.fruittrace.model;

public class Finca {
    private int id_Finca;
    private String nombre_Finca;
    private double area_Finca;
    private int estado;
    private String cultivo_NAV;
    private String cultivo_JP;

    // Getters y setters
    public int getId_Finca() {
        return id_Finca;
    }

    public void setId_Finca(int id_Finca) {
        this.id_Finca = id_Finca;
    }

    public String getNombre_Finca() {
        return nombre_Finca;
    }

    public void setNombre_Finca(String nombre_Finca) {
        this.nombre_Finca = nombre_Finca;
    }

    public double getArea_Finca() {
        return area_Finca;
    }

    public void setArea_Finca(double area_Finca) {
        this.area_Finca = area_Finca;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
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

    @Override
    public String toString() {
        return nombre_Finca;
    }
}
