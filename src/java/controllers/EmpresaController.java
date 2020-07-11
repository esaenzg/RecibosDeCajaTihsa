/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import conexion.SQLConexion;
import conexion.SQLConexion2;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import modelos.Empresa;
import org.primefaces.PrimeFaces;

/**
 *
 * @author IT Acer
 */
@ManagedBean
@ViewScoped
public class EmpresaController implements Serializable {

    private static final long serialVersionUID = -2939554410726384972L;

    private Empresa newEmpresa = new Empresa();
    private Empresa editEmpresa = new Empresa();
    private List<Empresa> listEmpresas;
    private SQLConexion2 con = new SQLConexion2();

    @PostConstruct
    public void init() {
        con.conectar();
        listarEmpresas();
    }

    public void listarEmpresas() {
        listEmpresas = new ArrayList<>();
        String query = "SELECT * FROM EmpresaWebService";
        ResultSet result = con.consulta(query);
        try {
            Empresa empresa;
            while (result.next()) {
                empresa = new Empresa();
                empresa.setIdEmpresa(Integer.valueOf(result.getString(1)));
                empresa.setNombreEmpresa(result.getString(2));
                empresa.setUsuario(result.getString(3));
                empresa.setPassword(result.getString(4));
                empresa.setMinExistencias(Integer.valueOf(result.getShort(8)));
                empresa.setDescuento(Float.valueOf(result.getShort(9)));
                empresa.setDescExtra(Float.valueOf(result.getShort(10)));
                empresa.setEstado(result.getString(7).equals("1") ? true : false);
                listEmpresas.add(empresa);
            }
        } catch (SQLException ex) {
            System.out.println("Error al consultar las empresas");
        }
    }

    public String parsearFecha(Date fecha) {
        if (fecha != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return "'" + sdf.format(fecha) + "'";
        } else {
            return null;
        }
    }

    public void guardar() {
        String query = "INSERT INTO EmpresaWebService (NombreEmpresa,Usuario,Password,FechaCreacion,Estado,MinExistencias,Descuento,DescExtra) "
                + "VALUES ('" + newEmpresa.getNombreEmpresa() + "','" + newEmpresa.getUsuario() + "','" + newEmpresa.getPassword() + "'," + parsearFecha(new Date()) + ",1," + newEmpresa.getMinExistencias() + "," + newEmpresa.getDescuento() + "," + newEmpresa.getDescExtra()+ ")";
        System.out.println("Query: " + query);
        if(con.insertar(query)){
            newEmpresa = new Empresa();
            listarEmpresas();
            FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa guardada correctamente", ""));
        }else{
            FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
        }
    }

    public void update() {
        String query = "UPDATE EmpresaWebService SET NombreEmpresa='" + editEmpresa.getNombreEmpresa() + "', Usuario='" + editEmpresa.getUsuario() + "', Password='" + editEmpresa.getPassword() + "',"
                +" FechaModificacion="+ parsearFecha(new Date()) + ", Estado="+(editEmpresa.getEstado() ? "1":"0") +", MinExistencias=" + editEmpresa.getMinExistencias() + ", Descuento=" + editEmpresa.getDescuento()  + ", DescExtra=" + editEmpresa.getDescExtra()
                + " where ID_Empresa="+editEmpresa.getIdEmpresa();
        System.out.println("Query: " + query);
        if(con.insertar(query)){
            editEmpresa = new Empresa();
            listarEmpresas();
            FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Empresa modificada correctamente", ""));
            PrimeFaces.current().executeScript("PF('dlgEditar').hide();");
        }else{
            FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
        }
    }
    
    public Empresa getNewEmpresa() {
        return newEmpresa;
    }

    public void setNewEmpresa(Empresa newEmpresa) {
        this.newEmpresa = newEmpresa;
    }

    public Empresa getEditEmpresa() {
        return editEmpresa;
    }

    public void setEditEmpresa(Empresa editEmpresa) {
        this.editEmpresa = editEmpresa;
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

}
