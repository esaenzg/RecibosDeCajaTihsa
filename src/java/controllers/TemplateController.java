/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import modelos.Usuario;

/**
 *
 * @author
 */
@ManagedBean
@ViewScoped
public class TemplateController implements Serializable {

    private static final long serialVersionUID = 6314613158052068507L;

    private final FacesContext context = FacesContext.getCurrentInstance();
    private final NavigationHandler navigator = context.getApplication().getNavigationHandler();

    private String nombreYApellido = null;
    private Boolean activo = true;
    private String estado = "En línea", mensaje;
    private List<String> mensajes = new ArrayList<>();
    private Number select = 1;
    private String fotoPerfil;
    private Boolean mostrar = true;
    private Integer tipo;
    private String icono;
    private Usuario usuario;

    public TemplateController() {
    }

    @PostConstruct
    public void init() {
        icono = "icono.ico";
        try {
            Usuario us = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
            if (us == null) {
                this.usuario = new Usuario();
            } else {
                this.usuario = us;
            }
        } catch (Exception e) {
            FacesContext contex = FacesContext.getCurrentInstance();
            try {
                contex.getExternalContext().redirect("faces/views/login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(TemplateController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Date obtenerFecha() {
        Calendar cal = Calendar.getInstance();
        System.out.println("Zona horaria:" + cal.getTimeZone());
        System.out.println("fecha actual: " + cal.getTime());
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
            Logger.getLogger(TemplateController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("La fecha en " + timezone.getID());
        System.out.println("Nueva fecha: " + sdf2.format(newDate));
        return newDate;
    }


    public List<String> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<String> mensajes) {
        this.mensajes = mensajes;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombreYApellido() {
        return nombreYApellido;
    }

    public void setNombreYApellido(String nombreYApellido) {
        this.nombreYApellido = nombreYApellido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void verificarSesion() {
        try {
            Usuario us = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
            if (us == null) {
                navigator.handleNavigation(context, null, "pretty:login");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void verSesion(Boolean ok) {
        if (ok) {
            estado = "En línea";
            activo = true;
        } else {
            Date date = obtenerFecha();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
            estado = "Últ. vez " + sdf.format(date);
            activo = false;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", null);
            FacesContext context = FacesContext.getCurrentInstance();
            NavigationHandler navigator = context.getApplication().getNavigationHandler();
            navigator.handleNavigation(context, null, "pretty:login");
        }
    }

    public void salir() {
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigator = context.getApplication().getNavigationHandler();
        navigator.handleNavigation(context, null, "pretty:login");
    }
   

    public Number getSelect() {
        return select;
    }

    public void setSelect(Number select) {
        this.select = select;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public Boolean getMostrar() {
        return mostrar;
    }

    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

}
