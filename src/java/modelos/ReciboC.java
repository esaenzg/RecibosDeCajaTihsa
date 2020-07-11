/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

import java.util.Date;

/**
 *
 * @author IT Acer
 */
public class ReciboC {

    private Integer numRecibo = null;
    private Date fecha = null;
    private String nit = null;
    private String nombreCliente = null;
    private Integer codCliente = null;
    private Double montoRecibo = null;
    private String concepto = null;
    private Double efectivo = null;
    private Double montoCheque1 = null;
    private Double montoCheque2 = null;
    private String numCheque1 = null;
    private String numCheque2 = null;
    private Integer codBanco1 = null;
    private Integer codBanco2 = null;
    private Date fechaCheque1 = null;
    private Date fechaCheque2 = null;
    private Double montoTC = null;
    private String numTC = null;
    private Integer tipoTC = null;
    private Double total = null;
    private Short estado = null;
    private String usuario = null;
    private String nombreBanco1=null;
    private String nombreBanco2=null;
    private String nombreBancoD=null;
    private Double montoDeposito=null;
    private Integer bancoDeposito=null;
    private String numBoleta=null;
    private String correo;
    private String whatsapp;
    private Boolean tipoRecibo;
    private Integer numOrden;
    

    public Integer getNumRecibo() {
        return numRecibo;
    }

    public void setNumRecibo(Integer numRecibo) {
        this.numRecibo = numRecibo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Integer getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(Integer codCliente) {
        this.codCliente = codCliente;
    }

    public Double getMontoRecibo() {
        return montoRecibo;
    }

    public void setMontoRecibo(Double montoRecibo) {
        this.montoRecibo = montoRecibo;
    }

    public Double getMontoCheque1() {
        return montoCheque1;
    }

    public void setMontoCheque1(Double montoCheque1) {
        this.montoCheque1 = montoCheque1;
    }

    public Double getMontoCheque2() {
        return montoCheque2;
    }

    public void setMontoCheque2(Double montoCheque2) {
        this.montoCheque2 = montoCheque2;
    }

    public Integer getCodBanco1() {
        return codBanco1;
    }

    public void setCodBanco1(Integer codBanco1) {
        this.codBanco1 = codBanco1;
    }

    public Integer getCodBanco2() {
        return codBanco2;
    }

    public void setCodBanco2(Integer codBanco2) {
        this.codBanco2 = codBanco2;
    }

    public Double getMontoTC() {
        return montoTC;
    }

    public void setMontoTC(Double montoTC) {
        this.montoTC = montoTC;
    }

    public String getNumTC() {
        return numTC;
    }

    public void setNumTC(String numTC) {
        this.numTC = numTC;
    }

    public Integer getTipoTC() {
        return tipoTC;
    }

    public void setTipoTC(Integer tipoTC) {
        this.tipoTC = tipoTC;
    }

    public Date getFechaCheque1() {
        return fechaCheque1;
    }

    public void setFechaCheque1(Date fechaCheque1) {
        this.fechaCheque1 = fechaCheque1;
    }

    public Date getFechaCheque2() {
        return fechaCheque2;
    }

    public void setFechaCheque2(Date fechaCheque2) {
        this.fechaCheque2 = fechaCheque2;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(Double efectivo) {
        this.efectivo = efectivo;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Short getEstado() {
        return estado;
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNumCheque1() {
        return numCheque1;
    }

    public void setNumCheque1(String numCheque1) {
        this.numCheque1 = numCheque1;
    }

    public String getNumCheque2() {
        return numCheque2;
    }

    public void setNumCheque2(String numCheque2) {
        this.numCheque2 = numCheque2;
    }

    public String getNombreBanco1() {
        return nombreBanco1;
    }

    public void setNombreBanco1(String nombreBanco1) {
        this.nombreBanco1 = nombreBanco1;
    }

    public String getNombreBanco2() {
        return nombreBanco2;
    }

    public void setNombreBanco2(String nombreBanco2) {
        this.nombreBanco2 = nombreBanco2;
    }

    public Double getMontoDeposito() {
        return montoDeposito;
    }

    public void setMontoDeposito(Double montoDeposito) {
        this.montoDeposito = montoDeposito;
    }

    public Integer getBancoDeposito() {
        return bancoDeposito;
    }

    public void setBancoDeposito(Integer bancoDeposito) {
        this.bancoDeposito = bancoDeposito;
    }

    public String getNumBoleta() {
        return numBoleta;
    }

    public void setNumBoleta(String numBoleta) {
        this.numBoleta = numBoleta;
    }

    public String getNombreBancoD() {
        return nombreBancoD;
    }

    public void setNombreBancoD(String nombreBancoD) {
        this.nombreBancoD = nombreBancoD;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public Boolean getTipoRecibo() {
        return tipoRecibo;
    }

    public void setTipoRecibo(Boolean tipoRecibo) {
        this.tipoRecibo = tipoRecibo;
    }

    public Integer getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(Integer numOrden) {
        this.numOrden = numOrden;
    }

}
