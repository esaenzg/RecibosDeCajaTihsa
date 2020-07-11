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
public class SQLConexion2 implements Serializable {

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
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLConexion2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion2.class.getName()).log(Level.SEVERE, null, ex);
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

    public Boolean insertar(ReciboC recibo, List<Factura> listFacturas) {
        Boolean ok;
        try {
            if (recibo.getUsuario() == null) {
                recibo.setUsuario("it");
            }
            String insertRecibo = "INSERT INTO FAC_Recibos_C(NumRecibo,Fecha,CodCliente,NIT,Nombre,MontoRecibo,Concepto,"
                    + "Efectivo,MontoCheque1,NumCheque1,FechaCheque1,BancoCheque1,MontoCheque2,NumCheque2,FechaCheque2,BancoCheque2,MontoTC,NumTC,TipoTC,Total,Estado,Usuario)"
                    + "VALUES(" + recibo.getNumRecibo() + "," + parsearFecha(recibo.getFecha()) + "," + recibo.getCodCliente() + ",'" + recibo.getNit() + "',"
                    + "'" + recibo.getNombreCliente() + "'," + recibo.getMontoRecibo() + ",'" + recibo.getConcepto() + "'," + recibo.getEfectivo() + ","
                    + recibo.getMontoCheque1() + "," + recibo.getNumCheque1() + "," + parsearFecha(recibo.getFechaCheque1()) + "," + recibo.getCodBanco1() + ","
                    + recibo.getMontoCheque2() + "," + recibo.getNumCheque2() + "," + parsearFecha(recibo.getFechaCheque2()) + "," + recibo.getCodBanco2() + ","
                    + recibo.getMontoTC() + ",'" + recibo.getNumTC() + "'," + recibo.getTipoTC() + "," + recibo.getTotal() + ",null," + recibo.getUsuario() + ");";
            System.out.println(insertRecibo);
            state.execute(insertRecibo);
            String insertDetalle;
            for (Factura factura : listFacturas) {
                insertDetalle = "INSERT INTO FAC_Recibos_D(CodEmp,NumRecibo,CodigoCliente,SerieFactura,NumDTE,NumAutorizacion,Fecha,Total,Abono,Saldo,Estado)"
                        + " VALUES (1," + recibo.getNumRecibo() + "," + recibo.getCodCliente() + ",'" + factura.getSerie() + "','" + factura.getNumDTE() + "','" + factura.getNumAutorizacion() + "',"
                        + parsearFecha(factura.getFecha()) + "," + factura.getTotal() + "," + factura.getAbono() + "," + factura.getSaldo() + "," + factura.getEstado() + ");";
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

    public ResultSet consulta(String query) {
        try {
            result = state.executeQuery(query);

        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public Boolean insertar(String query) {
        Boolean ok;
        try {
            state.execute(query);
            conexion.commit();
            ok=true;
        } catch (SQLException ex) {
            ok=false;
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
            Logger.getLogger(SQLConexion2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
