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
public class Articulo {
    
    private Integer idEmpArticulo;
    private String codigo;
    private String nombreArticulo;
    private Integer cantidad;
    private Float precio;
    private String marca;
    private String categoria;
    private Boolean estado;
    private Integer idEmpresa;
    private Integer minimo;
    private Integer codTArticulo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Integer getMinimo() {
        return minimo;
    }

    public void setMinimo(Integer minimo) {
        this.minimo = minimo;
    }

    public Integer getCodTArticulo() {
        return codTArticulo;
    }

    public void setCodTArticulo(Integer codTArticulo) {
        this.codTArticulo = codTArticulo;
    }

    public Integer getIdEmpArticulo() {
        return idEmpArticulo;
    }

    public void setIdEmpArticulo(Integer idEmpArticulo) {
        this.idEmpArticulo = idEmpArticulo;
    }
    
    
}
