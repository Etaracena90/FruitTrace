package com.planesa.fruittrace.model;

import java.io.Serializable;

public class Corte implements Serializable {
    private static final long serialVersionUID = 1L;

    // Nuevos campos
    private String descripcion;

    // Atributos existentes
    Integer item;
    Integer id;
    Integer id_enc_corte;
    String apuntador;
    String Fecha;
    String Area;
    Integer id_det_corte;
    String Lote;
    String nombre_lote;
    Integer finca;
    String nombrefinca;
    String codigo_cortador;
    String nombre_cortador;
    String nombre_clasificador;
    String codigo_clasificador;
    String destino;
    String Nombre_destino;
    String presentacion;
    String Nombre_presentacion;
    String etiqueta;
    String Nombre_etiqueta;
    String variedad;
    String nombre_variedad;
    String cultivo;
    String nombre_cultivo;
    Double Cantidad;
    Double Libras;
    String total_cajas;
    String unidad_medida;
    String nombre_estado;
    Integer estado;
    String fechai;
    String fechaf;
    String Dia;
    String factor_conversion;
    String cajas_6onzas;
    String precio_caja;
    String total_a_pagar;
    String Nombte_contratista;
    String Total_a_pagar_contratista;
    String Total_a_pagar_ruta;
    String Total_a_pagar_cooperativa;
    String Tipo_planilla;
    Integer Estado_envio;
    Integer No_envio_unificado;
    Integer No_envio;
    String Fecha_envio_unificado;
    Integer Estado_envio_unificado;
    String ruta;
    String cul_arandano;
    String cul_frambuesa;
    String cul_mora;
    String cul_uchuva;
    String codigo_pte;
    String preFijoUno;
    String preFijoDos;
    String codigoBarras;

    // Constructor por defecto
    public Corte() {}

    // Constructor adicional
    public Corte(String fecha, String apuntador, Integer finca, String cultivo, String presentacion, String destino, String etiqueta) {
        this.Fecha = fecha;
        this.apuntador = apuntador;
        this.finca = finca;
        this.nombre_cultivo = cultivo;
        this.presentacion = presentacion;
        this.destino = destino;
        this.etiqueta = etiqueta;
    }

    // Getter y Setter para descripcion
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Getters y Setters existentes
    // (El resto de los getters y setters siguen igual que en tu código original)

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_enc_corte() {
        return id_enc_corte;
    }

    public void setId_enc_corte(Integer id_enc_corte) {
        this.id_enc_corte = id_enc_corte;
    }

    public String getApuntador() {
        return apuntador;
    }

    public void setApuntador(String apuntador) {
        this.apuntador = apuntador;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String Area) {
        this.Area = Area;
    }

    public Integer getId_det_corte() {
        return id_det_corte;
    }

    public void setId_det_corte(Integer id_det_corte) {
        this.id_det_corte = id_det_corte;
    }

    public String getLote() {
        return Lote;
    }

    public void setLote(String Lote) {
        this.Lote = Lote;
    }

    public String getNombre_lote() {
        return nombre_lote;
    }

    public void setNombre_lote(String nombre_lote) {
        this.nombre_lote = nombre_lote;
    }

    public Integer getFinca() {
        return finca;
    }

    public void setFinca(Integer finca) {
        this.finca = finca;
    }

    public String getNombrefinca() {
        return nombrefinca;
    }

    public void setNombrefinca(String nombrefinca) {
        this.nombrefinca = nombrefinca;
    }

    public String getCodigo_cortador() {
        return codigo_cortador;
    }

    public void setCodigo_cortador(String codigo_cortador) {
        this.codigo_cortador = codigo_cortador;
    }

    public String getNombre_cortador() {
        return nombre_cortador;
    }

    public void setNombre_cortador(String nombre_cortador) {
        this.nombre_cortador = nombre_cortador;
    }

    public String getNombre_clasificador() {
        return nombre_clasificador;
    }

    public void setNombre_clasificador(String nombre_clasificador) {
        this.nombre_clasificador = nombre_clasificador;
    }

    public String getCodigo_clasificador() {
        return codigo_clasificador;
    }

    public void setCodigo_clasificador(String codigo_clasificador) {
        this.codigo_clasificador = codigo_clasificador;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getNombre_destino() {
        return Nombre_destino;
    }

    public void setNombre_destino(String Nombre_destino) {
        this.Nombre_destino = Nombre_destino;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getNombre_presentacion() {
        return Nombre_presentacion;
    }

    public void setNombre_presentacion(String Nombre_presentacion) {
        this.Nombre_presentacion = Nombre_presentacion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getNombre_etiqueta() {
        return Nombre_etiqueta;
    }

    public void setNombre_etiqueta(String Nombre_etiqueta) {
        this.Nombre_etiqueta = Nombre_etiqueta;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public String getNombre_variedad() {
        return nombre_variedad;
    }

    public void setNombre_variedad(String nombre_variedad) {
        this.nombre_variedad = nombre_variedad;
    }

    public String getCultivo() {
        return cultivo;
    }

    public void setCultivo(String cultivo) {
        this.cultivo = cultivo;
    }

    public String getNombre_cultivo() {
        return nombre_cultivo;
    }

    public void setNombre_cultivo(String nombre_cultivo) {
        this.nombre_cultivo = nombre_cultivo;
    }

    public Double getCantidad() {
        return Cantidad;
    }

    public void setCantidad(Double Cantidad) {
        this.Cantidad = Cantidad;
    }

    public Double getLibras() {
        return Libras;
    }

    public void setLibras(Double Libras) {
        this.Libras = Libras;
    }

    public String getTotal_cajas() {
        return total_cajas;
    }

    public void setTotal_cajas(String total_cajas) {
        this.total_cajas = total_cajas;
    }

    public String getUnidad_medida() {
        return unidad_medida;
    }

    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }

    public String getNombre_estado() {
        return nombre_estado;
    }

    public void setNombre_estado(String nombre_estado) {
        this.nombre_estado = nombre_estado;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getFechai() {
        return fechai;
    }

    public void setFechai(String fechai) {
        this.fechai = fechai;
    }

    public String getFechaf() {
        return fechaf;
    }

    public void setFechaf(String fechaf) {
        this.fechaf = fechaf;
    }

    public String getDia() {
        return Dia;
    }

    public void setDia(String Dia) {
        this.Dia = Dia;
    }

    public String getFactor_conversion() {
        return factor_conversion;
    }

    public void setFactor_conversion(String factor_conversion) {
        this.factor_conversion = factor_conversion;
    }

    public String getCajas_6onzas() {
        return cajas_6onzas;
    }

    public void setCajas_6onzas(String cajas_6onzas) {
        this.cajas_6onzas = cajas_6onzas;
    }

    public String getPrecio_caja() {
        return precio_caja;
    }

    public void setPrecio_caja(String precio_caja) {
        this.precio_caja = precio_caja;
    }

    public String getTotal_a_pagar() {
        return total_a_pagar;
    }

    public void setTotal_a_pagar(String total_a_pagar) {
        this.total_a_pagar = total_a_pagar;
    }

    public String getNombte_contratista() {
        return Nombte_contratista;
    }

    public void setNombte_contratista(String Nombte_contratista) {
        this.Nombte_contratista = Nombte_contratista;
    }

    public String getTotal_a_pagar_contratista() {
        return Total_a_pagar_contratista;
    }

    public void setTotal_a_pagar_contratista(String Total_a_pagar_contratista) {
        this.Total_a_pagar_contratista = Total_a_pagar_contratista;
    }

    public String getTotal_a_pagar_ruta() {
        return Total_a_pagar_ruta;
    }

    public void setTotal_a_pagar_ruta(String Total_a_pagar_ruta) {
        this.Total_a_pagar_ruta = Total_a_pagar_ruta;
    }

    public String getTotal_a_pagar_cooperativa() {
        return Total_a_pagar_cooperativa;
    }

    public void setTotal_a_pagar_cooperativa(String Total_a_pagar_cooperativa) {
        this.Total_a_pagar_cooperativa = Total_a_pagar_cooperativa;
    }

    public String getTipo_planilla() {
        return Tipo_planilla;
    }

    public void setTipo_planilla(String Tipo_planilla) {
        this.Tipo_planilla = Tipo_planilla;
    }

    public Integer getEstado_envio() {
        return Estado_envio;
    }

    public void setEstado_envio(Integer Estado_envio) {
        this.Estado_envio = Estado_envio;
    }

    public int getNo_envio_unificado() {
        return No_envio_unificado;
    }

    public void setNo_envio_unificado(int No_envio_unificado) {
        this.No_envio_unificado = No_envio_unificado;
    }

    public int getNo_envio() {
        return No_envio;
    }

    public void setNo_envio(int No_envio) {
        this.No_envio = No_envio;
    }

    public String getFecha_envio_unificado() {
        return Fecha_envio_unificado;
    }

    public void setFecha_envio_unificado(String Fecha_envio_unificado) {
        this.Fecha_envio_unificado = Fecha_envio_unificado;
    }

    public Integer getEstado_envio_unificado() {
        return Estado_envio_unificado;
    }

    public void setEstado_envio_unificado(Integer Estado_envio_unificado) {
        this.Estado_envio_unificado = Estado_envio_unificado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getCul_arandano() {
        return cul_arandano;
    }

    public void setCul_arandano(String cul_arandano) {
        this.cul_arandano = cul_arandano;
    }

    public String getCul_frambuesa() {
        return cul_frambuesa;
    }

    public void setCul_frambuesa(String cul_frambuesa) {
        this.cul_frambuesa = cul_frambuesa;
    }

    public String getCul_mora() {
        return cul_mora;
    }

    public void setCul_mora(String cul_mora) {
        this.cul_mora = cul_mora;
    }

    public String getCul_uchuva() {
        return cul_uchuva;
    }

    public void setCul_uchuva(String cul_uchuva) {
        this.cul_uchuva = cul_uchuva;
    }

    public String getCodigo_pte() {
        return codigo_pte;
    }

    public void setCodigo_pte(String codigo_pte) {
        this.codigo_pte = codigo_pte;
    }

    public String getPreFijoUno() {
        return preFijoUno;
    }

    public void setPreFijoUno(String preFijoUno) {
        this.preFijoUno = preFijoUno;
    }

    public String getPreFijoDos() {
        return preFijoDos;
    }

    public void setPreFijoDos(String preFijoDos) {
        this.preFijoDos = preFijoDos;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
}
