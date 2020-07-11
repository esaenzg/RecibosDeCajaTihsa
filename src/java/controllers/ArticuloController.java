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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import modelos.Articulo;
import modelos.Cliente;
import modelos.Empresa;
import modelos.Linea;
import modelos.Marca;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

/**
 *
 * @author IT Acer
 */
@ManagedBean
@ViewScoped
public class ArticuloController implements Serializable {

    private static final long serialVersionUID = 1217636108768868667L;

    private List<Empresa> listEmpresas;
    private List<Articulo> listArticulos;
    private List<Articulo> listArticulosGen;
    private List<Articulo> listSource;
    private List<Articulo> listSourceGen;
    private List<Articulo> listTarget;
    private SQLConexion2 con = new SQLConexion2();
    private Integer idEmpresa, codTArticulo = 0, codTArticulo1 = 0;
    private Empresa empresa = new Empresa();
    private Articulo editArticulo = new Articulo();
    private String buscar, buscarDlg, marca = "0", marca1 = "0";
    private List<Marca> listMarcas;
    private List<Linea> listLineas;

    @PostConstruct
    public void init() {
        con.conectar();
        listarEmpresas();
        listarMarcas();
        listarLinea();
    }

    public void listarMarcas() {
        listMarcas = new ArrayList<>();
        String query = "SELECT * FROM INV_Grupos";
        ResultSet result = con.consulta(query);
        Marca marca;
        try {
            while (result.next()) {
                marca = new Marca();
                marca.setCodigo(result.getInt(2));
                marca.setNombreMarca(result.getString(3));
                listMarcas.add(marca);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ArticuloController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarLinea() {
        listLineas = new ArrayList<>();
        String query = "SELECT CodTArticulo, NombreTArticulo FROM INV_Tipos_Articulos";
        ResultSet result = con.consulta(query);
        Linea linea;
        try {
            while (result.next()) {
                linea = new Linea();
                linea.setCodTArticulo(result.getInt(1));
                linea.setNombreLinea(result.getString(2));
                listLineas.add(linea);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ArticuloController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                empresa.setEstado(result.getString(7).equals("1") ? true : false);
                listEmpresas.add(empresa);
            }
        } catch (SQLException ex) {
            System.out.println("Error al consultar las empresas");
        }
    }

    public String decimal(Float doub) {
        String formato;
        if (doub != null) {
            DecimalFormat format = new DecimalFormat("##,###,##0.00");
            formato = format.format(doub);
        } else {
            formato = "0.00";
        }
        return formato;
    }

    public String decimal2(Float doub) {
        String formato;
        if (doub != null) {
            float nuevo = doub - ((doub * empresa.getDescuento()) / 100);
            if (empresa.getDescExtra() > 0) {
                nuevo = nuevo - ((nuevo * empresa.getDescExtra()) / 100);
            }
            DecimalFormat format = new DecimalFormat("##,###,##0.00");
            formato = format.format(nuevo);
        } else {
            formato = "0.00";
        }
        return formato;
    }

    public void obtenerEmpresa() {
        String query;
        query = "select * from EmpresaWebService where ID_Empresa = " + idEmpresa;
        ResultSet result = con.consulta(query);
        try {
            if (result.next()) {
                empresa = new Empresa();
                empresa.setIdEmpresa(result.getInt(1));
                empresa.setNombreEmpresa(result.getString(2));
                empresa.setUsuario(result.getString(3));
                empresa.setPassword(result.getString(4));
                empresa.setMinExistencias(result.getInt(8));
                empresa.setDescuento(result.getFloat(9));
                empresa.setDescExtra(result.getFloat(10));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se encontró la empresa", ""));
            }
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al consultar la empresa", ""));
        }
    }

    public void listarArticulos() {
        obtenerEmpresa();
        listArticulos = new ArrayList<>();
        listArticulosGen = new ArrayList<>();
        String query = "select A.Codigo, A.NombreArticulo, (select sum(x.Existencia) from INV_Existencias x where x.Codigo=A.Codigo) as Cantidad, A.Precio, G.NombreGrupo as Marca,"
                + "C.NombreClase as Categoria, w.Estado, w.ID_EmpresaWS, w.MinimoInv,A.CodTArticulo, w.ID_EmpresaArticulo  from INV_Articulos A "
                + "inner join INV_Existencias E on E.Codigo = A.Codigo inner join INV_Grupos G on A.CodigoGrupo = G.CodigoGrupo inner join INV_Clases C on A.CodigoClase = C.CodigoClase "
                + "inner join EmpresaArticuloWS w on A.Codigo=w.Codigo and A.CodEmp = w.CodEmp "
                + "where A.Inactivo = 0 and A.TiendaOnLine=1 and w.ID_EmpresaWS = " + idEmpresa + " group by A.Codigo, A.NombreArticulo, A.Precio, G.NombreGrupo, C.NombreClase, "
                + "w.Estado,w.ID_EmpresaWS,w.MinimoInv,A.CodTArticulo,w.ID_EmpresaArticulo order by A.Codigo asc;";
        System.out.println("Query: " + query);
        ResultSet result = con.consulta(query);
        try {
            Articulo articulo;
            while (result.next()) {
                articulo = new Articulo();
                articulo.setCodigo(result.getString(1));
                articulo.setNombreArticulo(result.getString(2));
                articulo.setCantidad(result.getInt(3));
                articulo.setPrecio(result.getFloat(4));
                articulo.setMarca(result.getString(5));
                articulo.setCategoria(result.getString(6));
                articulo.setEstado(result.getString(7).equals("1") ? true : false);
                articulo.setIdEmpresa(result.getInt(8));
                articulo.setMinimo(result.getInt(9));
                articulo.setCodTArticulo(result.getInt(10));
                articulo.setIdEmpArticulo(result.getInt(11));
                listArticulos.add(articulo);
                listArticulosGen.add(articulo);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
        }
        if (listArticulos.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay artículos asignados", ""));
        }
    }

    public void listarSource() {
        obtenerEmpresa();
        listSource = new ArrayList<>();
        listSourceGen = new ArrayList<>();
        String query = "select A.Codigo, A.NombreArticulo, A.Precio, G.NombreGrupo as Marca,"
                + "C.NombreClase as Categoria, A.CodTArticulo from INV_Articulos A "
                + "inner join INV_Existencias E on E.Codigo = A.Codigo inner join INV_Grupos G on A.CodigoGrupo = G.CodigoGrupo inner join INV_Clases C on A.CodigoClase = C.CodigoClase "
                + "where A.Inactivo = 0 and A.TiendaOnLine = 1  group by A.Codigo, A.NombreArticulo, A.Precio, G.NombreGrupo, C.NombreClase,A.CodTArticulo order by A.Codigo asc;";
        System.out.println("Query: " + query);
        ResultSet result = con.consulta(query);
        try {
            Articulo articulo;
            while (result.next()) {
                articulo = new Articulo();
                articulo.setCodigo(result.getString(1));
                articulo.setNombreArticulo(result.getString(2));
                articulo.setPrecio(result.getFloat(3));
                articulo.setMarca(result.getString(4));
                articulo.setCategoria(result.getString(5));
                articulo.setCodTArticulo(result.getInt(6));
                listSource.add(articulo);
                listSourceGen.add(articulo);
            }
            PrimeFaces.current().executeScript("PF('dlgAgregar').show();");
        } catch (SQLException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
        }
    }

    public Boolean validarLista(Articulo target) {
        Boolean find = true;
        if (listArticulos != null) {
            for (Articulo articulo : listArticulos) {
                if (target.getCodigo().equals(articulo.getCodigo()) && Objects.equals(idEmpresa, articulo.getIdEmpresa())) {
                    find = false;
                }
            }
        }
        return find;
    }

    public void agregar(Articulo articulo) {
        listarArticulos();
        if (validarLista(articulo)) {
            String query;
            if (empresa.getIdEmpresa() != null) {
                Integer minExistencias = empresa.getMinExistencias();
                query = "INSERT INTO EmpresaArticuloWS (ID_EmpresaWS,CodEmp,Codigo,MinimoInv,Estado) values "
                        + "(" + idEmpresa + ",1,'" + articulo.getCodigo() + "'," + minExistencias + ",1)";
                con.insertar(query);
                listarArticulos();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "El artículo ha sido agregado", ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "No ha seleccionado a la empresa", ""));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "El artículo ya está agregado", ""));
        }
    }

    public void agregaTodos() {
        int agregados = 0;
        listarArticulos();
        if (listSource != null) {
            if (empresa.getIdEmpresa() != null) {
                Integer minExistencias = empresa.getMinExistencias();
                for (Articulo articulo : listSource) {
                    if (validarLista(articulo)) {
                        String query;
                        query = "INSERT INTO EmpresaArticuloWS (ID_EmpresaWS,CodEmp,Codigo,MinimoInv,Estado) values "
                                + "(" + idEmpresa + ",1,'" + articulo.getCodigo() + "'," + minExistencias + ",1)";
                        con.insertar(query);
                        agregados++;
                    }
                }
                if (agregados > 0) {
                    listarArticulos();
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Se han agregado " + agregados + " artículos correctamente", ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "No se agregó ningún artículo nuevo", ""));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "No ha seleccionado a la empresa", ""));
            }
        }
    }

    public Integer restarExistencias(Articulo articulo) {
        if (articulo.getCantidad() > articulo.getMinimo()) {
            return articulo.getCantidad() - articulo.getMinimo();
        } else {
            return 0;
        }
    }

    public void updateMinimo() {
        String query = "update EmpresaArticuloWS set MinimoInv=" + editArticulo.getMinimo() + ", Estado=" + (editArticulo.getEstado() ? "1" : "0") + " where ID_EmpresaArticulo=" + editArticulo.getIdEmpArticulo();
        if (con.insertar(query)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "El artículo ha sido modificado", ""));
            PrimeFaces.current().executeScript("PF('dlgEditar').hide();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar guardar los datos", ""));
        }
    }

    public void delete(Articulo item) {
        String query = "delete from EmpresaArticuloWS where ID_EmpresaArticulo=" + item.getIdEmpArticulo() + "";
        if (con.insertar(query)) {
            listArticulos.remove(item);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "El artículo ha sido eliminado", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar guardar los datos", ""));
        }
    }

    public void buscar() {
        if (!listArticulosGen.isEmpty()) {
            CharSequence content = buscar.toUpperCase();
            List<Articulo> lista = new ArrayList<>();
            for (Articulo articulo : listArticulosGen) {
                if (articulo.getNombreArticulo().contains(content) || articulo.getCodigo().contains(content)) {
                    lista.add(articulo);
                }
            }
            listArticulos = lista;
        }
    }

    public void filtrar() {
        if (!listSourceGen.isEmpty()) {
            if (marca.equals("0") && codTArticulo == 0 && buscarDlg == null) {
                listSource = listSourceGen;
            } else if (marca.equals("0") && codTArticulo != 0 && buscarDlg == null) {
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getCodTArticulo().equals(codTArticulo)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            } else if (!marca.equals("0") && codTArticulo == 0 && buscarDlg == null) {
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getMarca().equals(marca)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            } else if (!marca.equals("0") && codTArticulo != 0 && buscarDlg == null) {
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getMarca().equals(marca) && articulo.getCodTArticulo().equals(codTArticulo)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            } else if (marca.equals("0") && codTArticulo == 0 && buscarDlg != null) {
                CharSequence content = buscarDlg.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getNombreArticulo().contains(content) || articulo.getCodigo().contains(content)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            } else if (marca.equals("0") && codTArticulo != 0 && buscarDlg != null) {
                CharSequence content = buscarDlg.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getNombreArticulo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo) || articulo.getCodigo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            } else if (!marca.equals("0") && codTArticulo == 0 && buscarDlg != null) {
                CharSequence content = buscarDlg.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getNombreArticulo().contains(content) && articulo.getMarca().equals(marca) || articulo.getCodigo().contains(content) && articulo.getMarca().equals(marca)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            } else {
                CharSequence content = buscarDlg.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getNombreArticulo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo) && articulo.getMarca().equals(marca)
                            || articulo.getCodigo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo) && articulo.getMarca().equals(marca)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            }
        }
    }

    public void filtrar2() {
        if (!listArticulosGen.isEmpty()) {
            if (marca1.equals("0") && codTArticulo1 == 0 && buscar == null) {
                listArticulos = listArticulosGen;
            } else if (marca1.equals("0") && codTArticulo1 != 0 && buscar == null) {
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getCodTArticulo().equals(codTArticulo1)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            } else if (!marca1.equals("0") && codTArticulo1 == 0 && buscar == null) {
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getMarca().equals(marca1)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            } else if (!marca1.equals("0") && codTArticulo1 != 0 && buscar == null) {
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getMarca().equals(marca1) && articulo.getCodTArticulo().equals(codTArticulo1)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            } else if (marca1.equals("0") && codTArticulo1 == 0 && buscar != null) {
                CharSequence content = buscar.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getNombreArticulo().contains(content) || articulo.getCodigo().contains(content)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            } else if (marca1.equals("0") && codTArticulo1 != 0 && buscar != null) {
                CharSequence content = buscar.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getNombreArticulo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo1) || articulo.getCodigo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo1)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            } else if (!marca1.equals("0") && codTArticulo1 == 0 && buscar != null) {
                CharSequence content = buscar.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getNombreArticulo().contains(content) && articulo.getMarca().equals(marca1) || articulo.getCodigo().contains(content) && articulo.getMarca().equals(marca1)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            } else {
                CharSequence content = buscar.toUpperCase();
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getNombreArticulo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo1) && articulo.getMarca().equals(marca1)
                            || articulo.getCodigo().contains(content) && articulo.getCodTArticulo().equals(codTArticulo1) && articulo.getMarca().equals(marca1)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            }
        }
    }

    public void buscarMarca() {
        if (marca1.equals("0")) {
            listArticulos = listArticulosGen;
        } else {
            if (!listArticulosGen.isEmpty()) {
                CharSequence content = marca1;
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listArticulosGen) {
                    if (articulo.getMarca().equals(content)) {
                        lista.add(articulo);
                    }
                }
                listArticulos = lista;
            }
        }
    }

    public void buscarDlg() {
        if (!listSourceGen.isEmpty()) {
            CharSequence content = buscarDlg.toUpperCase();
            List<Articulo> lista = new ArrayList<>();
            for (Articulo articulo : listSourceGen) {
                if (articulo.getNombreArticulo().contains(content) || articulo.getCodigo().contains(content)) {
                    lista.add(articulo);
                }
            }
            listSource = lista;
        }
    }

    public void buscarMarcaDlg() {
        if (marca.equals("0")) {
            listSource = listSourceGen;
        } else {
            if (!listSourceGen.isEmpty()) {
                CharSequence content = marca;
                List<Articulo> lista = new ArrayList<>();
                for (Articulo articulo : listSourceGen) {
                    if (articulo.getMarca().equals(content)) {
                        lista.add(articulo);
                    }
                }
                listSource = lista;
            }
        }
    }

    public List<Empresa> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Empresa> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public List<Articulo> getListArticulos() {
        return listArticulos;
    }

    public void setListArticulos(List<Articulo> listArticulos) {
        this.listArticulos = listArticulos;
    }

    public List<Articulo> getListSource() {
        return listSource;
    }

    public void setListSource(List<Articulo> listSource) {
        this.listSource = listSource;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Articulo getEditArticulo() {
        return editArticulo;
    }

    public void setEditArticulo(Articulo editArticulo) {
        this.editArticulo = editArticulo;
    }

    public String getBuscar() {
        return buscar;
    }

    public void setBuscar(String buscar) {
        this.buscar = buscar;
    }

    public String getBuscarDlg() {
        return buscarDlg;
    }

    public void setBuscarDlg(String buscarDlg) {
        this.buscarDlg = buscarDlg;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public List<Marca> getListMarcas() {
        return listMarcas;
    }

    public void setListMarcas(List<Marca> listMarcas) {
        this.listMarcas = listMarcas;
    }

    public Integer getCodTArticulo() {
        return codTArticulo;
    }

    public void setCodTArticulo(Integer codTArticulo) {
        this.codTArticulo = codTArticulo;
    }

    public String getMarca1() {
        return marca1;
    }

    public void setMarca1(String marca1) {
        this.marca1 = marca1;
    }

    public List<Linea> getListLineas() {
        return listLineas;
    }

    public void setListLineas(List<Linea> listLineas) {
        this.listLineas = listLineas;
    }

    public Integer getCodTArticulo1() {
        return codTArticulo1;
    }

    public void setCodTArticulo1(Integer codTArticulo1) {
        this.codTArticulo1 = codTArticulo1;
    }

}
