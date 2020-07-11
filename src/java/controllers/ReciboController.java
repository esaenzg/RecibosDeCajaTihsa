/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import conexion.SQLConexion;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import modelos.Banco;
import modelos.Cliente;
import modelos.Factura;
import modelos.ReciboC;
import modelos.TipoTC;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import reportes.ImprimirRecibo;

/**
 *
 * @author IT Acer
 */
@ManagedBean
@ViewScoped
public class ReciboController implements Serializable {

    private Factura factura = new Factura();
    private List<Factura> listFactura = new ArrayList<>();
    private List<Factura> listAllFactura = new ArrayList<>();
    private List<Factura> listDlgFactura = new ArrayList<>();
    private List<Banco> listBancos = new ArrayList<>();
    private List<Banco> listCuentas = new ArrayList<>();
    private List<TipoTC> listTipoTC = new ArrayList<>();
    private List<Cliente> listCliente = new ArrayList<>();
    private List<Cliente> listAllCliente = new ArrayList<>();
    private SQLConexion conexion = new SQLConexion();
    private String numRecibo, tipoTC = null, buscar, buscaFactura, buscaSerie, rutaPdf = "", usuario, referencia, mensaje, user, contenido;
    private Integer codBanco1 = null, codBanco2 = null, opcion, codCuenta;
    private ReciboC recibo = new ReciboC();
    private Double monto = 0.0;
    private Double descuento;
    private Boolean guardado = false, sendCorreo, sndWhatsapp, verRecibo;
    private StreamedContent fileDown;

    @PostConstruct
    public void init() {
        conexion.conectar();
        obtenerBancos();
        obtenerCuentas();
        obtenerTipoTC();
        obtenerClientes();
        obtenerUltimoRecibo();
    }

    public void pedidoSugerido() {
        String query = "select A.*,B.SumaCantidad SumaVentas,C.SumaCantidad SumaOrdenes,D.FaltanPartes,D.OT_Estado from(SELECT INV_Articulos.CodEmp, INV_Articulos.Codigo, INV_Articulos.NombreArticulo, INV_Articulos.CodTArticulo, INV_Articulos.CodigoGrupo, INV_Articulos.CodigoClase, INV_Articulos.Costo, INV_Articulos.Precio, INV_Articulos.PrecioListaCompra, INV_Articulos.Minimo, INV_Articulos.ABCZ, Sum(INV_Existencias.Existencia) AS SumaExistencia, Sum(INV_Existencias.Taller) AS SumaTaller, Sum(INV_Existencias.Ordenar) AS SumaOrdenar, INV_Articulos.Inactivo, INV_Articulos.Sustituto FROM INV_Articulos WITH (NOLOCK) LEFT JOIN INV_Existencias ON (INV_Articulos.CodEmp = INV_Existencias.CodEmp) AND (INV_Articulos.Codigo = INV_Existencias.Codigo) WHERE (((INV_Articulos.CodEmp)=1) AND ((INV_Articulos.Inactivo)=0)) and CodTArticulo=11013 GROUP BY INV_Articulos.CodEmp, INV_Articulos.Codigo, INV_Articulos.NombreArticulo, INV_Articulos.CodTArticulo, INV_Articulos.CodigoGrupo, INV_Articulos.CodigoClase, INV_Articulos.Costo, INV_Articulos.Precio, INV_Articulos.PrecioListaCompra, INV_Articulos.Minimo, INV_Articulos.ABCZ, INV_Articulos.Servicio, INV_Articulos.Inactivo, INV_Articulos.Sustituto HAVING (((INV_Articulos.Servicio)=0))) A left join (select CodEmp,Codigo,SUM(SumaCantidad) as SumaCantidad from (SELECT QRY_FAC_Facturas_D.CodEmp, QRY_FAC_Facturas_D.Codigo, (ROW_NUMBER() OVER(PARTITION BY QRY_FAC_Facturas_D.CodEmp, QRY_FAC_Facturas_D.Codigo, FAC_Facturas_C.Anulada, QRY_FAC_Facturas_D.Servicio ORDER BY QRY_FAC_Facturas_D.NombreArticulo DESC)) AS NumeroFila,QRY_FAC_Facturas_D.NombreArticulo, Sum(QRY_FAC_Facturas_D.Cantidad) AS SumaCantidad FROM QRY_FAC_Facturas_D INNER JOIN FAC_Facturas_C ON (QRY_FAC_Facturas_D.NumFactura = FAC_Facturas_C.NumFactura) AND (QRY_FAC_Facturas_D.SerieFactura = FAC_Facturas_C.SerieFactura) AND (QRY_FAC_Facturas_D.CodEmp = FAC_Facturas_C.CodEmp) WHERE QRY_FAC_Facturas_D.CodEmp = 1 and FAC_Facturas_C.Fecha>=CONVERT(DATETIME,'12/1/2019') and FAC_Facturas_C.Fecha <= CONVERT(DATETIME,'6/25/2020') GROUP BY QRY_FAC_Facturas_D.CodEmp, QRY_FAC_Facturas_D.Codigo, FAC_Facturas_C.Anulada, QRY_FAC_Facturas_D.Servicio, QRY_FAC_Facturas_D.NombreArticulo HAVING (((FAC_Facturas_C.Anulada)=0) AND ((QRY_FAC_Facturas_D.Servicio)=0))) A GROUP BY CodEmp,Codigo ) B on A.Codigo=B.Codigo and A.CodEmp=B.CodEmp or B.Codigo is null left join (SELECT INV_Entradas_D.CodEmp, INV_Entradas_D.Codigo, Sum(INV_Entradas_D.Cantidad) AS SumaCantidad FROM INV_Entradas_D INNER JOIN INV_Entradas_C ON (INV_Entradas_D.NumEntrada = INV_Entradas_C.NumEntrada) AND (INV_Entradas_D.CodEmp = INV_Entradas_C.CodEmp) WHERE INv_Entradas_D.CodEmp = 1 GROUP BY INV_Entradas_D.CodEmp, INV_Entradas_D.Codigo, INV_Entradas_C.Anulada, INV_Entradas_C.Operada HAVING (((INV_Entradas_C.Anulada)=0) AND ((INV_Entradas_C.Operada)=0)) ) C on A.CodEmp=C.CodEmp and A.Codigo=C.Codigo or C.Codigo is null left join(SELECT ORD_Ordenes_D.CodEmp, ORD_Ordenes_D.OT_NoParte, Sum(ORD_Ordenes_D.OT_PE) AS FaltanPartes, ORD_Ordenes_C.OT_Estado FROM ORD_Ordenes_D INNER JOIN ORD_Ordenes_C ON (ORD_Ordenes_D.CodEmp = ORD_Ordenes_C.CodEmp) AND (ORD_Ordenes_D.OT_NumRep = ORD_Ordenes_C.OT_NumRep) GROUP BY ORD_Ordenes_D.CodEmp, ORD_Ordenes_D.OT_NoParte, ORD_Ordenes_C.OT_Estado HAVING (((ORD_Ordenes_D.CodEmp)=1) AND ((Sum(ORD_Ordenes_D.OT_PE))<>0) AND ((ORD_Ordenes_C.OT_Estado)=4))) D on A.CodEmp=D.CodEmp and A.Codigo=D.OT_NoParte or D.OT_NoParte is null order by A.Codigo";
        System.out.println("query: " + query);
        ResultSet result = conexion.consulta(query);
        try {
            while (result.next()) {
                System.out.println("Codigo: " + result.getString(2));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregar() {
        if (numRecibo != null) {
            listFactura = new ArrayList<>();
            String selectSql2 = "SELECT * FROM FAC_Recibos_C where NumRecibo=" + numRecibo;
            ResultSet result = conexion.consulta(selectSql2);
            try {
                if (result.next()) {
                    selectSql2 = "SELECT * FROM FAC_Recibos_D where NumRecibo=" + result.getInt(1);
                    recibo = new ReciboC();
                    recibo.setNumRecibo(result.getInt(1));
                    recibo.setFecha(result.getDate(2));
                    recibo.setCodCliente(result.getInt(3));
                    recibo.setNit(result.getString(4));
                    recibo.setNombreCliente(result.getString(5));
                    recibo.setMontoRecibo(result.getDouble(6));
                    recibo.setConcepto(result.getString(7));
                    recibo.setEfectivo(result.getDouble(8));
                    recibo.setMontoCheque1(result.getDouble(9));
                    recibo.setNumCheque1(result.getString(10));
                    recibo.setFechaCheque1(result.getDate(11));
                    recibo.setCodBanco1(result.getInt(12));
                    recibo.setMontoCheque2(result.getDouble(13));
                    recibo.setNumCheque2(result.getString(14));
                    recibo.setFechaCheque2(result.getDate(15));
                    recibo.setCodBanco2(result.getInt(16));
                    recibo.setMontoTC(result.getDouble(17));
                    recibo.setNumTC(result.getString(18));
                    recibo.setTipoTC(result.getInt(19));
                    recibo.setTotal(result.getDouble(20));
                    recibo.setEstado(result.getString(21) != null ? Short.valueOf(result.getString(21)) : null);
                    recibo.setUsuario(result.getString(22));
                    recibo.setMontoDeposito(result.getDouble(23));
                    recibo.setNumBoleta(result.getString(24));
                    recibo.setBancoDeposito(result.getInt(25));
                    recibo.setCorreo(result.getString(26) != null ? result.getString(26) : "");
                    recibo.setWhatsapp(result.getString(27) != null ? result.getString(27) : "");
                    recibo.setTipoRecibo(result.getBoolean(28));
                    recibo.setNumOrden(result.getInt(29));
                    usuario = obtenerUsuario(recibo.getUsuario());
                    System.out.println("usuario tihsa: " + usuario);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tipo", "reimpresión");
                    monto = recibo.getMontoRecibo();
                    result = conexion.consulta(selectSql2);
                    Factura fac;
                    while (result.next()) {
                        fac = new Factura();
                        fac.setSerie(result.getString(5));
                        fac.setNumDTE(result.getString(6));
                        fac.setSaldo(result.getDouble(11));
                        fac.setAbono(Double.valueOf(result.getString(10)));
                        fac.setTotal(Double.valueOf(result.getString(9)));
                        fac.setFecha(result.getDate(8));
                        fac.setEstado(result.getString(12) != null ? Short.valueOf(result.getString(12)) : null);
                        fac.setSecuencia(result.getInt(2));
                        fac.setNumPago(result.getInt(13));
                        listFactura.add(fac);
                        if (fac.getEstado() != null && fac.getEstado() == 1) {
                            monto = Double.parseDouble(decimal2(monto)) - fac.getAbono();
                            System.out.println("abono: " + fac.getAbono());
                            System.out.println("monto: " + monto);
                        }
                    }
                    if (recibo.getEstado() != null && recibo.getEstado() == 1) {
                        monto = 0.0;
                    }
//                    if (recibo.getBancoDeposito() != null) {
//                        codCuenta = recibo.getBancoDeposito();
//                    }

                }
            } catch (SQLException ex) {
                Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void listarFacturas(int numRecibo) {
        String selectSql2 = "SELECT * FROM FAC_Recibos_D where NumRecibo=" + numRecibo;
        ResultSet result = conexion.consulta(selectSql2);
        listFactura = new ArrayList<>();
        monto = recibo.getMontoRecibo();
        try {
            Factura fac;
            while (result.next()) {
                fac = new Factura();
                fac.setSerie(result.getString(5));
                fac.setNumDTE(result.getString(6));
                fac.setSaldo(result.getDouble(11));
                fac.setAbono(result.getDouble(10));
                fac.setTotal(result.getDouble(9));
                fac.setFecha(result.getDate(8));
                fac.setEstado(result.getString(12) != null ? Short.valueOf(result.getString(12)) : null);
                fac.setSecuencia(result.getInt(2));
                fac.setNumPago(result.getInt(13));
                listFactura.add(fac);
                if (fac.getEstado() != null && fac.getEstado() == 1) {
                    monto = Double.parseDouble(decimal2(monto)) - fac.getAbono();
                    System.out.println("abono: " + fac.getAbono());
                    System.out.println("monto: " + monto);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Date parsearFecha(String fechaBD) {
        Date fecha = null;
        try {
            fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaBD);
        } catch (ParseException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return fecha;
    }

    public void obtenerBancos() {
        String query = "SELECT * FROM FAC_Bancos where CodBanco < 17;";
        ResultSet result = conexion.consulta(query);
        Banco banco;
        listBancos = new ArrayList<>();
        try {
            while (result.next()) {
                banco = new Banco();
                banco.setCodBanco(Integer.valueOf(result.getString(1)));
                banco.setNombre(result.getString(2).replace("(AHORRO EN DÓLARES)", ""));
                listBancos.add(banco);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void obtenerCuentas() {
        String query = "SELECT * FROM FAC_Cuentas;";
        ResultSet result = conexion.consulta(query);
        Banco banco;
        listCuentas = new ArrayList<>();
        try {
            while (result.next()) {
                banco = new Banco();
                banco.setCodBanco(Integer.valueOf(result.getString(2)));
                banco.setNombre(result.getString(3));
                listCuentas.add(banco);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void obtenerTipoTC() {
        String query = "SELECT * FROM FAC_TiposTC";
        ResultSet result = conexion.consulta(query);
        TipoTC tipoTC;
        listTipoTC = new ArrayList<>();
        try {
            while (result.next()) {
                tipoTC = new TipoTC();
                tipoTC.setTipoTC(result.getString(1));
                tipoTC.setNomTipoTC(result.getString(2));
                listTipoTC.add(tipoTC);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void obtenCorrelativoPago() {

    }

    public void sumarMontos() {
        Double suma = 0.0;
        if (recibo.getEfectivo() != null) {
            try {
                suma += recibo.getEfectivo();
            } catch (NumberFormatException e) {
                suma = 0.0;
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "El monto ingresado es incorreco", ""));
            }
        }
        if (recibo.getMontoCheque1() != null) {
            suma += recibo.getMontoCheque1();
            recibo.setFechaCheque1(new Date());
        } else {
            recibo.setFechaCheque1(null);
        }
        if (recibo.getMontoCheque2() != null) {
            suma += recibo.getMontoCheque2();
        }
        if (recibo.getMontoTC() != null) {
            suma += recibo.getMontoTC();
        }
        if (recibo.getMontoDeposito() != null) {
            suma += recibo.getMontoDeposito();
            recibo.setFechaCheque2(new Date());
        } else {
            recibo.setFechaCheque2(null);
        }
        if (suma > recibo.getMontoRecibo()) {
            monto = 0.0;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "La suma es mayor que el monto ingresado", ""));
        } else {
            monto = recibo.getMontoRecibo();
        }
        recibo.setTotal(suma);
        listFactura = new ArrayList<>();
        PrimeFaces.current().ajax().update("frmIngreso:tblFacturas");
    }

    public void sumarMontos2() {
        Double suma = 0.0;
        if (recibo.getEfectivo() != null) {
            suma += recibo.getEfectivo();
        }
        if (recibo.getMontoCheque1() != null) {
            suma += recibo.getMontoCheque1();
            recibo.setFechaCheque1(new Date());
        } else {
            recibo.setFechaCheque1(null);
        }
        if (recibo.getMontoCheque2() != null) {
            suma += recibo.getMontoCheque2();
        }
        if (recibo.getMontoTC() != null) {
            suma += recibo.getMontoTC();
        }
        if (recibo.getMontoDeposito() != null) {
            suma += recibo.getMontoDeposito();
            recibo.setFechaCheque2(new Date());
        } else {
            recibo.setFechaCheque2(null);
        }
        recibo.setTotal(suma);
    }

    public void obenerParametros() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map params = externalContext.getRequestParameterMap();
        try {
            opcion = Integer.valueOf(params.get("op").toString());
            if (opcion != null && opcion != 1) {
                numRecibo = params.get("numRecibo").toString();
                if (params.get("ver").toString() != null) {
                    verRecibo = Integer.valueOf(params.get("ver").toString()) == 1 ? true : false;
                    System.out.println("Ver recibo");
                } else {
                    verRecibo = false;
                    System.out.println("No ver recibo");
                }
                if (params.get("w").toString() != null) {
                    sndWhatsapp = Integer.valueOf(params.get("w").toString()) == 1;
                } else {
                    sndWhatsapp = false;
                }
                if (params.get("c").toString() != null) {
                    sendCorreo = Integer.valueOf(params.get("c").toString()) == 1;
                } else {
                    sendCorreo = false;
                }
                agregar();
            } else {
                System.out.println("Nuevo recibo");
                if (opcion == 1) {
                    if (usuario == null) {
                        user = params.get("user").toString();
                        usuario = obtenerUsuario(user);
                        recibo.setUsuario(user);
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
                    }
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("tipo", "impresión");
                    if (params.get("w").toString() != null) {
                        sndWhatsapp = Integer.valueOf(params.get("w").toString()) == 1;
                    } else {
                        sndWhatsapp = false;
                    }
                    if (params.get("c").toString() != null) {
                        sendCorreo = Integer.valueOf(params.get("c").toString()) == 1;
                    } else {
                        sendCorreo = false;
                    }

                }
            }
        } catch (NullPointerException e) {
            System.out.println("Error obener parametros");
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No vienen datos", ""));
        }
    }

    public String obtenerUsuario(String usuario) {
        String nombre = "";
        String query = "SELECT Nombre FROM FAC_Usuarios where Usuario='" + usuario + "'";
        ResultSet result = conexion.consulta(query);
        try {
            if (result.next()) {
                nombre = result.getString(1);
            } else {
                nombre = "Sin nombre";
                System.out.println("No se encontro el usuario");
            }
        } catch (SQLException ex) {
            nombre = "Sin nombre";
            System.out.println("Fallo en la consulta");
        }
        return nombre;
    }

    public String obtenerCorreo(String correo) {
        if (correo != null) {
            String[] email = correo.split(" ");
            if (email != null) {
                return email[0];
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public void buscaCodigo() {
        if (recibo.getCodCliente() != null && recibo.getCodCliente() != 9999) {
            String query = "SELECT NIT, Nombre, Celular, Email FROM FAC_Clientes where CodigoCliente=" + recibo.getCodCliente();
            ResultSet result = conexion.consulta(query);
            try {
                if (result.next()) {
                    recibo.setNit(result.getString(1));
                    recibo.setNombreCliente(result.getString(2));
                    recibo.setWhatsapp(result.getString(3).replace("-", ""));
                    recibo.setCorreo(obtenerCorreo(result.getString(4)).replace(";", ""));
                    consultaFacturas();
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se encontró el código ingresado", ""));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void buscaNit() {
        String query = "SELECT CodigoCliente, Nombre, Celular, Email FROM FAC_Clientes where NIT='" + recibo.getNit() + "'";
        ResultSet result = conexion.consulta(query);
        try {
            if (result.next()) {
                recibo.setCodCliente(Integer.valueOf(result.getString(1)));
                recibo.setNombreCliente(result.getString(2));
                recibo.setWhatsapp(result.getString(3).replace("-", ""));
                recibo.setCorreo(obtenerCorreo(result.getString(4)).replace(";", ""));
                consultaFacturas();
            } else {
                System.out.println("Busca en clientes 9999");
                query = "SELECT CodigoCliente, Nombre, Celular, Email FROM FAC_Clientes9999 where NIT='" + recibo.getNit() + "'";
                result = conexion.consulta(query);
                if (result.next()) {
                    recibo.setCodCliente(Integer.valueOf(result.getString(1)));
                    recibo.setNombreCliente(result.getString(2));
                    recibo.setWhatsapp(result.getString(3) != null ? result.getString(3).replace("-", "") : null);
                    recibo.setCorreo(obtenerCorreo(result.getString(4)).replace(";", ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se encontró el NIT ingresado", ""));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void obtenerClientes() {
        String query = "SELECT CodigoCliente, Nombre, NIT, Celular, Email FROM FAC_Clientes where Activo=1";
        ResultSet result = conexion.consulta(query);
        System.out.println("query: " + query);
        try {
            listCliente = new ArrayList<>();
            Cliente cliente;
            while (result.next()) {
                cliente = new Cliente();
                cliente.setCodigoCliente(Integer.valueOf(result.getString(1)));
                cliente.setNombre(result.getString(2));
                cliente.setNit(result.getString(3));
                cliente.setWhatsapp(result.getString(4) != null ? result.getString(4).replace("-", "") : "");
                cliente.setCorreo(obtenerCorreo(result.getString(5)).replace(";", ""));
                listCliente.add(cliente);
                listAllCliente.add(cliente);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarCliente() {
        if (!listAllCliente.isEmpty()) {
            String nombre;
            CharSequence content = buscar.toLowerCase();
            List<Cliente> lista = new ArrayList<>();
            for (Cliente cliente : listAllCliente) {
                nombre = cliente.getNombre().toLowerCase();
                nombre = nombre.replace("í", "i");
                nombre = nombre.replace("ó", "o");
                nombre = nombre.replace("ú", "u");
                nombre = nombre.replace("á", "a");
                nombre = nombre.replace("é", "e");
                if (nombre.contains(content) || cliente.getNit().contains(content) || cliente.getCodigoCliente().toString().contains(content)) {
                    lista.add(cliente);
                }
            }
            listCliente = lista;
        }
    }

    public void selectCliente(SelectEvent event) {
        Cliente cliente = (Cliente) event.getObject();
        recibo.setCodCliente(cliente.getCodigoCliente());
        recibo.setNombreCliente(cliente.getNombre());
        recibo.setNit(cliente.getNit());
        recibo.setWhatsapp(cliente.getWhatsapp());
        recibo.setCorreo(cliente.getCorreo());
        consultaFacturas();
    }

    public void selectCliente2(Cliente event) {
        Cliente cliente = event;
        recibo.setCodCliente(cliente.getCodigoCliente());
        recibo.setNombreCliente(cliente.getNombre());
        recibo.setNit(cliente.getNit());
        recibo.setWhatsapp(cliente.getWhatsapp());
        recibo.setCorreo(cliente.getCorreo());
        consultaFacturas();
    }

    public void consultaFacturas() {
        // String query = "select SerieFactura, NumFactura, FechaFac, Saldo, (select saldo from FAC_Recibos_D R where R.SerieFactura=Q.SerieFactura and R.NumDTE=Q.NumFactura and (R.Estado=1 or R.Estado is null)) as SaldoRecibo "
        //         + " from QRY_FAC_SaldosPagosHis Q where NIT='" + recibo.getNit() + "' "
        //         + "and ((select saldo from FAC_Recibos_D R where R.SerieFactura=Q.SerieFactura and R.NumDTE=Q.NumFactura and (R.Estado=1 or R.Estado is null))<>0 "
        //         + "or (select saldo from FAC_Recibos_D R where R.SerieFactura=Q.SerieFactura and R.NumDTE=Q.NumFactura and (R.Estado=1 or R.Estado is null)) is null) "
        //         + "and Cancelada=0 order by FechaFac asc;";
        String query = "select SerieFactura, NumFactura, FechaFac, Saldo, (Saldo-(select sum(abono) from FAC_Recibos_D R where R.SerieFactura=Q.SerieFactura and "
                + "R.NumDTE=Q.NumFactura and R.Estado is null)) as SaldoRecibo from QRY_FAC_SaldosPagosHis Q where NIT='" + recibo.getNit() + "' and "
                + "((select sum(abono) from FAC_Recibos_D R where R.SerieFactura=Q.SerieFactura and R.NumDTE=Q.NumFactura and (R.Estado=1 or R.Estado is null))<>Saldo "
                + "or (select sum(abono) from FAC_Recibos_D R where R.SerieFactura=Q.SerieFactura and R.NumDTE=Q.NumFactura and (R.Estado=1 or R.Estado is null)) "
                + "is null) and Cancelada=0 order by FechaFac asc;";
        System.out.println("Consulta facturas: " + query);
        ResultSet result = conexion.consulta(query);
        try {
            listAllFactura = new ArrayList<>();
            listDlgFactura = new ArrayList<>();
            Factura factura;
            while (result.next()) {
                factura = new Factura();
                factura.setSerie(result.getString(1));
                factura.setNumDTE(result.getString(2));
                factura.setFecha(parsearFecha(result.getString(3)));
                factura.setTotal(Double.valueOf(result.getString(5) != null ? result.getString(5) : result.getString(4)));
                //factura.setNumAutorizacion(result.getString(6));
                listAllFactura.add(factura);
                listDlgFactura.add(factura);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String decimal(Double doub) {
        String formato;
        if (doub != null) {
            DecimalFormat format = new DecimalFormat("##,###,##0.00");
            formato = format.format(doub);
        } else {
            formato = "0.00";
        }
        return formato;
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

    public String decimal2(Double doub) {
        String formato;
        if (doub != null) {
            DecimalFormat format = new DecimalFormat("#######0.00");
            formato = format.format(doub);
        } else {
            formato = "0.00";
        }
        return formato;
    }

    public String formatRCE(Integer doub) {
        String formato;
        if (doub != null) {
            DecimalFormat format = new DecimalFormat("000000");
            formato = format.format(doub);
        } else {
            formato = "000";
        }
        return formato;
    }

    public void pagarFacturasAntiguas() {
        if (recibo.getMontoRecibo() != null) {
            if (!listAllFactura.isEmpty()) {
                listFactura = new ArrayList<>();
                Factura newFactura;
                monto = recibo.getMontoRecibo();
                System.out.println("Monto1: " + monto);
                for (Factura factura : listAllFactura) {
                    if (monto != 0) {
                        newFactura = new Factura();
                        newFactura.setSerie(factura.getSerie());
                        newFactura.setNumDTE(factura.getNumDTE());
                        newFactura.setFecha(factura.getFecha());
                        newFactura.setTotal(factura.getTotal());
                        if (factura.getTotal() > monto) {
                            newFactura.setAbono((double) monto);
                            newFactura.setSaldo(factura.getTotal() - monto);
                            System.out.println("factura1: " + factura.getTotal());
                            System.out.println("monto1: " + decimal(monto));
                            monto = 0.0;
                        } else if (Objects.equals(factura.getTotal(), monto)) {
                            newFactura.setAbono(factura.getTotal());
                            newFactura.setSaldo(0.0);
                            monto = 0.0;
                            System.out.println("factura2: " + factura.getTotal());
                            System.out.println("monto2: " + decimal(monto));
                        } else {
                            newFactura.setAbono(factura.getTotal());
                            newFactura.setSaldo(0.0);
                            monto -= factura.getTotal();
                            monto = Double.parseDouble(decimal2(monto));
                            System.out.println("factura3: " + factura.getTotal());
                            System.out.println("monto3: " + decimal(monto));
                        }
                        listFactura.add(newFactura);
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se encontraron facturas por cancelar", ""));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese el monto del recibo", ""));
        }
    }

    public void buscarFacturas() {
        if (!listAllCliente.isEmpty()) {
            CharSequence content = buscaFactura.toUpperCase();
            CharSequence serie = buscaSerie.toUpperCase().trim();
            listDlgFactura = new ArrayList<>();
            for (Factura fact : listAllFactura) {
                if (fact.getSerie().equals(serie) && fact.getNumDTE().contains(content)) {
                    listDlgFactura.add(fact);
                }
            }
        }
    }

    public void validarMonto() {

        if (recibo.getMontoRecibo() != null) {
            if (listFactura.isEmpty()) {
                monto = recibo.getMontoRecibo();
            }
            PrimeFaces.current().executeScript("PF('dlgFacturas').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese el monto del recibo", ""));
        }
    }

    public void selectFactura(SelectEvent event) {
        if (recibo.getMontoRecibo() != null) {
            Factura factura = (Factura) event.getObject();
            if (monto != 0) {
                Boolean ok = true;
                if (!listFactura.isEmpty()) {
                    for (Factura fact : listFactura) {
                        if (fact.getSerie().equals(factura.getSerie()) && fact.getNumDTE().equals(factura.getNumDTE())) {
                            ok = false;
                            break;
                        }
                    }
                }
                if (ok) {
                    Factura newFactura = new Factura();
                    newFactura.setSerie(factura.getSerie());
                    newFactura.setNumDTE(factura.getNumDTE());
                    newFactura.setFecha(factura.getFecha());
                    newFactura.setTotal(factura.getTotal());
                    if (factura.getTotal() > monto) {
                        newFactura.setAbono((double) monto);
                        newFactura.setSaldo(factura.getTotal() - monto);
                        monto = 0.0;
                    } else if (Objects.equals(monto, factura.getTotal())) {
                        newFactura.setAbono(factura.getTotal());
                        newFactura.setSaldo(0.0);
                        monto = 0.0;
                    } else {
                        newFactura.setAbono(factura.getTotal());
                        newFactura.setSaldo(0.0);
                        monto -= factura.getTotal();
                        monto = Double.parseDouble(decimal2(monto));
                    }
                    listFactura.add(newFactura);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "La factura ya se encuentra agregada a la lista", ""));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "El monto del recibo es insuficiente para agregar otra factura", ""));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese el monto del recibo", ""));
        }
    }

    public void selectFactura2(Factura event) {
        if (recibo.getMontoRecibo() != null) {
            Factura factura = event;
            if (monto != 0) {
                Boolean ok = true;
                if (!listFactura.isEmpty()) {
                    for (Factura fact : listFactura) {
                        if (fact.getSerie().equals(factura.getSerie()) && fact.getNumDTE().equals(factura.getNumDTE())) {
                            ok = false;
                            break;
                        }
                    }
                }
                if (ok) {
                    Factura newFactura = new Factura();
                    newFactura.setSerie(factura.getSerie());
                    newFactura.setNumDTE(factura.getNumDTE());
                    newFactura.setFecha(factura.getFecha());
                    newFactura.setTotal(factura.getTotal());
                    if (factura.getTotal() > monto) {
                        newFactura.setAbono((double) monto);
                        newFactura.setSaldo(factura.getTotal() - monto);
                        monto = 0.0;
                    } else if (Objects.equals(monto, factura.getTotal())) {
                        newFactura.setAbono(factura.getTotal());
                        newFactura.setSaldo(0.0);
                        monto = 0.0;
                    } else {
                        newFactura.setAbono(factura.getTotal());
                        newFactura.setSaldo(0.0);
                        monto -= factura.getTotal();
                        monto = Double.parseDouble(decimal2(monto));
                    }
                    listFactura.add(newFactura);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "La factura ya se encuentra agregada a la lista", ""));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "El monto del recibo es insuficiente para agregar otra factura", ""));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese el monto del recibo", ""));
        }
    }

    public void quitarFactura(Factura factura) {
        monto += Float.parseFloat(factura.getAbono().toString());
        listFactura.remove(factura);
    }

    public void obtenerUltimoRecibo() {
        String query = "SELECT NumRecibo FROM FAC_Recibos_C order by NumRecibo desc";
        ResultSet result = conexion.consulta(query);
        try {
            if (result.next()) {
                System.out.println(result.getString(1));
                recibo.setNumRecibo(Integer.valueOf(result.getString(1)) + 1);
            } else {
                recibo.setNumRecibo(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (recibo.getNumRecibo() == null) {
            recibo.setNumRecibo(1);
        }
        recibo.setFecha(new Date());
    }

    public void obtenerUltimoRecibo2() {
        String query = "SELECT NumRecibo FROM FAC_Recibos_C order by NumRecibo desc";
        ResultSet result = conexion.consulta(query);
        try {
            if (result.next()) {
                System.out.println(result.getString(1));
                recibo.setNumRecibo((result.getInt(1) + 1));
            } else {
                recibo.setNumRecibo(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (recibo.getNumRecibo() == null) {
            recibo.setNumRecibo(1);
        }
    }

    public void borrarFacturas() {
        listFactura = new ArrayList<>();
        monto = recibo.getMontoRecibo();
    }

    public String obtenerBanco(Integer codigo) {
        String nombre = "";
        for (Banco banco : listBancos) {
            if (Objects.equals(banco.getCodBanco(), codigo)) {
                nombre = banco.getNombre();
            }
        }
        return nombre;
    }

    public void confirmarGuardado() {
        System.out.println("whatsapp: " + recibo.getWhatsapp());
        if (recibo.getWhatsapp() == null || recibo.getWhatsapp().isEmpty()) {
            System.out.println("wasap null");
            sndWhatsapp = false;
        } else {
            sndWhatsapp = true;
            System.out.println("wasap not null");
        }
        if (sendCorreo || sndWhatsapp) {
            mensaje = "El recibo se enviará a";
            if (sendCorreo) {
                mensaje += ": " + recibo.getCorreo();
            }
            if (sndWhatsapp) {
                mensaje += sendCorreo ? " y al " + recibo.getWhatsapp() : "l: " + recibo.getWhatsapp();
            }
            PrimeFaces.current().executeScript("PF('dlgConf').show();");
        } else {
            guardar();
        }
    }

    public void confirmarAnulado() {
        System.out.println("whatsapp: " + recibo.getWhatsapp());
        if (recibo.getWhatsapp() == null || recibo.getWhatsapp().isEmpty()) {
            System.out.println("wasap null");
            sndWhatsapp = false;
        } else {
            sndWhatsapp = true;
            System.out.println("wasap not null");
        }
        if (sendCorreo || sndWhatsapp) {
            mensaje = "El recibo se enviará a";
            if (sendCorreo) {
                mensaje += ": " + recibo.getCorreo();
            }
            if (sndWhatsapp) {
                mensaje += sendCorreo ? " y al " + recibo.getWhatsapp() : "l: " + recibo.getWhatsapp();
            }
            PrimeFaces.current().executeScript("PF('dlgConf').show();");
        } else {
            guardar();
        }
    }

    public void guardar() {
        System.out.println("Monto: " + recibo.getMontoRecibo());
        System.out.println("Total: " + recibo.getTotal());
        if (!Objects.equals(recibo.getMontoRecibo(), recibo.getTotal())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "El monto del recibo no cuadra con el total", ""));
        } else {
            if (recibo.getTipoRecibo() != null && recibo.getTipoRecibo()) {
                recibo.setEstado((short) 1);
            }
            obtenerUltimoRecibo2();
            if (conexion.insertar(recibo, listFactura)) {
                guardado = true;
                if (recibo.getCodBanco1() != null) {
                    recibo.setNombreBanco1(obtenerBanco(recibo.getCodBanco1()));
                }
                if (recibo.getCodBanco2() != null) {
                    recibo.setNombreBanco2(obtenerBanco(recibo.getCodBanco2()));
                }
                if (recibo.getBancoDeposito() != null) {
                    recibo.setNombreBancoD(obtenerBanco(recibo.getBancoDeposito()));
                }
                ImprimirRecibo print = new ImprimirRecibo();
                if (print.printLista(listFactura, recibo)) {
                    InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/archivos/RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf");
                    fileDown = new DefaultStreamedContent(stream, "document/pdf", "RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf");
                    if (sendCorreo) {
                        if (recibo.getCorreo() != null || !recibo.getCorreo().trim().isEmpty()) {
                            enviarCorreo();
                        }
                    }
                    if (sndWhatsapp) {
                        if (recibo.getWhatsapp() != null || !recibo.getWhatsapp().trim().isEmpty()) {
                            sendWhatsapp();
                        }
                    }
                    PrimeFaces.current().executeScript("PF('dlgPDF2').show();");
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "El recibo ha sido guardado correctamente", ""));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar generar el archivo", ""));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
            }
        }
    }

    public void revertir() {
        for (Factura factura : listFactura) {
            factura.setEstado(null);
        }
        recibo.setEstado(null);
        monto = recibo.getMontoRecibo();
        if (conexion.revertir(recibo, listFactura)) {
            listarFacturas(recibo.getNumRecibo());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "El pago aplicado ha sido revertido", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
        }
    }

    public void update() {
        if (descuento != null && descuento != 0) {
            for (Factura factura : listFactura) {
                if (factura.getAbono() > descuento) {
                    factura.setAbono(factura.getAbono() - descuento);
                    factura.setDescuento(descuento);
                }
            }
        }
        if (listFactura.size() > 0) {
            int size = 0;
            for (Factura factura : listFactura) {
                if (factura.getEstado() != null && factura.getEstado() == 1) {
                    size++;
                }
            }
            if (listFactura.size() == size) {
                recibo.setEstado((short) 1);
            }
        } else if (recibo.getTipoRecibo()) {
            recibo.setEstado((short) 1);
        }
        if (conexion.actualizar(recibo, listFactura, codCuenta, descuento, referencia)) {
            listarFacturas(recibo.getNumRecibo());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "El recibo ha sido actualizado correctamente", ""));
            PrimeFaces.current().executeScript("PF('dlgCuenta').hide();");
            PrimeFaces.current().ajax().update("frmIngreso:tblFacturas");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
        }
    }

    public void anular() {
        if (recibo.getWhatsapp() == null || recibo.getWhatsapp().isEmpty()) {
            System.out.println("wasap null");
            sndWhatsapp = false;
        } else {
            sndWhatsapp = true;
            System.out.println("wasap not null");
        }

        if (recibo == null && listFactura.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay registros que se puedan anular", ""));
        } else {
            recibo.setEstado((short) 0);
            for (Factura factura : listFactura) {
                factura.setEstado((short) 0);
            }
            if (conexion.anular(recibo, listFactura)) {
                if (sendCorreo) {
                    if (recibo.getCorreo() != null || !recibo.getCorreo().trim().isEmpty()) {
                        enviarCorreo2();
                    }
                }
                if (sndWhatsapp) {
                    if (recibo.getWhatsapp() != null || !recibo.getWhatsapp().trim().isEmpty()) {
                        sendWhatsapp2();
                    }
                }
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "El recibo ha sido anulado correctamente", ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
            }
        }

    }

    public void anular2() {
        if (recibo == null && listFactura.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay registros que se puedan anular", ""));
        } else {
            recibo.setEstado((short) 0);
            for (Factura factura : listFactura) {
                factura.setEstado((short) 0);
            }
            if (conexion.anular(recibo, listFactura)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "El recibo ha sido anulado correctamente", ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar realizar la operación", ""));
            }
        }

    }

    public void reimprimir() {
        ImprimirRecibo print = new ImprimirRecibo();
        if (recibo.getCodBanco1() != null) {
            recibo.setNombreBanco1(obtenerBanco(recibo.getCodBanco1()));
        }
        if (recibo.getCodBanco2() != null) {
            recibo.setNombreBanco2(obtenerBanco(recibo.getCodBanco2()));
        }
        if (recibo.getBancoDeposito() != null) {
            recibo.setNombreBancoD(obtenerBanco(recibo.getBancoDeposito()));
        }
        if (print.printLista(listFactura, recibo)) {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/archivos/RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf");
            fileDown = new DefaultStreamedContent(stream, "document/pdf", "RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf");
            PrimeFaces.current().ajax().update("frmPDF2");
            PrimeFaces.current().executeScript("PF('dlgPDF3').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar generar el archivo", ""));
        }
    }

    public void reimprimirMovil() {
        ImprimirRecibo print = new ImprimirRecibo();
        if (recibo.getCodBanco1() != null) {
            recibo.setNombreBanco1(obtenerBanco(recibo.getCodBanco1()));
        }
        if (recibo.getCodBanco2() != null) {
            recibo.setNombreBanco2(obtenerBanco(recibo.getCodBanco2()));
        }
        if (recibo.getBancoDeposito() != null) {
            recibo.setNombreBancoD(obtenerBanco(recibo.getBancoDeposito()));
        }
        if (print.printLista(listFactura, recibo)) {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/archivos/RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf");
            fileDown = new DefaultStreamedContent(stream, "document/pdf", "RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf");
            PrimeFaces.current().ajax().update("frmReimprimir");
            PrimeFaces.current().executeScript("PF('dlgReimprimir').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar generar el archivo", ""));
        }
    }

    public void limpiar() {
        System.out.println("limpiar");
        guardado = false;
        listFactura = new ArrayList<>();
        recibo = new ReciboC();
        recibo.setUsuario(user);
        System.out.println("User: " + user);
        obtenerUltimoRecibo();
        PrimeFaces.current().executeScript("PF('wiz').loadStep(PF('wiz').cfg.steps[0],true);");
    }

    public void reimprimir2() {
        ImprimirRecibo print = new ImprimirRecibo();
        if (recibo.getCodBanco1() != null) {
            recibo.setNombreBanco1(obtenerBanco(recibo.getCodBanco1()));
        }
        if (recibo.getCodBanco2() != null) {
            recibo.setNombreBanco2(obtenerBanco(recibo.getCodBanco2()));
        }
        if (recibo.getBancoDeposito() != null) {
            recibo.setNombreBancoD(obtenerBanco(recibo.getBancoDeposito()));
        }
        if (print.printLista(listFactura, recibo)) {
            PrimeFaces.current().executeScript("PF('dlgPDF').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar generar el archivo", ""));
        }
    }

    public void reimprimir3() {
        if (recibo.getCodBanco1() != null) {
            recibo.setNombreBanco1(obtenerBanco(recibo.getCodBanco1()));
        }
        if (recibo.getCodBanco2() != null) {
            recibo.setNombreBanco2(obtenerBanco(recibo.getCodBanco2()));
        }
        if (recibo.getBancoDeposito() != null) {
            recibo.setNombreBancoD(obtenerBanco(recibo.getBancoDeposito()));
        }
        ImprimirRecibo print = new ImprimirRecibo();
        if (print.printLista(listFactura, recibo)) {
            //enviarCorreo();

            PrimeFaces.current().ajax().update("frmPDF2");
            PrimeFaces.current().executeScript("PF('dlgPDF').show();");
            System.out.println("Recibo No. " + recibo.getNumRecibo());
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar generar el archivo", ""));
        }
    }

    public void enviarCorreo() {
        // TODO add your handling code here:
        String correo = "info@tihsa.com"; //tu correo
        String password = "IT2020rec"; //tu contraseña
        String mensaje = "Tihsa te agradece por el pago realizado por un monto de Q " + decimal(recibo.getMontoRecibo()) + ", el día " + formatoFecha(recibo.getFecha())
                + ", según detalle en recibo de caja No. RCE-" + formatRCE(recibo.getNumRecibo()) + ". Consultas al teléfono 2506-2909";
//        String mensaje = "Gracias por el pago realizado a su cuenta con NIT " + recibo.getNit() + " por un monto de Q " + decimal(recibo.getMontoRecibo())
//                + " el dia " + formatoFecha(recibo.getFecha()) + ". Para más información comunicarse al 2506 2900";
        String titulo = "Tienda Industrial de Herramientas, S.A.";
        String emisor = "info@tihsa.com";//tu correo
        String receptor = recibo.getCorreo();

        try {
            Properties p = new Properties();

            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");//host de hotmail
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");//puerto de conexion
            props.setProperty("mail.smtp.user", correo); //verificacion del correo
            props.setProperty("mail.smtp.auth", "false");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emisor));
            message.addRecipients(Message.RecipientType.TO, receptor); // remitente
            message.addRecipients(Message.RecipientType.TO, "recibos@tihsa.com"); // remitente
            message.setSubject(titulo);
            message.setText(mensaje,
                    "ISO-8859-1", "html");
            String ruta = getPath() + "resources/archivos/RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf";
            BodyPart texto = new MimeBodyPart();
            texto.setText(mensaje);
            BodyPart archivo = new MimeBodyPart();
            archivo.setDataHandler(new DataHandler(new FileDataSource(new File(ruta))));
            archivo.setFileName("Recibo de Caja.pdf");
            MimeMultipart body = new MimeMultipart();
            body.addBodyPart(texto);
            body.addBodyPart(archivo);
            message.setContent(body);
            Transport t = session.getTransport("smtp");
            t.connect(correo, password); // aquie es lo delicado donde se loguea
            t.sendMessage(message, message.getAllRecipients());// aqi envia el mensaje
            t.close();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Correo enviado", ""));
        } catch (HeadlessException | MessagingException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar enviar el correo", ""));
            e.printStackTrace();
        }
    }

    public void enviarCorreo2() {
        // TODO add your handling code here:
        String correo = "info@tihsa.com"; //tu correo
        String password = "IT2020rec"; //tu contraseña
        String msj = "Tihsa te informa que se ha anulado tu recibo de caja No. RCE-" + formatRCE(recibo.getNumRecibo()) + ". Motivo: " + contenido+" - Consultas: 2506-2909";
//        String mensaje = "Gracias por el pago realizado a su cuenta con NIT " + recibo.getNit() + " por un monto de Q " + decimal(recibo.getMontoRecibo())
//                + " el dia " + formatoFecha(recibo.getFecha()) + ". Para más información comunicarse al 2506 2900";
        String titulo = "Tienda Industrial de Herramientas, S.A.";
        String emisor = "Tihsa<info@tihsa.com>";//tu correo
        String receptor = recibo.getCorreo();

        try {
            Properties p = new Properties();

            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");//host de hotmail
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");//puerto de conexion
            props.setProperty("mail.smtp.user", correo); //verificacion del correo
            props.setProperty("mail.smtp.auth", "false");
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emisor));
            message.addRecipients(Message.RecipientType.TO, receptor); // remitente
            message.setSubject(titulo);
            message.setText(msj,
                    "ISO-8859-1", "html");
            //String ruta = getPath() + "resources/archivos/RCE-" + formatRCE(recibo.getNumRecibo()) + ".pdf";
            BodyPart texto = new MimeBodyPart();
            texto.setText(msj);
            //BodyPart archivo = new MimeBodyPart();
            //archivo.setDataHandler(new DataHandler(new FileDataSource(new File(ruta))));
            //archivo.setFileName("Recibo de Caja.pdf");
            MimeMultipart body = new MimeMultipart();
            body.addBodyPart(texto);
            // body.addBodyPart(archivo);
            message.setContent(body);
            Transport t = session.getTransport("smtp");
            t.connect(correo, password); // aquie es lo delicado donde se loguea
            t.sendMessage(message, message.getAllRecipients());// aqi envia el mensaje
            t.close();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Correo enviado", ""));
        } catch (HeadlessException | MessagingException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hubo un error al intentar enviar el correo", ""));
            e.printStackTrace();
        }
    }

    public void sendWhatsapp() {
        try {
            String token = "0a4409cee7d702039d7821e6a53abafb5d79435a7c300";
            String UID = "50245790716";
            SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
            String custom_id = sdf.format(new Date());
            String texto = "Tihsa te agradece por el pago realizado por un monto de Q " + decimal(recibo.getMontoRecibo()) + " el dia " + formatoFecha(recibo.getFecha())
                    + ", segun detalle en recibo de caja No. RCE-" + formatRCE(recibo.getNumRecibo()) + ". Consultas al telefono 2506-2909";
            String dir = "https://www.waboxapp.com/api/send/chat?token=" + token + "&uid=" + UID + "&to=502" + recibo.getWhatsapp() + "&custom_uid=msg-" + custom_id + "&text=" + URLEncoder.encode(texto);
            System.out.println(dir);
            URL url = new URL(dir);//your url i.e fetch data from .
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falló el envío del whatsapp", ""));
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());

            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Whatsapp enviado", ""));
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }
    }

    public void sendWhatsapp2() {
        try {
            String token = "0a4409cee7d702039d7821e6a53abafb5d79435a7c300";
            String UID = "50245790716";
            SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
            String custom_id = sdf.format(new Date());
            String texto = "Tihsa te informa que se ha anulado tu recibo de caja No. RCE-" + formatRCE(recibo.getNumRecibo())+". Motivo: " +contenido.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u") + " - Consultas: 2506-2909";
            String dir = "https://www.waboxapp.com/api/send/chat?token=" + token + "&uid=" + UID + "&to=502" + recibo.getWhatsapp() + "&custom_uid=msg-" + custom_id + "&text=" + URLEncoder.encode(texto);
            System.out.println(dir);
            URL url = new URL(dir);//your url i.e fetch data from .
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falló el envío del whatsapp", ""));
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());

            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Whatsapp enviado", ""));
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }
    }


    public String getPath() { //para obtener toda la direccion url (localhost/AltaIdea/)
        try {
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
                    .getExternalContext().getContext();
            return ctx.getRealPath("/"); //quiere decir que desde la carpeta raiz tomara la direccion
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "getPath() " + e.getLocalizedMessage(), ""));
        }
        return null;
    }

    public void aplicar(Factura item) {
        item.setEstado((short) 1);
        monto -= Float.parseFloat(item.getAbono().toString());
    }

    public void deshacer(Factura item) {
        if (item.getEstado() == 1) {
            monto += Float.parseFloat(item.getAbono().toString());
        }
        item.setEstado(null);

    }

    public void anular(Factura item) {
        item.setEstado((short) 0);
    }

    public void aplicarTodos() {
        if (!listFactura.isEmpty()) {
            for (Factura factura : listFactura) {
                factura.setEstado((short) 1);
            }
            monto = 0.0;
        } else {
            monto = 0.0;
        }
    }

    public String formatoFecha(Date date) {
        String fecha;
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                fecha = sdf.format(date);
            } catch (Exception e) {
                fecha = "";
            }
        } else {
            fecha = "---";
        }
        return fecha;
    }

    public String formatoFecha2(Date date) {
        String fecha;
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                fecha = sdf.format(date);
            } catch (Exception e) {
                fecha = "";
            }
        } else {
            fecha = "---";
        }
        return fecha;
    }

    public Boolean desactivar() {
        if (recibo.getTipoRecibo() != null && recibo.getTipoRecibo()) {
            if (!Objects.equals(recibo.getMontoRecibo(), recibo.getTotal())) {
                return true;
            } else {
                System.out.println("recibo true");
                return false;
            }
        } else {
            if (guardado) {
                System.out.println("guardado true");
                return true;
            } else {
                return !(monto == 0 && recibo.getMontoRecibo() != null);
            }
        }
    }

    //********************************************************************************VERSION MOVIL***********************************************//
    public String onFlowProcess(FlowEvent event) {
        return event.getNewStep();
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public List<Factura> getListFactura() {
        return listFactura;
    }

    public void setListFactura(List<Factura> listFactura) {
        this.listFactura = listFactura;
    }

    public String getNit() {
        return numRecibo;
    }

    public void setNit(String nit) {
        this.numRecibo = nit;
    }

    public List<Banco> getListBancos() {
        return listBancos;
    }

    public void setListBancos(List<Banco> listBancos) {
        this.listBancos = listBancos;
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

    public List<TipoTC> getListTipoTC() {
        return listTipoTC;
    }

    public void setListTipoTC(List<TipoTC> listTipoTC) {
        this.listTipoTC = listTipoTC;
    }

    public String getTipoTC() {
        return tipoTC;
    }

    public void setTipoTC(String tipoTC) {
        this.tipoTC = tipoTC;
    }

    public ReciboC getRecibo() {
        return recibo;
    }

    public void setRecibo(ReciboC recibo) {
        this.recibo = recibo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<Cliente> getListCliente() {
        return listCliente;
    }

    public void setListCliente(List<Cliente> listCliente) {
        this.listCliente = listCliente;
    }

    public String getBuscar() {
        return buscar;
    }

    public void setBuscar(String buscar) {
        this.buscar = buscar;
    }

    public List<Factura> getListAllFactura() {
        return listAllFactura;
    }

    public void setListAllFactura(List<Factura> listAllFactura) {
        this.listAllFactura = listAllFactura;
    }

    public List<Factura> getListDlgFactura() {
        return listDlgFactura;
    }

    public void setListDlgFactura(List<Factura> listDlgFactura) {
        this.listDlgFactura = listDlgFactura;
    }

    public String getBuscaFactura() {
        return buscaFactura;
    }

    public void setBuscaFactura(String buscaFactura) {
        this.buscaFactura = buscaFactura;
    }

    public String getBuscaSerie() {
        return buscaSerie;
    }

    public void setBuscaSerie(String buscaSerie) {
        this.buscaSerie = buscaSerie;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getNumRecibo() {
        return numRecibo;
    }

    public void setNumRecibo(String numRecibo) {
        this.numRecibo = numRecibo;
    }

    public Integer getOpcion() {
        return opcion;
    }

    public void setOpcion(Integer opcion) {
        this.opcion = opcion;
    }

    public String getRutaPdf() {
        return rutaPdf;
    }

    public void setRutaPdf(String rutaPdf) {
        this.rutaPdf = rutaPdf;
    }

    public Boolean getGuardado() {
        return guardado;
    }

    public void setGuardado(Boolean guardado) {
        this.guardado = guardado;
    }

    public List<Banco> getListCuentas() {
        return listCuentas;
    }

    public void setListCuentas(List<Banco> listCuentas) {
        this.listCuentas = listCuentas;
    }

    public Integer getCodCuenta() {
        return codCuenta;
    }

    public void setCodCuenta(Integer codCuenta) {
        this.codCuenta = codCuenta;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Boolean getSendCorreo() {
        return sendCorreo;
    }

    public void setSendCorreo(Boolean sendCorreo) {
        this.sendCorreo = sendCorreo;
    }

    public Boolean getSndWhatsapp() {
        return sndWhatsapp;
    }

    public void setSndWhatsapp(Boolean sndWhatsapp) {
        this.sndWhatsapp = sndWhatsapp;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public StreamedContent getFileDown() {
        return fileDown;
    }

    public void setFileDown(StreamedContent fileDown) {
        this.fileDown = fileDown;
    }

    public Boolean getVerRecibo() {
        return verRecibo;
    }

    public void setVerRecibo(Boolean verRecibo) {
        this.verRecibo = verRecibo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

}
