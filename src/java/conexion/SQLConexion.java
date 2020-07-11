/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexion;

import controllers.ReciboController;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelos.Factura;
import modelos.ReciboC;

/**
 *
 * @author IT Acer
 */
public class SQLConexion implements Serializable {

    final String url = "jdbc:sqlserver://sacs.cs50ilmcqjul.us-east-1.rds.amazonaws.com:1433";
    final String user = "appdatabase";
    final String pass = "04590459";
    Connection conexion = null;
    Statement state;
    ResultSet result;

    public void conectar() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conexion = DriverManager.getConnection(url, user, pass);
            if (conexion != null) {
                conexion.setCatalog("Tihsa");
                conexion.setAutoCommit(false);
                state = conexion.createStatement();
                System.out.println("Conexion exitosa");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String parsearFecha(Date fecha) {
        if (fecha != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return "'" + sdf.format(fecha) + "'";
        } else {
            return null;
        }
    }

    public String decimal(Double doub) {
        String formato;
        if (doub != null) {
            DecimalFormat format = new DecimalFormat("#########0.00");
            formato = format.format(doub);
        } else {
            formato = null;
        }
        return formato;
    }

    String formatoString(String cadena) {
        if (cadena != null) {
            return "'" + cadena + "'";
        } else {
            return null;
        }
    }

    public Boolean insertar(ReciboC recibo, List<Factura> listFacturas) {
        Boolean ok;
        try {
            if (recibo.getUsuario() == null) {
                recibo.setUsuario("Sin usuario");
            }
            String insertRecibo = "INSERT INTO FAC_Recibos_C(NumRecibo,Fecha,CodCliente,NIT,Nombre,MontoRecibo,Concepto,"
                    + "Efectivo,MontoCheque1,NumCheque1,FechaCheque1,BancoCheque1,MontoCheque2,NumCheque2,FechaCheque2,BancoCheque2,MontoTC,NumTC,TipoTC,Total,Estado,Usuario,MontoDeposito,NumBoleta,BancoDeposito,"
                    + "Correo,Whatsapp,TipoRecibo,NumOrden)"
                    + "VALUES(" + recibo.getNumRecibo() + "," + parsearFecha(recibo.getFecha()) + "," + recibo.getCodCliente() + ",'" + recibo.getNit() + "',"
                    + "'" + recibo.getNombreCliente() + "'," + decimal(recibo.getMontoRecibo()) + "," + formatoString(recibo.getConcepto()) + "," + decimal(recibo.getEfectivo()) + ","
                    + decimal(recibo.getMontoCheque1()) + "," + formatoString(recibo.getNumCheque1()) + "," + parsearFecha(recibo.getFechaCheque1()) + "," + recibo.getCodBanco1() + ","
                    + decimal(recibo.getMontoCheque2()) + "," + formatoString(recibo.getNumCheque2()) + "," + parsearFecha(recibo.getFechaCheque2()) + "," + recibo.getCodBanco2() + ","
                    + decimal(recibo.getMontoTC()) + "," + formatoString(recibo.getNumTC()) + "," + recibo.getTipoTC() + "," + decimal(recibo.getTotal()) + "," + recibo.getEstado() + ",'" + recibo.getUsuario() + "',"
                    + decimal(recibo.getMontoDeposito()) + "," + formatoString(recibo.getNumBoleta()) + "," + recibo.getBancoDeposito() + ","
                    + formatoString(recibo.getCorreo()) + "," + formatoString(recibo.getWhatsapp()) + ","
                    + (recibo.getTipoRecibo() ? 1 : 0) + "," + recibo.getNumOrden() + ");";
            System.out.println(insertRecibo);
            state.execute(insertRecibo);
            String insertDetalle;
            for (Factura factura : listFacturas) {
                insertDetalle = "INSERT INTO FAC_Recibos_D(CodEmp,NumRecibo,CodigoCliente,SerieFactura,NumDTE,NumAutorizacion,Fecha,Total,Abono,Saldo,Estado)"
                        + " VALUES (1," + recibo.getNumRecibo() + "," + recibo.getCodCliente() + ",'" + factura.getSerie() + "','" + factura.getNumDTE() + "','" + factura.getNumAutorizacion() + "',"
                        + parsearFecha(factura.getFecha()) + "," + decimal(factura.getTotal()) + "," + decimal(factura.getAbono()) + "," + decimal(factura.getSaldo()) + "," + factura.getEstado() + ");";
                System.out.println("Detalle: " + insertDetalle);
                state.execute(insertDetalle);
            }
            conexion.commit();
            ok = true;
        } catch (SQLException ex) {
            ok = false;
            ex.printStackTrace();
            try {
                System.out.println("Transacción fallida");
                conexion.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return ok;
    }

    public Integer correlativoPago() {
        Integer correlativo;
        String query = "SELECT UCorr_Pagos FROM FAC_Empresas ";
        try {
            ResultSet result = state.executeQuery(query);
            if (result.next()) {
                correlativo = result.getInt(1);
            } else {
                correlativo = null;
            }
        } catch (SQLException ex) {
            correlativo = null;
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return correlativo;
    }

    public Boolean actualizar(ReciboC recibo, List<Factura> listFacturas, Integer codCuenta, Double descuento, String referencia) {
        Boolean ok;
        try {
            if (recibo.getUsuario() == null) {
                recibo.setUsuario("Sin usuario");
            }
            Integer correlativo = correlativoPago(), corrOld;
            corrOld = correlativo;
            String insertRecibo = "UPDATE FAC_Recibos_C SET Estado=" + recibo.getEstado() + ", Fecha=" + parsearFecha(recibo.getFecha()) + ", Concepto='" + recibo.getConcepto() + "'"
                    + " WHERE NumRecibo=" + recibo.getNumRecibo();
            System.out.println(insertRecibo);
            state.execute(insertRecibo);
            String regPago, updFactura, insertDetalle;
            for (Factura factura : listFacturas) {
                if (factura.getEstado() != null && factura.getEstado() == 1) {
                    if (factura.getNumPago() == null || factura.getNumPago() == 0) {
                        correlativo += 1;
                        regPago = "INSERT INTO FAC_Pagos VALUES(1," + correlativo + "," + codCuenta + ",'" + referencia + "'," + parsearFecha(new Date()) + ",'"
                                + factura.getSerie() + "'," + factura.getNumDTE() + "," + decimal(factura.getAbono()) + "," + (factura.getDescuento() != null ? decimal(factura.getDescuento()) : 0) + ",0)";
                        System.out.println("regPago " + regPago);
                        state.execute(regPago);
                        insertDetalle = "UPDATE FAC_Recibos_D SET Estado=" + factura.getEstado() + ", NumPago=" + correlativo + " WHERE Secuencia=" + factura.getSecuencia();
                        if (factura.getSaldo() == 0) {
                            updFactura = "UPDATE FAC_Facturas_C set Cancelada=1 where SerieFactura='" + factura.getSerie() + "' and NumFactura=" + factura.getNumDTE();
                            System.out.println(insertDetalle);
                            state.execute(updFactura);
                        }
                        state.execute(insertDetalle);
                    } else {
                        regPago = "UPDATE FAC_Pagos set CodCuenta=" + codCuenta + ", Referencia='" + referencia + "', DescuentoPago=" + (factura.getDescuento() != null ? decimal(factura.getDescuento()) : 0) + ", "
                                + "MontoPago=" + decimal(factura.getAbono()) + " where NumPago=" + factura.getNumPago();
                        state.execute(regPago);
                    }
                } else {
                    insertDetalle = "UPDATE FAC_Recibos_D SET Estado=" + factura.getEstado() + " WHERE Secuencia=" + factura.getSecuencia();
                    System.out.println(insertDetalle);
                    state.execute(insertDetalle);
                }
            }
            if (correlativo != corrOld) {
                String query = "UPDATE FAC_Empresas set UCorr_Pagos=" + correlativo + " where CodEmp=1";
                System.out.println(query);
                state.execute(query);
            }
            conexion.commit();
            ok = true;
            System.out.println("Datos actualizados");
        } catch (SQLException ex) {
            ok = false;
            ex.printStackTrace();
            try {
                System.out.println("Transacción fallida");
                conexion.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return ok;
    }

    public Boolean anular(ReciboC recibo, List<Factura> listFacturas) {
        Boolean ok;
        try {
            if (recibo.getUsuario() == null) {
                recibo.setUsuario("Sin usuario");
            }
            String insertRecibo = "UPDATE FAC_Recibos_C SET Estado=0, Concepto='" + recibo.getConcepto() + "' WHERE NumRecibo=" + recibo.getNumRecibo();
            System.out.println(insertRecibo);
            state.execute(insertRecibo);
            String insertDetalle, updPago, updFactura;
            for (Factura factura : listFacturas) {
                insertDetalle = "UPDATE FAC_Recibos_D SET Estado=0 WHERE Secuencia=" + factura.getSecuencia();
                if (factura.getNumPago() != null) {
                    updPago = "UPDATE FAC_Pagos set Anulado=1 where NumPago=" + factura.getNumPago();
                    updFactura = "UPDATE FAC_Facturas_C set Cancelada=0 where SerieFactura='" + factura.getSerie() + "' and NumFactura=" + factura.getNumDTE();
                    System.out.println("Pago: " + updPago);
                    System.out.println("Factura: " + updFactura);
                    state.execute(updPago);
                    state.execute(updFactura);
                }
                System.out.println(insertDetalle);
                state.execute(insertDetalle);
            }
            conexion.commit();
            ok = true;
            System.out.println("Datos actualizados");
        } catch (SQLException ex) {
            ok = false;
            ex.printStackTrace();
            try {
                System.out.println("Transacción fallida");
                conexion.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return ok;
    }

    public Boolean revertir(ReciboC recibo, List<Factura> listFacturas) {
        Boolean ok;
        try {
            String insertRecibo = "UPDATE FAC_Recibos_C SET Estado=null WHERE NumRecibo=" + recibo.getNumRecibo();
            System.out.println(insertRecibo);
            state.execute(insertRecibo);
            String insertDetalle, updPago, updFactura;
            for (Factura factura : listFacturas) {
                insertDetalle = "UPDATE FAC_Recibos_D SET Estado=null, NumPago=null WHERE Secuencia=" + factura.getSecuencia();
                if (factura.getNumPago() != null || factura.getNumPago() != 0) {
                    updPago = "UPDATE FAC_Pagos set Anulado=1 where NumPago=" + factura.getNumPago();
                    state.execute(updPago);
                    if (factura.getSaldo() == 0) {
                        updFactura = "UPDATE FAC_Facturas_C set Cancelada=0 where SerieFactura='" + factura.getSerie() + "' and NumFactura=" + factura.getNumDTE();
                        state.execute(updFactura);
                        System.out.println("Factura: " + updFactura);
                    }
                    System.out.println("Pago: " + updPago);
                }
                System.out.println(insertDetalle);
                state.execute(insertDetalle);
            }
            conexion.commit();
            ok = true;
            System.out.println("Datos actualizados");
        } catch (SQLException ex) {
            ok = false;
            ex.printStackTrace();
            try {
                System.out.println("Transacción fallida");
                conexion.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return ok;
    }

    public ResultSet consulta(String query) {
        try {
            result = state.executeQuery(query);

        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public Boolean insertar(String query) {
        Boolean ok;
        try {
            state.execute(query);
            conexion.commit();
            ok = true;
        } catch (SQLException ex) {
            ok = false;
            ex.printStackTrace();
            try {
                conexion.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
        return ok;
    }

    public void cerrar() {
        try {
            state.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
