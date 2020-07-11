/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
 *
 * @author IT Acer
 */
public class Empresa {
    
    private String nombreEmpresa;
    private String usuario;
    private String password;
    private Boolean estado;
    private Integer minExistencias;
    private Float descuento;
    private Float descExtra;
    private Integer idEmpresa;

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getMinExistencias() {
        return minExistencias;
    }

    public void setMinExistencias(Integer minExistencias) {
        this.minExistencias = minExistencias;
    }

    public Float getDescuento() {
        return descuento;
    }

    public void setDescuento(Float descuento) {
        this.descuento = descuento;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Float getDescExtra() {
        return descExtra;
    }

    public void setDescExtra(Float descExtra) {
        this.descExtra = descExtra;
    }
    
    
}
