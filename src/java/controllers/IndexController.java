/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import conexion.SQLConexion2;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import modelos.Usuario;


/**
 *
 * @author ngomez
 */
@ManagedBean
@ViewScoped
public class IndexController implements Serializable {

    private static final long serialVersionUID = 426047598496441124L;
    private final FacesContext context = FacesContext.getCurrentInstance();
    private Usuario usuario;
    private SQLConexion2 con = new SQLConexion2();
  

    @PostConstruct
    public void init() {
        usuario = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
        con.conectar();
    }

    public Integer cantEmpresas()  {
        Integer total=0;
        String query = "Select count(*) from EmpresaWebService";
        ResultSet result = con.consulta(query);
        try {
            if(result.next()){
                total=result.getInt(1);
            }else{
                total=0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            total=0;
        }
        return total;
    }
    
    public Integer cantHabilitados()  {
        Integer total=0;
        String query = "select count(*) from INV_Articulos A "
                + "where A.Inactivo = 0 and A.TiendaOnLine = 1";;
        ResultSet result = con.consulta(query);
        try {
            if(result.next()){
                total=result.getInt(1);
            }else{
                total=0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            total=0;
        }
        return total;
    }
    
    public Integer cantMarcas()  {
        Integer total=0;
        String query = "Select count(*) from INV_Grupos";
        ResultSet result = con.consulta(query);
        try {
            if(result.next()){
                total=result.getInt(1);
            }else{
                total=0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            total=0;
        }
        return total;
    }
    
    public Integer cantLineas()  {
        Integer total=0;
        String query = "Select count(*) from INV_Tipos_Articulos";
        ResultSet result = con.consulta(query);
        try {
            if(result.next()){
                total=result.getInt(1);
            }else{
                total=0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            total=0;
        }
        return total;
    }
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
