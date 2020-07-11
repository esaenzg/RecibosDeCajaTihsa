/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emilio Saenz Guillen
 */
public class Funciones implements Serializable {

    private static final long serialVersionUID = -1873280630884874376L;

    public Funciones() {

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

    String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
    String[] mes = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

    public String formatoFecha(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DateFormatSymbols sim = new DateFormatSymbols(Locale.ROOT);
            sim.setMonths(meses);
            sim.setShortMonths(mes);
            sdf.setDateFormatSymbols(sim);
            return sdf.format(date);
        } else {
            return "---";
        }
    }
    public String formatoMesCorto(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM");
            DateFormatSymbols sim = new DateFormatSymbols(Locale.ROOT);
            sim.setMonths(meses);
            sim.setShortMonths(mes);
            sdf.setDateFormatSymbols(sim);
            return sdf.format(date);
        } else {
            return "---";
        }
    }

    public String formatoHora(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        return sdf.format(date);
    }

    public String dayOfWeek(Date date) {
        String dia = "";
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        int num = cal.get(Calendar.DAY_OF_WEEK);
        switch (num) {
            case 1:
                dia = "Domingo";
                break;
            case 2:
                dia = "Lunes";
                break;
            case 3:
                dia = "Martes";
                break;
            case 4:
                dia = "Miércoles";
                break;
            case 5:
                dia = "Jueves";
                break;
            case 6:
                dia = "Viernes";
                break;
            case 7:
                dia = "Sábado";
                break;
        }
        return dia;
    }
    
    public Date obtenerFecha() {
        Calendar cal = Calendar.getInstance();
        Date fecha = cal.getTime();
        TimeZone timezone = TimeZone.getTimeZone("America/Guatemala");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        sdf.setTimeZone(timezone);
        String sDate = sdf.format(fecha);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Date newDate = null;
        try {
            newDate = sdf2.parse(sDate);
        } catch (ParseException ex) {
            Logger.getLogger(Funciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newDate;
    }

    public String fechaReporte() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(obtenerFecha());
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        String fecha = sdf1.format(cal.getTime());
        return fecha;
    }

    public String fechaYhora(Date date) {
        String fecha;
        if (date != null) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
            fecha = sdf1.format(date);
        } else {
            fecha = "";
        }
        return fecha;
    }

    public String getMesMayus(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String mes = sdf.format(date);
        String fecha = "";
        switch (mes) {
            case "0":
                fecha = "ENERO";
                break;
            case "1":
                fecha = "FEBRERO";
                break;
            case "2":
                fecha = "MARZO";
                break;
            case "3":
                fecha = "ABRIL";
                break;
            case "4":
                fecha = "MAYO";
                break;
            case "5":
                fecha = "JUNIO";
                break;
            case "6":
                fecha = "JULIO";
                break;
            case "7":
                fecha = "AGOSTO";
                break;
            case "8":
                fecha = "SEPTIEMBRE";
                break;
            case "9":
                fecha = "OCTUBRE";
                break;
            case "10":
                fecha = "NOVIEMBRE";
                break;
            case "11":
                fecha = "DICIEMBRE";
                break;
        }
        return fecha;
    }

    public String getMes(Integer mes) {
        String fecha = "";
        switch (mes) {
            case 0:
                fecha = "Enero";
                break;
            case 1:
                fecha = "Febrero";
                break;
            case 2:
                fecha = "Marzo";
                break;
            case 3:
                fecha = "Abril";
                break;
            case 4:
                fecha = "Mayo";
                break;
            case 5:
                fecha = "Junio";
                break;
            case 6:
                fecha = "Julio";
                break;
            case 7:
                fecha = "Agosto";
                break;
            case 8:
                fecha = "Septiembre";
                break;
            case 9:
                fecha = "Octubre";
                break;
            case 10:
                fecha = "Noviembre";
                break;
            case 11:
                fecha = "Diciembre";
                break;
        }
        return fecha;
    }
    public String getMesCorto(Integer mes) {
        String fecha = "";
        switch (mes) {
            case 0:
                fecha = "Ene";
                break;
            case 1:
                fecha = "Feb";
                break;
            case 2:
                fecha = "Mar";
                break;
            case 3:
                fecha = "Abr";
                break;
            case 4:
                fecha = "May";
                break;
            case 5:
                fecha = "Jun";
                break;
            case 6:
                fecha = "Jul";
                break;
            case 7:
                fecha = "Ago";
                break;
            case 8:
                fecha = "Sep";
                break;
            case 9:
                fecha = "Oct";
                break;
            case 10:
                fecha = "Nov";
                break;
            case 11:
                fecha = "Dic";
                break;
        }
        return fecha;
    }

    public String diaYfecha(Date date) {
        String fecha = dayOfWeek(date) + ", " + formatoFecha(date);
        return fecha;
    }

    public String formatoDia(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(date);
    }

    public String getAño(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(date);
    }

    public String formatoDiaMes(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        return sdf.format(date);
    }
    
    public Date fechaInicio(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
    
    public Date fechaFinal(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

}
